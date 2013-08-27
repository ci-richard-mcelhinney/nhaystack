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
 * HDate models a date (day in year) tag value.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HDate extends HVal
{
  /** Construct from basic fields */
  public static HDate make(int year, int month, int day)
  {
    if (year < 1900) throw new IllegalArgumentException("Invalid year");
    if (month < 1 || month > 12) throw new IllegalArgumentException("Invalid month");
    if (day < 1 || day > 31) throw new IllegalArgumentException("Invalid day");
    return new HDate(year, month, day);
  }

  /** Construct from Java calendar instance */
  public static HDate make(Calendar c)
  {
    return new HDate(c.get(Calendar.YEAR),
                     c.get(Calendar.MONTH) + 1,
                     c.get(Calendar.DAY_OF_MONTH));
  }

  /** Parse from string fomat "YYYY-MM-DD" or raise ParseException */
  public static HDate make(String s)
  {
    HVal val = new HZincReader(s).readScalar();
    if (val instanceof HDate) return (HDate)val;
    throw new ParseException(s);
  }

  /** Get HDate for current time in default timezone */
  public static HDate today()
  {
    return HDateTime.now().date;
  }

  /** Private constructor */
  private HDate(int year, int month, int day)
  {
    this.year  = year;
    this.month = month;
    this.day   = day;
  }

  /** Hash is based on year, month, day */
  public int hashCode()
  {
    return (year << 16) ^ (month << 8) ^ day;
  }

    /** Equals is based on year, month, day */
  public boolean equals(Object that)
  {
    if (!(that instanceof HDate)) return false;
    HDate x = (HDate)that;
    return year == x.year && month == x.month && day == x.day;
  }

  /** Return sort order as negative, 0, or positive */
  public int compareTo(Object that)
  {
    HDate x = (HDate)that;
    if (year < x.year)   return -1; else if (year > x.year)   return 1;
    if (month < x.month) return -1; else if (month > x.month) return 1;
    if (day < x.day)     return -1; else if (day > x.day)     return 1;
    return 0;
  }

  /** Four digit year such as 2011 */
  public final int year;

  /** Month as 1-12 (Jan is 1, Dec is 12) */
  public final int month;

  /** Day of month as 1-31 */
  public final int day;

  /** Encode as "YYYY-MM-DD" */
  public String toZinc()
  {
    StringBuffer s = new StringBuffer();
    toZinc(s);
    return s.toString();
  }

  /** Package private implementation shared with HDateTime */
  void toZinc(StringBuffer s)
  {
    s.append(year).append('-');
    if (month < 10) s.append('0'); s.append(month).append('-');
    if (day < 10) s.append('0');   s.append(day);
  }

  /** Convert this date into HDateTime for midnight in given timezone. */
  public HDateTime midnight(HTimeZone tz)
  {
    return HDateTime.make(this, HTime.MIDNIGHT, tz);
  }

  /** Return date in future given number of days */
  public HDate plusDays(int numDays)
  {
    if (numDays == 0) return this;
    if (numDays < 0) return minusDays(-numDays);
    int year  = this.year;
    int month = this.month;
    int day   = this.day;
    for (; numDays > 0; --numDays)
    {
      day++;
      if (day > daysInMonth(year, month))
      {
        day = 1;
        month++;
        if (month > 12) { month = 1; year++; }
      }
    }
    return make(year, month, day);
  }

  /** Return date in past given number of days */
  public HDate minusDays(int numDays)
  {
    if (numDays == 0) return this;
    if (numDays < 0) return plusDays(-numDays);
    int year  = this.year;
    int month = this.month;
    int day   = this.day;
    for (; numDays > 0; --numDays)
    {
      day--;
      if (day <= 0)
      {
        month--;
        if (month < 1) { month = 12; year--; }
        day = daysInMonth(year, month);
      }
    }
    return make(year, month, day);
  }

  /** Return if given year a leap year */
  public static boolean isLeapYear(int year)
  {
    if ((year & 3) != 0) return false;
    return (year % 100 != 0) || (year % 400 == 0);
  }

  /** Return number of days in given year (2xxx) and month (1-12) */
  private static int daysInMonth(int year, int mon)
  {
    return isLeapYear(year) ? daysInMonLeap[mon] : daysInMon[mon];
  }
  private static final int daysInMon[]     = { -1, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  private static final int daysInMonLeap[] = { -1, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

}