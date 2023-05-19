//
// Copyright (c) 2023, Conserve It Pty. Ltd.
//

package nhaystack.ntest;

import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.server.*;
import nhaystack.site.BHSite;
import nhaystack.util.NHaystackConst;
import org.testng.Assert;
import org.testng.annotations.*;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;
import javax.baja.tag.*;
import javax.baja.util.BFolder;
import javax.baja.xml.*;

/**
 * Testing for hierarchical ref-based structure.
 *
 * @author Owen Feik
 * @creation 19/05/2023
 */
@NiagaraType
public class BNavTest
  extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BNavTest(2979906276)1.0$ @*/
/* Generated Fri May 19 16:24:53 AEST 2023 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNavTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  @BeforeTest
  @Override
  public void setupStation() throws Exception {
    super.setupStation();
    BStation station = stationHandler.getStation();

    station.add("folder", folder = new BFolder());
    folder.add("site", site = new BHSite());
  }

  @Test
  public void testNavHierarchy() throws Exception
  {
    // Structure:
    // site
    //   zone
    //     zone
    //       equip
    //       equip
    //         equip
    //     equip
    BComponent zone1 = newSpace();
    site.add("zone" + suffix++, zone1);
    newEquip(zone1);
    BComponent zone2 = newSpace(zone1);
    newEquip(zone2);
    BComponent equip3 = newEquip(zone2);
    newEquip(equip3);

    XElem root = getXml();
    Assert.assertEquals(root.elems().length, 1); // [sepNav]
    XElem sepNav = root.elems()[0];
    Assert.assertEquals(sepNav.elems().length, 1); // [site]
    XElem site = sepNav.elems()[0];
    Assert.assertEquals(site.name(), "site");
    Assert.assertEquals(site.elems().length, 1); // [zone1]
    XElem space1 = site.elems()[0];
    Assert.assertEquals(space1.name(), "space");
    Assert.assertEquals(space1.elems().length, 2); // [zone2, equip1]
    XElem space2 = space1.elem("space");
    Assert.assertEquals(space2.elems().length, 2); // [equip2, equip3]

    // Check that one of the equipment nodes has equip4 as a child.
    boolean childFound = false;
    for (XElem equip : space2.elems()) {
      Assert.assertEquals(equip.name(), "equip");
      if (equip.elems().length == 1) {
        Assert.assertFalse(childFound); // Only equip3 has a child equip
        childFound = true;
      }
    }
    Assert.assertTrue(childFound);
  }

  private XElem getXml() throws Exception
  {
      new BNHaystackRebuildCacheJob(nhaystackService).run(null);
      BValue val = nhaystackService.invoke(BNHaystackService.fetchSepNav, null);
      String xml = ((BString) val).getString();
      System.out.println(xml);
      return XParser.make(xml).parse();
  }

  private BComponent newSpace() {
    return newSpace(null);
  }

  private BComponent newSpace(BComponent parent) {
    BComponent space = new BComponent();
    tag(space, NHaystackConst.ID_SPACE);
    siteRef(space);
    if (parent != null) {
      spaceRef(parent, space);
      parent.add("space" + suffix++, space);
    }
    return space;
  }

  private BComponent newEquip() {
    return newEquip(null);
  }

  private BComponent newEquip(BComponent parent) {
    BComponent equip = new BComponent();
    tag(equip, NHaystackConst.ID_EQUIP);
    siteRef(equip);
    if (parent != null) {
      if (parent.tags().contains(NHaystackConst.ID_EQUIP)) {
        equipRef(parent, equip);
      } else if (parent.tags().contains(NHaystackConst.ID_SPACE)) {
        spaceRef(parent, equip);
      }
      parent.add("equip" + suffix++, equip);
    }
    return equip;
  }

  private void siteRef(BComponent comp) {
    relate(comp, site, NHaystackConst.ID_SITE_REF);
  }

  private static void spaceRef(BComponent space, BComponent comp) {
    relate(comp, space, NHaystackConst.ID_SPACE_REF);
  }

  private static void equipRef(BComponent equip, BComponent comp) {
    relate(comp, equip, NHaystackConst.ID_EQUIP_REF);
  }

  private static void tag(BComponent comp, Id tag) {
    comp.tags().set(Tag.newTag(tag.getQName()));
  }

  private static void relate(BComponent from, BComponent to, Id id) {
    from.makeRelation(id, to, null);
  }

  private BFolder folder;
  private BHSite site;
  int suffix = 1;
}
