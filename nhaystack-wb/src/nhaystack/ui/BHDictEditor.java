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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.baja.gx.BFont;
import javax.baja.gx.BImage;
import javax.baja.gx.BInsets;
import javax.baja.naming.BOrd;
import javax.baja.naming.OrdQuery;
import javax.baja.naming.SlotPath;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Action;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComponent;
import javax.baja.sys.BObject;
import javax.baja.sys.BRelation;
import javax.baja.sys.BString;
import javax.baja.sys.BValue;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Entity;
import javax.baja.ui.BBorder;
import javax.baja.ui.BButton;
import javax.baja.ui.BDialog;
import javax.baja.ui.BLabel;
import javax.baja.ui.BListDropDown;
import javax.baja.ui.BNullWidget;
import javax.baja.ui.BTextDropDown;
import javax.baja.ui.BTextField;
import javax.baja.ui.BWidget;
import javax.baja.ui.Command;
import javax.baja.ui.CommandArtifact;
import javax.baja.ui.enums.BButtonStyle;
import javax.baja.ui.enums.BHalign;
import javax.baja.ui.enums.BValign;
import javax.baja.ui.event.BWidgetEvent;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BConstrainedPane;
import javax.baja.ui.pane.BEdgePane;
import javax.baja.ui.pane.BGridPane;
import javax.baja.util.Lexicon;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;
import nhaystack.BHDict;
import nhaystack.BHFloor;
import nhaystack.BHNum;
import nhaystack.BHTimeZone;
import nhaystack.BHUnit;
import nhaystack.NHRef;
import nhaystack.util.NHaystackConst;
import nhaystack.res.Resources;
import nhaystack.server.TagManager;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HMarker;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HTimeZone;
import org.projecthaystack.HVal;
import org.projecthaystack.io.HZincReader;

/**
 * BHDictEditor is the editor for BHDicts.
 */
@NiagaraType
@NiagaraAction(
    name = "kindsModified",
    parameterType = "BWidgetEvent",
    defaultValue = "new BWidgetEvent()"
)
@NiagaraAction(
    name = "namesModified",
    parameterType = "BWidgetEvent",
    defaultValue = "new BWidgetEvent()"
)
@NiagaraAction(
    name = "valueModified",
    parameterType = "BWidgetEvent",
    defaultValue = "new BWidgetEvent()"
)
public class BHDictEditor extends BEdgePane implements NHaystackConst
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHDictEditor(1748009372)1.0$ @*/
/* Generated Wed Jul 25 12:32:51 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Action "kindsModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code kindsModified} action.
   * @see #kindsModified(BWidgetEvent parameter)
   */
  public static final Action kindsModified = newAction(0, new BWidgetEvent(), null);

  /**
   * Invoke the {@code kindsModified} action.
   * @see #kindsModified
   */
  public void kindsModified(BWidgetEvent parameter) { invoke(kindsModified, parameter, null); }

////////////////////////////////////////////////////////////////
// Action "namesModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code namesModified} action.
   * @see #namesModified(BWidgetEvent parameter)
   */
  public static final Action namesModified = newAction(0, new BWidgetEvent(), null);
  
  /**
   * Invoke the {@code namesModified} action.
   * @see #namesModified
   */
  public void namesModified(BWidgetEvent parameter) { invoke(namesModified, parameter, null); }

////////////////////////////////////////////////////////////////
// Action "valueModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code valueModified} action.
   * @see #valueModified(BWidgetEvent parameter)
   */
  public static final Action valueModified = newAction(0, new BWidgetEvent(), null);
  
  /**
   * Invoke the {@code valueModified} action.
   * @see #valueModified
   */
  public void valueModified(BWidgetEvent parameter) { invoke(valueModified, parameter, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHDictEditor.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHDictEditor()
    {
    }

    public BHDictEditor(
        BHDictEditorGroup editorGroup,
        int editorType,
        Map<String, HVal> origTags)
    {
        this.editorGroup = editorGroup;
        this.editorType = editorType;
        this.origTags = origTags;
        // save the current value of the haystack slot
        HDict workingHDict = ((BHDict)editorGroup.component().get("haystack")).getDict();
        HDictBuilder builder = new HDictBuilder();
        for (Entry<String, HVal> entry : origTags.entrySet())
        {
            if (workingHDict.has(entry.getKey()))
                builder.add(entry.getKey(), entry.getValue());
        }
        this.origHDict = BHDict.make(builder.toDict());
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
            BBorderPane border = new BBorderPane(new BButton(new AddMarkerSet()));
            border.setPadding(BInsets.make(4));
            BEdgePane ep = new BEdgePane();
            ep.setLeft(border);
            setBottom(ep);
        }

        loadMainGrid();
        //save() will initialize startingHDict
        // used determine if this editor changed anything.
        try { save(); }
        catch (Exception ignore) { }
    }

    /**
     * Set up the mainGrid
     */
    private void loadMainGrid()
    {
        this.rows = new ArrayList<>();
        this.refRows = new ArrayList<>();
        this.removedRows = new ArrayList<>();
        for (Map.Entry<String, HVal> entry : origTags.entrySet())
        {
            String name = entry.getKey();
            HVal val = entry.getValue();
            // don't include the implied tag group name tag for direct tag
            // groups
            if (editorGroup.impliedTagGroupNames().containsKey(name))
                continue;
            // but include the implied tagGroup individual tags.
            if (!isDirectTag(editorGroup.component(), name) && (editorType != AUTO_GEN))
            {
                if (editorGroup.impliedTagGroupTags().containsKey(name))
                {
                    // An existing implied tag group tag
                    this.rows.add(new Row(this, name, val, false, false));
                }
                else if(val instanceof HRef) // add HRef tags from relations
                {
                    final Row row = new Row(this, name, val, true, false);
                    BOrd refOrd = BOrd.NULL;
                    try
                    {
                        refOrd = (BOrd)row.fe.saveValue();
                    }
                    catch(Exception ignore) { }
                    String slot = null;
                    try
                    {
                        slot = getRelationSlot(editorGroup.component(), name, refOrd);
                        row.relationSlot = slot;
                    }
                    catch(Exception unresolved)
                    {
                        ((BRefOrdFE)row.fe).isUnresolved = true;
                        row.isUnresolvedRef = true;
                    }
                    this.rows.add(row);
                    //create a clone of this row and add to refRows
                    this.refRows.add(new Row(this, name, val, true, false, slot));
                }
                else
                {
                    this.rows.add(new Row(this, name, val, true, false));
                }
            }
            else
            {
                // An existing direct tag
                this.rows.add(new Row(this, name, val, true, false));
            }
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
        Set<String> used = new HashSet<>();
        final BComponent comp = group().component();
        for (int i = 0; i < rows.size(); i++)
        {
            Row row = rows.get(i);
            String kind = (String)row.kinds.getSelectedItem();
            String name = row.names.getText();

            // Skip rows for implied tags unless their value was modified.
            // Tags implied by tag groups are included in the optional
            // section
            if ( !row.isDirectTag )
            {
                // check to see if a value tag has modified in the editor
                if(kind.equals("Marker"))
                    continue;
                final HVal origValue = origTags.get(name);
                final BObject value = row.fe.saveValue();
                if(origValue.toString().equals(value.toString()))
                    continue;
            }


            // check blank name
            if (name.isEmpty())
                throw new BajaRuntimeException("Name is blank in row " + (i + 1));

            // check duplicate name
            if (used.contains(name))
                throw new BajaRuntimeException(
                    "Name '" + name + "' is used more than once.");
            used.add(name);

            ///////////////////////////////////////////////////////////////////

            // add to builder
            switch (kind)
            {
                case "Marker":
                    builder.add(name);
                    break;
                case "Number":
                    HNum num = ((BHNum)row.fe.saveValue()).getNum();
                    builder.add(name, num);
                    break;
                case "Str":
                    switch (name)
                    {
                        case "unit":
                            BHUnit unit = (BHUnit)row.fe.saveValue();
                            builder.add(name, unit.getSymbol());
                            break;
                        case "tz":
                            BHTimeZone tz = (BHTimeZone)row.fe.saveValue();
                            builder.add(name, tz.getTimeZone().name);
                            break;
                        case "floorName":
                            BHFloor floor = (BHFloor)row.fe.saveValue();
                            builder.add(name, floor.getFloor());
                            break;
                        default:
                            BString str = (BString)row.fe.saveValue();
                            builder.add(name, str.getString());
                            break;
                    }
                    break;
                case "Ref":
                    // For equipRefs, if the field editor hasn't been modified,
                    // then auto-generated implicit equipRefs shouldn't be saved
                    if (name.equals(EQUIP_REF) && !row.fe.isModified())
                    {
                        HDict annotation = BHDict.findTagAnnotation(comp);
                        if (annotation == null || !annotation.has(EQUIP_REF))
                            continue;
                    }

                    BOrd ord = (BOrd)row.fe.saveValue();

                    // if its a null ord, just don't add anything to the builder
                    if (!ord.equals(BOrd.DEFAULT))
                    {
                        OrdQuery[] oq = ord.parse();
                        OrdQuery query = oq[oq.length - 1];

                        if (query.getScheme().equals("slot"))
                        {
                            try
                            {
                                BComponent refComp = (BComponent)
                                    ord.resolve(editorGroup.session(), null).get();
                                if (!refComp.isMounted())
                                    throw new BajaRuntimeException(
                                        ord + " is not mounted.");

                                NHRef ref = TagManager.makeSlotPathRef(refComp);
                                builder.add(name, ref.getHRef());
                            }
                            catch(Exception ignore) {}
                        }

                        else
                        {
                            throw new BajaRuntimeException(
                                "Cannot save '" + ord + "' as an HRef.");
                        }
                    }
                    // primarily to catch the equipRef changed from direct relation to an implied relation.
                    else if(row.relationSlot != null && !row.relationSlot.isEmpty())
                    {
                        group().slotsToRemove().add(row.relationSlot);
                    }

                    break;
                case "Bool":
                    BBoolean bool = (BBoolean)row.fe.saveValue();
                    builder.add(name, bool.getBoolean());
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        // Handle row removals
        if (editorType == BHDictEditor.OPTIONAL)
        {
            for (Row row : removedRows)
            {
                String kind = (String)row.kinds.getSelectedItem();
                String name = row.names.getText();
                try
                {
                    if (kind.equals("Ref") && row.relationSlot != null)
                    {
                        group().slotsToRemove().add(row.relationSlot);
                    }
                    else
                    {
                        group().slotsToRemove().add(SlotPath.escape("hs:" + name));
                    }
                }
                catch (Exception ignore)
                {
                }

                if (!row.isDirectTag)
                {
                    for (BRelation bRelation : group().hsTagGroupRelations())
                    {
                        group().slotsToRemove().add(bRelation.getPropertyInParent().getName());
                    }
                }
            }

            removedRows.clear();
            removeRenamedTags();
            removeModifiedRefs();
        }

        // encode to zinc and back just to be sure
        this.zinc = builder.toDict().toZinc();
        this.tags = BHDict.make(new HZincReader(zinc).readDict());

        // initialize startingHDict if null
        if(startingHDict == null)
        {
            this.startingHDict = this.tags;
        }
        // else if this tags the same as the startingHDict. i.e., nothing has changed
        // return the origHdict value
        else if( this.tags.equals(this.startingHDict))
        {
            if(editorType != AUTO_GEN)
                this.tags = BHDict.make(origHDict.getDict());
        }
    }

////////////////////////////////////////////////////////////////
// Actions
////////////////////////////////////////////////////////////////

    public void doKindsModified(BWidgetEvent event)
    {
        BListDropDown kinds = (BListDropDown)event.getWidget();
        Row row = rows.get(kindsIndex(kinds));

        String kind = (String)kinds.getSelectedItem();

        row.populateNames(kind);
        if (row.names.getList().getItemCount() == 0)
            row.names.setText("");
        else
            row.names.setText((String)row.names.getList().getItem(0));

        row.fe = Row.initValueFE(kind, row.isUnresolvedRef);

        fillMainGrid();
    }

    public void doNamesModified(BWidgetEvent event)
    {
        BTextField text = (BTextField)event.getWidget();
        BTextDropDown names = (BTextDropDown)text.getParent();
        String name = names.getText();

        Row row = rows.get(namesIndex(names));
        String kind = (String)row.kinds.getSelectedItem();
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
            else if (!name.equals("tz") && row.fe instanceof BHTimeZoneFE)
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
            else if (!name.equals("unit") && row.fe instanceof BHUnitFE)
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

    public void doValueModified(BWidgetEvent event)
    {
        BWbFieldEditor editor = (BWbFieldEditor)event.getWidget();
        Row row = rows.get(valueIndex(editor));

        if( !row.isDirectTag)
        {
            BDialog.info(BHDictEditor.this, LEX.getText("tagGroup.tag.change.title"), LEX.getText("tagGroup.tag.change.message"));
            row.names.setEnabled(true);
            row.kinds.setEnabled(true);
        }
    }

    private int kindsIndex(BListDropDown kinds)
    {
        for (int i = 0; i < rows.size(); i++)
        {
            Row row = rows.get(i);
            if (row.kinds == kinds) return i;
        }
        throw new IllegalStateException();
    }

    private int namesIndex(BTextDropDown names)
    {
        for (int i = 0; i < rows.size(); i++)
        {
            Row row = rows.get(i);
            if (row.names == names) return i;
        }
        throw new IllegalStateException();
    }

    private int valueIndex(BWbFieldEditor editor)
    {
        for (int i = 0; i < rows.size(); i++)
        {
            Row row = rows.get(i);
            if (row.fe == editor) return i;
        }
        throw new IllegalStateException();
    }

////////////////////////////////////////////////////////////////
// Commands
////////////////////////////////////////////////////////////////

    class AddRowIcon extends Command
    {
        public AddRowIcon()
        {
            super(BHDictEditor.this, "");
        }

        @Override
        public BImage getIcon()
        {
            return ADD;
        }

        @Override
        public CommandArtifact doInvoke()
        {
            String[] tags = Resources.getKindTags("Marker");
            // A new direct tag
            rows.add(new Row(BHDictEditor.this, tags[0], HMarker.VAL, true, true));

            fillMainGrid();
            relayoutAncestors(mainGrid, BHDictEditor.this);

            return null;
        }
    }

    class AddRowButton extends Command
    {
        public AddRowButton()
        {
            super(BHDictEditor.this, LEX.getText("addTag"));
        }

        @Override
        public CommandArtifact doInvoke()
        {
            String[] tags = Resources.getKindTags("Marker");
            // A new direct tag
            rows.add(new Row(BHDictEditor.this, tags[0], HMarker.VAL, true, true));

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

        @Override
        public BImage getIcon()
        {
            return REMOVE;
        }

        @Override
        public CommandArtifact doInvoke()
        {
            final Row row = rows.get(index);
            if( !row.isDirectTag)
            {
                final int results = BDialog.confirm(BHDictEditor.this, LEX.getText("tagGroup.tag.remove.title"), LEX.getText("tagGroup.tag.remove.message"));
                if (results != BDialog.YES)
                    return null;
            }
            removedRows.add(row);
            rows.remove(index);
            if( !row.isDirectTag )
            {
                for (Row row1 : rows)
                {
                    if( !row1.isDirectTag )
                    {
                        row1.isDirectTag = true;
                        row1.fe.setReadonly(false);
                        row1.kinds.setEnabled(true);
                        row1.names.setEnabled(true);
                    }
                }
           }
            fillMainGrid();
            relayoutAncestors(mainGrid, BHDictEditor.this);

            return null;
        }

        private final int index;
    }

    class AddMarkerSet extends Command
    {
        public AddMarkerSet()
        {
            super(BHDictEditor.this, LEX, "addMarkerSet");
        }

        @Override
        public CommandArtifact doInvoke()
        {
            BMarkerSet markerSet = new BMarkerSet();

            int result = BDialog.open(
                BHDictEditor.this,
                LEX.getText("addMarkerSet"),
                markerSet,
                BDialog.OK_CANCEL);

            if (result == BDialog.OK)
            {
                Set<String> used = new HashSet<>();
                for (Row row : rows)
                {
                    used.add(row.names.getText());
                }

                String[] markers = markerSet.getMarkers();
                for (String marker : markers)
                {
                    if (!used.contains(marker))
                    {
                        // A new direct tag
                        rows.add(new Row(
                                BHDictEditor.this, marker, HMarker.VAL, true, true));
                    }
                }

                fillMainGrid();
                relayoutAncestors(mainGrid, BHDictEditor.this);
            }

            return null;
        }
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private void fillMainGrid()
    {
        mainGrid.removeAll();

        if (!rows.isEmpty())
        {
            mainGrid.add(null, new BNullWidget());
            mainGrid.add(null, new BLabel(LEX.getText("type"), BOLD));
            mainGrid.add(null, new BLabel(LEX.getText("name"), BOLD));
            mainGrid.add(null, new BLabel(LEX.getText("value"), BOLD));

            for (int i = 0; i < rows.size(); i++)
            {
                Row row = rows.get(i);
                mainGrid.add(null, editorType == OPTIONAL ?
                    makeAddRemove(new RemoveRow(i)) :
                    new BNullWidget());

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
        cons.setMinWidth(ADD_REMOVE_SIZE);
        cons.setMaxWidth(ADD_REMOVE_SIZE);
        cons.setMinHeight(ADD_REMOVE_SIZE);
        cons.setMaxHeight(ADD_REMOVE_SIZE);
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

    static boolean isDirectTag(BComponent comp, String hsTagName)
    {
        BValue tag = null;
        try
        {
            tag = comp.get(SlotPath.escape("hs:" + hsTagName));
        }
        catch (Exception ignore)
        {
        }
        return tag != null;
    }

    static String getRelationSlot(BComponent comp, String tag,  BOrd refOrd)
    {
        if(refOrd.isNull())
            return null;
        final BObject endPoint = refOrd.resolve(comp).get();
        if(endPoint == null)
            return null;
        for (BRelation relation : comp.getComponentRelations())
        {
            if(relation.isOutbound() && relation.getId().getQName().equals("hs:"+tag))
            {
                final Entity relationEndpoint = relation.getEndpoint();
                if(endPoint == relationEndpoint)
                {
                    final Property propertyInParent = relation.getPropertyInParent();
                    return propertyInParent.getName();
                }
            }
        }
        return null;
    }

    void removeRenamedTags()
    {
        for (Entry<String, HVal> origTag : origTags.entrySet())
        {
            boolean removeIt = true;
            for (Row row : rows)
            {
                if( origTag.getKey().equals(row.names.getText()) )
                {
                    removeIt = false;
                    break;
                }
            }
            if(removeIt)
            {
                try
                {
                    final String tagSlotName = SlotPath.escape("hs:" + origTag.getKey());
                    if(group().component().getSlot(tagSlotName) != null)
                    {
                        group().slotsToRemove().add(tagSlotName);
                    }
                }
                catch(Exception ignore) {}
            }
        }
    }

    void removeModifiedRefs()
    {
        for (Row hrefRow : refRows)
        {
            boolean removeIt = true;
            for (Row row : rows)
            {
                if( hrefRow.names.getText().equals(row.names.getText()) )
                {
                    try
                    {
                        if (hrefRow.fe.saveValue().equals(row.fe.saveValue()))
                        {
                            removeIt = false;
                            break;
                        }
                    }
                    catch (Exception ignore) {}
                }
            }
            if(removeIt)
            {
                try
                {
                    group().slotsToRemove().add(hrefRow.relationSlot);
                }
                catch(Exception ignore) {}
            }
        }
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

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
// static attribs
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");
    private static final String NONE = LEX.getText("none");
    private static final BFont BOLD = BFont.make("Tahoma", 11.0, BFont.BOLD);

    private static final BImage ADD = BImage.make("module://nhaystack/nhaystack/icons/tag_add.png");
    private static final BImage REMOVE = BImage.make("module://nhaystack/nhaystack/icons/tag_remove.png");

    // empirically determined
    private static final int MIN_GRID_WIDTH = 555;

    private static final int ADD_REMOVE_SIZE;

    static
    {
        BListDropDown listDrop = new BListDropDown();
        listDrop.computePreferredSize();
        ADD_REMOVE_SIZE = (int)listDrop.getPreferredHeight();
    }

////////////////////////////////////////////////////////////////
// access
////////////////////////////////////////////////////////////////

    BHDictEditorGroup group()
    {
        return editorGroup;
    }

    int editorType()
    {
        return editorType;
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    static final int ESSENTIALS = 1;
    static final int OPTIONAL = 2;
    static final int AUTO_GEN = 3;

    private BHDictEditorGroup editorGroup;
    private int editorType;
    private Map<String, HVal> origTags;
    private BHDict origHDict;
    private BHDict startingHDict;

    private List<Row> rows;
    private List<Row> refRows;
    private List<Row> removedRows;


    private BGridPane mainGrid;

    private BHDict tags;
    private String zinc;
}
