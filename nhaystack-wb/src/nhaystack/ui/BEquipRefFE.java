//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Feb 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//

package nhaystack.ui;

import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BDropDown;
import javax.baja.ui.BListDropDown;
import javax.baja.ui.list.BList;
import javax.baja.util.Lexicon;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;
import nhaystack.BHGrid;
import nhaystack.server.BNHaystackService;
import org.projecthaystack.HGrid;

/**
  * BEquipRefFE edits a 'equipRef' BOrd
  */
@NiagaraType
public class BEquipRefFE extends BWbFieldEditor
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BEquipRefFE(2979906276)1.0$ @*/
/* Generated Mon Nov 20 11:08:06 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
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
                    slotPath);
            else
                dropDown.getList().addItem(slotPath);
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
            int n = slotPath.lastIndexOf('/');
            String parentSlotPath = slotPath.substring(0, n);
            
            if (compSlotPath.startsWith(parentSlotPath))
                return slotPath;
        }
        return null;
    }

    @Override
    protected void doSetReadonly(boolean readonly)
    {
        dropDown.setEnabled(!readonly);
    }

    @Override
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
            String slotPath = ord.toString();
            if (slotPath.startsWith("station:|"))
                slotPath = slotPath.substring("station:|".length());

            boolean found = false;
            BList list = dropDown.getList();
            for (int i = 0; i < list.getItemCount(); i++)
            {
                String item = (String) list.getItem(i);
                if (item.endsWith(slotPath))
                {
                    dropDown.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }

            if (!found) throw new IllegalStateException(
                "Cannot find equip matching " + slotPath);
        }
    }

    @Override
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
            return BOrd.make("station:|" + str);
        }
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private BListDropDown dropDown;
}
