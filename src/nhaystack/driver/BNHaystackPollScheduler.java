//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation
//
package nhaystack.driver;

import javax.baja.driver.util.*;
import javax.baja.sys.*;

import nhaystack.driver.worker.*;

public class BNHaystackPollScheduler 
    extends BPollScheduler
{
    /*-
    class BNHaystackPollScheduler
    {
        properties
        {
        }
    }
  -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BNHaystackPollScheduler(1582072976)1.0$ @*/
/* Generated Mon Apr 07 12:15:44 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackPollScheduler.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void doPoll(BIPollable pollable) throws Exception
    {
        BNHaystackNetwork network = (BNHaystackNetwork) getParent();
        if (network.isDisabled() || network.isDown() || network.isFault())
            return;

        BNHaystackServer server = (BNHaystackServer) pollable; 
        server.postAsyncChore(new PollChore(server));
    }
}
