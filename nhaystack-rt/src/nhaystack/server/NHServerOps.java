//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   05 Jun 2014  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Added missing @Overrides annotations, added use of generics
//
package nhaystack.server;

import static nhaystack.server.HaystackSlotUtil.replaceHaystackSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.baja.control.BControlPoint;
import javax.baja.control.BIWritablePoint;
import javax.baja.naming.BOrd;
import javax.baja.nre.util.TextUtil;
import javax.baja.security.PermissionException;
import javax.baja.sys.Action;
import javax.baja.sys.BComponent;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import nhaystack.BHDict;
import nhaystack.BHGrid;
import nhaystack.NHRef;
import nhaystack.collection.ComponentTreeIterator;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
import nhaystack.site.BHTagged;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HBool;
import org.projecthaystack.HDateTimeRange;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HHisItem;
import org.projecthaystack.HMarker;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HRow;
import org.projecthaystack.HStr;
import org.projecthaystack.HUri;
import org.projecthaystack.HVal;
import org.projecthaystack.HWatch;
import org.projecthaystack.io.HZincReader;
import org.projecthaystack.server.HOp;
import org.projecthaystack.server.HServer;

/**
  * Custom Ops for NHServer
  */
final class NHServerOps
{
    private NHServerOps()
    {
    }

//////////////////////////////////////////////////////////////////////////
// ExtendedReadOp
//////////////////////////////////////////////////////////////////////////

    static class ExtendedReadOp extends HOp
    {
        @Override
        public String name() { return "extendedRead"; }
        @Override
        public String summary() { return "Extended Read"; }
        @Override
        public HGrid onService(HServer db, HGrid req)
        {
            NHServer server = (NHServer) db;
            if (!server.getCache().initialized()) 
                throw new IllegalStateException(Cache.NOT_INITIALIZED);

            long ticks = Clock.ticks();
            LOG.fine(() -> name() + " begin");

            HRow params = req.row(0);

            String filter = params.getStr("filter");

            int limit = params.has("limit") ?
                params.getInt("limit") :
                Integer.MAX_VALUE;

            HGrid result = server.onReadAll(filter, limit);

            // size
            if (params.has("size") && params.get("size").equals(HBool.TRUE))
                result = makeSizeGrid(result);

            // unique
            else if (params.has("unique"))
                result = makeUniqueGrid(result, params.getStr("unique"));

            LOG.fine(() -> name() + " end, " + (Clock.ticks()-ticks) + "ms.");
            return result;
        }

        private static HGrid makeSizeGrid(HGrid grid)
        {
            HDictBuilder hdb = new HDictBuilder();
            hdb.add("size", HNum.make(grid.numRows()));
            return HGridBuilder.dictToGrid(hdb.toDict());
        }

        private static HGrid makeUniqueGrid(HGrid grid, String column)
        {
            Map<HVal, Object> map = new HashMap<>();
            for (int i = 0; i < grid.numRows(); i++)
            {
                HRow row = grid.row(i);
                if (row.has(column))
                {
                    HVal val = row.get(column);
                    Integer count = (Integer) map.get(val);
                    map.put(val, count == null ?
                        Integer.valueOf(1) :
                        Integer.valueOf(count.intValue() + 1));
                }
            }

            ArrayList<HDict> arr = new ArrayList<>();
            for (Entry<HVal, Object> entry : map.entrySet())
            {
                Integer count = (Integer)entry.getValue();

                HDictBuilder hdb = new HDictBuilder();
                hdb.add(column, entry.getKey());
                hdb.add("count", HNum.make(count.intValue()));
                arr.add(hdb.toDict());
            }
            return HGridBuilder.dictsToGrid(arr.toArray(EMPTY_HDICT_ARRAY));
        }
    }
 
//////////////////////////////////////////////////////////////////////////
// ExtendedOp
//////////////////////////////////////////////////////////////////////////

    static class ExtendedOp extends HOp
    {
        @Override
        public String name() { return "extended"; }
        @Override
        public String summary() { return "Extended Functions"; }
        @Override
        public HGrid onService(HServer db, HGrid req)
        {
            NHServer server = (NHServer) db;
            if (!server.getCache().initialized()) 
                throw new IllegalStateException(Cache.NOT_INITIALIZED);

            HRow params = req.row(0);
            String function = params.getStr("function");

            long ticks = Clock.ticks();
            LOG.fine(() -> name() + ' ' + function + " begin");

            HGrid result;

            // write
            switch (function)
            {
            case "addHaystackSlots":
                result = addHaystackSlots(server, params);
                break;
            case "addEquips":
                result = addEquips(server, params);
                break;
            case "applyBatchTags":
                result = applyBatchTags(server, params);
                break;
            case "copyEquipTags":
                result = copyEquipTags(server, params);
                break;
            case "delete":
                result = delete(server, params);
                break;
            case "deleteHaystackSlot":
                result = deleteHaystackSlot(server, params);
                break;
            case "searchAndReplace":
                result = searchAndReplace(server, params);
                break;
            case "mapPointsToEquip":
                result = mapPointsToEquip(server, params);
                break;
            case "makeDynamicWritable":
                result = makeDynamicWritable(server);
                break;
            case "applyGridTags":
                result = applyGridTags(server, req);
                break;

            // invoke
            case "rebuildCache":
                result = rebuildCache(server, params);
                break;

            // read
            case "findDuplicatePoints":
                result = findDuplicatePoints(server, params);
                break;
            case "pullPropTags":
                result = pullPropTags(server, req);
                break;
            case "showPointsInWatch":
                result = showPointsInWatch(server, params);
                break;
            case "showWatches":
                result = showWatches(server, params);
                break;
            case "uniqueTags":
                result = uniqueTags(server, params);
                break;
            case "uniqueEquipTypes":
                result = uniqueEquipTypes(server);
                break;

            default:
                HDictBuilder hdb = new HDictBuilder();
                hdb.add("error", "There is no extended function called '" + function + "'.");
                result = HGridBuilder.dictsToGrid(new HDict[] { hdb.toDict() });
                break;
            }

            LOG.fine(() -> name() + ' ' + function + " end, " + (Clock.ticks()-ticks) + "ms.");
            return result;
        }
    }

    /**
      * makeDynamicWritable
      */
    private static HGrid makeDynamicWritable(NHServer server)
    {
        try
        {
            Iterator<BComponent> itr = new ComponentTreeIterator(
                (BComponent) BOrd.make("slot:/").resolve(server.getService(), null).get());
            outer: while (itr.hasNext())
            {
                BComponent comp = itr.next();

                // look for control points
                if (!(comp instanceof BControlPoint)) continue;

                // but they must be non-writable
                if (comp instanceof BIWritablePoint) continue;

                // and have at least one dynamic action that is not hidden
                Action[] actions = comp.getActionsArray();
                for (Action action : actions)
                {
                    if (action.isDynamic() && !Flags.isHidden(comp, action))
                    {
                        // add haystack slot if its missing
                        if (comp.get("haystack") == null)
                            comp.add("haystack", BHDict.DEFAULT);

                        // add 'writable' if its missing
                        HDict origTags = ((BHDict) comp.get("haystack")).getDict();
                        if (!origTags.has("writable"))
                        {
                            LOG.info("adding 'writable' to " + comp.getSlotPath());
                            HDict newTags = new HZincReader("writable").readDict();
                            HDict row = applyTagsToDict(origTags, newTags);
                            comp.set("haystack", BHDict.make(row));
                        }

                        continue outer;
                    }
                }
            }

            // done
            return HGrid.EMPTY;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
      * copyEquipTags
      */
    private static HGrid copyEquipTags(NHServer server, HRow params)
    {
        // TODO: consider relativizing the slot paths, and forcing them to match,
        // rather than just going by point name as we do now.

        try
        {
            Context cx = ThreadContext.getContext(Thread.currentThread());

            HRef fromId = HRef.make(params.getStr("fromEquip"));
            String targetFilter = params.getStr("targetFilter");
            if (targetFilter.equals("id"))
                targetFilter = "";
            else
                targetFilter = "equip and (" + targetFilter + ')';

            String toEquipIds = null;
            if (params.has("toEquips"))
                toEquipIds = params.getStr("toEquips");

            BHEquip from = (BHEquip) server.getTagManager().lookupComponent(fromId);

            BComponent[] toEquips = getFilterComponents(server, targetFilter, toEquipIds);

            // equips
            for (BComponent comp : toEquips)
            {
                if (comp instanceof BHEquip)
                {
                    if (comp == from) continue;

                    ((BHEquip)comp).setHaystack(cloneDict(BHDict.make(from.generateTags(server))));
                    replaceHaystackSlot(comp);
                }
                else
                {
                    BHEquip toEquip = (BHEquip) comp.get("equip");

                    if (toEquip == null)
                    {
                        toEquip = new BHEquip();
                        comp.add("equip", toEquip);
                    }
                    else if (toEquip == from) continue;

                    toEquip.setHaystack(cloneDict(BHDict.make(from.generateTags(server))));
                    replaceHaystackSlot(toEquip);
                }
            }

            // points
            Map<String, HDict> pointDictMap = new HashMap<>();
            collectPointDicts((BComponent) from.getParent(), pointDictMap, cx);

            for (BComponent toEquip : toEquips)
            {
                BComponent comp = toEquip instanceof BHEquip ?
                  (BComponent)toEquip.getParent() : toEquip;
                applyPointDicts(comp, pointDictMap, cx);
            }

            // done
            return HGrid.EMPTY;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
      * applyPointDicts
      */
    private static void applyPointDicts(BComponent comp, Map<String, HDict> pointDictMap, Context cx)
    {
        // check permissions on this Thread's saved context
        if (!TypeUtil.canWrite(comp, cx) || !TypeUtil.canRead(comp, cx)) 
            throw new PermissionException("Cannot write");

        if (comp instanceof BControlPoint)
        {
            HDict dict = pointDictMap.get(comp.getName());
            if (dict != null)
            {
                if (comp.get("haystack") == null)
                    comp.add("haystack", BHDict.make(dict));
                else
                    comp.set("haystack", BHDict.make(dict));
            }
        }
        else
        {
            BComponent[] kids = comp.getChildren(BComponent.class);
            for (BComponent kid : kids)
                applyPointDicts(kid, pointDictMap, cx);
        }
    }

    /**
      * collectPointDicts
      */
    private static void collectPointDicts(BComponent comp, Map<String, HDict> pointDictMap, Context cx)
    {
        // check permissions on this Thread's saved context
        if (!TypeUtil.canRead(comp, cx)) 
            throw new PermissionException("Cannot write");

        if (comp instanceof BControlPoint)
        {
            HDict dict = BHDict.findTagAnnotation(comp);
            if (dict != null)
                pointDictMap.put(comp.getName(), dict);
        }
        else
        {
            BComponent[] kids = comp.getChildren(BComponent.class);
            for (BComponent kid : kids)
                collectPointDicts(kid, pointDictMap, cx);
        }
    }

    /**
      * cloneDict
      */
    private static BHDict cloneDict(BHDict dict)
    {
        try
        {
            return (BHDict) 
                BHDict.DEFAULT.decodeFromString(
                    dict.encodeToString());
        }
        catch (Exception e)
        {
            throw new BajaRuntimeException(e);
        }
    }

    /**
      * lookupComponents
      */
    private static BComponent[] lookupComponents(TagManager tagMgr, HRef[] ids)
    {
        Context cx = ThreadContext.getContext(Thread.currentThread());

        ArrayList<BComponent> compArr = new ArrayList<>();
        for (HRef id : ids)
        {
            BComponent comp = tagMgr.doLookupComponent(id, false);
            if (!TypeUtil.canWrite(comp, cx))
                throw new PermissionException("Cannot write to " + id);

            if (comp != null)
                compArr.add(comp);
        }

        return compArr.toArray(EMPTY_COMPONENT_ARRAY);
    }

    /**
     * For each row in the request, use the row id to lookup the component and append the tags in
     * the row to the haystack slot of the component.
     */
    private static HGrid applyGridTags(NHServer server, HGrid request)
    {
        TagManager tagMgr = server.getTagManager();

        Context cx = ThreadContext.getContext(Thread.currentThread());
        for (int i = 1; i < request.numRows(); i++)
        {
            HRow row = request.row(i);
            BComponent comp = tagMgr.doLookupComponent(row.id(), false);
            if (!TypeUtil.canWrite(comp, cx)) 
                throw new PermissionException("Cannot write to " + row.id()); 

            HDictBuilder hdb = new HDictBuilder();

            // add new tags
            Iterator<Map.Entry<String, HVal>> it = row.iterator();
            while (it.hasNext())
            {
                Map.Entry<String, HVal> e = it.next();
                String key = e.getKey();
                HVal val = e.getValue();

                if (key.equals("id"))
                    continue;

                if (key.equals("function"))
                    continue;

                hdb.add(key, val);
            }

            // set haystack
            if (comp.get("haystack") == null)
                comp.add("haystack", BHDict.make(hdb.toDict()));
            else
                comp.set("haystack", BHDict.make(hdb.toDict()));
            replaceHaystackSlot(comp);

        }

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("rowsChanged", HNum.make(request.numRows()-1));
        return HGridBuilder.dictToGrid(hdb.toDict());
    }

    /**
     * Appends the tags specified in params "tags" to all to the components specified by params
     * "ids" and "targetFilter". See
     * {@link NHServerOps#getFilterComponents(NHServer, String, String)}.
     */
    private static HGrid applyBatchTags(NHServer server, HRow params)
    {
        String tags = params.getStr("tags");
        String targetFilter = params.getStr("targetFilter");

        boolean returnResultRows = false;
        if (params.has("returnResultRows"))
            returnResultRows = params.getBool("returnResultRows");

        String ids = null;
        if (params.has("ids"))
            ids = params.getStr("ids");

        HDict newTags = new HZincReader(tags).readDict();

        BComponent[] targets = getFilterComponents(server, targetFilter, ids);
        HDict[] rows = new HDict[targets.length];
        for (int i = 0; i < targets.length; i++)
        {
            BComponent target = targets[i];

            if (target.get("haystack") == null) 
                target.add("haystack", BHDict.DEFAULT);
            else if (!(target.get("haystack") instanceof BHDict)) 
                continue;

            HDict origTags = ((BHDict) target.get("haystack")).getDict();

            HDict row = applyTagsToDict(origTags, newTags);
            target.set("haystack", BHDict.make(row));
            replaceHaystackSlot(target);

            rows[i] = row;
        }

        HGrid result;
        if (returnResultRows)
        {
            result = HGridBuilder.dictsToGrid(rows);
        }
        else
        {
            HDictBuilder hdb = new HDictBuilder();
            hdb.add("rowsChanged", HNum.make(targets.length));
            result = HGridBuilder.dictToGrid(hdb.toDict());
        }
        return result;
    }

    /**
      * applyTagsToDict
      */
    private static HDict applyTagsToDict(HDict origTags, HDict newTags)
    {
        HDictBuilder hdb = new HDictBuilder();

        // add orig tags
        Iterator<Map.Entry<String, HVal>> it = origTags.iterator();
        while (it.hasNext())
        {
            Map.Entry<String, HVal> e = it.next();
            String key = e.getKey();
            HVal   val = e.getValue();

            HVal rem = newTags.get(key, false);
            if (!(rem != null && rem.equals(REMOVE)))
                hdb.add(key, val);
        }

        // add new tags
        it = newTags.iterator();
        while (it.hasNext())
        {
            Map.Entry<String, HVal> e = it.next();
            String key = e.getKey();
            HVal   val = e.getValue();

            if (!val.equals(REMOVE))
                hdb.add(key, val);
        }

        return hdb.toDict();
    }

    /**
     * Adds a haystack slot to components specified by params "ids" and "targetFilter". See
     * {@link NHServerOps#getFilterComponents(NHServer, String, String)}.  Components with a
     * haystack slot already are ignored.  All paths must be valid for the operation to be carried
     * out.
     */
    private static HGrid addHaystackSlots(NHServer server, HRow params)
    {
        String targetFilter = params.getStr("targetFilter");

        String ids = null;
        if (params.has("ids"))
            ids = params.getStr("ids");

        int count = 0;
        BComponent[] targets = getFilterComponents(server, targetFilter, ids);
        for (BComponent target : targets)
        {
            if (target.get("haystack") == null)
            {
                count++;
                target.add("haystack", BHDict.DEFAULT);
            }
        }

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("rowsChanged", HNum.make(count));
        return HGridBuilder.dictToGrid(hdb.toDict());
    }

    /**
     * Deletes haystack information on components specified by params "ids" (no params
     * "targetFilter" accepted; see
     * {@link NHServerOps#getFilterComponents(NHServer, String, String)}). If the component a
     * BHEquip or BHSite, the component is deleted. Otherwise, if the component has a dynamic
     * haystack slot, that slot is deleted.  If it has a frozen haystack slot, the contents of the
     * slot are deleted.
     */
    private static HGrid delete(NHServer server, HRow params)
    {
        String ids = params.getStr("ids");

        int count = 0;
        BComponent[] targets = getFilterComponents(server, "", ids);
        for (BComponent target : targets)
        {
            if (target instanceof BHTagged)
            {
                BComponent parent = (BComponent)target.getParent();
                parent.remove(target.getName());

                count++;
            }
            else
            {
                Property prop = target.getProperty("haystack");
                if (prop == null) continue;

                if (prop.isDynamic())
                    target.remove("haystack");
                else
                    target.set("haystack", BHDict.DEFAULT);

                count++;
            }
        }

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("rowsChanged", HNum.make(count));
        return HGridBuilder.dictToGrid(hdb.toDict());
    }

    /**
     * Deletes the haystack slot on components specified by params "ids" (no params "targetFilter"
     * accepted; see {@link NHServerOps#getFilterComponents(NHServer, String, String)}). If the
     * haystack slot is a dynamic slot, that slot is deleted.  If it is a frozen haystack slot, the
     * contents of the slot are deleted.
     */
    private static HGrid deleteHaystackSlot(NHServer server, HRow params)
    {
        String ids = params.getStr("ids");

        int count = 0;
        BComponent[] targets = getFilterComponents(server, "", ids);
        for (BComponent target : targets)
        {
            Property prop = target.getProperty("haystack");
            if (prop == null) continue;

            if (prop.isDynamic())
            {
                target.remove("haystack");
                count++;
            }
            else
            {
                target.set("haystack", BHDict.DEFAULT);
                count++;
            }
        }

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("rowsChanged", HNum.make(count));
        return HGridBuilder.dictToGrid(hdb.toDict());
    }

    /**
     * Adds BHEquip components to folders specified by params "ids" and "targetFilter". See
     * {@link NHServerOps#getFilterComponents(NHServer, String, String)}.  The BHEquip will be
     * associated a site specified by param "siteName".  This site will be created at the root of
     * the station if the site does not already exist.
     */
    private static HGrid addEquips(NHServer server, HRow params)
    {
        Context cx = ThreadContext.getContext(Thread.currentThread());

        String targetFilter = params.getStr("targetFilter");

        String ids = null;
        if (params.has("ids"))
            ids = params.getStr("ids");

        HRef siteRef = null;
        if (params.has("siteName"))
        {
            String siteName = params.getStr("siteName");
            HDict siteGrid = server.read(
                "site and dis == \"" + siteName + '"', false);

            if (siteGrid == null)
            {
                BComponent root = (BComponent) 
                    BOrd.make("slot:/").get(server.getService(), cx);

                // check permissions on this Thread's saved context
                if (!TypeUtil.canWrite(root, cx)) 
                    throw new PermissionException("Cannot write");

                BHSite site = new BHSite();
                root.add(siteName, site);
                siteRef = HRef.make("C." + siteName);
            }
            else
            {
                BHSite site = (BHSite) server.getTagManager().lookupComponent(
                    siteGrid.getRef("id"));
                siteRef = TagManager.makeSlotPathRef(site).getHRef();
            }
        }

        int count = 0;
        BComponent[] targets = getFilterComponents(server, targetFilter, ids);
        for (BComponent target : targets)
        {
            if (target.get("equip") == null)
            {
                count++;
                BHEquip equip = new BHEquip();

                if (siteRef != null)
                {
                    HDictBuilder hdb = new HDictBuilder();
                    hdb.add("siteRef", siteRef);
                    hdb.add("navNameFormat", "%parent.displayName%");

                    equip.setHaystack(BHDict.make(hdb.toDict()));
                }
                target.add("equip", equip);
                replaceHaystackSlot(equip);
            }
        }

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("rowsChanged", HNum.make(count));
        return HGridBuilder.dictToGrid(hdb.toDict());
    }

    /**
     * Associate points specified by params "ids" with a site specified by params "siteNavName" and
     * an equip specified by params "equipNavName".
     */
    private static HGrid mapPointsToEquip(NHServer server, HRow params)
    {
        Context cx = ThreadContext.getContext(Thread.currentThread());

        String siteNav  = params.getStr("siteNavName");
        String equipNav = params.getStr("equipNavName");
        HRef[] pointRefs = parseIdList(params.getStr("ids"));

        // make sure the site and equip really exist
        HRef siteRef = ensureSite(server, cx, siteNav);
        HRef equipRef = ensureEquip(server, cx, siteRef, siteNav, equipNav);

        // make every point be a child of the equip
        for (HRef pointRef : pointRefs)
        {
            BComponent point = server.getTagManager().lookupComponent(pointRef);
            HDictBuilder hdb = new HDictBuilder();

            // add tags from old haystack slot
            HDict oldDict = BHDict.findTagAnnotation(point);
            if (oldDict != null)
                hdb.add(oldDict);

            // add new tags
            hdb.add("siteRef", siteRef);
            hdb.add("equipRef", equipRef);

            // set new haystack slot
            if (point.get("haystack") == null)
                point.add("haystack", BHDict.make(hdb.toDict()));
            else
                point.set("haystack", BHDict.make(hdb.toDict()));
            replaceHaystackSlot(point);
        }

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("rowsChanged", HNum.make(pointRefs.length));
        return HGridBuilder.dictToGrid(hdb.toDict());
    }

    /**
      * ensureSite
      */
    private static HRef ensureSite(NHServer server, Context cx, String siteNav)
    {
        HDict navRec = lookupSiteNav(server, siteNav);
        if (navRec == null)
        {
            BComponent root = (BComponent) 
                BOrd.make("slot:/").get(server.getService(), cx);

            // check permissions on this Thread's saved context
            if (!TypeUtil.canWrite(root, cx)) 
                throw new PermissionException("Cannot write");

            BHSite site = new BHSite();
            root.add(siteNav, site);
            return HRef.make("C." + siteNav);
        }
        else
        {
            HRef id = navRec.getRef("id");
            BHSite site = (BHSite) server.getTagManager().lookupComponent(id);
            return TagManager.makeSlotPathRef(site).getHRef();
        }
    }

    /**
      * lookupSiteNav
      */
    private static HDict lookupSiteNav(NHServer server, String siteNav)
    {
        HGrid grid = server.getNav().onNav("sep:/");
        for (int i = 0; i < grid.numRows(); i++) 
        {
            HDict dict = grid.row(i);
            if (dict.getStr("navName").equals(siteNav))
                return dict;
        }
        return null;
    }

    /**
      * ensureEquip
      */
    private static HRef ensureEquip(
        NHServer server, Context cx, 
        HRef siteRef, String siteNav, String equipNav)
    {
        HDict navRec = lookupEquipNav(server, siteNav, equipNav);
        if (navRec == null)
        {
            BHSite site = (BHSite) server.getTagManager().lookupComponent(siteRef);

            // check permissions on this Thread's saved context
            if (!TypeUtil.canWrite(site, cx)) 
                throw new PermissionException("Cannot write");

            BHEquip equip = new BHEquip();

            // add site ref to equip tags
            HDictBuilder hdb = new HDictBuilder();
            hdb.add("siteRef", siteRef);
            equip.setHaystack(BHDict.make(hdb.toDict()));

            // add equip underneath site
            site.add(equipNav, equip);
            return HRef.make(siteRef.val + '.' + equipNav);
        }
        else
        {
            HRef id = navRec.getRef("id");
            BHEquip equip = (BHEquip) server.getTagManager().lookupComponent(id);
            return TagManager.makeSlotPathRef(equip).getHRef();
        }
    }

    /**
      * lookupEquipNav
      */
    private static HDict lookupEquipNav(
        NHServer server, String siteNav, String equipNav)
    {
        HGrid grid = server.getNav().onNav("sep:/" + siteNav);
        for (int i = 0; i < grid.numRows(); i++) 
        {
            HDict dict = grid.row(i);
            if (dict.getStr("navName").equals(equipNav))
                return dict;
        }
        return null;
    }

    /**
      * rebuildCache
      */
    private static HGrid rebuildCache(NHServer server, HRow params)
    {
        // check permissions on this Thread's saved context
        Context cx = ThreadContext.getContext(Thread.currentThread());
        if (!TypeUtil.canInvoke(server.getService(), cx)) 
            throw new PermissionException("Cannot invoke rebuildCache");

        server.getService().invoke(
            BNHaystackService.rebuildCache, null, null);

        return HGrid.EMPTY;
    }

    /**
     * For components specified by params "ids" and "filter" (see
     * {@link NHServerOps#getFilterComponents(NHServer, String, String)}), replace the text in
     * params "searchText" in the name of the component with the text in params "replaceText".
     */
    private static HGrid searchAndReplace(NHServer server, HRow params)
    {
        String filter = params.getStr("filter");
        String searchText = params.getStr("searchText");
        String replaceText = params.getStr("replaceText");

        String ids = null;
        if (params.has("ids"))
            ids = params.getStr("ids");

        int count = 0;
        BComponent[] comps = getFilterComponents(server, filter, ids);
        for (BComponent comp : comps)
        {
            String name = comp.getName();

            int n = name.indexOf(searchText);
            if (n != -1)
            {
                String newName = 
                    name.substring(0, n) + 
                    replaceText + 
                    name.substring(n + searchText.length());

                BComponent parent = (BComponent) comp.getParent();
                parent.rename(comp.getPropertyInParent(), newName);

                count++;
            }
        }

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("rowsChanged", HNum.make(count));
        return HGridBuilder.dictToGrid(hdb.toDict());
    }

    /**
      * uniqueTags
      */
    private static HGrid uniqueTags(NHServer server, HRow params)
    {
        String filter = params.getStr("filter");
        int limit = params.has("limit") ?
            params.getInt("limit") :
            Integer.MAX_VALUE;
        HGrid grid = server.onReadAll(filter, limit);

        // get all the distinct markers from the grid
        String[] markers = distinctMarkers(grid);

        // generate all combinations
        List<String[]> combinations = new ArrayList<>();
        combineMarkers(markers, new ArrayList<String>(), 0, combinations);

        // for each combination, count the number of times it occurs
        // in any of the rows in the grid
        ArrayList<HDict> resultRows = new ArrayList<>();
        for (Object combination : combinations)
        {
            String[] tags = (String[]) combination;
            int count = 0;

            for (int j = 0; j < grid.numRows(); j++)
            {
                if (dictHasAllTags(grid.row(j), tags))
                    count++;
            }

            if (count > 0)
            {
                HDictBuilder hdb = new HDictBuilder();
                hdb.add("markers", TextUtil.join(tags, ','));
                hdb.add("count", count);
                resultRows.add(hdb.toDict());
            }
        }
        return HGridBuilder.dictsToGrid(resultRows.toArray(EMPTY_HDICT_ARRAY));
    }

    /**
      * find all the distinct marker tags in the entire grid
      */
    private static String[] distinctMarkers(HGrid grid)
    {
        Set<String> set = new TreeSet<>();
        for (int i = 0; i < grid.numRows(); i++)
        {
            Iterator<Map.Entry<String, HVal>> it = grid.row(i).iterator();
            while (it.hasNext())
            {
                Map.Entry<String, HVal> e = it.next();
                String key = e.getKey();
                HVal val = e.getValue();
                if (val instanceof HMarker) set.add(key);
            }
        }
        return set.toArray(EMPTY_STRING_ARRAY);
    }

    /**
      * generate all of the combinations of markers
      */
    private static void combineMarkers(String[] markers, List<String> temp, int index, List<String[]> combinations)
    {
        for (int i = index; i < markers.length; i++)
        {
            temp.add(markers[i]);
            combinations.add(temp.toArray(EMPTY_STRING_ARRAY));
            combineMarkers(markers, temp, i + 1, combinations);
            temp.remove(temp.size() - 1);
        }
    } 

    /**
      * return whether the dict has all of the tags
      */
    private static boolean dictHasAllTags(HDict dict, String[] tags)
    {
        for (String tag : tags)
            if (!dict.has(tag)) return false;
        return true;
    }

    /**
      * showWatches
      */
    private static HGrid showWatches(NHServer server, HRow params)
    {
        ArrayList<HDict> arr = new ArrayList<>();

        HWatch[] watches = server.getWatches();
        for (HWatch watch : watches)
        {
            HRef watchId = HRef.make(watch.id());

            HDict[] subscribed = ((NHWatch)watch).curSubscribed();

            HDictBuilder hdb = new HDictBuilder();
            hdb.add("id", watchId);
            hdb.add("dis", Sys.getStation().getName());
            hdb.add("watchCount", subscribed.length);
            hdb.add("lastPoll", HNum.make(((NHWatch)watch).lastPoll(), "ms"));
            arr.add(hdb.toDict());
        }

        return HGridBuilder.dictsToGrid(arr.toArray(EMPTY_HDICT_ARRAY));
    }

    /**
      * showPointsInWatch
      */
    private static HGrid showPointsInWatch(NHServer server, HRow params)
    {
        HRef watchId = params.getRef("watchId");

        NHWatch watch = (NHWatch) server.getWatch(watchId.val);
        if (watch == null) return HGrid.EMPTY;

        return HGridBuilder.dictsToGrid(watch.curSubscribed());
    }

    /**
      * findDuplicatePoints
      */
    private static HGrid findDuplicatePoints(NHServer server, HRow params)
    {
        boolean annotatedOnly = true;
        if (params.has("annotatedOnly"))
            annotatedOnly = params.getBool("annotatedOnly");

        Cache cache = server.getCache();

        ArrayList<HDict> result = new ArrayList<>();
        BHEquip[] equips = cache.getAllEquips();
        for (BHEquip equip : equips)
        {
            BComponent[] points = cache.getEquipPoints(equip);

            // only included annotated points
            if (annotatedOnly)
            {
                ArrayList<BComponent> pointArr = new ArrayList<>();
                for (BComponent point : points)
                    if (BHDict.findTagAnnotation(point) != null)
                        pointArr.add(point);
                points = pointArr.toArray(EMPTY_COMPONENT_ARRAY);
            }

            Map<HRef, List<BComponent>> map = new HashMap<>();
            for (BComponent point : points)
            {
                NHRef nref = cache.lookupSepRefByComponent(point);
                if (nref == null) continue;

                HRef ref = nref.getHRef();
                map.computeIfAbsent(ref, k -> new ArrayList<>()).add(point);
            }

            for (Entry<HRef, List<BComponent>> entry : map.entrySet())
            {
                HRef ref = entry.getKey();
                List<BComponent> pointArr = entry.getValue();

                if (pointArr.size() > 1)
                {
                    for (BComponent point : pointArr)
                    {
                        result.add(makeDuplicateDict(ref, point));
                    }
                }
            }
        }

        return HGridBuilder.dictsToGrid(result.toArray(EMPTY_HDICT_ARRAY));
    }

    private static HDict makeDuplicateDict(HRef ref, BComponent point)
    {
        HDictBuilder hdb = new HDictBuilder();
        hdb.add("id", ref);
        hdb.add("slotPath", point.getSlotPath().toString());
        return hdb.toDict();
    }

    /**
      * pullPropTags -- pull into finstack
      *
      */
    private static HGrid pullPropTags(NHServer server, HGrid req)
    {
        HRow params = req.row(0);
        String[] tags = TextUtil.split(params.getStr("tags"), ',');

        ArrayList<HDict> arr = new ArrayList<>();
        for (int i = 1; i < req.numRows(); i++)
        {
            HRow inRow = req.row(i);

            HRef haystackCur = inRow.getRef("haystackCur");
            HDict outRow = server.onReadById(haystackCur);
            if (outRow == null) continue;

            HDictBuilder hdb = new HDictBuilder();
            hdb.add("id", inRow.id());
            hdb.add("mod", inRow.get("mod"));
            for (String tag : tags)
            {
                if (outRow.has(tag))
                    hdb.add(tag, outRow.get(tag));
                else
                    hdb.add(tag, REMOVE);
            }
            arr.add(hdb.toDict());
        }

        return HGridBuilder.dictsToGrid(arr.toArray(EMPTY_HDICT_ARRAY));
    }

    /**
      * uniqueEquipTypes
      */
    private static HGrid uniqueEquipTypes(NHServer server)
    {
        BHGrid grid = (BHGrid) server.getService().get(BUniqueEquipTypeJob.UNIQUE_EQUIP_TYPES);
        if (grid == null)
            throw new IllegalStateException(
                '\'' + BUniqueEquipTypeJob.UNIQUE_EQUIP_TYPES + "' not found.");

        return grid.getGrid();
    }

////////////////////////////////////////////////////////////////
// utils
////////////////////////////////////////////////////////////////

    public static BComponent[] getFilterComponents(NHServer server, String filter, String ids)
    {
        HServer hserver = server;
        TagManager tagMgr = server.getTagManager();

        ArrayList<BComponent> compArr = new ArrayList<>();
        Context cx = ThreadContext.getContext(Thread.currentThread());

        // if there is an id list, parse it and look up all the recs
        if (ids != null && !ids.isEmpty())
        {
            HRef[] refs = parseIdList(ids);

            if (refs.length > 0)
            {
                ArrayList<HDict> dictArr = new ArrayList<>();
                for (HRef ref : refs)
                {
                    BComponent comp = tagMgr.doLookupComponent(ref, false);
                    if (!TypeUtil.canWrite(comp, cx))
                        throw new PermissionException("Cannot write to " + ref);

                    if (comp != null)
                    {
                        compArr.add(comp);
                        dictArr.add(tagMgr.createTags(comp));
                    }
                }

                // filter against the recs
                hserver = new HDictFilterer(server, dictArr.toArray(EMPTY_HDICT_ARRAY));
            }
        }

        // filter against either the entire database, or the id list recs
        if (filter != null && !filter.isEmpty())
        {
            compArr.clear();
            HGrid grid = hserver.readAll(filter);
            for (int i = 0; i < grid.numRows(); i++)
            {
                HStr slotPath = (HStr) grid.row(i).get("axSlotPath", false);
                if (slotPath != null)
                    compArr.add((BComponent)BOrd.make("station:|" + slotPath.val).get(
                            server.getService(), null));
            }
        }

        return compArr.toArray(EMPTY_COMPONENT_ARRAY);
    }

    // this class just provides an iterator() so that we can filter against a list of ids
    private static class HDictFilterer extends HServer
    {
        private final NHServer server;
        private final List<HDict> list;

        private HDictFilterer(NHServer server, HDict[] dicts) 
        { 
            this.server = server;
            this.list = Arrays.asList(dicts); 
        }

        @Override
        protected Iterator<HDict> iterator() { return list.iterator(); }

        // HProj
        @Override
        protected HDict onReadById(HRef id) { return server.onReadById(id); }

        // HServer
        @Override
        public HOp[] ops() { throw new UnsupportedOperationException(); }
        @Override
        protected HDict onAbout() { throw new UnsupportedOperationException(); }
        @Override
        protected HGrid onNav(String navId) { throw new UnsupportedOperationException(); }
        @Override
        protected HDict onNavReadByUri(HUri uri) { throw new UnsupportedOperationException(); }
        @Override
        protected HWatch onWatchOpen(String dis, HNum lease) { throw new UnsupportedOperationException(); }
        @Override
        protected HWatch[] onWatches() { throw new UnsupportedOperationException(); }
        @Override
        protected HWatch onWatch(String id) { throw new UnsupportedOperationException(); }
        @Override
        protected HGrid onPointWriteArray(HDict rec) { throw new UnsupportedOperationException(); }
        @Override
        protected void onPointWrite(HDict rec, int level, HVal val, String who, HNum dur, HDict ops) { throw new UnsupportedOperationException(); }
        @Override
        protected HHisItem[] onHisRead(HDict rec, HDateTimeRange range) { throw new UnsupportedOperationException(); }
        @Override
        protected void onHisWrite(HDict rec, HHisItem[] items) { throw new UnsupportedOperationException(); }
        @Override
        protected HGrid onInvokeAction(HDict rec, String action, HDict args) { throw new UnsupportedOperationException(); }
    }

    /**
     * Returns an array of HRef objects given a comma separated list of HRef Strings.  The list must
     * start with '[' and end with ']'.  See {@link HRef#make(String)}.
     * (e.g. Playground.SiteA.EquipA.booleanPoint to indicate the path under config in which
     * "booleanPoint" may be found)
     */
    private static HRef[] parseIdList(String ids)
    {
        int len = ids.length();

        if (ids.charAt(0) != '[' || ids.charAt(len-1) != ']')
            throw new IllegalStateException(ids + " is malformed.");

        if (ids.equals("[,]")) return EMPTY_HREF_ARR;

        String[] tokens = TextUtil.split(ids.substring(1, len-1), ',');

        HRef[] refs = new HRef[tokens.length];
        for (int i = 0; i < tokens.length; i++)
            refs[i] = HRef.make(tokens[i].trim());

        return refs;
    }

    private static HRef valToId(HServer db, HVal val)
    {
        if (val instanceof HUri)
        {
            HDict rec = db.navReadByUri((HUri)val, false);
            return rec == null ? HRef.nullRef : rec.id();
        }
        else
        {
            return (HRef)val;
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack");
    private static final HRef[] EMPTY_HREF_ARR = new HRef[0];
    private static final HDict[] EMPTY_HDICT_ARRAY = new HDict[0];
    public static final BComponent[] EMPTY_COMPONENT_ARRAY = new BComponent[0];
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final HStr REMOVE = HStr.make("_remove_");
}
