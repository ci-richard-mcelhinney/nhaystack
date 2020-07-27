//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Jan 2013  Mike Jarmy       Creation
//   10 May 2018  Eric Anderson    Migrated to slot annotations, added missing @Overrides annotations
//   26 Sep 2018  Andrew Saunders  Managing interaction with Niagara Haystack tags
//

package nhaystack.ui;

import java.util.ArrayList;
import java.util.List;
import javax.baja.gx.BImage;
import javax.baja.gx.BInsets;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.AgentOn;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BComponentEvent;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Subscriber;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BButton;
import javax.baja.ui.BTextField;
import javax.baja.ui.BWidget;
import javax.baja.ui.Command;
import javax.baja.ui.CommandArtifact;
import javax.baja.ui.enums.BButtonStyle;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BEdgePane;
import javax.baja.util.BServiceContainer;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;
import javax.baja.workbench.view.BWbComponentView;

import nhaystack.BHDict;
import nhaystack.server.BNHaystackService;
import nhaystack.server.HaystackSlotUtil;
import org.projecthaystack.HDict;

/**
  * BHDictFE displays a BHDict, and allows editing via a BHDictEditor.
  * </p>
  * Note that this BWbFieldEditor only works if it is
  * mounted somewhere inside of a BWbComponentView.
  */
@NiagaraType(
  agent = @AgentOn(
    types = "nhaystack:HDict"
  )
)
public class BHDictFE extends BWbFieldEditor
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHDictFE(3567995877)1.0$ @*/
/* Generated Mon Nov 20 13:02:35 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
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

    @Override
    protected void doSetReadonly(boolean readonly)
    {
        button.setEnabled(!readonly);
    }

    @Override
    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        tags = (BHDict) value;
        textField.setText(tags.encodeToString());

        comp = (BComponent) findParentView().getCurrentValue();
        service = findService();
    }

    @Override
    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        // convert BHDict tags to niagara tags and relations on the cached component
        // and return the modified BHDict
        comp.lease();
        for (String slotName : slotsToRemove)
        {
            try
            {
                comp.remove(slotName);
            }
            catch(Exception ignore) {}
        }
        HDict hDict = HaystackSlotUtil.refactorHaystackSlot(comp, tags.getDict(), service);
        tags = BHDict.make(hDict);
        return tags;
    }

////////////////////////////////////////////////////////////////
// Popup
////////////////////////////////////////////////////////////////

    private class Popup extends Command
    {
        private Popup() { super(BHDictFE.this, null); }

        @Override
        public BImage getIcon() { return ARROW; }

        @Override
        public CommandArtifact doInvoke() { edit(); return null; }
    }

    private void edit()
    {
        BHDictEditorGroup edGroup = new BHDictEditorGroup(service, comp);
        slotsToRemove = new ArrayList<>();
        BHDictDialog dialog = BHDictDialog.make(this, edGroup);
        dialog.setBoundsCenteredOnOwner();
        dialog.open();

        BHDict result = edGroup.getTags();
        slotsToRemove = edGroup.slotsToRemove();
        if (result != null && !result.equals(tags))
        {
            tags = result;
            textField.setText(edGroup.getZinc());
            textField.relayout();
            setModified();
        }
        else if (!slotsToRemove.isEmpty()) //this will catch the last direct tag removed.
        {
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

        return kids.length == 0 ? null : kids[0];
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final int COLUMNS = 50;
    private static final BImage ARROW = BImage.make("module://icons/x16/arrowRight.png");

    private final BTextField textField;
    private final BButton button;

    private BHDict tags;

    private BComponent comp;
    private BNHaystackService service;

    private List<String> slotsToRemove;

    private final Subscriber subscriber = new Subscriber()
    {
        @Override
        public void event(BComponentEvent event) {}
    };
}
