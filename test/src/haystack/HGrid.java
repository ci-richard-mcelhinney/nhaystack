//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Sep 2012  Brian Frank  Creation
//
package haystack;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import haystack.io.*;

/**
 * HGrid is an immutable two dimension data structure of cols and rows.
 * Use HGridBuilder to construct a HGrid instance.
 *
 * @see <a href='http://project-haystack.org/doc/Grids'>Project Haystack</a>
 */
public class HGrid
{

//////////////////////////////////////////////////////////////////////////
// Package private constructor
//////////////////////////////////////////////////////////////////////////

  /** Empty grid with one column called "empty" and zero rows */
  public static final HGrid EMPTY = new HGrid(
    HDict.EMPTY,
    new HCol[] { new HCol(0, "empty", HDict.EMPTY) },
    new ArrayList());

  /** Package private constructor */
  HGrid(HDict meta, HCol[] cols, ArrayList rowList)
  {
    this.meta = meta;
    this.cols = cols;

    if (meta == null)
        throw new IllegalStateException("metadata cannot be null");

    this.rows = new HRow[rowList.size()];
    for (int i=0; i<rows.length; ++i)
    {
      HVal[] cells = (HVal[])rowList.get(i);
      if (cols.length != cells.length)
        throw new IllegalStateException("Row cells size != cols size");
      this.rows[i] = new HRow(this, cells);
    }

    this.colsByName = new HashMap();
    for (int i=0; i<cols.length; ++i)
    {
      HCol col = cols[i];
      String colName = col.name;
      if (colsByName.get(colName) != null)
        throw new IllegalStateException("Duplicate col name: " + colName);
      colsByName.put(colName, col);
    }
  }

//////////////////////////////////////////////////////////////////////////
// Access
//////////////////////////////////////////////////////////////////////////

  /** Return grid level meta */
  public HDict meta() { return meta; }

  /** Error grid have the meta.err marker tag */
  public boolean isErr() { return meta.has("err"); }

  /** Return if number of rows is zero */
  public boolean isEmpty() { return numRows() == 0; }

  /** Return number of rows */
  public int numRows() { return rows.length; }

  /** Get a row by its zero based index */
  public HRow row(int row) { return rows[row]; }

  /** Get number of columns  */
  public int numCols() { return cols.length; }

  /** Get a column by its index */
  public HCol col(int index) { return cols[index]; }

  /** Convenience for "col(name, true)" */
  public HCol col(String name) { return col(name, true); }

  /** Get a column by name.  If not found and checked if false then
      return null, otherwise throw UnknownNameException */
  public HCol col(String name, boolean checked)
  {
    HCol col = (HCol)colsByName.get(name);
    if (col != null) return col;
    if (checked) throw new UnknownNameException(name);
    return null;
  }

  /** Create iteratator to walk each row */
  public Iterator iterator()
  {
    return new GridIterator();
  }

//////////////////////////////////////////////////////////////////////////
// Debug
//////////////////////////////////////////////////////////////////////////

  /** Convenience for "dump(stdout)". */
  public void dump()
  {
    dump(new PrintWriter(System.out));
  }

  /** Debug dump - this is Zinc right now. */
  public void dump(PrintWriter out)
  {
    out.println(HZincWriter.gridToString(this));
    out.flush();
  }

//////////////////////////////////////////////////////////////////////////
// GridIterator
//////////////////////////////////////////////////////////////////////////

 class GridIterator implements Iterator
 {
    public boolean hasNext()
    {
      return pos < rows.length;
    }

    public Object next()
    {
      if (hasNext())
        return rows[pos++];
      else
        throw new NoSuchElementException();
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }

    private int pos = 0;
  }

//////////////////////////////////////////////////////////////////////////
// Rows
//////////////////////////////////////////////////////////////////////////

  final HRow[] rows;
  final HCol[] cols;
  final HashMap colsByName;
  final HDict meta;
}
