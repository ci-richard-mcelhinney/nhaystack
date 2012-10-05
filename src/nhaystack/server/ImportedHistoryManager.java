//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.server;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import javax.baja.collection.*;
import javax.baja.control.*;
import javax.baja.control.ext.*;
import javax.baja.driver.*;
import javax.baja.driver.history.*;
import javax.baja.driver.point.*;
import javax.baja.history.*;
import javax.baja.history.ext.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.timezone.*;
import javax.baja.units.*;
import javax.baja.util.*;
import javax.baja.web.*;

import haystack.*;
import haystack.io.*;
import haystack.server.*;

import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.util.*;

/**
  * ImportedHistoryManager is responsible for keeping track
  * of which fox-proxied BControlPoints go with which 
  * imported BHistoryConfigs.
  */
public class ImportedHistoryManager
{
    public ImportedHistoryManager(BNHaystackService service)
    {
        this.service = service;
    }

    /**
      * Find history for a point, or return null.  
      * The point must be a fox proxy point, and of course 
      * the matching imported history must actually exist.
      */
    public BHistoryConfig lookupImportedHistory(BControlPoint point)
    {
        //////////////////////////////////////////////////////
        // first, let's determine whether the point 
        // actually is proxied

        // Check for a NiagaraProxyExt
        BAbstractProxyExt proxyExt = point.getProxyExt();
        if (!proxyExt.getType().is(NIAGARA_PROXY_EXT)) return null;

        // "pointId" seems to always contain the slotPath on 
        // the remote host, which is nice because that's what 
        // we actually need.
        BValue pointId = proxyExt.get("pointId");
        if (pointId == null) return null;
        String slotPath = pointId.toString(); 

        // Find the ancestor NiagaraStation
        BDevice device = findParentDevice(point);
        if (device == null) return null;
        if (!device.getType().is(NIAGARA_STATION)) return null;
        String stationName = device.getName();

        //////////////////////////////////////////////////////
        // we are sure the point is proxied.  now lets see if
        // there is a matching history

        // Make an id that matches the ones created in ensureLoaded()
        String proxyPointId = stationName + ";" + slotPath;

        // look up imported history
        synchronized(this)
        {
            ensureLoaded();
            return (BHistoryConfig) mapPointToConfig.get(proxyPointId);
        }
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

            String pointId = makeProxyPointId(cfg);
            mapPointToConfig.put(pointId, cfg);
            mapHistoryToPoint.put(hid, pointId);
        }

        // now that we've loaded all the histories, we'll set up a
        // listener so that we can keep our HashMap up-to-date
        service.getHistoryDb().addHistoryEventListener(new Listener());
    }

    /**
      * Examine a BHistoryConfig from an imported history to figure
      * out which point it goes with.
      *
      * @return a string ID of the form stationName + ";" + proxySlotPath
      */
    private static String makeProxyPointId(BHistoryConfig cfg)
    {
        // cannot be local history
        if (cfg.getId().getDeviceName().equals(Sys.getStation().getStationName()))
            throw new IllegalStateException();

        // For imported histories, the "source" OrdList seems 
        // to always have two ords, in the following order:
        //
        //      (1) the slotPath of the historyExt on the remote host,
        //      (2) the slotPath of the imported history on this host.  
        //
        // We want the first one, because that matches the 
        // "pointId" slotPath on the matching imported 
        // proxy point (if there is one)
        BOrd[] ords = cfg.getSource().toArray();
        String slotPath = ords[0].toString();

        // get rid of unneeded "station:" ordQuery
        if (slotPath.startsWith("station:|"))   
            slotPath = slotPath.substring("station:|".length());

        // The slotPath will be of the form "slot:/Path/To/Point/HistoryExt".
        // This doesn't *quite* match the "pointId" slot on the 
        // proxy point since it goes all the way to 
        // the History Ext.  So we'll get rid of that last slot.
        int last = slotPath.lastIndexOf("/");
        slotPath = slotPath.substring(0, last);

        // make an id that is a combo of the station name 
        // (a.k.a. device name) and slot path
        return cfg.getId().getDeviceName() + ";" + slotPath;
    }

    private static BDevice findParentDevice(BComplex cpx)
    {
        if (cpx == null) return null;

        if (cpx instanceof BDevice) return (BDevice) cpx;

        return findParentDevice(cpx.getParent());
    }

////////////////////////////////////////////////////////////////
// Listener
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

            synchronized(this)
            {
                String pointId = makeProxyPointId(cfg);
                mapPointToConfig.put(pointId, cfg);
                mapHistoryToPoint.put(hid, pointId);
            }
        }

        private void removeHistory(BHistoryId hid)
        {
            // ignore local histories
            if (hid.getDeviceName().equals(Sys.getStation().getStationName()))
                return;

            synchronized(this)
            {
                String pointId = (String) mapHistoryToPoint.get(hid);
                mapPointToConfig.remove(pointId);
                mapHistoryToPoint.remove(hid);
            }
        }
    }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private static final Type NIAGARA_PROXY_EXT;
    private static final Type NIAGARA_STATION;

    static
    {
        NIAGARA_PROXY_EXT = BTypeSpec.make("niagaraDriver:NiagaraProxyExt").getResolvedType();
        NIAGARA_STATION   = BTypeSpec.make("niagaraDriver:NiagaraStation").getResolvedType();
    }

    private final BNHaystackService service;

    private Map mapPointToConfig  = null; // proxyPointId -> BHistoryConfig
    private Map mapHistoryToPoint = null; // BHistoryId   -> proxyPointId
}

