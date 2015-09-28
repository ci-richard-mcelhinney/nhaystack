//
// Copyright (c) 2015, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   20 Feb 2015  Brian Frank  Creation
//
package org.projecthaystack;

/**
 * HRemove is the singleton value used to indicate a tag remove.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HRemove extends HVal
{
  /** Singleton value */
  public static final HRemove VAL = new HRemove();

  private HRemove() {}

  /** Hash code */
  public int hashCode() { return 0x8ab3; }

  /** Equals is based on reference */
  public boolean equals(Object that) { return this == that; }

  /** Encode as "remove" */
  public String toString() { return "remove"; }

  /** Encode as "x:" */
  public String toJson() { return "x:"; }

  /** Encode as "R" */
  public String toZinc() { return "R"; }

}