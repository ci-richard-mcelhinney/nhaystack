//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//   10 May 2018  Eric Anderson       Added missing @Overrides annotations, added use of generics
//   26 Sep 2018  Andrew Saunders     Added shared constants and handling for geoCoord tag
//   31 Oct 2018  Andrew Saunders     Removed special handling for curVal and writeVal tags-
//                                    handled by new smart tag types BNCurValTag and BNWriteValTag
//   21 Dec 2018  Andrew Saunders     Allowing plain components to be used as sites and equips
//   12 Apr 2019  Eric Anderson       Converting String encoded id tag to an HRef value
//   19 Jul 2019  Eric Anderson       Exporting tags and relations from multiple namespaces based
//                                    on prioritizedNamespaces property
//
package nhaystack.server;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.control.BBooleanPoint;
import javax.baja.control.BControlPoint;
import javax.baja.control.BEnumPoint;
import javax.baja.control.BNumericPoint;
import javax.baja.control.BStringPoint;
import javax.baja.data.BIDataValue;
import javax.baja.driver.BDevice;
import javax.baja.history.BBooleanTrendRecord;
import javax.baja.history.BEnumTrendRecord;
import javax.baja.history.BHistoryConfig;
import javax.baja.history.BHistoryId;
import javax.baja.history.BIHistory;
import javax.baja.history.BNumericTrendRecord;
import javax.baja.history.BStringTrendRecord;
import javax.baja.history.BTrendRecord;
import javax.baja.history.HistorySpaceConnection;
import javax.baja.history.ext.BCovHistoryExt;
import javax.baja.history.ext.BHistoryExt;
import javax.baja.naming.BOrd;
import javax.baja.nre.util.TextUtil;
import javax.baja.schedule.BBooleanSchedule;
import javax.baja.schedule.BEnumSchedule;
import javax.baja.schedule.BNumericSchedule;
import javax.baja.schedule.BStringSchedule;
import javax.baja.schedule.BWeeklySchedule;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusEnum;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.status.BStatusValue;
import javax.baja.sys.Action;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDouble;
import javax.baja.sys.BDynamicEnum;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.BFloat;
import javax.baja.sys.BInteger;
import javax.baja.sys.BLong;
import javax.baja.sys.BMarker;
import javax.baja.sys.BNumber;
import javax.baja.sys.BObject;
import javax.baja.sys.BString;
import javax.baja.sys.BValue;
import javax.baja.sys.Flags;
import javax.baja.sys.Type;
import javax.baja.tag.Relation;
import javax.baja.tag.Tag;
import javax.baja.timezone.BTimeZone;
import javax.baja.units.BUnit;
import javax.baja.units.BUnitConversion;
import javax.baja.util.BFormat;

import nhaystack.BHDict;
import nhaystack.NHRef;
import nhaystack.res.Resources;
import nhaystack.res.Unit;
import nhaystack.site.BHTagged;
import nhaystack.util.NHaystackConst;
import nhaystack.util.SlotUtil;
import org.projecthaystack.HBool;
import org.projecthaystack.HCoord;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HMarker;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HStr;
import org.projecthaystack.HTimeZone;
import org.projecthaystack.HVal;
import org.projecthaystack.io.HZincWriter;
import org.projecthaystack.util.Base64;

/**
  * TagManager does various task associated with generating tags
  * and looking things up based on ids, etc.
  */
public class TagManager implements NHaystackConst
{
    TagManager(
        NHServer server,
        BNHaystackService service,
        SpaceManager spaceMgr,
        Cache cache)
    {
        this.server = server;
        this.service = service;
        this.spaceMgr = spaceMgr;
        this.cache = cache;
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    /**
      * Return a copy of the given annotated tags, except convert any
      * Refs from ComponentRefs to SepRefs.
      */
    public HDict convertAnnotatedRefTags(HDict annotatedTags)
    {
        HDictBuilder hdb = new HDictBuilder();

        Iterator<Map.Entry<String, HVal>> it = annotatedTags.iterator();
        while (it.hasNext())
        {
            Map.Entry<String, HVal> entry = it.next();
            String name = entry.getKey();
            HVal val = entry.getValue();

            if (val instanceof HRef)
            {
                HRef ref = convertAnnotatedRefTag((HRef) val);
                if (ref != null)
                    hdb.add(name, ref);
            }
            else
                hdb.add(name, val);
        }

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
        return doLookupComponent(id, true);
    }

    BComponent doLookupComponent(HRef id, boolean mustBeVisible)
    {
        NHRef nh = NHRef.make(id);

        switch (nh.getSpace())
        {
        // component space
        case NHRef.COMP:
        case NHRef.COMP_BASE64:
            BOrd ord = BOrd.make("station:|" +
                (nh.getSpace().equals(NHRef.COMP) ?
                    "slot:" + SlotUtil.toNiagara(nh.getPath()) :
                    Base64.URI.decodeUTF8(nh.getPath())));

            BComponent comp = (BComponent) ord.get(service, null);
            if (comp == null) return null;

            if (!mustBeVisible) return comp;

            return SpaceManager.isVisibleComponent(comp) ? comp : null;

        // history space
        case NHRef.HIS_BASE64:
        case NHRef.HIS:
            BHistoryId hid = BHistoryId.make(
                nh.getSpace().equals(NHRef.HIS) ?
                    '/' + SlotUtil.toNiagara(nh.getPath()) :
                    Base64.URI.decodeUTF8(nh.getPath()));

            try (HistorySpaceConnection conn = service.getHistoryDb().getConnection(null))
            {
                BIHistory history = conn.getHistory(hid);
                if (history == null) return null;
                BHistoryConfig cfg = history.getConfig();

                if (!mustBeVisible) return cfg;

                return spaceMgr.isVisibleHistory(cfg) ? cfg : null;
            }

        // sep space
        case NHRef.SEP:
            return cache.lookupComponentBySepRef(nh);

        // invalid space
        default:
            return null;
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
            // sepRefs are cached so we don't have to make them on the fly
            NHRef sepRef = cache.lookupSepRefByComponent(comp);
            if (sepRef != null) return sepRef;

            return makeSlotPathRef(comp);
        }
    }

    /**
     * Convert a given HRef to a BOrd
     */
    public static BOrd hrefToOrd(HRef hRef)
    {
        NHRef nh = NHRef.make(hRef);

        // component space
        if (nh.getSpace().equals(NHRef.COMP) ||
            nh.getSpace().equals(NHRef.COMP_BASE64))
        {
            BOrd ord = BOrd.make("station:|" +
                (nh.getSpace().equals(NHRef.COMP) ?
                    "slot:" + SlotUtil.toNiagara(nh.getPath()) :
                    Base64.URI.decodeUTF8(nh.getPath())));

            return ord;
        }
        return BOrd.NULL;
    }

    public static NHRef makeSlotPathRef(BComponent comp)
    {
        String path = comp.getSlotPath().toString();
        path = removePrefix(path, "slot:/");
        path = SlotUtil.fromNiagara(path);

        return NHRef.make(NHRef.COMP, path);
    }

    public static NHRef makeSepRef(String[] navPath)
    {
        return NHRef.make(
            NHRef.SEP, 
            SlotUtil.fromNiagara(
                TextUtil.join(navPath, '/')));
    }

    /**
     *  Generate a HDict tag representation of given component
     */
    public HDict generateComponentTags(BComponent comp)
    {
        List<String> namespaces = service.getPrioritizedNamespaceList();
        Collection<Tag> tags = comp.tags().getAll();
        HDictBuilder hdb = new HDictBuilder();

        for (int i = namespaces.size() - 1; i >= 0; --i)
        {
            for (Tag tag : tags)
            {
                if (tag.getId().getDictionary().equals(namespaces.get(i)))
                {
                    String tagName = tag.getId().getName();
                    BIDataValue tagValue = tag.getValue();
                    Type tagValueType = tagValue.getType();

                    if (tagValueType == BMarker.TYPE)
                    {
                        hdb.add(tagName, HMarker.VAL);
                    }
                    else if (tagValueType == BLong.TYPE)
                    {
                        hdb.add(tagName, HNum.make(((BLong) tagValue).getLong()));
                    }
                    else if (tagValueType == BDouble.TYPE)
                    {
                        hdb.add(tagName, HNum.make(((BDouble) tagValue).getDouble()));
                    }
                    else if (tagValueType == BFloat.TYPE)
                    {
                        hdb.add(tagName, HNum.make(((BFloat) tagValue).getFloat()));
                    }
                    else if (tagValueType == BInteger.TYPE)
                    {
                        hdb.add(tagName, HNum.make(((BInteger) tagValue).getInt()));
                    }
                    else if (tagValueType == BBoolean.TYPE)
                    {
                        hdb.add(tagName, HBool.make(((BBoolean) tagValue).getBoolean()));
                    }
                    else if (tagValueType == BString.TYPE)
                    {
                        String value = ((BString) tagValue).getString();
                        if (tagName.equals("geoCoord"))
                        {
                            hdb.add(tagName, HCoord.make(value));
                        }
                        else if (tagName.equals("id"))
                        {
                            hdb.add(tagName, HRef.make(value));
                        }
                        else
                        {
                            hdb.add(tagName, HStr.make(value));
                        }
                    }
                    else if (tagValueType == BTimeZone.TYPE)
                    {
                        hdb.add(tagName, HStr.make(((BTimeZone) tagValue).getId()));
                    }
                    else if (tagValueType == BOrd.TYPE && tagName.equals("id"))
                    {
                        hdb.add(tagName, makeComponentRef(comp).getHRef());
                    }
                    else if (tagValueType == BUnit.TYPE)
                    {
                        if (tagName.equals("unit") && !((BUnit) tagValue).isNull())
                        {
                            hdb.add(tagName, ((BUnit) tagValue).getSymbol());
                        }
                    }
                    else if (tagValueType == BDynamicEnum.TYPE)
                    {
                        hdb.add(tagName, HStr.make(((BDynamicEnum) tagValue).getTag()));
                    }
                    else
                    {
                        LOG.warning("Niagara tag not handled: " + tagName + ':' + tagValue + ':' + tagValueType);
                    }
                }
            }
        }

        return hdb.toDict();
    }

    /**
     * Generate a HDict HRef tags representation of the given component haystack relations
     */
    public HDict convertRelationsToRefTags(BComponent comp)
    {
        List<String> namespaces = service.getPrioritizedNamespaceList();
        Collection<Relation> relations = comp.relations().getAll();
        HDictBuilder hdb = new HDictBuilder();

        for (int i = namespaces.size() - 1; i >= 0; --i)
        {
            for (Relation relation : relations)
            {
                if (relation.getId().getDictionary().equals(namespaces.get(i)))
                {
                    BComponent relationEndpoint = (BComponent) relation.getEndpoint();
                    if (LOG.isLoggable(Level.FINE))
                    {
                        LOG.fine("process relation: " + relation.getId() +
                            (relation.isOutbound() ? " out " : " in ") +
                            relationEndpoint.getSlotPath());
                    }

                    if (relation.isOutbound())
                    {
                        String relName = relation.getId().getName();
                        HRef hRef = makeComponentRef(relationEndpoint).getHRef();
                        hdb.add(relName, hRef);
                    }
                }
            }
        }

        return hdb.toDict();
    }

////////////////////////////////////////////////////////////////
// package-scope
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
        return comp instanceof BHistoryConfig ?
            createHistoryTags((BHistoryConfig) comp) :
            createComponentTags(comp);
    }

    /**
      * look up the BHistoryConfig for a HRef
      */
    BHistoryConfig lookupHistoryConfig(HRef id)
    {
        BComponent comp = lookupComponent(id);
        if (comp == null)
        {
            LOG.severe("lookup failed for '" + id + '\'');
            return null;
        }

        if (comp instanceof BHistoryConfig)
        {
            // history space
            BHistoryConfig cfg = (BHistoryConfig) comp;
            return spaceMgr.isVisibleHistory(cfg) ? 
                cfg : null;
        }
        else if (comp instanceof BControlPoint)
        {
            // component space
            return spaceMgr.lookupHistoryFromPoint((BControlPoint) comp);
        }
        else
        {
            LOG.severe("cannot find history for for '" + id + '\'');
            return null;
        }
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
    public HDict createComponentTags(BComponent comp)
    {
        HDictBuilder hdb = new HDictBuilder();
        hdb.add(generateComponentTags(comp));
        hdb.add(convertRelationsToRefTags(comp));

        if (comp instanceof BHTagged)
        {
            hdb.add(((BHTagged) comp).generateTags(server));
        }
        else
        {
            // add existing tags
            HDict tags = BHDict.findTagAnnotation(comp);
            if (tags == null) 
                tags = HDict.EMPTY;
            else
                hdb.add("axAnnotated");

            hdb.add(convertAnnotatedRefTags(tags));

            // navName
            String navName = Nav.makeNavName(comp, tags);
            hdb.add("navName", navName);

            // add misc other tags
            hdb.add("axType", comp.getType().toString());
            if (comp.getSlotPath() != null)
            {
                // Expose the slot path under both the old axSlotPath and
                // the new n4SlotPath tags so we don't break existing clients
                // expecting the old name.
                String slotPath = comp.getSlotPath().toString();
                hdb.add("axSlotPath", slotPath);
                hdb.add("n4SlotPath", slotPath);
            }

            // points get special treatment
            if (comp instanceof BControlPoint)
                createPointTags((BControlPoint) comp, hdb, tags);

            // schedules get special treatment as 'points'
            else if (comp instanceof BWeeklySchedule)
                createScheduleTags((BWeeklySchedule) comp, hdb, tags);

            // dis
            String dis = createDis(comp, tags, hdb);
            // if the dis tag wasn't converted from niagara tags, add it the nhaystack way.
            if (!hdb.has("dis"))
            {
                hdb.add("dis", dis);
            }

            // add id if it doesn't exist
            if (!hdb.has("id"))
            {
                HRef id = makeComponentRef(comp).getHRef();
                hdb.add("id", HRef.make(id.val, dis));
            }

            // add device if it doesn't exist
            if (comp instanceof BDevice && !hdb.has("device"))
            {
                hdb.add("device", comp.getType().getModule().getModuleName());
            }
        }

        // add custom tags
        hdb.add(server.createCustomTags(comp));

        // done
        return hdb.toDict();
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
    HDict createComponentCovTags(BComponent comp)
    {
        if (comp instanceof BControlPoint)
            return createPointCovTags((BControlPoint) comp);

        else if (comp instanceof BWeeklySchedule)
            return createScheduleCovTags((BWeeklySchedule) comp);

        else
            throw new IllegalStateException("Cannot create COV tags for " + comp.getSlotPath());
    }

    /**
      * Create the haystack representation of a BHistoryConfig.
      *
      * The haystack representation is a combination of the 
      * autogenerated tags, and those tags specified
      * in the explicit haystack annotation (if any).
      *
      * This method never returns null.
      */
    HDict createHistoryTags(BHistoryConfig cfg)
    {
        HDictBuilder hdb = new HDictBuilder();
        BControlPoint point = spaceMgr.lookupPointFromHistory(cfg);

        // add existing tags
        HDict tags = BHDict.findTagAnnotation(cfg);
        if (tags == null) 
            tags = HDict.EMPTY;
        hdb.add(tags);

        // add dis and navName tags
        String navName = cfg.getId().toString();
        if (navName.startsWith("/")) navName = navName.substring(1);
        navName = TextUtil.replace(navName, "/", "_");
        hdb.add("navName", navName);

        String dis;
        if (point == null) hdb.add("dis", navName);
        else
        {
            dis = point.getDisplayName(null);
            if (dis == null)
                dis = navName;
            hdb.add("dis", dis);
        }

        // add id
        HRef ref = makeComponentRef(cfg).getHRef();
        hdb.add("id", HRef.make(ref.val, navName));


        // add misc other tags
        hdb.add("axType", cfg.getType().toString());

        // expose under both tag names to not break older clients.
        String historyId = cfg.getId().toString();
        hdb.add("axHistoryId", historyId);
        hdb.add("n4HistoryId", historyId);

        hdb.add("point");
        hdb.add("his");

        // time zone
        if (!tags.has("tz"))
        {
            HTimeZone tz = server.fromBajaTimeZone(cfg.getTimeZone());
            if (tz != null) hdb.add("tz", tz.name);
        }

        // point kind tags
        Type recType = cfg.getRecordType().getResolvedType();
        if (recType.is(BTrendRecord.TYPE))
        {
            int pointKind = getTrendRecordKind(recType);
            BFacets facets = (BFacets) cfg.get("valueFacets");
            addPointKindTags(pointKind, facets, tags, hdb);
        }
        // if its not a BTrendRecord, just say that the kind is Str
        else
        {
            if (!tags.has("kind")) hdb.add("kind", "Str");
        }

        // check if this history has a point
        if (point != null)
        {
            // add point ref
            hdb.add("axPointRef", makeComponentRef(point).getHRef());

            // hisInterpolate 
            if (!tags.has("hisInterpolate"))
            {
                BHistoryExt historyExt = spaceMgr.lookupHistoryExt(point);
                if (historyExt instanceof BCovHistoryExt)
                    hdb.add("hisInterpolate", "cov");
            }
        }

        // add custom tags
        hdb.add(server.createCustomTags(cfg));

        // done
        return hdb.toDict();
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    /**
      * Create the tags which represent a change-of-value on a point.
      * The only tags that are returned are id, curVal, and curStatus.
      */
    private HDict createPointCovTags(BControlPoint point)
    {
        HDictBuilder hdb = new HDictBuilder();

        // id
        HRef id = makeComponentRef(point).getHRef();
        hdb.add("id", id);

        // curVal
        BStatus status = point.getStatus();
        HDict tags = BHDict.findTagAnnotation(point);
        if (tags == null) tags = HDict.EMPTY;

        int pointKind = getControlPointKind(point);
        BFacets facets = (BFacets) point.get("facets");

        HVal curVal = makeCurVal(point, pointKind, facets, point.getStatusValue(), tags);
        if (curVal != null) hdb.add("curVal", curVal);

        // curStatus
        HStr curStatus = makeCurStatus(status);
        hdb.add("curStatus", curStatus);

        // done
        return hdb.toDict();
    }

    /**
      * Create the tags which represent a change-of-value on a point.
      * The only tags that are returned are id, curVal, and curStatus.
      */
    private HDict createScheduleCovTags(BWeeklySchedule point)
    {
        BValue val = point.get("out");
        BStatusValue out = (BStatusValue) val;

        int pointKind = getControlPointKind(point);
        BFacets facets = (BFacets) point.get("facets");

        // add existing tags
        HDict tags = BHDict.findTagAnnotation(point);
        if (tags == null) 
            tags = HDict.EMPTY;

        HDictBuilder hdb = new HDictBuilder();

        // add id
        HRef id = makeComponentRef(point).getHRef();
        hdb.add("id", id);

        // curVal
        HVal curVal = makeCurVal(point, pointKind, facets, point.getStatusValue(), tags);
        if (curVal != null) hdb.add("curVal", curVal);

        // curStatus
        HStr curStatus = makeCurStatus(out.getStatus());
        if (curStatus != null) hdb.add("curStatus", curStatus);

        // done
        return hdb.toDict();
    }

    /**
      * Convert the ref from ComponentRef to SepRef (unless there is no SepRef).
      */
    private HRef convertAnnotatedRefTag(HRef ref)
    {
        BComponent comp = null;
        try
        {
            comp = lookupComponent(ref);
        }
        catch (Exception e)
        {
            // cannot resolve
            return ref;
        }
        
        // note: its possible for the component to be null if e.g. its not
        // visible under the current Context.
        if (comp == null)
            return null;

        return makeComponentRef(comp).getHRef();
    }

    /**
      * create the 'dis' tag
      */
    private String createDis(BComponent comp, HDict tags, HDictBuilder hdb)
    {
        String dis = makeDisName(comp, tags);

        if (hdb.has("point"))
        {
            String equipDis = lookupDisName(hdb, EQUIP_REF);
            if (equipDis != null)
            {
                String siteDis = lookupDisName(hdb, SITE_REF);
                if (siteDis != null)
                {
                    dis = siteDis + ' ' + equipDis + ' ' + dis;
                }
                else
                {
                    dis = equipDis + ' ' + dis;
                }
            }
        }
        else if (hdb.has("equip"))
        {
            String siteDis = lookupDisName(hdb, SITE_REF);
            if (siteDis != null)
                dis = siteDis + ' ' + dis;
        }

        return dis;
    }

    /**
      * look up the 'dis' associated with the given tag name
      */
    private String lookupDisName(HDictBuilder hdb, String tagName)
    {
        if (hdb.has(tagName))
        {
            BComponent comp = lookupComponent((HRef) hdb.get(tagName));
            if (comp != null)
            {
                HDict compTags = BHDict.findTagAnnotation(comp);
                return makeDisName(comp, compTags);
            }
        }
        return null;
    }

    /**
      * create all the point-specific tags
      */
    private void createPointTags(
        BControlPoint point, 
        HDictBuilder hdb,
        HDict tags)
    {
        // ensure there is a point marker tag
        hdb.add("point");

        // check if this point has a history
        BHistoryConfig cfg = spaceMgr.lookupHistoryFromPoint(point);
        if (cfg != null)
        {
            hdb.add("his");

            if (service.getShowLinkedHistories())
                hdb.add("axHistoryRef", makeComponentRef(cfg).getHRef());

            // tz
            if (!tags.has("tz"))
            {
                HTimeZone tz = server.fromBajaTimeZone(cfg.getTimeZone());
                if (tz != null) hdb.add("tz", tz.name);
            }

            // hisInterpolate 
            if (!tags.has("hisInterpolate"))
            {
                BHistoryExt historyExt = spaceMgr.lookupHistoryExt(point);
                if (historyExt instanceof BCovHistoryExt)
                    hdb.add("hisInterpolate", "cov");
            }
        }

        // cur, writable
        hdb.add("cur");
        if (point.isWritablePoint())
            hdb.add("writable");

        // point kind tags
        int pointKind = getControlPointKind(point);
        BFacets facets = (BFacets) point.get("facets");
        addPointKindTags(pointKind, facets, tags, hdb);

        // curVal
        HVal curVal = makeCurVal(point, pointKind, facets, point.getStatusValue(), tags);
        if (curVal != null) hdb.add("curVal", curVal);

        // curStatus
        HStr curStatus = makeCurStatus(point.getStatus());
        if (curStatus != null) hdb.add("curStatus", curStatus);
        hdb.add("axStatus", axStatus(point.getStatus()));

        // minVal, maxVal, precision
        HashMap<String, String> supportedFacetNames = new HashMap<>();
        supportedFacetNames.put(BFacets.MIN, "minVal");
        supportedFacetNames.put(BFacets.MAX, "maxVal");
        supportedFacetNames.put(BFacets.PRECISION, "precision");
        supportedFacetNames.forEach((k, v) -> {
            BNumber facetVal = getNumberFacet(facets, k);
            if (facetVal != BDouble.NaN)
            {
                hdb.add(v, HNum.make(facetVal.getInt()));
            }
            else
            {
                LOG.warning("Problem generating tags from facets for: " + point.getSlotPath().toDisplayString());
                LOG.warning("Type of component with problem facets is: " + point.getType().getDisplayName(null));
            }
        });

        // actions tag
        if (point.isWritablePoint() || tags.has("writable"))
        {
            HGrid actionsGrid = createPointActions(point, pointKind);
            if (actionsGrid != null)
                hdb.add("actions", HStr.make(HZincWriter.gridToString(actionsGrid)));
        }

        // siteRef, equipRef
        addSiteEquipTags(point, hdb, tags);
    }

    private static String axStatus(BStatus status)
    {
        if (status.isOk()) return "ok";

        StringBuilder sb = new StringBuilder();
        if (status.isDisabled())     sb.append("disabled")     .append(',');
        if (status.isFault())        sb.append("fault")        .append(',');
        if (status.isDown())         sb.append("down")         .append(',');
        if (status.isAlarm())        sb.append("alarm")        .append(',');
        if (status.isStale())        sb.append("stale")        .append(',');
        if (status.isOverridden())   sb.append("overridden")   .append(',');
        if (status.isNull())         sb.append("null")         .append(',');
        if (status.isUnackedAlarm()) sb.append("unackedAlarm") .append(',');

        sb.setLength(sb.length()-1);
        return sb.toString();
    }

    /**
      * create all the point-specific tags
      */
    private void createScheduleTags(
        BWeeklySchedule point, 
        HDictBuilder hdb,
        HDict tags)
    {
        BValue val = point.get("out");
        if (!(val instanceof BStatusValue)) return;
        BStatusValue out = (BStatusValue) val;
        
        hdb.add("point");
        hdb.add("cur");

        // schedules are writable
        hdb.add("writable");

        // point kind tags
        int pointKind = getControlPointKind(point);
        BFacets facets = (BFacets) point.get("facets");
        addPointKindTags(pointKind, facets, tags, hdb);

        // curVal
        HVal curVal = makeCurVal(point, pointKind, facets, point.getStatusValue(), tags);
        if (curVal != null) hdb.add("curVal", curVal);

        // curStatus
        HStr curStatus = makeCurStatus(out.getStatus());
        if (curStatus != null) hdb.add("curStatus", curStatus);

        // minVal, maxVal, precision
        BNumber minVal    = getNumberFacet(facets, BFacets.MIN);
        BNumber maxVal    = getNumberFacet(facets, BFacets.MAX);
        BNumber precision = getNumberFacet(facets, BFacets.PRECISION);
        if (minVal    != null) hdb.add("minVal",    HNum.make(minVal.getInt()));
        if (maxVal    != null) hdb.add("maxVal",    HNum.make(maxVal.getInt()));
        if (precision != null) hdb.add("precision", HNum.make(precision.getInt()));

        // siteRef, equipRef
        addSiteEquipTags(point, hdb, tags);
    }

    private void addSiteEquipTags(
        BComponent point,
        HDictBuilder hdb,
        HDict tags)
    {
        // the point is explicitly tagged with an equipRef
        // the hdb will contain an equipRef tag converted from a equipRef
        // relation, if it exists.
        if (hdb.has(EQUIP_REF))
        {
            if (!hdb.has(SITE_REF))
            {
                BComponent equip = lookupComponent((HRef) hdb.get(EQUIP_REF));
                if (equip != null)
                {
                    HRef siteRef = findSiteRef(equip);
                    if (siteRef != null)
                    {
                        hdb.add(SITE_REF, siteRef);
                    }
                }
            }
        }
        else
        {
            // maybe we've cached an implicit equipRef
            BComponent equip = server.getCache().getImplicitEquip(point);
            if (equip != null)
            {
                hdb.add(EQUIP_REF, makeComponentRef(equip).getHRef());

                // try to look up siteRef too
                HRef siteRef = findSiteRef(equip);
                if (siteRef != null)
                {
                    hdb.add(SITE_REF, siteRef);
                }
            }
        }
    }

    private HRef findSiteRef(BComponent equip)
    {
        HRef siteRef = null;

        // check for hs:siteRef niagara relation on the equip component
        Optional<Relation> optRelation = equip.relations().get(ID_SITE_REF);
        if (optRelation.isPresent())
        {
            BComponent site = (BComponent) optRelation.get().getEndpoint();
            siteRef = makeComponentRef(site).getHRef();
        }

        if (siteRef == null)
        {
            HDict equipTags = BHDict.findTagAnnotation(equip);
            if (equipTags != null && equipTags.has(SITE_REF))
            {
                siteRef = convertAnnotatedRefTag(equipTags.getRef(SITE_REF));
            }
        }

        return siteRef;
    }

    public static BNumber getNumberFacet(BFacets facets, String name)
    {
        if (!(facets.get(name) instanceof BNumber))
        {
            LOG.warning("Detected incorrectly configured facet supplied for :" + name);
            LOG.warning("Please check all facets are correctly configured on all Control Points");
            return BDouble.NaN;
        }

        if (!name.equals(BFacets.MAX) &&
            !name.equals(BFacets.MIN) &&
            !name.equals(BFacets.PRECISION))
        {
            LOG.warning("Trying to retrieve unsupported number facet: " + name);
            return BDouble.NaN;
        }

        BNumber num = (BNumber) facets.get(name);
        if (num == null)                   return BDouble.NaN;
        if (num.toString().equals("+inf")) return BDouble.NaN;
        if (num.toString().equals("-inf")) return BDouble.NaN;
        return num;
    }

    /**
      * create the 'actions' tag for a point
      */
    private static HGrid createPointActions(BControlPoint point, int pointKind)
    {
        ArrayList<HDict> arr = new ArrayList<>();

        switch(pointKind)
        {
            case NUMERIC_KIND:
            case ENUM_KIND:
            case STRING_KIND:
                addPointAction(point, arr, "override",          "pointOverride($self, $val, $duration)");
                addPointAction(point, arr, "auto",              "pointAuto($self)");
                addPointAction(point, arr, "emergencyOverride", "pointEmergencyOverride($self, $val)");
                addPointAction(point, arr, "emergencyAuto",     "pointEmergencyAuto($self)");
                addPointAction(point, arr, "set",               "pointSetDef($self, $val)");
                break;

            case BOOLEAN_KIND:
                addPointAction(point, arr, "active",            "pointOverride($self, true, $duration)");
                addPointAction(point, arr, "inactive",          "pointOverride($self, false, $duration)");
                addPointAction(point, arr, "auto",              "pointAuto($self)");
                addPointAction(point, arr, "emergencyActive",   "pointEmergencyOverride($self, true, $duration)");
                addPointAction(point, arr, "emergencyInactive", "pointEmergencyOverride($self, false, $duration)");
                addPointAction(point, arr, "emergencyAuto",     "pointEmergencyAuto($self)");
                addPointAction(point, arr, "set",               "pointSetDef($self, $val)");
                break;
        }

        HDict[] rows = arr.toArray(EMPTY_HDICT_ARRAY);
        return rows.length == 0 ?
            null : HGridBuilder.dictsToGrid(rows);
    }

    /**
      * add an action for the point (unless its hidden)
      */
    private static void addPointAction(
        BControlPoint point,
        List<HDict> arr,
        String name,
        String expr)
    {
        Action action = point.getAction(name);
        if (action == null) return;
        if (Flags.isHidden(point, action)) return;

        HDictBuilder hdb = new HDictBuilder();
        hdb.add("dis", point.getDisplayName(action, null));
        hdb.add("expr", expr);

        arr.add(hdb.toDict());
    }

    static NHRef makeHistoryRef(BHistoryConfig cfg)
    {
        String path = cfg.getId().toString();
        path = removePrefix(path, "/");
        path = SlotUtil.fromNiagara(path);

        return NHRef.make(NHRef.HIS, path);
    }

    private static String removePrefix(String path, String prefix)
    {
        if (!path.startsWith(prefix))
            throw new IllegalStateException(
              '\'' + path + "' does not start with '" + prefix + '\'');

        return path.substring(prefix.length());
    }

    /**
      * make 'dis' based on navNameFormat
      */
    private static String makeDisName(BComponent comp, HDict tags)
    {
        String format = tags != null && tags.has("navNameFormat") ?
            tags.getStr("navNameFormat") :
            "%displayName%";
        return BFormat.format(format, comp);
    }

    /**
      * create a curVal for a point
      */
    private HVal makeCurVal(
        BComponent point, 
        int pointKind, BFacets facets, 
        BStatusValue sv, HDict tags)
    {
        if (sv.getStatus().isNull()) return null;
        if (!makeCurStatus(sv.getStatus()).val.equals("ok")) return null;

        switch(pointKind)
        {
            case NUMERIC_KIND:
                BStatusNumeric sn = (BStatusNumeric) sv;

                if (tags.has("unit"))
                {
                    HVal unit = tags.get("unit");
                    return HNum.make(sn.getNumeric(), unit.toString());
                }
                else
                {
                    Unit unit = findUnit(facets);
                    if (unit == null) 
                        return HNum.make(sn.getNumeric());
                    else
                        return HNum.make(sn.getNumeric(), unit.symbol);
                }

            case BOOLEAN_KIND:
                BStatusBoolean sb = (BStatusBoolean) sv;
                return HBool.make(sb.getBoolean());

            case ENUM_KIND:

                BEnumRange er = (BEnumRange) facets.get("range");
                if (er == null)
                {
                    LOG.severe("No 'range' facets found for point " + point.getSlotPath());
                    return HStr.make("INVALID_ENUM");
                }
                else
                {
                    BStatusEnum se = (BStatusEnum) sv;
                    return HStr.make(SlotUtil.fromEnum(
                        er.getTag(se.getEnum().getOrdinal()),
                        service.getTranslateEnums()));
                }

            case STRING_KIND:
                BStatusString ss = (BStatusString) sv;
                return HStr.make(ss.getValue());

            default: 
                return null;
        }
    }

    /**
      * create a curStatus from a BStatus
      */
    private static HStr makeCurStatus(BStatus status)
    {
        if      (status.isOk())       return HStr.make("ok");
        else if (status.isDisabled()) return HStr.make("disabled");
        else if (status.isFault())    return HStr.make("fault");
        else if (status.isDown())     return HStr.make("down");
//        else if (status.isNull())     return HStr.make("unknown");

        // these qualify as "ok" for curStatus
        else if (status.isOverridden() ||
                 status.isNull() ||
                 status.isAlarm() ||
                 status.isStale() ||
                 status.isUnackedAlarm())
        {
            return HStr.make("ok");
        }

        return null;
    }

    /**
      * add the 'kind' tag, along with an associated tags 
      * like 'enum' or 'unit'
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

                if (!tags.has("unit"))
                {
                    Unit unit = findUnit(facets);
                    if (unit != null) 
                        hdb.add("unit", unit.symbol);
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

    private static Unit findUnit(BFacets facets)
    {
        if (facets == null) 
            return null;

        BObject obj = facets.get(BFacets.UNITS);
        if (!(obj instanceof BUnit)) return null;

        BUnit unit = (BUnit) obj;

        if (unit == null || unit.isNull())
            return null;

        int conv = facets.geti("unitConversion", 0);
        if (conv != 0)
            unit = BUnitConversion.make(conv).getDesiredUnit(unit);

        return Resources.fromBajaUnit(unit);
    }

    private static int getControlPointKind(BComponent point)
    {
        if      (point instanceof BNumericPoint) return NUMERIC_KIND;
        else if (point instanceof BBooleanPoint) return BOOLEAN_KIND;
        else if (point instanceof BEnumPoint)    return ENUM_KIND;
        else if (point instanceof BStringPoint)  return STRING_KIND;

        if      (point instanceof BNumericSchedule) return NUMERIC_KIND;
        else if (point instanceof BBooleanSchedule) return BOOLEAN_KIND;
        else if (point instanceof BEnumSchedule)    return ENUM_KIND;
        else if (point instanceof BStringSchedule)  return STRING_KIND;

        else return UNKNOWN_KIND;
    }

    private static String findTrueFalse(BFacets facets)
    {
        if (facets == null) 
            return "false,true";

        return 
            facets.gets("falseText", "false") + ',' +
            facets.gets("trueText", "true");
    }

    private String findRange(BFacets facets)
    {
        if (facets == null) 
            return "";

        BEnumRange range = (BEnumRange) facets.get("range");
        if (range == null || range.isNull())
            return "";

        StringBuilder sb = new StringBuilder();
        int[] ords = range.getOrdinals();
        for (int i = 0; i < ords.length; i++)
        {
            if (i > 0) sb.append(',');
            sb.append(SlotUtil.fromEnum(
                range.get(ords[i]).getTag(),
                service.getTranslateEnums()));
        }
        return sb.toString();
    }

    private static int getTrendRecordKind(Type trendRecType)
    {
        if      (trendRecType.is(BNumericTrendRecord.TYPE)) return NUMERIC_KIND;
        else if (trendRecType.is(BBooleanTrendRecord.TYPE)) return BOOLEAN_KIND;
        else if (trendRecType.is(BEnumTrendRecord.TYPE))    return ENUM_KIND;
        else if (trendRecType.is(BStringTrendRecord.TYPE))  return STRING_KIND;

        else return UNKNOWN_KIND;
    }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    /** Every single tag which the server may have auto-generated.  */
    static final String[] AUTO_GEN_TAGS = {
        "axAnnotated",
        "axHistoryId",  // backward compatibility
        "axHistoryRef",
        "axPointRef",
        "axType",
        "axSlotPath",   // backward compatibility
        "axStatus",

        "n4HistoryId",  // replaces axHistoryId
        "n4SlotPath",   // replaces axSlotPath

        "actions",
        "cur",
        "curErr",
        "curStatus",
        "curVal",
        "dis",
        "enum",
        "equip",
        "his",
        "hisInterpolate",
        "id",
        "kind",
        "maxVal",
        "minVal",
        "navName",
        "point",
        "precision",
        "site",
        "tz",
        "unit",
        "writable",
        "writeErr",
        "writeLevel",
        "writeStatus"
    };

    private static final Logger LOG = Logger.getLogger("nhaystack");

    private static final HDict[] EMPTY_HDICT_ARRAY = new HDict[0];

    // point kinds
    private static final int UNKNOWN_KIND = -1;
    private static final int NUMERIC_KIND =  0;
    private static final int BOOLEAN_KIND =  1;
    private static final int ENUM_KIND    =  2;
    private static final int STRING_KIND  =  3;

    private final NHServer server;
    private final BNHaystackService service;
    private final SpaceManager spaceMgr;
    private final Cache cache;
}

