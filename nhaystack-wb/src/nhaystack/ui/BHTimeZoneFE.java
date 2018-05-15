//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Feb 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations,
//                               added use of generics
//

package nhaystack.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.baja.nre.annotations.AgentOn;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Action;
import javax.baja.sys.BObject;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.ui.BDropDown;
import javax.baja.ui.BListDropDown;
import javax.baja.ui.event.BWidgetEvent;
import javax.baja.ui.pane.BGridPane;
import javax.baja.workbench.BWbPlugin;
import javax.baja.workbench.fieldeditor.BWbFieldEditor;
import nhaystack.BHTimeZone;
import org.projecthaystack.HTimeZone;

/**
  * BHTimeZoneFE edits a Haystack timezone.
  */
@NiagaraType(
  agent =   @AgentOn(
    types = "nhaystack:HTimeZone"
  )
)
@NiagaraAction(
  name = "regionsModified",
  parameterType = "BWidgetEvent",
  defaultValue = "new BWidgetEvent()"
)
public class BHTimeZoneFE extends BWbFieldEditor
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ui.BHTimeZoneFE(2508526793)1.0$ @*/
/* Generated Mon Nov 20 13:12:15 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Action "regionsModified"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code regionsModified} action.
   * @see #regionsModified(BWidgetEvent parameter)
   */
  public static final Action regionsModified = newAction(0, new BWidgetEvent(), null);
  
  /**
   * Invoke the {@code regionsModified} action.
   * @see #regionsModified
   */
  public void regionsModified(BWidgetEvent parameter) { invoke(regionsModified, parameter, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHTimeZoneFE.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BHTimeZoneFE()
    {
        for (String s : zonesByRegion.keySet())
            regionDropDown.getList().addItem(s);

        linkTo(regionDropDown, BDropDown.valueModified, regionsModified);
        linkTo(tzDropDown, BDropDown.valueModified, BWbPlugin.setModified);

        gridPane.add(null, regionDropDown);
        gridPane.add(null, tzDropDown);

        setContent(gridPane);
    }

    @Override
    protected void doSetReadonly(boolean readonly)
    {
        tzDropDown.setEnabled(!readonly);
    }

    @Override
    protected void doLoadValue(BObject value, Context cx) throws Exception
    {
        HTimeZone tz = ((BHTimeZone) value).getTimeZone();

        String region = regionsByZone.get(tz.name);
        populateTzDropDown(region, tzDropDown);

        regionDropDown.setSelectedItem(region);
        tzDropDown.setSelectedItem(tz.name);
        gridPane.relayout();

        loaded = true;
    }

    @Override
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

        for (String zone : zonesByRegion.get(region))
            tzDropDown.getList().addItem(zone);
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private boolean loaded;

    private final BGridPane gridPane = new BGridPane();
    private final BListDropDown regionDropDown = new BListDropDown();
    private final BListDropDown tzDropDown = new BListDropDown();

    private static final Map<String, String> regionsByZone = new HashMap<>();
    private static final Map<String, Set<String>> zonesByRegion = new TreeMap<>();

    static
    {
        String[] ids = TimeZone.getAvailableIDs();
        for (String id : ids)
        {
            // skip ids not formatted as Region/City
            int slash = id.indexOf('/');
            if (slash < 0) continue;
            String region = id.substring(0, slash);
            if (!BHTimeZone.TZ_REGIONS.contains(region)) continue;

            // get city name as haystack id
            slash = id.lastIndexOf('/');
            String zone = id.substring(slash + 1);

            regionsByZone.put(zone, region);
            zonesByRegion.computeIfAbsent(region, k -> new TreeSet<>()).add(zone);
        }
    }
}
