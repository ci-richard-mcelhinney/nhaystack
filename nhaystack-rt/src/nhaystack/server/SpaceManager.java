//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Oct 2012  Mike Jarmy       Creation
//   10 May 2018  Eric Anderson    Added missing @Overrides annotations, added use of generics
//   26 Sep 2018  Andrew Saunders  Provided access to isVisibleComponent method from wb module
//
package nhaystack.server;

import java.util.Iterator;
import java.util.logging.Logger;
import javax.baja.control.BControlPoint;
import javax.baja.control.ext.BAbstractProxyExt;
import javax.baja.driver.BDevice;
import javax.baja.driver.BDeviceNetwork;
import javax.baja.driver.point.BPointDeviceExt;
import javax.baja.history.BHistoryConfig;
import javax.baja.history.HistorySpaceConnection;
import javax.baja.history.ext.BHistoryExt;
import javax.baja.naming.BOrd;
import javax.baja.naming.UnresolvedException;
import javax.baja.schedule.BWeeklySchedule;
import javax.baja.sys.BComponent;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Property;
import javax.baja.sys.SlotCursor;
import javax.baja.sys.Sys;
import nhaystack.BHDict;
import nhaystack.collection.ComponentTreeIterator;
import nhaystack.collection.HistoryDbIterator;
import nhaystack.site.BHTagged;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HDict;

/**
  * SpaceManager does various tasks associated with the ComponentSpace and
  * the HistorySpace, including iterating through the spaces, and relating the
  * two spaces to each other.
  */
public class SpaceManager
{
    SpaceManager(NHServer server)
    {
        this.server = server;
        this.service = server.getService();
    }

////////////////////////////////////////////////////////////////
// ComponentSpace
////////////////////////////////////////////////////////////////

    /**
      * Iterate through all the points
      */
    Iterator<HDict> makeComponentSpaceIterator()
    {
        return new CIterator();
    }

    /**
      * Return whether the given component
      * ought to be turned into a Haystack record.
      */
    public static boolean isVisibleComponent(BComponent comp)
    {
        // check permissions on this Thread's saved context
        Context cx = ThreadContext.getContext(Thread.currentThread());
        if (!TypeUtil.canRead(comp, cx)) 
            return false;

        if (comp instanceof BHTagged) return true;
        if (comp instanceof BControlPoint) return true;
        if (comp instanceof BDevice) return true;
        if (comp instanceof BWeeklySchedule) return true;

        // Return true for components that are annotated with a BHDict.
        BValue haystack = comp.get("haystack");
        if (haystack instanceof BHDict)
            return true;

        // nope
        return false;
    }

    /**
      * Try to find the point that goes with a history,
      * or return null.
      */
    BControlPoint lookupPointFromHistory(BHistoryConfig cfg)
    {
        // local history
        if (cfg.getId().getDeviceName().equals(Sys.getStation().getStationName()))
        {
            try
            {
                BOrd[] ords = cfg.getSource().toArray();
                if (ords.length == 1) 
                {
                    BComponent source = (BComponent) ords[0].resolve(service, null).get();

                    // The source is not always a BHistoryExt.  E.g. for 
                    // LogHistory its the LogHistoryService.
                    if (source instanceof BHistoryExt)
                    {
                        if (source.getParent() instanceof BControlPoint)
                            return (BControlPoint) source.getParent();
                    }
                }
            }
            catch (UnresolvedException e)
            {
                return null;
            }

            return null;
        }
        // look for imported point that goes with history (if any)
        else
        {
            RemotePoint remote = RemotePoint.fromHistoryConfig(cfg);
            if (remote == null) return null;

            return server.getCache().getControlPoint(remote);
        }
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    /**
      * Find the imported point that goes with an imported history, 
      * or return null.  
      */
    private BControlPoint lookupRemotePoint(
        BHistoryConfig cfg, 
        RemotePoint remote)
    {
        // look up the station
        BDeviceNetwork network = service.getNiagaraNetwork();
        BDevice station = (BDevice) network.get(remote.getStationName());
        if (station == null) return null;

        // look up the points
        // this fetches from sub-folders too
        BPointDeviceExt pointDevExt = (BPointDeviceExt) station.get("points");
        BControlPoint[] points = pointDevExt.getPoints(); 

        // find a point with matching slot path
        for (BControlPoint point : points)
        {
            // check for remote point
            if (!RemotePoint.isRemotePoint(point)) continue;

            BAbstractProxyExt proxyExt = point.getProxyExt();

            // "pointId" seems to always contain the slotPath on 
            // the remote host.
            String slotPath = proxyExt.get("pointId").toString();

            // found it!
            if (slotPath.equals(remote.getSlotPath().toString()))
                return point;
        }

        // no such point
        return null;
    }

////////////////////////////////////////////////////////////////
// Iterator
////////////////////////////////////////////////////////////////

    class CIterator implements Iterator<HDict>
    {
        CIterator()
        {
            this.iterator = new ComponentTreeIterator(
                (BComponent) BOrd.make("slot:/").resolve(service, null).get());
            findNext();
        }

        @Override
        public boolean hasNext()
        { 
            return nextDict != null; 
        }

        @Override
        public HDict next()
        {
            if (nextDict == null) throw new IllegalStateException();

            HDict dict = nextDict;
            findNext();
            return dict;
        }

        @Override
        public void remove()
        { 
            throw new UnsupportedOperationException(); 
        }

        private void findNext()
        {
            nextDict = null;
            while (iterator.hasNext())
            {
                BComponent comp = iterator.next();

                if (isVisibleComponent(comp))
                {
                    nextDict = server.getTagManager().createComponentTags(comp);
                    break;
                }
            }
        }

        private final ComponentTreeIterator iterator;
        private HDict nextDict;
    }

////////////////////////////////////////////////////////////////
// HistorySpace
////////////////////////////////////////////////////////////////

    /**
      * Iterate through all the histories.
      */
    Iterator<HDict> makeHistorySpaceIterator()
    {
        return new HIterator();
    }

    /**
      * Try to find either a local or imported history for the point
      */
    BHistoryConfig lookupHistoryFromPoint(BControlPoint point)
    {
        // local history
        BHistoryExt historyExt = lookupHistoryExt(point);
        if (historyExt != null) return historyExt.getHistoryConfig();

        // look for history that goes with a proxied point (if any)
        if (RemotePoint.isRemotePoint(point))
            return lookupRemoteHistory(point);
        else 
            return null;
    }

    /**
      * Return the BHistoryExt for the point, if there is one.
      * Returns null if the BHistoryExt has never been enabled.
      */
    BHistoryExt lookupHistoryExt(BControlPoint point)
    {
        SlotCursor<Property> cursor = point.getProperties();
        if (cursor.next(BHistoryExt.class))
        {
            BHistoryExt ext = (BHistoryExt) cursor.get();

            // Return null if the extension has never been enabled.
            BHistoryConfig config = ext.getHistoryConfig();

            try (HistorySpaceConnection conn = service.getHistoryDb().getConnection(null))
            {
                if (conn.getHistory(config.getId()) == null)
                    return null;
            }

            return ext;
        }

        return null;
    }

    /**
      * Return whether this history is visible to the outside world.
      */
    boolean isVisibleHistory(BHistoryConfig cfg)
    {
        // check permissions on this Thread's saved context
        Context cx = ThreadContext.getContext(Thread.currentThread());
        if (!TypeUtil.canRead(cfg, cx)) 
            return false;

        // make sure the history name is valid. This is a workaround for a bug
        // in third-party software.
        try
        {
            TagManager.makeHistoryRef(cfg);
        }
        catch (Exception e)
        {
            LOG.severe("Invalid history name: " + cfg.getId());
            return false;
        }

        // annotated 
        HDict dict = BHDict.findTagAnnotation(cfg);
        if (dict != null && !dict.isEmpty())
            return true;

        // show linked
        if (service.getShowLinkedHistories())
            return true;

        // make sure the history is not linked
        if (lookupPointFromHistory(cfg) == null)
            return true;

        return false;
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    /**
      * Find the imported history that goes with an imported point, 
      * or return null.  
      */
    private BHistoryConfig lookupRemoteHistory(BControlPoint point)
    {
        RemotePoint remotePoint = RemotePoint.fromControlPoint(point);
        if (remotePoint == null) return null;

        return server.getCache().getHistoryConfig(remotePoint);
    }

////////////////////////////////////////////////////////////////
// HIterator
////////////////////////////////////////////////////////////////

    class HIterator implements Iterator<HDict>
    {
        HIterator()
        {
            this.iterator = new HistoryDbIterator(service.getHistoryDb());
            findNext();
        }

        @Override
        public boolean hasNext()
        { 
            return nextDict != null; 
        }

        @Override
        public void remove() { throw new UnsupportedOperationException(); }

        @Override
        public HDict next()
        {
            if (nextDict == null) throw new IllegalStateException();

            HDict dict = nextDict;
            findNext();
            return dict;
        }

        private void findNext()
        {
            nextDict = null;
            while (iterator.hasNext())
            {
                BHistoryConfig cfg = iterator.next();

                if (isVisibleHistory(cfg))
                {
                    nextDict = server.getTagManager().createHistoryTags(cfg);
                    break;
                }
            }
        }

        private final HistoryDbIterator iterator;

        private HDict nextDict;
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack");

    final NHServer server;
    final BNHaystackService service;
}

