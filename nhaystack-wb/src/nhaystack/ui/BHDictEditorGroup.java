//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Jan 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations,
//                               added use of generics
//

package nhaystack.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import javax.baja.fox.BFoxProxySession;
import javax.baja.gx.BBrush;
import javax.baja.gx.BColor;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.nre.util.TextUtil;
import javax.baja.sys.BComponent;
import javax.baja.sys.BString;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BPane;
import javax.baja.ui.pane.BScrollPane;
import javax.baja.util.Lexicon;
import nhaystack.BHDict;
import nhaystack.BHRef;
import nhaystack.NHRef;
import nhaystack.server.BNHaystackService;
import nhaystack.server.TagManager;
import nhaystack.site.BHEquip;
import nhaystack.site.BHTagged;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HVal;

@NiagaraType
public class BHDictEditorGroup extends BScrollPane
{
  /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHDictEditorGroup(2979906276)1.0$ @*/
/* Generated Mon Nov 20 12:23:31 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHDictEditorGroup.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  public BHDictEditorGroup()
  {
  }

  public BHDictEditorGroup(BNHaystackService service, BComponent comp)
  {
    this.service = service;
    this.comp = comp;
    this.session = (BFoxProxySession) comp.getSession();

    HDict all = fetchTagsFromServer();

    Map<String, HVal> defaultEssentials = comp instanceof BHTagged ?
            asTagMap(((BHTagged) comp).getDefaultEssentials()) :
            new HashMap<>();

    Set<String> allPossibleAuto = new HashSet<>(Arrays.asList(fetchAutoGenTags()));

    // sites need 'tz' to show up in essentials.
    if (all.has("site")) allPossibleAuto.remove("tz");

    // points need 'siteRef' to show up in auto-gen.
    if (all.has("point")) allPossibleAuto.add("siteRef");

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
      if (optionalTags.containsKey("equipRef"))
        essentialTags.put("equipRef", optionalTags.remove("equipRef"));
      else
        essentialTags.put("equipRef", BHRef.DEFAULT.getRef());

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

    //////////////////////
    // widgets

    this.essentials = new BHDictEditor(this, BHDictEditor.ESSENTIALS, essentialTags);
    this.optional = new BHDictEditor(this, BHDictEditor.OPTIONAL, optionalTags);
    this.autoGen = new BHDictEditor(this, BHDictEditor.AUTO_GEN, autoGenTags);

    BGroupPane group = new BGroupPane(
            new String[]{
                    LEX.getText("essentials"),
                    LEX.getText("optional"),
                    LEX.getText("autoGen")},
            new BPane[]{
                    essentials,
                    optional,
                    autoGen});

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
    public BHDict getTags() { return tags; }

    /**
      * Returns null if the call to save() failed.
      */
    public String getZinc() { return zinc; }

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
            return ((BHDict) service.invoke(BNHaystackService.readById, id)).getDict();
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
            String tags = ((BString) service.invoke(
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

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    BComponent component() { return comp; }
    BFoxProxySession session() { return session; }
    BNHaystackService service() { return service; }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private static final String[] EMPTY_STRING_ARR = new String[0];

    private BComponent comp;
    private BFoxProxySession session;
    private BNHaystackService service;

    private BHDictEditor essentials;
    private BHDictEditor optional;
    private BHDictEditor autoGen;

    private BHDict tags;
    private String zinc;
}
