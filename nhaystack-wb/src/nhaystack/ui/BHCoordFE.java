//
// Copyright (c) 2018 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   15 Aug 2018  Andrew Saunders  Creation
//

package nhaystack.ui;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BDouble;
import javax.baja.sys.BObject;
import javax.baja.sys.BString;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BLabel;
import javax.baja.ui.enums.BHalign;
import javax.baja.ui.enums.BValign;
import javax.baja.ui.pane.BGridPane;
import javax.baja.workbench.BWbPlugin;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;

import org.projecthaystack.HCoord;

/**
  * BHCoordFE edits a String encoded HCoord, providing separate field editors for the latitude and
  * longitude.
  */
@NiagaraType
public class BHCoordFE extends BWbFieldEditor
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHCoordFE(2979906276)1.0$ @*/
/* Generated Mon Aug 06 13:28:37 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHCoordFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHCoordFE()
    {
        latValueFE = BWbFieldEditor.makeFor(BDouble.DEFAULT);
        lonValueFE = BWbFieldEditor.makeFor(BDouble.DEFAULT);
        BGridPane gp = new BGridPane(4);
        gp.setHalign(BHalign.left);
        gp.setValign(BValign.top);
        gp.add(null, new BLabel("latitude:"));
        gp.add(null, latValueFE);
        gp.add(null, new BLabel("longitude:"));
        gp.add(null, lonValueFE);
        setContent(gp);

        linkTo(latValueFE, BWbPlugin.pluginModified, BWbPlugin.setModified);
        linkTo(lonValueFE, BWbPlugin.pluginModified, BWbPlugin.setModified);
    }

    @Override
    protected void doSetReadonly(boolean readonly)
    {
        latValueFE.setEnabled(!readonly);
        lonValueFE.setEnabled(!readonly);
    }

    @Override
    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        final HCoord coord = HCoord.make(value.toString());
        latValueFE.loadValue(BDouble.make(coord.lat()));
        lonValueFE.loadValue(BDouble.make(coord.lng()));
    }

    @Override
    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        if (!getEnabled()) throw new IllegalStateException();
        final BDouble latValue = (BDouble)latValueFE.saveValue();
        final BDouble lonValue = (BDouble)lonValueFE.saveValue();
        HCoord coord = HCoord.make(latValue.getDouble(), lonValue.getDouble());
        return BString.make(coord.toString());
     }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        latValueFE.setEnabled(enabled);
        lonValueFE.setEnabled(enabled);
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private final BWbFieldEditor latValueFE;
    private final BWbFieldEditor lonValueFE;
}
