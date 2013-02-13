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
import javax.baja.ui.enums.*;
import javax.baja.ui.event.*;
import javax.baja.ui.list.*;
import javax.baja.ui.pane.*;
import javax.baja.util.*;
import javax.baja.workbench.*;
import javax.baja.workbench.fieldeditor.*;

import haystack.*;
import nhaystack.*;
import nhaystack.res.*;
import nhaystack.server.*;

/**
  * BSiteRefFE edits a 'siteRef' BOrd
  */
public class BSiteRefFE extends BWbFieldEditor
{
    /*-
    class BSiteRefFE
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BSiteRefFE(2588591749)1.0$ @*/
/* Generated Sun Feb 10 06:53:50 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BSiteRefFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BSiteRefFE() { }

    public BSiteRefFE(BHDictEditorGroup group)
    {
        this.dropDown = new BListDropDown();

        dropDown.getList().addItem(LEX.getText("none"));

        // populate the list by fetching all the sites from the server
        HGrid grid = ((BHGrid) group.service().invoke(
                BNHaystackService.fetchSites, null)).getGrid();

        for (int i = 0; i < grid.numRows(); i++)
        {
            HStr slotPath = (HStr) grid.row(i).get("axSlotPath", true);
            dropDown.getList().addItem(slotPath.val);
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
