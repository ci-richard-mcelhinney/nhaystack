//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   21 Sep 2012  Brian Frank  Creation
//
package org.projecthaystack;

/**
 * HBin models a binary file with a MIME type.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HBin extends HVal
{
  /** Construct for MIME type */
  public static HBin make(String mime)
  {
    if (mime == null || mime.length() == 0 || mime.indexOf('/') < 0)
      throw new IllegalArgumentException("Invalid mime val: \"" + mime + "\"");
    return new HBin(mime);
  }

  /** Private constructor */
  private HBin(String mime) { this.mime = mime; }

  /** MIME type for binary file */
  public final String mime;

  /** Hash code is based on mime field */
  public int hashCode() { return mime.hashCode(); }

  /** Equals is based on mime field */
  public boolean equals(Object that)
  {
    if (!(that instanceof HBin)) return false;
    return this.mime.equals(((HBin)that).mime);
  }

  /** Encode as "b:<mime>" */
  public String toJson()
  {
    StringBuffer s = new StringBuffer();
    s.append("b:");
    encodeMime(s);
    return s.toString();
  }

  /** Encode as "Bin(<mime>)" */
  public String toZinc()
  {
    StringBuffer s = new StringBuffer();
    s.append("Bin(");
    encodeMime(s);
    s.append(')');
    return s.toString();
  }

  private void encodeMime(StringBuffer s)
  {
    for (int i=0; i<mime.length(); ++i)
    {
      int c = mime.charAt(i);
      if (c > 127 || c == ')') throw new IllegalArgumentException("Invalid mime, char='" + (char)c + "'");
      s.append((char)c);
    }
  }

}