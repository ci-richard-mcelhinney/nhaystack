//
// Copyright 2018 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   31 Oct 2018  Andrew Saunders  Creation based on class in haystackTest-rt
//
package nhaystack.ntest;

import static nhaystack.util.NHaystackConst.ID_EQUIP;
import static nhaystack.util.NHaystackConst.TAGS_VERSION_IMPORT;
import static org.testng.Assert.assertFalse;

import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tagdictionary.BTagDictionaryService;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.tridium.haystack.BHsTagDictionary;
import com.tridium.nd.BNiagaraStation;
import com.tridium.testng.BStationTestBase;
import com.tridium.testng.TestUtil;

@Test
@NiagaraType
public class BNHaystackTagDictionaryTest extends BStationTestBase
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
    public static final Type TYPE = Sys.loadType(BNHaystackTagDictionaryTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort)
        throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);
        
        BTagDictionaryService tagDictionaryService = new BTagDictionaryService();
        getServices().add("TagDictionaryService", tagDictionaryService);

        haystackDict = new BHsTagDictionary();
        haystackDict.setTagsImportFile(BOrd.make("module://nhaystack/nhaystack/res/tagsMerge.csv"));
        tagDictionaryService.add("Haystack", haystackDict);
    }

    @Override
    @BeforeTest(alwaysRun = true, description = "Setup and start test station")
    public void setupStation() throws Exception
    {
        super.setupStation();

        TestUtil.waitFor(() -> TAGS_VERSION_IMPORT.equals(haystackDict.getVersion()), 10);
    }
    
    public void assertEquipNotImpliedOnBDevices()
    {
        // Assert equip tag is not implied on BDevices
        BNiagaraStation niagaraStation = new BNiagaraStation();
        getNiagaraNetwork().add("station", niagaraStation);
        assertFalse(niagaraStation.tags().get(ID_EQUIP).isPresent(), "Equip tag should not be implied on BDevices");
    }

    private BHsTagDictionary haystackDict;
}
