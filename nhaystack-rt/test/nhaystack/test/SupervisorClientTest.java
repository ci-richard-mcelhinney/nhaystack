//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.test;

import org.projecthaystack.*;
import org.projecthaystack.client.HClient;
import org.testng.Assert;
import org.testng.annotations.Test;

import static nhaystack.test.TestUtils.makeNavGrid;
import static nhaystack.test.TestUtils.numVal;
import static nhaystack.test.TestUtils.ts;

/**
 * NSupervisorClientTest -- this test uses nhaystack_sup
 */
public class SupervisorClientTest extends TestCore
{
    final String URI = "http://localhost:85/haystack/";
    HClient client;

    @Test(enabled = true)
    void verifySupAuth() throws Exception
    {
      try
      {
        this.client = HClient.open(URI, "admin", "Vk3ldb237847");
      }
      catch(Exception e)
      {
        Assert.fail("did not connect");
      }
      Assert.assertTrue(true);
    }

////////////////////////////////////////////////////////////////////////////
//// Reads
////////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  void verifySupRead()
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");
    HGrid grid = client.readAll("point");

//    for (int i = 0; i < grid.numRows(); i++)
//      System.out.println(i + ", " + grid.row(i).get("id"));

    Assert.assertEquals(grid.numRows(), 11);
    Assert.assertEquals(grid.row(0).get("id"), HRef.make("S.Blacksburg.nhaystack_j1.Sensor1"));
    Assert.assertEquals(grid.row(1).get("id"), HRef.make("S.Blacksburg.nhaystack_j1.SineWave1"));
    Assert.assertEquals(grid.row(2).get("id"), HRef.make("S.Blacksburg.nhaystack_j1.SineWave2"));
    Assert.assertEquals(grid.row(3).get("id"), HRef.make("C.Drivers.NiagaraNetwork.nhaystack_j2.points.Sensor2"));
    Assert.assertEquals(grid.row(4).get("id"), HRef.make("C.Drivers.NiagaraNetwork.nhaystack_j2.points.SineWave2"));
    Assert.assertEquals(grid.row(5).get("id"), HRef.make("S.Blacksburg.Transmogrifier.SineWave1"));
    Assert.assertEquals(grid.row(6).get("id"), HRef.make("H.nhaystack_j1.AuditHistory"));
    Assert.assertEquals(grid.row(7).get("id"), HRef.make("H.nhaystack_j1.LogHistory"));
    Assert.assertEquals(grid.row(8).get("id"), HRef.make("H.nhaystack_j2.SineWave2"));
    Assert.assertEquals(grid.row(9).get("id"), HRef.make("H.nhaystack_sup.AuditHistory"));
    Assert.assertEquals(grid.row(10).get("id"), HRef.make("H.nhaystack_sup.LogHistory"));

//        verifyEq(grid.row(3).get("id"), HRef.make("H.nhaystack_jace1.AuditHistory"));
//        verifyEq(grid.row(4).get("id"), HRef.make("H.nhaystack_jace1.LogHistory"));
//        verifyEq(grid.row(5).get("id"), HRef.make("H.nhaystack_jace2.SineWave2"));
//        verifyEq(grid.row(6).get("id"), HRef.make("H.nhaystack_sup.AuditHistory"));
//        verifyEq(grid.row(7).get("id"), HRef.make("H.nhaystack_sup.LogHistory"));
//
    HDict dict = client.readById(HRef.make("S.Blacksburg.nhaystack_j1.SineWave1"));
    Assert.assertEquals(dict.get("axType"), HStr.make("control:NumericPoint"));
    Assert.assertEquals(dict.get("kind"), HStr.make("Number"));

    Assert.assertTrue(dict.has("curStatus"));
    Assert.assertEquals(dict.get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_j1/points/SineWave1"));
    Assert.assertEquals(dict.get("unit"), HStr.make("°F"));
    Assert.assertTrue(dict.has("point"));
//    Assert.assertTrue(dict.has("his"));
//        verifyEq(dict.get("tz"), localTz());
//        verify(dict.getDouble("curVal") == 0.0);
//        verifyEq(dict.get("hisInterpolate"), HStr.make("cov")); TODO

    dict = client.readById(HRef.make("C.Drivers.NiagaraNetwork.nhaystack_j2.points.SineWave2"));
    Assert.assertEquals(dict.get("axType"), HStr.make("control:NumericPoint"));
    Assert.assertEquals(dict.get("kind"), HStr.make("Number"));
    Assert.assertTrue(dict.has("curStatus"));
    Assert.assertEquals(dict.get("axSlotPath"), HStr.make("slot:/Drivers/NiagaraNetwork/nhaystack_j2/points/SineWave2"));
    Assert.assertEquals(dict.get("unit"), HStr.make("psi"));
    Assert.assertTrue(dict.has("point"));
//        verify(dict.has("his"));
//        verifyEq(dict.get("tz"), localTz());
//        verify(dict.getDouble("curVal") == 0.0);
//        verifyEq(dict.get("hisInterpolate"), HStr.make("cov")); TODO

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
    }

////////////////////////////////////////////////////////////////////////////
// Nav
////////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  void verifySupNav()
  {
//[sep:/] 'Site'
//    [sep:/Blacksburg] 'Blacksburg'
//        [sep:/Blacksburg/nhaystack_jace1] 'Blacksburg nhaystack_jace1'
//            [---] 'Blacksburg nhaystack_jace1 SineWave1'
//            [---] 'Blacksburg nhaystack_jace1 SineWave2'
//        [sep:/Blacksburg/Transmogrifier] 'Blacksburg Transmogrifier'
//            [---] 'Blacksburg Transmogrifier SineWave1'
//        [sep:/Blacksburg/FluxCapacitor] 'Blacksburg FluxCapacitor'

    HGrid grid = client.call("nav", makeNavGrid(HStr.make("sep:/")));
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertEquals(grid.row(0).get("navId"), HStr.make("sep:/Blacksburg"));
    Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Blacksburg"));

    grid = client.call("nav", makeNavGrid(HStr.make("sep:/Blacksburg")));
    Assert.assertEquals(grid.numRows(), 2);
    Assert.assertEquals(grid.row(0).get("navId"), HStr.make("sep:/Blacksburg/nhaystack_j1"));
    Assert.assertEquals(grid.row(1).get("navId"), HStr.make("sep:/Blacksburg/Transmogrifier"));

    grid = client.call("nav", makeNavGrid(HStr.make("sep:/Blacksburg/nhaystack_j1")));
    Assert.assertEquals(grid.numRows(), 3);
    Assert.assertTrue(grid.row(0).missing("navId"));
    Assert.assertTrue(grid.row(1).missing("navId"));
    Assert.assertTrue(grid.row(2).missing("navId"));
    Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Blacksburg nhaystack_j1 Sensor1"));
    Assert.assertEquals(grid.row(1).get("dis"), HStr.make("Blacksburg nhaystack_j1 SineWave1"));
    Assert.assertEquals(grid.row(2).get("dis"), HStr.make("Blacksburg nhaystack_j1 SineWave2"));

    grid = client.call("nav", makeNavGrid(HStr.make("sep:/Blacksburg/Transmogrifier")));
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertTrue(grid.row(0).missing("navId"));
    Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Blacksburg Transmogrifier SineWave1"));

//[his:/] 'HistorySpace'
//    [his:/nhaystack_jace1] 'nhaystack_jace1'
//        [---] 'nhaystack_jace1_AuditHistory'
//        [---] 'nhaystack_jace1_LogHistory'
//    [his:/nhaystack_jace2] 'nhaystack_jace2'
//        [---] 'nhaystack_jace2_SineWave2'
//    [his:/nhaystack_sup] 'nhaystack_sup'
//        [---] 'nhaystack_sup_AuditHistory'
//        [---] 'nhaystack_sup_LogHistory'

    grid = client.call("nav", makeNavGrid(HStr.make("his:/")));
    Assert.assertEquals(grid.numRows(), 3);
    Assert.assertEquals(grid.row(0).get("navId"), HStr.make("his:/nhaystack_j1"));
    Assert.assertEquals(grid.row(1).get("navId"), HStr.make("his:/nhaystack_j2"));
    Assert.assertEquals(grid.row(2).get("navId"), HStr.make("his:/nhaystack_sup"));

    grid = client.call("nav", makeNavGrid(HStr.make("his:/nhaystack_j1")));
    Assert.assertEquals(grid.numRows(), 2);
    Assert.assertTrue(grid.row(0).missing("navId"));
    Assert.assertTrue(grid.row(1).missing("navId"));
    Assert.assertEquals(grid.row(0).get("dis"), HStr.make("nhaystack_j1_AuditHistory"));
    Assert.assertEquals(grid.row(1).get("dis"), HStr.make("nhaystack_j1_LogHistory"));

    grid = client.call("nav", makeNavGrid(HStr.make("his:/nhaystack_j2")));
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertTrue(grid.row(0).missing("navId"));
    Assert.assertEquals(grid.row(0).get("dis"), HStr.make("nhaystack_j2_SineWave2"));

    grid = client.call("nav", makeNavGrid(HStr.make("his:/nhaystack_sup")));
    Assert.assertEquals(grid.numRows(), 2);
    Assert.assertTrue(grid.row(0).missing("navId"));
    Assert.assertTrue(grid.row(1).missing("navId"));
    Assert.assertEquals(grid.row(0).get("dis"), HStr.make("nhaystack_sup_AuditHistory"));
    Assert.assertEquals(grid.row(1).get("dis"), HStr.make("nhaystack_sup_LogHistory"));
  }

////////////////////////////////////////////////////////////////////////////
//// His Reads
////////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  void verifySupHisRead()
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");
    HGrid grid = client.readAll("his");
    Assert.assertEquals(grid.numRows(), 6);

    HDict dict = client.read("axSlotPath==\"slot:/Drivers/NiagaraNetwork/nhaystack_j2/points/SineWave2\"");
    HGrid his = client.hisRead(dict.id(), "today");

    Assert.assertEquals(his.meta().id(), dict.id());
    Assert.assertTrue(his.numRows() > 0);

    int last = his.numRows() - 1;
    Assert.assertEquals(ts(his.row(last)).date, HDate.today());

    Assert.assertEquals(numVal(his.row(0)).unit, "psi");

    try
    {
      client.read("axHistoryId==\"/nhaystack_j1/SineWave1\"");
      Assert.fail("Should have received an exception....");
    }
    catch (UnknownRecException e)
    {
      Assert.assertEquals(e.getClass(), UnknownRecException.class);
    }
  }

////////////////////////////////////////////////////////////////////////////
//// Watches
////////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  void verifySupWatches() throws Exception
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");

    // create new watch
    HWatch w = client.watchOpen("NHaystack Supervisor Test", HNum.make(120, "s"));
    Assert.assertEquals(w.id(), null);
    Assert.assertEquals(w.dis(), "NHaystack Supervisor Test");

    // do query to get some recs
    HGrid recs = client.readAll("point");
    Assert.assertTrue(recs.numRows() >= 4);
    HDict a = recs.row(0);
    HDict b = recs.row(1);
    HDict c = recs.row(2);
    HDict d = recs.row(3);

//System.out.println(a);
//System.out.println(b);
//System.out.println(c);
//System.out.println(d);

    HGrid sub = w.sub(new HRef[]{a.id(), b.id(), c.id(), d.id()});
    Assert.assertEquals(sub.numRows(), 4);
    Assert.assertEquals(sub.row(0).id(), a.id());
    Assert.assertEquals(sub.row(1).id(), b.id());
    Assert.assertEquals(sub.row(2).id(), c.id());
    Assert.assertEquals(sub.row(3).id(), d.id());

    // verify state of watch now
    Assert.assertTrue(client.watch(w.id()) == w);
    Assert.assertEquals(client.watches().length, 1);
    Assert.assertTrue(client.watches()[0] == w);
    Assert.assertEquals(w.lease().millis(), 2L * 60 * 1000);

    // poll refresh
    HGrid poll = w.pollRefresh();
    Assert.assertEquals(poll.numRows(), 4);
//    verifyGridContains(poll, "id", a.id());
//    verifyGridContains(poll, "id", b.id());
//    verifyGridContains(poll, "id", c.id());
//    verifyGridContains(poll, "id", d.id());

    // poll changes
    Thread.sleep(10000); // wait for the sine waves to tick over
    poll = w.pollChanges();
    Assert.assertEquals(poll.numRows(), 2);

    // remove a, and then poll changes
    w.unsub(new HRef[]{a.id()});
    poll = w.pollChanges();
    Assert.assertEquals(poll.numRows(), 0);

    // close
    w.close();
    try
    {
      w.pollRefresh();
      Assert.fail();
    } catch (Exception e)
    {
      Assert.assertTrue(e instanceof Exception);
    }
    Assert.assertEquals(client.watch(w.id(), false), null);
    Assert.assertEquals(client.watches().length, 0);
  }

}
