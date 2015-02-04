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
import javax.baja.security.*;
import javax.baja.sys.*;
import javax.baja.util.*;
import javax.baja.xml.*;

import org.projecthaystack.*;
import nhaystack.site.*;
import nhaystack.util.*;

/**
  * Nav manages the nav trees
  */
public class Nav
{
    Nav(
        BNHaystackService service,
        SpaceManager spaceMgr,
        Cache cache,
        TagManager tagMgr)
    {
        this.service = service;
        this.spaceMgr = spaceMgr;
        this.cache = cache;
        this.tagMgr = tagMgr;
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
        navName = SlotPath.escape(navName);

        return SlotUtil.fromNiagara(navName);
    }

////////////////////////////////////////////////////////////////
// package-scope
////////////////////////////////////////////////////////////////

    static String makeSiteNavId(String siteNav)
    {
        return "sep:/" + siteNav;
    }

    static String makeEquipNavId(String siteNav, String equipNav)
    {
        return siteNav + "/" + equipNav;
    }

    /**
      * Return navigation tree children for given navId. 
      */
    HGrid onNav(String navId)
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
    String fetchSepNav() throws Exception
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
                HDict siteTags = tagMgr.createComponentTags(sites[i]);
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
                        HDict equipTags = tagMgr.createComponentTags(equips[j]);
                        String equipName = equipTags.getStr("navName");

                        BComponent[] points = cache.getNavEquipPoints(
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
                                HDict pointTags = tagMgr.createComponentTags(points[k]);
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
        Context cx = ThreadContext.getContext(Thread.currentThread());

        // child of ComponentSpace root
        if (navId.equals("slot:/"))
        {
            BComponent root = (BComponent) 
                BOrd.make("station:|slot:/").get(service, null);

            if (!TypeUtil.canRead(root, cx)) 
                throw new PermissionException("Cannot read " + navId);

            BComponent kids[] = root.getChildComponents();
            Array dicts = new Array(HDict.class);
            for (int i = 0; i < kids.length; i++)
            {
                if (TypeUtil.canRead(kids[i], cx)) 
                    dicts.add(makeCompNavRec(kids[i]));
            }
            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }
        // ComponentSpace component
        else if (navId.startsWith("slot:/"))
        {
            String slotPath = navId.substring("slot:/".length());
            BOrd ord = BOrd.make("station:|slot:/" + slotPath);
            BComponent comp = (BComponent) ord.get(service, null);

            if (!TypeUtil.canRead(comp, cx)) 
                throw new PermissionException("Cannot read " + navId);

            BComponent kids[] = comp.getChildComponents();
            Array dicts = new Array(HDict.class);
            for (int i = 0; i < kids.length; i++)
            {
                if (TypeUtil.canRead(kids[i], cx)) 
                    dicts.add(makeCompNavRec(kids[i]));
            }
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

        if (spaceMgr.isVisibleComponent(comp))
        {
            hdb.add(tagMgr.createComponentTags(comp));
        }
        else
        {
            hdb.add("dis", comp.getDisplayName(null));
            hdb.add("axType", comp.getType().toString());
            hdb.add("axSlotPath", comp.getSlotPath().toString());
        }

        // always use a slot path ref
        hdb.add("id", TagManager.makeSlotPathRef(comp).getHRef());

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
            Context cx = ThreadContext.getContext(Thread.currentThread());

            String stationName = navId.substring("his:/".length());
            BHistoryConfig[] configs = cache.getNavHistories(stationName);

            Array dicts = new Array(HDict.class);
            for (int i = 0; i < configs.length; i++)
            {
                if (TypeUtil.canRead(configs[i], cx)) 
                    dicts.add(tagMgr.createHistoryTags(configs[i]));
            }
            return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
        }

        else throw new BajaRuntimeException("Cannot lookup nav for " + navId);
    }

    private HGrid onSepNav(String navId)
    {
        if (navId.equals("sep:/"))
        {
            Context cx = ThreadContext.getContext(Thread.currentThread());
            Array dicts = new Array(HDict.class);

            BHSite[] sites = cache.getAllSites();
            for (int i = 0; i < sites.length; i++)
            {
                BHSite site = sites[i];
                if (!TypeUtil.canRead(site, cx)) continue;

                HDict tags = tagMgr.createComponentTags(site);

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
        Context cx = ThreadContext.getContext(Thread.currentThread());
        Array dicts = new Array(HDict.class);

        String siteNav = makeSiteNavId(siteName);
        BComponent[] equips = cache.getNavSiteEquips(siteNav);

        for (int i = 0; i < equips.length; i++)
        {
            BComponent equip = equips[i];
            if (!TypeUtil.canRead(equip, cx)) continue;

            HDict tags = tagMgr.createComponentTags(equip);

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
        Context cx = ThreadContext.getContext(Thread.currentThread());
        Array dicts = new Array(HDict.class);

        String equipNav = makeEquipNavId(siteName, equipName);
        BComponent[] points = cache.getNavEquipPoints(equipNav);
        for (int i = 0; i < points.length; i++)
        {
            BComponent point = points[i];

            HDictBuilder hd = new HDictBuilder();
            hd.add(tagMgr.createComponentTags(point));
            dicts.add(hd.toDict());
        }

        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    final BNHaystackService service;
    final Cache cache;
    final SpaceManager spaceMgr;
    final TagManager tagMgr;
}
