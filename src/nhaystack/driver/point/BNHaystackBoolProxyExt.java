package nhaystack.driver.point;

import java.util.*;

import javax.baja.driver.point.*;
import javax.baja.status.*;
import javax.baja.sys.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.*;
import nhaystack.driver.*;
import nhaystack.util.*;

public class BNHaystackBoolProxyExt extends BNHaystackProxyExt
{
    /*-
    class BNHaystackBoolProxyExt
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackBoolProxyExt(1679747500)1.0$ @*/
/* Generated Mon Apr 07 09:27:24 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackBoolProxyExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void doRead(HVal curVal, HStr curStatus)
    {
        BStatus status = TypeUtil.toBajaStatus(curStatus);
        if (status.isOk())
        {
            boolean b = ((HBool) curVal).val;
            readOk(new BStatusBoolean(b, status));
        }
        else
        {
            readFail("read fault");
            return;
        }
    }

    public void doWrite() throws Exception
    {
        BStatusBoolean writeValue = (BStatusBoolean) getWriteValue();

        HClient client = getHaystackServer().getHaystackClient();
        client.pointWrite(
            getId().getRef(),
            getHaystackWriteLevel(),
            null, // who
            HBool.make(writeValue.getBoolean()),
            null); // dur

        writeOk(writeValue);
    }
}

