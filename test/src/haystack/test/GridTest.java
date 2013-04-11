//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Sep 2012  Brian Frank  Creation
//
package haystack.test;

import haystack.*;
import java.util.*;

/**
 * GridTest tests the HGrid class
 */
public class GridTest extends Test
{
  public void testEmpty()
  {
    HGrid g = new HGridBuilder().toGrid();
    verifyEq(g.meta(), HDict.EMPTY);
    verifyEq(g.numRows(), 0);
    verifyEq(g.isEmpty(), true);
    verifyEq(g.col("foo", false), null);
    try { g.col("foo"); fail(); } catch (UnknownNameException e) { verifyException(e); }
  }

  public void testNoRows()
  {
    HGridBuilder b = new HGridBuilder();
    b.meta().add("dis", "Title");
    b.addCol("a").add("dis", "Alpha");
    b.addCol("b");
    HGrid g = b.toGrid();

    // meta
    verifyEq(g.meta().size(), 1);
    verifyEq(g.meta().get("dis"), HStr.make("Title"));

    // cols
    HCol c;
    verifyEq(g.numCols(), 2);
    c = verifyCol(g, 0, "a");
    verifyEq(c.dis(), "Alpha");
    verifyEq(c.meta().size(), 1);
    verifyEq(c.meta().get("dis"), HStr.make("Alpha"));

    // rows
    verifyEq(g.numRows(), 0);
    verifyEq(g.isEmpty(), true);

    // iterator
    verifyGridIterator(g);
  }

  public void testSimple()
  {
    HGridBuilder b = new HGridBuilder();
    b.addCol("id");
    b.addCol("dis");
    b.addCol("area");
    b.addRow(new HVal[] { HRef.make("a"), HStr.make("Alpha"), HNum.make(1200) });
    b.addRow(new HVal[] { HRef.make("b"), HStr.make("Beta"), null });

    // meta
    HGrid g = b.toGrid();
    verifyEq(g.meta().size(), 0);

    // cols
    HCol c;
    verifyEq(g.numCols(), 3);
    verifyCol(g, 0, "id");
    verifyCol(g, 1, "dis");
    verifyCol(g, 2, "area");

    // rows
    verifyEq(g.numRows(), 2);
    verifyEq(g.isEmpty(), false);
    HRow r;
    r = g.row(0);
    verifyEq(r.get("id"), HRef.make("a"));
    verifyEq(r.get("dis"), HStr.make("Alpha"));
    verifyEq(r.get("area"), HNum.make(1200));
    r = g.row(1);
    verifyEq(r.get("id"), HRef.make("b"));
    verifyEq(r.get("dis"), HStr.make("Beta"));
    verifyEq(r.get("area", false), null);
    try { r.get("area"); fail(); } catch (UnknownNameException e) { verifyException(e); }
    verifyEq(r.get("fooBar", false), null);
    try { r.get("fooBar"); fail(); } catch (UnknownNameException e) { verifyException(e); }

    // HRow.iterator no-nulls
    Iterator it = g.row(0).iterator();
    verifyRowIterator(it, "id",   HRef.make("a"));
    verifyRowIterator(it, "dis",  HStr.make("Alpha"));
    verifyRowIterator(it, "area", HNum.make(1200));
    verifyEq(it.hasNext(), false);

    // HRow.iterator with nulls
    it = g.row(1).iterator();
    verifyRowIterator(it, "id",  HRef.make("b"));
    verifyRowIterator(it, "dis", HStr.make("Beta"));
    verifyEq(it.hasNext(), false);

    // iterator
    verifyGridIterator(g);
  }

  HCol verifyCol(HGrid g, int i, String n)
  {
    HCol col = g.col(i);
    verify(g.col(i) == g.col(n));
    verifyEq(col.name(), n);
    return col;
  }

  void verifyRowIterator(Iterator it, String name, HVal val)
  {
    verifyEq(it.hasNext(), true);
    Map.Entry entry = (Map.Entry)it.next();
    verifyEq(entry.getKey(), name);
    verifyEq(entry.getValue(), val);
  }

  void verifyGridIterator(HGrid g)
  {
    Iterator it = g.iterator();
    int c = 0;
    while (c < g.numRows())
    {
      verifyEq(it.hasNext(), true);
      verify(it.next() == g.row(c++));
    }
    verifyEq(it.hasNext(), false);
    verifyEq(c, g.numRows());
  }

}