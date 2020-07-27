//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.worker;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatus;
import javax.baja.sys.BComponent;
import javax.baja.sys.Context;
import javax.baja.sys.NotRunningException;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BThreadPoolWorker;
import javax.baja.util.Queue;
import javax.baja.util.ThreadPoolWorker;
import javax.baja.util.Worker;

/**
  * BNHaystackThreadPoolWorker is a BWorker that serves nhaystack.
  */

@NiagaraType
/**
 * the size of the queue
 */
@NiagaraProperty(
  name = "maxQueueSize",
  type = "int",
  defaultValue = "5000"
)
public class BNHaystackThreadPoolWorker
    extends BThreadPoolWorker
    implements BINHaystackWorker
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.worker.BNHaystackThreadPoolWorker(1643292908)1.0$ @*/
/* Generated Mon Nov 20 09:47:23 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "maxQueueSize"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code maxQueueSize} property.
   * the size of the queue
   * @see #getMaxQueueSize
   * @see #setMaxQueueSize
   */
  public static final Property maxQueueSize = newProperty(0, 5000, null);
  
  /**
   * Get the {@code maxQueueSize} property.
   * the size of the queue
   * @see #maxQueueSize
   */
  public int getMaxQueueSize() { return getInt(maxQueueSize); }
  
  /**
   * Set the {@code maxQueueSize} property.
   * the size of the queue
   * @see #maxQueueSize
   */
  public void setMaxQueueSize(int v) { setInt(maxQueueSize, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackThreadPoolWorker.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

////////////////////////////////////////////////////////////////
// BComponent
////////////////////////////////////////////////////////////////

    @Override
    public void changed(Property property, Context context)
    {
        super.changed(property, context);

        if (!isRunning())
            return;

        if (property.equals(maxQueueSize))
        {
            if (queue != null)
            {
                stopWorker();
                queue = new Queue(getMaxQueueSize());
                worker = new ThreadPoolWorker(queue);
                startWorker();
            }
        }
    }

    @Override
    public boolean isParentLegal(BComponent parent)
    {
        return parent instanceof BINHaystackWorkerParent;
    }

////////////////////////////////////////////////////////////////
// BWorker
////////////////////////////////////////////////////////////////

    @Override
    public synchronized Worker getWorker()
    {
        if (worker == null)
        {
            queue = new Queue(getMaxQueueSize());
            worker = new ThreadPoolWorker(queue);
        }
        return worker;
    }

    public int getSize()
    {
        return queue.size();
    }

    public int getMaxSize()
    {
        return queue.maxSize();
    }

    @Override
    protected String getWorkerThreadName()
    {
        return "NHaystackThreadPoolWorker:" + ((BComponent) getParent()).getSlotPath();
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    @Override
    public BINHaystackWorkerParent getWorkerParent()
    {
        return (BINHaystackWorkerParent) getParent();
    }

    @Override
    public synchronized void enqueueChore(WorkerChore chore)
    {
        if (!isRunning() || queue == null)
          throw new NotRunningException();

        BStatus status = getWorkerParent().getStatus();
        Logger log = chore.getLogger();

        if (status.isDisabled() || status.isFault())
        {
            if (log.isLoggable(Level.FINE))
                log.fine("Pool Chore IGNORE " + chore + " -- " + status);
            return;
        }

        // if we are 'down', then all chores except pings will be ignored
        if (status.isDown() && !chore.isPing())
        {
            if (log.isLoggable(Level.FINE))
                log.fine("Pool Chore IGNORE " + chore + " -- " + status);
            return;
        }

        if (queue.isEmpty())
        {
            if (log.isLoggable(Level.FINE))
                log.fine("Pool Chore ENQUEUE " + chore);
            queue.enqueue(chore);
        }
        else
        {
            // Note that the tail is the NEWEST entry.
            WorkerChore tail = (WorkerChore) queue.tail();

            // Attempt to merge the chore into the tail.
            if (tail != null && tail.merge(chore))
            {
                // merge succeeded
                if (log.isLoggable(Level.FINE))
                    log.fine("Pool Chore MERGE " + chore);
            }
            // else the merge did not happen, so enqueue the chore
            else
            {
                if (log.isLoggable(Level.FINE))
                    log.fine("Pool Chore ENQUEUE " + chore);
                queue.enqueue(chore);
            }
        }
    }
  
////////////////////////////////////////////////////////////////
// attributes 
////////////////////////////////////////////////////////////////

    private Queue queue;
    private ThreadPoolWorker worker;
}
