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
import nhaystack.*;

/**
 * NSimpleClientTest -- this test uses nhaystack_simple
 */
public class NSimpleClientTest extends NTest
{
    final String URI = "http://localhost/haystack/";
    HClient client;

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

    public void test() throws Exception
    {
        verifyAuth();
        verifyAbout();
        verifyOps();
        verifyFormats();
        verifyRead();
        verifyNav();

//        verifyWatches();
//        verifyPointWrite();
//        verifyInvokeAction();
//        verifyHisRead();
//        verifyFilter();
    }

//////////////////////////////////////////////////////////////////////////
// Auth
//////////////////////////////////////////////////////////////////////////

    void verifyAuth() throws Exception
    {
        // get bad credentials
        try { HClient.open(URI, "baduser", "badpass").about(); fail(); } catch (CallException e) { verifyException(e); }
        try { HClient.open(URI, "admin",   "badpass").about(); fail(); } catch (CallException e) { verifyException(e); }

        // create proper client
        this.client = HClient.open(URI, "admin", "abcd1234");
    }

//////////////////////////////////////////////////////////////////////////
// About
//////////////////////////////////////////////////////////////////////////

    void verifyAbout() throws Exception
    {
        HDict r = client.about();
        verifyEq(r.getStr("haystackVersion"), "2.0");
        verifyEq(r.getStr("productName"), "Niagara AX");
//        verifyEq(r.getStr("productVersion"), "3.6.47"); 
        verifyEq(r.getStr("tz"), HTimeZone.DEFAULT.name);
    }

//////////////////////////////////////////////////////////////////////////
// Ops
//////////////////////////////////////////////////////////////////////////

    void verifyOps() throws Exception
    {
        HGrid g = client.ops();

        // verify required columns
        verify(g.col("name")  != null);
        verify(g.col("summary") != null);

        // verify required ops
        verifyGridContains(g, "name", "about");
        verifyGridContains(g, "name", "ops");
        verifyGridContains(g, "name", "formats");
        verifyGridContains(g, "name", "read");
    }

//////////////////////////////////////////////////////////////////////////
// Formats
//////////////////////////////////////////////////////////////////////////

    void verifyFormats() throws Exception
    {
        HGrid g = client.formats();

        // verify required columns
        verify(g.col("mime")  != null);
        verify(g.col("read") != null);
        verify(g.col("write") != null);

        // verify required ops
        verifyGridContains(g, "mime", "text/plain");
        verifyGridContains(g, "mime", "text/zinc");
    }

//////////////////////////////////////////////////////////////////////////
// Reads
//////////////////////////////////////////////////////////////////////////

    void verifyRead() throws Exception
    {
        HGrid grid = client.readAll("id");

//        for (int i = 0; i < grid.numRows(); i++)
//            System.out.println(i + ", " + grid.row(i).get("id"));

        verifyEq(grid.numRows(), 13);
        verifyEq(grid.row( 0).id(), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 1).id(), HRef.make("S.Winterfell.Equip1.BooleanWritable"));
        verifyEq(grid.row( 2).id(), HRef.make("S.Winterfell.Equip1.EnumWritable"));
        verifyEq(grid.row( 3).id(), HRef.make("S.Winterfell.Equip1.StringWritable"));
        verifyEq(grid.row( 4).id(), HRef.make("S.Winterfell.Equip1.SineWave1"));
        verifyEq(grid.row( 5).id(), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row( 6).id(), HRef.make("S.Winterfell.Equip1.SineWave2"));
        verifyEq(grid.row( 7).id(), HRef.make("S.Winterfell.Equip2.SineWave3"));
        verifyEq(grid.row( 8).id(), HRef.make("S.Winterfell.Equip2"));
        verifyEq(grid.row( 9).id(), HRef.make("S.Winterfell.Equip1.SineWave4"));
        verifyEq(grid.row(10).id(), HRef.make("C.SineWave5"));
        verifyEq(grid.row(11).id(), HRef.make("H.nhaystack_simple.AuditHistory"));
        verifyEq(grid.row(12).id(), HRef.make("H.nhaystack_simple.LogHistory"));

        verifyEq(grid.row( 1).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 2).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 3).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 4).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 5).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 6).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 7).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 8).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 9).get("siteRef"), HRef.make("S.Winterfell"));

        verifyEq(grid.row(1).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row(2).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row(3).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row(4).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row(6).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row(7).get("equipRef"), HRef.make("S.Winterfell.Equip2"));
        verifyEq(grid.row(9).get("equipRef"), HRef.make("S.Winterfell.Equip1"));

//        for (int i = 0; i < grid.numRows(); i++)
//        {
//            HRow row = grid.row(i);
//            if (row.has("equipRef"))
//                System.out.println(i + ", " + row.id() + ", " + row.get("equipRef"));
//        }

//        //////////////////////////////////////////
//
//        HDict dict = client.readById(HRef.make("C.Foo.SineWave1"));
//        verifyEq(dict.get("axType"), HStr.make("kitControl:SineWave"));
//        verify(dict.has("foo"));
//        verify(dict.has("bar"));
//        verifyEq(dict.get("kind"), HStr.make("Number"));
//        verify(dict.has("his"));
//        verifyEq(dict.get("hisInterpolate"), HStr.make("cov"));
//        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/Foo/SineWave1"));
//        verifyEq(dict.get("unit"), HStr.make("°F"));
//        verify(dict.has("point"));
//        verifyEq(dict.get("tz"), localTz());
//        verify(dict.has("cur"));
//        double curVal = dict.getDouble("curVal");
//        verifyEq(dict.get("curStatus"), HStr.make("ok"));
//        verify(curVal >= 0.0 && curVal <= 100.0);
//
//        verifyEq(dict.get("dis"), HStr.make("Foo_SineWave1"));
//        verifyEq(dict.get("navName"), HStr.make("Foo_SineWave1"));
//        verifyEq(dict.get("navNameFormat"), HStr.make("%parent.displayName%_%displayName%"));
//
//        //////////////////////////////////////////
//
//        dict = client.readById(HRef.make("C.Foo.Sine-Wave2~2fabc"));
//        verifyEq(dict.get("axType"), HStr.make("kitControl:SineWave"));
//        verify(dict.missing("foo"));
//        verify(dict.missing("bar"));
//        verifyEq(dict.get("kind"), HStr.make("Number"));
//        verify(dict.has("his"));
//        verifyEq(dict.get("curStatus"), HStr.make("ok"));
//        verify(dict.has("hisInterpolate"));
//        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/Foo/Sine$20Wave2$2fabc"));
//        verifyEq(dict.get("unit"), HStr.make("psi"));
//        verify(dict.has("point"));
//        verify(dict.has("tz"));
//        verify(dict.has("cur"));
//        curVal = dict.getDouble("curVal");
//        verify(curVal >= 0.0 && curVal <= 100.0);
//
//        verifyEq(dict.get("dis"), HStr.make("Sine-Wave2~2fabc"));
//        verifyEq(dict.get("navName"), HStr.make("Sine-Wave2~2fabc"));
//        verify(dict.missing("navNameFormat"));
//
//        //////////////////////////////////////////
//
//        dict = client.readById(HRef.make("S.Richmond.AHU2.NumericWritable"));
//
//        //////////////////////////////////////////
//
//        dict = client.readById(HRef.make("H.nhaystack_simple.AuditHistory"));
//        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        verify(dict.missing("kind"));
//        verify(dict.has("his"));
//        verify(dict.missing("cur"));
//        verify(dict.missing("curStatus"));
//        verify(dict.missing("curVal"));
//        verifyEq(dict.get("tz"), localTz());
//        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/AuditHistory"));
//        verify(dict.missing("hisInterpolate"));
//        verify(dict.missing("unit"));
//
//        dict = client.readById(HRef.make("H.nhaystack_simple.LogHistory"));
//        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        verify(dict.missing("kind"));
//        verify(dict.has("his"));
//        verify(dict.missing("cur"));
//        verify(dict.missing("curStatus"));
//        verify(dict.missing("curVal"));
//        verifyEq(dict.get("tz"), localTz());
//        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/LogHistory"));
//        verify(dict.missing("hisInterpolate"));
//        verify(dict.missing("unit"));
//
//        //        dict = client.readById(HRef.make("H.nhaystack_simple.SineWave3"));
//        //        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        //        verifyEq(dict.get("kind"), HStr.make("Number"));
//        //        verify(dict.has("his"));
//        //        verify(dict.missing("cur"));
//        //        verify(dict.missing("curStatus"));
//        //        verify(dict.missing("curVal"));
//        //        verifyEq(dict.get("tz"), localTz());
//        //        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/SineWave3"));
//        //        verify(dict.missing("hisInterpolate"));
//        //        verifyEq(dict.get("unit"), HStr.make("psi"));
//
//        try { client.readById(HRef.make("c.Mg~~")); } catch(Exception e) { verifyException(e); }
    }

//////////////////////////////////////////////////////////////////////////
// Nav
//////////////////////////////////////////////////////////////////////////

    void verifyNav() throws Exception
    {
        HGrid grid = client.call("read", makeIdGrid(HUri.make("sep:/Winterfell")));
        verifyEq(grid.numRows(), 1);
        verifyEq(grid.row(0).id(), HRef.make("S.Winterfell"));

        grid = client.call("read", makeIdGrid(HUri.make("sep:/Winterfell/Equip1")));
        verifyEq(grid.numRows(), 1);
        verifyEq(grid.row(0).id(), HRef.make("S.Winterfell.Equip1"));

        HGrid n = makeNavGrid(HStr.make("sep:/Winterfell/Equip1"));
        grid = client.call("nav", n);
        verifyEq(grid.numRows(), 6);
        verifyEq(grid.row(0).id(), HRef.make("S.Winterfell.Equip1.BooleanWritable"));
        verifyEq(grid.row(1).id(), HRef.make("S.Winterfell.Equip1.EnumWritable"));
        verifyEq(grid.row(2).id(), HRef.make("S.Winterfell.Equip1.StringWritable"));
        verifyEq(grid.row(3).id(), HRef.make("S.Winterfell.Equip1.SineWave1"));
        verifyEq(grid.row(4).id(), HRef.make("S.Winterfell.Equip1.SineWave2"));
        verifyEq(grid.row(5).id(), HRef.make("S.Winterfell.Equip1.SineWave4"));

//        HGrid grid = client.call("nav", HGrid.EMPTY);
//        verifyEq(grid.numRows(), 3);
//        verifyEq(grid.row(0).get("navId"), HStr.make("slot:/"));
//        verifyEq(grid.row(0).get("dis"),   HStr.make("ComponentSpace"));
//        verifyEq(grid.row(1).get("navId"), HStr.make("his:/"));
//        verifyEq(grid.row(1).get("dis"),   HStr.make("HistorySpace"));
//        verifyEq(grid.row(2).get("navId"), HStr.make("sep:/"));
//        verifyEq(grid.row(2).get("dis"),   HStr.make("Site"));
//
//        HGrid n = makeNavGrid(HStr.make("his:/"));
//        grid = client.call("nav", n);
//        verifyEq(grid.numRows(), 1);
//        verifyEq(grid.row(0).get("navId"), HStr.make("his:/nhaystack_simple"));
//
//        n = makeNavGrid(HStr.make("his:/nhaystack_simple"));
//        grid = client.call("nav", n);
////        verifyEq(grid.numRows(), 3);
//        verifyEq(grid.numRows(), 2);
//
//        n = makeNavGrid(HStr.make("slot:/"));
//        grid = client.call("nav", n);
//        verifyEq(grid.numRows(), 7);
//        verifyEq(grid.row(0).get("navId"), HStr.make("slot:/Services"));
//        verifyEq(grid.row(1).get("navId"), HStr.make("slot:/Drivers"));
//        verifyEq(grid.row(2).get("navId"), HStr.make("slot:/Foo"));
//        verifyEq(grid.row(3).get("navId"), HStr.make("slot:/Richmond"));
//        verifyEq(grid.row(4).get("navId"), HStr.make("slot:/AHU2"));
//        verifyEq(grid.row(5).get("navId"), HStr.make("slot:/AHU3"));
//
//        traverseComponents((HStr) grid.row(0).get("navId"));
//        traverseComponents((HStr) grid.row(1).get("navId"));
//        traverseComponents((HStr) grid.row(2).get("navId"));
//
////[sep:/] 'Site'
////    [sep:/Richmond] 'Richmond'
////        [sep:/Richmond/AHU1] 'Richmond AHU1'
////            [---] 'Richmond AHU1 AHU2_BooleanWritable'
////            [---] 'Richmond AHU1 AHU3_BooleanWritable'
////        [sep:/Richmond/AHU2] 'Richmond AHU2'
////            [---] 'Richmond AHU2 NumericWritable'
////            [---] 'Richmond AHU2 NumericWritable1'
////        [sep:/Richmond/AHU3] 'Richmond AHU3'
////            [---] 'Richmond AHU3 NumericWritable'
////            [---] 'Richmond AHU3 NumericWritable1'
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/")));
//        verifyEq(grid.numRows(), 1);
//        verifyEq(grid.row(0).get("navId"), HStr.make("sep:/Richmond"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("Richmond"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond")));
//        verifyEq(grid.numRows(), 3);
//        verifyEq(grid.row(0).get("navId"), HStr.make("sep:/Richmond/AHU1"));
//        verifyEq(grid.row(1).get("navId"), HStr.make("sep:/Richmond/AHU2"));
//        verifyEq(grid.row(2).get("navId"), HStr.make("sep:/Richmond/AHU3"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("Richmond AHU1"));
//        verifyEq(grid.row(1).get("dis"), HStr.make("Richmond AHU2"));
//        verifyEq(grid.row(2).get("dis"), HStr.make("Richmond AHU3"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU1")));
//        verifyEq(grid.numRows(), 2);
//        verify(grid.row(0).missing("navId"));
//        verify(grid.row(1).missing("navId"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("Richmond AHU1 AHU2_BooleanWritable"));
//        verifyEq(grid.row(1).get("dis"), HStr.make("Richmond AHU1 AHU3_BooleanWritable"));
//        
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU2")));
//        verifyEq(grid.numRows(), 2);
//        verify(grid.row(0).missing("navId"));
//        verify(grid.row(1).missing("navId"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("Richmond AHU2 NumericWritable"));
//        verifyEq(grid.row(1).get("dis"), HStr.make("Richmond AHU2 NumericWritable1"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU3")));
//        verifyEq(grid.numRows(), 2);
//        verify(grid.row(0).missing("navId"));
//        verify(grid.row(1).missing("navId"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("Richmond AHU3 NumericWritable"));
//        verifyEq(grid.row(1).get("dis"), HStr.make("Richmond AHU3 NumericWritable1"));
    }

//    private void traverseComponents(HStr navId)
//    {
//        HGrid grid = client.call("nav", makeNavGrid(navId));
//
//        for (int i = 0; i < grid.numRows(); i++)
//        {
//            if (grid.row(i).has("navId"))
//                traverseComponents((HStr) grid.row(i).get("navId"));
//        }
//    }
//
////////////////////////////////////////////////////////////////////////////
//// His Reads
////////////////////////////////////////////////////////////////////////////
//
//    void verifyHisRead() throws Exception
//    {
//        HGrid grid = client.readAll("his");
//        verifyEq(grid.numRows(), 5);
//
//        ///////////////////////////////////////////////
//
//        HDict dict = client.read("axSlotPath==\"slot:/Foo/SineWave1\"");
//        HGrid his = client.hisRead(dict.id(), "today");
//
//        verifyEq(his.meta().id(), dict.id());
//        verify(his.numRows() > 0);
//
//        int last = his.numRows()-1;
//        verifyEq(ts(his.row(last)).date, HDate.today());
//
//        verifyEq(numVal(his.row(0)).unit, "°F");
//
//        ///////////////////////////////////////////////
//
//        dict = client.read("axHistoryId==\"/nhaystack_simple/LogHistory\"");
//        his = client.hisRead(dict.id(), "today");
//        verifyEq(his.meta().id(), dict.id());
//        verify(his.numRows() > 0);
//
//        last = his.numRows()-1;
//        verifyEq(ts(his.row(last)).date, HDate.today());
//
//        ///////////////////////////////////////////////
//
////        dict = client.read("axHistoryId==\"/nhaystack_simple/SineWave3\"");
////        his = client.hisRead(dict.id(), "today");
////
////        verifyEq(his.meta().id(), dict.id());
//
//        ///////////////////////////////////////////////
//
//        client.hisRead(HRef.make("C.AHU2.NumericWritable"), "today");
//        client.hisRead(HRef.make("S.Richmond.AHU2.NumericWritable"), "today");
//    }
//
////////////////////////////////////////////////////////////////////////////
//// Watches
////////////////////////////////////////////////////////////////////////////
//
//    void verifyWatches() throws Exception
//    {
//        // create new watch
//        HWatch w = client.watchOpen("NHaystack Simple Test");
//        verifyEq(w.id(), null);
//        verifyEq(w.dis(), "NHaystack Simple Test");
//
//        // do query to get some recs
//        HGrid recs = client.readAll("point");
//        verify(recs.numRows() >= 4);
//        HDict a = recs.row(0);
//        HDict b = recs.row(1);
//        HDict c = recs.row(2);
//        HDict d = recs.row(3);
//
////System.out.println(a);
////System.out.println(b);
////System.out.println(c);
////System.out.println(d);
//
//        // do first sub
//        HGrid sub = w.sub(new HRef[] { a.id(), b.id() });
//        verifyEq(sub.numRows(), 2);
//        verifyEq(sub.row(0).dis(), a.dis());
//        verifyEq(sub.row(1).dis(), b.dis());
//
//        // now add c, d
//        sub = w.sub(new HRef[] { c.id(), d.id() }, false);
//        verifyEq(sub.numRows(), 2);
//        verifyEq(sub.row(0).dis(), c.dis());
//        verifyEq(sub.row(1).dis(), d.dis());
//
//        // verify state of watch now
//        verify(client.watch(w.id()) == w);
//        verifyEq(client.watches().length, 1);
//        verify(client.watches()[0] == w);
//        verifyEq(w.lease().millis(), 2L * 60 * 1000);
//
//        // poll refresh
//        HGrid poll = w.pollRefresh();
//        verifyEq(poll.numRows(), 4);
//        verifyGridContains(poll, "id", a.id());
//        verifyGridContains(poll, "id", b.id());
//        verifyGridContains(poll, "id", c.id());
//        verifyGridContains(poll, "id", d.id());
//
//        // poll changes
//        Thread.sleep(2000); // wait for the sine waves to tick over
//        poll = w.pollChanges();
//        verifyEq(poll.numRows(), 2);
//
//        // remove d, and then poll refresh
//        w.unsub(new HRef[] { d.id() });
//        poll = w.pollRefresh();
//        verifyEq(poll.numRows(), 3);
//
//        // close
//        w.close();
//        try { w.pollRefresh(); fail(); } catch (Exception e) { verifyException(e); }
//        verifyEq(client.watch(w.id(), false), null);
//        verifyEq(client.watches().length, 0);
//
//        // check bad id 
//        w = client.watchOpen("Bogus Test");
//        HRef badId = HRef.make("c." + Base64.URI.encode("badBadBad"));
//        try { w.sub(new HRef[] { badId }).dump(); fail(); } catch (Exception e) { verifyException(e); }
//    }
//
////////////////////////////////////////////////////////////////////////////
//// Point Write
////////////////////////////////////////////////////////////////////////////
//
//    void verifyPointWrite() throws Exception
//    {
//        doVerifyPointWrite(HRef.make("C.AHU2.NumericWritable"));
//        doVerifyPointWrite(HRef.make("S.Richmond.AHU2.NumericWritable"));
//    }
//    
//    private void doVerifyPointWrite(HRef id)
//    {
//        HGrid grid = client.pointWrite(id, 10, "admin", HNum.make(222), null);
//        verifyEq(grid.numRows(), 17);
//        for (int i = 0; i < 17; i++)
//        {
//            verifyEq(grid.row(i).getInt("level"), i+1);
//            switch(i+1)
//            {
//                case 10:
//                    verifyEq(grid.row(i).get("val"), HNum.make(222));
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
//        grid = client.pointWrite(id, 10, "admin", null, null);
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
//
//        // just make sure this works with no level, etc
//        grid = client.pointWriteArray(id);
//    }
//
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
//////////////////////////////////////////////////////////////////
//// filter
//////////////////////////////////////////////////////////////////
//
//    void verifyFilter() throws Exception
//    {
//        HGrid grid = client.readAll("point and equipRef->navName == \"AHU1\"");
//        verifyEq(grid.numRows(), 2);
//        verifyEq(grid.row(0).get("axSlotPath"), HStr.make("slot:/AHU2/BooleanWritable"));
//        verifyEq(grid.row(1).get("axSlotPath"), HStr.make("slot:/AHU3/BooleanWritable"));
//    }
//
////////////////////////////////////////////////////////////////
// main
////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        runTests(new String[] { "nhaystack.test.NSimpleClientTest", }, null);
    }
}
