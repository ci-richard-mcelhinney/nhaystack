//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Sep 2012  Brian Frank  Creation
//
package org.projecthaystack;

import java.io.*;
import java.util.*;

/**
 * HGridBuilder is used to construct an immutable HGrid instance.
 *
 * @see <a href='http://project-haystack.org/doc/Grids'>Project Haystack</a>
 */
public class HGridBuilder
{

//////////////////////////////////////////////////////////////////////////
// Utils
//////////////////////////////////////////////////////////////////////////

  /** Convenience to build one row grid from HDict. */
  public static HGrid dictToGrid(HDict dict)
  {
    HGridBuilder b = new HGridBuilder();
    Iterator it = dict.iterator();
    ArrayList cells = new ArrayList();
    while (it.hasNext())
    {
      Map.Entry entry = (Map.Entry)it.next();
      String name = (String)entry.getKey();
      HVal val = (HVal)entry.getValue();
      b.addCol(name);
      cells.add(val);
    }
    b.rows.add(cells.toArray(new HVal[cells.size()]));
    return b.toGrid();
  }

  /** Convenience to build grid from array of HDict.
      Any null entry will be row of all null cells. */
  public static HGrid dictsToGrid(HDict[] dicts)
  {
      return dictsToGrid(HDict.EMPTY, dicts);
  }

  /** Convenience to build grid from array of HDict.
      Any null entry will be row of all null cells. */
  public static HGrid dictsToGrid(HDict meta, HDict[] dicts)
  {
    if (dicts.length == 0) return new HGrid(
        meta,
        new HCol[] { new HCol(0, "empty", HDict.EMPTY) },
        new ArrayList());

    HGridBuilder b = new HGridBuilder();
    b.meta.add(meta);

    // collect column names
    HashMap colsByName = new HashMap();
    for (int i=0; i<dicts.length; ++i)
    {
      HDict dict = dicts[i];
      if (dict == null) continue;
      Iterator it = dict.iterator();
      while (it.hasNext())
      {
        Map.Entry entry = (Map.Entry)it.next();
        String name = (String)entry.getKey();
        if (colsByName.get(name) == null)
        {
          colsByName.put(name, name);
          b.addCol(name);
        }
      }
    }

    // if all dicts were null, handle special case
    // by creating a dummy column
    if (colsByName.size() == 0)
    {
      colsByName.put("empty", "empty");
      b.addCol("empty");
    }

    // now map rows
    int numCols = b.cols.size();
    for (int ri=0; ri<dicts.length; ++ri)
    {
      HDict dict = dicts[ri];
      HVal[] cells = new HVal[numCols];
      for (int ci=0; ci<numCols; ++ci)
      {
        if (dict == null)
          cells[ci] = null;
        else
          cells[ci] = dict.get(((BCol)b.cols.get(ci)).name, false);
      }
      b.rows.add(cells);
    }

    return b.toGrid();
  }

  /** Convenience to build an error grid from exception */
  public static HGrid errToGrid(Throwable e)
  {
    // Java sucks
    StringWriter sout = new StringWriter();
    PrintWriter pout = new PrintWriter(sout);
    e.printStackTrace(pout);
    pout.flush();
    String trace = sout.toString();
    StringBuffer temp = new StringBuffer(trace.length());
    for (int i=0; i<trace.length(); ++i)
    {
      int ch = trace.charAt(i);
      if (ch == '\t') temp.append("  ");
      else if (ch != '\r') temp.append((char)ch);
    }
    trace = temp.toString();

    HGridBuilder b = new HGridBuilder();
    b.meta().add("err")
            .add("dis", e.toString())
            .add("errTrace", trace);
    b.addCol("empty");
    return b.toGrid();
  }

  /** Convenience to build grid from array of HHisItem */
  public static HGrid hisItemsToGrid(HDict meta, HHisItem [] items)
  {
    HGridBuilder b = new HGridBuilder();
    b.meta.add(meta);
    b.addCol("ts");
    b.addCol("val");
    for (int i=0; i<items.length; ++i)
    {
      b.rows.add(new HVal[] { items[i].ts, items[i].val });
    }
    return b.toGrid();
  }

//////////////////////////////////////////////////////////////////////////
// Building
//////////////////////////////////////////////////////////////////////////

  /** Get the builder for the grid meta map */
  public final HDictBuilder meta()
  {
    return meta;
  }

  /** Add new column and return builder for column metadata.
      Columns cannot be added after adding the first row. */
  public final HDictBuilder addCol(String name)
  {
    if (rows.size() > 0)
      throw new IllegalStateException("Cannot add cols after rows have been added");
    if (!HDict.isTagName(name))
      throw new IllegalArgumentException("Invalid column name: " + name);
    BCol col = new BCol(name);
    cols.add(col);
    return col.meta;
  }

  /** Add new row with array of cells which correspond to column
      order.  Return this. */
  public final HGridBuilder addRow(HVal[] cells)
  {
    if (cols.size() != cells.length)
      throw new IllegalStateException("Row cells size != cols size");
    rows.add((HVal[])cells.clone());
    return this;
  }

  /** Convert current state to an immutable HGrid instance */
  public final HGrid toGrid()
  {
    // meta
    HDict meta = this.meta.toDict();

    // cols
    HCol[] hcols = new HCol[this.cols.size()];
    for (int i=0; i<hcols.length; ++i)
    {
      BCol bc = (BCol)this.cols.get(i);
      hcols[i] = new HCol(i, bc.name, bc.meta.toDict());
    }

    // let HGrid constructor do the rest...
    return new HGrid(meta, hcols, rows);
  }

//////////////////////////////////////////////////////////////////////////
// BCol
//////////////////////////////////////////////////////////////////////////

  static class BCol
  {
    BCol(String name) { this.name = name; }
    final String name;
    final HDictBuilder meta = new HDictBuilder();
  }

//////////////////////////////////////////////////////////////////////////
// Fields
//////////////////////////////////////////////////////////////////////////

  private final HDictBuilder meta = new HDictBuilder();
  private final ArrayList cols = new ArrayList();
  private final ArrayList rows = new ArrayList();
}