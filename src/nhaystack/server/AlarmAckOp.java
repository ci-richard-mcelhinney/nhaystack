//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   12 May 2015  Mike Jarmy  Creation
//
package nhaystack.server;

import javax.baja.alarm.*;
import javax.baja.log.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.server.*;

class AlarmAckOp extends HOp
{
    public String name() { return "alarmAck"; }
    public String summary() { return "Alarm Ack"; }
    public HGrid onService(HServer db, HGrid req)
    {
System.out.println("--------------------------------------------------------------");
System.out.println("alarmAck");
System.out.println();
req.dump();

        NHServer server = (NHServer) db;
        if (!server.getCache().initialized()) 
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        try
        {
            HRow params = req.row(0);
            BUuid alarmUuid = BUuid.make(params.getStr("alarmUuid"));

            if (LOG.isTraceOn()) 
                LOG.trace(name() + " alarmUuid:" + alarmUuid);

            BAlarmService alarmService = (BAlarmService) Sys.getService(BAlarmService.TYPE);
            BAlarmRecord alarm = alarmService.getAlarmDb().getRecord(alarmUuid);

            if (alarm == null)
            {
                LOG.warning(name() + " cannot find alarm with uuid " + alarmUuid);
            }
            else
            {
                if (LOG.isTraceOn()) 
                    LOG.trace(name() + " acking " + alarm);

                alarm.ackAlarm();
                alarmService.ackAlarm(alarm); 
            }

            return HGrid.EMPTY;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");
}
 
