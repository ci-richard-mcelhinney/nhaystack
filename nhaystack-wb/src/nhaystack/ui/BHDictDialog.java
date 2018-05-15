//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Feb 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//

package nhaystack.ui;

import javax.baja.gx.BInsets;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BButton;
import javax.baja.ui.BDialog;
import javax.baja.ui.Command;
import javax.baja.ui.CommandArtifact;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BConstrainedPane;
import javax.baja.ui.pane.BEdgePane;
import javax.baja.ui.pane.BGridPane;
import javax.baja.util.Lexicon;

/**
  * BHDictDialog is used by a BHDictFE to bring up a BHDictEditorGroup.
  */
@NiagaraType
public final class BHDictDialog extends BDialog
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHDictDialog(2979906276)1.0$ @*/
/* Generated Mon Nov 20 11:11:37 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHDictDialog.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHDictDialog() {}

    public static BHDictDialog make(BHDictFE fe, BHDictEditorGroup edGroup)
    {                             
        BGridPane grid = new BGridPane(2);

        BEdgePane edgeContent = new BEdgePane();
        edgeContent.setCenter(edGroup);
        edgeContent.setBottom(new BBorderPane(grid, BInsets.make(8, 0, 0, 0)));

        return new BHDictDialog(fe, edGroup, edgeContent, grid);
    }

    private BHDictDialog(
        BHDictFE fe, 
        BHDictEditorGroup edGroup, 
        BEdgePane edgeContent, 
        BGridPane grid)
    {                             
        super(fe, LEX.getText("haystackTags"), true, edgeContent);

        this.edGroup = edGroup;

        BButton ok = new BButton(new Ok(this));
        BButton cancel = new BButton(new Cancel(this));

        ok.computePreferredSize();
        cancel.computePreferredSize();
        double biggest = Math.max(
            ok.getPreferredWidth(), 
            cancel.getPreferredWidth());

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
        public Ok(BHDictDialog owner) 
        { 
            super(owner, LEX.getText("ok")); 
        }

        @Override
        public CommandArtifact doInvoke()
        {
            try
            {
                edGroup.save();
                ((BDialog) getOwner()).close();
            }
            catch (Exception e)
            {
                BDialog.error(
                    getOwner(), 
                    LEX.getText("cannotSaveTags"), 
                    e.getMessage(), 
                    e);
            }

            return null;
        }
    }

    static class Cancel extends Command
    {
        public Cancel(BHDictDialog owner) 
        { 
            super(owner, LEX.getText("cancel")); 
        }

        @Override
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

    private BHDictEditorGroup edGroup;
}
