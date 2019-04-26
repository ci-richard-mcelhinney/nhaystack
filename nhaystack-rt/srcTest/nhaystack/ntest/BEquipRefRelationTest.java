//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Apr 2019  Eric Anderson  Creation
//
package nhaystack.ntest;

import static nhaystack.util.NHaystackConst.ID_AHU;
import static nhaystack.util.NHaystackConst.ID_EQUIP;
import static nhaystack.util.NHaystackConst.ID_EQUIP_REF;
import static nhaystack.util.NHaystackConst.ID_SITE;
import static nhaystack.util.NHaystackConst.ID_SITE_REF;
import static nhaystack.util.NHaystackConst.ID_VAV;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import javax.baja.control.BControlPoint;
import javax.baja.control.BNumericPoint;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BMarker;
import javax.baja.sys.BRelation;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Id;
import javax.baja.tag.Relation;
import javax.baja.util.BFolder;

import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
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
        //       * impliedEquip2
        //         * impliedEquip2Point
        //           * impliedEquip3
        //             * impliedEquip3Point
        //               * impliedEquip4
        //                 * impliedEquip4Point
        //                   * impliedEquip5
        //                     * impliedEquip5Point
        directEquip.tags().set(ID_EQUIP, BMarker.MARKER);
        station.add("directEquipFolder", directEquip);
        station.add("impliedEquip1", impliedEquip1);
        impliedEquip1.add("impliedEquip1Point", impliedEquip1Point);
        impliedEquip1.add("impliedEquip2", impliedEquip2);
        impliedEquip2.add("impliedEquip2Point", impliedEquip2Point);
        impliedEquip2.add("impliedEquip3", impliedEquip3);
        impliedEquip3.add("impliedEquip3Point", impliedEquip3Point);
        impliedEquip3.add("impliedEquip4", impliedEquip4);
        impliedEquip4.add("impliedEquip4Point", impliedEquip4Point);
        impliedEquip4.add("impliedEquip5", impliedEquip5);
        impliedEquip5.add("impliedEquip5Point", impliedEquip5Point);
    }

    public void ahuVavPointTest() throws Exception
    {
        BStation station = stationHandler.getStation();
        BComponent site1 = new BComponent();
        station.add("site1", site1);
        BComponent site2 = new BComponent();
        station.add("site2", site2);
        BFolder ahuFolder = new BFolder();
        station.add("ahuFolder", ahuFolder);

        try
        {
            // build the following component tree
            // site1 (site)
            // site2 (site)
            // ahuFolder (equip & siteRef -> site1)
            //    vav (equip & siteRef -> site1)
            //      vavPoint1
            //      vavPoint2
            //      vavSite2Point (siteRef -> site2)
            //      ahuPointSub3 (equipRef -> ahuFolder)
            //    ahuSubPoints
            //      ahuSubPoint1
            //      ahuSubPoint2
            //    ahuPoint1
            //    ahuPoint2
            // should result in the following hs:equipRef relations.
            //    ahuPoint1----->ahuFolder
            //    ahuPoint2----->ahuFolder
            //    ahuSubPoint1-->ahuFolder
            //    ahuSubPoint2-->ahuFolder
            //    ahuSubPoint3-->ahuFolder
            //    vavPoint1----->vav
            //    vavPoint2----->vav
            //    vavSite2Point->vav
            // should result in the following hs:siteRef relations
            //    ahuFolder----->site1
            //    ahuPoint1----->site1
            //    ahuPoint2----->site1
            //    ahuPointSub1-->site1
            //    ahuPointSub2-->site1
            //    ahuPointSub3-->site1
            //    vav----------->site1
            //    vavPoint1----->site1
            //    vavPoint2----->site1
            //    vavSite2Point->site2
            BFolder vav = new BFolder();
            BNumericPoint vavPoint1 = new BNumericPoint();
            BNumericPoint vavPoint2 = new BNumericPoint();
            BFolder ahuPoints = new BFolder();
            BNumericPoint ahuPointSub1 = new BNumericPoint();
            BNumericPoint ahuPointSub2 = new BNumericPoint();
            BNumericPoint ahuPointSub3 = new BNumericPoint();
            BNumericPoint vavSite2Point = new BNumericPoint();
            BNumericPoint ahuPoint1 = new BNumericPoint();
            BNumericPoint ahuPoint2 = new BNumericPoint();

            addSiteTag(site1);
            addSiteTag(site2);
            addEquipTag(ahuFolder);
            ahuFolder.tags().set(ID_AHU, BMarker.MARKER);
            addEquipTag(vav);
            vav.tags().set(ID_VAV, BMarker.MARKER);

            addSiteRefRelation(ahuFolder, site1);
            addSiteRefRelation(vav, site1);
            addSiteRefRelation(vavSite2Point, site2);

            // Because of the direct relation, there should not be an implied equipRef relation to vav
            addEquipRefRelation(ahuPointSub3, ahuFolder);

            // build tree
            ahuFolder.add("vav", vav);
            ahuFolder.add("ahuPoints", ahuPoints);
            ahuFolder.add("ahuPoint1", ahuPoint1);
            ahuFolder.add("ahuPoint2", ahuPoint2);
            vav.add("vavPoint1", vavPoint1);
            vav.add("vavPoint2", vavPoint2);
            vav.add("vavSite2Point", vavSite2Point);
            vav.add("ahuPointSub3", ahuPointSub3);
            ahuPoints.add("ahuPointSub1", ahuPointSub1);
            ahuPoints.add("ahuPointSub2", ahuPointSub2);

            hasEquipRefs(ahuFolder,
                ahuPoint1,
                ahuPoint2,
                ahuPointSub1,
                ahuPointSub2,
                ahuPointSub3);
            hasEquipRefs(vav,
                vavPoint1,
                vavPoint2,
                vavSite2Point);
            hasSiteRefs(site1,
                ahuFolder,
                ahuPoint1,
                ahuPoint2,
                ahuPointSub1,
                ahuPointSub2,
                ahuPointSub3,
                vav,
                vavPoint1,
                vavPoint2);
            hasSiteRefs(site2,
                vavSite2Point);
        }
        finally
        {
            station.remove(site1);
            station.remove(site2);
            station.remove(ahuFolder);
        }
    }

    public void haystackEquipTest()
    {
        BStation station = stationHandler.getStation();
        BFolder haystackSites = new BFolder();
        station.add("haystackSites", haystackSites);
        BFolder haystackEquips = new BFolder();
        station.add("haystackEquips", haystackEquips);

        try
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
            //       * equip2Point5 (equipRef->equip2)
            //       * equip2Point6 (equipRef->equip2, siteRef->haystackSite1)
            //       * subPoints1
            //         * equip1Point3
            //         * equip1Point4 (siteRef->site2)
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
            BHSite haystackSite1 = new BHSite();
            BHSite haystackSite2 = new BHSite();
            BHEquip equip1 = new BHEquip();
            BFolder equip1Folder = new BFolder();
            BFolder subPoints1 = new BFolder();
            BFolder equip2Folder = new BFolder();
            BFolder subPoints2 = new BFolder();
            BNumericPoint equip1Point1 = new BNumericPoint();
            BNumericPoint equip1Point2 = new BNumericPoint();
            BNumericPoint equip1Point3 = new BNumericPoint();
            BNumericPoint equip1Point4 = new BNumericPoint();
            BNumericPoint equip1Point5 = new BNumericPoint();
            BNumericPoint equip1Point6 = new BNumericPoint();
            BNumericPoint equip1Point7 = new BNumericPoint();
            BNumericPoint equip1Point8 = new BNumericPoint();
            BHEquip equip2 = new BHEquip();
            BNumericPoint equip2Point1 = new BNumericPoint();
            BNumericPoint equip2Point2 = new BNumericPoint();
            BNumericPoint equip2Point3 = new BNumericPoint();
            BNumericPoint equip2Point4 = new BNumericPoint();
            BNumericPoint equip2Point5 = new BNumericPoint();
            BNumericPoint equip2Point6 = new BNumericPoint();
            BNumericPoint equip2Point7 = new BNumericPoint();
            BNumericPoint equip2Point8 = new BNumericPoint();

            haystackSites.add("haystackSite1", haystackSite1);
            haystackSites.add("haystackSite2", haystackSite2);

            haystackEquips.add("equipFolder", equip1Folder);

            equip1Folder.add("equip", equip1);
            equip1Folder.add("equip1Point1", equip1Point1);
            equip1Folder.add("equip1Point2", equip1Point2);
            equip1Folder.add("equip2Point5", equip2Point5);
            equip1Folder.add("equip2Point6", equip2Point6);
            equip1Folder.add("subPoints1", subPoints1);

            subPoints1.add("equip1Point3", equip1Point3);
            subPoints1.add("equip1Point4", equip1Point4);
            subPoints1.add("equip2Point7", equip2Point7);
            subPoints1.add("equip2Point8", equip2Point8);
            subPoints1.add("equip2", equip2Folder);

            equip2Folder.add("equip", equip2);
            equip2Folder.add("equip2Point1", equip2Point1);
            equip2Folder.add("equip2Point2", equip2Point2);
            equip2Folder.add("equip1Point5", equip1Point5);
            equip2Folder.add("equip1Point6", equip1Point6);
            equip2Folder.add("subPoints2", subPoints2);

            subPoints2.add("equip2Point3", equip2Point3);
            subPoints2.add("equip2Point4", equip2Point4);
            subPoints2.add("equip1Point7", equip1Point7);
            subPoints2.add("equip1Point8", equip1Point8);

            addSiteRefRelation(equip1, haystackSite1);
            addSiteRefRelation(equip1Point2, haystackSite2);
            addSiteRefRelation(equip2Point6, haystackSite1);
            addSiteRefRelation(equip1Point4, haystackSite2);
            addSiteRefRelation(equip2Point8, haystackSite1);
            addSiteRefRelation(equip2, haystackSite2);
            addSiteRefRelation(equip2Point2, haystackSite1);
            addSiteRefRelation(equip1Point6, haystackSite2);
            addSiteRefRelation(equip2Point4, haystackSite1);
            addSiteRefRelation(equip1Point8, haystackSite2);

            addEquipRefRelation(equip2Point5, equip2);
            addEquipRefRelation(equip2Point6, equip2);
            addEquipRefRelation(equip2Point7, equip2);
            addEquipRefRelation(equip2Point8, equip2);
            addEquipRefRelation(equip1Point5, equip1);
            addEquipRefRelation(equip1Point6, equip1);
            addEquipRefRelation(equip1Point7, equip1);
            addEquipRefRelation(equip1Point8, equip1);

            hasEquipRefs(equip1,
                equip1Point1,
                equip1Point2,
                equip1Point3,
                equip1Point4,
                equip1Point5,
                equip1Point6,
                equip1Point7,
                equip1Point8);
            hasEquipRefs(equip2,
                equip2Point1,
                equip2Point2,
                equip2Point3,
                equip2Point4,
                equip2Point5,
                equip2Point6,
                equip2Point7,
                equip2Point8);
            hasSiteRefs(haystackSite1,
                equip1,
                equip1Point1,
                equip1Point3,
                equip1Point5,
                equip1Point7,
                equip2Point2,
                equip2Point4,
                equip2Point6,
                equip2Point8);
            hasSiteRefs(haystackSite2,
                equip2,
                equip2Point1,
                equip2Point3,
                equip2Point5,
                equip2Point7,
                equip1Point2,
                equip1Point4,
                equip1Point6,
                equip1Point8);
        }
        finally
        {
            station.remove(haystackSites);
            station.remove(haystackEquips);
        }
    }

    public void equipRefAddedToBHEquipNamedEquip()
    {
        BStation station = stationHandler.getStation();
        BFolder testFolder = new BFolder();
        station.add("test", testFolder);

        try
        {
            // Even though "a" comes before "equip" alphabetically, the name
            // "equip" is given priority

            // * station
            //   * test
            //     * equip1 [BHEquip]
            //     * equip [BHEquip]
            //     * point1
            //     * point2
            //     * subPoints
            //       * point3
            //       * point4
            BHEquip equip1 = new BHEquip();
            BHEquip equip = new BHEquip();
            BFolder subPoints = new BFolder();
            BNumericPoint point1 = new BNumericPoint();
            BNumericPoint point2 = new BNumericPoint();
            BNumericPoint point3 = new BNumericPoint();
            BNumericPoint point4 = new BNumericPoint();

            testFolder.add("equip1", equip1);
            testFolder.add("equip", equip);
            testFolder.add("point1", point1);
            testFolder.add("point2", point2);
            testFolder.add("subPoints", subPoints);

            subPoints.add("point3", point3);
            subPoints.add("point4", point4);

            hasEquipRefs(equip,
                point1,
                point2,
                point3,
                point4);
            hasNoEquipRefs(equip1);
        }
        finally
        {
            station.remove(testFolder);
        }
    }

    public void equipRefAddedToBHEquipNamedA()
    {
        BStation station = stationHandler.getStation();
        BFolder testFolder = new BFolder();
        station.add("test", testFolder);

        try
        {
            // "a" is given priority over "b" because neither is named "equip"
            // and "a" comes before "b" alphabetically

            // * station
            //   * test
            //     * equip2
            //     * equip1
            //     * point1
            //     * point2
            //     * subPoints
            //       * point3
            //       * point4
            BHEquip equip1 = new BHEquip();
            BHEquip equip2 = new BHEquip();
            BNumericPoint point1 = new BNumericPoint();
            BNumericPoint point2 = new BNumericPoint();
            BNumericPoint point3 = new BNumericPoint();
            BNumericPoint point4 = new BNumericPoint();

            testFolder.add("equip2", equip2);
            testFolder.add("equip1", equip1);
            testFolder.add("point1", point1);
            testFolder.add("point2", point2);

            BFolder subPoints = new BFolder();
            testFolder.add("subPoints", subPoints);
            subPoints.add("point3", point3);
            subPoints.add("point4", point4);

            hasEquipRefs(equip2,
                point1,
                point2,
                point3,
                point4);
            hasNoEquipRefs(equip1);
        }
        finally
        {
            station.remove(testFolder);
        }
    }

    public void equipRefAddedToPointsUnderMultipleBranchesOfEquipTaggedFolder()
    {
        BStation station = stationHandler.getStation();
        BFolder testFolder = new BFolder();
        station.add("test", testFolder);

        try
        {
            // * station
            //   * test
            //     * equip
            //       * point1
            //       * point2
            //       * subPoints1
            //         * point3
            //         * point4
            //         * subPoints2
            //           * point5
            //           * point6
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
            BFolder equip = new BFolder();
            BFolder subPoints1 = new BFolder();
            BFolder subPoints2 = new BFolder();
            BFolder subPoints3 = new BFolder();
            BFolder subPoints4 = new BFolder();
            BFolder subPoints5 = new BFolder();
            BFolder subPoints6 = new BFolder();
            BNumericPoint point1 = new BNumericPoint();
            BNumericPoint point2 = new BNumericPoint();
            BNumericPoint point3 = new BNumericPoint();
            BNumericPoint point4 = new BNumericPoint();
            BNumericPoint point5 = new BNumericPoint();
            BNumericPoint point6 = new BNumericPoint();
            BNumericPoint point7 = new BNumericPoint();
            BNumericPoint point8 = new BNumericPoint();
            BNumericPoint point9 = new BNumericPoint();
            BNumericPoint point10 = new BNumericPoint();
            BNumericPoint point11 = new BNumericPoint();
            BNumericPoint point12 = new BNumericPoint();
            BNumericPoint point13 = new BNumericPoint();
            BNumericPoint point14 = new BNumericPoint();

            testFolder.add("equip", equip);

            equip.add("point1", point1);
            equip.add("point2", point2);
            equip.add("subPoints1", subPoints1);
            equip.add("subPoints4", subPoints4);

            subPoints1.add("point3", point3);
            subPoints1.add("point4", point4);
            subPoints1.add("subPoints2", subPoints2);
            subPoints1.add("subPoints3", subPoints3);

            subPoints2.add("point5", point5);
            subPoints2.add("point6", point6);

            subPoints3.add("point7", point7);
            subPoints3.add("point8", point8);

            subPoints4.add("point9", point9);
            subPoints4.add("point10", point10);
            subPoints4.add("subPoints5", subPoints5);
            subPoints4.add("subPoints6", subPoints6);

            subPoints5.add("point11", point11);
            subPoints5.add("point12", point12);

            subPoints6.add("point13", point13);
            subPoints6.add("point14", point14);

            addEquipTag(equip);

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
                point14);
        }
        finally
        {
            station.remove(testFolder);
        }
    }

    public void equipRefAddedToPointsUnderMultipleBranchesOfBHEquip()
    {
        BStation station = stationHandler.getStation();
        BFolder testFolder = new BFolder();
        station.add("test", testFolder);

        try
        {
            // * station
            //   * test
            //     * equip
            //     * point1
            //     * point2
            //     * subPoints1
            //       * point3
            //       * point4
            //       * subPoints2
            //         * point5
            //         * point6
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
            BHEquip equip = new BHEquip();
            BFolder subPoints1 = new BFolder();
            BFolder subPoints2 = new BFolder();
            BFolder subPoints3 = new BFolder();
            BFolder subPoints4 = new BFolder();
            BFolder subPoints5 = new BFolder();
            BFolder subPoints6 = new BFolder();
            BNumericPoint point1 = new BNumericPoint();
            BNumericPoint point2 = new BNumericPoint();
            BNumericPoint point3 = new BNumericPoint();
            BNumericPoint point4 = new BNumericPoint();
            BNumericPoint point5 = new BNumericPoint();
            BNumericPoint point6 = new BNumericPoint();
            BNumericPoint point7 = new BNumericPoint();
            BNumericPoint point8 = new BNumericPoint();
            BNumericPoint point9 = new BNumericPoint();
            BNumericPoint point10 = new BNumericPoint();
            BNumericPoint point11 = new BNumericPoint();
            BNumericPoint point12 = new BNumericPoint();
            BNumericPoint point13 = new BNumericPoint();
            BNumericPoint point14 = new BNumericPoint();

            testFolder.add("equip", equip);
            testFolder.add("point1", point1);
            testFolder.add("point2", point2);
            testFolder.add("subPoints1", subPoints1);
            testFolder.add("subPoints4", subPoints4);

            subPoints1.add("point3", point3);
            subPoints1.add("point4", point4);
            subPoints1.add("subPoints2", subPoints2);
            subPoints1.add("subPoints3", subPoints3);

            subPoints2.add("point5", point5);
            subPoints2.add("point6", point6);

            subPoints3.add("point7", point7);
            subPoints3.add("point8", point8);

            subPoints4.add("point9", point9);
            subPoints4.add("point10", point10);
            subPoints4.add("subPoints5", subPoints5);
            subPoints4.add("subPoints6", subPoints6);

            subPoints5.add("point11", point11);
            subPoints5.add("point12", point12);

            subPoints6.add("point13", point13);
            subPoints6.add("point14", point14);

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
                point14);
        }
        finally
        {
            station.remove(testFolder);
        }
    }

    public void combinationEquipTaggedComponentsAndBHEquip1()
    {
        BStation station = stationHandler.getStation();
        BFolder testFolder = new BFolder();
        station.add("test", testFolder);

        try
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
            BFolder equipFolder1 = new BFolder();
            BFolder equipFolder2 = new BFolder();
            BFolder subPoints1 = new BFolder();
            BFolder subPoints2 = new BFolder();
            BHEquip equip = new BHEquip();
            BNumericPoint point1 = new BNumericPoint();
            BNumericPoint point2 = new BNumericPoint();
            BNumericPoint point3 = new BNumericPoint();
            BNumericPoint point4 = new BNumericPoint();
            BNumericPoint point5 = new BNumericPoint();
            BNumericPoint point6 = new BNumericPoint();
            BNumericPoint point7 = new BNumericPoint();
            BNumericPoint point8 = new BNumericPoint();

            testFolder.add("equipFolder1", equipFolder1);

            equipFolder1.add("equip", equip);
            equipFolder1.add("point1", point1);
            equipFolder1.add("point2", point2);
            equipFolder1.add("subPoints1", subPoints1);

            subPoints1.add("point3", point3);
            subPoints1.add("point4", point4);
            subPoints1.add("equipFolder2", equipFolder2);

            equipFolder2.add("point5", point5);
            equipFolder2.add("point6", point6);
            equipFolder2.add("subPoints2", subPoints2);

            subPoints2.add("point7", point7);
            subPoints2.add("point8", point8);

            addEquipTag(equipFolder1);
            addEquipTag(equipFolder2);

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
        finally
        {
            station.remove(testFolder);
        }
    }

    public void combinationEquipTaggedComponentsAndBHEquip2()
    {
        BStation station = stationHandler.getStation();
        BFolder testFolder = new BFolder();
        station.add("test", testFolder);

        try
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
            BFolder equipFolder1 = new BFolder();
            BFolder equipFolder2 = new BFolder();
            BFolder subPoints1 = new BFolder();
            BFolder subPoints2 = new BFolder();
            BHEquip equip = new BHEquip();
            BNumericPoint point1 = new BNumericPoint();
            BNumericPoint point2 = new BNumericPoint();
            BNumericPoint point3 = new BNumericPoint();
            BNumericPoint point4 = new BNumericPoint();
            BNumericPoint point5 = new BNumericPoint();
            BNumericPoint point6 = new BNumericPoint();
            BNumericPoint point7 = new BNumericPoint();
            BNumericPoint point8 = new BNumericPoint();

            testFolder.add("equipFolder1", equipFolder1);

            equipFolder1.add("point1", point1);
            equipFolder1.add("point2", point2);
            equipFolder1.add("subPoints1", subPoints1);

            subPoints1.add("point3", point3);
            subPoints1.add("point4", point4);
            subPoints1.add("equipFolder2", equipFolder2);

            equipFolder2.add("equip", equip);
            equipFolder2.add("point5", point5);
            equipFolder2.add("point6", point6);
            equipFolder2.add("subPoints2", subPoints2);

            subPoints2.add("point7", point7);
            subPoints2.add("point8", point8);

            addEquipTag(equipFolder1);
            addEquipTag(equipFolder2);

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
        finally
        {
            station.remove(testFolder);
        }
    }

    public void addedToDescendantsOfBHEquip()
    {
        BStation station = stationHandler.getStation();
        BFolder testFolder = new BFolder();
        station.add("test", testFolder);

        try
        {
            // * station
            //   * test
            //     * equipFolder1
            //       * equip1Point1
            //       * equip1Point2
            //       * equip [BHEquip]
            //         * equip1Point3
            //         * equip1Point4
            //         * subPoints2
            //           * equip1Point5
            //           * equip1Point6
            //           * equipFolder2
            //             * equip [BHEquip]
            //             * equip2Point1
            //             * equip2Point2
            //           * equipFolder3 [hs:equip]
            //             * equip3Point1
            //             * equip3Point2
            //           * subPoints3
            //             * equip1Point7
            //             * equip1Point8
            //       * subPoints1
            //         * equip1Point9
            //         * equip1Point10
            //       * equipFolder4
            //         * equip [BHEquip]
            //         * equip4Point1
            //         * equip4Point2
            //       * equipFolder5 [hs:equip]
            //         * equip5Point1
            //         * equip5Point2
            BFolder equipFolder1 = new BFolder();
            BFolder equipFolder2 = new BFolder();
            BFolder equipFolder3 = new BFolder();
            BFolder equipFolder4 = new BFolder();
            BFolder equipFolder5 = new BFolder();
            BFolder subPoints1 = new BFolder();
            BFolder subPoints2 = new BFolder();
            BFolder subPoints3 = new BFolder();
            BHEquip equip1 = new BHEquip();
            BHEquip equip2 = new BHEquip();
            BHEquip equip4 = new BHEquip();
            BNumericPoint equip1Point1 = new BNumericPoint();
            BNumericPoint equip1Point2 = new BNumericPoint();
            BNumericPoint equip1Point3 = new BNumericPoint();
            BNumericPoint equip1Point4 = new BNumericPoint();
            BNumericPoint equip1Point5 = new BNumericPoint();
            BNumericPoint equip1Point6 = new BNumericPoint();
            BNumericPoint equip1Point7 = new BNumericPoint();
            BNumericPoint equip1Point8 = new BNumericPoint();
            BNumericPoint equip1Point9 = new BNumericPoint();
            BNumericPoint equip1Point10 = new BNumericPoint();
            BNumericPoint equip2Point1 = new BNumericPoint();
            BNumericPoint equip2Point2 = new BNumericPoint();
            BNumericPoint equip3Point1 = new BNumericPoint();
            BNumericPoint equip3Point2 = new BNumericPoint();
            BNumericPoint equip4Point1 = new BNumericPoint();
            BNumericPoint equip4Point2 = new BNumericPoint();
            BNumericPoint equip5Point1 = new BNumericPoint();
            BNumericPoint equip5Point2 = new BNumericPoint();

            testFolder.add("equipFolder1", equipFolder1);

            equipFolder1.add("equip1Point1", equip1Point1);
            equipFolder1.add("equip1Point2", equip1Point2);
            equipFolder1.add("equip", equip1);
            equipFolder1.add("subPoints1", subPoints1);
            equipFolder1.add("equipFolder4", equipFolder4);
            equipFolder1.add("equipFolder5", equipFolder5);

            equipFolder2.add("equip", equip2);
            equipFolder2.add("equip2Point1", equip2Point1);
            equipFolder2.add("equip2Point2", equip2Point2);

            addEquipTag(equipFolder3);
            equipFolder3.add("equip3Point1", equip3Point1);
            equipFolder3.add("equip3Point2", equip3Point2);

            equipFolder4.add("equip", equip4);
            equipFolder4.add("equip4Point1", equip4Point1);
            equipFolder4.add("equip4Point2", equip4Point2);

            addEquipTag(equipFolder5);
            equipFolder5.add("equip5Point1", equip5Point1);
            equipFolder5.add("equip5Point2", equip5Point2);

            equip1.add("equip1Point3", equip1Point3);
            equip1.add("equip1Point4", equip1Point4);
            equip1.add("subPoints2", subPoints2);

            subPoints1.add("equip1Point9", equip1Point9);
            subPoints1.add("equip1Point10", equip1Point10);

            subPoints2.add("equip1Point5", equip1Point5);
            subPoints2.add("equip1Point6", equip1Point6);
            subPoints2.add("equipFolder2", equipFolder2);
            subPoints2.add("equipFolder3", equipFolder3);
            subPoints2.add("subPoints3", subPoints3);

            subPoints3.add("equip1Point7", equip1Point7);
            subPoints3.add("equip1Point8", equip1Point8);

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
                equip1Point10);
            hasEquipRefs(equip2,
                equip2Point1,
                equip2Point2);
            hasEquipRefs(equipFolder3,
                equip3Point1,
                equip3Point2);
            hasEquipRefs(equip4,
                equip4Point1,
                equip4Point2);
            hasEquipRefs(equipFolder5,
                equip5Point1,
                equip5Point2);
        }
        finally
        {
            station.remove(testFolder);
        }
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

    private static void hasNoEquipRefs(BComponent... components)
    {
        for (BComponent component : components)
        {
            assertTrue(component.relations().getAll(ID_EQUIP_REF).isEmpty(),
              "Component " + component.getSlotPath() + " incorrectly has equipRef relation");
        }
    }

    private static void hasEquipRefs(BComponent equip, BControlPoint... points)
    {
        hasRefsOut(ID_EQUIP_REF, equip, points);
        hasRefsIn(ID_EQUIP_REF, equip, points);
    }

    private static void hasSiteRefs(BComponent site, BComponent... sources)
    {
        hasRefsOut(ID_SITE_REF, site, sources);
        hasRefsIn(ID_SITE_REF, site, sources);
    }

    private static void hasRefsOut(Id id, BComponent target, BComponent... sources)
    {
        for (BComponent source : sources)
        {
            Collection<Relation> relations = source.relations().getAll(id);
            assertEquals(relations.size(), 1,
                "Number of relations from source " + source.getSlotPath());
            Relation relation = relations.iterator().next();
            assertTrue(relation.isOutbound(),
                "Relation is not outbound on source " + source.getSlotPath());
            assertEquals(relation.getEndpoint(), target,
                "Endpoint is " + ((BComponent) relation.getEndpoint()).getSlotPath() +
                " instead of " + target.getSlotPath() +
                " on source " + source.getSlotPath());
        }
    }

    private static void hasRefsIn(Id id, BComponent target, BComponent... sources)
    {
        Collection<Relation> relations = target.relations().getAll(id);
        assertEquals(relations.size(), sources.length,
            "Number of relations to target " + target.getSlotPath());
        allRelationsAreInbound(target, relations);

        for (BComponent source : sources)
        {
            boolean foundSource = false;
            for (Relation relation : relations)
            {
                if (relation.getEndpoint().equals(source))
                {
                    foundSource = true;
                    break;
                }
            }

            assertTrue(foundSource,
                "No relation from source " + source.getSlotPath() +
                " to target " + target.getSlotPath());
        }
    }

    private static void allRelationsAreInbound(BComponent target, Collection<Relation> relations)
    {
        for (Relation relation : relations)
        {
            assertTrue(relation.isInbound(),
                "Relation is not inbound from " + target.getSlotPath() +
                " to endpoint " + ((BComponent) relation.getEndpoint()).getSlotPath());
        }
    }

    private static void addEquipTag(BComponent component)
    {
        component.tags().set(ID_EQUIP, BMarker.MARKER);
    }

    private static void addSiteTag(BComponent component)
    {
        component.tags().set(ID_SITE, BMarker.MARKER);
    }

    private static void removeEquipTag(BComponent component)
    {
        component.tags().remove(ID_EQUIP, BMarker.MARKER);
    }

    private static void addEquipRefRelation(BControlPoint point, BComponent equip)
    {
        point.relations().add(new BRelation(ID_EQUIP_REF, equip));
    }

    private static void removeEquipRefRelation(BControlPoint point, BComponent equip)
    {
        point.relations().remove(ID_EQUIP_REF, equip);
    }

    private static void addSiteRefRelation(BComponent source, BComponent site)
    {
        source.relations().add(new BRelation(ID_SITE_REF, site));
    }

    private final BComponent directEquip = new BComponent();
    private final BFolder impliedEquip1 = new BFolder();
    private final BFolder impliedEquip2 = new BFolder();
    private final BFolder impliedEquip3 = new BFolder();
    private final BFolder impliedEquip4 = new BFolder();
    private final BFolder impliedEquip5 = new BFolder();
    private final BNumericPoint impliedEquip1Point = new BNumericPoint();
    private final BNumericPoint impliedEquip2Point = new BNumericPoint();
    private final BNumericPoint impliedEquip3Point = new BNumericPoint();
    private final BNumericPoint impliedEquip4Point = new BNumericPoint();
    private final BNumericPoint impliedEquip5Point = new BNumericPoint();
}
