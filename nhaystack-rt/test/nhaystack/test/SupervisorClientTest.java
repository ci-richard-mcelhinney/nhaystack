//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.test;

//import org.projecthaystack.test.*;

/**
 * NSupervisorClientTest -- this test uses nhaystack_sup
 */
public class SupervisorClientTest //extends NTest
{
//    final String URI = "http://localhost:81/haystack/";
//    HClient client;
//
////////////////////////////////////////////////////////////////////////////
//// Main
////////////////////////////////////////////////////////////////////////////
//
//    public void test() throws Exception
//    {
//        verifyAuth();
//        verifyRead();
//        verifyHisRead();
//        verifyWatches();
//        verifyNav();
//    }
//
//    void verifyAuth() throws Exception
//    {
//        this.client = HClient.open(URI, "admin", "abc123");
//    }
//
////////////////////////////////////////////////////////////////////////////
//// Reads
////////////////////////////////////////////////////////////////////////////
//
//    void verifyRead() throws Exception
//    {
//        HGrid grid = client.readAll("point");
//
////        for (int i = 0; i < grid.numRows(); i++)
////            System.out.println(i + ", " + grid.row(i).get("id"));
//
//        verifyEq(grid.numRows(), 8);
//        verifyEq(grid.row(0).get("id"), HRef.make("S.Blacksburg.nhaystack_jace1.SineWave1"));
//        verifyEq(grid.row(1).get("id"), HRef.make("S.Blacksburg.nhaystack_jace1.SineWave2"));
//        verifyEq(grid.row(2).get("id"), HRef.make("S.Blacksburg.Transmogrifier.SineWave1"));
////        verifyEq(grid.row(0).get("id"), HRef.make("C.Drivers.NiagaraNetwork.nhaystack_jace1.points.SineWave1"));
////        verifyEq(grid.row(1).get("id"), HRef.make("C.Drivers.NiagaraNetwork.nhaystack_jace1.points.SineWave2"));
////        verifyEq(grid.row(2).get("id"), HRef.make("C.Drivers.NiagaraNetwork.nhaystack_jace2.points.SineWave1"));
//        verifyEq(grid.row(3).get("id"), HRef.make("H.nhaystack_jace1.AuditHistory"));
//        verifyEq(grid.row(4).get("id"), HRef.make("H.nhaystack_jace1.LogHistory"));
//        verifyEq(grid.row(5).get("id"), HRef.make("H.nhaystack_jace2.SineWave2"));
//        verifyEq(grid.row(6).get("id"), HRef.make("H.nhaystack_sup.AuditHistory"));
//        verifyEq(grid.row(7).get("id"), HRef.make("H.nhaystack_sup.LogHistory"));
//
//        HDict dict = client.readById(HRef.make("S.Blacksburg.nhaystack_jace1.SineWave1"));
//        verifyEq(dict.get("axType"), HStr.make("control:NumericPoint"));
//        verifyEq(dict.get("kind"), HStr.make("Number"));
//        verify(dict.has("his"));
//        verify(dict.has("curStatus"));
//        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave1"));
//        verifyEq(dict.get("unit"), HStr.make("?F"));
//        verify(dict.has("point"));
//        verifyEq(dict.get("tz"), localTz());
////        verify(dict.getDouble("curVal") == 0.0);
////        verifyEq(dict.get("hisInterpolate"), HStr.make("cov")); TODO
//
//        dict = client.readById(HRef.make("C.Drivers.NiagaraNetwork.nhaystack_jace1.points.SineWave2"));
//        verifyEq(dict.get("axType"), HStr.make("control:NumericPoint"));
//        verifyEq(dict.get("kind"), HStr.make("Number"));
//        verify(dict.has("his"));
//        verify(dict.has("curStatus"));
//        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave2"));
//        verifyEq(dict.get("unit"), HStr.make("psi"));
//        verify(dict.has("point"));
//        verifyEq(dict.get("tz"), localTz());
////        verify(dict.getDouble("curVal") == 0.0);
////        verifyEq(dict.get("hisInterpolate"), HStr.make("cov")); TODO
//
//        dict = client.readById(HRef.make("C.Drivers.NiagaraNetwork.nhaystack_jace2.points.SineWave1"));
//        verifyEq(dict.get("axType"), HStr.make("control:NumericPoint"));
//        verifyEq(dict.get("kind"), HStr.make("Number"));
//        verify(dict.missing("his"));
//        verify(dict.has("curStatus"));
//        verifyEq(dict.get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_jace2/points/SineWave1"));
//        verifyEq(dict.get("unit"), HStr.make("?F"));
//        verify(dict.has("point"));
//        verify(dict.missing("tz"));
////        verify(dict.getDouble("curVal") == 0.0);
//        verify(dict.missing("axHistoryRef"));
//        verify(dict.missing("hisInterpolate"));
//
//        ////////////////////////////////////////////////////////////////
//
//        dict = client.readById(HRef.make("H.nhaystack_jace2.SineWave2"));
//        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        verifyEq(dict.get("kind"), HStr.make("Number"));
//        verify(dict.has("point"));
//        verify(dict.has("his"));
//        verify(dict.missing("curStatus"));
//        verify(dict.missing("curVal"));
//        verifyEq(dict.get("tz"), localTz());
//        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_jace2/SineWave2"));
////        verifyEq(dict.get("hisInterpolate"), HStr.make("cov")); TODO
//        verifyEq(dict.get("unit"), HStr.make("psi"));
//
//        dict = client.readById(HRef.make("H.nhaystack_sup.AuditHistory"));
//        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        verify(dict.missing("kind"));
//        verify(dict.has("his"));
//        verify(dict.missing("curStatus"));
//        verify(dict.missing("curVal"));
//        verifyEq(dict.get("tz"), localTz());
//        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_sup/AuditHistory"));
//        verify(dict.missing("hisInterpolate"));
//        verify(dict.missing("unit"));
//
//        dict = client.readById(HRef.make("H.nhaystack_sup.LogHistory"));
//        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
////        verifyEq(dict.get("kind"), HStr.make("Str")); // TODO
//        verify(dict.has("point"));
//        verify(dict.has("his"));
//        verify(dict.missing("curStatus"));
//        verify(dict.missing("curVal"));
//        verifyEq(dict.get("tz"), localTz());
//        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_sup/LogHistory"));
//        verify(dict.missing("hisInterpolate"));
//        verify(dict.missing("unit"));
//
//        dict = client.readById(HRef.make("H.nhaystack_jace1.AuditHistory"));
//        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
////        verifyEq(dict.get("kind"), HStr.make("Str")); // TODO
//        verify(dict.has("point"));
//        verify(dict.has("his"));
//        verify(dict.missing("curStatus"));
//        verify(dict.missing("curVal"));
//        verifyEq(dict.get("tz"), localTz());
//        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_jace1/AuditHistory"));
//        verify(dict.missing("hisInterpolate"));
//        verify(dict.missing("unit"));
//
//        dict = client.readById(HRef.make("H.nhaystack_jace1.LogHistory"));
//        verifyEq(dict.get("axType"), HStr.make("history:HistoryConfig"));
////        verifyEq(dict.get("kind"), HStr.make("Str")); // TODO
//        verify(dict.has("point"));
//        verify(dict.has("his"));
//        verify(dict.missing("curStatus"));
//        verify(dict.missing("curVal"));
//        verifyEq(dict.get("tz"), localTz());
//        verifyEq(dict.get("axHistoryId"), HStr.make("/nhaystack_jace1/LogHistory"));
//        verify(dict.missing("hisInterpolate"));
//        verify(dict.missing("unit"));
//    }
//
////////////////////////////////////////////////////////////////////////////
//// Nav
////////////////////////////////////////////////////////////////////////////
//
//    void verifyNav() throws Exception
//    {
////[sep:/] 'Site'
////    [sep:/Blacksburg] 'Blacksburg'
////        [sep:/Blacksburg/nhaystack_jace1] 'Blacksburg nhaystack_jace1'
////            [---] 'Blacksburg nhaystack_jace1 SineWave1'
////            [---] 'Blacksburg nhaystack_jace1 SineWave2'
////        [sep:/Blacksburg/Transmogrifier] 'Blacksburg Transmogrifier'
////            [---] 'Blacksburg Transmogrifier SineWave1'
////        [sep:/Blacksburg/FluxCapacitor] 'Blacksburg FluxCapacitor'
//
//        HGrid grid = client.call("nav", makeNavGrid(HStr.make("sep:/")));
//        verifyEq(grid.numRows(), 1);
//        verifyEq(grid.row(0).get("navId"), HStr.make("sep:/Blacksburg"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("Blacksburg"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Blacksburg")));
//        verifyEq(grid.numRows(), 3);
//        verifyEq(grid.row(0).get("navId"), HStr.make("sep:/Blacksburg/nhaystack_jace1"));
//        verifyEq(grid.row(1).get("navId"), HStr.make("sep:/Blacksburg/Transmogrifier"));
//        verifyEq(grid.row(2).get("navId"), HStr.make("sep:/Blacksburg/FluxCapacitor"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("Blacksburg nhaystack_jace1"));
//        verifyEq(grid.row(1).get("dis"), HStr.make("Blacksburg Transmogrifier"));
//        verifyEq(grid.row(2).get("dis"), HStr.make("Blacksburg FluxCapacitor"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Blacksburg/nhaystack_jace1")));
//        verifyEq(grid.numRows(), 2);
//        verify(grid.row(0).missing("navId"));
//        verify(grid.row(1).missing("navId"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("Blacksburg nhaystack_jace1 SineWave1"));
//        verifyEq(grid.row(1).get("dis"), HStr.make("Blacksburg nhaystack_jace1 SineWave2"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Blacksburg/Transmogrifier")));
//        verifyEq(grid.numRows(), 1);
//        verify(grid.row(0).missing("navId"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("Blacksburg Transmogrifier SineWave1"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Blacksburg/FluxCapacitor")));
//        verifyEq(grid.numRows(), 0);
//
////[his:/] 'HistorySpace'
////    [his:/nhaystack_jace1] 'nhaystack_jace1'
////        [---] 'nhaystack_jace1_AuditHistory'
////        [---] 'nhaystack_jace1_LogHistory'
////    [his:/nhaystack_jace2] 'nhaystack_jace2'
////        [---] 'nhaystack_jace2_SineWave2'
////    [his:/nhaystack_sup] 'nhaystack_sup'
////        [---] 'nhaystack_sup_AuditHistory'
////        [---] 'nhaystack_sup_LogHistory'
//
//        grid = client.call("nav", makeNavGrid(HStr.make("his:/")));
//        verifyEq(grid.numRows(), 3);
//        verifyEq(grid.row(0).get("navId"), HStr.make("his:/nhaystack_jace1"));
//        verifyEq(grid.row(1).get("navId"), HStr.make("his:/nhaystack_jace2"));
//        verifyEq(grid.row(2).get("navId"), HStr.make("his:/nhaystack_sup"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("his:/nhaystack_jace1")));
//        verifyEq(grid.numRows(), 2);
//        verify(grid.row(0).missing("navId"));
//        verify(grid.row(1).missing("navId"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("nhaystack_jace1_AuditHistory"));
//        verifyEq(grid.row(1).get("dis"), HStr.make("nhaystack_jace1_LogHistory"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("his:/nhaystack_jace2")));
//        verifyEq(grid.numRows(), 1);
//        verify(grid.row(0).missing("navId"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("nhaystack_jace2_SineWave2"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("his:/nhaystack_sup")));
//        verifyEq(grid.numRows(), 2);
//        verify(grid.row(0).missing("navId"));
//        verify(grid.row(1).missing("navId"));
//        verifyEq(grid.row(0).get("dis"), HStr.make("nhaystack_sup_AuditHistory"));
//        verifyEq(grid.row(1).get("dis"), HStr.make("nhaystack_sup_LogHistory"));
//    }
//
////////////////////////////////////////////////////////////////////////////
//// His Reads
////////////////////////////////////////////////////////////////////////////
//
//    void verifyHisRead() throws Exception
//    {
//        HGrid grid = client.readAll("his");
//        verifyEq(grid.numRows(), 7);
//
//        ///////////////////////////////////////////////
//
//        HDict dict = client.read("axSlotPath==\"slot:/Drivers/NiagaraNetwork/nhaystack_jace1/points/SineWave1\"");
//        HGrid his = client.hisRead(dict.id(), "today");
//
//        verifyEq(his.meta().id(), dict.id());
//        verify(his.numRows() > 0);
//
//        int last = his.numRows()-1;
//        verifyEq(ts(his.row(last)).date, HDate.today());
//
//        verifyEq(numVal(his.row(0)).unit, "?F");
//
//        ///////////////////////////////////////////////
//
//        try { client.read("axHistoryId==\"/nhaystack_jace1/SineWave1\""); } 
//        catch(UnknownRecException e) { verifyException(e); }
//    }
//
////////////////////////////////////////////////////////////////////////////
//// Watches
////////////////////////////////////////////////////////////////////////////
//
//    void verifyWatches() throws Exception
//    {
//        // create new watch
//        HWatch w = client.watchOpen("NHaystack Supervisor Test");
//        verifyEq(w.id(), null);
//        verifyEq(w.dis(), "NHaystack Supervisor Test");
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
//        HGrid sub = w.sub(new HRef[] { a.id(), b.id(), c.id(), d.id() });
//        verifyEq(sub.numRows(), 4);
//        verifyEq(sub.row(0).dis(), a.dis());
//        verifyEq(sub.row(1).dis(), b.dis());
//        verifyEq(sub.row(2).dis(), c.dis());
//        verifyEq(sub.row(3).dis(), d.dis());
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
//        Thread.sleep(3000); // wait for the sine waves to tick over
//        poll = w.pollChanges();
//        verifyEq(poll.numRows(), 3);
//
//        // remove a, and then poll changes
//        w.unsub(new HRef[] { a.id() });
//        poll = w.pollChanges();
//        verifyEq(poll.numRows(), 2);
//
//        // close
//        w.close();
//        try { w.pollRefresh(); fail(); } catch (Exception e) { verifyException(e); }
//        verifyEq(client.watch(w.id(), false), null);
//        verifyEq(client.watches().length, 0);
//    }
//
////////////////////////////////////////////////////////////////////////////
//// Main
////////////////////////////////////////////////////////////////////////////
//
//    public static void main(String[] args)
//    {
//        runTests(new String[] { "nhaystack.test.NSupervisorClientTest", }, null);
//    }
}
