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
        this.historyMgr = new ImportedHistoryManager(service);
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

////////////////////////////////////////////////////////////////
// Reads
////////////////////////////////////////////////////////////////

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
      * Iterate every haystack-annotated entry in both the BComponentSpace
      * and the BHistoryDatabase.
      */
    protected Iterator iterator()
    {
        Iterator c = new ComponentTreeIterator(
            (BComponent) BOrd.make("slot:/").resolve(service, null).get());

        Iterator h = new HistoryDbIterator(service.getHistoryDb());

        return new NHServerIterator(
            this, new CompositeIterator(new Iterator[] { c, h }));
    }

////////////////////////////////////////////////////////////////
// Navigation
////////////////////////////////////////////////////////////////

  /**
   * Return navigation children for given navId.
   */
  public HGrid nav(String navId)
  {
    return onNav(navId);
  }

  /**
   * Return navigation tree children for given navId.
   * The grid must define the "navId" column.
   */
  protected HGrid onNav(String navId)
  {
      // TODO
      throw new UnsupportedOperationException();
  }

////////////////////////////////////////////////////////////////
// Watches
////////////////////////////////////////////////////////////////

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

////////////////////////////////////////////////////////////////
// Point Writes
////////////////////////////////////////////////////////////////

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

////////////////////////////////////////////////////////////////
// History
////////////////////////////////////////////////////////////////

    /**
      * Read the history for the given BComponent.
      * The items wil be exclusive of start and inclusive of end time.
      */
    protected HHisItem[] onHisRead(HDict rec, HDateTimeRange range)
    {
        BIHistory history = lookupHistory(rec.id());
        if (history == null) return new HHisItem[0];

        return readFromHistory(history, range);
    }

    /**
      * Write the history for the given BComponent
      */
    protected void onHisWrite(HDict rec, HHisItem[] items)
    {
//        BIHistory history = lookupHistory(rec.id());
//        if (history == null) return;

        // TODO
        throw new UnsupportedOperationException();
    }

//////////////////////////////////////////////////////////////// 
// public API 
////////////////////////////////////////////////////////////////

    /**
      * Create the haystack representation of an annotated BComponent,
      * or return null if the component is not annotated.
      *
      * The HDict that is returned is a combination of the annotation
      * tags, and an 'id' HRef tag that is created via NHref.make().
      */
    public HDict makeDict(BComponent comp)
    {
        BTags btags = findTags(comp);
        if (btags == null) 
            return null;

        HDict tags = btags.getDict();
        HDictBuilder hdb = new HDictBuilder();

        // add existing tags
        hdb.add(tags);

        // add id
        hdb.add("id", NHRef.make(comp).ref);
        
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
// private
////////////////////////////////////////////////////////////////

    private BIHistory lookupHistory(HRef id)
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
            return service.getHistoryDb().getHistory(cfg.getId());
        }
        // component space
        else if (comp instanceof BControlPoint)
        {
            BControlPoint point = (BControlPoint) comp;
            BHistoryConfig cfg = lookupHistoryConfig(point);
            return service.getHistoryDb().getHistory(cfg.getId());
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
    private HHisItem[] readFromHistory(BIHistory history, HDateTimeRange range)
    {
        // ASSUMPTION: the tz in both ends of the range matches the 
        // tz of the historized point, which in turn matches the 
        // history's tz in its historyConfig.
        HTimeZone tz = range.start.tz;

        BHistoryConfig cfg = history.getConfig();
        BAbsTime rangeStart = BAbsTime.make(range.start.millis(), cfg.getTimeZone());
        BAbsTime rangeEnd   = BAbsTime.make(range.end.millis(),   cfg.getTimeZone());

        // NOTE: be careful, timeQuery() is inclusive of both start and end
        BITable table = (BITable) history.timeQuery(rangeStart, rangeEnd);
        ColumnList columns = table.getColumns();
        Column timestampCol = columns.get("timestamp");
        Column valueCol = columns.get("value");

        Array arr = new Array(HHisItem.class, table.size());
        for (int i = 0; i < table.size(); i++)
        {
            BAbsTime timestamp = (BAbsTime) table.get(i, timestampCol);
            BValue   value     = (BValue)   table.get(i, valueCol);

            // ignore inclusive start value
            if (timestamp.equals(rangeStart)) continue;

            // create ts
            HDateTime ts = HDateTime.make(timestamp.getMillis(), tz);

            // create val
            HVal val = null;
            if (cfg.getRecordType().getResolvedType().is(BNumericTrendRecord.TYPE))
            {
                BNumber num = (BNumber) value;
                val = HNum.make(num.getDouble());
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

            // add item
            arr.add(HHisItem.make(ts, val));
        }

        // done
        return (HHisItem[]) arr.trim();
    }

    /**
      * Return the annotation BTags for the component, or null
      * if there are no tags.
      *
      * In order for the tags to be valid, they 
      * must be stored in a property called 'haystack'.
      *
      * TODO explain BHistoryImport.configOverrides
      */
    private BTags findTags(BComponent comp)
    {
        // ignore tags on BHistoryImport.configOverrides -- they 
        // will show up automatically in the history space
        BComplex parent = comp.getParent();
        if ((parent != null) && (parent instanceof BHistoryImport))
        {
            Property prop = comp.getPropertyInParent();
            if (prop.getName().equals("configOverrides"))
                return null;
        }

        BValue val = comp.get("haystack");
        if (val == null) return null;

        return (val instanceof BTags) ? (BTags) val : null;
    }

    private void addComponentTags(BComponent comp, HDict tags, HDictBuilder hdb)
    {
        // add ax-specific tags
        hdb.add("axSlotPath", comp.getSlotPath().toString());

        // points get special treatment
        if (tags.has("point") && (comp instanceof BControlPoint))
        {
            BControlPoint point = (BControlPoint) comp;
            int pointKind = getControlPointKind(point);
            BFacets facets = (BFacets) point.get("facets");

            // time zone
            if (!tags.has("tz"))
            {
                BHistoryConfig cfg = lookupHistoryConfig(point);
                if (cfg != null)
                {
                    HTimeZone tz = makeTimeZone(cfg.getTimeZone());
                    hdb.add("tz", tz.name);

                    // add ax-specific tags
                    hdb.add("axHistoryId", cfg.getId().toString());

                    hdb.add("axHistorySource", 
                        TextUtil.replace(cfg.getSource().toString(), "\n", "\\n"));

                    // add hisInterpolate 
                    if (!tags.has("hisInterpolate"))
                    {
                        BHistoryExt historyExt = lookupHistoryExt(point);
                        if (historyExt != null && (historyExt instanceof BCovHistoryExt))
                            hdb.add("hisInterpolate", "cov");
                    }
                }
            }

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
        // add ax-specific tags
        hdb.add("axHistoryId", cfg.getId().toString());

        hdb.add("axHistorySource", 
            TextUtil.replace(cfg.getSource().toString(), "\n", "\\n"));

        // points get special treatment
        if (tags.has("point"))
        {
            // time zone
            if (!tags.has("tz"))
            {
                HTimeZone tz = makeTimeZone(cfg.getTimeZone());
                hdb.add("tz", tz.name);
            }

            Type recType = cfg.getRecordType().getResolvedType();
            if (recType.is(BTrendRecord.TYPE))
            {
                int pointKind = getTrendRecordKind(recType);
                BFacets facets = (BFacets) cfg.get("valueFacets");

                addPointKindTags(pointKind, facets, tags, hdb);
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
        if (!nid.stationName.equals(Sys.getStation().getStationName()))
            return null;

        // component space
        if (nid.space.equals("c"))
        {
            BComponent comp = service.getComponentSpace().findByHandle(nid.handle);
            if (comp == null) return null; // not found
            if (findTags(comp) == null) return null; // must be annotated
            return comp;
        }
        // history space
        else if (nid.space.equals("h"))
        {
            BHistoryId hid = BHistoryId.make(Base64.URI.decodeUTF8(nid.handle));

            BIHistory history = service.getHistoryDb().getHistory(hid);
            BHistoryConfig cfg = history.getConfig();
            if (findTags(cfg) == null) return null; // must be annotated
            return cfg;
        }
        // invalid space
        else 
        {
            return null;
        }
    }

    /**
      * Try to find either a local or imported history for the point
      */
    private BHistoryConfig lookupHistoryConfig(BControlPoint point)
    {
        // look for local history
        BHistoryExt historyExt = lookupHistoryExt(point);
        if (historyExt != null) return historyExt.getHistoryConfig();

        // look for history that goes with a proxied point
        return historyMgr.lookupImportedHistory(point);
    }

    /**
      * Check if there is a history extension.  This will succeed
      * if the point lives in this station, rather than being proxied.
      */
    private static BHistoryExt lookupHistoryExt(BControlPoint point)
    {
        Cursor cursor = point.getProperties();
        if (cursor.next(BHistoryExt.class))
            return (BHistoryExt) cursor.get();

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

//        StringBuffer sb = new StringBuffer();
//        if (status.isDisabled())     { if (sb.length() > 0) sb.append(","); sb.append("disabled");     }
//        if (status.isFault())        { if (sb.length() > 0) sb.append(","); sb.append("fault");        }
//        if (status.isDown())         { if (sb.length() > 0) sb.append(","); sb.append("down");         }
//        if (status.isAlarm())        { if (sb.length() > 0) sb.append(","); sb.append("alarm");        }
//        if (status.isStale())        { if (sb.length() > 0) sb.append(","); sb.append("stale");        }
//        if (status.isOverridden())   { if (sb.length() > 0) sb.append(","); sb.append("overridden");   }
//        if (status.isNull())         { if (sb.length() > 0) sb.append(","); sb.append("null");         }
//        if (status.isUnackedAlarm()) { if (sb.length() > 0) sb.append(","); sb.append("unackedAlarm"); }
//        return sb.toString();
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
        HStdOps.watchSub,
        HStdOps.watchUnsub,
        HStdOps.watchPoll,
        HStdOps.hisRead,
        HStdOps.hisWrite
    };

    // point kinds
    private static final int UNKNOWN_KIND = -1;
    private static final int NUMERIC_KIND =  0;
    private static final int BOOLEAN_KIND =  1;
    private static final int ENUM_KIND    =  2;
    private static final int STRING_KIND  =  3;

    final BNHaystackService service;
    final ImportedHistoryManager historyMgr;

    final HashMap watches = new HashMap();
}

