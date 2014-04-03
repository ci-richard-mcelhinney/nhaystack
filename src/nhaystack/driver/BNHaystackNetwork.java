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
/*@ $j2inn.nhaystack.driver.BNHaystackNetwork(480915566)1.0$ @*/
/* Generated Tue Dec 24 13:45:40 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
