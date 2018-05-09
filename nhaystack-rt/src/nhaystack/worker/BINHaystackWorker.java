//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations

package nhaystack.worker;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BInterface;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

/**
  * BINHaystackWorker is the parent of a BNHaystackWorker
  */
@NiagaraType
public interface BINHaystackWorker extends BInterface
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.worker.BINHaystackWorker(2979906276)1.0$ @*/
/* Generated Mon Nov 20 09:44:58 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  Type TYPE = Sys.loadType(BINHaystackWorker.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    BINHaystackWorkerParent getWorkerParent();
    void enqueueChore(WorkerChore chore);
}
