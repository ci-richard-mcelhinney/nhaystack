//
// Copyright (c) 2018 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 May 2018  Eric Anderson  Creation
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
import javax.baja.sys.BSimple;
import javax.baja.sys.Property;
import javax.baja.tag.Entity;
import javax.baja.tag.Id;
import javax.baja.tag.Relation;
import javax.baja.tag.Relations;
import javax.baja.tag.Tags;
import nhaystack.BHDict;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HRef;
import org.projecthaystack.HVal;
import com.tridium.sys.tag.ComponentRelations;

/**
 * @author Eric Anderson
 * @creation 5/14/2018
 * @since Niagara 4.6
 */
public final class HaystackSlotUtil
{
    // private constructor
    private HaystackSlotUtil()
    {
    }

    private static final Logger LOG = Logger.getLogger("nhaystack");

    public static void replaceHaystackSlot(BComponent component)
    {
        try
        {
            HDict dict = BHDict.findTagAnnotation(component);
            if (dict == null)
            {
                return;
            }
            replaceHaystackSlot(component, dict);
        }
        catch(Exception e)
        {
            LOG.log(Level.WARNING, e, () -> "Exception encountered replacing haystack slots within the station");
        }
    }

    private static void replaceHaystackSlot(BComponent component, HDict dict)
    {
        HDict newDictValue = refactorHaystackSlot(component, dict);
        if (newDictValue == HDict.EMPTY)
        {
            Property property = component.getProperty(BHDict.HAYSTACK_IDENTIFIER);
            if (property.isDynamic())
            {
                component.remove(BHDict.HAYSTACK_IDENTIFIER);
            }
        }
        else
        {
            component.set(BHDict.HAYSTACK_IDENTIFIER, BHDict.make(newDictValue));
        }
    }

    // this method is also called from BHDictFE when editing a BHDict slot.
    public static HDict refactorHaystackSlot(BComponent component, HDict dict)
    {
        HDictBuilder newValueBuilder = new HDictBuilder();
        ComponentRelations componentRelations = new ComponentRelations(component);

        // Existing relations with the same href id but pointing to the wrong endpoint.
        List<Relation> toRemove = new ArrayList<>();

        Set<String> blacklist = getBlacklist();
        Set<String> whitelist = getWhitelist();
        Set<String> tagGroupList = getTagGroupList();

        @SuppressWarnings("unchecked")
        Iterator<Entry<String, HVal>> tags = (Iterator<Map.Entry<String, HVal>>)dict.iterator();
        while (tags.hasNext())
        {
            Map.Entry<String, HVal> tag = tags.next();
            if (blacklist.contains(tag.getKey()))
            {
                newValueBuilder.add(tag.getKey(), tag.getValue());
            }
            else if (tagGroupList.contains(tag.getKey()))
            {
                // tagGroupId tags should be ignored
                // TODO NCCB-34662 Change handling of tag groups- do not include in the haystack field editor at all
                // and handle keeping or breaking it up here
                continue;
            }
            else if (!whitelist.contains(tag.getKey()))
            {
                LOG.warning("The entry " + tag.getKey() + " is not in the white or black lists; it will remain in the haystack slot; " + component.getSlotPath());
                newValueBuilder.add(tag.getKey(), tag.getValue());
            }
            else
            {
                // whitelisted items
                try
                {
                    if (tag.getValue() instanceof HRef)
                    {
                        // convert HRef to haystack relation in niagara
                        Id relationId = Id.newId("hs", tag.getKey());
                        HRef href = (HRef)tag.getValue();

                        // TODO NCCB-34661: Haystack refs that refer to an entity outside the station should be preserved in the haystack slot
                        BOrd refedOrd = TagManager.hrefToOrd(href);
                        if (refedOrd.isNull())
                        {
                            continue;
                        }

                        BComponent refedComp = refedOrd.get(component).asComponent();
                        boolean exists = false;
                        toRemove.clear();
                        for (Relation relation : componentRelations.getAll(relationId, Relations.OUT))
                        {
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

                        for (Relation relation : toRemove)
                        {
                            componentRelations.remove(relation);
                        }

                        if (!exists)
                        {
                            componentRelations.add(relationId, refedComp);
                        }
                    }
                    else
                    {
                        // convert HDict tags to niagara tags
                        BSimple simple = TypeUtil.toBajaSimple(tag.getValue());
                        if (simple instanceof BIDataValue)
                        {
                            Id tagId = Id.newId("hs", tag.getKey());
                            Tags niagaraTags = component.tags();
                            Optional<BIDataValue> niagaraTag = niagaraTags.get(tagId);
                            if (niagaraTag.isPresent())
                            {
                                BIDataValue tagValue = niagaraTag.get();
                                if (!simple.equals(tagValue))
                                {
                                    niagaraTags.set(tagId, (BIDataValue)simple);
                                }
                            }
                            else
                            {
                                niagaraTags.set(tagId, (BIDataValue)simple);
                            }
                        }
                        else
                        {
                            LOG.info("The tag " + tag.getKey() + " is on the white list but its value is not a BIDataValue; it will remain in the haystack slot; " + component.getSlotPath());
                            newValueBuilder.add(tag.getKey(), tag.getValue());
                        }
                    }
                }
                catch (Exception ise)
                {
                    LOG.log(Level.WARNING, ise, () -> "The tag " + tag.getKey() + " is on the white list but an exception was encountered; it will remain in the haystack slot; " + component.getSlotPath());
                    newValueBuilder.add(tag.getKey(), tag.getValue());
                }
            }
        }

        // TODO NCCB-34667: Remove outbound haystack relations that no longer have a corresponding href
//        // check for relations that have been removed
//        for (BRelation relation : relations)
//        {
//            if (relationMap.containsKey(relation.getName()))
//                continue;
//
//            if (!relation.getId().getDictionary().equals("hs"))
//                continue;
//
//            // TODO: Not sure it safe to actually remove the relation
//            // Assumes all haystack relations were also HRef HDict tags.
//            System.out.println("*** remove relation candidate: " + relation.getId() + "-->" /*+ ((BComponent)relation.getEndpoint()).getSlotPath()*/);
//        }

        // Don't call isEmpty on HDictBuilder: if the internal map is
        // null, it throws a null pointer exception
        return newValueBuilder.toDict();
    }

    private static final class ListHolder
    {
        public static final Set<String> whitelist;
        public static final Set<String> blacklist;
        public static final Set<String> tagGroupList;

        static
        {
            Set<String> tempWhitelist = null;
            Set<String> tempBlacklist = null;
            Set<String> tempTagGroupList = null;
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
