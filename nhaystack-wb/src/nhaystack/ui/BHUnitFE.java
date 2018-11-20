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
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Action;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BDropDown;
import javax.baja.ui.BListDropDown;
import javax.baja.ui.enums.BHalign;
import javax.baja.ui.enums.BValign;
import javax.baja.ui.event.BWidgetEvent;
import javax.baja.ui.list.BList;
import javax.baja.ui.pane.BGridPane;
import javax.baja.workbench.BWbPlugin;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;
import nhaystack.BHUnit;
import nhaystack.res.Resources;
import nhaystack.res.Unit;

/**
  * BHUnitFE edits a haystack unit.
  */

@NiagaraType(
  agent =   @AgentOn(
    types = "nhaystack:HUnit"
  )
)
@NiagaraAction(
  name = "quantitiesModified",
  parameterType = "BWidgetEvent",
  defaultValue = "new BWidgetEvent()"
)
public class BHUnitFE extends BWbFieldEditor
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHUnitFE(1142692412)1.0$ @*/
/* Generated Mon Nov 20 13:19:26 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Action "quantitiesModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code quantitiesModified} action.
   * @see #quantitiesModified(BWidgetEvent parameter)
   */
  public static final Action quantitiesModified = newAction(0, new BWidgetEvent(), null);
  
  /**
   * Invoke the {@code quantitiesModified} action.
   * @see #quantitiesModified
   */
  public void quantitiesModified(BWidgetEvent parameter) { invoke(quantitiesModified, parameter, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHUnitFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHUnitFE()
    {
        String[] quantities = Resources.getUnitQuantities();
        BList list = quantDropDown.getList();
        for (String quantity : quantities)
          list.addItem(quantity);

        BGridPane grid = new BGridPane(2);
        grid.setHalign(BHalign.left);
        grid.setValign(BValign.top);
        grid.add(null, quantDropDown);
        grid.add(null, unitsDropDown);

        setContent(grid);

        linkTo(quantDropDown, BDropDown.valueModified, quantitiesModified);
        linkTo(unitsDropDown, BDropDown.valueModified, BWbPlugin.setModified);

        eventsEnabled = true;
    }

    @Override
    protected void doSetReadonly(boolean readonly)
    {
        quantDropDown.setEnabled(!readonly);
        unitsDropDown.setEnabled(!readonly);
    }

    @Override
    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        String sym = ((BHUnit) value).getSymbol();
        Unit unit = Resources.getSymbolUnit(sym);

        quantDropDown.setSelectedItem(unit.quantity);

        populateUnitsDropDown(unit.quantity);
        unitsDropDown.setSelectedItem(unit.toDisplayString());
    }

    @Override
    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        if (!getEnabled()) throw new IllegalStateException();

        Unit unit = curUnits[unitsDropDown.getSelectedIndex()];
        return BHUnit.make(unit.symbol);
    }

    @Override
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
        for (Unit curUnit : curUnits)
          list.addItem(curUnit.toDisplayString());
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private boolean eventsEnabled;

    private final BListDropDown quantDropDown = new BListDropDown();
    private final BListDropDown unitsDropDown = new BListDropDown();
    private Unit[] curUnits;
}
