//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   28 Apr 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//
package nhaystack.ui.view;

import java.util.Arrays;
import java.util.Comparator;
import javax.baja.gx.BFont;
import javax.baja.gx.BImage;
import javax.baja.gx.BInsets;
import javax.baja.gx.Graphics;
import javax.baja.nre.annotations.AgentOn;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Action;
import javax.baja.sys.BObject;
import javax.baja.sys.BString;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BLabel;
import javax.baja.ui.BTextField;
import javax.baja.ui.event.BWidgetEvent;
import javax.baja.ui.options.BMruTextDropDown;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BEdgePane;
import javax.baja.ui.table.BTable;
import javax.baja.ui.table.TableCellRenderer;
import javax.baja.ui.table.TableModel;
import javax.baja.ui.util.BTitlePane;
import javax.baja.util.Lexicon;
import javax.baja.workbench.view.BWbView;
import nhaystack.BHGrid;
import nhaystack.server.BNHaystackService;
import org.projecthaystack.HGrid;
import org.projecthaystack.HMarker;
import org.projecthaystack.HVal;

/**
  * BNHaystackQueryView displays the Site-Equip-Point nav table.
  */
@NiagaraType(
  agent = @AgentOn(
    types = "nhaystack:NHaystackService"
  )
)
@NiagaraAction(
  name = "queryFilter",
  parameterType = "BWidgetEvent",
  defaultValue = "new BWidgetEvent()"
)
public class BNHaystackQueryView extends BWbView
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.view.BNHaystackQueryView(80306529)1.0$ @*/
/* Generated Mon Nov 20 10:41:39 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Action "queryFilter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code queryFilter} action.
   * @see #queryFilter(BWidgetEvent parameter)
   */
  public static final Action queryFilter = newAction(0, new BWidgetEvent(), null);
  
  /**
   * Invoke the {@code queryFilter} action.
   * @see #queryFilter
   */
  public void queryFilter(BWidgetEvent parameter) { invoke(queryFilter, parameter, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackQueryView.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackQueryView()
    {
        this.filterMru = new BMruTextDropDown(MRU);
        this.table = new BTable();
        this.table.setCellRenderer(new GridCellRenderer());
        this.title = new BTitlePane("0 recs", table);

        BEdgePane ep1 = new BEdgePane();
        ep1.setLeft(new BLabel(LEX.getText("filter") + "  ", BOLD));
        ep1.setCenter(filterMru);

        BBorderPane bp = new BBorderPane(ep1);
        bp.setPadding(BInsets.make(0, 0, 4, 0));

        BEdgePane ep2 = new BEdgePane();
        ep2.setTop(bp);
        ep2.setCenter(title); 

        setContent(ep2);

        linkTo(filterMru.getEditor(), BTextField.actionPerformed, queryFilter);  
    }

    @Override
    protected void doLoadValue(BObject value, Context cx)
    {
        this.service = (BNHaystackService) value;
        this.filterMru.setEnabled(service.getEnabled());
    }

    public void doQueryFilter(BWidgetEvent event)
    {
        enterBusy();
        try
        {
            String filter = filterMru.getTextAndSave();
            filterMru.setListFromMruOptions();

            HGrid grid = ((BHGrid) service.invoke(
                    BNHaystackService.readAll, 
                    BString.make(filter))).getGrid();

            table.setModel(new GridModel(grid));
            table.relayout();

            title.setTitle(grid.numRows() + " recs");
            title.relayout();
        }
        finally
        {
            exitBusy();
        }
    }

    static class GridModel extends TableModel
    {
        GridModel(HGrid grid) 
        { 
            this.grid = grid; 

            // sort columns by id first, then alphabetically
            IndexName[] indexNames = new IndexName[grid.numCols()];
            for (int i = 0; i < grid.numCols(); i++)
                indexNames[i] = new IndexName(i, grid.col(i).name());

            Arrays.sort(indexNames, new Comparator<IndexName>()
            {
                @Override
                public int compare(IndexName o1, IndexName o2)
                {
                    if (o1.name.equals("id")) return -1;
                    else if (o2.name.equals("id")) return 1;
                    else return o1.name.compareTo(o2.name);
                }
            });

            this.columnIndex = new int[indexNames.length];
            for (int i = 0; i < indexNames.length; i++)
                columnIndex[i] = indexNames[i].index;
        }

        @Override
        public int getRowCount() { return grid.numRows(); }

        @Override
        public int getColumnCount() { return grid.numCols(); }

        @Override
        public String getColumnName(int col)
        { 
            return grid.col(columnIndex[col]).name(); 
        }

        @Override
        public Object getValueAt(int row, int col)
        {
            HVal val = grid.row(row).get(grid.col(columnIndex[col]), false);
            if (val == null) return "";
            else return val;
        }

        private final HGrid grid;
        private final int[] columnIndex;
    }

    static class IndexName
    {
        IndexName(int index, String name)
        {
            this.index = index;
            this.name = name;
        }

        private final int index;
        private final String name;
    }

    static class GridCellRenderer extends TableCellRenderer
    {
        @Override
        public void paintCell(Graphics g, Cell cell)
        {
            if (cell.value instanceof HMarker)
            {
                paintCellBackground(g, cell);

                double x = 2;
                double y = (cell.height-16) / 2;
                g.drawImage(CHECK, x, y);
            }
            else
            {
                super.paintCell(g, cell);
            }
        }
    }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");
    private static final BImage CHECK = BImage.make("module://icons/x16/check.png");
    private static final BFont BOLD = BFont.make("Tahoma", 11.0, BFont.BOLD);
    private static final String MRU = "nhaystack_filter";
 
    private final BMruTextDropDown filterMru;
    private final BTitlePane title;
    private final BTable table;
    private BNHaystackService service;
}
