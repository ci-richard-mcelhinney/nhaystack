//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   23 Apr 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//

package nhaystack.ui;

import javax.baja.gx.BInsets;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.nre.util.TextUtil;
import javax.baja.sys.Action;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BDropDown;
import javax.baja.ui.BListDropDown;
import javax.baja.ui.enums.BHalign;
import javax.baja.ui.enums.BValign;
import javax.baja.ui.pane.BBorderPane;
import javax.baja.ui.pane.BEdgePane;
import javax.baja.ui.pane.BGridPane;
import nhaystack.res.Resources;

@NiagaraType
@NiagaraAction(
  name = "setsModified"
)
public class BMarkerSet extends BEdgePane
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BMarkerSet(3744666326)1.0$ @*/
/* Generated Mon Nov 20 13:21:13 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Action "setsModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code setsModified} action.
   * @see #setsModified()
   */
  public static final Action setsModified = newAction(0, null);
  
  /**
   * Invoke the {@code setsModified} action.
   * @see #setsModified
   */
  public void setsModified() { invoke(setsModified, null, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BMarkerSet.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BMarkerSet()
    {
        markerSetsDropDown = new BListDropDown();
        markerSetTagsDropDown = new BListDropDown();

        BGridPane grid = new BGridPane(3); 
        grid.setHalign(BHalign.left);
        grid.setValign(BValign.top);
        grid.add(null, markerSetsDropDown);
        grid.add(null, markerSetTagsDropDown);
        BBorderPane border = new BBorderPane(grid);
        border.setPadding(BInsets.make(4));

        String[] markerSets = Resources.getMarkerSets();
        for (String markerSet : markerSets)
          markerSetsDropDown.getList().addItem(markerSet);

        markerSetsDropDown.setSelectedItem(markerSets[0]);

        String[] markerSetTags = Resources.getMarkerSetTags(markerSets[0]);
        for (String markerSetTag : markerSetTags)
          markerSetTagsDropDown.getList().addItem(markerSetTag);

        markerSetTagsDropDown.setSelectedItem(markerSetTags[0]);

        linkTo(markerSetsDropDown, BDropDown.valueModified, setsModified);
        setCenter(border);
    }

    public void doSetsModified()
    {
        String ms = (String) markerSetsDropDown.getSelectedItem();

        markerSetTagsDropDown.setSelectedIndex(-1);
        markerSetTagsDropDown.getList().removeAllItems();

        String[] markerSetTags = Resources.getMarkerSetTags(ms);
        for (String markerSetTag : markerSetTags)
          markerSetTagsDropDown.getList().addItem(markerSetTag);

        markerSetTagsDropDown.setSelectedItem(markerSetTags[0]);
    }

    public String[] getMarkers()
    {
        String mt = (String) markerSetTagsDropDown.getSelectedItem();
        return TextUtil.split(mt, ' ');
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private final BListDropDown markerSetsDropDown;
    private final BListDropDown markerSetTagsDropDown;
}
