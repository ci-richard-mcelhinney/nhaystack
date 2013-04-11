//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package haystack;

/**
 * HRef wraps a string reference identifier and optional display name.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HRef extends HIdentifier
{
  /** Construct for string identifier and optional display */
  public static HRef make(String val, String dis)
  {
    if (val == null || val.length() == 0) throw new IllegalArgumentException("Invalid id val: \"" + val + "\"");
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

  /** Display name for reference of null */
  public final String dis;

  /** Hash code is based on val field only */
  public int hashCode() { return val.hashCode(); }

  /** Equals is based on val field only */
  public boolean equals(Object that)
  {
    if (!(that instanceof HRef)) return false;
    return this.val.equals(((HRef)that).val);
  }

  /** Return the val string */
  public String toString() { return val; }

  /** Encode as "@id" */
  public String toCode() { return "@" + val; }

  /** Encode as "@id <dis>" */
  public String toZinc()
  {
    StringBuffer s = new StringBuffer();
    s.append('@');
    for (int i=0; i<val.length(); ++i)
    {
      int c = val.charAt(i);
      if (!isIdChar(c)) throw new IllegalArgumentException("Invalid ref val'" + val + "', char='" + (char)c + "'");
      s.append((char)c);
    }
    if (dis != null)
    {
      s.append(' ');
      HStr.toZinc(s, dis);
    }
    return s.toString();
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
