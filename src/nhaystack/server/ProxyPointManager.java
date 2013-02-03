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
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.util.*;

/**
  * ProxyPointManager is responsible for keeping track
  * of which imported BControlPoints go with which 
  * imported BHistoryConfigs.
  */
public class ProxyPointManager
{
    public ProxyPointManager(BNHaystackService service)
    {
        this.service = service;
    }

    /**
      * Find the imported history that goes with an imported point, 
      * or return null.  
      *
      * The point must be a fox proxy point, and of course 
      * the matching imported history must actually exist.
      */
    public BHistoryConfig lookupImportedHistory(BControlPoint point)
    {
        if (!point.getProxyExt().getType().is(ProxyPointManager.NIAGARA_PROXY_EXT)) 
            throw new IllegalStateException();
        ProxyPoint proxyPoint = ProxyPoint.make(point);

        synchronized(this)
        {
            ensureLoaded();
            return (BHistoryConfig) mapPointToConfig.get(proxyPoint);
        }
    }

    /**
      * Find the imported point that goes with an imported history, 
      * or return null.  
      */
    public BControlPoint lookupImportedPoint(BHistoryConfig cfg)
    {
        // cannot be local history
        if (cfg.getId().getDeviceName().equals(Sys.getStation().getStationName()))
            throw new IllegalStateException();

        ProxyPoint proxyPoint = ProxyPoint.make(cfg);
        if (proxyPoint == null) return null;

        // look up the station
        BDeviceNetwork network = getNiagaraNetwork();
        BDevice station = (BDevice) network.get(proxyPoint.getStationName());
        if (station == null) return null;

        // TODO: The following code is very inefficient, since we are doing it 
        // every time we need to turn an imported BHistoryConfig into 
        // an HDict.  
        //
        // A better solution would be to make a 
        // custom javax.baja.sys.Subscriber, and keep track of the imported
        // points (and their comings and goings) in a data structure.
        //
        // This is essentially what we are already doing with histories, 
        // via the HistoryEventListener.  The problem here is that the points
        // can be found arbitrarily deep inside sub-folders, which
        // makes subscription difficult to do properly. In addition, subscribing
        // to these points actually causes them to go out to their respective
        // jaces (right???), which is obviously not what we want.

        // look up the points
        BPointDeviceExt pointDevExt = (BPointDeviceExt) station.get("points");
        BControlPoint[] points = pointDevExt.getPoints(); // fetches from sub-folders too

        // find a point with matching slot path
        for (int i = 0; i < points.length; i++)
        {
            BControlPoint point = points[i];

            // Check for a NiagaraProxyExt
            BAbstractProxyExt proxyExt = point.getProxyExt();
            if (!proxyExt.getType().is(NIAGARA_PROXY_EXT)) continue;

            // "pointId" seems to always contain the slotPath on 
            // the remote host.
            String slotPath = proxyExt.get("pointId").toString();

            // found it!
            if (slotPath.equals(proxyPoint.getSlotPath().toString()))
                return point;
        }

        // no such point
        return null;
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    /**
      * Stash away all the imported histories.
      * This only happens once.
      */
    private void ensureLoaded()
    {
        if (mapPointToConfig != null) return;

        mapPointToConfig = new HashMap();
        mapHistoryToPoint = new HashMap();

        BIHistory[] histories = service.getHistoryDb().getHistories(); 
        for (int i = 0; i < histories.length; i++)
        {
            BIHistory h = histories[i];
            BHistoryId hid = h.getId();
            BHistoryConfig cfg = h.getConfig();

            // ignore local histories
            if (hid.getDeviceName().equals(Sys.getStation().getStationName()))
                continue;

            ProxyPoint proxyPoint = ProxyPoint.make(cfg);
            if (proxyPoint != null)
            {
                mapPointToConfig.put(proxyPoint, cfg);
                mapHistoryToPoint.put(hid, proxyPoint);
            }
        }

        // now that we've loaded all the histories, we'll set up a
        // listener so that we can keep our HashMap up-to-date
        service.getHistoryDb().addHistoryEventListener(new Listener());
    }

    private BDeviceNetwork getNiagaraNetwork()
    {
        if (niagaraNetwork == null)
        {
            niagaraNetwork = (BDeviceNetwork)
                NIAGARA_NETWORK.resolve(service, null).get();
        }
        return niagaraNetwork;
    }

////////////////////////////////////////////////////////////////
// HistoryEventListener
////////////////////////////////////////////////////////////////

    private class Listener implements HistoryEventListener
    {
        public void historyEvent(BHistoryEvent event)
        {
            if (event.getId() == BHistoryEvent.CREATED)
                addHistory(event.getHistoryId());

            else if (event.getId() == BHistoryEvent.DELETED)
                removeHistory(event.getHistoryId());
        }

        private void addHistory(BHistoryId hid)
        {
            // ignore local histories
            if (hid.getDeviceName().equals(Sys.getStation().getStationName()))
                return;

            BIHistory h = service.getHistoryDb().getHistory(hid);
            BHistoryConfig cfg = h.getConfig();
            ProxyPoint proxyPoint = ProxyPoint.make(cfg);

            synchronized(this)
            {
                if (proxyPoint != null)
                {
                    mapPointToConfig.put(proxyPoint, cfg);
                    mapHistoryToPoint.put(hid, proxyPoint);
                }
            }
        }

        private void removeHistory(BHistoryId hid)
        {
            // ignore local histories
            if (hid.getDeviceName().equals(Sys.getStation().getStationName()))
                return;

            synchronized(this)
            {
                ProxyPoint proxyPoint = (ProxyPoint) mapHistoryToPoint.get(hid);
                mapPointToConfig.remove(proxyPoint);
                mapHistoryToPoint.remove(hid);
            }
        }
    }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static BOrd NIAGARA_NETWORK = BOrd.make("station:|slot:/Drivers/NiagaraNetwork");

    public static final Type NIAGARA_PROXY_EXT;
    public static final Type NIAGARA_STATION;

    static
    {
        NIAGARA_PROXY_EXT = BTypeSpec.make("niagaraDriver:NiagaraProxyExt").getResolvedType();
        NIAGARA_STATION   = BTypeSpec.make("niagaraDriver:NiagaraStation").getResolvedType();
    }

    private final BNHaystackService service;

    private BDeviceNetwork niagaraNetwork;

    // access to these data structures must be synchronized
    private Map mapPointToConfig  = null; // ProxyPoint -> BHistoryConfig
    private Map mapHistoryToPoint = null; // BHistoryId -> ProxyPoint
}

