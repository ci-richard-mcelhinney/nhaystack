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

import org.projecthaystack.*;
import nhaystack.*;
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
/*@ $nhaystack.ui.BSiteRefFE(1076586063)1.0$ @*/
/* Generated Tue May 30 17:08:43 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
            String slotPath = grid.row(i).getStr("axSlotPath");
            dropDown.getList().addItem(slotPath);
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

        String slotPath = ord.toString();
        if (slotPath.startsWith("station:|"))
            slotPath = slotPath.substring("station:|".length());

        if (ord.equals(BOrd.DEFAULT))
            dropDown.setSelectedIndex(0);
        else
            dropDown.setSelectedItem(slotPath);
    }

    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        String str = (String) dropDown.getSelectedItem();

        if (str.equals(LEX.getText("none")))
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
