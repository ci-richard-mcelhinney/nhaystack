//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Feb 2013  Mike Jarmy  Creation
//

package nhaystack.ui;

import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
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

        dropDown.getList().addItem(LEX.getText("none"));

        // populate the list by fetching all the equips from the server
        HGrid grid = ((BHGrid) group.service().invoke(
            BNHaystackService.fetchEquips, null)).getGrid();

        for (int i = 0; i < grid.numRows(); i++)
        {
            String slotPath = grid.row(i).getStr("axSlotPath");
            dropDown.getList().addItem("station:|" + slotPath);
        }

        setContent(dropDown);

        linkTo(dropDown, BDropDown.valueModified, setModified);  
    }

    protected void doSetReadonly(boolean readonly)
    {
        dropDown.setEnabled(!readonly);
    }

    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        BOrd ord = (BOrd) value;

        if (ord.equals(BOrd.DEFAULT))
            dropDown.setSelectedIndex(0);
        else
            dropDown.setSelectedItem(ord.toString());
    }

    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        String str = (String) dropDown.getSelectedItem();

        return (str.equals(LEX.getText("none"))) ?
            BOrd.DEFAULT : BOrd.make(str);
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private BListDropDown dropDown;
}
