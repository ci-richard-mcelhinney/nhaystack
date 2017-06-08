//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 May 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import javax.baja.sys.*;

import nhaystack.*;

/**
  * BTimeZoneAlias provides for a custom mapping between AX TimeZones
  * and Haystack TimeZones.
  */
public class BTimeZoneAlias extends BComponent
{
    /*-
    class BTimeZoneAlias
    {
        properties
        {
            axTimeZoneId: String default {[ "" ]}
            haystackTimeZone: BHTimeZone default {[ BHTimeZone.DEFAULT ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BTimeZoneAlias(1755663353)1.0$ @*/
/* Generated Tue May 30 17:08:43 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "axTimeZoneId"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>axTimeZoneId</code> property.
   * @see nhaystack.server.BTimeZoneAlias#getAxTimeZoneId
   * @see nhaystack.server.BTimeZoneAlias#setAxTimeZoneId
   */
  public static final Property axTimeZoneId = newProperty(0, "",null);
  
  /**
   * Get the <code>axTimeZoneId</code> property.
   * @see nhaystack.server.BTimeZoneAlias#axTimeZoneId
   */
  public String getAxTimeZoneId() { return getString(axTimeZoneId); }
  
  /**
   * Set the <code>axTimeZoneId</code> property.
   * @see nhaystack.server.BTimeZoneAlias#axTimeZoneId
   */
  public void setAxTimeZoneId(String v) { setString(axTimeZoneId,v,null); }

////////////////////////////////////////////////////////////////
// Property "haystackTimeZone"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystackTimeZone</code> property.
   * @see nhaystack.server.BTimeZoneAlias#getHaystackTimeZone
   * @see nhaystack.server.BTimeZoneAlias#setHaystackTimeZone
   */
  public static final Property haystackTimeZone = newProperty(0, BHTimeZone.DEFAULT,null);
  
  /**
   * Get the <code>haystackTimeZone</code> property.
   * @see nhaystack.server.BTimeZoneAlias#haystackTimeZone
   */
  public BHTimeZone getHaystackTimeZone() { return (BHTimeZone)get(haystackTimeZone); }
  
  /**
   * Set the <code>haystackTimeZone</code> property.
   * @see nhaystack.server.BTimeZoneAlias#haystackTimeZone
   */
  public void setHaystackTimeZone(BHTimeZone v) { set(haystackTimeZone,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTimeZoneAlias.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public boolean isParentLegal(BComponent parent)
    {
        return (parent instanceof BTimeZoneAliasFolder);
    }
}
