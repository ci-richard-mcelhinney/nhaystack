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
  * BNHaystackWorker is a BWorker that serves nhaystack.
  */
public class BNHaystackWorker
  extends BWorker
{
    /*-

    class BNHaystackWorker
    {
        properties
        {
            maxQueueSize: int
                -- the size of the queue
                default {[ 256 ]}
        }
    }

    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.worker.BNHaystackWorker(319417993)1.0$ @*/
/* Generated Thu Dec 26 12:05:12 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "maxQueueSize"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>maxQueueSize</code> property.
   * the size of the queue
   * @see nhaystack.worker.BNHaystackWorker#getMaxQueueSize
   * @see nhaystack.worker.BNHaystackWorker#setMaxQueueSize
   */
  public static final Property maxQueueSize = newProperty(0, 256,null);
  
  /**
   * Get the <code>maxQueueSize</code> property.
   * @see nhaystack.worker.BNHaystackWorker#maxQueueSize
   */
  public int getMaxQueueSize() { return getInt(maxQueueSize); }
  
  /**
   * Set the <code>maxQueueSize</code> property.
   * @see nhaystack.worker.BNHaystackWorker#maxQueueSize
   */
  public void setMaxQueueSize(int v) { setInt(maxQueueSize,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackWorker.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

////////////////////////////////////////////////////////////////
// BComponent
////////////////////////////////////////////////////////////////

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
                worker = new Worker(queue);
                startWorker();
            }
        }
    }

    public boolean isParentLegal(BComponent parent)
    {
        return (parent instanceof BINHaystackWorkerParent);
    }

////////////////////////////////////////////////////////////////
// BWorker
////////////////////////////////////////////////////////////////

    public synchronized Worker getWorker()
    {
        if (worker == null)
        {
            queue = new Queue(getMaxQueueSize());
            worker = new Worker(queue);
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

    protected String getWorkerThreadName()
    {
        return "NHaystackWorker:" + ((BComponent) getParent()).getSlotPath();
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    public BINHaystackWorkerParent getWorkerParent()
    {
        return (BINHaystackWorkerParent) getParent();
    }

    public synchronized void enqueueChore(WorkerChore chore)
    {
        if (!isRunning() || queue == null)
          throw new NotRunningException();

        BStatus status = getWorkerParent().getStatus();

        if (status.isDisabled() || status.isFault())
        {
            if (LOG.isTraceOn()) LOG.trace("Chore IGNORE " + chore + " -- " + status);
            return;
        }

        // if we are 'down', then all chores except pings will be ignored
        if (status.isDown() && !chore.isPing())
        {
            if (LOG.isTraceOn()) LOG.trace("Chore IGNORE " + chore + " -- " + status);
            return;
        }

        if (queue.size() == 0)
        {
            if (LOG.isTraceOn()) LOG.trace("Chore ENQUEUE " + chore);
            queue.enqueue(chore);
        }
        else
        {
            // Note that the tail is the NEWEST entry.
            WorkerChore tail = (WorkerChore) queue.tail();

            // Attempt to merge the chore into the tail.
            if (tail.merge(chore))
            {
                // merge succeeded
                if (LOG.isTraceOn()) LOG.trace("Chore MERGE " + chore);
            }
            // else the merge did not happen, so enqueue the chore
            else
            {
                if (LOG.isTraceOn()) LOG.trace("Chore ENQUEUE " + chore);
                queue.enqueue(chore);
            }
        }
    }
  
////////////////////////////////////////////////////////////////
// attributes 
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private Queue queue = null;
    private Worker worker = null;
}
