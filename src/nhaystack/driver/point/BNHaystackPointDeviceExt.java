//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.point;

import javax.baja.driver.point.*;
import javax.baja.sys.*;

import nhaystack.driver.*;

public class BNHaystackPointDeviceExt extends BPointDeviceExt
{
    /*-
    class BNHaystackPointDeviceExt
    {
        properties
        {
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackPointDeviceExt(306764068)1.0$ @*/
/* Generated Tue May 30 17:08:42 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackPointDeviceExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public final BNHaystackNetwork getNHaystackNetwork()
    {
        return (BNHaystackNetwork) getNetwork();
    }

    public final BNHaystackServer getNHaystackServer()
    {
        return (BNHaystackServer) getDevice();
    }

    public Type getDeviceType()
    {
        return BNHaystackServer.TYPE;
    }

    public Type getPointFolderType()
    {
        return BNHaystackPointFolder.TYPE;
    }

    public Type getProxyExtType()
    {
        return BNHaystackProxyExt.TYPE;
    }

}
