#!/usr/bin/env python3
"""
nHaystack | Haystack HTTP API tests
===================================

Command-line test runner for the nHaystack Haystack HTTP API, converted from
the Postman collection "nHaystack | Haystack HTTP API Tests" (Collection v2.1).

It uses only the Python standard library, so there is nothing to pip install.
It runs the same way on Linux, macOS and Windows (Python 3.8 or newer).

Quick start
-----------
    Linux / macOS:
        export NHAYSTACK_PASSWORD='...'
        python3 nhaystack_tests.py --base-url http://10.211.55.3:8081/haystack

    Windows (PowerShell):
        $env:NHAYSTACK_PASSWORD = '...'
        py nhaystack_tests.py --base-url http://10.211.55.3:8081/haystack

If no password is supplied and the script is run interactively, it prompts
for one.

Settings (command-line flag, then environment variable, then default)
---------------------------------------------------------------------
    --base-url   NHAYSTACK_BASE_URL   http://10.211.55.3:8081/haystack
    --username   NHAYSTACK_USERNAME   nhaystack
    --password   NHAYSTACK_PASSWORD   (prompted)

Run `python nhaystack_tests.py --help` to see every option, including JUnit
XML output for CI, running a subset of requests and relaxing response-time
limits.

Exit status: 0 = all checks passed, 1 = at least one failure, 2 = bad usage.

How this file is laid out
-------------------------
1. Defaults and station-specific test data (point ids, nav ids). Edit these
   to point the tests at a different station.
2. The tests: one function per request, in the same order as the Postman
   collection. Each `with t.check("..."):` block is one Postman pm.test(...).
3. The assertion helpers and the runner underneath. You shouldn't need to
   touch these to add or change tests.

To add a test, copy one of the functions in section 2, give it a new name in
its @api_request("...") decorator and put it where it should run. Requests run
from top to bottom.
"""

import argparse
import base64
import contextlib
import getpass
import http.client
import json
import os
import ssl
import sys
import time
import traceback
import urllib.error
import urllib.parse
import urllib.request
import xml.etree.ElementTree as ET
from dataclasses import dataclass, field
from http import HTTPStatus
from typing import Any, Callable, Dict, List, Optional, Tuple

if sys.version_info < (3, 8):
    sys.exit("This script needs Python 3.8 or newer.")


# =============================================================================
# 1. Defaults and station-specific test data
# =============================================================================

COLLECTION_NAME = "nHaystack | Haystack HTTP API Tests"
DEFAULT_BASE_URL = "http://10.211.55.3:8081/haystack"
DEFAULT_USERNAME = "nhaystack"
USER_AGENT = "nhaystack-tests/1.0 (Python urllib)"

# These ids refer to points and folders on the nHaystack test station.
SAT_POINT_ID = "@S.Building-1.AHU~2d01.SAT"
ENABLE_POINT_ID = "@S.Building-1.AHU~2d01.enable"
AHU_NAV_ID = "slot:/Building$201/Floor_01/AHU$2d01"
EXPECTED_SITE_COUNT = 2

# Points subscribed by watchSub. The "Bogus" ids don't exist on purpose.
WATCH_POINT_IDS = [
    "@S.Building-1.AHU~2d01.Bogus",
    "@S.Building-1.AHU~2d01.SAT",
    "@S.Building-1.AHU~2d01.RAT",
    "@S.Building-1.AHU~2d01.MAT",
    "@S.Building-2.AHU~2d01.Bogus",
    "@S.Building-2.AHU~2d01.SAT",
    "@S.Building-2.AHU~2d01.RAT",
    "@S.Building-2.AHU~2d01.MAT",
]

# Request header sets used below.
JSON = {"Accept": "application/json"}
ZINC_IN_JSON_OUT = {"Accept": "application/json", "Content-Type": "text/zinc"}
ZINC_IN = {"Content-Type": "text/zinc"}


# The registry of requests, in run order. Each test function registers itself
# with the @api_request decorator.
REQUESTS: List[Tuple[str, Callable[..., None]]] = []


def api_request(name: str) -> Callable[[Callable[..., None]], Callable[..., None]]:
    def register(fn: Callable[..., None]) -> Callable[..., None]:
        REQUESTS.append((name, fn))
        return fn
    return register


# =============================================================================
# 2. The tests: one function per Postman request, in collection order
# =============================================================================
# `t` is the RequestContext (defined in section 3). It offers:
#   t.send(method, path, query=..., headers=..., body=...)  -> Response
#   t.check("name")                  a `with` block = one pm.test(...)
#   t.check_status(r, 200)           pm.response.to.have.status(200)
#   t.check_response_time(r, 200)    pm.expect(responseTime).to.be.below(200)
#   t.vars                           collection variables (e.g. watchId)
# Response `r`: r.status, r.elapsed_ms, r.text, r.json()


@api_request("about")
def about(t):
    r = t.send("GET", "/about", headers=JSON)
    t.check_status(r, 200)
    t.check_response_time(r, 200)

    with t.check("Verify that the response has the required fields - meta, cols, and rows"):
        data = r.json()
        expect_type(data, "object", "response")
        for key in ("meta", "cols", "rows"):
            expect_property(data, key, "response")

    with t.check("Validate that the 'ver' field in the meta object is a non-empty string"):
        data = r.json()
        expect_type(data, "object", "response")
        expect_non_empty_string(dig(data, "meta", "ver"), "meta.ver")

    with t.check("Ensure that the 'cols' array contains at least one element with a non-empty name"):
        cols = dig(r.json(), "cols")
        expect_type(cols, "array", "cols")
        expect_not_empty(cols, "cols")
        for i, col in enumerate(cols):
            name = dig(col, "name")
            expect_exists(name, f"cols[{i}].name")
            expect_min_length(name, 1, f"cols[{i}].name")


@api_request("ops")
def ops(t):
    r = t.send("GET", "/ops", headers=JSON)
    t.check_status(r, 200)
    t.check_response_time(r, 200, "Response time is within an acceptable range")

    with t.check("Validate the structure of the response"):
        data = r.json()
        expect_type(data, "object", "response")
        expect_exists_as(dig(data, "meta"), "object", "meta")
        expect_exists_as(dig(data, "cols"), "array", "cols")
        expect_exists_as(dig(data, "rows"), "array", "rows")

    with t.check("Ver field in meta object is present and non-empty"):
        meta = expect_property(r.json(), "meta", "response")
        expect_not_empty(expect_property(meta, "ver", "meta"), "meta.ver")

    with t.check("Name and summary fields in the rows array are non-empty strings"):
        data = r.json()
        expect_type(data, "object", "response")
        rows = dig(data, "rows")
        expect_type(rows, "array", "rows")
        for i, row in enumerate(rows):
            expect_non_empty_string(dig(row, "name"), f"rows[{i}].name")
            expect_non_empty_string(dig(row, "summary"), f"rows[{i}].summary")


@api_request("formats")
def formats(t):
    r = t.send("GET", "/formats", headers=JSON)
    t.check_status(r, 200)
    t.check_response_time(r, 200)

    with t.check("Meta object exists and is not empty"):
        data = r.json()
        expect_type(data, "object", "response")
        expect_exists(dig(data, "meta"), "meta")
        expect_not_empty(dig(data, "meta"), "meta")

    with t.check("Cols array should exist and be an array"):
        data = r.json()
        expect_type(data, "object", "response")
        expect_exists_as(dig(data, "cols"), "array", "cols")

    with t.check("Rows array in the response should exist and be an array"):
        expect_exists_as(dig(r.json(), "rows"), "array", "rows")


@api_request("filetypes")
def filetypes(t):
    # No tests in the Postman collection: the request is only sent.
    t.send("GET", "/filetypes", headers=JSON)


@api_request("read by id")
def read_by_id(t):
    r = t.send("GET", "/read", query={"id": SAT_POINT_ID}, headers=JSON)
    t.check_status(r, 200)

    with t.check("Meta object should exist and be an object"):
        expect_exists_as(dig(r.json(), "meta"), "object", "meta")

    with t.check("Cols array has the correct structure"):
        cols = dig(r.json(), "cols")
        expect_type(cols, "array", "cols")
        expect_min_length(cols, 1, "cols")
        for i, col in enumerate(cols):
            expect_type(col, "object", f"cols[{i}]")
            expect_exists_as(dig(col, "name"), "string", f"cols[{i}].name")

    with t.check("Rows array structure is valid"):
        rows = dig(r.json(), "rows")
        expect_type(rows, "array", "rows")
        for i, row in enumerate(rows):
            expect_type(row, "object", f"rows[{i}]")


@api_request("read by filter site")
def read_by_filter_site(t):
    r = t.send("GET", "/read", query={"filter": "site"}, headers=JSON)
    t.check_status(r, 200)
    t.check_response_time(r, 1000)

    with t.check("Meta object should exist in the response"):
        data = r.json()
        expect_type(data, "object", "response")
        expect_exists(dig(data, "meta"), "meta")
        expect_type(expect_property(data, "cols", "response"), "array", "cols")

    with t.check("Cols array must exist in the response"):
        data = r.json()
        expect_type(data, "object", "response")
        expect_exists_as(dig(data, "cols"), "array", "cols")

    with t.check(f"Rows must exist and there must be {EXPECTED_SITE_COUNT} rows, 1 for each site"):
        rows = expect_property(r.json(), "rows", "response")
        expect_type(rows, "array", "rows")
        expect_length(rows, EXPECTED_SITE_COUNT, "rows")


@api_request("read by filter equip")
def read_by_filter_equip(t):
    r = t.send("GET", "/read", query={"filter": "equip"}, headers=JSON)
    t.check_status(r, 200)
    t.check_response_time(r, 1000)

    with t.check("Validate the response schema for meta, cols, and rows"):
        data = r.json()
        expect_type(data, "object", "response")
        expect_type(expect_property(data, "meta", "response"), "object", "meta")
        expect_type(expect_property(data, "cols", "response"), "array", "cols")
        expect_type(expect_property(data, "rows", "response"), "array", "rows")

    with t.check("Meta version is a non-empty string"):
        meta = dig(r.json(), "meta")
        expect_type(meta, "object", "meta")
        expect_non_empty_string(dig(meta, "ver"), "meta.ver")

    with t.check("Rows contain non-empty values for specific properties"):
        rows = dig(r.json(), "rows")
        expect_type(rows, "array", "rows")
        tags = ("hvac", "dis", "axType", "navName", "id", "n4SlotPath",
                "axSlotPath", "siteRef", "ahu", "equip")
        for i, row in enumerate(rows):
            for tag in tags:
                expect_exists(dig(row, tag), f"rows[{i}].{tag}")
                expect_not_empty(dig(row, tag), f"rows[{i}].{tag}")


@api_request("read by filter point")
def read_by_filter_point(t):
    r = t.send("GET", "/read", query={"filter": "point"}, headers=JSON)
    t.check_status(r, 200)
    t.check_response_time(r, 1000)

    with t.check("Meta object structure is valid"):
        meta = dig(r.json(), "meta")
        expect_exists_as(meta, "object", "meta")
        expect_exists_as(dig(meta, "ver"), "string", "meta.ver")

    with t.check("Validate the structure of the 'cols' array"):
        cols = dig(r.json(), "cols")
        expect_type(cols, "array", "cols")
        expect_min_length(cols, 1, "cols")
        for i, col in enumerate(cols):
            expect_type(col, "object", f"cols[{i}]")
            expect_exists_as(dig(col, "name"), "string", f"cols[{i}].name")

    with t.check("Rows array should have the expected structure"):
        rows = dig(r.json(), "rows")
        expect_type(rows, "array", "rows")
        expect_not_empty(rows, "rows")
        for i, row in enumerate(rows):
            expect_type(row, "object", f"rows[{i}]")
            for tag in ("tz", "point", "dis", "axType", "navName", "id", "kind"):
                expect_property(row, tag, f"rows[{i}]")


@api_request("nav root")
def nav_root(t):
    r = t.send("GET", "/nav", headers=JSON)
    t.check_status(r, 200)
    t.check_response_time(r, 200)

    with t.check("Meta object exists in the response"):
        expect_exists_as(dig(r.json(), "meta"), "object", "meta")

    with t.check("Cols array should exist and be an array"):
        expect_exists_as(dig(r.json(), "cols"), "array", "cols")

    with t.check("Rows array should exist and be an array"):
        expect_exists_as(dig(r.json(), "rows"), "array", "rows")


@api_request("nav by navId")
def nav_by_nav_id(t):
    r = t.send("GET", "/nav", query={"navId": AHU_NAV_ID}, headers=JSON)
    t.check_status(r, 200)
    t.check_response_time(r, 200)

    with t.check("Validate the response schema for the presence of meta, cols, and rows"):
        data = r.json()
        expect_type(data, "object", "response")
        expect_exists_as(dig(data, "meta"), "object", "meta")
        expect_exists_as(dig(data, "cols"), "array", "cols")
        expect_exists_as(dig(data, "rows"), "array", "rows")

    with t.check("Meta version is not empty"):
        ver = dig(r.json(), "meta", "ver")
        expect_exists(ver, "meta.ver")
        expect_not_empty(ver, "meta.ver")

    with t.check("Rows array contains objects with non-empty properties"):
        rows = dig(r.json(), "rows")
        expect_type(rows, "array", "rows")
        expect_not_empty(rows, "rows")
        for i, row in enumerate(rows):
            for tag in ("dis", "axType", "id", "n4SlotPath", "navId", "axSlotPath"):
                value = expect_property(row, tag, f"rows[{i}]")
                expect_non_empty_string(value, f"rows[{i}].{tag}")


@api_request("hisRead")
def his_read(t):
    r = t.send("GET", "/hisRead", query={"id": SAT_POINT_ID, "range": "today"}, headers=JSON)
    t.check_status(r, 200)
    t.check_response_time(r, 200, "Response time is within an acceptable range")

    with t.check("Response has the required fields - meta, cols, and rows"):
        data = r.json()
        expect_type(data, "object", "response")
        for key in ("meta", "cols", "rows"):
            expect_property(data, key, "response")

    with t.check("Timestamp (ts) is in a valid format"):
        rows = dig(r.json(), "rows")
        expect_type(rows, "array", "rows")
        for i, row in enumerate(rows):
            expect_exists_as(dig(row, "ts"), "string", f"rows[{i}].ts")

    with t.check("Value (val) is in a valid format"):
        data = r.json()
        expect_type(data, "object", "response")
        rows = dig(data, "rows")
        expect_type(rows, "array", "rows")
        for i, row in enumerate(rows):
            expect_exists_as(dig(row, "val"), "string", f"rows[{i}].val")


@api_request("watchSub")
def watch_sub(t):
    body = (
        'ver:"2.0" watchDis:"nHaystack Testing" lease:150000ms\r\n'
        "id\r\n"
        + "".join(point_id + "\r\n" for point_id in WATCH_POINT_IDS)
    )
    r = t.send("POST", "/watchSub", headers=ZINC_IN_JSON_OUT, body=body)
    t.check_status(r, 200)
    t.check_response_time(r, 200)

    with t.check("Meta object must exist and have specific properties"):
        meta = dig(r.json(), "meta")
        expect_exists_as(meta, "object", "meta")
        for key in ("ver", "watchId", "lease"):
            expect_property(meta, key, "meta")

    with t.check("Cols array schema is valid"):
        cols = dig(r.json(), "cols")
        expect_type(cols, "array", "cols")
        for i, col in enumerate(cols):
            expect_type(col, "object", f"cols[{i}]")
            expect_type(dig(col, "name"), "string", f"cols[{i}].name")

    with t.check("Rows array schema is valid"):
        data = r.json()
        expect_type(data, "object", "response")
        rows = dig(data, "rows")
        expect_type(rows, "array", "rows")
        for i, row in enumerate(rows):
            expect_type(row, "object", f"rows[{i}]")

    # Same as pm.collectionVariables.set("watchId", ...) in Postman: keep the
    # watch id for watchPoll and watchUnsub.
    watch_id = dig(r.json_or_none(), "meta", "watchId")
    if watch_id:
        t.vars["watchId"] = watch_id


@api_request("watchPoll")
def watch_poll(t):
    watch_id = t.require_var("watchId", set_by="watchSub")
    body = f'ver:"2.0" watchId:"{watch_id}" refresh\r\nid\r\n'
    r = t.send("POST", "/watchPoll", headers=ZINC_IN_JSON_OUT, body=body)
    t.check_status(r, 200)
    t.check_response_time(r, 200, "Response time is within an acceptable range")

    with t.check("Meta object structure is valid"):
        meta = dig(r.json(), "meta")
        expect_exists_as(meta, "object", "meta")
        expect_exists_as(dig(meta, "ver"), "string", "meta.ver")

    with t.check("Cols array should have the correct structure"):
        cols = dig(r.json(), "cols")
        expect_type(cols, "array", "cols")
        for i, col in enumerate(cols):
            expect_type(col, "object", f"cols[{i}]")
            expect_type(dig(col, "name"), "string", f"cols[{i}].name")

    with t.check("Rows array structure is valid"):
        data = r.json()
        expect_type(data, "object", "response")
        rows = dig(data, "rows")
        expect_type(rows, "array", "rows")
        for i, row in enumerate(rows):
            expect_type(row, "object", f"rows[{i}]")
            expect_exists_as(dig(row, "curStatus"), "string", f"rows[{i}].curStatus")
            expect_exists(dig(row, "curVal"), f"rows[{i}].curVal")
            expect_exists_as(dig(row, "id"), "string", f"rows[{i}].id")


@api_request("watchUnsub")
def watch_unsub(t):
    watch_id = t.require_var("watchId", set_by="watchSub")
    body = f'ver:"2.0" watchId:"{watch_id}"\r\nid\r\n{SAT_POINT_ID}\r\n'
    r = t.send("POST", "/watchUnsub", headers=ZINC_IN_JSON_OUT, body=body)
    t.check_status(r, 200)
    t.check_response_time(r, 200, "Response time is within an acceptable range")

    with t.check("Meta object has the correct structure"):
        meta = dig(r.json(), "meta")
        expect_exists_as(meta, "object", "meta")
        expect_exists_as(dig(meta, "ver"), "string", "meta.ver")

    with t.check("Cols array structure is valid"):
        data = r.json()
        expect_type(data, "object", "response")
        cols = dig(data, "cols")
        expect_type(cols, "array", "cols")
        for i, col in enumerate(cols):
            expect_type(col, "object", f"cols[{i}]")
            expect_type(dig(col, "name"), "string", f"cols[{i}].name")

    with t.check("Rows array is present and empty"):
        rows = expect_property(r.json(), "rows", "response")
        expect_type(rows, "array", "rows")
        expect_empty(rows, "rows")


@api_request("invokeAction - NumericWritable")
def invoke_action_numeric_override(t):
    body = f'ver:"3.0" id: {SAT_POINT_ID} action:"override"\nvalue\n120.0'
    r = t.send("POST", "/invokeAction", headers=ZINC_IN, body=body)
    t.check_status(r, 200)


@api_request("invokeAction - NumericWritable Auto")
def invoke_action_numeric_auto(t):
    body = f'ver:"3.0" id: {SAT_POINT_ID} action:"auto"\nempty\n'
    r = t.send("POST", "/invokeAction", headers=ZINC_IN, body=body)
    t.check_status(r, 200)


@api_request("invokeAction - EnumWritable Override")
def invoke_action_enum_override(t):
    body = f'ver:"3.0" id: {ENABLE_POINT_ID} action:"override"\nvalue,duration\n"run",1min'
    r = t.send("POST", "/invokeAction", headers=ZINC_IN, body=body)
    t.check_status(r, 200)


@api_request("close")
def close(t):
    # No tests in the Postman collection: the request is only sent. Postman
    # had "follow original HTTP method" switched on for this one, so a
    # redirect stays a POST.
    t.send("POST", "/close", follow_original_method=True)


# =============================================================================
# 3. Assertion helpers
# =============================================================================
# These mirror the Chai assertions used in the Postman scripts and use the
# same type names: "object", "array", "string", "number", "boolean", "null".
# Each one raises AssertionError with a readable message when it fails.

def type_name(value: Any) -> str:
    if value is None:
        return "null"
    if isinstance(value, bool):
        return "boolean"
    if isinstance(value, (int, float)):
        return "number"
    if isinstance(value, str):
        return "string"
    if isinstance(value, list):
        return "array"
    if isinstance(value, dict):
        return "object"
    return type(value).__name__


def _article(kind: str) -> str:
    return "an" if kind[:1] in "aeiou" else "a"


def dig(value: Any, *keys: str) -> Any:
    """Walk into nested objects: dig(data, "meta", "ver") is data.meta.ver.
    Returns None if any step is missing or isn't an object (like undefined
    in JavaScript)."""
    for key in keys:
        if not isinstance(value, dict):
            return None
        value = value.get(key)
    return value


def ensure(condition: bool, message: str) -> None:
    if not condition:
        raise AssertionError(message)


def expect_type(value: Any, kind: str, what: str) -> None:
    """pm.expect(value).to.be.a(kind)"""
    actual = type_name(value)
    ensure(actual == kind, f"expected {what} to be {_article(kind)} {kind} but got {actual}")


def expect_exists(value: Any, what: str) -> None:
    """pm.expect(value).to.exist"""
    ensure(value is not None, f"expected {what} to exist")


def expect_exists_as(value: Any, kind: str, what: str) -> None:
    """pm.expect(value).to.exist.and.to.be.a(kind)"""
    expect_exists(value, what)
    expect_type(value, kind, what)


def expect_property(obj: Any, key: str, what: str) -> Any:
    """pm.expect(obj).to.have.property(key). Returns the property value."""
    ensure(isinstance(obj, dict), f"expected {what} to be an object with a '{key}' property "
                                  f"but it is {_article(type_name(obj))} {type_name(obj)}")
    ensure(key in obj, f"expected {what} to have property '{key}'")
    return obj[key]


def _sized(value: Any, what: str) -> int:
    kind = type_name(value)
    ensure(kind in ("string", "array", "object"),
           f"expected {what} to be a string, array or object but got {kind}")
    return len(value)


def expect_not_empty(value: Any, what: str) -> None:
    """pm.expect(value).to.not.be.empty"""
    ensure(_sized(value, what) > 0, f"expected {what} not to be empty")


def expect_empty(value: Any, what: str) -> None:
    """pm.expect(value).to.be.empty"""
    size = _sized(value, what)
    ensure(size == 0, f"expected {what} to be empty but it has {size} item(s)")


def expect_min_length(value: Any, minimum: int, what: str) -> None:
    """pm.expect(value).to.have.lengthOf.at.least(minimum)"""
    kind = type_name(value)
    ensure(kind in ("string", "array"), f"expected {what} to have a length but got {kind}")
    ensure(len(value) >= minimum,
           f"expected {what} to have a length of at least {minimum} but got {len(value)}")


def expect_length(value: Any, length: int, what: str) -> None:
    """pm.expect(value).to.have.length(length)"""
    kind = type_name(value)
    ensure(kind in ("string", "array"), f"expected {what} to have a length but got {kind}")
    ensure(len(value) == length, f"expected {what} to have a length of {length} but got {len(value)}")


def expect_non_empty_string(value: Any, what: str) -> None:
    """pm.expect(value).to.be.a('string').and.to.have.lengthOf.at.least(1)"""
    expect_type(value, "string", what)
    ensure(len(value) > 0, f"expected {what} not to be empty")


# =============================================================================
# 4. HTTP, runner and reporting
# =============================================================================

# Characters left as they are in query values, as Postman does, so that ids
# like @S.Building-1.AHU~2d01.SAT and slot:/Building$201 are sent unchanged.
QUERY_SAFE = "@:/$~!*'(),;"


@dataclass
class Config:
    base_url: str
    username: str
    password: str
    timeout: float = 30.0
    time_factor: float = 1.0
    no_timing: bool = False
    insecure: bool = False
    no_proxy: bool = False
    verbose: bool = False
    bail: bool = False
    _openers: Dict[bool, urllib.request.OpenerDirector] = field(default_factory=dict, repr=False)

    def opener(self, follow_original_method: bool) -> urllib.request.OpenerDirector:
        if follow_original_method not in self._openers:
            handlers: List[Any] = [_RedirectHandler(follow_original_method)]
            if self.insecure:
                ctx = ssl.create_default_context()
                ctx.check_hostname = False
                ctx.verify_mode = ssl.CERT_NONE
                handlers.append(urllib.request.HTTPSHandler(context=ctx))
            if self.no_proxy:
                handlers.append(urllib.request.ProxyHandler({}))
            self._openers[follow_original_method] = urllib.request.build_opener(*handlers)
        return self._openers[follow_original_method]


class _RedirectHandler(urllib.request.HTTPRedirectHandler):
    """Follow redirects the way Postman does. 307 and 308 always keep the
    method and body. With follow_original_method, 301, 302 and 303 keep them
    too; otherwise those become a GET."""

    def __init__(self, follow_original_method: bool) -> None:
        super().__init__()
        self.follow_original_method = follow_original_method

    def redirect_request(self, req, fp, code, msg, headers, newurl):  # noqa: D102
        if code in (307, 308) or (self.follow_original_method and code in (301, 302, 303)):
            return urllib.request.Request(newurl, data=req.data, headers=dict(req.headers),
                                          method=req.get_method())
        return super().redirect_request(req, fp, code, msg, headers, newurl)


class RequestAborted(Exception):
    """The request couldn't be sent or a precondition wasn't met."""


class Response:
    def __init__(self, method: str, url: str, status: int, headers: Any,
                 body: bytes, elapsed_ms: float) -> None:
        self.method = method
        self.url = url
        self.status = status
        self.headers = headers
        self.body = body
        self.elapsed_ms = elapsed_ms
        self._parsed: Optional[Tuple[Any, Optional[str]]] = None

    @property
    def reason(self) -> str:
        try:
            return HTTPStatus(self.status).phrase
        except ValueError:
            return ""

    @property
    def content_type(self) -> str:
        return self.headers.get("Content-Type", "") if self.headers else ""

    @property
    def text(self) -> str:
        charset = None
        if self.headers is not None and hasattr(self.headers, "get_content_charset"):
            charset = self.headers.get_content_charset()
        try:
            return self.body.decode(charset or "utf-8", errors="replace")
        except LookupError:
            return self.body.decode("utf-8", errors="replace")

    def _parse(self) -> Tuple[Any, Optional[str]]:
        if self._parsed is None:
            try:
                self._parsed = (json.loads(self.text), None)
            except ValueError as exc:
                self._parsed = (None, str(exc))
        return self._parsed

    def json(self) -> Any:
        """The body parsed as JSON, like pm.response.json(). Fails the
        enclosing check if the body isn't JSON."""
        value, error = self._parse()
        if error is not None:
            snippet = self.text[:120].replace("\r", " ").replace("\n", " ")
            raise AssertionError(f"response body is not valid JSON ({error}); "
                                 f"Content-Type: {self.content_type or 'none'}; body starts: {snippet!r}")
        return value

    def json_or_none(self) -> Any:
        return self._parse()[0]


def build_url(base_url: str, path: str, query: Optional[Dict[str, str]]) -> str:
    url = base_url.rstrip("/") + path
    if query:
        url += "?" + "&".join(
            urllib.parse.quote(str(k), safe=QUERY_SAFE) + "=" + urllib.parse.quote(str(v), safe=QUERY_SAFE)
            for k, v in query.items()
        )
    return url


def http_send(cfg: Config, method: str, path: str, query: Optional[Dict[str, str]] = None,
              headers: Optional[Dict[str, str]] = None, body: Optional[str] = None,
              follow_original_method: bool = False) -> Response:
    url = build_url(cfg.base_url, path, query)
    credentials = base64.b64encode(f"{cfg.username}:{cfg.password}".encode("utf-8")).decode("ascii")
    all_headers = {
        "User-Agent": USER_AGENT,
        "Accept": "*/*",
        # Sent up front on every request, like Postman's basic auth.
        "Authorization": f"Basic {credentials}",
    }
    all_headers.update(headers or {})
    if body is not None:
        data: Optional[bytes] = body.encode("utf-8")
    elif method in ("POST", "PUT", "PATCH"):
        data = b""  # send Content-Length: 0 rather than no length at all
    else:
        data = None
    req = urllib.request.Request(url, data=data, headers=all_headers, method=method)

    start = time.perf_counter()
    try:
        with cfg.opener(follow_original_method).open(req, timeout=cfg.timeout) as resp:
            payload = resp.read()
            status, resp_headers = resp.getcode(), resp.headers
    except urllib.error.HTTPError as exc:  # 4xx / 5xx: still a response to check
        payload = exc.read() if exc.fp else b""
        status, resp_headers = exc.code, exc.headers
    except urllib.error.URLError as exc:
        raise RequestAborted(f"{method} {url} failed: {exc.reason}") from None
    except (OSError, http.client.HTTPException) as exc:
        raise RequestAborted(f"{method} {url} failed: {type(exc).__name__}: {exc}") from None
    elapsed_ms = (time.perf_counter() - start) * 1000.0
    return Response(method, url, status, resp_headers, payload, elapsed_ms)


@dataclass
class CheckResult:
    name: str
    outcome: str  # "pass", "fail" or "skip"
    message: str = ""


@dataclass
class RequestResult:
    name: str
    response: Optional[Response] = None
    checks: List[CheckResult] = field(default_factory=list)
    error: Optional[str] = None

    @property
    def failed(self) -> bool:
        return self.error is not None or any(c.outcome == "fail" for c in self.checks)


class RequestContext:
    """What each test function receives as `t`."""

    def __init__(self, cfg: Config, variables: Dict[str, Any], result: RequestResult,
                 reporter: "Reporter") -> None:
        self.cfg = cfg
        self.vars = variables
        self.result = result
        self.reporter = reporter

    def send(self, method: str, path: str, *, query: Optional[Dict[str, str]] = None,
             headers: Optional[Dict[str, str]] = None, body: Optional[str] = None,
             follow_original_method: bool = False) -> Response:
        response = http_send(self.cfg, method, path, query, headers, body, follow_original_method)
        self.result.response = response
        self.reporter.request_sent(response)
        return response

    @contextlib.contextmanager
    def check(self, name: str):
        """One named check. A failed assertion, or any error, inside the
        `with` block fails this check only; the rest still run."""
        try:
            yield
        except AssertionError as exc:
            self._record(CheckResult(name, "fail", str(exc) or "assertion failed"))
        except Exception as exc:  # e.g. a TypeError from unexpected data
            self._record(CheckResult(name, "fail", f"{type(exc).__name__}: {exc}"))
        else:
            self._record(CheckResult(name, "pass"))

    def skip(self, name: str, reason: str) -> None:
        self._record(CheckResult(name, "skip", reason))

    def check_status(self, r: Response, code: int, name: Optional[str] = None) -> None:
        with self.check(name or f"Response status code is {code}"):
            ensure(r.status == code, f"expected status {code} but got {r.status} {r.reason}".rstrip())

    def check_response_time(self, r: Response, limit_ms: float, name: Optional[str] = None) -> None:
        name = name or f"Response time is less than {limit_ms:g}ms"
        if self.cfg.no_timing:
            self.skip(name, "switched off with --no-timing")
            return
        limit = limit_ms * self.cfg.time_factor
        with self.check(name):
            ensure(r.elapsed_ms < limit,
                   f"expected the response in under {limit:g} ms but it took {r.elapsed_ms:.0f} ms")

    def require_var(self, name: str, set_by: str) -> Any:
        value = self.vars.get(name)
        if not value:
            raise RequestAborted(f"'{name}' is not set. It is captured by the '{set_by}' request, "
                                 f"which has to run and succeed first.")
        return value

    def _record(self, check: CheckResult) -> None:
        self.result.checks.append(check)
        self.reporter.check_done(check)


class Style:
    def __init__(self, enabled: bool) -> None:
        self.enabled = enabled

    def _wrap(self, code: str, text: str) -> str:
        return f"\033[{code}m{text}\033[0m" if self.enabled else text

    def green(self, text: str) -> str:
        return self._wrap("32", text)

    def red(self, text: str) -> str:
        return self._wrap("31", text)

    def yellow(self, text: str) -> str:
        return self._wrap("33", text)

    def dim(self, text: str) -> str:
        return self._wrap("2", text)

    def bold(self, text: str) -> str:
        return self._wrap("1", text)


def _enable_windows_ansi() -> bool:
    """Switch on ANSI colour handling in a Windows console."""
    try:
        import ctypes
        kernel32 = ctypes.windll.kernel32  # type: ignore[attr-defined]
        handle = kernel32.GetStdHandle(-11)  # STD_OUTPUT_HANDLE
        mode = ctypes.c_uint32()
        if not kernel32.GetConsoleMode(handle, ctypes.byref(mode)):
            return False
        return bool(kernel32.SetConsoleMode(handle, mode.value | 0x0004))
    except Exception:
        return False


def use_colour(disabled: bool) -> bool:
    if disabled or os.environ.get("NO_COLOR") or not sys.stdout.isatty():
        return False
    if os.name == "nt":
        return _enable_windows_ansi()
    return True


def _size(n: int) -> str:
    return f"{n} B" if n < 1024 else f"{n / 1024:.1f} kB"


class Reporter:
    def __init__(self, style: Style, verbose: bool) -> None:
        self.s = style
        self.verbose = verbose

    def header(self, cfg: Config) -> None:
        print(self.s.bold(COLLECTION_NAME))
        print(f"  target  {cfg.base_url}")
        print(f"  user    {cfg.username}")

    def request_start(self, name: str) -> None:
        print()
        print(self.s.bold(f"> {name}"))

    def request_sent(self, r: Response) -> None:
        status = f"{r.status} {r.reason}".strip()
        status = self.s.green(status) if 200 <= r.status < 300 else self.s.red(status)
        print(f"  {r.method} {r.url}  " + self.s.dim("[") + status
              + self.s.dim(f", {_size(len(r.body))}, {r.elapsed_ms:.0f} ms]"))

    def check_done(self, c: CheckResult) -> None:
        if c.outcome == "pass":
            print(f"  {self.s.green('PASS')}  {c.name}")
        elif c.outcome == "skip":
            print(f"  {self.s.yellow('SKIP')}  {c.name}  " + self.s.dim(f"({c.message})"))
        else:
            print(f"  {self.s.red('FAIL')}  {c.name}")
            print(f"        {self.s.red(c.message)}")

    def request_error(self, message: str) -> None:
        print(f"  {self.s.red('ERROR')} {message}")

    def request_end(self, result: RequestResult) -> None:
        if self.verbose and result.failed and result.response is not None:
            text = result.response.text
            shown = text if len(text) <= 2000 else text[:2000] + "\n... (truncated)"
            print(self.s.dim("        response body:"))
            for line in (shown.splitlines() or ["(empty)"]):
                print(self.s.dim(f"        | {line}"))

    def summary(self, results: List[RequestResult], duration_s: float) -> None:
        checks = [c for r in results for c in r.checks]
        failed_checks = sum(c.outcome == "fail" for c in checks)
        skipped = sum(c.outcome == "skip" for c in checks)
        failed_requests = sum(r.failed for r in results)
        errors = sum(r.error is not None for r in results)
        times = [r.response.elapsed_ms for r in results if r.response is not None]

        line = "-" * 60
        print()
        print(line)
        print(f"  {'':10}{'run':>8}{'failed':>9}{'skipped':>10}")
        print(f"  {'requests':10}{len(results):>8}{failed_requests:>9}")
        print(f"  {'checks':10}{len(checks):>8}{failed_checks:>9}{skipped:>10}")
        print(line)
        print(f"  total time {duration_s:.1f} s", end="")
        if times:
            print(f", average response {sum(times) / len(times):.0f} ms", end="")
        print()

        problems: List[Tuple[str, Optional[str], str]] = []
        for r in results:  # in run order
            problems += [(r.name, c.name, c.message) for c in r.checks if c.outcome == "fail"]
            if r.error is not None:
                problems.append((r.name, None, r.error))
        if problems:
            print()
            print(self.s.bold("Failures"))
            for i, (req, check, msg) in enumerate(problems, 1):
                where = f"{req} > {check}" if check else f"{req} (request error)"
                print(f"  {i}. {where}")
                print(f"     {msg}")

        if any(r.response is not None and r.response.status == 401 for r in results):
            print()
            print(self.s.yellow("Hint: the server answered 401 Unauthorized. Check the username and "
                                "password, and that the station allows HTTP basic authentication."))

        print()
        if failed_requests:
            print(self.s.red(self.s.bold(
                f"FAILED: {failed_checks} check(s) failed, {errors} request error(s)")))
        else:
            passed = len(checks) - skipped
            note = f" ({skipped} skipped)" if skipped else ""
            print(self.s.green(self.s.bold(f"PASSED: {passed}/{passed} checks{note}")))


def write_junit(path: str, results: List[RequestResult], duration_s: float) -> None:
    """JUnit XML, readable by Jenkins, GitLab, GitHub Actions, Azure DevOps etc.
    One <testsuite> per request, one <testcase> per check."""
    root = ET.Element("testsuites", name=COLLECTION_NAME, time=f"{duration_s:.3f}")
    totals = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}
    for result in results:
        elapsed = (result.response.elapsed_ms / 1000.0) if result.response else 0.0
        suite = ET.SubElement(root, "testsuite", name=result.name, time=f"{elapsed:.3f}")
        counts = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}
        classname = f"nhaystack.{result.name}"
        for check in result.checks:
            case = ET.SubElement(suite, "testcase", classname=classname, name=check.name, time="0")
            counts["tests"] += 1
            if check.outcome == "fail":
                ET.SubElement(case, "failure", message=check.message).text = check.message
                counts["failures"] += 1
            elif check.outcome == "skip":
                ET.SubElement(case, "skipped", message=check.message)
                counts["skipped"] += 1
        if result.error is not None:
            case = ET.SubElement(suite, "testcase", classname=classname, name="request", time="0")
            ET.SubElement(case, "error", message=result.error).text = result.error
            counts["tests"] += 1
            counts["errors"] += 1
        if not result.checks and result.error is None:
            # A request with no checks still shows up as one passing case.
            ET.SubElement(suite, "testcase", classname=classname, name="request sent", time="0")
            counts["tests"] += 1
        for key, value in counts.items():
            suite.set(key, str(value))
            totals[key] += value
    for key, value in totals.items():
        root.set(key, str(value))
    tree = ET.ElementTree(root)
    if hasattr(ET, "indent"):  # Python 3.9+
        ET.indent(tree)
    tree.write(path, encoding="utf-8", xml_declaration=True)


def run(cfg: Config, selected: List[Tuple[str, Callable[..., None]]],
        reporter: Reporter) -> List[RequestResult]:
    variables: Dict[str, Any] = {}  # collection variables, e.g. watchId
    results: List[RequestResult] = []
    for name, fn in selected:
        result = RequestResult(name)
        results.append(result)
        reporter.request_start(name)
        try:
            fn(RequestContext(cfg, variables, result, reporter))
        except RequestAborted as exc:
            result.error = str(exc)
            reporter.request_error(result.error)
        except Exception as exc:  # a bug in a test function: report it, keep going
            result.error = f"{type(exc).__name__}: {exc}"
            reporter.request_error(result.error)
            if cfg.verbose:
                traceback.print_exc()
        reporter.request_end(result)
        if cfg.bail and result.failed:
            print()
            print("Stopping after the first failed request (--bail).")
            break
    return results


# =============================================================================
# 5. Command line
# =============================================================================

def parse_args(argv: Optional[List[str]]) -> argparse.Namespace:
    env = os.environ.get
    parser = argparse.ArgumentParser(
        description=f"Run the '{COLLECTION_NAME}' checks against an nHaystack server.",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""\
examples:
  python nhaystack_tests.py --base-url http://10.211.55.3:8081/haystack
  python nhaystack_tests.py --only watch --verbose
  python nhaystack_tests.py --junit results.xml --time-factor 3
  python nhaystack_tests.py --list

The password is best supplied in the NHAYSTACK_PASSWORD environment variable:
--password ends up in your shell history and the process list.""")
    parser.add_argument("--base-url", default=env("NHAYSTACK_BASE_URL", DEFAULT_BASE_URL),
                        help="Haystack API root, e.g. http://host:port/haystack "
                             "(env NHAYSTACK_BASE_URL; default %(default)s)")
    parser.add_argument("--username", default=env("NHAYSTACK_USERNAME", DEFAULT_USERNAME),
                        help="basic auth user (env NHAYSTACK_USERNAME; default %(default)s)")
    parser.add_argument("--password", default=env("NHAYSTACK_PASSWORD"),
                        help="basic auth password (env NHAYSTACK_PASSWORD; prompted if missing)")
    parser.add_argument("--only", action="append", metavar="NAME", default=[],
                        help="run only requests whose name contains NAME (case-insensitive); repeatable")
    parser.add_argument("--list", action="store_true", help="list the requests in run order and exit")
    parser.add_argument("--junit", metavar="FILE", help="also write results as JUnit XML to FILE")
    parser.add_argument("--time-factor", type=float, default=1.0, metavar="X",
                        help="multiply every response-time limit by X, e.g. 3 on a slow network "
                             "(default 1)")
    parser.add_argument("--no-timing", action="store_true", help="skip all response-time checks")
    parser.add_argument("--timeout", type=float, default=30.0, metavar="SECONDS",
                        help="give up on a request after this long (default 30)")
    parser.add_argument("--insecure", action="store_true",
                        help="don't verify HTTPS certificates (e.g. a station's self-signed cert)")
    parser.add_argument("--no-proxy", action="store_true",
                        help="ignore HTTP_PROXY / HTTPS_PROXY environment variables")
    parser.add_argument("--bail", action="store_true", help="stop after the first failed request")
    parser.add_argument("-v", "--verbose", action="store_true",
                        help="print the response body of any request that fails")
    parser.add_argument("--no-color", action="store_true", help="plain output without colours")
    args = parser.parse_args(argv)
    if args.time_factor <= 0:
        parser.error("--time-factor must be greater than 0")
    return args


def main(argv: Optional[List[str]] = None) -> int:
    # Never crash on a console that can't show a character (old Windows code pages).
    for stream in (sys.stdout, sys.stderr):
        if hasattr(stream, "reconfigure"):
            stream.reconfigure(errors="replace")

    args = parse_args(argv)

    selected = REQUESTS
    if args.only:
        wanted = [w.lower() for w in args.only]
        selected = [(n, f) for n, f in REQUESTS if any(w in n.lower() for w in wanted)]
        if not selected:
            print(f"No request name contains {' or '.join(map(repr, args.only))}. "
                  f"Use --list to see the names.", file=sys.stderr)
            return 2

    if args.list:
        for i, (name, _) in enumerate(selected, 1):
            print(f"{i:>3}. {name}")
        return 0

    password = args.password
    if password is None:
        if sys.stdin.isatty():
            password = getpass.getpass(f"Password for {args.username}: ")
        else:
            print("No password given. Set NHAYSTACK_PASSWORD or use --password.", file=sys.stderr)
            return 2

    cfg = Config(base_url=args.base_url, username=args.username, password=password,
                 timeout=args.timeout, time_factor=args.time_factor, no_timing=args.no_timing,
                 insecure=args.insecure, no_proxy=args.no_proxy, verbose=args.verbose,
                 bail=args.bail)
    reporter = Reporter(Style(use_colour(args.no_color)), args.verbose)
    reporter.header(cfg)

    start = time.perf_counter()
    try:
        results = run(cfg, selected, reporter)
    except KeyboardInterrupt:
        print("\nInterrupted.", file=sys.stderr)
        return 130
    duration = time.perf_counter() - start

    reporter.summary(results, duration)
    if args.junit:
        write_junit(args.junit, results, duration)
        print(f"JUnit report written to {args.junit}")
    return 1 if any(r.failed for r in results) else 0


if __name__ == "__main__":
    sys.exit(main())
