//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package haystack;

/**
 * HVal is the base class for representing haystack tag
 * scalar values as an immutable class.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public abstract class HVal implements Comparable
{
  /** Package private constructor */
  HVal() {}

  /** String format is for human consumption only */
  public String toString() { return toZinc(); }

  /** Encode value to zinc format */
  public abstract String toZinc();

  /** Hash code is value based */
  public abstract int hashCode();

  /** Equality is value based */
  public abstract boolean equals(Object that);

  /** Return sort order as negative, 0, or positive */
  public int compareTo(Object that)
  {
    return toString().compareTo(that.toString());
  }

}