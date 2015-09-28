//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Nov 2011  Brian Frank  Creation
//
package org.projecthaystack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

/**
 * HHisItem is used to model a timestamp/value pair
 *
 * @see <a href='http://project-haystack.org/doc/Ops#hisRead'>Project Haystack</a>
 */
public class HHisItem extends HDict
{
  /** Map HGrid to HHisItem[].  Grid must have ts and val columns. */
  public static HHisItem[] gridToItems(HGrid grid)
  {
    HCol ts  = grid.col("ts");
    HCol val = grid.col("val");
    HHisItem[] items = new HHisItem[grid.numRows()];
    for (int i=0; i<items.length; ++i)
    {
      HRow row = grid.row(i);
      items[i] = new HHisItem((HDateTime)row.get(ts, true), row.get(val, false));
    }
    return items;
  }

  /** Construct from timestamp, value */
  public static HHisItem make(HDateTime ts, HVal val)
  {
    if (ts == null || val == null) throw new IllegalArgumentException("ts or val is null");
    return new HHisItem(ts, val);
  }

  /** Private constructor */
  private HHisItem(HDateTime ts, HVal val)
  {
    this.ts = ts;
    this.val = val;
  }

  /** Timestamp of history sample */
  public final HDateTime ts;

  /** Value of history sample */
  public final HVal val;

  public int size() { return 2; }

  public HVal get(String name, boolean checked)
  {
    if (name.equals("ts")) return ts;
    if (name.equals("val")) return val;
    if (!checked) return null;
    throw new UnknownNameException(name);
  }

  public Iterator iterator() { return new FixedIterator(); }

  class FixedIterator implements Iterator
  {
    public boolean hasNext() { return cur < 1; }
    public Object next()
    {
      ++cur;
      if (cur == 0) return new MapEntry("ts", ts);
      if (cur == 1) return new MapEntry("val", val);
      throw new NoSuchElementException();
    }
    public void remove() { throw new UnsupportedOperationException(); }
    int cur = -1;
  }

}