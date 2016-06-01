//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver;

import javax.baja.driver.*;
import javax.baja.sys.*;

/**
  * BNHaystackServerFolder is used to organize BNHaystackServer instances
  * underneath a BNHaystackNetwork.
  */
public class BNHaystackServerFolder extends BDeviceFolder
{
    /*-
    class BNHaystackServerFolder
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $j2inn.nhaystack.driver.BNHaystackServerFolder(3322669339)1.0$ @*/
/* Generated Tue Dec 24 13:41:23 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackServerFolder.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public boolean isParentLegal(BComponent comp)
    {
        return 
            (comp instanceof BNHaystackNetwork) || 
            (comp instanceof BNHaystackServerFolder);
    }
}
