//
// Copyright 2017 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//     05 Dec 2017  Rowyn Brunner  Creation
//     19 Jul 2019  Eric Anderson  Adjusted tests for adhoc tag support and no whitelist
//

package nhaystack.ntest;

import static nhaystack.ntest.helper.TagTestUtil.assertDictEntry;
import static nhaystack.ntest.helper.TagTestUtil.assertTag;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import java.io.IOException;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDouble;
import javax.baja.sys.BMarker;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BStation;
import javax.baja.sys.BString;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Id;
import javax.baja.tag.Tags;
import javax.baja.util.BFolder;
import nhaystack.BHDict;
import nhaystack.ntest.helper.TagTestUtil;
import nhaystack.server.BNHaystackService;
import org.projecthaystack.HBool;
import org.projecthaystack.HDate;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HList;
import org.projecthaystack.HMarker;
import org.projecthaystack.HNum;
import org.projecthaystack.HStr;
import org.projecthaystack.HVal;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.tridium.testng.BStationTestBase;

@NiagaraType
public class BReplaceHaystackSlotStationTest
    extends BStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.test.BReplaceHaystackSlotStationTest(2979906276)1.0$ @*/
/* Generated Tue Dec 05 13:55:27 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
    
    @Override
    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BReplaceHaystackSlotStationTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort) throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        BFolder folder = new BFolder();
        station.add("folder", folder);

        haystackService = new BNHaystackService();
        getServices().add("NHaystackService", haystackService);

        HDict dict;

        dict = new HDictBuilder()
            .add("zone", HNum.make(12))
            .add("water", HBool.make(false))
            .add("visibility", HNum.make(12, "ms"))
            .add("waterCooled", "yes it did")
            .add("pump", HMarker.VAL)
            .toDict();
        noBlacklistComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("weeklySchedule", HNum.make(12))
            .add("navNameFormat", HNum.make(17, "ms"))
            .add("schedulable", HStr.make("very bad"))
            .toDict();
        allBlacklistComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("zone", HStr.make("calzone"))
            .add("water", HNum.make(59, "ft"))
            .add("weeklySchedule", HBool.make(true))
            .add("schedulable", HNum.make(13, "yr"))
            .toDict();
        someBlacklistComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("weather", HList.make(EMPTY_HVAL_ARRAY))
            .add("screw", HDate.make(2017, 2, 14))
            .add("solar", HStr.make("yessir"))
            .add("irradiance", HMarker.VAL)
            .toDict();
        someNonBIDataValueComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("weeklySchedule", HList.make(EMPTY_HVAL_ARRAY))
            .add("schedulable", HDate.make(2012, 12, 12))
            .add("navNameFormat", HBool.make(true))
            .toDict();
        blacklistNonBIDataValueComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("weeklySchedule", HNum.make(8))
            .add("schedulable", HList.make(EMPTY_HVAL_ARRAY))
            .add("weather", HBool.make(false))
            .add("heatExchanger", HDate.make(2017, 8, 22))
            .add("faceBypass", HMarker.VAL)
            .toDict();
        allCombinationsComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("zone", HNum.make(12))
            .add("water", HBool.make(false))
            .add("adHoc1", HNum.make(12, "ms"))
            .add("adHoc2", "yes it did")
            .add("pump", HMarker.VAL)
            .toDict();
        someAdHocComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("zone", HNum.make(12))
            .add("water", HBool.make(false))
            .add("adHoc1", HNum.make(12, "ms"))
            .add("adHoc2", "yes it did")
            .add("weeklySchedule", HBool.make(true))
            .add("schedulable", HNum.make(13, "yr"))
            .toDict();
        someAdHocBlacklistComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("geoLat", HNum.make(1.11))
            .add("geoLon", HNum.make(2.22))
            .toDict();
        geoLatGeoLonComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("geoLat", HNum.make(1.11))
            .toDict();
        geoLatComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("geoLon", HNum.make(2.22))
            .toDict();
        geoLonComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("geoLat", HNum.make(1.11))
            .add("geoLon", HNum.make(2.22))
            .toDict();
        geoLatGeoLonTagsComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));
        geoLatGeoLonTagsComponent.tags().set(Id.newId("hs", "geoLat"), BDouble.make(3.33));
        geoLatGeoLonTagsComponent.tags().set(Id.newId("hs", "geoLon"), BDouble.make(4.44));

        dict = new HDictBuilder()
            .add("geoLat", HNum.make(1.11))
            .toDict();
        geoLatTagComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));
        geoLatTagComponent.tags().set(Id.newId("hs", "geoLat"), BDouble.make(3.33));

        dict = new HDictBuilder()
            .add("geoLon", HNum.make(2.22))
            .toDict();
        geoLonTagComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));
        geoLonTagComponent.tags().set(Id.newId("hs", "geoLon"), BDouble.make(4.44));

        folder.add("NoHaystackSlot", noSlotComponent);
        folder.add("NoBlacklist", noBlacklistComponent);
        folder.add("AllBlacklist", allBlacklistComponent);
        folder.add("SomeBlacklist", someBlacklistComponent);
        folder.add("SomeNonBIDataValue", someNonBIDataValueComponent);
        folder.add("BlacklistNonBIDataValue", blacklistNonBIDataValueComponent);
        folder.add("AllCombinations", allCombinationsComponent);
        folder.add("SomeAdHoc", someAdHocComponent);
        folder.add("SomeAdHocBlacklist", someAdHocBlacklistComponent);
        folder.add("geoLatGeoLon", geoLatGeoLonComponent);
        folder.add("geoLat", geoLatComponent);
        folder.add("geoLon", geoLonComponent);
        folder.add("geoLatGeoLonTag", geoLatGeoLonTagsComponent);
        folder.add("geoLatTag", geoLatTagComponent);
        folder.add("geoLonTag", geoLonTagComponent);
    }

    @Test
    public void testWholeStation()
        throws IOException
    {
        int count = 0;
        // wait 10 seconds for async operation to complete
        while (haystackService.getSchemaVersion() < 1 && count < 100)
        {
            try
            {
                Thread.sleep(100);
                count++;
            }
            catch (InterruptedException ignored)
            {
            }
        }
        if (haystackService.getSchemaVersion() < 1)
        {
            Assert.fail("Tag update not completed after 10 seconds, aborting test.");
        }

        Tags tags = noSlotComponent.tags();
        HDict compDict;
        assertEquals(tags.getAll().size(), 0, "Size of component tags");
        assertNull(noSlotComponent.get(BHDict.HAYSTACK_IDENTIFIER));

        tags = noBlacklistComponent.tags();
        assertEquals(tags.getAll().size(), 5, "Size of component tags");
        assertNotNull(noBlacklistComponent.get(BHDict.HAYSTACK_IDENTIFIER));
        assertTag(tags, "zone", BDouble.make(12));
        assertTag(tags, "water", BBoolean.make(false));
        assertTag(tags, "visibility", BRelTime.make(12));
        assertTag(tags, "waterCooled", BString.make("yes it did"));
        assertTag(tags, "pump", BMarker.MARKER);

        tags = allBlacklistComponent.tags();
        compDict = TagTestUtil.getCompDict(allBlacklistComponent);
        assertEquals(tags.getAll().size(), 0, "Size of component tags");
        assertEquals(compDict.size(), 3, "Size of haystack slot dict");
        assertDictEntry(compDict, "weeklySchedule", HNum.make(12));
        assertDictEntry(compDict, "navNameFormat", HNum.make(17, "ms"));
        assertDictEntry(compDict, "schedulable", HStr.make("very bad"));

        tags = someBlacklistComponent.tags();
        compDict = TagTestUtil.getCompDict(someBlacklistComponent);
        assertEquals(tags.getAll().size(), 2, "Size of component tags");
        assertEquals(compDict.size(), 2, "Size of haystack slot dict");
        assertTag(tags, "zone", BString.make("calzone"));
        assertTag(tags, "water", BDouble.make(59));
        assertDictEntry(compDict, "weeklySchedule", HBool.make(true));
        assertDictEntry(compDict, "schedulable", HNum.make(13, "yr"));

        tags = someNonBIDataValueComponent.tags();
        compDict = TagTestUtil.getCompDict(someNonBIDataValueComponent);
        assertEquals(tags.getAll().size(), 2, "Size of component tags");
        assertEquals(compDict.size(), 2, "Size of haystack slot dict");
        assertDictEntry(compDict, "weather", HList.make(EMPTY_HVAL_ARRAY));
        assertDictEntry(compDict, "screw", HDate.make(2017, 2, 14));
        assertTag(tags, "solar", BString.make("yessir"));
        assertTag(tags, "irradiance", BMarker.MARKER);

        tags = blacklistNonBIDataValueComponent.tags();
        compDict = TagTestUtil.getCompDict(blacklistNonBIDataValueComponent);
        assertEquals(tags.getAll().size(), 0, "Size of component tags");
        assertEquals(compDict.size(), 3, "Size of haystack slot dict");
        assertDictEntry(compDict, "weeklySchedule", HList.make(EMPTY_HVAL_ARRAY));
        assertDictEntry(compDict, "schedulable", HDate.make(2012, 12, 12));
        assertDictEntry(compDict, "navNameFormat", HBool.make(true));

        tags = allCombinationsComponent.tags();
        compDict = TagTestUtil.getCompDict(allCombinationsComponent);
        assertEquals(tags.getAll().size(), 2, "Size of component tags");
        assertEquals(compDict.size(), 3, "Size of haystack slot dict");
        assertDictEntry(compDict, "weeklySchedule", HNum.make(8));
        assertDictEntry(compDict, "schedulable", HList.make(EMPTY_HVAL_ARRAY));
        assertTag(tags, "weather", BBoolean.make(false));
        assertDictEntry(compDict, "heatExchanger", HDate.make(2017, 8, 22));
        assertTag(tags, "faceBypass", BMarker.MARKER);

        tags = someAdHocComponent.tags();
        compDict = TagTestUtil.getCompDict(someAdHocComponent);
        assertEquals(tags.getAll().size(), 5, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        assertTag(tags, "zone", BDouble.make(12));
        assertTag(tags, "water", BBoolean.make(false));
        assertTag(tags, "adHoc1", BRelTime.make(12));
        assertTag(tags, "adHoc2", BString.make("yes it did"));
        assertTag(tags, "pump", BMarker.MARKER);

        tags = someAdHocBlacklistComponent.tags();
        compDict = TagTestUtil.getCompDict(someAdHocBlacklistComponent);
        assertEquals(tags.getAll().size(), 4, "Size of component tags");
        assertEquals(compDict.size(), 2, "Size of haystack slot dict");
        assertTag(tags, "zone", BDouble.make(12));
        assertTag(tags, "water", BBoolean.make(false));
        assertTag(tags, "adHoc1", BRelTime.make(12));
        assertTag(tags, "adHoc2", BString.make("yes it did"));
        assertDictEntry(compDict, "weeklySchedule", HBool.make(true));
        assertDictEntry(compDict, "schedulable", HNum.make(13, "yr"));

        tags = geoLatGeoLonComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLatGeoLonComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        assertTag(tags, "geoCoord", BString.make("C(1.11,2.22)"));

        tags = geoLatComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLatComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        assertTag(tags, "geoCoord", BString.make("C(1.11,0.0)"));

        tags = geoLonComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLonComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        assertTag(tags, "geoCoord", BString.make("C(0.0,2.22)"));

        tags = geoLatGeoLonTagsComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLatGeoLonTagsComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        assertTag(tags, "geoCoord", BString.make("C(3.33,4.44)"));

        tags = geoLatTagComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLatTagComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        assertTag(tags, "geoCoord", BString.make("C(3.33,0.0)"));

        tags = geoLonTagComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLonTagComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        assertTag(tags, "geoCoord", BString.make("C(0.0,4.44)"));
    }

    public static final HVal[] EMPTY_HVAL_ARRAY = new HVal[0];

    private BNHaystackService haystackService;
    BComponent noSlotComponent = new BComponent();
    BComponent noBlacklistComponent = new BComponent();
    BComponent allBlacklistComponent = new BComponent();
    BComponent someBlacklistComponent = new BComponent();
    BComponent someNonBIDataValueComponent = new BComponent();
    BComponent blacklistNonBIDataValueComponent = new BComponent();
    BComponent allCombinationsComponent = new BComponent();
    BComponent someAdHocComponent = new BComponent();
    BComponent someAdHocBlacklistComponent = new BComponent();
    BComponent geoLatGeoLonComponent = new BComponent();
    BComponent geoLatComponent = new BComponent();
    BComponent geoLonComponent = new BComponent();
    BComponent geoLatGeoLonTagsComponent = new BComponent();
    BComponent geoLatTagComponent = new BComponent();
    BComponent geoLonTagComponent = new BComponent();
}
