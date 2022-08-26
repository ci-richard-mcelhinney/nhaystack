package nhaystack.ntest;

import nhaystack.server.TagManager;
import org.testng.annotations.Test;

import javax.baja.control.BNumericWritable;
import javax.baja.data.BIDataValue;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.test.BTestNg;
import javax.baja.sys.*;

import static org.testng.Assert.*;

@NiagaraType
public class BTagManagerTest extends BTestNg
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BTagManagerTest(2979906276)1.0$ @*/
/* Generated Fri Aug 26 11:06:01 AEST 2022 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTagManagerTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  @Test
  public void testGetNumberFacetsBadConfig()
  {
    BFacets f;
    BNumericWritable number;
    BNumber facetVal;

    assertTrue(true);

    number = new BNumericWritable();
    f = BFacets.make(BFacets.MIN, BRelTime.makeSeconds(5));
    number.setFacets(f);
    facetVal = TagManager.getNumberFacet(number.getFacets(), BFacets.MIN);
    assertEquals(facetVal, BDouble.NaN);

    number = new BNumericWritable();
    f = BFacets.make(BFacets.MAX, BRelTime.makeSeconds(5));
    number.setFacets(f);
    facetVal = TagManager.getNumberFacet(number.getFacets(), BFacets.MAX);
    assertEquals(facetVal, BDouble.NaN);

    number = new BNumericWritable();
    f = BFacets.make(BFacets.PRECISION, BRelTime.makeSeconds(5));
    number.setFacets(f);
    facetVal = TagManager.getNumberFacet(number.getFacets(), BFacets.PRECISION);
    assertEquals(facetVal, BDouble.NaN);
  }

  @Test
  public void testGetNumberFacetsFieldNotSupported()
  {
    BFacets f;
    BNumericWritable number;
    BNumber facetVal;

    assertTrue(true);

    number = new BNumericWritable();
    f = BFacets.make("testFacetName", BDouble.make(20d));
    number.setFacets(f);
    facetVal = TagManager.getNumberFacet(number.getFacets(), "testFacetName");
    assertEquals(facetVal, BDouble.NaN);

    f = BFacets.make(BFacets.MAX, BDouble.make(100d));
    number.setFacets(f);
    facetVal = TagManager.getNumberFacet(number.getFacets(), BFacets.MAX);
    assertEquals(facetVal, BDouble.make(100d));

    f = BFacets.make(BFacets.MIN, BDouble.make(-1d));
    number.setFacets(f);
    facetVal = TagManager.getNumberFacet(number.getFacets(), BFacets.MIN);
    assertEquals(facetVal, BDouble.make(-1d));

    f = BFacets.make(BFacets.PRECISION, BDouble.make(2d));
    number.setFacets(f);
    facetVal = TagManager.getNumberFacet(number.getFacets(), BFacets.PRECISION);
    assertEquals(facetVal, BDouble.make(2d));
  }

  @Test
  public void testGetNumberFacetsSupportedFields()
  {
    BFacets f;
    BNumericWritable number;
    BNumber facetVal;

    number = new BNumericWritable();
    f = BFacets.make(
            new String[]{BFacets.MAX, BFacets.MIN, BFacets.PRECISION},
            new BIDataValue[] {BDouble.NEGATIVE_INFINITY, BDouble.POSITIVE_INFINITY, BDouble.make(2d)}
    );
    number.setFacets(f);

    facetVal = TagManager.getNumberFacet(number.getFacets(), BFacets.MAX);
    assertEquals(facetVal, null);

    facetVal = TagManager.getNumberFacet(number.getFacets(), BFacets.MIN);
    assertEquals(facetVal, null);

    facetVal = TagManager.getNumberFacet(number.getFacets(), BFacets.PRECISION);
    assertEquals(facetVal, BDouble.make(2d));
  }
}
