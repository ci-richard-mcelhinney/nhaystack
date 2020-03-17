//
// Copyright 2019 Project Haystack All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//    16 Mar 2020    Richard McELhinney  Creation
//

package nhaystack.e2e;

import com.tridium.history.log.BLogHistoryService;
import com.tridium.kitControl.math.BSine;
import com.tridium.kitControl.util.BSineWave;
import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.server.BNHaystackRebuildCacheJob;
import nhaystack.site.BHSite;
import org.projecthaystack.*;
import org.projecthaystack.client.CallException;
import org.projecthaystack.client.HClient;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.baja.control.*;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;
import javax.baja.tag.Id;
import javax.baja.units.BUnit;
import javax.baja.util.BFolder;

import java.util.Iterator;

import static nhaystack.util.NHaystackConst.ID_EQUIP_REF;
import static nhaystack.util.NHaystackConst.ID_SITE_REF;
import static org.testng.Assert.fail;

@NiagaraType
@Test
public class BSimpleClientTest extends BNHaystackStationTestBase
{

    @Override
    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BSimpleClientTest.class);

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort) throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        this.station = station;
        station.getServices().add("LogHistoryService", new BLogHistoryService());
    }

    @BeforeTest
    @Override
    public void setupStation() throws Exception
    {
        super.setupStation();
        setupCompTree();
    }

    private void setupCompTree()
    {
        BFolder home = new BFolder();
        station.add("home", home);
        BHSite winterfell = new BHSite();
        BHSite richmond = new BHSite();
        BFolder equip1 = new BFolder();
        equip1.tags().set(Id.newId("hs", "equip"), BMarker.DEFAULT);

        BFolder equip2 = new BFolder();
        equip2.tags().set(Id.newId("hs", "equip"), BMarker.DEFAULT);

        BFolder ahu = new BFolder();
        ahu.tags().set(Id.newId("hs", "equip"), BMarker.DEFAULT);

        home.add("Winterfell", winterfell);
        home.add("Richmond", richmond);
        home.add("AHU2", ahu);
        winterfell.add("equip1", equip1);
        winterfell.add("equip2", equip2);

        // equip1 points
        BBooleanWritable bw = new BBooleanWritable();
        BEnumWritable ew = new BEnumWritable();
        BStringWritable sw = new BStringWritable();
        BSineWave sine = new BSineWave();
        equip1.add("BooleanWritable", bw);
        equip1.add("EnumWritable", ew);
        equip1.add("StringWritable", sw);
        equip1.add("SineWave", sine);

        BSineWave sine2 = new BSineWave();
        BSineWave sine4 = new BSineWave();
        station.add("SineWave2", sine2);
        station.add("SineWave4", sine4);

        // equip2 points
        BSineWave e2sine2 = new BSineWave();
        BSineWave e2sine3 = new BSineWave();
        equip2.add("SineWave2", e2sine2);
        equip2.add("SineWave3", e2sine3);

        // ahu points
        BNumericWritable nm = new BNumericWritable();
        ahu.add("NumericWritable", nm);

        // lonely point
        BSineWave sine5 = new BSineWave();
        station.add("SineWave5", sine5);
        sine5.setFacets(BFacets.makeNumeric(BUnit.getUnit("fahrenheit"), 2));
        sine5.tags().set(Id.newId("foo"), BMarker.DEFAULT);
        sine5.tags().set(Id.newId("ph", "bar"), BMarker.DEFAULT);
        sine5.tags().set(Id.newId("foo"), BMarker.DEFAULT);

        // relations
        addSiteRefRelation(equip1, winterfell);
        addSiteRefRelation(equip2, winterfell);
        addSiteRefRelation(ahu, richmond);
        addSiteRefRelation(bw, winterfell);
        addSiteRefRelation(ew, winterfell);
        addSiteRefRelation(sw, winterfell);
        addSiteRefRelation(sine, winterfell);
        addSiteRefRelation(sine2, winterfell);
        addSiteRefRelation(sine4, winterfell);
        addSiteRefRelation(e2sine2, winterfell);
        addSiteRefRelation(e2sine3, winterfell);

        addEquipRefRelation(bw, equip1);
        addEquipRefRelation(ew, equip1);
        addEquipRefRelation(sw, equip1);
        addEquipRefRelation(sine, equip1);
        addEquipRefRelation(sine2, equip1);
        addEquipRefRelation(sine4, equip1);

        addEquipRefRelation(e2sine2, equip2);
        addEquipRefRelation(e2sine3, equip2);

        addEquipRefRelation(nm, ahu);

        rebuildCache();
    }

    @Test
    public void verifyAuth()
    {
        // get bad credentials
        try
        {  HClient.open(LOCAL_URI, INVALID_USER, INVALID_PASS).about(); fail(); }
        catch (Exception e)
        {
            Assert.assertTrue(true);
        }

        try
        {  HClient.open(LOCAL_URI, super.getSuperUsername(), INVALID_PASS).about(); fail(); }
        catch (CallException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            this.client = openClient(false);
            client.about();
        }
        catch(Exception e)
        {  e.printStackTrace(); fail(); }
    }

    @Test
    public void verifyAbout() throws Exception
    {
        // non-secure
        this.client = openClient(false);
        HDict r = client.about();
        Assert.assertEquals(r.getStr("haystackVersion"), "2.0");
        Assert.assertEquals(r.getStr("productName"), "Niagara 4");
        Assert.assertEquals(r.getStr("productVersion"), "4.8.0.110");
        Assert.assertEquals(r.getStr("moduleVersion"), "3.0.1");
    }

    @Test
    public void verifyOps() throws Exception
    {
        this.client = openClient(false);
        HGrid g = client.ops();

        // verify required columns
        Assert.assertTrue(g.col("name") != null);
        Assert.assertTrue(g.col("summary") != null);

        // verify required ops
        verifyGridContains(g, "name", HStr.make("about"));
        verifyGridContains(g, "name", HStr.make("ops"));
        verifyGridContains(g, "name", HStr.make("formats"));
        verifyGridContains(g, "name", HStr.make("read"));
        verifyGridContains(g, "name", HStr.make("nav"));
        verifyGridContains(g, "name", HStr.make("watchSub"));
        verifyGridContains(g, "name", HStr.make("watchUnsub"));
        verifyGridContains(g, "name", HStr.make("watchPoll"));
        verifyGridContains(g, "name", HStr.make("pointWrite"));
        verifyGridContains(g, "name", HStr.make("hisRead"));
        verifyGridContains(g, "name", HStr.make("hisWrite"));
        verifyGridContains(g, "name", HStr.make("invokeAction"));
    }

    @Test
    public void verifyFormats() throws Exception
    {
        this.client = openClient(false);
        HGrid g = client.formats();

        // verify required columns
        Assert.assertTrue(g.col("mime") != null);
        Assert.assertTrue(g.col("read") != null);
        Assert.assertTrue(g.col("write") != null);

        // verify required ops
        verifyGridContains(g, "mime", HStr.make("text/plain"));
        verifyGridContains(g, "mime", HStr.make("text/zinc"));
    }

    @Test
    public void verifyRead() throws Exception
    {
        this.client = openClient(false);
        HDict dict;
        HGrid grid = client.readAll("id");

        // do checks
        Assert.assertEquals(grid.numRows(), 16);
        Assert.assertEquals(grid.row( 0).id(), HRef.make("S.Winterfell"));
        Assert.assertEquals(grid.row( 1).id(), HRef.make("S.Winterfell.equip1"));
        Assert.assertEquals(grid.row( 2).id(), HRef.make("S.Winterfell.equip1.BooleanWritable"));
        Assert.assertEquals(grid.row( 3).id(), HRef.make("S.Winterfell.equip1.EnumWritable"));
        Assert.assertEquals(grid.row( 4).id(), HRef.make("S.Winterfell.equip1.StringWritable"));
        Assert.assertEquals(grid.row( 5).id(), HRef.make("S.Winterfell.equip1.SineWave"));
        Assert.assertEquals(grid.row( 6).id(), HRef.make("S.Winterfell.equip2"));
        Assert.assertEquals(grid.row( 7).id(), HRef.make("S.Winterfell.equip2.SineWave2"));
        Assert.assertEquals(grid.row( 8).id(), HRef.make("S.Winterfell.equip2.SineWave3"));
        Assert.assertEquals(grid.row( 9).id(), HRef.make("S.Richmond"));
        Assert.assertEquals(grid.row(10).id(), HRef.make("S.Richmond.AHU2"));
        Assert.assertEquals(grid.row(11).id(), HRef.make("S.Richmond.AHU2.NumericWritable"));
        Assert.assertEquals(grid.row(12).id(), HRef.make("S.Winterfell.equip1.SineWave2"));
        Assert.assertEquals(grid.row(13).id(), HRef.make("S.Winterfell.equip1.SineWave4"));

        Assert.assertEquals(grid.row(1).get("siteRef"), HRef.make("S.Winterfell"));
        Assert.assertEquals(grid.row(2).get("siteRef"), HRef.make("S.Winterfell"));
        Assert.assertEquals(grid.row(3).get("siteRef"), HRef.make("S.Winterfell"));
        Assert.assertEquals(grid.row(4).get("siteRef"), HRef.make("S.Winterfell"));
        Assert.assertEquals(grid.row(5).get("siteRef"), HRef.make("S.Winterfell"));
        Assert.assertEquals(grid.row(6).get("siteRef"), HRef.make("S.Winterfell"));
        Assert.assertEquals(grid.row(7).get("siteRef"), HRef.make("S.Winterfell"));
        Assert.assertEquals(grid.row(8).get("siteRef"), HRef.make("S.Winterfell"));

        Assert.assertEquals(grid.row( 2).get("equipRef"), HRef.make("S.Winterfell.equip1"));
        Assert.assertEquals(grid.row( 3).get("equipRef"), HRef.make("S.Winterfell.equip1"));
        Assert.assertEquals(grid.row( 4).get("equipRef"), HRef.make("S.Winterfell.equip1"));
        Assert.assertEquals(grid.row( 5).get("equipRef"), HRef.make("S.Winterfell.equip1"));
        Assert.assertEquals(grid.row( 7).get("equipRef"), HRef.make("S.Winterfell.equip2"));
        Assert.assertEquals(grid.row( 8).get("equipRef"), HRef.make("S.Winterfell.equip2"));
        Assert.assertEquals(grid.row(12).get("equipRef"), HRef.make("S.Winterfell.equip1"));

        dict = client.readById(HRef.make("C.SineWave5"));
        Assert.assertEquals(dict.get("axType"), HStr.make("kitControl:SineWave"));
//        Assert.assertTrue(dict.has("foo"));
//        Assert.assertTrue(dict.has("bar"));
        Assert.assertEquals(dict.get("kind"), HStr.make("Number"));
//        Assert.assertTrue(dict.has("his"));
//        Assert.assertEquals(dict.get("hisInterpolate"), HStr.make("cov"));
        Assert.assertEquals(dict.get("axSlotPath"), HStr.make("slot:/SineWave5"));
        Assert.assertEquals(dict.get("unit"), HStr.make("°F"));
        Assert.assertTrue(dict.has("point"));
        Assert.assertEquals(dict.get("tz"), localTz());
        Assert.assertTrue(dict.has("cur"));
        double curVal = dict.getDouble("curVal");
        Assert.assertEquals(dict.get("curStatus"), HStr.make("ok"));
        Assert.assertTrue(curVal >= 0.0 && curVal <= 100.0);

        Assert.assertEquals(dict.get("dis"), HStr.make("SineWave5"));
        Assert.assertEquals(dict.get("navName"), HStr.make("SineWave5"));

        printBasicGrid(grid);
    }



////////////////////////////////////////////////////////////////
// Utils
////////////////////////////////////////////////////////////////

    void verifyGridContains(HGrid g, String col, HVal val)
    {
        boolean found = false;
        for (int i=0; i<g.numRows(); ++i)
        {
            HVal x = g.row(i).get(col, false);
            if (x != null && x.equals(val)) { found = true; break; }
        }
        if (!found)
        {
            System.out.println("verifyGridContains " + col + "=" + val + " failed!");
            fail();
        }
    }

    private static void addSiteRefRelation(BComponent source, BComponent site)
    {
        source.relations().add(new BRelation(ID_SITE_REF, site));
    }

    private static void addEquipRefRelation(BControlPoint point, BComponent equip)
    {
        point.relations().add(new BRelation(ID_EQUIP_REF, equip));
    }

    protected void rebuildCache()
    {
        try
        {
            new BNHaystackRebuildCacheJob(nhaystackService).run(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Print a full grid
     *
     * @param grid
     */
    void printFullGrid(HGrid grid)
    {
        int row = 0;
        for (Iterator i = grid.iterator(); i.hasNext();)
        {
            HRow r = (HRow) i.next();
            System.out.print(row + ", " + r.toString());
            System.out.println();
            row++;
        }
    }

    /**
     * Convenience to print and format a basic grid nicely for debugging
     *
     * @param grid
     */
    void printBasicGrid(HGrid grid)
    {
        int row = 0;
        for (Iterator i = grid.iterator(); i.hasNext();)
        {
            HRow r = (HRow) i.next();
            System.out.printf("%2s%s", row, ", ");
            System.out.printf("%-40s ", r.id());
            if (r.has("equipRef"))
                System.out.printf("%-30s", r.get("equipRef"));
            else
                System.out.printf("%-30s", "");

            if (r.has("kind"))
                System.out.printf("%-10s", r.get("kind"));

            System.out.println();
            row++;
        }
    }

////////////////////////////////////////////////////////////////
// Utils
////////////////////////////////////////////////////////////////

    HStr localTz() { return HStr.make(HTimeZone.DEFAULT.name); }
}
