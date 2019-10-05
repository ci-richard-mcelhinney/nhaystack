//
// Copyright 2019 Project Haystack All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//    5 Oct 2019    Richard McELhinney  Creation
//

package nhaystack.server;

import org.projecthaystack.HNum;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.baja.control.BNumericWritable;
import javax.baja.control.enums.BPriorityLevel;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatus;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.test.BTestNg;

import org.projecthaystack.HVal;

@NiagaraType
@Test
public class BPointIOTest extends BTestNg
{
    @Override
    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BPointIOTest.class);


    @Test
    public void testMatchLevel()
    {
        Assert.assertEquals(PointIO.matchLevel(1), BPriorityLevel.level_1);
        Assert.assertEquals(PointIO.matchLevel(10), BPriorityLevel.level_10);
        Assert.assertEquals(PointIO.matchLevel(16), BPriorityLevel.level_16);
        Assert.assertEquals(PointIO.matchLevel(17), BPriorityLevel.fallback);
    }

    @Test
    public void testWiteNW()
    {
        BNumericWritable tp = new BNumericWritable();

        PointIO.writeNW(tp, BPriorityLevel.level_10, null);
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

        PointIO.writeNW(tp, BPriorityLevel.level_10, HNum.make(100d));
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.ok);
        Assert.assertEquals(tp.getIn10().getValue(), 100d);

        PointIO.writeNW(tp, BPriorityLevel.level_10, null);
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

        PointIO.writeNW(tp, BPriorityLevel.level_16, HNum.make(100d));
        Assert.assertEquals(tp.getIn16().getStatus(), BStatus.ok);
        Assert.assertEquals(tp.getIn16().getValue(), 100d);

        PointIO.writeNW(tp, BPriorityLevel.level_16, null);
        Assert.assertEquals(tp.getIn16().getStatus(), BStatus.nullStatus);

        PointIO.writeNW(tp, BPriorityLevel.fallback, HNum.make(100d));
        Assert.assertEquals(tp.getFallback().getStatus(), BStatus.ok);
        Assert.assertEquals(tp.getFallback().getValue(), 100d);
    }
}
