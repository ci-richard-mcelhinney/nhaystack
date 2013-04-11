//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package haystack.test;

import java.lang.reflect.*;
import haystack.*;
import haystack.client.*;

/**
 * Simple test harness to avoid pulling in dependencies.
 */
public abstract class Test
{

//////////////////////////////////////////////////////////////////////////
// Test Case List
//////////////////////////////////////////////////////////////////////////

  public static String[] TESTS =
  {
    "haystack.test.ValTest",
    "haystack.test.DictTest",
    "haystack.test.FilterTest",
    "haystack.test.GridTest",
    "haystack.test.ZincTest",
    "haystack.test.UtilTest",
    "haystack.test.ClientTest",
  };

//////////////////////////////////////////////////////////////////////////
// Reflection Utils
//////////////////////////////////////////////////////////////////////////

  public Object newInstance(Class cls, Object[] args) throws Exception
  {
    return cls.getConstructor(argsToParams(args)).newInstance(args);
  }

  public Object make(Class cls) throws Exception { return make(cls, new Object[0]); }
  public Object make(Class cls, Object[] args) throws Exception
  {
    return findMethod(cls, "make", args.length).invoke(null, args);
  }

  public Object invoke(Class cls, String name) throws Exception { return invoke(cls, name, new Object[0]); }
  public Object invoke(Class cls, String name, Object[] args) throws Exception
  {
    try
    {
      int paramCount = (args == null) ? 0 : args.length;
      return findMethod(cls, name, paramCount).invoke(null, args);
    }
    catch (InvocationTargetException e)
    {
      throw (Exception)e.getCause();
    }
  }

  public Object invoke(Object instance, String name) throws Exception { return invoke(instance, name, new Object[0]); }
  public Object invoke(Object instance, String name, Object[] args) throws Exception
  {
    try
    {
      int paramCount = (args == null) ? 0 : args.length;
      return findMethod(instance.getClass(), name, paramCount).invoke(instance, args);
    }
    catch (InvocationTargetException e)
    {
      throw (Exception)e.getCause();
    }
  }

  public Method findMethod(Class cls, String name) throws Exception
  {
    return findMethod(cls, name, -1);
  }

  public Method findMethod(Class cls, String name, int paramCount) throws Exception
  {
    Method method;
    method = findMethod(cls, name, paramCount, cls.getMethods()); if (method != null) return method;
    method = findMethod(cls, name, paramCount, cls.getDeclaredMethods()); if (method != null) return method;
    throw new IllegalStateException("No method " + name);
  }

  public Method findMethod(Class cls, String name, int paramCount, Method[] methods) throws Exception
  {
    for (int i=0; i<methods.length; ++i)
      if (methods[i].getName().equals(name))
      {
        if (paramCount != -1 && methods[i].getParameterTypes().length != paramCount) continue;
        return methods[i];
      }
    return null;
  }

  public Object get(Class cls, String name) throws Exception
  {
    return cls.getField(name).get(null);
  }

  public Object get(Object instance, String name) throws Exception
  {
    if (instance instanceof Class)
      return ((Class)instance).getField(name).get(null);
    else
      return instance.getClass().getField(name).get(instance);
  }

  public void set(Class cls, String name, Object val) throws Exception
  {
    cls.getField(name).set(null, val);
  }

  public void set(Object instance, String name, Object val) throws Exception
  {
    if (instance instanceof Class)
      ((Class)instance).getField(name).set(null, val);
    else
      instance.getClass().getField(name).set(instance, val);
  }

  public Class[] argsToParams(Object[] args)
  {
    Class[] params = new Class[args.length];
    for (int i=0; i<params.length; ++i)
      params[i] = argToParam(args[i]);
    return params;
  }

  public Class argToParam(Object arg)
  {
    Class cls = arg.getClass();
    if (cls == Boolean.class) return boolean.class;
    if (cls == Integer.class) return int.class;
    if (cls == Long.class)    return long.class;
    if (cls == Float.class)   return float.class;
    if (cls == Double.class)  return double.class;
    return cls;
  }

//////////////////////////////////////////////////////////////////////////
// Verify Utils
//////////////////////////////////////////////////////////////////////////

  /**
   * Verify the condition is true, otherwise throw an exception.
   */
  public void verify(boolean b)
  {
    if (!b) throw new RuntimeException("Test failed");
    verified++;
    totalVerified++;
  }

  /**
   * Verify a and b are equal.
   */
  public void verifyEq(boolean a, boolean b)
  {
    if (a != b) throw new RuntimeException("Test failed " + a + " != " + b);
    verified++;
    totalVerified++;
  }

  /**
   * Verify a and b are equal.
   */
  public void verifyEq(long a, long b)
  {
    if (a != b) throw new RuntimeException("Test failed " + a + " != " + b);
    verified++;
    totalVerified++;
  }

  /**
   * Verify a and b are equal.
   */
  public void verifyEq(double a, double b)
  {
    if (a != b) throw new RuntimeException("Test failed " + a + " != " + b);
    verified++;
    totalVerified++;
  }

  /**
   * Verify a and b are equal taking into account nulls.
   */
  public void verifyEq(Object a, Object b)
  {
    try
    {
      verify(equals(a, b));
      if (a != null && b != null)
        verifyEq(a.hashCode(), b.hashCode());
    }
    catch (RuntimeException e)
    {
      String aStr = String.valueOf(a);
      String bStr = String.valueOf(b);
      if (a != null) aStr = aStr + " [" + a.getClass().getName() + "]";
      if (b != null) bStr = bStr + " [" + b.getClass().getName() + "]";
      if (!e.getMessage().equals("Test failed")) throw e;
      throw new RuntimeException("Test failed " + aStr  + " != " + bStr);
    }
  }

  /**
   * Verify a and b are equal taking into account nulls.
   */
  public void verifyNotEq(Object a, Object b)
  {
    try
    {
      verify(!equals(a, b));
    }
    catch (RuntimeException e)
    {
      if (!e.getMessage().equals("Test failed")) throw e;
      throw new RuntimeException("Test failed " + a  + " == " + b);
    }
  }

  /**
   * Equals taking into account nulls.
   */
  public static boolean equals(Object a, Object b)
  {
    if (a == null) return b == null;
    else if (b == null) return false;
    boolean eq = a.equals(b);
    if (eq) return true;
    /*
    if (a instanceof HNum && b instanceof HNum)
    {
      HNum ax = (HNum)a;
      HNum bx = (HNum)b;
      if (!equals(ax.unit, bx.unit)) return false;
      return approx(ax.val, bx.val);
    }
    */
    return false;
  }

  /*
  public static boolean approx(double a, double b)
  {
    // need this to check +inf, -inf, and nan
    // if (compare(self, that) == 0) return true;
    double tolerance = Math.min( Math.abs(a/1e6), Math.abs(b/1e6) );
    return Math.abs(a - b) <= tolerance;
  }
  */

  /**
   * Force test failure
   */
  public void fail()
  {
    verify(false);
  }

  /**
   * Check that exception wasn't test failure itself
   */
  public void verifyException(Exception e)
  {
    verbose(e.toString());
    verify(!e.toString().contains("Test failed"));
  }

//////////////////////////////////////////////////////////////////////////
// Misc Utils
//////////////////////////////////////////////////////////////////////////

  /**
   * Print a line to standard out.
   */
  public static void println(Object s)
  {
    System.out.println(s);
  }

  /**
   * Print a line to standard out if in verbose mode.
   */
  public static void verbose(Object s)
  {
    if (verbose) System.out.println(s);
  }

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

  static boolean runTest(String testName)
  {
    try
    {
      Class cls = Class.forName(testName);
      Method[] methods = cls.getMethods();
      for (int i=0; i<methods.length; ++i)
      {
        Method m = methods[i];
        if (!m.getName().startsWith("test")) continue;
        Test test = (Test)cls.newInstance();
        test.testName = testName;
        println("-- Run:  " + testName + "." + m.getName() + "...");
        m.invoke(test, new Object[0]);
        println("   Pass: " + testName + "." + m.getName() + " [" + test.verified + "]");
      }
      return true;
    }
    catch (Throwable e)
    {
      if (e instanceof InvocationTargetException)
        e = ((InvocationTargetException)e).getCause();

      println("### Failed: " + testName);
      e.printStackTrace();

      if (e instanceof CallErrException)
      {
        System.out.println();
        System.out.println("CallErrException server side trace:");
        System.out.println(((CallErrException)e).trace());
      }

      return false;
    }
  }

  public static boolean runTests(String[] tests, String pattern)
  {
    if (pattern == null) pattern = "";

    boolean allPassed = true;
    int testCount = 0;

    for (int i=0; i<tests.length; ++i)
    {
      if (tests[i].startsWith(pattern))
      {
        testCount++;
        if (!runTest(tests[i]))
          allPassed = false;
      }
    }

    return allPassed;
  }

  public static void main(String[] args)
  {
    String pattern = null;
    for (int i=0; i<args.length; ++i)
    {
      String arg = args[i];
      if (arg.startsWith("-"))
      {
        if (arg.equals("-v")) verbose = true;
        else println("Uknown option: " + arg);
      }
      else if (pattern == null)
      {
        pattern = arg;
      }
    }
    runTests(TESTS, pattern);
  }

//////////////////////////////////////////////////////////////////////////
// Fields
//////////////////////////////////////////////////////////////////////////

  public static boolean verbose;
  public static int totalVerified;
  private int verified;
  private String testName;

}
