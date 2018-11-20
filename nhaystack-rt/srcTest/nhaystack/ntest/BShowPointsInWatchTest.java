//
// Copyright (c) 2018. Tridium, Inc. All rights reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   16 May 2018  Eric Anderson  Creation
//

package nhaystack.ntest;

import static nhaystack.ntest.helper.NHaystackTestUtil.SITE_REF_ID;
import static nhaystack.ntest.helper.NHaystackTestUtil.addEquip;
import static nhaystack.ntest.helper.NHaystackTestUtil.addFolder;
import static nhaystack.ntest.helper.NHaystackTestUtil.addSite;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import javax.baja.control.BBooleanWritable;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BFolder;

import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HStr;
import org.projecthaystack.HWatch;
import org.testng.annotations.Test;

@NiagaraType
@Test(groups = {"ci", "nhaystack"})
public class BShowPointsInWatchTest extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.test.BShowPointsInWatchTest(2979906276)1.0$ @*/
/* Generated Wed May 16 09:58:56 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BShowPointsInWatchTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    private final BBooleanWritable bw1 = new BBooleanWritable();
    private final BBooleanWritable bw2 = new BBooleanWritable();

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort) throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        BHSite site = addSite("site", station);

        BFolder equip1Folder = addFolder("equip1", station);
        BHEquip equip = addEquip(equip1Folder);
        equip.relations().add(SITE_REF_ID, site);

        equip1Folder.add("BW1", bw1);
        equip1Folder.add("BW2", bw2);
    }

    public void testAbout()
    {
        HDict r = hClient.about();
        assertEquals(r.getStr("haystackVersion"), "2.0");
    }

    public void testShowPointsInWatch()
    {
        // Add a watch
        HWatch watch = nhServer.onWatchOpen("testWatch", HNum.make(1, "hr"));

        try
        {
            // Subscribe points to the watch
            HRef bw1ref = nhServer.getTagManager().makeComponentRef(bw1).getHRef();
            HRef bw2ref = nhServer.getTagManager().makeComponentRef(bw2).getHRef();
            watch.sub(new HRef[] {bw1ref, bw2ref});

            HGrid response = callShowPointsInWatch(watch);

            List<HRef> actualHRefs = collectHRefs(response);
            assertEquals(actualHRefs.size(), 2, "Number of rows in the response");
            assertTrue(actualHRefs.contains(bw1ref), "BW1 href");
            assertTrue(actualHRefs.contains(bw2ref), "BW2 href");
        }
        finally
        {
            watch.close();
        }
    }

    private HGrid callShowPointsInWatch(HWatch watch)
    {
        HDictBuilder req = new HDictBuilder()
            .add(FUNCTION_OP_ARG_NAME, HStr.make("showPointsInWatch"))
            .add("watchId", HRef.make(watch.id()));
        return hClient.call(EXTENDED_OP_NAME, HGridBuilder.dictToGrid(req.toDict()));
    }

    private static List<HRef> collectHRefs(HGrid res)
    {
        List<HRef> hRefs = new ArrayList<>(res.numRows());
        for (int i = 0; i < res.numRows(); i++)
        {
            hRefs.add((HRef)res.row(i).get("id"));
        }
        return hRefs;
    }
}
