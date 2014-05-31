//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   29 Mar 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;
import javax.baja.control.*;
import javax.baja.history.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.site.*;
import nhaystack.util.*;

/**
  * Cache stores various data structures that make it faster to look things up.
  */
class Cache
{
    Cache(NHServer server)
    {
        this.server = server;
    }

    /**
      * Rebuild the cache.
      */
    synchronized void rebuild()
    {
        long t0 = Clock.ticks();
        LOG.message("Begin cache rebuild.");

        LOG.trace("Rebuild cache, pass 1 of 4..."); 
        rebuildComponentCache_firstPass();

        LOG.trace("Rebuild cache, pass 2 of 4..."); 
        rebuildComponentCache_secondPass();

        LOG.trace("Rebuild cache, pass 3 of 4..."); 
        rebuildHistoryCache_firstPass();

        LOG.trace("Rebuild cache, pass 4 of 4..."); 
        rebuildHistoryCache_secondPass();

        lastRebuildTime = BAbsTime.now();
        long t1 = Clock.ticks();
        LOG.message("End cache rebuild " + (t1-t0) + "ms.");
        lastRebuildDuration = BRelTime.make(t1-t0);

        initialized = true;
    }

    /**
      * Get the history config that goes with the remote point, or return null.
      */
    synchronized BHistoryConfig getHistoryConfig(RemotePoint remotePoint)
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        return (BHistoryConfig) remoteToConfig.get(remotePoint);
    }

    /**
      * Get the control point that goes with the remote point, or return null.
      */
    synchronized BControlPoint getControlPoint(RemotePoint remotePoint)
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        return (BControlPoint) remoteToPoint.get(remotePoint);
    }

    /**
      * Return the implicit 'equip' for the point, or null.
      */
    BHEquip getImplicitEquip(BControlPoint point)
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        return (BHEquip) implicitEquips.get(point);
    }

    BHSite[] getAllSites()
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");
        return sites;
    }

    BHEquip[] getAllEquips()
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");
        return equips;
    }

    /**
      * Get all the equips associated with the given site navId.
      */
    BHEquip[] getNavSiteEquips(String siteNav)
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        BHSite site = (BHSite) siteNavs.get(siteNav);

        Array arr = (Array) siteEquips.get(site);
        return (arr == null) ?  
            new BHEquip[0] : 
            (BHEquip[]) arr.trim();
    }

    /**
      * Get all the points associated with the given equip navId.
      */
    BControlPoint[] getNavEquipPoints(String equipNav)
    {
        if (!initialized) throw new IllegalStateException("Cache is not initialized.");

        BHEquip equip = (BHEquip) equipNavs.get(equipNav);

        Array arr = (Array) equipPoints.get(equip);
        return (arr == null) ?  
            new BControlPoint[0] : 
            (BControlPoint[]) arr.trim();
    }

    /**
      * Get the stationNames for nav histories
      */
    String[] getNavHistoryStationNames()
    {
        Array arr = new Array(String.class, navHistories.keySet());
        return (String[]) arr.trim();
    }

    /**
      * Get the nav histories for the given stationName
      */
    BHistoryConfig[] getNavHistories(String stationName)
    {
        Array arr = (Array) navHistories.get(stationName);

        if (arr == null) 
            throw new BajaRuntimeException(
                "No nav histories found for '" + stationName + "'");

        return (BHistoryConfig[]) arr.trim();
    }

    /**
      * Return the BComponent that is associate with the SepRef id
      */
    BComponent lookupComponentBySepRef(NHRef id)
    {
        return (BComponent) sepRefToComp.get(id);
    }

    /**
      * Return the SepRef id that is associate with the component.
      */
    NHRef lookupSepRefByComponent(BComponent comp)
    {
        return (NHRef) compToSepRef.get(comp);
    }

////////////////////////////////////////////////////////////////
// private -- component space
////////////////////////////////////////////////////////////////

    private void rebuildComponentCache_firstPass()
    {
        remoteToPoint  = new HashMap();
        implicitEquips = new HashMap();
        siteNavs  = new HashMap();
        equipNavs = new HashMap();
        siteEquips  = new HashMap();
        equipPoints = new HashMap();
        sepRefToComp = new HashMap();
        compToSepRef = new HashMap();

        Array sitesArr = new Array(BHSite.class);
        Array equipsArr = new Array(BHEquip.class);
        numPoints = 0;

        BHEquip curImplicitEquip = null;
        int curImplicitDepth = -1;

        ComponentTreeIterator iterator = new ComponentTreeIterator(
            (BComponent) BOrd.make("slot:/").resolve(server.getService(), null).get());

        while (iterator.hasNext())
        {
            BComponent comp = (BComponent) iterator.next();

            HDict tags = BHDict.findTagAnnotation(comp);

            // set implicit equip 
            Cursor cursor = comp.getProperties();
            if (cursor.next(BHEquip.class))
            {
                curImplicitEquip = (BHEquip) cursor.get();
                curImplicitDepth = iterator.getStackDepth();
            }
            // clear implicit equip once it goes out of scope
            else
            {
                if (iterator.getStackDepth() <= curImplicitDepth)
                {
                    curImplicitEquip = null;
                    curImplicitDepth = -1;
                }
            }

            // point
            if (comp instanceof BControlPoint)
            {
                BControlPoint point = (BControlPoint) comp;
                numPoints++;

                // BControlPoints always have tags generated
                if (tags == null) tags = HDict.EMPTY;

                // save remote point 
                if (point.getProxyExt().getType().is(RemotePoint.NIAGARA_PROXY_EXT)) 
                {
                    RemotePoint remote = RemotePoint.fromControlPoint(point);
                    if (remote != null) remoteToPoint.put(remote, point);
                }

                // explicit equip
                if (tags.has("equipRef"))
                {
                    HRef ref = tags.getRef("equipRef");
                    BHEquip equip = (BHEquip) server.getTagManager().lookupComponent(ref);
                    addPointToEquip(equip, point);
                }
                // implicit equip
                else
                {
                    if (curImplicitEquip != null)
                    {
                        addPointToEquip(curImplicitEquip, point);
                        implicitEquips.put(point, curImplicitEquip);
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
                        Nav.makeSiteNavId(
                            Nav.makeNavName(comp, tags)),
                        comp);
                }
                else if (comp instanceof BHEquip)
                {
                    equipsArr.add(comp);
                    processEquip((BHEquip) comp);
                }
            }
        }

        sites  = (BHSite[]) sitesArr.trim();
        equips = (BHEquip[]) equipsArr.trim();
        numSites = sites.length;
        numEquips = equips.length;
    }

    private void addPointToEquip(BHEquip equip, BControlPoint point)
    {
        Array arr = (Array) equipPoints.get(equip);
        if (arr == null)
            equipPoints.put(equip, arr = new Array(BControlPoint.class));
        arr.add(point);
    }

    private void addEquipToSite(BHSite site, BHEquip equip)
    {
        Array arr = (Array) siteEquips.get(site);
        if (arr == null)
            siteEquips.put(site, arr = new Array(BHEquip.class));
        arr.add(equip);
    }

    private void processEquip(BHEquip equip)
    {
        HDict equipTags = BHDict.findTagAnnotation(equip);
        if (equipTags.has("siteRef"))
        {
            HRef ref = equipTags.getRef("siteRef");
            BHSite site = (BHSite) server.getTagManager().lookupComponent(ref);
            if (site != null)
            {
                addEquipToSite(site, equip);

                // save the equip nav 
                HDict siteTags = BHDict.findTagAnnotation(site);
                equipNavs.put(
                    Nav.makeEquipNavId(
                        Nav.makeNavName(site, siteTags),
                        Nav.makeNavName(equip, equipTags)),
                    equip);
            }
        }
    }

    private void rebuildComponentCache_secondPass()
    {
        for (int i = 0; i < sites.length; i++)
        {
            BHSite site = sites[i];

            // make ref for site
            HDict siteTags = site.getHaystack().getDict();
            String siteNav = Nav.makeNavName(site, siteTags);
            NHRef siteRef = makeSepRef(new String[] { siteNav });

//System.out.println(">>>>: " + site.getSlotPath() + ", " + siteRef.getHRef());

            // save bi-directional lookup for site
            sepRefToComp.put(siteRef, site);
            compToSepRef.put(site, siteRef);

            // lookup equips for site
            Array arr = (Array) siteEquips.get(site);
            BHEquip[] equips = (arr == null) ?  
                new BHEquip[0] : (BHEquip[]) arr.trim();

            // iterate through equips for site
            for (int j = 0; j < equips.length; j++)
            {
                BHEquip equip = equips[j];

                // make ref for equip
                HDict equipTags = equip.getHaystack().getDict();
                String equipNav = Nav.makeNavName(equip, equipTags);
                NHRef equipRef = makeSepRef(new String[] { siteNav, equipNav });

//System.out.println(">>>>: " + equip.getSlotPath() + ", " + equipRef.getHRef());

                // save bi-directional lookup for equip
                sepRefToComp.put(equipRef, equip);
                compToSepRef.put(equip, equipRef);

                // lookup points for equip
                arr = (Array) equipPoints.get(equip);
                BControlPoint[] points = (arr == null) ?  
                    new BControlPoint[0] : (BControlPoint[]) arr.trim();

                // iterate through points for equip
                for (int k = 0; k < points.length; k++)
                {
                    BControlPoint point = points[k];

                    // make ref for point
                    HDict pointTags = BHDict.findTagAnnotation(point);
                    if (pointTags == null) pointTags = HDict.EMPTY;
                    String pointNav = Nav.makeNavName(point, pointTags);
                    NHRef pointRef = makeSepRef(new String[] { siteNav, equipNav, pointNav });

//System.out.println(">>>>: " + point.getSlotPath() + ", " + pointRef.getHRef());

                    // save bi-directional lookup for point
                    sepRefToComp.put(pointRef, point);
                    compToSepRef.put(point, pointRef);
                }
            }
        }
    }

    private static NHRef makeSepRef(String[] navPath)
    {
        return NHRef.make(
            NHRef.SEP, 
            PathUtil.fromNiagaraPath(
                TextUtil.join(navPath, '/')));
    }

////////////////////////////////////////////////////////////////
// private -- history space
////////////////////////////////////////////////////////////////

    private void rebuildHistoryCache_firstPass()
    {
        remoteToConfig = new HashMap();
        navHistories = new TreeMap();

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

    private void rebuildHistoryCache_secondPass()
    {
        Iterator itr = new HistoryDbIterator(server.getService().getHistoryDb());
        while (itr.hasNext())
        {
            BHistoryConfig cfg = (BHistoryConfig) itr.next();

            if (server.getSpaceManager().isVisibleHistory(cfg))
            {
                String stationName = cfg.getId().getDeviceName();

                Array arr = (Array) navHistories.get(stationName);
                if (arr == null)
                    navHistories.put(stationName, arr = new Array(BHistoryConfig.class));
                arr.add(cfg);
            }
        }
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private final NHServer server;
    private boolean initialized = false;

    private Map remoteToConfig = null; // RemotePoint -> BHistoryConfig
    private Map remoteToPoint  = null; // RemotePoint -> BControlPoint
    private Map navHistories   = null; // stationName -> Array<BHistoryConfig>

    private BHSite[] sites = new BHSite[0];
    private BHEquip[] equips = new BHEquip[0];

    private Map implicitEquips = null; // BControlPoint -> BHEquip
    private Map siteNavs       = null; // String -> BHSite
    private Map equipNavs      = null; // String -> BHEquip
    private Map siteEquips     = null; // BHSite -> Array<BHEquip>
    private Map equipPoints    = null; // BHEquip -> Array<BControlPoint>

    private Map sepRefToComp = null; // NHRef -> BComponent
    private Map compToSepRef = null; // BComponent -> NHRef

    int numSites = 0;
    int numEquips = 0;
    int numPoints = 0;
    BRelTime lastRebuildDuration = BRelTime.DEFAULT;
    BAbsTime lastRebuildTime = BAbsTime.DEFAULT;
}

