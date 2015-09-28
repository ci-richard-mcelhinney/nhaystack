//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Nov 2011  Brian Frank  Creation
//
package org.projecthaystack;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.HashMap;

/**
 * HTimeZone handles the mapping between Haystack timezone
 * names and Java timezones.
 *
 * @see <a href='http://project-haystack.org/doc/TimeZones'>Project Haystack</a>
 */
public final class HTimeZone
{
  /** Convenience for make(name, true) */
  public static HTimeZone make(String name) { return make(name, true); }

  /**
   * Construct with Haystack timezone name, raise exception or
   * return null on error based on check flag.
   */
  public static HTimeZone make(String name, boolean checked)
  {
    synchronized (cache)
    {
      // lookup in cache
      HTimeZone tz = (HTimeZone)cache.get(name);
      if (tz != null) return tz;

      // map haystack id to Java full id
      String javaId = (String)toJava.get(name);
      if (javaId == null)
      {
        if (checked) throw new RuntimeException("Unknown tz: " + name);
        return null;
      }

      // resolve full id to HTimeZone and cache
      TimeZone java = TimeZone.getTimeZone(javaId);
      tz = new HTimeZone(name, java);
      cache.put(name, tz);
      return tz;
    }
  }

  /** Convenience for make(java, true) */
  public static HTimeZone make(TimeZone java) { return make(java, true); }

  /**
   * Construct from Java timezone.  Throw exception or return
   * null based on checked flag.
   */
  public static HTimeZone make(TimeZone java, boolean checked)
  {
    String javaId = java.getID();

    // Sometimes the java ID is of the form "GMT[+,-]hh:00".  This seems
    // to occur when timesync in turned off.  In that case, convert the 
    // java ID to "Etc/GMT[+,-]h".
    //
    // Note that this does not handle settings like "GMT+03:30", which
    // cannot be automatically converted to an Etc timezone.
    if (javaId.startsWith("GMT") && javaId.endsWith(":00"))
    {
      javaId = javaId.substring(0, javaId.length() - ":00".length());

      // remove leading 0
      if (javaId.startsWith("GMT-0"))
        javaId = "GMT-" + javaId.substring("GMT-0".length());
      else if (javaId.startsWith("GMT+0"))
        javaId = "GMT+" + javaId.substring("GMT+0".length());

      javaId = "Etc/" + javaId;
    }

    String name = (String)fromJava.get(javaId);
    if (name != null) return make(name);
    if (checked) throw new RuntimeException("Invalid Java timezone: " + java.getID());
    return null;
  }

  /** Private constructor */
  private HTimeZone(String name, TimeZone java)
  {
    this.name = name;
    this.java = java;
  }

  /** Haystack timezone name */
  public final String name;

  /** Java representation of this timezone. */
  public final TimeZone java;

  /** Return Haystack timezone name */
  public String toString() { return name; }

  // haystack name -> HTimeZone
  private static HashMap cache = new HashMap();

  // haystack name <-> java name mapping
  private static HashMap toJava;
  private static HashMap fromJava;
  static
  {
    HashMap toJava = new HashMap();
    HashMap fromJava = new HashMap();
    try
    {
      // only time zones which start with these
      // regions are considered valid timezones
      HashMap regions = new HashMap();
      regions.put("Africa",     "ok");
      regions.put("America",    "ok");
      regions.put("Antarctica", "ok");
      regions.put("Asia",       "ok");
      regions.put("Atlantic",   "ok");
      regions.put("Australia",  "ok");
      regions.put("Etc",        "ok");
      regions.put("Europe",     "ok");
      regions.put("Indian",     "ok");
      regions.put("Pacific",    "ok");

      // iterate Java timezone IDs available
      String[] ids = TimeZone.getAvailableIDs();
      for (int i=0; i<ids.length; ++i)
      {
        String java = ids[i];

        // skip ids not formatted as Region/City
        int slash = java.indexOf('/');
        if (slash < 0) continue;
        String region = java.substring(0, slash);
        if (regions.get(region) == null) continue;

        // get city name as haystack id
        slash = java.lastIndexOf('/');
        String haystack = java.substring(slash+1);

        // store mapping b/w Java <-> Haystack
        toJava.put(haystack, java);
        fromJava.put(java, haystack);
      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
    HTimeZone.toJava   = toJava;
    HTimeZone.fromJava = fromJava;
  }

  /** UTC timezone */
  public static final HTimeZone UTC;

  /** Default timezone for VM */
  public static final HTimeZone DEFAULT;

  static
  {
    HTimeZone utc = null;
    try
    {
      utc = HTimeZone.make(TimeZone.getTimeZone("Etc/UTC"));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    HTimeZone def = null;
    try
    {
      // check if configured with system property
      String defName = System.getProperty("haystack.tz");
      if (defName != null)
      {
        def = HTimeZone.make(defName, false);
        if (def == null) System.out.println("WARN: invalid haystack.tz system property: " + defName);
      }

      // if we still don't have a default, try to use Java's
      if (def == null) def = HTimeZone.make(TimeZone.getDefault());
    }
    catch (Exception e)
    {
      // fallback to UTC
      e.printStackTrace();
      def = utc;
    }

    DEFAULT = def;
    UTC = utc;
  }
}
