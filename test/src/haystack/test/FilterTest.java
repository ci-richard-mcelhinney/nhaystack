//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Oct 2011  Brian Frank  Creation
//
package haystack.test;

import haystack.*;
import java.util.*;

/**
 * FilterTest tests parsing and filtering of HFilter
 */
public class FilterTest extends Test
{

//////////////////////////////////////////////////////////////////////////
// Path
//////////////////////////////////////////////////////////////////////////

  /* Path isn't public, so we can't run these tests all the time
  public void testPath()
  {
    // single name
    HFilter.Path path = HFilter.Path.make("foo");
    verifyEq(path.size(), 1);
    verifyEq(path.get(0), "foo");
    verifyEq(path.toString(), "foo");
    verifyEq(path, HFilter.Path.make("foo"));

    // two names
    path = HFilter.Path.make("foo->bar");
    verifyEq(path.size(), 2);
    verifyEq(path.get(0), "foo");
    verifyEq(path.get(1), "bar");
    verifyEq(path.toString(), "foo->bar");
    verifyEq(path, HFilter.Path.make("foo->bar"));

    // three names
    path = HFilter.Path.make("x->y->z");
    verifyEq(path.size(), 3);
    verifyEq(path.get(0), "x");
    verifyEq(path.get(1), "y");
    verifyEq(path.get(2), "z");
    verifyEq(path.toString(), "x->y->z");
    verifyEq(path, HFilter.Path.make("x->y->z"));
  }
  */

//////////////////////////////////////////////////////////////////////////
// Identity
//////////////////////////////////////////////////////////////////////////

  public void testIdentity()
  {
    verifyEq(HFilter.has("a"), HFilter.has("a"));
    verifyNotEq(HFilter.has("a"), HFilter.has("b"));
  }

//////////////////////////////////////////////////////////////////////////
// Parse
//////////////////////////////////////////////////////////////////////////

  public void testParse()
  {
    // basics
    verifyParse("x", HFilter.has("x"));
    verifyParse("foo", HFilter.has("foo"));
    verifyParse("fooBar", HFilter.has("fooBar"));
    verifyParse("foo7Bar", HFilter.has("foo7Bar"));
    verifyParse("foo_bar->a", HFilter.has("foo_bar->a"));
    verifyParse("a->b->c", HFilter.has("a->b->c"));
    verifyParse("not foo", HFilter.missing("foo"));

    // verify Zinc only literals do not work
    verifyEq(HFilter.make("x==T", false), null);
    verifyEq(HFilter.make("x==F", false), null);
    verifyEq(HFilter.make("x==F", false), null);

    // bool literals
    verifyParse("x->y==true", HFilter.eq("x->y", HBool.TRUE));
    verifyParse("x->y!=false", HFilter.ne("x->y", HBool.FALSE));

    // str literals
    verifyParse("x==\"hi\"", HFilter.eq("x", HStr.make("hi")));
    verifyParse("x!=\"\\\"hi\\\"\"",  HFilter.ne("x", HStr.make("\"hi\"")));
    verifyParse("x==\"_\\uabcd_\\n_\"", HFilter.eq("x", HStr.make("_\uabcd_\n_")));

    // uri literals
    verifyParse("ref==`http://foo/?bar`", HFilter.eq("ref", HUri.make("http://foo/?bar")));
    verifyParse("ref->x==`file name`", HFilter.eq("ref->x", HUri.make("file name")));
    verifyParse("ref == `foo bar`", HFilter.eq("ref", HUri.make("foo bar")));

    // int literals
    verifyParse("num < 4", HFilter.lt("num", n(4)));
    verifyParse("num <= -99", HFilter.le("num", n(-99)));

    // float literals
    verifyParse("num < 4.0", HFilter.lt("num", n(4f)));
    verifyParse("num <= -9.6", HFilter.le("num", n(-9.6f)));
    verifyParse("num > 400000", HFilter.gt("num", n(4e5f)));
    verifyParse("num >= 16000", HFilter.ge("num", n(1.6e+4f)));
    verifyParse("num >= 2.16", HFilter.ge("num", n(2.16)));

    // unit literals
    verifyParse("dur < 5ns", HFilter.lt("dur", n(5,"ns")));
    verifyParse("dur < 10kg", HFilter.lt("dur", n(10, "kg")));
    verifyParse("dur < -9sec", HFilter.lt("dur", n(-9, "sec")));
    verifyParse("dur < 2.5hr", HFilter.lt("dur", n(2.5, "hr")));

    // date, time, datetime
    verifyParse("foo < 2009-10-30", HFilter.lt("foo", HDate.make("2009-10-30")));
    verifyParse("foo < 08:30:00", HFilter.lt("foo", HTime.make("08:30:00")));
    verifyParse("foo < 13:00:00", HFilter.lt("foo", HTime.make("13:00:00")));

    // ref literals
    verifyParse("author == @xyz", HFilter.eq("author", HRef.make("xyz")));
    verifyParse("author==@xyz:foo.bar", HFilter.eq("author", HRef.make("xyz:foo.bar")));

    // and
    verifyParse("a and b", HFilter.has("a").and(HFilter.has("b")));
    verifyParse("a and b and c == 3", HFilter.has("a").and( HFilter.has("b").and(HFilter.eq("c", n(3))) ));

    // or
    verifyParse("a or b", HFilter.has("a").or(HFilter.has("b")));
    verifyParse("a or b or c == 3", HFilter.has("a").or(HFilter.has("b").or(HFilter.eq("c", n(3)))));

    // parens
    verifyParse("(a)", HFilter.has("a"));
    verifyParse("(a) and (b)", HFilter.has("a").and(HFilter.has("b")));
    verifyParse("( a )  and  ( b ) ", HFilter.has("a").and(HFilter.has("b")));
    verifyParse("(a or b) or (c == 3)", HFilter.has("a").or(HFilter.has("b")).or(HFilter.eq("c", n(3))));

    // combo
    HFilter isA = HFilter.has("a");
    HFilter isB = HFilter.has("b");
    HFilter isC = HFilter.has("c");
    HFilter isD = HFilter.has("d");
    verifyParse("a and b or c", (isA.and(isB)).or(isC));
    verifyParse("a or b and c", isA.or(isB.and(isC)));
    verifyParse("a and b or c and d", (isA.and(isB)).or(isC.and(isD)));
    verifyParse("(a and (b or c)) and d", isA.and(isB.or(isC)).and(isD));
    verifyParse("(a or (b and c)) or d", isA.or(isB.and(isC)).or(isD));
  }

  void verifyParse(String s, HFilter expected)
  {
    HFilter actual = HFilter.make(s);
    verifyEq(actual, expected);
  }

//////////////////////////////////////////////////////////////////////////
// Include
//////////////////////////////////////////////////////////////////////////

  public void testInclude()
  {
    HDict a = new HDictBuilder()
      .add("dis", "a")
      .add("num", 100)
      .add("foo", 99)
      .add("date", HDate.make(2011,10,5))
      .toDict();

   HDict b = new HDictBuilder()
      .add("dis", "b")
      .add("num", 200)
      .add("foo", 88)
      .add("date", HDate.make(2011,10,20))
      .add("bar")
      .add("ref", HRef.make("a"))
      .toDict();

   HDict c = new HDictBuilder()
      .add("dis", "c")
      .add("num", 300)
      .add("ref", HRef.make("b"))
      .add("bar")
      .toDict();

    final HashMap db = new HashMap();
    db.put("a", a);
    db.put("b", b);
    db.put("c", c);

    verifyInclude(db, "dis",                "a,b,c");
    verifyInclude(db, "dis == \"b\"",       "b");
    verifyInclude(db, "dis != \"b\"",       "a,c");
    verifyInclude(db, "dis <= \"b\"",       "a,b");
    verifyInclude(db, "dis >  \"b\"",       "c");
    verifyInclude(db, "num < 200",          "a");
    verifyInclude(db, "num <= 200",         "a,b");
    verifyInclude(db, "num > 200",          "c");
    verifyInclude(db, "num >= 200",         "b,c");
    verifyInclude(db, "date",               "a,b");
    verifyInclude(db, "date == 2011-10-20", "b");
    verifyInclude(db, "date < 2011-10-10",  "a");
    verifyInclude(db, "foo",                "a,b");
    verifyInclude(db, "not foo",            "c");
    verifyInclude(db, "foo == 88",          "b");
    verifyInclude(db, "foo != 88",          "a");
    verifyInclude(db, "foo == \"x\"",       "");
    verifyInclude(db, "ref",                "b,c");
    verifyInclude(db, "ref->dis",           "b,c");
    verifyInclude(db, "ref->dis == \"a\"",  "b");
    verifyInclude(db, "ref->bar",           "c");
    verifyInclude(db, "not ref->bar",       "a,b");
    verifyInclude(db, "foo and bar",        "b");
    verifyInclude(db, "foo or bar",         "a,b,c");
    verifyInclude(db, "(foo and bar) or num==300",  "b,c");
    verifyInclude(db, "foo and bar and num==300",   "");
  }

  void verifyInclude(final HashMap map, String query, String expected)
  {
    HFilter.Pather db = new HFilter.Pather()
    {
      public HDict find(String id) { return (HDict)map.get(id); }
    };

    HFilter q = HFilter.make(query);

    String actual = "";
    for (int c='a'; c<='c'; ++c)
    {
      String id = "" + (char)c;
      if (q.include(db.find(id), db))
        actual += actual.length() > 0 ? ","+id : id;
    }
    verifyEq(expected, actual);
  }

//////////////////////////////////////////////////////////////////////////
// Utils
//////////////////////////////////////////////////////////////////////////

  HNum n(double v) { return HNum.make(v); }
  HNum n(double v, String u) { return HNum.make(v, u); }

}