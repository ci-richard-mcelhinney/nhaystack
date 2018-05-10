//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   30 Mar 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Added missing @Overrides annotations, added use of generics
//
package nhaystack.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.control.BControlPoint;
import javax.baja.schedule.BWeeklySchedule;
import javax.baja.sys.BComponent;
import javax.baja.sys.BComponentEvent;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Clock;
import javax.baja.sys.Subscriber;
import javax.baja.util.BUuid;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HStr;
import org.projecthaystack.HWatch;

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
            "leaseInterval:" + leaseInterval + ']';
    }

    /**
     * Unique watch identifier within a project database.
     */
    @Override
    public String id()
    {
        return watchId;
    }

    /**
     * Debug display string used during "HProj.watchOpen"
     */
    @Override
    public String dis()
    {
        return dis;
    }

    /**
     * Lease period or null if watch has not been opened yet.
     */
    @Override
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
    @Override
    public synchronized HGrid sub(HRef[] ids, boolean checked)
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");

        long ticks = Clock.ticks();
        if (LOG.isLoggable(Level.FINE))
            LOG.fine("NHWatch.sub begin " + watchId + ", length " + ids.length);

        lastPoll = System.currentTimeMillis();
        scheduleLeaseTimeout();

        HDict meta = new HDictBuilder()
            .add("watchId", HStr.make(id()))
            .add("lease", lease())
            .toDict();

        ArrayList<HDict> response = new ArrayList<>();
        ArrayList<BComponent> pointArr = new ArrayList<>();
        for (HRef id : ids)
        {
            try
            {
                BComponent comp = server.getTagManager().lookupComponent(id);

                // no such component -- treat 'checked' as if it were false, since
                // 'checked' is handled on the client side.
                //
                // we also ignore anything that's not a control point or schedule.
                //
                if (!(comp instanceof BControlPoint || comp instanceof BWeeklySchedule))
                {
                    if (LOG.isLoggable(Level.WARNING))
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

        subscriber.subscribe(pointArr.toArray(EMPTY_COMPONENT_ARRAY), 0, null);

        HGrid grid = HGridBuilder.dictsToGrid(meta, response.toArray(EMPTY_HDICT_ARRAY));

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("NHWatch.sub end   " + watchId + ", length " + ids.length + ", " +
                (Clock.ticks()-ticks) + "ms.");

        return grid;
    }

    /**
     * Remove a list of records from watch.  Silently ignore
     * any invalid ids.
     */
    @Override
    public synchronized void unsub(HRef[] ids)
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("NHWatch.unsub " + watchId + ", length " + ids.length);

        scheduleLeaseTimeout();

        ArrayList<BComponent> pointArr = new ArrayList<>();
        for (HRef id : ids)
        {
            BComponent comp = server.getTagManager().lookupComponent(id);
            if (comp != null && allSubscribed.containsKey(comp))
            {
                if (LOG.isLoggable(Level.FINE))
                    LOG.fine("NHWatch.unsub " + watchId + " unsubscribe " + id);

                pointArr.add(comp);
                allSubscribed.remove(comp);
                nextPoll.remove(comp);
            }
        }

        // unsubscribe
        subscriber.unsubscribe(pointArr.toArray(EMPTY_COMPONENT_ARRAY), null);
    }

    /**
     * Poll for any changes to the subscriptions records.
     * This returns only the id, curVal and curStatus tags for each point.
     */
    @Override
    public synchronized HGrid pollChanges()
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("NHWatch.pollChanges begin " + watchId);

        lastPoll = System.currentTimeMillis();
        scheduleLeaseTimeout();

        // create a response from all the COV values in nextPoll
        ArrayList<HDict> response = new ArrayList<>(nextPoll.values());

        // clear out nextPoll so we can start accumulating more COVs
        nextPoll.clear();

        // done
        if (LOG.isLoggable(Level.FINE))
            LOG.fine("NHWatch.pollChanges end   " + watchId + ", size " + response.size());
        return HGridBuilder.dictsToGrid(response.toArray(EMPTY_HDICT_ARRAY));
    }

    /**
     * Poll all the subscriptions records even if there have been no changes.
     * This returns all of the tags for each point.
     */
    @Override
    public synchronized HGrid pollRefresh()
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("NHWatch.pollRefresh begin " + watchId);

        lastPoll = System.currentTimeMillis();
        scheduleLeaseTimeout();

        // create a response that represents every tag for every subscribed point
        ArrayList<HDict> response = new ArrayList<>();
        for (BComponent point : allSubscribed.keySet())
        {
            HDict dict = server.getTagManager().createComponentCovTags(point);
            response.add(dict);
            allSubscribed.put(point, dict);
        }

        // since this method counts as a poll, clear out nextPoll so we 
        // can start accumulating more Covs.
        nextPoll.clear();

        // done
        if (LOG.isLoggable(Level.FINE))
            LOG.fine("NHWatch.pollRefresh end   " + watchId + ", size " + response.size());

        return HGridBuilder.dictsToGrid(response.toArray(EMPTY_HDICT_ARRAY));
    }

    /**
     * Close the watch and free up any state resources.
     */
    @Override
    public synchronized void close()
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("NHWatch.close " + watchId);

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
    @Override
    public synchronized boolean isOpen()
    {
        return open;
    }

////////////////////////////////////////////////////////////////
// NSubscriber
////////////////////////////////////////////////////////////////

    private class NSubscriber extends Subscriber
    {
        @Override
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
        ArrayList<HDict> arr = new ArrayList<>();
        for (BComponent point : allSubscribed.keySet())
        {
            HDict dict = server.getTagManager().createComponentCovTags(point);
            arr.add(dict);
        }
        return arr.toArray(EMPTY_HDICT_ARRAY);
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
        @Override
        public void run()
        {
            LOG.warning("Watch " + watchId + " timed out.");
            close();
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack.watch");

    private static final BComponent[] EMPTY_COMPONENT_ARRAY = new BComponent[0];
    private static final HDict[] EMPTY_HDICT_ARRAY = new HDict[0];

    private final NHServer server;
    private final String dis;
    private final String watchId;
    private final long leaseInterval;

    private final Subscriber subscriber = new NSubscriber();

    private final Map<BComponent, HDict> allSubscribed = new HashMap<>(); // point -> HDict (all tags)
    private final Map<BComponent, HDict> nextPoll = new HashMap<>(); // point -> HDict (cov)

    private boolean open;
    private final Timer timer = new Timer();
    private Timeout timeout;
    private long lastPoll;
}

