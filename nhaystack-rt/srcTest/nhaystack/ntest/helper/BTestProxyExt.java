//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   02 Aug 2019  Eric Anderson  Creation
//

package nhaystack.ntest.helper;

import javax.baja.driver.point.BProxyExt;
import javax.baja.driver.point.BReadWriteMode;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

@NiagaraType
public class BTestProxyExt extends BProxyExt
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.helper.BTestProxyExt(2979906276)1.0$ @*/
/* Generated Fri Aug 02 16:28:08 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTestProxyExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    public Type getDeviceExtType()
    {
        return null;
    }

    @Override
    public BReadWriteMode getMode()
    {
        return getParentPoint().isWritablePoint() ?
            BReadWriteMode.writeonly :
            BReadWriteMode.readonly;
    }

    @Override
    public void readSubscribed(Context context) throws Exception
    {
    }

    @Override
    public void readUnsubscribed(Context context) throws Exception
    {
    }

    @Override
    public boolean write(Context context) throws Exception
    {
        writeOk(getWriteValue());
        return true;
    }
}
