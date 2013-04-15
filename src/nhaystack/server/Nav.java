//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   11 Apr 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;

import javax.baja.control.*;
import javax.baja.history.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import haystack.*;
import nhaystack.collection.*;
import nhaystack.server.*;
import nhaystack.server.storehouse.*;
import nhaystack.site.*;

/**
  * Nav manages the nav trees
  */
public class Nav
{
    Nav(
        BNHaystackService service,
        ComponentStorehouse compStore,
        HistoryStorehouse hisStore,
        Cache cache)
    {
        this.service = service;
        this.compStore = compStore;
        this.hisStore = hisStore;
        this.cache = cache;
    }

    /**
      * Return navigation tree children for given navId. 
      */
    HGrid onNav(String navId)
    {
        if (navId == null) return roots();

        else if (navId.startsWith("/comp")) return onCompNav(navId);
        else if (navId.startsWith("/his"))  return onHisNav(navId);
        else if (navId.startsWith("/site")) return onSiteNav(navId);

        else
            throw new IllegalStateException("Cannot lookup nav for " + navId);
    }

    public static String makeNavFormat(BComponent comp, HDict tags)
    {
        String format = tags.has("navNameFormat") ?
            tags.getStr("navNameFormat") :
            "%displayName%";

        return BFormat.format(format, comp);
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private static HGrid roots()
    {
        Array dicts = new Array(HDict.class);

        dicts.add(
            new HDictBuilder() 
            .add("navId", "/comp")
            .add("dis", "ComponentSpace")
            .toDict());

        dicts.add(
            new HDictBuilder() 
            .add("navId", "/his")
            .add("dis", "HistorySpace")
            .toDict());

        dicts.add(
            new HDictBuilder() 
            .add("navId", "/site")
            .add("dis", "Site")
            .toDict());

        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }

    private HGrid onCompNav(String navId)
    {
        // child of ComponentSpace root
        if (navId.equals("/comp"))
        {
            BComponent root = (BComponent) 
                BOrd.make("station:|slot:/").get(service, null);

            BComponent kids[] = root.getChildComponents();
            Array dicts = new Array(HDict.class);
            for (int i = 0; i < kids.length; i++)
                dicts.add(makeCompNavRec(kids[i]));
            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }
        // ComponentSpace component
        else if (navId.startsWith("/comp/"))
        {
            String slotPath = navId.substring("/comp/".length());
            BOrd ord = BOrd.make("station:|slot:/" + slotPath);
            BComponent comp = (BComponent) ord.get(service, null);

            BComponent kids[] = comp.getChildComponents();
            Array dicts = new Array(HDict.class);
            for (int i = 0; i < kids.length; i++)
                dicts.add(makeCompNavRec(kids[i]));
            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }
        else throw new BajaRuntimeException("Cannot lookup nav for " + navId);
    }

    private HDict makeCompNavRec(BComponent comp)
    {
        HDictBuilder hdb = new HDictBuilder();

        // add a navId, but only if this component is not a leaf
        if (comp.getChildComponents().length > 0)
        {
            // always starts with "slot:/"
            String slotPath = comp.getSlotPath().toString();
            slotPath = slotPath.substring("slot:/".length());

            hdb.add("navId", "/comp/" + slotPath);
        }

        if (compStore.isVisibleComponent(comp))
        {
            hdb.add(compStore.createComponentTags(comp));
        }
        else
        {
            hdb.add("dis", comp.getDisplayName(null));
            hdb.add("axType", comp.getType().toString());
            hdb.add("axSlotPath", comp.getSlotPath().toString());
        }

        return hdb.toDict();
    }

    private HGrid onHisNav(String navId)
    {
        // distinct station names
        if (navId.equals("/his"))
        {
            String[] stationNames = cache.getNavHistoryStationNames();

            Array dicts = new Array(HDict.class);
            for (int i = 0; i < stationNames.length; i++)
            {
                String stationName = stationNames[i];

                HDictBuilder hd = new HDictBuilder();
                hd.add("navId", "/his/" + stationName);
                hd.add("dis", stationName);
                hd.add("stationName", stationName);
                dicts.add(hd.toDict());
            }
            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }

        // histories that go with station
        else if (navId.startsWith("/his/"))
        {
            String stationName = navId.substring("/his/".length());
            BHistoryConfig[] configs = cache.getNavHistories(stationName);

            Array dicts = new Array(HDict.class);
            for (int i = 0; i < configs.length; i++)
            {
                dicts.add(hisStore.createHistoryTags(configs[i]));
            }
            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }

        else throw new BajaRuntimeException("Cannot lookup nav for " + navId);
    }

    private HGrid onSiteNav(String navId)
    {
        if (navId.equals("/site"))
        {
            Array dicts = new Array(HDict.class);

            BComponent[] sites = cache.getAllSites();
            for (int i = 0; i < sites.length; i++)
            {
                BComponent site = sites[i];
                HDict tags = compStore.createComponentTags(site);

                SiteNavId siteNav = SiteNavId.make(tags.getStr("navName"));

                HDictBuilder hd = new HDictBuilder();
                hd.add("navId", HStr.make(siteNav.getHRef().val));
                hd.add(tags);

                dicts.add(hd.toDict());
            }

            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }

        else if (navId.startsWith("/site/"))
        {
            String str = navId.substring("/site/".length());

            String[] navNames = TextUtil.split(str, '/');
            switch (navNames.length)
            {
                case 1: return makeSiteNav(navNames[0]);
                case 2: return makeEquipNav(navNames[0], navNames[1]);
                default: throw new BajaRuntimeException("Cannot lookup nav for " + navId);
            }
        }

        // oops
        else throw new BajaRuntimeException("Cannot lookup nav for " + navId);
    }

    private HGrid makeSiteNav(String siteName)
    {
        Array dicts = new Array(HDict.class);

        SiteNavId siteNav = SiteNavId.make(siteName);
        BComponent[] equips = cache.getNavSiteEquips(siteNav);

        for (int i = 0; i < equips.length; i++)
        {
            BComponent equip = equips[i];
            HDict tags = compStore.createComponentTags(equip);

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

    private HGrid makeEquipNav(String siteName, String equipName)
    {
        Array dicts = new Array(HDict.class);

        EquipNavId equipNav = EquipNavId.make(siteName, equipName);
        BControlPoint[] points = cache.getNavEquipPoints(equipNav);

        for (int i = 0; i < points.length; i++)
        {
            BControlPoint point = points[i];

            HDictBuilder hd = new HDictBuilder();
            hd.add(compStore.createComponentTags(point));
            dicts.add(hd.toDict());
        }

        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    final BNHaystackService service;
    final Cache cache;
    final ComponentStorehouse compStore;
    final HistoryStorehouse hisStore;
}

