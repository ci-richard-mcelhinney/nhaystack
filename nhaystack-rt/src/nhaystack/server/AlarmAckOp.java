//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   12 May 2015  Mike Jarmy  Creation
//
package nhaystack.server;

import nhaystack.driver.alarm.BNHaystackAlarmRecipient;
import org.projecthaystack.*;
import org.projecthaystack.server.*;

import javax.baja.alarm.*;
import javax.baja.sys.Sys;
import javax.baja.util.BUuid;
import java.util.logging.*;

class AlarmAckOp extends HOp
{
  @Override
  public String name()
  {
    return "alarmAck";
  }

  @Override
  public String summary()
  {
    return "Alarm Ack";
  }

  @Override
  public HGrid onService(HServer db, HGrid req)
  {
    NHServer server = (NHServer) db;
      if (!server.getCache().initialized())
      {
          throw new IllegalStateException(Cache.NOT_INITIALIZED);
      }

    try
    {
      HRow params = req.row(0);
      BUuid uuid = BUuid.make(params.getStr("alarmUuid"));

        if (LOG.isLoggable(Level.FINE))
        {
            LOG.fine(name() + " alarmUuid:" + uuid);
        }

      BAlarmService alarmService = (BAlarmService) Sys.getService(BAlarmService.TYPE);
      try (AlarmDbConnection conn = alarmService.getAlarmDb().getDbConnection(null))
      {
        BAlarmRecord alarm = conn.getRecord(uuid);

        if (alarm == null)
        {
          LOG.warning(name() + " cannot find alarm with uuid " + uuid);
        }
        else
        {
            if (LOG.isLoggable(Level.FINE))
            {
                LOG.fine(name() + " acking " + alarm);
            }

          // We have to ignore the alarm while we are acking it
          // so we don't get caught in a loop.
          BAlarmService service = (BAlarmService) Sys.getService(BAlarmService.TYPE);
          BNHaystackAlarmRecipient[] recips = service.getChildren(BNHaystackAlarmRecipient.class);
          for (BNHaystackAlarmRecipient recip : recips)
            recip.beginIgnore(uuid);

          // ack the alarm
          alarm.ackAlarm();

          // Route the ack (we will end up ignoring this when it
          // gets routed to BNHaystackAlarmRecipient instances).
          alarmService.ackAlarm(alarm);
        }

        return HGrid.EMPTY;
      }
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

  private static final Logger LOG = Logger.getLogger("nhaystack.alarm");
}
 
