//
// Copyright 2018 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   11 Dec 2018  Andrew Saunders    Creation
//
package nhaystack.ntest;

import com.tridium.haystack.BHsTagDictionary;
import com.tridium.testng.BStationTestBase;
import com.tridium.testng.TestUtil;
import nhaystack.server.BNHaystackService;
import org.projecthaystack.*;
import org.projecthaystack.client.HClient;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.baja.control.BBooleanPoint;
import javax.baja.control.BBooleanWritable;
import javax.baja.control.BNumericPoint;
import javax.baja.control.BNumericWritable;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;
import javax.baja.tag.Id;
import javax.baja.tag.Relation;
import javax.baja.tag.Tag;
import javax.baja.tagdictionary.BTagDictionaryService;
import javax.baja.util.BFolder;
import javax.baja.util.BServiceContainer;
import javax.baja.web.BWebService;
import java.io.File;

import static nhaystack.ntest.helper.NHaystackTestUtil.*;
import static org.testng.Assert.assertEquals;

@NiagaraType
@SuppressWarnings("MagicNumber")
public class BHaystackClientTest2 extends BStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.test.BReplaceHaystackSlotStationTest(2979906276)1.0$ @*/
/* Generated Tue Dec 05 13:55:27 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

    @Override
    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHaystackClientTest2.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

/*
*   This test will test client access to a station that has folders tagged as site and equip. BHSite and BHEquip components
*   are NOT used.  A siteRef relation will be added to each of the equip tagged folders to their respective site tagged folders.
*   Some points are added as a child to the equip tagged folders, where they will have an implied equipRef relation.
*   Some other points are added as a child to a playground folder, An equipRef relation will be added to these points relating
*   each to the desired equip tagged folder.
*
*   This should result inthe followin nav organization
*
*   siteFolder1
*       equipFolder1
*            boolPoint1
*            numericPoint1
*            playBoolPoint1
*            playNumPoint1
*   siteFolder2
*       equipFolder2
*            boolPoint2
*            numericPoint2
*            playBoolPoint2
*            playNumPoint2
*
 */

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

        services.add("tagDictionaryService", new BTagDictionaryService());
        BTagDictionaryService service = (BTagDictionaryService)Sys.getService(BTagDictionaryService.TYPE);
        service.add("haystack", new BHsTagDictionary());
        haystackDict = (BHsTagDictionary)service.getSmartTagDictionary("hs").orElseThrow(() -> new Exception("No haystack dictionary"));

        services.add("NHaystackService", haystackService);
        haystackService.setSchemaVersion(1);

        BWebService webService = getWebService();
        webService.setHttpEnabled(true);
        webService.setHttpsOnly(false);
        webService.setHttpsEnabled(true);


        BFolder playground = addChild(PLAYGROUND, new BFolder(), station);
        playground.add("siteFolder1", siteFolder1);
        playground.add("siteFolder2", siteFolder2);
        siteFolder1.tags().set(new Tag(ID_SITE, BMarker.MARKER));
        siteFolder2.tags().set(new Tag(ID_SITE, BMarker.MARKER));
        siteFolder1.add("equipFolder1", equipFolder1);
        siteFolder2.add("equipFolder2", equipFolder2);
        equipFolder1.tags().set(new Tag(ID_EQUIP, BMarker.MARKER));
        equipFolder2.tags().set(new Tag(ID_EQUIP, BMarker.MARKER));

        equipFolder1.relations().add(new BRelation(SITE_REF_ID, siteFolder1, Relation.OUTBOUND));
        equipFolder2.relations().add(new BRelation(SITE_REF_ID, siteFolder2, Relation.OUTBOUND));

        equipFolder1.add("boolPoint1", new BBooleanWritable());
        equipFolder1.add("numericPoint1", new BNumericWritable());
        equipFolder2.add("boolPoint2", new BBooleanWritable());
        equipFolder2.add("numericPoint2", new BNumericWritable());

        playground.add("playBoolPoint1", boolPoint1);
        playground.add("playBoolPoint2", boolPoint2);
        playground.add("playNumPoint1", numPoint1);
        playground.add("playNumPoint2", numPoint2);

        boolPoint1.relations().add(new BRelation(EQUIP_REF_ID, equipFolder1, Relation.OUTBOUND));
        numPoint1.relations().add(new BRelation(EQUIP_REF_ID, equipFolder1, Relation.OUTBOUND));
        boolPoint2.relations().add(new BRelation(EQUIP_REF_ID, equipFolder2, Relation.OUTBOUND));
        numPoint2.relations().add(new BRelation(EQUIP_REF_ID, equipFolder2, Relation.OUTBOUND));

    }

    @BeforeTest(alwaysRun = true, description = "Setup and start test station")
    @Override
    public void setupStation() throws Exception
    {
        super.setupStation();

        TestUtil.waitFor(10, () -> "3.0.2".equals(haystackDict.getVersion()), "Waiting for asynchronous import to finish");
        assertEquals(haystackDict.getVersion(), "3.0.2", "dictionary version after import");
    }

//////////////////////////////////////////////////////////////////////////
// Reads
//////////////////////////////////////////////////////////////////////////

    @Test(priority = 40)
    public void verifyRead() throws InterruptedException
    {
        client = client == null ? openClient() : client;
        
        HGrid grid = client.readAll("id");

        assertEquals(grid.numRows(), 8);
        assertEquals(grid.row(0).id(), HRef.make("S.siteFolder1.equipFolder1.boolPoint1"));
        assertEquals(grid.row(0).get(SITE_REF_TAG_NAME), HRef.make("S.siteFolder1"));
        assertEquals(grid.row(0).get(EQUIP_REF_TAG_NAME), HRef.make("S.siteFolder1.equipFolder1"));
        assertEquals(grid.row(1).id(), HRef.make("S.siteFolder1.equipFolder1.numericPoint1"));
        assertEquals(grid.row(1).get(SITE_REF_TAG_NAME), HRef.make("S.siteFolder1"));
        assertEquals(grid.row(1).get(EQUIP_REF_TAG_NAME), HRef.make("S.siteFolder1.equipFolder1"));
        assertEquals(grid.row(2).id(), HRef.make("S.siteFolder2.equipFolder2.boolPoint2"));
        assertEquals(grid.row(2).get(SITE_REF_TAG_NAME), HRef.make("S.siteFolder2"));
        assertEquals(grid.row(2).get(EQUIP_REF_TAG_NAME), HRef.make("S.siteFolder2.equipFolder2"));
        assertEquals(grid.row(3).id(), HRef.make("S.siteFolder2.equipFolder2.numericPoint2"));
        assertEquals(grid.row(3).get(SITE_REF_TAG_NAME), HRef.make("S.siteFolder2"));
        assertEquals(grid.row(3).get(EQUIP_REF_TAG_NAME), HRef.make("S.siteFolder2.equipFolder2"));
        assertEquals(grid.row(4).id(), HRef.make("S.siteFolder1.equipFolder1.playBoolPoint1"));
        assertEquals(grid.row(4).get(SITE_REF_TAG_NAME), HRef.make("S.siteFolder1"));
        assertEquals(grid.row(4).get(EQUIP_REF_TAG_NAME), HRef.make("S.siteFolder1.equipFolder1"));
        assertEquals(grid.row(5).id(), HRef.make("S.siteFolder2.equipFolder2.playBoolPoint2"));
        assertEquals(grid.row(5).get(SITE_REF_TAG_NAME), HRef.make("S.siteFolder2"));
        assertEquals(grid.row(5).get(EQUIP_REF_TAG_NAME), HRef.make("S.siteFolder2.equipFolder2"));
        assertEquals(grid.row(6).id(), HRef.make("S.siteFolder1.equipFolder1.playNumPoint1"));
        assertEquals(grid.row(6).get(SITE_REF_TAG_NAME), HRef.make("S.siteFolder1"));
        assertEquals(grid.row(6).get(EQUIP_REF_TAG_NAME), HRef.make("S.siteFolder1.equipFolder1"));
        assertEquals(grid.row(7).id(), HRef.make("S.siteFolder2.equipFolder2.playNumPoint2"));
        assertEquals(grid.row(7).get(SITE_REF_TAG_NAME), HRef.make("S.siteFolder2"));
        assertEquals(grid.row(7).get(EQUIP_REF_TAG_NAME), HRef.make("S.siteFolder2.equipFolder2"));
    }

//////////////////////////////////////////////////////////////////////////
// Nav
//////////////////////////////////////////////////////////////////////////

    @Test(priority = 50)
    public void verifyNav() throws Exception
    {
        client = client == null ? openClient() : client;

        HGrid grid = client.call("read", makeIdGrid(HUri.make("sep:/siteFolder1")));
        assertEquals(grid.numRows(), 1);
        assertEquals(grid.row(0).id(), HRef.make("S.siteFolder1"));

        grid = client.call("read", makeIdGrid(HUri.make("sep:/siteFolder1/equipFolder1")));
        assertEquals(grid.numRows(), 1);
        assertEquals(grid.row(0).id(), HRef.make("S.siteFolder1.equipFolder1"));

        HGrid n = makeNavGrid(HStr.make("sep:/siteFolder1/equipFolder1"));
        grid = client.call("nav", n);

        assertEquals(grid.numRows(), 4);
        assertEquals(grid.row(0).id(), HRef.make("S.siteFolder1.equipFolder1.boolPoint1"));
        assertEquals(grid.row(1).id(), HRef.make("S.siteFolder1.equipFolder1.numericPoint1"));
        assertEquals(grid.row(2).id(), HRef.make("S.siteFolder1.equipFolder1.playBoolPoint1"));
        assertEquals(grid.row(3).id(), HRef.make("S.siteFolder1.equipFolder1.playNumPoint1"));

        n = makeNavGrid(HStr.make("sep:/siteFolder2/equipFolder2"));
        grid = client.call("nav", n);

        assertEquals(grid.numRows(), 4);
        assertEquals(grid.row(0).id(), HRef.make("S.siteFolder2.equipFolder2.boolPoint2"));
        assertEquals(grid.row(1).id(), HRef.make("S.siteFolder2.equipFolder2.numericPoint2"));
        assertEquals(grid.row(2).id(), HRef.make("S.siteFolder2.equipFolder2.playBoolPoint2"));
        assertEquals(grid.row(3).id(), HRef.make("S.siteFolder2.equipFolder2.playNumPoint2"));
    }

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

    private static final String PLAYGROUND = "Playground";

    private static final Id ID_SITE = Id.newId("hs:site");
    private static final Id ID_EQUIP = Id.newId("hs:equip");
    
    private final BNHaystackService haystackService = new BNHaystackService();
    private final BComponent siteFolder1 = new BFolder();
    private final BComponent siteFolder2 = new BFolder();
    private final BComponent equipFolder1 = new BFolder();
    private final BComponent equipFolder2 = new BFolder();
    private final BBooleanPoint boolPoint1 = new BBooleanPoint();
    private final BBooleanPoint boolPoint2 = new BBooleanPoint();
    private final BNumericPoint numPoint1 = new BNumericPoint();
    private final BNumericPoint numPoint2 = new BNumericPoint();

    private BHsTagDictionary haystackDict;

}
