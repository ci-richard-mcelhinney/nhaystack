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
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;

/**
  * NHWatch manages leased components
  */
public class NHWatch extends HWatch
{
    public NHWatch(NHServer server, String dis, long leaseInterval)
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

        if (LOG.isTraceOn())
            LOG.trace("NHWatch.sub " + watchId + ", length " + ids.length);

        scheduleLeaseTimeout();

        HDict meta = new HDictBuilder()
            .add("watchId", HStr.make(id()))
            .add("lease", lease())
            .toDict();

        Array response = new Array(HDict.class);
        Array pointArr = new Array(BControlPoint.class);
        for (int i = 0; i < ids.length; i++)
        {
            HRef id = (HRef) ids[i];

            try
            {
                BComponent comp = server.lookupComponent(id);

                // no such component -- treat 'checked' as if it were false, since
                // 'checked' is handled on the client side.
                //
                // we also ignore anything that's not a control point.
                //
                if ((comp == null) || !(comp instanceof BControlPoint))
                {
                    response.add(null);
                }
                // found
                else
                {
                    if (LOG.isTraceOn())
                        LOG.trace("NHWatch.sub " + watchId + " subscribe " + id);

                    pointArr.add(comp);
                    HDict dict = server.createTags(comp);
                    response.add(dict);
                    allSubscribed.put(comp, dict);
                }
            }
            catch (Exception e)
            {
                LOG.warning("Could not subscribe to " + id + ": " + e.getMessage());
                response.add(null);
            }
        }

        // subscribe
        BControlPoint[] points = (BControlPoint[]) pointArr.trim();
        subscriber.subscribe(points, DEPTH, null);

        return HGridBuilder.dictsToGrid(meta, (HDict[]) response.trim());
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

        Array pointArr = new Array(BControlPoint.class);
        for (int i = 0; i < ids.length; i++)
        {
            HRef id = (HRef) ids[i];

            BComponent comp = server.lookupComponent(id);
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
        BControlPoint[] points = (BControlPoint[]) pointArr.trim();
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
            LOG.trace("NHWatch.pollChanges " + watchId);

        scheduleLeaseTimeout();

        // create a response from all the COV values in nextPoll
        Array response = new Array(HDict.class);
        Iterator itr = nextPoll.values().iterator();
        while (itr.hasNext())
            response.add(itr.next());

        // clear out nextPoll so we can start accumulating more COVs
        nextPoll.clear();

        // done
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
            LOG.trace("NHWatch.pollRefresh " + watchId);

        scheduleLeaseTimeout();

        // create a response that represents every tag for every subscribed point
        Array response = new Array(HDict.class);
        Iterator itr = allSubscribed.keySet().iterator();
        while (itr.hasNext())
        {
            BControlPoint point = (BControlPoint) itr.next();
            HDict dict = server.createTags(point);

            response.add(dict);
            allSubscribed.put(point, dict);
        }

        // since this method counts as a poll, clear out nextPoll so we 
        // can start accumulating more Covs.
        nextPoll.clear();

        // done
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

                // Check components upwards to the depth that we are subscribed.
                // This will put things like proxyExts COVs up to the 'point' 
                // component where they belong.
                for (int i = 0; i < DEPTH; i++)
                {
                    if (allSubscribed.containsKey(comp))
                    {
                        BControlPoint point = (BControlPoint) comp;
                        HDict cov = server.getComponentStorehouse().createPointCovTags(point);

                        if (LOG.isTraceOn())
                            LOG.trace("NSubscriber.event " + cov);

                        nextPoll.put(comp, cov);
                        break;
                    }

                    comp = (BComponent) comp.getParent();
                }
            }
        }
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

    private static final Log LOG = Log.getLog("nhaystack");

    // this is deep enough to get COV callbacks on proxy extensions
    private static final int DEPTH = 2;

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
}

