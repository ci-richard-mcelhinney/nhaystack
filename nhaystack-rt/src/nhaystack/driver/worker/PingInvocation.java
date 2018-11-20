//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Added missing @Overrides annotations

package nhaystack.driver.worker;

import javax.baja.status.BStatus;
import javax.baja.util.Invocation;
import nhaystack.worker.BNHaystackThreadPoolWorker;
import nhaystack.worker.WorkerChore;

/**
  * PingInvocation handles pinging a BNHaystackServer
  */
public final class PingInvocation extends DriverChore
{
    public PingInvocation(BNHaystackThreadPoolWorker worker, String name, Invocation invocation) 
    { 
        super(worker, name);
        this.invocation = invocation;
    }

    @Override
    public void doRun()
    {
        BStatus status = worker.getWorkerParent().getStatus();
        if (status.isDisabled() || status.isFault())
            return;

        invocation.run();
    }

    /**
      * Pings are merged by just ignoring new Ping requests
      */
    @Override
    public boolean merge(WorkerChore chore)
    {
        return chore instanceof PingInvocation;
    }

    @Override
    public boolean isPing() { return true; }

    private final Invocation invocation;
}
