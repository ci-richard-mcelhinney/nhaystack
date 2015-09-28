//
// Copyright (c) 2015, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Jun 2015  Brian Frank  Creation
//
package org.projecthaystack.test;

import org.projecthaystack.*;
import org.projecthaystack.io.*;

/**
 * JsonTest tests the JSON reader/writer
 */
public class JsonTest extends Test
{

  public void testWriter()
  {
    HGridBuilder gb = new HGridBuilder();
    gb.addCol("a");
    gb.addCol("b");
    gb.addRow(new HVal[] { null, HBool.TRUE });
    gb.addRow(new HVal[] { HMarker.VAL, null });
    gb.addRow(new HVal[] { HRemove.VAL, HNA.VAL });
    gb.addRow(new HVal[] { HStr.make("test"), HStr.make("with:colon") });
    gb.addRow(new HVal[] { HNum.make(12), HNum.make(72.3, "\u00b0F") });
    gb.addRow(new HVal[] { HNum.make(Double.NEGATIVE_INFINITY), HNum.make(Double.NaN) });
    gb.addRow(new HVal[] { HDate.make(2015, 6, 9), HTime.make(1, 2, 3) });
    gb.addRow(new HVal[] { HDateTime.make(1307377618069L, HTimeZone.make("New_York")), HUri.make("foo.txt") });
    gb.addRow(new HVal[] { HRef.make("abc"), HRef.make("abc", "A B C") });
    gb.addRow(new HVal[] { HBin.make("text/plain"), HCoord.make(90, -123) });
    HGrid grid = gb.toGrid();

    String actual = HJsonWriter.gridToString(grid);
    // System.out.println(actual);
    String[] lines = HStr.split(actual, '\n', false);
    verifyEq(lines[0], "{");
    verifyEq(lines[1], "\"meta\": {\"ver\":\"2.0\"},");
    verifyEq(lines[2], "\"cols\":[");
    verifyEq(lines[3], "{\"name\":\"a\"},");
    verifyEq(lines[4], "{\"name\":\"b\"}");
    verifyEq(lines[5], "],");
    verifyEq(lines[6], "\"rows\":[");
    verifyEq(lines[7], "{\"b\":true},");
    verifyEq(lines[8], "{\"a\":\"m:\"},");
    verifyEq(lines[9], "{\"a\":\"x:\", \"b\":\"z:\"},");
    verifyEq(lines[10], "{\"a\":\"test\", \"b\":\"s:with:colon\"},");
    verifyEq(lines[11], "{\"a\":\"n:12\", \"b\":\"n:72.3 \u00b0F\"},");
    verifyEq(lines[12], "{\"a\":\"n:-INF\", \"b\":\"n:NaN\"},");
    verifyEq(lines[13], "{\"a\":\"d:2015-06-09\", \"b\":\"h:01:02:03\"},");
    verifyEq(lines[14], "{\"a\":\"t:2011-06-06T12:26:58.069-04:00 New_York\", \"b\":\"u:foo.txt\"},");
    verifyEq(lines[15], "{\"a\":\"r:abc\", \"b\":\"r:abc A B C\"},");
    verifyEq(lines[16], "{\"a\":\"b:text/plain\", \"b\":\"c:90.0,-123.0\"}");
    verifyEq(lines[17], "]");
    verifyEq(lines[18], "}");
  }

}