//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Sep 2012  Brian Frank  Creation
//
package haystack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * HCol is a column in a HGrid.
 *
 * @see <a href='http://project-haystack.org/doc/Grids'>Project Haystack</a>
 */
public class HCol
{

//////////////////////////////////////////////////////////////////////////
// Constructor
//////////////////////////////////////////////////////////////////////////

  /** Package private constructor */
  HCol(int index, String name, HDict meta)
  {
    this.index = index;
    this.name  = name;
    this.meta  = meta;
  }

//////////////////////////////////////////////////////////////////////////
// Access
//////////////////////////////////////////////////////////////////////////

  /** Return programatic name of column */
  public String name() { return name; }

  /** Return display name of column which is meta.dis or name */
  public String dis()
  {
    HVal dis = meta.get("dis", false);
    if (dis instanceof HStr) return ((HStr)dis).val;
    return name;
  }

  /** Column meta-data tags */
  public HDict meta() { return meta; };

//////////////////////////////////////////////////////////////////////////
// Identity
//////////////////////////////////////////////////////////////////////////

  /** Hash code is based on name and meta */
  public final int hashCode()
  {
    return (name.hashCode() << 13) ^ meta.hashCode();
  }

  /** Equality is name and meta */
  public final boolean equals(Object that)
  {
    if (!(that instanceof HCol)) return false;
    HCol x = (HCol)that;
    return name.equals(x.name) && meta.equals(x.meta);
  }

//////////////////////////////////////////////////////////////////////////
// Fields
//////////////////////////////////////////////////////////////////////////

  final int index;
  final String name;
  final HDict meta;
}