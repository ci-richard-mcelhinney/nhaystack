package nhaystack.driver.point;

import javax.baja.status.*;
import javax.baja.sys.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.util.*;

public class BNHaystackNumberProxyExt extends BNHaystackProxyExt
{
    /*-
    class BNHaystackNumberProxyExt
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackNumberProxyExt(290784672)1.0$ @*/
/* Generated Mon Apr 07 09:39:40 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackNumberProxyExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void doRead(HVal curVal, HStr curStatus)
    {
        BStatus status = TypeUtil.toBajaStatus(curStatus);
        if (status.isOk())
        {
            double d = ((HNum) curVal).val;
            readOk(new BStatusNumeric(d, status));
        }
        else
        {
            readFail("read fault");
            return;
        }
    }

    public void doWrite() throws Exception
    {
        BStatusNumeric writeValue = (BStatusNumeric) getWriteValue();

        HClient client = getHaystackServer().getHaystackClient();
        client.pointWrite(
            getId().getRef(),
            getHaystackWriteLevel(),
            null, // who
            HNum.make(writeValue.getNumeric()),
            null); // dur

        writeOk(writeValue);
    }
}

