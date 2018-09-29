//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy       Creation
//   08 May 2018  Eric Anderson    Migrated to slot annotations, added missing @Overrides annotations
//   05 Sep 2018  Andrew Saunders  Added essential tags as frozen tags so they would be available in
//                                 the Niagara tagging system.  tz is excluded because it is implied
//                                 by the Niagara Haystack dictionary.
//

package nhaystack.site;


import javax.baja.nre.annotations.Facet;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIcon;
import javax.baja.sys.BMarker;
import javax.baja.sys.BString;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Slot;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

import nhaystack.res.Resources;
import nhaystack.server.NHServer;
import nhaystack.server.Nav;
import nhaystack.util.NHaystackConst;
import org.projecthaystack.HCoord;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HStr;
import org.projecthaystack.HTimeZone;

/**
 *  BHSite represents a Haystack 'site' rec.
 */
@SuppressWarnings("DollarSignInName")
@NiagaraType
@NiagaraProperty(
    name = "hs$3aarea",
    type = "double",
    flags = Flags.METADATA,
    defaultValue = "0.0"
)
@NiagaraProperty(
    name = "hs$3ageoAddr",
    type = "String",
    flags = Flags.METADATA,
    defaultValue = ""
)
@NiagaraProperty(
    name = "hs$3ageoCity",
    type = "String",
    flags = Flags.METADATA,
    defaultValue = ""
)
@NiagaraProperty(
    name = "hs$3ageoCoord",
    type = "String",
    flags = Flags.METADATA,
    defaultValue = "\"C(0,0)\"",
    facets = @Facet(name = "BFacets.FIELD_EDITOR", value = "BString.make(\"nhaystack:HCoordFE\")")
)
@NiagaraProperty(
    name = "hs$3ageoCountry",
    type = "String",
    flags = Flags.METADATA,
    defaultValue = ""
)
@NiagaraProperty(
    name = "hs$3ageoPostalCode",
    type = "String",
    flags = Flags.METADATA,
    defaultValue = ""
)
@NiagaraProperty(
    name = "hs$3ageoState",
    type = "String",
    flags = Flags.METADATA,
    defaultValue = ""
)
@NiagaraProperty(
    name = "hs$3ageoStreet",
    type = "String",
    flags = Flags.METADATA,
    defaultValue = ""
)
@NiagaraProperty(
    name = "hs$3asite",
    type = "BMarker",
    flags = Flags.METADATA,
    defaultValue = "BMarker.DEFAULT"
)
public class BHSite extends BHTagged
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.site.BHSite(3540364429)1.0$ @*/
/* Generated Thu Sep 27 17:29:58 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "hs$3aarea"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code hs$3aarea} property.
   * @see #getHs$3aarea
   * @see #setHs$3aarea
   */
  public static final Property hs$3aarea = newProperty(Flags.METADATA, 0.0, null);
  
  /**
   * Get the {@code hs$3aarea} property.
   * @see #hs$3aarea
   */
  public double getHs$3aarea() { return getDouble(hs$3aarea); }
  
  /**
   * Set the {@code hs$3aarea} property.
   * @see #hs$3aarea
   */
  public void setHs$3aarea(double v) { setDouble(hs$3aarea, v, null); }

////////////////////////////////////////////////////////////////
// Property "hs$3ageoAddr"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code hs$3ageoAddr} property.
   * @see #getHs$3ageoAddr
   * @see #setHs$3ageoAddr
   */
  public static final Property hs$3ageoAddr = newProperty(Flags.METADATA, "", null);
  
  /**
   * Get the {@code hs$3ageoAddr} property.
   * @see #hs$3ageoAddr
   */
  public String getHs$3ageoAddr() { return getString(hs$3ageoAddr); }
  
  /**
   * Set the {@code hs$3ageoAddr} property.
   * @see #hs$3ageoAddr
   */
  public void setHs$3ageoAddr(String v) { setString(hs$3ageoAddr, v, null); }

////////////////////////////////////////////////////////////////
// Property "hs$3ageoCity"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code hs$3ageoCity} property.
   * @see #getHs$3ageoCity
   * @see #setHs$3ageoCity
   */
  public static final Property hs$3ageoCity = newProperty(Flags.METADATA, "", null);
  
  /**
   * Get the {@code hs$3ageoCity} property.
   * @see #hs$3ageoCity
   */
  public String getHs$3ageoCity() { return getString(hs$3ageoCity); }
  
  /**
   * Set the {@code hs$3ageoCity} property.
   * @see #hs$3ageoCity
   */
  public void setHs$3ageoCity(String v) { setString(hs$3ageoCity, v, null); }

////////////////////////////////////////////////////////////////
// Property "hs$3ageoCoord"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code hs$3ageoCoord} property.
   * @see #getHs$3ageoCoord
   * @see #setHs$3ageoCoord
   */
  public static final Property hs$3ageoCoord = newProperty(Flags.METADATA, "C(0,0)", BFacets.make(BFacets.FIELD_EDITOR, BString.make("nhaystack:HCoordFE")));
  
  /**
   * Get the {@code hs$3ageoCoord} property.
   * @see #hs$3ageoCoord
   */
  public String getHs$3ageoCoord() { return getString(hs$3ageoCoord); }
  
  /**
   * Set the {@code hs$3ageoCoord} property.
   * @see #hs$3ageoCoord
   */
  public void setHs$3ageoCoord(String v) { setString(hs$3ageoCoord, v, null); }

////////////////////////////////////////////////////////////////
// Property "hs$3ageoCountry"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code hs$3ageoCountry} property.
   * @see #getHs$3ageoCountry
   * @see #setHs$3ageoCountry
   */
  public static final Property hs$3ageoCountry = newProperty(Flags.METADATA, "", null);
  
  /**
   * Get the {@code hs$3ageoCountry} property.
   * @see #hs$3ageoCountry
   */
  public String getHs$3ageoCountry() { return getString(hs$3ageoCountry); }
  
  /**
   * Set the {@code hs$3ageoCountry} property.
   * @see #hs$3ageoCountry
   */
  public void setHs$3ageoCountry(String v) { setString(hs$3ageoCountry, v, null); }

////////////////////////////////////////////////////////////////
// Property "hs$3ageoPostalCode"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code hs$3ageoPostalCode} property.
   * @see #getHs$3ageoPostalCode
   * @see #setHs$3ageoPostalCode
   */
  public static final Property hs$3ageoPostalCode = newProperty(Flags.METADATA, "", null);
  
  /**
   * Get the {@code hs$3ageoPostalCode} property.
   * @see #hs$3ageoPostalCode
   */
  public String getHs$3ageoPostalCode() { return getString(hs$3ageoPostalCode); }
  
  /**
   * Set the {@code hs$3ageoPostalCode} property.
   * @see #hs$3ageoPostalCode
   */
  public void setHs$3ageoPostalCode(String v) { setString(hs$3ageoPostalCode, v, null); }

////////////////////////////////////////////////////////////////
// Property "hs$3ageoState"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code hs$3ageoState} property.
   * @see #getHs$3ageoState
   * @see #setHs$3ageoState
   */
  public static final Property hs$3ageoState = newProperty(Flags.METADATA, "", null);
  
  /**
   * Get the {@code hs$3ageoState} property.
   * @see #hs$3ageoState
   */
  public String getHs$3ageoState() { return getString(hs$3ageoState); }
  
  /**
   * Set the {@code hs$3ageoState} property.
   * @see #hs$3ageoState
   */
  public void setHs$3ageoState(String v) { setString(hs$3ageoState, v, null); }

////////////////////////////////////////////////////////////////
// Property "hs$3ageoStreet"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code hs$3ageoStreet} property.
   * @see #getHs$3ageoStreet
   * @see #setHs$3ageoStreet
   */
  public static final Property hs$3ageoStreet = newProperty(Flags.METADATA, "", null);
  
  /**
   * Get the {@code hs$3ageoStreet} property.
   * @see #hs$3ageoStreet
   */
  public String getHs$3ageoStreet() { return getString(hs$3ageoStreet); }
  
  /**
   * Set the {@code hs$3ageoStreet} property.
   * @see #hs$3ageoStreet
   */
  public void setHs$3ageoStreet(String v) { setString(hs$3ageoStreet, v, null); }

////////////////////////////////////////////////////////////////
// Property "hs$3asite"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code hs$3asite} property.
   * @see #getHs$3asite
   * @see #setHs$3asite
   */
  public static final Property hs$3asite = newProperty(Flags.METADATA, BMarker.DEFAULT, null);
  
  /**
   * Get the {@code hs$3asite} property.
   * @see #hs$3asite
   */
  public BMarker getHs$3asite() { return (BMarker)get(hs$3asite); }
  
  /**
   * Set the {@code hs$3asite} property.
   * @see #hs$3asite
   */
  public void setHs$3asite(BMarker v) { set(hs$3asite, v, null); }

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
    public BFacets getSlotFacets(Slot slot)
    {
        if(slot.getName().equals(NHaystackConst.TN_TZ))
        {
            return BFacets.make(BFacets.FIELD_EDITOR, BString.make("nhaystack:HTimeZoneFE"));
        }
        return super.getSlotFacets(slot);
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
        hd.add("geoCoord",      HCoord.make(0, 0));
//        hd.add("weather",       HRef.make("null")); TODO
        ESSENTIALS = hd.toDict();
    }
}

