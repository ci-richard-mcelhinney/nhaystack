//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.worker;

import java.util.logging.*;

import javax.baja.status.*;
import javax.baja.util.*;

/**
  * A Worker Invocation is a WorkerChore that invokes an action
  * via a javax.baja.util.Invocation
  */
public class WorkerInvocation extends WorkerChore
{
    public WorkerInvocation(BNHaystackWorker worker, String name, Invocation invocation) 
    { 
        super(worker, name);
        this.invocation = invocation;
    }

    public void doRun() throws Exception
    {
        BStatus status = worker.getWorkerParent().getStatus();
        if (status.isDisabled() || status.isFault() || status.isDown())
            return;

        invocation.run();
    }

    public boolean merge(WorkerChore chore) { return false; }

    public boolean isPing() { return false; }

    protected final Logger getLogger() { return LOG; }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack");

    protected final Invocation invocation;
}
