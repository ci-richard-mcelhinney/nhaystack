//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.history;

import javax.baja.driver.history.*;
import javax.baja.sys.*;
import nhaystack.driver.*;

public class BNHaystackHistoryDeviceExt extends BHistoryDeviceExt
{
    /*-
    class BNHaystackHistoryDeviceExt
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.BNHaystackHistoryDeviceExt(2279933918)1.0$ @*/
/* Generated Fri Apr 04 07:54:01 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
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

    public Type getImportDescriptorType()
    {
        return BNHaystackHistoryImport.TYPE;
    }
}
