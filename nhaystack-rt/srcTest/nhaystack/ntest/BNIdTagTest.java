//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Apr 2019  Eric Anderson  Creation
//
package nhaystack.ntest;

import static nhaystack.util.NHaystackConst.ID_EQUIP;
import static nhaystack.util.NHaystackConst.ID_EQUIP_REF;
import static nhaystack.util.NHaystackConst.ID_SITE;
import static nhaystack.util.NHaystackConst.ID_SITE_REF;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import javax.baja.control.BControlPoint;
import javax.baja.control.BNumericPoint;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.schedule.BBooleanSchedule;
import javax.baja.schedule.BWeeklySchedule;
import javax.baja.sys.BComponent;
import javax.baja.sys.BMarker;
import javax.baja.sys.BRelation;
import javax.baja.sys.BStation;
import javax.baja.sys.BString;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Tag;
import javax.baja.tag.util.BasicEntity;
import javax.baja.util.BFolder;

import nhaystack.BHDict;
import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.ntest.helper.BTestProxyExt;
import nhaystack.server.tags.BNIdTag;
import nhaystack.site.BHEquip;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.tridium.nd.BNiagaraStation;

@NiagaraType
@Test
public class BNIdTagTest extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BNIdTagTest(2979906276)1.0$ @*/
/* Generated Tue Apr 02 16:49:03 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNIdTagTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    private BNIdTag idTag;

    // Non-visible component
    private final BComponent nonVisibleComponent = new BComponent();

    // Visible component without a site
    private final BHEquip hEquip = new BHEquip();
    private final BControlPoint pointNoEquipNoSite = new BNumericPoint();
    private final BControlPoint pointDirectEquipNoSite = new BNumericPoint();
    private final BControlPoint pointImpliedEquipNoSite = new BNumericPoint();
    private final BControlPoint pointNoEquipSite = new BNumericPoint();
    private final BNiagaraStation niagaraStation = new BNiagaraStation();
    private final BWeeklySchedule weeklySchedule = new BBooleanSchedule();
    private final BComponent haystackComponent = new BComponent();
    private final BFolder equipNoSite = new BFolder();

    // Site
    private final BComponent site = new BComponent();

    // Visible component with a site
    private final BControlPoint pointDirectEquipWithSite = new BNumericPoint();
    private final BControlPoint pointImpliedEquipWithSite = new BNumericPoint();
    private final BFolder equipWithSite = new BFolder();

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort)
        throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        // Non-visible component
        BFolder nonVisibleFolder = new BFolder();
        station.add("nonVisible", nonVisibleFolder);
        nonVisibleFolder.add("nonVisibleComponent", nonVisibleComponent);

        // Visible components that do not have a site
        BFolder visibleNoSiteFolder = new BFolder();
        station.add("visibleNoSite", visibleNoSiteFolder);
        visibleNoSiteFolder.add("hEquip", hEquip);
        visibleNoSiteFolder.add("pointNoEquipNoSite", pointNoEquipNoSite);
        visibleNoSiteFolder.add("pointDirectEquipNoSite", pointDirectEquipNoSite);
        visibleNoSiteFolder.add("pointNoEquipSite", pointNoEquipSite);
        visibleNoSiteFolder.add("weeklySchedule", weeklySchedule);
        visibleNoSiteFolder.add("haystackComponent", haystackComponent);
        visibleNoSiteFolder.add("equipNoSite", equipNoSite);
        equipNoSite.add("pointImpliedEquipNoSite", pointImpliedEquipNoSite);

        pointNoEquipNoSite.setProxyExt(new BTestProxyExt());
        pointDirectEquipNoSite.setProxyExt(new BTestProxyExt());
        pointImpliedEquipNoSite.setProxyExt(new BTestProxyExt());
        pointNoEquipSite.setProxyExt(new BTestProxyExt());

        station.add("site", site);

        pointDirectEquipNoSite.relations().add(new BRelation(ID_EQUIP_REF, equipNoSite));
        pointNoEquipSite.relations().add(new BRelation(ID_SITE_REF, site));
        haystackComponent.add(BHDict.HAYSTACK_IDENTIFIER, BHDict.DEFAULT);
        equipNoSite.tags().set(ID_EQUIP, BMarker.MARKER);
        site.tags().set(ID_SITE, BMarker.MARKER);

        getNiagaraNetwork().add("niagaraStation", niagaraStation);

        // Visible components that have a site
        BFolder visibleWithSiteFolder = new BFolder();
        station.add("visibleWithSite", visibleWithSiteFolder);
        visibleWithSiteFolder.add("pointDirectEquipWithSite", pointDirectEquipWithSite);
        visibleWithSiteFolder.add("equipWithSite", equipWithSite);
        equipWithSite.add("pointImpliedEquipWithSite", pointImpliedEquipWithSite);

        pointDirectEquipWithSite.setProxyExt(new BTestProxyExt());
        pointImpliedEquipWithSite.setProxyExt(new BTestProxyExt());

        equipWithSite.tags().set(ID_EQUIP, BMarker.MARKER);
        equipWithSite.relations().add(new BRelation(ID_SITE_REF, site));
        pointDirectEquipWithSite.relations().add(new BRelation(ID_EQUIP_REF, equipWithSite));
    }

    @BeforeTest
    @Override
    public void setupStation() throws Exception
    {
        super.setupStation();

        idTag = (BNIdTag) haystackDict.getTagDefinitions().get("id");
    }

    public void returnsNullForNonComponent()
    {
        BasicEntity entity = new BasicEntity();
        assertNull(idTag.getTag(entity), "Not null for a non-component");
    }

    public void returnsNullForNonVisibleComponent()
    {
        // Not a BHTagged, BControlPoint, BDevice, BWeeklySchedule
        // Does not have a haystack slot
        // Is not tagged with hs:site or hs:equip
        assertNull(idTag.getTag(nonVisibleComponent), "Not null for non-visible component");
    }

    @DataProvider
    private Object[][] visibleComponentsWithoutSiteProvider()
    {
        return new Object[][]
        {
            // component,             expected
            {hEquip,                  "C.visibleNoSite.hEquip"                             },
            {pointNoEquipNoSite,      "C.visibleNoSite.pointNoEquipNoSite"                 },
            {pointDirectEquipNoSite,  "C.visibleNoSite.pointDirectEquipNoSite"             },
            {pointImpliedEquipNoSite, "C.visibleNoSite.equipNoSite.pointImpliedEquipNoSite"},
            {pointNoEquipSite,        "C.visibleNoSite.pointNoEquipSite"                   },
            {niagaraStation,          "C.Drivers.NiagaraNetwork.niagaraStation"            },
            {weeklySchedule,          "C.visibleNoSite.weeklySchedule"                     },
            {haystackComponent,       "C.visibleNoSite.haystackComponent"                  },
            {equipNoSite,             "C.visibleNoSite.equipNoSite"                        },
        };
    }

    @Test(dataProvider = "visibleComponentsWithoutSiteProvider")
    public void returnsSlotPathRefForVisibleComponentsWithoutSite(BComponent component, String expected)
    {
        assertIdTag(component, expected);
    }

    @DataProvider
    private Object[][] visibleComponentsWithSiteProvider()
    {
        return new Object[][]
        {
            // component,               expected
            {site,                      "S.site"                                        },
            {equipWithSite,             "S.site.equipWithSite"                          },
            {pointDirectEquipWithSite,  "S.site.equipWithSite.pointDirectEquipWithSite" },
            {pointImpliedEquipWithSite, "S.site.equipWithSite.pointImpliedEquipWithSite"},
        };
    }

    @Test(dataProvider = "visibleComponentsWithSiteProvider")
    public void returnsRefForVisibleComponentsWithSite(BComponent component, String expected)
    {
        assertIdTag(component, expected);
    }

    private void assertIdTag(BComponent component, String expected)
    {
        Tag tag = idTag.getTag(component);
        assertNotNull(tag, "ID tag for component " + component.getSlotPath());
        // Tag type should be a BString
        assertEquals(tag.getValue().getType(), BString.TYPE, "ID tag type for component " + component.getSlotPath());
        // Tag value should match expected
        assertEquals(tag.getValue().toString(), expected, "ID tag value for component " + component.getSlotPath());
    }
}
