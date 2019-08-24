//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Apr 2019  Eric Anderson  Creation
//
package nhaystack.ntest;

import static nhaystack.ntest.helper.NHaystackTestUtil.addAhuTag;
import static nhaystack.ntest.helper.NHaystackTestUtil.addChild;
import static nhaystack.ntest.helper.NHaystackTestUtil.addEquipRefRelation;
import static nhaystack.ntest.helper.NHaystackTestUtil.addEquipTag;
import static nhaystack.ntest.helper.NHaystackTestUtil.addFolder;
import static nhaystack.ntest.helper.NHaystackTestUtil.addNumericPoint;
import static nhaystack.ntest.helper.NHaystackTestUtil.addNumericTestProxyPoint;
import static nhaystack.ntest.helper.NHaystackTestUtil.addSiteRefRelation;
import static nhaystack.ntest.helper.NHaystackTestUtil.addSiteTag;
import static nhaystack.ntest.helper.NHaystackTestUtil.addVavTag;
import static nhaystack.ntest.helper.NHaystackTestUtil.hasEquipRefs;
import static nhaystack.ntest.helper.NHaystackTestUtil.hasNoEquipRefs;
import static nhaystack.ntest.helper.NHaystackTestUtil.hasNoSiteRefs;
import static nhaystack.ntest.helper.NHaystackTestUtil.hasSiteRefs;
import static nhaystack.ntest.helper.NHaystackTestUtil.removeEquipRefRelation;
import static nhaystack.ntest.helper.NHaystackTestUtil.removeEquipTag;

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
public class BEquipRefRelationTest extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BEquipRefRelationTest(2979906276)1.0$ @*/
/* Generated Wed Apr 10 10:19:43 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BEquipRefRelationTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort) throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);
        this.station = station;

        // * station
        //   * directEquipFolder
        //   * impliedEquip1
        //     * impliedEquip1Point
        //     * impliedEquip2
        //       * impliedEquip2Point
        //       * impliedEquip3
        //         * impliedEquip3Point
        //         * impliedEquip4
        //           * impliedEquip4Point
        //           * impliedEquip5
        //             * impliedEquip5Point
        directEquip = addFolder("directEquipFolder", station);
        addEquipTag(directEquip);
        impliedEquip1 = addFolder("impliedEquip1", station);

        impliedEquip1Point = addNumericTestProxyPoint("impliedEquip1Point", impliedEquip1);
        impliedEquip2 = addFolder("impliedEquip2", impliedEquip1);

        impliedEquip2Point = addNumericTestProxyPoint("impliedEquip2Point", impliedEquip2);
        impliedEquip3 = addFolder("impliedEquip3", impliedEquip2);

        impliedEquip3Point = addNumericTestProxyPoint("impliedEquip3Point", impliedEquip3);
        impliedEquip4 = addFolder("impliedEquip4", impliedEquip3);

        impliedEquip4Point = addNumericTestProxyPoint("impliedEquip4Point", impliedEquip4);
        impliedEquip5 = addFolder("impliedEquip5", impliedEquip4);

        impliedEquip5Point = addNumericTestProxyPoint("impliedEquip5Point", impliedEquip5);
    }

    @BeforeMethod
    public void beforeMethod()
    {
        BStation station = stationHandler.getStation();
        if (testFolder != null)
        {
            station.remove(testFolder);
        }

        testFolder = addFolder("Test", station);
    }

    public void ahuVavPointTest() throws Exception
    {
        // build the following component tree
        // site1 (site)
        // site2 (site)
        // ahuFolder (equip & siteRef -> site1)
        //    vav (equip & siteRef -> site1)
        //      vavPoint1
        //      vavPoint2
        //      // No implied equipRef relation to this point with a NullProxyExt
        //      vavNullPoint1
        //      // Has an implied siteRef relation to ahuFolder's site (site1)
        //      // because of the direct equipRef relation
        //      vavNullPoint2 (equipRef -> vav)
        //      // Direct siteRef relation prevents an implied one to site1
        //      vavNullPoint3 (equipRef -> vav, siteRef -> site2)
        //      vavSite2Point (siteRef -> site2)
        //      ahuPointSub3 (equipRef -> ahuFolder)
        //    ahuPoints
        //      ahuPointSub1
        //      ahuPointSub2
        //      // No implied equipRef relation to this point with a NullProxyExt
        //      ahuNullPointSub1
        //      // Has an implied siteRef relation to ahuFolder's site (site1)
        //      // because of the direct equipRef relation
        //      ahuNullPointSub2 (equipRef -> ahuFolder)
        //    ahuPoint1
        //    ahuPoint2
        // should result in the following equipRef relations.
        //    ahuPoint1-------->ahuFolder
        //    ahuPoint2-------->ahuFolder
        //    ahuSubPoint1----->ahuFolder
        //    ahuSubPoint2----->ahuFolder
        //    ahuSubPoint3----->ahuFolder
        //    ahuNullPointSub2->ahuFolder
        //    vavPoint1----->vav
        //    vavPoint2----->vav
        //    vavSite2Point->vav
        //    vavNullPoint2->vav
        //    vavNullPoint3->vav
        // should result in the following siteRef relations
        //    ahuFolder-------->site1
        //    ahuPoint1-------->site1
        //    ahuPoint2-------->site1
        //    ahuPointSub1----->site1
        //    ahuPointSub2----->site1
        //    ahuPointSub3----->site1
        //    ahuNullPointSub2->site1
        //    vav----------->site1
        //    vavPoint1----->site1
        //    vavPoint2----->site1
        //    vavNullPoint2->site1
        //    vavNullPoint3->site2
        //    vavSite2Point->site2
        BComponent site1 = addChild("site1", new BComponent(), testFolder);
        addSiteTag(site1);
        BComponent site2 = addChild("site2", new BComponent(), testFolder);
        addSiteTag(site2);

        BFolder ahuFolder = addFolder("ahuFolder", testFolder);
        addEquipTag(ahuFolder);
        addAhuTag(ahuFolder);

        BFolder vav = addFolder("vav", ahuFolder);
        addEquipTag(vav);
        addVavTag(vav);
        BFolder ahuPoints = addFolder("ahuPoints", ahuFolder);
        BNumericPoint ahuPoint1 = addNumericTestProxyPoint("ahuPoint1", ahuFolder);
        BNumericPoint ahuPoint2 = addNumericTestProxyPoint("ahuPoint2", ahuFolder);

        BNumericPoint vavPoint1 = addNumericTestProxyPoint("vavPoint1", vav);
        BNumericPoint vavPoint2 = addNumericTestProxyPoint("vavPoint2", vav);
        BNumericPoint vavNullPoint1 = addNumericPoint("vavNullPoint1", vav);
        BNumericPoint vavNullPoint2 = addNumericPoint("vavNullPoint2", vav);
        BNumericPoint vavNullPoint3 = addNumericPoint("vavNullPoint3", vav);
        BNumericPoint vavSite2Point = addNumericTestProxyPoint("vavSite2Point", vav);
        BNumericPoint ahuPointSub3 = addNumericTestProxyPoint("ahuPointSub3", vav);

        BNumericPoint ahuPointSub1 = addNumericTestProxyPoint("ahuPointSub1", ahuPoints);
        BNumericPoint ahuPointSub2 = addNumericTestProxyPoint("ahuPointSub2", ahuPoints);
        BNumericPoint ahuNullPointSub1 = addNumericPoint("ahuNullPointSub1", ahuPoints);
        BNumericPoint ahuNullPointSub2 = addNumericPoint("ahuNullPointSub2", ahuPoints);

        addSiteRefRelation(ahuFolder, site1);
        addSiteRefRelation(vav, site1);
        addSiteRefRelation(vavNullPoint3, site2);
        addSiteRefRelation(vavSite2Point, site2);

        addEquipRefRelation(vavNullPoint2, vav);
        addEquipRefRelation(vavNullPoint3, vav);
        addEquipRefRelation(ahuNullPointSub2, ahuFolder);

        // Because of the direct relation, there should not be an implied equipRef relation to vav
        addEquipRefRelation(ahuPointSub3, ahuFolder);

        hasEquipRefs(ahuFolder,
            ahuPoint1,
            ahuPoint2,
            ahuPointSub1,
            ahuPointSub2,
            ahuPointSub3,
            ahuNullPointSub2);
        hasEquipRefs(vav,
            vavPoint1,
            vavPoint2,
            vavNullPoint2,
            vavNullPoint3,
            vavSite2Point);
        hasNoEquipRefs(
            vavNullPoint1,
            ahuNullPointSub1);
        hasSiteRefs(site1,
            ahuFolder,
            ahuPoint1,
            ahuPoint2,
            ahuPointSub1,
            ahuPointSub2,
            ahuPointSub3,
            ahuNullPointSub2,
            vav,
            vavPoint1,
            vavPoint2,
            vavNullPoint2);
        hasSiteRefs(site2,
            vavNullPoint3,
            vavSite2Point);
        hasNoSiteRefs(
            vavNullPoint1,
            ahuNullPointSub1);
    }

    public void haystackEquipTest()
    {
        // * station
        //   * haystackSites
        //     * haystackSite1 [BHSite]
        //     * haystackSite2 [BHSite]
        //   * haystackEquips
        //     * equip1 [BFolder]
        //       * equip [BHEquip] (siteRef->haystackSite1)
        //       * equip1Point1
        //       * equip1Point2 (siteRef->site2)
        //       // No implied equipRef relation to this point with a NullProxyExt
        //       * equip1NullPoint1
        //       // Has an implied siteRef relation to equip1's site (haystackSite1)
        //       // because of the direct equipRef relation
        //       * equip1NullPoint2 (equipRef->equip1)
        //       // Direct siteRef relation prevents an implied one to haystackSite1
        //       * equip1NullPoint3 (equipRef->equip1, siteRef->haystackSite2)
        //       * equip2Point5 (equipRef->equip2)
        //       * equip2Point6 (equipRef->equip2, siteRef->haystackSite1)
        //       // Has an implied siteRef relation to equip2's site (haystackSite2)
        //       // because of the direct equipRef relation
        //       * equip2NullPoint1 (equipRef->equip2)
        //       // Direct siteRef relation prevents an implied one to haystackSite2
        //       * equip2NullPoint2 (equipRef->equip2, siteRef->haystackSite1)
        //       * subPoints1
        //         * equip1Point3
        //         * equip1Point4 (siteRef->site2)
        //         // Similar tests except one folder below the BHEquip
        //         * equip1NullPoint4
        //         * equip1NullPoint5 (equipRef->equip1)
        //         * equip1NullPoint6 (equipRef->equip1, siteRef->haystackSite2)
        //         * equip2Point7 (equipRef->equip2)
        //         * equip2Point8 (equipRef->equip2, siteRef->haystackSite1)
        //         * equip2 [BFolder]
        //           * equip [BHEquip] (siteRef->haystackSite2)
        //           * equip2Point1
        //           * equip2Point2 (siteRef->haystackSite1)
        //           * equip1Point5 (equipRef->equip1)
        //           * equip1Point6 (equipRef->equip1, siteRef->haystackSite2)
        //           * subPoints2
        //             * equip2Point3
        //             * equip2Point4 (siteRef->haystackSite1)
        //             * equip1Point7 (equipRef->equip1)
        //             * equip1Point8 (equipRef->equip1, siteRef->haystackSite2)
        BFolder haystackSites = addFolder("haystackSites", testFolder);
        BFolder haystackEquips = addFolder("haystackEquips", testFolder);

        BHSite haystackSite1 = addChild("haystackSite1", new BHSite(), haystackSites);
        BHSite haystackSite2 = addChild("haystackSite2", new BHSite(), haystackSites);

        BFolder equip1Folder = addFolder("equip1Folder", haystackEquips);

        BHEquip equip1 = addChild("equip", new BHEquip(), equip1Folder);
        BNumericPoint equip1Point1 = addNumericTestProxyPoint("equip1Point1", equip1Folder);
        BNumericPoint equip1Point2 = addNumericTestProxyPoint("equip1Point2", equip1Folder);
        BNumericPoint equip1NullPoint1 = addNumericPoint("equip1NullPoint1", equip1Folder);
        BNumericPoint equip1NullPoint2 = addNumericPoint("equip1NullPoint2", equip1Folder);
        BNumericPoint equip1NullPoint3 = addNumericPoint("equip1NullPoint3", equip1Folder);
        BNumericPoint equip2Point5 = addNumericTestProxyPoint("equip2Point5", equip1Folder);
        BNumericPoint equip2Point6 = addNumericTestProxyPoint("equip2Point6", equip1Folder);
        BNumericPoint equip2NullPoint1 = addNumericPoint("equip2NullPoint1", equip1Folder);
        BNumericPoint equip2NullPoint2 = addNumericPoint("equip2NullPoint2", equip1Folder);
        BFolder subPoints1 = addFolder("subPoints1", equip1Folder);

        BNumericPoint equip1Point3 = addNumericTestProxyPoint("equip1Point3", subPoints1);
        BNumericPoint equip1Point4 = addNumericTestProxyPoint("equip1Point4", subPoints1);
        BNumericPoint equip1NullPoint4 = addNumericPoint("equip1NullPoint4", subPoints1);
        BNumericPoint equip1NullPoint5 = addNumericPoint("equip1NullPoint5", subPoints1);
        BNumericPoint equip1NullPoint6 = addNumericPoint("equip1NullPoint6", subPoints1);
        BNumericPoint equip2Point7 = addNumericTestProxyPoint("equip2Point7", subPoints1);
        BNumericPoint equip2Point8 = addNumericTestProxyPoint("equip2Point8", subPoints1);
        BFolder equip2Folder = addFolder("equip2", subPoints1);

        BHEquip equip2 = addChild("equip", new BHEquip(), equip2Folder);
        BNumericPoint equip2Point1 = addNumericTestProxyPoint("equip2Point1", equip2Folder);
        BNumericPoint equip2Point2 = addNumericTestProxyPoint("equip2Point2", equip2Folder);
        BNumericPoint equip1Point5 = addNumericTestProxyPoint("equip1Point5", equip2Folder);
        BNumericPoint equip1Point6 = addNumericTestProxyPoint("equip1Point6", equip2Folder);
        BFolder subPoints2 = addFolder("subPoints2", equip2Folder);

        BNumericPoint equip2Point3 = addNumericTestProxyPoint("equip2Point3", subPoints2);
        BNumericPoint equip2Point4 = addNumericTestProxyPoint("equip2Point4", subPoints2);
        BNumericPoint equip1Point7 = addNumericTestProxyPoint("equip1Point7", subPoints2);
        BNumericPoint equip1Point8 = addNumericTestProxyPoint("equip1Point8", subPoints2);

        addSiteRefRelation(equip1, haystackSite1);
        addSiteRefRelation(equip2Point2, haystackSite1);
        addSiteRefRelation(equip2Point4, haystackSite1);
        addSiteRefRelation(equip2Point6, haystackSite1);
        addSiteRefRelation(equip2Point8, haystackSite1);
        addSiteRefRelation(equip2NullPoint2, haystackSite1);

        addSiteRefRelation(equip2, haystackSite2);
        addSiteRefRelation(equip1Point2, haystackSite2);
        addSiteRefRelation(equip1Point4, haystackSite2);
        addSiteRefRelation(equip1Point6, haystackSite2);
        addSiteRefRelation(equip1Point8, haystackSite2);
        addSiteRefRelation(equip1NullPoint3, haystackSite2);
        addSiteRefRelation(equip1NullPoint6, haystackSite2);

        addEquipRefRelation(equip1Point5, equip1);
        addEquipRefRelation(equip1Point6, equip1);
        addEquipRefRelation(equip1Point7, equip1);
        addEquipRefRelation(equip1Point8, equip1);
        addEquipRefRelation(equip1NullPoint2, equip1);
        addEquipRefRelation(equip1NullPoint3, equip1);
        addEquipRefRelation(equip1NullPoint5, equip1);
        addEquipRefRelation(equip1NullPoint6, equip1);

        addEquipRefRelation(equip2Point5, equip2);
        addEquipRefRelation(equip2Point6, equip2);
        addEquipRefRelation(equip2Point7, equip2);
        addEquipRefRelation(equip2Point8, equip2);
        addEquipRefRelation(equip2NullPoint1, equip2);
        addEquipRefRelation(equip2NullPoint2, equip2);

        hasEquipRefs(equip1,
            equip1Point1,
            equip1Point2,
            equip1Point3,
            equip1Point4,
            equip1Point5,
            equip1Point6,
            equip1Point7,
            equip1Point8,
            equip1NullPoint2,
            equip1NullPoint3,
            equip1NullPoint5,
            equip1NullPoint6);
        hasEquipRefs(equip2,
            equip2Point1,
            equip2Point2,
            equip2Point3,
            equip2Point4,
            equip2Point5,
            equip2Point6,
            equip2Point7,
            equip2Point8,
            equip2NullPoint1,
            equip2NullPoint2);
        hasNoEquipRefs(
            equip1NullPoint1,
            equip1NullPoint4);
        hasSiteRefs(haystackSite1,
            equip1,
            equip1Point1,
            equip1Point3,
            equip1Point5,
            equip1Point7,
            equip2Point2,
            equip2Point4,
            equip2Point6,
            equip2Point8,
            equip1NullPoint2,
            equip1NullPoint5,
            equip2NullPoint2);
        hasSiteRefs(haystackSite2,
            equip2,
            equip2Point1,
            equip2Point3,
            equip2Point5,
            equip2Point7,
            equip1Point2,
            equip1Point4,
            equip1Point6,
            equip1Point8,
            equip1NullPoint3,
            equip1NullPoint6,
            equip2NullPoint1);
        hasNoEquipRefs(
            equip1NullPoint1,
            equip1NullPoint4);
    }

    public void equipRefAddedToBHEquipNamedEquip()
    {
        // Even though "a" comes before "equip" alphabetically, the name
        // "equip" is given priority

        // * station
        //   * test
        //     * equip1 [BHEquip]
        //     * equip [BHEquip]
        //     * point1
        //     * point2
        //     // No implied equipRef relation to this point with a NullProxyExt
        //     * nullPoint1
        //     * subPoints
        //       * point3
        //       * point4
        //       // No implied equipRef relation to this point with a NullProxyExt
        //       * nullPoint2
        BHEquip equip1 = addChild("equip1", new BHEquip(), testFolder);
        BHEquip equip = addChild("equip", new BHEquip(), testFolder);
        BNumericPoint point1 = addNumericTestProxyPoint("point1", testFolder);
        BNumericPoint point2 = addNumericTestProxyPoint("point2", testFolder);
        BNumericPoint nullPoint1 = addNumericPoint("nullPoint1", testFolder);
        BFolder subPoints = addFolder("subPoints", testFolder);

        BNumericPoint point3 = addNumericTestProxyPoint("point3", subPoints);
        BNumericPoint point4 = addNumericTestProxyPoint("point4", subPoints);
        BNumericPoint nullPoint2 = addNumericPoint("nullPoint2", testFolder);

        hasEquipRefs(equip,
            point1,
            point2,
            point3,
            point4);
        hasNoEquipRefs(
            equip1,
            nullPoint1,
            nullPoint2);
    }

    public void equipRefAddedToBHEquipNamedA()
    {
        // "a" is given priority over "b" because neither is named "equip"
        // and "a" comes before "b" alphabetically

        // * station
        //   * test
        //     * equip2
        //     * equip1
        //     * point1
        //     * point2
        //     // No implied equipRef relation to this point with a NullProxyExt
        //     * nullPoint1
        //     * subPoints
        //       * point3
        //       * point4
        //       // No implied equipRef relation to this point with a NullProxyExt
        //       * nullPoint2
        BHEquip equip2 = addChild("equip2", new BHEquip(), testFolder);
        BHEquip equip1 = addChild("equip1", new BHEquip(), testFolder);
        BNumericPoint point1 = addNumericTestProxyPoint("point1", testFolder);
        BNumericPoint point2 = addNumericTestProxyPoint("point2", testFolder);
        BNumericPoint nullPoint1 = addNumericPoint("nullPoint1", testFolder);
        BFolder subPoints = addFolder("subPoints", testFolder);

        BNumericPoint point3 = addNumericTestProxyPoint("point3", subPoints);
        BNumericPoint point4 = addNumericTestProxyPoint("point4", subPoints);
        BNumericPoint nullPoint2 = addNumericPoint("nullPoint2", subPoints);

        hasEquipRefs(equip2,
            point1,
            point2,
            point3,
            point4);
        hasNoEquipRefs(
            equip1,
            nullPoint1,
            nullPoint2);
    }

    public void equipRefAddedToPointsUnderMultipleBranchesOfEquipTaggedFolder()
    {
        // * station
        //   * test
        //     * equip
        //       * point1
        //       * point2
        //       // No implied equipRef relation to this point with a NullProxyExt
        //       * nullPoint1
        //       * nullPoint2 (equipRef->equip)
        //       * subPoints1
        //         * point3
        //         * point4
        //         // No implied equipRef relation to this point with a NullProxyExt
        //         * nullPoint3
        //         * nullPoint4 (equipRef->equip)
        //         * subPoints2
        //           * point5
        //           * point6
        //           // No implied equipRef relation to this point with a NullProxyExt
        //           * nullPoint5
        //           * nullPoint6 (equipRef->equip)
        //         * subPoints3
        //           * point7
        //           * point8
        //       * subPoints4
        //         * point9
        //         * point10
        //         * subPoints5
        //           * point11
        //           * point12
        //         * subPoints6
        //           * point13
        //           * point14
        BFolder equip = addFolder("subPoints", testFolder);
        addEquipTag(equip);

        BNumericPoint point1 = addNumericTestProxyPoint("point1", equip);
        BNumericPoint point2 = addNumericTestProxyPoint("point2", equip);
        BNumericPoint nullPoint1 = addNumericPoint("nullPoint1", equip);
        BNumericPoint nullPoint2 = addNumericPoint("nullPoint2", equip);
        BFolder subPoints1 = addFolder("subPoints1", equip);
        BFolder subPoints4 = addFolder("subPoints4", equip);

        BNumericPoint point3 = addNumericTestProxyPoint("point3", subPoints1);
        BNumericPoint point4 = addNumericTestProxyPoint("point4", subPoints1);
        BNumericPoint nullPoint3 = addNumericPoint("nullPoint3", subPoints1);
        BNumericPoint nullPoint4 = addNumericPoint("nullPoint4", subPoints1);
        BFolder subPoints2 = addFolder("subPoints2", subPoints1);
        BFolder subPoints3 = addFolder("subPoints3", subPoints1);

        BNumericPoint point5 = addNumericTestProxyPoint("point5", subPoints2);
        BNumericPoint point6 = addNumericTestProxyPoint("point6", subPoints2);
        BNumericPoint nullPoint5 = addNumericPoint("nullPoint5", subPoints2);
        BNumericPoint nullPoint6 = addNumericPoint("nullPoint6", subPoints2);

        BNumericPoint point7 = addNumericTestProxyPoint("point7", subPoints3);
        BNumericPoint point8 = addNumericTestProxyPoint("point8", subPoints3);

        BNumericPoint point9 = addNumericTestProxyPoint("point9", subPoints4);
        BNumericPoint point10 = addNumericTestProxyPoint("point10", subPoints4);
        BFolder subPoints5 = addFolder("subPoints5", subPoints4);
        BFolder subPoints6 = addFolder("subPoints6", subPoints4);

        BNumericPoint point11 = addNumericTestProxyPoint("point11", subPoints5);
        BNumericPoint point12 = addNumericTestProxyPoint("point12", subPoints5);

        BNumericPoint point13 = addNumericTestProxyPoint("point13", subPoints6);
        BNumericPoint point14 = addNumericTestProxyPoint("point14", subPoints6);

        addEquipRefRelation(nullPoint2, equip);
        addEquipRefRelation(nullPoint4, equip);
        addEquipRefRelation(nullPoint6, equip);

        hasEquipRefs(equip,
            point1,
            point2,
            point3,
            point4,
            point5,
            point6,
            point7,
            point8,
            point9,
            point10,
            point11,
            point12,
            point13,
            point14,
            nullPoint2,
            nullPoint4,
            nullPoint6);
        hasNoEquipRefs(
            nullPoint1,
            nullPoint3,
            nullPoint5);
    }

    public void equipRefAddedToPointsUnderMultipleBranchesOfBHEquip()
    {
        // * station
        //   * test
        //     * equip
        //     * point1
        //     * point2
        //     // No implied equipRef relation to this point with a NullProxyExt
        //     * nullPoint1
        //     * nullPoint2 (equipRef->equip)
        //     * subPoints1
        //       * point3
        //       * point4
        //       // No implied equipRef relation to this point with a NullProxyExt
        //       * nullPoint3
        //       * nullPoint4 (equipRef->equip)
        //       * subPoints2
        //         * point5
        //         * point6
        //         // No implied equipRef relation to this point with a NullProxyExt
        //         * nullPoint5
        //         * nullPoint6 (equipRef->equip)
        //       * subPoints3
        //         * point7
        //         * point8
        //     * subPoints4
        //       * point9
        //       * point10
        //       * subPoints5
        //         * point11
        //         * point12
        //       * subPoints6
        //         * point13
        //         * point14
        BHEquip equip = addChild("equip", new BHEquip(), testFolder);
        BNumericPoint point1 = addNumericTestProxyPoint("point1", testFolder);
        BNumericPoint point2 = addNumericTestProxyPoint("point2", testFolder);
        BNumericPoint nullPoint1 = addNumericPoint("nullPoint1", testFolder);
        BNumericPoint nullPoint2 = addNumericPoint("nullPoint2", testFolder);
        BFolder subPoints1 = addFolder("subPoints1", testFolder);
        BFolder subPoints4 = addFolder("subPoints4", testFolder);

        BNumericPoint point3 = addNumericTestProxyPoint("point3", subPoints1);
        BNumericPoint point4 = addNumericTestProxyPoint("point4", subPoints1);
        BNumericPoint nullPoint3 = addNumericPoint("nullPoint3", subPoints1);
        BNumericPoint nullPoint4 = addNumericPoint("nullPoint4", subPoints1);
        BFolder subPoints2 = addFolder("subPoints2", subPoints1);
        BFolder subPoints3 = addFolder("subPoints3", subPoints1);

        BNumericPoint point5 = addNumericTestProxyPoint("point5", subPoints2);
        BNumericPoint point6 = addNumericTestProxyPoint("point6", subPoints2);
        BNumericPoint nullPoint5 = addNumericPoint("nullPoint5", subPoints2);
        BNumericPoint nullPoint6 = addNumericPoint("nullPoint6", subPoints2);

        BNumericPoint point7 = addNumericTestProxyPoint("point7", subPoints3);
        BNumericPoint point8 = addNumericTestProxyPoint("point8", subPoints3);

        BNumericPoint point9 = addNumericTestProxyPoint("point9", subPoints4);
        BNumericPoint point10 = addNumericTestProxyPoint("point10", subPoints4);
        BFolder subPoints5 = addFolder("subPoints5", subPoints4);
        BFolder subPoints6 = addFolder("subPoints6", subPoints4);

        BNumericPoint point11 = addNumericTestProxyPoint("point11", subPoints5);
        BNumericPoint point12 = addNumericTestProxyPoint("point12", subPoints5);

        BNumericPoint point13 = addNumericTestProxyPoint("point13", subPoints6);
        BNumericPoint point14 = addNumericTestProxyPoint("point14", subPoints6);

        addEquipRefRelation(nullPoint2, equip);
        addEquipRefRelation(nullPoint4, equip);
        addEquipRefRelation(nullPoint6, equip);

        hasEquipRefs(equip,
            point1,
            point2,
            point3,
            point4,
            point5,
            point6,
            point7,
            point8,
            point9,
            point10,
            point11,
            point12,
            point13,
            point14,
            nullPoint2,
            nullPoint4,
            nullPoint6);
        hasNoEquipRefs(
            nullPoint1,
            nullPoint3,
            nullPoint5);
    }

    public void combinationEquipTaggedComponentsAndBHEquip1()
    {
        // * station
        //   * test
        //     * equipFolder1
        //       * equip
        //       * point1
        //       * point2
        //       * subPoints1
        //         * point3
        //         * point4
        //         * equipFolder2
        //           * point5
        //           * point6
        //           * subPoints2
        //             * point7
        //             * point8
        BFolder equipFolder1 = addFolder("equipFolder1", testFolder);
        addEquipTag(equipFolder1);

        BHEquip equip = addChild("equip", new BHEquip(), equipFolder1);
        BNumericPoint point1 = addNumericTestProxyPoint("point1", equipFolder1);
        BNumericPoint point2 = addNumericTestProxyPoint("point2", equipFolder1);
        BFolder subPoints1 = addFolder("subPoints1", equipFolder1);

        BNumericPoint point3 = addNumericTestProxyPoint("point3", subPoints1);
        BNumericPoint point4 = addNumericTestProxyPoint("point4", subPoints1);
        BFolder equipFolder2 = addFolder("equipFolder2", subPoints1);
        addEquipTag(equipFolder2);

        BNumericPoint point5 = addNumericTestProxyPoint("point5", equipFolder2);
        BNumericPoint point6 = addNumericTestProxyPoint("point6", equipFolder2);
        BFolder subPoints2 = addFolder("subPoints2", equipFolder2);

        BNumericPoint point7 = addNumericTestProxyPoint("point7", subPoints2);
        BNumericPoint point8 = addNumericTestProxyPoint("point8", subPoints2);

        hasEquipRefs(equip,
            point1,
            point2,
            point3,
            point4);
        hasEquipRefs(equipFolder2,
            point5,
            point6,
            point7,
            point8);
        hasNoEquipRefs(equipFolder1);
    }

    public void combinationEquipTaggedComponentsAndBHEquip2()
    {
        // * station
        //   * test
        //     * equipFolder1
        //       * point1
        //       * point2
        //       * subPoints1
        //         * point3
        //         * point4
        //         * equipFolder2
        //           * equip
        //           * point5
        //           * point6
        //           * subPoints2
        //             * point7
        //             * point8
        BFolder equipFolder1 = addFolder("equipFolder1", testFolder);
        addEquipTag(equipFolder1);

        BNumericPoint point1 = addNumericTestProxyPoint("point1", equipFolder1);
        BNumericPoint point2 = addNumericTestProxyPoint("point2", equipFolder1);
        BFolder subPoints1 = addFolder("subPoints1", equipFolder1);

        BNumericPoint point3 = addNumericTestProxyPoint("point3", subPoints1);
        BNumericPoint point4 = addNumericTestProxyPoint("point4", subPoints1);
        BFolder equipFolder2 = addFolder("equipFolder2", subPoints1);
        addEquipTag(equipFolder2);

        BHEquip equip = addChild("equip", new BHEquip(), equipFolder2);
        BNumericPoint point5 = addNumericTestProxyPoint("point5", equipFolder2);
        BNumericPoint point6 = addNumericTestProxyPoint("point6", equipFolder2);
        BFolder subPoints2 = addFolder("subPoints2", equipFolder2);

        BNumericPoint point7 = addNumericTestProxyPoint("point7", subPoints2);
        BNumericPoint point8 = addNumericTestProxyPoint("point8", subPoints2);

        hasEquipRefs(equipFolder1,
            point1,
            point2,
            point3,
            point4);
        hasEquipRefs(equip,
            point5,
            point6,
            point7,
            point8);
        hasNoEquipRefs(equipFolder2);
    }

    public void addedToDescendantsOfBHEquip()
    {
        // * station
        //   * test
        //     * equipFolder1
        //       * equip1Point1
        //       * equip1Point2
        //       // No implied equipRef relation to this point with a NullProxyExt
        //       * equip1NullPoint1
        //       * equip1NullPoint2 (equipRef->equip1)
        //       * equip [BHEquip]
        //         * equip1Point3
        //         * equip1Point4
        //         // No implied equipRef relation to this point with a NullProxyExt
        //         * equip1NullPoint3
        //         * equip1NullPoint4 (equipRef->equip1)
        //         * subPoints2
        //           * equip1Point5
        //           * equip1Point6
        //           // No implied equipRef relation to this point with a NullProxyExt
        //           * equip1NullPoint5
        //           * equip1NullPoint6 (equipRef->equip1)
        //           * equipFolder2
        //             * equip [BHEquip]
        //             * equip2Point1
        //             * equip2Point2
        //             // No implied equipRef relation to this point with a NullProxyExt
        //             * equip2NullPoint1
        //             * equip2NullPoint2 (equipRef->equip2)
        //           * equipFolder3 [hs:equip]
        //             * equip3Point1
        //             * equip3Point2
        //             // No implied equipRef relation to this point with a NullProxyExt
        //             * equip3NullPoint1
        //             * equip3NullPoint2 (equipRef->equipFolder3)
        //           * subPoints3
        //             * equip1Point7
        //             * equip1Point8
        //             // No implied equipRef relation to this point with a NullProxyExt
        //             * equip1NullPoint7
        //             * equip1NullPoint8 (equipRef->equip1)
        //       * subPoints1
        //         * equip1Point9
        //         * equip1Point10
        //         // No implied equipRef relation to this point with a NullProxyExt
        //         * equip1NullPoint9
        //         * equip1NullPoint10 (equipRef->equip1)
        //       * equipFolder4
        //         * equip [BHEquip]
        //         * equip4Point1
        //         * equip4Point2
        //         // No implied equipRef relation to this point with a NullProxyExt
        //         * equip4NullPoint1
        //         * equip4NullPoint2 (equipRef->equip4)
        //       * equipFolder5 [hs:equip]
        //         * equip5Point1
        //         * equip5Point2
        //         // No implied equipRef relation to this point with a NullProxyExt
        //         * equip5NullPoint1
        //         * equip5NullPoint2 (equipRef->equipFolder5)
        BFolder equipFolder1 = addFolder("equipFolder1", testFolder);

        BNumericPoint equip1Point1 = addNumericTestProxyPoint("equip1Point1", equipFolder1);
        BNumericPoint equip1Point2 = addNumericTestProxyPoint("equip1Point2", equipFolder1);
        BNumericPoint equip1NullPoint1 = addNumericPoint("equip1NullPoint1", equipFolder1);
        BNumericPoint equip1NullPoint2 = addNumericPoint("equip1NullPoint2", equipFolder1);
        BHEquip equip1 = addChild("equip", new BHEquip(), equipFolder1);
        BFolder subPoints1 = addFolder("subPoints1", equipFolder1);
        BFolder equipFolder4 = addFolder("equipFolder4", equipFolder1);
        BFolder equipFolder5 = addFolder("equipFolder5", equipFolder1);
        addEquipTag(equipFolder5);

        BNumericPoint equip1Point9 = addNumericTestProxyPoint("equip1Point9", subPoints1);
        BNumericPoint equip1Point10 = addNumericTestProxyPoint("equip1Point10", subPoints1);
        BNumericPoint equip1NullPoint9 = addNumericPoint("equip1NullPoint9", subPoints1);
        BNumericPoint equip1NullPoint10 = addNumericPoint("equip1NullPoint10", subPoints1);

        BHEquip equip4 = addChild("equip", new BHEquip(), equipFolder4);
        BNumericPoint equip4Point1 = addNumericTestProxyPoint("equip4Point1", equipFolder4);
        BNumericPoint equip4Point2 = addNumericTestProxyPoint("equip4Point2", equipFolder4);
        BNumericPoint equip4NullPoint1 = addNumericPoint("equip4NullPoint1", equipFolder4);
        BNumericPoint equip4NullPoint2 = addNumericPoint("equip4NullPoint2", equipFolder4);

        BNumericPoint equip5Point1 = addNumericTestProxyPoint("equip5Point1", equipFolder5);
        BNumericPoint equip5Point2 = addNumericTestProxyPoint("equip5Point2", equipFolder5);
        BNumericPoint equip5NullPoint1 = addNumericPoint("equip5NullPoint1", equipFolder5);
        BNumericPoint equip5NullPoint2 = addNumericPoint("equip5NullPoint2", equipFolder5);

        BNumericPoint equip1Point3 = addNumericTestProxyPoint("equip1Point3", equip1);
        BNumericPoint equip1Point4 = addNumericTestProxyPoint("equip1Point4", equip1);
        BNumericPoint equip1NullPoint3 = addNumericPoint("equip1NullPoint3", equip1);
        BNumericPoint equip1NullPoint4 = addNumericPoint("equip1NullPoint4", equip1);
        BFolder subPoints2 = addFolder("subPoints2", equip1);

        BNumericPoint equip1Point5 = addNumericTestProxyPoint("equip1Point5", subPoints2);
        BNumericPoint equip1Point6 = addNumericTestProxyPoint("equip1Point6", subPoints2);
        BNumericPoint equip1NullPoint5 = addNumericPoint("equip1NullPoint5", subPoints2);
        BNumericPoint equip1NullPoint6 = addNumericPoint("equip1NullPoint6", subPoints2);
        BFolder equipFolder2 = addFolder("equipFolder2", subPoints2);
        BFolder equipFolder3 = addFolder("equipFolder3", subPoints2);
        addEquipTag(equipFolder3);
        BFolder subPoints3 = addFolder("subPoints3", subPoints2);

        BHEquip equip2 = addChild("equip", new BHEquip(), equipFolder2);
        BNumericPoint equip2Point1 = addNumericTestProxyPoint("equip2Point1", equipFolder2);
        BNumericPoint equip2Point2 = addNumericTestProxyPoint("equip2Point2", equipFolder2);
        BNumericPoint equip2NullPoint1 = addNumericPoint("equip2NullPoint1", equipFolder2);
        BNumericPoint equip2NullPoint2 = addNumericPoint("equip2NullPoint2", equipFolder2);

        BNumericPoint equip3Point1 = addNumericTestProxyPoint("equip3Point1", equipFolder3);
        BNumericPoint equip3Point2 = addNumericTestProxyPoint("equip3Point2", equipFolder3);
        BNumericPoint equip3NullPoint1 = addNumericPoint("equip3NullPoint1", equipFolder3);
        BNumericPoint equip3NullPoint2 = addNumericPoint("equip3NullPoint2", equipFolder3);

        BNumericPoint equip1Point7 = addNumericTestProxyPoint("equip1Point7", subPoints3);
        BNumericPoint equip1Point8 = addNumericTestProxyPoint("equip1Point8", subPoints3);
        BNumericPoint equip1NullPoint7 = addNumericPoint("equip1NullPoint7", subPoints3);
        BNumericPoint equip1NullPoint8 = addNumericPoint("equip1NullPoint8", subPoints3);

        addEquipRefRelation(equip1NullPoint2, equip1);
        addEquipRefRelation(equip1NullPoint4, equip1);
        addEquipRefRelation(equip1NullPoint6, equip1);
        addEquipRefRelation(equip1NullPoint8, equip1);
        addEquipRefRelation(equip1NullPoint10, equip1);

        addEquipRefRelation(equip2NullPoint2, equip2);

        addEquipRefRelation(equip3NullPoint2, equipFolder3);

        addEquipRefRelation(equip4NullPoint2, equip4);

        addEquipRefRelation(equip5NullPoint2, equipFolder5);

        hasEquipRefs(equip1,
            equip1Point1,
            equip1Point2,
            equip1Point3,
            equip1Point4,
            equip1Point5,
            equip1Point6,
            equip1Point7,
            equip1Point8,
            equip1Point9,
            equip1Point10,
            equip1NullPoint2,
            equip1NullPoint4,
            equip1NullPoint6,
            equip1NullPoint8,
            equip1NullPoint10);
        hasEquipRefs(equip2,
            equip2Point1,
            equip2Point2,
            equip2NullPoint2);
        hasEquipRefs(equipFolder3,
            equip3Point1,
            equip3Point2,
            equip3NullPoint2);
        hasEquipRefs(equip4,
            equip4Point1,
            equip4Point2,
            equip4NullPoint2);
        hasEquipRefs(equipFolder5,
            equip5Point1,
            equip5Point2,
            equip5NullPoint2);
        hasNoEquipRefs(
            equip1NullPoint1,
            equip1NullPoint3,
            equip1NullPoint5,
            equip1NullPoint7,
            equip1NullPoint9,
            equip2NullPoint1,
            equip3NullPoint1,
            equip4NullPoint1,
            equip5NullPoint1);
    }

    public void impliedEquipRef1Test()
    {
        addEquipTag(impliedEquip1);
        try
        {
            hasEquipRefs(impliedEquip1,
                impliedEquip1Point,
                impliedEquip2Point,
                impliedEquip3Point,
                impliedEquip4Point,
                impliedEquip5Point);
            hasNoEquipRefs(
                impliedEquip2,
                impliedEquip3,
                impliedEquip4,
                impliedEquip5);
        }
        finally
        {
            removeEquipTag(impliedEquip1);
        }
    }

    public void impliedEquipRef2Test()
    {
        addEquipTag(impliedEquip2);
        try
        {
            hasEquipRefs(impliedEquip2,
                impliedEquip2Point,
                impliedEquip3Point,
                impliedEquip4Point,
                impliedEquip5Point);
            hasNoEquipRefs(
                impliedEquip1,
                impliedEquip3,
                impliedEquip4,
                impliedEquip5,
                impliedEquip1Point);
        }
        finally
        {
            removeEquipTag(impliedEquip2);
        }
    }

    public void impliedEquipRef3Test()
    {
        addEquipTag(impliedEquip3);
        try
        {
            hasEquipRefs(impliedEquip3,
                impliedEquip3Point,
                impliedEquip4Point,
                impliedEquip5Point);
            hasNoEquipRefs(
                impliedEquip1,
                impliedEquip2,
                impliedEquip4,
                impliedEquip5,
                impliedEquip1Point,
                impliedEquip2Point);
        }
        finally
        {
            removeEquipTag(impliedEquip3);
        }
    }

    public void impliedEquipRef4Test()
    {
        addEquipTag(impliedEquip4);
        try
        {
            hasEquipRefs(impliedEquip4,
                impliedEquip4Point,
                impliedEquip5Point);
            hasNoEquipRefs(
                impliedEquip1,
                impliedEquip2,
                impliedEquip3,
                impliedEquip5,
                impliedEquip1Point,
                impliedEquip2Point,
                impliedEquip3Point);
        }
        finally
        {
            removeEquipTag(impliedEquip4);
        }
    }

    public void impliedEquipRef5Test()
    {
        addEquipTag(impliedEquip5);
        try
        {
            hasEquipRefs(impliedEquip5,
                impliedEquip5Point);
            hasNoEquipRefs(
                impliedEquip1,
                impliedEquip2,
                impliedEquip3,
                impliedEquip4,
                impliedEquip1Point,
                impliedEquip2Point,
                impliedEquip3Point,
                impliedEquip4Point);
        }
        finally
        {
            removeEquipTag(impliedEquip5);
        }
    }

    public void directEquipRefTest()
    {
        addEquipTag(impliedEquip1);
        addEquipRefRelation(impliedEquip3Point, directEquip);
        try
        {
            hasEquipRefs(impliedEquip1,
                impliedEquip1Point,
                impliedEquip2Point,
                impliedEquip4Point,
                impliedEquip5Point);
            hasEquipRefs(directEquip,
                impliedEquip3Point);
            hasNoEquipRefs(
                impliedEquip2,
                impliedEquip3,
                impliedEquip4,
                impliedEquip5);
        }
        finally
        {
            removeEquipTag(impliedEquip1);
            removeEquipRefRelation(impliedEquip3Point, directEquip);
        }
    }

    private BComponent directEquip;
    private BFolder impliedEquip1;
    private BFolder impliedEquip2;
    private BFolder impliedEquip3;
    private BFolder impliedEquip4;
    private BFolder impliedEquip5;
    private BNumericPoint impliedEquip1Point;
    private BNumericPoint impliedEquip2Point;
    private BNumericPoint impliedEquip3Point;
    private BNumericPoint impliedEquip4Point;
    private BNumericPoint impliedEquip5Point;

    private BFolder testFolder;
}
