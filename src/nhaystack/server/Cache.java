//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   29 Mar 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;
import javax.baja.collection.*;
import javax.baja.control.*;
import javax.baja.control.enums.*;
import javax.baja.history.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import haystack.*;
import haystack.server.*;
import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.server.storehouse.*;
import nhaystack.site.*;

/**
  * Cache stores various data structures that make it faster to look things up.
  */
public class Cache
{
    Cache(NHServer server)
    {
        this.server = server;
    }

    /**
      * Rebuild the cache.
      */
    public synchronized void rebuild()
    {
        long ticks = Clock.ticks();
        if (initialized)
        {
            LOG.message("Begin cache rebuild.");
            rebuildComponentCache();
            rebuildHistoryCache();
            LOG.message("End cache rebuild " + 
                (Clock.ticks()-ticks) + "ms.");
        }
        else
        {
            LOG.message("Begin cache build.");
            rebuildComponentCache();
            rebuildHistoryCache();
            initialized = true;
            LOG.message("End cache build " + 
                (Clock.ticks()-ticks) + "ms.");
        }
    }

    /**
      * Get the history config that goes with the remote point, or return null.
      */
    public synchronized BHistoryConfig getHistoryConfig(RemotePoint remotePoint)
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        return (BHistoryConfig) remoteToConfig.get(remotePoint);
    }

    /**
      * Get the control point that goes with the remote point, or return null.
      */
    public synchronized BControlPoint getControlPoint(RemotePoint remotePoint)
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        return (BControlPoint) remoteToPoint.get(remotePoint);
    }

    /**
      * Get everything that is tagged as 'site'
      */
    public BComponent[] getAllSites()
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        return sites;
    }

    /**
      * Get everything that is tagged as 'equip'
      */
    public BComponent[] getAllEquips()
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        return equips;
    }

    /**
      * Return the implicit 'equip' for the point, or null.
      */
    public BComponent getImplicitEquip(BControlPoint point)
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        return (BComponent) implicitEquips.get(point.getHandle());
    }

    /**
      * Get all the equips associated with the given site.
      */
    public BComponent[] getSiteEquips(SiteNavId siteNav)
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        BComponent site = (BComponent) siteNavs.get(siteNav);

        Array arr = (Array) siteEquips.get(site.getHandle());
        return (arr == null) ?  
            new BComponent[0] : 
            (BComponent[]) arr.trim();
    }

    /**
      * Get all the points associated with the given equip.
      */
    public BControlPoint[] getEquipPoints(EquipNavId equipNav)
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        BComponent equip = (BComponent) equipNavs.get(equipNav);

        Array arr = (Array) equipPoints.get(equip.getHandle());
        return (arr == null) ?  
            new BControlPoint[0] : 
            (BControlPoint[]) arr.trim();
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private void rebuildComponentCache()
    {
        remoteToPoint  = new HashMap();
        implicitEquips = new HashMap();
        siteNavs  = new HashMap();
        equipNavs = new HashMap();
        siteEquips  = new HashMap();
        equipPoints = new HashMap();

        ConfigStorehouse storehouse = server.getConfigStorehouse();

        Array sitesArr = new Array(BComponent.class);
        Array equipsArr = new Array(BComponent.class);

        Iterator iterator = new ComponentTreeIterator(
            (BComponent) BOrd.make("slot:/").resolve(server.getService(), null).get());

        while (iterator.hasNext())
        {
            BComponent comp = (BComponent) iterator.next();
            BHDict btags = BHDict.findTagAnnotation(comp);
            HDict tags = (btags == null) ? null : btags.getDict();

            // point
            if (comp instanceof BControlPoint)
            {
                BControlPoint point = (BControlPoint) comp;

                // save remote point 
                if (point.getProxyExt().getType().is(RemotePoint.NIAGARA_PROXY_EXT)) 
                {
                    RemotePoint remote = RemotePoint.fromControlPoint(point);
                    if (remote != null) remoteToPoint.put(remote, point);
                }

                // explicit equip
                if (tags != null && tags.has("equipRef"))
                {
                    HRef ref = tags.getRef("equipRef");
                    BComponent equip = server.lookupComponent(ref);
                    addBackwardsEquipPoint(equip, point);
                }
                // implicit equip
                else
                {
                    BComponent equip = findImplicitEquip(point);
                    if (equip != null)
                    {
                        addBackwardsEquipPoint(equip, point);
                        implicitEquips.put(point.getHandle(), equip);
                    }
                }
            }
            // auto-tagged site and equip
            else if (comp instanceof BHTagged)
            {
                if (comp instanceof BHSite)
                {
                    sitesArr.add(comp);
                    siteNavs.put(
                        SiteNavId.make(comp.getDisplayName(null)),
                        comp);
                }
                else if (comp instanceof BHEquip)
                {
                    equipsArr.add(comp);
                    processEquip(comp);
                }
            }
            // implicit equip
            else
            {
                if ((tags != null) && tags.has("equip"))
                {
                    equipsArr.add(comp);
                    processEquip(comp);
                }
            }
        }

        sites  = (BComponent[]) sitesArr.trim();
        equips = (BComponent[]) equipsArr.trim();
    }

    /**
      * This will save a reference to the point from its equip.
      */
    private void addBackwardsEquipPoint(BComponent equip, BControlPoint point)
    {
        Array arr = (Array) equipPoints.get(equip.getHandle());
        if (arr == null)
            equipPoints.put(equip.getHandle(), arr = new Array(BControlPoint.class));
        arr.add(point);
    }

    /**
      * This will save a reference to the equip from its site.
      */
    private void addBackwardsSiteEquip(BComponent site, BComponent equip)
    {
        Array arr = (Array) siteEquips.get(site.getHandle());
        if (arr == null)
            siteEquips.put(site.getHandle(), arr = new Array(BComponent.class));
        arr.add(equip);
    }

    private void processEquip(BComponent equip)
    {
        BHDict btags = BHDict.findTagAnnotation(equip);
        if (btags != null)
        {
            HDict tags = btags.getDict();
            if (tags.has("siteRef"))
            {
                HRef ref = tags.getRef("siteRef");
                BComponent site = server.lookupComponent(ref);
                if (site != null)
                {
                    // add backwards reference
                    addBackwardsSiteEquip(site, equip);

                    // save the equip nav too
                    equipNavs.put(
                        EquipNavId.make(
                            site.getDisplayName(null),
                            equip.getDisplayName(null)),
                        equip);
                }
            }
        }
    }

    private void rebuildHistoryCache()
    {
        remoteToConfig = new HashMap();
        HistoryStorehouse storehouse = server.getHistoryStorehouse();

        BIHistory[] histories = server.getService().getHistoryDb().getHistories(); 
        for (int i = 0; i < histories.length; i++)
        {
            BIHistory h = histories[i];
            BHistoryId hid = h.getId();

            // ignore local histories
            if (hid.getDeviceName().equals(Sys.getStation().getStationName()))
                continue;

            BHistoryConfig cfg = h.getConfig();
            RemotePoint remotePoint = RemotePoint.fromHistoryConfig(cfg);
            if (remotePoint != null)
                remoteToConfig.put(remotePoint, cfg);
        }
    }

    /**
      * Find a parent device that has a BHEquip child
      */
    private BComponent findImplicitEquip(BControlPoint point)
    {
        BComponent parent = (BComponent) point.getParent();

        while (true)
        {
            if (parent == null) return null;

            BHDict btags = BHDict.findTagAnnotation(parent);
            if (btags != null && btags.getDict().has("equip"))
                return parent;

            parent = (BComponent) parent.getParent();
        }
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private final NHServer server;
    private boolean initialized = false;

    private Map remoteToConfig  = null; // RemotePoint -> BHistoryConfig
    private Map remoteToPoint   = null; // RemotePoint -> BControlPoint
    private BComponent[] sites  = null;
    private BComponent[] equips = null;
    private Map implicitEquips  = null; // Handle -> BComponent
    private Map siteNavs  = null; // SiteNavId -> BComponent
    private Map equipNavs = null; // EquipNavId -> BComponent
    private Map siteEquips  = null; // Handle -> Array<BComponent>
    private Map equipPoints = null; // Handle -> Array<BControlPoint>
}

