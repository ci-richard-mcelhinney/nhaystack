//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   23 Apr 2013  Mike Jarmy  Creation
//

package nhaystack.ui;

import javax.baja.gx.*;
import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.list.*;
import javax.baja.ui.enums.*;
import javax.baja.ui.pane.*;
import javax.baja.util.*;

import nhaystack.res.*;

public class BMarkerSet extends BEdgePane
{
    /*-
    class BMarkerSet
    {
        actions
        {
            setsModified()
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BMarkerSet(3020760542)1.0$ @*/
/* Generated Tue Apr 23 18:43:23 EDT 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Action "setsModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>setsModified</code> action.
   * @see nhaystack.ui.BMarkerSet#setsModified()
   */
  public static final Action setsModified = newAction(0,null);
  
  /**
   * Invoke the <code>setsModified</code> action.
   * @see nhaystack.ui.BMarkerSet#setsModified
   */
  public void setsModified() { invoke(setsModified,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BMarkerSet.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BMarkerSet()
    {
        this.markerSets = new BListDropDown();
        this.markerSetTags = new BListDropDown();

        BGridPane grid = new BGridPane(3); 
        grid.setHalign(BHalign.left);
        grid.setValign(BValign.top);
        grid.add(null, markerSets);
        grid.add(null, markerSetTags);
        BBorderPane border = new BBorderPane(grid);
        border.setPadding(BInsets.make(4));

        String[] ms = Resources.getMarkerSets();
        for (int i = 0; i < ms.length; i++)
            markerSets.getList().addItem(ms[i]);
        markerSets.setSelectedItem(ms[0]);

        String[] mt = Resources.getMarkerSetTags(ms[0]);
        for (int i = 0; i < mt.length; i++)
            markerSetTags.getList().addItem(mt[i]);
        markerSetTags.setSelectedItem(mt[0]);

        linkTo(markerSets, BDropDown.valueModified, setsModified);  
        setCenter(border);
    }

    public void doSetsModified()
    {
        String ms = (String) markerSets.getSelectedItem();

        markerSetTags.setSelectedIndex(-1);
        markerSetTags.getList().removeAllItems();

        String[] mt = Resources.getMarkerSetTags(ms);
        for (int i = 0; i < mt.length; i++)
            markerSetTags.getList().addItem(mt[i]);
        markerSetTags.setSelectedItem(mt[0]);
    }

    public String[] getMarkers()
    {
        String mt = (String) markerSetTags.getSelectedItem();
        return TextUtil.split(mt, ' ');
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private BListDropDown markerSets;
    private BListDropDown markerSetTags;
}
