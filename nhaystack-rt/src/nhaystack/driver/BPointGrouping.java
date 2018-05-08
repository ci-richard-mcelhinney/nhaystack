//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Apr 2015  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
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
  name = "filter",
  type = "String",
  defaultValue = ""
)
@NiagaraProperty(
  name = "groupName",
  type = "String",
  defaultValue = ""
)
public class BPointGrouping extends BComponent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BPointGrouping(1645078487)1.0$ @*/
/* Generated Sat Nov 18 18:02:20 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "filter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code filter} property.
   * @see #getFilter
   * @see #setFilter
   */
  public static final Property filter = newProperty(0, "", null);
  
  /**
   * Get the {@code filter} property.
   * @see #filter
   */
  public String getFilter() { return getString(filter); }
  
  /**
   * Set the {@code filter} property.
   * @see #filter
   */
  public void setFilter(String v) { setString(filter, v, null); }

////////////////////////////////////////////////////////////////
// Property "groupName"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code groupName} property.
   * @see #getGroupName
   * @see #setGroupName
   */
  public static final Property groupName = newProperty(0, "", null);
  
  /**
   * Get the {@code groupName} property.
   * @see #groupName
   */
  public String getGroupName() { return getString(groupName); }
  
  /**
   * Set the {@code groupName} property.
   * @see #groupName
   */
  public void setGroupName(String v) { setString(groupName, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BPointGrouping.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
}
