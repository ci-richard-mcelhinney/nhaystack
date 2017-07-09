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
 * NScheduleTest
 */
public class NScheduleTest extends NTest
{
    final String URI = "https://localhost/haystack/";
    HClient client;

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

    public void test() throws Exception
    {
        this.client = HClient.open(URI, "admin", "Abcde12345");
        verifySchedule();
    }

    void verifySchedule() throws Exception
    {
        System.out.println("verifySchedule aaa");

//        HRef id = HRef.make("S.Tatooine.Equip1.BooleanWritable");
//        client.pointWrite(id, 15, "foo", HBool.TRUE, HNum.ZERO);

//        HRef id = HRef.make("C.Misc_Comps.BooleanSchedule");
//        client.pointWrite(id, 15, "foo", HBool.TRUE, HNum.ZERO, schedItems());
    }

    private HHisItem[] schedItems()
    {
        return new HHisItem[] {
            HHisItem.make(HDateTime.make("2015-01-09T10:00:00-05:00 New_York"),HBool.FALSE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:05-05:00 New_York"),HBool.TRUE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:10-05:00 New_York"),HBool.FALSE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:15-05:00 New_York"),HBool.TRUE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:20-05:00 New_York"),HBool.FALSE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:25-05:00 New_York"),HBool.TRUE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:30-05:00 New_York"),HBool.FALSE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:35-05:00 New_York"),HBool.TRUE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:40-05:00 New_York"),HBool.FALSE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:45-05:00 New_York"),HBool.TRUE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:50-05:00 New_York"),HBool.FALSE),
            HHisItem.make(HDateTime.make("2015-01-09T10:00:55-05:00 New_York"),HBool.TRUE),
        };
//        return new HHisItem[] {
//            HHisItem.make(HDateTime.make("2014-07-29T10:00:00-04:00 New_York"),HBool.FALSE),
//            HHisItem.make(HDateTime.make("2014-07-29T11:00:00-04:00 New_York"),HBool.TRUE),
//            HHisItem.make(HDateTime.make("2014-07-29T12:00:00-04:00 New_York"),HBool.FALSE),
//            HHisItem.make(HDateTime.make("2014-07-29T13:00:00-04:00 New_York"),HBool.TRUE),
//            HHisItem.make(HDateTime.make("2014-07-29T14:00:00-04:00 New_York"),HBool.FALSE),
//            HHisItem.make(HDateTime.make("2014-07-29T15:00:00-04:00 New_York"),HBool.TRUE),
//            HHisItem.make(HDateTime.make("2014-07-29T16:00:00-04:00 New_York"),HBool.FALSE),
//            HHisItem.make(HDateTime.make("2014-07-29T17:00:00-04:00 New_York"),HBool.TRUE),
//            HHisItem.make(HDateTime.make("2014-07-29T18:00:00-04:00 New_York"),HBool.FALSE),
//            HHisItem.make(HDateTime.make("2014-07-29T19:00:00-04:00 New_York"),HBool.TRUE),
//            HHisItem.make(HDateTime.make("2014-07-29T20:00:00-04:00 New_York"),HBool.FALSE),
//            HHisItem.make(HDateTime.make("2014-07-29T21:00:00-04:00 New_York"),HBool.TRUE),
//        };
    }

////////////////////////////////////////////////////////////////
// main
////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        runTests(new String[] { "nhaystack.test.NScheduleTest", }, null);
    }
}
