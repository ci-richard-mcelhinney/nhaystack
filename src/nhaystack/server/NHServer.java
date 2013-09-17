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
import org.projecthaystack.server.*;
import org.projecthaystack.util.*;
import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.server.storehouse.*;
import nhaystack.site.*;
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

        try
        {
            BComponent comp = lookupComponent(id);
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
            LOG.trace("onHisRead " + rec.id() + ", " + range);

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
            LOG.trace("onInvokeAction " + rec.id() + ", " + actionName + ", " + args);

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

        BComponent comp = lookupComponentByUri(uri);

        return (comp == null) ?  null : 
            compStore.createComponentTags(comp);
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
        return AUTO_GEN_TAGS;
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
        return (comp instanceof BHistoryConfig) ?
            hisStore.createHistoryTags((BHistoryConfig) comp) :
            compStore.createComponentTags(comp);
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
        if (nh.getSpace().equals(NHRef.COMP) ||
            nh.getSpace().equals(NHRef.COMP_BASE64))
        {
            BOrd ord = BOrd.make("station:|" + 
                (nh.getSpace().equals(NHRef.COMP) ? 
                    "slot:" + PathUtil.toNiagaraPath(nh.getPath()) : 
                    Base64.URI.decodeUTF8(nh.getPath())));

            BComponent comp = (BComponent) ord.get(service, null);
            if (comp == null) return null;
            return compStore.isVisibleComponent(comp) ? comp : null;
        }
        // history space
        else if (
            nh.getSpace().equals(NHRef.HIS_BASE64) ||
            nh.getSpace().equals(NHRef.HIS))
        {
            BHistoryId hid = BHistoryId.make(
                nh.getSpace().equals(NHRef.HIS) ? 
                    "/" + PathUtil.toNiagaraPath(nh.getPath()) :
                    Base64.URI.decodeUTF8(nh.getPath()));

            BIHistory history = service.getHistoryDb().getHistory(hid);
            BHistoryConfig cfg = history.getConfig();
            return hisStore.isVisibleHistory(cfg) ? cfg : null;
        }
        // sep space
        else if (nh.getSpace().equals(NHRef.SEP))
        {
            return lookupComponentByUri(HUri.make(
                "sep:/" + TextUtil.replace(nh.getPath(), ".", "/")));
        }
        // invalid space
        else 
        {
            return null;
        }
    }

    /**
      * Look up a component by its URI, or return null.
      */
    public BComponent lookupComponentByUri(HUri uri)
    {
        if (!uri.val.startsWith("sep:/")) return null;
        String str = uri.val.substring("sep:/".length());
        if (str.endsWith("/")) str = str.substring(0, str.length() - 1);

        String[] navNames = TextUtil.split(str, '/');
//        for (int i = 0; i < navNames.length; i++)
//            navNames[i] = com.tridium.util.EscUtil.slot.unescape(navNames[i]);

        switch (navNames.length)
        {
            case 1: return cache.getNavSite  (Nav.makeSiteNavId  (navNames[0]));
            case 2: return cache.getNavEquip (Nav.makeEquipNavId (navNames[0], navNames[1]));
            case 3: return cache.getNavPoint (Nav.makeEquipNavId (navNames[0], navNames[1]), navNames[2]);

            // bad uri
            default: return null;
        }
    }

    /**
      * Make an ID from a BComponent.  
      */
    public NHRef makeComponentRef(BComponent comp)
    {
        // history space
        if (comp instanceof BHistoryConfig)
        {
            BHistoryConfig cfg = (BHistoryConfig) comp;
            return makeHistoryRef(cfg);
        }
        // component space
        else
        {
            NHRef sepRef = makeComponentSepRef(comp);
            if (sepRef != null) return sepRef;

            return makeSlotPathRef(comp);
        }
    }

    public static NHRef makeSlotPathRef(BComponent comp)
    {
        String path = comp.getSlotPath().toString();
        path = removePrefix(path, "slot:/");
        path = PathUtil.fromNiagaraPath(path);

        return NHRef.make(NHRef.COMP, path);
    }

    public static NHRef makeHistoryRef(BHistoryConfig cfg)
    {
        String path = cfg.getId().toString();
        path = removePrefix(path, "/");
        path = PathUtil.fromNiagaraPath(path);

        return NHRef.make(NHRef.HIS, path);
    }

    private static String removePrefix(String path, String prefix)
    {
        if (!path.startsWith(prefix))
            throw new IllegalStateException(
                "'" + path + "' does not start with '" + prefix + "'");

        return path.substring(prefix.length());
    }

    public static NHRef makeSepRef(String[] navPath)
    {
        return NHRef.make(
            NHRef.SEP, 
            PathUtil.fromNiagaraPath(
                TextUtil.join(navPath, '/')));
    }

    /**
      * Make a SEP-style ref
      */
    private NHRef makeComponentSepRef(BComponent comp)
    {
        if (comp instanceof BHSite)
        {
            BHSite site = (BHSite) comp;
            HDict siteTags = site.getHaystack().getDict();

            String navName = Nav.makeNavName(site, siteTags);

            return makeSepRef(new String[] { navName });
        }
        else if (comp instanceof BHEquip) 
        {
            String[] equipNavPath = makeEquipNavPath((BHEquip) comp);
            if (equipNavPath != null) 
                return makeSepRef(equipNavPath);
        }
        else if (comp instanceof BControlPoint) 
        {
            BControlPoint point = (BControlPoint) comp;
            HDict pointTags = BHDict.findTagAnnotation(point);

            BHEquip equip = ((pointTags != null) && pointTags.has("equipRef")) ?
                (BHEquip) lookupComponent(pointTags.getRef("equipRef")) :
                cache.getImplicitEquip(point);

            if (equip != null)
            {
                String[] equipNavPath = makeEquipNavPath(equip);
                if (equipNavPath != null)
                {
                    // this can happen on an unannotated point with an implicit equip
                    if (pointTags == null) pointTags = HDict.EMPTY;

                    String navName = Nav.makeNavName(point, pointTags);
                    return makeSepRef(
                        new String[] {
                            equipNavPath[0], 
                            equipNavPath[1], 
                            navName });
                }
            }
        }
        return null;
    }

    /**
      * Make a SEP-style ref for an equip
      */
    private String[] makeEquipNavPath(BHEquip equip)
    {
        HDict equipTags = equip.getHaystack().getDict();

        if ((equipTags != null) && equipTags.has("siteRef"))
        {
            BHSite site = (BHSite) lookupComponent((HRef) equipTags.get("siteRef"));
            if (site != null)
            {
                HDict siteTags = BHDict.findTagAnnotation(site);
                String siteNav = Nav.makeNavName(site, siteTags);
                String navName = Nav.makeNavName(equip, equipTags);
                return new String[] { siteNav, navName };
            }
        }

        return null;
    }

    /**
      * Make an HTimeZone from a BTimeZone.
      * <p>
      * If the BTimeZone does not correspond to a standard HTimeZone,
      * then this method uses of the timeZoneAliases stored on the
      * BNHaystackService to attempt to perform a custom mapping.
      */
    public HTimeZone makeTimeZone(BTimeZone timeZone)
    {
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
            LOG.error("Cannot create tz tag: " + e.getMessage());
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

    void removeBrokenRefs() 
    {
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
                        lookupComponent((HRef) val);
                    }
                    // failed!
                    catch (UnresolvedException ue)
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

    public Nav getNav() { return nav; }

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

    // Every single tag which the server may have auto-generated.
    protected static final String[] AUTO_GEN_TAGS = new String[] {
        "actions",    "axHistoryId",    "axHistoryRef", "axPointRef",
        "axSlotPath", "axType",         "cur",          "curStatus",
        "curVal",     "dis",            "enum",         "equip",
        "his",        "hisInterpolate", "id",           "kind",
        "navName",    "point",          "site",         /*"siteUri",*/
        "tz",         "unit",           "writable"      };

    private final HashMap watches = new HashMap();

    private final BNHaystackService service;
    private final ComponentStorehouse compStore;
    private final HistoryStorehouse hisStore;
    private final Cache cache;
    private final Nav nav;
}

