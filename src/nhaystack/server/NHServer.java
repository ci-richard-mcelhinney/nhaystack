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

        this.configStorehouse  = new ConfigStorehouse(this);
        this.historyStorehouse = new HistoryStorehouse(this);
        this.siteStorehouse    = new SiteStorehouse(this);
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
                configStorehouse.makeIterator(),
                historyStorehouse.makeIterator() });
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
    protected HDict onReadById(HIdentifier id)
    {
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
        try
        {
            // nav roots
            if (navId == null)
            {
                Array dicts = new Array(HDict.class);

                HDictBuilder hd = new HDictBuilder();
                hd.add("navId", HStr.make(NHRef.COMPONENT));
                hd.add("dis", "ComponentSpace");
                dicts.add(hd.toDict());

                hd = new HDictBuilder();
                hd.add("navId", HStr.make(NHRef.HISTORY));
                hd.add("dis", "HistorySpace");
                dicts.add(hd.toDict());

                hd = new HDictBuilder();
                hd.add("navId", HStr.make(SiteNavId.SITE));
                hd.add("dis", "Sites");
                dicts.add(hd.toDict());

                return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
            }
            // config nav
            else if (navId.startsWith(NHRef.COMPONENT))
            {
                return configStorehouse.onNav(navId);
            }
            // history nav
            else if (navId.startsWith(NHRef.HISTORY))
            {
                return historyStorehouse.onNav(navId);
            }
            // site nav
            else if (navId.startsWith(SiteNavId.SITE) || navId.startsWith(EquipNavId.EQUIP))
            {
                return siteStorehouse.onNav(navId);
            }
            // error
            else
            {
                throw new IllegalStateException("Cannot lookup nav for " + navId);
            }
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
        try
        {
            HVal[] vals = new HVal[17];
            BControlPoint point = (BControlPoint) lookupComponent(rec.id());

            if (point instanceof BNumericWritable)
            {
                BNumericWritable nw = (BNumericWritable) point;
                vals[  0] = HNum.make(nw.getIn1().getValue());
                vals[  1] = HNum.make(nw.getIn2().getValue());
                vals[  2] = HNum.make(nw.getIn3().getValue());
                vals[  3] = HNum.make(nw.getIn4().getValue());
                vals[  4] = HNum.make(nw.getIn5().getValue());
                vals[  5] = HNum.make(nw.getIn6().getValue());
                vals[  6] = HNum.make(nw.getIn7().getValue());
                vals[  7] = HNum.make(nw.getIn8().getValue());
                vals[  8] = HNum.make(nw.getIn9().getValue());
                vals[  9] = HNum.make(nw.getIn10().getValue());
                vals[ 10] = HNum.make(nw.getIn11().getValue());
                vals[ 11] = HNum.make(nw.getIn12().getValue());
                vals[ 12] = HNum.make(nw.getIn13().getValue());
                vals[ 13] = HNum.make(nw.getIn14().getValue());
                vals[ 14] = HNum.make(nw.getIn15().getValue());
                vals[ 15] = HNum.make(nw.getIn16().getValue());
                vals[ 16] = HNum.make(nw.getFallback().getValue());
            }
            else if (point instanceof BBooleanWritable)
            {
                BBooleanWritable bw = (BBooleanWritable) point;
                vals[  0] = HBool.make(bw.getIn1().getValue());
                vals[  1] = HBool.make(bw.getIn2().getValue());
                vals[  2] = HBool.make(bw.getIn3().getValue());
                vals[  3] = HBool.make(bw.getIn4().getValue());
                vals[  4] = HBool.make(bw.getIn5().getValue());
                vals[  5] = HBool.make(bw.getIn6().getValue());
                vals[  6] = HBool.make(bw.getIn7().getValue());
                vals[  7] = HBool.make(bw.getIn8().getValue());
                vals[  8] = HBool.make(bw.getIn9().getValue());
                vals[  9] = HBool.make(bw.getIn10().getValue());
                vals[ 10] = HBool.make(bw.getIn11().getValue());
                vals[ 11] = HBool.make(bw.getIn12().getValue());
                vals[ 12] = HBool.make(bw.getIn13().getValue());
                vals[ 13] = HBool.make(bw.getIn14().getValue());
                vals[ 14] = HBool.make(bw.getIn15().getValue());
                vals[ 15] = HBool.make(bw.getIn16().getValue());
                vals[ 16] = HBool.make(bw.getFallback().getValue());
            }
            else if (point instanceof BEnumWritable)
            {
                BEnumWritable ew = (BEnumWritable) point;
                vals[  0] = HStr.make(ew.getIn1().getValue().getTag());
                vals[  1] = HStr.make(ew.getIn2().getValue().getTag());
                vals[  2] = HStr.make(ew.getIn3().getValue().getTag());
                vals[  3] = HStr.make(ew.getIn4().getValue().getTag());
                vals[  4] = HStr.make(ew.getIn5().getValue().getTag());
                vals[  5] = HStr.make(ew.getIn6().getValue().getTag());
                vals[  6] = HStr.make(ew.getIn7().getValue().getTag());
                vals[  7] = HStr.make(ew.getIn8().getValue().getTag());
                vals[  8] = HStr.make(ew.getIn9().getValue().getTag());
                vals[  9] = HStr.make(ew.getIn10().getValue().getTag());
                vals[ 10] = HStr.make(ew.getIn11().getValue().getTag());
                vals[ 11] = HStr.make(ew.getIn12().getValue().getTag());
                vals[ 12] = HStr.make(ew.getIn13().getValue().getTag());
                vals[ 13] = HStr.make(ew.getIn14().getValue().getTag());
                vals[ 14] = HStr.make(ew.getIn15().getValue().getTag());
                vals[ 15] = HStr.make(ew.getIn16().getValue().getTag());
                vals[ 16] = HStr.make(ew.getFallback().getValue().getTag());
            }
            else throw new IllegalStateException();

            //level: number from 1 - 17 (17 is default)
            //levelDis: human description of level
            //val: current value at level or null
            //who: who last controlled the value at this level

            throw new IllegalStateException(); // TODO
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
        HNum dur) // 'dur' will always be null
    {
        try
        {
            BControlPoint point = (BControlPoint) lookupComponent(rec.id());

            if (point instanceof BNumericWritable)
            {
                HNum num = (HNum) val;
                BNumericWritable nw = (BNumericWritable) point;
                BStatusNumeric sn = nw.getLevel(BPriorityLevel.make(level));
                sn.setValue(num.val);
                sn.setStatus(BStatus.ok);

                saveLastWrite(point, level, val, who);
            }
            else if (point instanceof BBooleanWritable)
            {
                HBool bool = (HBool) val;
                BBooleanWritable bw = (BBooleanWritable) point;
                BStatusBoolean sb = bw.getLevel(BPriorityLevel.make(level));
                sb.setValue(bool.val);
                sb.setStatus(BStatus.ok);

                saveLastWrite(point, level, val, who);
            }
            else if (point instanceof BEnumWritable)
            {
                HStr str = (HStr) val;
                BEnumWritable ew = (BEnumWritable) point;
                BStatusEnum se = ew.getLevel(BPriorityLevel.make(level));

                BEnumRange range = (BEnumRange) point.getFacets().get(BFacets.RANGE);
                se.setValue(range.get(str.val));
                se.setStatus(BStatus.ok);

                saveLastWrite(point, level, val, who);
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
        // e.g. "site:/Blacksburg/Transmogrifier/SineWave1"

        if (!uri.val.startsWith("site:/")) return null;
        String str = uri.val.substring("site:/".length());
        if (str.endsWith("/")) str = str.substring(0, str.length() - 1);

        String[] navNames = TextUtil.split(str, '/');

        switch (navNames.length)
        {
            // site
            case 1:
                BComponent comp = cache.getSite(
                    SiteNavId.make(navNames[0]));
                return (comp == null) ?
                    null : 
                    configStorehouse.createComponentTags(comp);

            // equip
            case 2:
                comp = cache.getEquip(
                    EquipNavId.make(navNames[0], navNames[1]));
                return (comp == null) ?
                    null : 
                    configStorehouse.createComponentTags(comp);

            // point
            case 3:
                String siteNav  = navNames[0];
                String equipNav = navNames[1];
                String pointNav = navNames[2];

                // TODO make this more efficient
                BControlPoint[] points = cache.getEquipPoints(
                    EquipNavId.make(siteNav, equipNav));
                for (int i = 0; i < points.length; i++)
                {
                    BControlPoint point = points[i];
                    HDict tags = configStorehouse.createComponentTags(point);
                    if (tags.getStr("navName").equals(pointNav))
                        return tags;
                }
                return null;

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
            return historyStorehouse.createHistoryTags((BHistoryConfig) comp);
        else
            return configStorehouse.createComponentTags(comp);
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
            // this might be null
            BOrd ord = BOrd.make("station:|" + nh.getPath());
            BComponent comp = (BComponent) ord.get(service, null);
            if (comp == null) return null;
            return configStorehouse.isVisibleComponent(comp) ? comp : null;
        }
        // history space
        else if (nh.getSpace().equals(NHRef.HISTORY))
        {
            BHistoryId hid = BHistoryId.make(nh.getPath());

            BIHistory history = service.getHistoryDb().getHistory(hid);
            BHistoryConfig cfg = history.getConfig();
            return historyStorehouse.isVisibleHistory(cfg) ? cfg : null;
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
            return historyStorehouse.isVisibleHistory(cfg) ? 
                cfg : null;
        }
        // component space
        else if (comp instanceof BControlPoint)
        {
            return historyStorehouse.lookupHistoryFromPoint((BControlPoint) comp);
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
    private static void saveLastWrite(BControlPoint point, int level, HVal val, String who)
    {
        BHGrid oldGrid = (BHGrid) point.get(LAST_WRITE);

        if (oldGrid == null)
        {
            HGrid grid = saveLastWriteToGrid(HGrid.EMPTY, level, val, who);
            point.add(
                LAST_WRITE, 
                BHGrid.make(grid),
                Flags.SUMMARY | Flags.READONLY);
        }
        else
        {
            HGrid newGrid = saveLastWriteToGrid(oldGrid.getGrid(), level, val, who);
            point.set(
                LAST_WRITE, 
                BHGrid.make(newGrid));
        }
    }

    private static HGrid saveLastWriteToGrid(HGrid grid, int level, HVal val, String who)
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
        db.add("val", val);
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

//    void verifyLastWrite() throws Exception
//    {
//        HGrid grid = HGrid.EMPTY;
//
//        grid = NHServer.saveLastWriteToGrid(grid, 16, HNum.make(42.0), "admin");
//        String str = HZincWriter.gridToString(grid);
//        System.out.println(str);
//        verify(str.equals(
//            "ver:\"2.0\""     + "\n" +
//            "val,who,level"   + "\n" +
//            "42,\"admin\",16" + "\n"));
//
//        grid = NHServer.saveLastWriteToGrid(grid, 3, HNum.make(123.0), "foo");
//        str = HZincWriter.gridToString(grid);
//        System.out.println(str);
//        verify(str.equals(
//            "ver:\"2.0\""     + "\n" +
//            "val,who,level"   + "\n" +
//            "123,\"foo\",3"   + "\n" +
//            "42,\"admin\",16" + "\n"));
//
//        grid = NHServer.saveLastWriteToGrid(grid, 3, HNum.make(333.0), "admin");
//        str = HZincWriter.gridToString(grid);
//        System.out.println(str);
//        verify(str.equals(
//            "ver:\"2.0\""     + "\n" +
//            "val,who,level"   + "\n" +
//            "333,\"admin\",3" + "\n" +
//            "42,\"admin\",16" + "\n"));
//    }

////////////////////////////////////////////////////////////////
// access
////////////////////////////////////////////////////////////////

    public BNHaystackService getService() { return service; }

    public ConfigStorehouse  getConfigStorehouse()  { return configStorehouse;  }
    public HistoryStorehouse getHistoryStorehouse() { return historyStorehouse; }
    public SiteStorehouse    getSiteStorehouse()    { return siteStorehouse;    }

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
    private final ConfigStorehouse  configStorehouse;
    private final HistoryStorehouse historyStorehouse;
    private final SiteStorehouse    siteStorehouse;

    private final Cache cache = new Cache(this);
}

