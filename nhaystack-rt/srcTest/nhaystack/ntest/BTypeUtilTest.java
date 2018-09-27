package nhaystack.ntest;

import nhaystack.util.*;

import org.projecthaystack.*;
import org.testng.Assert;
import org.testng.annotations.*;

import javax.baja.alarm.ext.BAlarmState;
import javax.baja.control.BEnumWritable;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatus;
import javax.baja.sys.*;

import javax.baja.test.BTestNg;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.*;

@NiagaraType
public class BTypeUtilTest extends BTestNg
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BTypeUtilTest(2979906276)1.0$ @*/
/* Generated Wed Sep 06 12:03:04 AEST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTypeUtilTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  @BeforeClass(alwaysRun = true)
  public void setup()
  {}

  @AfterClass(alwaysRun = true)
  public void teardown()
  {}

  @Test(enabled = true)
  public void verifyToBajaSimple()
  {
    System.out.println("-- nhaystack-rtTest:BTypeUtilTest.verifyToBajaSimple");
    try
    {
      // HStr -> BString conversion
      System.out.println("  -- HStr -> BString");
      HStr str = HStr.make("discharge");
      BSimple res = TypeUtil.toBajaSimple(str);
      Assert.assertEquals(BString.class, res.getClass());
      Assert.assertEquals(res.encodeToString(), "discharge");

      // HNum -> BDouble conversion
      HNum num = HNum.make(5.5d);
      System.out.println("  -- HNum -> BDouble");
      res = TypeUtil.toBajaSimple(num);
      Assert.assertEquals(BDouble.class, res.getClass());
      Assert.assertEquals(((BDouble)res).getDouble(), 5.5);

      // HNum as time -> BRelTime
      System.out.println("  -- HNum as a time based quantity -> BRelTime");
      num = HNum.make(2000, "s");
      res = TypeUtil.toBajaSimple(num);
      Assert.assertEquals(res.getClass(), BRelTime.class);
      Assert.assertEquals(((BRelTime)res).getSeconds(), 2000);

      // HBool -> BBoolean
      System.out.println("  -- HBool -> BBoolean");
      HBool bool = HBool.TRUE;
      res = TypeUtil.toBajaSimple(bool);
      Assert.assertEquals(res.getClass(), BBoolean.class);
      Assert.assertEquals(((BBoolean)res).getBoolean(), true);

      // HMarker -> BMarker.MARKER
      System.out.println("  -- HMarker -> BMarker.MARKER");
      res = TypeUtil.toBajaSimple(HMarker.VAL);
      Assert.assertEquals(res.getClass(), BMarker.class);
      Assert.assertEquals((BMarker)res, BMarker.MARKER);
    }
    catch(IOException e)
    {
      e.printStackTrace();
      Assert.fail();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      Assert.fail();
    }
  }


  @Test(enabled = true)
  public void verifyFromBajaSimple()
  {
    System.out.println("-- nhaystack-rtTest:BTypeUtilTest.verifyFromBajaSimple");
    try
    {
      System.out.println("  -- BString -> HStr");
      BString str = BString.make("condenser");
      HVal hstr = TypeUtil.fromBajaSimple(str, false);
      Assert.assertEquals(hstr.getClass(), HStr.class);
      Assert.assertEquals(((HStr)hstr).val, "condenser");

      System.out.println("  -- BNumber -> HNum");
      BDouble dbl = BDouble.make(5.5d);
      HNum num = (HNum) TypeUtil.fromBajaSimple(dbl, false);
      Assert.assertEquals(num.getClass(), HNum.class);
      Assert.assertTrue(num.val == dbl.getDouble());

      System.out.println("  -- BBoolean -> HBool");
      BBoolean bool = BBoolean.TRUE;
      HBool hbool = (HBool) TypeUtil.fromBajaSimple(bool, false);
      Assert.assertEquals(hbool.getClass(), HBool.class);
      Assert.assertEquals(hbool.val, bool.getBoolean());

      System.out.println("  -- BRelTime -> IllegalStateException");
      BRelTime reltime = BRelTime.makeSeconds(300);
      try
      {
        HNum timeNum = (HNum) TypeUtil.fromBajaSimple(reltime, false );
        Assert.fail();
      }
      catch(Exception e)
      {
        Assert.assertTrue(true);
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test(enabled = true)
  public void verifyToBajaStatus()
  {
    System.out.println("-- nhaystack-rtTest:BTypeUtilTest.verifyToBajaStatus");
    Assert.assertEquals(TypeUtil.toBajaStatus(HStr.make("ok")), BStatus.ok);
    Assert.assertEquals(TypeUtil.toBajaStatus((HStr.make("fault"))), BStatus.fault);
    Assert.assertEquals(TypeUtil.toBajaStatus((HStr.make("down"))), BStatus.down);
    Assert.assertEquals(TypeUtil.toBajaStatus((HStr.make("disabled"))), BStatus.disabled);
    Assert.assertEquals(TypeUtil.toBajaStatus((HStr.make("unknown"))), BStatus.nullStatus);
  }

  @Test(enabled = true)
  public void verifyActionArgsToBaja_Simples()
  {
    HDict args = new HDictBuilder().add("test", 1).toDict();
    BComponent comp = mock(BComponent.class);
    Action action = mock(Action.class);

    // action parameter is null
    when(action.getParameterDefault()).thenReturn(null);
    BValue val = TypeUtil.actionArgsToBaja(null, comp, action);
    Assert.assertNull(val);

    // action parameter is not null
    when(action.getParameterDefault()).thenReturn(BInteger.make(1));
    when(action.getParameterType()).thenReturn(BDouble.TYPE);
    val = TypeUtil.actionArgsToBaja(args, comp, action);
    Assert.assertEquals(((BDouble)val).getInt(), 1);

    // resolved simple is different type to action parameter
    when(action.getParameterDefault()).thenReturn(BInteger.make(1));
    when(action.getParameterType()).thenReturn(BString.TYPE);
    try
    {
      val = TypeUtil.actionArgsToBaja(args, comp, action);
      Assert.fail();
    }
    catch(Exception e)
    {
      Assert.assertEquals(e.getClass(), IllegalStateException.class);
    }

    // testing the retrieval of an enum from facets
    args = new HDictBuilder().add("test", HStr.make("normal")).toDict();
    BFacets facets = BFacets.makeEnum(BEnumRange.make(BAlarmState.TYPE));
    BEnumWritable enumw = mock(BEnumWritable.class);
    when(action.getParameterDefault()).thenReturn(BAlarmState.normal);
    when(action.getParameterType()).thenReturn(BAlarmState.TYPE);
    when(enumw.getFacets()).thenReturn(facets);
    BEnum enm = (BEnum) TypeUtil.actionArgsToBaja(args, enumw, action);
    Assert.assertEquals(enm.getOrdinal(), BAlarmState.NORMAL);

  }

  @Test(enabled = true)
  public void verifyActionArgsToBaja_Complexes()
  {

  }
}
