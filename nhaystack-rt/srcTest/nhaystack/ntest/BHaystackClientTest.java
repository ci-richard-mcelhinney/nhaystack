//
// Copyright 2017 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   05 Dec 2017  Rowyn Brunner  Creation
//

package nhaystack.ntest;

import static nhaystack.ntest.helper.NHaystackTestUtil.AIR_ID;
import static nhaystack.ntest.helper.NHaystackTestUtil.CUR_VAL_TAG_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.DISCHARGE_ID;
import static nhaystack.ntest.helper.NHaystackTestUtil.EQUIP_REF_TAG_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.EQUIP_SLOT_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.HAYSTACK_SLOT_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.NAV_ID_TAG_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.SENSOR_ID;
import static nhaystack.ntest.helper.NHaystackTestUtil.SITE_REF_ID;
import static nhaystack.ntest.helper.NHaystackTestUtil.SITE_REF_TAG_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.TEMP_ID;
import static nhaystack.ntest.helper.NHaystackTestUtil.WRITE_LEVEL_TAG_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.WRITE_VAL_TAG_NAME;
import static nhaystack.ntest.helper.NHaystackTestUtil.addChild;
import static nhaystack.ntest.helper.NHaystackTestUtil.purgeDirectory;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import javax.baja.control.BBooleanWritable;
import javax.baja.control.BEnumWritable;
import javax.baja.control.BNumericPoint;
import javax.baja.control.BNumericWritable;
import javax.baja.control.BStringWritable;
import javax.baja.history.ext.BNumericCovHistoryExt;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.registry.TypeInfo;
import javax.baja.sys.BComplex;
import javax.baja.sys.BComponent;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.BRelation;
import javax.baja.sys.BStation;
import javax.baja.sys.BString;
import javax.baja.sys.Clock;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Entity;
import javax.baja.tag.Id;
import javax.baja.tag.Relation;
import javax.baja.units.BUnit;
import javax.baja.util.BFolder;
import javax.baja.util.BServiceContainer;
import javax.baja.web.BWebService;

import junit.extensions.PA;
import nhaystack.server.BNHaystackConvertHaystackSlotsJob;
import nhaystack.server.BNHaystackRebuildCacheJob;
import nhaystack.server.BNHaystackService;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
import org.projecthaystack.HBool;
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
import org.projecthaystack.HUri;
import org.projecthaystack.HVal;
import org.projecthaystack.client.CallErrException;
import org.projecthaystack.client.HClient;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.tridium.history.log.BLogHistoryService;
import com.tridium.kitControl.util.BSineWave;
import com.tridium.testng.BStationTestBase;

@NiagaraType
@SuppressWarnings("MagicNumber")
public class BHaystackClientTest extends BStationTestBase
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

        // Ensure that the history directory is purged so histories from other tests do not show up
        // and fail tests in this class that are not expecting them (audit history, for example).
        File protectedStationHome = Sys.getProtectedStationHome();
        if (protectedStationHome.exists())
        {
            purgeDirectory(protectedStationHome);
        }

        BServiceContainer services = station.getServices();

        services.add("LogHistoryService", new BLogHistoryService());

//        BTagDictionaryService tagDictionaryService =
//          addChild("TagDictionaryService", new BTagDictionaryService(), services);
//        BHsTagDictionary haystackDictionary =
//          addChild("Haystack", new BHsTagDictionary(), tagDictionaryService);
//        haystackDictionary.importDictionary(null);

        final TypeInfo tdsTypeInfo = Sys.getRegistry().getType("tagdictionary:TagDictionaryService");
        final TypeInfo hstdTypeInfo = Sys.getRegistry().getType("haystack:HsTagDictionary");
        BComponent tagDictionaryService =
          addChild("TagDictionaryService", tdsTypeInfo.getInstance().asComponent(), services);
        BComponent haystackDictionary =
          addChild("Haystack", hstdTypeInfo.getInstance().asComponent(), tagDictionaryService);
        final Method importDictionary = haystackDictionary.getClass().getDeclaredMethod("importDictionary");
        importDictionary.setAccessible(true);
        importDictionary.invoke(haystackDictionary);

        services.add("NHaystackService", haystackService);
        haystackService.setSchemaVersion(1);
        haystackService.setShowLinkedHistories(true);

        BWebService webService = getWebService();
        webService.setHttpEnabled(true);
        webService.setHttpsOnly(false);
        webService.setHttpsEnabled(true);

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
        //     * equip (HEquip)

        BFolder siteFolder = addChild(SITE_FOLDER, new BFolder(), station);
        station.add("testFolder", testFolder);
        station.add("testFolder2", testFolder2);
        testFolder2.add(EQUIP_SLOT_NAME, new BHEquip());

        siteA = addChild("SiteA", new BHSite(), siteFolder);
        siteB = addChild(SITE_B, new BHSite(), siteFolder);

        BFolder siteAEquipAFolder = addChild(EQUIP_A, new BFolder(), siteA);
        BFolder siteBEquipAFolder = addChild(EQUIP_A, new BFolder(), siteB);
        siteAEquipAFolder.add(EQUIP_SLOT_NAME, siteAequipA);
        siteAequipA.relations().add(new BRelation(SITE_REF_ID, siteA, Relation.OUTBOUND));
        BHEquip siteBEquipAEquip = addChild(EQUIP_SLOT_NAME, new BHEquip(), siteBEquipAFolder);
        siteBEquipAEquip.relations().add(new BRelation(SITE_REF_ID, siteB, Relation.OUTBOUND));

        siteAequipA.add("boolPoint", new BBooleanWritable());

        BEnumWritable enumPoint = addChild("enumPoint", new BEnumWritable(), siteAequipA);
        // initialize enum point facets
        String[] enumTags = Arrays.stream(STR_ARRAY).map(HStr::toString).toArray(String[]::new);
        enumPoint.setFacets(BFacets.makeEnum(BEnumRange.make(enumTags)));

        siteAequipA.add("strPoint", new BStringWritable());

        siteAEquipAFolder.add("point1", new BNumericWritable());

        BSineWave sineWave1 = addChild("sineWave1", new BSineWave(),siteAEquipAFolder);
        sineWave1.setFacets(BFacets.makeNumeric(BUnit.getUnit("second"), 3, -50, 50));
        BNumericCovHistoryExt histExt = addChild("NumericCov", new BNumericCovHistoryExt(), sineWave1);
        histExt.setEnabled(true);

        siteAEquipAFolder.add("sineWave2", new BSineWave());
        siteAequipA.add("sineWave3", new BSineWave());
        siteAequipA.add("sineWave4", new BSineWave());
    }

    private static boolean isSlotConversionInProgress(BNHaystackService haystackService)
    {
        return (boolean)PA.getValue(haystackService, "slotConversionInProgress");
    }

    @Test(priority = 1)  // must be first test to run
    public void testConversionInitLockout() throws InterruptedException
    {
//        openClient(); // just let stuff initialize.
        assertFalse(isSlotConversionInProgress(haystackService));
        final BOrd bOrd = haystackService.convertHaystackSlots();
        final BNHaystackConvertHaystackSlotsJob job = (BNHaystackConvertHaystackSlotsJob) bOrd.resolve(haystackService).get();
        // spin until convert job is in process
        long startTicks = Clock.ticks();
        while(!isSlotConversionInProgress(haystackService))
        {
            Thread.yield();
            if (Clock.ticks() - startTicks > 5000)
            {
                break;
            }
        }
        try  // now try to initialize haystack while job is in process should throw an exception.
        {
            haystackService.doInitializeHaystack();
            Assert.fail("Expected RuntimeException but didn't get one.");

        }
        catch (RuntimeException e)
        {
            assertTrue(true, "Expected RuntimeException " + e);
        }
        startTicks = Clock.ticks();
        while(job.isAlive())
        {
            Thread.yield();
            if( Clock.ticks() - startTicks > 5000)
            {
                break;
            }
        }
        Thread.sleep(3000L); // provide some time for the convert job's initializeHaystack to complete.

    }

    @Test(priority = 10)
    public void testAbout() throws InterruptedException
    {
        client = client == null ? openClient() : client;
        HDict r = client.about();
        assertEquals(r.getStr("haystackVersion"), "2.0");
    }

    @Test(priority = 20)
    public void verifyOps() throws InterruptedException
    {
        client = client == null ? openClient() : client;

        HGrid g = client.ops();

        // verify required columns
        assertNotNull(g.col("name"));
        assertNotNull(g.col("summary"));

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

    @Test(priority = 30)
    public void verifyFormats() throws InterruptedException
    {
        client = client == null ? openClient() : client;
        
        HGrid g = client.formats();

        // assertTrue required columns
        assertNotNull(g.col("mime"));
        assertNotNull(g.col("read"));
        assertNotNull(g.col("write"));

        // assertTrue required ops
        verifyGridContains(g, "mime", HStr.make("text/plain"));
        verifyGridContains(g, "mime", HStr.make("text/zinc"));
    }

//////////////////////////////////////////////////////////////////////////
// Reads
//////////////////////////////////////////////////////////////////////////

    @Test(priority = 40)
    public void verifyRead() throws InterruptedException
    {
        client = client == null ? openClient() : client;
        
        HGrid grid = client.readAll("id");

        assertEquals(grid.numRows(), 15);
        assertEquals(grid.row(0).id(), HRef.make("S.SiteA"));
        assertEquals(grid.row(1).id(), HRef.make("S.SiteA.EquipA"));
        assertEquals(grid.row(2).id(), HRef.make("S.SiteA.EquipA.boolPoint"));
        assertEquals(grid.row(3).id(), HRef.make("S.SiteA.EquipA.enumPoint"));
        assertEquals(grid.row(4).id(), HRef.make("S.SiteA.EquipA.strPoint"));
        assertEquals(grid.row(5).id(), HRef.make("S.SiteA.EquipA.sineWave3"));
        assertEquals(grid.row(6).id(), HRef.make("S.SiteA.EquipA.sineWave4"));
        assertEquals(grid.row(7).id(), HRef.make("S.SiteA.EquipA.point1"));
        assertEquals(grid.row(8).id(), HRef.make("S.SiteA.EquipA.sineWave1"));
        assertEquals(grid.row(9).id(), HRef.make("S.SiteA.EquipA.sineWave2"));
        assertEquals(grid.row(10).id(), HRef.make("S.SiteB"));
        assertEquals(grid.row(11).id(), HRef.make("S.SiteB.EquipA"));
        assertEquals(grid.row(12).id(), HRef.make("C.testFolder2.equip"));
        assertEquals(grid.row(13).id(), HRef.make("H.test.LogHistory"));
        assertEquals(grid.row(14).id(), HRef.make("H.test.sineWave1"));

        assertEquals(grid.row(1).get(SITE_REF_TAG_NAME), HRef.make("S.SiteA"));
        assertEquals(grid.row(2).get(SITE_REF_TAG_NAME), HRef.make("S.SiteA"));
        assertEquals(grid.row(3).get(SITE_REF_TAG_NAME), HRef.make("S.SiteA"));
        assertEquals(grid.row(4).get(SITE_REF_TAG_NAME), HRef.make("S.SiteA"));
        assertEquals(grid.row(5).get(SITE_REF_TAG_NAME), HRef.make("S.SiteA"));
        assertEquals(grid.row(6).get(SITE_REF_TAG_NAME), HRef.make("S.SiteA"));
        assertEquals(grid.row(7).get(SITE_REF_TAG_NAME), HRef.make("S.SiteA"));
        assertEquals(grid.row(8).get(SITE_REF_TAG_NAME), HRef.make("S.SiteA"));
        assertEquals(grid.row(9).get(SITE_REF_TAG_NAME), HRef.make("S.SiteA"));
        assertEquals(grid.row(11).get(SITE_REF_TAG_NAME), HRef.make("S.SiteB"));
        assertEquals(grid.row(14).get("axPointRef"), HRef.make("S.SiteA.EquipA.sineWave1"));

        // TODO add check for equipRef to Equip2
        assertEquals(grid.row(2).get(EQUIP_REF_TAG_NAME), HRef.make("S.SiteA.EquipA"));
        assertEquals(grid.row(3).get(EQUIP_REF_TAG_NAME), HRef.make("S.SiteA.EquipA"));
        assertEquals(grid.row(4).get(EQUIP_REF_TAG_NAME), HRef.make("S.SiteA.EquipA"));
        assertEquals(grid.row(5).get(EQUIP_REF_TAG_NAME), HRef.make("S.SiteA.EquipA"));
        assertEquals(grid.row(6).get(EQUIP_REF_TAG_NAME), HRef.make("S.SiteA.EquipA"));
        assertEquals(grid.row(7).get(EQUIP_REF_TAG_NAME), HRef.make("S.SiteA.EquipA"));
        assertEquals(grid.row(8).get(EQUIP_REF_TAG_NAME), HRef.make("S.SiteA.EquipA"));
        assertEquals(grid.row(9).get(EQUIP_REF_TAG_NAME), HRef.make("S.SiteA.EquipA"));

        //////////////////////////////////////////

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
//
//    Assert.assertEquals(dict.get("dis"), HStr.make("Foo_SineWave1"));
//    Assert.assertEquals(dict.get("navName"), HStr.make("Foo_SineWave1"));
//    Assert.assertEquals(dict.get("navNameFormat"), HStr.make("%parent.displayName%_%displayName%"));
//
//        //////////////////////////////////////////
//
//        dict = client.readById(HRef.make("C.Foo.Sine-Wave2~2fabc"));
//        Assert.assertEquals(dict.get("axType"), HStr.make("kitControl:SineWave"));
//        assertTrue(dict.missing("foo"));
//        assertTrue(dict.missing("bar"));
//        Assert.assertEquals(dict.get("kind"), HStr.make("Number"));
//        assertTrue(dict.has("his"));
//        Assert.assertEquals(dict.get("curStatus"), HStr.make("ok"));
//        assertTrue(dict.has("hisInterpolate"));
//        Assert.assertEquals(dict.get("axSlotPath"), HStr.make("slot:/Foo/Sine$20Wave2$2fabc"));
//        Assert.assertEquals(dict.get("unit"), HStr.make("psi"));
//        assertTrue(dict.has("point"));
//        assertTrue(dict.has("tz"));
//        assertTrue(dict.has("cur"));
//        curVal = dict.getDouble(CUR_VAL_TAG_NAME);
//        assertTrue(curVal >= 0.0 && curVal <= 100.0);
//
//        Assert.assertEquals(dict.get("dis"), HStr.make("Sine-Wave2~2fabc"));
//        Assert.assertEquals(dict.get("navName"), HStr.make("Sine-Wave2~2fabc"));
//        assertTrue(dict.missing("navNameFormat"));
//
//        //////////////////////////////////////////
//
//        dict = client.readById(HRef.make("S.Richmond.AHU2.NumericWritable"));
//
//        //////////////////////////////////////////

//    dict = client.readById(HRef.make("H.nhaystack1.AuditHistory"));
//    Assert.assertEquals(dict.get("axType"), HStr.make("history:HistoryConfig"));
//    assertTrue(dict.missing("kind"));
//    assertTrue(dict.has("his"));
//    assertTrue(dict.missing("cur"));
//    assertTrue(dict.missing("curStatus"));
//    assertTrue(dict.missing(CUR_VAL_TAG_NAME));
//    Assert.assertEquals(dict.get("tz"), localTz());
//    Assert.assertEquals(dict.get("axHistoryId"), HStr.make("/nhaystack1/AuditHistory"));
//    assertTrue(dict.missing("hisInterpolate"));
//    assertTrue(dict.missing("unit"));

//        dict = client.readById(HRef.make("H.nhaystack_simple.LogHistory"));
//        Assert.assertEquals(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        assertTrue(dict.missing("kind"));
//        assertTrue(dict.has("his"));
//        assertTrue(dict.missing("cur"));
//        assertTrue(dict.missing("curStatus"));
//        assertTrue(dict.missing(CUR_VAL_TAG_NAME));
//        Assert.assertEquals(dict.get("tz"), localTz());
//        Assert.assertEquals(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/LogHistory"));
//        assertTrue(dict.missing("hisInterpolate"));
//        assertTrue(dict.missing("unit"));
//
//        //        dict = client.readById(HRef.make("H.nhaystack_simple.SineWave3"));
//        //        Assert.assertEquals(dict.get("axType"), HStr.make("history:HistoryConfig"));
//        //        Assert.assertEquals(dict.get("kind"), HStr.make("Number"));
//        //        assertTrue(dict.has("his"));
//        //        assertTrue(dict.missing("cur"));
//        //        assertTrue(dict.missing("curStatus"));
//        //        assertTrue(dict.missing(CUR_VAL_TAG_NAME));
//        //        Assert.assertEquals(dict.get("tz"), localTz());
//        //        Assert.assertEquals(dict.get("axHistoryId"), HStr.make("/nhaystack_simple/SineWave3"));
//        //        assertTrue(dict.missing("hisInterpolate"));
//        //        Assert.assertEquals(dict.get("unit"), HStr.make("psi"));
//
//        try { client.readById(HRef.make("c.Mg~~")); } catch(Exception e) { assertTrueException(e); }
    }

//////////////////////////////////////////////////////////////////////////
// Nav
//////////////////////////////////////////////////////////////////////////

    @Test(priority = 50)
    public void verifyNav() throws Exception
    {
        client = client == null ? openClient() : client;

        HGrid grid = client.call("read", makeIdGrid(HUri.make("sep:/SiteA")));
        assertEquals(grid.numRows(), 1);
        assertEquals(grid.row(0).id(), HRef.make("S.SiteA"));

        grid = client.call("read", makeIdGrid(HUri.make("sep:/SiteA/EquipA")));
        assertEquals(grid.numRows(), 1);
        assertEquals(grid.row(0).id(), HRef.make("S.SiteA.EquipA"));

        HGrid n = makeNavGrid(HStr.make("sep:/SiteA/EquipA"));
        grid = client.call("nav", n);
        assertEquals(grid.numRows(), 8);
        assertEquals(grid.row(0).id(), HRef.make("S.SiteA.EquipA.boolPoint"));
        assertEquals(grid.row(1).id(), HRef.make("S.SiteA.EquipA.enumPoint"));
        assertEquals(grid.row(2).id(), HRef.make("S.SiteA.EquipA.strPoint"));
        assertEquals(grid.row(3).id(), HRef.make("S.SiteA.EquipA.sineWave3"));
        assertEquals(grid.row(4).id(), HRef.make("S.SiteA.EquipA.sineWave4"));
        assertEquals(grid.row(5).id(), HRef.make("S.SiteA.EquipA.point1"));
        assertEquals(grid.row(6).id(), HRef.make("S.SiteA.EquipA.sineWave1"));
        assertEquals(grid.row(7).id(), HRef.make("S.SiteA.EquipA.sineWave2"));

        grid = client.call("nav", HGrid.EMPTY);
        assertEquals(grid.numRows(), 3);
        assertEquals(grid.row(0).get(NAV_ID_TAG_NAME), HStr.make("slot:/"));
        assertEquals(grid.row(0).get("dis"), HStr.make("ComponentSpace"));
        assertEquals(grid.row(1).get(NAV_ID_TAG_NAME), HStr.make("his:/"));
        assertEquals(grid.row(1).get("dis"), HStr.make("HistorySpace"));
        assertEquals(grid.row(2).get(NAV_ID_TAG_NAME), HStr.make("sep:/"));
        assertEquals(grid.row(2).get("dis"), HStr.make("Site"));

        n = makeNavGrid(HStr.make("his:/"));
        grid = client.call("nav", n);
        assertEquals(grid.numRows(), 1);
        assertEquals(grid.row(0).get(NAV_ID_TAG_NAME), HStr.make("his:/test"));

        n = makeNavGrid(HStr.make("his:/test"));
        grid = client.call("nav", n);
        assertEquals(grid.numRows(), 2);

        n = makeNavGrid(HStr.make("slot:/"));
        grid = client.call("nav", n);
        assertEquals(grid.numRows(), 6);
        assertEquals(grid.row(0).get(NAV_ID_TAG_NAME), HStr.make("slot:/Services"));
        assertEquals(grid.row(1).get(NAV_ID_TAG_NAME), HStr.make("slot:/Drivers"));
        assertTrue(grid.row(2).missing(NAV_ID_TAG_NAME));
        assertEquals(grid.row(3).get(NAV_ID_TAG_NAME), HStr.make("slot:/Playground"));

        grid = client.call("nav", makeNavGrid(HStr.make("sep:/")));
        assertEquals(grid.numRows(), 2);
        assertEquals(grid.row(0).get(NAV_ID_TAG_NAME), HStr.make("sep:/SiteA"));
        assertEquals(grid.row(0).get("dis"), HStr.make("SiteA"));
        assertEquals(grid.row(1).get(NAV_ID_TAG_NAME), HStr.make("sep:/SiteB"));
        assertEquals(grid.row(1).get("dis"), HStr.make(SITE_B));

        grid = client.call("nav", makeNavGrid(HStr.make("sep:/SiteA")));
        assertEquals(grid.numRows(), 1);
        assertEquals(grid.row(0).get(NAV_ID_TAG_NAME), HStr.make("sep:/SiteA/EquipA"));
        assertEquals(grid.row(0).get("dis"), HStr.make(EQUIP_A));

        grid = client.call("nav", makeNavGrid(HStr.make("sep:/SiteA/EquipA")));
        assertEquals(grid.numRows(), 8);
        assertTrue(grid.row(0).missing(NAV_ID_TAG_NAME));
        assertTrue(grid.row(1).missing(NAV_ID_TAG_NAME));
        assertTrue(grid.row(2).missing(NAV_ID_TAG_NAME));
        assertTrue(grid.row(3).missing(NAV_ID_TAG_NAME));
        assertTrue(grid.row(4).missing(NAV_ID_TAG_NAME));
        assertEquals(grid.row(0).get("dis"), HStr.make("SiteA EquipA boolPoint"));
        assertEquals(grid.row(1).get("dis"), HStr.make("SiteA EquipA enumPoint"));
        assertEquals(grid.row(2).get("dis"), HStr.make("SiteA EquipA strPoint"));
        assertEquals(grid.row(3).get("dis"), HStr.make("SiteA EquipA sineWave3"));
        assertEquals(grid.row(4).get("dis"), HStr.make("SiteA EquipA sineWave4"));
        assertEquals(grid.row(5).get("dis"), HStr.make("SiteA EquipA point1"));
        assertEquals(grid.row(6).get("dis"), HStr.make("SiteA EquipA sineWave1"));
        assertEquals(grid.row(7).get("dis"), HStr.make("SiteA EquipA sineWave2"));

        grid = client.call("nav", makeNavGrid(HStr.make("sep:/SiteB/EquipA")));
        assertEquals(grid.numRows(), 0);

//        HGridBuilder hb = new HGridBuilder();
//        hb.addCol(FUNCTION_OP_NAME);
//        hb.addCol("filter");
//        hb.addRow(new HVal[]{HStr.make("makeDynamicWritable"), HStr.make("id")});
//        try
//        {
//            grid = client.call("extended", hb.toGrid());
//            grid.dump();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }

////////////////////////////////////////////////////////////////////////////
//// His Reads
////////////////////////////////////////////////////////////////////////////

    @Test(priority = 60)
    public void verifyHisRead() throws InterruptedException
    {
        client = client == null ? openClient() : client;

        HGrid grid = client.readAll("his");
        assertEquals(grid.numRows(), 3);

        ///////////////////////////////////////////////

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

////////////////////////////////////////////////////////////////////////////
//// Point Write
////////////////////////////////////////////////////////////////////////////

    @Test(priority = 70)
    public void verifyPointWrite() throws InterruptedException
    {
        client = client == null ? openClient() : client;
        doVerifyPointWrite(HRef.make("S.SiteA.EquipA.point1"), NUM_ARRAY, HNum.make(0) );
        doVerifyPointWrite(HRef.make("S.SiteA.EquipA.boolPoint"), BOOL_ARRAY, HBool.FALSE );
        doVerifyPointWrite(HRef.make("S.SiteA.EquipA.strPoint"), STR_ARRAY, HStr.make("") );
        doVerifyPointWrite(HRef.make("S.SiteA.EquipA.enumPoint"), STR_ARRAY, HStr.make("a17") );
    }

    private void doVerifyPointWrite(HRef id, HVal[] arrayVals, HVal endDefaultVal) throws InterruptedException
    {
        // set the default value
        HDictBuilder hd = new HDictBuilder();
        // first verify that curVal is not present
        HDict hDict = client.readById(id);
        assertFalse(hDict.has(CUR_VAL_TAG_NAME) /*, "curVal missing test, curVal = " + hDict.get(CUR_VAL_TAG_NAME)*/);
//        assertEquals(hDict.get(WRITE_LEVEL_TAG_NAME), HNum.make(17));
//        assertFalse(hDict.has(WRITE_LEVEL_TAG_NAME));
//        assertFalse(hDict.has(WRITE_VAL_TAG_NAME));

        hd.add("arg", arrayVals[16]);
        // first set the point's default value to false.
        client.invokeAction(id, "set", hd.toDict());

        HGrid wrArray = null;
        String who = null;
        // verify that a null who throws an exception
        try
        {
            wrArray = client.pointWrite(id, 1, who,  arrayVals[16], null);
        }
        catch(CallErrException une)
        {
            assertTrue(true, "pointWrite CallErrException: " + une);
        }
        catch(Exception e)
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

////////////////////////////////////////////////////////////////////////////
//// Extended ops
////////////////////////////////////////////////////////////////////////////

    /*
    * These are the write function extended Ops
    *   addHaystackSlots   : doAddRemoveHaystackSlot()
    *   addEquips          : doVerifyAddEquips()
    *   applyBatchTags     : doVerifyApplyBatchTags()
    *   copyEquipTags      : doVerifyCopyEquipTags()
    *   delete             : no test as it just deletes the haystack slot
    *   deleteHaystackSlot : doAddRemoveHaystackSlot()
    *   searchAndReplace   : no test as it only renames components
    *   mapPointsToEquip   :
    *   makeDynamicWritable: no test, not sure what it actually does.  Looks for dynamic action and
    *                      : adds haystack slot with writable tag.
    *   applyGridTags      : doVerifyApplyGridTags()
    */

    @Test(priority = 100)
    public void verifyExtendedOps() throws InterruptedException
    {
        client = client == null ? openClient() : client;
        
        BComponent target = siteAequipA.get("boolPoint").asComponent();
        target.lease();
        doVerifyAddRemoveHaystackSlot(HStr.make("[S.SiteA.EquipA.boolPoint]"), target);
        doVerifyAddEquips(HStr.make("[C.testFolder]"), testFolder);
        doVerifyApplyBatchTags(HStr.make("[C.testFolder.equip]"), addedEquip);
        doVerifyCopyEquipTags(HStr.make("C.testFolder"), addedEquip);
        doVerifyApplyGridTags();
        doVerifyMapPointsToEquip();
        doVerifyDelete();
    }

    private void doVerifyDelete()
    {
    }

    private void doVerifyMapPointsToEquip()
    {
        //setup
        testFolder2.add("MottsForest", new BHSite());
        testFolder2.add("HeatPump", new BHEquip());
        testFolder2.add("somePoints", new BFolder());
        BFolder spf = (BFolder)testFolder2.get("somePoints");
        spf.add("hp1", new BNumericPoint());
        spf.add("hp2", new BNumericPoint());
        spf.add("hp3", new BNumericPoint());

        // rebuild cache.
        BNHaystackRebuildCacheJob rebuildCacheJob = new BNHaystackRebuildCacheJob(haystackService);
        try
        {
            rebuildCacheJob.run(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //build request
        HDictBuilder hd = new HDictBuilder();
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("mapPointsToEquip"));
        hd.add("siteNavName", HStr.make("MottsForest"));
        hd.add("equipNavName", HStr.make("HeatPump"));
        String sb = "[C.testFolder2.somePoints.hp1,C.testFolder2.somePoints.hp2,C.testFolder2.somePoints.hp3]";
        hd.add("ids", HStr.make(sb));
        HGrid arguments = HGridBuilder.dictToGrid(hd.toDict());

        // send request to map points to equip
        client.call(EXTENDED_OP_NAME, arguments);
        // rebuild cache.
        try
        {
            rebuildCacheJob.run(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        testFolder2.lease();
        HGrid siteGrid = client.call("read", makeIdGrid(HUri.make("sep:/MottsForest")));
        HGrid equipGrid = client.call("read", makeIdGrid(HUri.make("sep:/MottsForest/HeatPump")));
        HGrid n = makeNavGrid(HStr.make("sep:/MottsForest/HeatPump"));
        HGrid pointGrid = client.call("nav", n);
        assertEquals(siteGrid.numRows(), 1);
        assertEquals(equipGrid.numRows(), 1);
        assertEquals(pointGrid.numRows(), 3);
        assertEquals(pointGrid.row(0).id(), HRef.make("S.MottsForest.HeatPump.hp1"));
        assertEquals(pointGrid.row(1).id(), HRef.make("S.MottsForest.HeatPump.hp2"));
        assertEquals(pointGrid.row(2).id(), HRef.make("S.MottsForest.HeatPump.hp3"));
    }

    private void doVerifyApplyGridTags()
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

    private void doVerifyCopyEquipTags(HStr id, BComponent target)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("copyEquipTags"));
        hd.add("ids", id);
        hd.add("fromEquip", HStr.make("C.testFolder.equip"));
        hd.add("toEquips", HStr.make("[C.testFolder2.equip]"));
        hd.add(TARGET_FILTER_OP_ARG_NAME, HStr.make("id"));
        HGrid arguments = HGridBuilder.dictToGrid(hd.toDict());
        client.call(EXTENDED_OP_NAME, arguments);

        // now assertTrue that it is now there.
        target.lease();
        assertTrue(target.tags().contains(DISCHARGE_ID));
        assertTrue(target.tags().contains(AIR_ID));
        assertTrue(target.tags().contains(TEMP_ID));
        assertTrue(target.tags().contains(SENSOR_ID));

        BComponent toEquip = testFolder2.get(EQUIP_SLOT_NAME).asComponent();
        assertTrue(toEquip.tags().contains(DISCHARGE_ID));
        assertTrue(toEquip.tags().contains(AIR_ID));
        assertTrue(toEquip.tags().contains(TEMP_ID));
        assertTrue(toEquip.tags().contains(SENSOR_ID));

        //now remove equip from testFolder2 and copyEquipTags again.
        //it should add the BHEquip object again and copy the tags.
        testFolder2.remove(EQUIP_SLOT_NAME);
        Assert.assertNull(testFolder2.get(EQUIP_SLOT_NAME));
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("copyEquipTags"));
        hd.add("ids", id);
        hd.add("fromEquip", HStr.make("C.testFolder.equip"));
        hd.add("toEquips", HStr.make("[C.testFolder2]"));
        hd.add(TARGET_FILTER_OP_ARG_NAME, HStr.make("id"));
        arguments = HGridBuilder.dictToGrid(hd.toDict());
        client.call(EXTENDED_OP_NAME, arguments);

        // now assertTrue that it is now there.
        toEquip = testFolder2.get(EQUIP_SLOT_NAME).asComponent();
        toEquip.lease();
        assertTrue(toEquip.tags().contains(DISCHARGE_ID));
        assertTrue(toEquip.tags().contains(AIR_ID));
        assertTrue(toEquip.tags().contains(TEMP_ID));
        assertTrue(toEquip.tags().contains(SENSOR_ID));
    }

    private void doVerifyApplyBatchTags(HStr id, BComponent target)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("applyBatchTags"));
        hd.add("ids", id);
        hd.add("tags", HStr.make("{discharge air temp sensor}"));
        hd.add(TARGET_FILTER_OP_ARG_NAME, HStr.make(""));
        HGrid arguments = HGridBuilder.dictToGrid(hd.toDict());
        // make sure the equip slot is not present
//        Assert.assertTrue(target.get(EQUIP_SLOT_NAME)==null);
        // now through the client call add the equip component
        client.call(EXTENDED_OP_NAME, arguments);
        
        // now assertTrue that it is now there.
        target.lease();
        assertTrue(target.tags().contains(DISCHARGE_ID));
        assertTrue(target.tags().contains(AIR_ID));
        assertTrue(target.tags().contains(TEMP_ID));
        assertTrue(target.tags().contains(SENSOR_ID));
    }

    private void doVerifyAddEquips(HStr id, BComponent target)
    {
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
        addedEquip = target.get(EQUIP_SLOT_NAME).asComponent();
        assertNotNull(addedEquip);
        
        // assertTrue siteRef exist.
        addedEquip.lease();
        Optional<Relation> optRelation = addedEquip.relations().get(Id.newId("hs:siteRef"));
        assertTrue(optRelation.isPresent());
        Relation siteRelation = optRelation.orElseThrow(IllegalStateException::new);
        Entity endpoint = siteRelation.getEndpoint();
        assertEquals(((BComplex)endpoint).getName(), SITE_B);
    }

    private void doVerifyAddRemoveHaystackSlot(HStr id, BComponent target) throws InterruptedException
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("addHaystackSlots"));
        hd.add("ids", id);
        hd.add(TARGET_FILTER_OP_ARG_NAME, HStr.make(""));
        HGrid arguments = HGridBuilder.dictToGrid(hd.toDict());
        // make sure the haystack slot is not present
        Assert.assertNull(target.get(HAYSTACK_SLOT_NAME));
        // now through the client call add the haystack slot
        client.call(EXTENDED_OP_NAME, arguments);
        
        // now assertTrue that it is now there.
        assertNotNull(target.get(HAYSTACK_SLOT_NAME));

        // now remove it through the client call
        hd.add(FUNCTION_OP_ARG_NAME, HStr.make("deleteHaystackSlot"));
        hd.add("ids", id);
        hd.add(TARGET_FILTER_OP_ARG_NAME, HStr.make(""));
        arguments = HGridBuilder.dictToGrid(hd.toDict());
        client.call(EXTENDED_OP_NAME, arguments);
        target.lease();
        Assert.assertNull(target.get(HAYSTACK_SLOT_NAME));

        Thread.sleep(100);
    }

////////////////////////////////////////////////////////////////////////////
//// Invoke Action
////////////////////////////////////////////////////////////////////////////

    @Test(priority = 80)
    public void verifyInvokeAction() throws InterruptedException
    {
        client = client == null ? openClient() : client;
        doVerifyInvokeAction(HRef.make("S.SiteA.EquipA.point1"));
    }

    private void doVerifyInvokeAction(HRef id) throws InterruptedException
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add("arg", HNum.make(0));
        
        // first set the point's default value to 0.
        client.invokeAction(id, "set", hd.toDict());
        Thread.sleep(100);
        
        // now set emergency level to 999
        hd.add("arg", HNum.make(999));
        client.invokeAction(id, "emergencyOverride", hd.toDict());
        Thread.sleep(100);

        HDict hDict = client.readById(id);
        final Iterator iterator = hDict.iterator();
        while (iterator.hasNext())
        {
            final Entry entry = (Entry)iterator.next();
        }
        assertEquals(hDict.get(CUR_VAL_TAG_NAME), HNum.make(999), "Check curVAl");
        assertEquals(hDict.get(WRITE_LEVEL_TAG_NAME), HNum.make(1), "Check writeLevel");
        HVal wrValue = hDict.get(WRITE_VAL_TAG_NAME);
        if (wrValue instanceof HNum)
        {
            assertEquals(wrValue, HNum.make(999), "Check writeVal HNum");
        }
        else if(wrValue instanceof HStr)
        {
            assertEquals(wrValue, HStr.make("999.00"), "Check writeVal HStr");
        }

        client.invokeAction(id, "emergencyAuto", HDict.EMPTY);
        Thread.sleep(100);
        
        hDict = client.readById(id);
        assertTrue(hDict.has(CUR_VAL_TAG_NAME));
        assertEquals(hDict.get(CUR_VAL_TAG_NAME), HNum.make(0));
//        assertFalse(hDict.has(WRITE_LEVEL_TAG_NAME), "Should not have " + WRITE_LEVEL_TAG_NAME);
        assertEquals(hDict.get(WRITE_LEVEL_TAG_NAME), HNum.make(17));
        wrValue = hDict.get(WRITE_VAL_TAG_NAME);
        if(wrValue instanceof HNum)
        {
            assertEquals(wrValue, HNum.make(0), "Check writeVal HNum");
        }
        else if(wrValue instanceof HStr)
        {
            assertEquals(wrValue, HStr.make("0.00"), "Check writeVal HStr");
        }

    }

////////////////////////////////////////////////////////////////////////////
//// GeoCoord test
////////////////////////////////////////////////////////////////////////////
    @Test(priority = 85)
    public void verifyGeoCoord() throws Exception
    {
        client = client == null ? openClient() : client;
        siteA.tags().set(Id.newId("hs", "geoCoord"), BString.make("C(1.11,2.22)"));
        HDict hDict = client.readById(HRef.make("S.SiteA"));
        final HVal geoCoord = hDict.get("geoCoord");
        assertEquals(geoCoord.toString(), "C(1.11,2.22)");

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

    HClient openClient() throws InterruptedException
    {
        return openClient(false);
    }

    HClient openClient(boolean useHttps) throws InterruptedException
    {
        int count = 0;
        // wait up to 10 seconds for async operation to complete
        haystackService.doInitializeHaystack();
        while (!haystackService.getInitialized() || haystackService.getSchemaVersion() < 1 && count < 10)
        {
            Thread.sleep(1000);
            ++count;
        }

        if (haystackService.getSchemaVersion() < 1)
        {
            Assert.fail("Tag update not completed after nhaystackService started, aborting test.");
        }

        String baseURI = getBaseURI() + "haystack/";
        if (useHttps)
        {
            baseURI = SECURE_URI;
        }
        
        return HClient.open(baseURI, getSuperUsername(), getSuperUserPassword());
    }

//    @Override
//    protected BWebService makeWebService(int port) throws Exception
//    {
//        BWebService service = new BWebService();
//        service.getHttpPort().setPublicServerPort(port);
//        service.getHttpsPort().setPublicServerPort(443);
//
//        BWebServer server = new BJettyWebServer();
//        service.add("JettyWebServer", server);
//
//        return service;
//    }

    static HGrid makeIdGrid(HVal id)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add("id", id);
        return HGridBuilder.dictsToGrid(new HDict[] { hd.toDict() });
    }

    static HGrid makeNavGrid(HStr navId)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add(NAV_ID_TAG_NAME, navId);
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
            Assert.fail();
        }
    }

    HDateTime ts(HDict r)
    {
        return (HDateTime)r.get("ts");
    }
    
    HStr localTz()
    {
        return HStr.make(HTimeZone.DEFAULT.name);
    }

//////////////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////////////////

    HClient client;

    private static final String SECURE_URI = "https://localhost/haystack/";

    private static final String SITE_FOLDER = "Playground";
    private static final String SITE_B = "SiteB";
    private static final String EQUIP_A = "EquipA";
    
    private static final String EXTENDED_OP_NAME = "extended";
    private static final String FUNCTION_OP_ARG_NAME = "function";
    private static final String TARGET_FILTER_OP_ARG_NAME = "targetFilter";
    
    private final BNHaystackService haystackService = new BNHaystackService();
    private BHSite siteA = null;
    private BHSite siteB = null;
    private final BHEquip siteAequipA = new BHEquip();
    private final BComponent testFolder = new BFolder();
    private final BComponent testFolder2 = new BFolder();
    private BComponent addedEquip;

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
