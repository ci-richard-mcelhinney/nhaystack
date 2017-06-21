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
import org.projecthaystack.util.*;
import nhaystack.*;

/**
 * NSimpleClientTest -- this test uses nhaystack_simple
 * 
 * Uses the station called 'nhaystack_simple' contained in
 * the repo for this module.
 * 
 * The station ports are configured as follows:
 *   Fox = 1911
 *   Web = 8080
 */
public class NSimpleClientTest extends NTest
{
    final String URI = "http://localhost:8080/haystack/";
    HClient client;
    boolean debug = true;

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

    public void test() throws Exception
    {
        verifyAuth();
        System.out.println("--- Passed verifyAuth");
        verifyAbout();
        System.out.println("--- Passed verifyAbout");
        verifyOps();
        System.out.println("--- Passed verifyOps");
        verifyFormats();
        System.out.println("--- Passed verifyFormats");
        verifyRead();
        System.out.println("--- Passed verifyRead");
        verifyNav();
        System.out.println("--- Passed verifyNav");
        verifyWatches();
        System.out.println("--- Passed verifyWatches");
        verifyPointWrite();
        System.out.println("--- Passed verifyPointWrite");
        verifyInvokeAction();
        System.out.println("--- Passed verifyInvokeAction");
        verifyHisRead();
        System.out.println("--- Passed verifyHisRead");
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
        verifyEq(r.getStr("productVersion"), "3.8.111"); 
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

//        if (debug)
//          printBasicGrid(grid);

        verifyEq(grid.numRows(), 17);
        verifyEq(grid.row( 0).id(), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 1).id(), HRef.make("S.Winterfell.Equip1.BooleanWritable"));
        verifyEq(grid.row( 2).id(), HRef.make("S.Winterfell.Equip1.EnumWritable"));
        verifyEq(grid.row( 3).id(), HRef.make("S.Winterfell.Equip1.StringWritable"));
        verifyEq(grid.row( 4).id(), HRef.make("S.Winterfell.Equip1.SineWave1"));
        verifyEq(grid.row( 5).id(), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row( 6).id(), HRef.make("S.Winterfell.Equip1.SineWave2"));
        verifyEq(grid.row( 7).id(), HRef.make("S.Winterfell.Equip2.SineWave2"));
        verifyEq(grid.row( 8).id(), HRef.make("S.Winterfell.Equip2.SineWave3"));
        verifyEq(grid.row( 9).id(), HRef.make("S.Winterfell.Equip2"));
        verifyEq(grid.row(10).id(), HRef.make("S.Winterfell.Equip1.SineWave4"));
        verifyEq(grid.row(11).id(), HRef.make("C.SineWave5"));
        verifyEq(grid.row(12).id(), HRef.make("S.Richmond"));
        verifyEq(grid.row(13).id(), HRef.make("S.Richmond.AHU2"));
        verifyEq(grid.row(14).id(), HRef.make("S.Richmond.AHU2.NumericWritable"));        
        verifyEq(grid.row(15).id(), HRef.make("H.nhaystack_simple.AuditHistory"));
        verifyEq(grid.row(16).id(), HRef.make("H.nhaystack_simple.LogHistory"));

        verifyEq(grid.row( 1).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 2).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 3).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 4).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 5).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 6).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 7).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 8).get("siteRef"), HRef.make("S.Winterfell"));
        verifyEq(grid.row( 9).get("siteRef"), HRef.make("S.Winterfell"));

        verifyEq(grid.row( 1).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row( 2).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row( 3).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row( 4).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row( 6).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
        verifyEq(grid.row( 7).get("equipRef"), HRef.make("S.Winterfell.Equip2"));
        verifyEq(grid.row(10).get("equipRef"), HRef.make("S.Winterfell.Equip1"));

        //////////////////////////////////////////

        HDict dict = client.readById(HRef.make("C.SineWave5"));
        verifyEq(dict.get("axType"), HStr.make("kitControl:SineWave"));
        verify(dict.has("foo"));
        verify(dict.has("bar"));
        verifyEq(dict.get("kind"), HStr.make("Number"));
        verify(dict.has("his"));
        verifyEq(dict.get("hisInterpolate"), HStr.make("cov"));
        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/SineWave5"));
        verifyEq(dict.get("unit"), HStr.make("°F"));
        verify(dict.has("point"));
        verifyEq(dict.get("tz"), localTz());
        verify(dict.has("cur"));
        double curVal = dict.getDouble("curVal");
        verifyEq(dict.get("curStatus"), HStr.make("ok"));
        verify(curVal >= 0.0 && curVal <= 100.0);

        verifyEq(dict.get("dis"), HStr.make("Config_SineWave5"));
        verifyEq(dict.get("navName"), HStr.make("Config_SineWave5"));
        verifyEq(dict.get("navNameFormat"), HStr.make("%parent.displayName%_%displayName%"));

        
// TODO this structure doesn't exist in the test station, need to look and 
//      see if it's really necessary
        
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
        //////////////////////////////////////////

        dict = client.readById(HRef.make("S.Richmond.AHU2.NumericWritable"));

        //////////////////////////////////////////

        dict = client.readById(HRef.make("H.nhaystack_simple.AuditHistory"));
        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
        verify(!dict.missing("kind"));
        verify(dict.has("his"));
        verify(dict.missing("cur"));
        verify(dict.missing("curStatus"));
        verify(dict.missing("curVal"));
        verifyEq(dict.get("tz"), localTz());
        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/AuditHistory"));
        verify(dict.missing("hisInterpolate"));
        verify(dict.missing("unit"));

        dict = client.readById(HRef.make("H.nhaystack_simple.LogHistory"));
        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
        verify(!dict.missing("kind"));
        verify(dict.has("his"));
        verify(dict.missing("cur"));
        verify(dict.missing("curStatus"));
        verify(dict.missing("curVal"));
        verifyEq(dict.get("tz"), localTz());
        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/LogHistory"));
        verify(dict.missing("hisInterpolate"));
        verify(dict.missing("unit"));

//        dict = client.readById(HRef.make("H.nhaystack_simple.SineWave5"));
//        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        verifyEq(dict.get("kind"), HStr.make("Number"));
//        verify(dict.has("his"));
//        verify(dict.missing("cur"));
//        verify(dict.missing("curStatus"));
//        verify(dict.missing("curVal"));
//        verifyEq(dict.get("tz"), localTz());
//        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/SineWave5"));
//        verify(dict.missing("hisInterpolate"));
//        verifyEq(dict.get("unit"), HStr.make("psi"));

        try { client.readById(HRef.make("c.Mg~~")); } catch(Exception e) { verifyException(e); }
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

        grid = client.call("nav", HGrid.EMPTY);
        verifyEq(grid.numRows(), 3);
        verifyEq(grid.row(0).get("navId"), HStr.make("slot:/"));
        verifyEq(grid.row(0).get("dis"),   HStr.make("ComponentSpace"));
        verifyEq(grid.row(1).get("navId"), HStr.make("his:/"));
        verifyEq(grid.row(1).get("dis"),   HStr.make("HistorySpace"));
        verifyEq(grid.row(2).get("navId"), HStr.make("sep:/"));
        verifyEq(grid.row(2).get("dis"),   HStr.make("Site"));

        n = makeNavGrid(HStr.make("his:/"));
        grid = client.call("nav", n);
        verifyEq(grid.numRows(), 1);
        verifyEq(grid.row(0).get("navId"), HStr.make("his:/nhaystack_simple"));

        n = makeNavGrid(HStr.make("his:/nhaystack_simple"));
        grid = client.call("nav", n);
        verifyEq(grid.numRows(), 2);

        n = makeNavGrid(HStr.make("slot:/"));
        grid = client.call("nav", n);
//        printBasicGrid(grid);
        verifyEq(grid.numRows(), 10);
        verifyEq(grid.row(0).get("navId"), HStr.make("slot:/Services"));
        verifyEq(grid.row(1).get("navId"), HStr.make("slot:/Drivers"));
        verify(grid.row(2).missing("navId"));
        verify(grid.row(3).missing("navId"));
        verifyEq(grid.row(4).get("navId"), HStr.make("slot:/Equip1"));
        verifyEq(grid.row(5).get("navId"), HStr.make("slot:/Equip2"));
        verifyEq(grid.row(6).get("navId"), HStr.make("slot:/SineWave4"));
        verifyEq(grid.row(7).get("navId"), HStr.make("slot:/SineWave5"));
        verify(grid.row(8).missing("navId"));
        verifyEq(grid.row(9).get("navId"), HStr.make("slot:/AHU2"));

//        traverseComponents((HStr) grid.row(0).get("navId"));
//        traverseComponents((HStr) grid.row(1).get("navId"));
//        traverseComponents((HStr) grid.row(2).get("navId"));

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
        grid = client.call("nav", makeNavGrid(HStr.make("sep:/")));
        verifyEq(grid.numRows(), 2);
        verifyEq(grid.row(1).get("navId"), HStr.make("sep:/Richmond"));
        verifyEq(grid.row(1).get("dis"), HStr.make("Richmond"));

        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond")));
        verifyEq(grid.numRows(), 1);
        verifyEq(grid.row(0).get("navId"), HStr.make("sep:/Richmond/AHU2"));
        verifyEq(grid.row(0).get("dis"), HStr.make("Richmond AHU2"));

        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU2")));
        verifyEq(grid.numRows(), 1);
        verify(grid.row(0).missing("navId"));
        
        verifyEq(grid.row(0).get("dis"), HStr.make("Richmond AHU2 NumericWritable"));
// TODO add some more tests in here
        
        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU2")));
        verifyEq(grid.numRows(), 1);
        verify(grid.row(0).missing("navId"));
        verifyEq(grid.row(0).get("dis"), HStr.make("Richmond AHU2 NumericWritable"));

// TODO add in another AHU for testing 
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU3")));
//        verifyEq(grid.numRows(), 2);
//        verify(grid.row(0).missing("navId"));
//        verify(grid.row(1).missing("navId"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("Richmond AHU3 NumericWritable"));
//        verifyEq(grid.row(1).get("dis"), HStr.make("Richmond AHU3 NumericWritable1"));
    }

    private void traverseComponents(HStr navId)
    {
        HGrid grid = client.call("nav", makeNavGrid(navId));

        for (int i = 0; i < grid.numRows(); i++)
        {
            if (grid.row(i).has("navId"))
                traverseComponents((HStr) grid.row(i).get("navId"));
        }
    }

////////////////////////////////////////////////////////////////////////////
//// His Reads
////////////////////////////////////////////////////////////////////////////

    void verifyHisRead() throws Exception
    {
        HGrid grid = client.readAll("his");
        verifyEq(grid.numRows(), 5);

        ///////////////////////////////////////////////

        HDict dict = client.read("axSlotPath==\"slot:/Equip1/SineWave1\"");
        HGrid his = client.hisRead(dict.id(), "today");

        verifyEq(his.meta().id(), dict.id());
        verify(his.numRows() > 0);

        int last = his.numRows()-1;
        verifyEq(ts(his.row(last)).date, HDate.today());

        verifyEq(numVal(his.row(0)).unit, "°F");

        ///////////////////////////////////////////////

        dict = client.read("axHistoryId==\"/nhaystack_simple/LogHistory\"");
        his = client.hisRead(dict.id(), "today");
        verifyEq(his.meta().id(), dict.id());
        verify(his.numRows() > 0);

        last = his.numRows()-1;
        verifyEq(ts(his.row(last)).date, HDate.today());

        ///////////////////////////////////////////////

//        dict = client.read("axHistoryId==\"/nhaystack_simple/SineWave5\"");
//        his = client.hisRead(dict.id(), "today");
//        verifyEq(his.meta().id(), dict.id());

//        ///////////////////////////////////////////////

        client.hisRead(HRef.make("C.AHU2.NumericWritable"), "today");
//        client.hisRead(HRef.make("S.Richmond.AHU2.NumericWritable"), "today");
    }

////////////////////////////////////////////////////////////////////////////
//// Watches
////////////////////////////////////////////////////////////////////////////

    void verifyWatches() throws Exception
    {
        // create new watch
        HWatch w = client.watchOpen("NHaystack Simple Test", HNum.make(120, "s"));
        verifyEq(w.id(), null);
        verifyEq(w.dis(), "NHaystack Simple Test");

        // do query to get some recs
        HGrid recs = client.readAll("point");
        verify(recs.numRows() >= 4);
        HDict a = recs.row(0);
        HDict b = recs.row(1);
        HDict c = recs.row(2);
        HDict d = recs.row(3);

//System.out.println(a);
//System.out.println(b);
//System.out.println(c);
//System.out.println(d);

        // do first sub
        HGrid sub = w.sub(new HRef[] { a.id(), b.id() });
        verifyEq(sub.numRows(), 2);
        verifyEq(sub.row(0).id(), a.id());
        verifyEq(sub.row(1).id(), b.id());

        // now add c, d
        sub = w.sub(new HRef[] { c.id(), d.id() }, false);
        verifyEq(sub.numRows(), 2);
        verifyEq(sub.row(0).id(), c.id());
        verifyEq(sub.row(1).id(), d.id());

        // verify state of watch now
        verify(client.watch(w.id()) == w);
        verifyEq(client.watches().length, 1);
        verify(client.watches()[0] == w);
        verifyEq(w.lease().millis(), 2L * 60 * 1000);

        // poll refresh
        HGrid poll = w.pollRefresh();
        verifyEq(poll.numRows(), 4);
        verifyGridContains(poll, "id", a.id());
        verifyGridContains(poll, "id", b.id());
        verifyGridContains(poll, "id", c.id());
        verifyGridContains(poll, "id", d.id());

        // poll changes
        Thread.sleep(3000); // wait for the sine waves to tick over
        poll = w.pollChanges();
        verifyEq(poll.numRows(), 1);

        // remove d, and then poll refresh
        w.unsub(new HRef[] { d.id() });
        poll = w.pollRefresh();
        verifyEq(poll.numRows(), 3);

        // close
        w.close();
        try { w.pollRefresh(); fail(); } catch (Exception e) { verifyException(e); }
        verifyEq(client.watch(w.id(), false), null);
        verifyEq(client.watches().length, 0);

        // check bad id 
        w = client.watchOpen("Bogus Test", HNum.make(120, "s"));
        HRef badId = HRef.make("c." + Base64.URI.encode("badBadBad"));
        try { w.sub(new HRef[] { badId }).dump(); fail(); } catch (Exception e) { verifyException(e); }
    }

////////////////////////////////////////////////////////////////////////////
//// Point Write
////////////////////////////////////////////////////////////////////////////

    void verifyPointWrite() throws Exception
    {
        doVerifyPointWrite(HRef.make("C.AHU2.NumericWritable"));
//        doVerifyPointWrite(HRef.make("S.Richmond.AHU2.NumericWritable"));
    }
    
    private void doVerifyPointWrite(HRef id)
    {
        HGrid grid = client.pointWrite(id, 10, "admin", HNum.make(222), null);
//        printFullGrid(grid);
        verifyEq(grid.numRows(), 17);
        for (int i = 0; i < 17; i++)
        {
            verifyEq(grid.row(i).getInt("level"), i+1);
            switch(i+1)
            {
                case 10:
                    verifyEq(grid.row(i).get("val"), HNum.make(222));
                    // TODO check to see if this is a bug in the Niagara side
//                    verifyEq(grid.row(i).get("who"), HStr.make("admin"));
                    break;
                case 17:
                    verifyEq(grid.row(i).get("val"), HNum.make(111));
                    verify(grid.row(i).missing("who"));
                    break;
                default:
                    verify(grid.row(i).missing("val"));
                    verify(grid.row(i).missing("who"));
                    break;
            }
        }

        grid = client.pointWrite(id, 10, "admin", null, null);
        verifyEq(grid.numRows(), 17);
        for (int i = 0; i < 17; i++)
        {
            verifyEq(grid.row(i).getInt("level"), i+1);
            switch(i+1)
            {
                case 10:
                    verify(grid.row(i).missing("val"));
//                    verifyEq(grid.row(i).get("who"), HStr.make("admin"));
                    break;
                case 17:
                    verifyEq(grid.row(i).get("val"), HNum.make(111));
                    verify(grid.row(i).missing("who"));
                    break;
                default:
                    verify(grid.row(i).missing("val"));
                    verify(grid.row(i).missing("who"));
                    break;
            }
        }

        // just make sure this works with no level, etc
        grid = client.pointWriteArray(id);
    }

////////////////////////////////////////////////////////////////////////////
//// Invoke Action
////////////////////////////////////////////////////////////////////////////
//
    void verifyInvokeAction() throws Exception
    {
        doVerifyInvokeAction(HRef.make("C.AHU2.NumericWritable"));
//        doVerifyInvokeAction(HRef.make("S.Richmond.AHU2.NumericWritable"));
    }
//    
    private void doVerifyInvokeAction(HRef id)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add("arg", HNum.make(333));
        client.invokeAction(id, "emergencyOverride", hd.toDict());

        HGrid grid = client.pointWriteArray(id);
        verifyEq(grid.numRows(), 17);
        for (int i = 0; i < 17; i++)
        {
            verifyEq(grid.row(i).getInt("level"), i+1);
            switch(i+1)
            {
                case 1:
                    verifyEq(grid.row(i).get("val"), HNum.make(333));
//                    verify(grid.row(i).missing("who"));
                    break;
                case 10:
                    verify(grid.row(i).missing("val"));
//                    verifyEq(grid.row(i).get("who"), HStr.make("admin"));
                    break;
                case 17:
                    verifyEq(grid.row(i).get("val"), HNum.make(111));
//                    verify(grid.row(i).missing("who"));
                    break;
                default:
                    verify(grid.row(i).missing("val"));
                    verify(grid.row(i).missing("who"));
                    break;
            }
        }

        client.invokeAction(id, "emergencyAuto", HDict.EMPTY);

        grid = client.pointWriteArray(id);
        verifyEq(grid.numRows(), 17);
        for (int i = 0; i < 17; i++)
        {
            verifyEq(grid.row(i).getInt("level"), i+1);
            switch(i+1)
            {
                case 10:
                    verify(grid.row(i).missing("val"));
//                    verifyEq(grid.row(i).get("who"), HStr.make("admin"));
                    break;
                case 17:
                    verifyEq(grid.row(i).get("val"), HNum.make(111));
                    verify(grid.row(i).missing("who"));
                    break;
                default:
                    verify(grid.row(i).missing("val"));
                    verify(grid.row(i).missing("who"));
                    break;
            }
        }
    }

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
