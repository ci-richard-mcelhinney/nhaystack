//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.worker;

import java.util.logging.Logger;
import javax.baja.status.BStatus;
import javax.baja.util.Invocation;

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

    @Override
    public void doRun() throws Exception
    {
        BStatus status = worker.getWorkerParent().getStatus();
        if (status.isDisabled() || status.isFault() || status.isDown())
            return;

        invocation.run();
    }

    @Override
    public boolean merge(WorkerChore chore) { return false; }

    @Override
    public boolean isPing() { return false; }

    @Override
    protected final Logger getLogger() { return LOG; }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack");

    protected final Invocation invocation;
}
