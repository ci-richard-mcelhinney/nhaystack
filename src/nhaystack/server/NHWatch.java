//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   30 Mar 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;

import javax.baja.control.*;
import javax.baja.log.*;
import javax.baja.schedule.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;

/**
  * NHWatch manages leased components
  */
class NHWatch extends HWatch
{
    NHWatch(NHServer server, String dis, long leaseInterval)
    {
        this.server = server;
        this.dis = dis;
        this.watchId = BUuid.make().toString();
        this.leaseInterval = leaseInterval; 
        this.open = true;

        scheduleLeaseTimeout();
    }

    public String toString()
    {
        return "[NHWatch " +
            "dis:'" + dis + "', " +
            "watchId:'" + watchId + "', " +
            "leaseInterval:" + leaseInterval + "]";
    }

    /**
     * Unique watch identifier within a project database.
     */
    public String id()
    {
        return watchId;
    }

    /**
     * Debug display string used during "HProj.watchOpen"
     */
    public String dis()
    {
        return dis;
    }

    /**
     * Lease period or null if watch has not been opened yet.
     */
    public HNum lease()
    {
        return HNum.make(leaseInterval, "ms");
    }

    /**
     * Add a list of records to the subscription list and return their
     * current representation.  If checked is true and any one of the
     * ids cannot be resolved then raise UnknownRecException for first id
     * not resolved.  If checked is false, then each id not found has a
     * row where every cell is null.
     * <p>
     * The HGrid that is returned must contain metadata entries 
     * for 'watchId' and 'lease'.
     */
    public synchronized HGrid sub(HRef[] ids, boolean checked)
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");

        long ticks = Clock.ticks();
        if (LOG.isTraceOn())
            LOG.trace("NHWatch.sub begin " + watchId + ", length " + ids.length);

        lastPoll = System.currentTimeMillis();
        scheduleLeaseTimeout();

        HDict meta = new HDictBuilder()
            .add("watchId", HStr.make(id()))
            .add("lease", lease())
            .toDict();

        Array response = new Array(HDict.class);
        Array pointArr = new Array(BComponent.class);
        for (int i = 0; i < ids.length; i++)
        {
            HRef id = (HRef) ids[i];

            try
            {
                BComponent comp = server.getTagManager().lookupComponent(id);

                // no such component -- treat 'checked' as if it were false, since
                // 'checked' is handled on the client side.
                //
                // we also ignore anything that's not a control point or schedule.
                //
                if ((comp == null) || !((comp instanceof BControlPoint) || (comp instanceof BWeeklySchedule)))
                {
                    if (LOG.isTraceOn())
                        LOG.warning("NHWatch.sub " + watchId + " cannot subscribe to " + id);

                    response.add(null);
                }
                // found
                else
                {
                    pointArr.add(comp);
                    HDict cov = server.getTagManager().createComponentCovTags(comp);
                    allSubscribed.put(comp, cov);
                    response.add(cov);
                }
            }
            catch (Exception e)
            {
                LOG.warning("NHWatch.sub " + watchId + " cannot subscribe to " + id +
                    ": " + e.getMessage());
                response.add(null);
            }
        }

        subscriber.subscribe((BComponent[]) pointArr.trim(), 0, null);

        HGrid grid = HGridBuilder.dictsToGrid(meta, (HDict[]) response.trim());

        if (LOG.isTraceOn())
            LOG.trace("NHWatch.sub end   " + watchId + ", length " + ids.length + ", " +
                (Clock.ticks()-ticks) + "ms.");

        return grid;
    }

    /**
     * Remove a list of records from watch.  Silently ignore
     * any invalid ids.
     */
    public synchronized void unsub(HRef[] ids)
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");

        if (LOG.isTraceOn())
            LOG.trace("NHWatch.unsub " + watchId + ", length " + ids.length);

        scheduleLeaseTimeout();

        Array pointArr = new Array(BComponent.class);
        for (int i = 0; i < ids.length; i++)
        {
            HRef id = (HRef) ids[i];

            BComponent comp = server.getTagManager().lookupComponent(id);
            if ((comp != null) && allSubscribed.containsKey(comp))
            {
                if (LOG.isTraceOn())
                    LOG.trace("NHWatch.unsub " + watchId + " unsubscribe " + id);

                pointArr.add(comp);
                allSubscribed.remove(comp);
                nextPoll.remove(comp);
            }
        }

        // unsubscribe
        BComponent[] points = (BComponent[]) pointArr.trim();
        subscriber.unsubscribe(points, null);
    }

    /**
     * Poll for any changes to the subscriptions records.
     * This returns only the id, curVal and curStatus tags for each point.
     */
    public synchronized HGrid pollChanges()
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");

        if (LOG.isTraceOn())
            LOG.trace("NHWatch.pollChanges begin " + watchId);

        lastPoll = System.currentTimeMillis();
        scheduleLeaseTimeout();

        // create a response from all the COV values in nextPoll
        Array response = new Array(HDict.class);
        Iterator itr = nextPoll.values().iterator();
        while (itr.hasNext())
            response.add(itr.next());

        // clear out nextPoll so we can start accumulating more COVs
        nextPoll.clear();

        // done
        if (LOG.isTraceOn())
            LOG.trace("NHWatch.pollChanges end   " + watchId + ", size " + response.size());
        return HGridBuilder.dictsToGrid((HDict[]) response.trim());
    }

    /**
     * Poll all the subscriptions records even if there have been no changes.
     * This returns all of the tags for each point.
     */
    public synchronized HGrid pollRefresh()
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");

        if (LOG.isTraceOn())
            LOG.trace("NHWatch.pollRefresh begin " + watchId);

        lastPoll = System.currentTimeMillis();
        scheduleLeaseTimeout();

        // create a response that represents every tag for every subscribed point
        Array response = new Array(HDict.class);
        Iterator itr = allSubscribed.keySet().iterator();
        while (itr.hasNext())
        {
            BComponent point = (BComponent) itr.next();
            HDict dict = server.getTagManager().createComponentCovTags(point);

            response.add(dict);
            allSubscribed.put(point, dict);
        }

        // since this method counts as a poll, clear out nextPoll so we 
        // can start accumulating more Covs.
        nextPoll.clear();

        // done
        if (LOG.isTraceOn())
            LOG.trace("NHWatch.pollRefresh end   " + watchId + ", size " + response.size());

        return HGridBuilder.dictsToGrid((HDict[]) response.trim());
    }

    /**
     * Close the watch and free up any state resources.
     */
    public synchronized void close()
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");

        if (LOG.isTraceOn())
            LOG.trace("NHWatch.close " + watchId);

        if (timeout != null) timeout.cancel();
        open = false;

        subscriber.unsubscribeAll();

        allSubscribed.clear();
        nextPoll.clear();

        server.removeWatch(watchId);
    }

    /**
     * Return whether this watch is currently open.
     */
    public synchronized boolean isOpen()
    {
        return open;
    }

////////////////////////////////////////////////////////////////
// NSubscriber
////////////////////////////////////////////////////////////////

    private class NSubscriber extends Subscriber
    {
        public void event(BComponentEvent event)     
        {
            synchronized(NHWatch.this)
            {
                BComponent comp = event.getSourceComponent();

                if (event.getSlotName().equals("out") && // we only care about the "out" slot
                    allSubscribed.containsKey(comp))     // and lets double check that we are really subscribed
                {
                    HDict cov = server.getTagManager().createComponentCovTags(comp);
                    nextPoll.put(comp, cov);
                }
            }
        }
    }

////////////////////////////////////////////////////////////////
// package-scope
////////////////////////////////////////////////////////////////

    synchronized HDict[] curSubscribed()
    {
        Array arr = new Array(HDict.class);
        Iterator itr = allSubscribed.keySet().iterator();
        while (itr.hasNext())
        {
            BComponent point = (BComponent) itr.next();
            HDict dict = server.getTagManager().createComponentCovTags(point);
            arr.add(dict);
        }
        return (HDict[]) arr.trim();
    }

    synchronized long lastPoll()
    {
        return lastPoll;
    }

////////////////////////////////////////////////////////////////
// Timeout
////////////////////////////////////////////////////////////////

    private void scheduleLeaseTimeout()
    {
        if (timeout != null) timeout.cancel();

        timer.schedule(timeout = new Timeout(), leaseInterval);
    }

    private class Timeout extends TimerTask
    {
        public void run()
        {
            LOG.warning("Watch " + watchId + " timed out.");
            close();
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack.watch");

    private final NHServer server;
    private final String dis;
    private final String watchId;
    private final long leaseInterval;

    private final Subscriber subscriber = new NSubscriber();

    private final HashMap allSubscribed = new HashMap(); // point -> HDict (all tags)
    private final HashMap nextPoll      = new HashMap(); // point -> HDict (cov)

    private boolean open;
    private final Timer timer = new Timer();
    private Timeout timeout = null;
    private long lastPoll = 0;
}

