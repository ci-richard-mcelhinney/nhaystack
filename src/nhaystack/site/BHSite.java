//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//

package nhaystack.site;

import javax.baja.sys.*;
import javax.baja.util.*;

import haystack.*;
import nhaystack.*;
import nhaystack.res.*;
import nhaystack.ui.*;

/**
 *  BHSite represents a Haystack 'site' rec.
 */
public class BHSite extends BHTagged
{
    /*-
    class BHSite
    {
        properties
        {
            haystackNav: BFormat default{[ BFormat.make("%name%") ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.site.BHSite(269428171)1.0$ @*/
/* Generated Sun Feb 10 10:55:18 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "haystackNav"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystackNav</code> property.
   * @see nhaystack.site.BHSite#getHaystackNav
   * @see nhaystack.site.BHSite#setHaystackNav
   */
  public static final Property haystackNav = newProperty(0, BFormat.make("%name%"),null);
  
  /**
   * Get the <code>haystackNav</code> property.
   * @see nhaystack.site.BHSite#haystackNav
   */
  public BFormat getHaystackNav() { return (BFormat)get(haystackNav); }
  
  /**
   * Set the <code>haystackNav</code> property.
   * @see nhaystack.site.BHSite#haystackNav
   */
  public void setHaystackNav(BFormat v) { set(haystackNav,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHSite.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public SiteNavId makeNavId()
    {
        return SiteNavId.make(getHaystackNav().format(this));
    }

    /**
      * Return default values for those tags which are essential for
      * defining this component.
      */
    public HDict getDefaultEssentials()
    {
        return ESSENTIALS;
    }

    /**
      * Generate all the tags for this component. 
      * This will include the auto-generated tags, and
      * any other tags defined in the 'haystack' property.
      */
    public HDict generateTags()
    {
        HDictBuilder hdb = new HDictBuilder();

        // add annotated
        hdb.add(getHaystack().getDict());

        // add id and site
        hdb.add("id", NHRef.make(this).getHRef());
        hdb.add("site");

        // add dis
//        String dis = getDisplayName(null);
//        if (dis != null) hdb.add("dis", dis);
        hdb.add("dis", getHaystackNav().format(this));

        // add misc other tags
        hdb.add("axType", getType().toString());
        hdb.add("axSlotPath", getSlotPath().toString());

        return hdb.toDict();
    }

    public BIcon getIcon() { return ICON; }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/site.png");

    private static final String SQUARE_FOOT = Resources.getUnits("area", "square_foot")[0].symbol;

    private static final HDict ESSENTIALS;
    static
    {
        HDictBuilder hd = new HDictBuilder();

        hd.add("area",          HNum.make(0.0,SQUARE_FOOT));
        hd.add("tz",            HStr.make(HTimeZone.DEFAULT.name));
        hd.add("geoAddr",       HStr.make(""));
        hd.add("geoStreet",     HStr.make(""));
        hd.add("geoCity",       HStr.make(""));
        hd.add("geoCountry",    HStr.make(""));
        hd.add("geoPostalCode", HStr.make(""));
        hd.add("geoState",      HStr.make(""));
        hd.add("geoLat",        HNum.make(0.0));
        hd.add("geoLon",        HNum.make(0.0));
        hd.add("geoLon",        HNum.make(0.0));
//        hd.add("weather",       HRef.make("null")); TODO
        ESSENTIALS = hd.toDict();
    }
}

