//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   15 Apr 2019  Eric Anderson  Creation
//
package nhaystack.ntest;

import static nhaystack.ntest.helper.NHaystackTestUtil.addChild;
import static nhaystack.ntest.helper.NHaystackTestUtil.addEquipRefRelation;
import static nhaystack.ntest.helper.NHaystackTestUtil.addEquipTag;
import static nhaystack.ntest.helper.NHaystackTestUtil.addFolder;
import static nhaystack.ntest.helper.NHaystackTestUtil.addNumericPoint;
import static nhaystack.ntest.helper.NHaystackTestUtil.addSiteRefRelation;
import static nhaystack.ntest.helper.NHaystackTestUtil.addSiteTag;
import static nhaystack.ntest.helper.NHaystackTestUtil.addNumericTestProxyPoint;
import static nhaystack.ntest.helper.NHaystackTestUtil.hasEquipRefs;
import static nhaystack.ntest.helper.NHaystackTestUtil.hasNoEquipRefs;
import static nhaystack.ntest.helper.NHaystackTestUtil.hasNoSiteRefs;
import static nhaystack.ntest.helper.NHaystackTestUtil.hasSiteRefs;

import javax.baja.control.BNumericPoint;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BFolder;

import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(singleThreaded = true)
@NiagaraType
public class BSiteRefRelationTest extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BSiteRefRelationTest(2979906276)1.0$ @*/
/* Generated Mon Apr 15 15:41:19 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BSiteRefRelationTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @BeforeMethod
    public void beforeMethod()
    {
        BStation station = stationHandler.getStation();
        if (testFolder != null)
        {
            station.remove(testFolder);
        }

        testFolder = new BFolder();
        station.add("Test", testFolder);
    }

    public void testTaggedComponents() throws Exception
    {
        // sites
        //   site1 (site)
        //   site2 (site)
        //   site3 (site)
        // equips
        //   equip1 (equip, siteRef->site1)
        //     equip1Point1 ([equipRef->equip1], [siteRef->site1])
        //     equip1Point2 ([equipRef->equip1], siteRef->site3)
        //     // No implied equipRef relation to this point with a NullProxyExt
        //     equip1NullPoint1
        //     // Has an implied siteRef relation to equip1's site (site1)
        //     // because of the direct equipRef relation
        //     equip1NullPoint2 (equipRef->equip1, [siteRef->site1])
        //     // Direct siteRef relation prevents an implied one to site3
        //     equip1NullPoint3 (equipRef->equip1, siteRef->site3)
        //     equip2Point3 (equip->equip2, [siteRef->site2])
        //     equip2Point4 (equip->equip2, siteRef->site3)
        //     // Has an implied siteRef relation to equip2's site (site1)
        //     // because of the direct equipRef relation
        //     equip2NullPoint1 (equipRef->equip2, [siteRef->site2])
        //     // Direct siteRef relation prevents an implied one to site3
        //     equip2NullPoint2 (equipRef->equip2, siteRef->site3)
        //   equip2 (equip, siteRef->site2)
        //     equip2Point1 ([equipRef->equip2], [siteRef->site2])
        //     equip2Point2 ([equipRef->equip2], siteRef->site3)
        //   equip3 (equip)
        //     equip3Point1 ([equipRef->equip3])
        //     equip3Point2 ([equipRef->equip3], siteRef->site3)
        //     // No implied siteRef because equip3 does not have a siteRef relation
        //     equip3NullPoint1 (equipRef->equip3)
        //     // Direct siteRef relation prevents an implied one to site3
        //     equip3NullPoint2 (equipRef->equip3, siteRef->site3)
        // externalPoints
        //   equip1Point3 (equip->equip1, [siteRef->site1])
        //   equip1Point4 (equip->equip1, siteRef->site3)
        //
        // Should result in the following Site - Equip - Point organization
        // site1 <- siteRef -< equip1
        //       <- siteRef -< equip1Point1
        //       <- siteRef -< equip1Point3
        //       <- siteRef -< equip1NullPoint2
        // site2 <- siteRef -< equip2
        //       <- siteRef -< equip2Point1
        //       <- siteRef -< equip2Point3
        //       <- siteRef -< equip2NullPoint1
        // site3 <- siteRef -< equip1Point2
        //       <- siteRef -< equip2Point4
        //       <- siteRef -< equip2Point2
        //       <- siteRef -< equip3Point2
        //       <- siteRef -< equip1Point4
        //       <- siteRef -< equip1NullPoint3
        //       <- siteRef -< equip2NullPoint2
        //       <- siteRef -< equip3NullPoint2
        // equip1 <- equipRef -< equip1Point1
        //        <- equipRef -< equip1Point2
        //        <- equipRef -< equip1Point3
        //        <- equipRef -< equip1Point4
        //        <- equipRef -< equip1NullPoint2
        //        <- equipRef -< equip1NullPoint3
        // equip2 <- equipRef -< equip2Point3
        //        <- equipRef -< equip2Point4
        //        <- equipRef -< equip2Point1
        //        <- equipRef -< equip2Point2
        //        <- equipRef -< equip2NullPoint1
        //        <- equipRef -< equip2NullPoint2
        // equip3 <- equipRef -< equip3Point1
        //        <- equipRef -< equip3Point2
        //        <- equipRef -< equip3NullPoint1
        //        <- equipRef -< equip3NullPoint2
        BFolder sites = addFolder("sites", testFolder);
        BFolder equips = addFolder("equips", testFolder);
        BFolder externalPoints = addFolder("externalPoints", testFolder);

        BComponent site1 = addChild("site1", new BComponent(), sites);
        addSiteTag(site1);
        BComponent site2 = addChild("site2", new BComponent(), sites);
        addSiteTag(site2);
        BComponent site3 = addChild("site3", new BComponent(), sites);
        addSiteTag(site3);

        BFolder equip1 = addFolder("equip1", equips);
        addEquipTag(equip1);
        BFolder equip2 = addFolder("equip2", equips);
        addEquipTag(equip2);
        BFolder equip3 = addFolder("equip3", equips);
        addEquipTag(equip3);

        BNumericPoint equip1Point1 = addNumericTestProxyPoint("equip1Point1", equip1);
        BNumericPoint equip1Point2 = addNumericTestProxyPoint("equip1Point2", equip1);
        BNumericPoint equip1NullPoint1 = addNumericPoint("equip1NullPoint1", equip1);
        BNumericPoint equip1NullPoint2 = addNumericPoint("equip1NullPoint2", equip1);
        BNumericPoint equip1NullPoint3 = addNumericPoint("equip1NullPoint3", equip1);
        BNumericPoint equip2Point3 = addNumericTestProxyPoint("equip2Point3", equip1);
        BNumericPoint equip2Point4 = addNumericTestProxyPoint("equip2Point4", equip1);
        BNumericPoint equip2NullPoint1 = addNumericPoint("equip2NullPoint1", equip1);
        BNumericPoint equip2NullPoint2 = addNumericPoint("equip2NullPoint2", equip1);

        BNumericPoint equip2Point1 = addNumericTestProxyPoint("equip2Point1", equip2);
        BNumericPoint equip2Point2 = addNumericTestProxyPoint("equip2Point2", equip2);

        BNumericPoint equip3Point1 = addNumericTestProxyPoint("equip3Point1", equip3);
        BNumericPoint equip3Point2 = addNumericTestProxyPoint("equip3Point2", equip3);
        BNumericPoint equip3NullPoint1 = addNumericPoint("equip3NullPoint1", equip3);
        BNumericPoint equip3NullPoint2 = addNumericPoint("equip3NullPoint2", equip3);

        BNumericPoint equip1Point3 = addNumericTestProxyPoint("equip1Point3", externalPoints);
        BNumericPoint equip1Point4 = addNumericTestProxyPoint("equip1Point4", externalPoints);

        addSiteRefRelation(equip1, site1);
        addSiteRefRelation(equip2, site2);
        addSiteRefRelation(equip1Point2, site3);
        addSiteRefRelation(equip1Point4, site3);
        addSiteRefRelation(equip1NullPoint3, site3);
        addSiteRefRelation(equip2Point2, site3);
        addSiteRefRelation(equip2Point4, site3);
        addSiteRefRelation(equip2NullPoint2, site3);
        addSiteRefRelation(equip3Point2, site3);
        addSiteRefRelation(equip3NullPoint2, site3);

        addEquipRefRelation(equip1Point3, equip1);
        addEquipRefRelation(equip1Point4, equip1);
        addEquipRefRelation(equip1NullPoint2, equip1);
        addEquipRefRelation(equip1NullPoint3, equip1);
        addEquipRefRelation(equip2Point3, equip2);
        addEquipRefRelation(equip2Point4, equip2);
        addEquipRefRelation(equip2NullPoint1, equip2);
        addEquipRefRelation(equip2NullPoint2, equip2);
        addEquipRefRelation(equip3NullPoint1, equip3);
        addEquipRefRelation(equip3NullPoint2, equip3);

        hasEquipRefs(equip1,
            equip1Point1,
            equip1Point2,
            equip1Point3,
            equip1Point4,
            equip1NullPoint2,
            equip1NullPoint3);
        hasEquipRefs(equip2,
            equip2Point1,
            equip2Point2,
            equip2Point3,
            equip2Point4,
            equip2NullPoint1,
            equip2NullPoint2);
        hasEquipRefs(equip3,
            equip3Point1,
            equip3Point2,
            equip3NullPoint1,
            equip3NullPoint2);
        hasNoEquipRefs(
            equip1NullPoint1);
        hasSiteRefs(site1,
            equip1,
            equip1Point1,
            equip1Point3,
            equip1NullPoint2);
        hasSiteRefs(site2,
            equip2,
            equip2Point1,
            equip2Point3,
            equip2NullPoint1);
        hasSiteRefs(site3,
            equip1Point2,
            equip1Point4,
            equip2Point2,
            equip2Point4,
            equip3Point2,
            equip1NullPoint3,
            equip2NullPoint2,
            equip3NullPoint2);
        hasNoSiteRefs(
            equip3,
            equip3Point1,
            equip1NullPoint1,
            equip3NullPoint1);
    }

    public void testHaystackComponents() throws Exception
    {
        // sites
        //   site1
        //   site2
        //   site3
        // equips
        //   equip1
        //     equip (siteRef->site1)
        //     equip1Point1 ([equipRef->equip1], [siteRef->site1])
        //     equip1Point2 ([equipRef->equip1], siteRef->site3)
        //     // No implied equipRef relation to this point with a NullProxyExt
        //     equip1NullPoint1
        //     // Has an implied siteRef relation to equip1's site (site1)
        //     // because of the direct equipRef relation
        //     equip1NullPoint2 (equipRef->equip1, [siteRef->site1])
        //     // Direct siteRef relation prevents an implied one to site3
        //     equip1NullPoint3 (equipRef->equip1, siteRef->site3)
        //     equip2Point3 (equip->equip2, [siteRef->site2])
        //     equip2Point4 (equip->equip2, siteRef->site3)
        //     // Has an implied siteRef relation to equip2's site (site2)
        //     // because of the direct equipRef relation
        //     equip2NullPoint1 (equipRef->equip2, [siteRef->site2])
        //     // Direct siteRef relation prevents an implied one to site3
        //     equip2NullPoint2 (equipRef->equip2, siteRef->site3)
        //   equip2
        //     equip (siteRef->site2)
        //     equip2Point1 ([equipRef->equip2], [siteRef->site2])
        //     equip2Point2 ([equipRef->equip2], siteRef->site3)
        //   equip3
        //     equip
        //     equip3Point1 ([equipRef->equip3])
        //     equip3Point2 ([equipRef->equip3], siteRef->site3)
        //     // No implied siteRef because equip3 does not have a siteRef relation
        //     equip3NullPoint1 (equipRef->equip3)
        //     // Direct siteRef relation prevents an implied one to site3
        //     equip3NullPoint2 (equipRef->equip3, siteRef->site3)
        // externalPoints
        //   equip1Point3 (equip->equip1, [siteRef->site1])
        //   equip1Point4 (equip->equip1, siteRef->site3)
        //
        // Should result in the following Site - Equip - Point organization
        // site1 <- siteRef -< equip1
        //       <- siteRef -< equip1Point1
        //       <- siteRef -< equip1Point3
        //       <- siteRef -< equip1NullPoint2
        // site2 <- siteRef -< equip2
        //       <- siteRef -< equip2Point1
        //       <- siteRef -< equip2Point3
        //       <- siteRef -< equip2NullPoint1
        // site3 <- siteRef -< equip1Point2
        //       <- siteRef -< equip2Point4
        //       <- siteRef -< equip2Point2
        //       <- siteRef -< equip3Point2
        //       <- siteRef -< equip1Point4
        //       <- siteRef -< equip1NullPoint3
        //       <- siteRef -< equip2NullPoint2
        //       <- siteRef -< equip3NullPoint2
        // equip1 <- equipRef -< equip1Point1
        //        <- equipRef -< equip1Point2
        //        <- equipRef -< equip1Point3
        //        <- equipRef -< equip1Point4
        //        <- equipRef -< equip1NullPoint2
        //        <- equipRef -< equip1NullPoint3
        // equip2 <- equipRef -< equip2Point3
        //        <- equipRef -< equip2Point4
        //        <- equipRef -< equip2Point1
        //        <- equipRef -< equip2Point2
        //        <- equipRef -< equip2NullPoint1
        //        <- equipRef -< equip2NullPoint2
        // equip3 <- equipRef -< equip3Point1
        //        <- equipRef -< equip3Point2
        //        <- equipRef -< equip3NullPoint1
        //        <- equipRef -< equip3NullPoint2
        BFolder sites = addFolder("sites", testFolder);
        BFolder equips = addFolder("equips", testFolder);
        BFolder externalPoints = addFolder("externalPoints", testFolder);

        BHSite site1 = addChild("site1", new BHSite(), sites);
        BHSite site2 = addChild("site2", new BHSite(), sites);
        BHSite site3 = addChild("site3", new BHSite(), sites);

        BFolder equip1Folder = addFolder("equip1", equips);
        BFolder equip2Folder = addFolder("equip2", equips);
        BFolder equip3Folder = addFolder("equip3", equips);

        BHEquip equip1 = addChild("equip", new BHEquip(), equip1Folder);
        BNumericPoint equip1Point1 = addNumericTestProxyPoint("equip1Point1", equip1Folder);
        BNumericPoint equip1Point2 = addNumericTestProxyPoint("equip1Point2", equip1Folder);
        BNumericPoint equip1NullPoint1 = addNumericPoint("equip1NullPoint1", equip1Folder);
        BNumericPoint equip1NullPoint2 = addNumericPoint("equip1NullPoint2", equip1Folder);
        BNumericPoint equip1NullPoint3 = addNumericPoint("equip1NullPoint3", equip1Folder);
        BNumericPoint equip2Point3 = addNumericTestProxyPoint("equip2Point3", equip1Folder);
        BNumericPoint equip2Point4 = addNumericTestProxyPoint("equip2Point4", equip1Folder);
        BNumericPoint equip2NullPoint1 = addNumericPoint("equip2NullPoint1", equip1Folder);
        BNumericPoint equip2NullPoint2 = addNumericPoint("equip2NullPoint2", equip1Folder);

        BHEquip equip2 = addChild("equip", new BHEquip(), equip2Folder);
        BNumericPoint equip2Point1 = addNumericTestProxyPoint("equip2Point1", equip2Folder);
        BNumericPoint equip2Point2 = addNumericTestProxyPoint("equip2Point2", equip2Folder);

        BHEquip equip3 = addChild("equip", new BHEquip(), equip3Folder);
        BNumericPoint equip3Point1 = addNumericTestProxyPoint("equip3Point1", equip3Folder);
        BNumericPoint equip3Point2 = addNumericTestProxyPoint("equip3Point2", equip3Folder);
        BNumericPoint equip3NullPoint1 = addNumericPoint("equip3NullPoint1", equip3Folder);
        BNumericPoint equip3NullPoint2 = addNumericPoint("equip3NullPoint2", equip3Folder);

        BNumericPoint equip1Point3 = addNumericTestProxyPoint("equip1Point3", externalPoints);
        BNumericPoint equip1Point4 = addNumericTestProxyPoint("equip1Point4", externalPoints);

        addSiteRefRelation(equip1, site1);
        addSiteRefRelation(equip2, site2);
        addSiteRefRelation(equip1Point2, site3);
        addSiteRefRelation(equip1Point4, site3);
        addSiteRefRelation(equip2Point2, site3);
        addSiteRefRelation(equip2Point4, site3);
        addSiteRefRelation(equip3Point2, site3);
        addSiteRefRelation(equip1NullPoint3, site3);
        addSiteRefRelation(equip2NullPoint2, site3);
        addSiteRefRelation(equip3NullPoint2, site3);

        addEquipRefRelation(equip1Point3, equip1);
        addEquipRefRelation(equip1Point4, equip1);
        addEquipRefRelation(equip2Point3, equip2);
        addEquipRefRelation(equip2Point4, equip2);
        addEquipRefRelation(equip1NullPoint2, equip1);
        addEquipRefRelation(equip1NullPoint3, equip1);
        addEquipRefRelation(equip2NullPoint1, equip2);
        addEquipRefRelation(equip2NullPoint2, equip2);
        addEquipRefRelation(equip3NullPoint1, equip3);
        addEquipRefRelation(equip3NullPoint2, equip3);

        hasEquipRefs(equip1,
            equip1Point1,
            equip1Point2,
            equip1Point3,
            equip1Point4,
            equip1NullPoint2,
            equip1NullPoint3);
        hasEquipRefs(equip2,
            equip2Point1,
            equip2Point2,
            equip2Point3,
            equip2Point4,
            equip2NullPoint1,
            equip2NullPoint2);
        hasEquipRefs(equip3,
            equip3Point1,
            equip3Point2,
            equip3NullPoint1,
            equip3NullPoint2);
        hasNoEquipRefs(
            equip1NullPoint1);
        hasSiteRefs(site1,
            equip1,
            equip1Point1,
            equip1Point3,
            equip1NullPoint2);
        hasSiteRefs(site2,
            equip2,
            equip2Point1,
            equip2Point3,
            equip2NullPoint1);
        hasSiteRefs(site3,
            equip1Point2,
            equip1Point4,
            equip2Point2,
            equip2Point4,
            equip3Point2,
            equip1NullPoint3,
            equip2NullPoint2,
            equip3NullPoint2);
        hasNoSiteRefs(
            equip3,
            equip3Point1,
            equip1NullPoint1,
            equip3NullPoint1);
    }

    private BFolder testFolder;
}
