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
import javax.baja.util.*;

import haystack.*;
import haystack.server.*;
import haystack.util.*;
import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.server.storehouse.*;
import nhaystack.site.*;

/**
  * NHServer is responsible for serving up 
  * haystack-annotated BComponents.
  */
public class NHServer extends HServer
{
    public NHServer(BNHaystackService service)
    {
        this.service = service;
        this.compStore = new ComponentStorehouse(this);
        this.hisStore = new HistoryStorehouse(this);
        this.cache = new Cache(this);
        this.nav = new Nav(service, compStore, hisStore, cache);
    }

////////////////////////////////////////////////////////////////
// HServer
////////////////////////////////////////////////////////////////

    /**
      * Return the operations supported by this database.
      */
    public HOp[] ops()
    {
        return OPS;
    }

    /**
      * Return the 'about' tags.
      */
    protected HDict onAbout()
    {
        if (LOG.isTraceOn())
            LOG.trace("onAbout");

        try

        {
            HDictBuilder hd = new HDictBuilder();

            hd.add("serverName", Sys.getStation().getStationName());

            BModule baja = BComponent.TYPE.getModule();
            hd.add("productName",    "Niagara AX");
            hd.add("productVersion", baja.getVendorVersion().toString());
            hd.add("productUri",     HUri.make("http://www.tridium.com/"));

            BModule module = BNHaystackService.TYPE.getModule();
            hd.add("moduleName",    module.getModuleName());
            hd.add("moduleVersion", module.getVendorVersion().toString());
            hd.add("moduleUri",     HUri.make("https://bitbucket.org/jasondbriggs/nhaystack"));

            return hd.toDict();
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    protected HGrid onReadAll(String filter, int limit)
    {
        try
        {
            long ticks = Clock.ticks();
            HGrid grid = super.onReadAll(filter, limit);

            if (LOG.isTraceOn())
                LOG.trace("onReadAll " + (Clock.ticks()-ticks) + "ms.");

            return grid;
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * Iterate every haystack-annotated entry in both the 
      * BComponentSpace and the BHistoryDatabase.
      */
    protected Iterator iterator()
    {
        try
        {
            return new CompositeIterator(new Iterator[] { 
                compStore.makeIterator(),
                hisStore.makeIterator() });
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * Look up the HDict representation of a BComponent 
      * by its id.
      *
      * Return null if the BComponent cannot be found,
      * or if it is not haystack-annotated.
      */
    protected HDict onReadById(HRef id)
    {
        if (LOG.isTraceOn())
            LOG.trace("onReadById " + id);

        // we can assume this because onNavReadByUri will have
        // already been called for us.
        HRef ref = (HRef) id;

        try
        {
            BComponent comp = lookupComponent(ref);
            return (comp == null) ? null : createTags(comp);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * Return navigation tree children for given navId.
      * The grid must define the "navId" column.
      */
    protected HGrid onNav(String navId)
    {
        if (LOG.isTraceOn())
            LOG.trace("onNav " + navId);

        try
        {
            return nav.onNav(navId);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * Open a new watch.
      */
    protected HWatch onWatchOpen(String dis)
    {
        if (LOG.isTraceOn())
            LOG.trace("onWatchOpen " + dis);

        try
        {
            NHWatch watch = new NHWatch(
                this, 
                dis, 
                service.getLeaseInterval().getMillis());

            watches.put(watch.id(), watch);
            return watch;
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * Return current watches
      */
    protected HWatch[] onWatches()
    {
        if (LOG.isTraceOn())
            LOG.trace("onWatches");

        try
        {
            HWatch[] arr = new HWatch[watches.size()];
            int n = 0;
            Iterator itr = watches.values().iterator();
            while (itr.hasNext())
                arr[n++] = (HWatch) itr.next();
            return arr;
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * Look up a watch by id
      */
    protected HWatch onWatch(String id)
    {
        try
        {
            return (HWatch) watches.get(id);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * Implementation hook for pointWriteArray
      */
    protected HGrid onPointWriteArray(HDict rec)
    {
        if (LOG.isTraceOn())
            LOG.trace("onPointWriteArray " + rec.id());

        try
        {
            HVal[] vals = new HVal[17];

            BControlPoint point = (BControlPoint) lookupComponent(rec.id());

            // Numeric
            if (point instanceof BNumericWritable)
            {
                BNumericWritable nw = (BNumericWritable) point;
                for (int i = 0; i < 16; i++)
                {
                    BStatusNumeric sn = (BStatusNumeric) nw.get("in" + (i+1));
                    if (!sn.getStatus().isNull())
                        vals[i] = HNum.make(sn.getValue());
                }
                BStatusNumeric sn = (BStatusNumeric) nw.getFallback();
                if (!sn.getStatus().isNull())
                    vals[16] = HNum.make(sn.getValue());
            }
            // Boolean
            else if (point instanceof BBooleanWritable)
            {
                BBooleanWritable bw = (BBooleanWritable) point;
                for (int i = 0; i < 16; i++)
                {
                    BStatusBoolean sb = (BStatusBoolean) bw.get("in" + (i+1));
                    if (!sb.getStatus().isNull())
                        vals[i] = HBool.make(sb.getValue());
                }
                BStatusBoolean sb = (BStatusBoolean) bw.getFallback();
                if (!sb.getStatus().isNull())
                    vals[16] = HBool.make(sb.getValue());
            }
            // Enum
            else if (point instanceof BEnumWritable)
            {
                BEnumWritable ew = (BEnumWritable) point;
                for (int i = 0; i < 16; i++)
                {
                    BStatusEnum se = (BStatusEnum) ew.get("in" + (i+1));
                    if (!se.getStatus().isNull())
                        vals[i] = HStr.make(se.getValue().getTag());
                }
                BStatusEnum se = (BStatusEnum) ew.getFallback();
                if (!se.getStatus().isNull())
                    vals[16] = HStr.make(se.getValue().getTag());
            }
            else throw new IllegalStateException();

            //////////////////////////////////////////////

            // Return priority array for writable point identified by id.
            // The grid contains 17 rows with following columns:
            //   - level: number from 1 - 17 (17 is default)
            //   - levelDis: human description of level
            //   - val: current value at level or null
            //   - who: who last controlled the value at this level

            Map lastWrite = getLastWrite(point);
            HDict[] result = new HDict[17];
            for (int i = 0; i < 17; i++)
            {
                HDictBuilder hd = new HDictBuilder();
                HNum level = HNum.make(i+1);
                hd.add("level", level);
                hd.add("levelDis", "level " + (i+1)); // TODO
                if (vals[i] != null)
                    hd.add("val", vals[i]);

                HDict lw = (HDict) lastWrite.get(level);
                if (lw != null)
                    hd.add("who", lw.getStr("who"));

                result[i] = hd.toDict();
            }
            return HGridBuilder.dictsToGrid(result);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
  
    /**
      * Implementation hook for pointWrite
      */
    protected void onPointWrite(
        HDict rec, 
        int level, 
        HVal val, 
        String who, 
        HNum dur) // ignore this for now
    {
        if (LOG.isTraceOn())
            LOG.trace("onPointWrite " + 
              "id:   "  + rec.id()   + ", " +
              "level: " + level + ", " +
              "val:   " + val   + ", " +
              "who:   " + who);

        try
        {
            BControlPoint point = (BControlPoint) lookupComponent(rec.id());

            if (point instanceof BNumericWritable)
            {
                BNumericWritable nw = (BNumericWritable) point;
                BStatusNumeric sn = nw.getLevel(BPriorityLevel.make(level));

                if (val == null)
                {
                    sn.setStatus(BStatus.nullStatus);
                }
                else
                {
                    HNum num = (HNum) val;
                    sn.setValue(num.val);
                    sn.setStatus(BStatus.ok);
                }

                saveLastWrite(point, level, who);
            }
            else if (point instanceof BBooleanWritable)
            {
                BBooleanWritable bw = (BBooleanWritable) point;
                BStatusBoolean sb = bw.getLevel(BPriorityLevel.make(level));

                if (val == null)
                {
                    sb.setStatus(BStatus.nullStatus);
                }
                else
                {
                    HBool bool = (HBool) val;
                    sb.setValue(bool.val);
                    sb.setStatus(BStatus.ok);
                }

                saveLastWrite(point, level, who);
            }
            else if (point instanceof BEnumWritable)
            {
                BEnumWritable ew = (BEnumWritable) point;
                BStatusEnum se = ew.getLevel(BPriorityLevel.make(level));

                if (val == null)
                {
                    se.setStatus(BStatus.nullStatus);
                }
                else
                {
                    HStr str = (HStr) val;
                    BEnumRange range = (BEnumRange) point.getFacets().get(BFacets.RANGE);
                    se.setValue(range.get(str.val));
                    se.setStatus(BStatus.ok);
                }

                saveLastWrite(point, level, who);
            }
            else
                throw new IllegalStateException("Cannot write to " + point.getType());
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * Read the history for the given BComponent.
      * The items wil be exclusive of start and inclusive of end time.
      */
    protected HHisItem[] onHisRead(HDict rec, HDateTimeRange range)
    {
        if (LOG.isTraceOn())
            LOG.trace("onHisRead " + rec + ", " + range);

        try
        {
            BHistoryConfig cfg = lookupHisRead(rec.id());
            if (cfg == null) return new HHisItem[0];

            HStr unit = (HStr) rec.get("unit", false);

            // ASSUMPTION: the tz in both ends of the range matches the 
            // tz of the historized point, which in turn matches the 
            // history's tz in its historyConfig.
            HTimeZone tz = range.start.tz;

            BAbsTime rangeStart = BAbsTime.make(range.start.millis(), cfg.getTimeZone());
            BAbsTime rangeEnd   = BAbsTime.make(range.end.millis(),   cfg.getTimeZone());

            // NOTE: be careful, timeQuery() is inclusive of both start and end
            BIHistory history = service.getHistoryDb().getHistory(cfg.getId());
            BITable table = (BITable) history.timeQuery(rangeStart, rangeEnd);
            ColumnList columns = table.getColumns();
            Column timestampCol = columns.get("timestamp");

            // this will be null if its not a BTrendRecord
            boolean isTrendRecord = cfg.getRecordType().getResolvedType().is(BTrendRecord.TYPE);
            Column valueCol = isTrendRecord ? columns.get("value") : null;

            Array arr = new Array(HHisItem.class, table.size());
            for (int i = 0; i < table.size(); i++)
            {
                BAbsTime timestamp = (BAbsTime) table.get(i, timestampCol);

                // ignore inclusive start value
                if (timestamp.equals(rangeStart)) continue;

                // create ts
                HDateTime ts = HDateTime.make(timestamp.getMillis(), tz);

                // create val
                HVal val = null;
                if (isTrendRecord)
                {
                    // extract value from BTrendRecord
                    BValue value = (BValue) table.get(i, valueCol);

                    Type recType = cfg.getRecordType().getResolvedType();
                    if (recType.is(BNumericTrendRecord.TYPE))
                    {
                        BNumber num = (BNumber) value;
                        val = (unit == null) ? 
                            HNum.make(num.getDouble()) :
                            HNum.make(num.getDouble(), unit.val);
                    }
                    else if (recType.is(BBooleanTrendRecord.TYPE))
                    {
                        BBoolean bool = (BBoolean) value;
                        val = HBool.make(bool.getBoolean());
                    }
                    else
                    {
                        val = HStr.make(value.toString());
                    }
                }
                else
                {
                    // if its not a BTrendRecord, just do a toString() 
                    // of the whole record
                    val = HStr.make(table.get(i).toString());
                }

                // add item
                arr.add(HHisItem.make(ts, val));
            }

            // done
            return (HHisItem[]) arr.trim();
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * Write the history for the given BComponent.
      * This is not currently supported.
      */
    protected void onHisWrite(HDict rec, HHisItem[] items)
    {
        throw new UnsupportedOperationException();
    }

    /**
      * Implementation hook for invokeAction
      */
    protected HGrid onInvokeAction(HDict rec, String actionName, HDict args)
    {
        if (LOG.isTraceOn())
            LOG.trace("onInvokeAction " + rec + ", " + actionName + ", " + args);

        try
        {
            BComponent comp = lookupComponent(rec.id());

            Action[] actions = comp.getActionsArray();
            for (int i = 0; i < actions.length; i++)
            {
                if (actions[i].getName().equals(actionName))
                {
                    BValue result = comp.invoke(
                        actions[i], 
                        Types.actionArgsToBaja(args, actions[i]));

                    if (result == null)
                    {
                        return HGrid.EMPTY;
                    }
                    else if (result instanceof BSimple)
                    {
                        HDictBuilder hd = new HDictBuilder();
                        hd.add("result", Types.fromBajaSimple((BSimple) result));
                        return HGridBuilder.dictToGrid(hd.toDict());
                    }
                    else
                    {
                        // TODO
                        throw new IllegalStateException(
                            "Don't know how to return complex result " + result.getClass());
                    }
                }
            }

            throw new IllegalStateException(
                "Cannot find action '" + actionName + "' on component " +
                comp.getSlotPath());
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

   /**
     * Implementation hook for navReadByUri.  Return null if not
     * found.  Do NOT raise any exceptions.
     */
    protected HDict onNavReadByUri(HUri uri)
    {
        if (LOG.isTraceOn())
            LOG.trace("onNavReadByUri " + uri);

        // e.g. "/site/Blacksburg/Transmogrifier/SineWave1"
        if (!uri.val.startsWith("/site/")) return null;
        String str = uri.val.substring("/site/".length());
        if (str.endsWith("/")) str = str.substring(0, str.length() - 1);

        String[] navNames = TextUtil.split(str, '/');
        switch (navNames.length)
        {
            // site
            case 1:

                BComponent comp = cache.getNavSite(
                    SiteNavId.make(navNames[0]));

                return (comp == null) ?  null : 
                    compStore.createComponentTags(comp);

            // equip
            case 2:

                comp = cache.getNavEquip(
                    EquipNavId.make(navNames[0], navNames[1]));

                return (comp == null) ?  null : 
                    compStore.createComponentTags(comp);

            // point
            case 3:

                comp = cache.getNavPoint(
                    EquipNavId.make(navNames[0], navNames[1]), 
                    navNames[2]);

                return (comp == null) ?  null : 
                    compStore.createComponentTags(comp);

            // bad uri
            default: return null;
        }
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    /**
      * Create the haystack representation of a BComponent.
      *
      * The haystack representation is a combination of the 
      * autogenerated tags, and those tags specified
      * in the explicit haystack annotation (if any).
      *
      * This method never returns null.
      */
    public HDict createTags(BComponent comp)
    {
        if (comp instanceof BHistoryConfig)
            return hisStore.createHistoryTags((BHistoryConfig) comp);
        else
            return compStore.createComponentTags(comp);
    }

    /**
      * Look up the a BComponent by its HRef id.
      *
      * Return null if the BComponent cannot be found,
      * or if it is not haystack-annotated.
      */
    public BComponent lookupComponent(HRef id)
    {
        NHRef nh = NHRef.make(id);

        // component space
        if (nh.getSpace().equals(NHRef.COMPONENT))
        {
            BComponent comp = (BComponent) nh.getOrd().get(service, null);
            if (comp == null) return null;
            return compStore.isVisibleComponent(comp) ? comp : null;
        }
        // history space
        else if (nh.getSpace().equals(NHRef.HISTORY))
        {
            BHistoryId hid = BHistoryId.make(
                Base64.URI.decodeUTF8(nh.getPath()));

            BIHistory history = service.getHistoryDb().getHistory(hid);
            BHistoryConfig cfg = history.getConfig();
            return hisStore.isVisibleHistory(cfg) ? cfg : null;
        }
        // invalid space
        else 
        {
            return null;
        }
    }

////////////////////////////////////////////////////////////////
// package-scope
////////////////////////////////////////////////////////////////

    void removeWatch(String watchId)
    {
        watches.remove(watchId);
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private BHistoryConfig lookupHisRead(HRef id)
    {
        BComponent comp = lookupComponent(id);
        if (comp == null)
        {
            LOG.error("lookup failed for '" + id + "'");
            return null;
        }

        // history space
        if (comp instanceof BHistoryConfig)
        {
            BHistoryConfig cfg = (BHistoryConfig) comp;
            return hisStore.isVisibleHistory(cfg) ? 
                cfg : null;
        }
        // component space
        else if (comp instanceof BControlPoint)
        {
            return hisStore.lookupHistoryFromPoint((BControlPoint) comp);
        }
        else
        {
            LOG.error("cannot find history for for '" + id + "'");
            return null;
        }
    }

    /**
      * save the last point write to a BHGrid slot.
      */
    private static void saveLastWrite(BControlPoint point, int level, String who)
    {
        BHGrid oldGrid = (BHGrid) point.get(LAST_WRITE);

        if (oldGrid == null)
        {
            HGrid grid = saveLastWriteToGrid(HGrid.EMPTY, level, who);
            point.add(
                LAST_WRITE, 
                BHGrid.make(grid),
                Flags.SUMMARY | Flags.READONLY);
        }
        else
        {
            HGrid grid = saveLastWriteToGrid(oldGrid.getGrid(), level, who);
            point.set(
                LAST_WRITE, 
                BHGrid.make(grid));
        }
    }

    /**
      * get the last write
      */
    private static Map getLastWrite(BControlPoint point)
    {
        Map map = new HashMap();

        BHGrid bgrid = (BHGrid) point.get(LAST_WRITE);
        if (bgrid != null) 
        {
            HGrid grid = bgrid.getGrid();
            for (int i = 0; i < grid.numRows(); i++)
            {
                HDict row = grid.row(i);
                map.put(row.get("level"), row);
            }
        }

        return map;
    }

    private static HGrid saveLastWriteToGrid(HGrid grid, int level, String who)
    {
        // store rows by level
        Map map = new HashMap();
        for (int i = 0; i < grid.numRows(); i++)
        {
            HDict row = grid.row(i);
            map.put(row.get("level"), row);
        }

        // create or replace new row
        HNum hlevel = HNum.make(level);
        HDictBuilder db = new HDictBuilder();
        db.add("level", hlevel);
        db.add("who", HStr.make(who));
        map.put(hlevel, db.toDict());

        // create new grid
        HDict[] dicts = new HDict[map.size()];
        int n = 0;
        Iterator it = map.values().iterator();
        while (it.hasNext())
            dicts[n++] = (HDict) it.next();

        return HGridBuilder.dictsToGrid(dicts);
    }

////////////////////////////////////////////////////////////////
// access
////////////////////////////////////////////////////////////////

    public BNHaystackService getService() { return service; }

    public ComponentStorehouse getComponentStorehouse() { return compStore; }
    public HistoryStorehouse   getHistoryStorehouse()   { return hisStore;  }

    public Cache getCache() { return cache; }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private static final String LAST_WRITE = "haystackLastWrite";

    private static final HOp[] OPS = new HOp[]
    {
        HStdOps.about,
        HStdOps.ops,
        HStdOps.formats,
        HStdOps.read,
        HStdOps.nav,
        HStdOps.watchSub,
        HStdOps.watchUnsub,
        HStdOps.watchPoll,
        HStdOps.pointWrite,
        HStdOps.hisRead,
        HStdOps.hisWrite,
        HStdOps.invokeAction,
    };

    private final HashMap watches = new HashMap();

    private final BNHaystackService service;
    private final ComponentStorehouse compStore;
    private final HistoryStorehouse hisStore;
    private final Cache cache;
    private final Nav nav;
}

