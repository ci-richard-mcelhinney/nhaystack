//
// Copyright (c) 2018 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 May 2018  Eric Anderson    Creation
//   26 Sep 2018  Andrew Saunders  Retaining the Haystack slot when migrating tags,
//                                 converting geoLat and geoLon tags to a geoCoord tag,
//                                 added shared constants, logging messages to job when
//                                 appropriate
//   19 Jul 2019  Eric Anderson    Ad hoc tags transferred to Niagara tags; support for
//                                 multiple, prioritized namespaces when migrating the
//                                 Haystack slot
//
package nhaystack.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.data.BIDataValue;
import javax.baja.file.BIFile;
import javax.baja.file.FilePath;
import javax.baja.naming.BOrd;
import javax.baja.sys.BComponent;
import javax.baja.sys.BModule;
import javax.baja.sys.BNumber;
import javax.baja.sys.BRelation;
import javax.baja.sys.BSimple;
import javax.baja.sys.BString;
import javax.baja.sys.Sys;
import javax.baja.tag.Id;
import javax.baja.tag.Relation;
import javax.baja.tag.Relations;
import javax.baja.tag.Tag;
import javax.baja.tag.Tags;
import javax.baja.util.Lexicon;

import nhaystack.BHDict;
import nhaystack.util.NHaystackConst;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HCoord;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HVal;
import com.tridium.sys.tag.ComponentRelations;
import com.tridium.sys.tag.ComponentTags;

public final class HaystackSlotUtil
    implements NHaystackConst
{
    // private constructor
    private HaystackSlotUtil()
    {
    }

    private static final Lexicon LEX = Lexicon.make("nhaystack");
    private static final Logger LOG = Logger.getLogger("nhaystack");

    public static void migrateHaystackTags(BComponent component)
    {
        migrateHaystackTags(component, null);
    }

    public static void migrateHaystackTags(BComponent component, BNHaystackConvertHaystackSlotsJob job)
    {
        try
        {
            HDict dict = BHDict.findTagAnnotation(component);
            if (dict == null)
            {
                return;
            }
            migrateHaystackTags(component, dict, job, findNHaystackService());
        }
        catch (Exception e)
        {
            String logMsg = LEX.getText("haystack.slot.conv.replace.exception");
            if (job == null)
            {
                LOG.log(Level.WARNING, e, () -> logMsg);
            }
            else
            {
                job.log().message(logMsg + ": " + e);
            }
        }
    }

    private static BNHaystackService findNHaystackService()
    {
        return Sys.isStation() ?
            (BNHaystackService) Sys.findService(BNHaystackService.TYPE).orElse(null) :
            null;
    }

    private static void migrateHaystackTags(
        BComponent component,
        HDict dict,
        BNHaystackConvertHaystackSlotsJob job,
        BNHaystackService nhaystackService)
    {
        HDict newDictValue = refactorHaystackSlot(component, dict, job, nhaystackService);
        component.set(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(newDictValue));
    }

    // this method is also called from BHDictFE when editing a BHDict slot.
    public static HDict refactorHaystackSlot(BComponent component, HDict dict, BNHaystackService service)
    {
        return refactorHaystackSlot(component, dict, null, service);
    }

    private static HDict refactorHaystackSlot(
        BComponent component,
        HDict dict,
        BNHaystackConvertHaystackSlotsJob job,
        BNHaystackService nhaystackService)
    {
        if (job != null)
        {
            job.log().message(LEX.getText("haystack.slot.conv.current", new Object[] {component.getSlotPath(), dict}));
            job.incCount();
        }

        HDictBuilder newValueBuilder = new HDictBuilder();

        Set<String> blacklist = getBlacklist();
        Set<String> tagGroupList = getTagGroupList();

        // migrate niagara geoLat & geoLon tags to a geoCoord tag
        boolean hasGeoCoord = geoLatLonToGeoCoord(component);

        @SuppressWarnings("unchecked")
        Iterator<Entry<String, HVal>> tags = (Iterator<Map.Entry<String, HVal>>)dict.iterator();
        // setup for geoLat & geoLon conversion
        HCoord coord;
        if (dict.has(GEO_LAT) || dict.has(GEO_LON))
        {
            // an existing niagara geoCoord tag will override HDict geoLat, Lon, or Coord tags
            if (hasGeoCoord)
            {
                if (job != null)
                {
                    String coordVal = component.tags().get(ID_GEO_COORD).get().toString();
                    job.log().message(LEX.getText("haystack.slot.conv.geoCoord", new Object[] { component.getSlotPath(), coordVal }));
                }
            }
            else
            {
                coord = geoLatLonToGeoCoord(dict);
                if (coord != null)
                {
                    setAddTag(GEO_COORD, BString.make(coord.toString()), component, null);
                    if (job != null)
                    {
                        job.log().message(LEX.getText("haystack.slot.conv.geoLatLon", new Object[] { component.getSlotPath(), coord.toString() }));
                    }
                }
            }
        }

        while (tags.hasNext())
        {
            Map.Entry<String, HVal> tag = tags.next();
            final String tagName = tag.getKey();
            if (blacklist.contains(tagName))
            {
                if (tagName.equals(GEO_LAT) || tagName.equals(GEO_LON))
                {
                    continue;
                }

                if (job != null)
                {
                    job.log().message(LEX.getText("haystack.slot.conv.keepBlackListTag", new Object[]{component.getSlotPath(), tagName }));
                }

                newValueBuilder.add(tagName, tag.getValue());
            }
            else if (tagGroupList.contains(tagName))
            {
                // tagGroupId tags should be ignored
                continue;
            }
            else
            {
                // Everything else
                try
                {
                    if (tag.getValue() instanceof HRef)
                    {
                        // convert HRef to haystack relation in niagara
                        HRef href = (HRef)tag.getValue();
                        BOrd refedOrd = TagManager.hrefToOrd(href);
                        if (refedOrd.isNull())
                        {
                            if (job != null)
                            {
                                job.log().message(LEX.getText("haystack.slot.conv.refTag.noResolve", new Object[] {component.getSlotPath(), href.toString()}));
                                job.incWarningCount();
                            }
                            continue;
                        }

                        BComponent refedComp = refedOrd.get(component).asComponent();
                        replaceAddRelation(tagName, refedComp, component, nhaystackService);
                    }
                    else
                    {
                        // Skip geoCoord if migrating the slot and the hs:geoCoord Niagara tag
                        // already exists
                        if (job != null && tagName.equals(GEO_COORD) && hasGeoCoord)
                        {
                            continue;
                        }

                        // convert HDict tags to niagara tags
                        BSimple simple = TypeUtil.toBajaSimple(tag.getValue());
                        if (simple instanceof BIDataValue)
                        {
                            setAddTag(tagName, (BIDataValue)simple, component, nhaystackService);
                        }
                        else
                        {
                            String msg = LEX.getText("haystack.slot.conv.invalid.value", new Object[] {component.getSlotPath(), tagName });
                            if (job != null)
                            {
                                job.log().message(msg);
                            }
                            else
                            {
                                LOG.info(msg);
                            }

                            newValueBuilder.add(tagName, tag.getValue());
                        }
                    }
                }
                catch (Exception ise)
                {
                    String logMsg = LEX.getText("haystack.slot.conv.exception", new Object[] { component.getSlotPath(), tagName });
                    if (job != null)
                    {
                        job.incErrorCount();
                        job.setUpgradeFault(component.getSlotPath() + ": tag " + tagName + " SEE JOB LOG.");
                        job.log().failed(logMsg);
                    }

                    LOG.log(Level.WARNING, ise, () -> logMsg);

                    newValueBuilder.add(tagName, tag.getValue());
                }
            }
        }

        // Don't call isEmpty on HDictBuilder: if the internal map is
        // null, it throws a null pointer exception
        final HDict rtnValue = newValueBuilder.toDict();
        if (job != null)
        {
            if (dict.equals(rtnValue))
            {
                job.log().message(LEX.getText("haystack.slot.conv.noChange", new Object[] {component.getSlotPath(), dict}));
            }
            else
            {
                job.log().message(LEX.getText("haystack.slot.conv.change", new Object[] {component.getSlotPath(), rtnValue}));
            }
        }
        return rtnValue;
    }

    private static void setAddTag(
        String tagName,
        BIDataValue value,
        BComponent component,
        BNHaystackService nhaystackService)
    {
        Tags tags = new ComponentTags(component);

        if (nhaystackService == null)
        {
            setAddTag(Id.newId(NAME_SPACE, tagName), value, tags);
            return;
        }

        if (!Sys.isStation())
        {
            nhaystackService.lease();
        }
        List<String> namespaces = nhaystackService.getPrioritizedNamespaceList();
        for (String namespace : namespaces)
        {
            Id id = Id.newId(namespace, tagName);
            if (tags.contains(id))
            {
                setAddTag(id, value, tags);
                return;
            }
        }

        // Not in any namespace
        setAddTag(Id.newId(namespaces.get(0), tagName), value, tags);
    }

    private static void setAddTag(Id id, BIDataValue value, Tags tags)
    {
        Optional<BIDataValue> existing = tags.get(id);
        if (!existing.isPresent() || !existing.get().equals(value))
        {
            tags.set(id, value);
        }
    }

    private static void replaceAddRelation(
        String tagName,
        BComponent endpoint,
        BComponent component,
        BNHaystackService nhaystackService)
    {
        if (nhaystackService == null)
        {
            replaceAddRelation(Id.newId(NAME_SPACE, tagName), endpoint, component);
            return;
        }

        if (!Sys.isStation())
        {
            nhaystackService.lease();
        }
        List<String> namespaces = nhaystackService.getPrioritizedNamespaceList();
        for (String namespace : namespaces)
        {
            Id id = Id.newId(namespace, tagName);
            if (new ComponentRelations(component).get(id).isPresent())
            {
                replaceAddRelation(id, endpoint, component);
                return;
            }
        }

        // Not in any namespace
        replaceAddRelation(Id.newId(namespaces.get(0), tagName), endpoint, component);
    }

    private static void replaceAddRelation(Id id, BComponent endpoint, BComponent component)
    {
        Relations relations = new ComponentRelations(component);
        List<BRelation> toRemove = new ArrayList<>();

        boolean exists = false;
        for (BRelation relation : component.getComponentRelations())
        {
            if (relation.getId().equals(id))
            {
                if (relation.isOutbound() && relation.getEndpoint().equals(endpoint))
                {
                    exists = true;
                }
                else
                {
                    toRemove.add(relation);
                }
            }
        }

        for (Relation relation : toRemove)
        {
            relations.remove(relation);
        }

        if (!exists)
        {
            // Relation needs to be added with an endpoint ord because, if
            // called from workbench, the component is only a proxy component
            // and will not be marshaled back correctly.
            relations.add(new BRelation(id, endpoint.getSlotPathOrd()));
        }
    }

    // convert geoLat and geoLon HDict tags and return
    // a HCoord.
    private static HCoord geoLatLonToGeoCoord(HDict dict)
    {
        try
        {
            final HVal lat = dict.get(GEO_LAT, false);
            final HVal lon = dict.get(GEO_LON, false);
            if (lat != null && lon != null)
            {
                return HCoord.make(((HNum)lat).val, ((HNum)lon).val);
            }
            else if (lat != null && lon == null)
            {
                return HCoord.make(((HNum)lat).val, 0.0);
            }
            else if (lat == null && lon != null)
            {
                return HCoord.make(0.0, ((HNum)lon).val);
            }
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    // migrate geoLon & geoLat niagara tags to a geoCoord tag.
    // if geoCoord tag already exists leave it in place and
    // remove the geoLon and geoLat tags.
    // return true if a geoCoord exist on exit.
    private static boolean geoLatLonToGeoCoord(BComponent comp)
    {
        try
        {
            final Tags tags = comp.tags();
            if (tags.get(ID_GEO_COORD).isPresent())
            {
                tags.removeAll(ID_GEO_LAT);
                tags.removeAll(ID_GEO_LON);
                return true;
            }

            HCoord coord = null;
            final Optional<BIDataValue> optGeoLat = tags.get(ID_GEO_LAT);
            final Optional<BIDataValue> optGeoLon = tags.get(ID_GEO_LON);
            double lat = optGeoLat.map(value -> ((BNumber) value).getDouble()).orElse(Double.NaN);
            double lon = optGeoLon.map(value -> ((BNumber) value).getDouble()).orElse(Double.NaN);
            if (optGeoLat.isPresent() && optGeoLon.isPresent())
            {
                coord = HCoord.make(lat, lon);
            }
            else if (optGeoLat.isPresent() && !optGeoLon.isPresent())
            {
                coord = HCoord.make(lat, 0.0);
            }
            else if (!optGeoLat.isPresent() && optGeoLon.isPresent())
            {
                coord = HCoord.make(0.0, lon);
            }

            if (coord != null)
            {
                tags.removeAll(ID_GEO_LAT);
                tags.removeAll(ID_GEO_LON);
                tags.set(new Tag(ID_GEO_COORD, BString.make(coord.toString())));
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    private static Set<String> getBlacklist()
    {
        return ListHolder.blacklist;
    }

    private static Set<String> getTagGroupList()
    {
        return ListHolder.tagGroupList;
    }

    private static final class ListHolder
    {
        public static final Set<String> blacklist;
        public static final Set<String> tagGroupList;

        static
        {
            Set<String> tempBlacklist = Collections.emptySet();
            Set<String> tempTagGroupList = Collections.emptySet();
            try
            {
                tempBlacklist = getTagListFromFile("blacklist.txt");
                tempTagGroupList = getTagListFromFile("tagGroups.txt");
            }
            catch (Exception ignored)
            {
            }

            blacklist = Collections.unmodifiableSet(tempBlacklist);
            tagGroupList = Collections.unmodifiableSet(tempTagGroupList);
        }

        /**
         * Given the location of a list of tag names, get a set of those strings.
         * Intended for use obtaining white and black lists of tags.
         */
        private static Set<String> getTagListFromFile(String fileName) throws Exception
        {
            BModule module = BNHaystackService.TYPE.getModule();
            BIFile dataDir = module.findFile(new FilePath("/nhaystack/res"));
            BIFile listFile = module.getChild(dataDir, fileName);
            if (listFile == null)
            {
                throw new Exception("Missing tag list file " + fileName);
            }

            try
            {
                Set<String> tagNames = new HashSet<>();
                Scanner scanner = new Scanner(listFile.getInputStream());
                while (scanner.hasNext())
                {
                    tagNames.add(scanner.next());
                }
                return tagNames;
            }
            catch (Exception e)
            {
                throw new Exception("Error loading tag list file " + fileName, e);
            }
        }
    }
}
