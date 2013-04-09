//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 Apr 2013  Mike Jarmy  Creation
//
package nhaystack.ui.view;

import javax.baja.gx.*;
import javax.baja.space.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.text.*;
import javax.baja.ui.transfer.*;
import nhaystack.*;

/**
  * BAddHaystackSlot adds a "haystack" BHDict slot to BComponents that
  * are dropped on it. 
  */
public class BAddHaystackSlot extends BTextEditor
{
    /*-
    class BAddHaystackSlot
    {
    }
  -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.view.BAddHaystackSlot(1390518057)1.0$ @*/
/* Generated Tue Apr 09 10:10:53 EDT 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BAddHaystackSlot.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BAddHaystackSlot() { }

    public BAddHaystackSlot(BNHaystackServiceView view)
    {
        this.view = view;
    }

    public int dragOver(TransferContext ctx)
    {
        Mark mark = (Mark) ctx.getEnvelope().getData(TransferFormat.mark);
        for (int i = 0; i < mark.size(); i++)
        {
            BObject obj = mark.getValue(i);
            if (obj instanceof BComponent)
                return TransferConst.ACTION_COPY;
        }
        return 0;
    }

    public CommandArtifact drop(TransferContext ctx)
        throws Exception
    {
        Mark mark = (Mark) ctx.getEnvelope().getData(TransferFormat.mark);
        for (int i = 0; i < mark.size(); i++)
        {
            BObject obj = mark.getValue(i);
            if (obj instanceof BComponent)
            {
                BComponent comp = (BComponent) obj;
                view.registerForComponentEvents(comp, 1);

                BHDict dict = (BHDict) comp.get("haystack");
                if (dict == null)
                    comp.add("haystack", BHDict.DEFAULT);
            }
        }

        return null;
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private BNHaystackServiceView view;
}
