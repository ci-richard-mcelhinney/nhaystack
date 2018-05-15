//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//
package nhaystack.driver;

import javax.baja.driver.util.BIPollable;
import javax.baja.driver.util.BPollScheduler;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.driver.worker.PollChore;

/**
  * BNHaystackPollScheduler schedules polling for BNHaystackServer instances.
  */
@NiagaraType
public class BNHaystackPollScheduler 
    extends BPollScheduler
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BNHaystackPollScheduler(2979906276)1.0$ @*/
/* Generated Sat Nov 18 17:55:32 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackPollScheduler.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    public void doPoll(BIPollable pollable) throws Exception
    {
        BNHaystackNetwork network = (BNHaystackNetwork) getParent();
        if (network.isDisabled() || network.isDown() || network.isFault())
            return;

        BNHaystackServer server = (BNHaystackServer) pollable; 
        server.postAsyncChore(new PollChore(server));
    }
}
