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
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.io.*;
import org.projecthaystack.server.*;

import nhaystack.*;
import nhaystack.site.*;

/**
  * Custom Ops for NHServer
  */
class NHServerOps
{

//////////////////////////////////////////////////////////////////////////
// ExtendedRead
//////////////////////////////////////////////////////////////////////////

    static class ExtendedRead extends HOp
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
// Extended
//////////////////////////////////////////////////////////////////////////

    static class Extended extends HOp
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
            if      (function.equals("addHaystackSlots"))    result = addHaystackSlots    (server, params);
            else if (function.equals("applyBatchTags"))      result = applyBatchTags      (server, params);
            else if (function.equals("findDuplicatePoints")) result = findDuplicatePoints (server, params);
            else if (function.equals("searchAndReplace"))    result = searchAndReplace    (server, params);
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
      * applyBatchTags
      */
    private static HGrid applyBatchTags(NHServer server, HRow params)
    {
        String tags = params.getStr("tags");
        String targetFilter = params.getStr("targetFilter");

        boolean returnResultRows = false;
        if (params.has("returnResultRows"))
            returnResultRows = params.getBool("returnResultRows");

        HDict newTags = new HZincReader(tags).readDict();

        BComponent[] targets = getFilterComponents(server, targetFilter);
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

        int count = 0;
        BComponent[] targets = getFilterComponents(server, targetFilter);
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
      * searchAndReplace
      */
    private static HGrid searchAndReplace(NHServer server, HRow params)
    {
        String filter = params.getStr("filter");
        String searchText = params.getStr("searchText");
        String replaceText = params.getStr("replaceText");

        int count = 0;
        BComponent[] comps = getFilterComponents(server, filter);
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
        Cache cache = server.getCache();

        Array arr = new Array(HDict.class);
        BHEquip[] equips = cache.getAllEquips();
        for (int i = 0; i < equips.length; i++)
        {
            BControlPoint[] points = cache.getEquipPoints(equips[i]);

            HRef[] refs = new HRef[points.length]; 
            for (int j = 0; j < points.length; j++)
                refs[j] = cache.lookupSepRefByComponent(points[j]).getHRef();
            SortUtil.sort(refs);

            int a = 0;
            while (a < refs.length - 1)
            {
                if (refs[a].val.equals(refs[a+1].val))
                {
                    arr.add(makeDuplicateDict(refs[a], points[a]));
                    arr.add(makeDuplicateDict(refs[a], points[a+1]));
                    int b = a+2;
                    while ((b < refs.length) && refs[a].val.equals(refs[b].val))
                        arr.add(makeDuplicateDict(refs[a], points[b++]));
                    a = b;
                }
                else a++;
            }
        }

        return HGridBuilder.dictsToGrid((HDict[]) arr.trim());
    }

    private static HDict makeDuplicateDict(HRef ref, BControlPoint point)
    {
        HDictBuilder hdb = new HDictBuilder();
        hdb.add("id", ref);
        hdb.add("slotPath", point.getSlotPath().toString());
        return hdb.toDict();
    }

////////////////////////////////////////////////////////////////
// utils
////////////////////////////////////////////////////////////////

    private static BComponent[] getFilterComponents(NHServer server, String filter)
    {
        Array arr = new Array(BComponent.class);

        HGrid grid = server.readAll(filter);
        for (int i = 0; i < grid.numRows(); i++)
        {
            HStr slotPath = (HStr) grid.row(i).get("axSlotPath", false);
            if (slotPath != null)
                arr.add(BOrd.make("station:|" + slotPath.val).get(
                        server.getService(), null));
        }

        return (BComponent[]) arr.trim();
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private static final HStr REMOVE = HStr.make("_remove_");
}
