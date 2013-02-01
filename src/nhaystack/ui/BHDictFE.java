//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Jan 2013  Mike Jarmy Creation
//

package nhaystack.ui;

import java.io.*;

import javax.baja.fox.*;
import javax.baja.gx.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.enums.*;
import javax.baja.ui.pane.*;
import javax.baja.util.*;
import javax.baja.workbench.fieldeditor.*;
import javax.baja.workbench.view.*;

import haystack.*;
import nhaystack.*;

public class BHDictFE extends BWbFieldEditor
{
    /*-
    class BHDictFE
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHDictFE(2503827154)1.0$ @*/
/* Generated Sun Jan 27 11:25:33 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHDictFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHDictFE()
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
        tags = (BHDict) value;
        textField.setText(tags.encodeToString());
    }

    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        return tags;
    }

////////////////////////////////////////////////////////////////
// Popup
////////////////////////////////////////////////////////////////

    private class Popup extends Command
    {
        private Popup() { super(BHDictFE.this, null); }

        public BImage getIcon() { return ARROW; }

        public CommandArtifact doInvoke() { edit(); return null; }
    }

    private void edit()
    {
        BHDictEditor editor = new BHDictEditor(findProxySession(), tags);

        BHDictDialog dialog = BHDictDialog.make(this, editor);
        dialog.setBoundsCenteredOnOwner();
        dialog.open();

        BHDict result = editor.getTags();
        if (result != null && !result.equals(tags))
        {
            tags = result;
            textField.setText(editor.getZinc());
            textField.relayout();
            setModified();
        }
    }

    /**
      * A BFoxProxySession can be obtained by walking up
      * the widget tree until we find a BWbComponentView,
      * and then using the proxy session of the view's component.
      */
    private BFoxProxySession findProxySession()
    {
        BWidget parent = getParentWidget();
        while (parent != null)
        {
            if (parent instanceof BWbComponentView) 
            {
                BWbComponentView view = (BWbComponentView) parent;
                BComponent comp = (BComponent) view.getCurrentValue();
                return (BFoxProxySession) comp.getSession();
            }
            parent = parent.getParentWidget();
        }

        throw new IllegalStateException();
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private static final int COLUMNS = 50;
    private static BImage ARROW = BImage.make("module://icons/x16/arrowRight.png");

    private final BTextField textField;
    private final BButton button;

    private BHDict tags;
}
