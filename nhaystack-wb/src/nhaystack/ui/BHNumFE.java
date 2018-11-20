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
import javax.baja.sys.BDouble;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BAbstractButton;
import javax.baja.ui.BCheckBox;
import javax.baja.ui.enums.BHalign;
import javax.baja.ui.enums.BValign;
import javax.baja.ui.pane.BGridPane;
import javax.baja.util.Lexicon;
import javax.baja.workbench.BWbPlugin;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;
import nhaystack.BHNum;
import nhaystack.BHUnit;
import org.projecthaystack.HNum;

/**
  * BHNumFE edits an HNum
  */
@NiagaraType(
  agent =   @AgentOn(
    types = "nhaystack:HNum"
  )
)
@NiagaraAction(
  name = "hasUnitModified"
)
public class BHNumFE extends BWbFieldEditor
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHNumFE(3766775585)1.0$ @*/
/* Generated Mon Nov 20 13:07:30 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Action "hasUnitModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code hasUnitModified} action.
   * @see #hasUnitModified()
   */
  public static final Action hasUnitModified = newAction(0, null);
  
  /**
   * Invoke the {@code hasUnitModified} action.
   * @see #hasUnitModified
   */
  public void hasUnitModified() { invoke(hasUnitModified, null, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
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
        linkTo(hasUnit, BAbstractButton.actionPerformed, hasUnitModified);
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

    @Override
    protected void doSetReadonly(boolean readonly)
    {
        valFE.setEnabled(!readonly);
        hasUnit.setEnabled(!readonly);
        unitFE.setEnabled(!readonly);
    }

    @Override
    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        HNum num = ((BHNum) value).getNum();

        eventsEnabled = false;

        boolean restrictUnits = cx == null ?
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

    @Override
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

    private final BWbFieldEditor valFE;
    private final BCheckBox hasUnit;
    private final BHUnitFE unitFE;
}
