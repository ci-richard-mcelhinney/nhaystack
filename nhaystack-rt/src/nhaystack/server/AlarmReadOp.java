package nhaystack.server;

import org.projecthaystack.*;
import org.projecthaystack.server.*;

import javax.baja.alarm.*;
import javax.baja.alarm.ext.BAlarmSourceExt;
import javax.baja.control.*;
import javax.baja.naming.BOrdList;
import javax.baja.sys.*;
import java.util.*;

public class AlarmReadOp extends HOp
{
  @Override
  public String name()
  {
    return "alarmRead";
  }

  @Override
  public String summary()
  {
    return "Alarm Read";
  }

  @Override
  public HGrid onService(HServer db, HGrid req) throws Exception
  {
    NHServer server = (NHServer) db;
    Cache cache = server.getCache();
    TagManager tagMgr = server.getTagManager();
    if (!cache.initialized())
    {
      throw new IllegalStateException(Cache.NOT_INITIALIZED);
    }

    BAlarmService alarmService = (BAlarmService) Sys.getService(BAlarmService.TYPE);
    try (AlarmDbConnection conn = alarmService.getAlarmDb().getDbConnection(null))
    {
      // find the point in the station and check for any alarm extensions
      HDict filter = req.row(0);
      BControlPoint point = (BControlPoint) tagMgr.lookupComponent(filter.id());
      Optional<BAlarmSourceExt> optExt = findAlarmExt(point);
      if (!optExt.isPresent())
      {
        return HGrid.EMPTY;
      }

      // get the open alarms for the found alarm extension
      BOrdList list = BOrdList.make(optExt.get().getAbsoluteOrd());
      Cursor<BAlarmRecord> alarmCursor = conn.getOpenAlarmsForSource(list);

      HGridBuilder gridBuilder = new HGridBuilder();
      ArrayList<HDict> a = new ArrayList<>();

      // process the alarms
      while (alarmCursor.next())
      {
        BAlarmRecord alarmRec = alarmCursor.get();
        a.add(createAlarmRecordDict(alarmRec));
      }

      // do a array type conversion!
      HDict[] dicts = new HDict[a.size()];
      System.arraycopy(a.toArray(), 0, dicts, 0, a.size());

      return HGridBuilder.dictsToGrid(dicts);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static HDict createAlarmRecordDict(BAlarmRecord alarmRec) throws Exception
  {
    HDictBuilder dictBuilder = new HDictBuilder();

    dictBuilder.add("uuid", HRef.make(alarmRec.getUuid().encodeToString()));
    dictBuilder.add("ackState", HStr.make(alarmRec.getAckState().getTag()));
    dictBuilder.add("alarmClass", HStr.make(alarmRec.getAlarmClass()));
    dictBuilder.add("ts", HDateTime.make(alarmRec.getTimestamp().getMillis()));
    dictBuilder.add("normalTime", HDateTime.make(alarmRec.getNormalTime().getMillis()));
    dictBuilder.add("ackTime", HDateTime.make(alarmRec.getAckTime().getMillis()));
    dictBuilder.add("lastUpdate", HDateTime.make(alarmRec.getLastUpdate().getMillis()));

    return dictBuilder.toDict();
  }

  public static Optional<BAlarmSourceExt> findAlarmExt(BControlPoint point)
  {
    BPointExtension[] exts = point.getExtensions();

    // find the first alarm extension
    // for now just handle 1 alarm extension per point
    // TODO support more alarm extensions per point
    BPointExtension alarmExt = null;
    for (BPointExtension ext : exts)
    {
      if (ext.getType() == BAlarmSourceExt.TYPE)
      {
        return Optional.of((BAlarmSourceExt) ext);
      }
    }

    return Optional.empty();
  }
}
