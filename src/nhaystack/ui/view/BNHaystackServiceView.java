//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Apr 2013  Mike Jarmy  Creation
//
package nhaystack.ui.view;

import javax.baja.gx.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.pane.*;
import javax.baja.util.*;
import javax.baja.workbench.view.*;

/**
  * BNHaystackServiceView is a view on BNHaystackService
  */
public class BNHaystackServiceView extends BWbComponentView
{
    /*-
    class BNHaystackServiceView
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BNHaystackServiceView(1870466022)1.0$ @*/
/* Generated Tue Apr 09 09:47:22 EDT 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackServiceView.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackServiceView()
    {
        addSlot = new BAddHaystackSlot(this);
        addSlot.setEditable(false);
        addSlot.setText(LEX.getText("dragHere"));

        BConstrainedPane cons = new BConstrainedPane(addSlot);
        cons.setMinHeight(60);

        BBorderPane border = new BBorderPane(cons);
        border.setBorder(BBorder.make("inset"));
        border.setPadding(BInsets.make(3, 3, 3, 3));

        BEdgePane edge = new BEdgePane();
        edge.setTop(border);

        setContent(edge);
    }

   public void stopped() throws Exception 
   {
       super.stopped();
       unregisterForAllComponentEvents();
   }

////////////////////////////////////////////////////////////////
// Attributes 
////////////////////////////////////////////////////////////////

    private static final Lexicon LEX = Lexicon.make("nhaystack");

    private BAddHaystackSlot addSlot;
}
