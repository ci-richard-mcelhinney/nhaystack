//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Apr 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import javax.baja.sys.*;

public class BUniqueEquipTypeArgs extends BStruct
{
    /*-
    class BUniqueEquipTypeArgs
    {
        properties
        {
            filter: String default{[ "equip" ]}
            percentMatch: int default{[ 80 ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BUniqueEquipTypeArgs(1301521084)1.0$ @*/
/* Generated Mon May 04 13:59:46 EDT 2015 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "filter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>filter</code> property.
   * @see nhaystack.server.BUniqueEquipTypeArgs#getFilter
   * @see nhaystack.server.BUniqueEquipTypeArgs#setFilter
   */
  public static final Property filter = newProperty(0, "equip",null);
  
  /**
   * Get the <code>filter</code> property.
   * @see nhaystack.server.BUniqueEquipTypeArgs#filter
   */
  public String getFilter() { return getString(filter); }
  
  /**
   * Set the <code>filter</code> property.
   * @see nhaystack.server.BUniqueEquipTypeArgs#filter
   */
  public void setFilter(String v) { setString(filter,v,null); }

////////////////////////////////////////////////////////////////
// Property "percentMatch"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>percentMatch</code> property.
   * @see nhaystack.server.BUniqueEquipTypeArgs#getPercentMatch
   * @see nhaystack.server.BUniqueEquipTypeArgs#setPercentMatch
   */
  public static final Property percentMatch = newProperty(0, 80,null);
  
  /**
   * Get the <code>percentMatch</code> property.
   * @see nhaystack.server.BUniqueEquipTypeArgs#percentMatch
   */
  public int getPercentMatch() { return getInt(percentMatch); }
  
  /**
   * Set the <code>percentMatch</code> property.
   * @see nhaystack.server.BUniqueEquipTypeArgs#percentMatch
   */
  public void setPercentMatch(int v) { setInt(percentMatch,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BUniqueEquipTypeArgs.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
}
