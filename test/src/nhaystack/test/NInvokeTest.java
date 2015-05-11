//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.test;

import org.projecthaystack.*;
import org.projecthaystack.io.*;
import org.projecthaystack.client.*;
import org.projecthaystack.test.*;
import org.projecthaystack.util.*;

/**
 * NInvokeTest
 */
public class NInvokeTest extends NTest
{
    final String URI = "http://localhost/haystack/";

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

    public void test() throws Exception
    {
        HClient client = HClient.open(URI, "admin", "abcd1234");
        verifyInvoke(client);
    }

    private static void verifyInvoke(HClient client)
    throws Exception
    {
        verifyNumeric(client, HRef.make("S.Winterfell.Equip1.NumericWritable"));
        verifyBoolean(client, HRef.make("S.Winterfell.Equip1.BooleanWritable"));
    }

    private static void verifyNumeric(HClient client, HRef id)
    throws Exception
    {
        int sleep = 1000; // milliseconds to sleep between invocations

        HDictBuilder hd = new HDictBuilder();
        hd.add("duration", HNum.make(5, "min"));
        hd.add("value", HNum.make(222));
        client.invokeAction(id, "override", hd.toDict());
        Thread.sleep(sleep);

        hd = new HDictBuilder();
        hd.add("arg", HNum.make(333));
        client.invokeAction(id, "emergencyOverride", hd.toDict());
        Thread.sleep(sleep);

        client.invokeAction(id, "emergencyAuto", HDict.EMPTY);
        Thread.sleep(sleep);

        client.invokeAction(id, "auto", HDict.EMPTY);
        Thread.sleep(sleep);
    }

    private static void verifyBoolean(HClient client, HRef id)
    throws Exception
    {
        int sleep = 1000; // milliseconds to sleep between invocations

        HDictBuilder hd = new HDictBuilder();
        hd.add("duration", HNum.make(5, "min"));
        // workaround so that TypeUtil.actionArgsToBaja()
        // will construct a BStruct
        hd.add("maxOverrideDuration", HNum.make(0, "ms")); 
        client.invokeAction(id, "active", hd.toDict());
        Thread.sleep(sleep);

        client.invokeAction(id, "emergencyInactive", HDict.EMPTY);
        Thread.sleep(sleep);

        client.invokeAction(id, "emergencyAuto", HDict.EMPTY);
        Thread.sleep(sleep);

        client.invokeAction(id, "auto", HDict.EMPTY);
        Thread.sleep(sleep);
    }

////////////////////////////////////////////////////////////////////////////
//// Invoke Action
////////////////////////////////////////////////////////////////////////////
//
//    void verifyInvokeAction() throws Exception
//    {
//        doVerifyInvokeAction(HRef.make("C.AHU2.NumericWritable"));
//        doVerifyInvokeAction(HRef.make("S.Richmond.AHU2.NumericWritable"));
//    }
//    
//    private void doVerifyInvokeAction(HRef id)
//    {
//        HDictBuilder hd = new HDictBuilder();
//        hd.add("arg", HNum.make(333));
//        client.invokeAction(id, "emergencyOverride", hd.toDict());
//
//        HGrid grid = client.pointWriteArray(id);
//        verifyEq(grid.numRows(), 17);
//        for (int i = 0; i < 17; i++)
//        {
//            verifyEq(grid.row(i).getInt("level"), i+1);
//            switch(i+1)
//            {
//                case 1:
//                    verifyEq(grid.row(i).get("val"), HNum.make(333));
//                    verify(grid.row(i).missing("who"));
//                    break;
//                case 10:
//                    verify(grid.row(i).missing("val"));
//                    verifyEq(grid.row(i).get("who"), HStr.make("admin"));
//                    break;
//                case 17:
//                    verifyEq(grid.row(i).get("val"), HNum.make(111));
//                    verify(grid.row(i).missing("who"));
//                    break;
//                default:
//                    verify(grid.row(i).missing("val"));
//                    verify(grid.row(i).missing("who"));
//                    break;
//            }
//        }
//
//        client.invokeAction(id, "emergencyAuto", HDict.EMPTY);
//
//        grid = client.pointWriteArray(id);
//        verifyEq(grid.numRows(), 17);
//        for (int i = 0; i < 17; i++)
//        {
//            verifyEq(grid.row(i).getInt("level"), i+1);
//            switch(i+1)
//            {
//                case 10:
//                    verify(grid.row(i).missing("val"));
//                    verifyEq(grid.row(i).get("who"), HStr.make("admin"));
//                    break;
//                case 17:
//                    verifyEq(grid.row(i).get("val"), HNum.make(111));
//                    verify(grid.row(i).missing("who"));
//                    break;
//                default:
//                    verify(grid.row(i).missing("val"));
//                    verify(grid.row(i).missing("who"));
//                    break;
//            }
//        }
//    }
//
////////////////////////////////////////////////////////////////
// main
////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        runTests(new String[] { "nhaystack.test.NInvokeTest", }, null);
    }
}
