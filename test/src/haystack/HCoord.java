//
// Copyright (c) 2013, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   31 Jan 2013  Brian Frank  Creation
//
package haystack;

import java.text.DecimalFormat;
import org.projecthaystack.io.HZincReader;

/**
 * HCoord models a geographic coordinate as latitude and longitude
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HCoord extends HVal
{
  /** Construct from basic fields */
  public static HCoord make(double lat, double lng)
  {
    return new HCoord((int)(lat * 1000000.0), (int)(lng * 1000000.0));
  }

  /** Parse from string fomat "C(lat,lng)" or raise ParseException */
  public static HCoord make(String s)
  {
    try
    {
      if (!s.startsWith("C(")) throw new Exception();
      if (!s.endsWith(")")) throw new Exception();
      int comma = s.indexOf(',');
      if (comma < 3) throw new Exception();
      String lat = s.substring(2, comma);
      String lng = s.substring(comma+1, s.length()-1);
      return make(Double.parseDouble(lat), Double.parseDouble(lng));
    }
    catch (Exception e)
    {
      throw new ParseException(s);
    }
  }

  /** Package private constructor */
  HCoord(int ulat, int ulng)
  {
    if (ulat < -90000000 || ulat > 90000000) throw new IllegalArgumentException("Invalid lat > +/- 90");
    if (ulng < -180000000 || ulng > 180000000) throw new IllegalArgumentException("Invalid lng > +/- 180");
    this.ulat = ulat;
    this.ulng = ulng;
  }

//////////////////////////////////////////////////////////////////////////
// Access
//////////////////////////////////////////////////////////////////////////

  /** Latitude in decimal degrees */
  public double lat() { return ulat / 1000000.0; }

  /** Longtitude in decimal degrees */
  public double lng() { return ulng / 1000000.0; }

  /** Latitude in micro-degrees */
  final int ulat;

  /** Longitude in micro-degrees */
  final int ulng;

  /** Hash is based on lat/lng */
  public int hashCode() { return (ulat << 7) ^ ulng; }

  /** Equality is based on lat/lng */
  public boolean equals(Object that)
  {
    if (!(that instanceof HCoord)) return false;
    HCoord x = (HCoord)that;
    return ulat == x.ulat && ulng == x.ulng;
  }

  /** Represented as "C(lat,lng)" */
  public String toZinc()
  {
    StringBuffer s = new StringBuffer();
    s.append("C(");
    uToStr(s, ulat);
    s.append(',');
    uToStr(s, ulng);
    s.append(")");
    return s.toString();
  }

  private void uToStr(StringBuffer s, int ud)
  {
    if (ud < 0) { s.append('-'); ud = -ud; }
    if (ud < 1000000.0)
    {
      s.append(new DecimalFormat("0.0#####").format(ud/1000000.0));
      return;
    }
    String x = String.valueOf(ud);
    int dot = x.length() - 6;
    int end = x.length();
    while (end > dot+1 && x.charAt(end-1) == '0') --end;
    for (int i=0; i<dot; ++i) s.append(x.charAt(i));
    s.append('.');
    for (int i=dot; i<end; ++i) s.append(x.charAt(i));
  }
}