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
@NiagaraType

/**
 * Utility class to automatically setup a test
 * station for a user to run manual tests.
 *
 */
public class BTestSetup extends BComponent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.util.BTestSetup(84644805)1.0$ @*/
/* Generated Wed Apr 24 20:55:34 AEST 2024 by Slot-o-Matic (c) Tridium, Inc. 2012 */

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
      addZoneToLevel("zone1", level0);
      addZoneToLevel("zone2", level0);
      addZoneToLevel("zone3", level0);
    BFolder level1 = new BFolder(); 
      addZoneToLevel("zone1", level1);
      addZoneToLevel("zone2", level1);
      addZoneToLevel("zone3", level1);
    BFolder level2 = new BFolder(); 
      addZoneToLevel("zone1", level2);
      addZoneToLevel("zone2", level2);
      addZoneToLevel("zone3", level2);
    BFolder level3 = new BFolder();
      addZoneToLevel("zone1", level3);
      addZoneToLevel("zone2", level3);
      addZoneToLevel("zone3", level3);

    // equips
    

    // finish the build
    site.add("ground", level0);
    site.add("level1", level1);
    site.add("level2", level2);
    site.add("level3", level3);

    this.add("test_site", site);

  }

  private void addZoneToLevel(String zoneName, BFolder level)
  {
    BFolder zone = new BFolder();
    
    // add tags
    Tag spaceTag = Tag.newTag("hs:space");
    zone.tags().set(spaceTag);
    
    // add relationship
    zone.relations().add(Id.newId("hs:spaceRef"), level);

    // add zone to level
    level.add(zoneName, zone);
  }

  /**
   * Create an AHU and relate it to a site
   */
  private void makeAirHandler(String name, BHSite site)
  {
    BFolder ahu = new BFolder();

    // add tags
    addTag(ahu, "hs:ahu",   BMarker.DEFAULT);
    addTag(ahu, "hs:equip", BMarker.DEFAULT);

    // add relationshops
    addRef(ahu, site, "hs:siteRef");

    // add ahu to site
  }

  /**
   * Create a VAV and relate it to an AHU and a site
   */
  private void makeVAV(String name, BFolder ahu, BHSite site)
  {

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
