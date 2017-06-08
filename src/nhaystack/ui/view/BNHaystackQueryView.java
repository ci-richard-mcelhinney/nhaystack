//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   28 Apr 2013  Mike Jarmy  Creation
//
package nhaystack.ui.view;

import java.util.*;

import javax.baja.gx.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.event.*;
import javax.baja.ui.options.*;
import javax.baja.ui.pane.*;
import javax.baja.ui.table.*;
import javax.baja.ui.util.*;
import javax.baja.util.*;
import javax.baja.workbench.view.*;

import org.projecthaystack.*;
import nhaystack.*;
import nhaystack.server.*;

/**
  * BNHaystackQueryView displays the Site-Equip-Point nav table.
  */
public class BNHaystackQueryView extends BWbView
{
    /*-
    class BNHaystackQueryView
    {
        actions
        {
            queryFilter(event: BWidgetEvent) default {[ new BWidgetEvent() ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.view.BNHaystackQueryView(3296896567)1.0$ @*/
/* Generated Tue May 30 17:08:43 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Action "queryFilter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>queryFilter</code> action.
   * @see nhaystack.ui.view.BNHaystackQueryView#queryFilter()
   */
  public static final Action queryFilter = newAction(0,new BWidgetEvent(),null);
  
  /**
   * Invoke the <code>queryFilter</code> action.
   * @see nhaystack.ui.view.BNHaystackQueryView#queryFilter
   */
  public void queryFilter(BWidgetEvent event) { invoke(queryFilter,event,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
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

    class GridModel extends TableModel
    {
        GridModel(HGrid grid) 
        { 
            this.grid = grid; 

            // sort columns by id first, then alphabetically
            IndexName[] indexNames = new IndexName[grid.numCols()];
            for (int i = 0; i < grid.numCols(); i++)
                indexNames[i] = new IndexName(i, grid.col(i).name());

            Arrays.sort(indexNames, new Comparator() {
                public int compare(Object o1, Object o2) {
                    IndexName c1 = (IndexName) o1;
                    IndexName c2 = (IndexName) o2;

                    if (c1.name.equals("id")) return -1;
                    else if (c2.name.equals("id")) return 1;
                    else return (c1.name.compareTo(c2.name));
            }});

            this.columnIndex = new int[indexNames.length];
            for (int i = 0; i < indexNames.length; i++)
                columnIndex[i] = indexNames[i].index;
        }

        public int getRowCount() { return grid.numRows(); }

        public int getColumnCount() { return grid.numCols(); }

        public String getColumnName(int col) 
        { 
            return grid.col(columnIndex[col]).name(); 
        }

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

    class GridCellRenderer extends TableCellRenderer
    {
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
    private static BImage CHECK = BImage.make("module://icons/x16/check.png");
    private static BFont BOLD = BFont.make("Tahoma", 11.0, BFont.BOLD);
    private static String MRU = "nhaystack_filter";
 
    private final BMruTextDropDown filterMru;
    private final BTitlePane title;
    private final BTable table;
    private BNHaystackService service;
}
