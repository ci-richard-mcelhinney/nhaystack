//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Feb 2013  Mike Jarmy Creation
//

package nhaystack.ui;

import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.list.*;
import javax.baja.workbench.fieldeditor.*;

import haystack.*;
import nhaystack.*;
import nhaystack.res.*;

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

        this.fe = makeValueFE(editor, kind, name, val);
    }

////////////////////////////////////////////////////////////////
// package scope
////////////////////////////////////////////////////////////////

    void populateNames(String kind)
    {
        BList list = names.getList();
        list.removeAllItems();
        String[] tags = Resources.getKindTags(kind);
        for (int i = 0; i < tags.length; i++)
            list.addItem(tags[i]);
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
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BDouble.DEFAULT);
            fe.loadValue(BDouble.DEFAULT);
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
//        else if (val instanceof HUri)      return "Uri";
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

    private static BWbFieldEditor makeValueFE(BHDictEditor editor, String kind, String name, HVal val)
    {
        if (val instanceof HMarker) 
        {
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
            fe.loadValue(BString.DEFAULT);
            fe.setReadonly(true);
            return fe;
        }
        else if (val instanceof HNum)
        {
            HNum num = (HNum) val;
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BDouble.DEFAULT);
            fe.loadValue(BDouble.make(num.val));
            return fe;
        }
        else if (val instanceof HStr)
        {
            HStr str = (HStr) val;

            if (kind.equals("Str") && name.equals("tz"))
            {
                BWbFieldEditor fe = new BHTimeZoneFE();
                fe.loadValue(BString.make(str.val));
                return fe;
            }
            else if (kind.equals("Str") && name.equals("unit"))
            {
                BWbFieldEditor fe = new BHUnitFE();
                fe.loadValue(BString.make(Resources.getSymbolUnit(str.val).symbol));
                return fe;
            }
            else
            {
                BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
                fe.loadValue(BString.make(str.val));
                return fe;
            }
        }
        else if (val instanceof HRef)
        {
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BOrd.DEFAULT);

            HRef id = (HRef) val;
            NHRef nh = NHRef.make(id);
            if (!nh.getStationName().equals(editor.session().getStationName()))
                throw new BajaRuntimeException(
                    "station name '" + nh.getStationName() + "' does not match " +
                    "session station name '" + editor.session().getStationName() + "'");

            if (nh.isComponentSpace())
            {
                BOrd ord = BOrd.make("station:|h:" + nh.getHandle());
                BComponent comp = (BComponent) ord.resolve(editor.session(), null).get();
                fe.loadValue(BOrd.make("station:|" + comp.getSlotPathOrd()));
            }
            else if (nh.isHistorySpace())
            {
                fe.loadValue(BOrd.make("history:" + nh.getHandle()));
            }
            else throw new IllegalStateException();

            return fe;
        }
        else if (val instanceof HBool)
        {
            HBool bool = (HBool) val;
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BBoolean.DEFAULT);
            fe.loadValue(bool.val ? BBoolean.TRUE : BBoolean.FALSE);
            return fe;
        }
        else throw new IllegalStateException();
    }

////////////////////////////////////////////////////////////////
// Attribs
////////////////////////////////////////////////////////////////

    BListDropDown kinds;
    BTextDropDown names;
    BWbFieldEditor fe;
}
