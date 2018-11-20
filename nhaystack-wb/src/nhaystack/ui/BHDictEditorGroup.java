//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Jan 2013  Mike Jarmy       Creation
//   10 May 2018  Eric Anderson    Migrated to slot annotations, added missing @Overrides annotations,
//                                 added use of generics
//   26 Sep 2018  Andrew Saunders  Managing interaction with Niagara Haystack tags
//

package nhaystack.ui;

import java.util.*;
import java.util.Map.Entry;
import javax.baja.data.BIDataValue;
import javax.baja.fox.BFoxProxySession;
import javax.baja.gx.BBrush;
import javax.baja.gx.BColor;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.nre.util.TextUtil;
import javax.baja.sys.BComponent;
import javax.baja.sys.BMarker;
import javax.baja.sys.BRelation;
import javax.baja.sys.BString;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Entity;
import javax.baja.tag.Tag;
import javax.baja.tag.TagDictionaryService;
import javax.baja.tag.TagInfo;
import javax.baja.tag.util.ImpliedTags;
import javax.baja.tagdictionary.BTagGroupInfo;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BPane;
import javax.baja.ui.pane.BScrollPane;
import javax.baja.util.Lexicon;
import nhaystack.BHDict;
import nhaystack.BHRef;
import nhaystack.NHRef;
import nhaystack.util.NHaystackConst;
import nhaystack.server.BNHaystackService;
import nhaystack.server.TagManager;
import nhaystack.site.BHEquip;
import nhaystack.site.BHTagged;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HVal;
import com.tridium.tagdictionary.BNiagaraTagDictionary;

@NiagaraType
public class BHDictEditorGroup extends BScrollPane implements NHaystackConst
{
  /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHDictEditorGroup(2979906276)1.0$ @*/
/* Generated Mon Nov 20 12:23:31 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

    @Override
    public Type getType()
    {
        return TYPE;
    }

    public static final Type TYPE = Sys.loadType(BHDictEditorGroup.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHDictEditorGroup()
    {
    }

    public BHDictEditorGroup(BNHaystackService service, BComponent comp)
    {
        this.service = service;
        this.comp = comp;
        this.session = (BFoxProxySession)comp.getSession();
        this.tdService = comp.getTagDictionaryService();
        this.slotsToRemove = new ArrayList<>();

        if (tdService != null)
        {
            ((BComponent)tdService).lease(Integer.MAX_VALUE);

            // Get a list of haystack tagGroup relations
            this.hsTagGroupRelations = getHsTagGroupRelations(component());
            // Gather the implied name tags for direct tag groups- these are
            // not included in any sections
            this.impliedTagGroupNames = getImpliedTagGroupTags(hsTagGroupRelations, true);
            // Gather the tags implied by direct tag groups- these are included
            // in the optional section
            this.impliedTagGroupTags = getImpliedTagGroupTags(hsTagGroupRelations, false);
        }
        else
        {
            this.impliedTagGroupNames = Collections.emptyMap();
            this.impliedTagGroupTags = Collections.emptyMap();
        }
        HDict all = fetchTagsFromServer();

        Map<String, HVal> defaultEssentials = comp instanceof BHTagged ?
            asTagMap(((BHTagged)comp).getDefaultEssentials()) :
            new HashMap<>();

        Set<String> allPossibleAuto = new HashSet<>(Arrays.asList(fetchAutoGenTags()));

        // sites need 'tz' to show up in essentials.
        if (all.has("site")) allPossibleAuto.remove("tz");

        // points need 'siteRef' to show up in auto-gen.
        if (all.has("point")) allPossibleAuto.add(SITE_REF);

        //////////////////////
        // essentials

        Map<String, HVal> essentialTags = asTagMap(all);
        essentialTags.keySet().removeAll(allPossibleAuto);
        essentialTags.keySet().retainAll(defaultEssentials.keySet());

        for (Entry<String, HVal> entry : defaultEssentials.entrySet())
        {
            String name = entry.getKey();
            HVal val = entry.getValue();
            if (!essentialTags.containsKey(name))
                essentialTags.put(name, val);
        }

        //////////////////////
        // optional

        Map<String, HVal> optionalTags = asTagMap(all);
        optionalTags.keySet().removeAll(allPossibleAuto);
        optionalTags.keySet().removeAll(defaultEssentials.keySet());

        // points need 'equipRef' and 'schedulable' to show up in essentials.
        if (all.has("point"))
        {
            if (optionalTags.containsKey(EQUIP_REF))
                essentialTags.put(EQUIP_REF, optionalTags.remove(EQUIP_REF));
            else
                essentialTags.put(EQUIP_REF, BHRef.DEFAULT.getRef());

//            if (optionalTags.containsKey("schedulable"))
//                essentialTags.put("schedulable", optionalTags.remove("schedulable"));
//            else
//                essentialTags.put("schedulable", HNum.make(BHSchedulable.DEFAULT.getPriority()));
        }

        //////////////////////
        // autoGen

        Map<String, HVal> autoGenTags = asTagMap(all);
        autoGenTags.keySet().retainAll(allPossibleAuto);

        // move 'equip' to optional if its not really a BHEquip
        if (autoGenTags.containsKey("equip") && !(comp instanceof BHEquip))
            optionalTags.put("equip", autoGenTags.remove("equip"));

        // Add tags implied by smart tag dictionaries
        if (tdService != null)
        {
            // Get the set of implied tags for this component
            final ImpliedTags impTags = new ImpliedTags(tdService, comp);

            for (Tag impTag : impTags)
            {
                // Skip implied tags without the "hs" namespace (may be implied
                // by Niagara tag dictionary, for example)
                if (!impTag.getId().getDictionary().equals("hs"))
                    continue;

                String tagName = impTag.getId().getName();

                // Skip tags already in the autoGenTag map
                if (autoGenTags.containsKey(tagName))
                    continue;

                // Skip tags implied by a Niagara tag group- these are added to
                // the optional section
                if (impliedTagGroupTags.containsKey(tagName))
                    continue;

                final BIDataValue tagValue = impTag.getValue();
                try
                {
                    HVal hVal = TypeUtil.fromBajaDataValue(tagValue);
                    if (hVal != null &&
                        !essentialTags.containsKey(tagName) &&
                        !optionalTags.containsKey(tagName))
                    {
                        autoGenTags.put(tagName, hVal);
                    }
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        }



        //////////////////////
        // widgets

        this.essentials = new BHDictEditor(this, BHDictEditor.ESSENTIALS, essentialTags);
        this.optional = new BHDictEditor(this, BHDictEditor.OPTIONAL, optionalTags);
        this.autoGen = new BHDictEditor(this, BHDictEditor.AUTO_GEN, autoGenTags);

        BGroupPane group = new BGroupPane(
            new String[] {
                LEX.getText("essentials"),
                LEX.getText("optional"),
                LEX.getText("autoGen") },
            new BPane[] {
                essentials,
                optional,
                autoGen });

        setViewportBackground(BBrush.makeSolid(BColor.make("#CCCCCC")));
        setContent(new BBorderPane(group));
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    public void save() throws Exception
    {
        essentials.save();
        optional.save();

        HDictBuilder db = new HDictBuilder();
        db.add(essentials.getTags().getDict());
        db.add(optional.getTags().getDict());

        this.tags = BHDict.make(db.toDict());
        this.zinc = tags.getDict().toZinc();
    }

    /**
     * Returns null if the call to save() failed.
     */
    public BHDict getTags()
    {
        return tags;
    }

    /**
     * Returns null if the call to save() failed.
     */
    public String getZinc()
    {
        return zinc;
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    /**
     * Fetch all the tags that were generated for the current component.
     */
    private HDict fetchTagsFromServer()
    {
        try
        {
            NHRef ref = TagManager.makeSlotPathRef(comp);
            BHRef id = BHRef.make(ref.getHRef());
            return ((BHDict)service.invoke(BNHaystackService.readById, id)).getDict();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return HDict.EMPTY;
        }
    }

    /**
     * Fetch the list of any tag that could be auto generated
     */
    private String[] fetchAutoGenTags()
    {
        try
        {
            String tags = ((BString)service.invoke(
                BNHaystackService.fetchAutoGenTags, null)).getString();
            return TextUtil.split(tags, ',');
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return EMPTY_STRING_ARR;
        }
    }

    /**
     * Convert an HDict into a TreeMap<String,HVal>
     */
    private static Map<String, HVal> asTagMap(HDict dict)
    {
        Map<String, HVal> tagMap = new TreeMap<>();
        Iterator<Map.Entry<String, HVal>> it = dict.iterator();
        while (it.hasNext())
        {
            Map.Entry<String, HVal> entry = it.next();
            String name = entry.getKey();
            HVal val = entry.getValue();
            tagMap.put(name, val);
        }
        return tagMap;
    }

    static Map<String, HVal> getImpliedTagGroupTags(ArrayList<BRelation> relations, boolean groupNameOnly)
    {
        Map<String, HVal> tgTags = new TreeMap<>();
        for (BRelation relation : relations)
        {
            final Entity ep = relation.getEndpoint();
            BTagGroupInfo endPoint = (BTagGroupInfo)ep;
            if (groupNameOnly)
                tgTags.put(endPoint.getName(), TypeUtil.fromBajaDataValue(BMarker.MARKER));
            else
            {
                final Iterator<TagInfo> iterator = endPoint.getTags();
                while (iterator.hasNext())
                {
                    final TagInfo tagInfo = iterator.next();
                    tgTags.put(tagInfo.getName(), TypeUtil.fromBajaDataValue(tagInfo.getDefaultValue()));
                }
            }
        }
        return tgTags;
    }

    static ArrayList<BRelation> getHsTagGroupRelations(BComponent comp)
    {
        ArrayList<BRelation> tgRelations = new ArrayList<>();
        final BRelation[] relations = comp.getComponentRelations();
        for (BRelation relation : relations)
        {
            if (relation.getId().equals(BNiagaraTagDictionary.TAG_GROUP_RELATION))
            {
                final Entity ep = relation.getEndpoint();
                if (ep != null && (ep instanceof BTagGroupInfo))
                {
                    BTagGroupInfo endPoint = (BTagGroupInfo)ep;
                    //make sure it is a haystack group.
                    if(endPoint.getGroupId().getDictionary().equals("hs"))
                    {
                        tgRelations.add(relation);
                    }
                }
            }
        }
        return tgRelations;
    }


////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    BComponent component()
    {
        return comp;
    }

    BFoxProxySession session()
    {
        return session;
    }

    BNHaystackService service()
    {
        return service;
    }

    TagDictionaryService tdService()
    {
        return tdService;
    }

    Map<String, HVal> impliedTagGroupNames()
    {
        return impliedTagGroupNames;
    }

    Map<String, HVal> impliedTagGroupTags()
    {
        return impliedTagGroupTags;
    }

    ArrayList<BRelation> hsTagGroupRelations() { return hsTagGroupRelations; }

    public List<String> slotsToRemove()
    {
        return slotsToRemove;
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private static final String[] EMPTY_STRING_ARR = new String[0];

    private BComponent comp;
    private BFoxProxySession session;
    private BNHaystackService service;
    private TagDictionaryService tdService;

    private BHDictEditor essentials;
    private BHDictEditor optional;
    private BHDictEditor autoGen;

    private ArrayList<BRelation> hsTagGroupRelations;
    private Map<String, HVal> impliedTagGroupNames;
    private Map<String, HVal> impliedTagGroupTags;

    private List<String> slotsToRemove;

    private BHDict tags;
    private String zinc;
}
