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
    //final String URI = "http://localhost/haystack/";
    final String URI = "http://192.168.1.125/haystack/";
    HClient client;

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

    public void test() throws Exception
    {
        this.client = HClient.open(URI, "admin", "abcd1234");

        verifySchedule();
    }

    private HGrid scheduleRead(HClient client, HRef id, Object range)
    {
        HGridBuilder b = new HGridBuilder();
        b.addCol("id");
        b.addCol("range");
        b.addRow(new HVal[] { id, HStr.make(range.toString()) });
        HGrid req = b.toGrid();
        HGrid res = client.call("scheduleRead", req);
        return res;
    }

    void verifySchedule() throws Exception
    {
        HRef id = HRef.make("S.Winterfell.Equip1.BooleanWritable");

//        HZincReader zr = new HZincReader(
//            "ver:\"2.0\" id:@S.Winterfell.Equip1.BooleanWritable\n" +
//            "ts,val\n" +
//            "2014-07-29T13:55:00-04:00 New_York,F\n" +
//            "2014-07-29T13:55:05-04:00 New_York,T\n" +
//            "2014-07-29T13:55:10-04:00 New_York,F\n" +
//            "2014-07-29T13:55:15-04:00 New_York,T\n" +
//            "2014-07-29T13:55:20-04:00 New_York,F\n" +
//            "2014-07-29T13:55:25-04:00 New_York,T\n" +
//            "2014-07-29T13:55:30-04:00 New_York,F\n" +
//            "2014-07-29T13:55:35-04:00 New_York,T\n" +
//            "2014-07-29T13:55:40-04:00 New_York,F\n" +
//            "2014-07-29T13:55:45-04:00 New_York,T\n" +
//            "2014-07-29T13:55:50-04:00 New_York,F\n" +
//            "2014-07-29T13:55:55-04:00 New_York,T\n");
//        client.call("scheduleWrite", zr.readGrid());

        HZincReader zr = new HZincReader(
            "ver:\"2.0\" id:@S.Winterfell.Equip1.BooleanWritable\n" +
            "ts,val\n" +
            "2014-07-21T18:00:00-04:00 New_York,F\n" +
            "2014-07-22T18:00:00-04:00 New_York,T\n" +
            "2014-07-23T18:00:00-04:00 New_York,F\n" +
            "2014-07-24T18:00:00-04:00 New_York,T\n" +
            "2014-07-25T18:00:00-04:00 New_York,F\n" +
            "2014-07-26T18:00:00-04:00 New_York,T\n" +
            "2014-07-27T18:00:00-04:00 New_York,F\n");
        client.call("scheduleWrite", zr.readGrid());
    }

////////////////////////////////////////////////////////////////
// main
////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        runTests(new String[] { "nhaystack.test.NScheduleTest", }, null);
    }
}
