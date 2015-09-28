//
// Copyright (c) 2015, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Jun 2015  Brian Frank  Creation
//
package org.projecthaystack;

/**
 * HNA is the singleton value used to indicate not available.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HNA extends HVal
{
  /** Singleton value */
  public static final HNA VAL = new HNA();

  private HNA() {}

  /** Hash code */
  public int hashCode() { return 0x6e61; }

  /** Equals is based on reference */
  public boolean equals(Object that) { return this == that; }

  /** Encode as "na" */
  public String toString() { return "na"; }

  /** Encode as "z:" */
  public String toJson() { return "z:"; }

  /** Encode as "NA" */
  public String toZinc() { return "NA"; }

}