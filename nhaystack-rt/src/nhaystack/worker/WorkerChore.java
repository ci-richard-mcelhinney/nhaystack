//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.worker;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Clock;
import org.projecthaystack.client.CallNetworkException;

/**
  * A WorkerChore is a Runnable that is used to post work to a
  * BNHaystackWorker instance.
  */
public abstract class WorkerChore implements Runnable
{
    protected WorkerChore(BINHaystackWorker worker, String name)
    { 
        this.worker = worker;
        this.name = name; 
    }

    public String toString()
    {
        return name;
    }

    @Override
    public final void run()
    {
        long begin = Clock.ticks();
        if (getLogger().isLoggable(Level.FINE))
            getLogger().fine("Chore BEGIN " + toString());

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
            if (getLogger().isLoggable(Level.FINE))
            {
                long end = Clock.ticks();
                getLogger().fine("Chore END " + toString() + " (" + (end-begin) + "ms)");
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

    /**
      * get the Log
      */
    protected abstract Logger getLogger();

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    public final BINHaystackWorker worker;
    public final String name;
}
