//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Feb 2013  Mike Jarmy Creation
//

package nhaystack.ui;

import java.util.*;

import javax.baja.fox.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.list.*;
import javax.baja.util.*;
import javax.baja.workbench.fieldeditor.*;

import org.projecthaystack.*;
import nhaystack.*;
import nhaystack.res.*;
import nhaystack.server.*;

class Row
{
    Row(BHDictEditor editor, String name, HVal val)
    {
        String kind = makeKind(val);

        this.kinds = new BListDropDown();
        populateKinds(kind, kinds);
        kinds.setSelectedItem(kind);
        editor.linkTo(kinds, BDropDown.valueModified, BHDictEditor.kindsModified);  

        this.names = new BTextDropDown();
        populateNames(kind);
        names.setText(name);
        editor.linkTo(names, BDropDown.valueModified, BHDictEditor.namesModified);  

        this.fe = makeValueFE(editor, name, val);

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
    }

////////////////////////////////////////////////////////////////
// package scope
////////////////////////////////////////////////////////////////

    void populateNames(String kind)
    {
        BList list = names.getList();
        list.removeAllItems();
        String[] tags = Resources.getKindTags(kind);

        // smuggle navNameFormat into the dropdown
        if (kind.equals("Str"))
        {
            Array arr = new Array(tags);
            arr.add("navNameFormat");
            tags = (String[]) arr.sort().trim();
        }

        for (int i = 0; i < tags.length; i++)
        {
            if (tags[i].equals("id")) continue;
            if (tags[i].equals("siteRef")) continue;
            if (tags[i].equals("equipRef")) continue;

            list.addItem(tags[i]);
        }
    }

    static BWbFieldEditor initValueFE(String kind)
    {
        if (kind.equals("Marker"))
        {
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
            fe.loadValue(BString.DEFAULT);
            fe.setReadonly(true);
            return fe;
        }
        else if (kind.equals("Number"))
        {
            BWbFieldEditor fe = new BHNumFE();
            fe.loadValue(BHNum.DEFAULT);
            return fe;
        }
        else if (kind.equals("Str"))
        {
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
            fe.loadValue(BString.DEFAULT);
            return fe;
        }
        else if (kind.equals("Ref"))
        {
            // because we removed siteRef and equipRef from the names dropdown,
            // its OK to just use the default ord FE
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BOrd.DEFAULT);
            fe.loadValue(BOrd.DEFAULT);
            return fe;
        }
        else if (kind.equals("Bool"))
        {
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BBoolean.DEFAULT);
            fe.loadValue(BBoolean.DEFAULT);
            return fe;
        }
        else throw new IllegalStateException();
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
        else if (val instanceof HUri)      return "Str"; //"Uri";
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
        BHDictEditor editor, String name, HVal val)
    {
        if (val instanceof HMarker) 
            return makeMarkerFE();

        else if (val instanceof HNum)
            return makeNumFE((HNum) val);

        else if (val instanceof HStr)
            return makeStrFE(name, (HStr) val);

        else if (val instanceof HRef)
            return makeRefFE(editor, name, (HRef) val);

        else if (val instanceof HBool)
            return makeBoolFE((HBool) val);

        else throw new IllegalStateException();
    }

    private static BWbFieldEditor makeMarkerFE()
    {
        BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
        fe.loadValue(BString.DEFAULT);
        fe.setReadonly(true);
        return fe;
    }

    private static BWbFieldEditor makeNumFE(HNum num)
    {
        BWbFieldEditor fe = new BHNumFE();
        fe.loadValue(BHNum.make(num));
        return fe;
    }

    private static BWbFieldEditor makeStrFE(String name, HStr str)
    {
        if (name.equals("tz"))
        {
            BWbFieldEditor fe = new BHTimeZoneFE();
            fe.loadValue(BHTimeZone.make(HTimeZone.make(str.val)));
            return fe;
        }
        else if (name.equals("unit"))
        {
            BWbFieldEditor fe = new BHUnitFE();
            fe.loadValue(BHUnit.make(Resources.getSymbolUnit(str.val).symbol));
            return fe;
        }
        else
        {
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
            fe.loadValue(BString.make(str.val));
            return fe;
        }
    }

    private static BWbFieldEditor makeRefFE(
        BHDictEditor editor, String name, HRef ref)
    {
        BFoxProxySession session = editor.group().session();

        // create ord
        BOrd ord = BOrd.DEFAULT;

        if (!(BHRef.make(ref).equals(BHRef.DEFAULT)))
        {
            HDict dict = ((BHDict) editor.group().service().invoke(
                BNHaystackService.readById, BHRef.make(ref))).getDict();

            ord = BOrd.make("station:|" + dict.getStr("axSlotPath"));
        }

        // create field editor
        BWbFieldEditor fe = null;
        if (name.equals("siteRef"))
        {
            fe = new BSiteRefFE(editor.group());
        }
        else if (name.equals("equipRef"))
        {
            fe = new BEquipRefFE(editor.group());
        }
        else
        {
            fe = BWbFieldEditor.makeFor(BOrd.DEFAULT);
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

    BListDropDown kinds;
    BTextDropDown names;
    BWbFieldEditor fe;
}
