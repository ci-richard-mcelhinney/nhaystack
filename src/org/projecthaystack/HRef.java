//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package org.projecthaystack;

/**
 * HRef wraps a string reference identifier and optional display name.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HRef extends HVal
{
  /** Construct for string identifier and optional display */
  public static HRef make(String val, String dis)
  {
    if (val == null || !isId(val)) throw new IllegalArgumentException("Invalid id val: \"" + val + "\"");
    return new HRef(val, dis);
  }

  /** Construct for string identifier and null display */
  public static HRef make(String val)
  {
    return make(val, null);
  }

  /** Private constructor */
  private HRef(String val, String dis) { this.val = val; this.dis = dis; }

  /** String identifier for reference */
  public final String val;

  /** Display name for reference or null */
  public final String dis;

  /** Hash code is based on val field only */
  public int hashCode() { return val.hashCode(); }

  /** Equals is based on val field only */
  public boolean equals(Object that)
  {
    if (!(that instanceof HRef)) return false;
    return this.val.equals(((HRef)that).val);
  }

  /** Return display string which is dis field if non-null, val field otherwise */
  public String dis()
  {
    if (dis != null) return dis;
    return val;
  }

  /** Return the val string */
  public String toString() { return val; }

  /** Encode as "@id" */
  public String toCode() { return "@" + val; }

  /** Encode as "r:<id> [dis]" */
  public String toJson()
  {
    StringBuffer s = new StringBuffer();
    s.append("r:").append(val);
    if (dis != null) s.append(' ').append(dis);
    return s.toString();
  }

  /** Encode as "@<id> [dis]" */
  public String toZinc()
  {
    StringBuffer s = new StringBuffer();
    s.append('@');
    s.append(val);
    if (dis != null)
    {
      s.append(' ');
      HStr.toZinc(s, dis);
    }
    return s.toString();
  }

  /** Return if the given string is a valid id for a reference */
  public static boolean isId(String id)
  {
    if (id.length() == 0) return false;
    for (int i=0; i<id.length(); ++i)
      if (!isIdChar(id.charAt(i))) return false;
    return true;
  }

  /** Is the given character valid in the identifier part */
  public static boolean isIdChar(int ch)
  {
    return ch >= 0 && ch < idChars.length && idChars[ch];
  }

  /** Singleton for the null ref */
  public static final HRef nullRef = new HRef("null",  null);

  private static boolean[] idChars = new boolean[127];
  static
  {
    for (int i='a'; i<='z'; ++i) idChars[i] = true;
    for (int i='A'; i<='Z'; ++i) idChars[i] = true;
    for (int i='0'; i<='9'; ++i) idChars[i] = true;
    idChars['_'] = true;
    idChars[':'] = true;
    idChars['-'] = true;
    idChars['.'] = true;
    idChars['~'] = true;
  }

}