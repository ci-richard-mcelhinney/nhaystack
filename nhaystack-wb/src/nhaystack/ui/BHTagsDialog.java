//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Feb 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//

package nhaystack.ui;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import javax.baja.gx.BImage;
import javax.baja.gx.Graphics;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BButton;
import javax.baja.ui.BDialog;
import javax.baja.ui.Command;
import javax.baja.ui.CommandArtifact;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BEdgePane;
import javax.baja.ui.table.BTable;
import javax.baja.ui.table.TableCellRenderer;
import javax.baja.ui.table.TableModel;
import javax.baja.util.Lexicon;
import nhaystack.driver.BHTags;
import org.projecthaystack.HDict;
import org.projecthaystack.HMarker;
import org.projecthaystack.HVal;

/**
  * BHTagsDialog is used by a BHTagsFE.
  */

@NiagaraType
public final class BHTagsDialog extends BDialog
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHTagsDialog(2979906276)1.0$ @*/
/* Generated Mon Nov 20 13:09:09 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
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
        Iterator<Map.Entry<String, HVal>> it = dict.iterator();
        while (it.hasNext())
        {
            Map.Entry<String, HVal> entry = it.next();
            String name = entry.getKey();
            HVal val = entry.getValue();

            this.tags[n] = name;
            this.vals[n++] = val;
        }

        ////////////////////

        // sort columns by id first, then alphabetically
        IndexName[] indexNames = new IndexName[tags.length];
        for (int i = 0; i < tags.length; i++)
            indexNames[i] = new IndexName(i, tags[i]);

        Arrays.sort(indexNames, new Comparator<IndexName>()
        {
            @Override
            public int compare(IndexName c1, IndexName c2)
            {
                if (c1.name.equals("id")) return -1;
                else if (c2.name.equals("id")) return 1;
                else return c1.name.compareTo(c2.name);
            }
        });

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

    static class Ok extends Command
    {
        public Ok(BHTagsDialog owner) 
        { 
            super(owner, LEX.getText("ok")); 
        }

        @Override
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
        @Override
        public int getRowCount() { return tags.length; }
        @Override
        public int getColumnCount() { return 2; }
        @Override
        public String getColumnName(int col)
        {
            switch(col)
            {
                case 0: return "Tag";
                case 1: return "Value";
                default: throw new IllegalStateException();
            }
        }
        @Override
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

    static class CellRenderer extends TableCellRenderer
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

    private String[] tags;
    private HVal[] vals;
    private int[] rowIndex;
}
