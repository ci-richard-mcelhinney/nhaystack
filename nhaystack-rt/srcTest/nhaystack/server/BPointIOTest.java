//
// Copyright 2019 Project Haystack All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//    5 Oct 2019    Richard McELhinney  Creation
//

package nhaystack.server;

import org.projecthaystack.HBool;
import org.projecthaystack.HNum;
import org.projecthaystack.HStr;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.baja.control.*;
import javax.baja.control.enums.BPriorityLevel;
import javax.baja.driver.util.BPollFrequency;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.*;
import javax.baja.sys.*;
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
        verifyPointArrayStatus(tp, BPriorityLevel.level_10, BStatus.nullStatus);

        PointIO.writeNW(tp, BPriorityLevel.level_10, null);
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

        PointIO.writeNW(tp, BPriorityLevel.level_16, HNum.make(100d));
        Assert.assertEquals(tp.getIn16().getStatus(), BStatus.ok);
        Assert.assertEquals(tp.getIn16().getValue(), 100d);
        verifyPointArrayStatus(tp, BPriorityLevel.level_16, BStatus.nullStatus);

        PointIO.writeNW(tp, BPriorityLevel.level_16, null);
        Assert.assertEquals(tp.getIn16().getStatus(), BStatus.nullStatus);

        PointIO.writeNW(tp, BPriorityLevel.fallback, HNum.make(100d));
        Assert.assertEquals(tp.getFallback().getStatus(), BStatus.ok);
        Assert.assertEquals(tp.getFallback().getValue(), 100d);
        verifyPointArrayStatus(tp, BPriorityLevel.none, BStatus.nullStatus);
    }

    @Test
    public void testWriteBW()
    {
       BBooleanWritable tp = new BBooleanWritable();

       PointIO.writeBW(tp, BPriorityLevel.level_10, null);
       Assert.assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

       PointIO.writeBW(tp, BPriorityLevel.level_10, HBool.make(true));
       Assert.assertEquals(tp.getIn10().getStatus(), BStatus.ok);
       Assert.assertTrue(tp.getIn10().getValue());
       verifyPointArrayStatus(tp, BPriorityLevel.level_10, BStatus.nullStatus);

       PointIO.writeBW(tp, BPriorityLevel.level_10, null);
       Assert.assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

       PointIO.writeBW(tp, BPriorityLevel.level_16, HBool.make(true));
       Assert.assertEquals(tp.getIn16().getStatus(), BStatus.ok);
       Assert.assertTrue(tp.getIn16().getValue());
       verifyPointArrayStatus(tp, BPriorityLevel.level_16, BStatus.nullStatus);

       PointIO.writeBW(tp, BPriorityLevel.level_16, null);
       Assert.assertEquals(tp.getIn16().getStatus(), BStatus.nullStatus);

       PointIO.writeBW(tp, BPriorityLevel.fallback, HBool.make(true));
       Assert.assertEquals(tp.getFallback().getStatus(), BStatus.ok);
       Assert.assertTrue(tp.getFallback().getValue());
       verifyPointArrayStatus(tp, BPriorityLevel.none, BStatus.nullStatus);
    }

    @Test
    public void testWriteEW()
    {
        BEnumWritable tp = new BEnumWritable();
        tp.setFacets(BFacets.makeEnum(BEnumRange.make(BPollFrequency.TYPE)));

        PointIO.writeEW(tp, BPriorityLevel.level_10, null);
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

        PointIO.writeEW(tp, BPriorityLevel.level_10, HStr.make("fast"));
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.ok);
        verifyPointArrayStatus(tp, BPriorityLevel.level_10, BStatus.nullStatus);

        PointIO.writeEW(tp, BPriorityLevel.level_10, null);
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

        PointIO.writeEW(tp, BPriorityLevel.level_16, HStr.make("fast"));
        Assert.assertEquals(tp.getIn16().getStatus(), BStatus.ok);
        verifyPointArrayStatus(tp, BPriorityLevel.level_16, BStatus.nullStatus);

        PointIO.writeEW(tp, BPriorityLevel.level_16, null);
        Assert.assertEquals(tp.getIn16().getStatus(), BStatus.nullStatus);

        PointIO.writeEW(tp, BPriorityLevel.fallback, HStr.make("fast"));
        Assert.assertEquals(tp.getFallback().getStatus(), BStatus.ok);
        verifyPointArrayStatus(tp, BPriorityLevel.fallback, BStatus.nullStatus);
    }

    @Test
    public void testWriteSW()
    {
        BStringWritable tp = new BStringWritable();

        PointIO.writeSW(tp, BPriorityLevel.level_10, null);
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

        PointIO.writeSW(tp, BPriorityLevel.level_10, HStr.make("hellow world"));
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.ok);
        verifyPointArrayStatus(tp, BPriorityLevel.level_10, BStatus.nullStatus);

        PointIO.writeSW(tp, BPriorityLevel.level_10, null);
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

        PointIO.writeSW(tp, BPriorityLevel.level_16, HStr.make("hellow world"));
        Assert.assertEquals(tp.getIn16().getStatus(), BStatus.ok);
        verifyPointArrayStatus(tp, BPriorityLevel.level_16, BStatus.nullStatus);

        PointIO.writeSW(tp, BPriorityLevel.level_16, null);
        Assert.assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

        PointIO.writeSW(tp, BPriorityLevel.fallback, HStr.make("hellow world"));
        Assert.assertEquals(tp.getFallback().getStatus(), BStatus.ok);
        verifyPointArrayStatus(tp, BPriorityLevel.none, BStatus.nullStatus);
    }

    private void verifyPointArrayStatus(BControlPoint p, BPriorityLevel skip, BStatus st)
    {
        if      (p instanceof BNumericWritable)  verifyNWPointArrayStatus((BNumericWritable) p, skip, st);
        else if (p instanceof BBooleanWritable)  verifyBWPointArrayStatus((BBooleanWritable) p, skip, st);
        else if (p instanceof BEnumWritable)     verifyEWPointArrayStatus((BEnumWritable) p, skip, st);
        else if (p instanceof BStringWritable)   verifySWPointArrayStatus((BStringWritable) p, skip, st);
    }

    /**
     * Verify that all levels in the priority array, except a specified one to skip, have
     * the specified status
     *
     * @param nw the control point to check
     * @param skip the priority array level to skip
     * @param st the status to check for
     */
    private void verifyNWPointArrayStatus(BNumericWritable nw, BPriorityLevel skip, BStatus st)
    {
        BPriorityLevel curLevel;
        BStatusNumeric sn;

        for (int i = 1; i < 17; i++)
        {
            curLevel = BPriorityLevel.make(i);
            if (curLevel == skip)
            {
                continue;
            }
            sn = nw.getLevel(curLevel);
            Assert.assertEquals(sn.getStatus(), st);
        }
    }

    private void verifyBWPointArrayStatus(BBooleanWritable bw, BPriorityLevel skip, BStatus st)
    {
        BPriorityLevel curLevel;
        BStatusBoolean sb;

        for (int i = 1; i < 17; i++)
        {
            curLevel = BPriorityLevel.make(i);
            if (curLevel == skip)
            {
                continue;
            }

            sb = bw.getLevel(curLevel);
            Assert.assertEquals(sb.getStatus(), st);
        }
    }

    private void verifyEWPointArrayStatus(BEnumWritable ew, BPriorityLevel skip, BStatus st)
    {
        BPriorityLevel curLevel;
        BStatusEnum se;

        for (int i = 1; i < 17; i++)
        {
            curLevel = BPriorityLevel.make(i);
            if (curLevel == skip)
            {
                continue;
            }

            se = ew.getLevel(curLevel);
            Assert.assertEquals(se.getStatus(), st);
        }
    }

    private void verifySWPointArrayStatus(BStringWritable sw, BPriorityLevel skip, BStatus st)
    {
        BPriorityLevel curLevel;
        BStatusString ss;

        for (int i = 1; i < 17; i++)
        {
            curLevel = BPriorityLevel.make(i);
            if (curLevel == skip)
            {
                continue;
            }

            ss = sw.getLevel(curLevel);
            Assert.assertEquals(ss.getStatus(), st);
        }
    }
}
