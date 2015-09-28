//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Sep 2012  Brian Frank  Creation
//
package org.projecthaystack.test;

import org.projecthaystack.*;
import org.projecthaystack.io.*;

/**
 * ZincTest tests the zinc reader/writer
 */
public class ZincTest extends Test
{

//////////////////////////////////////////////////////////////////////////
// Auto-generated test code - else could you possibly write
// test code in a crappy language like Java
//////////////////////////////////////////////////////////////////////////

  public void test()
  {

    verifyGrid(
      "ver:\"2.0\"\n" +
      "fooBar33\n" +
      "\n",
      null,
      new Object[] {
         "fooBar33", null,
      },
      new HVal[][] {
      }
    );

    verifyGrid(
      "ver:\"2.0\" tag foo:\"bar\"\n" +
      "xyz\n" +
      "\"val\"\n" +
      "\n",
      new HDictBuilder().add("tag", HMarker.VAL).add("foo", HStr.make("bar")).toDict(),
      new Object[] {
         "xyz", null,
      },
      new HVal[][] {
        new HVal[] {HStr.make("val"), },
      }
    );

    verifyGrid(
      "ver:\"2.0\"\n" +
      "val\n" +
      "N\n" +
      "\n",
      null,
      new Object[] {
         "val", null,
      },
      new HVal[][] {
        new HVal[] {null, },
      }
    );

    verifyGrid(
      "ver:\"2.0\"\n" +
      "a,b\n" +
      "1,2\n" +
      "3,4\n" +
      "\n",
      null,
      new Object[] {
         "a", null,
         "b", null,
      },
      new HVal[][] {
        new HVal[] {HNum.make(1.0), HNum.make(2.0), },
        new HVal[] {HNum.make(3.0), HNum.make(4.0), },
      }
    );

    verifyGrid(
      "ver:\"2.0\" bg: Bin(image/jpeg) mark\n" +
      "file1 dis:\"F1\" icon: Bin(image/gif),file2 icon: Bin(image/jpg)\n" +
      "Bin(text/plain),N\n" +
      "4,Bin(image/png)\n" +
      "Bin(text/html; a=foo; bar=\"sep\"),Bin(text/html; charset=utf8)\n",
      new HDictBuilder().add("bg", HBin.make("image/jpeg")).add("mark", HMarker.VAL).toDict(),
      new Object[] {
         "file1", new HDictBuilder().add("icon", HBin.make("image/gif")).add("dis", HStr.make("F1")).toDict(),
         "file2", new HDictBuilder().add("icon", HBin.make("image/jpg")).toDict(),
      },
      new HVal[][] {
        new HVal[] {HBin.make("text/plain"), null, },
        new HVal[] {HNum.make(4.0), HBin.make("image/png"), },
        new HVal[] {HBin.make("text/html; a=foo; bar=\"sep\""), HBin.make("text/html; charset=utf8"), },
      }
    );

    verifyGrid(
      "ver:\"2.0\"\n" +
      "a,    b,      c,      d\n" +
      "T,    F,      N,   -99\n" +
      "2.3,  -5e-10, 2.4e20, 123e-10\n" +
      "\"\",   \"a\",   \"\\\" \\\\ \\t \\n \\r\", \"\\uabcd\"\n" +
      "`path`, @12cbb082-0c02ae73, 4s, -2.5min\n" +
      "M,R,Bin(image/png),Bin(image/png)\n" +
      "2009-12-31, 23:59:01, 01:02:03.123, 2009-02-03T04:05:06Z\n" +
      "INF, -INF, \"\", NaN\n" +
      "C(12,-34),C(0.123,-.789),C(84.5,-77.45),C(-90,180)\n" +
      "\n",
      null,
      new Object[] {
         "a", null,
         "b", null,
         "c", null,
         "d", null,
      },
      new HVal[][] {
        new HVal[] {HBool.TRUE, HBool.FALSE, null, HNum.make(-99.0), },
        new HVal[] {HNum.make(2.3), HNum.make(-5.0E-10), HNum.make(2.4E20), HNum.make(1.23E-8), },
        new HVal[] {HStr.make(""), HStr.make("a"), HStr.make("\" \\ \t \n \r"), HStr.make("\uabcd"), },
        new HVal[] {HUri.make("path"), HRef.make("12cbb082-0c02ae73", null), HNum.make(4.0, "s"), HNum.make(-2.5, "min"), },
        new HVal[] {HMarker.VAL, HRemove.VAL, HBin.make("image/png"), HBin.make("image/png"), },
        new HVal[] {HDate.make(2009, 12, 31), HTime.make(23, 59, 1, 0), HTime.make(1, 2, 3, 123), HDateTime.make(HDate.make(2009, 2, 3),HTime.make(4, 5, 6, 0),HTimeZone.make("UTC")), },
        new HVal[] {HNum.POS_INF, HNum.NEG_INF, HStr.make(""), HNum.NaN, },
        new HVal[] {HCoord.make(12.0, -34.0), HCoord.make(0.123, -0.789), HCoord.make(84.5, -77.45), HCoord.make(-90.0, 180.0), },
      }
    );

    verifyGrid(
      "ver:\"2.0\"\n" +
      "foo\n" +
      "`foo$20bar`\n" +
      "`foo\\`bar`\n" +
      "`file \\#2`\n" +
      "\"$15\"\n",
      null,
      new Object[] {
         "foo", null,
      },
      new HVal[][] {
        new HVal[] {HUri.make("foo$20bar"), },
        new HVal[] {HUri.make("foo`bar"), },
        new HVal[] {HUri.make("file \\#2"), },
        new HVal[] {HStr.make("$15"), },
      }
    );

    verifyGrid(
      "ver:\"2.0\"\n" +
      "a, b\n" +
      "-3.1kg,4kg\n" +
      "5%,3.2%\n" +
      "5kWh/ft\u00b2,-15kWh/m\u00b2\n" +
      "123e+12kJ/kg_dry,74\u0394\u00b0F\n",
      null,
      new Object[] {
         "a", null,
         "b", null,
      },
      new HVal[][] {
        new HVal[] {HNum.make(-3.1, "kg"), HNum.make(4.0, "kg"), },
        new HVal[] {HNum.make(5.0, "%"), HNum.make(3.2, "%"), },
        new HVal[] {HNum.make(5.0, "kWh/ft\u00b2"), HNum.make(-15.0, "kWh/m\u00b2"), },
        new HVal[] {HNum.make(1.23E14, "kJ/kg_dry"), HNum.make(74.0, "\u0394\u00b0F"), },
      }
    );

    verifyGrid(
      "ver:\"2.0\"\n" +
      "a, b, c\n" +
      ", 1, 2\n" +
      "3, , 5\n" +
      "6, 7_000,\n" +
      ",,10\n" +
      ",,\n" +
      "14,,\n" +
      "\n",
      null,
      new Object[] {
         "a", null,
         "b", null,
         "c", null,
      },
      new HVal[][] {
        new HVal[] {null, HNum.make(1.0), HNum.make(2.0), },
        new HVal[] {HNum.make(3.0), null, HNum.make(5.0), },
        new HVal[] {HNum.make(6.0), HNum.make(7000.0), null, },
        new HVal[] {null, null, HNum.make(10.0), },
        new HVal[] {null, null, null, },
        new HVal[] {HNum.make(14.0), null, null, },
      }
    );

    verifyGrid(
      "ver:\"2.0\"\n" +
      "a,b\n" +
      "2010-03-01T23:55:00.013-05:00 GMT+5,2010-03-01T23:55:00.013+10:00 GMT-10\n",
      null,
      new Object[] {
         "a", null,
         "b", null,
      },
      new HVal[][] {
        new HVal[] {HDateTime.make(HDate.make(2010, 3, 1),HTime.make(23, 55, 0, 13),HTimeZone.make("GMT+5")), HDateTime.make(HDate.make(2010, 3, 1),HTime.make(23, 55, 0, 13),HTimeZone.make("GMT-10")), },
      }
    );

    verifyGrid(
      "ver:\"2.0\" a: 2009-02-03T04:05:06Z foo b: 2010-02-03T04:05:06Z UTC bar c: 2009-12-03T04:05:06Z London baz\n" +
      "a\n" +
      "3.814697265625E-6\n" +
      "2010-12-18T14:11:30.924Z\n" +
      "2010-12-18T14:11:30.925Z UTC\n" +
      "2010-12-18T14:11:30.925Z London\n" +
      "45$\n" +
      "33\u00a3\n" +
      "@12cbb08e-0c02ae73\n" +
      "7.15625E-4kWh/ft\u00b2\n" +
      "R\n" +
      "NA\n",
      new HDictBuilder().add("b", HDateTime.make(HDate.make(2010, 2, 3),HTime.make(4, 5, 6, 0),HTimeZone.make("UTC"))).add("baz", HMarker.VAL).add("c", HDateTime.make(HDate.make(2009, 12, 3),HTime.make(4, 5, 6, 0),HTimeZone.make("London"))).add("a", HDateTime.make(HDate.make(2009, 2, 3),HTime.make(4, 5, 6, 0),HTimeZone.make("UTC"))).add("foo", HMarker.VAL).add("bar", HMarker.VAL).toDict(),
      new Object[] {
         "a", null,
      },
      new HVal[][] {
        new HVal[] {HNum.make(3.814697265625E-6), },
        new HVal[] {HDateTime.make(HDate.make(2010, 12, 18),HTime.make(14, 11, 30, 924),HTimeZone.make("UTC")), },
        new HVal[] {HDateTime.make(HDate.make(2010, 12, 18),HTime.make(14, 11, 30, 925),HTimeZone.make("UTC")), },
        new HVal[] {HDateTime.make(HDate.make(2010, 12, 18),HTime.make(14, 11, 30, 925),HTimeZone.make("London")), },
        new HVal[] {HNum.make(45.0, "$"), },
        new HVal[] {HNum.make(33.0, "\u00a3"), },
        new HVal[] {HRef.make("12cbb08e-0c02ae73", null), },
        new HVal[] {HNum.make(7.15625E-4, "kWh/ft\u00b2"), },
        new HVal[] {HRemove.VAL},
        new HVal[] {HNA.VAL},
      }
    );
  }

//////////////////////////////////////////////////////////////////////////
// Utils
//////////////////////////////////////////////////////////////////////////

  void verifyGrid(String str, HDict meta, Object[] cols, HVal[][] rows)
  {
    /*
    System.out.println();
    System.out.println("###############################################");
    System.out.println();
    System.out.println(str);
    */

    // normalize nulls
    if (meta == null) meta = HDict.EMPTY;
    for (int i=0; i<cols.length; ++i)
      if (cols[i] == null) cols[i] = HDict.EMPTY;

    // read from zinc
    HGrid grid = new HZincReader(str).readGrid();
    verifyGridEq(grid, meta, cols, rows);

    // write grid and verify we can parse that too
    String writeStr = HZincWriter.gridToString(grid);
    HGrid writeGrid = new HZincReader(writeStr).readGrid();
    verifyGridEq(writeGrid, meta, cols, rows);
  }

  void verifyGridEq(HGrid grid, HDict meta, Object[] cols, HVal[][] rows)
  {
    // meta
    verifyEq(grid.meta(), meta);

    // cols
    verifyEq(grid.numCols(), cols.length/2);
    for (int i=0; i<grid.numCols(); ++i)
    {
      verifyEq(grid.col(i).name(), cols[i*2+0]);
      verifyEq(grid.col(i).meta(), cols[i*2+1]);
    }

    // rows
    verifyEq(grid.numRows(), rows.length);
    for (int ri=0; ri<rows.length; ++ri)
    {
      HVal[] expected = rows[ri];
      HRow actual = grid.row(ri);
      for (int ci=0; ci<expected.length; ++ci)
      {
        verifyEq(expected[ci], actual.get(grid.col(ci).name(), false));
      }
    }
  }

}