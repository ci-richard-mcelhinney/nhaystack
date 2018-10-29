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
import java.util.logging.*;

import javax.baja.collection.*;
import javax.baja.control.*;
import javax.baja.control.enums.*;
import javax.baja.fox.*;
import javax.baja.history.*;
import javax.baja.history.db.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.schedule.*;
import javax.baja.security.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.timezone.*;
import javax.baja.nre.util.*;

import org.projecthaystack.*;
import org.projecthaystack.io.*;
import org.projecthaystack.server.*;
import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.driver.history.*;
import nhaystack.util.*;

/**
  * NHServer is responsible for serving up 
  * haystack-annotated BComponents.
  */
public class NHServer extends HServer
{
    public NHServer(BNHaystackService service)
    {
        this.service = service;
        this.spaceMgr = new SpaceManager(this);
        this.schedMgr = new ScheduleManager(this, service);
        this.cache = new Cache(this, schedMgr);
        this.tagMgr = new TagManager(this, service, spaceMgr, cache);
        this.nav = new Nav(service, spaceMgr, cache, tagMgr);
        this.foxSessionMgr = new FoxSessionManager();
        this.pointIO = new PointIO(service, cache, tagMgr, schedMgr, foxSessionMgr);
    }

////////////////////////////////////////////////////////////////
// HServer
////////////////////////////////////////////////////////////////

    /**
      * Return the operations supported by this database.
      */
    public HOp[] ops()
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        return OPS;
    }

    /**
      * Return the 'about' tags.
      */
    public HDict onAbout()
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("onAbout");

        try
        {
            HDictBuilder hd = new HDictBuilder();

            hd.add("serverName", Sys.getStation().getStationName());

            hd.add("productName",    "Niagara 4");
            hd.add("productVersion", BComponent.TYPE.getVendorVersion().toString());
            hd.add("productUri",     HUri.make("http://www.tridium.com/"));

            hd.add("moduleName",    BNHaystackService.TYPE.getModule().getModuleName());
            hd.add("moduleVersion", BNHaystackService.TYPE.getVendorVersion().toString());
            hd.add("moduleUri",     HUri.make("https://bitbucket.org/richiemac_77/nhaystack"));

            return hd.toDict();
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public HGrid onReadAll(String filter, int limit)
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        try
        {
            if (LOG.isLoggable(Level.FINE))
                LOG.fine("onReadAll begin filter:\"" + filter + "\", limit:" + limit);

            long ticks = Clock.ticks();
            HGrid grid = super.onReadAll(filter, limit);

            if (LOG.isLoggable(Level.FINE))
                LOG.fine("onReadAll end   filter:\"" + filter + "\", limit:" + limit + ", " + (Clock.ticks()-ticks) + "ms.");

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
    public Iterator iterator()
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        try
        {
            return new CompositeIterator(new Iterator[] { 
                spaceMgr.makeComponentSpaceIterator(),
                spaceMgr.makeHistorySpaceIterator() });
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
    public HDict onReadById(HRef id)
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("onReadById " + id);

        try
        {
            BComponent comp = tagMgr.lookupComponent(id);
            return (comp == null) ? null : tagMgr.createTags(comp);
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
    public HGrid onNav(String navId)
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("onNav " + navId);

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
    public HWatch onWatchOpen(String dis, HNum lease)
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG_WATCH.isLoggable(Level.FINE))
            LOG_WATCH.fine("onWatchOpen " + dis);

        try
        {
            NHWatch watch = new NHWatch(
                this, dis, lease.millis());

            synchronized(watches) 
            { 
                watches.put(watch.id(), watch); 
                service.setWatchCount(watches.size());
            }

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
    public HWatch[] onWatches()
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG_WATCH.isLoggable(Level.FINE))
            LOG_WATCH.fine("onWatches");

        try
        {
            return getWatches();
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
    public HWatch onWatch(String id)
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG_WATCH.isLoggable(Level.FINE))
            LOG_WATCH.fine("onWatch " + id);

        try
        {
            synchronized(watches) { return (HWatch) watches.get(id); }
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
    public HGrid onPointWriteArray(HDict rec)
    {
        return pointIO.onPointWriteArray(rec);
    }

    /**
      * Implementation hook for pointWrite
      */
    public void onPointWrite(
        HDict rec, int level, HVal val, String who, HNum dur, HDict opts)
    {
        pointIO.onPointWrite(rec, level, val, who, dur, opts);
    }

    /**
      * Read the history for the given BComponent.
      * The items wil be exclusive of start and inclusive of end time.
      */
    public HHisItem[] onHisRead(HDict rec, HDateTimeRange range)
    {
        if (!cache.initialized())
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("onHisRead " + rec.id() + ", " + range);

        try
        {
            BHistoryConfig cfg = tagMgr.lookupHistoryConfig(rec.id());
            if (cfg == null) return new HHisItem[0];

            HStr unit = (HStr) rec.get("unit", false);

            // ASSUMPTION: the tz in both ends of the range matches the 
            // tz of the historized point, which in turn matches the 
            // history's tz in its historyConfig.
            HTimeZone tz = range.start.tz;

            BAbsTime rangeStart = BAbsTime.make(range.start.millis(), cfg.getTimeZone());
            BAbsTime rangeEnd = BAbsTime.make(range.end.millis(), cfg.getTimeZone());
            LOG.fine("Start range: " + rangeStart.encodeToString());
            LOG.fine("End range:   " + rangeEnd.encodeToString());

            // NOTE: be careful, timeQuery() is inclusive of both start and end
            try (HistorySpaceConnection conn = service.getHistoryDb().getConnection(null))
            {
                BIHistory history = conn.getHistory(cfg.getId());

                BITable table = (BITable) conn.timeQuery(history, rangeStart, rangeEnd);

                // this will be null if its not a BTrendRecord
                boolean isTrendRecord = cfg.getRecordType().getResolvedType().is(BTrendRecord.TYPE);

                int recCounter = 0;
                Array arr = new Array(HHisItem.class);
                try (TableCursor cursor = table.cursor())
                {
                    // iterate over results and extract HHisItem's
                    while (cursor.next())
                    {
                        recCounter++;
                        BHistoryRecord hrec = (BHistoryRecord) cursor.get();
                        BAbsTime timestamp = (BAbsTime) hrec.get("timestamp");

                        // ignore inclusive start value
                        if (!timestamp.equals(rangeStart))
                        {
                            // create ts
                            HDateTime ts = HDateTime.make(timestamp.getMillis(), tz);

                            // create val
                            HVal val = null;
                            if (isTrendRecord)
                            {
                                // extract value from BTrendRecord
                                BValue value = hrec.get("value");

                                Type recType = cfg.getRecordType().getResolvedType();
                                if (recType.is(BNumericTrendRecord.TYPE))
                                {
                                    BNumber num = (BNumber) value;
                                    val = (unit == null) ?
                                            HNum.make(num.getDouble()) :
                                            HNum.make(num.getDouble(), unit.val);
                                } else if (recType.is(BBooleanTrendRecord.TYPE))
                                {
                                    BBoolean bool = (BBoolean) value;
                                    val = HBool.make(bool.getBoolean());
                                } else if (recType.is(BEnumTrendRecord.TYPE))
                                {
                                    BDynamicEnum dyn = (BDynamicEnum) value;
                                    BFacets facets = (BFacets) cfg.get("valueFacets");
                                    BEnumRange er = (BEnumRange) facets.get("range");
                                    val = HStr.make(SlotUtil.fromNiagara(er.getTag(dyn.getOrdinal())));
                                } else
                                {
                                    val = HStr.make(value.toString());
                                }
                            } else
                            {
                                // if its not a BTrendRecord, just do a toString()
                                // of the whole record
                                val = HStr.make(hrec.toString());
                            }

                            // add item
                            arr.add(HHisItem.make(ts, val));
                        }
                    }
                }

                HHisItem[] items = (HHisItem[]) arr.trim();
                LOG.fine("Found " + recCounter + " items...");
                if (items.length > 0)
                {
                    LOG.fine("Start range check, is the found item (" + items[0].ts.millis() + ") after the queried item (" + range.start.millis() + ")?");
                    if (range.start.millis() < items[0].ts.millis()) LOG.fine("Start range check passed!");
                    else LOG.fine("Start range check failed...");
                }
                // done
                return items;
            }
        } catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * Write the history for the given BComponent.
      */
    public void onHisWrite(HDict rec, HHisItem[] items)
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("onHisWrite " + rec.id());

        BHistoryConfig cfg = tagMgr.lookupHistoryConfig(rec.id());

        // check permissions on this Thread's saved context
        Context cx = ThreadContext.getContext(Thread.currentThread());
        if (!TypeUtil.canWrite(cfg, cx)) 
            throw new PermissionException("Cannot write to " + rec.id()); 

        try (HistorySpaceConnection conn = service.getHistoryDb().getConnection(null))
        {
            BIHistory history = conn.getHistory(cfg.getId());
            String kind = rec.getStr("kind");
            for (int i = 0; i < items.length; i++)
                conn.append(
                    history,
                    makeTrendRecord(
                        kind, items[i].ts, items[i].val));
        }
    }

    /**
      * Implementation hook for invokeAction
      */
    public HGrid onInvokeAction(HDict rec, String actionName, HDict args)
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("onInvokeAction " + rec.id() + ", " + actionName + ", " + args);

        try
        {
            BComponent comp = tagMgr.lookupComponent(rec.id());

            // check permissions on this Thread's saved context
            Context cx = ThreadContext.getContext(Thread.currentThread());
            if (!TypeUtil.canInvoke(comp, cx)) 
                throw new PermissionException("Cannot invoke on " + rec.id()); 

            Action[] actions = comp.getActionsArray();
            for (int i = 0; i < actions.length; i++)
            {
                if (actions[i].getName().equals(actionName))
                {
                    BValue result = comp.invoke(
                        actions[i], 
                        TypeUtil.actionArgsToBaja(args, comp, actions[i]));

                    if (result == null)
                    {
                        return HGrid.EMPTY;
                    }
                    else if (result instanceof BSimple)
                    {
                        HDictBuilder hd = new HDictBuilder();
                        hd.add("result", TypeUtil.fromBajaSimple(
                            (BSimple) result, service.getTranslateEnums()));
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
    public HDict onNavReadByUri(HUri uri)
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("onNavReadByUri " + uri);

        if (!uri.val.startsWith("sep:/")) return null;
        String str = uri.val.substring("sep:/".length());
        if (str.endsWith("/")) str = str.substring(0, str.length() - 1);
        String[] navNames = TextUtil.split(str, '/');

        NHRef ref = TagManager.makeSepRef(navNames);
        BComponent comp = cache.lookupComponentBySepRef(ref);
        return (comp == null) ?  null : tagMgr.createTags(comp);
    }

////////////////////////////////////////////////////////////////
// public -- overrideable
////////////////////////////////////////////////////////////////

    /**
      * Override this method to provide custom tags for a BComponent 
      */
    public HDict createCustomTags(BComponent comp)
    {
        return HDict.EMPTY;
    }

    /**
      * If you return any custom tags in createCustomTags(), then 
      * override this method to add those tags to the list of 
      * all possible auto-generated tags.
      */
    public String[] getAutoGeneratedTags()
    {
        return TagManager.AUTO_GEN_TAGS;
    }

////////////////////////////////////////////////////////////////
// package-scope
////////////////////////////////////////////////////////////////

    /**
      * Make an HTimeZone from a BTimeZone.
      * <p>
      * If the BTimeZone does not correspond to a standard HTimeZone,
      * then this method uses of the timeZoneAliases stored on the
      * BNHaystackService to attempt to perform a custom mapping.
      */
    final HTimeZone fromBajaTimeZone(BTimeZone timeZone)
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        String tzName = timeZone.getId();

        // lop off the region, e.g. "America" 
        int n = tzName.indexOf("/");
        if (n != -1) 
        {
            String region = tzName.substring(0, n);
            if (BHTimeZone.TZ_REGIONS.contains(region))
                tzName = tzName.substring(n+1);
        }

        try
        {
            return HTimeZone.make(tzName);
        }
        catch (Exception e)
        {
            // look through the aliases
            BTimeZoneAlias[] aliases = service.getTimeZoneAliases().getAliases();
            for (int i = 0; i < aliases.length; i++)
            {
                if (aliases[i].getAxTimeZoneId().equals(timeZone.getId()))
                    return aliases[i].getHaystackTimeZone().getTimeZone();
            }

            // cannot create timezone tag
            LOG.severe("Cannot create tz tag: " + e.getMessage());
            return null;
        }
    }

    void removeWatch(String watchId)
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        synchronized(watches) 
        { 
            watches.remove(watchId); 
            service.setWatchCount(watches.size());
        }
    }

    void removeBrokenRefs() 
    {
//        if (!cache.initialized()) 
//            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG.isLoggable(Level.FINE)) LOG.fine("BEGIN removeBrokenRefs"); 

        Iterator compItr = new ComponentTreeIterator(
            (BComponent) BOrd.make("slot:/").resolve(service, null).get());

        // check every component
        while (compItr.hasNext())
        {
            BComponent comp = (BComponent) compItr.next();
            HDict tags = BHDict.findTagAnnotation(comp);
            if (tags == null) continue;

            // check if any of the tags are a broken ref
            Set brokenRefs = null;
            Iterator tagItr = tags.iterator();
            while (tagItr.hasNext())
            {
                Map.Entry e = (Map.Entry) tagItr.next();
                String name = (String) e.getKey();
                HVal val = (HVal) e.getValue();

                if (val instanceof HRef)
                {
                    // try to resolve the ref
                    try
                    {
                        BComponent lookup = tagMgr.lookupComponent((HRef) val);
                        if (lookup == null)
                            throw new IllegalStateException("Cannot find component for " + val);
                    }
                    // failed!
                    catch (Exception e2)
                    {
                        LOG.warning(
                            "broken ref '" + name + "' found in " + 
                            comp.getSlotPath());

                        if (brokenRefs == null)
                            brokenRefs = new HashSet();
                        brokenRefs.add(name);
                    }
                }
            }

            // at least one broken ref was found
            if (brokenRefs != null)
            {
                HDictBuilder hdb = new HDictBuilder();
                tagItr = tags.iterator();
                while (tagItr.hasNext())
                {
                    Map.Entry e = (Map.Entry) tagItr.next();
                    String name = (String) e.getKey();
                    HVal val = (HVal) e.getValue();

                    if (!brokenRefs.contains(name))
                        hdb.add(name, val);
                }
                comp.set("haystack", BHDict.make(hdb.toDict()));
            }
        }

        if (LOG.isLoggable(Level.FINE)) LOG.fine("END removeBrokenRefs"); 
    }

////////////////////////////////////////////////////////////////
// watches
////////////////////////////////////////////////////////////////

    HWatch[] getWatches() 
    {
        if (!cache.initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        synchronized(watches) 
        {
            HWatch[] arr = new HWatch[watches.size()];
            int n = 0;
            Iterator itr = watches.values().iterator();
            while (itr.hasNext())
                arr[n++] = (HWatch) itr.next();
            return arr;
        }
    }

    HWatch getWatch(String watchId)
    {
        synchronized(watches) { return (HWatch) watches.get(watchId); }
    }

////////////////////////////////////////////////////////////////
// trend record
////////////////////////////////////////////////////////////////

    public static BTrendRecord makeTrendRecord(String kind, HDateTime ts, HVal val)
    {
        BAbsTime abs = BAbsTime.make(
            ts.millis(), 
            TypeUtil.toBajaTimeZone(ts.tz));

        if (kind.equals("Bool"))
        {
            BBooleanTrendRecord boolTrend = new BBooleanTrendRecord();
            boolTrend.set(abs, ((HBool) val).val, BStatus.ok);
            return boolTrend;
        }
        else if (kind.equals("Number"))
        {
            BNumericTrendRecord numTrend = new BNumericTrendRecord();
            numTrend.set(abs, ((HNum) val).val, BStatus.ok);
            return numTrend;
        }

        else throw new IllegalStateException("Cannot create trend record for kind " + kind);
    }

////////////////////////////////////////////////////////////////
// access
////////////////////////////////////////////////////////////////

    public BNHaystackService getService() { return service; }
    public TagManager getTagManager() { return tagMgr; }

    SpaceManager getSpaceManager() { return spaceMgr; }
    Cache getCache() { return cache; }
    Nav getNav() { return nav; }
    ScheduleManager getScheduleManager() { return schedMgr; }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack");
    private static final Logger LOG_WATCH = Logger.getLogger("nhaystack.watch");

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
        new NHServerOps.ExtendedReadOp(),
        new NHServerOps.ExtendedOp(),
        new AlarmAckOp()
    };

    private final HashMap watches = new HashMap();

    private final BNHaystackService service;
    private final SpaceManager spaceMgr;
    private final Cache cache;
    private final Nav nav;
    private final TagManager tagMgr;
    private final ScheduleManager schedMgr;
    private final FoxSessionManager foxSessionMgr;
    private final PointIO pointIO;
}

