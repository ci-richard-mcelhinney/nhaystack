//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Apr 2015  Mike Jarmy  Creation
//
package nhaystack.driver;

import javax.baja.sys.*;

public class BPointGrouping extends BComponent
{
    /*-
    class BPointGrouping
    {
        properties
        {
            filter:    String default{[ "" ]}
            groupName: String default{[ "" ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BPointGrouping(1898514170)1.0$ @*/
/* Generated Tue May 30 17:08:42 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "filter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>filter</code> property.
   * @see nhaystack.driver.BPointGrouping#getFilter
   * @see nhaystack.driver.BPointGrouping#setFilter
   */
  public static final Property filter = newProperty(0, "",null);
  
  /**
   * Get the <code>filter</code> property.
   * @see nhaystack.driver.BPointGrouping#filter
   */
  public String getFilter() { return getString(filter); }
  
  /**
   * Set the <code>filter</code> property.
   * @see nhaystack.driver.BPointGrouping#filter
   */
  public void setFilter(String v) { setString(filter,v,null); }

////////////////////////////////////////////////////////////////
// Property "groupName"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>groupName</code> property.
   * @see nhaystack.driver.BPointGrouping#getGroupName
   * @see nhaystack.driver.BPointGrouping#setGroupName
   */
  public static final Property groupName = newProperty(0, "",null);
  
  /**
   * Get the <code>groupName</code> property.
   * @see nhaystack.driver.BPointGrouping#groupName
   */
  public String getGroupName() { return getString(groupName); }
  
  /**
   * Set the <code>groupName</code> property.
   * @see nhaystack.driver.BPointGrouping#groupName
   */
  public void setGroupName(String v) { setString(groupName,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BPointGrouping.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
}
