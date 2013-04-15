//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.test;

import haystack.*;
import haystack.io.*;
import haystack.client.*;
import haystack.test.*;
import haystack.util.*;
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
        verifyWatches();
        verifyHisRead();
        verifyNav();
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
        this.client = HClient.open(URI, "admin", "abc123");
    }

//////////////////////////////////////////////////////////////////////////
// About
//////////////////////////////////////////////////////////////////////////

    void verifyAbout() throws Exception
    {
        HDict r = client.about();
        verifyEq(r.getStr("haystackVersion"), "2.0");
        verifyEq(r.getStr("productName"), "Niagara AX");
//        verifyEq(r.getStr("productVersion"), "3.6.47"); TODO
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

//grid.dump();

//ver:"2.0"
//axType,foo,kind,id,his,curStatus,dis,hisInterpolate,axSlotPath,unit,point,tz,cur,curVal,bar,axHistoryId
//"kitControl:SineWave",M,"Number",  @c.c2xvdDovRm9vL1NpbmVXYXZlMQ~~,M,"ok","SineWave1","cov","slot:/Foo/SineWave1","¦F",M,"New_York",M,60.5389¦F,M,
//"kitControl:SineWave",,"Number",   @c.c2xvdDovRm9vL1NpbmVXYXZlMg~~,,"ok","SineWave2",,"slot:/Foo/SineWave2","psi",M,,M,60.5389psi,,
//"history:HistoryConfig",,,         @h.L25oYXlzdGFja19zaW1wbGUvQXVkaXRIaXN0b3J5,M,,,,,,M,"New_York",,,,"/nhaystack_simple/AuditHistory"
//"history:HistoryConfig",,,         @h.L25oYXlzdGFja19zaW1wbGUvTG9nSGlzdG9yeQ~~,M,,,,,,M,"New_York",,,,"/nhaystack_simple/LogHistory"
//"history:HistoryConfig",,"Number", @h.L25oYXlzdGFja19zaW1wbGUvU2luZVdhdmUz,M,,,,,"psi",M,"New_York",,,,"/nhaystack_simple/SineWave3"

        verifyEq(grid.numRows(), 5);
        verifyEq(grid.row(0).get("id"), HRef.make("c.c2xvdDovRm9vL1NpbmVXYXZlMQ~~"));
        verifyEq(grid.row(1).get("id"), HRef.make("c.c2xvdDovRm9vL1NpbmVXYXZlMg~~"));
        verifyEq(grid.row(2).get("id"), HRef.make("h.L25oYXlzdGFja19zaW1wbGUvQXVkaXRIaXN0b3J5"));
        verifyEq(grid.row(3).get("id"), HRef.make("h.L25oYXlzdGFja19zaW1wbGUvTG9nSGlzdG9yeQ~~"));
        verifyEq(grid.row(4).get("id"), HRef.make("h.L25oYXlzdGFja19zaW1wbGUvU2luZVdhdmUz"));

        verifyEq(grid.row(0).getRef("id").dis, "Foo_SineWave1");
        verifyEq(grid.row(1).getRef("id").dis, "SineWave2");
        verifyEq(grid.row(2).getRef("id").dis, "nhaystack_simple_AuditHistory");
        verifyEq(grid.row(3).getRef("id").dis, "nhaystack_simple_LogHistory");
        verifyEq(grid.row(4).getRef("id").dis, "nhaystack_simple_SineWave3");

        //////////////////////////////////////////

        HDict dict = client.readById(HRef.make("c.c2xvdDovRm9vL1NpbmVXYXZlMQ~~"));
        verifyEq(dict.get("axType"), HStr.make("kitControl:SineWave"));
        verify(dict.has("foo"));
        verify(dict.has("bar"));
        verifyEq(dict.get("kind"), HStr.make("Number"));
        verify(dict.has("his"));
        verifyEq(dict.get("hisInterpolate"), HStr.make("cov"));
        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/Foo/SineWave1"));
        verifyEq(dict.get("unit"), HStr.make("°F"));
        verify(dict.has("point"));
        verifyEq(dict.get("tz"), localTz());
        verify(dict.has("cur"));
        double curVal = dict.getDouble("curVal");
        verifyEq(dict.get("curStatus"), HStr.make("ok"));
        verify(curVal >= 0.0 && curVal <= 100.0);

        verifyEq(dict.get("dis"), HStr.make("Foo_SineWave1"));
        verifyEq(dict.get("navName"), HStr.make("Foo_SineWave1"));
        verifyEq(dict.get("navNameFormat"), HStr.make("%parent.displayName%_%displayName%"));

        //////////////////////////////////////////

        dict = client.readById(HRef.make("c.c2xvdDovRm9vL1NpbmVXYXZlMg~~"));
        verifyEq(dict.get("axType"), HStr.make("kitControl:SineWave"));
        verify(dict.missing("foo"));
        verify(dict.missing("bar"));
        verifyEq(dict.get("kind"), HStr.make("Number"));
        verify(dict.missing("his"));
        verifyEq(dict.get("curStatus"), HStr.make("ok"));
        verify(dict.missing("hisInterpolate"));
        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/Foo/SineWave2"));
        verifyEq(dict.get("unit"), HStr.make("psi"));
        verify(dict.has("point"));
        verify(dict.missing("tz"));
        verify(dict.has("cur"));
        curVal = dict.getDouble("curVal");
        verify(curVal >= 0.0 && curVal <= 100.0);

        verifyEq(dict.get("dis"), HStr.make("SineWave2"));
        verifyEq(dict.get("navName"), HStr.make("SineWave2"));
        verify(dict.missing("navNameFormat"));

        //////////////////////////////////////////

        dict = client.readById(HRef.make("h.L25oYXlzdGFja19zaW1wbGUvQXVkaXRIaXN0b3J5"));
        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
        verify(dict.missing("kind"));
        verify(dict.has("his"));
        verify(dict.missing("cur"));
        verify(dict.missing("curStatus"));
        verify(dict.missing("curVal"));
        verifyEq(dict.get("tz"), localTz());
        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/AuditHistory"));
        verify(dict.missing("hisInterpolate"));
        verify(dict.missing("unit"));

        dict = client.readById(HRef.make("h.L25oYXlzdGFja19zaW1wbGUvTG9nSGlzdG9yeQ~~"));
        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
        verify(dict.missing("kind"));
        verify(dict.has("his"));
        verify(dict.missing("cur"));
        verify(dict.missing("curStatus"));
        verify(dict.missing("curVal"));
        verifyEq(dict.get("tz"), localTz());
        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/LogHistory"));
        verify(dict.missing("hisInterpolate"));
        verify(dict.missing("unit"));

        dict = client.readById(HRef.make("h.L25oYXlzdGFja19zaW1wbGUvU2luZVdhdmUz"));
        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
        verifyEq(dict.get("kind"), HStr.make("Number"));
        verify(dict.has("his"));
        verify(dict.missing("cur"));
        verify(dict.missing("curStatus"));
        verify(dict.missing("curVal"));
        verifyEq(dict.get("tz"), localTz());
        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/SineWave3"));
        verify(dict.missing("hisInterpolate"));
        verifyEq(dict.get("unit"), HStr.make("psi"));

        try { client.readById(HRef.make("c.Mg~~")); } catch(Exception e) { verifyException(e); }
    }

//////////////////////////////////////////////////////////////////////////
// Nav
//////////////////////////////////////////////////////////////////////////

    void verifyNav() throws Exception
    {
        HGrid grid = client.call("nav", HGrid.EMPTY);
        verifyEq(grid.numRows(), 3);
        verifyEq(grid.row(0).get("navId"), HStr.make("/comp"));
        verifyEq(grid.row(0).get("dis"),   HStr.make("ComponentSpace"));
        verifyEq(grid.row(1).get("navId"), HStr.make("/his"));
        verifyEq(grid.row(1).get("dis"),   HStr.make("HistorySpace"));
        verifyEq(grid.row(2).get("navId"), HStr.make("/site"));
        verifyEq(grid.row(2).get("dis"),   HStr.make("Site"));

        HGrid n = makeNavGrid(HStr.make("/his"));
        grid = client.call("nav", n);
        verifyEq(grid.numRows(), 1);
        verifyEq(grid.row(0).get("navId"), HStr.make("/his/nhaystack_simple"));

        n = makeNavGrid(HStr.make("/his/nhaystack_simple"));
        grid = client.call("nav", n);
        verifyEq(grid.numRows(), 3);

        n = makeNavGrid(HStr.make("/comp"));
        grid = client.call("nav", n);
        verifyEq(grid.numRows(), 3);
        verifyEq(grid.row(0).get("navId"), HStr.make("/comp/Services"));
        verifyEq(grid.row(1).get("navId"), HStr.make("/comp/Drivers"));
        verifyEq(grid.row(2).get("navId"), HStr.make("/comp/Foo"));

        traverseComponents((HStr) grid.row(0).get("navId"));
        traverseComponents((HStr) grid.row(1).get("navId"));
        traverseComponents((HStr) grid.row(2).get("navId"));
    }

    private void traverseComponents(HStr navId)
    {
        HGrid grid = client.call("nav", makeNavGrid(navId));
//grid.dump();

        for (int i = 0; i < grid.numRows(); i++)
        {
            if (grid.row(i).has("navId"))
                traverseComponents((HStr) grid.row(i).get("navId"));
        }
    }

//////////////////////////////////////////////////////////////////////////
// His Reads
//////////////////////////////////////////////////////////////////////////

    void verifyHisRead() throws Exception
    {
        HGrid grid = client.readAll("his");
//grid.dump();
        verifyEq(grid.numRows(), 4);

        ///////////////////////////////////////////////

        HDict dict = client.read("axSlotPath==\"slot:/Foo/SineWave1\"");
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

        dict = client.read("axHistoryId==\"/nhaystack_simple/SineWave3\"");
        his = client.hisRead(dict.id(), "today");

        verifyEq(his.meta().id(), dict.id());
//        verify(his.numRows() > 0);
//
//        last = his.numRows()-1;
//        verifyEq(ts(his.row(last)).date, HDate.today());
//
//        verifyEq(numVal(his.row(0)).unit, "psi");
    }

//////////////////////////////////////////////////////////////////////////
// Watches
//////////////////////////////////////////////////////////////////////////

    void verifyWatches() throws Exception
    {
        // create new watch
        HWatch w = client.watchOpen("NHaystack Simple Test");
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
        HGrid sub = w.sub(new HIdentifier[] { a.id(), b.id() });
        verifyEq(sub.numRows(), 2);
        verifyEq(sub.row(0).dis(), a.dis());
        verifyEq(sub.row(1).dis(), b.dis());

        // now add c, d
        sub = w.sub(new HIdentifier[] { c.id(), d.id() }, false);
        verifyEq(sub.numRows(), 2);
        verifyEq(sub.row(0).dis(), c.dis());
        verifyEq(sub.row(1).dis(), d.dis());

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
        Thread.sleep(2000); // wait for the sine waves to tick over
        poll = w.pollChanges();
        verifyEq(poll.numRows(), 2);

        // remove d, and then poll refresh
        w.unsub(new HIdentifier[] { d.id() });
        poll = w.pollRefresh();
        verifyEq(poll.numRows(), 3);

        // close
        w.close();
        try { w.pollRefresh(); fail(); } catch (Exception e) { verifyException(e); }
        verifyEq(client.watch(w.id(), false), null);
        verifyEq(client.watches().length, 0);

        // check bad id 
        w = client.watchOpen("Bogus Test");
        HRef badId = HRef.make("c." + Base64.URI.encode("badBadBad"));
        try { w.sub(new HIdentifier[] { badId }).dump(); fail(); } catch (Exception e) { verifyException(e); }
    }

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        runTests(new String[] { "nhaystack.test.NSimpleClientTest", }, null);
    }
}
