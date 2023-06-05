//
// Copyright (c) 2018, VRT Systems
//
// Based on BNHaystackHistoryEntry
// Copyright (c) 2012, J2 Innovations
//
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Apr 2018  Stuart Longland  Creation based on BNHaystackHistoryEntry

package nhaystack.driver.history.learn;

import javax.baja.history.*;
import javax.baja.sys.*;

import nhaystack.*;
import nhaystack.driver.*;
import nhaystack.driver.history.*;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;

/**
  * BNHaystackHistoryExpEntry represents an object that was discovered
  * during a 'learn' in the BNHaystackHistoryExportManager.
  */
@NiagaraType
@NiagaraProperty(
  name = "tz",
  type = "BHTimeZone",
  defaultValue = "BHTimeZone.DEFAULT"
)
@NiagaraProperty(
  name = "historyId",
  type = "BHistoryId",
  defaultValue = "BHistoryId.DEFAULT"
)
public final class BNHaystackHistoryExpEntry extends BComponent
{

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.learn.BNHaystackHistoryExpEntry(1886900171)1.0$ @*/
/* Generated Tue May 01 17:08:39 AEST 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "tz"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code tz} property.
   * @see #getTz
   * @see #setTz
   */
  public static final Property tz = newProperty(0, BHTimeZone.DEFAULT, null);

  /**
   * Get the {@code tz} property.
   * @see #tz
   */
  public BHTimeZone getTz() { return (BHTimeZone)get(tz); }

  /**
   * Set the {@code tz} property.
   * @see #tz
   */
  public void setTz(BHTimeZone v) { set(tz, v, null); }

////////////////////////////////////////////////////////////////
// Property "historyId"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code historyId} property.
   * @see #getHistoryId
   * @see #setHistoryId
   */
  public static final Property historyId = newProperty(0, BHistoryId.DEFAULT, null);

  /**
   * Get the {@code historyId} property.
   * @see #historyId
   */
  public BHistoryId getHistoryId() { return (BHistoryId)get(historyId); }

  /**
   * Set the {@code historyId} property.
   * @see #historyId
   */
  public void setHistoryId(BHistoryId v) { set(historyId, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackHistoryExpEntry.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  public boolean is(BComponent component)
  {
    boolean res = false;

    if (component instanceof BNHaystackHistoryExport)
    {
      BNHaystackHistoryExport exp = (BNHaystackHistoryExport) component;

      if (exp.getHistoryId().equals(getHistoryId()))
        res = true;
    }

    return res;
  }
}
