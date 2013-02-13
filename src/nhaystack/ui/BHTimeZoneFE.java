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
import javax.baja.ui.list.*;
import javax.baja.workbench.*;
import javax.baja.workbench.fieldeditor.*;

import haystack.*;
import nhaystack.*;
import nhaystack.res.*;

/**
  * BHTimeZoneFE edits a Haystack timezone.
  */
public class BHTimeZoneFE extends BWbFieldEditor
{
    /*-
    class BHTimeZoneFE
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHTimeZoneFE(975572595)1.0$ @*/
/* Generated Fri Feb 01 11:41:31 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHTimeZoneFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHTimeZoneFE()
    {
        BList list = dropDown.getList();
        String[] tz = Resources.getTimeZones();
        for (int i = 0; i < tz.length; i++)
            list.addItem(tz[i]);

        linkTo(dropDown, BDropDown.valueModified, BWbPlugin.setModified);

        setContent(dropDown);
    }

    protected void doSetReadonly(boolean readonly)
    {
        dropDown.setEnabled(!readonly);
    }

    protected void doLoadValue(BObject value, Context cx) throws Exception

    {
        BHTimeZone tz = (BHTimeZone) value;
        dropDown.setSelectedItem(tz.getTimeZone().name);
    }

    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        return BHTimeZone.make(HTimeZone.make((String) dropDown.getSelectedItem()));
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private BListDropDown dropDown = new BListDropDown();
}
