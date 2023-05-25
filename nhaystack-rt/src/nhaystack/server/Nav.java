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

import java.io.*;
import java.util.*;
import javax.baja.control.BControlPoint;
import javax.baja.history.BHistoryConfig;
import javax.baja.naming.BOrd;
import javax.baja.naming.SlotPath;
import javax.baja.nre.util.TextUtil;
import javax.baja.schedule.BWeeklySchedule;
import javax.baja.security.PermissionException;
import javax.baja.sys.BComponent;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Context;
import javax.baja.util.BFormat;
import javax.baja.xml.XWriter;

import nhaystack.site.*;
import nhaystack.util.*;
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
        return SEP + siteNav;
    }

    static String makeEquipNavId(String siteNav, String equipNav)
    {
        return siteNav + '/' + equipNav;
    }

    static String makeSpaceNavId(String siteNav, String spaceNav)
    {
        return makeEquipNavId(siteNav, spaceNav);
    }

    /**
      * Return navigation tree children for given navId. 
      */
    HGrid onNav(String navId)
    {
        if (navId == null) return roots();

        else if (navId.startsWith(SLOT)) return onCompNav(navId);
        else if (navId.startsWith(HIS))  return onHisNav(navId);
        else if (navId.startsWith(SEP)) return onSepNav(navId);

        else
            throw new IllegalStateException(STATEMSG + navId);
    }

    /**
      * Fetch the site-equip-point nav tree in xml format
      */
    String fetchSepNav() throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        XWriter out = new XWriter(bout);

        BComponent[] sites = cache.getAllSites();
        out.w("<sepNav").w(sites.length == 0 ? "/>" : ">").nl();
        for (BComponent site: sites) {
            HDict siteTags = tagMgr.createComponentTags(site);
            String siteName = siteTags.getStr(NAVNAME);

            String siteNavId = makeSiteNavId(siteName);
            BComponent[] equips = cache.getNavSiteEquips(siteNavId);
            BComponent[] spaces = cache.getNavSiteSpaces(siteNavId);

            boolean noChildren = equips.length + spaces.length == 0;
            openTag(out, "site", siteTags, 1, noChildren);

            for (BComponent space : spaces)
            {
                // Only include components that aren't a child of another space
                // in this site (as those components will be included in those
                // subtrees instead).
                BComponent parent = cache.getParent(space);
                if (
                  parent == null ||
                    Arrays.stream(spaces).noneMatch(parent::equals)
                ) {
                    writeXml(out, 2, space);
                }
            }

            for (BComponent equip : equips)
            {
                // Only include components that aren't a child of another space
                // or equip in this site (as those components will be included
                // in those subtrees instead).
                BComponent parent = cache.getParent(equip);
                if (
                  parent == null
                    || (
                      Arrays.stream(equips).noneMatch(parent::equals)
                        && Arrays.stream(spaces).noneMatch(parent::equals)
                    )
                ) {
                    writeXml(out, 2, equip);
                }
            }

            if (!noChildren) {
                closeTag(out, "site", 1);
            }
        }

        if (sites.length != 0) {
            closeTag(out, "sepNav", 0);
        }

        out.close();

        String xml = bout.toString();
        return xml;
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    /**
     * Return XML tag name for a given component, based on its haystack tagging.
     *
     * @param comp Component to return tag name for.
     * @return Tag name for component, or null if unable to choose.
     */
    private static String getTag(BComponent comp) {
        if (comp instanceof BControlPoint || comp instanceof BWeeklySchedule)
        {
            return NHaystackConst.POINT;
        }
        else if (
            comp instanceof BHSite
                || comp.tags().contains(NHaystackConst.ID_SITE)
        )
        {
            return NHaystackConst.SITE;
        }
        else if (
            comp instanceof BHEquip
                || comp.tags().contains(NHaystackConst.ID_EQUIP)
        ) {
            return NHaystackConst.EQUIP;
        }
        else if (comp.tags().contains(NHaystackConst.ID_SPACE)) {
            return NHaystackConst.SPACE;
        }

        return null;
    }

    /**
     * Write an XML opening tag to out. If empty is specified, will include a
     * closing slash.
     *
     * @param out        Writer to write tag to.
     * @param tagName    Tag name of tag to open.
     * @param entityTags Tags of the entity to include nav name from.
     * @param indent     Indentation level.
     * @param empty      Whether the tag should be closed.
     */
    private static void openTag(
      XWriter out, String tagName, HDict entityTags, int indent, boolean empty
    )
    {
        out
            .indent(indent)
            .w("<")
            .w(tagName)
            .w(" ")
            .attr(NAVNAME, entityTags.getStr(NAVNAME))
            .w(empty ? "/>" : ">")
            .nl();
    }

    /**
     * Write an XML closing tag to out with a given tag name.
     * @param out     Writer to write tag to.
     * @param tagName Name of tag to close.
     * @param indent  Indentation level.
     */
    private static void closeTag(XWriter out, String tagName, int indent) {
        out
            .indent(indent)
            .w("</")
            .w(tagName)
            .w(">")
            .nl();
    }

    /**
     * Write XML description of a component to a given XML writer.
     *
     * @param out XML writer to write component data out to.
     * @param indent Indentation level to start at.
     * @param comp Component to encode to XML.
     */
    private void writeXml(XWriter out, int indent, BComponent comp)
    {
        BComponent[] children = cache.getChildren(comp);

        String tagName = getTag(comp);

        BComponent[] points = null;
        if (NHaystackConst.EQUIP.equals(tagName))  {
            points = cache.getEquipPoints(comp);
        } else if (NHaystackConst.SPACE.equals(tagName)) {
            points = cache.getSpacePoints(comp);
        }

        boolean hasChildren = (
          children.length > 0
            || (points != null && points.length > 0)
        );

        HDict tags = tagMgr.createComponentTags(comp);
        openTag(out, tagName, tags, indent, !hasChildren);

        for (BComponent child : children) {
            writeXml(out, indent + 1, child);
        }

        if (points != null) {
            writePointListXml(out, indent + 1, points);
        }

        if (hasChildren)
        {
            closeTag(out, tagName, indent);
        }
    }

    /**
     * Encode points to XML and write to the provided writer.
     *
     * @param out XML writer to use to write points out to.
     * @param indent Indentation level for each line of XML.
     * @param points Points to encode.
     */
    private void writePointListXml(
      XWriter out, int indent, BComponent[] points
    )
    {
        for (BComponent point : points)
        {
            HDict pointTags = tagMgr.createComponentTags(point);
            out.indent(indent)
              .w("<point ")
              .attr(NAVNAME, pointTags.getStr(NAVNAME)).w(" ")
              .attr(AXTYPE, pointTags.getStr(AXTYPE))
              .w("/>").nl();
        }
    }

    private static HGrid roots()
    {
        HDict[] dicts = {
            new HDictBuilder()
                .add(NAVID, SLOT)
                .add("dis", "ComponentSpace")
                .toDict(),

            new HDictBuilder()
                .add(NAVID, HIS)
                .add("dis", "HistorySpace")
                .toDict(),

            new HDictBuilder()
                .add(NAVID, SEP)
                .add("dis", "Site")
                .toDict()
        };

        return HGridBuilder.dictsToGrid(dicts);
    }

    private HGrid onCompNav(String navId)
    {
        Context cx = ThreadContext.getContext(Thread.currentThread());

        // child of ComponentSpace root
        if (navId.equals(SLOT))
        {
            BComponent root = (BComponent) 
                BOrd.make("station:|slot:/").get(service, null);

            return getHGrid(navId, cx, root);
        }
        // ComponentSpace component
        else if (navId.startsWith(SLOT))
        {
            String slotPath = navId.substring(SLOT.length());
            BOrd ord = BOrd.make("station:|slot:/" + slotPath);
            BComponent comp = (BComponent) ord.get(service, null);

            return getHGrid(navId, cx, comp);
        }
        else throw new BajaRuntimeException(STATEMSG + navId);
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
            hdb.add(NAVID, comp.getSlotPath().toString());
        }

        if (SpaceManager.isVisibleComponent(comp))
        {
            hdb.add(tagMgr.createComponentTags(comp));
        }
        else
        {
            hdb.add("dis", comp.getDisplayName(null));
            hdb.add(AXTYPE, comp.getType().toString());
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
        if (navId.equals(HIS))
        {
            String[] stationNames = cache.getNavHistoryStationNames();

            ArrayList<HDict> dicts = new ArrayList<>();
            for (String stationName : stationNames)
            {
                if (getAccessibleHistoryConfigs(stationName, cx).length > 0)
                {
                    HDictBuilder hd = new HDictBuilder();
                    hd.add(NAVID, HIS + stationName);
                    hd.add("dis", stationName);
                    hd.add("stationName", stationName);
                    dicts.add(hd.toDict());
                }
            }
            return HGridBuilder.dictsToGrid(dicts.toArray(EMPTY_HDICT_ARRAY));
        }

        // histories that go with station
        else if (navId.startsWith(HIS))
        {
            String stationName = navId.substring(HIS.length());

            BHistoryConfig[] configs = getAccessibleHistoryConfigs(stationName, cx);

            ArrayList<HDict> dicts = new ArrayList<>();
            for (BHistoryConfig config : configs)
                dicts.add(tagMgr.createHistoryTags(config));
            return HGridBuilder.dictsToGrid(dicts.toArray(EMPTY_HDICT_ARRAY));
        }

        else throw new BajaRuntimeException(STATEMSG + navId);
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
        if (navId.equals(SEP))
        {
            Context cx = ThreadContext.getContext(Thread.currentThread());
            ArrayList<HDict> dicts = new ArrayList<>();

            BComponent[] sites = cache.getAllSites();
            for (BComponent site : sites)
            {
                if (!TypeUtil.canRead(site, cx)) continue;

                HDict tags = tagMgr.createComponentTags(site);

                String siteNav = makeSiteNavId(tags.getStr(NAVNAME));

                HDictBuilder hd = new HDictBuilder();
                hd.add(NAVID, HStr.make(siteNav));
                hd.add(tags);

                dicts.add(hd.toDict());
            }

            return HGridBuilder.dictsToGrid(dicts.toArray(EMPTY_HDICT_ARRAY));
        }

        else if (navId.startsWith(SEP))
        {
            String str = navId.substring(SEP.length());

            String[] navNames = TextUtil.split(str, '/');
            switch (navNames.length)
            {
                case 1: return makeSiteNav(navNames[0]);
                case 2: return makeEquipNav(navNames[0], navNames[1]);
                default: throw new BajaRuntimeException(STATEMSG + navId);
            }
        }

        // oops
        else throw new BajaRuntimeException(STATEMSG + navId);
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
                siteNav, tags.getStr(NAVNAME));

            HDictBuilder hd = new HDictBuilder();
            hd.add(NAVID, HStr.make(equipNav));
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

    private static final String SEP = "sep:/";
    private static final String SLOT = "slot:/";
    private static final String HIS = "his:/";
    private static final String STATEMSG = "Cannot lookup nav for ";
    private static final String NAVNAME = "navName";
    private static final String NAVID = "navId";
    private static final String AXTYPE = "axType";

    final BNHaystackService service;
    final Cache cache;
    final SpaceManager spaceMgr;
    final TagManager tagMgr;
}
