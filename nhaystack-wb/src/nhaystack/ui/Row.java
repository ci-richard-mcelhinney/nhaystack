//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Feb 2013  Mike Jarmy       Creation
//   10 May 2018  Eric Anderson    Added use of generics
//   26 Sep 2018  Andrew Saunders  Managing interaction with Niagara Haystack tags
//

package nhaystack.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.baja.fox.BFoxProxySession;
import javax.baja.naming.BOrd;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BString;
import javax.baja.ui.BDropDown;
import javax.baja.ui.BListDropDown;
import javax.baja.ui.BTextDropDown;
import javax.baja.ui.list.BList;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;
import nhaystack.BHDict;
import nhaystack.BHFloor;
import nhaystack.BHNum;
import nhaystack.BHRef;
import nhaystack.BHTimeZone;
import nhaystack.BHUnit;
import nhaystack.NHRef;
import nhaystack.util.NHaystackConst;
import nhaystack.res.Resources;
import nhaystack.server.BNHaystackService;
import nhaystack.util.SlotUtil;
import org.projecthaystack.HBool;
import org.projecthaystack.HCoord;
import org.projecthaystack.HDict;
import org.projecthaystack.HMarker;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HStr;
import org.projecthaystack.HTimeZone;
import org.projecthaystack.HUri;
import org.projecthaystack.HVal;
import org.projecthaystack.util.Base64;

class Row implements NHaystackConst
{
    Row(BHDictEditor editor, String name, HVal val, boolean isDirectTag, boolean isNew)
    {
        this(editor, name, val, isDirectTag, isNew, null);
    }

    Row(BHDictEditor editor, String name, HVal val, boolean isDirectTag, boolean isNew, String relationSlot)
    {
        String kind = makeKind(val);
        this.isDirectTag = isDirectTag;
        this.isNew = isNew;
        this.isUnresolvedRef = false;
        this.relationSlot = relationSlot;
        this.kinds = new BListDropDown();
        populateKinds(kind, kinds);
        kinds.setSelectedItem(kind);
        editor.linkTo(kinds, BDropDown.valueModified, BHDictEditor.kindsModified);  

        this.names = new BTextDropDown();
        populateNames(kind);
        names.setText(name);
        editor.linkTo(names, BDropDown.valueModified, BHDictEditor.namesModified);  

        this.fe = makeValueFE(editor, name, val, this);
        editor.linkTo(fe, BWbFieldEditor.pluginModified, BHDictEditor.valueModified);

        switch(editor.editorType())
        {
            case BHDictEditor.ESSENTIALS:
                kinds.setEnabled(false);
                names.setEnabled(false);
                break;
            case BHDictEditor.AUTO_GEN:
                kinds.setEnabled(false);
                names.setEnabled(false);
                fe.setReadonly(true);
                break;
        }
        if(!isDirectTag) // if implied (tagGroup tag) disable editors
        {
            kinds.setEnabled(false);
            names.setEnabled(false);
            fe.setReadonly(kinds.getSelectedItem().equals("Marker"));
        }
    }

////////////////////////////////////////////////////////////////
// package scope
////////////////////////////////////////////////////////////////

    void populateNames(String kind)
    {
        BList list = names.getList();
        list.removeAllItems();
        List<String> tags = new ArrayList<>(Arrays.asList(Resources.getKindTags(kind)));

        // smuggle navNameFormat into the dropdown
        if (kind.equals("Str"))
        {
            tags.add("navNameFormat");
            Collections.sort(tags);
        }

        for (String tag : tags)
        {
            if (tag.equals("id")) continue;
            if (tag.equals(SITE_REF)) continue;
            if (tag.equals(EQUIP_REF)) continue;

            list.addItem(tag);
        }
    }

    static BWbFieldEditor initValueFE(String kind, boolean isUnresolvedRef)
    {
        switch (kind)
        {
            case "Marker":
            {
                BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
                fe.loadValue(BString.DEFAULT);
                fe.setReadonly(true);
                return fe;
            }
            case "Number":
            {
                BWbFieldEditor fe = new BHNumFE();
                fe.loadValue(BHNum.DEFAULT);
                return fe;
            }
            case "Str":
            {
                BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
                fe.loadValue(BString.DEFAULT);
                return fe;
            }
            case "Ref":
            {
                // because we removed siteRef and equipRef from the names dropdown,
                // its OK to just use the default ord FE
                BWbFieldEditor fe = new BRefOrdFE(isUnresolvedRef);
                fe.loadValue(BOrd.DEFAULT);
                return fe;
            }
            case "Bool":
            {
                BWbFieldEditor fe = BWbFieldEditor.makeFor(BBoolean.DEFAULT);
                fe.loadValue(BBoolean.DEFAULT);
                return fe;
            }
            default:
                throw new IllegalStateException();
        }
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private static String makeKind(HVal val)
    {
        if      (val instanceof HMarker) return "Marker";
        else if (val instanceof HNum)    return "Number";
        else if (val instanceof HStr)    return "Str";
        else if (val instanceof HRef)    return "Ref";
        else if (val instanceof HBool)   return "Bool";
        else if (val instanceof HUri)    return "Str"; //"Uri";
        else if (val instanceof HCoord)  return "Str";
//        else if (val instanceof HBin)      return "Bin";
//        else if (val instanceof HDate)     return "Date";
//        else if (val instanceof HTime)     return "Time";
//        else if (val instanceof HDateTime) return "DateTime";
        else throw new IllegalStateException();
    }

    private static void populateKinds(String kind, BListDropDown kinds)
    {
        BList list = kinds.getList();
        list.addItem("Marker");
        list.addItem("Number");
        list.addItem("Str");
        list.addItem("Ref");
        list.addItem("Bool");
//        list.addItem("Uri");
//        list.addItem("Bin");
//        list.addItem("Date");
//        list.addItem("Time");
//        list.addItem("DateTime");
        list.setSelectedItem(kind);
    }

    private static BWbFieldEditor makeValueFE(
        BHDictEditor editor, String name, HVal val, Row row)
    {
        if (val instanceof HMarker) 
            return makeMarkerFE();

        else if (val instanceof HNum)
            return makeNumFE(name, (HNum) val);

        else if (val instanceof HStr)
            return makeStrFE(name, (HStr) val);

        else if (val instanceof HRef)
            return makeRefFE(editor, name, (HRef) val, row.isUnresolvedRef);

        else if (val instanceof HBool)
            return makeBoolFE((HBool) val);

        else if (val instanceof HCoord)
            return makeCoordFE((HCoord)val);

        else throw new IllegalStateException();
    }

    private static BWbFieldEditor makeCoordFE(HCoord coord)
    {
        BWbFieldEditor fe = new BHCoordFE();
        fe.loadValue(BString.make(coord.toString()));
        return fe;
    }

    private static BWbFieldEditor makeMarkerFE()
    {
        BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
        fe.loadValue(BString.DEFAULT);
        fe.setReadonly(true);
        return fe;
    }

    private static BWbFieldEditor makeNumFE(String name, HNum num)
    {
        BWbFieldEditor fe = new BHNumFE();
        fe.loadValue(BHNum.make(num));
        return fe;
    }

    private static BWbFieldEditor makeStrFE(String name, HStr str)
    {
        switch (name)
        {
            case "tz":
            {
                BWbFieldEditor fe = new BHTimeZoneFE();
                fe.loadValue(BHTimeZone.make(HTimeZone.make(str.val)));
                return fe;
            }
            case "unit":
            {
                BWbFieldEditor fe = new BHUnitFE();
                fe.loadValue(BHUnit.make(Resources.getSymbolUnit(str.val).symbol));
                return fe;
            }
            case "floorName":
            {
                BWbFieldEditor fe = new BHFloorFE();
                fe.loadValue(BHFloor.make(str.val));
                return fe;
            }
            default:
            {
                BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
                fe.loadValue(BString.make(str.val));

                if (name.equals("weeklySchedule"))
                    fe.setReadonly(true);

                return fe;
            }
        }
    }

    private static BWbFieldEditor makeRefFE(
        BHDictEditor editor, String name, HRef ref, boolean isUnresolvedRef)
    {
        BFoxProxySession session = editor.group().session();

        // create ord
        BOrd ord = BOrd.DEFAULT;

        final BHRef bhRef = BHRef.make(ref);
        if (!bhRef.equals(BHRef.DEFAULT))
        {
            HDict dict = null;
            try
            {
                dict = ((BHDict)editor.group().service().invoke(
                    BNHaystackService.readById, bhRef)).getDict();
            }
            catch(Exception e)
            {
                final NHRef nhRef = NHRef.make(ref);
                ord = BOrd.make("station:|" +
                    (nhRef.getSpace().equals(NHRef.COMP) ?
                        "slot:" + SlotUtil.toNiagara(nhRef.getPath()) :
                        Base64.URI.decodeUTF8(nhRef.getPath())));
            }
            if(dict != null)
            {
                // workaround for weird problem with n4SlotPath
                if (dict.has("n4SlotPath"))
                    ord = BOrd.make("station:|" + dict.getStr("n4SlotPath"));
                else
                    System.out.println("ERROR: " + dict.toZinc() + " does not have n4SlotPath.");
            }
        }

        // create field editor
        BWbFieldEditor fe;
        switch (name)
        {
        case SITE_REF:
            fe = new BSiteRefFE(editor.group());
            break;
        case EQUIP_REF:
            fe = new BEquipRefFE(editor.group());
            break;
        default:
            fe = new BRefOrdFE(isUnresolvedRef);
            break;
        }

        // load
        fe.loadValue(ord);
        return fe;
    }

    private static BWbFieldEditor makeBoolFE(HBool bool)
    {
        BWbFieldEditor fe = BWbFieldEditor.makeFor(BBoolean.DEFAULT);
        fe.loadValue(bool.val ? BBoolean.TRUE : BBoolean.FALSE);
        return fe;
    }

////////////////////////////////////////////////////////////////
// Attribs
////////////////////////////////////////////////////////////////

    final BListDropDown kinds;
    final BTextDropDown names;
    BWbFieldEditor fe;
    boolean isDirectTag;
    final boolean isNew;
    boolean isUnresolvedRef;
    String relationSlot;
}
