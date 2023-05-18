//
// Copyright (c) 2018. Tridium, Inc. All rights reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Aug 2018  Andrew Saunders  Creation
//   21 Dec 2018  Andrew Saunders  Allowing plain components to be used as sites and equips
//   13 Mar 2019  Andrew Saunders  Added point, point ID, and dictionary version constants
//   12 Apr 2019  Eric Anderson    Added vav and ahu constants
//

package nhaystack.util;

import com.tridium.haystack.BHsTagDictionary;

import javax.baja.tag.Id;

/**
 * Defines haystack dictionary constants.
 */
public interface NHaystackConst
{
    String NAME_SPACE = "hs";

    String AHU             = "ahu";
    String AREA            = "area";
    String EQUIP           = "equip";
    String EQUIP_REF       = "equipRef";
    String GEO_ADDR        = "geoAddr";
    String GEO_CITY        = "geoCity";
    String GEO_COORD       = "geoCoord";
    String GEO_COUNTRY     = "geoCountry";
    String GEO_LAT         = "geoLat";
    String GEO_LON         = "geoLon";
    String GEO_POSTAL_CODE = "geoPostalCode";
    String GEO_STATE       = "geoState";
    String GEO_STREET      = "geoStreet";
    String POINT           = "point";
    String SITE            = "site";
    String SITE_REF        = "siteRef";
    String SPACE           = "space";
    String SPACE_REF       = "spaceRef";
    String TZ              = "tz";
    String VAV             = "vav";

    Id ID_AHU             = Id.newId(NAME_SPACE, AHU);
    Id ID_AREA            = Id.newId(NAME_SPACE, AREA);
    Id ID_EQUIP           = Id.newId(NAME_SPACE, EQUIP);
    Id ID_EQUIP_REF       = Id.newId(NAME_SPACE, EQUIP_REF);
    Id ID_GEO_ADDR        = Id.newId(NAME_SPACE, GEO_ADDR);
    Id ID_GEO_CITY        = Id.newId(NAME_SPACE, GEO_CITY);
    Id ID_GEO_COORD       = Id.newId(NAME_SPACE, GEO_COORD);
    Id ID_GEO_COUNTRY     = Id.newId(NAME_SPACE, GEO_COUNTRY);
    Id ID_GEO_LAT         = Id.newId(NAME_SPACE, GEO_LAT);
    Id ID_GEO_LON         = Id.newId(NAME_SPACE, GEO_LON);
    Id ID_GEO_POSTAL_CODE = Id.newId(NAME_SPACE, GEO_POSTAL_CODE);
    Id ID_GEO_STATE       = Id.newId(NAME_SPACE, GEO_STATE);
    Id ID_GEO_STREET      = Id.newId(NAME_SPACE, GEO_STREET);
    Id ID_POINT           = Id.newId(NAME_SPACE, POINT);
    Id ID_SITE            = Id.newId(NAME_SPACE, SITE);
    Id ID_SITE_REF        = Id.newId(NAME_SPACE, SITE_REF);
    Id ID_SPACE           = Id.newId(NAME_SPACE, SPACE);
    Id ID_SPACE_REF       = Id.newId(NAME_SPACE, SPACE_REF);
    Id ID_TZ              = Id.newId(NAME_SPACE, TZ);
    Id ID_VAV             = Id.newId(NAME_SPACE, VAV);

    String TN_AREA            = "hs$3aarea";
    String TN_EQUIP_REF       = "hs$3aequipRef";
    String TN_GEO_ADDR        = "hs$3ageoAddr";
    String TN_GEO_CITY        = "hs$3ageoCity";
    String TN_GEO_COORD       = "hs$3ageoCoord";
    String TN_GEO_COUNTRY     = "hs$3ageoCountry";
    String TN_GEO_LAT         = "hs$3ageoLat";
    String TN_GEO_LON         = "hs$3ageoLon";
    String TN_GEO_POSTAL_CODE = "hs$3ageoPostalCode";
    String TN_GEO_STATE       = "hs$3ageoState";
    String TN_GEO_STREET      = "hs$3ageoStreet";
    String TN_SITE_REF        = "hs$3asiteRef";
    String TN_TZ              = "hs$3atz";

    String TAGS_VERSION = BHsTagDictionary.HAYSTACK_VERSION + " NH.3";
    String IMPORT_SUFIX = " (import)";
    String TAGS_VERSION_IMPORT = TAGS_VERSION + IMPORT_SUFIX;
    String TAGS_CSV_FILE_VERSION = BHsTagDictionary.TAGS_CSV_FILE_VERSION;
}
