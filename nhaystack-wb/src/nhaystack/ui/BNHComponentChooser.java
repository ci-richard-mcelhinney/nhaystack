//
// Copyright (c) 2018. Tridium, Inc. All rights reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   30 Jul 2018  Andrew Saunders  Creation
//

package nhaystack.ui;

import javax.baja.control.BControlPoint;
import javax.baja.driver.BDevice;
import javax.baja.naming.SlotPath;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.schedule.BWeeklySchedule;
import javax.baja.sys.BComponent;
import javax.baja.sys.BObject;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHDict;
import nhaystack.server.SpaceManager;
import nhaystack.server.TagManager;
import nhaystack.server.ThreadContext;
import nhaystack.site.BHTagged;
import nhaystack.util.TypeUtil;
import com.tridium.workbench.ord.BComponentChooser;
import com.tridium.workbench.ord.RefFilter;

/**
 * This ComponentChooser is for nhaystack component choosing only.
 */
@NiagaraType
public class BNHComponentChooser extends BComponentChooser
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BNHComponentChooser(2979906276)1.0$ @*/
/* Generated Mon Jul 30 19:09:04 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHComponentChooser.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    public BComponentChooser getComponentChooser(BComponent root, SlotPath path, BObject base)
    {
        return new BComponentChooser(root, path, RefFilter.components, nhaystackComponent);
    }

    /** The all filter accepts only property
     slots which are components. */
    public static final RefFilter nhaystackComponent = new RefFilter()
    {
        @Override
        public boolean accept(BObject parent, Slot slot)
        {
            if (!(slot != null && parent != null && slot.isProperty() &&
                  parent.asComplex().get(slot.asProperty()).isComponent()))
            {
                return false;
            }

            BComponent comp = parent.asComponent().get(slot.asProperty()).asComponent();
            return SpaceManager.isVisibleComponent(comp);
        }
    };
}
