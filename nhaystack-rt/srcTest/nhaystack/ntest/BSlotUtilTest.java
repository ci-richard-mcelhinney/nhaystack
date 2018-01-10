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

import com.tridium.testng.*;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.*;

@NiagaraType
public class BSlotUtilTest extends BTestNg
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BSlotUtilTest(2979906276)1.0$ @*/
/* Generated Sun Nov 12 20:46:32 AEDT 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BSlotUtilTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  @Test
  public void testFromNiagara()
  {
    String res = SlotUtil.fromNiagara("");
    Assert.assertEquals(res.length(), 0);
    Assert.assertEquals(res, "");

    String str1 = "/Services/UserService/richard$20mac$/email";
    String str1expected = ".Services.UserService.richard-mac~.email";
    res = SlotUtil.fromNiagara(str1);
    Assert.assertEquals(res, str1expected);

    String str2 = "/Services/UserService/richard$22mac$/email";
    String str2expected = ".Services.UserService.richard~22mac~.email";
    res = SlotUtil.fromNiagara(str2);
    Assert.assertEquals(res, str2expected);
    Assert.assertEquals(res.length(), str2expected.length());
  }

  @Test
  public void testToNiagara()
  {
    String str1 = ".Folder.Component.mySlot-Name.how~can.this.wo-rk";
    String epet = "/Folder/Component/mySlot$20Name/how$can/this/wo$20rk";

    String res = SlotUtil.toNiagara(str1);
    Assert.assertEquals(res.length(), epet.length());
    Assert.assertEquals(res, epet);
  }

  @Test
  public void testFromEnum()
  {

  }

//  public void test() throws Exception
//  {
//    verifyUtil();
//  }
//
//  void verifyPath(String axPath, String hPath) throws Exception
//  {
////        verifyEq(SlotUtil.fromNiagara(axPath), hPath);
////        verifyEq(SlotUtil.toNiagara(hPath), axPath);
//
//  }
//
//  void verifyUtil() throws Exception
//  {
//    verifyPath(
//            "/AHU2/BooleanWritable",
//            "AHU2.BooleanWritable");
//
//    verifyPath(
//            "/AHU2/Boolean$20Writable",
//            "AHU2.Boolean-Writable");
//
//    verifyPath(
//            "/AHU2/Boolean$20Writable$2f",
//            "AHU2.Boolean-Writable~2f");
//
//    verifyPath(
//            "/$20AHU2/Boolean$20Writable$2f$20",
//            "-AHU2.Boolean-Writable~2f-");
//
//    verifyPath(
//            "/$21AHU2/Boolean$21Writable$2f$21",
//            "~21AHU2.Boolean~21Writable~2f~21");
//  }
}
