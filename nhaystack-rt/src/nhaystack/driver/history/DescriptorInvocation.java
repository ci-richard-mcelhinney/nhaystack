//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   07 May 2018  Eric Anderson  Added missing @Overrides annotations

package nhaystack.driver.history;

import java.util.logging.Logger;
import javax.baja.driver.util.BDescriptor;
import javax.baja.status.BStatus;
import javax.baja.sys.BComponent;
import javax.baja.util.Invocation;
import nhaystack.worker.BNHaystackWorker;
import nhaystack.worker.WorkerChore;

/**
  * A DescriptorInvocation is a WorkerChore that invokes an action
  * on a BDescriptor.  It is almost the same as a WorkerInvocation,
  * except that if the status is disabled, fault, or down, then
  * the descriptor has executeFail() called on it.
  */
public class DescriptorInvocation extends WorkerChore
{
    public DescriptorInvocation(
        BNHaystackWorker worker, String name, 
        BDescriptor descriptor,
        Invocation invocation) 
    { 
        super(worker, name);

        this.descriptor = descriptor;
        this.invocation = invocation;
    }

    @Override
    public void doRun() throws Exception
    {
        BStatus status = worker.getWorkerParent().getStatus();
        if (status.isDisabled() || status.isFault() || status.isDown())
        {
            // begin progress just to fake out the state machine
            descriptor.executeInProgress();

            // call executeFail() on the descriptor.  this will keep the
            // descriptor from getting stuck in 'pending'
            BComponent parent = (BComponent) worker.getWorkerParent();
            descriptor.executeFail(parent.getSlotPath() + " has status " + status);
            return;
        }

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

    private static final Logger LOG = Logger.getLogger("nhaystack.driver");

    protected final BDescriptor descriptor;
    protected final Invocation invocation;
}
