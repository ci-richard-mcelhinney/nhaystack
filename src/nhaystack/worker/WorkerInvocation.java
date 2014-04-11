/**
  * Copyright (c) 2012 All Right Reserved, J2 Innovations
  */
package nhaystack.worker;

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

    protected final Invocation invocation;
}
