#!/usr/bin/env bash

curl --location --request GET 'http://localhost/haystack/read?filter=id==@C.Apps.EnumWritable' \
-H "Content-Type: text/zinc; charset=utf-8" \
--user "basicuser:C0ns3rv31tL0g1n"