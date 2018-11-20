//
// Copyright (c) 2018. Tridium, Inc. All rights reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   30 Jul 2018  Andrew Saunders  Creation
//

package nhaystack.ui;

import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.registry.TypeInfo;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.Lexicon;
import com.tridium.workbench.fieldeditors.BOrdFE;
import com.tridium.workbench.ord.BComponentChooser;

/**
 * This Ref ORD FE allows valid nhaystack component choosing only.
 */
@NiagaraType

@NiagaraProperty(
    name = "ordFieldLength",
    type = "int",
    defaultValue = "45",
    flags = Flags.TRANSIENT,
    override = true
)
public class BRefOrdFE
  extends BOrdFE
{

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BRefOrdFE(2023509165)1.0$ @*/
/* Generated Mon Jul 30 20:27:51 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "ordFieldLength"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code ordFieldLength} property.
   * @see #getOrdFieldLength
   * @see #setOrdFieldLength
   */
  public static final Property ordFieldLength = newProperty(Flags.TRANSIENT, 45, null);

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BRefOrdFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BRefOrdFE(boolean isUnresolvedRef)
    {
        this.isUnresolved = isUnresolvedRef;
    }

    @Override
  public TypeInfo[] loadTypes()
  {
    return new TypeInfo[] { BComponentChooser.TYPE.getTypeInfo() };
  }

  @Override
  protected void doLoadValue(BObject v, Context cx)
  {
      super.doLoadValue(v, cx);

      if (isUnresolved)
      {
          getTextField().setText(getTextField().getText() + ' ' + LEX.getText("unresolved.warning"));
      }

      defaultBrowse.info = BNHComponentChooser.TYPE.getTypeInfo();
  }

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    boolean isUnresolved;
}
