//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   15 Apr 2019  Eric Anderson  Creation
//
package nhaystack.ntest;

import static nhaystack.util.NHaystackConst.ID_EQUIP;
import static nhaystack.util.NHaystackConst.ID_EQUIP_REF;
import static nhaystack.util.NHaystackConst.ID_SITE;
import static nhaystack.util.NHaystackConst.ID_SITE_REF;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import javax.baja.control.BControlPoint;
import javax.baja.control.BNumericPoint;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BMarker;
import javax.baja.sys.BRelation;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Id;
import javax.baja.tag.Relation;
import javax.baja.util.BFolder;

import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
import org.testng.annotations.Test;

@Test(singleThreaded = true)
@NiagaraType
public class BSiteRefRelationTest extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BSiteRefRelationTest(2979906276)1.0$ @*/
/* Generated Mon Apr 15 15:41:19 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BSiteRefRelationTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void testTaggedComponents() throws Exception
    {
        BStation station = stationHandler.getStation();
        BFolder sites = new BFolder();
        station.add("sites", sites);
        BFolder equips = new BFolder();
        station.add("equips", equips);
        BFolder externalPoints = new BFolder();
        station.add("externalPoints", externalPoints);

        try
        {
            // sites
            //   site1 (hs:site)
            //   site2 (hs:site)
            //   site3 (ha:site)
            // equips
            //   equip1 (hs:equip, hs:siteRef->site1)
            //     equip1Point1 ([hs:equipRef->equip1], [hs:site->site1])
            //     equip1Point2 ([hs:equipRef->equip1], hs:site->site3)
            //     equip2Point3 (hs:equip->equip2, [hs:site->site2])
            //     equip2Point4 (hs:equip->equip2, hs:site->site3)
            //   equip2 (hs:equip, hs:siteRef->site2)
            //     equip2Point1 ([hs:equipRef->equip2], [hs:site->site2])
            //     equip2Point2 ([hs:equipRef->equip2], hs:site->site3)
            //   equip3 (hs:equip)
            //     equip3Point1 ([hs:equipRef->equip3])
            //     equip3Point2 ([hs:equipRef->equip3], hs:site->site3)
            // externalPoints
            //   equip1Point3 (hs:equip->equip1, [hs:site->site1])
            //   equip1Point4 (hs:equip->equip1, hs:site->site3)
            //
            // Should result in the following Site - Equip - Point organization
            // site1 <- hs:siteRef -< equip1
            //       <- hs:siteRef -< equip1Point1
            //       <- hs:siteRef -< equip1Point3
            // site2 <- hs:siteRef -< equip2
            //       <- hs:siteRef -< equip2Point1
            //       <- hs:siteRef -< equip2Point3
            // site3 <- hs:siteRef -< equip1Point2
            //       <- hs:siteRef -< equip2Point4
            //       <- hs:siteRef -< equip2Point2
            //       <- hs:siteRef -< equip3Point2
            //       <- hs:siteRef -< equip1Point4
            // equip1 <- hs:equipRef -< equip1Point1
            //        <- hs:equipRef -< equip1Point2
            //        <- hs:equipRef -< equip1Point3
            //        <- hs:equipRef -< equip1Point4
            // equip2 <- hs:equipRef -< equip2Point3
            //        <- hs:equipRef -< equip2Point4
            //        <- hs:equipRef -< equip2Point1
            //        <- hs:equipRef -< equip2Point2
            // equip3 <- hs:equipRef -< equip3Point1
            //        <- hs:equipRef -< equip3Point2
            BComponent site1 = new BComponent();
            BComponent site2 = new BComponent();
            BComponent site3 = new BComponent();
            BFolder equip1 = new BFolder();
            BFolder equip2 = new BFolder();
            BFolder equip3 = new BFolder();
            BNumericPoint equip1Point1 = new BNumericPoint();
            BNumericPoint equip1Point2 = new BNumericPoint();
            BNumericPoint equip1Point3 = new BNumericPoint();
            BNumericPoint equip1Point4 = new BNumericPoint();
            BNumericPoint equip2Point1 = new BNumericPoint();
            BNumericPoint equip2Point2 = new BNumericPoint();
            BNumericPoint equip2Point3 = new BNumericPoint();
            BNumericPoint equip2Point4 = new BNumericPoint();
            BNumericPoint equip3Point1 = new BNumericPoint();
            BNumericPoint equip3Point2 = new BNumericPoint();

            sites.add("site1", site1);
            sites.add("site2", site2);
            sites.add("site3", site3);

            equips.add("equip1", equip1);
            equips.add("equip2", equip2);
            equips.add("equip3", equip3);

            equip1.add("equip1Point1", equip1Point1);
            equip1.add("equip1Point2", equip1Point2);
            equip1.add("equip2Point3", equip2Point3);
            equip1.add("equip2Point4", equip2Point4);

            equip2.add("equip2Point1", equip2Point1);
            equip2.add("equip2Point2", equip2Point2);

            equip3.add("equip3Point1", equip3Point1);
            equip3.add("equip3Point2", equip3Point2);

            externalPoints.add("equip1Point3", equip1Point3);
            externalPoints.add("equip1Point4", equip1Point4);

            addSiteTag(site1);
            addSiteTag(site2);
            addSiteTag(site3);

            addEquipTag(equip1);
            addEquipTag(equip2);
            addEquipTag(equip3);

            addSiteRefRelation(equip1, site1);
            addSiteRefRelation(equip2, site2);
            addSiteRefRelation(equip1Point2, site3);
            addSiteRefRelation(equip1Point4, site3);
            addSiteRefRelation(equip2Point2, site3);
            addSiteRefRelation(equip2Point4, site3);
            addSiteRefRelation(equip3Point2, site3);

            addEquipRefRelation(equip1Point3, equip1);
            addEquipRefRelation(equip1Point4, equip1);
            addEquipRefRelation(equip2Point3, equip2);
            addEquipRefRelation(equip2Point4, equip2);

            hasEquipRefs(equip1,
                equip1Point1,
                equip1Point2,
                equip1Point3,
                equip1Point4);
            hasEquipRefs(equip2,
                equip2Point1,
                equip2Point2,
                equip2Point3,
                equip2Point4);
            hasEquipRefs(equip3,
                equip3Point1,
                equip3Point2);
            hasSiteRefs(site1,
                equip1,
                equip1Point1,
                equip1Point3);
            hasSiteRefs(site2,
                equip2,
                equip2Point1,
                equip2Point3);
            hasSiteRefs(site3,
                equip1Point2,
                equip1Point4,
                equip2Point2,
                equip2Point4,
                equip3Point2);
            hasNoSiteRefs(
                equip3,
                equip3Point1);
        }
        finally
        {
            station.remove(sites);
            station.remove(equips);
            station.remove(externalPoints);
        }
    }

    public void testHaystackComponents() throws Exception
    {
        BStation station = stationHandler.getStation();
        BFolder sites = new BFolder();
        station.add("sites", sites);
        BFolder equips = new BFolder();
        station.add("equips", equips);
        BFolder externalPoints = new BFolder();
        station.add("externalPoints", externalPoints);

        try
        {
            // sites
            //   site1
            //   site2
            //   site3
            // equips
            //   equip1
            //     equip (hs:siteRef->site1)
            //     equip1Point1 ([hs:equipRef->equip1], [hs:site->site1])
            //     equip1Point2 ([hs:equipRef->equip1], hs:site->site3)
            //     equip2Point3 (hs:equip->equip2, [hs:site->site2])
            //     equip2Point4 (hs:equip->equip2, hs:site->site3)
            //   equip2
            //     equip (hs:siteRef->site2)
            //     equip2Point1 ([hs:equipRef->equip2], [hs:site->site2])
            //     equip2Point2 ([hs:equipRef->equip2], hs:site->site3)
            //   equip3
            //     equip
            //     equip3Point1 ([hs:equipRef->equip3])
            //     equip3Point2 ([hs:equipRef->equip3], hs:site->site3)
            // externalPoints
            //   equip1Point3 (hs:equip->equip1, [hs:site->site1])
            //   equip1Point4 (hs:equip->equip1, hs:site->site3)
            //
            // Should result in the following Site - Equip - Point organization
            // site1 <- hs:siteRef -< equip1
            //       <- hs:siteRef -< equip1Point1
            //       <- hs:siteRef -< equip1Point3
            // site2 <- hs:siteRef -< equip2
            //       <- hs:siteRef -< equip2Point1
            //       <- hs:siteRef -< equip2Point3
            // site3 <- hs:siteRef -< equip1Point2
            //       <- hs:siteRef -< equip2Point4
            //       <- hs:siteRef -< equip2Point2
            //       <- hs:siteRef -< equip3Point2
            //       <- hs:siteRef -< equip1Point4
            // equip1 <- hs:equipRef -< equip1Point1
            //        <- hs:equipRef -< equip1Point2
            //        <- hs:equipRef -< equip1Point3
            //        <- hs:equipRef -< equip1Point4
            // equip2 <- hs:equipRef -< equip2Point3
            //        <- hs:equipRef -< equip2Point4
            //        <- hs:equipRef -< equip2Point1
            //        <- hs:equipRef -< equip2Point2
            // equip3 <- hs:equipRef -< equip3Point1
            //        <- hs:equipRef -< equip3Point2
            BHSite site1 = new BHSite();
            BHSite site2 = new BHSite();
            BHSite site3 = new BHSite();
            BFolder equip1Folder = new BFolder();
            BFolder equip2Folder = new BFolder();
            BFolder equip3Folder = new BFolder();
            BHEquip equip1 = new BHEquip();
            BHEquip equip2 = new BHEquip();
            BHEquip equip3 = new BHEquip();
            BNumericPoint equip1Point1 = new BNumericPoint();
            BNumericPoint equip1Point2 = new BNumericPoint();
            BNumericPoint equip1Point3 = new BNumericPoint();
            BNumericPoint equip1Point4 = new BNumericPoint();
            BNumericPoint equip2Point1 = new BNumericPoint();
            BNumericPoint equip2Point2 = new BNumericPoint();
            BNumericPoint equip2Point3 = new BNumericPoint();
            BNumericPoint equip2Point4 = new BNumericPoint();
            BNumericPoint equip3Point1 = new BNumericPoint();
            BNumericPoint equip3Point2 = new BNumericPoint();

            sites.add("site1", site1);
            sites.add("site2", site2);
            sites.add("site3", site3);

            equips.add("equip1", equip1Folder);
            equips.add("equip2", equip2Folder);
            equips.add("equip3", equip3Folder);

            equip1Folder.add("equip", equip1);
            equip1Folder.add("equip1Point1", equip1Point1);
            equip1Folder.add("equip1Point2", equip1Point2);
            equip1Folder.add("equip2Point3", equip2Point3);
            equip1Folder.add("equip2Point4", equip2Point4);

            equip2Folder.add("equip", equip2);
            equip2Folder.add("equip2Point1", equip2Point1);
            equip2Folder.add("equip2Point2", equip2Point2);

            equip3Folder.add("equip", equip3);
            equip3Folder.add("equip3Point1", equip3Point1);
            equip3Folder.add("equip3Point2", equip3Point2);

            externalPoints.add("equip1Point3", equip1Point3);
            externalPoints.add("equip1Point4", equip1Point4);

            addSiteRefRelation(equip1, site1);
            addSiteRefRelation(equip2, site2);
            addSiteRefRelation(equip1Point2, site3);
            addSiteRefRelation(equip1Point4, site3);
            addSiteRefRelation(equip2Point2, site3);
            addSiteRefRelation(equip2Point4, site3);
            addSiteRefRelation(equip3Point2, site3);

            addEquipRefRelation(equip1Point3, equip1);
            addEquipRefRelation(equip1Point4, equip1);
            addEquipRefRelation(equip2Point3, equip2);
            addEquipRefRelation(equip2Point4, equip2);

            hasEquipRefs(equip1,
                equip1Point1,
                equip1Point2,
                equip1Point3,
                equip1Point4);
            hasEquipRefs(equip2,
                equip2Point1,
                equip2Point2,
                equip2Point3,
                equip2Point4);
            hasEquipRefs(equip3,
                equip3Point1,
                equip3Point2);
            hasSiteRefs(site1,
                equip1,
                equip1Point1,
                equip1Point3);
            hasSiteRefs(site2,
                equip2,
                equip2Point1,
                equip2Point3);
            hasSiteRefs(site3,
                equip1Point2,
                equip1Point4,
                equip2Point2,
                equip2Point4,
                equip3Point2);
            hasNoSiteRefs(
                equip3,
                equip3Point1);
        }
        finally
        {
            station.remove(sites);
            station.remove(equips);
            station.remove(externalPoints);
        }
    }

    private static void hasNoSiteRefs(BComponent... components)
    {
        for (BComponent component : components)
        {
            assertTrue(component.relations().getAll(ID_SITE_REF).isEmpty(),
              "Component " + component.getSlotPath() + " incorrectly has siteRef relation");
        }
    }

    private static void hasEquipRefs(BComponent equip, BControlPoint... points)
    {
        hasRefsOut(ID_EQUIP_REF, equip, points);
        hasRefsIn(ID_EQUIP_REF, equip, points);
    }

    private static void hasSiteRefs(BComponent site, BComponent... sources)
    {
        hasRefsOut(ID_SITE_REF, site, sources);
        hasRefsIn(ID_SITE_REF, site, sources);
    }

    private static void hasRefsOut(Id id, BComponent target, BComponent... sources)
    {
        for (BComponent source : sources)
        {
            Collection<Relation> relations = source.relations().getAll(id);
            assertEquals(relations.size(), 1,
                "Number of relations from source " + source.getSlotPath());
            Relation relation = relations.iterator().next();
            assertTrue(relation.isOutbound(),
                "Relation is not outbound on source " + source.getSlotPath());
            assertEquals(relation.getEndpoint(), target,
                "Endpoint is " + ((BComponent) relation.getEndpoint()).getSlotPath() +
                " instead of " + target.getSlotPath() +
                " on source " + source.getSlotPath());
        }
    }

    private static void hasRefsIn(Id id, BComponent target, BComponent... sources)
    {
        Collection<Relation> relations = target.relations().getAll(id);
        assertEquals(relations.size(), sources.length,
            "Number of relations to target " + target.getSlotPath());
        allRelationsAreInbound(target, relations);

        for (BComponent source : sources)
        {
            boolean foundSource = false;
            for (Relation relation : relations)
            {
                if (relation.getEndpoint().equals(source))
                {
                    foundSource = true;
                    break;
                }
            }

            assertTrue(foundSource,
                "No relation from source " + source.getSlotPath() +
                " to target " + target.getSlotPath());
        }
    }

    private static void allRelationsAreInbound(BComponent target, Collection<Relation> relations)
    {
        for (Relation relation : relations)
        {
            assertTrue(relation.isInbound(),
                "Relation is not inbound from " + target.getSlotPath() +
                " to endpoint " + ((BComponent) relation.getEndpoint()).getSlotPath());
        }
    }

    private static void addEquipTag(BComponent component)
    {
        component.tags().set(ID_EQUIP, BMarker.MARKER);
    }

    private static void addSiteTag(BComponent component)
    {
        component.tags().set(ID_SITE, BMarker.MARKER);
    }

    private static void addEquipRefRelation(BControlPoint point, BComponent equip)
    {
        point.relations().add(new BRelation(ID_EQUIP_REF, equip));
    }

    private static void addSiteRefRelation(BComponent source, BComponent site)
    {
        source.relations().add(new BRelation(ID_SITE_REF, site));
    }
}
