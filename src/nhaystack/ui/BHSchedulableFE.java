//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Feb 2013  Mike Jarmy Creation
//

package nhaystack.ui;

import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.enums.*;
import javax.baja.ui.event.*;
import javax.baja.ui.list.*;
import javax.baja.ui.pane.*;
import javax.baja.util.*;
import javax.baja.workbench.*;
import javax.baja.workbench.fieldeditor.*;

import nhaystack.*;
import nhaystack.res.*;

/**
  * BHSchedulableFE edits a haystack schedulable.
  */
public class BHSchedulableFE extends BWbFieldEditor
{
    /*-
    class BHSchedulableFE
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHSchedulableFE(1332306162)1.0$ @*/
/* Generated Wed Jun 25 09:16:11 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHSchedulableFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHSchedulableFE()
    {
        BList list = dropDown.getList();

        list.addItem(LEX.getText("none"));
        for (int i = 1; i <= 16; i++)
            list.addItem(Integer.toString(i));

        setContent(dropDown);

        linkTo(dropDown, BDropDown.valueModified, BWbPlugin.setModified);
    }

    protected void doSetReadonly(boolean readonly)
    {
        dropDown.setEnabled(!readonly);
    }

    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        BHSchedulable sched = (BHSchedulable) value;

        if (sched.equals(BHSchedulable.DEFAULT))
            dropDown.setSelectedItem(LEX.getText("none"));
        else
            dropDown.setSelectedItem(sched.toString());
    }

    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        if (!getEnabled()) throw new IllegalStateException();

        String str = (String) dropDown.getSelectedItem();

        if (str.equals(LEX.getText("none")))
            return BHSchedulable.DEFAULT;
        else
            return BHSchedulable.make(Integer.parseInt(str));
    }

    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        dropDown.setEnabled(enabled);
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private BListDropDown dropDown = new BListDropDown();
}
