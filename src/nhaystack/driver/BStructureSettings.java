//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Apr 2015  Mike Jarmy  Creation
//
package nhaystack.driver;

import javax.baja.sys.*;

public class BStructureSettings extends BComponent
{
    /*-
    class BStructureSettings
    {
        properties
        {
            siteFilter:  String default{[ "" ]}
            pointFilter: String default{[ "" ]}
            equipFilter: String default{[ "" ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BStructureSettings(2764181960)1.0$ @*/
/* Generated Tue May 30 17:08:42 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "siteFilter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>siteFilter</code> property.
   * @see nhaystack.driver.BStructureSettings#getSiteFilter
   * @see nhaystack.driver.BStructureSettings#setSiteFilter
   */
  public static final Property siteFilter = newProperty(0, "",null);
  
  /**
   * Get the <code>siteFilter</code> property.
   * @see nhaystack.driver.BStructureSettings#siteFilter
   */
  public String getSiteFilter() { return getString(siteFilter); }
  
  /**
   * Set the <code>siteFilter</code> property.
   * @see nhaystack.driver.BStructureSettings#siteFilter
   */
  public void setSiteFilter(String v) { setString(siteFilter,v,null); }

////////////////////////////////////////////////////////////////
// Property "pointFilter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>pointFilter</code> property.
   * @see nhaystack.driver.BStructureSettings#getPointFilter
   * @see nhaystack.driver.BStructureSettings#setPointFilter
   */
  public static final Property pointFilter = newProperty(0, "",null);
  
  /**
   * Get the <code>pointFilter</code> property.
   * @see nhaystack.driver.BStructureSettings#pointFilter
   */
  public String getPointFilter() { return getString(pointFilter); }
  
  /**
   * Set the <code>pointFilter</code> property.
   * @see nhaystack.driver.BStructureSettings#pointFilter
   */
  public void setPointFilter(String v) { setString(pointFilter,v,null); }

////////////////////////////////////////////////////////////////
// Property "equipFilter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>equipFilter</code> property.
   * @see nhaystack.driver.BStructureSettings#getEquipFilter
   * @see nhaystack.driver.BStructureSettings#setEquipFilter
   */
  public static final Property equipFilter = newProperty(0, "",null);
  
  /**
   * Get the <code>equipFilter</code> property.
   * @see nhaystack.driver.BStructureSettings#equipFilter
   */
  public String getEquipFilter() { return getString(equipFilter); }
  
  /**
   * Set the <code>equipFilter</code> property.
   * @see nhaystack.driver.BStructureSettings#equipFilter
   */
  public void setEquipFilter(String v) { setString(equipFilter,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BStructureSettings.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
    
    public BPointGrouping[] getPointGroupings()
    {
        return (BPointGrouping[]) getChildren(BPointGrouping.class);
    }
}
