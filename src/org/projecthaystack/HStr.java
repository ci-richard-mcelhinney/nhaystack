//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package org.projecthaystack;

import java.util.ArrayList;

/**
 * HStr wraps a java.lang.String as a tag value.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HStr extends HVal
{
  /** Construct from java.lang.String value */
  public static HStr make(String val)
  {
    if (val == null) return null;
    if (val.length() == 0) return EMPTY;
    return new HStr(val);
  }

  /** Singleton value for empty string "" */
  private static final HStr EMPTY = new HStr("");

  /** Private constructor */
  private HStr(String val) { this.val = val; }

  /** String value */
  public final String val;

  /** Hash code is same as java.lang.String */
  public int hashCode() { return val.hashCode(); }

  /** Equals is based on java.lang.String */
  public boolean equals(Object that)
  {
    if (!(that instanceof HStr)) return false;
    return this.val.equals(((HStr)that).val);
  }

  /** Return value string. */
  public String toString()
  {
    return val;
  }

  /** Encode as "s:" if string contains a colon */
  public String toJson()
  {
    return val.indexOf(':') < 0 ? val : "s:"+val;
  }

  /** Encode using double quotes and back slash escapes */
  public String toZinc()
  {
    StringBuffer s = new StringBuffer();
    toZinc(s, val);
    return s.toString();
  }

  /** Encode using double quotes and back slash escapes */
  public static String toCode(String val)
  {
    StringBuffer s = new StringBuffer();
    toZinc(s, val);
    return s.toString();
  }

  /** Encode using double quotes and back slash escapes */
  static void toZinc(StringBuffer s, String val)
  {
    s.append('"');
    for (int i=0; i<val.length(); ++i)
    {
      int c = val.charAt(i);
      if (c < ' ' || c == '"' || c == '\\')
      {
        s.append('\\');
        switch (c)
        {
          case '\n':  s.append('n');  break;
          case '\r':  s.append('r');  break;
          case '\t':  s.append('t');  break;
          case '"':   s.append('"');  break;
          case '\\':  s.append('\\'); break;
          default:
            s.append('u').append('0').append('0');
            if (c <= 0xf) s.append('0');
            s.append(Integer.toHexString(c));
        }
      }
      else
      {
        s.append((char)c);
      }
    }
    s.append('"');
  }

  /**
   * Custom split routine so we don't have depend on Java 1.5 regex
   */
  public static String[] split(String str, int separator, boolean trim)
  {
    ArrayList toks = new ArrayList(16);
    int len = str.length();
    int x = 0;
    for (int i=0; i<len; ++i)
    {
      if (str.charAt(i) != separator) continue;
      if (x <= i) toks.add(splitStr(str, x, i, trim));
      x = i+1;
    }
    if (x <= len) toks.add(splitStr(str, x, len, trim));
    return (String[])toks.toArray(new String[toks.size()]);
  }

  private static String splitStr(String val, int s, int e, boolean trim)
  {
    if (trim)
    {
      while (s < e && val.charAt(s) <= ' ') ++s;
      while (e > s && val.charAt(e-1) <= ' ') --e;
    }
    return val.substring(s, e);
  }

}