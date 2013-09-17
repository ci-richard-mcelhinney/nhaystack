//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Feb 2013  Mike Jarmy Creation
//

package nhaystack.ui;

import java.util.*;

import javax.baja.sys.*;
import javax.baja.ui.*;
import javax.baja.ui.event.*;
import javax.baja.ui.list.*;
import javax.baja.ui.pane.*;
import javax.baja.workbench.*;
import javax.baja.workbench.fieldeditor.*;

import org.projecthaystack.*;
import nhaystack.*;
import nhaystack.res.*;

/**
  * BHTimeZoneFE edits a Haystack timezone.
  */
public class BHTimeZoneFE extends BWbFieldEditor
{
    /*-
    class BHTimeZoneFE
    {
        actions
        {
            regionsModified(event: BWidgetEvent) default {[ new BWidgetEvent() ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHTimeZoneFE(2453278263)1.0$ @*/
/* Generated Sat May 04 12:25:24 GMT-05:00 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Action "regionsModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>regionsModified</code> action.
   * @see nhaystack.ui.BHTimeZoneFE#regionsModified()
   */
  public static final Action regionsModified = newAction(0,new BWidgetEvent(),null);
  
  /**
   * Invoke the <code>regionsModified</code> action.
   * @see nhaystack.ui.BHTimeZoneFE#regionsModified
   */
  public void regionsModified(BWidgetEvent event) { invoke(regionsModified,event,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHTimeZoneFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHTimeZoneFE()
    {
        Iterator it = zonesByRegion.keySet().iterator();
        while (it.hasNext())
            regionDropDown.getList().addItem(it.next());

        linkTo(regionDropDown, BDropDown.valueModified, regionsModified);
        linkTo(tzDropDown, BDropDown.valueModified, BWbPlugin.setModified);

        gridPane.add(null, regionDropDown);
        gridPane.add(null, tzDropDown);

        setContent(gridPane);
    }

    protected void doSetReadonly(boolean readonly)
    {
        tzDropDown.setEnabled(!readonly);
    }

    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        HTimeZone tz = ((BHTimeZone) value).getTimeZone();

        String region = (String) regionsByZone.get(tz.name);
        populateTzDropDown(region, tzDropDown);

        regionDropDown.setSelectedItem(region);
        tzDropDown.setSelectedItem(tz.name);
        gridPane.relayout();

        loaded = true;
    }

    protected BObject doSaveValue(BObject value, Context cx) throws Exception
    {
        return BHTimeZone.make(HTimeZone.make((String) tzDropDown.getSelectedItem()));
    }

    public void doRegionsModified(BWidgetEvent event)
    {
        if (!loaded) return;

        setModified();

        String region = (String) regionDropDown.getList().getSelectedItem();
        populateTzDropDown(region, tzDropDown);
        tzDropDown.getList().setSelectedIndex(0);

        gridPane.relayout();
    }

    private static void populateTzDropDown(String region, BListDropDown tzDropDown)
    {
        tzDropDown.getList().removeAllItems();

        TreeSet zones = (TreeSet) zonesByRegion.get(region);
        Iterator it = zones.iterator();
        while (it.hasNext())
            tzDropDown.getList().addItem(it.next());
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private boolean loaded = false;

    private BGridPane gridPane = new BGridPane();
    private BListDropDown regionDropDown = new BListDropDown();
    private BListDropDown tzDropDown = new BListDropDown();

    private static final Map regionsByZone = new HashMap(); // String -> String
    private static final Map zonesByRegion = new TreeMap(); // String -> TreeSet<String>

    static
    {
        String[] ids = TimeZone.getAvailableIDs();
        for (int i=0; i<ids.length; ++i)
        {
            String java = ids[i];

            // skip ids not formatted as Region/City
            int slash = java.indexOf('/');
            if (slash < 0) continue;
            String region = java.substring(0, slash);
            if (!BHTimeZone.TZ_REGIONS.contains(region)) continue;

            // get city name as haystack id
            slash = java.lastIndexOf('/');
            String zone = java.substring(slash+1);

            regionsByZone.put(zone, region);

            Set zones = (Set) zonesByRegion.get(region);
            if (zones == null) zonesByRegion.put(
                region, zones = new TreeSet());
            zones.add(zone);
        }
    }
}
