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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import javax.baja.data.BIDataValue;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDouble;
import javax.baja.sys.BMarker;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BString;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Id;
import javax.baja.tag.Tags;
import javax.baja.test.BTestNg;

import nhaystack.BHDict;
import nhaystack.server.HaystackSlotUtil;
import org.projecthaystack.HBool;
import org.projecthaystack.HDate;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HList;
import org.projecthaystack.HMarker;
import org.projecthaystack.HNum;
import org.projecthaystack.HStr;
import org.projecthaystack.HVal;
import org.testng.annotations.Test;

@Test
@NiagaraType
public class BMigrateHaystackTagsTest
    extends BTestNg
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

    public static final HVal[] EMPTY_HVAL_ARRAY = new HVal[0];

    public void testNoHaystackSlot()
    {
        BComponent component = new BComponent();
        assertNull(BHDict.findTagAnnotation(component), "before");
        HaystackSlotUtil.migrateHaystackTags(component);
        assertNull(BHDict.findTagAnnotation(component), "after");
    }

    public void testOnlyWhitelistTags()
    {
        BComponent component = new BComponent();

        HDict dict = new HDictBuilder()
            .add("absorption", HMarker.VAL)
            .add("ac", HNum.make(8.22, "kJ"))
            .add("active", "some string")
            .add("ahu", true)
            .add("air", HNum.make(417, "ms"))
            .toDict();
        component.add("haystack", BHDict.make(dict));
        assertNotNull(component.get("haystack"), "before");

        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertNotNull(haystackSlot, "after");
        assertEquals(haystackSlot.getDict().size(), 0, "Size of haystack slot dict");

        Tags tags = component.tags();
        assertEquals(tags.getAll().size(), 5, "Size of component's tags");
        assertTag(tags, "absorption", BMarker.MARKER);
        assertTag(tags, "ac", BDouble.make(8.22));
        assertTag(tags, "active", BString.make("some string"));
        assertTag(tags, "ahu", BBoolean.TRUE);
        assertTag(tags, "air", BRelTime.make(417));
    }

    public void testOnlyBlacklistTags()
    {
        BComponent component = new BComponent();

        HDict dict = new HDictBuilder()
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
        assertEquals(haystackSlot.getDict().size(), 5, "Size of haystack slot dict");
        assertDictEntry(haystackSlot.getDict(), "tag1", HMarker.VAL);
        assertDictEntry(haystackSlot.getDict(), "tag2", HNum.make(8.22, "kJ"));
        assertDictEntry(haystackSlot.getDict(), "tag3", HStr.make("some string"));
        assertDictEntry(haystackSlot.getDict(), "tag4", HBool.TRUE);
        assertDictEntry(haystackSlot.getDict(), "tag5", HNum.make(417, "ms"));

        assertEquals(component.tags().getAll().size(), 0, "Size of component tags");
    }

    public void testWhiteAndBlackTags()
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        BComponent component = new BComponent();

        HDict dict = new HDictBuilder()
            .add("absorption", HMarker.VAL)
            .add("tag2", HNum.make(8.22, "kJ"))
            .add("active", "some string")
            .add("tag4", true)
            .add("air", HNum.make(417, "ms"))
            .toDict();
        component.add("haystack", BHDict.make(dict));
        assertNotNull(component.get("haystack"), "before");

        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertNotNull(haystackSlot, "after");
        assertEquals(haystackSlot.getDict().size(), 2, "Size of haystack slot dict");
        assertDictEntry(haystackSlot.getDict(), "tag2", HNum.make(8.22, "kJ"));
        assertDictEntry(haystackSlot.getDict(), "tag4", HBool.TRUE);

        Tags tags = component.tags();
        assertEquals(tags.getAll().size(), 3, "Size of component's tags");
        assertTag(tags, "absorption", BMarker.MARKER);
        assertTag(tags, "active", BString.make("some string"));
        assertTag(tags, "air", BRelTime.make(417));
    }

    public void testWhitelistNonBIDataValue()
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        BComponent component = new BComponent();

        HDict dict = new HDictBuilder()
            .add("ac", HDate.make(2012, 8, 22))
            .add("active", HList.make(EMPTY_HVAL_ARRAY))
            .add("ahu", true)
            .toDict();
        component.add("haystack", BHDict.make(dict));
        assertNotNull(component.get("haystack"), "before");

        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertNotNull(haystackSlot, "after");
        assertEquals(haystackSlot.getDict().size(), 2, "Size of haystack slot dict");
        assertDictEntry(haystackSlot.getDict(), "ac", HDate.make(2012, 8, 22));
        assertDictEntry(haystackSlot.getDict(), "active", HList.make(EMPTY_HVAL_ARRAY));

        Tags tags = component.tags();
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertTag(tags, "ahu", BBoolean.make(true));
    }

    public void testBlacklistNonBIDataValue()
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        BComponent component = new BComponent();

        HDict dict = new HDictBuilder()
            .add("tag1", HDate.make(2012, 8, 22))
            .add("tag2", HList.make(EMPTY_HVAL_ARRAY))
            .add("tag3", true)
            .toDict();
        component.add("haystack", BHDict.make(dict));
        assertNotNull(component.get("haystack"), "before");

        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertNotNull(haystackSlot, "after");
        assertEquals(haystackSlot.getDict().size(), 3, "Size of haystack slot dict");
        assertDictEntry(haystackSlot.getDict(), "tag1", HDate.make(2012, 8, 22));
        assertDictEntry(haystackSlot.getDict(), "tag2", HList.make(EMPTY_HVAL_ARRAY));
        assertDictEntry(haystackSlot.getDict(), "tag3", HBool.TRUE);

        assertEquals(component.tags().getAll().size(), 0, "Size of component tags");
    }

    public void testAllCombinations() throws Exception
    {
        // None of these tags are whitelisted so none should be converted to tags on the component.
        BComponent component = new BComponent();
        HDictBuilder builder = new HDictBuilder();
        builder.add("ac", 8.22);
        builder.add("active", HDate.make(2012, 8, 22));
        builder.add("tag1", true);
        builder.add("tag2", HList.make(EMPTY_HVAL_ARRAY));
        component.add("haystack", BHDict.make(builder.toDict()));
        assertNotNull(component.get("haystack"), "before");

        HaystackSlotUtil.migrateHaystackTags(component);

        BHDict haystackSlot = (BHDict) component.get("haystack");
        assertNotNull(haystackSlot, "after");
        assertEquals(haystackSlot.getDict().size(), 3, "Size of haystack slot dict");
        assertDictEntry(haystackSlot.getDict(), "active", HDate.make(2012, 8, 22));
        assertDictEntry(haystackSlot.getDict(), "tag1", HBool.TRUE);
        assertDictEntry(haystackSlot.getDict(), "tag2", HList.make(EMPTY_HVAL_ARRAY));

        Tags tags = component.tags();
        assertEquals(tags.getAll().size(), 1, "Size of component tags");
        assertTag(tags, "ac", BDouble.make(8.22));
    }

    private static void assertTag(Tags tags, String tagName, BIDataValue expectedValue)
    {
        Optional<BIDataValue> tag = tags.get(Id.newId("hs", tagName));
        assertTrue(tag.isPresent(), "Missing hs:" + tagName);
        assertEquals(tag.get(), expectedValue, "Value of hs:" + tagName + " not correct");
    }

    private static void assertDictEntry(HDict dict, String key, HVal expectedValue)
    {
        HVal actualValue = dict.get(key, false);
        assertNotNull(actualValue, "Key " + key + " not found");
        assertEquals(actualValue, expectedValue, "Value of " + key + "not correct");
    }
}
