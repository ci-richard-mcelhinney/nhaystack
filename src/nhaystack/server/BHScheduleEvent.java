//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Jul 2014  Mike Jarmy  Creation
//
package nhaystack.server;

import javax.baja.sys.*;

import nhaystack.*;

/**
  * BHScheduleEvent 
  */
public class BHScheduleEvent extends BComponent
{
    /*-
    class BHScheduleEvent
    {
        properties
        {
            id:    BHRef  default {[ BHRef.DEFAULT    ]}
            value: BValue default {[ BInteger.DEFAULT ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BHScheduleEvent(3020360521)1.0$ @*/
/* Generated Thu Jul 03 16:11:30 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "id"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>id</code> property.
   * @see nhaystack.server.BHScheduleEvent#getId
   * @see nhaystack.server.BHScheduleEvent#setId
   */
  public static final Property id = newProperty(0, BHRef.DEFAULT,null);
  
  /**
   * Get the <code>id</code> property.
   * @see nhaystack.server.BHScheduleEvent#id
   */
  public BHRef getId() { return (BHRef)get(id); }
  
  /**
   * Set the <code>id</code> property.
   * @see nhaystack.server.BHScheduleEvent#id
   */
  public void setId(BHRef v) { set(id,v,null); }

////////////////////////////////////////////////////////////////
// Property "value"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>value</code> property.
   * @see nhaystack.server.BHScheduleEvent#getValue
   * @see nhaystack.server.BHScheduleEvent#setValue
   */
  public static final Property value = newProperty(0, BInteger.DEFAULT,null);
  
  /**
   * Get the <code>value</code> property.
   * @see nhaystack.server.BHScheduleEvent#value
   */
  public BValue getValue() { return (BValue)get(value); }
  
  /**
   * Set the <code>value</code> property.
   * @see nhaystack.server.BHScheduleEvent#value
   */
  public void setValue(BValue v) { set(value,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
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
