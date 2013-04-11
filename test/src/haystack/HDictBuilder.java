//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Jun 2011  Brian Frank  My birthday!
//
package haystack;

import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;

/**
 * HDictBuilder is used to construct an immutable HDict instance.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tags'>Project Haystack</a>
 */
public class HDictBuilder
{

  /** Convenience for <code>add(name, HMarker.VAL)</code> */
  public final HDictBuilder add(String name)
  {
    return add(name, HMarker.VAL);
  }

  /** Convenience for <code>add(name, HBool.make(val))</code> */
  public final HDictBuilder add(String name, boolean val)
  {
    return add(name, HBool.make(val));
  }

  /** Convenience for <code>add(name, HNum.make(val))</code> */
  public final HDictBuilder add(String name, long val)
  {
    return add(name, HNum.make(val));
  }

  /** Convenience for <code>add(name, HNum.make(val, unit))</code> */
  public final HDictBuilder add(String name, long val, String unit)
  {
    return add(name, HNum.make(val, unit));
  }

  /** Convenience for <code>add(name, HNum.make(val))</code> */
  public final HDictBuilder add(String name, double val)
  {
    return add(name, HNum.make(val));
  }

  /** Convenience for <code>add(name, HNum.make(val, unit))</code> */
  public final HDictBuilder add(String name, double val, String unit)
  {
    return add(name, HNum.make(val, unit));
  }

  /** Convenience for <code>add(name, HStr.make(val))</code> */
  public final HDictBuilder add(String name, String val)
  {
    return add(name, HStr.make(val));
  }

  /** Add all the name/value pairs in given HDict.  Return this. */
  public HDictBuilder add(HDict dict)
  {
    for (Iterator it = dict.iterator(); it.hasNext(); )
    {
      Entry entry = (Entry)it.next();
      add((String)entry.getKey(), (HVal)entry.getValue());
    }
    return this;
  }

  /** Add tag name and value.  Return this. */
  public HDictBuilder add(String name, HVal val)
  {
    if (map == null) map = new HashMap(37);
    map.put(name, val);
    return this;
  }

  /** Convert current state to an immutable HDict instance */
  public final HDict toDict()
  {
    if (map == null || map.isEmpty()) return HDict.EMPTY;
    HDict dict = new HDict.MapImpl(this.map);
    this.map = null;
    return dict;
  }

//////////////////////////////////////////////////////////////////////////
// Access
//////////////////////////////////////////////////////////////////////////

  /** Return if size is zero */
  public final boolean isEmpty() { return size() == 0; }

  /** Return number of tag name/value pairs */
  public int size() { return map.size(); }

  /** Return if the given tag is present */
  public final boolean has(String name) { return get(name, false) != null; }

  /** Return if the given tag is not present */
  public final boolean missing(String name) { return get(name, false) == null; }

  /** Convenience for "get(name, true)" */
  public final HVal get(String name) { return get(name, true); }

  /** Get a tag by name.  If not found and checked if false then
      return null, otherwise throw UnknownNameException */
  public HVal get(String name, boolean checked)
  {
      HVal val = (HVal)map.get(name);
      if (val != null) return val;
      if (!checked) return null;
      throw new UnknownNameException(name);
  }

  private HashMap map;
}
