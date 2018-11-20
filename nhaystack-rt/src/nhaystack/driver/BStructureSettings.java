//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Apr 2015  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations,
//                               added use of generics
//
package nhaystack.driver;

import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

@NiagaraType
@NiagaraProperty(
  name = "siteFilter",
  type = "String",
  defaultValue = ""
)
@NiagaraProperty(
  name = "pointFilter",
  type = "String",
  defaultValue = ""
)
@NiagaraProperty(
  name = "equipFilter",
  type = "String",
  defaultValue = ""
)
public class BStructureSettings extends BComponent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BStructureSettings(3450089725)1.0$ @*/
/* Generated Sat Nov 18 18:03:08 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "siteFilter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code siteFilter} property.
   * @see #getSiteFilter
   * @see #setSiteFilter
   */
  public static final Property siteFilter = newProperty(0, "", null);
  
  /**
   * Get the {@code siteFilter} property.
   * @see #siteFilter
   */
  public String getSiteFilter() { return getString(siteFilter); }
  
  /**
   * Set the {@code siteFilter} property.
   * @see #siteFilter
   */
  public void setSiteFilter(String v) { setString(siteFilter, v, null); }

////////////////////////////////////////////////////////////////
// Property "pointFilter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code pointFilter} property.
   * @see #getPointFilter
   * @see #setPointFilter
   */
  public static final Property pointFilter = newProperty(0, "", null);
  
  /**
   * Get the {@code pointFilter} property.
   * @see #pointFilter
   */
  public String getPointFilter() { return getString(pointFilter); }
  
  /**
   * Set the {@code pointFilter} property.
   * @see #pointFilter
   */
  public void setPointFilter(String v) { setString(pointFilter, v, null); }

////////////////////////////////////////////////////////////////
// Property "equipFilter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code equipFilter} property.
   * @see #getEquipFilter
   * @see #setEquipFilter
   */
  public static final Property equipFilter = newProperty(0, "", null);
  
  /**
   * Get the {@code equipFilter} property.
   * @see #equipFilter
   */
  public String getEquipFilter() { return getString(equipFilter); }
  
  /**
   * Set the {@code equipFilter} property.
   * @see #equipFilter
   */
  public void setEquipFilter(String v) { setString(equipFilter, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BStructureSettings.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
    
    public BPointGrouping[] getPointGroupings()
    {
        return getChildren(BPointGrouping.class);
    }
}
