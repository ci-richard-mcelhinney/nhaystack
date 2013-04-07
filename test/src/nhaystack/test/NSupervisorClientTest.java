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

/**
 * NSupervisorClientTest -- this test uses nhaystack_sup
 */
public class NSupervisorClientTest extends NTest
{
    final String URI = "http://localhost:81/haystack/";
    HClient client;

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

    public void test() throws Exception
    {
        verifyAuth();
        verifyRead();
        verifyHisRead();
        verifyNav();
        verifyWatches();
        verifyNavUri();
    }

    void verifyAuth() throws Exception
    {
        this.client = HClient.open(URI, "admin", "abc123");
    }

//////////////////////////////////////////////////////////////////////////
// Reads
//////////////////////////////////////////////////////////////////////////

    void verifyRead() throws Exception
    {
        HGrid grid = client.readAll("point");

//axType,kind,id,curStatus,dis,axSlotPath,unit,point,cur,curVal,his,axHistoryId,tz
//"history:HistoryConfig",,@h.L25oYXlzdGFja19qYWNlMS9BdWRpdEhpc3Rvcnk~,,,,,M,,,M,"/nhaystack_jace1/AuditHistory","New_York"
//"history:HistoryConfig",,@h.L25oYXlzdGFja19qYWNlMS9Mb2dIaXN0b3J5,,,,,M,,,M,"/nhaystack_jace1/LogHistory","New_York"
//"history:HistoryConfig","Number",@h.L25oYXlzdGFja19qYWNlMi9TaW5lV2F2ZTI~,,,,"psi",M,,,M,"/nhaystack_jace2/SineWave2","New_York"
//"history:HistoryConfig",,@h.L25oYXlzdGFja19zdXAvQXVkaXRIaXN0b3J5,,,,,M,,,M,"/nhaystack_sup/AuditHistory","New_York"
//"history:HistoryConfig",,@h.L25oYXlzdGFja19zdXAvTG9nSGlzdG9yeQ~~,,,,,M,,,M,"/nhaystack_sup/LogHistory","New_York"

        verifyEq(grid.numRows(), 8);
        verifyEq(grid.row(0).get("id"), HRef.make("c.c2xvdDovRHJpdmVycy9OaWFnYXJhTmV0d29yay9uaGF5c3RhY2tfamFjZTEvcG9pbnRzL1NpbmVXYXZlMQ~~"));
        verifyEq(grid.row(1).get("id"), HRef.make("c.c2xvdDovRHJpdmVycy9OaWFnYXJhTmV0d29yay9uaGF5c3RhY2tfamFjZTEvcG9pbnRzL1NpbmVXYXZlMg~~"));
        verifyEq(grid.row(2).get("id"), HRef.make("c.c2xvdDovRHJpdmVycy9OaWFnYXJhTmV0d29yay9uaGF5c3RhY2tfamFjZTIvcG9pbnRzL1NpbmVXYXZlMQ~~"));
        verifyEq(grid.row(3).get("id"), HRef.make("h.L25oYXlzdGFja19qYWNlMS9BdWRpdEhpc3Rvcnk~"));
        verifyEq(grid.row(4).get("id"), HRef.make("h.L25oYXlzdGFja19qYWNlMS9Mb2dIaXN0b3J5"));
        verifyEq(grid.row(5).get("id"), HRef.make("h.L25oYXlzdGFja19qYWNlMi9TaW5lV2F2ZTI~"));
        verifyEq(grid.row(6).get("id"), HRef.make("h.L25oYXlzdGFja19zdXAvQXVkaXRIaXN0b3J5"));
        verifyEq(grid.row(7).get("id"), HRef.make("h.L25oYXlzdGFja19zdXAvTG9nSGlzdG9yeQ~~"));

        grid = client.readAll("id");
        verify(grid.numRows() == 12);
        verifyEq(grid.row( 0).getRef("id").dis, "nhaystack_jace1");
        verifyEq(grid.row( 1).getRef("id").dis, "Blacksburg nhaystack_jace1 SineWave1");
        verifyEq(grid.row( 2).getRef("id").dis, "Blacksburg nhaystack_jace1 SineWave2");
        verifyEq(grid.row( 3).getRef("id").dis, "Blacksburg Transmogrifier SineWave1");
        verifyEq(grid.row( 4).getRef("id").dis, "Blacksburg");
        verifyEq(grid.row( 5).getRef("id").dis, "Blacksburg Transmogrifier");
        verifyEq(grid.row( 6).getRef("id").dis, "Blacksburg FluxCapacitor");
        verifyEq(grid.row( 7).getRef("id").dis, "nhaystack_jace1_AuditHistory");
        verifyEq(grid.row( 8).getRef("id").dis, "nhaystack_jace1_LogHistory");
        verifyEq(grid.row( 9).getRef("id").dis, "nhaystack_jace2_SineWave2");
        verifyEq(grid.row(10).getRef("id").dis, "nhaystack_sup_AuditHistory");
        verifyEq(grid.row(11).getRef("id").dis, "nhaystack_sup_LogHistory");

//axType,kind,id,curStatus,dis,axSlotPath,unit,point,cur,curVal,his,axHistoryId,tz
//
//"control:NumericPoint","Number",@c.c2xvdDovRHJpdmVycy9OaWFnYXJhTmV0d29yay9uaGF5c3RhY2tfamFjZTEvcG9pbnRzL1NpbmVXYXZlMQ~~,
//"ok","SineWave1","slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave1","¦F",M,M,26.0041¦F,,,
//
//"control:NumericPoint","Number",@c.c2xvdDovRHJpdmVycy9OaWFnYXJhTmV0d29yay9uaGF5c3RhY2tfamFjZTEvcG9pbnRzL1NpbmVXYXZlMg~~,
//"ok","SineWave2","slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave2","psi",M,M,26.1513psi,,,
//
//"control:NumericPoint","Number",@c.c2xvdDovRHJpdmVycy9OaWFnYXJhTmV0d29yay9uaGF5c3RhY2tfamFjZTIvcG9pbnRzL1NpbmVXYXZlMQ~~,
//"ok","SineWave1","slot:/Drivers/NiagaraNetwork/nhaystack_jace2/points/SineWave1","¦F",M,M,0.8856374635655655¦F,,,

        HDict dict = client.readById(HRef.make("c.c2xvdDovRHJpdmVycy9OaWFnYXJhTmV0d29yay9uaGF5c3RhY2tfamFjZTEvcG9pbnRzL1NpbmVXYXZlMQ~~"));
        verifyEq(dict.get("axType"), HStr.make("control:NumericPoint"));
        verifyEq(dict.get("kind"), HStr.make("Number"));
        verify(dict.has("his"));
        verify(dict.has("curStatus"));
        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave1"));
        verifyEq(dict.get("unit"), HStr.make("°F"));
        verify(dict.has("point"));
        verifyEq(dict.get("tz"), localTz());
//        verify(dict.getDouble("curVal") == 0.0);
//        verifyEq(dict.get("hisInterpolate"), HStr.make("cov")); TODO

        dict = client.readById(HRef.make("c.c2xvdDovRHJpdmVycy9OaWFnYXJhTmV0d29yay9uaGF5c3RhY2tfamFjZTEvcG9pbnRzL1NpbmVXYXZlMg~~"));
        verifyEq(dict.get("axType"), HStr.make("control:NumericPoint"));
        verifyEq(dict.get("kind"), HStr.make("Number"));
        verify(dict.has("his"));
        verify(dict.has("curStatus"));
        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave2"));
        verifyEq(dict.get("unit"), HStr.make("psi"));
        verify(dict.has("point"));
        verifyEq(dict.get("tz"), localTz());
//        verify(dict.getDouble("curVal") == 0.0);
//        verifyEq(dict.get("hisInterpolate"), HStr.make("cov")); TODO

        dict = client.readById(HRef.make("c.c2xvdDovRHJpdmVycy9OaWFnYXJhTmV0d29yay9uaGF5c3RhY2tfamFjZTIvcG9pbnRzL1NpbmVXYXZlMQ~~"));
        verifyEq(dict.get("axType"), HStr.make("control:NumericPoint"));
        verifyEq(dict.get("kind"), HStr.make("Number"));
        verify(dict.missing("his"));
        verify(dict.has("curStatus"));
        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_jace2/points/SineWave1"));
        verifyEq(dict.get("unit"), HStr.make("°F"));
        verify(dict.has("point"));
        verify(dict.missing("tz"));
//        verify(dict.getDouble("curVal") == 0.0);
        verify(dict.missing("axHistoryRef"));
        verify(dict.missing("hisInterpolate"));

        ////////////////////////////////////////////////////////////////

        dict = client.readById(HRef.make("h.L25oYXlzdGFja19qYWNlMi9TaW5lV2F2ZTI~"));
        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
        verifyEq(dict.get("kind"), HStr.make("Number"));
        verify(dict.has("point"));
        verify(dict.has("his"));
        verify(dict.missing("curStatus"));
        verify(dict.missing("curVal"));
        verifyEq(dict.get("tz"), localTz());
        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_jace2/SineWave2"));
//        verifyEq(dict.get("hisInterpolate"), HStr.make("cov")); TODO
        verifyEq(dict.get("unit"), HStr.make("psi"));

        dict = client.readById(HRef.make("h.L25oYXlzdGFja19zdXAvQXVkaXRIaXN0b3J5"));
        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
        verify(dict.missing("kind"));
        verify(dict.has("his"));
        verify(dict.missing("curStatus"));
        verify(dict.missing("curVal"));
        verifyEq(dict.get("tz"), localTz());
        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_sup/AuditHistory"));
        verify(dict.missing("hisInterpolate"));
        verify(dict.missing("unit"));

        dict = client.readById(HRef.make("h.L25oYXlzdGFja19zdXAvTG9nSGlzdG9yeQ~~"));
        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        verifyEq(dict.get("kind"), HStr.make("Str")); // TODO
        verify(dict.has("point"));
        verify(dict.has("his"));
        verify(dict.missing("curStatus"));
        verify(dict.missing("curVal"));
        verifyEq(dict.get("tz"), localTz());
        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_sup/LogHistory"));
        verify(dict.missing("hisInterpolate"));
        verify(dict.missing("unit"));

        dict = client.readById(HRef.make("h.L25oYXlzdGFja19qYWNlMS9BdWRpdEhpc3Rvcnk~"));
        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        verifyEq(dict.get("kind"), HStr.make("Str")); // TODO
        verify(dict.has("point"));
        verify(dict.has("his"));
        verify(dict.missing("curStatus"));
        verify(dict.missing("curVal"));
        verifyEq(dict.get("tz"), localTz());
        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_jace1/AuditHistory"));
        verify(dict.missing("hisInterpolate"));
        verify(dict.missing("unit"));

        dict = client.readById(HRef.make("h.L25oYXlzdGFja19qYWNlMS9Mb2dIaXN0b3J5"));
        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        verifyEq(dict.get("kind"), HStr.make("Str")); // TODO
        verify(dict.has("point"));
        verify(dict.has("his"));
        verify(dict.missing("curStatus"));
        verify(dict.missing("curVal"));
        verifyEq(dict.get("tz"), localTz());
        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_jace1/LogHistory"));
        verify(dict.missing("hisInterpolate"));
        verify(dict.missing("unit"));
    }

//////////////////////////////////////////////////////////////////////////
// Nav
//////////////////////////////////////////////////////////////////////////

    void verifyNav() throws Exception
    {
//[site]
//    [site:Blacksburg] slot:/Sites/Blacksburg
//        [equip:Blacksburg.nhaystack_jace1] slot:/Drivers/NiagaraNetwork/nhaystack_jace1
//            [---] slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave1
//            [---] slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave2
//        [equip:Blacksburg.Transmogrifier] slot:/Sites/Blacksburg/Transmogrifier
//            [---] slot:/Drivers/NiagaraNetwork/nhaystack_jace2/points/SineWave1
//        [equip:Blacksburg.FluxCapacitor] slot:/Sites/Blacksburg/FluxCapacitor

        HGrid grid = client.call("nav", makeNavGrid(HStr.make("site")));
        verifyEq(grid.numRows(), 1);
        verifyEq(grid.row(0).get("navId"), HStr.make("site:Blacksburg"));
        verifyEq(grid.row(0).get("axSlotPath"), HStr.make("slot:/Sites/Blacksburg"));

        grid = client.call("nav", makeNavGrid(HStr.make("site:Blacksburg")));
        verifyEq(grid.numRows(), 3);
        verifyEq(grid.row(0).get("navId"), HStr.make("equip:Blacksburg.nhaystack_jace1"));
        verifyEq(grid.row(1).get("navId"), HStr.make("equip:Blacksburg.Transmogrifier"));
        verifyEq(grid.row(2).get("navId"), HStr.make("equip:Blacksburg.FluxCapacitor"));
        verifyEq(grid.row(0).get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_jace1"));
        verifyEq(grid.row(1).get("axSlotPath"), HStr.make("slot:/Sites/Blacksburg/Transmogrifier"));
        verifyEq(grid.row(2).get("axSlotPath"), HStr.make("slot:/Sites/Blacksburg/FluxCapacitor"));

        grid = client.call("nav", makeNavGrid(HStr.make("equip:Blacksburg.nhaystack_jace1")));
        verifyEq(grid.numRows(), 2);
        verify(grid.row(0).missing("navId"));
        verify(grid.row(1).missing("navId"));
        verifyEq(grid.row(0).get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave1"));
        verifyEq(grid.row(1).get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave2"));

        grid = client.call("nav", makeNavGrid(HStr.make("equip:Blacksburg.Transmogrifier")));
        verifyEq(grid.numRows(), 1);
        verify(grid.row(0).missing("navId"));
        verifyEq(grid.row(0).get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_jace2/points/SineWave1"));

        grid = client.call("nav", makeNavGrid(HStr.make("equip:Blacksburg.FluxCapacitor")));
        verifyEq(grid.numRows(), 0);
    }

//////////////////////////////////////////////////////////////////////////
// His Reads
//////////////////////////////////////////////////////////////////////////

    void verifyHisRead() throws Exception
    {
        HGrid grid = client.readAll("his");
        verifyEq(grid.numRows(), 7);

        ///////////////////////////////////////////////

        HDict dict = client.read("axSlotPath==\"slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave1\"");
        HGrid his = client.hisRead(dict.id(), "today");

        verifyEq(his.meta().id(), dict.id());
        verify(his.numRows() > 0);

        int last = his.numRows()-1;
        verifyEq(ts(his.row(last)).date, HDate.today());

        verifyEq(numVal(his.row(0)).unit, "°F");

        ///////////////////////////////////////////////

        try { client.read("axHistoryId==\"/nhaystack_jace1/SineWave1\""); } 
        catch(UnknownRecException e) { verifyException(e); }
    }

//////////////////////////////////////////////////////////////////////////
// Watches
//////////////////////////////////////////////////////////////////////////

    void verifyWatches() throws Exception
    {
        // create new watch
        HWatch w = client.watchOpen("NHaystack Supervisor Test");
        verifyEq(w.id(), null);
        verifyEq(w.dis(), "NHaystack Supervisor Test");

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

        HGrid sub = w.sub(new HIdentifier[] { a.id(), b.id(), c.id(), d.id() });
        verifyEq(sub.numRows(), 4);
        verifyEq(sub.row(0).dis(), a.dis());
        verifyEq(sub.row(1).dis(), b.dis());
        verifyEq(sub.row(2).dis(), c.dis());
        verifyEq(sub.row(3).dis(), d.dis());

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
        verifyEq(poll.numRows(), 3);

        // remove a, and then poll changes
        w.unsub(new HIdentifier[] { a.id() });
        poll = w.pollChanges();
        verifyEq(poll.numRows(), 2);

        // close
        w.close();
        try { w.pollRefresh(); fail(); } catch (Exception e) { verifyException(e); }
        verifyEq(client.watch(w.id(), false), null);
        verifyEq(client.watches().length, 0);

    }

//////////////////////////////////////////////////////////////////////////
// NavUri
//////////////////////////////////////////////////////////////////////////

    void verifyNavUri() throws Exception
    {
        HUri uri = HUri.make("site:/Blacksburg");
        HDict tags = client.readById(uri);
        verifyEq(tags.getStr("dis"), "Blacksburg");

        uri = HUri.make("site:/Blacksburg/Transmogrifier");
        tags = client.readById(uri);
        verifyEq(tags.getStr("dis"), "Blacksburg Transmogrifier");

        uri = HUri.make("site:/Blacksburg/Transmogrifier/SineWave1");
        tags = client.readById(uri);
        verifyEq(tags.getStr("dis"), "Blacksburg Transmogrifier SineWave1");

        //////////////////////////////////

        uri = HUri.make("site:/Blacksburg/");
        tags = client.readById(uri);
        verifyEq(tags.getStr("dis"), "Blacksburg");

        uri = HUri.make("site:/Blacksburg/Transmogrifier/");
        tags = client.readById(uri);
        verifyEq(tags.getStr("dis"), "Blacksburg Transmogrifier");

        uri = HUri.make("site:/Blacksburg/Transmogrifier/SineWave1/");
        tags = client.readById(uri);
        verifyEq(tags.getStr("dis"), "Blacksburg Transmogrifier SineWave1");

        //////////////////////////////////

        HWatch w = client.watchOpen("NHaystack NavUri Test");
        w.sub(new HIdentifier[] { uri });

        Thread.sleep(2000); // wait for the sine waves to tick over
        HGrid poll = w.pollChanges();
        verifyEq(poll.numRows(), 1);

        w.unsub(new HIdentifier[] { uri });
    }

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        runTests(new String[] { "nhaystack.test.NSupervisorClientTest", }, null);
    }
}
