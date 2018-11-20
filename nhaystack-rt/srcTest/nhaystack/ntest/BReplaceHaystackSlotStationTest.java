//
// Copyright 2017 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//     05 Dec 2017 Rowyn Brunner Creation
//

package nhaystack.ntest;

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
        whitelistOnlyComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("weeklySchedule", HNum.make(12))
            .add("navNameFormat", HNum.make(17, "ms"))
            .add("schedulable", HStr.make("very bad"))
            .toDict();
        blacklistOnlyComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("zone", HStr.make("calzone"))
            .add("water", HNum.make(59, "ft"))
            .add("weeklySchedule", HBool.make(true))
            .add("schedulable", HNum.make(13, "yr"))
            .toDict();
        blackAndWhitelistComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

        dict = new HDictBuilder()
            .add("weather", HList.make(EMPTY_HVAL_ARRAY))
            .add("screw", HDate.make(2017, 2, 14))
            .add("solar", HStr.make("yessir"))
            .add("irradiance", HMarker.VAL)
            .toDict();
        whitelistNonBIDataValueComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(dict));

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
        folder.add("WhitelistOnly", whitelistOnlyComponent);
        folder.add("BlacklistOnly", blacklistOnlyComponent);
        folder.add("WhiteAndBlackList", blackAndWhitelistComponent);
        folder.add("WhitelistNonBIDataValue", whitelistNonBIDataValueComponent);
        folder.add("BlacklistNonBIDataValue", blacklistNonBIDataValueComponent);
        folder.add("AllCombinations", allCombinationsComponent);
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

        tags = whitelistOnlyComponent.tags();
        assertEquals(tags.getAll().size(), 5, "Size of component tags");
        assertNotNull(whitelistOnlyComponent.get(BHDict.HAYSTACK_IDENTIFIER));
        TagTestUtil.assertTag(tags, "zone", BDouble.make(12));
        TagTestUtil.assertTag(tags, "water", BBoolean.make(false));
        TagTestUtil.assertTag(tags, "visibility", BRelTime.make(12));
        TagTestUtil.assertTag(tags, "waterCooled", BString.make("yes it did"));
        TagTestUtil.assertTag(tags, "pump", BMarker.MARKER);

        tags = blacklistOnlyComponent.tags();
        compDict = TagTestUtil.getCompDict(blacklistOnlyComponent);
        assertEquals(tags.getAll().size(), 0, "Size of component tags");
        assertEquals(compDict.size(), 3, "Size of haystack slot dict");
        TagTestUtil.assertDictEntry(compDict, "weeklySchedule", HNum.make(12));
        TagTestUtil.assertDictEntry(compDict, "navNameFormat", HNum.make(17, "ms"));
        TagTestUtil.assertDictEntry(compDict, "schedulable", HStr.make("very bad"));

        tags = blackAndWhitelistComponent.tags();
        compDict = TagTestUtil.getCompDict(blackAndWhitelistComponent);
        assertEquals(tags.getAll().size(), 2, "Size of component tags");
        assertEquals(compDict.size(), 2, "Size of haystack slot dict");
        TagTestUtil.assertTag(tags, "zone", BString.make("calzone"));
        TagTestUtil.assertTag(tags, "water", BDouble.make(59));
        TagTestUtil.assertDictEntry(compDict, "weeklySchedule", HBool.make(true));
        TagTestUtil.assertDictEntry(compDict, "schedulable", HNum.make(13, "yr"));

        tags = whitelistNonBIDataValueComponent.tags();
        compDict = TagTestUtil.getCompDict(whitelistNonBIDataValueComponent);
        assertEquals(tags.getAll().size(), 2, "Size of component tags");
        assertEquals(compDict.size(), 2, "Size of haystack slot dict");
        TagTestUtil.assertDictEntry(compDict, "weather", HList.make(EMPTY_HVAL_ARRAY));
        TagTestUtil.assertDictEntry(compDict, "screw", HDate.make(2017, 2, 14));
        TagTestUtil.assertTag(tags, "solar", BString.make("yessir"));
        TagTestUtil.assertTag(tags, "irradiance", BMarker.MARKER);

        tags = blacklistNonBIDataValueComponent.tags();
        compDict = TagTestUtil.getCompDict(blacklistNonBIDataValueComponent);
        assertEquals(tags.getAll().size(), 0, "Size of component tags");
        assertEquals(compDict.size(), 3, "Size of haystack slot dict");
        TagTestUtil.assertDictEntry(compDict, "weeklySchedule", HList.make(EMPTY_HVAL_ARRAY));
        TagTestUtil.assertDictEntry(compDict, "schedulable", HDate.make(2012, 12, 12));
        TagTestUtil.assertDictEntry(compDict, "navNameFormat", HBool.make(true));

        tags = allCombinationsComponent.tags();
        compDict = TagTestUtil.getCompDict(allCombinationsComponent);
        assertEquals(tags.getAll().size(), 2, "Size of component tags");
        assertEquals(compDict.size(), 3, "Size of haystack slot dict");
        TagTestUtil.assertDictEntry(compDict, "weeklySchedule", HNum.make(8));
        TagTestUtil.assertDictEntry(compDict, "schedulable", HList.make(EMPTY_HVAL_ARRAY));
        TagTestUtil.assertTag(tags, "weather", BBoolean.make(false));
        TagTestUtil.assertDictEntry(compDict, "heatExchanger", HDate.make(2017, 8, 22));
        TagTestUtil.assertTag(tags, "faceBypass", BMarker.MARKER);

        tags = geoLatGeoLonComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLatGeoLonComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        TagTestUtil.assertTag(tags, "geoCoord", BString.make("C(1.11,2.22)"));

        tags = geoLatComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLatComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        TagTestUtil.assertTag(tags, "geoCoord", BString.make("C(1.11,0.0)"));

        tags = geoLonComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLonComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        TagTestUtil.assertTag(tags, "geoCoord", BString.make("C(0.0,2.22)"));

        tags = geoLatGeoLonTagsComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLatGeoLonTagsComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        TagTestUtil.assertTag(tags, "geoCoord", BString.make("C(3.33,4.44)"));

        tags = geoLatTagComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLatTagComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        TagTestUtil.assertTag(tags, "geoCoord", BString.make("C(3.33,0.0)"));

        tags = geoLonTagComponent.tags();
        compDict = TagTestUtil.getCompDict(geoLonTagComponent);
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertEquals(compDict.size(), 0, "Size of haystack slot dict");
        TagTestUtil.assertTag(tags, "geoCoord", BString.make("C(0.0,4.44)"));
    }

    public static final HVal[] EMPTY_HVAL_ARRAY = new HVal[0];

    private BNHaystackService haystackService;
    BComponent noSlotComponent = new BComponent();
    BComponent whitelistOnlyComponent = new BComponent();
    BComponent blacklistOnlyComponent = new BComponent();
    BComponent blackAndWhitelistComponent = new BComponent();
    BComponent whitelistNonBIDataValueComponent = new BComponent();
    BComponent blacklistNonBIDataValueComponent = new BComponent();
    BComponent allCombinationsComponent = new BComponent();
    BComponent geoLatGeoLonComponent = new BComponent();
    BComponent geoLatComponent = new BComponent();
    BComponent geoLonComponent = new BComponent();
    BComponent geoLatGeoLonTagsComponent = new BComponent();
    BComponent geoLatTagComponent = new BComponent();
    BComponent geoLonTagComponent = new BComponent();
}
