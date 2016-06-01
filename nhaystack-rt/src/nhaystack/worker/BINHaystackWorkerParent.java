//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.worker;

import javax.baja.status.*;
import javax.baja.sys.*;

import org.projecthaystack.client.*;

/**
  * BINHaystackWorkerParent is the parent of a BNHaystackWorker
  */
public interface BINHaystackWorkerParent extends BInterface
{
    public static final Type TYPE = Sys.loadType(BINHaystackWorkerParent.class);

    /**
      * Handle a network exception that occured when running a chore.
      */
    public void handleNetworkException(WorkerChore chore, CallNetworkException e);

    public BStatus getStatus();
}
