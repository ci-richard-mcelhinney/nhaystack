//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Jul 2014  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations
//
package nhaystack.server;

import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BInteger;
import javax.baja.sys.BValue;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHRef;

/**
  * BHScheduleEvent 
  */
@NiagaraType
@NiagaraProperty(
  name = "id",
  type = "BHRef",
  defaultValue = "BHRef.DEFAULT"
)
@NiagaraProperty(
  name = "value",
  type = "BValue",
  defaultValue = "BInteger.DEFAULT"
)
public class BHScheduleEvent extends BComponent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BHScheduleEvent(1419960706)1.0$ @*/
/* Generated Sat Nov 18 18:32:23 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "id"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code id} property.
   * @see #getId
   * @see #setId
   */
  public static final Property id = newProperty(0, BHRef.DEFAULT, null);
  
  /**
   * Get the {@code id} property.
   * @see #id
   */
  public BHRef getId() { return (BHRef)get(id); }
  
  /**
   * Set the {@code id} property.
   * @see #id
   */
  public void setId(BHRef v) { set(id, v, null); }

////////////////////////////////////////////////////////////////
// Property "value"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code value} property.
   * @see #getValue
   * @see #setValue
   */
  public static final Property value = newProperty(0, BInteger.DEFAULT, null);
  
  /**
   * Get the {@code value} property.
   * @see #value
   */
  public BValue getValue() { return get(value); }
  
  /**
   * Set the {@code value} property.
   * @see #value
   */
  public void setValue(BValue v) { set(value, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHScheduleEvent.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  public BHScheduleEvent() {}

  public BHScheduleEvent(BHRef id, BValue value)
  {
      setId(id);
      setValue(value);
  }
}
