//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.test;

//import org.projecthaystack.util.*;
//import nhaystack.util.*;

/**
 * NUtilTest
 */
public class NUtilTest extends NTest
{
    public void test() throws Exception
    {
        verifyUtil();
    }

    void verifyPath(String axPath, String hPath) throws Exception
    {
//        verifyEq(SlotUtil.fromNiagara(axPath), hPath);
//        verifyEq(SlotUtil.toNiagara(hPath), axPath);

    }

    void verifyUtil() throws Exception
    {
        verifyPath(
            "/AHU2/BooleanWritable",
            "AHU2.BooleanWritable");

        verifyPath(
            "/AHU2/Boolean$20Writable",
            "AHU2.Boolean-Writable");

        verifyPath(
            "/AHU2/Boolean$20Writable$2f",
            "AHU2.Boolean-Writable~2f");

        verifyPath(
            "/$20AHU2/Boolean$20Writable$2f$20",
            "-AHU2.Boolean-Writable~2f-");

        verifyPath(
            "/$21AHU2/Boolean$21Writable$2f$21",
            "~21AHU2.Boolean~21Writable~2f~21");
    }

////////////////////////////////////////////////////////////////
// main
////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
//        runTests(new String[] { "nhaystack.test.NUtilTest", }, null);
    }
}
