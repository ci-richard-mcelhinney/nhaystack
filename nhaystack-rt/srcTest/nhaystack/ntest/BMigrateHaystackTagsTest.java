//
// Copyright 2017 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//     05 Dec 2017 Rowyn Brunner Creation
//     19 Jul 2019 Added tests for multi-namespace support
//

package nhaystack.ntest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Optional;
import javax.baja.control.BNumericPoint;
import javax.baja.data.BIDataValue;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDouble;
import javax.baja.sys.BMarker;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BRelation;
import javax.baja.sys.BStation;
import javax.baja.sys.BString;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Id;
import javax.baja.tag.Relation;
import javax.baja.tag.Relations;
import javax.baja.tag.Tag;
import javax.baja.tag.Tags;
import javax.baja.util.BFolder;

import nhaystack.BHDict;
import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.server.HaystackSlotUtil;
import org.projecthaystack.HDate;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HList;
import org.projecthaystack.HMarker;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HStr;
import org.projecthaystack.HVal;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.tridium.sys.tag.ComponentRelations;
import com.tridium.sys.tag.ComponentTags;

@Test
@NiagaraType
public class BMigrateHaystackTagsTest
    extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.test.BMigrateHaystackTagsTest(2979906276)1.0$ @*/
/* Generated Wed Nov 29 10:17:48 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BMigrateHaystackTagsTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort)
        throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        BFolder pointsFolder = new BFolder();
        pointsFolder.add("Point1", point1);
        pointsFolder.add("Point2", point2);
        pointsFolder.add("Point3", point3);
        pointsFolder.add("Point4", point4);
        pointsFolder.add("Point5", point5);
        pointsFolder.add("Point6", point6);
        pointsFolder.add("Point7", point7);

        station.add("Points", pointsFolder);
    }

    @BeforeMethod
    public void beforeMethod()
    {
        nhaystackService.setPrioritizedNamespaces("hs");

        BStation station = stationHandler.getStation();
        if (station.get("Component") != null)
        {
            station.remove("Component");
        }
        station.add("Component", component = new BComponent());
    }

    public void haystackSlotNotAddedWhenMigratingTags()
    {
        BComponent component = new BComponent();
        assertNull(BHDict.findTagAnnotation(component), "before");
        HaystackSlotUtil.migrateHaystackTags(component);
        assertNull(BHDict.findTagAnnotation(component), "after");
    }

    public void migrateAllTagsNotOnBlacklist()
    {
        BComponent component = new BComponent();

        HDict dict = new HDictBuilder()
            .add("absorption", HMarker.VAL)
            .add("ac", HNum.make(8.22, "kJ"))
            .add("active", "some string")
            .add("ahu", true)
            .add("air", HNum.make(417, "ms"))
            .add("tag1", HMarker.VAL)
            .add("tag2", HNum.make(8.22, "kJ"))
            .add("tag3", "some string")
            .add("tag4", true)
            .add("tag5", HNum.make(417, "ms"))
            .toDict();
        component.add("haystack", BHDict.make(dict));
        assertNotNull(component.get("haystack"), "before");

        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertNotNull(haystackSlot, "after");
        assertEquals(haystackSlot.getDict().size(), 0, "Size of haystack slot dict");

        Tags tags = component.tags();
        assertEquals(tags.getAll().size(), 10, "Size of component's tags");
        assertTag(tags, "absorption", BMarker.MARKER);
        assertTag(tags, "ac", BDouble.make(8.22));
        assertTag(tags, "active", BString.make("some string"));
        assertTag(tags, "ahu", BBoolean.TRUE);
        assertTag(tags, "air", BRelTime.make(417));
        assertTag(tags, "tag1", BMarker.MARKER);
        assertTag(tags, "tag2", BDouble.make(8.22));
        assertTag(tags, "tag3", BString.make("some string"));
        assertTag(tags, "tag4", BBoolean.TRUE);
        assertTag(tags, "tag5", BRelTime.make(417));
    }

    public void skipNonBIDataValueTags()
    {
        BComponent component = new BComponent();

        HDict dict = new HDictBuilder()
            .add("ac", HDate.make(2012, 8, 22)) // non-BIDataValue
            .add("tag1", HDate.make(2012, 8, 22)) // ad hoc, non-BIDataValue
            .add("active", HList.make(EMPTY_HVAL_ARRAY)) // non-BIDataValue
            .add("tag2", HList.make(EMPTY_HVAL_ARRAY)) // ad hoc, non-BIDataValue
            .add("ahu", true)
            .add("tag3", true) // ad hoc
            .toDict();
        component.add("haystack", BHDict.make(dict));
        assertNotNull(component.get("haystack"), "before");

        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertNotNull(haystackSlot, "after");
        assertEquals(haystackSlot.getDict().size(), 4, "Size of haystack slot dict");
        assertDictEntry(haystackSlot.getDict(), "ac", HDate.make(2012, 8, 22));
        assertDictEntry(haystackSlot.getDict(), "tag1", HDate.make(2012, 8, 22));
        assertDictEntry(haystackSlot.getDict(), "active", HList.make(EMPTY_HVAL_ARRAY));
        assertDictEntry(haystackSlot.getDict(), "tag2", HList.make(EMPTY_HVAL_ARRAY));

        Tags tags = component.tags();
        assertEquals(tags.getAll().size(), 2, "Size of component tags");
        assertTag(tags, "ahu", BBoolean.make(true));
        assertTag(tags, "tag3", BBoolean.make(true));
    }

    public void skipBlacklistTags()
    {
        BComponent component = new BComponent();

        HDict dict = new HDictBuilder()
            .add("absorption", HMarker.VAL)
            .add("tag1", HMarker.VAL)
            .add("weeklySchedule", "scheduleZincString") // blacklist
            .add("ac", HDate.make(2012, 8, 22)) // non-BIDataValue
            .add("tag2", HList.make(EMPTY_HVAL_ARRAY)) // non-BIDataValue
            .add("navNameFormat", "%parent.displayName%") // blacklist
            .add("active", "some string")
            .add("tag3", "some string")
            .add("schedulable", 1) // blacklist
            .add("ahu", true)
            .add("tag4", true)
            .add("geoLat", 1.11) // blacklist
            .add("air", HNum.make(417, "ms"))
            .add("tag5", HNum.make(417, "ms"))
            .add("geoLon", 2.22) // blacklist
            .toDict();
        component.add("haystack", BHDict.make(dict));
        assertNotNull(component.get("haystack"), "before");

        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertNotNull(haystackSlot, "after");
        assertEquals(haystackSlot.getDict().size(), 5, "Size of haystack slot dict");
        assertDictEntry(haystackSlot.getDict(), "weeklySchedule", HStr.make("scheduleZincString"));
        assertDictEntry(haystackSlot.getDict(), "ac", HDate.make(2012, 8, 22));
        assertDictEntry(haystackSlot.getDict(), "tag2", HList.make(EMPTY_HVAL_ARRAY));
        assertDictEntry(haystackSlot.getDict(), "navNameFormat", HStr.make("%parent.displayName%"));
        assertDictEntry(haystackSlot.getDict(), "schedulable", HNum.make(1));

        Tags tags = component.tags();
        assertEquals(tags.getAll().size(), 9, "Size of component tags");
        assertTag(tags, "absorption", BMarker.MARKER);
        assertTag(tags, "tag1", BMarker.MARKER);
        assertTag(tags, "active", BString.make("some string"));
        assertTag(tags, "tag3", BString.make("some string"));
        assertTag(tags, "ahu", BBoolean.make(true));
        assertTag(tags, "tag4", BBoolean.make(true));
        assertTag(tags, "air", BRelTime.make(417));
        assertTag(tags, "tag5", BRelTime.make(417));
        assertTag(tags, "geoCoord", BString.make("C(1.11,2.22)"));
    }

    public void migrateTagsRelationsInNeitherNamespace()
    {
        HDict dict = new HDictBuilder()
            .add("markerTag", HMarker.VAL)
            .add("valueTag", 10.0)
            .add("refTag", HRef.make("C.Points.Point1"))
            .toDict();
        component.add("haystack", BHDict.make(dict));

        nhaystackService.setPrioritizedNamespaces("a,b");
        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertEquals(haystackSlot.getDict().size(), 0, "Size of haystack slot dict");

        assertTags(new ComponentTags(component),
            Tag.newTag("a:markerTag"),
            Tag.newTag("a:valueTag", 10.0));
        assertRelations(new ComponentRelations(component),
            new BRelation(Id.newId("a:refTag"), point1));
    }

    public void migrateTagsRelationsInHigherPriorityNamespace()
    {
        HDict dict = new HDictBuilder()
            .add("markerTag", HMarker.VAL)
            .add("valueTag1", 10.0)
            .add("valueTag2", 35.0)
            .add("refTag1", HRef.make("C.Points.Point1"))
            .add("refTag2", HRef.make("C.Points.Point2"))
            .toDict();
        component.add("haystack", BHDict.make(dict));

        Tags tags = new ComponentTags(component);
        tags.set(Tag.newTag("a:markerTag"));
        tags.set(Tag.newTag("a:valueTag1", 10.0));
        tags.set(Tag.newTag("a:valueTag2", 15.0));
        Relations relations = new ComponentRelations(component);
        relations.add(new BRelation(Id.newId("a:refTag1"), point1));
        relations.add(new BRelation(Id.newId("a:refTag2"), point1));

        nhaystackService.setPrioritizedNamespaces("a,b");
        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertEquals(haystackSlot.getDict().size(), 0, "Size of haystack slot dict");

        assertTags(new ComponentTags(component),
            Tag.newTag("a:markerTag"),
            Tag.newTag("a:valueTag1", 10.0),
            Tag.newTag("a:valueTag2", 35.0));
        assertRelations(new ComponentRelations(component),
            new BRelation(Id.newId("a:refTag1"), point1),
            new BRelation(Id.newId("a:refTag2"), point2));
    }

    public void migrateTagsRelationsInLowerPriorityNamespace()
    {
        HDict dict = new HDictBuilder()
            .add("markerTag", HMarker.VAL)
            .add("valueTag1", 10.0)
            .add("valueTag2", 35.0)
            .add("refTag1", HRef.make("C.Points.Point1"))
            .add("refTag2", HRef.make("C.Points.Point2"))
            .toDict();
        component.add("haystack", BHDict.make(dict));

        Tags tags = new ComponentTags(component);
        tags.set(Tag.newTag("b:markerTag"));
        tags.set(Tag.newTag("b:valueTag1", 10.0));
        tags.set(Tag.newTag("b:valueTag2", 15.0));
        Relations relations = new ComponentRelations(component);
        relations.add(new BRelation(Id.newId("b:refTag1"), point1));
        relations.add(new BRelation(Id.newId("b:refTag2"), point1));

        nhaystackService.setPrioritizedNamespaces("a,b");
        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertEquals(haystackSlot.getDict().size(), 0, "Size of haystack slot dict");

        assertTags(new ComponentTags(component),
            Tag.newTag("b:markerTag"),
            Tag.newTag("b:valueTag1", 10.0),
            Tag.newTag("b:valueTag2", 35.0));
        assertRelations(new ComponentRelations(component),
            new BRelation(Id.newId("b:refTag1"), point1),
            new BRelation(Id.newId("b:refTag2"), point2));
    }

    public void migrateTagsRelationsInBothNamespaces()
    {
        HDict dict = new HDictBuilder()
            .add("markerTag", HMarker.VAL)
            .add("valueTag1", 10.0)
            .add("valueTag2", 35.0)
            .add("refTag1", HRef.make("C.Points.Point1"))
            .add("refTag2", HRef.make("C.Points.Point2"))
            .toDict();
        component.add("haystack", BHDict.make(dict));

        Tags tags = new ComponentTags(component);
        tags.set(Tag.newTag("a:markerTag"));
        tags.set(Tag.newTag("a:valueTag1", 10.0));
        tags.set(Tag.newTag("a:valueTag2", 15.0));
        tags.set(Tag.newTag("b:markerTag"));
        tags.set(Tag.newTag("b:valueTag1", 20.0));
        tags.set(Tag.newTag("b:valueTag2", 25.0));
        Relations relations = new ComponentRelations(component);
        relations.add(new BRelation(Id.newId("a:refTag1"), point1));
        relations.add(new BRelation(Id.newId("a:refTag2"), point1));
        relations.add(new BRelation(Id.newId("b:refTag1"), point3));
        relations.add(new BRelation(Id.newId("b:refTag2"), point4));

        nhaystackService.setPrioritizedNamespaces("a,b");
        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertEquals(haystackSlot.getDict().size(), 0, "Size of haystack slot dict");

        assertTags(new ComponentTags(component),
            Tag.newTag("a:markerTag"),
            Tag.newTag("a:valueTag1", 10.0),
            Tag.newTag("a:valueTag2", 35.0),
            Tag.newTag("b:markerTag"),
            Tag.newTag("b:valueTag1", 20.0),
            Tag.newTag("b:valueTag2", 25.0));
        assertRelations(new ComponentRelations(component),
            new BRelation(Id.newId("a:refTag1"), point1),
            new BRelation(Id.newId("a:refTag2"), point2),
            new BRelation(Id.newId("b:refTag1"), point3),
            new BRelation(Id.newId("b:refTag2"), point4));
    }

    public void migrateTagsRelationsMixedNamespaces()
    {
        HDict dict = new HDictBuilder()
            .add("markerTag1", HMarker.VAL) // Neither
            .add("markerTag2", HMarker.VAL) // Higher only
            .add("markerTag3", HMarker.VAL) // Lower only
            .add("markerTag4", HMarker.VAL) // Both
            .add("valueTag1", 10.0) // Neither
            .add("valueTag2", 15.0) // Higher only- value is same
            .add("valueTag3", 20.0) // Higher only- value is different
            .add("valueTag4", 25.0) // Lower only- value is same
            .add("valueTag5", 30.0) // Lower only- value is different
            .add("valueTag6", 35.0) // Both- value is same as higher
            .add("valueTag7", 40.0) // Both- value is different than higher
            .add("refTag1", HRef.make("C.Points.Point1"))
            .add("refTag2", HRef.make("C.Points.Point2"))
            .add("refTag3", HRef.make("C.Points.Point3"))
            .add("refTag4", HRef.make("C.Points.Point4"))
            .add("refTag5", HRef.make("C.Points.Point5"))
            .add("refTag6", HRef.make("C.Points.Point6"))
            .add("refTag7", HRef.make("C.Points.Point7"))
            .toDict();
        component.add("haystack", BHDict.make(dict));

        Tags tags = new ComponentTags(component);
        tags.set(Tag.newTag("a:markerTag2"));
        tags.set(Tag.newTag("a:markerTag4"));
        tags.set(Tag.newTag("a:valueTag2", 15.0));
        tags.set(Tag.newTag("a:valueTag3", 5.0));
        tags.set(Tag.newTag("a:valueTag6", 35.0));
        tags.set(Tag.newTag("a:valueTag7", 4.0));
        tags.set(Tag.newTag("b:markerTag3"));
        tags.set(Tag.newTag("b:markerTag4"));
        tags.set(Tag.newTag("b:valueTag4", 25.0));
        tags.set(Tag.newTag("b:valueTag5", 3.0));
        tags.set(Tag.newTag("b:valueTag6", 2.0));
        tags.set(Tag.newTag("b:valueTag7", 1.0));
        Relations relations = new ComponentRelations(component);
        relations.add(new BRelation(Id.newId("a:refTag2"), point2));
        relations.add(new BRelation(Id.newId("a:refTag3"), point1));
        relations.add(new BRelation(Id.newId("a:refTag6"), point6));
        relations.add(new BRelation(Id.newId("a:refTag7"), point1));
        relations.add(new BRelation(Id.newId("b:refTag4"), point4));
        relations.add(new BRelation(Id.newId("b:refTag5"), point1));
        relations.add(new BRelation(Id.newId("b:refTag6"), point1));
        relations.add(new BRelation(Id.newId("b:refTag7"), point2));

        nhaystackService.setPrioritizedNamespaces("a,b");
        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertEquals(haystackSlot.getDict().size(), 0, "Size of haystack slot dict");

        assertTags(new ComponentTags(component),
            Tag.newTag("a:markerTag1"),
            Tag.newTag("a:markerTag2"),
            Tag.newTag("a:markerTag4"),
            Tag.newTag("a:valueTag1", 10.0),
            Tag.newTag("a:valueTag2", 15.0),
            Tag.newTag("a:valueTag3", 20.0),
            Tag.newTag("a:valueTag6", 35.0),
            Tag.newTag("a:valueTag7", 40.0),
            Tag.newTag("b:markerTag3"),
            Tag.newTag("b:markerTag4"),
            Tag.newTag("b:valueTag4", 25.0),
            Tag.newTag("b:valueTag5", 30.0),
            Tag.newTag("b:valueTag6", 2.0),
            Tag.newTag("b:valueTag7", 1.0));
        assertRelations(new ComponentRelations(component),
            new BRelation(Id.newId("a:refTag1"), point1),
            new BRelation(Id.newId("a:refTag2"), point2),
            new BRelation(Id.newId("a:refTag3"), point3),
            new BRelation(Id.newId("a:refTag6"), point6),
            new BRelation(Id.newId("a:refTag7"), point7),
            new BRelation(Id.newId("b:refTag4"), point4),
            new BRelation(Id.newId("b:refTag5"), point5),
            new BRelation(Id.newId("b:refTag6"), point1),
            new BRelation(Id.newId("b:refTag7"), point2));
    }

    private static void assertTag(Tags tags, String tagName, BIDataValue expectedValue)
    {
        Optional<BIDataValue> tag = tags.get(Id.newId("hs", tagName));
        assertTrue(tag.isPresent(), "Missing hs:" + tagName);
        assertEquals(tag.get(), expectedValue, "Value of hs:" + tagName + " not correct");
    }

    private static void assertTags(Tags actualTags, Tag... expectedTags)
    {
        assertEquals(actualTags.getAll().size(), expectedTags.length, "Size of component tags");
        for (Tag expected : expectedTags)
        {
            Optional<BIDataValue> actual = actualTags.get(expected.getId());
            assertTrue(actual.isPresent(), "Missing " + tagToString(expected));
            assertEquals(actual.get(), expected.getValue(), "Value of " + tagToString(expected) + " not correct");
        }
    }

    private static void assertRelations(Relations actualRelations, BRelation... expectedRelations)
    {
        assertEquals(actualRelations.getAll().size(), expectedRelations.length, "Size of component relations");
        for (Relation expected : expectedRelations)
        {
            Optional<Relation> actual = actualRelations.get(expected.getId(), expected.getEndpoint());
            assertTrue(actual.isPresent(), "Missing " + expected);
        }
    }

    private static void assertDictEntry(HDict dict, String key, HVal expectedValue)
    {
        HVal actualValue = dict.get(key, false);
        assertNotNull(actualValue, "Key " + key + " not found");
        assertEquals(actualValue, expectedValue, "Value of " + key + "not correct");
    }

    private static String tagToString(Tag tag)
    {
        return "Tag: id=" + tag.getId() + ", value=" + tag.getValue();
    }

    private static final HVal[] EMPTY_HVAL_ARRAY = new HVal[0];

    private BComponent component;
    private final BNumericPoint point1 = new BNumericPoint();
    private final BNumericPoint point2 = new BNumericPoint();
    private final BNumericPoint point3 = new BNumericPoint();
    private final BNumericPoint point4 = new BNumericPoint();
    private final BNumericPoint point5 = new BNumericPoint();
    private final BNumericPoint point6 = new BNumericPoint();
    private final BNumericPoint point7 = new BNumericPoint();
}
