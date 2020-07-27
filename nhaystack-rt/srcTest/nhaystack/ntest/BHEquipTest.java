//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   11 Apr 2019  Eric Anderson  Creation
//
package nhaystack.ntest;

import static nhaystack.util.NHaystackConst.ID_EQUIP;
import static org.testng.Assert.assertTrue;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.test.BTestNg;

import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.site.BHEquip;
import org.testng.annotations.Test;
import com.tridium.testng.BStationTestBase;

@Test
@NiagaraType
public class BHEquipTest extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BHEquipTest(2979906276)1.0$ @*/
/* Generated Thu Apr 11 14:04:24 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHEquipTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void bhEquipHasNiagaraEquipTag()
    {
        BStation station = stationHandler.getStation();
        BHEquip equip = new BHEquip();
        station.add("equip", equip);
        assertTrue(equip.tags().contains(ID_EQUIP));
    }
}
