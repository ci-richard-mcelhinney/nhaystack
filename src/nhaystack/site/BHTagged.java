//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//

package nhaystack.site;

import javax.baja.sys.*;

import haystack.*;
import nhaystack.*;
import nhaystack.server.*;

/**
 * A BHTagged is a BComponent that is tagged
 * with haystack properties in a stylized way.
 */
public abstract class BHTagged extends BComponent
{
    /*-
    class BHTagged
    {
        properties
        {
            haystack:  BHDict 
                default{[ BHDict.DEFAULT ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.site.BHTagged(2191060138)1.0$ @*/
/* Generated Fri Mar 29 19:40:38 EDT 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "haystack"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystack</code> property.
   * @see nhaystack.site.BHTagged#getHaystack
   * @see nhaystack.site.BHTagged#setHaystack
   */
  public static final Property haystack = newProperty(0, BHDict.DEFAULT,null);
  
  /**
   * Get the <code>haystack</code> property.
   * @see nhaystack.site.BHTagged#haystack
   */
  public BHDict getHaystack() { return (BHDict)get(haystack); }
  
  /**
   * Set the <code>haystack</code> property.
   * @see nhaystack.site.BHTagged#haystack
   */
  public void setHaystack(BHDict v) { set(haystack,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
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

