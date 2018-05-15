//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.driver.point;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HNum;
import org.projecthaystack.HStr;
import org.projecthaystack.HVal;
import org.projecthaystack.client.HClient;

/**
  * BNHaystackNumberProxyExt is a proxy extension for numeric remote haystack points.
  */
@NiagaraType
public class BNHaystackNumberProxyExt extends BNHaystackProxyExt
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackNumberProxyExt(2979906276)1.0$ @*/
/* Generated Fri Nov 17 12:09:49 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackNumberProxyExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
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
        }
    }

    @Override
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

