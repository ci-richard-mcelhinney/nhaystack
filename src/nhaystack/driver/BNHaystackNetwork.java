//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver;

import javax.baja.driver.*;
import javax.baja.sys.*;
import javax.baja.util.*;

public class BNHaystackNetwork extends BDeviceNetwork
{
    /*-
    class BNHaystackNetwork
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BNHaystackNetwork(3549694058)1.0$ @*/
/* Generated Fri Apr 04 07:54:00 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackNetwork.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public Type getDeviceFolderType()
    {
        return BNHaystackServerFolder.TYPE;
    }

    public Type getDeviceType()
    {
        return BNHaystackServer.TYPE;
    }

    public boolean isParentLegal(BComponent comp)
    {
        return (comp instanceof BDriverContainer);
    }
}
