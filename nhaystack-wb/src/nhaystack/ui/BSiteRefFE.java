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
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BDropDown;
import javax.baja.ui.BListDropDown;
import javax.baja.util.Lexicon;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;
import nhaystack.BHGrid;
import nhaystack.server.BNHaystackService;
import org.projecthaystack.HGrid;

/**
  * BSiteRefFE edits a 'siteRef' BOrd
  */
@NiagaraType
public class BSiteRefFE extends BWbFieldEditor
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BSiteRefFE(2979906276)1.0$ @*/
/* Generated Mon Nov 20 13:22:59 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
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
            String slotPath = getRowSlotPath(grid.row(i));
            dropDown.getList().addItem(slotPath);
        }

        setContent(dropDown);

        linkTo(dropDown, BDropDown.valueModified, setModified);  
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

        String slotPath = ord.toString();
        if (slotPath.startsWith("station:|"))
            slotPath = slotPath.substring("station:|".length());

        if (ord.equals(BOrd.DEFAULT))
            dropDown.setSelectedIndex(0);
        else
            dropDown.setSelectedItem(slotPath);
    }

    @Override
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

    private static String getRowSlotPath(HRow row)
    {
        if (row.has("n4SlotPath"))
            return row.getStr("n4SlotPath");
        else
            return row.getStr("axSlotPath");
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private BListDropDown dropDown;
}
