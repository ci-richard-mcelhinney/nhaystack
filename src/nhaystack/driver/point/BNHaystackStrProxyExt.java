//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.point;

import javax.baja.status.*;
import javax.baja.sys.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.util.*;

/**
  * BNHaystackStrProxyExt is a proxy extension for string remote haystack points.
  */
public class BNHaystackStrProxyExt extends BNHaystackProxyExt
{
    /*-
    class BNHaystackStrProxyExt
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackStrProxyExt(3391908686)1.0$ @*/
/* Generated Sun Jun 01 09:34:38 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackStrProxyExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void doRead(HVal curVal, HStr curStatus)
    {
        BStatus status = TypeUtil.toBajaStatus(curStatus);
        if (status.isOk())
        {
            String s = ((HStr) curVal).val;
            readOk(new BStatusString(s, status));
        }
        else
        {
            readFail("read fault");
            return;
        }
    }

    public void doWrite() throws Exception
    {
        BStatusString writeValue = (BStatusString) getWriteValue();

        HClient client = getHaystackServer().getHaystackClient();
        client.pointWrite(
            getId().getRef(),
            getHaystackWriteLevel(),
            null, // who
            HStr.make(writeValue.getValue()),
            null); // dur

        writeOk(writeValue);
    }
}

