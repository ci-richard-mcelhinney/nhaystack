//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   30 Mar 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;
import javax.baja.log.*;
import javax.baja.sys.*;
import javax.baja.util.*;
import haystack.*;

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
    public synchronized HGrid sub(HIdentifier[] ids, boolean checked)
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");
        scheduleLeaseTimeout();

        HDict meta = new HDictBuilder()
            .add("watchId", HStr.make(id()))
            .add("lease", lease())
            .toDict();

        Array dictArr = new Array(HDict.class);
        for (int i = 0; i < ids.length; i++)
        {
            // we can assume this because onNavReadByUri will have
            // already been called for us.
            HRef id = (HRef) ids[i];

            BComponent comp = server.lookupComponent(id);

            // no such component -- treat 'checked' as if it were false, since
            // 'checked' is handled on the client side.
            if (comp == null)
            {
                dictArr.add(null);
            }
            // found
            else
            {
                subscriber.subscribe(comp, DEPTH);

                HDict dict = server.createTags(comp);
                dictArr.add(dict);
                allDicts.put(comp, dict);
            }
        }

        return HGridBuilder.dictsToGrid(meta, (HDict[]) dictArr.trim());
    }

    /**
     * Remove a list of records from watch.  Silently ignore
     * any invalid ids.
     */
    public synchronized void unsub(HIdentifier[] ids)
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");
        scheduleLeaseTimeout();

        for (int i = 0; i < ids.length; i++)
        {
            // we can assume this because onNavReadByUri will have
            // already been called for us.
            HRef id = (HRef) ids[i];

            BComponent comp = server.lookupComponent(id);
            if (comp != null)
            {
                subscriber.unsubscribe(comp);
                allDicts.remove(comp);
                changedDicts.remove(comp);
            }
        }
    }

    /**
     * Poll for any changes to the subscriptions records.
     */
    public synchronized HGrid pollChanges()
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");
        scheduleLeaseTimeout();

        Array dictArr = new Array(HDict.class);

        Iterator itr = changedDicts.values().iterator();
        while (itr.hasNext())
        {
            HDict dict = (HDict) itr.next();
            dictArr.add(dict);
        }

        return HGridBuilder.dictsToGrid((HDict[]) dictArr.trim());
    }

    /**
     * Poll all the subscriptions records even if there have been no changes.
     */
    public synchronized HGrid pollRefresh()
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");
        scheduleLeaseTimeout();

        Array dictArr = new Array(HDict.class);

        Iterator itr = allDicts.keySet().iterator();
        while (itr.hasNext())
        {
            BComponent comp = (BComponent) itr.next();
            HDict dict = server.createTags(comp);

            dictArr.add(dict);
            allDicts.put(comp, dict);
        }

        changedDicts.clear();
        return HGridBuilder.dictsToGrid((HDict[]) dictArr.trim());
    }

    /**
     * Close the watch and free up any state resources.
     */
    public synchronized void close()
    {
        if (!open) throw new BajaRuntimeException(
            "Watch " + watchId + " is closed.");
        if (timeout != null) timeout.cancel();
        open = false;

        subscriber.unsubscribeAll();

        allDicts.clear();
        changedDicts.clear();

        server.removeWatch(watchId);
    }

////////////////////////////////////////////////////////////////
// NSubscriber
////////////////////////////////////////////////////////////////

    private class NSubscriber extends Subscriber
    {
        public void event(BComponentEvent event)     
        {
            BComponent comp = event.getSourceComponent();

            // Check components upwards to the depth that we are subscribed.
            // This will put things like proxyExts covs up to the 'point' 
            // component where they belong.
            for (int i = 0; i < DEPTH; i++)
            {
                if (allDicts.containsKey(comp))
                {
                    HDict dict = server.createTags(comp);
                    changedDicts.put(comp, dict);
                    break;
                }

                comp = (BComponent) comp.getParent();
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

    // this is deep enough to get Cov callbacks on proxy extensions
    private static final int DEPTH = 2;

    private final NHServer server;
    private final String dis;
    private final String watchId;
    private final long leaseInterval;

    private final Subscriber subscriber = new NSubscriber();

    private final HashMap allDicts     = new HashMap(); // comp -> dict
    private final HashMap changedDicts = new HashMap(); // comp -> dict

    private boolean open;
    private final Timer timer = new Timer();
    private Timeout timeout = null;
}

