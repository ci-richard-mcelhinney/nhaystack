//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.site;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BIcon;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.res.Resources;
import nhaystack.server.NHServer;
import nhaystack.server.Nav;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HStr;
import org.projecthaystack.HTimeZone;

/**
 *  BHSite represents a Haystack 'site' rec.
 */
@NiagaraType
public class BHSite extends BHTagged
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.site.BHSite(2979906276)1.0$ @*/
/* Generated Sun Nov 19 22:46:59 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHSite.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
      * Return default values for those tags which are essential for
      * defining this component.
      */
    @Override
    public HDict getDefaultEssentials()
    {
        return ESSENTIALS;
    }

    /**
      * Generate all the tags for this component. 
      * This will include the auto-generated tags, and
      * any other tags defined in the 'haystack' property.
      */
    @Override
    public HDict generateTags(NHServer server)
    {
        HDictBuilder hdb = new HDictBuilder();

        // add annotated
        HDict tags = getHaystack().getDict();
        hdb.add(server.getTagManager().convertAnnotatedRefTags(tags));

        // navName
        String navName = Nav.makeNavName(this, tags);
        hdb.add("navName", navName);

        // dis
        String dis = navName; 
        hdb.add("dis", dis);

        // add id
        HRef ref = server.getTagManager().makeComponentRef(this).getHRef();
        hdb.add("id", HRef.make(ref.val, dis));

        // add site
        hdb.add("site");

        // add misc other tags
        hdb.add("axType", getType().toString());
        hdb.add("axSlotPath", getSlotPath().toString());

        return hdb.toDict();
    }

    @Override
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

