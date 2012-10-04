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
  * HistoryManager is responsible for keeping track
  * of which BControlPoints go with which BHistoryConfigs.
  */
public class HistoryManager
{
    public HistoryManager(BNHaystackService service)
    {
        this.service = service;
    }

    /**
      * Try to find either a local or imported history for the point
      */
    public BHistoryConfig lookupHistoryConfig(BControlPoint point)
    {
        // look for local history
        BHistoryExt historyExt = lookupHistoryExt(point);
        if (historyExt != null) return historyExt.getHistoryConfig();

        // look for history that goes with a proxied point
        return lookupImportedHistory(point);
    }

    /**
      * Check if there is a history extension.  This will succeed
      * if the point lives in this station, rather than being proxied.
      */
    public static BHistoryExt lookupHistoryExt(BControlPoint point)
    {
        Cursor cursor = point.getProperties();
        if (cursor.next(BHistoryExt.class))
            return (BHistoryExt) cursor.get();

        return null;
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    /**
      * find history for a proxied point, if any
      */
    private synchronized BHistoryConfig lookupImportedHistory(BControlPoint point)
    {
        //////////////////////////////////////////////////////
        // first, let's determine whether the point 
        // actually is proxied

        // Check for a NiagaraProxyExt
        BAbstractProxyExt proxyExt = point.getProxyExt();
        if (!proxyExt.getType().is(NIAGARA_PROXY_EXT)) return null;

        // "pointId" seems to always containt the slot path on 
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

        // make an id that matches the ones from ensureImportedHistoriesLoaded
        String remoteId = stationName + ";station:|" + slotPath;

        // look up imported history
        ensureImportedHistoriesLoaded();
        return (BHistoryConfig) importedHistories.get(remoteId);
    }

    /**
      * stash away all the imported histories
      */
    private void ensureImportedHistoriesLoaded()
    {
        if (importedHistories != null) return;

        importedHistories = new HashMap();

        BIHistory[] histories = service.getHistoryDb().getHistories(); 
        for (int i = 0; i < histories.length; i++)
        {
            BIHistory h = histories[i];
            BHistoryId hid = h.getId();
            BHistoryConfig cfg = h.getConfig();

            // ignore local histories
            if (hid.getDeviceName().equals(Sys.getStation().getStationName()))
                continue;

            // For imported histories, the source seems to always
            // have two ords -- the ord on the remote host,
            // and the ord on this host.  We want the ord-on-remote-host,
            // because that matches the "pointId" slot on the 
            // matching imported proxy point (if there is one)
            BOrd[] ords = cfg.getSource().toArray();
            if (ords.length != 2) continue;
            String ord = ords[0].toString();

            // The ord will be of the form 
            // "station:|slot:/Path/To/Point/NumericCov".
            // This doesn't *quite* match the "pointId" slot on the 
            // proxy point since our ord goes all the way to 
            // the History Ext.  So we'll get rid of that last slot.
            int last = ord.lastIndexOf("/");
            ord = ord.substring(0, last);

            // make an id that is a combo of the station name 
            // (a.k.a. device name) and slot path
            String remoteId = hid.getDeviceName() + ";" + ord;

            // save
            importedHistories.put(remoteId, cfg);
        }

        // now that we've loaded all the histories, we'll set up a
        // listener so that we can keep our HashMap up-to-date
        service.getHistoryDb().addHistoryEventListener(new Listener());
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

// TODO
// and don't forget to synchronize!

//            switch(event.getId())
//            {
//    BHistoryEvent.CREATED:
//    BHistoryEvent.DELETED:
//    BHistoryEvent.RENAMED:
//    BHistoryEvent.CONFIG_CHANGED:
//            }

        }
    }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private static final Type NIAGARA_PROXY_EXT = 
        BTypeSpec.make("niagaraDriver:NiagaraProxyExt").getResolvedType();

    private static final Type NIAGARA_STATION = 
        BTypeSpec.make("niagaraDriver:NiagaraStation").getResolvedType();

    private final BNHaystackService service;
    private Map importedHistories = null;
}

