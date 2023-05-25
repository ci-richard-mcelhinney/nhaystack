//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   29 Mar 2013  Mike Jarmy       Creation
//   09 May 2018  Eric Anderson    Added use of generics
//   26 Sep 2018  Andrew Saunders  Added shared constants for siteRef and equipRef tag names
//   21 Dec 2018  Andrew Saunders  Allowing plain components to be used as sites and equips
//   13 Mar 2019  Andrew Saunders  Added spy
//
package nhaystack.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.baja.control.BControlPoint;
import javax.baja.history.BHistoryConfig;
import javax.baja.history.BHistoryId;
import javax.baja.history.BIHistory;
import javax.baja.naming.BOrd;
import javax.baja.schedule.BWeeklySchedule;
import javax.baja.spy.SpyWriter;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BComponent;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Property;
import javax.baja.sys.SlotCursor;
import javax.baja.sys.Sys;
import javax.baja.tag.*;

import nhaystack.BHDict;
import nhaystack.NHRef;
import nhaystack.collection.ComponentTreeIterator;
import nhaystack.collection.HistoryDbIterator;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
import nhaystack.site.BHTagged;
import nhaystack.util.NHaystackConst;
import org.projecthaystack.HDict;
import org.projecthaystack.HRef;

/**
  * Cache stores various data structures that make it faster to look things up.
  */
class Cache implements NHaystackConst
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

        // rebuildCache runs 'permission-less', so lets remove the
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
            schedMgr.makePointEvents(scheduledPoints.toArray(EMPTY_COMPONENT_ARRAY));

            lastRebuildTime = BAbsTime.now();
            long t1 = Clock.ticks();
            LOG.fine("End cache rebuild " + (t1-t0) + "ms.");
            lastRebuildDuration = BRelTime.make(t1-t0);

            stats.setNumSites(sites.size());
            stats.setNumEquips(equips.size());
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
        return remoteToConfig.get(remotePoint);
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

        return remoteToPoint.get(remotePoint);
    }

    /**
      * Return the implicit 'equip' for the point, or null.
      */
    synchronized BComponent getImplicitEquip(BComponent point)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);
        return implicitEquips.get(point);
    }

    /**
     * Return the parent space or equip referenced by the entity, if any, else
     * null.
     *
     * @param entity Entity to return parent of.
     * @return Parent component or null.
     */
    synchronized BComponent getParent(BComponent entity)
    {
        requireInitialized();
        return parents.get(entity);
    }

    /**
     * Return the child spaces or equips which reference the entity.
     *
     * @param entity Entity to return children of.
     * @return Child components.
     */
    synchronized BComponent[] getChildren(BComponent entity)
    {
        requireInitialized();
        return compsToArr(children.get(entity));
    }

    synchronized BComponent[] getAllSites()
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);
        return sites.toArray(EMPTY_COMPONENT_ARRAY);
    }

    synchronized BComponent[] getAllEquips()
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);
        return equips.toArray(EMPTY_COMPONENT_ARRAY);
    }

    /**
     * Get all the spaces associated with the given site navId.
     *
     * @param siteNav Site navId query.
     * @return Spaces within that site.
     */
    synchronized BComponent[] getNavSiteSpaces(String siteNav)
    {
        requireInitialized();

        return compsToArr(siteSpaces.get(siteNavs.get(siteNav)));
    }

    /**
      * Get all the equips associated with the given site navId.
      */
    synchronized BComponent[] getNavSiteEquips(String siteNav)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        Collection<BComponent> arr = siteEquips.get(siteNavs.get(siteNav));
        return arr == null ? EMPTY_COMPONENT_ARRAY : arr.toArray(EMPTY_COMPONENT_ARRAY);
    }

    /**
      * Get all the points associated with the given equip navId.
      */
    synchronized BComponent[] getNavEquipPoints(String equipNav)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);
        return getEquipPoints(equipNavs.get(equipNav));
    }

    /**
     * Get all the points associated with the given space.
     *
     * @param space Space to get associated points for.
     * @return Points associated with the given space.
     */
    synchronized BComponent[] getSpacePoints(BComponent space)
    {
        return compsToArr(spacePoints.get(space));
    }

    /**
      * Get all the points associated with the given equip.
      */
    synchronized BComponent[] getEquipPoints(BComponent equip)
    {
        Collection<BComponent> arr = equipPoints.get(equip);
        return arr == null ? EMPTY_COMPONENT_ARRAY : arr.toArray(EMPTY_COMPONENT_ARRAY);
    }

    /**
      * Get the stationNames for nav histories
      */
    synchronized String[] getNavHistoryStationNames()
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);
        return navHistories.keySet().toArray(EMPTY_STRING_ARRAY);
    }

    /**
      * Get the nav histories for the given stationName
      */
    synchronized BHistoryConfig[] getNavHistories(String stationName)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);

        Collection<BHistoryConfig> arr = navHistories.get(stationName);

        if (arr == null) 
            throw new BajaRuntimeException(
                "No nav histories found for '" + stationName + '\'');

        return arr.toArray(EMPTY_HISTORY_CONFIG_ARRAY);
    }

    /**
      * Return the BComponent that is associate with the SepRef id, or null.
      */
    synchronized BComponent lookupComponentBySepRef(NHRef id)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);
        return sepRefToComp.get(id);
    }

    /**
      * Return the SepRef id that is associate with the component, or null.
      */
    synchronized NHRef lookupSepRefByComponent(BComponent comp)
    {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);
        return compToSepRef.get(comp);
    }

////////////////////////////////////////////////////////////////
// private -- component space
////////////////////////////////////////////////////////////////

    /**
      * rebuildComponentCache_firstPass
      */
    private void rebuildComponentCache_firstPass()
    {
        remoteToPoint = new HashMap<>();
        parents = new HashMap<>();
        implicitEquips = new HashMap<>();
        siteNavs = new HashMap<>();
        spaceNavs = new HashMap<>();
        equipNavs = new HashMap<>();
        siteSpaces = new HashMap<>();
        children = new HashMap<>();
        siteEquips = new HashMap<>();
        spaceEquips = new HashMap<>();
        spacePoints = new HashMap<>();
        equipPoints = new HashMap<>();
        sepRefToComp = new HashMap<>();
        compToSepRef = new HashMap<>();
        scheduledPoints = new ArrayList<>();

        sites = new ArrayList<>();
        spaces = new ArrayList<>();
        equips = new ArrayList<>();
        Stack<ImplicitEquip> implicitEquipStack = new Stack<>();
        numPoints = 0;

        ComponentTreeIterator iterator = new ComponentTreeIterator(
            (BComponent) BOrd.make("slot:/").resolve(server.getService(), null).get());

        while (iterator.hasNext())
        {
            BComponent comp = iterator.next();

            // push implicit equip 
            SlotCursor<Property> cursor = comp.getProperties();
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
                if (!implicitEquipStack.isEmpty())
                {
                    ImplicitEquip ie = implicitEquipStack.peek();
                    if (iterator.getStackDepth() <= ie.depth)
                        implicitEquipStack.pop();
                }
            }

            // get cur implicit
            BHEquip curImplicitEquip = implicitEquipStack.isEmpty() ?
                null : implicitEquipStack.peek().equip;

            processComponent(comp, curImplicitEquip);
        }
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
    private void processComponent(BComponent comp, BComponent curImplicitEquip)
    {
        HDict tags = BHDict.findTagAnnotation(comp);
        if (tags == null) tags = HDict.EMPTY;

        if (comp instanceof BControlPoint)
        {
            // point
            BControlPoint point = (BControlPoint) comp;
            numPoints++;

            if (tags.has("weeklySchedule") && tags.has("schedulable"))
                scheduledPoints.add(point);

            // save remote point 
            RemotePoint remote = RemotePoint.fromControlPoint(point);
            if (remote != null) remoteToPoint.put(remote, point);

            handleEquip(point, tags, curImplicitEquip);
        }
        else if (comp instanceof BWeeklySchedule)
        {
            // schedule
            BWeeklySchedule sched = (BWeeklySchedule) comp;
            numPoints++;

            handleEquip(sched, tags, curImplicitEquip);
        }
        else if (comp instanceof BHTagged)
        {
            // auto-tagged site and equip
            if (comp instanceof BHSite)
            {
                sites.add(comp);
                siteNavs.put(
                    Nav.makeSiteNavId(Nav.makeNavName(comp, tags)),
                    comp);
            }
            else if (comp instanceof BHEquip)
            {
                equips.add(comp);
                processEquip(comp);
            }
        }
        else if(comp.tags().contains(ID_SITE))
        {
            sites.add(comp);
            siteNavs.put(Nav.makeSiteNavId(Nav.makeNavName(comp, tags)), comp);
        }
        else if (comp.tags().contains(ID_EQUIP))
        {
            equips.add(comp);
            processEquip(comp);
        }
        else if (comp.tags().contains(ID_SPACE))
        {
            spaces.add(comp);
            processSpace(comp);
        }
    }

    /**
     * Attempt to find an equip reference and a space reference for a point
     * component and cache those associations.
     *
     * @param component Point component to find references for.
     * @param tags Tags of the component.
     * @param curImplicitEquip Implicit equip to fall back to.
     */
    private void handleEquip(
      BComponent component, HDict tags, BComponent curImplicitEquip
    )
    {
        Optional<BComponent> equipOpt = findReferencedEquip(component, tags);
        if (equipOpt.isPresent())
        {
            // explicit equip
            addPointToEquip(equipOpt.get(), component);
        }
        else if (curImplicitEquip != null) {
            // implicit equip
            addPointToEquip(curImplicitEquip, component);
            implicitEquips.put(component, curImplicitEquip);
        }

        // explicit space
        findReferencedSpace(component, tags).ifPresent(
          space -> addPointToSpace(space, component)
        );
    }

    /**
      * addPointToEquip
      */
    private void addPointToEquip(BComponent equip, BComponent point)
    {
        equipPoints.computeIfAbsent(equip, k -> new ArrayList<>()).add(point);
    }

    /**
     * Cache association of a point with a space.
     *
     * @param space Space to associate the point with.
     * @param point Point associated with the space.
     */
    private void addPointToSpace(BComponent space, BComponent point)
    {
        spacePoints.computeIfAbsent(space, k -> new ArrayList<>()).add(point);
    }

    /**
      * addEquipToSite
      */
    private void addEquipToSite(BComponent site, BComponent equip)
    {
        siteEquips.computeIfAbsent(site, k -> new ArrayList<>()).add(equip);
    }

    /**
     * Add a ref association from a child to a parent.
     *
     * @param parent Space or equip the child has a ref to.
     * @param child Child which has a ref to parent.
     */
    private void addChildToParent(BComponent parent, BComponent child)
    {
        children.computeIfAbsent(
          parent,
          k -> new ArrayList<>()
        ).add(child);
        parents.put(child, parent);
    }

    /**
     * Cache that an equip has a relation to a space.
     *
     * @param space Parent space related to by child.
     * @param equip Child equip relating to space.
     */
    private void addEquipToSpace(BComponent space, BComponent equip)
    {
        spaceEquips.computeIfAbsent(space, k -> new ArrayList<>()).add(equip);
    }

    /**
     * Cache that a space has a relation to a site.
     *
     * @param site Parent site related to by the child space.
     * @param space Child space related to parent site.
     */
    private void addSpaceToSite(BComponent site, BComponent space)
    {
        siteSpaces.computeIfAbsent(site, k -> new ArrayList<>()).add(space);
    }

    /**
     * Follow a reference of a provided type to find a referenced entity from
     * a provided entity.
     *
     * @param entity Entity to find reference from.
     * @param tags Tags on the entity.
     * @param refTag Tag to look for.
     * @param refId Relation id to look for.
     * @return Referenced entity if found, else empty.
     */
    private Optional<BComponent> findReferencedEntity(
      BComponent entity, HDict tags, String refTag, Id refId
    )
    {
        BComponent referenced = null;

        // Check for tagged reference.
        if (tags.has(refTag)) {
            HRef ref = tags.getRef(refTag);
            referenced = server.getTagManager().lookupComponent(ref);
        }

        // Check for Niagara relation to initialize entity.
        if (referenced == null)
        {
            Optional<Relation> relationOpt = entity.relations().get(refId);
            if (relationOpt.isPresent()) {
                Relation relation = relationOpt.get();
                if (relation.isOutbound()) {
                    referenced = (BComponent) relation.getEndpoint();
                }
            }
        }
        return Optional.ofNullable(referenced);
    }

    /**
     * Attempt to find the site component that the given entity is associated
     * with.
     *
     * @param entity Entity to attempt to find site for.
     * @param tags Tags on the entity.
     * @return Associated site component if found, else empty.
     */
    private Optional<BComponent> findReferencedSite(
      BComponent entity, HDict tags
    )
    {
        return findReferencedEntity(entity, tags, SITE_REF, ID_SITE_REF);
    }

    /**
     * Attempt to find the space component that the given entity is associated
     * with.
     *
     * @param entity Entity to attempt to find space for.
     * @param tags Tags on the entity.
     * @return Associated space component if found, else empty.
     */
    private Optional<BComponent> findReferencedSpace(
      BComponent entity, HDict tags
    )
    {
        return findReferencedEntity(entity, tags, SPACE_REF, ID_SPACE_REF);
    }

    /**
     * Attempt to find the equip component that the given entity is associated
     * with.
     *
     * @param entity Entity to attempt to find equip for.
     * @param tags Tags on the entity.
     * @return Associated equip component if found, else empty.
     */
    private Optional<BComponent> findReferencedEquip(
      BComponent entity, HDict tags
    )
    {
        return findReferencedEntity(entity, tags, EQUIP_REF, ID_EQUIP_REF);
    }

    /**
      * processEquip
      */
    private void processEquip(BComponent equip)
    {
        HDict equipTags = BHDict.findTagAnnotation(equip);
        if (equipTags == null)
        {
            equipTags = HDict.EMPTY;
        }

        Optional<BComponent> siteOpt = findReferencedSite(equip, equipTags);
        if (siteOpt.isPresent())
        {
            BComponent site = siteOpt.get();
            addEquipToSite(site, equip);

            // save the equip nav
            HDict siteTags = BHDict.findTagAnnotation(site);
            if (siteTags == null)
            {
                siteTags = HDict.EMPTY;
            }

            equipNavs.put(
                Nav.makeEquipNavId(
                    Nav.makeNavName(site, siteTags),
                    Nav.makeNavName(equip, equipTags)),
                equip);
        }

        // Add equip to relevant space, add association with parent.
        Optional<BComponent> equipOpt = findReferencedEquip(equip, equipTags);
        equipOpt.ifPresent(bComponent -> addChildToParent(bComponent, equip));
        findReferencedSpace(equip, equipTags).ifPresent(
          space -> {
              addEquipToSpace(space, equip);
              if (!equipOpt.isPresent()) {
                  addChildToParent(space, equip);
              }
          }
        );
    }

    /**
     * Process a component tagged as a space, attempting to find a site to
     * associate it with and cache an appropriate nav.
     *
     * @param space Space to process.
     */
    private void processSpace(BComponent space)
    {
        HDict spaceTags = BHDict.findTagAnnotation(space);
        if (spaceTags == null)
        {
            spaceTags = HDict.EMPTY;
        }

        Optional<BComponent> siteOpt = findReferencedSite(space, spaceTags);
        if (siteOpt.isPresent())
        {
            BComponent site = siteOpt.get();
            addSpaceToSite(site, space);

            // save the equip nav
            HDict siteTags = BHDict.findTagAnnotation(site);
            if (siteTags == null)
            {
                siteTags = HDict.EMPTY;
            }

            spaceNavs.put(
                Nav.makeEquipNavId(
                    Nav.makeNavName(site, siteTags),
                    Nav.makeNavName(space, spaceTags)
                ),
              space
            );
        }

        findReferencedSpace(space, spaceTags).ifPresent(
            parent -> addChildToParent(parent, space)
        );
    }

    /**
      * rebuildComponentCache_secondPass
      */
    private void rebuildComponentCache_secondPass()
    {
        for (BComponent site : sites)
        {
            // make ref for site
            HDict siteTags = site instanceof BHSite ? ((BHSite)site).getHaystack().getDict() : HDict.EMPTY;
            String siteNav = Nav.makeNavName(site, siteTags);
            NHRef siteRef = TagManager.makeSepRef(new String[] { siteNav });

            // save bi-directional lookup for site
            sepRefToComp.put(siteRef, site);
            compToSepRef.put(site, siteRef);

            // iterate through equips for site
            for (BComponent equip : siteEquips.getOrDefault(site, Collections.emptyList()))
            {
                // make ref for equip
                HDict equipTags = equip instanceof BHEquip ? ((BHEquip)equip).getHaystack().getDict() : HDict.EMPTY;
                String equipNav = Nav.makeNavName(equip, equipTags);
                NHRef equipRef = TagManager.makeSepRef(new String[] { siteNav, equipNav });

                // save bi-directional lookup for equip
                sepRefToComp.put(equipRef, equip);
                compToSepRef.put(equip, equipRef);

                // iterate through points for equip
                for (BComponent point : equipPoints.getOrDefault(equip, Collections.emptyList()))
                {
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
        remoteToConfig = new HashMap<>();
        navHistories = new TreeMap<>();

        BIHistory[] histories = server.getService().getHistoryDb().getHistories();
        for (BIHistory h : histories)
        {
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
        Iterator<BHistoryConfig> itr = new HistoryDbIterator(server.getService().getHistoryDb());
        while (itr.hasNext())
        {
            BHistoryConfig cfg = itr.next();

            if (server.getSpaceManager().isVisibleHistory(cfg))
            {
                String stationName = cfg.getId().getDeviceName();
                navHistories.computeIfAbsent(stationName, k -> new ArrayList<>()).add(cfg);
            }
        }
    }

////////////////////////////////////////////////////////////////
// private -- helpers
////////////////////////////////////////////////////////////////

    /**
     * Throw an exception if the cache is not initialized.
     */
    private void requireInitialized() {
        if (!initialized) throw new IllegalStateException(NOT_INITIALIZED);
    }

    /**
     * Add a collection of components to a new array. If comps is null, returns
     * an empty array, otherwise returns and array of the components in the
     * collection.
     *
     * @param comps Components to add to array.
     * @return Array of components in the collection.
     */
    private static BComponent[] compsToArr(Collection<BComponent> comps) {
        return (
          comps == null
            ? EMPTY_COMPONENT_ARRAY
            : comps.toArray(EMPTY_COMPONENT_ARRAY)
        );
    }

////////////////////////////////////////////////////////////////
// spy
////////////////////////////////////////////////////////////////

    public void spy(SpyWriter out) throws Exception {
        out.startProps();
        out.trTitle("Cache SiteNavs", 2);
        for (Map.Entry<String, BComponent> siteNav : siteNavs.entrySet())
        {
            out.prop(siteNav.getKey(), siteNav.getValue().getSlotPath());
        }
        out.endProps();

        out.startProps();
        out.trTitle("Cache EquipNavs", 2);
        for (Map.Entry<String, BComponent> nav : equipNavs.entrySet())
        {
            out.prop(nav.getKey(), nav.getValue().getSlotPath());
        }
        out.endProps();

        out.startProps();
        out.trTitle("Cache ImplicitEquips", 2);
        for (Map.Entry<BComponent, BComponent> nav : implicitEquips.entrySet())
        {
            out.prop(nav.getKey().getSlotPath(), nav.getValue().getSlotPath());
        }
        out.endProps();

        out.startProps();
        out.trTitle("Cache SiteEquips", 2);
        for (Map.Entry<BComponent, Collection<BComponent>> nav : siteEquips.entrySet())
        {
            String site = nav.getKey().getSlotPath().toString();
            for (BComponent component : nav.getValue())
            {
                out.prop(site, component.getSlotPath());
                site = "";
            }
        }
        out.endProps();

        out.startProps();
        out.trTitle("Cache EquipPoints", 2);
        for (Map.Entry<BComponent, Collection<BComponent>> nav : equipPoints.entrySet())
        {
            String equip = nav.getKey().getSlotPath().toString();
            for (BComponent component : nav.getValue())
            {
                out.prop(equip, component.getSlotPath());
                equip = "";
            }
        }
        out.endProps();
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

    private static final BComponent[] EMPTY_COMPONENT_ARRAY = new BComponent[0];
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final BHistoryConfig[] EMPTY_HISTORY_CONFIG_ARRAY = new BHistoryConfig[0];

    private static final Logger LOG = Logger.getLogger("nhaystack");

    private final NHServer server;
    private final ScheduleManager schedMgr;
    private boolean initialized;

    private Map<RemotePoint, BHistoryConfig> remoteToConfig;
    private Map<RemotePoint, BControlPoint> remoteToPoint;
    private Map<String, Collection<BHistoryConfig>> navHistories;

    private Collection<BComponent> sites = Collections.emptyList();
    private Collection<BComponent> spaces = Collections.emptyList();
    private Collection<BComponent> equips = Collections.emptyList();

    /**
     * Map from child component to parent component, by ref. For example an
     * equip with an equip ref to a parent equipment would be placed in here
     * with the parent as value. An equip or space with a space ref would also
     * be placed here. Equip refs are prioritised above space refs for
     * parenthood.
     */
    private Map<BComponent, BComponent> parents;
    private Map<BComponent, BComponent> implicitEquips;
    private Map<String, BComponent> siteNavs;
    private Map<String, BComponent> spaceNavs;
    private Map<String, BComponent> equipNavs;

    /**
     * Map from parent component to collection of child components, by ref. For
     * instance if a chiller has an equip ref to a plant, the plant would be the
     * key and the chiller would be added to the collection of children.
     */
    private Map<BComponent, Collection<BComponent>> children;
    private Map<BComponent, Collection<BComponent>> siteSpaces;
    private Map<BComponent, Collection<BComponent>> siteEquips;
    private Map<BComponent, Collection<BComponent>> equipPoints;
    private Map<BComponent, Collection<BComponent>> spaceEquips;
    private Map<BComponent, Collection<BComponent>> spacePoints;

    private Map<NHRef, BComponent> sepRefToComp;
    private Map<BComponent, NHRef> compToSepRef;

    private Collection<BComponent> scheduledPoints;

    private int numPoints;
    private BRelTime lastRebuildDuration = BRelTime.DEFAULT;
    private BAbsTime lastRebuildTime = BAbsTime.DEFAULT;
}
