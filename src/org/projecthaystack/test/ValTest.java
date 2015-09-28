//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package org.projecthaystack.test;

import java.util.*;
import org.projecthaystack.*;
import org.projecthaystack.io.*;

/**
 * ValTest tests the scalar value HVal types
 */
public class ValTest extends Test
{
  public void testMarker()
  {
    // equality
    verifyEq(HMarker.VAL, HMarker.VAL);

    // toString
    verifyEq(HMarker.VAL.toString(), "marker");

    // zinc
    verifyZinc(HMarker.VAL, "M");
  }

  public void testBool()
  {
    // equality
    verifyEq(HBool.TRUE, HBool.TRUE);
    verifyNotEq(HBool.TRUE, HBool.FALSE);
    verify(HBool.make(true) == HBool.TRUE);
    verify(HBool.make(false) == HBool.FALSE);

    // compare
    verify(HBool.FALSE.compareTo(HBool.TRUE) < 0);
    verify(HBool.TRUE.compareTo(HBool.TRUE) == 0);

    // toString
    verifyEq(HBool.TRUE.toString(), "true");
    verifyEq(HBool.FALSE.toString(), "false");

    // zinc
    verifyZinc(HBool.TRUE, "T");
    verifyZinc(HBool.FALSE, "F");
  }

  public void testNum()
  {
    // equality
    verifyEq(HNum.make(2), HNum.make(2.0, null));
    verifyNotEq(HNum.make(2), HNum.make(2, "%"));
    verifyNotEq(HNum.make(2, "%"), HNum.make(2));
    verify(HNum.make(0) == HNum.make(0.0));

    // compare
    verify(HNum.make(9).compareTo(HNum.make(11)) < 0);
    verify(HNum.make(-3).compareTo(HNum.make(-4)) > 0);
    verify(HNum.make(-23).compareTo(HNum.make(-23)) == 0);

    // zinc
    verifyZinc(HNum.make(123), "123");
    verifyZinc(HNum.make(123.4, "m/s"), "123.4m/s");
    verifyZinc(HNum.make(9.6, "m/s"), "9.6m/s");
    verifyZinc(HNum.make(-5.2, "\u00b0F"), "-5.2\u00b0F");
    verifyZinc(HNum.make(23, "%"), "23%");
    verifyZinc(HNum.make(2.4e-3, "fl_oz"), "0.0024fl_oz");
    verifyZinc(HNum.make(2.4e5, "$"), "240000$");
    verifyEq(read("1234.56fl_oz"), HNum.make(1234.56, "fl_oz"));
    verifyEq(read("0.000028fl_oz"), HNum.make(0.000028, "fl_oz"));

    // specials
    verifyZinc(HNum.make(Double.NEGATIVE_INFINITY), "-INF");
    verifyZinc(HNum.make(Double.POSITIVE_INFINITY), "INF");
    verifyZinc(HNum.make(Double.NaN), "NaN");

    // verify units never serialized for special values
    verifyEq(HNum.make(Double.NaN, "ignore").toZinc(), "NaN");
    verifyEq(HNum.make(Double.POSITIVE_INFINITY, "%").toZinc(), "INF");
    verifyEq(HNum.make(Double.NEGATIVE_INFINITY, "%").toZinc(), "-INF");

    // verify bad unit names
    verifyEq(HNum.isUnitName(null),  true);
    verifyEq(HNum.isUnitName(""),    false);
    verifyEq(HNum.isUnitName("x_z"), true);
    verifyEq(HNum.isUnitName("x z"), false);
    try { HNum.make(123.4, "foo bar"); fail(); } catch (IllegalArgumentException e) { verifyException(e); }
    try { HNum.make(123.4, "foo,bar"); fail(); } catch (IllegalArgumentException e) { verifyException(e); }

    // verify we format decimal with dot
    Locale locale = Locale.getDefault();
    Locale.setDefault(new Locale("fr"));
    verifyZinc(HNum.make(2.4), "2.4");
    Locale.setDefault(locale);
  }

  public void testStr()
  {
    // equality
    verifyEq(HStr.make("a"), HStr.make("a"));
    verifyNotEq(HStr.make("a"), HStr.make("b"));
    verify(HStr.make("") == HStr.make(""));

    // compare
    verify(HStr.make("abc").compareTo(HStr.make("z")) < 0);
    verify(HStr.make("Foo").compareTo(HStr.make("Foo")) == 0);

    // encoding
    verifyZinc(HStr.make("hello"), "\"hello\"");
    verifyZinc(HStr.make("_ \\ \" \n \r \t \u0011 _"), "\"_ \\\\ \\\" \\n \\r \\t \\u0011 _\"");
    verifyZinc(HStr.make("\u0abc"), "\"\u0abc\"");

    // hex upper and lower case
    verifyEq(read("\"[\\uabcd \\u1234]\""), HStr.make("[\uabcd \u1234]"));
    verifyEq(read("\"[\\uABCD \\u1234]\""), HStr.make("[\uABCD \u1234]"));
    try { read("\"end..."); fail(); } catch (Exception e) { verifyException(e); }
    try { read("\"end...\n\""); fail(); } catch (ParseException e) { verifyException(e); }
    try { read("\"\\u1x34\""); fail(); } catch (ParseException e) { verifyException(e); }
    try { read("\"hi\" "); fail(); } catch (ParseException e) { verifyException(e); }
  }

  public void testUri()
  {
    // equality
    verifyEq(HUri.make("a"), HUri.make("a"));
    verifyNotEq(HUri.make("a"), HUri.make("b"));
    verify(HUri.make("") == HUri.make(""));

    // compare
    verify(HUri.make("abc").compareTo(HUri.make("z")) < 0);
    verify(HUri.make("Foo").compareTo(HUri.make("Foo")) == 0);

    // encoding
    verifyZinc(HUri.make("http://foo.com/f?q"), "`http://foo.com/f?q`");
    verifyZinc(HUri.make("a$b"), "`a$b`");
    verifyZinc(HUri.make("a`b"), "`a\\`b`");
    verifyZinc(HUri.make("http\\:a\\?b"), "`http\\:a\\?b`");
    verifyZinc(HUri.make("\u01ab.txt"), "`\u01ab.txt`");

    // errors
    try { read("`no end"); fail(); } catch (ParseException e) { verifyException(e); }
    try { read("`new\nline`"); fail(); } catch (ParseException e) { verifyException(e); }
  }

  public void testRef()
  {
    // equality (ignore dis)
    verifyEq(HRef.make("foo"), HRef.make("foo"));
    verifyEq(HRef.make("foo"), HRef.make("foo", "Foo"));
    verifyNotEq(HRef.make("foo"), HRef.make("Foo"));

    // encoding
    verifyZinc(HRef.make("1234-5678.foo:bar"), "@1234-5678.foo:bar");
    verifyZinc(HRef.make("1234-5678", "Foo Bar"), "@1234-5678 \"Foo Bar\"");
    verifyZinc(HRef.make("1234-5678", "Foo \"Bar\""), "@1234-5678 \"Foo \\\"Bar\\\"\"");

    // verify bad refs are caught on encoding
    verifyEq(HRef.isId(""), false);
    verifyEq(HRef.isId("%"), false);
    verifyEq(HRef.isId("a"), true);
    verifyEq(HRef.isId("a-b:c"), true);
    verifyEq(HRef.isId("a b"), false);
    verifyEq(HRef.isId("a\u0129b"), false);
    try { HRef.make("@a"); fail(); } catch (Exception e) { verifyException(e); }
    try { HRef.make("a b"); fail(); } catch (Exception e) { verifyException(e); }
    try { HRef.make("a\n"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("@"); fail(); } catch (Exception e) { verifyException(e); }
  }


  public void testBin()
  {
    // equality
    verifyEq(HBin.make("text/plain"), HBin.make("text/plain"));
    verifyNotEq(HBin.make("text/plain"), HBin.make("text/xml"));

    // encoding
    verifyZinc(HBin.make("text/plain"), "Bin(text/plain)");
    verifyZinc(HBin.make("text/plain; charset=utf-8"), "Bin(text/plain; charset=utf-8)");

    // verify bad bins are caught on encoding
    try { HBin.make("text/plain; f()").toZinc(); fail(); } catch (Exception e) { verifyException(e); }
    try { read("Bin()"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("Bin(text)"); fail(); } catch (Exception e) { verifyException(e); }
  }

  public void testDate()
  {
    // equality
    verifyEq(HDate.make(2011, 6, 7), HDate.make(2011, 6, 7));
    verifyNotEq(HDate.make(2011, 6, 7), HDate.make(2011, 6, 8));
    verifyNotEq(HDate.make(2011, 6, 7), HDate.make(2011, 2, 7));
    verifyNotEq(HDate.make(2011, 6, 7), HDate.make(2009, 6, 7));

    // compare
    verify(HDate.make(2011, 6, 9).compareTo(HDate.make(2011, 6, 21)) < 0);
    verify(HDate.make(2011, 10, 9).compareTo(HDate.make(2011, 3, 21)) > 0);
    verify(HDate.make(2010, 6, 9).compareTo(HDate.make(2000, 9, 30)) > 0);
    verify(HDate.make(2010, 6, 9).compareTo(HDate.make(2010, 6, 9))  == 0);

    // plus/minus
    verifyEq(HDate.make(2011, 12, 1).minusDays(0), HDate.make(2011, 12, 1));
    verifyEq(HDate.make(2011, 12, 1).minusDays(1), HDate.make(2011, 11, 30));
    verifyEq(HDate.make(2011, 12, 1).minusDays(-2), HDate.make(2011, 12, 3));
    verifyEq(HDate.make(2011, 12, 1).plusDays(2), HDate.make(2011, 12, 3));
    verifyEq(HDate.make(2011, 12, 1).plusDays(31), HDate.make(2012, 1, 1));
    verifyEq(HDate.make(2008, 3, 3).minusDays(3), HDate.make(2008, 2, 29));
    verifyEq(HDate.make(2008, 3, 3).minusDays(4), HDate.make(2008, 2, 28));

    // encoding
    verifyZinc(HDate.make(2011, 6, 7), "2011-06-07");
    verifyZinc(HDate.make(2011,10,10), "2011-10-10");
    verifyZinc(HDate.make(2011,12,31), "2011-12-31");
    try { read("2003-xx-02"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("2003-02"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("2003-02-xx"); fail(); } catch (Exception e) { verifyException(e); }

    // leap year
    for (int y = 1900; y <= 2100; y++)
    {
      if (((y % 4) == 0) && (y != 1900) && (y != 2100))
        verify(HDate.isLeapYear(y));
      else
        verify(!HDate.isLeapYear(y));
    }
  }

  public void testTime()
  {
    // equality
    verifyEq(HTime.make(1, 2, 3, 4), HTime.make(1, 2, 3, 4));
    verifyNotEq(HTime.make(1, 2, 3, 4), HTime.make(9, 2, 3, 4));
    verifyNotEq(HTime.make(1, 2, 3, 4), HTime.make(1, 9, 3, 4));
    verifyNotEq(HTime.make(1, 2, 3, 4), HTime.make(1, 2, 9, 9));

    // compare
    verify(HTime.make(0, 0, 0, 0).compareTo(HTime.make(0, 0, 0, 9)) < 0);
    verify(HTime.make(0, 0, 0, 0).compareTo(HTime.make(0, 0, 1, 0)) < 0);
    verify(HTime.make(0, 1, 0, 0).compareTo(HTime.make(0, 0, 0, 0)) > 0);
    verify(HTime.make(0, 0, 0, 0).compareTo(HTime.make(2, 0, 0, 0)) < 0);
    verify(HTime.make(2, 0, 0, 0).compareTo(HTime.make(2, 0, 0, 0)) == 0);

    // encoding
    verifyZinc(HTime.make(2, 3), "02:03:00");
    verifyZinc(HTime.make(2, 3, 4), "02:03:04");
    verifyZinc(HTime.make(2, 3, 4, 5), "02:03:04.005");
    verifyZinc(HTime.make(2, 3, 4, 56), "02:03:04.056");
    verifyZinc(HTime.make(2, 3, 4, 109), "02:03:04.109");
    verifyZinc(HTime.make(2, 3, 10, 109), "02:03:10.109");
    verifyZinc(HTime.make(2, 10, 59), "02:10:59");
    verifyZinc(HTime.make(10, 59, 30), "10:59:30");
    verifyZinc(HTime.make(23, 59, 59, 999), "23:59:59.999");

    try { read("3:20:00"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("13:xx:00"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("13:45:0x"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("13:45:00.4561"); fail(); } catch (Exception e) { verifyException(e); }
  }

  public void testTz()
  {
    verifyTz("New_York", "America/New_York");
    verifyTz("Chicago",  "America/Chicago");
    verifyTz("Phoenix",  "America/Phoenix");
    verifyTz("London",   "Europe/London");
    verifyTz("UTC",      "Etc/UTC");
  }

  private void verifyTz(String name, String javaId)
  {
    HTimeZone tz = HTimeZone.make(name);
    TimeZone java = TimeZone.getTimeZone(javaId);
    verifyEq(tz.name, name);
    verifyEq(tz.java, java);
    verifyEq(tz, HTimeZone.make(java));
  }

  public void testDateTime()
  {
    // equality
    HTimeZone utc = HTimeZone.UTC;
    HTimeZone london = HTimeZone.make("London");

    verifyEq(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0), HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0));
    verifyNotEq(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0), HDateTime.make(2009, 1, 2, 3, 4, 5, utc, 0));
    verifyNotEq(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0), HDateTime.make(2011, 9, 2, 3, 4, 5, utc, 0));
    verifyNotEq(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0), HDateTime.make(2011, 1, 9, 3, 4, 5, utc, 0));
    verifyNotEq(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0), HDateTime.make(2011, 1, 2, 9, 4, 5, utc, 0));
    verifyNotEq(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0), HDateTime.make(2011, 1, 2, 3, 9, 5, utc, 0));
    verifyNotEq(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0), HDateTime.make(2011, 1, 2, 3, 4, 9, utc, 0));
    verifyNotEq(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0), HDateTime.make(2011, 1, 2, 3, 4, 5, london, 0));
    verifyNotEq(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0), HDateTime.make(2011, 1, 2, 3, 4, 5, london, 3600));

    // compare
    verify(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0).compareTo(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0)) == 0);
    verify(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0).compareTo(HDateTime.make(2011, 1, 2, 3, 4, 6, utc, 0)) < 0);
    verify(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0).compareTo(HDateTime.make(2011, 1, 2, 3, 5, 5, utc, 0)) < 0);
    verify(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0).compareTo(HDateTime.make(2011, 1, 2, 4, 4, 5, utc, 0)) < 0);
    verify(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0).compareTo(HDateTime.make(2011, 1, 3, 3, 4, 5, utc, 0)) < 0);
    verify(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0).compareTo(HDateTime.make(2011, 2, 2, 3, 4, 5, utc, 0)) < 0);
    verify(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0).compareTo(HDateTime.make(2012, 1, 2, 3, 4, 5, utc, 0)) < 0);
    verify(HDateTime.make(2011, 1, 2, 3, 4, 5, utc, 0).compareTo(HDateTime.make(2011, 1, 2, 3, 4, 0, utc, 0)) > 0);

    // encoding
    HDateTime ts = HDateTime.make(1307377618069L, HTimeZone.make("New_York"));
    verifyZinc(ts, "2011-06-06T12:26:58.069-04:00 New_York");
    verifyEq(ts.date.toString(), "2011-06-06");
    verifyEq(ts.time.toString(), "12:26:58.069");
    verifyEq(ts.tzOffset, -4*60*60);
    verifyEq(ts.tz.name, "New_York");
    verifyEq(ts.tz.java.getID(), "America/New_York");
    verifyEq(ts.millis(), 1307377618069L);

    // convert back to millis
    ts = HDateTime.make(ts.date, ts.time, ts.tz, ts.tzOffset);
    verifyEq(ts.millis(), 1307377618069L);

    // different timezones
    ts = HDateTime.make(949478640000L, HTimeZone.make("New_York"));
    verifyZinc(ts, "2000-02-02T03:04:00-05:00 New_York");
    ts = HDateTime.make(949478640000L, HTimeZone.make("UTC"));
    verifyZinc(ts, "2000-02-02T08:04:00Z UTC");
    ts = HDateTime.make(949478640000L, HTimeZone.make("Taipei"));
    verifyZinc(ts, "2000-02-02T16:04:00+08:00 Taipei");
    verifyZinc(HDateTime.make(2011, 6, 7, 11, 3, 43, HTimeZone.make("GMT+10"), -36000),
             "2011-06-07T11:03:43-10:00 GMT+10");
    verifyZinc(HDateTime.make(HDate.make(2011, 6, 8), HTime.make(4, 7, 33, 771), HTimeZone.make("GMT-7"), 25200),
             "2011-06-08T04:07:33.771+07:00 GMT-7");

    // verify millis()
    HDate date = HDate.make(2014, 12, 24);
    HTime time = HTime.make(11, 12, 13, 456);
    HTimeZone newYork = HTimeZone.make("New_York");
    long utcMillis = 1419437533456L;

    HDateTime a = HDateTime.make(date, time, newYork);
    HDateTime b = HDateTime.make(date, time, newYork, a.tzOffset);
    HDateTime c = HDateTime.make(utcMillis,  newYork);
    HDateTime d = HDateTime.make("2014-12-24T11:12:13.456-05:00 New_York");

    verifyEq(a.millis(), utcMillis);
    verifyEq(b.millis(), utcMillis);
    verifyEq(c.millis(), utcMillis);
    verifyEq(d.millis(), utcMillis);

    // errors
    try { read("2000-02-02T03:04:00-0x:00 New_York"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("2000-02-02T03:04:00-05 New_York"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("2000-02-02T03:04:00-05:!0 New_York"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("2000-02-02T03:04:00-05:00"); fail(); } catch (Exception e) { verifyException(e); }
    try { read("2000-02-02T03:04:00-05:00 @"); fail(); } catch (Exception e) { verifyException(e); }
  }

  public void testMidnight()
  {
    verifyMidnight(HDate.make(2011, 11, 3),  "UTC",      "2011-11-03T00:00:00Z UTC");
    verifyMidnight(HDate.make(2011, 11, 3),  "New_York", "2011-11-03T00:00:00-04:00 New_York");
    verifyMidnight(HDate.make(2011, 12, 15), "Chicago",  "2011-12-15T00:00:00-06:00 Chicago");
    verifyMidnight(HDate.make(2008, 2, 29),  "Phoenix",  "2008-02-29T00:00:00-07:00 Phoenix");
  }

  private void verifyMidnight(HDate date, String tzName, String str)
  {
    HDateTime ts = date.midnight(HTimeZone.make(tzName));
    verifyEq(ts.date, date);
    verifyEq(ts.time.hour, 0);
    verifyEq(ts.time.min,  0);
    verifyEq(ts.time.sec,  0);
    verifyEq(ts.toString(), str);
    verifyEq(ts, read(ts.toZinc()));
    verifyEq(ts.millis(), ((HDateTime)read(str)).millis());
  }

  public void testCoord()
  {
    verifyCoord(12, 34, "C(12.0,34.0)");

    // lat boundaries
    verifyCoord(90, 123, "C(90.0,123.0)");
    verifyCoord(-90, 123, "C(-90.0,123.0)");
    verifyCoord(89.888999, 123, "C(89.888999,123.0)");
    verifyCoord(-89.888999, 123, "C(-89.888999,123.0)");

    // lon boundaries
    verifyCoord(45, 180, "C(45.0,180.0)");
    verifyCoord(45, -180, "C(45.0,-180.0)");
    verifyCoord(45, 179.999129, "C(45.0,179.999129)");
    verifyCoord(45, -179.999129, "C(45.0,-179.999129)");

    // decimal places
    verifyCoord(9.1, -8.1, "C(9.1,-8.1)");
    verifyCoord(9.12, -8.13, "C(9.12,-8.13)");
    verifyCoord(9.123, -8.134, "C(9.123,-8.134)");
    verifyCoord(9.1234, -8.1346, "C(9.1234,-8.1346)");
    verifyCoord(9.12345,- 8.13456, "C(9.12345,-8.13456)");
    verifyCoord(9.123452, -8.134567, "C(9.123452,-8.134567)");

    // zero boundaries
    verifyCoord(0, 0, "C(0.0,0.0)");
    verifyCoord(0.3, -0.3, "C(0.3,-0.3)");
    verifyCoord(0.03, -0.03, "C(0.03,-0.03)");
    verifyCoord(0.003, -0.003, "C(0.003,-0.003)");
    verifyCoord(0.0003, -0.0003, "C(0.0003,-0.0003)");
    verifyCoord(0.02003, -0.02003, "C(0.02003,-0.02003)");
    verifyCoord(0.020003, -0.020003, "C(0.020003,-0.020003)");
    verifyCoord(0.000123, -0.000123, "C(0.000123,-0.000123)");
    verifyCoord(7.000123, -7.000123, "C(7.000123,-7.000123)");

    // arg errors
    verifyEq(HCoord.isLat(-91.0), false);
    verifyEq(HCoord.isLat(-90.0), true);
    verifyEq(HCoord.isLat(-89.0), true);
    verifyEq(HCoord.isLat(90.0), true);
    verifyEq(HCoord.isLat(91.0), false);
    verifyEq(HCoord.isLng(-181.0), false);
    verifyEq(HCoord.isLng(-179.99), true);
    verifyEq(HCoord.isLng(180.0), true);
    verifyEq(HCoord.isLng(181.0), false);
    try { HCoord.make(91, 12); fail(); } catch (IllegalArgumentException e) { verifyException(e); }
    try { HCoord.make(-90.2, 12); fail(); } catch (IllegalArgumentException e) { verifyException(e); }
    try { HCoord.make(13, 180.009); fail(); } catch (IllegalArgumentException e) { verifyException(e); }
    try { HCoord.make(13, -181); fail(); } catch (IllegalArgumentException e) { verifyException(e); }

    // parse errs
    try { HCoord.make("1.0,2.0"); fail(); } catch (ParseException e) { verifyException(e); }
    try { HCoord.make("(1.0,2.0)"); fail(); } catch (ParseException e) { verifyException(e); }
    try { HCoord.make("C(1.0,2.0"); fail(); } catch (ParseException e) { verifyException(e); }
    try { HCoord.make("C(x,9)"); fail(); } catch (ParseException e) { verifyException(e); }
  }

  void verifyCoord(double lat, double lng, String s)
  {
    HCoord c = HCoord.make(lat, lng);
    verifyEq(c.lat(), lat);
    verifyEq(c.lng(), lng);
    verifyEq(c.toString(), s);
    verifyEq(HCoord.make(s), c);
  }

  public void testRange()
  {
    HTimeZone ny = HTimeZone.make("New_York");
    HDate today = HDate.today();
    HDate yesterday = today.minusDays(1);
    HDate x = HDate.make(2011, 7, 4);
    HDate y = HDate.make(2011, 11, 4);
    HDateTime xa = HDateTime.make(x, HTime.make(2, 30), ny);
    HDateTime xb = HDateTime.make(x, HTime.make(22, 5), ny);

    verifyRange(HDateTimeRange.make("today", ny), today, today);
    verifyRange(HDateTimeRange.make("yesterday", ny), yesterday, yesterday);
    verifyRange(HDateTimeRange.make("2011-07-04", ny), x, x);
    verifyRange(HDateTimeRange.make("2011-07-04,2011-11-04", ny), x, y);
    verifyRange(HDateTimeRange.make(""+xa+","+xb, ny), xa, xb);

    HDateTimeRange r = HDateTimeRange.make(xb.toString(), ny);
    verifyEq(r.start, xb);
    verifyEq(r.end.date, today);
    verifyEq(r.end.tz, ny);

    // this week
    HDate sun = today;
    HDate sat = today;
    while (sun.weekday() > Calendar.SUNDAY) sun = sun.minusDays(1);
    while (sat.weekday() < Calendar.SATURDAY) sat = sat.plusDays(1);
    verifyRange(HDateTimeRange.thisWeek(ny), sun, sat);

    // this month
    HDate first = today;
    HDate last = today;
    while (first.day > 1)  first = first.minusDays(1);
    while (last.day < HDate.daysInMonth(today.year, today.month)) last = last.plusDays(1);
    verifyRange(HDateTimeRange.thisMonth(ny), first, last);

    // this year
    first = HDate.make(today.year, 1, 1);
    last = HDate.make(today.year, 12, 31);
    verifyRange(HDateTimeRange.thisYear(ny), first, last);

    // last week
    HDate prev = today.minusDays(7);
    sun = prev;
    sat = prev;
    while (sun.weekday() > Calendar.SUNDAY) sun = sun.minusDays(1);
    while (sat.weekday() < Calendar.SATURDAY) sat = sat.plusDays(1);
    verifyRange(HDateTimeRange.lastWeek(ny), sun, sat);

    // last month
    last = today;
    while (last.month == today.month) last = last.minusDays(1);
    first = HDate.make(last.year, last.month, 1);
    verifyRange(HDateTimeRange.lastMonth(ny), first, last);

    // last year
    first = HDate.make(today.year-1, 1, 1);
    last = HDate.make(today.year-1, 12, 31);
    verifyRange(HDateTimeRange.lastYear(ny), first, last);
  }

  private void verifyRange(HDateTimeRange r, HDate start, HDate end)
  {
    verifyEq(r.start.date,    start);
    verifyEq(r.start.time,    HTime.MIDNIGHT);
    verifyEq(r.start.tz.name, "New_York");
    verifyEq(r.end.date,      end.plusDays(1));
    verifyEq(r.end.time,      HTime.MIDNIGHT);
    verifyEq(r.end.tz.name,   "New_York");
  }

  private void verifyRange(HDateTimeRange r, HDateTime start, HDateTime end)
  {
    verifyEq(r.start, start);
    verifyEq(r.end, end);
  }

  public void verifyZinc(HVal val, String s)
  {
    // println("  :: " + s);
    // println("     " + read(s).toZinc());
    verifyEq(val.toZinc(), s);
    verifyEq(read(s), val);
  }

  public HVal read(String s)
  {
    return new HZincReader(s).readScalar();
  }
}
