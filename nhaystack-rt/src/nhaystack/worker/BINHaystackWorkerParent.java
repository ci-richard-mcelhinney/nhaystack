//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations

package nhaystack.worker;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatus;
import javax.baja.sys.BInterface;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import org.projecthaystack.client.CallNetworkException;

/**
  * BINHaystackWorkerParent is the parent of a BNHaystackWorker
  */
@NiagaraType
public interface BINHaystackWorkerParent extends BInterface
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.worker.BINHaystackWorkerParent(2979906276)1.0$ @*/
/* Generated Mon Nov 20 09:46:27 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  Type TYPE = Sys.loadType(BINHaystackWorkerParent.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
      * Handle a network exception that occured when running a chore.
      */
    void handleNetworkException(WorkerChore chore, CallNetworkException e);

    BStatus getStatus();
}
