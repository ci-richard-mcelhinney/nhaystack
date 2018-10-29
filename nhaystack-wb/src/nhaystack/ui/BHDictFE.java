//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Jan 2013  Mike Jarmy Creation
//

package nhaystack.ui;

import javax.baja.gx.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.enums.*;
import javax.baja.ui.pane.*;
import javax.baja.util.*;
import javax.baja.workbench.fieldeditor.*;
import javax.baja.workbench.view.*;

import nhaystack.*;
import nhaystack.server.*;

/**
  * BHDictFE displays a BHDict, and allows editing via a BHDictEditor.
  * </p>
  * Note that this BWbFieldEditor only works if it is
  * mounted somewhere inside of a BWbComponentView.
  */
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

    protected void doSetReadonly(boolean readonly)
    {
        button.setEnabled(!readonly);
    }

    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        tags = (BHDict) value;
        textField.setText(tags.encodeToString());

        this.comp = (BComponent) findParentView().getCurrentValue();
        this.service = findService();
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
        BHDictEditorGroup edGroup = new BHDictEditorGroup(service, comp);

        BHDictDialog dialog = BHDictDialog.make(this, edGroup);
        dialog.setBoundsCenteredOnOwner();
        dialog.open();

        BHDict result = edGroup.getTags();
        if (result != null && !result.equals(tags))
        {
            tags = result;
            textField.setText(edGroup.getZinc());
            textField.relayout();
            setModified();
        }
    }

    private BWbComponentView findParentView()
    {
        BWidget widget = getParentWidget();
        while (widget != null)
        {
            if (widget instanceof BWbComponentView) 
                return (BWbComponentView) widget;

            widget = widget.getParentWidget();
        }
        throw new IllegalStateException(
            "Cannot find parent BWbComponentView");
    }

    private BNHaystackService findService()
    {
        BOrd ord = BOrd.make("station:|slot:/Services");
        BServiceContainer services = (BServiceContainer) ord.get(comp, null);

        subscriber.subscribe(services, 2);
        BNHaystackService[] kids = services.getChildren(BNHaystackService.class);
        subscriber.unsubscribe(services);

        return (kids.length == 0) ? null : kids[0];
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

    private BComponent comp;
    private BNHaystackService service;

    private Subscriber subscriber = new Subscriber() 
        { public void event(BComponentEvent event) {} };
}
