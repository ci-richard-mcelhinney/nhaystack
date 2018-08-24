//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   07 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.driver.history;

import javax.baja.driver.history.BHistoryDeviceExt;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.driver.BNHaystackNetwork;
import nhaystack.driver.BNHaystackServer;

/**
  * BNHaystackHistoryDeviceExt maps haystack history data
  * into Baja histories.
  */
@NiagaraType
public class BNHaystackHistoryDeviceExt extends BHistoryDeviceExt
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.BNHaystackHistoryDeviceExt(2979906276)1.0$ @*/
/* Generated Fri Nov 17 11:49:32 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackHistoryDeviceExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  public final BNHaystackNetwork getNHaystackNetwork()
  {
    return (BNHaystackNetwork)getNetwork();
  }

  public final BNHaystackServer getHaystackServer()
  {
    return (BNHaystackServer) getDevice();
  }

  @Override
  public Type getImportDescriptorType()
  {
    return BNHaystackHistoryImport.TYPE;
  }

  public Type getExportDescriptorType()
  {
    return BNHaystackHistoryExport.TYPE;
  }
}
