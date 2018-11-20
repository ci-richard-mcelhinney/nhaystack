//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Apr 2013  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations
//
package nhaystack.server;

import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BStruct;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

@NiagaraType
@NiagaraProperty(
  name = "filter",
  type = "String",
  defaultValue = "equip"
)
@NiagaraProperty(
  name = "percentMatch",
  type = "int",
  defaultValue = "80"
)
@NiagaraProperty(
  name = "applyTags",
  type = "boolean",
  defaultValue = "false"
)
public class BUniqueEquipTypeArgs extends BStruct
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BUniqueEquipTypeArgs(1436495067)1.0$ @*/
/* Generated Sat Nov 18 21:08:17 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "filter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code filter} property.
   * @see #getFilter
   * @see #setFilter
   */
  public static final Property filter = newProperty(0, "equip", null);
  
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
// Property "percentMatch"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code percentMatch} property.
   * @see #getPercentMatch
   * @see #setPercentMatch
   */
  public static final Property percentMatch = newProperty(0, 80, null);
  
  /**
   * Get the {@code percentMatch} property.
   * @see #percentMatch
   */
  public int getPercentMatch() { return getInt(percentMatch); }
  
  /**
   * Set the {@code percentMatch} property.
   * @see #percentMatch
   */
  public void setPercentMatch(int v) { setInt(percentMatch, v, null); }

////////////////////////////////////////////////////////////////
// Property "applyTags"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code applyTags} property.
   * @see #getApplyTags
   * @see #setApplyTags
   */
  public static final Property applyTags = newProperty(0, false, null);
  
  /**
   * Get the {@code applyTags} property.
   * @see #applyTags
   */
  public boolean getApplyTags() { return getBoolean(applyTags); }
  
  /**
   * Set the {@code applyTags} property.
   * @see #applyTags
   */
  public void setApplyTags(boolean v) { setBoolean(applyTags, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BUniqueEquipTypeArgs.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
}
