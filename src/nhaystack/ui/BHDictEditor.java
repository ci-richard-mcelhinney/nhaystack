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
import nhaystack.server.*;

/**
  * BHDictEditor is the editor for BHDicts.
  */
public class BHDictEditor extends BEdgePane
{
    /*-
    class BHDictEditor
    {
        actions
        {
            kindsModified(event: BWidgetEvent) default {[ new BWidgetEvent() ]}
            namesModified(event: BWidgetEvent) default {[ new BWidgetEvent() ]} 
            markerGroupsModified()
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHDictEditor(703237734)1.0$ @*/
/* Generated Sun Feb 10 06:48:00 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
// Action "namesModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>namesModified</code> action.
   * @see nhaystack.ui.BHDictEditor#namesModified()
   */
  public static final Action namesModified = newAction(0,new BWidgetEvent(),null);
  
  /**
   * Invoke the <code>namesModified</code> action.
   * @see nhaystack.ui.BHDictEditor#namesModified
   */
  public void namesModified(BWidgetEvent event) { invoke(namesModified,event,null); }

////////////////////////////////////////////////////////////////
// Action "markerGroupsModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>markerGroupsModified</code> action.
   * @see nhaystack.ui.BHDictEditor#markerGroupsModified()
   */
  public static final Action markerGroupsModified = newAction(0,null);
  
  /**
   * Invoke the <code>markerGroupsModified</code> action.
   * @see nhaystack.ui.BHDictEditor#markerGroupsModified
   */
  public void markerGroupsModified() { invoke(markerGroupsModified,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHDictEditor.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHDictEditor() {}

    public BHDictEditor(
        BHDictEditorGroup editorGroup, 
        Map origTags, 
        int editorType)
    {
        this.editorGroup = editorGroup;
        this.editorType = editorType;

        // main grid
        this.mainGrid = new BGridPane(4);
        mainGrid.setHalign(BHalign.left);
        mainGrid.setValign(BValign.top);

        BBorderPane mainBorder = new BBorderPane(mainGrid);
        mainBorder.setPadding(BInsets.make(4, 4, 8, 4));
        mainBorder.setMargin(BInsets.make(4));
        mainBorder.setBorder(BBorder.make("inset"));

        BConstrainedPane mainCons = new BConstrainedPane(mainBorder);
        mainCons.setMinWidth(MIN_GRID_WIDTH);

        setCenter(mainCons);

        if (editorType == OPTIONAL)
        {
            // marker groups
            this.markerGroups = new BListDropDown();
            this.markerGroupTags = new BListDropDown();
            BGridPane mgGrid = new BGridPane(3); 
            mgGrid.setHalign(BHalign.left);
            mgGrid.setValign(BValign.top);
            mgGrid.add(null, new BLabel(LEX.getText("markerGroups") + " ", BOLD));
            mgGrid.add(null, markerGroups);
            mgGrid.add(null, constrainMinWidth(markerGroupTags, 150));
            BBorderPane mgBorder = new BBorderPane(mgGrid);
            mgBorder.setPadding(BInsets.make(4));
            setBottom(mgBorder);

            loadMarkerGroups(origTags); // this might remove some tags!
        }

        loadMainGrid(origTags);
    }

    private static BConstrainedPane constrainMinWidth(BWidget widget, int minWidth)
    {
        BConstrainedPane cons = new BConstrainedPane(widget);
        cons.setMinWidth(minWidth);
        return cons;
    }

    /**
      * set up the marker ui.
      */
    private void loadMarkerGroups(Map tagMap) // TreeMap<String,HVal>
    {
        // populate markerGroups
        markerGroups.getList().addItem(NONE);
        String[] mg = Resources.getMarkerGroups();
        for (int i = 0; i < mg.length; i++)
            markerGroups.getList().addItem(mg[i]);

        // assumer that there are no groups in the tagMap
        markerGroups.setSelectedIndex(0);
        markerGroupTags.setEnabled(false);

        // try to find a group in the tagMap
        Set tagKeys = tagMap.keySet();
        outerLoop: for (int i = 0; i < mg.length; i++)
        {
            String[] mt = Resources.getMarkerGroupTags(mg[i]);
            for (int j = 0; j < mt.length; j++)
            {
                Set set = new HashSet(Arrays.asList(TextUtil.split(mt[j], ' ')));

                // found a group!
                if (tagKeys.containsAll(set))
                {
                    markerGroupTags.setEnabled(true);
                    BList list = markerGroupTags.getList();
                    for (int k = 0; k < mt.length; k++)
                        list.addItem(mt[k]);

                    markerGroups.setSelectedItem(mg[i]);
                    markerGroupTags.setSelectedItem(mt[j]);

                    tagKeys.removeAll(set);
                    break outerLoop;
                }
            }
        }

        // done
        linkTo(markerGroups, BDropDown.valueModified, markerGroupsModified);  
    }

    /**
      * Set up the mainGrid
      */
    private void loadMainGrid(Map tagMap) // TreeMap<String,HVal>
    {
        this.rows = new Array(Row.class);
        Iterator it = tagMap.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            HVal val = (HVal) entry.getValue();

            this.rows.add(new Row(this, name, val));
        }
        fillMainGrid();
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    public void save() throws Exception
    {
        if (editorType == AUTO_GEN)
            throw new IllegalStateException();

        HDictBuilder builder = new HDictBuilder();

        // from mainGrid rows
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
                throw new BajaRuntimeException(
                    "Name '" + name + "' is used more than once.");
            used.add(name);

            // add to builder
            if (kind.equals("Marker"))
            {
                builder.add(name);
            }
            else if (kind.equals("Number"))
            {
                HNum num = ((BHNum) row.fe.saveValue()).getNum();
                builder.add(name, num);
            }
            else if (kind.equals("Str"))
            {
                if (name.equals("unit"))
                {
                    BHUnit unit = (BHUnit) row.fe.saveValue();
                    builder.add(name, unit.getSymbol());
                }
                else if (name.equals("tz"))
                {
                    BHTimeZone tz = (BHTimeZone) row.fe.saveValue();
                    builder.add(name, tz.getTimeZone().name);
                }
                else
                {
                    BString str = (BString) row.fe.saveValue();
                    builder.add(name, str.getString());
                }
            }
            else if (kind.equals("Ref"))
            {
                BOrd ord = (BOrd) row.fe.saveValue();

                // if its a null ord, just don't add anything to the builder
                if (!ord.equals(BOrd.DEFAULT))
                {
                    OrdQuery[] oq = ord.parse();
                    OrdQuery query = oq[oq.length - 1];

                    if (query.getScheme().equals("slot"))
                    {
                        BObject obj = BOrd.make("station:|" + ord).resolve(
                                editorGroup.session(), null).get();
                        BComponent comp = (BComponent) obj;
                        if (!comp.isMounted())
                            throw new BajaRuntimeException(
                                ord + " is not mounted.");

                        NHRef nh = NHRef.make(
                            editorGroup.session().getStationName(), comp);
                        builder.add(name, nh.getHRef());
                    }
                    else if (query.getScheme().equals("history"))
                    {
                        BObject obj = ord.resolve(
                            editorGroup.session(), null).get();

                        BIHistory history = (BIHistory) obj;
                        BHistoryConfig cfg = history.getConfig();
                        NHRef nh = NHRef.make(
                            editorGroup.session().getStationName(), cfg);
                        builder.add(name, nh.getHRef());
                    }
                    else
                    {
                        throw new BajaRuntimeException(
                            "Cannot save '" + ord + "' as an HRef.");
                    }
                }
            }
            else if (kind.equals("Bool"))
            {
                BBoolean bool = (BBoolean) row.fe.saveValue();
                builder.add(name, bool.getBoolean());
            }
            else throw new IllegalStateException();
        }

        // from markerGroupTags
        if (editorType == OPTIONAL)
        {
            String mg = (String) markerGroups.getSelectedItem();
            if (!mg.equals(NONE))
            {
                String item = (String) markerGroupTags.getSelectedItem();
                String[] markers = TextUtil.split(item, ' ');
                for (int i = 0; i < markers.length; i++)
                    builder.add(markers[i]);
            }
        }

        // encode to zinc and back just to be sure
        this.zinc = builder.toDict().toZinc();
        this.tags = BHDict.make(new HZincReader(zinc).readDict());
    }

////////////////////////////////////////////////////////////////
// Actions
////////////////////////////////////////////////////////////////

    public void doKindsModified(BWidgetEvent event)
    {
        BListDropDown kinds = (BListDropDown) event.getWidget();
        Row row = (Row) rows.get(kindsIndex(kinds));

        String kind = (String) kinds.getSelectedItem();

        row.populateNames(kind);
        if (row.names.getList().getItemCount() == 0)
            row.names.setText("");
        else
            row.names.setText((String) row.names.getList().getItem(0));

        row.fe = Row.initValueFE(kind);

        fillMainGrid();
    }

    public void doNamesModified(BWidgetEvent event)
    {
        BTextField text = (BTextField) event.getWidget();
        BTextDropDown names = (BTextDropDown) text.getParent();
        String name = names.getText();

        Row row = (Row) rows.get(namesIndex(names));
        String kind = (String) row.kinds.getSelectedItem();
        if (kind.equals("Str"))
        {
            // change to tz
            if (name.equals("tz") && !(row.fe instanceof BHTimeZoneFE))
            {
                row.fe = new BHTimeZoneFE();
                row.fe.loadValue(BHTimeZone.make(HTimeZone.DEFAULT));
                fillMainGrid();
            }
            // change away from tz
            else if (!name.equals("tz") && (row.fe instanceof BHTimeZoneFE))
            {
                if (name.equals("unit"))
                {
                    row.fe = new BHUnitFE();
                    row.fe.loadValue(BHUnit.make(Resources.getSymbolUnit("%").symbol));
                }
                else
                {
                    row.fe = BWbFieldEditor.makeFor(BString.DEFAULT);
                    row.fe.loadValue(BString.DEFAULT);
                }
                fillMainGrid();
            }
            // change to unit
            else if (name.equals("unit") && !(row.fe instanceof BHUnitFE))
            {
                row.fe = new BHUnitFE();
                row.fe.loadValue(BHUnit.make(Resources.getSymbolUnit("%").symbol));
                fillMainGrid();
            }
            // change away from unit
            else if (!name.equals("unit") && (row.fe instanceof BHUnitFE))
            {
                if (name.equals("tz"))
                {
                    row.fe = new BHTimeZoneFE();
                    row.fe.loadValue(BHTimeZone.make(HTimeZone.DEFAULT));
                }
                else
                {
                    row.fe = BWbFieldEditor.makeFor(BString.DEFAULT);
                    row.fe.loadValue(BString.DEFAULT);
                }
                fillMainGrid();
            }
        }
    }

    private int kindsIndex(BListDropDown kinds)
    {
        for (int i = 0; i < rows.size(); i++)
        {
            Row row = (Row) rows.get(i);
            if (row.kinds == kinds) return i;
        }
        throw new IllegalStateException();
    }

    private int namesIndex(BTextDropDown names)
    {
        for (int i = 0; i < rows.size(); i++)
        {
            Row row = (Row) rows.get(i);
            if (row.names == names) return i;
        }
        throw new IllegalStateException();
    }

    public void doMarkerGroupsModified()
    {
        markerGroupTags.setSelectedIndex(-1);
        BList list = markerGroupTags.getList();
        list.removeAllItems();

        String mg = (String) markerGroups.getSelectedItem();
        if (mg.equals(NONE))
        {
            markerGroupTags.setEnabled(false);
        }
        else
        {
            markerGroupTags.setEnabled(true);

            String[] mt = Resources.getMarkerGroupTags(mg);
            for (int i = 0; i < mt.length; i++)
                list.addItem(mt[i]);
            markerGroupTags.setSelectedItem(mt[0]);
        }

        relayoutAncestors(markerGroupTags, this);
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
            rows.add(new Row(BHDictEditor.this, tags[0], HMarker.VAL));

            fillMainGrid();
            relayoutAncestors(mainGrid, BHDictEditor.this);

            return null;
        }
    }

    class AddRowButton extends Command
    {
        public AddRowButton() { super(BHDictEditor.this, LEX.getText("addTag")); }

        public CommandArtifact doInvoke()
        {
            String[] tags = Resources.getKindTags("Marker");
            rows.add(new Row(BHDictEditor.this, tags[0], HMarker.VAL));

            fillMainGrid();
            relayoutAncestors(mainGrid, BHDictEditor.this);

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

            fillMainGrid();
            relayoutAncestors(mainGrid, BHDictEditor.this);

            return null;
        }

        private final int index;
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private void fillMainGrid()
    {
        mainGrid.removeAll();

        if (rows.size() > 0)
        {
            mainGrid.add(null, new BNullWidget());
            mainGrid.add(null, new BLabel(LEX.getText("type"), BOLD));
            mainGrid.add(null, new BLabel(LEX.getText("name"), BOLD));
            mainGrid.add(null, new BLabel(LEX.getText("value"), BOLD));

            for (int i = 0; i < rows.size(); i++)
            {
                Row row = (Row) rows.get(i);

                mainGrid.add(null, (editorType == OPTIONAL) ?
                    (BWidget) makeAddRemove(new RemoveRow(i)) :
                    (BWidget) new BNullWidget());

                mainGrid.add(null, row.kinds);
                mainGrid.add(null, row.names);
                mainGrid.add(null, row.fe);
            }
        }

        if (editorType == OPTIONAL)
        {
            BButton button = new BButton(new AddRowButton());
            button.setButtonStyle(BButtonStyle.toolBar);

            mainGrid.add(null, makeAddRemove(new AddRowIcon()));
            mainGrid.add(null, button);
            mainGrid.add(null, new BNullWidget());
            mainGrid.add(null, new BNullWidget());
        }

        mainGrid.relayout();
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

    static void relayoutAncestors(BWidget from, BWidget to)
    {
        BWidget widget = from;
        while (widget != to)
        {
            widget.relayout();
            widget = widget.getParentWidget();
        }
        to.relayout();
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
// static attribs
////////////////////////////////////////////////////////////////

    private static final BOrd NH_SERVICE = BOrd.make("station:|slot:/Services/NHaystackService");

    private static final Lexicon LEX = Lexicon.make("nhaystack");
    private static final String NONE = LEX.getText("none");
    private static BFont BOLD = BFont.make("Tahoma", 11.0, BFont.BOLD);

    private static final BImage ADD    = BImage.make("module://nhaystack/nhaystack/icons/tag_add.png");
    private static final BImage REMOVE = BImage.make("module://nhaystack/nhaystack/icons/tag_remove.png");

    // empirically determined
    private static final int MIN_GRID_WIDTH  = 555; 

    private static final int ADD_REMOVE_SIZE;
    static
    {
        BListDropDown listDrop = new BListDropDown();
        listDrop.computePreferredSize();
        ADD_REMOVE_SIZE = (int) listDrop.getPreferredHeight();
    }

////////////////////////////////////////////////////////////////
// access
////////////////////////////////////////////////////////////////
    
    BHDictEditorGroup group() { return editorGroup; }
    int editorType() { return editorType; }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    static final int ESSENTIALS = 1;
    static final int OPTIONAL = 2;
    static final int AUTO_GEN = 3;

    private BHDictEditorGroup editorGroup;
    private int editorType;

    private Array rows;

    private BGridPane mainGrid;
    private BListDropDown markerGroups;
    private BListDropDown markerGroupTags;

    private BHDict tags = null;
    private String zinc = null;
}
