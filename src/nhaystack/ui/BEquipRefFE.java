//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Feb 2013  Mike Jarmy  Creation
//

package nhaystack.ui;

import java.util.*;

import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.list.*;
import javax.baja.util.*;
import javax.baja.workbench.fieldeditor.*;

import haystack.*;
import nhaystack.*;
import nhaystack.server.*;

/**
  * BEquipRefFE edits a 'equipRef' BOrd
  */
public class BEquipRefFE extends BWbFieldEditor
{
    /*-
    class BEquipRefFE
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BEquipRefFE(3045417629)1.0$ @*/
/* Generated Sun Feb 10 10:52:19 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BEquipRefFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BEquipRefFE() { }

    public BEquipRefFE(BHDictEditorGroup group)
    {
        this.dropDown = new BListDropDown();

        // fetch all the equips from the server
        HGrid equipsGrid = ((BHGrid) group.service().invoke(
            BNHaystackService.fetchEquips, null)).getGrid();

        // see if we are currently explicitly tagged
        String implicitEquip = findImplicitEquip(group.component(), equipsGrid);

        // if we can't find an implicit candidate, then add a 'NONE' equip
        if (implicitEquip == null)
            dropDown.getList().addItem(LEX.getText("none"));

        // add the equips
        for (int i = 0; i < equipsGrid.numRows(); i++)
        {
            String slotPath = equipsGrid.row(i).getStr("axSlotPath");

            if (implicitEquip != null && slotPath.equals(implicitEquip))
                dropDown.getList().addItem(
                    LEX.getText("autoFind") + ": " +
                    "station:|" + slotPath);
            else
                dropDown.getList().addItem(
                    LEX.getText("explicit") + ": " +
                    "station:|" + slotPath);
        }

        setContent(dropDown);

        linkTo(dropDown, BDropDown.valueModified, setModified);  
    }

    private static String findImplicitEquip(BComponent comp, HGrid equipsGrid)
    {
        String compSlotPath = comp.getSlotPath().toString();
        for (int i = 0; i < equipsGrid.numRows(); i++)
        {
            String slotPath = equipsGrid.row(i).getStr("axSlotPath");
            int n = slotPath.lastIndexOf("/");
            String parentSlotPath = slotPath.substring(0, n);
            
            if (compSlotPath.startsWith(parentSlotPath))
                return slotPath;
        }
        return null;
    }

    protected void doSetReadonly(boolean readonly)
    {
        dropDown.setEnabled(!readonly);
    }

    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        BOrd ord = (BOrd) value;

        // this must always be "none"
        if (ord.equals(BOrd.DEFAULT))
        {
            dropDown.setSelectedIndex(0);
        }
        else
        {
            String ordStr = ord.toString();

            boolean found = false;
            BList list = dropDown.getList();
            for (int i = 0; i < list.getItemCount(); i++)
            {
                String item = (String) list.getItem(i);
                if (item.endsWith(ordStr))
                {
                    dropDown.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }

            if (!found) throw new IllegalStateException(
                "Cannot find equip matching " + ordStr);
        }
    }

    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        String str = (String) dropDown.getSelectedItem();

        if (str.equals(LEX.getText("none")))
        {
            return BOrd.DEFAULT;
        }
        else if (str.startsWith(LEX.getText("autoFind")))
        {
            return BOrd.DEFAULT;
        }
        else
        {
            String prefix = LEX.getText("explicit") + ": ";
            str = str.substring(prefix.length());
            return BOrd.make(str);
        }
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private BListDropDown dropDown;
}
