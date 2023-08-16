//
// Copyright 2018 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   31 Oct 2018  Andrew Saunders  Creation based on class in haystackTest-rt
//
package nhaystack.ntest;

import static nhaystack.ntest.helper.NHaystackTestUtil.addChild;
import static nhaystack.ntest.helper.NHaystackTestUtil.addEquipRefRelation;
import static nhaystack.ntest.helper.NHaystackTestUtil.addEquipTag;
import static nhaystack.ntest.helper.NHaystackTestUtil.addFolder;
import static nhaystack.ntest.helper.NHaystackTestUtil.addSiteRefRelation;
import static nhaystack.ntest.helper.NHaystackTestUtil.addSiteTag;
import static nhaystack.util.NHaystackConst.TAGS_CSV_FILE_VERSION;
import static nhaystack.util.NHaystackConst.TAGS_VERSION_IMPORT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Optional;
import javax.baja.control.BNumericWritable;
import javax.baja.data.BIDataValue;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.schedule.BBooleanSchedule;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDouble;
import javax.baja.sys.BString;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Id;
import javax.baja.tag.TagInfo;
import javax.baja.tagdictionary.BTagDictionaryService;
import javax.baja.util.BFolder;

import nhaystack.server.tags.BNCurValTag;
import nhaystack.server.tags.BNWriteValTag;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.tridium.haystack.BHsTagDictionary;
import com.tridium.testng.BStationTestBase;
import com.tridium.testng.TestUtil;

@Test
@NiagaraType
public class BHaystackImportOverlayTest extends BStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $test.com.tridium.haystack.BHaystackImportOverlayTest(2979906276)1.0$ @*/
/* Generated Mon Oct 08 16:08:37 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

    @Override
    public Type getType()
    {
        return TYPE;
    }
    public static final Type TYPE = Sys.loadType(BHaystackImportOverlayTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    protected void makeStationHandler() throws Exception
    {
        // admin/Password10
        stationHandler = createTestStation(BOrd.make("module://nhaystackTest/stations/importTestStation.xml"));
        station = stationHandler.getStation();
    }

    @Override
    @BeforeTest(alwaysRun = true, description = "Setup and start test station")
    public void setupStation() throws Exception
    {
        super.setupStation();

        BTagDictionaryService service = (BTagDictionaryService)Sys.getService(BTagDictionaryService.TYPE);
        haystackDict = (BHsTagDictionary)service.getSmartTagDictionary("hs").orElseThrow(() -> new Exception("No haystack dictionary"));
        haystackDict.doImportDictionary(Context.NULL);
        TestUtil.waitFor(() -> !"1.0".equals(haystackDict.getVersion()), 10);
    }

    public void testImport() throws Exception
    {
        // Before
        assertEquals(haystackDict.getNamespace(), "hs", "imported namespace");
        assertTrue(haystackDict.getFrozen(), "imported is frozen");
        assertEquals(haystackDict.getVersion(), TAGS_CSV_FILE_VERSION, "dictionary version before import");

        // Set ImportTagsFile & ImportEquipFile
        haystackDict.setTagsImportFile(BOrd.make("module://nhaystack/nhaystack/res/tagsMerge.csv"));
        haystackDict.doImportDictionary(Context.NULL);
        TestUtil.waitFor(() -> TAGS_VERSION_IMPORT.equals(haystackDict.getVersion()), 10);

        assertEquals(haystackDict.getVersion(), TAGS_VERSION_IMPORT, "dictionary version after import");
        assertTagsModified(haystackDict);

        BFolder folder = addFolder("folder", station);
        BNumericWritable wp = addChild("wp", new BNumericWritable(), folder);
        assertCurValTag(wp);
        assertWriteValTag(wp);

        // test overridden hs:id tag
        BFolder site = addFolder("site", station);
        assertNoIdTag(site);

        addSiteTag(site);
        assertIdValue(site, "S.site");

        BFolder equip = addFolder("equip", station);
        assertNoIdTag(equip);

        addEquipTag(equip);
        assertIdValue(equip, "C.equip");

        BNumericWritable point = addChild("point", new BNumericWritable(), equip);
        assertIdValue(point, "C.equip.point");

        addSiteRefRelation(equip, site);
        assertIdValue(equip, "S.site.equip");
        assertIdValue(point, "C.equip.point");

        addEquipRefRelation(point, equip);
        assertIdValue(point, "S.site.equip.point");

        BBooleanSchedule schedule = addChild("schedule", new BBooleanSchedule(), equip);
        assertIdValue(schedule, "C.equip.schedule");
    }

    private static void assertNoIdTag(BComponent comp)
    {
        assertFalse(comp.tags().contains(ID_ID), comp.getSlotPath() + " should NOT have a hs:id tag");
    }

    private static void assertIdValue(BComponent comp, String expected)
    {
        final Optional<BIDataValue> valueOpt = comp.tags().get(ID_ID);
        assertTrue(valueOpt.isPresent(), "assertIdValue " + comp.getSlotPath() + " should have a hs:id tag");
        assertEquals(valueOpt.get(), BString.make(expected), "assertIdValue " + comp.getSlotPath() );
    }

    private static void assertTagsModified(BHsTagDictionary dict)
    {
        // Only modification was to override the SmartType of the curVal & writeVal tag
        Optional<TagInfo> tagInfo = dict.getTagDefinitions().getTag(ID_CUR_VAL);
        assertTrue(tagInfo.isPresent(), ID_CUR_VAL.getQName() + " is not present.");
        assertTrue(tagInfo.get() instanceof BNCurValTag, ID_CUR_VAL.getQName() + " is not a BNCurValTag");
        tagInfo = dict.getTagDefinitions().getTag(ID_WRITE_VAL);
        assertTrue(tagInfo.isPresent(), ID_WRITE_VAL.getQName() + " is not present.");
        assertTrue(tagInfo.get() instanceof BNWriteValTag, ID_WRITE_VAL.getQName() + " is not a BNWriteValTag");
    }

    private static void assertCurValTag(BNumericWritable wp)
    {
        Optional<BIDataValue> optValue = wp.tags().get(ID_CUR_VAL);
        assertFalse(optValue.isPresent(), "null value should result in no curVal tag.");
        wp.setFallback(new BStatusNumeric(1234.0, BStatus.ok));
        try{Thread.sleep(250);} catch (Exception ignore){}
        optValue = wp.tags().get(ID_CUR_VAL);
        assertTrue(optValue.isPresent(), "non null value should result in a curVal tag.");
        assertEquals(optValue.get(), BDouble.make(1234.0), "curVal tag should be 1234.0");
        wp.setFallback(new BStatusNumeric(0.0, BStatus.nullStatus));
        try{Thread.sleep(250);} catch (Exception ignore){}
    }

    private static void assertWriteValTag(BNumericWritable wp)
    {
        Optional<BIDataValue> optValue = wp.tags().get(ID_WRITE_VAL);
        assertFalse(optValue.isPresent(), "null write value should result in no writeVal tag.");
        wp.doSet(BDouble.make(4321.0));
        try{Thread.sleep(250);} catch (Exception ignore){}
        optValue = wp.tags().get(ID_WRITE_VAL);
        assertTrue(optValue.isPresent(), "non null write value should result in a writeVal tag.");
        assertEquals(optValue.get(), BDouble.make(4321.0), "writeVal tag should be 4321.0");
    }

    private static final Id ID_CUR_VAL = Id.newId("hs:curVal");
    private static final Id ID_WRITE_VAL = Id.newId("hs:writeVal");
    private static final Id ID_ID = Id.newId("hs:id");

    private BHsTagDictionary haystackDict;
}
