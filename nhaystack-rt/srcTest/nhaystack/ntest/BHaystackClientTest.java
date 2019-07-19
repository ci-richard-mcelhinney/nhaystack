//
// Copyright 2017 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   05 Dec 2017  Rowyn Brunner    Creation
//   31 Oct 2018  Andrew Saunders  Added Haystack tag dictionary import asserts
//   19 Jul 2019  Eric Anderson    Moved rebuildCache method to super class
//
package nhaystack.ntest;

import static nhaystack.ntest.helper.NHaystackTestUtil.AIR_ID;
import static nhaystack.ntest.helper.NHaystackTestUtil.CUR_VAL_TAG_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.DISCHARGE_ID;
import static nhaystack.ntest.helper.NHaystackTestUtil.EQUIP_SLOT_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.HAYSTACK_SLOT_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.NAV_ID_TAG_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.SENSOR_ID;
import static nhaystack.ntest.helper.NHaystackTestUtil.TEMP_ID;
import static nhaystack.ntest.helper.NHaystackTestUtil.WRITE_LEVEL_TAG_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.WRITE_VAL_TAG_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.assertRowDis;
import static nhaystack.ntest.helper.NHaystackTestUtil.assertRowIds;
import static nhaystack.ntest.helper.NHaystackTestUtil.assertRowNavIds;
import static nhaystack.ntest.helper.NHaystackTestUtil.makeIdGrid;
import static nhaystack.ntest.helper.NHaystackTestUtil.makeNavGrid;
import static nhaystack.ntest.helper.NHaystackTestUtil.rowHasEquipRef;
import static nhaystack.ntest.helper.NHaystackTestUtil.rowHasSiteRef;
import static nhaystack.util.NHaystackConst.ID_SITE_REF;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import javax.baja.control.BBooleanWritable;
import javax.baja.control.BEnumWritable;
import javax.baja.control.BNumericPoint;
import javax.baja.control.BNumericWritable;
import javax.baja.control.BStringWritable;
import javax.baja.history.ext.BNumericCovHistoryExt;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComplex;
import javax.baja.sys.BComponent;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.BMarker;
import javax.baja.sys.BRelation;
import javax.baja.sys.BStation;
import javax.baja.sys.BString;
import javax.baja.sys.Clock;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Entity;
import javax.baja.tag.Id;
import javax.baja.tag.Relation;
import javax.baja.tag.Tags;
import javax.baja.units.BUnit;
import javax.baja.util.BFolder;

import junit.extensions.PA;
import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.server.BNHaystackConvertHaystackSlotsJob;
import nhaystack.server.BNHaystackRebuildCacheJob;
import nhaystack.server.BNHaystackService;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
import org.projecthaystack.HBool;
import org.projecthaystack.HCol;
import org.projecthaystack.HCoord;
import org.projecthaystack.HDate;
import org.projecthaystack.HDateTime;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HMarker;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HStr;
import org.projecthaystack.HTimeZone;
import org.projecthaystack.HVal;
import org.projecthaystack.client.CallErrException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.tridium.history.log.BLogHistoryService;
import com.tridium.kitControl.util.BSineWave;

@NiagaraType
@Test(singleThreaded = true)
@SuppressWarnings("MagicNumber")
public class BHaystackClientTest extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.test.BReplaceHaystackSlotStationTest(2979906276)1.0$ @*/
/* Generated Tue Dec 05 13:55:27 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
    
    @Override
    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHaystackClientTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort) throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        this.station = station;
        station.getServices().add("LogHistoryService", new BLogHistoryService());
    }

    @BeforeTest
    @Override
    public void setupStation() throws Exception
    {
        super.setupStation();

        client = openClient(false);
    }

    @BeforeMethod
    public void beforeMethod()
    {
        // build up nHaystack site - equipment - point structures.
        // * station root
        //   * Playground (Folder)
        //     * SiteA (HSite)
        //       * EquipA (Folder)
        //         * equip (HEquip) (-- hs:siteRef --> SiteA)
        //           * boolPoint (BooleanWritable)
        //           * enumPoint (EnumWritable)
        //           * strPoint (StringWritable)
        //           * sineWave3 (SineWave)
        //           * sineWave4 (SineWave)
        //         * point1 (NumericWritable)
        //         * sineWave1 (SineWave)
        //           * NumericCov (NumericCovHistoryExt)
        //         * sineWave2 (SineWave)
        //     * SiteB (HSite)
        //       * EquipA (Folder)
        //         * equip (HEquip) (-- hs:siteRef --> SiteB)
        //   * testFolder (Folder)
        //   * testFolder2 (Folder)
        //     * MottsForest (HSite)
        //     * HeatPump
        //       * equip
        //     * Points
        //       * hp1
        //       * hp2
        //       * hp3

        playgroundFolder = new BFolder();
        replace("Playground", playgroundFolder);

        testFolder = new BFolder();
        replace("testFolder", testFolder);

        testFolder2 = new BFolder();
        replace("testFolder2", testFolder2);

        // Playground
        siteA = new BHSite();
        siteB = new BHSite();
        BFolder siteAEquipAFolder = new BFolder();
        BFolder siteBEquipAFolder = new BFolder();
        siteAequipA = new BHEquip();
        BHEquip siteBEquipAEquip = new BHEquip();
        BEnumWritable enumPoint = new BEnumWritable();
        BSineWave sineWave1 = new BSineWave();
        BNumericCovHistoryExt histExt = new BNumericCovHistoryExt();

        siteA.tags().set(Id.newId("hs", "geoCoord"), BString.make("C(1.11,2.22)"));
        addSiteRefRelation(siteAequipA, siteA);
        addSiteRefRelation(siteBEquipAEquip, siteB);

        // initialize enum point facets
        String[] enumTags = Arrays.stream(STR_ARRAY).map(HStr::toString).toArray(String[]::new);
        enumPoint.setFacets(BFacets.makeEnum(BEnumRange.make(enumTags)));

        sineWave1.setFacets(BFacets.makeNumeric(BUnit.getUnit("second"), 3, -50, 50));
        histExt.setEnabled(true);

        playgroundFolder.add("SiteA", siteA);
        playgroundFolder.add(SITE_B, siteB);

        siteA.add(EQUIP_A, siteAEquipAFolder);

        siteB.add(EQUIP_A, siteBEquipAFolder);

        siteAEquipAFolder.add(EQUIP_SLOT_NAME, siteAequipA);
        siteAEquipAFolder.add("point1", new BNumericWritable());
        siteAEquipAFolder.add("sineWave1", sineWave1);
        siteAEquipAFolder.add("sineWave2", new BSineWave());

        siteBEquipAFolder.add(EQUIP_SLOT_NAME, siteBEquipAEquip);

        siteAequipA.add("boolPoint", new BBooleanWritable());
        siteAequipA.add("enumPoint", enumPoint);
        siteAequipA.add("strPoint", new BStringWritable());
        siteAequipA.add("sineWave3", new BSineWave());
        siteAequipA.add("sineWave4", new BSineWave());

        sineWave1.add("NumericCov", histExt);

        // testFolder2
        heatPumpFolder = new BFolder();
        BFolder points = new BFolder();
        BHSite mottsForest = new BHSite();
        heatPumpEquip = new BHEquip();

        addSiteRefRelation(heatPumpEquip, mottsForest);

        testFolder2.add("MottsForest", mottsForest);
        testFolder2.add("HeatPump", heatPumpFolder);
        testFolder2.add("Points", points);

        heatPumpFolder.add(EQUIP_SLOT_NAME, heatPumpEquip);

        points.add("hp1", new BNumericPoint());
        points.add("hp2", new BNumericPoint());
        points.add("hp3", new BNumericPoint());

        rebuildCache();
    }

    private void replace(String name, BComponent component)
    {
        if (station.get(name) != null)
        {
            station.remove(name);
        }
        station.add(name, component);
    }

    private static boolean isSlotConversionInProgress(BNHaystackService haystackService)
    {
        return (boolean)PA.getValue(haystackService, "slotConversionInProgress");
    }

    public void testConversionInitLockout() throws InterruptedException
    {
        assertFalse(isSlotConversionInProgress(nhaystackService));
        final BOrd bOrd = nhaystackService.convertHaystackSlots();
        final BNHaystackConvertHaystackSlotsJob job = (BNHaystackConvertHaystackSlotsJob) bOrd.resolve(nhaystackService).get();
        // spin until convert job is in process
        long startTicks = Clock.ticks();
        while(!isSlotConversionInProgress(nhaystackService))
        {
            Thread.yield();
            if (Clock.ticks() - startTicks > 5000)
            {
                break;
            }
        }

        // now try to initialize haystack while job is in process should throw an exception.
        try
        {
            nhaystackService.doInitializeHaystack();
            Assert.fail("Expected RuntimeException but didn't get one.");

        }
        catch (RuntimeException ignore)
        {
        }

        startTicks = Clock.ticks();
        while (job.isAlive())
        {
            Thread.yield();
            if (Clock.ticks() - startTicks > 5000)
            {
                break;
            }
        }

        // provide some time for the convert job's initializeHaystack to complete.
        Thread.sleep(3000L);
    }

    public void testAbout() throws InterruptedException
    {
        HDict r = client.about();
        assertEquals(r.getStr("haystackVersion"), "2.0");
    }

    public void testOps() throws InterruptedException
    {
        HGrid grid = client.ops();

        gridContainsColumns(grid, "name", "summary");

        Collection<HStr> actual = gatherColumn(grid, "name");
        verifyColumnContains(actual,
            "about",
            "ops",
            "formats",
            "read",
            "nav",
            "watchSub",
            "watchUnsub",
            "watchPoll",
            "pointWrite",
            "hisRead",
            "hisWrite",
            "invokeAction",
            "extendedRead", // TODO non-standard
            EXTENDED_OP_NAME, // non-standard
            "alarmAck");    // TODO non-standard
    }

    public void testFormats() throws InterruptedException
    {
        HGrid grid = client.formats();

        gridContainsColumns(grid, "mime", "read", "write");

        Collection<HStr> actual = gatherColumn(grid, "mime");
        verifyColumnContains(actual,
            "text/zinc",
            "application/json",
            "text/csv",
            "text/plain");
    }

    public void testReadAll() throws InterruptedException
    {
        HGrid grid = client.readAll("id");

        assertRowIds(grid,
            "S.SiteA",
            "S.SiteA.EquipA",
            "S.SiteA.EquipA.boolPoint",
            "S.SiteA.EquipA.enumPoint",
            "S.SiteA.EquipA.strPoint",
            "S.SiteA.EquipA.sineWave3",
            "S.SiteA.EquipA.sineWave4",
            "S.SiteA.EquipA.point1",
            "S.SiteA.EquipA.sineWave1",
            "S.SiteA.EquipA.sineWave2",
            "S.SiteB",
            "S.SiteB.EquipA",
            "S.MottsForest",
            "S.MottsForest.HeatPump",
            "C.testFolder2.Points.hp1",
            "C.testFolder2.Points.hp2",
            "C.testFolder2.Points.hp3",
            "H.test.LogHistory",
            "H.test.sineWave1");

        rowHasSiteRef(grid.row(1), "S.SiteA");
        rowHasSiteRef(grid.row(2), "S.SiteA");
        rowHasSiteRef(grid.row(3), "S.SiteA");
        rowHasSiteRef(grid.row(4), "S.SiteA");
        rowHasSiteRef(grid.row(5), "S.SiteA");
        rowHasSiteRef(grid.row(6), "S.SiteA");
        rowHasSiteRef(grid.row(7), "S.SiteA");
        rowHasSiteRef(grid.row(8), "S.SiteA");
        rowHasSiteRef(grid.row(9), "S.SiteA");
        rowHasSiteRef(grid.row(11), "S.SiteB");
        rowHasSiteRef(grid.row(13), "S.MottsForest");

        assertEquals(grid.row(18).get("axPointRef"), HRef
            .make("S.SiteA.EquipA.sineWave1"));

        rowHasEquipRef(grid.row(2), "S.SiteA.EquipA");
        rowHasEquipRef(grid.row(3), "S.SiteA.EquipA");
        rowHasEquipRef(grid.row(4), "S.SiteA.EquipA");
        rowHasEquipRef(grid.row(5), "S.SiteA.EquipA");
        rowHasEquipRef(grid.row(6), "S.SiteA.EquipA");
        rowHasEquipRef(grid.row(7), "S.SiteA.EquipA");
        rowHasEquipRef(grid.row(8), "S.SiteA.EquipA");
        rowHasEquipRef(grid.row(9), "S.SiteA.EquipA");
    }

    public void testReadById() throws InterruptedException
    {
        HDict dict = client.readById(HRef.make("C.Playground.SiteA.EquipA.sineWave1"));

        assertEquals(dict.get("axType"), HStr.make("kitControl:SineWave"));
        assertEquals(dict.get("kind"), HStr.make("Number"));
        assertTrue(dict.has("his"));
        assertEquals(dict.get("hisInterpolate"), HStr.make("cov"));
        assertEquals(dict.get("axSlotPath"), HStr.make("slot:/Playground/SiteA/EquipA/sineWave1"));
        assertEquals(dict.get("unit"), HStr.make("sec"));
        assertEquals(dict.get("precision"), HNum.make(3));
        assertEquals(dict.get("minVal"), HNum.make(-50));
        assertEquals(dict.get("maxVal"), HNum.make(50));
        assertTrue(dict.has("point"));
        assertEquals(dict.get("tz"), localTz());
        assertTrue(dict.has("cur"));
        double curVal = dict.getDouble(CUR_VAL_TAG_NAME);
        assertEquals(dict.get("curStatus"), HStr.make("ok"));
        assertTrue(curVal >= 0.0 && curVal <= 100.0);
    }

    public void testNav() throws Exception
    {
        HGrid grid = client.call("read", makeIdGrid("sep:/SiteA"));
        assertRowIds(grid, "S.SiteA");

        grid = client.call("read", makeIdGrid("sep:/SiteA/EquipA"));
        assertRowIds(grid, "S.SiteA.EquipA");

        grid = client.call("nav", makeNavGrid("sep:/SiteA/EquipA"));
        assertRowIds(grid,
            "S.SiteA.EquipA.boolPoint",
            "S.SiteA.EquipA.enumPoint",
            "S.SiteA.EquipA.strPoint",
            "S.SiteA.EquipA.sineWave3",
            "S.SiteA.EquipA.sineWave4",
            "S.SiteA.EquipA.point1",
            "S.SiteA.EquipA.sineWave1",
            "S.SiteA.EquipA.sineWave2");

        grid = client.call("nav", HGrid.EMPTY);
        assertRowNavIds(grid, "slot:/", "his:/", "sep:/");
        assertRowDis(grid, "ComponentSpace", "HistorySpace", "Site");

        grid = client.call("nav", makeNavGrid("his:/"));
        assertRowNavIds(grid, "his:/test");

        grid = client.call("nav", makeNavGrid("his:/test"));
        assertEquals(grid.numRows(), 2);

        grid = client.call("nav", makeNavGrid("slot:/"));
        assertEquals(grid.numRows(), 6);
        assertEquals(grid.row(0).get(NAV_ID_TAG_NAME), HStr.make("slot:/Services"));
        assertEquals(grid.row(1).get(NAV_ID_TAG_NAME), HStr.make("slot:/Drivers"));
        assertTrue(grid.row(2).missing(NAV_ID_TAG_NAME));
        assertEquals(grid.row(3).get(NAV_ID_TAG_NAME), HStr.make("slot:/Playground"));

        grid = client.call("nav", makeNavGrid("sep:/"));
        assertRowNavIds(grid, "sep:/SiteA", "sep:/SiteB", "sep:/MottsForest");
        assertRowDis(grid, "SiteA", SITE_B, "MottsForest");

        grid = client.call("nav", makeNavGrid("sep:/SiteA"));
        assertRowNavIds(grid, "sep:/SiteA/EquipA");
        assertRowDis(grid, EQUIP_A);

        grid = client.call("nav", makeNavGrid("sep:/SiteA/EquipA"));
        assertEquals(grid.numRows(), 8);
        assertTrue(grid.row(0).missing(NAV_ID_TAG_NAME));
        assertTrue(grid.row(1).missing(NAV_ID_TAG_NAME));
        assertTrue(grid.row(2).missing(NAV_ID_TAG_NAME));
        assertTrue(grid.row(3).missing(NAV_ID_TAG_NAME));
        assertTrue(grid.row(4).missing(NAV_ID_TAG_NAME));
        assertRowDis(grid,
            "SiteA EquipA boolPoint",
            "SiteA EquipA enumPoint",
            "SiteA EquipA strPoint",
            "SiteA EquipA sineWave3",
            "SiteA EquipA sineWave4",
            "SiteA EquipA point1",
            "SiteA EquipA sineWave1",
            "SiteA EquipA sineWave2");

        grid = client.call("nav", makeNavGrid("sep:/SiteB/EquipA"));
        assertEquals(grid.numRows(), 0);
    }

    public void testHisRead() throws InterruptedException
    {
        HGrid grid = client.readAll("his");
        assertEquals(grid.numRows(), 3);

        HDict dict = client.read("axSlotPath==\"slot:/Playground/SiteA/EquipA/sineWave1\"");
        HGrid his = client.hisRead(dict.id(), "today");

        assertEquals(his.meta().id(), dict.id());
        assertTrue(his.numRows() > 0);

        int last = his.numRows() - 1;
        assertEquals(ts(his.row(last)).date, HDate.today());

        //TODO there is an issue with units here that needs to be solved
//    Assert.assertEquals(numVal(his.row(0)).unit, "\\uxxB0" + "F");

        ///////////////////////////////////////////////

        dict = client.read("axHistoryId==\"/test/LogHistory\"");
        his = client.hisRead(dict.id(), "today");
        assertEquals(his.meta().id(), dict.id());
        assertTrue(his.numRows() > 0);

        last = his.numRows() - 1;
        assertEquals(ts(his.row(last)).date, HDate.today());

        ///////////////////////////////////////////////

        dict = client.read("axHistoryId==\"/test/sineWave1\"");
        his = client.hisRead(dict.id(), "today");
        assertEquals(his.meta().id(), dict.id());

        ///////////////////////////////////////////////

        client.hisRead(HRef.make("C.Playground.SiteA.EquipA.sineWave1"), "today");
        client.hisRead(HRef.make("S.SiteA.EquipA.sineWave1"), "today");
    }

    public void testPointWrite() throws InterruptedException
    {
        doVerifyPointWrite(HRef.make("S.SiteA.EquipA.point1"), NUM_ARRAY, HNum.make(0));
        doVerifyPointWrite(HRef.make("S.SiteA.EquipA.boolPoint"), BOOL_ARRAY, HBool.FALSE);
        doVerifyPointWrite(HRef.make("S.SiteA.EquipA.strPoint"), STR_ARRAY, HStr.make(""));
        doVerifyPointWrite(HRef.make("S.SiteA.EquipA.enumPoint"), STR_ARRAY, HStr.make("a17"));
    }

    private void doVerifyPointWrite(HRef id, HVal[] arrayVals, HVal endDefaultVal) throws InterruptedException
    {
        // set the default value
        HDictBuilder hd = new HDictBuilder();

        // first verify that curVal is not present
        HDict hDict = client.readById(id);
        assertFalse(hDict.has(CUR_VAL_TAG_NAME));

        hd.add("arg", arrayVals[16]);

        // first set the point's default value to false.
        client.invokeAction(id, "set", hd.toDict());

        HGrid wrArray = null;
        String who = null;
        // verify that a null who throws an exception
        try
        {
            wrArray = client.pointWrite(id, 1, who, arrayVals[16], null);
        }
        catch (CallErrException une)
        {
            assertTrue(true, "pointWrite CallErrException: " + une);
        }
        catch (Exception e)
        {
            Assert.fail("pointWrite exception: " + e);
        }

        who = "admin";
        // write a value to all levels
        for (int i = 1; i < 17; ++i)
        {
            wrArray = client.pointWrite(id, i, who, arrayVals[i-1], null);
        }

        // very value at each level
        assertEquals(wrArray.numRows(), 17);
        for (int i = 0; i < 17; i++)
        {
            assertEquals(wrArray.row(i).getInt("level"), i + 1, "level expected");
            assertEquals(wrArray.row(i).get("val"), arrayVals[i], "value expected");
            assertTrue(wrArray.row(i).missing("who"));
        }

        // auto levels and verify values
        for (int i = 1; i < 17; i++)
        {
            hDict = client.readById(id);
            assertEquals(hDict.get(CUR_VAL_TAG_NAME), arrayVals[i-1], "Out value not correct.");
            wrArray = client.pointWrite(id, i, "admin", null, null);
            Thread.sleep(100);
        }

        // assertTrue all levels are null-ed out.
        assertEquals(wrArray.numRows(), 17);
        for (int i = 0; i < 16; i++)
        {
            assertEquals(wrArray.row(i).getInt("level"), i + 1);
            assertTrue(wrArray.row(i).missing("val"), "val null check: " + i);
            assertTrue(wrArray.row(i).missing("who"), "who null check: " + i);
        }

        hDict = client.readById(id);
        assertEquals(hDict.get(CUR_VAL_TAG_NAME), arrayVals[16], "Out value not correct.");

        // reset point to known state before next test
        hd = new HDictBuilder();
        hd.add("arg", endDefaultVal);
        client.invokeAction(id, "set", hd.toDict());
        Thread.sleep(100);
        hDict = client.readById(id);
        assertEquals(hDict.get(CUR_VAL_TAG_NAME), endDefaultVal);
    }

    public void testInvokeAction() throws InterruptedException
    {
        HRef id = HRef.make("S.SiteA.EquipA.point1");

        HDictBuilder hd = new HDictBuilder();

        // first set the point's default value to 0.
        hd.add("arg", HNum.make(0));
        client.invokeAction(id, "set", hd.toDict());
        Thread.sleep(100);

        // now set emergency level to 999
        hd.add("arg", HNum.make(999));
        client.invokeAction(id, "emergencyOverride", hd.toDict());
        Thread.sleep(100);

        HDict hDict = client.readById(id);
        assertEquals(hDict.get(CUR_VAL_TAG_NAME), HNum.make(999), "Check curVAl");
        assertEquals(hDict.get(WRITE_LEVEL_TAG_NAME), HNum.make(1), "Check writeLevel");
        assertEquals(hDict.get(WRITE_VAL_TAG_NAME), HNum.make(999), "Check writeVal HNum");

        client.invokeAction(id, "emergencyAuto", HDict.EMPTY);
        Thread.sleep(100);

        hDict = client.readById(id);
        assertEquals(hDict.get(CUR_VAL_TAG_NAME), HNum.make(0));
        assertEquals(hDict.get(WRITE_LEVEL_TAG_NAME), HNum.make(17));
        assertEquals(hDict.get(WRITE_VAL_TAG_NAME), HNum.make(0), "Check writeVal HNum");
    }

    @Test(dependsOnMethods = "testReadAll")
    public void testGeoCoord() throws Exception
    {
        HDict hDict = client.readById(HRef.make("S.SiteA"));
        final HVal geoCoord = hDict.get("geoCoord");
        assertTrue(geoCoord instanceof HCoord, "geoCoord instance of HCoord");
        assertEquals(geoCoord.toString(), "C(1.11,2.22)");
    }

////////////////////////////////////////////////////////////////////////////
//// Extended ops
////////////////////////////////////////////////////////////////////////////

    /*
    * These are the write function extended Ops
    *   addHaystackSlots   : testAddRemoveHaystackSlot
    *   addEquips          : testAddEquips
    *   applyBatchTags     : testApplyBatchTags
    *   copyEquipTags      : testCopyEquipTags
    *   delete             : no test as it just deletes the haystack slot
    *   deleteHaystackSlot : testAddRemoveHaystackSlot
    *   searchAndReplace   : no test as it only renames components
    *   mapPointsToEquip   : testMapPointsToEquip
    *   makeDynamicWritable: no test, not sure what it actually does.  Looks for dynamic action and
    *                      : adds haystack slot with writable tag.
    *   applyGridTags      : testApplyGridTags
    */

    // TODO
    private void testDelete()
    {
    }

    public void testMapPointsToEquip()
    {
        // build request
        HDictBuilder hd = new HDictBuilder();
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("mapPointsToEquip"));
        hd.add("siteNavName", HStr.make("MottsForest"));
        hd.add("equipNavName", HStr.make("HeatPump"));
        String sb = "[C.testFolder2.Points.hp1,C.testFolder2.Points.hp2,C.testFolder2.Points.hp3]";
        hd.add("ids", HStr.make(sb));

        // send request to map points to equip
        client.call(EXTENDED_OP_NAME, HGridBuilder.dictToGrid(hd.toDict()));
        rebuildCache();

        HGrid grid = client.call("read", makeIdGrid("sep:/MottsForest"));
        assertRowIds(grid, "S.MottsForest");

        grid = client.call("read", makeIdGrid("sep:/MottsForest/HeatPump"));
        assertRowIds(grid, "S.MottsForest.HeatPump");

        grid = client.call("nav", makeNavGrid("sep:/MottsForest/HeatPump"));
        assertRowIds(grid,
            "S.MottsForest.HeatPump.hp1",
            "S.MottsForest.HeatPump.hp2",
            "S.MottsForest.HeatPump.hp3");
    }

    public void testApplyGridTags()
    {
        // create a test target.
        testFolder2.add("testComp", new BComponent());
        HDict[] reqDicts = new HDict[2];
        HDictBuilder hd = new HDictBuilder();
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("applyGridTags"));
        reqDicts[0] = hd.toDict();
        //
        hd.add("id", HRef.make("C.testFolder2.testComp"));
        hd.add("active", HMarker.VAL);
        hd.add("power", HMarker.VAL);
        hd.add("phase", HStr.make("A"));
        hd.add("sensor",HMarker.VAL);
        reqDicts[1] = hd.toDict();
        HGrid arguments = HGridBuilder.dictsToGrid(reqDicts);

        BComponent testComp = testFolder2.get("testComp").asComponent();
        // verify that these tags aren't present
        assertFalse(testComp.tags().contains(Id.newId("hs:active")));
        assertFalse(testComp.tags().contains(Id.newId("hs:power")));
        assertFalse(testComp.tags().contains(Id.newId("hs:phase")));
        assertFalse(testComp.tags().contains(SENSOR_ID));

        client.call(EXTENDED_OP_NAME, arguments);

        testComp.lease();
        assertTrue(testComp.tags().contains(Id.newId("hs:active")));
        assertTrue(testComp.tags().contains(Id.newId("hs:power")));
        assertTrue(testComp.tags().contains(Id.newId("hs:phase")));
        assertTrue(testComp.tags().contains(SENSOR_ID));
    }

    public void testCopyEquipTags()
    {
        HStr id = HStr.make("C.testFolder");
        BHEquip equip = new BHEquip();
        Tags equipTags = equip.tags();
        equipTags.set(DISCHARGE_ID, BMarker.MARKER);
        equipTags.set(AIR_ID, BMarker.MARKER);
        equipTags.set(TEMP_ID, BMarker.MARKER);
        equipTags.set(SENSOR_ID, BMarker.MARKER);
        testFolder.add("equip", equip);

        Tags toEquipTags = heatPumpEquip.tags();
        assertFalse(toEquipTags.contains(DISCHARGE_ID));
        assertFalse(toEquipTags.contains(AIR_ID));
        assertFalse(toEquipTags.contains(TEMP_ID));
        assertFalse(toEquipTags.contains(SENSOR_ID));

        HDictBuilder hd = new HDictBuilder();
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("copyEquipTags"));
        hd.add("ids", id);
        hd.add("fromEquip", HStr.make("C.testFolder.equip"));
        hd.add("toEquips", HStr.make("[C.testFolder2.HeatPump]"));
        hd.add(TARGET_FILTER_OP_ARG_NAME, HStr.make("id"));
        client.call(EXTENDED_OP_NAME, HGridBuilder.dictToGrid(hd.toDict()));

        // now assertTrue that it is now there.
        toEquipTags = heatPumpEquip.tags();
        assertTrue(toEquipTags.contains(DISCHARGE_ID));
        assertTrue(toEquipTags.contains(AIR_ID));
        assertTrue(toEquipTags.contains(TEMP_ID));
        assertTrue(toEquipTags.contains(SENSOR_ID));

        //now remove equip from testFolder2 and copyEquipTags again.
        //it should add the BHEquip object again and copy the tags.
        heatPumpFolder.remove(heatPumpEquip);

        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("copyEquipTags"));
        hd.add("ids", id);
        hd.add("fromEquip", HStr.make("C.testFolder.equip"));
        hd.add("toEquips", HStr.make("[C.testFolder2.HeatPump]"));
        hd.add(TARGET_FILTER_OP_ARG_NAME, HStr.make("id"));
        client.call(EXTENDED_OP_NAME, HGridBuilder.dictToGrid(hd.toDict()));

        // now assertTrue that it is now there.
        BComponent toEquip = heatPumpFolder.get(EQUIP_SLOT_NAME).asComponent();
        toEquipTags = toEquip.tags();
        assertTrue(toEquipTags.contains(DISCHARGE_ID));
        assertTrue(toEquipTags.contains(AIR_ID));
        assertTrue(toEquipTags.contains(TEMP_ID));
        assertTrue(toEquipTags.contains(SENSOR_ID));
    }

    public void testApplyBatchTags()
    {
        HStr id = HStr.make("[C.testFolder.equip]");
        BHEquip equip = new BHEquip();
        testFolder.add("equip", equip);

        HDictBuilder hd = new HDictBuilder();
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("applyBatchTags"));
        hd.add("ids", id);
        hd.add("tags", HStr.make("{discharge air temp sensor}"));
        hd.add(TARGET_FILTER_OP_ARG_NAME, HStr.make(""));
        HGrid arguments = HGridBuilder.dictToGrid(hd.toDict());
        client.call(EXTENDED_OP_NAME, arguments);
        
        // now assertTrue that it is now there.
        equip.lease();
        assertTrue(equip.tags().contains(DISCHARGE_ID));
        assertTrue(equip.tags().contains(AIR_ID));
        assertTrue(equip.tags().contains(TEMP_ID));
        assertTrue(equip.tags().contains(SENSOR_ID));
    }

    public void testAddEquips()
    {
        HStr id = HStr.make("[C.testFolder]");
        BComponent target = testFolder;

        HDict dict = new HDictBuilder()
            .add(FUNCTION_OP_ARG_NAME, HStr.make("addEquips"))
            .add("ids", id)
            .add("siteName", SITE_B)
            .add(TARGET_FILTER_OP_ARG_NAME, HStr.make(""))
            .toDict();
        HGrid arguments = HGridBuilder.dictToGrid(dict);
        
        // make sure the equip slot is not present
        Assert.assertNull(target.get(EQUIP_SLOT_NAME));
        
        // now through the client call add the equip component
        client.call(EXTENDED_OP_NAME, arguments);
        
        // now assertTrue that it is now there.
        BComponent addedEquip = target.get(EQUIP_SLOT_NAME).asComponent();
        assertNotNull(addedEquip);
        
        // assertTrue siteRef exist.
        addedEquip.lease();
        Optional<Relation> optRelation = addedEquip.relations().get(Id.newId("hs:siteRef"));
        assertTrue(optRelation.isPresent());
        Relation siteRelation = optRelation.orElseThrow(IllegalStateException::new);
        Entity endpoint = siteRelation.getEndpoint();
        assertEquals(((BComplex)endpoint).getName(), SITE_B);
    }

    public void testAddRemoveHaystackSlot() throws InterruptedException
    {
        HStr id = HStr.make("[S.SiteA.EquipA.boolPoint]");

        // make sure the haystack slot is not present
        BComponent target = siteAequipA.get("boolPoint").asComponent();
        target.lease();
        Assert.assertNull(target.get(HAYSTACK_SLOT_NAME));

        // now through the client call add the haystack slot
        HDictBuilder hd = new HDictBuilder();
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("addHaystackSlots"));
        hd.add("ids", id);
        hd.add(TARGET_FILTER_OP_ARG_NAME, HStr.make(""));
        client.call(EXTENDED_OP_NAME, HGridBuilder.dictToGrid(hd.toDict()));
        
        // check that the haystack slot is there now
        assertNotNull(target.get(HAYSTACK_SLOT_NAME));

        // now remove it through the client call
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("deleteHaystackSlot"));
        hd.add("ids", id);
        hd.add(TARGET_FILTER_OP_ARG_NAME, HStr.make(""));
        client.call(EXTENDED_OP_NAME, HGridBuilder.dictToGrid(hd.toDict()));

        // check that the haystack slot has been removed
        target.lease();
        Assert.assertNull(target.get(HAYSTACK_SLOT_NAME));
    }

////////////////////////////////////////////////////////////////////////////
//// Watches
////////////////////////////////////////////////////////////////////////////
//    TODO: This test does pass, but the watch timer thread on the server is not terminating when the watch is closed.
//    TODO: This thread is causing a thread dump when the test station is stopped.
//    TODO: It appears that it will require a code change in the server code to correct.

/*
    @Test(priority = 90)
    void verifyWatches() throws Exception
    {
        client = client == null ? openClient() : client;

        // create new watch
        HWatch w = client.watchOpen("NHaystack Simple Test", HNum.make(120, "s"));
        Assert.assertEquals(w.id(), null);
        Assert.assertEquals(w.dis(), "NHaystack Simple Test");

        // do query to get some recs
        HGrid recs = client.readAll("point");
        Assert.assertTrue(recs.numRows() >= 4);
        HDict a = recs.row(0);
        HDict b = recs.row(1);
        HDict c = recs.row(3);
        HDict d = recs.row(4);

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
        w.unsub(new HRef[]{b.id()});
        poll = w.pollRefresh();
        Assert.assertEquals(poll.numRows(), 3);

        w.unsub(new HRef[]{a.id(), c.id(), d.id() });
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

        Assert.assertEquals(client.watch(w.id(), false), null);
        Assert.assertEquals(client.watches().length, 0);

    }
*/

////////////////////////////////////////////////////////////////
// Utils
////////////////////////////////////////////////////////////////

    HDateTime ts(HDict r)
    {
        return (HDateTime)r.get("ts");
    }
    
    HStr localTz()
    {
        return HStr.make(HTimeZone.DEFAULT.name);
    }

    private static void addSiteRefRelation(BComponent source, BComponent site)
    {
        source.relations().add(new BRelation(ID_SITE_REF, site));
    }

    private static Collection<HStr> gatherColumn(HGrid grid, String columnName)
    {
        int numRows = grid.numRows();
        Collection<HStr> actual = new ArrayList<>(numRows);
        HCol nameCol = grid.col(columnName);
        for (int i = 0; i < numRows; ++i)
        {
            actual.add((HStr) grid.row(i).get(nameCol, true));
        }
        return actual;
    }

    private static void verifyColumnContains(Collection<HStr> actual, String... values)
    {
        Collection<HStr> expected = new ArrayList<>(values.length);
        for (String value : values)
        {
            expected.add(HStr.make(value));
        }
        assertEquals(actual, expected, "Actual: " + actual);
    }

    private static void gridContainsColumns(HGrid grid, String... columnNames)
    {
        for (String columnName : columnNames)
        {
            assertNotNull(grid.col(columnName));
        }
    }

//////////////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////////////////

    private static final String SITE_B = "SiteB";
    private static final String EQUIP_A = "EquipA";
    
    private static final String EXTENDED_OP_NAME = "extended";
    private static final String FUNCTION_OP_ARG_NAME = "function";
    private static final String TARGET_FILTER_OP_ARG_NAME = "targetFilter";
    
    private BHSite siteA;
    private BHSite siteB;
    private BHEquip siteAequipA;
    private BFolder playgroundFolder;

    private BComponent testFolder;

    private BComponent testFolder2;
    private BFolder heatPumpFolder;
    private BHEquip heatPumpEquip;

    // used for Numeric point testing
    private static final HNum[] NUM_ARRAY =
    {
        HNum.make(10),
        HNum.make(20),
        HNum.make(30),
        HNum.make(40),
        HNum.make(50),
        HNum.make(60),
        HNum.make(70),
        HNum.make(80),
        HNum.make(90),
        HNum.make(100),
        HNum.make(110),
        HNum.make(120),
        HNum.make(130),
        HNum.make(140),
        HNum.make(150),
        HNum.make(160),
        HNum.make(170),
    };

    // used for Boolean point testing
    private static final HBool[] BOOL_ARRAY =
    {
        HBool.TRUE,
        HBool.FALSE,
        HBool.TRUE,
        HBool.FALSE,
        HBool.TRUE,
        HBool.FALSE,
        HBool.TRUE,
        HBool.FALSE,
        HBool.TRUE,
        HBool.FALSE,
        HBool.TRUE,
        HBool.FALSE,
        HBool.TRUE,
        HBool.FALSE,
        HBool.TRUE,
        HBool.FALSE,
        HBool.TRUE,
        HBool.FALSE,
    };

    // used for String and Enum point testing
    private static final HStr[] STR_ARRAY =
    {
        HStr.make("a1"),
        HStr.make("a2"),
        HStr.make("a3"),
        HStr.make("a4"),
        HStr.make("a5"),
        HStr.make("a6"),
        HStr.make("a7"),
        HStr.make("a8"),
        HStr.make("a9"),
        HStr.make("a10"),
        HStr.make("a11"),
        HStr.make("a12"),
        HStr.make("a13"),
        HStr.make("a14"),
        HStr.make("a15"),
        HStr.make("a16"),
        HStr.make("a17"),
    };
}
