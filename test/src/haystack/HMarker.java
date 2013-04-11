//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package haystack;

/**
 * HMarker is the singleton value for a marker tag.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HMarker extends HVal
{
  /** Singleton value */
  public static final HMarker VAL = new HMarker();

  private HMarker() {}

  /** Hash code */
  public int hashCode() { return 0x1379de; }

  /** Equals is based on reference */
  public boolean equals(Object that) { return this == that; }

  /** Encode as "marker" */
  public String toString() { return "marker"; }

  /** Encode as "M" */
  public String toZinc() { return "M"; }

}