//
// Copyright 2019 Project Haystack All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//    16 Mar 2020    Richard McELhinney  Creation
//

package nhaystack.e2e;

import com.tridium.history.log.BLogHistoryService;
import com.tridium.kitControl.util.*;
import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.server.*;
import nhaystack.site.BHSite;
import org.projecthaystack.*;
import org.projecthaystack.client.*;
import org.testng.Assert;
import org.testng.annotations.*;

import javax.baja.control.*;
import javax.baja.history.ext.*;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;
import javax.baja.tag.Id;
import javax.baja.units.BUnit;
import javax.baja.util.*;
import java.util.Iterator;

import static nhaystack.util.NHaystackConst.*;

@NiagaraType
@Test
public class BSimpleClientTest extends BNHaystackStationTestBase
{

  @Override
  public Type getType()
  {
    return TYPE;
  }

  public static final Type TYPE = Sys.loadType(BSimpleClientTest.class);

  @Override
  protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort) throws Exception
  {
    super.configureTestStation(station, stationName, webPort, foxPort);

    this.station = station;
    station.getServices().add("LogHistoryService", new BLogHistoryService());
  }

  @BeforeClass
  @Override
  public void setupStation() throws Exception
  {
    super.setupStation();
    setupCompTree();
  }

  private void setupCompTree()
  {
    BNHaystackService service = (BNHaystackService) station.getServices().get("NHaystackService");
    if (service == null)
    {
      System.out.println("Can't find nhaystack service...");
    }

    service.setShowLinkedHistories(false);

    BFolder home = new BFolder();
    station.add("home", home);
    BHSite winterfell = new BHSite();
    BHSite richmond = new BHSite();
    BFolder equip1 = new BFolder();
    equip1.tags().set(Id.newId("hs", "equip"), BMarker.DEFAULT);

    BFolder equip2 = new BFolder();
    equip2.tags().set(Id.newId("hs", "equip"), BMarker.DEFAULT);

    BFolder ahu = new BFolder();
    ahu.tags().set(Id.newId("hs", "equip"), BMarker.DEFAULT);

    home.add("Winterfell", winterfell);
    home.add("Richmond", richmond);
    home.add("AHU2", ahu);
    winterfell.add("equip1", equip1);
    winterfell.add("equip2", equip2);

    // equip1 points
    BBooleanWritable bw = new BBooleanWritable();
    BEnumWritable ew = new BEnumWritable();
    BStringWritable sw = new BStringWritable();
    BSineWave sine = new BSineWave();
    equip1.add("BooleanWritable", bw);
    equip1.add("EnumWritable", ew);
    equip1.add("StringWritable", sw);
    equip1.add("SineWave", sine);

    BSineWave sine2 = new BSineWave();
    BSineWave sine4 = new BSineWave();
    station.add("SineWave2", sine2);
    station.add("SineWave4", sine4);

    // equip2 points
    BSineWave e2sine2 = new BSineWave();
    BSineWave e2sine3 = new BSineWave();
    equip2.add("SineWave2", e2sine2);
    equip2.add("SineWave3", e2sine3);

    // ahu points
    BNumericWritable nm = new BNumericWritable();
    ahu.add("NumericWritable", nm);

    // lonely point
    BSineWave sine5 = new BSineWave();
    station.add("SineWave5", sine5);
    sine5.setFacets(BFacets.makeNumeric(BUnit.getUnit("fahrenheit"), 2));
    sine5.tags().set(Id.newId("foo"), BMarker.DEFAULT);
    sine5.tags().set(Id.newId("ph", "bar"), BMarker.DEFAULT);
    sine5.tags().set(Id.newId("foo"), BMarker.DEFAULT);

    // add histories
    addNumericPointHis(sine);
    addNumericPointHis(sine2);
    addNumericPointHis(sine4);
    addNumericPointHis(e2sine2);
    addNumericPointHis(e2sine3);
    addNumericPointHis(nm);
    addNumericPointHis(sine5);
    addBooleanPointHis(station, bw);

    // relations
    addSiteRefRelation(equip1, winterfell);
    addSiteRefRelation(equip2, winterfell);
    addSiteRefRelation(ahu, richmond);
    addSiteRefRelation(bw, winterfell);
    addSiteRefRelation(ew, winterfell);
    addSiteRefRelation(sw, winterfell);
    addSiteRefRelation(sine, winterfell);
    addSiteRefRelation(sine2, winterfell);
    addSiteRefRelation(sine4, winterfell);
    addSiteRefRelation(e2sine2, winterfell);
    addSiteRefRelation(e2sine3, winterfell);

    addEquipRefRelation(bw, equip1);
    addEquipRefRelation(ew, equip1);
    addEquipRefRelation(sw, equip1);
    addEquipRefRelation(sine, equip1);
    addEquipRefRelation(sine2, equip1);
    addEquipRefRelation(sine4, equip1);

    addEquipRefRelation(e2sine2, equip2);
    addEquipRefRelation(e2sine3, equip2);

    addEquipRefRelation(nm, ahu);

    rebuildCache();
  }

  @Test
  public void verifyAuth()
  {
    // get bad credentials
    try
    {
      HClient.open(LOCAL_URI, INVALID_USER, INVALID_PASS).about();
      fail();
    }
    catch (Exception e)
    {
      Assert.assertTrue(true);
    }

    try
    {
      HClient.open(LOCAL_URI, super.getSuperUsername(), INVALID_PASS).about();
      fail();
    }
    catch (CallException e)
    {
      Assert.assertTrue(true);
    }

    try
    {
      this.client = openClient(false);
      client.about();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void verifyAbout() throws Exception
  {
    // non-secure
    this.client = openClient(false);
    HDict r = client.about();
    Assert.assertEquals(r.getStr("haystackVersion"), "2.0");
    Assert.assertEquals(r.getStr("productName"), "Niagara 4");
    Assert.assertEquals(r.getStr("productVersion"), "4.11.0.142");
    Assert.assertEquals(r.getStr("moduleVersion"), "3.2.0");
  }

  @Test
  public void verifyOps() throws Exception
  {
    this.client = openClient(false);
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

  @Test
  public void verifyFormats() throws Exception
  {
    this.client = openClient(false);
    HGrid g = client.formats();

    // verify required columns
    Assert.assertTrue(g.col("mime") != null);
    Assert.assertTrue(g.col("read") != null);
    Assert.assertTrue(g.col("write") != null);

    // verify required ops
    verifyGridContains(g, "mime", HStr.make("text/plain"));
    verifyGridContains(g, "mime", HStr.make("text/zinc"));
  }

  @Test
  public void verifyRead() throws Exception
  {
    this.client = openClient(false);
    HDict dict;
    HGrid grid = client.readAll("id");

    // do checks
    printBasicGrid(grid);
    Assert.assertEquals(grid.numRows(), 17);
    Assert.assertEquals(grid.row(0).id(), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(1).id(), HRef.make("S.Winterfell.equip1"));
    Assert.assertEquals(grid.row(2).id(), HRef.make("S.Winterfell.equip1.BooleanWritable"));
    Assert.assertEquals(grid.row(3).id(), HRef.make("S.Winterfell.equip1.EnumWritable"));
    Assert.assertEquals(grid.row(4).id(), HRef.make("S.Winterfell.equip1.StringWritable"));
    Assert.assertEquals(grid.row(5).id(), HRef.make("S.Winterfell.equip1.SineWave"));
    Assert.assertTrue(grid.row(5).has("n4SlotPath"));
//    printFullGrid(grid);
    Assert.assertEquals(grid.row(6).id(), HRef.make("S.Winterfell.equip2"));
    Assert.assertEquals(grid.row(7).id(), HRef.make("S.Winterfell.equip2.SineWave2"));
    Assert.assertEquals(grid.row(8).id(), HRef.make("S.Winterfell.equip2.SineWave3"));
    Assert.assertEquals(grid.row(9).id(), HRef.make("S.Richmond"));
    Assert.assertEquals(grid.row(10).id(), HRef.make("S.Richmond.AHU2"));
    Assert.assertEquals(grid.row(11).id(), HRef.make("S.Richmond.AHU2.NumericWritable"));
    Assert.assertEquals(grid.row(12).id(), HRef.make("S.Winterfell.equip1.SineWave2"));
    Assert.assertEquals(grid.row(13).id(), HRef.make("S.Winterfell.equip1.SineWave4"));
    Assert.assertEquals(grid.row(14).id(), HRef.make("C.SineWave5"));
    Assert.assertEquals(grid.row(15).id(), HRef.make("C.mv"));
    Assert.assertEquals(grid.row(16).id(), HRef.make("H.test.LogHistory"));

    Assert.assertEquals(grid.row(1).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(2).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(3).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(4).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(5).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(6).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(7).get("siteRef"), HRef.make("S.Winterfell"));
    Assert.assertEquals(grid.row(8).get("siteRef"), HRef.make("S.Winterfell"));

    Assert.assertEquals(grid.row(2).get("equipRef"), HRef.make("S.Winterfell.equip1"));
    Assert.assertEquals(grid.row(3).get("equipRef"), HRef.make("S.Winterfell.equip1"));
    Assert.assertEquals(grid.row(4).get("equipRef"), HRef.make("S.Winterfell.equip1"));
    Assert.assertEquals(grid.row(5).get("equipRef"), HRef.make("S.Winterfell.equip1"));
    Assert.assertEquals(grid.row(7).get("equipRef"), HRef.make("S.Winterfell.equip2"));
    Assert.assertEquals(grid.row(8).get("equipRef"), HRef.make("S.Winterfell.equip2"));
    Assert.assertEquals(grid.row(12).get("equipRef"), HRef.make("S.Winterfell.equip1"));

    dict = client.readById(HRef.make("C.SineWave5"));
    Assert.assertEquals(dict.get("axType"), HStr.make("kitControl:SineWave"));
//        Assert.assertTrue(dict.has("foo"));
//        Assert.assertTrue(dict.has("bar"));
    Assert.assertEquals(dict.get("kind"), HStr.make("Number"));
//        Assert.assertTrue(dict.has("his"));
//        Assert.assertEquals(dict.get("hisInterpolate"), HStr.make("cov"));
    Assert.assertEquals(dict.get("n4SlotPath"), HStr.make("slot:/SineWave5"));
    Assert.assertEquals(dict.get("unit"), HStr.make("°F"));
    Assert.assertTrue(dict.has("point"));
    Assert.assertEquals(dict.get("tz"), localTz());
    Assert.assertTrue(dict.has("cur"));
    double curVal = dict.getDouble("curVal");
    Assert.assertEquals(dict.get("curStatus"), HStr.make("ok"));
    Assert.assertTrue(curVal >= 0.0 && curVal <= 100.0);

    Assert.assertEquals(dict.get("dis"), HStr.make("SineWave5"));
    Assert.assertEquals(dict.get("navName"), HStr.make("SineWave5"));

//        printBasicGrid(grid);
  }

//////////////////////////////////////////////////////////////////////////
// Nav
//////////////////////////////////////////////////////////////////////////

  @Test
  public void verifyNav() throws Exception
  {
    HGrid grid = client.call("read", makeIdGrid(HUri.make("sep:/Winterfell")));
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertEquals(grid.row(0).id(), HRef.make("S.Winterfell"));

    grid = client.call("read", makeIdGrid(HUri.make("sep:/Winterfell/equip1")));
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertEquals(grid.row(0).id(), HRef.make("S.Winterfell.equip1"));

    HGrid n = makeNavGrid(HStr.make("sep:/Winterfell/equip1"));
    grid = client.call("nav", n);
    Assert.assertEquals(grid.numRows(), 6);
    Assert.assertEquals(grid.row(0).id(), HRef.make("S.Winterfell.equip1.BooleanWritable"));
    Assert.assertEquals(grid.row(1).id(), HRef.make("S.Winterfell.equip1.EnumWritable"));
    Assert.assertEquals(grid.row(2).id(), HRef.make("S.Winterfell.equip1.StringWritable"));
    Assert.assertEquals(grid.row(3).id(), HRef.make("S.Winterfell.equip1.SineWave"));
    Assert.assertEquals(grid.row(4).id(), HRef.make("S.Winterfell.equip1.SineWave2"));
    Assert.assertEquals(grid.row(5).id(), HRef.make("S.Winterfell.equip1.SineWave4"));

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
    Assert.assertEquals(grid.row(0).get("navId"), HStr.make("his:/test"));

    n = makeNavGrid(HStr.make("his:/test"));
    grid = client.call("nav", n);
//printBasicGrid(grid);
    Assert.assertEquals(grid.numRows(), 1);

    n = makeNavGrid(HStr.make("slot:/"));
    grid = client.call("nav", n);
//System.out.println("verifyNav- slot:/");
//printBasicGrid(grid);
    Assert.assertEquals(grid.numRows(), 8);
    Assert.assertEquals(grid.row(0).get("navId"), HStr.make("slot:/Services"));
    Assert.assertEquals(grid.row(1).get("navId"), HStr.make("slot:/Drivers"));
//printFullGrid(grid);
    Assert.assertTrue(grid.row(2).missing("navId"));
    Assert.assertEquals(grid.row(3).get("navId"), HStr.make("slot:/home"));
    Assert.assertEquals(grid.row(4).get("navId"), HStr.make("slot:/SineWave2"));
    Assert.assertEquals(grid.row(5).get("navId"), HStr.make("slot:/SineWave4"));
    Assert.assertEquals(grid.row(6).get("navId"), HStr.make("slot:/SineWave5"));
    Assert.assertEquals(grid.row(7).get("navId"), HStr.make("slot:/mv"));
//        Assert.assertEquals(grid.row(7).get("navId"), HStr.make("slot:/SineWave5"));
//        Assert.assertTrue(grid.row(8).missing("navId"));
//        Assert.assertEquals(grid.row(9).get("navId"), HStr.make("slot:/AHU2"));

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
    grid = client.call("nav", makeNavGrid(HStr.make("sep:/")));
    Assert.assertEquals(grid.numRows(), 2);
    Assert.assertEquals(grid.row(1).get("navId"), HStr.make("sep:/Richmond"));
    Assert.assertEquals(grid.row(1).get("dis"), HStr.make("Richmond"));

    grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond")));
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertEquals(grid.row(0).get("navId"), HStr.make("sep:/Richmond/AHU2"));
    Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Richmond AHU2"));

    grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU2")));
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertTrue(grid.row(0).missing("navId"));

    Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Richmond AHU2 NumericWritable"));
// TODO add some more tests in here

    grid = client.call("nav", makeNavGrid(HStr.make("sep:/Richmond/AHU2")));
    Assert.assertEquals(grid.numRows(), 1);
    Assert.assertTrue(grid.row(0).missing("navId"));
    Assert.assertEquals(grid.row(0).get("dis"), HStr.make("Richmond AHU2 NumericWritable"));

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
      {
        traverseComponents((HStr) grid.row(i).get("navId"));
      }
    }
  }

  @Test
  public void verifyHisRead() throws Exception
  {
    HGrid grid = client.readAll("his");
    Assert.assertEquals(grid.numRows(), 9);
//    printBasicGrid(grid);

    ///////////////////////////////////////////////

    HDict dict = client.read("n4SlotPath==\"slot:/home/Winterfell/equip1/SineWave\"");
    HGrid his = client.hisRead(dict.id(), "today");

    Assert.assertEquals(his.meta().id(), dict.id());
    Assert.assertTrue(his.numRows() > 0);

    int last = his.numRows() - 1;
    Assert.assertEquals(ts(his.row(last)).date, HDate.today());

    // TODO
//    Assert.assertEquals(numVal(his.row(0)).unit, "°F");

    his = client.hisRead(dict.id(), "2018-01-01");
//    System.out.println("******************************** " + his.numRows());
//    his.dump();
    Assert.assertTrue(his.isEmpty());

    ///////////////////////////////////////////////

    dict = client.read("n4HistoryId==\"/test/LogHistory\"");
    his = client.hisRead(dict.id(), "today");
    Assert.assertEquals(his.meta().id(), dict.id());
    Assert.assertTrue(his.numRows() > 0);

    last = his.numRows() - 1;
    Assert.assertEquals(ts(his.row(last)).date, HDate.today());

    // test read with no data expected
    his = client.hisRead(dict.id(), "2018-01-01");
//    System.out.println("******************************** " + his.numRows());
//    his.dump();
    Assert.assertTrue(his.isEmpty());

    ///////////////////////////////////////////////

//        dict = client.read("n4HistoryId==\"/nhaystack_simple/SineWave5\"");
//        his = client.hisRead(dict.id(), "today");
//        verifyEq(his.meta().id(), dict.id());

//        ///////////////////////////////////////////////

//    client.hisRead(HRef.make("C.AHU2.NumericWritable"), "today");
//        client.hisRead(HRef.make("S.Richmond.AHU2.NumericWritable"), "today");
  }

////////////////////////////////////////////////////////////////////////////
// Watches
////////////////////////////////////////////////////////////////////////////

//  @Test(enabled = true)
//  void verifyWatches() throws Exception
//  {
//    System.out.println("******** watch test");
//    // create new watch
//    HWatch w = client.watchOpen("NHaystack Simple Test", HNum.make(120, "s"));
//    Assert.assertEquals(w.id(), null);
//    Assert.assertEquals(w.dis(), "NHaystack Simple Test");
//
//    // do query to get some recs
//    HGrid recs = client.readAll("point");
//    Assert.assertTrue(recs.numRows() >= 4);
//    HDict a = recs.row(0);
//    HDict b = recs.row(1);
//    HDict c = recs.row(2);
//    HDict d = recs.row(3);
//
////System.out.println(a);
////System.out.println(b);
////System.out.println(c);
////System.out.println(d);
//
//    // do first sub
//    HGrid sub = w.sub(new HRef[]{a.id(), b.id()});
//    Assert.assertEquals(sub.numRows(), 2);
//    Assert.assertEquals(sub.row(0).id(), a.id());
//    Assert.assertEquals(sub.row(1).id(), b.id());
//
//    // now add c, d
//    sub = w.sub(new HRef[]{c.id(), d.id()}, false);
//    Assert.assertEquals(sub.numRows(), 2);
//    Assert.assertEquals(sub.row(0).id(), c.id());
//    Assert.assertEquals(sub.row(1).id(), d.id());
//
//    // verify state of watch now
//    Assert.assertTrue(client.watch(w.id()) == w);
//    Assert.assertEquals(client.watches().length, 1);
//    Assert.assertTrue(client.watches()[0] == w);
//    Assert.assertEquals(w.lease().millis(), 2L * 60 * 1000);
//
//    // poll refresh
//    HGrid poll = w.pollRefresh();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//    w.pollChanges();
//
//    Runnable r = () ->
//    {
//      int count = 0;
//      while (count < 1000)
//      {
//        System.out.println(Thread.currentThread().getName() + " " + count);
//        w.pollChanges();
//        try
//        {
//          Thread.sleep(100);
//        } catch (InterruptedException e)
//        {
//          e.printStackTrace();
//        }
//        count++;
//      }
//    };
//
//    Thread t = new Thread(r, "thread-1");
//    Thread x = new Thread(r, "thread-2");
//    t.start();
//    x.start();
//    while (t.isAlive() && x.isAlive())
//    {
//      Thread.sleep(1000);
//    }
//
//    System.out.println("******** end watch test");
//    Assert.assertEquals(poll.numRows(), 4);
//    verifyGridContains(poll, "id", a.id());
//    verifyGridContains(poll, "id", b.id());
//    verifyGridContains(poll, "id", c.id());
//    verifyGridContains(poll, "id", d.id());

  // poll changes
//    Thread.sleep(3000); // wait for the sine waves to tick over
//    poll = w.pollChanges();
//    Assert.assertEquals(poll.numRows(), 1);

  // remove d, and then poll refresh
//    w.unsub(new HRef[]{d.id()});
//    poll = w.pollRefresh();
//    Assert.assertEquals(poll.numRows(), 3);

  // close
//    w.close();
//    try
//    {
//      w.pollRefresh();
//      Assert.fail();
//    }
//    catch (Exception e)
//    {
//      verifyEx(e);
//    }
//    Assert.assertEquals(client.watch(w.id(), false), null);
//    Assert.assertEquals(client.watches().length, 0);

  // check bad id
//    w = client.watchOpen("Bogus Test", HNum.make(120, "s"));
//    HRef badId = HRef.make("c." + Base64.URI.encode("badBadBad"));
//    try
//    {
//      w.sub(new HRef[]{badId}).dump();
//      fail();
//    }
//    catch (Exception e)
//    {
//      verifyEx(e);
//    }
//  }

////////////////////////////////////////////////////////////////
// Utils
////////////////////////////////////////////////////////////////

  void verifyGridContains(HGrid g, String col, HVal val)
  {
    boolean found = false;
    for (int i = 0; i < g.numRows(); ++i)
    {
      HVal x = g.row(i).get(col, false);
      if (x != null && x.equals(val))
      {
        found = true;
        break;
      }
    }
    if (!found)
    {
      System.out.println("verifyGridContains " + col + "=" + val + " failed!");
      fail();
    }
  }

  private static void addSiteRefRelation(BComponent source, BComponent site)
  {
    source.relations().add(new BRelation(ID_SITE_REF, site));
  }

  private static void addEquipRefRelation(BControlPoint point, BComponent equip)
  {
    point.relations().add(new BRelation(ID_EQUIP_REF, equip));
  }

  private static void addNumericPointHis(BNumericPoint point)
  {
    BNumericCovHistoryExt cov = new BNumericCovHistoryExt();
    addPointHis(point, cov);
  }

  private static void addBooleanPointHis(BStation station, BBooleanPoint point)
  {
    // setup a data source for a boolean point
    BMultiVibrator mv = new BMultiVibrator();
    station.add("mv?", mv);
    point.linkTo(mv, BMultiVibrator.out, BBooleanWritable.in10);

    BBooleanCovHistoryExt ext = new BBooleanCovHistoryExt();
    addPointHis(point, ext);
  }

  private static void addPointHis(BControlPoint point, BHistoryExt ext)
  {
    point.add("histExt", ext);
    ext.setHistoryName(BFormat.make("%parent.name%"));
    ext.setEnabled(true);
  }


  protected void rebuildCache()
  {
    try
    {
      new BNHaystackRebuildCacheJob(nhaystackService).run(null);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Print a full grid
   *
   * @param grid
   */
  void printFullGrid(HGrid grid)
  {
    int row = 0;
    for (Iterator i = grid.iterator(); i.hasNext(); )
    {
      HRow r = (HRow) i.next();
      System.out.print(row + ", " + r.toString());
      System.out.println();
      row++;
    }
  }

  /**
   * Convenience to print and format a basic grid nicely for debugging
   *
   * @param grid
   */
  void printBasicGrid(HGrid grid)
  {
    int row = 0;
    for (Iterator i = grid.iterator(); i.hasNext(); )
    {
      HRow r = (HRow) i.next();
      System.out.printf("%2s%s", row, ", ");
      System.out.printf("%-40s ", r.id());
      if (r.has("equipRef"))
      {
        System.out.printf("%-30s", r.get("equipRef"));
      }
      else
      {
        System.out.printf("%-30s", "");
      }

      if (r.has("kind"))
      {
        System.out.printf("%-10s", r.get("kind"));
      }

      System.out.println();
      row++;
    }
  }

  static HGrid makeIdGrid(HVal id)
  {
    HDictBuilder hd = new HDictBuilder();
    hd.add("id", id);
    return HGridBuilder.dictsToGrid(new HDict[]{hd.toDict()});
  }

  static HGrid makeNavGrid(HStr navId)
  {
    HDictBuilder hd = new HDictBuilder();
    hd.add("navId", navId);
    return HGridBuilder.dictsToGrid(new HDict[]{hd.toDict()});
  }

  HDateTime ts(HDict r, String col)
  {
    return (HDateTime) r.get(col);
  }

  HDateTime ts(HDict r)
  {
    return (HDateTime) r.get("ts");
  }

  HNum numVal(HRow r)
  {
    return (HNum) r.get("val");
  }

  HStr localTz()
  {
    return HStr.make(HTimeZone.DEFAULT.name);
  }
}
