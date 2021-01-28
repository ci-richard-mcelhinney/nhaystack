//
// Copyright (c) 2021, Project Haystack Corporation
// Licensed under the Academic Free License version 3.0
//
// History:
//   28 Jan 2021  Richard McElhinney  Creation
//

package nhaystack.server;

import org.testng.annotations.Test;

import javax.baja.alarm.ext.BAlarmSourceExt;
import javax.baja.control.*;
import javax.baja.control.ext.BDiscreteTotalizerExt;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;
import javax.baja.test.BTestNg;
import java.util.Optional;

import static org.testng.Assert.*;

@NiagaraType
@Test
public class BAlarmReadOpTest extends BTestNg
{
  @Override
  public Type getType()
  {
    return TYPE;
  }

  public static final Type TYPE = Sys.loadType(BAlarmReadOpTest.class);


  @Test
  public void testFindAlarmExt()
  {
    assertTrue(true);

    BNumericWritable numeric = new BNumericWritable();
    numeric.add("outOfRangeAlarmExt", new BAlarmSourceExt());
    Optional<BAlarmSourceExt> res = AlarmReadOp.findAlarmExt(numeric);
    assertTrue(res.isPresent());
    assertEquals(res.get().getType(), BAlarmSourceExt.TYPE);

    numeric = new BNumericWritable();
    res = AlarmReadOp.findAlarmExt(numeric);
    assertFalse(res.isPresent());

    BBooleanWritable booleanWritable = new BBooleanWritable();
    booleanWritable.add("totalizer", new BDiscreteTotalizerExt());
    res = AlarmReadOp.findAlarmExt(booleanWritable);
    assertFalse(res.isPresent());

    booleanWritable.add("outOfRangeAlarmExt", new BAlarmSourceExt());
    res = AlarmReadOp.findAlarmExt(booleanWritable);
    assertTrue(res.isPresent());
  }
}
