//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//

package nhaystack.site;

import javax.baja.sys.*;

import org.projecthaystack.*;
import nhaystack.res.*;
import nhaystack.server.*;

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
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.site.BHSite(875174785)1.0$ @*/
/* Generated Fri Mar 29 12:39:07 EDT 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHSite.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

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
    public HDict generateTags(NHServer server)
    {
        HDictBuilder hdb = new HDictBuilder();

        // add annotated
        HDict tags = getHaystack().getDict();
        hdb.add(server.convertAnnotatedRefTags(tags));

        // navName
        String navName = Nav.makeNavName(this, tags);
        hdb.add("navName", navName);

        // dis
        String dis = navName; 
        hdb.add("dis", dis);

        // add id
        HRef ref = server.makeComponentRef(this).getHRef();
        hdb.add("id", HRef.make(ref.val, dis));

        // add site
        hdb.add("site");

        // add misc other tags
        hdb.add("axType", getType().toString());
        hdb.add("axSlotPath", getSlotPath().toString());

        return hdb.toDict();
    }

    public BIcon getIcon() { return ICON; }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    public static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/site.png");

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

