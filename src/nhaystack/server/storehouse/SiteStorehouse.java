//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.server.storehouse;

import javax.baja.control.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import haystack.*;
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
        /////////////////////////////////////////////
        // find all sites

        if (navId.equals("/site"))
        {
            Array dicts = new Array(HDict.class);

            BComponent[] sites = server.getCache().getAllSites();
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

            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }

        /////////////////////////////////////////////
        // find equips for a given site

        else if (navId.startsWith("/site/"))
        {
            String str = navId.substring("/site/".length());

            String[] navNames = TextUtil.split(str, '/');
            switch (navNames.length)
            {
                case 1: return siteNav(navNames[0]);
                case 2: return equipNav(navNames[0], navNames[1]);
                default: throw new BajaRuntimeException("Cannot lookup nav for " + navId);
            }
        }

        // oops
        else throw new BajaRuntimeException("Cannot lookup nav for " + navId);
    }

    private HGrid siteNav(String siteName)
    {
        Array dicts = new Array(HDict.class);

        SiteNavId siteNav = SiteNavId.make(siteName);
        BComponent[] equips = server.getCache().getSiteEquips(siteNav);

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

        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }

    private HGrid equipNav(String siteName, String equipName)
    {
        Array dicts = new Array(HDict.class);

        EquipNavId equipNav = EquipNavId.make(siteName, equipName);
        BControlPoint[] points = server.getCache().getEquipPoints(equipNav);

        for (int i = 0; i < points.length; i++)
        {
            BControlPoint point = points[i];

            HDictBuilder hd = new HDictBuilder();
            hd.add(server.getConfigStorehouse().createComponentTags(point));
            dicts.add(hd.toDict());
        }

        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }
}

