//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   26 Feb 2015  Mike Jarmy  Creation

package nhaystack.driver.alarm;

import java.util.*;

import javax.baja.alarm.*;
import javax.baja.sys.*;
import javax.baja.util.*;

public class BNHaystackAlarmRecipient
    extends BAlarmRecipient
{
    /*-
    class BNHaystackAlarmRecipient
    {
    }
   -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.alarm.BNHaystackAlarmRecipient(116051387)1.0$ @*/
/* Generated Thu Feb 26 10:14:38 EST 2015 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackAlarmRecipient.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void handleAlarm(BAlarmRecord alarmRecord)
    {
        System.out.println("handleAlarm: " + alarmRecord);
    }
}
