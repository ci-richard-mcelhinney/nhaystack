//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 May 2013  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotation
//
package nhaystack.server;

import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHTimeZone;

/**
  * BTimeZoneAlias provides for a custom mapping between AX TimeZones
  * and Haystack TimeZones.
  */
@NiagaraType
@NiagaraProperty(
  name = "axTimeZoneId",
  type = "String",
  defaultValue = ""
)
@NiagaraProperty(
  name = "haystackTimeZone",
  type = "BHTimeZone",
  defaultValue = "BHTimeZone.DEFAULT"
)
public class BTimeZoneAlias extends BComponent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BTimeZoneAlias(2627705236)1.0$ @*/
/* Generated Sat Nov 18 18:40:11 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "axTimeZoneId"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code axTimeZoneId} property.
   * @see #getAxTimeZoneId
   * @see #setAxTimeZoneId
   */
  public static final Property axTimeZoneId = newProperty(0, "", null);
  
  /**
   * Get the {@code axTimeZoneId} property.
   * @see #axTimeZoneId
   */
  public String getAxTimeZoneId() { return getString(axTimeZoneId); }
  
  /**
   * Set the {@code axTimeZoneId} property.
   * @see #axTimeZoneId
   */
  public void setAxTimeZoneId(String v) { setString(axTimeZoneId, v, null); }

////////////////////////////////////////////////////////////////
// Property "haystackTimeZone"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code haystackTimeZone} property.
   * @see #getHaystackTimeZone
   * @see #setHaystackTimeZone
   */
  public static final Property haystackTimeZone = newProperty(0, BHTimeZone.DEFAULT, null);
  
  /**
   * Get the {@code haystackTimeZone} property.
   * @see #haystackTimeZone
   */
  public BHTimeZone getHaystackTimeZone() { return (BHTimeZone)get(haystackTimeZone); }
  
  /**
   * Set the {@code haystackTimeZone} property.
   * @see #haystackTimeZone
   */
  public void setHaystackTimeZone(BHTimeZone v) { set(haystackTimeZone, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTimeZoneAlias.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    public boolean isParentLegal(BComponent parent)
    {
        return parent instanceof BTimeZoneAliasFolder;
    }
}
