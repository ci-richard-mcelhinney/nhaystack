//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
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
import javax.baja.timezone.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.io.*;
import org.projecthaystack.server.*;
import org.projecthaystack.util.*;

import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.driver.history.*;
import nhaystack.site.*;
import nhaystack.util.*;

/**
  * Custom Ops for NHServer
  */
class NHServerOps
{

//////////////////////////////////////////////////////////////////////////
// ApplyBatchTags
//////////////////////////////////////////////////////////////////////////

    static class ApplyBatchTags extends HOp
    {
        public String name() { return "applyBatchTags"; }
        public String summary() { return "Apply Batch Tags"; }
        public HGrid onService(HServer db, HGrid req)
        {
            NHServer server = (NHServer) db;

            HRow params = req.row(0);

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

            if (returnResultRows)
            {
                return HGridBuilder.dictsToGrid(rows);
            }
            else
            {
                HDictBuilder hdb = new HDictBuilder();
                hdb.add("rowsChanged", HNum.make(targets.length));
                return HGridBuilder.dictToGrid(hdb.toDict());
            }
        }
    }

//////////////////////////////////////////////////////////////////////////
// AddHaystackSlots
//////////////////////////////////////////////////////////////////////////

    static class AddHaystackSlots extends HOp
    {
        public String name() { return "addHaystackSlots"; }
        public String summary() { return "Add Haystack Slots"; }
        public HGrid onService(HServer db, HGrid req)
        {
            NHServer server = (NHServer) db;

            HRow params = req.row(0);
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
    }

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

            HRow params = req.row(0);

            String filter = params.getStr("filter");

            int limit = params.has("limit") ?
                params.getInt("limit") :
                Integer.MAX_VALUE;

            HGrid grid = server.onReadAll(filter, limit);

           // size
           if (params.has("size") && params.get("size").equals(HBool.TRUE))
               grid = makeSizeGrid(grid);

           // unique
           else if (params.has("unique"))
               grid = makeUniqueGrid(grid, params.getStr("unique"));

           return grid;
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
// SearchAndReplace
//////////////////////////////////////////////////////////////////////////

    static class SearchAndReplace extends HOp
    {
        public String name() { return "searchAndReplace"; }
        public String summary() { return "Search and Replace"; }
        public HGrid onService(HServer db, HGrid req)
        {
            NHServer server = (NHServer) db;

            HRow params = req.row(0);
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
    }
 
//////////////////////////////////////////////////////////////////////////
// WatchStatus
//////////////////////////////////////////////////////////////////////////

    static class WatchStatus extends HOp
    {
        public String name() { return "watchStatus"; }
        public String summary() { return "Status of currrently open Watches"; }
        public HGrid onService(HServer db, HGrid req)
        {
            NHServer server = (NHServer) db;

            Array arr = new Array(HDict.class);

            HWatch[] watches = server.getWatches();
            for (int i = 0; i < watches.length; i++)
            {
                HRef watchId = HRef.make(watches[i].id());

                HDict[] sub = ((NHWatch) watches[i]).curSubscribed();

                HDictBuilder hdb = new HDictBuilder();
                hdb.add("id", watchId);
                hdb.add("watch");
                hdb.add("subscriptionCount", sub.length);
                arr.add(hdb.toDict());

                for (int j = 0; j < sub.length; j++)
                {
                    hdb = new HDictBuilder();
                    hdb.add("watchRef", watchId);

                    Iterator it = sub[j].iterator();
                    while (it.hasNext())
                    {
                        Map.Entry e = (Map.Entry) it.next();
                        String key = (String) e.getKey();
                        HVal   val = (HVal)   e.getValue();
                        hdb.add(key, val);
                    }
                    arr.add(hdb.toDict());
                }
            }

            return HGridBuilder.dictsToGrid((HDict[]) arr.trim());
        }
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

    private static final HStr REMOVE = HStr.make("_remove_");
}
