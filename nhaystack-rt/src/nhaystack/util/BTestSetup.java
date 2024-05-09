//
//
//

 
package nhaystack.util;

import javax.baja.sys.*;
import javax.baja.tag.*;
import javax.baja.data.*;
import javax.baja.util.BFolder;
import javax.baja.nre.annotations.*;
import nhaystack.site.BHSite;

@NiagaraAction(name="setup")
@NiagaraAction(name="deleteSetup")
@NiagaraType

/**
 * Utility class to automatically setup a test
 * station for a user to run manual tests.
 *
 */
public class BTestSetup extends BComponent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.util.BTestSetup(1340033183)1.0$ @*/
/* Generated Mon Apr 29 21:01:25 AEST 2024 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Action "setup"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code setup} action.
   * @see #setup()
   */
  public static final Action setup = newAction(0, null);
  
  /**
   * Invoke the {@code setup} action.
   * @see #setup
   */
  public void setup() { invoke(setup, null, null); }

////////////////////////////////////////////////////////////////
// Action "deleteSetup"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code deleteSetup} action.
   * @see #deleteSetup()
   */
  public static final Action deleteSetup = newAction(0, null);
  
  /**
   * Invoke the {@code deleteSetup} action.
   * @see #deleteSetup
   */
  public void deleteSetup() { invoke(deleteSetup, null, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTestSetup.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  /**
   * Default constructor
   */
  public BTestSetup() {}

  @Override
  public void started()
  {
     
  }

  /**
   * Implementation of setup action
   */
  public void doSetup()
  {
    BHSite site = new BHSite();

    // spaces
    BFolder level0 = new BFolder();
    level0.tags().set(Tag.newTag("hs:space"));
    level0.tags().set(Tag.newTag("hs:floor"));
    level0.tags().set(Tag.newTag("hs:floorNum", 0));
    addRef(level0, site, "hs:siteRef");
      addZoneToLevel("zone1", level0);
      addZoneToLevel("zone2", level0);
      addZoneToLevel("zone3", level0);
    BFolder level1 = new BFolder(); 
    level1.tags().set(Tag.newTag("hs:space"));
    level1.tags().set(Tag.newTag("hs:floor"));
    level1.tags().set(Tag.newTag("hs:floorNum", 1));
    addRef(level1, site, "hs:siteRef");
      addZoneToLevel("zone1", level1);
      addZoneToLevel("zone2", level1);
      addZoneToLevel("zone3", level1);
    BFolder level2 = new BFolder(); 
    level2.tags().set(Tag.newTag("hs:space"));
    level2.tags().set(Tag.newTag("hs:floor"));
    level2.tags().set(Tag.newTag("hs:floorNum", 2));
    addRef(level2, site, "hs:siteRef");
      addZoneToLevel("zone1", level2);
      addZoneToLevel("zone2", level2);
      addZoneToLevel("zone3", level2);
    BFolder level3 = new BFolder();
    level3.tags().set(Tag.newTag("hs:space"));
    level3.tags().set(Tag.newTag("hs:floor"));
    level3.tags().set(Tag.newTag("hs:floorNum", 3));
    addRef(level0, site, "hs:siteRef");
      addZoneToLevel("zone1", level3);
      addZoneToLevel("zone2", level3);
      addZoneToLevel("zone3", level3);

    // equips
    BFolder ahu1 = makeAirHandler("AHU1", site);
      BFolder vav11 = makeVAV("vav11", ahu1, site);
      BFolder vav12 = makeVAV("vav12", ahu1, site);
      BFolder vav13 = makeVAV("vav13", ahu1, site);
      ahu1.add("vav11", vav11);
      ahu1.add("vav12", vav12);
      ahu1.add("vav13", vav13);
    BFolder ahu2 = makeAirHandler("AHU2", site);
      BFolder vav21 = makeVAV("vav21", ahu2, site);
      BFolder vav22 = makeVAV("vav22", ahu2, site);
      BFolder vav23 = makeVAV("vav23", ahu2, site);    
       ahu2.add("vav21", vav21);
       ahu2.add("vav22", vav22);
       ahu2.add("vav23", vav23);
    BFolder ahu3 = makeAirHandler("AHU3", site);
      ahu3.add("vav31", makeVAV("vav31", ahu3, site));
      ahu3.add("vav32", makeVAV("vav32", ahu3, site));
      ahu3.add("vav33", makeVAV("vav33", ahu3, site));
    BFolder ahu4 = makeAirHandler("AHU4", site);
      ahu4.add("vav41", makeVAV("vav31", ahu4, site));
      ahu4.add("vav42", makeVAV("vav42", ahu4, site));
      ahu4.add("vav43", makeVAV("vav43", ahu4, site));

    // make relations from equip to spaces
    addRef(ahu1, level0, "hs:spaceRef");
    addRef(vav11, (BComponent) level0.get("zone1"), "hs:spaceRef");
    addRef(vav12, (BComponent) level0.get("zone2"), "hs:spaceRef");
    addRef(vav13, (BComponent) level0.get("zone3"), "hs:spaceRef");

    addRef(ahu1, level1, "hs:spaceRef");
    addRef(vav21, (BComponent) level1.get("zone1"), "hs:spaceRef");
    addRef(vav22, (BComponent) level1.get("zone2"), "hs:spaceRef");
    addRef(vav23, (BComponent) level1.get("zone3"), "hs:spaceRef");

    addRef(ahu2, level2, "hs:spaceRef");
    addRef(ahu3, level3, "hs:spaceRef");

    // finish the build
    site.add("ahu1", ahu1);
    site.add("ahu2", ahu2);
    site.add("ahu3", ahu3);
    site.add("ahu4", ahu4);
    site.add("ground", level0);
    site.add("level1", level1);
    site.add("level2", level2);
    site.add("level3", level3);

    this.add("test_site", site);
  }

  /**
   * Removes the test site from the Niagara Station
   */
  public void doDeleteSetup()
  {
    this.remove("test_site");
  }

  private void addZoneToLevel(String zoneName, BFolder level)
  {
    BFolder zone = new BFolder();
    
    // add tags
    zone.tags().set(Tag.newTag("hs:space"));

    // add relationship
    zone.relations().add(Id.newId("hs:spaceRef"), level);

    // add zone to level
    level.add(zoneName, zone);
  }

  /**
   * Create an AHU and relate it to a site
   */
  private BFolder makeAirHandler(String name, BHSite site)
  {
    BFolder ahu = new BFolder();

    // add tags
    addTag(ahu, "hs:ahu",   BMarker.DEFAULT);
    addTag(ahu, "hs:equip", BMarker.DEFAULT);

    // add relationshops
    addRef(ahu, site, "hs:siteRef");

    return ahu;
  }

  /**
   * Create a VAV and relate it to an AHU and a site
   */
  private BFolder makeVAV(String name, BFolder ahu, BHSite site)
  {
    BFolder vav = new BFolder();

    // add tags
    addTag(vav, "hs:vav", BMarker.DEFAULT);
    addTag(vav, "hs:equip", BMarker.DEFAULT);

    // add relationships
    addRef(vav, ahu, "hs:equipRef");
    addRef(vav, site, "hs:siteRef");

    // add to vav as a container
    return vav;
  }

  /**
   * Helper method to add a tag
   */
  private void addTag(BComponent c, String tagName, BIDataValue tagType)
  {
    if (c == null || tagName == null || tagType == null) return;
    if (tagName.length() <= 0) return;

    c.tags().set(Id.newId(tagName), tagType);
  }

  /**
   * Helper method to add a ref or Niagara Relationship
   */
  private void addRef(BComponent src, BComponent target, String name)
  {
    if (src == null || target == null || name == null) return;
    if (name.length() <= 0) return;

    src.relations().add(Id.newId(name), target);
  }
}
