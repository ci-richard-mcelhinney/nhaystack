//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Oct 2011  Brian Frank  Creation
//
package haystack;

import java.util.*;
import haystack.io.HZincReader;

/**
 * HFilter models a parsed tag query string.
 *
 * @see <a href='http://project-haystack.org/doc/Filters'>Project Haystack</a>
 */
public abstract class HFilter
{

//////////////////////////////////////////////////////////////////////////
// Encoding
//////////////////////////////////////////////////////////////////////////

  /** Convenience for "make(s, true)" */
  public static HFilter make(String s) { return make(s, true); }

  /** Decode a string into a HFilter; return null or throw
      ParseException if not formatted correctly */
  public static HFilter make(String s, boolean checked)
  {
    try
    {
      return new HZincReader(s).readFilter();
    }
    catch (Exception e)
    {
      if (!checked) return null;
      if (e instanceof ParseException) throw (ParseException)e;
      throw new ParseException(s, e);
    }
  }

//////////////////////////////////////////////////////////////////////////
// Factories
//////////////////////////////////////////////////////////////////////////

  /**
   * Match records which have the specified tag path defined.
   */
  public static HFilter has(String path) { return new Has(Path.make(path)); }

  /**
   * Match records which do not define the specified tag path.
   */
  public static HFilter missing(String path) { return new Missing(Path.make(path)); }

  /**
   * Match records which have a tag are equal to the specified value.
   * If the path is not defined then it is unmatched.
   */
  public static HFilter eq(String path, HVal val) { return new Eq(Path.make(path), val); }

  /**
   * Match records which have a tag not equal to the specified value.
   * If the path is not defined then it is unmatched.
   */
  public static HFilter ne(String path, HVal val) { return new Ne(Path.make(path), val); }

  /**
   * Match records which have tags less than the specified value.
   * If the path is not defined then it is unmatched.
   */
  public static HFilter lt(String path, HVal val) { return new Lt(Path.make(path), val); }

  /**
   * Match records which have tags less than or equals to specified value.
   * If the path is not defined then it is unmatched.
   */
  public static HFilter le(String path, HVal val) { return new Le(Path.make(path), val); }

  /**
   * Match records which have tags greater than specified value.
   * If the path is not defined then it is unmatched.
   */
  public static HFilter gt(String path, HVal val) { return new Gt(Path.make(path), val); }

  /**
   * Match records which have tags greater than or equal to specified value.
   * If the path is not defined then it is unmatched.
   */
  public static HFilter ge(String path, HVal val) { return new Ge(Path.make(path), val); }

  /**
   * Return a query which is the logical-and of this and that query.
   */
  public HFilter and(HFilter that) { return new And(this, that); }

  /**
   * Return a query which is the logical-or of this and that query.
   */
  public HFilter or(HFilter that) { return new Or(this, that); }

//////////////////////////////////////////////////////////////////////////
// Constructor
//////////////////////////////////////////////////////////////////////////

  /** Package private constructor subclasses */
  HFilter() {}

//////////////////////////////////////////////////////////////////////////
// Access
//////////////////////////////////////////////////////////////////////////

  /* Return if given tags entity matches this query. */
  public abstract boolean include(HDict dict, Pather pather);

  /** String encoding */
  public final String toString()
  {
    if (string == null) string = toStr();
    return string;
  }

  private String string;

  /* Used to lazily build toString */
  abstract String toStr();

  /** Hash code is based on string encoding */
  public final int hashCode() { return toString().hashCode(); }

  /** Equality is based on string encoding */
  public final boolean equals(Object that)
  {
    if (!(that instanceof HFilter)) return false;
    return toString().equals(that.toString());
  }

//////////////////////////////////////////////////////////////////////////
// HFilter.Path
//////////////////////////////////////////////////////////////////////////

  /** Pather is a callback interface used to resolve query paths. */
  public interface Pather
  {
    /**
     * Given a HRef string identifier, resolve to an entity's
     * HDict respresentation or ref is not found return null.
     */
    public HDict find(String ref);
  }

//////////////////////////////////////////////////////////////////////////
// HFilter.Path
//////////////////////////////////////////////////////////////////////////

  /** Path is a simple name or a complex path using the "->" separator */
  static abstract class Path
  {
    /** Construct a new Path from string or throw ParseException */
    public static Path make(String path)
    {
      try
      {
        // optimize for common single name case
        int dash = path.indexOf('-');
        if (dash < 0) return new Path1(path);

        // parse
        int s = 0;
        ArrayList acc = new ArrayList();
        while (true)
        {
          String n = path.substring(s, dash);
          if (n.length() == 0) throw new Exception();
          acc.add(n);
          if (path.charAt(dash+1) != '>') throw new Exception();
          s = dash+2;
          dash = path.indexOf('-', s);
          if (dash < 0)
          {
            n = path.substring(s, path.length());
            if (n.length() == 0) throw new Exception();
            acc.add(n);
            break;
          }
        }
        return new PathN(path, (String[])acc.toArray(new String[acc.size()]));
      }
      catch (Exception e) {}
      throw new ParseException("Path: " + path);
    }

    /** Number of names in the path. */
    public abstract int size();

    /** Get name at given index. */
    public abstract String get(int i);

    /** Hashcode is based on string. */
    public int hashCode() { return toString().hashCode(); }

    /** Equality is based on string. */
    public boolean equals(Object that) { return toString().equals(that.toString()); }

    /** Get string encoding. */
    public abstract String toString();
  }

  static final class Path1 extends Path
  {
    Path1(String n) { this.name = n; }
    public int size() { return 1; }
    public String get(int i) { if (i == 0) return name; throw new IndexOutOfBoundsException(""+i); }
    public String toString() { return name; }
    private final String name;
  }

  static final class PathN extends Path
  {
    PathN(String s, String[] n) { this.string = s; this.names = n; }
    public int size() { return names.length; }
    public String get(int i) { return names[i]; }
    public String toString() { return string; }
    private final String string;
    private final String[] names;
  }

//////////////////////////////////////////////////////////////////////////
// PathFilter
//////////////////////////////////////////////////////////////////////////

  static abstract class PathFilter extends HFilter
  {
    PathFilter(Path p) { path = p; }
    public final boolean include(HDict dict, Pather pather)
    {
      HVal val = dict.get(path.get(0), false);
      if (path.size() != 1)
      {
        HDict nt = dict;
        for (int i=1; i<path.size(); ++i)
        {
          if (!(val instanceof HRef)) { val = null; break; }
          nt = pather.find(((HRef)val).val);
          if (nt == null) { val = null; break; }
          val = nt.get(path.get(i), false);
        }
      }
      return doInclude(val);
    }
    abstract boolean doInclude(HVal val);
    final Path path;
  }

//////////////////////////////////////////////////////////////////////////
// Has
//////////////////////////////////////////////////////////////////////////

  static final class Has extends PathFilter
  {
    Has(Path p) { super(p); }
    final boolean doInclude(HVal v) { return v != null; }
    final String toStr() { return path.toString(); }
  }

//////////////////////////////////////////////////////////////////////////
// Missing
//////////////////////////////////////////////////////////////////////////

  static final class Missing extends PathFilter
  {
    Missing(Path p) { super(p); }
    final boolean doInclude(HVal v) { return v == null; }
    final String toStr() { return "not " + path; }
  }

//////////////////////////////////////////////////////////////////////////
// CmpFilter
//////////////////////////////////////////////////////////////////////////

  static abstract class CmpFilter extends PathFilter
  {
    CmpFilter(Path p, HVal val) { super(p); this.val = val; }
    final String toStr()
    {
      StringBuffer s = new StringBuffer();
      s.append(path).append(cmpStr()).append(val.toZinc());
      return s.toString();
    }
    final boolean sameType(HVal v) { return v != null && v.getClass() == val.getClass(); }
    abstract String cmpStr();
    final HVal val;
  }

//////////////////////////////////////////////////////////////////////////
// Eq
//////////////////////////////////////////////////////////////////////////

  static final class Eq extends CmpFilter
  {
    Eq(Path p, HVal v) { super(p, v); }
    final String cmpStr() { return "=="; }
    final boolean doInclude(HVal v) { return v != null && v.equals(val); }
  }

//////////////////////////////////////////////////////////////////////////
// Ne
//////////////////////////////////////////////////////////////////////////

  static final class Ne extends CmpFilter
  {
    Ne(Path p, HVal v) { super(p, v); }
    final String cmpStr() { return "!="; }
    final boolean doInclude(HVal v) { return v != null && !v.equals(val); }
  }

//////////////////////////////////////////////////////////////////////////
// Lt
//////////////////////////////////////////////////////////////////////////

  static final class Lt extends CmpFilter
  {
    Lt(Path p, HVal v) { super(p, v); }
    final String cmpStr() { return "<"; }
    final boolean doInclude(HVal v) { return sameType(v) && v.compareTo(val) < 0; }
  }

//////////////////////////////////////////////////////////////////////////
// Le
//////////////////////////////////////////////////////////////////////////

  static final class Le extends CmpFilter
  {
    Le(Path p, HVal v) { super(p, v); }
    final String cmpStr() { return "<="; }
    final boolean doInclude(HVal v) { return sameType(v) && v.compareTo(val) <= 0; }
  }

//////////////////////////////////////////////////////////////////////////
// Gt
//////////////////////////////////////////////////////////////////////////

  static final class Gt extends CmpFilter
  {
    Gt(Path p, HVal v) { super(p, v); }
    final String cmpStr() { return ">"; }
    final boolean doInclude(HVal v) { return sameType(v) && v.compareTo(val) > 0; }
  }

//////////////////////////////////////////////////////////////////////////
// Ge
//////////////////////////////////////////////////////////////////////////

  static final class Ge extends CmpFilter
  {
    Ge(Path p, HVal v) { super(p, v); }
    final String cmpStr() { return ">="; }
    final boolean doInclude(HVal v) { return sameType(v) && v.compareTo(val) >= 0; }
  }

//////////////////////////////////////////////////////////////////////////
// Compound
//////////////////////////////////////////////////////////////////////////

  static abstract class CompoundFilter extends HFilter
  {
    CompoundFilter(HFilter a, HFilter b) { this.a = a; this.b = b; }
    abstract String keyword();
    final String toStr()
    {
      boolean deep = a instanceof CompoundFilter || b instanceof CompoundFilter;
      StringBuffer s = new StringBuffer();
      if (a instanceof CompoundFilter) s.append('(').append(a).append(')');
      else s.append(a);
      s.append(' ').append(keyword()).append(' ');
      if (b instanceof CompoundFilter) s.append('(').append(b).append(')');
      else s.append(b);
      return s.toString();
    }
    final HFilter a;
    final HFilter b;
  }

//////////////////////////////////////////////////////////////////////////
// And
//////////////////////////////////////////////////////////////////////////

  static final class And extends CompoundFilter
  {
    And(HFilter a, HFilter b) { super(a, b); }
    final String keyword() { return "and"; }
    public final boolean include(HDict dict, Pather pather)
    {
      return a.include(dict, pather) && b.include(dict, pather);
    }
  }

//////////////////////////////////////////////////////////////////////////
// Or
//////////////////////////////////////////////////////////////////////////

  static final class Or extends CompoundFilter
  {
    Or(HFilter a, HFilter b) { super(a, b); }
    final String keyword() { return "or"; }
    public final boolean include(HDict dict, Pather pather)
    {
      return a.include(dict, pather) || b.include(dict, pather);
    }
  }

}