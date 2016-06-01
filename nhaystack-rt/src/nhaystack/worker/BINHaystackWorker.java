//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.worker;

import javax.baja.log.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.util.*;

/**
  * BINHaystackWorker is the parent of a BNHaystackWorker
  */
public interface BINHaystackWorker extends BInterface
{
    public static final Type TYPE = Sys.loadType(BINHaystackWorker.class);

    public BINHaystackWorkerParent getWorkerParent();

    public void enqueueChore(WorkerChore chore);
}
