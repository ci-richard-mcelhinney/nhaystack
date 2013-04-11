//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package haystack;

import java.text.*;

/**
 * HNum wraps a 64-bit floating point number and optional unit name.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel#tagKinds'>Project Haystack</a>
 */
public class HNum extends HVal
{
  /** Construct with int and null unit (may have loss of precision) */
  public static HNum make(int val)
  {
    return make(val, null);
  }

  /** Construct with int and null/non-null unit (may have loss of precision) */
  public static HNum make(int val, String unit)
  {
    if (val == 0 && unit == null) return ZERO;
    return new HNum((double)val, unit);
  }

  /** Construct with long and null unit (may have loss of precision) */
  public static HNum make(long val)
  {
    return make(val, null);
  }

  /** Construct with long and null/non-null unit (may have loss of precision) */
  public static HNum make(long val, String unit)
  {
    if (val == 0L && unit == null) return ZERO;
    return new HNum((double)val, unit);
  }

  /** Construct with double and null unit */
  public static HNum make(double val)
  {
    return make(val, null);
  }

  /** Construct with double and null/non-null unit */
  public static HNum make(double val, String unit)
  {
    if (val == 0.0 && unit == null) return ZERO;
    return new HNum(val, unit);
  }

  /** Singleton value for zero */
  public static final HNum ZERO = new HNum(0.0, null);

  /** Singleton value for positive infinity "Inf" */
  public static final HNum POS_INF = new HNum(Double.POSITIVE_INFINITY, null);

  /** Singleton value for negative infinity "-Inf" */
  public static final HNum NEG_INF = new HNum(Double.NEGATIVE_INFINITY, null);

  /** Singleton value for not-a-number "NaN" */
  public static final HNum NaN = new HNum(Double.NaN, null);

  /** Private constructor */
  private HNum(double val, String unit) { this.val = val; this.unit = unit; }

  /** Double scalar value */
  public final double val;

  /** Unit name or null */
  public final String unit;

  /** Hash code is based on val, unit */
  public int hashCode()
  {
    long bits = Double.doubleToLongBits(val);
    int hash = (int)(bits ^ (bits >>> 32));
    if (unit != null) hash ^= unit.hashCode();
    return hash;
  }

  /** Equals is based on val, unit (NaN == NaN) */
  public boolean equals(Object that)
  {
    if (!(that instanceof HNum)) return false;
    HNum x = (HNum)that;
    if (Double.isNaN(val)) return Double.isNaN(x.val);
    if (val != x.val) return false;
    if (unit == null) return x.unit == null;
    if (x.unit == null) return false;
    return unit.equals(x.unit);
  }

  /** Return sort order as negative, 0, or positive */
  public int compareTo(Object that)
  {
    double thatVal = ((HNum)that).val;
    if (this.val < thatVal) return -1;
    if (this.val == thatVal) return 0;
    return 1;
  }

  /** Encode as floating value followed by optional unit string */
  public String toZinc()
  {
    StringBuffer s = new StringBuffer();
    if (val == Double.POSITIVE_INFINITY) s.append("INF");
    else if (val == Double.NEGATIVE_INFINITY) s.append("-INF");
    else if (Double.isNaN(val)) s.append("NaN");
    else
    {
      // don't let fractions
      double abs = val; if (abs < 0) abs = -abs;
      if (abs > 1.0)
        s.append(new DecimalFormat("#0.####").format(val));
      else
        s.append(val);

      if (unit != null)
      {
        for (int i=0; i<unit.length(); ++i)
        {
          int c = unit.charAt(i);
          if (c < 128 && !unitChars[c]) throw new IllegalArgumentException("Invalid unit name '" + unit + "', char='" + (char)c + "'");
          s.append((char)c);
        }
      }
    }
    return s.toString();
  }

  /**
   * Get this number as a duration of milliseconds.
   * Raise IllegalStateException if the unit is not a duration unit.
   */
  public long millis()
  {
    String u = this.unit;
    if (u == null) u = "null";
    if (u.equals("ms")  || u.equals("millisecond")) return (long)val;
    if (u.equals("s")   || u.equals("sec") || u.equals("millisecond")) return (long)(val*1000.0);
    if (u.equals("min") || u.equals("minute")) return (long)(val*1000.0*60.0);
    if (u.equals("h")   || u.equals("hr") || u.equals("minute")) return (long)(val*1000.0*60.0*60.0);
    throw new IllegalStateException("Invalid duration unit: " + u);
  }

  private static boolean[] unitChars = new boolean[128];
  static
  {
    for (int i='a'; i<='z'; ++i) unitChars[i] = true;
    for (int i='A'; i<='Z'; ++i) unitChars[i] = true;
    unitChars['_'] = true;
    unitChars['$'] = true;
    unitChars['%'] = true;
    unitChars['/'] = true;
  }

}

