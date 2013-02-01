//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//
package nhaystack.server;

import java.util.*;
import javax.baja.sys.*;
import javax.baja.util.*;
import haystack.*;

/**
  * NHWatch manages leased components
  */
public class NHWatch extends HWatch
{
    public NHWatch(NHServer server, String dis)
    {
        this.server = server;
        this.dis = dis;

        this.watchId = BUuid.make().toString();

        this.leaseInterval = 
            server.service.getLeaseInterval().getMillis();
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
    public HGrid sub(HRef[] ids, boolean checked)
    {
        // meta
        HDict meta = new HDictBuilder()
            .add("watchId", HStr.make(id()))
            .add("lease", lease())
            .toDict();

        // dicts
        Array dicts = new Array(HDict.class);
        for (int i = 0; i < ids.length; i++)
        {
            HRef id = ids[i];

            // lookup
            BComponent comp = server.lookupComponent(id);

            // no such component -- treat 'checked' as if it were false.
            // ('checked' is handled on the client side).
            if (comp == null)
            {
                dicts.add(null);
            }
            // found
            else
            {
                comp.lease(LEASE_DEPTH, leaseInterval); 
                HDict dict = server.makeDict(comp);
                dicts.add(dict);
                subscriptions.put(id, new Subscription(comp, dict));
            }
        }

        return HGridBuilder.dictsToGrid(meta, (HDict[]) dicts.trim());
    }

    /**
     * Remove a list of records from watch.  Silently ignore
     * any invalid ids.
     */
    public void unsub(HRef[] ids)
    {
        for (int i = 0; i < ids.length; i++)
            subscriptions.remove(ids[i]);
    }

    /**
     * Poll for any changes to the subscriptions records.
     */
    public HGrid pollChanges()
    {
        Array dicts = new Array(HDict.class);

        Iterator itr = subscriptions.values().iterator();
        while (itr.hasNext())
        {
            Subscription sub = (Subscription) itr.next();
            sub.comp.lease(LEASE_DEPTH, leaseInterval); 
            HDict newDict = server.makeDict(sub.comp);

            if (!sub.dict.equals(newDict))
            {
                sub.dict = newDict;
                dicts.add(sub.dict);
            }
        }

        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }

    /**
     * Poll all the subscriptions records even if there have been no changes.
     */
    public HGrid pollRefresh()
    {
        Array dicts = new Array(HDict.class);

        Iterator itr = subscriptions.values().iterator();
        while (itr.hasNext())
        {
            Subscription sub = (Subscription) itr.next();
            sub.comp.lease(LEASE_DEPTH, leaseInterval); 
            sub.dict = server.makeDict(sub.comp);
            dicts.add(sub.dict);
        }

        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }

    /**
     * Close the watch and free up any state resources.
     */
    public void close()
    {
        subscriptions.clear();
        server.removeWatch(watchId);
    }

////////////////////////////////////////////////////////////////
// Subscription
////////////////////////////////////////////////////////////////

    private static class Subscription
    {
        public Subscription(BComponent comp, HDict dict)
        {
            this.comp = comp;
            this.dict = dict;
        }

        private BComponent comp;
        private HDict dict;
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final int LEASE_DEPTH = 1;

    private final NHServer server;
    private final String dis;
    private final String watchId;
    private final long leaseInterval;

    private final HashMap subscriptions = new HashMap();
}

