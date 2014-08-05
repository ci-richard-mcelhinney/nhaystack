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
    final String URI = "http://localhost/haystack/";
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
//        HDictBuilder meta = new HDictBuilder();
//        meta.add("id", ref);
//        HGrid grid = HGridBuilder.dictsToGrid(meta.toDict(), new HDict[] {}); 
//        grid.dump();

        HRef id = HRef.make("S.Winterfell.Equip1.BooleanWritable");

//        HZincReader zr = new HZincReader(
//            "ver:\"2.0\" id:@S.Winterfell.Equip1.BooleanWritable\n" +
//            "ts,val\n" +
//            "2013-09-26T00:00:00-04:00 New_York,F\n" +
//            "2013-09-26T08:15:00-04:00 New_York,T\n" +
//            "2013-09-26T18:00:00-04:00 New_York,F\n" +
//            "2013-09-27T00:00:00-04:00 New_York,F\n" +
//            "2013-09-27T08:15:00-04:00 New_York,T\n" +
//            "2013-09-27T18:00:00-04:00 New_York,F\n" +
//            "2013-09-28T00:00:00-04:00 New_York,F\n" +
//            "2013-09-29T00:00:00-04:00 New_York,F\n" +
//            "2013-09-30T00:00:00-04:00 New_York,F\n" +
//            "2013-09-30T08:15:00-04:00 New_York,T\n" +
//            "2013-09-30T18:00:00-04:00 New_York,F\n" +
//            "2013-10-01T00:00:00-04:00 New_York,F\n" +
//            "2013-10-01T08:15:00-04:00 New_York,T\n" +
//            "2013-10-01T18:00:00-04:00 New_York,F\n" +
//            "2013-10-02T00:00:00-04:00 New_York,F\n" +
//            "2013-10-02T08:15:00-04:00 New_York,T\n" +
//            "2013-10-02T18:00:00-04:00 New_York,F\n" +
//            "2013-10-03T00:00:00-04:00 New_York,F\n" +
//            "2013-10-03T08:15:00-04:00 New_York,T\n" +
//            "2013-10-03T18:00:00-04:00 New_York,F\n");
//        client.call("scheduleWrite", zr.readGrid());

        HZincReader zr = new HZincReader(
            "ver:\"2.0\" id:@S.Winterfell.Equip1.BooleanWritable\n" +
            "ts,val\n" +
            "2014-07-03T18:59:00-04:00 New_York,F\n" +
            "2014-07-03T18:59:05-04:00 New_York,T\n" +
            "2014-07-03T18:59:10-04:00 New_York,F\n" +
            "2014-07-03T18:59:15-04:00 New_York,F\n" +
            "2014-07-03T18:59:20-04:00 New_York,T\n" +
            "2014-07-03T18:59:25-04:00 New_York,F\n" +
            "2014-07-03T18:59:30-04:00 New_York,F\n" +
            "2014-07-03T18:59:35-04:00 New_York,F\n" +
            "2014-07-03T18:59:40-04:00 New_York,F\n" +
            "2014-07-03T18:59:45-04:00 New_York,T\n" +
            "2014-07-03T18:59:50-04:00 New_York,F\n" +
            "2014-07-03T18:59:55-04:00 New_York,F\n");
        client.call("scheduleWrite", zr.readGrid());

//        HGrid readGrid = scheduleRead(client, id, HDateTimeRange.lastYear(HTimeZone.DEFAULT));
//readGrid.dump();
    }

////////////////////////////////////////////////////////////////
// main
////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        runTests(new String[] { "nhaystack.test.NScheduleTest", }, null);
    }
}
