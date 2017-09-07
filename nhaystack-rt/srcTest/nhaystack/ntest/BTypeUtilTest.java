package nhaystack.ntest;

import nhaystack.util.*;

import org.projecthaystack.*;
import org.testng.Assert;
import org.testng.TestNG;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;

import com.tridium.testng.*;

import java.io.IOException;

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
    System.out.println("running niagara tests");
    try
    {
      // HStr -> BString conversion
      System.out.println("HStr -> BString");
      HStr str = HStr.make("discharge");
      BSimple res = TypeUtil.toBajaSimple(str);
      Assert.assertEquals(BString.class, res.getClass());
      Assert.assertEquals(res.encodeToString(), "discharge");

      // HNum -> BDouble conversion
      HNum num = HNum.make(5.5d);
      System.out.println("HNum -> BDouble");
      res = TypeUtil.toBajaSimple(num);
      Assert.assertEquals(BDouble.class, res.getClass());
      Assert.assertEquals(((BDouble)res).getDouble(), 5.5);

      // HNum as time -> BRelTime
      System.out.println("HNum as a time based quantity -> BRelTime");
      num = HNum.make(2000, "s");
      res = TypeUtil.toBajaSimple(num);
      Assert.assertEquals(res.getClass(), BRelTime.class);
      Assert.assertEquals(((BRelTime)res).getSeconds(), 2000);

      // HBool -> BBoolean
      System.out.println("HBool -> BBoolean");
      HBool bool = HBool.TRUE;
      res = TypeUtil.toBajaSimple(bool);
      Assert.assertEquals(res.getClass(), BBoolean.class);
      Assert.assertEquals(((BBoolean)res).getBoolean(), true);

      try
      {
        System.out.println("HMarker -> IllegalStateException");
        TypeUtil.toBajaSimple(HMarker.VAL);
        Assert.fail();
      }
      catch(Exception ise)
      {
        Assert.assertEquals(ise.getClass(), IllegalStateException.class);
      }
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

  }
}
