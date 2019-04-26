//
// Copyright 2018 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Apr 2018  Eric Anderson  Creation
//

package nhaystack.ntest.helper;

import static nhaystack.util.NHaystackConst.EQUIP_REF;
import static nhaystack.util.NHaystackConst.SITE_REF;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.baja.sys.BComponent;
import javax.baja.sys.BValue;
import javax.baja.tag.Id;
import javax.baja.util.BFolder;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HRef;
import org.projecthaystack.HRow;
import org.projecthaystack.HStr;
import org.projecthaystack.HUri;

public final class NHaystackTestUtil
{
    // private constructor
    private NHaystackTestUtil()
    {
    }

    public static final String HAYSTACK_SLOT_NAME = "haystack";
    public static final String EQUIP_SLOT_NAME = "equip";

    public static final String CUR_VAL_TAG_NAME = "curVal";
    public static final String WRITE_LEVEL_TAG_NAME = "writeLevel";
    public static final String WRITE_VAL_TAG_NAME = "writeVal";
    public static final String NAV_ID_TAG_NAME = "navId";

    public static final Id DISCHARGE_ID = Id.newId("hs:discharge");
    public static final Id SENSOR_ID = Id.newId("hs:sensor");
    public static final Id AIR_ID = Id.newId("hs:air");
    public static final Id TEMP_ID = Id.newId("hs:temp");

    public static <T extends BValue> T addChild(String name, T child, BComponent parent)
    {
        parent.add(name, child);
        return child;
    }

    public static BFolder addFolder(String name, BComponent parent)
    {
        return addChild(name, new BFolder(), parent);
    }

    public static BHSite addSite(String name, BComponent parent)
    {
        return addChild(name, new BHSite(), parent);
    }

    public static BHEquip addEquip(BComponent parent)
    {
        return addChild("equip", new BHEquip(), parent);
    }

    public static HGrid makeIdGrid(String id)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add("id", HUri.make(id));
        return HGridBuilder.dictsToGrid(new HDict[] { hd.toDict() });
    }

    public static HGrid makeNavGrid(String navId)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add(NAV_ID_TAG_NAME, HStr.make(navId));
        return HGridBuilder.dictsToGrid(new HDict[] { hd.toDict() });
    }

    public static void purgeDirectory(File dir) throws IOException
    {
        for (File file : dir.listFiles())
        {
            if (file.isDirectory())
            {
                purgeDirectory(file);
            }

            Files.delete(file.toPath());
        }
    }

    public static void assertRowIds(HGrid grid, String... ids)
    {
        assertEquals(grid.numRows(), ids.length);
        for (int i = 0; i < ids.length; ++i)
        {
            assertEquals(grid.row(i).id(), HRef.make(ids[i]));
        }
    }

    public static void assertRowNavIds(HGrid grid, String... navIds)
    {
        assertRowValues(grid, "navId", navIds);
    }

    public static void assertRowDis(HGrid grid, String... dis)
    {
        assertRowValues(grid, "dis", dis);
    }

    private static void assertRowValues(HGrid grid, String colName, String... values)
    {
        assertEquals(grid.numRows(), values.length);
        for (int i = 0; i < values.length; ++i)
        {
            assertEquals(grid.row(i).get(colName), HStr.make(values[i]));
        }
    }

    public static void rowHasSiteRef(HRow row, String expected)
    {
        assertEquals(row.get(SITE_REF), HRef.make(expected));
    }

    public static void rowHasEquipRef(HRow row, String expected)
    {
        assertEquals(row.get(EQUIP_REF), HRef.make(expected));
    }
}
