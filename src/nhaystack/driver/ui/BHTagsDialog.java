//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Feb 2013  Mike Jarmy Creation
//

package nhaystack.driver.ui;

import java.util.*;

import javax.baja.gx.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.pane.*;
import javax.baja.ui.table.*;
import javax.baja.util.*;

import org.projecthaystack.*;

import nhaystack.driver.*;

/**
  * BHTagsDialog is used by a BHDictFE to bring up a BHDictEditorGroup.
  */
public final class BHTagsDialog extends BDialog
{
    /*-
    class BHTagsDialog
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.ui.BHTagsDialog(4266311391)1.0$ @*/
/* Generated Thu Apr 10 15:56:03 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHTagsDialog.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHTagsDialog() {}

    public static BHTagsDialog make(BHTagsFE fe, BHTags htags)
    {                             
        return new BHTagsDialog(fe, htags, new BEdgePane());
    }

    private BHTagsDialog(BHTagsFE fe, BHTags htags, BEdgePane content)
    {                             
        super(fe, LEX.getText("haystackTags"), true, content);

        HDict dict = htags.getDict();
        this.tags = new String[dict.size()];
        this.vals = new HVal[dict.size()];

        int n = 0;
        Iterator it = dict.iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            HVal val = (HVal) entry.getValue();

            this.tags[n] = name;
            this.vals[n++] = val;
        }

        ////////////////////

        // sort columns by id first, then alphabetically
        IndexName[] indexNames = new IndexName[tags.length];
        for (int i = 0; i < tags.length; i++)
            indexNames[i] = new IndexName(i, tags[i]);

        Arrays.sort(indexNames, new Comparator() {
            public int compare(Object o1, Object o2) {
                IndexName c1 = (IndexName) o1;
                IndexName c2 = (IndexName) o2;

                if (c1.name.equals("id")) return -1;
                else if (c2.name.equals("id")) return 1;
                else return (c1.name.compareTo(c2.name));
        }});

        this.rowIndex = new int[indexNames.length];
        for (int i = 0; i < indexNames.length; i++)
            rowIndex[i] = indexNames[i].index;

        ////////////////////

        BTable table = new BTable();
        table.setCellRenderer(new CellRenderer());
        table.setModel(new Model());

        BButton ok = new BButton(new Ok(this));
        content.setCenter(new BBorderPane(table, 4, 4, 4, 4));
        content.setBottom(ok);
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

////////////////////////////////////////////////////////////////
// Commands
////////////////////////////////////////////////////////////////

    class Ok extends Command
    {
        public Ok(BHTagsDialog owner) 
        { 
            super(owner, LEX.getText("ok")); 
        }

        public CommandArtifact doInvoke()
        {
            ((BDialog) getOwner()).close();
            return null;
        }
    }

////////////////////////////////////////////////////////////////
// TableModel
////////////////////////////////////////////////////////////////

    class Model extends TableModel
    {
        public int getRowCount() { return tags.length; }
        public int getColumnCount() { return 2; }
        public String getColumnName(int col)
        {
            switch(col)
            {
                case 0: return "Tag";
                case 1: return "Value";
                default: throw new IllegalStateException();
            }
        }
        public Object getValueAt(int row, int col)
        {
            switch(col)
            {
                case 0: return tags[rowIndex[row]];
                case 1: return vals[rowIndex[row]];
                default: throw new IllegalStateException();
            }
        }
    }

////////////////////////////////////////////////////////////////
// TableCellRenderer
////////////////////////////////////////////////////////////////

    class CellRenderer extends TableCellRenderer
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

    private String[] tags;
    private HVal[] vals;
    private int[] rowIndex;
}
