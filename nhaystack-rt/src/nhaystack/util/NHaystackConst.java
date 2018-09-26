//
// Copyright (c) 2018. Tridium, Inc. All rights reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Aug 2018  Andrew Saunders  Creation
//

package nhaystack.util;

import javax.baja.tag.Id;

/**
 * Defines haystack dictionary constants.
 */
public interface NHaystackConst
{
	String NAME_SPACE = "hs";

    String AREA            = "area";
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
    String SITE_REF        = "siteRef";
    String TZ              = "tz";

    Id ID_AREA            = Id.newId(NAME_SPACE, AREA);
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
    Id ID_SITE_REF        = Id.newId(NAME_SPACE, SITE_REF);
    Id ID_TZ              = Id.newId(NAME_SPACE, TZ);

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
}
