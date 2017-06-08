//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Jan 2013  Mike Jarmy Creation
//

package nhaystack.driver.ui;

import javax.baja.gx.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.enums.*;
import javax.baja.ui.pane.*;
import javax.baja.util.*;
import javax.baja.workbench.fieldeditor.*;

import nhaystack.driver.*;

/**
  * BHTagsFE displays a BHTags, and allows editing via a BHTagsEditor.
  * </p>
  * Note that this BWbFieldEditor only works if it is
  * mounted somewhere inside of a BWbComponentView.
  */
public class BHTagsFE extends BWbFieldEditor
{
    /*-
    class BHTagsFE
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.ui.BHTagsFE(708555327)1.0$ @*/
/* Generated Tue May 30 17:08:42 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
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

    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        tags = (BHTags) value;
        textField.setText(tags.encodeToString());
    }

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

        public BImage getIcon() { return ARROW; }

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
    private static BImage ARROW = BImage.make("module://icons/x16/arrowRight.png");

    private final BTextField textField;
    private final BButton button;

    private BHTags tags;
}
