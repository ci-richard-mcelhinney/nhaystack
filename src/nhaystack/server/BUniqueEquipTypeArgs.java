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
            applyTags: boolean default{[ false ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BUniqueEquipTypeArgs(99543646)1.0$ @*/
/* Generated Wed May 06 09:30:55 EDT 2015 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
// Property "applyTags"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>applyTags</code> property.
   * @see nhaystack.server.BUniqueEquipTypeArgs#getApplyTags
   * @see nhaystack.server.BUniqueEquipTypeArgs#setApplyTags
   */
  public static final Property applyTags = newProperty(0, false,null);
  
  /**
   * Get the <code>applyTags</code> property.
   * @see nhaystack.server.BUniqueEquipTypeArgs#applyTags
   */
  public boolean getApplyTags() { return getBoolean(applyTags); }
  
  /**
   * Set the <code>applyTags</code> property.
   * @see nhaystack.server.BUniqueEquipTypeArgs#applyTags
   */
  public void setApplyTags(boolean v) { setBoolean(applyTags,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BUniqueEquipTypeArgs.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
}
