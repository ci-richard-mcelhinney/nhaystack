//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Jan 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//

package nhaystack.ui;

import javax.baja.gx.BImage;
import javax.baja.gx.BInsets;
import javax.baja.nre.annotations.AgentOn;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BButton;
import javax.baja.ui.BTextField;
import javax.baja.ui.Command;
import javax.baja.ui.CommandArtifact;
import javax.baja.ui.enums.BButtonStyle;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BEdgePane;
import javax.baja.util.Lexicon;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;
import nhaystack.driver.BHTags;

/**
  * BHTagsFE displays a BHTags, and allows editing via a BHTagsEditor.
  * </p>
  * Note that this BWbFieldEditor only works if it is
  * mounted somewhere inside of a BWbComponentView.
  */

@NiagaraType(
  agent =   @AgentOn(
    types = "nhaystack:HTags"
  )
)
public class BHTagsFE extends BWbFieldEditor
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHTagsFE(930204479)1.0$ @*/
/* Generated Mon Nov 20 13:11:05 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHTagsFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHTagsFE()
    {
        textField = new BTextField("", COLUMNS, false);
        button = new BButton(new Popup());
        button.setButtonStyle(BButtonStyle.toolBar);

        BEdgePane ep = new BEdgePane();
        ep.setCenter(textField);
        ep.setRight(new BBorderPane(button, BInsets.make(0,0,0,4)));

        setContent(ep);
    }

    @Override
    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        tags = (BHTags) value;
        textField.setText(tags.encodeToString());
    }

    @Override
    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        throw new IllegalStateException();
    }

////////////////////////////////////////////////////////////////
// Popup
////////////////////////////////////////////////////////////////

    private class Popup extends Command
    {
        private Popup() { super(BHTagsFE.this, null); }

        @Override
        public BImage getIcon() { return ARROW; }

        @Override
        public CommandArtifact doInvoke()
        { 
            BHTagsDialog dialog = BHTagsDialog.make(BHTagsFE.this, tags);
            dialog.setBoundsCenteredOnOwner();
            dialog.open();
            return null;
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private static final int COLUMNS = 50;
    private static final BImage ARROW = BImage.make("module://icons/x16/arrowRight.png");

    private final BTextField textField;
    private final BButton button;

    private BHTags tags;
}
