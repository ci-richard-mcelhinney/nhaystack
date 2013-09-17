//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Jan 2013  Mike Jarmy Creation
//

package nhaystack.ui;

import java.util.*;

import javax.baja.fox.*;
import javax.baja.gx.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.ui.pane.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import nhaystack.*;
import nhaystack.server.*;
import nhaystack.site.*;

public class BHDictEditorGroup extends BScrollPane
{
    /*-
    class BHDictEditorGroup
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHDictEditorGroup(2327187967)1.0$ @*/
/* Generated Sat Feb 09 10:19:16 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHDictEditorGroup.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHDictEditorGroup() {}

    public BHDictEditorGroup(BNHaystackService service, BComponent comp)
    {
        this.service = service;
        this.comp = comp;
        this.session = (BFoxProxySession) comp.getSession();

        HDict all = fetchTagsFromServer();

        Map defaultEssentials = (comp instanceof BHTagged) ?
            asTagMap(((BHTagged) comp).getDefaultEssentials()) :
            new HashMap();

        Set allPossibleAuto = new HashSet(Arrays.asList(fetchAutoGenTags()));

        // sites need 'tz' to show up in essentials.
        if (all.has("site")) allPossibleAuto.remove("tz");

        // points need 'siteRef' to show up in auto-gen.
        if (all.has("point")) allPossibleAuto.add("siteRef");

        //////////////////////
        // essentials

        Map essentialTags = asTagMap(all);
        essentialTags.keySet().removeAll(allPossibleAuto);
        essentialTags.keySet().retainAll(defaultEssentials.keySet());

        Iterator it = defaultEssentials.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            HVal val = (HVal) entry.getValue();
            if (!essentialTags.containsKey(name))
                essentialTags.put(name, val);
        }

        //////////////////////
        // optional

        Map optionalTags = asTagMap(all);
        optionalTags.keySet().removeAll(allPossibleAuto);
        optionalTags.keySet().removeAll(defaultEssentials.keySet());

        // points need 'equipRef' to show up in essentials.
        if (all.has("point"))
        {
            if (optionalTags.containsKey("equipRef"))
                essentialTags.put("equipRef", optionalTags.remove("equipRef"));
            else
                essentialTags.put("equipRef", BHRef.DEFAULT.getRef());
        }

        //////////////////////
        // autoGen

        Map autoGenTags = asTagMap(all);
        autoGenTags.keySet().retainAll(allPossibleAuto);

        // move 'equip' to optional if its not really a BHEquip
        if (autoGenTags.containsKey("equip") && !(comp instanceof BHEquip))
            optionalTags.put("equip", autoGenTags.remove("equip"));

        //////////////////////
        // widgets

        this.essentials = new BHDictEditor(this, BHDictEditor.ESSENTIALS, essentialTags);
        this.optional   = new BHDictEditor(this, BHDictEditor.OPTIONAL,   optionalTags);
        this.autoGen    = new BHDictEditor(this, BHDictEditor.AUTO_GEN,   autoGenTags);

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
            NHRef ref = NHServer.makeSlotPathRef(comp);
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
            return new String[0];
        }
    }

    /**
      * Convert an HDict into a TreeMap<String,HVal>
      */
    private static Map asTagMap(HDict dict)
    {
        Map tagMap = new TreeMap();
        Iterator it = dict.iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            HVal val = (HVal) entry.getValue();
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

    private BComponent comp;
    private BFoxProxySession session;
    private BNHaystackService service;

    private BHDictEditor essentials;
    private BHDictEditor optional;
    private BHDictEditor autoGen;

    private BHDict tags = null;
    private String zinc = null;
}
