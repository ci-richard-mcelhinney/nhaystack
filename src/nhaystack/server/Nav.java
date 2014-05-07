//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   11 Apr 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import java.io.*;

import javax.baja.control.*;
import javax.baja.history.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.util.*;
import javax.baja.xml.*;
import com.tridium.util.EscUtil;

import org.projecthaystack.*;
import nhaystack.server.storehouse.*;
import nhaystack.site.*;
import nhaystack.util.*;

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

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    public static String makeNavName(BComponent comp, HDict tags)
    {
        String format = tags.has("navNameFormat") ?
            tags.getStr("navNameFormat") :
            "%displayName%";

        String navName = BFormat.format(format, comp);
        navName = EscUtil.slot.escape(navName);

        return PathUtil.fromNiagaraPath(navName);
    }

    public static String makeSiteNavId(String siteNav)
    {
        return "sep:/" + siteNav;
    }

    public static String makeEquipNavId(String siteNav, String equipNav)
    {
        return siteNav + "/" + equipNav;
    }

    /**
      * Return navigation tree children for given navId. 
      */
    public HGrid onNav(String navId)
    {
        if (navId == null) return roots();

        else if (navId.startsWith("slot:/")) return onCompNav(navId);
        else if (navId.startsWith("his:/"))  return onHisNav(navId);
        else if (navId.startsWith("sep:/")) return onSepNav(navId);

        else
            throw new IllegalStateException("Cannot lookup nav for " + navId);
    }

    /**
      * Fetch the site-equip-point nav tree in xml format
      */
    public String fetchSepNav() throws Exception
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        XWriter out = new XWriter(bout);

        BHSite[] sites = cache.getAllSites();
        if (sites.length == 0)
        {
            out.w("<sepNav/>").nl();
        }
        else
        {
            out.w("<sepNav>").nl();
            for (int i = 0; i < sites.length; i++)
            {
                HDict siteTags = compStore.createComponentTags(sites[i]);
                String siteName = siteTags.getStr("navName");

                BComponent[] equips = cache.getNavSiteEquips(makeSiteNavId(siteName));
                if (equips.length == 0)
                {
                    out.indent(1)
                        .w("<site ")
                        .attr("navName", siteTags.getStr("navName"))
                        .w("/>").nl();
                }
                else
                {
                    out.indent(1)
                        .w("<site ")
                        .attr("navName", siteTags.getStr("navName"))
                        .w(">").nl();

                    for (int j = 0; j < equips.length; j++)
                    {
                        HDict equipTags = compStore.createComponentTags(equips[j]);
                        String equipName = equipTags.getStr("navName");

                        BControlPoint[] points = cache.getNavEquipPoints(
                            makeEquipNavId(siteName, equipName));

                        if (points.length == 0)
                        {
                            out.indent(2)
                                .w("<equip ")
                                .attr("navName", equipTags.getStr("navName"))
                                .w("/>").nl();
                        }
                        else
                        {
                            out.indent(2)
                                .w("<equip ")
                                .attr("navName", equipTags.getStr("navName"))
                                .w(">").nl();

                            for (int k = 0; k < points.length; k++)
                            {
                                HDict pointTags = compStore.createComponentTags(points[k]);
                                out.indent(3)
                                    .w("<point ")
                                    .attr("navName", pointTags.getStr("navName")).w(" ")
                                    .attr("axType", pointTags.getStr("axType"))
                                    .w("/>").nl();
                            }
                            out.indent(2).w("</equip>").nl();
                        }
                    }
                    out.indent(1).w("</site>").nl();
                }

            }
            out.w("</sepNav>").nl();
        }

        out.close();

        return new String(bout.toByteArray());
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private static HGrid roots()
    {
        Array dicts = new Array(HDict.class);

        dicts.add(
            new HDictBuilder() 
            .add("navId", "slot:/")
            .add("dis", "ComponentSpace")
            .toDict());

        dicts.add(
            new HDictBuilder() 
            .add("navId", "his:/")
            .add("dis", "HistorySpace")
            .toDict());

        dicts.add(
            new HDictBuilder() 
            .add("navId", "sep:/")
            .add("dis", "Site")
            .toDict());

        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }

    private HGrid onCompNav(String navId)
    {
        // child of ComponentSpace root
        if (navId.equals("slot:/"))
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
        else if (navId.startsWith("slot:/"))
        {
            String slotPath = navId.substring("slot:/".length());
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
            hdb.add("navId", comp.getSlotPath().toString());
        }

        if (compStore.isVisibleComponent(comp))
        {
            hdb.add(compStore.createComponentTags(comp));
        }
        else
        {
            hdb.add("id", NHServer.makeSlotPathRef(comp).getHRef());
            hdb.add("dis", comp.getDisplayName(null));
            hdb.add("axType", comp.getType().toString());
            hdb.add("axSlotPath", comp.getSlotPath().toString());
        }

        return hdb.toDict();
    }

    private HGrid onHisNav(String navId)
    {
        // distinct station names
        if (navId.equals("his:/"))
        {
            String[] stationNames = cache.getNavHistoryStationNames();

            Array dicts = new Array(HDict.class);
            for (int i = 0; i < stationNames.length; i++)
            {
                String stationName = stationNames[i];

                HDictBuilder hd = new HDictBuilder();
                hd.add("navId", "his:/" + stationName);
                hd.add("dis", stationName);
                hd.add("stationName", stationName);
                dicts.add(hd.toDict());
            }
            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }

        // histories that go with station
        else if (navId.startsWith("his:/"))
        {
            String stationName = navId.substring("his:/".length());
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

    private HGrid onSepNav(String navId)
    {
        if (navId.equals("sep:/"))
        {
            Array dicts = new Array(HDict.class);

            BHSite[] sites = cache.getAllSites();
            for (int i = 0; i < sites.length; i++)
            {
                BHSite site = sites[i];
                HDict tags = compStore.createComponentTags(site);

                String siteNav = makeSiteNavId(tags.getStr("navName"));

                HDictBuilder hd = new HDictBuilder();
                hd.add("navId", HStr.make(siteNav));
                hd.add(tags);

                dicts.add(hd.toDict());
            }

            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }

        else if (navId.startsWith("sep:/"))
        {
            String str = navId.substring("sep:/".length());

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

        String siteNav = makeSiteNavId(siteName);
        BComponent[] equips = cache.getNavSiteEquips(siteNav);

        for (int i = 0; i < equips.length; i++)
        {
            BComponent equip = equips[i];
            HDict tags = compStore.createComponentTags(equip);

            String equipNav = makeEquipNavId(
                siteNav, tags.getStr("navName"));

            HDictBuilder hd = new HDictBuilder();
            hd.add("navId", HStr.make(equipNav));
            hd.add(tags);
            dicts.add(hd.toDict());
        }

        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }

    private HGrid makeEquipNav(String siteName, String equipName)
    {
        Array dicts = new Array(HDict.class);

        String equipNav = makeEquipNavId(siteName, equipName);
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
