//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Sep 2012  Brian Frank  Creation
//
package org.projecthaystack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

/**
 * HRow is a row in a HGrid.  It implements the HDict interface also.
 *
 * @see <a href='http://project-haystack.org/doc/Grids'>Project Haystack</a>
 */
public class HRow extends HDict
{
  /** Package private constructor */
  HRow(HGrid grid, HVal[] cells)
  {
    this.grid = grid;
    this.cells = cells;
  }

  /** Get the grid associated with this row */
  public HGrid grid() { return grid; }

  /** Number of columns in grid (which may map to null cells) */
  public int size() { return grid.cols.length; }

  /** Get a cell by column name.  If the column is undefined or
      the cell is null then raise UnknownNameException or return
      null based on checked flag. */
  public HVal get(String name, boolean checked)
  {
    HCol col = grid.col(name, false);
    if (col != null)
    {
      HVal val = cells[col.index];
      if (val != null) return val;
    }
    if (checked) throw new UnknownNameException(name);
    return null;
  }

  /** Get a cell by column.  If cell is null then raise
      UnknownNameException or return  null based on checked flag. */
  public HVal get(HCol col, boolean checked)
  {
    HVal val = cells[col.index];
    if (val != null) return val;
    if (checked) throw new UnknownNameException(col.name());
    return null;
  }

  /** Return Map.Entry name/value iterator which only includes
      non-null cells */
  public Iterator iterator()
  {
    return new RowIterator();
  }

//////////////////////////////////////////////////////////////////////////
// RowIterator
//////////////////////////////////////////////////////////////////////////

 class RowIterator implements Iterator
 {
   RowIterator()
   {
     for (; col < grid.cols.length; ++col)
       if (cells[col] != null) break;
   }

    public boolean hasNext()
    {
      return col < grid.cols.length;
    }

    public Object next()
    {
      if (col >= grid.cols.length) throw new NoSuchElementException();
      String name = grid.col(col).name();
      HVal val = cells[col];
      for (col++; col < grid.cols.length; ++col) if (cells[col] != null) break;
      return new MapEntry(name, val);
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }

    private int col = 0;
  }

//////////////////////////////////////////////////////////////////////////
// Fields
//////////////////////////////////////////////////////////////////////////

  private HGrid grid;
  private HVal[] cells;
}