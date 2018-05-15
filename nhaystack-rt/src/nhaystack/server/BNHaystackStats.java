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
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BStruct;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

@NiagaraType
@NiagaraProperty(
  name = "numPoints",
  type = "int",
  defaultValue = "0",
  flags = Flags.READONLY
)
@NiagaraProperty(
  name = "numEquips",
  type = "int",
  defaultValue = "0",
  flags = Flags.READONLY
)
@NiagaraProperty(
  name = "numSites",
  type = "int",
  defaultValue = "0",
  flags = Flags.READONLY
)
@NiagaraProperty(
  name = "lastCacheRebuildDuration",
  type = "BRelTime",
  defaultValue = "BRelTime.DEFAULT",
  flags = Flags.READONLY
)
@NiagaraProperty(
  name = "lastCacheRebuildTime",
  type = "BAbsTime",
  defaultValue = "BAbsTime.DEFAULT",
  flags = Flags.READONLY
)
public class BNHaystackStats extends BStruct
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackStats(1940082169)1.0$ @*/
/* Generated Sat Nov 18 18:39:29 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "numPoints"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code numPoints} property.
   * @see #getNumPoints
   * @see #setNumPoints
   */
  public static final Property numPoints = newProperty(Flags.READONLY, 0, null);
  
  /**
   * Get the {@code numPoints} property.
   * @see #numPoints
   */
  public int getNumPoints() { return getInt(numPoints); }
  
  /**
   * Set the {@code numPoints} property.
   * @see #numPoints
   */
  public void setNumPoints(int v) { setInt(numPoints, v, null); }

////////////////////////////////////////////////////////////////
// Property "numEquips"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code numEquips} property.
   * @see #getNumEquips
   * @see #setNumEquips
   */
  public static final Property numEquips = newProperty(Flags.READONLY, 0, null);
  
  /**
   * Get the {@code numEquips} property.
   * @see #numEquips
   */
  public int getNumEquips() { return getInt(numEquips); }
  
  /**
   * Set the {@code numEquips} property.
   * @see #numEquips
   */
  public void setNumEquips(int v) { setInt(numEquips, v, null); }

////////////////////////////////////////////////////////////////
// Property "numSites"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code numSites} property.
   * @see #getNumSites
   * @see #setNumSites
   */
  public static final Property numSites = newProperty(Flags.READONLY, 0, null);
  
  /**
   * Get the {@code numSites} property.
   * @see #numSites
   */
  public int getNumSites() { return getInt(numSites); }
  
  /**
   * Set the {@code numSites} property.
   * @see #numSites
   */
  public void setNumSites(int v) { setInt(numSites, v, null); }

////////////////////////////////////////////////////////////////
// Property "lastCacheRebuildDuration"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code lastCacheRebuildDuration} property.
   * @see #getLastCacheRebuildDuration
   * @see #setLastCacheRebuildDuration
   */
  public static final Property lastCacheRebuildDuration = newProperty(Flags.READONLY, BRelTime.DEFAULT, null);
  
  /**
   * Get the {@code lastCacheRebuildDuration} property.
   * @see #lastCacheRebuildDuration
   */
  public BRelTime getLastCacheRebuildDuration() { return (BRelTime)get(lastCacheRebuildDuration); }
  
  /**
   * Set the {@code lastCacheRebuildDuration} property.
   * @see #lastCacheRebuildDuration
   */
  public void setLastCacheRebuildDuration(BRelTime v) { set(lastCacheRebuildDuration, v, null); }

////////////////////////////////////////////////////////////////
// Property "lastCacheRebuildTime"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code lastCacheRebuildTime} property.
   * @see #getLastCacheRebuildTime
   * @see #setLastCacheRebuildTime
   */
  public static final Property lastCacheRebuildTime = newProperty(Flags.READONLY, BAbsTime.DEFAULT, null);
  
  /**
   * Get the {@code lastCacheRebuildTime} property.
   * @see #lastCacheRebuildTime
   */
  public BAbsTime getLastCacheRebuildTime() { return (BAbsTime)get(lastCacheRebuildTime); }
  
  /**
   * Set the {@code lastCacheRebuildTime} property.
   * @see #lastCacheRebuildTime
   */
  public void setLastCacheRebuildTime(BAbsTime v) { set(lastCacheRebuildTime, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackStats.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
}
