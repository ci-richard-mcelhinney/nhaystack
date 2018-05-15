//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Feb 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//

package nhaystack.ui;

import javax.baja.nre.annotations.AgentOn;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BDropDown;
import javax.baja.ui.BListDropDown;
import javax.baja.ui.BTextField;
import javax.baja.ui.list.BList;
import javax.baja.ui.pane.BEdgePane;
import javax.baja.ui.text.BTextEditor;
import javax.baja.util.Lexicon;
import javax.baja.workbench.BWbPlugin;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;
import nhaystack.BHFloor;

/**
  * BHFloorFE edits a haystack floor.
  */
@NiagaraType(
  agent =   @AgentOn(
    types = "nhaystack:HFloor"
  )
)
public class BHFloorFE extends BWbFieldEditor
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHFloorFE(4155728940)1.0$ @*/
/* Generated Mon Nov 20 13:04:40 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHFloorFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHFloorFE()
    {
        BList list = prefixDropDown.getList();
        for (String prefix : PREFIXES)
            list.addItem(prefix);
        list.addItem(CUSTOM);

        BEdgePane ep = new BEdgePane();
        ep.setLeft(prefixDropDown);
        ep.setRight(suffixTextField);
        setContent(ep);

        linkTo(prefixDropDown, BDropDown.valueModified, BWbPlugin.setModified);
        linkTo(suffixTextField, BTextEditor.textModified, BWbPlugin.setModified);
    }

    @Override
    protected void doSetReadonly(boolean readonly)
    {
        prefixDropDown.setEnabled(!readonly);
        suffixTextField.setEnabled(!readonly);
    }

    @Override
    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        BHFloor floor = (BHFloor) value;
        String str = floor.getFloor();

        if (str.isEmpty())
        {
            prefixDropDown.setSelectedItem("Floor");
            suffixTextField.setText("");
        }
        else
        {
            boolean found = false;
            for (String prefix : PREFIXES)
            {
                if (str.startsWith(prefix))
                {
                    this.prefixDropDown.setSelectedItem(prefix);
                    suffixTextField.setText(str.substring(prefix.length() + 1));
                    found = true;
                    break;
                }
            }

            // Custom
            if (!found)
            {
                prefixDropDown.setSelectedItem(CUSTOM);
                suffixTextField.setText(str);
            }
        }
    }

    @Override
    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        if (!getEnabled()) throw new IllegalStateException();

        String a = (String) prefixDropDown.getSelectedItem();
        String b = suffixTextField.getText();

        if (a.equals("Floor") && b.isEmpty())
            return BHFloor.DEFAULT;

        else if (a.equals(CUSTOM))
            return BHFloor.make(b);

        else
            return BHFloor.make(a + ' ' + b);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        prefixDropDown.setEnabled(enabled);
        suffixTextField.setEnabled(enabled);
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private static final String[] PREFIXES =
    { 
        "Floor",
        "Basement",
        "Ground Floor",
        "Mezzanine",
        "Roof",
        "Utility",
    };
    private static final String CUSTOM = "Custom";

    private final BListDropDown prefixDropDown = new BListDropDown();
    private final BTextField suffixTextField = new BTextField();
}
