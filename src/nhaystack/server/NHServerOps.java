//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   05 Jun 2014  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;

import javax.baja.control.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.security.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.io.*;
import org.projecthaystack.server.*;

import nhaystack.*;
import nhaystack.site.*;
import nhaystack.util.*;

/**
  * Custom Ops for NHServer
  */
class NHServerOps
{

//////////////////////////////////////////////////////////////////////////
// ExtendedReadOp
//////////////////////////////////////////////////////////////////////////

    static class ExtendedReadOp extends HOp
    {
        public String name() { return "extendedRead"; }
        public String summary() { return "Extended Read"; }
        public HGrid onService(HServer db, HGrid req)
        {
            NHServer server = (NHServer) db;
            if (!server.getCache().initialized()) 
                throw new IllegalStateException(Cache.NOT_INITIALIZED);

            long ticks = Clock.ticks();
            if (LOG.isTraceOn()) LOG.trace(name() + " begin");

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

            if (LOG.isTraceOn()) LOG.trace(name() + " end, " + (Clock.ticks()-ticks) + "ms.");
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
            Map map = new HashMap();
            for (int i = 0; i < grid.numRows(); i++)
            {
                HRow row = grid.row(i);
                if (row.has(column))
                {
                    HVal val = row.get(column);
                    Integer count = (Integer) map.get(val);
                    map.put(val, (count == null) ? 
                        new Integer(1) :
                        new Integer(count.intValue() + 1));
                }
            }

            Array arr = new Array(HDict.class);
            Iterator it = map.keySet().iterator();
            while (it.hasNext())
            {
                HVal val = (HVal) it.next();
                Integer count = (Integer) map.get(val);

                HDictBuilder hdb = new HDictBuilder();
                hdb.add(column, val);
                hdb.add("count", HNum.make(count.intValue()));
                arr.add(hdb.toDict());
            }
            return HGridBuilder.dictsToGrid((HDict[]) arr.trim());
        }
    }
 
//////////////////////////////////////////////////////////////////////////
// ExtendedOp
//////////////////////////////////////////////////////////////////////////

    static class ExtendedOp extends HOp
    {
        public String name() { return "extended"; }
        public String summary() { return "Extended Functions"; }
        public HGrid onService(HServer db, HGrid req)
        {
            NHServer server = (NHServer) db;
            if (!server.getCache().initialized()) 
                throw new IllegalStateException(Cache.NOT_INITIALIZED);

            HRow params = req.row(0);
            String function = params.getStr("function");

            long ticks = Clock.ticks();
            if (LOG.isTraceOn()) LOG.trace(name() + " " + function + " begin");

            HGrid result = HGrid.EMPTY;

            // write
            if      (function.equals("addHaystackSlots"))   result = addHaystackSlots   (server, params);
            else if (function.equals("addEquips"))          result = addEquips          (server, params);
            else if (function.equals("applyBatchTags"))     result = applyBatchTags     (server, params);
            else if (function.equals("copyEquipTags"))      result = copyEquipTags      (server, params);
            else if (function.equals("delete"))             result = delete             (server, params);
            else if (function.equals("deleteHaystackSlot")) result = deleteHaystackSlot (server, params);
            else if (function.equals("searchAndReplace"))   result = searchAndReplace   (server, params);

            // invoke
            else if (function.equals("rebuildCache"))        result = rebuildCache        (server, params);

            // read
            else if (function.equals("findDuplicatePoints")) result = findDuplicatePoints (server, params);
            else if (function.equals("pullPropTags"))        result = pullPropTags        (server, req);
            else if (function.equals("showPointsInWatch"))   result = showPointsInWatch   (server, params);
            else if (function.equals("showWatches"))         result = showWatches         (server, params);
            else if (function.equals("uniqueTags"))          result = uniqueTags          (server, params);

            else 
            {
                HDictBuilder hdb = new HDictBuilder();
                hdb.add("error", "There is no extended function called '" + function + "'.");
                result = HGridBuilder.dictsToGrid(new HDict[] { hdb.toDict() });
            }

            if (LOG.isTraceOn()) LOG.trace(name() + " " + function + " end, " + (Clock.ticks()-ticks) + "ms.");
            return result;
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
            HRef toIds[] = parseIdList(params.getStr("toEquips"));

            BHEquip from = (BHEquip) server.getTagManager().lookupComponent(fromId);
            BComponent[] to = lookupComponents(server.getTagManager(), toIds);

            // equips
            for (int i = 0; i < to.length; i++)
            {
                if (to[i] instanceof BHEquip)
                {
                    ((BHEquip) to[i]).setHaystack(cloneDict(from.getHaystack()));
                }
                else
                {
                    BHEquip toEquip = (BHEquip) to[i].get("equip");
                    if (toEquip == null)
                    {
                        toEquip = new BHEquip();
                        to[i].add("equip", toEquip);
                    }

                    toEquip.setHaystack(cloneDict(from.getHaystack()));
                }
            }

            // points
            Map pointDictMap = new HashMap();
            collectPointDicts((BComponent) from.getParent(), pointDictMap, cx);
            for (int i = 0; i < to.length; i++)
            {
                BComponent comp = (to[i] instanceof BHEquip) ? 
                    (BComponent) to[i].getParent() : to[i];
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
    private static void applyPointDicts(BComponent comp, Map pointDictMap, Context cx)
    {
        // check permissions on this Thread's saved context
        if (!TypeUtil.canWrite(comp, cx) || !TypeUtil.canRead(comp, cx)) 
            throw new PermissionException("Cannot write");

        if (comp instanceof BControlPoint)
        {
            HDict dict = (HDict) pointDictMap.get(comp.getName());
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
            BComponent[] kids = (BComponent[]) comp.getChildren(BComponent.class);
            for (int i = 0; i < kids.length; i++)
                applyPointDicts(kids[i], pointDictMap, cx);
        }
    }

    /**
      * collectPointDicts
      */
    private static void collectPointDicts(BComponent comp, Map pointDictMap, Context cx)
    {
        // check permissions on this Thread's saved context
        if (!TypeUtil.canRead(comp, cx)) 
            throw new PermissionException("Cannot write");

        if (comp instanceof BControlPoint)
        {
            HDict dict = BHDict.findTagAnnotation(comp);
            if (dict != null)
            {
                pointDictMap.put(comp.getName(), dict);
            }
        }
        else
        {
            BComponent[] kids = (BComponent[]) comp.getChildren(BComponent.class);
            for (int i = 0; i < kids.length; i++)
                collectPointDicts(kids[i], pointDictMap, cx);
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

        Array compArr = new Array(BComponent.class);
        for (int i = 0; i < ids.length; i++)
        {
            BComponent comp = tagMgr.doLookupComponent(ids[i], false);
            if (!TypeUtil.canWrite(comp, cx)) 
                throw new PermissionException("Cannot write to " + ids[i]); 

            if (comp != null)
                compArr.add(comp);
        }
        return (BComponent[]) compArr.trim();
    }

    /**
      * applyBatchTags
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
            if (target.get("haystack") == null) continue;
            if (!(target.get("haystack") instanceof BHDict)) continue;

            HDictBuilder hdb = new HDictBuilder();

            // add orig tags
            HDict origTags = ((BHDict) target.get("haystack")).getDict();
            Iterator it = origTags.iterator();
            while (it.hasNext())
            {
                Map.Entry e = (Map.Entry) it.next();
                String key = (String) e.getKey();
                HVal   val = (HVal)   e.getValue();

                HVal rem = (HVal) newTags.get(key, false);
                if (!(rem != null && rem.equals(REMOVE)))
                    hdb.add(key, val);
            }

            // add new tags
            it = newTags.iterator();
            while (it.hasNext())
            {
                Map.Entry e = (Map.Entry) it.next();
                String key = (String) e.getKey();
                HVal   val = (HVal)   e.getValue();

                if (!val.equals(REMOVE))
                    hdb.add(key, val);
            }

            HDict row = hdb.toDict();
            target.set("haystack", BHDict.make(row));
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
      * addHaystackSlots
      */
    private static HGrid addHaystackSlots(NHServer server, HRow params)
    {
        String targetFilter = params.getStr("targetFilter");

        String ids = null;
        if (params.has("ids"))
            ids = params.getStr("ids");

        int count = 0;
        BComponent[] targets = getFilterComponents(server, targetFilter, ids);
        for (int i = 0; i < targets.length; i++)
        {
            BComponent target = targets[i];
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
      * delete
      */
    private static HGrid delete(NHServer server, HRow params)
    {
        String ids = params.getStr("ids");

        int count = 0;
        BComponent[] targets = getFilterComponents(server, "", ids);
        for (int i = 0; i < targets.length; i++)
        {
            if (targets[i] instanceof BHTagged)
            {
                BComponent parent = (BComponent) targets[i].getParent();
                parent.remove(targets[i].getName());

                count++;
            }
            else
            {
                Property prop = targets[i].getProperty("haystack");
                if (prop == null) continue;

                if (prop.isDynamic())
                    targets[i].remove("haystack");
                else
                    targets[i].set("haystack", BHDict.DEFAULT);

                count++;
            }
        }

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("rowsChanged", HNum.make(targets.length));
        return HGridBuilder.dictToGrid(hdb.toDict());
    }

    /**
      * deleteHaystackSlot
      */
    private static HGrid deleteHaystackSlot(NHServer server, HRow params)
    {
        String ids = params.getStr("ids");

        int count = 0;
        BComponent[] targets = getFilterComponents(server, "", ids);
        for (int i = 0; i < targets.length; i++)
        {
            Property prop = targets[i].getProperty("haystack");
            if (prop == null) continue;

            if (prop.isDynamic()) {
                targets[i].remove("haystack");
                count++;
            }
            else {
                targets[i].set("haystack", BHDict.DEFAULT);
                count++;
            }
        }

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("rowsChanged", HNum.make(targets.length));
        return HGridBuilder.dictToGrid(hdb.toDict());
    }

    /**
      * addEquips
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
                "site and dis == \"" + siteName + "\"", false);

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
        for (int i = 0; i < targets.length; i++)
        {
            BComponent target = targets[i];
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
            }
        }

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("rowsChanged", HNum.make(count));
        return HGridBuilder.dictToGrid(hdb.toDict());
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
      * searchAndReplace
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
        for (int i = 0; i < comps.length; i++)
        {
            BComponent comp = comps[i];
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
        List combinations = new ArrayList();
        combineMarkers(markers, new ArrayList(), 0, combinations);

        // for each combination, count the number of times it occurs
        // in any of the rows in the grid
        Array resultRows = new Array(HDict.class);
        for (int i = 0; i < combinations.size(); i++)
        {
            String[] tags = (String[]) combinations.get(i);
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
        return HGridBuilder.dictsToGrid((HDict[]) resultRows.trim());
    }

    /**
      * find all the distinct marker tags in the entire grid
      */
    static String[] distinctMarkers(HGrid grid)
    {
        Set set = new TreeSet();
        for (int i = 0; i < grid.numRows(); i++)
        {
            Iterator it = grid.row(i).iterator();
            while (it.hasNext())
            {
                Map.Entry e = (Map.Entry) it.next();
                String key = (String) e.getKey();
                HVal val = (HVal) e.getValue();
                if (val instanceof HMarker) set.add(key);
            }
        }
        List list = new ArrayList(set);
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
      * generate all of the combinations of markers
      */
    static void combineMarkers(String[] markers, List temp, int index, List combinations)
    {
        for (int i = index; i < markers.length; i++)
        {
            temp.add(markers[i]);
            combinations.add(temp.toArray(new String[temp.size()]));
            combineMarkers(markers, temp, i + 1, combinations);
            temp.remove(temp.size() - 1);
        }
    } 

    /**
      * return whether the dict has all of the tags
      */
    static boolean dictHasAllTags(HDict dict, String[] tags)
    {
        for (int i = 0; i < tags.length; i++)
            if (!dict.has(tags[i])) return false;
        return true;
    }

    /**
      * showWatches
      */
    private static HGrid showWatches(NHServer server, HRow params)
    {
        Array arr = new Array(HDict.class);

        HWatch[] watches = server.getWatches();
        for (int i = 0; i < watches.length; i++)
        {
            HRef watchId = HRef.make(watches[i].id());
            NHWatch watch = (NHWatch) watches[i];

            HDict[] subscribed = watch.curSubscribed();

            HDictBuilder hdb = new HDictBuilder();
            hdb.add("id", watchId);
            hdb.add("dis", Sys.getStation().getName());
            hdb.add("watchCount", subscribed.length);
            hdb.add("lastPoll", HNum.make(watch.lastPoll(), "ms"));
            arr.add(hdb.toDict());
        }

        return HGridBuilder.dictsToGrid((HDict[]) arr.trim());
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

        Array result = new Array(HDict.class);
        BHEquip[] equips = cache.getAllEquips();
        for (int i = 0; i < equips.length; i++)
        {
            BComponent[] points = cache.getEquipPoints(equips[i]);

            // only included annotated points
            if (annotatedOnly)
            {
                Array pointArr = new Array(BComponent.class);
                for (int j = 0; j < points.length; j++)
                    if (BHDict.findTagAnnotation(points[j]) != null)
                        pointArr.add(points[j]);
                points = (BComponent[]) pointArr.trim();
            }

            Map map = new HashMap();
            for (int j = 0; j < points.length; j++)
            {
                NHRef nref = cache.lookupSepRefByComponent(points[j]);
                if (nref == null) continue;
                HRef ref = nref.getHRef();
                Array pointArr = (Array) map.get(ref);
                if (pointArr == null)
                    map.put(ref, pointArr = new Array(BComponent.class));
                pointArr.add(points[j]);
            }

            Iterator it = map.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry) it.next();
                HRef ref = (HRef) entry.getKey();
                Array pointArr = (Array) entry.getValue();

                if (pointArr.size() > 1)
                {
                    for (int j = 0; j < pointArr.size(); j++)
                    {
                        BComponent point = (BComponent) pointArr.get(j);
                        result.add(makeDuplicateDict(ref, point));
                    }
                }
            }
        }

        return HGridBuilder.dictsToGrid((HDict[]) result.trim());
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

        Array arr = new Array(HDict.class);
        for (int i = 1; i < req.numRows(); i++)
        {
            HRow inRow = req.row(i);

            HRef haystackCur = inRow.getRef("haystackCur");
            HDict outRow = server.onReadById(haystackCur);
            if (outRow == null) continue;

            HDictBuilder hdb = new HDictBuilder();
            hdb.add("id", inRow.id());
            hdb.add("mod", inRow.get("mod"));
            for (int j = 0; j < tags.length; j++)
            {
                String tag = tags[j];

                if (outRow.has(tag))
                    hdb.add(tag, outRow.get(tag));
                else
                    hdb.add(tag, REMOVE);
            }
            arr.add(hdb.toDict());
        }

        return HGridBuilder.dictsToGrid((HDict[]) arr.trim());
    }

////////////////////////////////////////////////////////////////////////////
//// ScheduleRead
////////////////////////////////////////////////////////////////////////////
//
//    static class ScheduleReadOp extends HOp
//    {
//        public String name() { return "scheduleRead"; }
//        public String summary() { return "Read time series from point schedule"; }
//        public HGrid onService(HServer db, HGrid req) throws Exception
//        {
//            NHServer server = (NHServer) db;
//            if (!server.getCache().initialized()) 
//                throw new IllegalStateException(Cache.NOT_INITIALIZED);
//
//            if (req.isEmpty()) throw new Exception("Request has no rows");
//            HRow row = req.row(0);
//            HRef id = valToId(db, row.get("id"));
//
//            return server.scheduleRead(id);
//        }
//    }
//
////////////////////////////////////////////////////////////////////////////
//// ScheduleWriteOp
////////////////////////////////////////////////////////////////////////////
//
//    static class ScheduleWriteOp extends HOp
//    {
//        public String name() { return "scheduleWrite"; }
//        public String summary() { return "Write time series data to point schedule"; }
//        public HGrid onService(HServer db, HGrid req) throws Exception
//        {
//            NHServer server = (NHServer) db;
//            if (!server.getCache().initialized()) 
//                throw new IllegalStateException(Cache.NOT_INITIALIZED);
//
//            if (req.isEmpty()) throw new Exception("Request has no rows");
//            HRef id = valToId(db, req.meta().get("id"));
//
//            HHisItem[] items = HHisItem.gridToItems(req);
//            server.scheduleWrite(id, items);
//            return HGrid.EMPTY;
//        }
//    }
//
////////////////////////////////////////////////////////////////
// utils
////////////////////////////////////////////////////////////////

    private static BComponent[] getFilterComponents(NHServer server, String filter, String ids)
    {
        HServer hserver = server;
        TagManager tagMgr = server.getTagManager();

        Array compArr = new Array(BComponent.class);
        Context cx = ThreadContext.getContext(Thread.currentThread());

        // if there is an id list, parse it and look up all the recs
        if ((ids != null) && (ids.length() > 0))
        {
            HRef refs[] = parseIdList(ids);
            if (refs.length > 0)
            {
                Array dictArr = new Array(HDict.class);
                for (int i = 0; i < refs.length; i++)
                {
                    BComponent comp = tagMgr.doLookupComponent(refs[i], false);
                    if (!TypeUtil.canWrite(comp, cx)) 
                        throw new PermissionException("Cannot write to " + refs[i]); 

                    if (comp != null)
                    {
                        compArr.add(comp);
                        dictArr.add(tagMgr.createTags(comp));
                    }
                }

                // filter against the recs
                hserver = new HDictFilterer((HDict[]) dictArr.trim()); 
            }
        }

        // filter against either the entire database, or the id list recs
        if ((filter != null) && (filter.length() > 0))
        {
            compArr.clear();
            HGrid grid = hserver.readAll(filter);
            for (int i = 0; i < grid.numRows(); i++)
            {
                HStr slotPath = (HStr) grid.row(i).get("axSlotPath", false);
                if (slotPath != null)
                    compArr.add(BOrd.make("station:|" + slotPath.val).get(
                            server.getService(), null));
            }
        }

        return (BComponent[]) compArr.trim();
    }

    // this class just provides an iterator() so that we can filter against a list of ids
    private static class HDictFilterer extends HServer
    {
        private final List list;
        private HDictFilterer(HDict[] dicts) { this.list = Arrays.asList(dicts); }

        protected Iterator iterator() { return list.iterator(); }

        // HProj
        protected HDict onReadById(HRef id) { throw new UnsupportedOperationException(); }

        // HServer
        public HOp[] ops() { throw new UnsupportedOperationException(); }
        protected HDict onAbout() { throw new UnsupportedOperationException(); }
        protected HGrid onNav(String navId) { throw new UnsupportedOperationException(); }
        protected HDict onNavReadByUri(HUri uri) { throw new UnsupportedOperationException(); }
        protected HWatch onWatchOpen(String dis, HNum lease) { throw new UnsupportedOperationException(); }
        protected HWatch[] onWatches() { throw new UnsupportedOperationException(); }
        protected HWatch onWatch(String id) { throw new UnsupportedOperationException(); }
        protected HGrid onPointWriteArray(HDict rec) { throw new UnsupportedOperationException(); }
        protected void onPointWrite(HDict rec, int level, HVal val, String who, HNum dur, HDict ops) { throw new UnsupportedOperationException(); }
        protected HHisItem[] onHisRead(HDict rec, HDateTimeRange range) { throw new UnsupportedOperationException(); }
        protected void onHisWrite(HDict rec, HHisItem[] items) { throw new UnsupportedOperationException(); }
        protected HGrid onInvokeAction(HDict rec, String action, HDict args) { throw new UnsupportedOperationException(); }
    }

    private static HRef[] parseIdList(String ids)
    {
        int len = ids.length();

        if ((ids.charAt(0) != '[') || (ids.charAt(len-1) != ']'))
            throw new IllegalStateException(ids + " is malformed.");

        if (ids.equals("[,]")) return new HRef[0];

        String[] tokens = TextUtil.split(ids.substring(1, len-1), ',');

        HRef[] refs = new HRef[tokens.length];
        for (int i = 0; i < tokens.length; i++)
            refs[i] = HRef.make(tokens[i].trim());

        return refs;
    }

    static HRef valToId(HServer db, HVal val)
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

    private static final Log LOG = Log.getLog("nhaystack");

    private static final HStr REMOVE = HStr.make("_remove_");
}
