//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   11 Apr 2013  Mike Jarmy       Creation
//   10 May 2018  Eric Anderson    Added use of generics
//   21 Dec 2018  Andrew Saunders  Allowing plain components to be used as sites and equips
//
package nhaystack.server;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import javax.baja.history.BHistoryConfig;
import javax.baja.naming.BOrd;
import javax.baja.naming.SlotPath;
import javax.baja.nre.util.TextUtil;
import javax.baja.security.PermissionException;
import javax.baja.sys.BComponent;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Context;
import javax.baja.util.BFormat;
import javax.baja.xml.XWriter;
import nhaystack.util.SlotUtil;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HStr;

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
        return siteNav + '/' + equipNav;
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

        BComponent[] sites = cache.getAllSites();
        if (sites.length == 0)
        {
            out.w("<sepNav/>").nl();
        }
        else
        {
            out.w("<sepNav>").nl();
            for (BComponent site : sites)
            {
                HDict siteTags = tagMgr.createComponentTags(site);
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

                    for (BComponent equip : equips)
                    {
                        HDict equipTags = tagMgr.createComponentTags(equip);
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

                            for (BComponent point : points)
                            {
                                HDict pointTags = tagMgr.createComponentTags(point);
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
        HDict[] dicts = {
            new HDictBuilder()
                .add("navId", "slot:/")
                .add("dis", "ComponentSpace")
                .toDict(),

            new HDictBuilder()
                .add("navId", "his:/")
                .add("dis", "HistorySpace")
                .toDict(),

            new HDictBuilder()
                .add("navId", "sep:/")
                .add("dis", "Site")
                .toDict()
        };

        return HGridBuilder.dictsToGrid(dicts);
    }

    private HGrid onCompNav(String navId)
    {
        Context cx = ThreadContext.getContext(Thread.currentThread());

        // child of ComponentSpace root
        if (navId.equals("slot:/"))
        {
            BComponent root = (BComponent) 
                BOrd.make("station:|slot:/").get(service, null);

            return getHGrid(navId, cx, root);
        }
        // ComponentSpace component
        else if (navId.startsWith("slot:/"))
        {
            String slotPath = navId.substring("slot:/".length());
            BOrd ord = BOrd.make("station:|slot:/" + slotPath);
            BComponent comp = (BComponent) ord.get(service, null);

            return getHGrid(navId, cx, comp);
        }
        else throw new BajaRuntimeException("Cannot lookup nav for " + navId);
    }

    private HGrid getHGrid(String navId, Context cx, BComponent root)
    {
        if (!TypeUtil.canRead(root, cx))
            throw new PermissionException("Cannot read " + navId);

        BComponent[] kids = root.getChildComponents();
        ArrayList<HDict> dicts = new ArrayList<>();
        for (BComponent kid : kids)
        {
            if (TypeUtil.canRead(kid, cx))
                dicts.add(makeCompNavRec(kid));
        }
        return HGridBuilder.dictsToGrid(dicts.toArray(EMPTY_HDICT_ARRAY));
    }

    private HDict makeCompNavRec(BComponent comp)
    {
        HDictBuilder hdb = new HDictBuilder();

        // add a navId, but only if this component is not a leaf
        if (comp.getChildComponents().length > 0)
        {
            hdb.add("navId", comp.getSlotPath().toString());
        }

        if (SpaceManager.isVisibleComponent(comp))
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
        Context cx = ThreadContext.getContext(Thread.currentThread());

        // distinct station names
        if (navId.equals("his:/"))
        {
            String[] stationNames = cache.getNavHistoryStationNames();

            ArrayList<HDict> dicts = new ArrayList<>();
            for (String stationName : stationNames)
            {
                if (getAccessibleHistoryConfigs(stationName, cx).length > 0)
                {
                    HDictBuilder hd = new HDictBuilder();
                    hd.add("navId", "his:/" + stationName);
                    hd.add("dis", stationName);
                    hd.add("stationName", stationName);
                    dicts.add(hd.toDict());
                }
            }
            return HGridBuilder.dictsToGrid(dicts.toArray(EMPTY_HDICT_ARRAY));
        }

        // histories that go with station
        else if (navId.startsWith("his:/"))
        {
            String stationName = navId.substring("his:/".length());

            BHistoryConfig[] configs = getAccessibleHistoryConfigs(stationName, cx);

            ArrayList<HDict> dicts = new ArrayList<>();
            for (BHistoryConfig config : configs)
                dicts.add(tagMgr.createHistoryTags(config));
            return HGridBuilder.dictsToGrid(dicts.toArray(EMPTY_HDICT_ARRAY));
        }

        else throw new BajaRuntimeException("Cannot lookup nav for " + navId);
    }

    private BHistoryConfig[] getAccessibleHistoryConfigs(String stationName, Context cx)
    {
        BHistoryConfig[] configs = cache.getNavHistories(stationName);

        ArrayList<BHistoryConfig> arr = new ArrayList<>();
        for (BHistoryConfig config : configs)
        {
            if (TypeUtil.canRead(config, cx))
                arr.add(config);
        }
        return arr.toArray(EMPTY_HISTORY_CONFIG_ARRAY);
    }

    private HGrid onSepNav(String navId)
    {
        if (navId.equals("sep:/"))
        {
            Context cx = ThreadContext.getContext(Thread.currentThread());
            ArrayList<HDict> dicts = new ArrayList<>();

            BComponent[] sites = cache.getAllSites();
            for (BComponent site : sites)
            {
                if (!TypeUtil.canRead(site, cx)) continue;

                HDict tags = tagMgr.createComponentTags(site);

                String siteNav = makeSiteNavId(tags.getStr("navName"));

                HDictBuilder hd = new HDictBuilder();
                hd.add("navId", HStr.make(siteNav));
                hd.add(tags);

                dicts.add(hd.toDict());
            }

            return HGridBuilder.dictsToGrid(dicts.toArray(EMPTY_HDICT_ARRAY));
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
        ArrayList<HDict> dicts = new ArrayList<>();

        String siteNav = makeSiteNavId(siteName);
        BComponent[] equips = cache.getNavSiteEquips(siteNav);

        for (BComponent equip : equips)
        {
            if (!TypeUtil.canRead(equip, cx)) continue;

            HDict tags = tagMgr.createComponentTags(equip);

            String equipNav = makeEquipNavId(
                siteNav, tags.getStr("navName"));

            HDictBuilder hd = new HDictBuilder();
            hd.add("navId", HStr.make(equipNav));
            hd.add(tags);
            dicts.add(hd.toDict());
        }

        return HGridBuilder.dictsToGrid(dicts.toArray(EMPTY_HDICT_ARRAY));
    }

    private HGrid makeEquipNav(String siteName, String equipName)
    {
        ArrayList<HDict> dicts = new ArrayList<>();

        String equipNav = makeEquipNavId(siteName, equipName);
        BComponent[] points = cache.getNavEquipPoints(equipNav);
        for (BComponent point : points)
        {
            HDictBuilder hd = new HDictBuilder();
            hd.add(tagMgr.createComponentTags(point));
            dicts.add(hd.toDict());
        }

        return HGridBuilder.dictsToGrid(dicts.toArray(EMPTY_HDICT_ARRAY));
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final HDict[] EMPTY_HDICT_ARRAY = new HDict[0];
    private static final BHistoryConfig[] EMPTY_HISTORY_CONFIG_ARRAY = new BHistoryConfig[0];

    final BNHaystackService service;
    final Cache cache;
    final SpaceManager spaceMgr;
    final TagManager tagMgr;
}
