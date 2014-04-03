/**
  * Copyright (c) 2012 All Right Reserved, J2 Innovations
  */
package nhaystack.driver;

import javax.baja.status.*;
import javax.baja.util.*;

import nhaystack.worker.*;

public final class PingInvocation extends WorkerChore
{
    public PingInvocation(BNHaystackWorker worker, String name, Invocation invocation) 
    { 
        super(worker, name);
        this.invocation = invocation;
    }

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
    public boolean merge(WorkerChore chore)
    {
        return (chore instanceof PingInvocation);
    }

    public boolean isPing() { return true; }

    private final Invocation invocation;
}
