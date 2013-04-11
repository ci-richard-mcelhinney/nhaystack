//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Nov 2011  Brian Frank  Creation
//
package haystack;

import haystack.io.HZincReader;

/**
 * HDateTimeRange models a starting and ending timestamp
 *
 * @see <a href='http://project-haystack.org/doc/Ops#hisRead'>Project Haystack</a>
 */
public class HDateTimeRange
{
  /**
   * Parse from string using the given timezone as context for
   * date based ranges.  The formats are:
   *  - "today"
   *  - "yesterday"
   *  - "{date}"
   *  - "{date},{date}"
   *  - "{dateTime},{dateTime}"
   *  - "{dateTime}"  // anything after given timestamp
   * Throw ParseException is invalid string format.
   */
  public static HDateTimeRange make(String str, HTimeZone tz)
  {
    // handle keywords
    str = str.trim();
    if (str.equals("today"))     return make(HDate.today(), tz);
    if (str.equals("yesterday")) return make(HDate.today().minusDays(1), tz);

    // parse scalars
    int comma = str.indexOf(',');
    HVal start = null, end = null;
    if (comma < 0)
    {
      start = new HZincReader(str).readScalar();
    }
    else
    {
      start = new HZincReader(str.substring(0, comma)).readScalar();
      end   = new HZincReader(str.substring(comma+1)).readScalar();
    }

    // figure out what we parsed for start,end
    if (start instanceof HDate)
    {
      if (end == null) return make((HDate)start, tz);
      if (end instanceof HDate) return make((HDate)start, (HDate)end, tz);
    }
    else if (start instanceof HDateTime)
    {
      if (end == null) return make((HDateTime)start, HDateTime.now(tz));
      if (end instanceof HDateTime) return make((HDateTime)start, (HDateTime)end);
    }

    throw new ParseException("Invalid HDateTimeRange: " + str);
  }

  /** Make for single date within given timezone */
  public static HDateTimeRange make(HDate date, HTimeZone tz)
  {
    return make(date, date, tz);
  }

  /** Make for inclusive dates within given timezone */
  public static HDateTimeRange make(HDate start, HDate end, HTimeZone tz)
  {
    return make(start.midnight(tz), end.plusDays(1).midnight(tz));
  }

  /** Make from two timestamps */
  public static HDateTimeRange make(HDateTime start, HDateTime end)
  {
    if (start.tz != end.tz) throw new IllegalArgumentException("start.tz != end.tz");
    return new HDateTimeRange(start, end);
  }

  /** Private constructor */
  private HDateTimeRange(HDateTime start, HDateTime end)
  {
    this.start = start;
    this.end   = end;
  }

  /** Return "start to end" */
  public String toString()
  {
    return start.toString() + "," + end.toString();
  }

  /** Inclusive starting timestamp */
  public final HDateTime start;

  /** Inclusive ending timestamp */
  public final HDateTime end;

}