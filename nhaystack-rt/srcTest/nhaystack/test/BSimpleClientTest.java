//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.test;


import com.tridium.testng.BTestNg;
import org.projecthaystack.*;
import org.projecthaystack.client.*;
import org.projecthaystack.auth.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.*;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;

/**
 * BSimpleClientTest -- this test uses nhaystack_simple
 */
@NiagaraType
@Test
public class BSimpleClientTest extends BTestNg
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.test.BSimpleClientTest(2979906276)1.0$ @*/
/* Generated Sat May 21 21:24:09 AEST 2016 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
//  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BSimpleClientTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

//////////////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////////////////

  final String URI = "http://localhost:82/haystack/";
  HClient client;

//////////////////////////////////////////////////////////////////////////
// Setup / Teardown
//////////////////////////////////////////////////////////////////////////

  @BeforeClass(alwaysRun = true)
  public void setup()
  { }

  @AfterClass(alwaysRun = true)
  public void tearDown()
  { }


//////////////////////////////////////////////////////////////////////////
// Auth
//////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  public void verifyAuth() throws Exception
  {
    // get bad credentials
    try
    {  HClient.open(URI, "baduser", "badpass").about(); Assert.fail(); }
    catch (Exception e)
    {  Assert.assertEquals(e.getClass(), AuthException.class); }

    try
    {  HClient.open(URI, "admin", "badpass").about(); Assert.fail(); }
    catch (CallException e)
    {  Assert.assertEquals(e.getClass(), AuthException.class); }

    try
    {
      this.client = HClient.open(URI, "admin", "Vk3ldb237847");
      client.about();
    }
    catch(Exception e)
    {  e.printStackTrace(); Assert.fail(); }

    // create proper client
//    this.client = HClient.open(URI, "admin", "Vk3ldb237847");
//    client.about();
  }

  @Test(enabled = true)
  public void verifyHttpsAuth()
  {
    try
    {
      HClient local = HClient.open("https://localhost/haystack/", "admin", "Vk3ldb237847");
      local.about();
    }
    catch(Exception e)
    {
      e.printStackTrace();
      Assert.fail();
    }
  }

//////////////////////////////////////////////////////////////////////////
// About
//////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  void verifyAbout() throws Exception
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");
    HDict r = client.about();
    Assert.assertEquals(r.getStr("haystackVersion"), "2.0");
    Assert.assertEquals(r.getStr("productName"), "Niagara AX");
    Assert.assertEquals(r.getStr("productVersion"), "4.1.27.20");
    Assert.assertEquals(r.getStr("tz"), HTimeZone.DEFAULT.name);
  }

//////////////////////////////////////////////////////////////////////////
// Ops
//////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  public void verifyOps() throws Exception
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");
    HGrid g = client.ops();

    // verify required columns
    Assert.assertTrue(g.col("name") != null);
    Assert.assertTrue(g.col("summary") != null);

    // verify required ops
    verifyGridContains(g, "name", HStr.make("about"));
    verifyGridContains(g, "name", HStr.make("ops"));
    verifyGridContains(g, "name", HStr.make("formats"));
    verifyGridContains(g, "name", HStr.make("read"));
    verifyGridContains(g, "name", HStr.make("nav"));
    verifyGridContains(g, "name", HStr.make("watchSub"));
    verifyGridContains(g, "name", HStr.make("watchUnsub"));
    verifyGridContains(g, "name", HStr.make("watchPoll"));
    verifyGridContains(g, "name", HStr.make("pointWrite"));
    verifyGridContains(g, "name", HStr.make("hisRead"));
    verifyGridContains(g, "name", HStr.make("hisWrite"));
    verifyGridContains(g, "name", HStr.make("invokeAction"));
  }

//////////////////////////////////////////////////////////////////////////
// Formats
//////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  public void verifyFormats() throws Exception
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");
    HGrid g = client.formats();

    // verify required columns
    Assert.assertTrue(g.col("mime") != null);
    Assert.assertTrue(g.col("read") != null);
    Assert.assertTrue(g.col("write") != null);

    // verify required ops
    verifyGridContains(g, "mime", HStr.make("text/plain"));
    verifyGridContains(g, "mime", HStr.make("text/zinc"));
  }

//////////////////////////////////////////////////////////////////////////
// Reads
//////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  public void verifyRead() throws Exception
  {
    HDict dict;
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");
    HGrid grid = client.readAll("id");

    Assert.assertEquals(grid.numRows(), 18);
    Assert.assertEquals(grid.row(0).id(), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(1).id(), HRef.make("S.Winterfell.Equip1.point1"));
    Assert.assertEquals(grid.row(2).id(), HRef.make("S.Winterfell.Equip1.point2"));
    Assert.assertEquals(grid.row(3).id(), HRef.make("S.Winterfell.Equip1.point3"));
    Assert.assertEquals(grid.row(4).id(), HRef.make("S.Winterfell.Equip1.point4"));
    Assert.assertEquals(grid.row(5).id(), HRef.make("S.Winterfell.Equip1.BooleanWritable"));
    Assert.assertEquals(grid.row(6).id(), HRef.make("S.Winterfell.Equip1.EnumWritable"));
    Assert.assertEquals(grid.row(7).id(), HRef.make("S.Winterfell.Equip1.StringWritable"));
    Assert.assertEquals(grid.row(8).id(), HRef.make("S.Winterfell.Equip1.SineWave1"));
    Assert.assertEquals(grid.row(9).id(), HRef.make("S.Winterfell.Equip1"));
    Assert.assertEquals(grid.row(10).id(), HRef.make("S.Winterfell.Equip1.point5"));
    Assert.assertEquals(grid.row(11).id(), HRef.make("S.Winterfell.Equip1.SineWave2"));
    Assert.assertEquals(grid.row(12).id(), HRef.make("S.Winterfell.Equip2.SineWave3"));
    Assert.assertEquals(grid.row(13).id(), HRef.make("S.Winterfell.Equip2"));
    Assert.assertEquals(grid.row(14).id(), HRef.make("S.Winterfell.Equip1.SineWave4"));
    Assert.assertEquals(grid.row(15).id(), HRef.make("C.SineWave5"));
    Assert.assertEquals(grid.row(16).id(), HRef.make("H.nhaystack1.AuditHistory"));
    Assert.assertEquals(grid.row(17).id(), HRef.make("H.nhaystack1.LogHistory"));

    Assert.assertEquals(grid.row(1).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(2).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(3).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(4).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(5).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(6).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(7).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(8).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(9).get("siteRef"), HRef.make("S.Winterfell"));

    // TODO add check for equipRef to Equip2
    Assert.assertEquals(grid.row(1).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
    Assert.assertEquals(grid.row(2).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
    Assert.assertEquals(grid.row(3).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
    Assert.assertEquals(grid.row(4).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
    Assert.assertEquals(grid.row(6).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
    Assert.assertEquals(grid.row(7).get("equipRef"), HRef.make("S.Winterfell.Equip1"));
//    Assert.assertEquals(grid.row(9).get("equipRef"), HRef.make("S.Winterfell.Equip1"));

//        for (int i = 0; i < grid.numRows(); i++)
//        {
//            HRow row = grid.row(i);
//            if (row.has("equipRef"))
//                System.out.println(i + ", " + row.id() + ", " + row.get("equipRef"));
//        }

//        //////////////////////////////////////////
//
//    HDict dict = client.readById(HRef.make("C.Foo.SineWave1"));
//    Assert.assertEquals(dict.get("axType"), HStr.make("kitControl:SineWave"));
//    verify(dict.has("foo"));
//    verify(dict.has("bar"));
//    Assert.assertEquals(dict.get("kind"), HStr.make("Number"));
//    verify(dict.has("his"));
//    Assert.assertEquals(dict.get("hisInterpolate"), HStr.make("cov"));
//    Assert.assertEquals(dict.get("axSlotPath"), HStr.make("slot:/Foo/SineWave1"));
//    Assert.assertEquals(dict.get("unit"), HStr.make("\\uxxB0" + "F"));
//    verify(dict.has("point"));
//    Assert.assertEquals(dict.get("tz"), localTz());
//    verify(dict.has("cur"));
//    double curVal = dict.getDouble("curVal");
//    Assert.assertEquals(dict.get("curStatus"), HStr.make("ok"));
//    verify(curVal >= 0.0 && curVal <= 100.0);
//
//    Assert.assertEquals(dict.get("dis"), HStr.make("Foo_SineWave1"));
//    Assert.assertEquals(dict.get("navName"), HStr.make("Foo_SineWave1"));
//    Assert.assertEquals(dict.get("navNameFormat"), HStr.make("%parent.displayName%_%displayName%"));
//
//        //////////////////////////////////////////
//
//        dict = client.readById(HRef.make("C.Foo.Sine-Wave2~2fabc"));
//        Assert.assertEquals(dict.get("axType"), HStr.make("kitControl:SineWave"));
//        verify(dict.missing("foo"));
//        verify(dict.missing("bar"));
//        Assert.assertEquals(dict.get("kind"), HStr.make("Number"));
//        verify(dict.has("his"));
//        Assert.assertEquals(dict.get("curStatus"), HStr.make("ok"));
//        verify(dict.has("hisInterpolate"));
//        Assert.assertEquals(dict.get("axSlotPath"), HStr.make("slot:/Foo/Sine$20Wave2$2fabc"));
//        Assert.assertEquals(dict.get("unit"), HStr.make("psi"));
//        verify(dict.has("point"));
//        verify(dict.has("tz"));
//        verify(dict.has("cur"));
//        curVal = dict.getDouble("curVal");
//        verify(curVal >= 0.0 && curVal <= 100.0);
//
//        Assert.assertEquals(dict.get("dis"), HStr.make("Sine-Wave2~2fabc"));
//        Assert.assertEquals(dict.get("navName"), HStr.make("Sine-Wave2~2fabc"));
//        verify(dict.missing("navNameFormat"));
//
//        //////////////////////////////////////////
//
//        dict = client.readById(HRef.make("S.Richmond.AHU2.NumericWritable"));
//
//        //////////////////////////////////////////

//    dict = client.readById(HRef.make("H.nhaystack1.AuditHistory"));
//    System.out.println(dict.toString());
//    Assert.assertEquals(dict.get("axType"), HStr.make("history:HistoryConfig"));
//    verify(dict.missing("kind"));
//    verify(dict.has("his"));
//    verify(dict.missing("cur"));
//    verify(dict.missing("curStatus"));
//    verify(dict.missing("curVal"));
//    Assert.assertEquals(dict.get("tz"), localTz());
//    Assert.assertEquals(dict.get("axHistoryId"), HStr.make("/nhaystack1/AuditHistory"));
//    verify(dict.missing("hisInterpolate"));
//    verify(dict.missing("unit"));

//        dict = client.readById(HRef.make("H.nhaystack_simple.LogHistory"));
//        Assert.assertEquals(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        verify(dict.missing("kind"));
//        verify(dict.has("his"));
//        verify(dict.missing("cur"));
//        verify(dict.missing("curStatus"));
//        verify(dict.missing("curVal"));
//        Assert.assertEquals(dict.get("tz"), localTz());
//        Assert.assertEquals(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/LogHistory"));
//        verify(dict.missing("hisInterpolate"));
//        verify(dict.missing("unit"));
//
//        //        dict = client.readById(HRef.make("H.nhaystack_simple.SineWave3"));
//        //        Assert.assertEquals(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        //        Assert.assertEquals(dict.get("kind"), HStr.make("Number"));
//        //        verify(dict.has("his"));
//        //        verify(dict.missing("cur"));
//        //        verify(dict.missing("curStatus"));
//        //        verify(dict.missing("curVal"));
//        //        Assert.assertEquals(dict.get("tz"), localTz());
//        //        Assert.assertEquals(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/SineWave3"));
//        //        verify(dict.missing("hisInterpolate"));
//        //        Assert.assertEquals(dict.get("unit"), HStr.make("psi"));
//
//        try { client.readById(HRef.make("c.Mg~~")); } catch(Exception e) { verifyException(e); }
  }

//////////////////////////////////////////////////////////////////////////
// Nav
//////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  public void verifyNav() throws Exception
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");

    HGrid grid = client.call("read", makeIdGrid(HUri.make("sep:/Winterfell")));
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertEquals(grid.row(0).id(), HRef.make("S.Winterfell"));

    grid = client.call("read", makeIdGrid(HUri.make("sep:/Winterfell/Equip1")));
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertEquals(grid.row(0).id(), HRef.make("S.Winterfell.Equip1"));

    HGrid n = makeNavGrid(HStr.make("sep:/Winterfell/Equip1"));
    grid = client.call("nav", n);
    Assert.assertEquals(grid.numRows(), 11);
    Assert.assertEquals(grid.row(0).id(), HRef.make("S.Winterfell.Equip1.point1"));
    Assert.assertEquals(grid.row(1).id(), HRef.make("S.Winterfell.Equip1.point2"));
    Assert.assertEquals(grid.row(2).id(), HRef.make("S.Winterfell.Equip1.point3"));
    Assert.assertEquals(grid.row(3).id(), HRef.make("S.Winterfell.Equip1.point4"));
    Assert.assertEquals(grid.row(4).id(), HRef.make("S.Winterfell.Equip1.BooleanWritable"));
    Assert.assertEquals(grid.row(5).id(), HRef.make("S.Winterfell.Equip1.EnumWritable"));
    Assert.assertEquals(grid.row(6).id(), HRef.make("S.Winterfell.Equip1.StringWritable"));
    Assert.assertEquals(grid.row(7).id(), HRef.make("S.Winterfell.Equip1.SineWave1"));
    Assert.assertEquals(grid.row(8).id(), HRef.make("S.Winterfell.Equip1.point5"));
    Assert.assertEquals(grid.row(9).id(), HRef.make("S.Winterfell.Equip1.SineWave2"));
    Assert.assertEquals(grid.row(10).id(), HRef.make("S.Winterfell.Equip1.SineWave4"));

    grid = client.call("nav", HGrid.EMPTY);
    Assert.assertEquals(grid.numRows(), 3);
    Assert.assertEquals(grid.row(0).get("navId"), HStr.make("slot:/"));
    Assert.assertEquals(grid.row(0).get("dis"), HStr.make("ComponentSpace"));
    Assert.assertEquals(grid.row(1).get("navId"), HStr.make("his:/"));
    Assert.assertEquals(grid.row(1).get("dis"), HStr.make("HistorySpace"));
    Assert.assertEquals(grid.row(2).get("navId"), HStr.make("sep:/"));
    Assert.assertEquals(grid.row(2).get("dis"), HStr.make("Site"));

    n = makeNavGrid(HStr.make("his:/"));
    grid = client.call("nav", n);
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertEquals(grid.row(0).get("navId"), HStr.make("his:/nhaystack1"));

//    n = makeNavGrid(HStr.make("his:/nhaystack1"));
//    grid = client.call("nav", n);
//    Assert.assertEquals(grid.numRows(), 3);
//    Assert.assertEquals(grid.numRows(), 2);
//
    n = makeNavGrid(HStr.make("slot:/"));
    grid = client.call("nav", n);
//    System.out.println(grid.row(0).toString());
//    System.out.println(grid.row(1).toString());
//    System.out.println(grid.row(2).toString());
//    System.out.println(grid.row(3).toString());
//    System.out.println(grid.row(4).toString());
//    System.out.println(grid.row(5).toString());
    Assert.assertEquals(grid.numRows(), 7);
    Assert.assertEquals(grid.row(0).get("navId"), HStr.make("slot:/Services"));
    Assert.assertEquals(grid.row(1).get("navId"), HStr.make("slot:/Drivers"));
//    Assert.assertEquals(grid.row(2).get("navId"), HStr.make("slot:/Winterfell"));
    Assert.assertEquals(grid.row(3).get("navId"), HStr.make("slot:/Equip1"));
    Assert.assertEquals(grid.row(4).get("navId"), HStr.make("slot:/Equip2"));
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
//        Assert.assertEquals(grid.numRows(), 1);
//        Assert.assertEquals(grid.row(0).get("navId"), HStr.make("sep:/Richmond"));
//        Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Richmond"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond")));
//        Assert.assertEquals(grid.numRows(), 3);
//        Assert.assertEquals(grid.row(0).get("navId"), HStr.make("sep:/Richmond/AHU1"));
//        Assert.assertEquals(grid.row(1).get("navId"), HStr.make("sep:/Richmond/AHU2"));
//        Assert.assertEquals(grid.row(2).get("navId"), HStr.make("sep:/Richmond/AHU3"));
//        Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Richmond AHU1"));
//        Assert.assertEquals(grid.row(1).get("dis"), HStr.make("Richmond AHU2"));
//        Assert.assertEquals(grid.row(2).get("dis"), HStr.make("Richmond AHU3"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU1")));
//        Assert.assertEquals(grid.numRows(), 2);
//        verify(grid.row(0).missing("navId"));
//        verify(grid.row(1).missing("navId"));
//        Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Richmond AHU1 AHU2_BooleanWritable"));
//        Assert.assertEquals(grid.row(1).get("dis"), HStr.make("Richmond AHU1 AHU3_BooleanWritable"));
//        
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU2")));
//        Assert.assertEquals(grid.numRows(), 2);
//        verify(grid.row(0).missing("navId"));
//        verify(grid.row(1).missing("navId"));
//        Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Richmond AHU2 NumericWritable"));
//        Assert.assertEquals(grid.row(1).get("dis"), HStr.make("Richmond AHU2 NumericWritable1"));
//
//        grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU3")));
//        Assert.assertEquals(grid.numRows(), 2);
//        verify(grid.row(0).missing("navId"));
//        verify(grid.row(1).missing("navId"));
//        Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Richmond AHU3 NumericWritable"));
//        Assert.assertEquals(grid.row(1).get("dis"), HStr.make("Richmond AHU3 NumericWritable1"));
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

  @Test(enabled = true)
  public void verifyHisRead() throws Exception
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");

    HGrid grid = client.readAll("his");
    Assert.assertEquals(grid.numRows(), 4);

    ///////////////////////////////////////////////

    HDict dict = client.read("axSlotPath==\"slot:/SineWave4\"");
    HGrid his = client.hisRead(dict.id(), "today");

    Assert.assertEquals(his.meta().id(), dict.id());
    Assert.assertTrue(his.numRows() > 0);

    int last = his.numRows() - 1;
    Assert.assertEquals(ts(his.row(last)).date, HDate.today());

    //TODO there is an issue with units here that needs to be solved
//    Assert.assertEquals(numVal(his.row(0)).unit, "\\uxxB0" + "F");

    ///////////////////////////////////////////////

    dict = client.read("axHistoryId==\"/nhaystack1/LogHistory\"");
    his = client.hisRead(dict.id(), "today");
    Assert.assertEquals(his.meta().id(), dict.id());
    Assert.assertTrue(his.numRows() > 0);

    last = his.numRows() - 1;
    Assert.assertEquals(ts(his.row(last)).date, HDate.today());

    ///////////////////////////////////////////////

    // TODO investigate the auto-generation of the axHistoryId tag
    // TODO this particular test requires that, but right now it
    // TODO isn't being generated, maybe it's not necessary
//    dict = client.read("axHistoryId==\"/nhaystack1/SineWave5\"");
//    his = client.hisRead(dict.id(), "today");
//    Assert.assertEquals(his.meta().id(), dict.id());

    ///////////////////////////////////////////////

    // TODO work out what these to lines are testing????
//    client.hisRead(HRef.make("C.AHU2.NumericWritable"), "today");
//    client.hisRead(HRef.make("S.Richmond.AHU2.NumericWritable"), "today");
  }

////////////////////////////////////////////////////////////////////////////
//// Watches
////////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  void verifyWatches() throws Exception
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");

    // create new watch
    HWatch w = client.watchOpen("NHaystack Simple Test", HNum.make(120, "s"));
    Assert.assertEquals(w.id(), null);
    Assert.assertEquals(w.dis(), "NHaystack Simple Test");

    // do query to get some recs
    HGrid recs = client.readAll("point");
    Assert.assertTrue(recs.numRows() >= 4);
    HDict a = recs.row(0);
    HDict b = recs.row(1);
    HDict c = recs.row(2);
    HDict d = recs.row(3);

//    System.out.println(a);
//    System.out.println(b);
//    System.out.println(c);
//    System.out.println(d);

    // do first sub
    HGrid sub = w.sub(new HRef[]{a.id(), b.id()});
    Assert.assertEquals(sub.numRows(), 2);
    Assert.assertEquals(sub.row(0).id(), a.id());
    Assert.assertEquals(sub.row(1).id(), b.id());

    // now add c, d
    sub = w.sub(new HRef[]{c.id(), d.id()}, false);
    Assert.assertEquals(sub.numRows(), 2);
    Assert.assertEquals(sub.row(0).id(), c.id());
    Assert.assertEquals(sub.row(1).id(), d.id());

    // verify state of watch now
    Assert.assertTrue(client.watch(w.id()) == w);
    Assert.assertEquals(client.watches().length, 1);
    Assert.assertTrue((client.watches()[0] == w));
    Assert.assertEquals(w.lease().millis(), 2L * 60 * 1000);

    // poll refresh
    HGrid poll = w.pollRefresh();
    Assert.assertEquals(poll.numRows(), 4);
    verifyGridContains(poll, "id", a.id());
    verifyGridContains(poll, "id", b.id());
    verifyGridContains(poll, "id", c.id());
    verifyGridContains(poll, "id", d.id());

    // poll changes
    Thread.sleep(2000); // wait for the sine waves to tick over
    poll = w.pollChanges();
    Assert.assertEquals(poll.numRows(), 4);

    // remove d, and then poll refresh
    w.unsub(new HRef[]{d.id()});
    poll = w.pollRefresh();
    Assert.assertEquals(poll.numRows(), 3);

    // close
    w.close();
    try
    {
      w.pollRefresh();
      Assert.fail();
    }
    catch (Exception e)
    {
      verifyEx(e);
    }
//    Assert.assertEquals(client.watch(w.id(), false), null);
//    Assert.assertEquals(client.watches().length, 0);

    // check bad id
//    w = client.watchOpen("Bogus Test");
//    HRef badId = HRef.make("c." + Base64.URI.encode("badBadBad"));
//    try
//    {
//      w.sub(new HRef[]{badId}).dump();
//      fail();
//    } catch (Exception e)
//    {
//      verifyException(e);
//    }
  }

////////////////////////////////////////////////////////////////////////////
//// Point Write
////////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  void verifyPointWrite() throws Exception
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");
    doVerifyPointWrite(HRef.make("S.Winterfell.Equip1.point5"));
//    doVerifyPointWrite(HRef.make("S.Winterfell.Equip1.point2"));
  }

  @Test(enabled = true)
  public void verifyRemotePointWrite() throws Exception
  {
    this.client = HClient.open("http://localhost:85/haystack/", "user", "Vk3ldb237847");
    doVerifyPointWrite(HRef.make("C.Drivers.NiagaraNetwork.nhaystack_j1.points.Sensor1"));
  }

  private void doVerifyPointWrite(HRef id)
  {
    client.pointWrite(id, 16, "user", HNum.make(111), null);
    HGrid grid = client.pointWrite(id, 10, "admin", HNum.make(222), null);
    Assert.assertEquals(grid.numRows(), 17);
    for (int i = 0; i < 17; i++)
    {
      Assert.assertEquals(grid.row(i).getInt("level"), i + 1);
      switch (i + 1)
      {
        case 10:
          Assert.assertEquals(grid.row(i).get("val"), HNum.make(222));
          // TODO check if this is supposed to work in Niagara, from reading the code
          // TODO it doesn't look as if "who" functionality is supported right now
//          Assert.assertEquals(grid.row(i).get("who"), HStr.make("admin"));
          break;
        case 16:
          Assert.assertEquals(grid.row(i).get("val"), HNum.make(111));
          Assert.assertTrue(grid.row(i).missing("who"));
          break;
        default:
          Assert.assertTrue(grid.row(i).missing("val"));
          Assert.assertTrue(grid.row(i).missing("who"));
          break;
      }
    }

    grid = client.pointWrite(id, 10, "admin", null, null);
    Assert.assertEquals(grid.numRows(), 17);
    for (int i = 0; i < 17; i++)
    {
      Assert.assertEquals(grid.row(i).getInt("level"), i + 1);
      switch (i + 1)
      {
        case 10:
          Assert.assertTrue(grid.row(i).missing("val"));
          // TODO check if this is supposed to work in Niagara, from reading the code
          // TODO it doesn't look as if "who" functionality is supported right now
//          Assert.assertEquals(grid.row(i).get("who"), HStr.make("admin"));
          break;
        case 16:
          Assert.assertEquals(grid.row(i).get("val"), HNum.make(111));
          Assert.assertTrue(grid.row(i).missing("who"));
          break;
        default:
          Assert.assertTrue(grid.row(i).missing("val"));
          Assert.assertTrue(grid.row(i).missing("who"));
          break;
      }
    }

    // reset point to known state before next test
    grid = client.pointWrite(id, 16, "admin", null, null);
    // just make sure this works with no level, etc
    grid = client.pointWriteArray(id);
  }

////////////////////////////////////////////////////////////////////////////
//// Invoke Action
////////////////////////////////////////////////////////////////////////////

  @Test(enabled = true)
  void verifyInvokeAction() throws Exception
  {
    this.client = HClient.open(URI, "admin", "Vk3ldb237847");
    doVerifyInvokeAction(HRef.make("S.Winterfell.Equip1.point5"));
//        doVerifyInvokeAction(HRef.make("S.Richmond.AHU2.NumericWritable"));
  }

  private void doVerifyInvokeAction(HRef id)
  {
    HDictBuilder hd = new HDictBuilder();
    hd.add("arg", HNum.make(333));
    client.invokeAction(id, "emergencyOverride", hd.toDict());

    HGrid grid = client.pointWriteArray(id);
    Assert.assertEquals(grid.numRows(), 17);
    for (int i = 0; i < 17; i++)
    {
      Assert.assertEquals(grid.row(i).getInt("level"), i + 1);
      switch (i + 1)
      {
        case 1:
          Assert.assertEquals(grid.row(i).get("val"), HNum.make(333));
          Assert.assertTrue(grid.row(i).missing("who"));
          break;
        case 10:
//          Assert.assertTrue(grid.row(i).missing("val"));
//          Assert.assertEquals(grid.row(i).get("who"), HStr.make("admin"));
          break;
        case 17:
//          Assert.assertEquals(grid.row(i).get("val"), HNum.make(111));
//          Assert.assertTrue(grid.row(i).missing("who"));
          break;
        default:
          Assert.assertTrue(grid.row(i).missing("val"));
          Assert.assertTrue(grid.row(i).missing("who"));
          break;
      }
    }

    client.invokeAction(id, "emergencyAuto", HDict.EMPTY);

    grid = client.pointWriteArray(id);
    Assert.assertEquals(grid.numRows(), 17);
    for (int i = 0; i < 17; i++)
    {
      Assert.assertEquals(grid.row(i).getInt("level"), i + 1);
      Assert.assertTrue(grid.row(i).missing("val"));
      Assert.assertTrue(grid.row(i).missing("who"));

      // TODO not sure what the following was trying to accomplish
      // TODO instead just checking that all levels have returned
      // TODO null status in the above checks
//      switch (i + 1)
//      {
//        case 10:
//          Assert.assertTrue(grid.row(i).missing("val"));
//          Assert.assertEquals(grid.row(i).get("who"), HStr.make("admin"));
//          break;
//        case 17:
//          Assert.assertEquals(grid.row(i).get("val"), HNum.make(111));
//          Assert.assertTrue(grid.row(i).missing("who"));
//          break;
//        default:
//          Assert.assertTrue(grid.row(i).missing("val"));
//          Assert.assertTrue(grid.row(i).missing("who"));
//          break;
//      }
    }
  }

//////////////////////////////////////////////////////////////////
//// filter
//////////////////////////////////////////////////////////////////
//
//    void verifyFilter() throws Exception
//    {
//        HGrid grid = client.readAll("point and equipRef->navName == \"AHU1\"");
//        Assert.assertEquals(grid.numRows(), 2);
//        Assert.assertEquals(grid.row(0).get("axSlotPath"), HStr.make("slot:/AHU2/BooleanWritable"));
//        Assert.assertEquals(grid.row(1).get("axSlotPath"), HStr.make("slot:/AHU3/BooleanWritable"));
//    }
//

////////////////////////////////////////////////////////////////
// Utils
////////////////////////////////////////////////////////////////
  static HGrid makeIdGrid(HVal id)
  {
    HDictBuilder hd = new HDictBuilder();
    hd.add("id", id);
    return HGridBuilder.dictsToGrid(new HDict[] { hd.toDict() });
  }

  static HGrid makeNavGrid(HStr navId)
  {
    HDictBuilder hd = new HDictBuilder();
    hd.add("navId", navId);
    return HGridBuilder.dictsToGrid(new HDict[] { hd.toDict() });
  }

  void verifyGridContains(HGrid g, String col, HVal val)
  {
    boolean found = false;
    for (int i=0; i<g.numRows(); ++i)
    {
      HVal x = g.row(i).get(col, false);
      if (x != null && x.equals(val)) { found = true; break; }
    }
    if (!found)
    {
      System.out.println("verifyGridContains " + col + "=" + val + " failed!");
      Assert.fail();
    }
  }

  void verifyEx(Exception e)
  {
    System.out.println(e.toString());
    Assert.assertTrue(!e.toString().contains("Test failed"));
  }

  HDateTime ts(HDict r, String col) { return (HDateTime)r.get(col); }
  HDateTime ts(HDict r) { return (HDateTime)r.get("ts"); }
  HNum numVal(HRow r) { return (HNum)r.get("val"); }
  HStr localTz() { return HStr.make(HTimeZone.DEFAULT.name); }
}
