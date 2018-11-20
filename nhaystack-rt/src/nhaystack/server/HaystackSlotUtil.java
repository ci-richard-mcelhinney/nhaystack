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
import javax.baja.tag.Entity;
import javax.baja.tag.Id;
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
            migrateHaystackTags(component, dict, job);
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

    private static void migrateHaystackTags(BComponent component, HDict dict, BNHaystackConvertHaystackSlotsJob job)
    {
        HDict newDictValue = refactorHaystackSlot(component, dict, job);
        component.set(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(newDictValue));
    }

    // this method is also called from BHDictFE when editing a BHDict slot.
    public static HDict refactorHaystackSlot(BComponent component, HDict dict)
    {
        return refactorHaystackSlot(component, dict, null);
    }

    private static HDict refactorHaystackSlot(BComponent component, HDict dict, BNHaystackConvertHaystackSlotsJob job)
    {
        if (job != null)
        {
            job.log().message(LEX.getText("haystack.slot.conv.current", new Object[] {component.getSlotPath(), dict}));
            job.incCount();
        }

        HDictBuilder newValueBuilder = new HDictBuilder();
        ComponentRelations componentRelations = new ComponentRelations(component);

        // Existing relations with the same href id but pointing to the wrong endpoint.
        List<BRelation> toRemove = new ArrayList<>();

        Set<String> blacklist = getBlacklist();
        Set<String> whitelist = getWhitelist();
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
                    setAddTag(GEO_COORD, BString.make(coord.toString()), component);
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
            else if (!whitelist.contains(tagName))
            {
                String logMsg = LEX.getText("haystack.slot.conv.notInList", new Object[]  { component.getSlotPath(), tagName } );
                if (job != null)
                {
                    job.log().message(logMsg);
                    job.incWarningCount();
                }
                else
                {
                    LOG.warning(logMsg);
                }

                newValueBuilder.add(tagName, tag.getValue());
            }
            else
            {
                // whitelisted items
                try
                {
                    if (tag.getValue() instanceof HRef)
                    {
                        // convert HRef to haystack relation in niagara
                        Id relationId = Id.newId(NAME_SPACE, tagName);
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
                        boolean exists = false;
                        toRemove.clear();
                        for (BRelation relation : component.getComponentRelations())
                        {
                            if (!relation.getId().equals(relationId))
                            {
                                continue;
                            }
                                
                            Entity endpoint = relation.getEndpoint();
                            if (endpoint.equals(refedComp))
                            {
                                exists = true;
                            }
                            else
                            {
                                toRemove.add(relation);
                            }
                        }

                        for (BRelation relation : toRemove)
                        {
                            componentRelations.remove(relation);
                        }

                        if (!exists)
                        {
                            // Relation needs to be added with an endpoint ord because, if called
                            // from workbench, the component is only a proxy component and will not
                            // be marshaled back correctly.
                            componentRelations.add(new BRelation(relationId, refedComp.getSlotPathOrd()));
                        }
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
                            setAddTag(tagName, (BIDataValue)simple, component);
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

    private static void setAddTag(String tagName, BIDataValue value, BComponent component )
    {
        Id tagId = Id.newId(NAME_SPACE, tagName);
        Tags tags = component.tags();
        Optional<BIDataValue> existingValue = tags.get(tagId);
        if (!existingValue.isPresent() || !existingValue.get().equals(value))
        {
            component.tags().set(tagId, value);
        }
    }

    // convert geoLat and geoLon HDict tags and return
    // a HCoord.
    private static HCoord geoLatLonToGeoCoord(HDict dict)
    {
        HCoord coord = null;
        try
        {
            final HVal lat = dict.get(GEO_LAT, false);
            final HVal lon = dict.get(GEO_LON, false);
            if (lat != null && lon != null)
            {
                coord = HCoord.make(((HNum)lat).val, ((HNum)lon).val);
            }
            else if (lat != null && lon == null)
            {
                coord = HCoord.make(((HNum)lat).val, 0.0);
            }
            else if (lat == null && lon != null)
            {
                coord = HCoord.make(0.0, ((HNum)lon).val);
            }
        }
        catch (Exception ignore) {}
        return coord;
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

    private static Set<String> getWhitelist()
    {
        return ListHolder.whitelist;
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
        public static final Set<String> whitelist;
        public static final Set<String> blacklist;
        public static final Set<String> tagGroupList;

        static
        {
            Set<String> tempWhitelist = Collections.emptySet();
            Set<String> tempBlacklist = Collections.emptySet();
            Set<String> tempTagGroupList = Collections.emptySet();
            try
            {
                tempWhitelist = getTagListFromFile("whitelist.txt");
                tempBlacklist = getTagListFromFile("blacklist.txt");
                tempTagGroupList = getTagListFromFile("tagGroups.txt");
            }
            catch (Exception ignored)
            {
            }

            whitelist = Collections.unmodifiableSet(tempWhitelist);
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

            Set<String> tagNames = new HashSet<>();
            try
            {
                Scanner scanner = new Scanner(listFile.getInputStream());
                while (scanner.hasNext())
                {
                    tagNames.add(scanner.next());
                }
            }
            catch (Exception e)
            {
                throw new Exception("Error loading tag list file " + fileName, e);
            }
            return tagNames;
        }
    }
}
