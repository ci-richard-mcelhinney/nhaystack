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
  * BHFloorFE edits a haystack floor.
  */
public class BHFloorFE extends BWbFieldEditor
{
    /*-
    class BHFloorFE
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHFloorFE(1332306162)1.0$ @*/
/* Generated Wed Jun 25 09:16:11 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHFloorFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHFloorFE()
    {
        BList list = prefix.getList();
        for (int i = 0; i < PREFIXES.length; i++)
            list.addItem(PREFIXES[i]);
        list.addItem(CUSTOM);

        BEdgePane ep = new BEdgePane();
        ep.setLeft(prefix);
        ep.setRight(suffix);
        setContent(ep);

        linkTo(prefix, BDropDown.valueModified, BWbPlugin.setModified);
        linkTo(suffix, BTextField.actionPerformed, BWbPlugin.setModified);
    }

    protected void doSetReadonly(boolean readonly)
    {
        prefix.setEnabled(!readonly);
        suffix.setEnabled(!readonly);
    }

    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        BHFloor floor = (BHFloor) value;
        String str = floor.getFloor();

        if (str.equals(""))
        {
            prefix.setSelectedItem("Floor");
            suffix.setText("");
        }
        else
        {
            boolean isCustom = false;
            for (int i = 0; i < PREFIXES.length; i++)
            {
                if (str.startsWith(PREFIXES[i]))
                {
                    prefix.setSelectedItem(PREFIXES[i]);
                    suffix.setText(str.substring(PREFIXES[i].length()));
                    isCustom = true;
                    break;
                }
            }

            // Custom
            if (!isCustom)
            {
                prefix.setSelectedItem(CUSTOM);
                suffix.setText(str);
            }
        }
    }

    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        if (!getEnabled()) throw new IllegalStateException();

        String a = (String) prefix.getSelectedItem();
        String b = (String) suffix.getText();

        if (a.equals("Floor") && b.equals(""))
            return BHFloor.DEFAULT;

        else if (a.equals(CUSTOM))
            return BHFloor.make(b);

        else
            return BHFloor.make(a + b);
    }

    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        prefix.setEnabled(enabled);
        suffix.setEnabled(enabled);
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private static final String[] PREFIXES = new String[]
    { 
        "Floor",
        "Basement",
        "Ground Floor",
        "Mezzanine",
        "Roof",
        "Utility",
    };
    private static final String CUSTOM = "Custom";

    private BListDropDown prefix = new BListDropDown();
    private BTextField suffix = new BTextField();
}
