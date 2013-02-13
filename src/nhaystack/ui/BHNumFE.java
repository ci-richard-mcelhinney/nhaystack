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
import javax.baja.ui.pane.*;
import javax.baja.util.*;
import javax.baja.workbench.*;
import javax.baja.workbench.fieldeditor.*;

import haystack.*;
import nhaystack.*;

/**
  * BHNumFE edits an HNum
  */
public class BHNumFE extends BWbFieldEditor
{
    /*-
    class BHNumFE
    {
        actions
        {
            hasUnitModified()
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHNumFE(298792993)1.0$ @*/
/* Generated Tue Feb 05 14:25:56 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Action "hasUnitModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>hasUnitModified</code> action.
   * @see nhaystack.ui.BHNumFE#hasUnitModified()
   */
  public static final Action hasUnitModified = newAction(0,null);
  
  /**
   * Invoke the <code>hasUnitModified</code> action.
   * @see nhaystack.ui.BHNumFE#hasUnitModified
   */
  public void hasUnitModified() { invoke(hasUnitModified,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHNumFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHNumFE()
    {
        this.valFE = BWbFieldEditor.makeFor(BDouble.DEFAULT);
        this.hasUnit = new BCheckBox(LEX.getText("Unit"));
        this.unitFE = new BHUnitFE(); 

        BGridPane grid = new BGridPane(3);
        grid.setHalign(BHalign.left);
        grid.setValign(BValign.top);
        grid.add(null, valFE);
        grid.add(null, hasUnit);
        grid.add(null, unitFE);

        setContent(grid);

        linkTo(valFE,   BWbPlugin.pluginModified,  BWbPlugin.setModified);
        linkTo(hasUnit, BCheckBox.actionPerformed, hasUnitModified);
        linkTo(unitFE,  BWbPlugin.pluginModified,  BWbPlugin.setModified);
        eventsEnabled = true;
    }

    public void doHasUnitModified()
    {
        if (!eventsEnabled) return;

        setModified();

        if (hasUnit.getSelected())
        {
            unitFE.setEnabled(true);
            unitFE.loadValue(BHUnit.DEFAULT);
        }
        else
        {
            unitFE.setEnabled(false);
        }
    }

    protected void doSetReadonly(boolean readonly)
    {
        valFE.setEnabled(!readonly);
        hasUnit.setEnabled(!readonly);
        unitFE.setEnabled(!readonly);
    }

    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        HNum num = ((BHNum) value).getNum();

        eventsEnabled = false;

        boolean restrictUnits = (cx == null) ? 
            false : cx.getFacets().getb(RESTRICT_UNITS, false);

        valFE.loadValue(BDouble.make(num.val));
        hasUnit.setSelected(num.unit != null);

        if (hasUnit.getSelected())
        {
            unitFE.loadValue(BHUnit.make(num.unit));
            unitFE.setEnabled(true);
        }
        else
        {
            unitFE.setEnabled(false);
        }

        // restrict the type of units that can be selected
        if (restrictUnits)
        {
            hasUnit.setEnabled(false);
            unitFE.lockQuantity();
        }

        eventsEnabled = true;
    }

    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        BDouble val = (BDouble) valFE.saveValue();

        if (hasUnit.getSelected())
        {
            BHUnit unit = (BHUnit) unitFE.saveValue(); 
            return BHNum.make(HNum.make(val.getDouble(), unit.getSymbol()));
        }
        else
        {
            return BHNum.make(HNum.make(val.getDouble()));
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

// TODO
//tag,quantity
//area,area
//coolingCapacity,power

    public static final String RESTRICT_UNITS = "restrict_units";

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private boolean eventsEnabled;

    private BWbFieldEditor valFE;
    private BCheckBox hasUnit;
    private BHUnitFE unitFE;
}
