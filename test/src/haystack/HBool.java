//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package haystack;

/**
 * HBool defines singletons for true/false tag values.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HBool extends HVal
{
  /** Construct from boolean value */
  public static HBool make(boolean val) { return val ? TRUE : FALSE;  }

  /** Singleton value for true */
  public static final HBool TRUE = new HBool(true);

  /** Singleton value for false */
  public static final HBool FALSE = new HBool(false);

  /** Private constructor */
  private HBool(boolean val) { this.val = val; }

  /** Boolean value */
  public final boolean val;

  /** Hash code is same as java.lang.Boolean */
  public int hashCode() { return val ? 1231 : 1237; }

  /** Equals is based on reference */
  public boolean equals(Object that) { return this == that; }

  /** Encode as "true" or "false" */
  public String toString() { return val ? "true" : "false"; }

  /** Encode as "T" or "F" */
  public String toZinc() { return val ? "T" : "F"; }

}