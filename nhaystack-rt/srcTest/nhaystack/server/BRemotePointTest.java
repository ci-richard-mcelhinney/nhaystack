//
// Copyright 2019 Project Haystack All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//    16 Mar 2020    Richard McELhinney  Creation
//


package nhaystack.server;

import com.tridium.nd.BNiagaraStation;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.baja.driver.BDevice;
import javax.baja.naming.SlotPath;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;
import javax.baja.bacnet.BBacnetDevice;
import javax.baja.test.BTestNg;

@NiagaraType
@Test
public class BRemotePointTest extends BTestNg
{
    @Override
    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BRemotePointTest.class);

    @Test
    public void testMakeSlotPath()
    {
        SlotPath res;
        SlotPath exp = new SlotPath("slot", "/Drivers/BacNet");

        res = RemotePoint.makeSlotPath("slot:/Drivers/BacNet");
        Assert.assertTrue(res.toString().equals(exp.toString()));
        Assert.assertNull(RemotePoint.makeSlotPath("richard:/test/a/b"));
        Assert.assertNull(RemotePoint.makeSlotPath("slot:|/|/Drivers/BacNet"));
    }

    @Test
    public void testIsRemoteDevice()
    {
        BDevice device = new BBacnetDevice();
        Assert.assertFalse(RemotePoint.isRemoteDevice(device));

        device = new BNiagaraStation();
        Assert.assertTrue(RemotePoint.isRemoteDevice(device));
    }

}
