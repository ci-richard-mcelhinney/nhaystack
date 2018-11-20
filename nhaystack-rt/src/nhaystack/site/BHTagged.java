//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.site;

import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHDict;
import nhaystack.server.NHServer;
import org.projecthaystack.HDict;

/**
 * A BHTagged is a BComponent that is tagged
 * with haystack properties in a stylized way.
 */
@NiagaraType
@NiagaraProperty(
  name = "haystack",
  type = "BHDict",
  defaultValue = "BHDict.DEFAULT"
)
public abstract class BHTagged extends BComponent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.site.BHTagged(1147514850)1.0$ @*/
/* Generated Sun Nov 19 22:47:54 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "haystack"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code haystack} property.
   * @see #getHaystack
   * @see #setHaystack
   */
  public static final Property haystack = newProperty(0, BHDict.DEFAULT, null);
  
  /**
   * Get the {@code haystack} property.
   * @see #haystack
   */
  public BHDict getHaystack() { return (BHDict)get(haystack); }
  
  /**
   * Set the {@code haystack} property.
   * @see #haystack
   */
  public void setHaystack(BHDict v) { set(haystack, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHTagged.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
      * Return default values for those tags which are essential for
      * defining this component.
      */
    public abstract HDict getDefaultEssentials();

    /**
      * Generate all the tags for this component. 
      * This will include the auto-generated tags, and
      * any other tags defined in the 'haystack' property.
      */
    public abstract HDict generateTags(NHServer server);
}

