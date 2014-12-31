//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Apr 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import javax.baja.sys.*;

public class BNHaystackStats extends BStruct
{
    /*-
    class BNHaystackStats
    {
        properties
        {
            numPoints: int flags { readonly } default{[ 0 ]}
            numEquips: int flags { readonly } default{[ 0 ]}
            numSites:  int flags { readonly } default{[ 0 ]}
            lastCacheRebuildDuration: BRelTime flags{ readonly } default{[ BRelTime.DEFAULT ]}
            lastCacheRebuildTime:     BAbsTime flags{ readonly } default{[ BAbsTime.DEFAULT ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackStats(3626972504)1.0$ @*/
/* Generated Wed Apr 10 15:56:18 EDT 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "numPoints"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>numPoints</code> property.
   * @see nhaystack.server.BNHaystackStats#getNumPoints
   * @see nhaystack.server.BNHaystackStats#setNumPoints
   */
  public static final Property numPoints = newProperty(Flags.READONLY, 0,null);
  
  /**
   * Get the <code>numPoints</code> property.
   * @see nhaystack.server.BNHaystackStats#numPoints
   */
  public int getNumPoints() { return getInt(numPoints); }
  
  /**
   * Set the <code>numPoints</code> property.
   * @see nhaystack.server.BNHaystackStats#numPoints
   */
  public void setNumPoints(int v) { setInt(numPoints,v,null); }

////////////////////////////////////////////////////////////////
// Property "numEquips"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>numEquips</code> property.
   * @see nhaystack.server.BNHaystackStats#getNumEquips
   * @see nhaystack.server.BNHaystackStats#setNumEquips
   */
  public static final Property numEquips = newProperty(Flags.READONLY, 0,null);
  
  /**
   * Get the <code>numEquips</code> property.
   * @see nhaystack.server.BNHaystackStats#numEquips
   */
  public int getNumEquips() { return getInt(numEquips); }
  
  /**
   * Set the <code>numEquips</code> property.
   * @see nhaystack.server.BNHaystackStats#numEquips
   */
  public void setNumEquips(int v) { setInt(numEquips,v,null); }

////////////////////////////////////////////////////////////////
// Property "numSites"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>numSites</code> property.
   * @see nhaystack.server.BNHaystackStats#getNumSites
   * @see nhaystack.server.BNHaystackStats#setNumSites
   */
  public static final Property numSites = newProperty(Flags.READONLY, 0,null);
  
  /**
   * Get the <code>numSites</code> property.
   * @see nhaystack.server.BNHaystackStats#numSites
   */
  public int getNumSites() { return getInt(numSites); }
  
  /**
   * Set the <code>numSites</code> property.
   * @see nhaystack.server.BNHaystackStats#numSites
   */
  public void setNumSites(int v) { setInt(numSites,v,null); }

////////////////////////////////////////////////////////////////
// Property "lastCacheRebuildDuration"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>lastCacheRebuildDuration</code> property.
   * @see nhaystack.server.BNHaystackStats#getLastCacheRebuildDuration
   * @see nhaystack.server.BNHaystackStats#setLastCacheRebuildDuration
   */
  public static final Property lastCacheRebuildDuration = newProperty(Flags.READONLY, BRelTime.DEFAULT,null);
  
  /**
   * Get the <code>lastCacheRebuildDuration</code> property.
   * @see nhaystack.server.BNHaystackStats#lastCacheRebuildDuration
   */
  public BRelTime getLastCacheRebuildDuration() { return (BRelTime)get(lastCacheRebuildDuration); }
  
  /**
   * Set the <code>lastCacheRebuildDuration</code> property.
   * @see nhaystack.server.BNHaystackStats#lastCacheRebuildDuration
   */
  public void setLastCacheRebuildDuration(BRelTime v) { set(lastCacheRebuildDuration,v,null); }

////////////////////////////////////////////////////////////////
// Property "lastCacheRebuildTime"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>lastCacheRebuildTime</code> property.
   * @see nhaystack.server.BNHaystackStats#getLastCacheRebuildTime
   * @see nhaystack.server.BNHaystackStats#setLastCacheRebuildTime
   */
  public static final Property lastCacheRebuildTime = newProperty(Flags.READONLY, BAbsTime.DEFAULT,null);
  
  /**
   * Get the <code>lastCacheRebuildTime</code> property.
   * @see nhaystack.server.BNHaystackStats#lastCacheRebuildTime
   */
  public BAbsTime getLastCacheRebuildTime() { return (BAbsTime)get(lastCacheRebuildTime); }
  
  /**
   * Set the <code>lastCacheRebuildTime</code> property.
   * @see nhaystack.server.BNHaystackStats#lastCacheRebuildTime
   */
  public void setLastCacheRebuildTime(BAbsTime v) { set(lastCacheRebuildTime,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackStats.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
}
