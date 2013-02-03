//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Feb 2013  Mike Jarmy Creation
//

package nhaystack.ui;

import javax.baja.gx.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.pane.*;
import javax.baja.util.*;

/**
  * BHDictDialog is used by a BHDictFE to bring up a BHDictEditor.
  */
public final class BHDictDialog extends BDialog
{
    /*-
    class BHDictDialog
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHDictDialog(4101567885)1.0$ @*/
/* Generated Fri Feb 01 09:12:38 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHDictDialog.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHDictDialog() {}

    public static BHDictDialog make(BHDictFE fe, BHDictEditor editor)
    {                             
        BGridPane grid = new BGridPane(2);

        BEdgePane edgeContent = new BEdgePane();
        edgeContent.setCenter(editor);
        edgeContent.setBottom(new BBorderPane(grid, BInsets.make(8, 0, 0, 0)));

        return new BHDictDialog(fe, editor, edgeContent, grid);
    }

    private BHDictDialog(
        BHDictFE fe, 
        BHDictEditor editor, 
        BEdgePane edgeContent, 
        BGridPane grid)
    {                             
        super(fe, LEX.getText("haystackTags"), true, edgeContent);

        this.fe = fe;
        this.editor = editor;

        BButton ok = new BButton(new Ok(this));
        BButton cancel = new BButton(new Cancel(this));

        ok.computePreferredSize();
        cancel.computePreferredSize();
        double biggest = Math.max(ok.getPreferredWidth(), cancel.getPreferredWidth());

        BConstrainedPane c1 = new BConstrainedPane(ok);
        BConstrainedPane c2 = new BConstrainedPane(cancel);
        c1.setMinWidth(biggest);
        c2.setMinWidth(biggest);

        grid.add(null, c1);
        grid.add(null, c2);
    }

////////////////////////////////////////////////////////////////
// Commands
////////////////////////////////////////////////////////////////

    class Ok extends Command
    {
        public Ok(BHDictDialog owner) { super(owner, LEX.getText("ok")); }

        public CommandArtifact doInvoke()
        {
            try
            {
                editor.save();
                ((BDialog) getOwner()).close();
            }
            catch (Exception e)
            {
                BDialog.error(getOwner(), LEX.getText("cannotSaveTags"), e.getMessage(), e);
            }

            return null;
        }
    }

    class Cancel extends Command
    {
        public Cancel(BHDictDialog owner) { super(owner, LEX.getText("cancel")); }

        public CommandArtifact doInvoke()
        {
            ((BDialog) getOwner()).close();
            return null;
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private BHDictFE fe; 
    private BHDictEditor editor;
}
