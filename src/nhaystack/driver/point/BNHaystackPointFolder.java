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

/**
  * BNHaystackPointFolder organizes haystack points underneath a
  * BNHaystackPointDeviceExt.
  */
public class BNHaystackPointFolder extends BPointFolder
{
    /*-
    class BNHaystackPointFolder
    {
        properties
        {
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackPointFolder(2820132054)1.0$ @*/
/* Generated Tue May 30 17:08:42 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackPointFolder.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public final BNHaystackNetwork getNHaystackNetwork()
    {
        return (BNHaystackNetwork)getNetwork();
    }

    public final BNHaystackServer getNHaystackServer()
    {
        return (BNHaystackServer) getDevice();
    }

    public boolean isParentLegal(BComponent comp)
    {
        return 
            (comp instanceof BNHaystackPointDeviceExt) ||
            (comp instanceof BNHaystackPointFolder);
//            (comp instanceof BNHaystackPointFolder) ||
//            (comp instanceof BNHaystackNetwork);
    }
}
