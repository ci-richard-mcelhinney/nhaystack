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
import javax.baja.history.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.enums.*;
import javax.baja.ui.event.*;
import javax.baja.ui.list.*;
import javax.baja.ui.pane.*;
import javax.baja.util.*;
import javax.baja.workbench.fieldeditor.*;

import haystack.*;
import haystack.io.*;
import nhaystack.*;
import nhaystack.res.*;

public class BHDictEditor extends BEdgePane
{
    /*-
    class BHDictEditor
    {
        actions
        {
            kindsModified (event: BWidgetEvent) default {[ new BWidgetEvent() ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHDictEditor(2939891505)1.0$ @*/
/* Generated Thu Jan 31 17:55:00 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Action "kindsModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>kindsModified</code> action.
   * @see nhaystack.ui.BHDictEditor#kindsModified()
   */
  public static final Action kindsModified = newAction(0,new BWidgetEvent(),null);
  
  /**
   * Invoke the <code>kindsModified</code> action.
   * @see nhaystack.ui.BHDictEditor#kindsModified
   */
  public void kindsModified(BWidgetEvent event) { invoke(kindsModified,event,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHDictEditor.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHDictEditor() {}

    public BHDictEditor(BFoxProxySession session, BHDict orig)
    {
        this.session = session;

        // load rows
        this.rows = new Array(Row.class);
        Iterator it = orig.getDict().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            HVal val = (HVal) entry.getValue();

            this.rows.add(new Row(name, val));
        }

        // set up ui
        this.grid = new BGridPane();
        grid.setColumnCount(4);
        grid.setHalign(BHalign.left);
        grid.setValign(BValign.top);
        fillGrid();

        BConstrainedPane cons = new BConstrainedPane(this.grid);
        cons.setMinWidth(MIN_WIDTH);
        cons.setMinHeight(MIN_HEIGHT);

        BBorderPane border = new BBorderPane(cons, BInsets.make(5));

        BScrollPane scroll = new BScrollPane();
        scroll.setViewportBackground(BBrush.makeSolid(BColor.make("#CCCCCC")));
        scroll.setContent(border);
        setCenter(scroll);
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    public void save() throws Exception
    {
        HDictBuilder db = new HDictBuilder();

        Set used = new HashSet();
        for (int i = 0; i < rows.size(); i++)
        {
            Row row = (Row) rows.get(i);
            String kind = (String) row.kinds.getSelectedItem();
            String name = row.names.getText();

            // check blank name
            if (name.equals(""))
                throw new BajaRuntimeException("Name is blank in row " + (i+1));

            // check duplicate name
            if (used.contains(name))
                throw new BajaRuntimeException("Name '" + name + "' is used more than once.");
            used.add(name);

            // add to builder
            if (kind.equals("Marker"))
            {
                db.add(name);
            }
            else if (kind.equals("Number"))
            {
                BDouble num = (BDouble) row.fe.saveValue();
                db.add(name, num.getDouble());
            }
            else if (kind.equals("Str"))
            {
                BString str = (BString) row.fe.saveValue();
                db.add(name, str.getString());
            }
            else if (kind.equals("Ref"))
            {
                BOrd ord = (BOrd) row.fe.saveValue();
                OrdQuery[] oq = ord.parse();
                OrdQuery query = oq[oq.length - 1];

                if (!(query.getScheme().equals("slot") || 
                      query.getScheme().equals("history")))
                    throw new BajaRuntimeException("Cannot save '" + ord + "' as an HRef.");

                BObject obj = ord.resolve(session, null).get();
                if (obj instanceof BComponent)
                {
                    BComponent comp = (BComponent) obj;
                    if (!comp.isMounted())
                        throw new BajaRuntimeException(ord + " is not mounted.");
                    NHRef nh = NHRef.make(session.getStationName(), comp);
                    db.add(name, nh.getHRef());
                }
                else if (obj instanceof BIHistory)
                {
                    BIHistory history = (BIHistory) obj;
                    BHistoryConfig cfg = history.getConfig();
                    NHRef nh = NHRef.make(session.getStationName(), cfg);
                    db.add(name, nh.getHRef());
                }
                else
                {
                    throw new BajaRuntimeException("Cannot save '" + ord + "' as an HRef.");
                }
            }
            else if (kind.equals("Bool"))
            {
                BBoolean bool = (BBoolean) row.fe.saveValue();
                db.add(name, bool.getBoolean());
            }
            else throw new IllegalStateException();
        }

        // encode to zinc and back just to be sure
        this.zinc = db.toDict().toZinc();
        this.tags = BHDict.make(new HZincReader(zinc).readDict());
    }

////////////////////////////////////////////////////////////////
// Actions
////////////////////////////////////////////////////////////////

    public void doKindsModified(BWidgetEvent event)
    {
        BListDropDown kinds = (BListDropDown) event.getWidget();
        Row row = (Row) rows.get(rowIndex(kinds));

        String kind = (String) kinds.getSelectedItem();

        populateNames(kind, row.names);
        if (row.names.getList().getItemCount() == 0)
            row.names.setText("");
        else
            row.names.setText((String) row.names.getList().getItem(0));

        row.fe = initValueFE(kind);

        fillGrid();
    }

    private int rowIndex(BListDropDown kinds)
    {
        for (int i = 0; i < rows.size(); i++)
        {
            Row row = (Row) rows.get(i);
            if (row.kinds == kinds) return i;
        }
        throw new IllegalStateException();
    }

////////////////////////////////////////////////////////////////
// Commands
////////////////////////////////////////////////////////////////

    class AddRowIcon extends Command
    {
        public AddRowIcon() { super(BHDictEditor.this, ""); }

        public BImage getIcon() { return ADD; }

        public CommandArtifact doInvoke()
        {
            String[] tags = Resources.getKindTags("Marker");
            rows.add(new Row(tags[0], HMarker.VAL));
            fillGrid();

            return null;
        }
    }

    class AddRowButton extends Command
    {
        public AddRowButton() { super(BHDictEditor.this, LEX.getText("addTag")); }

        public CommandArtifact doInvoke()
        {
            String[] tags = Resources.getKindTags("Marker");
            rows.add(new Row(tags[0], HMarker.VAL));
            fillGrid();

            return null;
        }
    }

    class RemoveRow extends Command
    {
        public RemoveRow(int index) 
        { 
            super(BHDictEditor.this, ""); 
            this.index = index;
        }

        public BImage getIcon() { return REMOVE; }

        public CommandArtifact doInvoke()
        {
            rows.remove(index);
            fillGrid();

            return null;
        }

        private final int index;
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

    private static void populateNames(String kind, BTextDropDown names)
    {
        BList list = names.getList();
        list.removeAllItems();
        String[] tags = Resources.getKindTags(kind);
        for (int i = 0; i < tags.length; i++)
            list.addItem(tags[i]);
    }

    private BWbFieldEditor makeValueFE(HVal val)
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
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BString.DEFAULT);
            fe.loadValue(BString.make(str.val));
            return fe;
        }
        else if (val instanceof HRef)
        {
            BWbFieldEditor fe = BWbFieldEditor.makeFor(BOrd.DEFAULT);

            HRef id = (HRef) val;
            NHRef nh = NHRef.make(id);
            if (!nh.getStationName().equals(session.getStationName()))
                throw new BajaRuntimeException(
                    "station name '" + nh.getStationName() + "' does not match " +
                    "session station name '" + session.getStationName() + "'");

            if (nh.isComponentSpace())
            {
                BOrd ord = BOrd.make("station:|h:" + nh.getHandle());
                BComponent comp = (BComponent) ord.resolve(session, null).get();
                fe.loadValue(comp.getSlotPathOrd());
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

    private BWbFieldEditor initValueFE(String kind)
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

    private void fillGrid()
    {
        grid.removeAll();

        if (rows.size() > 0)
        {
            grid.add(null, new BNullWidget());
            grid.add(null, new BLabel(LEX.getText("type"), BOLD));
            grid.add(null, new BLabel(LEX.getText("name"), BOLD));
            grid.add(null, new BLabel(LEX.getText("value"), BOLD));

            for (int i = 0; i < rows.size(); i++)
            {
                Row row = (Row) rows.get(i);
                grid.add(null, makeAddRemove(new RemoveRow(i)));
                grid.add(null, row.kinds);
                grid.add(null, row.names);
                grid.add(null, row.fe);
            }
        }

        BButton button = new BButton(new AddRowButton());
        button.setButtonStyle(BButtonStyle.toolBar);

        grid.add(null, makeAddRemove(new AddRowIcon()));
        grid.add(null, button);
        grid.add(null, new BNullWidget());
        grid.add(null, new BNullWidget());

        grid.relayout();
    }

    static BConstrainedPane makeAddRemove(Command command)
    {
        BButton button = new BButton(command);
        button.setButtonStyle(BButtonStyle.toolBar);

        BConstrainedPane cons = new BConstrainedPane();
        cons.setMinWidth  (ADD_REMOVE_SIZE);
        cons.setMaxWidth  (ADD_REMOVE_SIZE);
        cons.setMinHeight (ADD_REMOVE_SIZE);
        cons.setMaxHeight (ADD_REMOVE_SIZE);
        cons.setContent(button);
        return cons;
    }

////////////////////////////////////////////////////////////////
// Row
////////////////////////////////////////////////////////////////

    class Row
    {
        Row(String name, HVal val)
        {
            String kind = makeKind(val);

            this.kinds = new BListDropDown();
            populateKinds(kind, kinds);
            kinds.setSelectedItem(kind);
            linkTo(kinds, BDropDown.valueModified, kindsModified);  

            this.names = new BTextDropDown();
            populateNames(kind, names);
            names.setText(name);

            this.fe = makeValueFE(val);
        }

        BListDropDown kinds = new BListDropDown();
        BTextDropDown names = new BTextDropDown();
        BWbFieldEditor fe;
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * Returns null if the call to save() failed.
      */
    public BHDict getTags() { return tags; }

    /**
      * Returns null if the call to save() failed.
      */
    public String getZinc() { return zinc; }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private static BFont BOLD = BFont.make("Tahoma", 11.0, BFont.BOLD);

    private static final BImage ADD    = BImage.make("module://nhaystack/nhaystack/icons/tag_add.png");
    private static final BImage REMOVE = BImage.make("module://nhaystack/nhaystack/icons/tag_remove.png");

    private static final int MIN_WIDTH  = 555; // empirically determined
    private static final int MIN_HEIGHT = 89;

    private static final int ADD_REMOVE_SIZE;
    static
    {
        BListDropDown listDrop = new BListDropDown();
        listDrop.computePreferredSize();
        ADD_REMOVE_SIZE = (int) listDrop.getPreferredHeight();
    }

    private BFoxProxySession session;

    private Array rows;
    private BGridPane grid;

    private BHDict tags = null;
    private String zinc = null;
}
