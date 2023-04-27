//
// Copyright (c) 2021, Project Haystack Corporation
// Licensed under the Academic Free License version 3.0
//
// History:
//   28 Jan 2021  Richard McElhinney  Creation
//

package nhaystack.server;

import org.projecthaystack.*;
import org.projecthaystack.server.*;

import javax.baja.alarm.*;
import javax.baja.alarm.ext.BAlarmSourceExt;
import javax.baja.control.*;
import javax.baja.naming.BOrdList;
import javax.baja.sys.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Implements a Haystack REST API Op for reading alarms generated
 * by a specific control point
 *
 * @author Richard McElhinney
 */
public class AlarmReadOp extends HOp
{
  public AlarmReadOp()
  {
  }

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
      HDict filter = req.row(0);
      if (!filter.has("id"))
      {
        return HGrid.EMPTY;
      }

      // find the point in the station and check for any alarm extensions
      BControlPoint point = (BControlPoint) tagMgr.lookupComponent(filter.id());
      log.fine("Found point by id " + filter.id().toString());
      Optional<BAlarmSourceExt> optExt = findAlarmExt(point);
      if (!optExt.isPresent())
      {
        log.fine("Point " + point.getSlotPath() + " has no alarm extension");
        return HGrid.EMPTY;
      }

      // get the open alarms for the found alarm extension
      log.fine("Using source ord: " + optExt.get().getNavOrd() + " for alarm source list");
      BOrdList list = BOrdList.make(optExt.get().getNavOrd());
      Cursor<BAlarmRecord> alarmCursor = conn.getOpenAlarmsForSource(list);

      // process the alarms
      ArrayList<HDict> a = new ArrayList<>();
      log.fine("Start processing alarm records");
      while (alarmCursor.next())
      {
        BAlarmRecord alarmRec = alarmCursor.get();
        a.add(createAlarmRecordDict(alarmRec));
      }
      log.fine("Processed open alarms search, found " + a.size() + " records");

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

  /**
   * Helper function to create an HDict from a BAlarmRecord
   *
   * @param alarmRec a BAlarmRecord to translate into HDict
   * @return a HDict containing the given tags for the alarm record
   * @throws Exception if a problem retrieving the BAlarmRecord Uuid as a String
   */
  public static HDict createAlarmRecordDict(BAlarmRecord alarmRec) throws Exception
  {
    HDictBuilder dictBuilder = new HDictBuilder();

    dictBuilder.add("uuid", HStr.make(alarmRec.getUuid().encodeToString()));
    dictBuilder.add("ackState", HStr.make(alarmRec.getAckState().getTag()));
    dictBuilder.add("alarmClass", HStr.make(alarmRec.getAlarmClass()));
    dictBuilder.add("ts", HDateTime.make(alarmRec.getTimestamp().getMillis()));
    dictBuilder.add("normalTime", HDateTime.make(alarmRec.getNormalTime().getMillis()));
    dictBuilder.add("ackTime", HDateTime.make(alarmRec.getAckTime().getMillis()));
    dictBuilder.add("lastUpdate", HDateTime.make(alarmRec.getLastUpdate().getMillis()));

    return dictBuilder.toDict();
  }

  /**
   * Helper function to retrieve the first BAlarmSourceExt from a BControlPoint
   *
   * @param point the BControlPoint to search for a BAlarmSourceExt
   * @return an Optional of BAlarmSourceExt
   */
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

  private Logger log = Logger.getLogger("nhaystack.alarm");
}
