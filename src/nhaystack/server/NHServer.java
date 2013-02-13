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
import javax.baja.history.*;
import javax.baja.log.*;
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
        HDictBuilder hd = new HDictBuilder();

        hd.add("serverName", Sys.getStation().getStationName());

        BModule module = service.getType().getModule();

        hd.add("productName",    module.getModuleName());
        hd.add("productVersion", module.getBajaVersion().toString());
        hd.add("productUri",     REPO);

        hd.add("moduleName",    module.getModuleName());
        hd.add("moduleVersion", module.getBajaVersion().toString());
        hd.add("moduleUri",     REPO);

        return hd.toDict();
    }

    protected HGrid onReadAll(String filter, int limit)
    {
        long ticks = Clock.ticks();
        HGrid grid = super.onReadAll(filter, limit);
        if (LOG.isTraceOn())
            LOG.trace("onReadAll " + (Clock.ticks()-ticks) + "ms.");
        return grid;
    }

    /**
      * Iterate every haystack-annotated entry in both the 
      * BComponentSpace and the BHistoryDatabase.
      */
    public Iterator iterator()
    {
        ConfigStorehouseIterator c = configStorehouse.makeIterator();
        HistoryStorehouseIterator h = historyStorehouse.makeIterator(c);

        return new CompositeIterator(new Iterator[] { c, h });
    }

    /**
      * Look up the HDict representation of a BComponent 
      * by its HRef id.
      *
      * Return null if the BComponent cannot be found,
      * or if it is not haystack-annotated.
      */
    protected HDict onReadById(HRef id)
    {
        BComponent comp = lookupComponent(id);
        return (comp == null) ? null : createTags(comp);
    }

    /**
      * Return navigation tree children for given navId.
      * The grid must define the "navId" column.
      */
    protected HGrid onNav(String navId)
    {
        String stationName = Sys.getStation().getStationName();

        // nav roots
        if (navId == null)
        {
            Array dicts = new Array(HDict.class);

            HDictBuilder hd = new HDictBuilder();
            hd.add("navId", HStr.make(stationName + ":" + NHRef.COMPONENT));
            hd.add("dis", "ComponentSpace");
            dicts.add(hd.toDict());

            hd = new HDictBuilder();
            hd.add("navId", HStr.make(stationName + ":" + NHRef.HISTORY));
            hd.add("dis", "HistorySpace");
            dicts.add(hd.toDict());

            hd = new HDictBuilder();
            hd.add("navId", HStr.make(SiteNavId.SITE));
            hd.add("dis", "Sites");
            dicts.add(hd.toDict());

            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }
        // config nav
        else if (navId.startsWith(stationName + ":" + NHRef.COMPONENT))
        {
            return configStorehouse.onNav(navId);
        }
        // history nav
        else if (navId.startsWith(stationName + ":" + NHRef.HISTORY))
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
            throw new BajaRuntimeException("Cannot lookup nav for " + navId);
        }
    }

    /**
      * Open a new watch.
      */
    protected HWatch onWatchOpen(String dis)
    {
        NHWatch watch = new NHWatch(
            this, 
            dis, 
            service.getLeaseInterval().getMillis());

        watches.put(watch.id(), watch);
        return watch;
    }

    /**
      * Return current watches
      */
    protected HWatch[] onWatches()
    {
        HWatch[] arr = new HWatch[watches.size()];
        int n = 0;
        Iterator itr = watches.values().iterator();
        while (itr.hasNext())
            arr[n++] = (HWatch) itr.next();
        return arr;
    }

    /**
      * Look up a watch by id
      */
    protected HWatch onWatch(String id)
    {
        return (HWatch) watches.get(id);
    }

    /**
      * Implementation hook for pointWriteArray
      */
    protected HGrid onPointWriteArray(HDict rec)
    {
        // TODO
        throw new UnsupportedOperationException();
    }
  
    /**
      * Implementation hook for pointWrite
      */
    protected void onPointWrite(HDict rec, int level, HVal val, String who, HNum dur)
    {
        // TODO
        throw new UnsupportedOperationException();
    }

    /**
      * Read the history for the given BComponent.
      * The items wil be exclusive of start and inclusive of end time.
      */
    protected HHisItem[] onHisRead(HDict rec, HDateTimeRange range)
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

    /**
      * Write the history for the given BComponent.
      * This is not currently supported.
      */
    protected void onHisWrite(HDict rec, HHisItem[] items)
    {
        throw new UnsupportedOperationException();
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

        // make sure station matches
        if (!nh.getStationName().equals(Sys.getStation().getStationName()))
            return null;

        // component space
        if (nh.getSpace().equals(NHRef.COMPONENT))
        {
            // this might be null
            BComponent comp = service.getComponentSpace().findByHandle(nh.getHandle());
            if (comp == null) return null;
            return configStorehouse.isVisibleComponent(comp) ? comp : null;
        }
        // history space
        else if (nh.getSpace().equals(NHRef.HISTORY))
        {
            BHistoryId hid = BHistoryId.make(nh.getHandle());

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

////////////////////////////////////////////////////////////////
// access
////////////////////////////////////////////////////////////////

    public BNHaystackService getService() { return service; }

    public ConfigStorehouse  getConfigStorehouse()  { return configStorehouse;  }
    public HistoryStorehouse getHistoryStorehouse() { return historyStorehouse; }
    public SiteStorehouse    getSiteStorehouse()    { return siteStorehouse;    }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private static final HUri REPO = HUri.make("https://bitbucket.org/jasondbriggs/nhaystack");

    protected static final HOp[] OPS = new HOp[]
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
    };

    private final HashMap watches = new HashMap();

    private final BNHaystackService service;
    private final ConfigStorehouse  configStorehouse;
    private final HistoryStorehouse historyStorehouse;
    private final SiteStorehouse    siteStorehouse;
}

