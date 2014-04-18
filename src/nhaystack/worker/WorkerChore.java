//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.worker;

import javax.baja.log.*;
import javax.baja.sys.*;

import org.projecthaystack.client.*;

/**
  * A WorkerChore is a Runnable that is used to post work to a
  * BNHaystackWorker instance.
  */
public abstract class WorkerChore implements Runnable
{
    public WorkerChore(BNHaystackWorker worker, String name) 
    { 
        this.worker = worker;
        this.name = name; 
    }

    public String toString()
    {
        return name;
    }

    public final void run()
    {
        long begin = Clock.ticks();
        if (LOG.isTraceOn())
            LOG.trace("Chore BEGIN " + toString());

        try
        {
            doRun();
        }
        catch (Exception e)
        {
            if (e instanceof CallNetworkException)
                worker.getWorkerParent().handleNetworkException(
                    this, (CallNetworkException) e);
            else
                throw new BajaRuntimeException(e);
        }
        finally
        {
            if (LOG.isTraceOn())
            {
                long end = Clock.ticks();
                LOG.trace("Chore END " + toString() + " (" + (end-begin) + "ms)");
            }
        }
    }

    /**
      * Run the chore.  
      */
    protected abstract void doRun() throws Exception;

    /**
      * Return true if this chore was able to succesfully combine
      * itself with the chore that was passed in.
      */
    public abstract boolean merge(WorkerChore chore);

    /**
      * Is this chore performing a ping on a BIPingable?
      */
    public abstract boolean isPing();

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    public final BNHaystackWorker worker;
    public final String name;

    private static final Log LOG = Log.getLog("nhaystack");
}
