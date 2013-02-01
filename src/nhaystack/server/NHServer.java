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
import javax.baja.driver.history.*;
import javax.baja.history.*;
import javax.baja.history.ext.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.timezone.*;
import javax.baja.units.*;
import javax.baja.util.*;

import haystack.*;
import haystack.server.*;
import haystack.util.*;
import nhaystack.*;
import nhaystack.collection.*;

/**
  * NHServer is responsible for serving up 
  * haystack-annotated BComponents.
  */
public class NHServer extends HServer
{
    public NHServer(BNHaystackService service)
    {
        this.service = service;
        this.proxyPointMgr = new ProxyPointManager(service);
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

    /**
      * Iterate every haystack-annotated entry in both the BComponentSpace
      * and the BHistoryDatabase.
      */
    protected Iterator iterator()
    {
        return new CompositeIterator(new Iterator[] { 
           new NComponentSpaceIterator(),
           new NHistorySpaceIterator()
        });
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
        return (comp == null) ? null : makeDict(comp);
    }

    /**
      * Return navigation tree children for given navId.
      * The grid must define the "navId" column.
      */
    protected HGrid onNav(String navId)
    {
        // nav roots
        if (navId == null)
        {
            Array dicts = new Array(HDict.class);

            HDictBuilder hd = new HDictBuilder();
            hd.add("navId", HStr.make(Sys.getStation().getStationName() + ":c"));
            hd.add("dis", "ComponentSpace");
            dicts.add(hd.toDict());

            hd = new HDictBuilder();
            hd.add("navId", HStr.make(Sys.getStation().getStationName() + ":h"));
            hd.add("dis", "HistorySpace");
            dicts.add(hd.toDict());

            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }
        // child of ComponentSpace root
        else if (navId.equals(Sys.getStation().getStationName() + ":c"))
        {
            BComponent root = (BComponent) BOrd.make("slot:/").resolve(service, null).get();
            return HGridBuilder.dictsToGrid(new HDict[] { makeNavResult(root) });
        }
        // ComponentSpace component
        else if (navId.startsWith(Sys.getStation().getStationName() + ":c."))
        {
            NHRef nid = NHRef.make(HRef.make(navId));
            BComponent comp = service.getComponentSpace().findByHandle(nid.getHandle());
            BComponent kids[] = comp.getChildComponents();

            Array dicts = new Array(HDict.class);
            for (int i = 0; i < kids.length; i++)
                dicts.add(makeNavResult(kids[i]));
            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }
        // children of HistorySpace root
        else if (navId.equals(Sys.getStation().getStationName() + ":h"))
        {
            NHistorySpaceIterator itr = new NHistorySpaceIterator();
            Array dicts = new Array(HDict.class);
            while (itr.hasNext())
                dicts.add(itr.next());
            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }
        // error
        else
        {
            throw new RuntimeException("Cannot lookup nav for " + navId);
        }
    }

    private HDict makeNavResult(BComponent comp)
    {
        HDictBuilder hdb = new HDictBuilder();

        // add a navId, but only if this component is not a leaf
        if (comp.getChildComponents().length > 0)
            hdb.add("navId", NHRef.make(comp).getHRef().val);

        if (isVisibleComponent(comp))
        {
            hdb.add(makeDict(comp));
        }
        else
        {
            String dis = comp.getDisplayName(null);
            if (dis != null) hdb.add("dis", dis);
            hdb.add("axType", comp.getType().toString());
            hdb.add("axSlotPath", comp.getSlotPath().toString());
        }

        return hdb.toDict();
    }

    /**
      * Open a new watch.
      */
    protected HWatch onWatchOpen(String dis)
    {
        NHWatch watch = new NHWatch(this, dis);
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
        BHistoryConfig cfg = lookupHistory(rec.id());
        if (cfg == null) return new HHisItem[0];

        return readFromHistory(range, cfg, (HStr) rec.get("units", false));
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
      * Return the explicit annotation BHDict for the component, or null
      * if there are no tags.
      *
      * In order for the tags to be valid, they 
      * must be stored in a property called 'haystack'.
      */
    public BHDict findAnnotatedTags(BComponent comp)
    {
        BValue val = comp.get("haystack");
        if (val == null) return null;

        return (val instanceof BHDict) ? (BHDict) val : null;
    }

    /**
      * Create the haystack representation of a BComponent.
      *
      * The haystack representation is a combination of the 
      * autogenerated tags, and those tags specified
      * in the explicit haystack annotation (if any).
      *
      * This method never returns null.
      */
    public HDict makeDict(BComponent comp)
    {
        BHDict btags = findAnnotatedTags(comp);

        HDict tags = (btags == null) ? 
            HDict.EMPTY : btags.getDict();

        // add existing tags
        HDictBuilder hdb = new HDictBuilder();
        hdb.add(tags);

        // add id
        hdb.add("id", NHRef.make(comp).getHRef());
        
        // add space-specific tags
        if (comp instanceof BHistoryConfig)
            addHistoryTags((BHistoryConfig) comp, tags, hdb);
        else
            addComponentTags(comp, tags, hdb);

        // done
        return hdb.toDict();
    }

    /**
      * Look up the a BComponent by its HRef id.
      *
      * Return null if the BComponent cannot be found,
      * or if it is not haystack-annotated.
      */
    public BComponent lookupComponent(HRef id)
    {
        BComponent comp = doLookupById(id);

        // there are so many ways for the component to 
        // end up null that its easier to just trace 
        // them all here in one spot.
        if (comp == null)
            LOG.trace("lookupByHRef failed for id " + id);

        return comp;
    }

////////////////////////////////////////////////////////////////
// package
////////////////////////////////////////////////////////////////

    void removeWatch(String watchId)
    {
        watches.remove(watchId);
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private BHistoryConfig lookupHistory(HRef id)
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
            return isVisibleHistory(cfg) ? 
                cfg : null;
        }
        // component space
        else if (comp instanceof BControlPoint)
        {
            return lookupHistoryFromPoint((BControlPoint) comp);
        }
        else
        {
            LOG.error("cannot find history for for '" + id + "'");
            return null;
        }
    }

    /**
      * The items wil be exclusive of start and inclusive of end time.
      */
    private HHisItem[] readFromHistory(HDateTimeRange range, BHistoryConfig cfg, HStr units)
    {
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
                if (cfg.getRecordType().getResolvedType().is(BNumericTrendRecord.TYPE))
                {
                    BNumber num = (BNumber) value;
                    val = (units == null) ? 
                        HNum.make(num.getDouble()) :
                        HNum.make(num.getDouble(), units.val);
                }
                else if (cfg.getRecordType().getResolvedType().is(BBooleanTrendRecord.TYPE))
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
                // if its not a BTrendRecord, just do a toString() of the whole record
                val = HStr.make(table.get(i).toString());
            }

            // add item
            arr.add(HHisItem.make(ts, val));
        }

        // done
        return (HHisItem[]) arr.trim();
    }

    private void addComponentTags(BComponent comp, HDict tags, HDictBuilder hdb)
    {
        String dis = comp.getDisplayName(null);
        if (dis != null) hdb.add("dis", dis);
        hdb.add("axType", comp.getType().toString());
        hdb.add("axSlotPath", comp.getSlotPath().toString());

        // points get special treatment
        if (comp instanceof BControlPoint)
        {
            BControlPoint point = (BControlPoint) comp;

            // ensure there is a point marker tag
            hdb.add("point");

            // check if this point has a history
            BHistoryConfig cfg = lookupHistoryFromPoint(point);
            if (cfg != null)
            {
                hdb.add("his");

                if (service.getShowLinkedHistories())
                    hdb.add("axHistoryRef", NHRef.make(cfg).getHRef());

                // tz
                if (!tags.has("tz"))
                {
                    HTimeZone tz = makeTimeZone(cfg.getTimeZone());
                    hdb.add("tz", tz.name);
                }

                // hisInterpolate 
                if (!tags.has("hisInterpolate"))
                {
                    BHistoryExt historyExt = lookupHistoryExt(point);
                    if (historyExt != null && (historyExt instanceof BCovHistoryExt))
                        hdb.add("hisInterpolate", "cov");
                }
            }

            // point kind tags
            int pointKind = getControlPointKind(point);
            BFacets facets = (BFacets) point.get("facets");
            addPointKindTags(pointKind, facets, tags, hdb);

            // curVal, curStatus
            switch(pointKind)
            {
                case NUMERIC_KIND:
                    BNumericPoint np = (BNumericPoint) point;
                    hdb.add("curVal",    HNum.make(np.getNumeric()));
                    hdb.add("curStatus", makeStatusString(point.getStatus()));
                    break;

                case BOOLEAN_KIND:
                    BBooleanPoint bp = (BBooleanPoint) point;
                    hdb.add("curVal",    HBool.make(bp.getBoolean()));
                    hdb.add("curStatus", makeStatusString(point.getStatus()));
                    break;

                case ENUM_KIND:
                    BEnumPoint ep = (BEnumPoint) point;
                    hdb.add("curVal",    HStr.make(ep.getEnum().toString()));
                    hdb.add("curStatus", makeStatusString(point.getStatus()));
                    break;

                case STRING_KIND:
                    BStringPoint sp = (BStringPoint) point;
                    hdb.add("curVal",    HStr.make(sp.getOut().getValue().toString()));
                    hdb.add("curStatus", makeStatusString(point.getStatus()));
                    break;
            }
        }
    }

    private void addHistoryTags(
        BHistoryConfig cfg, 
        HDict tags, 
        HDictBuilder hdb)
    {
        String dis = cfg.getDisplayName(null);
        if (dis != null) hdb.add("dis", dis);
        hdb.add("axType", cfg.getType().toString());
        hdb.add("axHistoryId", cfg.getId().toString());

        hdb.add("point");
        hdb.add("his");

        // time zone
        if (!tags.has("tz"))
        {
            HTimeZone tz = makeTimeZone(cfg.getTimeZone());
            hdb.add("tz", tz.name);
        }

        // point kind tags
        Type recType = cfg.getRecordType().getResolvedType();
        if (recType.is(BTrendRecord.TYPE))
        {
            int pointKind = getTrendRecordKind(recType);
            BFacets facets = (BFacets) cfg.get("valueFacets");
            addPointKindTags(pointKind, facets, tags, hdb);
        }

        // if this history is connected to a point...
        BControlPoint point = lookupPointFromHistory(cfg);
        if (point != null)
        {
            // add point ref
            hdb.add("axPointRef", NHRef.make(point).getHRef());

            // hisInterpolate 
            if (!tags.has("hisInterpolate"))
            {
                BHistoryExt historyExt = lookupHistoryExt(point);
                if (historyExt != null && (historyExt instanceof BCovHistoryExt))
                    hdb.add("hisInterpolate", "cov");
            }
        }
    }

    /**
      * add the 'kind' tag, along with an associated tags 
      * like 'enum' or 'units'
      */
    private void addPointKindTags(
        int pointKind, 
        BFacets facets, 
        HDict tags, 
        HDictBuilder hdb)
    {
        switch(pointKind)
        {
            case NUMERIC_KIND:

                if (!tags.has("kind")) hdb.add("kind", "Number");

                if (!tags.has("units"))
                {
                    BUnit units = findUnits(facets);
                    if (units != null) 
                        hdb.add("units", units.toString());
                }

                break;

            case BOOLEAN_KIND:

                if (!tags.has("kind")) hdb.add("kind", "Bool");
                if (!tags.has("enum")) hdb.add("enum", findTrueFalse(facets));
                break;

            case ENUM_KIND:

                if (!tags.has("kind")) hdb.add("kind", "Str");
                if (!tags.has("enum")) hdb.add("enum", findRange(facets));
                break;

            case STRING_KIND:

                if (!tags.has("kind")) hdb.add("kind", "Str");
                break;
        }
    }

    private BComponent doLookupById(HRef id)
    {
        NHRef nid = NHRef.make(id);

        // make sure station matches
        if (!nid.getStationName().equals(Sys.getStation().getStationName()))
            return null;

        // component space
        if (nid.isComponentSpace())
        {
            // this might be null
            BComponent comp = service.getComponentSpace().findByHandle(nid.getHandle());
            if (comp == null) return null;
            return isVisibleComponent(comp) ? comp : null;
        }
        // history space
        else if (nid.isHistorySpace())
        {
            BHistoryId hid = BHistoryId.make(nid.getHandle());

            BIHistory history = service.getHistoryDb().getHistory(hid);
            BHistoryConfig cfg = history.getConfig();
            return isVisibleHistory(cfg) ? cfg : null;
        }
        // invalid space
        else 
        {
            return null;
        }
    }

    /**
      * Return whether the given component-space component
      * ought to be turned into a record.
      */
    private boolean isVisibleComponent(BComponent comp)
    {
        // Return true for components that have been 
        // annotated with a BHDict instance.
        if (findAnnotatedTags(comp) != null)
            return true;

        // Return true for BControlPoints.
        if (comp instanceof BControlPoint)
            return true;

        // nope
        return false;
    }

    /**
      * Return whether this history is visible to the outside world.
      */
    private boolean isVisibleHistory(BHistoryConfig cfg)
    {
        // annotated 
        if (findAnnotatedTags(cfg) != null)
            return true;

        // show linked
        if (service.getShowLinkedHistories())
            return true;

        // make sure not linked
        if (lookupPointFromHistory(cfg) == null)
            return true;

        return false;
    }

    /**
      * Try to find either a local or imported history for the point
      */
    private BHistoryConfig lookupHistoryFromPoint(BControlPoint point)
    {
        // local history
        BHistoryExt historyExt = lookupHistoryExt(point);
        if (historyExt != null) return historyExt.getHistoryConfig();

        // look for history that goes with a proxied point (if any)
        if (point.getProxyExt().getType().is(ProxyPointManager.NIAGARA_PROXY_EXT)) 
            return proxyPointMgr.lookupImportedHistory(point);
        else 
            return null;
    }

    /**
      * Try to find the point that goes with a history,
      * or return null.
      */
    private BControlPoint lookupPointFromHistory(BHistoryConfig cfg)
    {
        // local history
        if (cfg.getId().getDeviceName().equals(Sys.getStation().getStationName()))
        {
            BOrd[] ords = cfg.getSource().toArray();
            if (ords.length != 1) new IllegalStateException(
                "invalid Source: " + cfg.getSource());

            BComponent source = (BComponent) ords[0].resolve(service, null).get();

            // The source is not always a BHistoryExt.  E.g. for 
            // LogHistory its the LogHistoryService.
            if (source instanceof BHistoryExt)
            {
                if (source.getParent() instanceof BControlPoint)
                    return (BControlPoint) source.getParent();
            }

            return null;
        }
        // look for imported point that goes with history (if any)
        else
        {
            // TODO: very inefficient!
            return proxyPointMgr.lookupImportedPoint(cfg);
        }
    }

    /**
      * Check if there is a history extension.  This will succeed
      * if the point lives in this station, rather than being proxied.
      */
    private BHistoryExt lookupHistoryExt(BControlPoint point)
    {
        Cursor cursor = point.getProperties();
        if (cursor.next(BHistoryExt.class))
        {
            BHistoryExt ext = (BHistoryExt) cursor.get();

            // Return null if the extension has never been enabled.
            BHistoryConfig config = ext.getHistoryConfig();
            if (service.getHistoryDb().getHistory(config.getId()) == null)
                return null;

            return ext;
        }

        return null;
    }

    private static BUnit findUnits(BFacets facets)
    {
        if (facets == null) 
            return null;

        BUnit units = (BUnit)facets.get("units");
        if ((units == null) || (units.isNull()))
            return null;

        int conv = facets.geti("unitConversion", 0);
        if (conv != 0)
            units = BUnitConversion.make(conv).getDesiredUnit(units);

        return units;
    }

    private String findTrueFalse(BFacets facets)
    {
        if (facets == null) 
            return "false,true";

        return 
            facets.gets("falseText", "false") + "," +
            facets.gets("trueText", "true");
    }

    private static String findRange(BFacets facets)
    {
        if (facets == null) 
            return "";

        BEnumRange range = (BEnumRange) facets.get("range");
        if ((range == null) || (range.isNull()))
            return "";

        StringBuffer sb = new StringBuffer();
        int[] ord = range.getOrdinals();
        for (int i = 0; i < ord.length; i++)
        {
            if (i > 0) sb.append(",");
            sb.append(range.get(i).getTag());
        }
        return sb.toString();
    }

    private static String makeStatusString(BStatus status)
    {
        if (status.isOk())
            return "ok";

        // TODO define proper ordering for these -- look at oBIX
        if (status.isDisabled())     return "disabled";
        if (status.isFault())        return "fault";
        if (status.isDown())         return "down";
        if (status.isAlarm())        return "alarm";
        if (status.isStale())        return "stale";
        if (status.isOverridden())   return "overridden";
        if (status.isNull())         return "null";
        if (status.isUnackedAlarm()) return "unackedAlarm";

        throw new IllegalStateException();
    }

    private static int getControlPointKind(BControlPoint point)
    {
        if      (point instanceof BNumericPoint) return NUMERIC_KIND;
        else if (point instanceof BBooleanPoint) return BOOLEAN_KIND;
        else if (point instanceof BEnumPoint)    return ENUM_KIND;
        else if (point instanceof BStringPoint)  return STRING_KIND;

        else return UNKNOWN_KIND;
    }

    private static int getTrendRecordKind(Type trendRecType)
    {
        if      (trendRecType.is(BNumericTrendRecord.TYPE)) return NUMERIC_KIND;
        else if (trendRecType.is(BBooleanTrendRecord.TYPE)) return BOOLEAN_KIND;
        else if (trendRecType.is(BEnumTrendRecord.TYPE))    return ENUM_KIND;
        else if (trendRecType.is(BStringTrendRecord.TYPE))  return STRING_KIND;

        else return UNKNOWN_KIND;
    }

    private static HTimeZone makeTimeZone(BTimeZone timeZone)
    {
        String tzName = timeZone.getId();

        // lop off the continent, e.g. "America" 
        int n = tzName.indexOf("/");
        if (n != -1) tzName = tzName.substring(n+1);

        return HTimeZone.make(tzName, false);
    }

////////////////////////////////////////////////////////////////
// Iterators
////////////////////////////////////////////////////////////////

    /**
      * Iterator for component space
      */
    private class NComponentSpaceIterator implements Iterator
    {
        private NComponentSpaceIterator()
        {
            iterator = new ComponentTreeIterator(
                (BComponent) BOrd.make("slot:/").resolve(service, null).get());

            findNext();
        }

        public boolean hasNext() { return nextDict != null; }

        public void remove() { throw new UnsupportedOperationException(); }

        public Object next()
        {
            if (nextDict == null) throw new IllegalStateException();

            HDict dict = nextDict;
            findNext();
            return dict;
        }

        private void findNext()
        {
            nextDict = null;
            while (iterator.hasNext())
            {
                BComponent comp = (BComponent) iterator.next();

                if (isVisibleComponent(comp))
                {
                    nextDict = makeDict(comp);
                    break;
                }
            }
        }

        private final ComponentTreeIterator iterator;
        private HDict nextDict;
    }

    /**
      * Iterator for history space
      */
    private class NHistorySpaceIterator implements Iterator
    {
        private NHistorySpaceIterator()
        {
            iterator = new HistoryDbIterator(service.getHistoryDb());

            findNext();
        }

        public boolean hasNext() { return nextDict != null; }

        public void remove() { throw new UnsupportedOperationException(); }

        public Object next()
        {
            if (nextDict == null) throw new IllegalStateException();

            HDict dict = nextDict;
            findNext();
            return dict;
        }

        private void findNext()
        {
            nextDict = null;
            while (iterator.hasNext())
            {
                BHistoryConfig cfg = (BHistoryConfig) iterator.next();

                if (isVisibleHistory(cfg))
                {
                    nextDict = makeDict(cfg);
                    break;
                }
            }
        }

        private final HistoryDbIterator iterator;
        private HDict nextDict;
    }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private static final HUri REPO = 
        HUri.make("https://bitbucket.org/jasondbriggs/nhaystack");

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

    // point kinds
    private static final int UNKNOWN_KIND = -1;
    private static final int NUMERIC_KIND =  0;
    private static final int BOOLEAN_KIND =  1;
    private static final int ENUM_KIND    =  2;
    private static final int STRING_KIND  =  3;

    private final ProxyPointManager proxyPointMgr;
    private final HashMap watches = new HashMap();

    final BNHaystackService service;
}

