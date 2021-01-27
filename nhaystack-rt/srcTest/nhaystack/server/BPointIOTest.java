//
// Copyright 2019 Project Haystack All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//    5 Oct 2019    Richard McELhinney  Creation
//

package nhaystack.server;

import org.projecthaystack.*;
import org.testng.annotations.Test;

import javax.baja.control.*;
import javax.baja.control.enums.BPriorityLevel;
import javax.baja.driver.util.BPollFrequency;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.schedule.BTimeSchedule;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.test.BTestNg;
import javax.baja.units.BUnit;

import static org.testng.Assert.*;

@NiagaraType
@Test
public class BPointIOTest extends BTestNg
{
  @Override
  public Type getType()
  {
    return TYPE;
  }

  public static final Type TYPE = Sys.loadType(BPointIOTest.class);


  @Test
  public void testMatchLevel()
  {
    assertEquals(PointIO.matchLevel(1), BPriorityLevel.level_1);
    assertEquals(PointIO.matchLevel(10), BPriorityLevel.level_10);
    assertEquals(PointIO.matchLevel(16), BPriorityLevel.level_16);
    assertEquals(PointIO.matchLevel(17), BPriorityLevel.fallback);
  }

  @Test
  public void testWriteNW()
  {
    BNumericWritable tp = new BNumericWritable();

    PointIO.writeNW(tp, BPriorityLevel.level_10, null);
    assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

    PointIO.writeNW(tp, BPriorityLevel.level_10, HNum.make(100d));
    assertEquals(tp.getIn10().getStatus(), BStatus.ok);
    assertEquals(tp.getIn10().getValue(), 100d);
    verifyPointArrayStatus(tp, BPriorityLevel.level_10, BStatus.nullStatus);

    PointIO.writeNW(tp, BPriorityLevel.level_10, null);
    assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

    PointIO.writeNW(tp, BPriorityLevel.level_16, HNum.make(100d));
    assertEquals(tp.getIn16().getStatus(), BStatus.ok);
    assertEquals(tp.getIn16().getValue(), 100d);
    verifyPointArrayStatus(tp, BPriorityLevel.level_16, BStatus.nullStatus);

    PointIO.writeNW(tp, BPriorityLevel.level_16, null);
    assertEquals(tp.getIn16().getStatus(), BStatus.nullStatus);

    PointIO.writeNW(tp, BPriorityLevel.fallback, HNum.make(100d));
    assertEquals(tp.getFallback().getStatus(), BStatus.ok);
    assertEquals(tp.getFallback().getValue(), 100d);
    verifyPointArrayStatus(tp, BPriorityLevel.none, BStatus.nullStatus);
  }

  @Test
  public void testWriteBW()
  {
    BBooleanWritable tp = new BBooleanWritable();

    PointIO.writeBW(tp, BPriorityLevel.level_10, null);
    assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

    PointIO.writeBW(tp, BPriorityLevel.level_10, HBool.make(true));
    assertEquals(tp.getIn10().getStatus(), BStatus.ok);
    assertTrue(tp.getIn10().getValue());
    verifyPointArrayStatus(tp, BPriorityLevel.level_10, BStatus.nullStatus);

    PointIO.writeBW(tp, BPriorityLevel.level_10, null);
    assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

    PointIO.writeBW(tp, BPriorityLevel.level_16, HBool.make(true));
    assertEquals(tp.getIn16().getStatus(), BStatus.ok);
    assertTrue(tp.getIn16().getValue());
    verifyPointArrayStatus(tp, BPriorityLevel.level_16, BStatus.nullStatus);

    PointIO.writeBW(tp, BPriorityLevel.level_16, null);
    assertEquals(tp.getIn16().getStatus(), BStatus.nullStatus);

    PointIO.writeBW(tp, BPriorityLevel.fallback, HBool.make(true));
    assertEquals(tp.getFallback().getStatus(), BStatus.ok);
    assertTrue(tp.getFallback().getValue());
    verifyPointArrayStatus(tp, BPriorityLevel.none, BStatus.nullStatus);
  }

  @Test
  public void testWriteEW()
  {
    BEnumWritable tp = new BEnumWritable();
    tp.setFacets(BFacets.makeEnum(BEnumRange.make(BPollFrequency.TYPE)));

    PointIO.writeEW(tp, BPriorityLevel.level_10, null);
    assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

    PointIO.writeEW(tp, BPriorityLevel.level_10, HStr.make("fast"));
    assertEquals(tp.getIn10().getStatus(), BStatus.ok);
    verifyPointArrayStatus(tp, BPriorityLevel.level_10, BStatus.nullStatus);

    PointIO.writeEW(tp, BPriorityLevel.level_10, null);
    assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

    PointIO.writeEW(tp, BPriorityLevel.level_16, HStr.make("fast"));
    assertEquals(tp.getIn16().getStatus(), BStatus.ok);
    verifyPointArrayStatus(tp, BPriorityLevel.level_16, BStatus.nullStatus);

    PointIO.writeEW(tp, BPriorityLevel.level_16, null);
    assertEquals(tp.getIn16().getStatus(), BStatus.nullStatus);

    PointIO.writeEW(tp, BPriorityLevel.fallback, HStr.make("fast"));
    assertEquals(tp.getFallback().getStatus(), BStatus.ok);
    verifyPointArrayStatus(tp, BPriorityLevel.fallback, BStatus.nullStatus);
  }

  @Test
  public void testWriteSW()
  {
    BStringWritable tp = new BStringWritable();

    PointIO.writeSW(tp, BPriorityLevel.level_10, null);
    assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

    PointIO.writeSW(tp, BPriorityLevel.level_10, HStr.make("hellow world"));
    assertEquals(tp.getIn10().getStatus(), BStatus.ok);
    verifyPointArrayStatus(tp, BPriorityLevel.level_10, BStatus.nullStatus);

    PointIO.writeSW(tp, BPriorityLevel.level_10, null);
    assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

    PointIO.writeSW(tp, BPriorityLevel.level_16, HStr.make("hellow world"));
    assertEquals(tp.getIn16().getStatus(), BStatus.ok);
    verifyPointArrayStatus(tp, BPriorityLevel.level_16, BStatus.nullStatus);

    PointIO.writeSW(tp, BPriorityLevel.level_16, null);
    assertEquals(tp.getIn10().getStatus(), BStatus.nullStatus);

    PointIO.writeSW(tp, BPriorityLevel.fallback, HStr.make("hellow world"));
    assertEquals(tp.getFallback().getStatus(), BStatus.ok);
    verifyPointArrayStatus(tp, BPriorityLevel.none, BStatus.nullStatus);
  }

  @Test
  public void testProcessScheduleTime()
  {
    HStr dayVal = HStr.make("1");
    BTimeSchedule time = new BTimeSchedule();

    assertEquals(PointIO.processTimes(null, dayVal, null), HDict.EMPTY);
    assertEquals(PointIO.processTimes(new BTimeSchedule(), null, null), HDict.EMPTY);
    assertEquals(PointIO.processTimes(null, null, null), HDict.EMPTY);

    time.setStart(BTime.make(23, 59, 59));
    time.setFinish(BTime.make(1, 2, 3));
    assertEquals(PointIO.processTimes(time, dayVal, null), HDict.EMPTY);

    HDictBuilder dict = new HDictBuilder();
    dict.add("start", HTime.make(2, 3, 4));
    dict.add("end", HTime.make(13, 14, 15));
    dict.add("val", HBool.TRUE);
    dict.add("dates", "N");
    dict.add("weekdays", dayVal);
    HDict exp = dict.toDict();
    time.setStart(BTime.make(2, 3, 4));
    time.setFinish(BTime.make(13, 14, 15));
    time.setEffectiveValue(new BStatusBoolean(true));

    assertEquals(PointIO.processTimes(time, dayVal, null), exp);

    dict.add("start", HTime.make(8, 0, 0));
    dict.add("end", HTime.make(17, 30, 00));
    dict.add("val", HNum.make(21.5d));
    dict.add("dates", "N");
    dict.add("weekdays", dayVal);
    exp = dict.toDict();
    time.setStart(BTime.make(8, 0, 0));
    time.setFinish(BTime.make(17, 30, 00));
    time.setEffectiveValue(new BStatusNumeric(21.5d));
    assertEquals(PointIO.processTimes(time, dayVal, null), exp);
  }

  @Test
  public void testProcessStatusValue()
  {
    // null input
    assertEquals(PointIO.processStatusValue(null), HNA.VAL);

    // boolean
    BStatusBoolean boolVal = new BStatusBoolean(false);
    assertEquals(PointIO.processStatusValue(boolVal), HBool.FALSE);
    boolVal = new BStatusBoolean(true);
    assertEquals(PointIO.processStatusValue(boolVal), HBool.TRUE);

    // string
    BStatusString strVal = new BStatusString("hello");
    assertEquals(PointIO.processStatusValue(strVal), HStr.make("hello"));
    strVal = new BStatusString("world");
    assertEquals(PointIO.processStatusValue(strVal), HStr.make("world"));

    // numeric
    BStatusNumeric numVal = new BStatusNumeric(10d);
    assertEquals(PointIO.processStatusValue(numVal), HNum.make(10d));
    BNumericWritable pointVal = new BNumericWritable();
    pointVal.setFacets(BFacets.makeNumeric(BUnit.getUnit("celsius"), 1));
    pointVal.setOut(new BStatusNumeric(35.1d));
    assertEquals(PointIO.processStatusValue(pointVal.getOut()), HNum.make(35.1d, "celsius"));
    pointVal = new BNumericWritable();
    pointVal.setFacets(BFacets.make("test", 1));
    pointVal.setOut(new BStatusNumeric(35.1d));
    assertEquals(PointIO.processStatusValue(pointVal.getOut()), HNum.make(35.1d));
  }

  private void verifyPointArrayStatus(BControlPoint p, BPriorityLevel skip, BStatus st)
  {
    if (p instanceof BNumericWritable)
    {
      verifyNWPointArrayStatus((BNumericWritable) p, skip, st);
    }
    else if (p instanceof BBooleanWritable)
    {
      verifyBWPointArrayStatus((BBooleanWritable) p, skip, st);
    }
    else if (p instanceof BEnumWritable)
    {
      verifyEWPointArrayStatus((BEnumWritable) p, skip, st);
    }
    else if (p instanceof BStringWritable)
    {
      verifySWPointArrayStatus((BStringWritable) p, skip, st);
    }
  }

  /**
   * Verify that all levels in the priority array, except a specified one to skip, have
   * the specified status
   *
   * @param nw   the control point to check
   * @param skip the priority array level to skip
   * @param st   the status to check for
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
      assertEquals(sn.getStatus(), st);
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
      assertEquals(sb.getStatus(), st);
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
      assertEquals(se.getStatus(), st);
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
      assertEquals(ss.getStatus(), st);
    }
  }
}
