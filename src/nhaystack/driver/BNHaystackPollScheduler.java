/**
  * Copyright (c) 2012 All Right Reserved, J2 Innovations
  */
package nhaystack.driver;

import javax.baja.driver.*;
import javax.baja.driver.util.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.net.*;
import javax.baja.sys.*;
import javax.baja.util.*;

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
        server.poll();
    }
}
