//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package haystack;

import java.util.Calendar;
import org.projecthaystack.io.HZincReader;

/**
 * HTime models a time of day tag value.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HTime extends HVal
{
  /** Construct with all fields */
  public static HTime make(int hour, int min, int sec, int ms)
  {
    if (hour < 0 || hour > 23)  throw new IllegalArgumentException("Invalid hour");
    if (min  < 0 || min  > 59)  throw new IllegalArgumentException("Invalid min");
    if (sec  < 0 || sec  > 59)  throw new IllegalArgumentException("Invalid sec");
    if (ms   < 0 || ms   > 999) throw new IllegalArgumentException("Invalid ms");
    return new HTime(hour, min, sec, ms);
  }

  /** Convenience constructing with ms = 0 */
  public static HTime make(int hour, int min, int sec)
  {
    return make(hour, min, sec, 0);
  }

  /** Convenience constructing with sec = 0 and ms = 0 */
  public static HTime make(int hour, int min)
  {
    return make(hour, min, 0, 0);
  }

  /** Initialize from Java calendar instance */
  public static HTime make(Calendar c)
  {
    return new HTime(c.get(Calendar.HOUR_OF_DAY),
                     c.get(Calendar.MINUTE),
                     c.get(Calendar.SECOND),
                     c.get(Calendar.MILLISECOND));
  }

  /** Parse from string fomat "hh:mm:ss.FF" or raise ParseException */
  public static HTime make(String s)
  {
    HVal val = new HZincReader(s).readScalar();
    if (val instanceof HTime) return (HTime)val;
    throw new ParseException(s);
  }

  /** Singleton for midnight 00:00 */
  public static final HTime MIDNIGHT = new HTime(0, 0, 0, 0);

  /** Private constructor */
  private HTime(int hour, int min, int sec, int ms)
  {
    this.hour = hour;
    this.min  = min;
    this.sec  = sec;
    this.ms   = ms;
  }

  /** Hour of day as 0-23 */
  public final int hour;

  /** Minute of hour as 0-59 */
  public final int min;

  /** Second of minute as 0-59 */
  public final int sec;

  /** Fractional seconds in milliseconds 0-999 */
  public final int ms;

  /** Hash is based on hour, min, sec, ms */
  public int hashCode()
  {
    return (hour << 24) ^ (min << 20) ^ (sec << 16) ^ ms;
  }

  /** Equals is based on year, month, day */
  public boolean equals(Object that)
  {
    if (!(that instanceof HTime)) return false;
    HTime x = (HTime)that;
    return hour == x.hour && min == x.min && sec == x.sec && ms == x.ms;
  }

  /** Return sort order as negative, 0, or positive */
  public int compareTo(Object that)
  {
    HTime x = (HTime)that;
    if (hour < x.hour) return -1; else if (hour > x.hour) return 1;
    if (min < x.min)   return -1; else if (min > x.min)   return 1;
    if (sec < x.sec)   return -1; else if (sec > x.sec)   return 1;
    if (ms < x.ms)     return -1; else if (ms > x.ms)     return 1;

    return 0;
  }

  /** Encode as "hh:mm:ss.FFF" */
  public String toZinc()
  {
    StringBuffer s = new StringBuffer();
    toZinc(s);
    return s.toString();
  }

  /** Package private implementation shared with HDateTime */
  void toZinc(StringBuffer s)
  {
    if (hour < 10) s.append('0'); s.append(hour).append(':');
    if (min  < 10) s.append('0'); s.append(min).append(':');
    if (sec  < 10) s.append('0'); s.append(sec);
    if (ms != 0)
    {
      s.append('.');
      if (ms < 10) s.append('0');
      if (ms < 100) s.append('0');
      s.append(ms);
    }
  }

}