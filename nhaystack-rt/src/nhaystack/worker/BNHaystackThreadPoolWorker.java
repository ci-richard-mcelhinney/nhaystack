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
  * BNHaystackThreadPoolWorker is a BWorker that serves nhaystack.
  */
public class BNHaystackThreadPoolWorker
    extends BThreadPoolWorker
    implements BINHaystackWorker
{
    /*-

    class BNHaystackThreadPoolWorker
    {
        properties
        {
            maxQueueSize: int
                -- the size of the queue
                default {[ 5000 ]}
        }
    }

    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.worker.BNHaystackThreadPoolWorker(2268244792)1.0$ @*/
/* Generated Sat Apr 25 09:05:06 EDT 2015 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "maxQueueSize"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>maxQueueSize</code> property.
   * the size of the queue
   * @see nhaystack.worker.BNHaystackThreadPoolWorker#getMaxQueueSize
   * @see nhaystack.worker.BNHaystackThreadPoolWorker#setMaxQueueSize
   */
  public static final Property maxQueueSize = newProperty(0, 5000,null);
  
  /**
   * Get the <code>maxQueueSize</code> property.
   * the size of the queue
   * @see nhaystack.worker.BNHaystackThreadPoolWorker#maxQueueSize
   */
  public int getMaxQueueSize() { return getInt(maxQueueSize); }
  
  /**
   * Set the <code>maxQueueSize</code> property.
   * the size of the queue
   * @see nhaystack.worker.BNHaystackThreadPoolWorker#maxQueueSize
   */
  public void setMaxQueueSize(int v) { setInt(maxQueueSize,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackThreadPoolWorker.class);

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
                worker = new ThreadPoolWorker(queue);
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

    protected String getWorkerThreadName()
    {
        return "NHaystackThreadPoolWorker:" + ((BComponent) getParent()).getSlotPath();
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
        Log log = chore.getLog();

        if (status.isDisabled() || status.isFault())
        {
            if (log.isTraceOn()) 
                log.trace("Pool Chore IGNORE " + chore + " -- " + status);
            return;
        }

        // if we are 'down', then all chores except pings will be ignored
        if (status.isDown() && !chore.isPing())
        {
            if (log.isTraceOn()) 
                log.trace("Pool Chore IGNORE " + chore + " -- " + status);
            return;
        }

        if (queue.size() == 0)
        {
            if (log.isTraceOn()) 
                log.trace("Pool Chore ENQUEUE " + chore);
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
                if (log.isTraceOn()) 
                    log.trace("Pool Chore MERGE " + chore);
            }
            // else the merge did not happen, so enqueue the chore
            else
            {
                if (log.isTraceOn()) 
                    log.trace("Pool Chore ENQUEUE " + chore);
                queue.enqueue(chore);
            }
        }
    }
  
////////////////////////////////////////////////////////////////
// attributes 
////////////////////////////////////////////////////////////////

    private Queue queue = null;
    private ThreadPoolWorker worker = null;
}
