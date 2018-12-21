//
// Copyright 2018 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Apr 2018  Eric Anderson  Creation
//

package nhaystack.ntest.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.baja.sys.BComponent;
import javax.baja.sys.BValue;
import javax.baja.tag.Id;
import javax.baja.util.BFolder;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;

public final class NHaystackTestUtil
{
    // private constructor
    private NHaystackTestUtil()
    {
    }

    public static final String HAYSTACK_SLOT_NAME = "haystack";
    public static final String EQUIP_SLOT_NAME = "equip";

    public static final String SITE_REF_TAG_NAME = "siteRef";
    public static final String EQUIP_REF_TAG_NAME = "equipRef";
    public static final String CUR_VAL_TAG_NAME = "curVal";
    public static final String WRITE_LEVEL_TAG_NAME = "writeLevel";
    public static final String WRITE_VAL_TAG_NAME = "writeVal";
    public static final String NAV_ID_TAG_NAME = "navId";

    public static final Id SITE_REF_ID = Id.newId("hs:siteRef");
    public static final Id EQUIP_REF_ID = Id.newId("hs:equipRef");
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
}
