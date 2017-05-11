//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;

import javax.baja.control.*;
import javax.baja.control.ext.*;
import javax.baja.driver.*;
import javax.baja.driver.point.*;
import javax.baja.history.*;
import javax.baja.history.ext.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.schedule.*;
import javax.baja.sys.*;

import org.projecthaystack.*;

import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.site.*;
import nhaystack.util.*;

/**
  * SpaceManager does various tasks associated with the ComponentSpace and
  * the HistorySpace, including iterating through the spaces, and relating the
  * two spaces to each other.
  */
class SpaceManager
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
    Iterator makeComponentSpaceIterator()
    {
        return new CIterator();
    }

    /**
      * Return whether the given component
      * ought to be turned into a Haystack record.
      */
    boolean isVisibleComponent(BComponent comp)
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
        if ((haystack != null) && (haystack instanceof BHDict))
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
        for (int i = 0; i < points.length; i++)
        {
            BControlPoint point = points[i];

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

    class CIterator implements Iterator
    {
        CIterator()
        {
            this.iterator = new ComponentTreeIterator(
                (BComponent) BOrd.make("slot:/").resolve(service, null).get());
            findNext();
        }

        public boolean hasNext() 
        { 
            return nextDict != null; 
        }

        public Object next()
        {
            if (nextDict == null) throw new IllegalStateException();

            HDict dict = nextDict;
            findNext();
            return dict;
        }

        public void remove() 
        { 
            throw new UnsupportedOperationException(); 
        }

        private void findNext()
        {
            nextDict = null;
            while (iterator.hasNext())
            {
                BComponent comp = (BComponent) iterator.next();

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
    Iterator makeHistorySpaceIterator()
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
        SlotCursor cursor = point.getProperties();
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
            LOG.error("Invalid history name: " + cfg.getId());
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

    class HIterator implements Iterator
    {
        HIterator()
        {
            this.iterator = new HistoryDbIterator(service.getHistoryDb());
            findNext();
        }

        public boolean hasNext() 
        { 
            return nextDict != null; 
        }

        public void remove() { throw new UnsupportedOperationException(); }

        public Object next()
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
                BHistoryConfig cfg = (BHistoryConfig) iterator.next();

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

    private static final Log LOG = Log.getLog("nhaystack");

    final NHServer server;
    final BNHaystackService service;
}

