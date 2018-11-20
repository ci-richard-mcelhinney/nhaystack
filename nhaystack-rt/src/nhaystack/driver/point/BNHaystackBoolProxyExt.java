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
import javax.baja.status.BStatusBoolean;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HBool;
import org.projecthaystack.HStr;
import org.projecthaystack.HVal;
import org.projecthaystack.client.HClient;

/**
  * BNHaystackBoolProxyExt is a proxy extension for boolean remote haystack points.
  */

@NiagaraType
public class BNHaystackBoolProxyExt extends BNHaystackProxyExt
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackBoolProxyExt(2979906276)1.0$ @*/
/* Generated Fri Nov 17 12:03:21 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackBoolProxyExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
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
        }
    }

    @Override
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

