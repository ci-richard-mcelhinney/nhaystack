//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   29 Mar 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;
import java.util.logging.*;

import javax.baja.control.*;
import javax.baja.history.*;
import javax.baja.naming.*;
import javax.baja.schedule.*;
import javax.baja.sys.*;
import javax.baja.nre.util.*;

import org.projecthaystack.*;
import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.site.*;

/**
  * Cache stores various data structures that make it faster to look things up.
  */
class Cache
{
    Cache(NHServer server, ScheduleManager schedMgr)
    {
        this.server = server;
        this.schedMgr = schedMgr;
    }

    /**
      * Rebuild the cache.
      */
    synchronized void rebuild(BNHaystackStats stats)
    {
        Thread thread = Thread.currentThread();
        Context cx = ThreadContext.getContext(thread);

        // rebuildCache runs 'permissionless', so lets remove the
        // current context and then put it back in when we are done
        if (cx != null) ThreadContext.removeContext(thread);

        try
        {
            long t0 = Clock.ticks();
            LOG.info("Begin cache rebuild.");

            LOG.fine("Rebuild cache: step 1 of 5...");
            rebuildComponentCache_firstPass();

            LOG.fine("Rebuild cache: step 2 of 5...");
            rebuildComponentCache_secondPass();

            LOG.fine("Rebuild cache: step 3 of 5...");
            rebuildHistoryCache_firstPass();

            LOG.fine("Rebuild cache: step 4 of 5...");
            rebuildHistoryCache_secondPass();
            initialized = true;

            LOG.fine("Rebuild cache: step 5 of 5...");
            schedMgr.makePointEvents((BComponent[]) scheduledPoints.trim());

            lastRebuildTime = BAbsTime.now();
            long t1 = Clock.ticks();
            LOG.fine("End cache rebuild " + (t1-t0) + "ms.");
            lastRebuildDuration = BRelTime.make(t1-t0);

            stats.setNumSites(numSites);
            stats.setNumEquips(numEquips);
            stats.setNumPoints(numPoints);
            stats.setLastCacheRebuildDuration(lastRebuildDuration);
            stats.setLastCacheRebuildTime(lastRebuildTime);
        }
        finally
        {
            if (cx != null) ThreadContext.putContext(thread, cx);
        }
    }

    /**
      * Get the history config that goes with the remote point, or return null.
      */
    synchronized BHistoryConfig getHistoryConfig(RemotePoint remotePoint)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        return (BHistoryConfig) remoteToConfig.get(remotePoint);
    }

    /**
      * Get the control point that goes with the remote point, or return null.
      */
    synchronized BControlPoint getControlPoint(RemotePoint remotePoint)
    {
        // skip this check, since this method gets called during
        // rebuildHistoryCache_secondPass()
        //
        //if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        return (BControlPoint) remoteToPoint.get(remotePoint);
    }

    /**
      * Return the implicit 'equip' for the point, or null.
      */
    synchronized BHEquip getImplicitEquip(BComponent point)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        return (BHEquip) implicitEquips.get(point);
    }

    synchronized BHSite[] getAllSites()
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);
        return sites;
    }

    synchronized BHEquip[] getAllEquips()
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);
        return equips;
    }

    /**
      * Get all the equips associated with the given site navId.
      */
    synchronized BHEquip[] getNavSiteEquips(String siteNav)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        BHSite site = (BHSite) siteNavs.get(siteNav);

        Array arr = (Array) siteEquips.get(site);
        return (arr == null) ?  
            new BHEquip[0] : 
            (BHEquip[]) arr.trim();
    }

    /**
      * Get all the points associated with the given equip navId.
      */
    synchronized BComponent[] getNavEquipPoints(String equipNav)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        BHEquip equip = (BHEquip) equipNavs.get(equipNav);
        return getEquipPoints(equip);
    }

    /**
      * Get all the points associated with the given equip.
      */
    synchronized BComponent[] getEquipPoints(BHEquip equip)
    {
        Array arr = (Array) equipPoints.get(equip);
        return (arr == null) ?  
            new BComponent[0] : 
            (BComponent[]) arr.trim();
    }

    /**
      * Get the stationNames for nav histories
      */
    synchronized String[] getNavHistoryStationNames()
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        Array arr = new Array(String.class, navHistories.keySet());
        return (String[]) arr.trim();
    }

    /**
      * Get the nav histories for the given stationName
      */
    synchronized BHistoryConfig[] getNavHistories(String stationName)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        Array arr = (Array) navHistories.get(stationName);

        if (arr == null) 
            throw new BajaRuntimeException(
                "No nav histories found for '" + stationName + "'");

        return (BHistoryConfig[]) arr.trim();
    }

    /**
      * Return the BComponent that is associate with the SepRef id, or null.
      */
    synchronized BComponent lookupComponentBySepRef(NHRef id)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        return (BComponent) sepRefToComp.get(id);
    }

    /**
      * Return the SepRef id that is associate with the component, or null.
      */
    synchronized NHRef lookupSepRefByComponent(BComponent comp)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        return (NHRef) compToSepRef.get(comp);
    }

////////////////////////////////////////////////////////////////
// private -- component space
////////////////////////////////////////////////////////////////

    /**
      * rebuildComponentCache_firstPass
      */
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
        scheduledPoints = new Array(BComponent.class);

        Array sitesArr = new Array(BHSite.class);
        Array equipsArr = new Array(BHEquip.class);
        Array implicitEquipStack = new Array(ImplicitEquip.class);
        numPoints = 0;

        ComponentTreeIterator iterator = new ComponentTreeIterator(
            (BComponent) BOrd.make("slot:/").resolve(server.getService(), null).get());

        while (iterator.hasNext())
        {
            BComponent comp = (BComponent) iterator.next();

            // push implicit equip 
            SlotCursor cursor = comp.getProperties();
            if (cursor.next(BHEquip.class))
            {
                implicitEquipStack.push(
                    new ImplicitEquip(
                        (BHEquip) cursor.get(),
                        iterator.getStackDepth()));
            }
            // pop implicit equip once it goes out of scope
            else
            {
                if (implicitEquipStack.size() > 0)
                {
                    ImplicitEquip ie = (ImplicitEquip) implicitEquipStack.peek();
                    if (iterator.getStackDepth() <= ie.depth)
                        implicitEquipStack.pop();
                }
            }

            // get cur implicit
            BHEquip curImplicitEquip = (implicitEquipStack.isEmpty()) ?
                null : ((ImplicitEquip) implicitEquipStack.peek()).equip;

            processComponent(comp, sitesArr, equipsArr, curImplicitEquip);
        }

        sites  = (BHSite[]) sitesArr.trim();
        equips = (BHEquip[]) equipsArr.trim();
        numSites = sites.length;
        numEquips = equips.length;
    }

    /**
      * ImplicitEquip
      */
    static class ImplicitEquip
    {
        ImplicitEquip(BHEquip equip, int depth) 
        {
            this.equip = equip;
            this.depth = depth;
        }

        final BHEquip equip;
        final int depth;
    }

    /**
      * processComponent
      */
    private void processComponent(
        BComponent comp,
        Array sitesArr,
        Array equipsArr,
        BHEquip curImplicitEquip)
    {
        HDict tags = BHDict.findTagAnnotation(comp);

        // point
        if (comp instanceof BControlPoint)
        {
            BControlPoint point = (BControlPoint) comp;
            numPoints++;

            // BControlPoints always have tags generated
            if (tags == null) tags = HDict.EMPTY;

            if (tags.has("weeklySchedule") && tags.has("schedulable"))
                scheduledPoints.add(point);

            // save remote point 
            RemotePoint remote = RemotePoint.fromControlPoint(point);
            if (remote != null) remoteToPoint.put(remote, point);

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
        // schedule
        else if (comp instanceof BWeeklySchedule)
        {
            BWeeklySchedule sched = (BWeeklySchedule) comp;
            numPoints++;

            // BAbstractSchedules always have tags generated
            if (tags == null) tags = HDict.EMPTY;

            // explicit equip
            if (tags.has("equipRef"))
            {
                HRef ref = tags.getRef("equipRef");
                BHEquip equip = (BHEquip) server.getTagManager().lookupComponent(ref);
                addPointToEquip(equip, sched);
            }
            // implicit equip
            else
            {
                if (curImplicitEquip != null)
                {
                    addPointToEquip(curImplicitEquip, sched);
                    implicitEquips.put(sched, curImplicitEquip);
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

    /**
      * addPointToEquip
      */
    private void addPointToEquip(BHEquip equip, BComponent point)
    {
        Array arr = (Array) equipPoints.get(equip);
        if (arr == null)
            equipPoints.put(equip, arr = new Array(BComponent.class));
        arr.add(point);
    }

    /**
      * addEquipToSite
      */
    private void addEquipToSite(BHSite site, BHEquip equip)
    {
        Array arr = (Array) siteEquips.get(site);
        if (arr == null)
            siteEquips.put(site, arr = new Array(BHEquip.class));
        arr.add(equip);
    }

    /**
      * processEquip
      */
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

    /**
      * rebuildComponentCache_secondPass
      */
    private void rebuildComponentCache_secondPass()
    {
        for (int i = 0; i < sites.length; i++)
        {
            BHSite site = sites[i];

            // make ref for site
            HDict siteTags = site.getHaystack().getDict();
            String siteNav = Nav.makeNavName(site, siteTags);
            NHRef siteRef = TagManager.makeSepRef(new String[] { siteNav });

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
                NHRef equipRef = TagManager.makeSepRef(new String[] { siteNav, equipNav });

                // save bi-directional lookup for equip
                sepRefToComp.put(equipRef, equip);
                compToSepRef.put(equip, equipRef);

                // lookup points for equip
                arr = (Array) equipPoints.get(equip);
                BComponent[] points = (arr == null) ?  
                    new BComponent[0] : (BComponent[]) arr.trim();

                // iterate through points for equip
                for (int k = 0; k < points.length; k++)
                {
                    BComponent point = points[k];

                    // make ref for point
                    HDict pointTags = BHDict.findTagAnnotation(point);
                    if (pointTags == null) pointTags = HDict.EMPTY;
                    String pointNav = Nav.makeNavName(point, pointTags);
                    NHRef pointRef = TagManager.makeSepRef(new String[] { siteNav, equipNav, pointNav });

                    // save bi-directional lookup for point
                    sepRefToComp.put(pointRef, point);
                    compToSepRef.put(point, pointRef);
                }
            }
        }
    }

////////////////////////////////////////////////////////////////
// private -- history space
////////////////////////////////////////////////////////////////

    /**
      * rebuildHistoryCache_firstPass
      */
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

    /**
      * rebuildHistoryCache_secondPass
      */
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
// access
////////////////////////////////////////////////////////////////

    boolean initialized() { return initialized; }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    static final String NOT_INITIALIZED = 
        "NHAYSTACK CACHE NOT INITIALIZED";

    private static final Logger LOG = Logger.getLogger("nhaystack");

    private final NHServer server;
    private final ScheduleManager schedMgr;
    private boolean initialized = false;

    private Map remoteToConfig = null; // RemotePoint -> BHistoryConfig
    private Map remoteToPoint  = null; // RemotePoint -> BComponent
    private Map navHistories   = null; // stationName -> Array<BHistoryConfig>

    private BHSite[] sites = new BHSite[0];
    private BHEquip[] equips = new BHEquip[0];

    private Map implicitEquips = null; // BComponent -> BHEquip
    private Map siteNavs       = null; // String -> BHSite
    private Map equipNavs      = null; // String -> BHEquip
    private Map siteEquips     = null; // BHSite -> Array<BHEquip>
    private Map equipPoints    = null; // BHEquip -> Array<BComponent>

    private Map sepRefToComp = null; // NHRef -> BComponent
    private Map compToSepRef = null; // BComponent -> NHRef

    private Array scheduledPoints = null; // BComponent

    private int numSites = 0;
    private int numEquips = 0;
    private int numPoints = 0;
    private BRelTime lastRebuildDuration = BRelTime.DEFAULT;
    private BAbsTime lastRebuildTime = BAbsTime.DEFAULT;
}

