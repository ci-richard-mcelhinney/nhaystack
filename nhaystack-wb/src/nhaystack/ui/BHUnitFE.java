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
import javax.baja.workbench.*;
import javax.baja.workbench.fieldeditor.*;

import nhaystack.*;
import nhaystack.res.*;

/**
  * BHUnitFE edits a haystack unit.
  */
public class BHUnitFE extends BWbFieldEditor
{
    /*-
    class BHUnitFE
    {
        actions
        {
            quantitiesModified (event: BWidgetEvent) default {[ new BWidgetEvent() ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHUnitFE(3872698644)1.0$ @*/
/* Generated Fri Feb 01 13:41:37 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Action "quantitiesModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>quantitiesModified</code> action.
   * @see nhaystack.ui.BHUnitFE#quantitiesModified()
   */
  public static final Action quantitiesModified = newAction(0,new BWidgetEvent(),null);
  
  /**
   * Invoke the <code>quantitiesModified</code> action.
   * @see nhaystack.ui.BHUnitFE#quantitiesModified
   */
  public void quantitiesModified(BWidgetEvent event) { invoke(quantitiesModified,event,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHUnitFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHUnitFE()
    {
        String[] quantities = Resources.getUnitQuantities();
        BList list = quantDropDown.getList();
        for (int i = 0; i < quantities.length; i++)
            list.addItem(quantities[i]);

        BGridPane grid = new BGridPane(2);
        grid.setHalign(BHalign.left);
        grid.setValign(BValign.top);
        grid.add(null, quantDropDown);
        grid.add(null, unitsDropDown);

        setContent(grid);

        linkTo(quantDropDown, BDropDown.valueModified, BHUnitFE.quantitiesModified);
        linkTo(unitsDropDown, BDropDown.valueModified, BWbPlugin.setModified);

        eventsEnabled = true;
    }

    protected void doSetReadonly(boolean readonly)
    {
        quantDropDown.setEnabled(!readonly);
        unitsDropDown.setEnabled(!readonly);
    }

    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        String sym = ((BHUnit) value).getSymbol();
        Unit unit = Resources.getSymbolUnit(sym);

        quantDropDown.setSelectedItem(unit.quantity);

        populateUnitsDropDown(unit.quantity);
        unitsDropDown.setSelectedItem(unit.toDisplayString());
    }

    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        if (!getEnabled()) throw new IllegalStateException();

        Unit unit = curUnits[unitsDropDown.getSelectedIndex()];
        return BHUnit.make(unit.symbol);
    }

    public void setEnabled(boolean enabled)
    {
        eventsEnabled = false;

        super.setEnabled(enabled);

        quantDropDown.setEnabled(enabled);
        unitsDropDown.setEnabled(enabled);

        eventsEnabled = true;
    }

    public void doQuantitiesModified(BWidgetEvent event)
    {
        if (!eventsEnabled) return;

        setModified();

        populateUnitsDropDown((String) quantDropDown.getSelectedItem());
        unitsDropDown.setSelectedIndex(0);
    }

    void lockQuantity()
    {
        quantDropDown.setEnabled(false);
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private void populateUnitsDropDown(String quantity)
    {
        this.curUnits = Resources.getUnits(quantity);
        BList list = unitsDropDown.getList();
        list.removeAllItems();
        for (int i = 0; i < curUnits.length; i++)
            list.addItem(curUnits[i].toDisplayString());
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private boolean eventsEnabled;

    private BListDropDown quantDropDown = new BListDropDown();
    private BListDropDown unitsDropDown = new BListDropDown();
    private Unit[] curUnits;
}
