//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Jun 2011  Brian Frank  Creation
//
package nhaystack.test;

import java.lang.reflect.*;
import haystack.*;
import haystack.client.*;
import haystack.test.*;

/**
 * Simple test harness to avoid pulling in dependencies.
 */
public abstract class NTest extends Test
{
//////////////////////////////////////////////////////////////////////////
// Test Case List
//////////////////////////////////////////////////////////////////////////

    public static String[] TESTS =
    {
        "nhaystack.test.NSimpleClientTest",
        "nhaystack.test.NSupervisorClientTest",
    };

    static HGrid makeNavGrid(HStr navId)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add("navId", navId);
        return HGridBuilder.dictsToGrid(new HDict[] { hd.toDict() });
    }

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

    static boolean showLinkedHistories = false;

    public static void main(String[] args)
    {
        showLinkedHistories = false;
        if ((args.length == 1) && (args[0].equals("showLinkedHistories")))
            showLinkedHistories = true;

        runTests(TESTS, null);
    }

//  public static void main(String[] args)
//  {
//    String pattern = null;
//    for (int i=0; i<args.length; ++i)
//    {
//      String arg = args[i];
//      if (arg.startsWith("-"))
//      {
//        if (arg.equals("-v")) verbose = true;
//        else println("Uknown option: " + arg);
//      }
//      else if (pattern == null)
//      {
//        pattern = arg;
//      }
//    }
//    runTests(TESTS, pattern);
//  }
}
