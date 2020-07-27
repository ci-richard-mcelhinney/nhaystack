//
// Copyright 2018 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   11 Dec 2018  Andrew Saunders    Creation
//
package nhaystack.ntest;

import static nhaystack.ntest.helper.NHaystackTestUtil.addBooleanTestProxyPoint;
import static nhaystack.ntest.helper.NHaystackTestUtil.addEquipRefRelation;
import static nhaystack.ntest.helper.NHaystackTestUtil.addEquipTag;
import static nhaystack.ntest.helper.NHaystackTestUtil.addFolder;
import static nhaystack.ntest.helper.NHaystackTestUtil.addNumericTestProxyPoint;
import static nhaystack.ntest.helper.NHaystackTestUtil.addSiteRefRelation;
import static nhaystack.ntest.helper.NHaystackTestUtil.addSiteTag;
import static nhaystack.ntest.helper.NHaystackTestUtil.assertRowIds;
import static nhaystack.ntest.helper.NHaystackTestUtil.makeIdGrid;
import static nhaystack.ntest.helper.NHaystackTestUtil.makeNavGrid;
import static nhaystack.ntest.helper.NHaystackTestUtil.rowHasEquipRef;
import static nhaystack.ntest.helper.NHaystackTestUtil.rowHasSiteRef;

import javax.baja.control.BBooleanPoint;
import javax.baja.control.BNumericPoint;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BFolder;

import nhaystack.ntest.helper.BNHaystackStationTestBase;
import org.projecthaystack.HGrid;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@NiagaraType
@Test
@SuppressWarnings("MagicNumber")
public class BHaystackClientTest2 extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.test.BReplaceHaystackSlotStationTest(2979906276)1.0$ @*/
/* Generated Tue Dec 05 13:55:27 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

    @Override
    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHaystackClientTest2.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

/*
 *   This test will test client access to a station that has folders tagged as site and equip. BHSite and BHEquip components
 *   are NOT used.  A siteRef relation will be added to each of the equip tagged folders to their respective site tagged folders.
 *   Some points are added as a child to the equip tagged folders, where they will have an implied equipRef relation.
 *   Some other points are added as a child to a playground folder, An equipRef relation will be added to these points relating
 *   each to the desired equip tagged folder.
 */

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort) throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        // * station
        // * Playground
        //   * siteFolder1 (hs:site)
        //     * equipFolder1 (hs:equip, hs:siteRef->siteFolder1)
        //       * boolPoint1
        //       * numericPoint1
        //   * siteFolder2 (hs:site)
        //     * equipFolder2 (hs:equip, hs:siteRef->siteFolder2)
        //       * boolPoint2
        //       * numericPoint2
        //   * playBoolPoint1 (hs:equipRef->equipFolder1)
        //   * playBoolPoint2 (hs:equipRef->equipFolder2)
        //   * playNumPoint1 (hs:equipRef->equipFolder1)
        //   * playNumPoint2 (hs:equipRef->equipFolder2)
        BFolder playground = addFolder("Playground", station);

        siteFolder1 = addFolder("siteFolder1", playground);
        siteFolder2 = addFolder("siteFolder2", playground);

        addSiteTag(siteFolder1);
        addSiteTag(siteFolder2);

        equipFolder1 = addFolder("equipFolder1", siteFolder1);
        equipFolder2 = addFolder("equipFolder2", siteFolder2);

        addEquipTag(equipFolder1);
        addEquipTag(equipFolder2);

        addSiteRefRelation(equipFolder1, siteFolder1);
        addSiteRefRelation(equipFolder2, siteFolder2);

        addBooleanTestProxyPoint("boolPoint1", equipFolder1);
        addNumericTestProxyPoint("numericPoint1", equipFolder1);
        addBooleanTestProxyPoint("boolPoint2", equipFolder2);
        addNumericTestProxyPoint("numericPoint2", equipFolder2);

        playBoolPoint1 = addBooleanTestProxyPoint("playBoolPoint1", playground);
        playBoolPoint2 = addBooleanTestProxyPoint("playBoolPoint2", playground);
        playNumPoint1 = addNumericTestProxyPoint("playNumPoint1", playground);
        playNumPoint2 = addNumericTestProxyPoint("playNumPoint2", playground);

        addEquipRefRelation(playBoolPoint1, equipFolder1);
        addEquipRefRelation(playNumPoint1, equipFolder1);
        addEquipRefRelation(playBoolPoint2, equipFolder2);
        addEquipRefRelation(playNumPoint2, equipFolder2);
    }

    @BeforeTest
    @Override
    public void setupStation() throws Exception
    {
        super.setupStation();

        client = openClient(false);
    }

    public void testReadAll() throws InterruptedException
    {
        HGrid grid = client.readAll("id");

        assertRowIds(grid,
            "S.siteFolder1",
            "S.siteFolder1.equipFolder1",
            "S.siteFolder1.equipFolder1.boolPoint1",
            "S.siteFolder1.equipFolder1.numericPoint1",
            "S.siteFolder2",
            "S.siteFolder2.equipFolder2",
            "S.siteFolder2.equipFolder2.boolPoint2",
            "S.siteFolder2.equipFolder2.numericPoint2",
            "S.siteFolder1.equipFolder1.playBoolPoint1",
            "S.siteFolder2.equipFolder2.playBoolPoint2",
            "S.siteFolder1.equipFolder1.playNumPoint1",
            "S.siteFolder2.equipFolder2.playNumPoint2");

        rowHasSiteRef(grid.row(1), "S.siteFolder1");

        rowHasSiteRef(grid.row(2), "S.siteFolder1");
        rowHasEquipRef(grid.row(2), "S.siteFolder1.equipFolder1");

        rowHasSiteRef(grid.row(3), "S.siteFolder1");
        rowHasEquipRef(grid.row(3), "S.siteFolder1.equipFolder1");

        rowHasSiteRef(grid.row(5), "S.siteFolder2");

        rowHasSiteRef(grid.row(6), "S.siteFolder2");
        rowHasEquipRef(grid.row(6), "S.siteFolder2.equipFolder2");

        rowHasSiteRef(grid.row(7), "S.siteFolder2");
        rowHasEquipRef(grid.row(7), "S.siteFolder2.equipFolder2");

        rowHasSiteRef(grid.row(8), "S.siteFolder1");
        rowHasEquipRef(grid.row(8), "S.siteFolder1.equipFolder1");

        rowHasSiteRef(grid.row(9), "S.siteFolder2");
        rowHasEquipRef(grid.row(9), "S.siteFolder2.equipFolder2");

        rowHasSiteRef(grid.row(10), "S.siteFolder1");
        rowHasEquipRef(grid.row(10), "S.siteFolder1.equipFolder1");

        rowHasSiteRef(grid.row(11), "S.siteFolder2");
        rowHasEquipRef(grid.row(11), "S.siteFolder2.equipFolder2");
    }

    public void testNav() throws Exception
    {
        // This should result in the following nav organization
        // * siteFolder1
        //   * equipFolder1
        //     * boolPoint1
        //     * numericPoint1
        //     * playBoolPoint1
        //     * playNumPoint1
        // * siteFolder2
        //   * equipFolder2
        //     * boolPoint2
        //     * numericPoint2
        //     * playBoolPoint2
        //     * playNumPoint2

        HGrid grid = client.call("read", makeIdGrid("sep:/siteFolder1"));
        assertRowIds(grid, "S.siteFolder1");

        grid = client.call("read", makeIdGrid("sep:/siteFolder1/equipFolder1"));
        assertRowIds(grid, "S.siteFolder1.equipFolder1");

        grid = client.call("nav", makeNavGrid("sep:/siteFolder1/equipFolder1"));
        assertRowIds(grid,
            "S.siteFolder1.equipFolder1.boolPoint1",
            "S.siteFolder1.equipFolder1.numericPoint1",
            "S.siteFolder1.equipFolder1.playBoolPoint1",
            "S.siteFolder1.equipFolder1.playNumPoint1");

        grid = client.call("nav", makeNavGrid("sep:/siteFolder2/equipFolder2"));
        assertRowIds(grid,
            "S.siteFolder2.equipFolder2.boolPoint2",
            "S.siteFolder2.equipFolder2.numericPoint2",
            "S.siteFolder2.equipFolder2.playBoolPoint2",
            "S.siteFolder2.equipFolder2.playNumPoint2");
    }
    
    private BComponent siteFolder1 = new BFolder();
    private BComponent siteFolder2 = new BFolder();
    private BComponent equipFolder1 = new BFolder();
    private BComponent equipFolder2 = new BFolder();
    private BBooleanPoint playBoolPoint1 = new BBooleanPoint();
    private BBooleanPoint playBoolPoint2 = new BBooleanPoint();
    private BNumericPoint playNumPoint1 = new BNumericPoint();
    private BNumericPoint playNumPoint2 = new BNumericPoint();
}
