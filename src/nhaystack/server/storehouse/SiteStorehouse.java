//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.server.storehouse;

import java.util.*;

import javax.baja.control.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import haystack.*;
import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.server.*;
import nhaystack.site.*;

/**
  * SiteStorehouse manages access to the sites
  */
public class SiteStorehouse extends Storehouse
{
    public SiteStorehouse(NHServer server)
    {
        super(server);
    }

////////////////////////////////////////////////////////////////
// Storehouse
////////////////////////////////////////////////////////////////

    /**
      * Return navigation tree children for given navId. 
      */
    public HGrid onNav(String navId)
    {
        Cache cache = server.getCache();

        Array dicts = new Array(HDict.class);

        /////////////////////////////////////////////
        // find all sites

        if (navId.equals(SiteNavId.SITE))
        {
            BComponent[] sites = cache.getAllSites();
            for (int i = 0; i < sites.length; i++)
            {
                BComponent site = sites[i];
                HDict tags = server.getConfigStorehouse().createComponentTags(site);

                SiteNavId siteNav = SiteNavId.make(tags.getStr("navName"));

                HDictBuilder hd = new HDictBuilder();
                hd.add("navId", HStr.make(siteNav.getHRef().val));
                hd.add(tags);

                dicts.add(hd.toDict());
            }
        }

        /////////////////////////////////////////////
        // find equips for a given site

        else if (navId.startsWith(SiteNavId.SITE + ":"))
        {
            SiteNavId siteNav = SiteNavId.make(HRef.make(navId));
            BComponent[] equips = cache.getSiteEquips(siteNav);

            for (int i = 0; i < equips.length; i++)
            {
                BComponent equip = equips[i];
                HDict tags = server.getConfigStorehouse().createComponentTags(equip);

                EquipNavId equipNav = EquipNavId.make(
                    siteNav.getSiteName(),
                    tags.getStr("navName"));

                HDictBuilder hd = new HDictBuilder();
                hd.add("navId", HStr.make(equipNav.getHRef().val));
                hd.add(tags);
                dicts.add(hd.toDict());
            }
        }

        /////////////////////////////////////////////
        // find points for a given equip

        else if (navId.startsWith(EquipNavId.EQUIP + ":"))
        {
            EquipNavId equipNav = EquipNavId.make(HRef.make(navId));
            BControlPoint[] points = cache.getEquipPoints(equipNav);

            for (int i = 0; i < points.length; i++)
            {
                BControlPoint point = points[i];

                HDictBuilder hd = new HDictBuilder();
                hd.add(server.getConfigStorehouse().createComponentTags(point));
                dicts.add(hd.toDict());
            }
        }

        // oops
        else throw new BajaRuntimeException("Cannot lookup nav for " + navId);

        // done
        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }
}

