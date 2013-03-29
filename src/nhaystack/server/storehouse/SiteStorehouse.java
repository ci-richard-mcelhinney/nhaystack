//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.server.storehouse;

import java.util.*;

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
        throw new IllegalStateException();
//        Array dicts = new Array(HDict.class);
//
//        /////////////////////////////////////////////
//        // find all sites
//
//        if (navId.equals(SiteNavId.SITE))
//        {
//            BHSite[] sites = fetchSites();
//            for (int i = 0; i < sites.length; i++)
//            {
//                BHSite site = sites[i];
//
//                SiteNavId siteNav = site.makeNavId();
//
//                HDictBuilder hd = new HDictBuilder();
//                hd.add("navId", HStr.make(siteNav.getHRef().val));
//                hd.add(server.getConfigStorehouse().createComponentTags(site));
//
//                dicts.add(hd.toDict());
//            }
//        }
//
//        /////////////////////////////////////////////
//        // find equips for a given site
//
//        else if (navId.startsWith(SiteNavId.SITE + ":"))
//        {
//            SiteNavId siteNav = SiteNavId.make(HRef.make(navId));
//
//            BHSite site = fetchSiteByNav(siteNav);
//            BHEquip[] equips = fetchEquips(NHRef.make(site).getHRef());
//
//            for (int i = 0; i < equips.length; i++)
//            {
//                BHEquip equip = equips[i];
//
//                EquipNavId equipNav = equip.makeNavId(siteNav);
//
//                HDictBuilder hd = new HDictBuilder();
//                hd.add("navId", HStr.make(equipNav.getHRef().val));
//                hd.add(server.getConfigStorehouse().createComponentTags(equip));
//
//                dicts.add(hd.toDict());
//            }
//        }
//
//        /////////////////////////////////////////////
//        // find points for a given equip
//
//        else if (navId.startsWith(EquipNavId.EQUIP + ":"))
//        {
//            EquipNavId equipNav = EquipNavId.make(HRef.make(navId));
//            BHEquip equip = fetchEquipByNav(equipNav);
//            HRef equipRef = NHRef.make(equip).getHRef();
//
//            Iterator it = server.getConfigStorehouse().makeIterator();
//            while (it.hasNext())
//            {
//                HDict tags = (HDict) it.next();
//                if (tags.has("point") && tags.has("equipRef"))
//                {
//                    HRef ref = tags.getRef("equipRef");
//                    if (ref.equals(equipRef))
//                        dicts.add(tags);
//                }
//            }
//        }
//
//        // oops
//        else throw new BajaRuntimeException("Cannot lookup nav for " + navId);
//
//        // done
//        return HGridBuilder.dictsToGrid((HDict[]) dicts.trim());
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

//    /**
//      * Fetch all the sites
//      */
//    public BHSite[] fetchSites() 
//    {
//        Iterator itr = new ComponentTreeIterator(
//            (BComponent) BOrd.make("slot:/").resolve(service, null).get());
//
//        Array arr = new Array(BHSite.class);
//        while (itr.hasNext())
//        {
//            BComponent comp = (BComponent) itr.next();
//            if (comp instanceof BHSite)
//                arr.add(comp);
//        }
//
//        return (BHSite[]) arr.trim();
//    }
//
//    /**
//      * Fetch all the equips
//      */
//    public BHEquip[] fetchEquips()
//    {
//        Iterator itr = new ComponentTreeIterator(
//            (BComponent) BOrd.make("slot:/").resolve(service, null).get());
//
//        Array arr = new Array(BHEquip.class);
//        while (itr.hasNext())
//        {
//            BComponent comp = (BComponent) itr.next();
//            if (comp instanceof BHEquip)
//                arr.add(comp);
//        }
//
//        return (BHEquip[]) arr.trim();
//    }
//
//    /**
//      * Fetch all the equips who reference the given site.
//      */
//    public BHEquip[] fetchEquips(HRef siteRef)
//    {
//        Iterator itr = new ComponentTreeIterator(
//            (BComponent) BOrd.make("slot:/").resolve(service, null).get());
//
//        Array arr = new Array(BHEquip.class);
//        while (itr.hasNext())
//        {
//            BComponent comp = (BComponent) itr.next();
//            if (comp instanceof BHEquip)
//            {
//                HDict tags = server.getConfigStorehouse().createComponentTags(comp);
//                if (tags.has("siteRef") && tags.get("siteRef").equals(siteRef))
//                    arr.add(comp);
//            }
//        }
//
//        return (BHEquip[]) arr.trim();
//    }
//
//    /**
//      * Fetch the site with the given navId
//      */
//    public BHSite fetchSiteByNav(SiteNavId siteNav) 
//    {
//        Iterator itr = new ComponentTreeIterator(
//            (BComponent) BOrd.make("slot:/").resolve(service, null).get());
//
//        Array arr = new Array(BHSite.class);
//        while (itr.hasNext())
//        {
//            BComponent comp = (BComponent) itr.next();
//            if (comp instanceof BHSite)
//            {
//                BHSite site = (BHSite) comp;
//                if (site.makeNavId().equals(siteNav))
//                    return site;
//            }
//        }
//
//        throw new BajaRuntimeException(
//            "Cannot fetch site with nav " + siteNav);
//    }
//
//    /**
//      * Fetch the equip with the given navId
//      */
//    public BHEquip fetchEquipByNav(EquipNavId equipNav) 
//    {
//        SiteNavId siteNav = SiteNavId.make(equipNav.getSiteName());
//
//        Iterator itr = new ComponentTreeIterator(
//            (BComponent) BOrd.make("slot:/").resolve(service, null).get());
//
//        Array arr = new Array(BHEquip.class);
//        while (itr.hasNext())
//        {
//            BComponent comp = (BComponent) itr.next();
//            if (comp instanceof BHEquip)
//            {
//                BHEquip equip = (BHEquip) comp;
//                if (equip.makeNavId(siteNav).equals(equipNav))
//                    return equip;
//            }
//        }
//
//        throw new BajaRuntimeException(
//            "Cannot fetch equip with nav " + equipNav);
//    }
}

