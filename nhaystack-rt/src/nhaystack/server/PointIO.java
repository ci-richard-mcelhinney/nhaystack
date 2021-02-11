//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Feb 2015  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Added missing @Overrides annotations, added use of generics
//
package nhaystack.server;

import nhaystack.*;
import nhaystack.res.Resources;
import nhaystack.util.*;
import org.projecthaystack.*;

import javax.baja.control.*;
import javax.baja.control.enums.BPriorityLevel;
import javax.baja.fox.BFoxProxySession;
import javax.baja.naming.BOrd;
import javax.baja.schedule.*;
import javax.baja.security.PermissionException;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.timezone.BTimeZone;
import javax.baja.units.BUnit;
import java.util.*;
import java.util.logging.*;

/**
 * PointIO handles onPointWriteArray() and onPointWrite()
 */
public class PointIO
{
  PointIO(BNHaystackService service, Cache cache, TagManager tagMgr, ScheduleManager schedMgr, FoxSessionManager foxSessionMgr)
  {
    this.service = service;
    this.cache = cache;
    this.tagMgr = tagMgr;
    this.schedMgr = schedMgr;
    this.foxSessionMgr = foxSessionMgr;
  }

  /**
   * Implementation hook for pointWriteArray
   */
  HGrid onPointWriteArray(HDict rec)
  {
    if (!cache.initialized())
    {
      throw new IllegalStateException(Cache.NOT_INITIALIZED);
    }

    if (LOG.isLoggable(Level.FINE))
    {
      LOG.fine("onPointWriteArray " + rec.id());
    }


    BComponent comp = tagMgr.lookupComponent(rec.id());

    if (comp instanceof BControlPoint)
    {
      return doControlPointArray((BControlPoint) comp);
    }

    else if (comp instanceof BWeeklySchedule)
    {
      return doScheduleArray((BWeeklySchedule) comp);
    }

    else
    {
      throw new BajaRuntimeException("pointWriteArray() failed for " + comp.getSlotPath());
    }
  }

  /**
   * Implementation hook for pointWrite
   */
  void onPointWrite(HDict rec, int level, HVal val, String who, HNum dur, // ignore this for now
                    HDict opts)
  {
    try
    {
      if (!cache.initialized())
      {
        throw new IllegalStateException(Cache.NOT_INITIALIZED);
      }

      if (LOG.isLoggable(Level.FINE))
      {
        LOG.fine("onPointWrite " + "id:" + rec.id() + ", " + "level:" + level + ", " + "val:" + val + ", " + "who:" + who + ", " + "dur:" + dur + ", " + "opts:" + (opts == null ? "null" : opts.toZinc()));
      }

      HHisItem[] schedItems = schedMgr.getOptionsSchedule(opts);
      if (schedItems == null)
      {
        BComponent comp = tagMgr.lookupComponent(rec.id());
        if (comp == null)
        {
          throw new BajaRuntimeException("Cannot find component for " + rec.id());
        }

        if (comp instanceof BControlPoint)
        {
          BControlPoint point = (BControlPoint) comp;
          onControlPointWrite(point, rec, level, val, who, dur);
        }
        else if (comp instanceof BWeeklySchedule)
        {
          LOG.fine("ignoring write to " + comp.getSlotPath());
        }
        else
        {
          throw new BajaRuntimeException("Cannot write to " + comp.getSlotPath());
        }
      }
      else
      {
        schedMgr.onScheduleWrite(rec, schedItems);
      }
    }
    catch (RuntimeException e)
    {
      e.printStackTrace();
      throw e;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

  /**
   * return point array for BControlPoint
   */
  private HGrid doControlPointArray(BControlPoint point)
  {
    try
    {
      HVal[] vals = new HVal[17];
      BControlPoint working;
      BControlPoint remote;

      // check to see if point is remote
      RemotePoint rp = RemotePoint.fromControlPoint(point);
      if (rp == null)
      {
        LOG.finest(point.getSlotPath() + " is not remote, assuming point is local to this station");
        working = point;
      }
      else
      {
        // look up fox session
        long millis = service.getFoxLeaseInterval().getMillis();
        BFoxProxySession session = foxSessionMgr.getSession(RemotePoint.findParentDevice(point), millis);

        // resolve remote point
        remote = (BControlPoint) BOrd.make("station:|" + rp.getSlotPath()).get(session);
        remote.lease(1, millis);
        if (!(remote instanceof BIWritablePoint))
        {
          LOG.severe("cannot write to " + remote.getSlotPath() + ", it is not writable.");
          throw new IllegalStateException("point is not writable: " + remote.getSlotPath());
        }
        working = remote;
      }

      // Numeric
      if (working instanceof BNumericWritable)
      {
        BNumericWritable nw = (BNumericWritable) working;
        for (int i = 0; i < 16; i++)
        {
          BStatusNumeric sn = (BStatusNumeric) nw.get("in" + (i + 1));
          if (!sn.getStatus().isNull())
          {
            vals[i] = HNum.make(sn.getValue());
          }
        }
        BStatusNumeric sn = nw.getFallback();
        if (!sn.getStatus().isNull())
        {
          vals[16] = HNum.make(sn.getValue());
        }
      }
      // Boolean
      else if (working instanceof BBooleanWritable)
      {
        BBooleanWritable bw = (BBooleanWritable) working;
        for (int i = 0; i < 16; i++)
        {
          BStatusBoolean sb = (BStatusBoolean) bw.get("in" + (i + 1));
          if (!sb.getStatus().isNull())
          {
            vals[i] = HBool.make(sb.getValue());
          }
        }
        BStatusBoolean sb = bw.getFallback();
        if (!sb.getStatus().isNull())
        {
          vals[16] = HBool.make(sb.getValue());
        }
      }
      // Enum
      else if (working instanceof BEnumWritable)
      {
        BEnumWritable ew = (BEnumWritable) working;
        for (int i = 0; i < 16; i++)
        {
          BStatusEnum se = (BStatusEnum) ew.get("in" + (i + 1));
          if (!se.getStatus().isNull())
          {
            vals[i] = HStr.make(SlotUtil.fromEnum(se.getValue().getTag(), service.getTranslateEnums()));
          }
        }
        BStatusEnum se = ew.getFallback();
        if (!se.getStatus().isNull())
        {
          vals[16] = HStr.make(SlotUtil.fromEnum(se.getValue().getTag(), service.getTranslateEnums()));
        }
      }
      // String
      else if (working instanceof BStringWritable)
      {
        BStringWritable sw = (BStringWritable) working;
        for (int i = 0; i < 16; i++)
        {
          BStatusString s = (BStatusString) sw.get("in" + (i + 1));
          if (!s.getStatus().isNull())
          {
            vals[i] = HStr.make(s.getValue());
          }
        }
        BStatusString s = sw.getFallback();
        if (!s.getStatus().isNull())
        {
          vals[16] = HStr.make(s.getValue());
        }
      }
      else
      {
        throw new IllegalStateException("unknown type: " + working.getType());
      }

      //////////////////////////////////////////////

      // Return priority array for writable point identified by id.
      // The grid contains 17 rows with following columns:
      //   - level: number from 1 - 17 (17 is default)
      //   - levelDis: human description of level
      //   - val: current value at level or null
      //   - who: who last controlled the value at this level

      String[] who = getLinkWho(working);
      HDict[] result = new HDict[17];
      for (int i = 0; i < 17; i++)
      {
        HDictBuilder hd = new HDictBuilder();
        HNum level = HNum.make(i + 1);
        hd.add("level", level);
        hd.add("levelDis", "level " + (i + 1)); // TODO?
        if (vals[i] != null)
        {
          hd.add("val", vals[i]);
        }

        if (!who[i].isEmpty())
        {
          hd.add("who", who[i]);
        }

        result[i] = hd.toDict();
      }
      return HGridBuilder.dictsToGrid(result);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /**
   * return point array for BWeeklySchedule
   */
  private static HGrid doScheduleArray(BWeeklySchedule sched)
  {
    Logger log = BNHaystackService.SCHEDULE_LOG;
    BCompositeSchedule compositeSchedule = sched.getSchedule();
    BWeekSchedule week = (BWeekSchedule) compositeSchedule.get("week");
    BDailySchedule[] days = (BDailySchedule[]) week.getChildren(BDailySchedule.class);
    log.fine("Found " + days.length + " in schedule " + sched.getName() + " to process");

    // timezone
    BTimeZone stTz = BTimeZone.getLocal();
    String tz = stTz.getId();

    ArrayList<HDict> items = new ArrayList<>();
    for (BDailySchedule day : days)
    {
      log.fine("Processing " + day.getName());

      BDaySchedule dailyTimes = (BDaySchedule) day.get("day");
      BTimeSchedule[] times = dailyTimes.getTimesInOrder();
      log.fine("Found " + times.length + " to process");

      HDictBuilder dict = new HDictBuilder();
      BWeekdaySchedule applicability = (BWeekdaySchedule) day.get("days");
      HStr dayVal = HStr.make(String.valueOf(applicability.getSet().getOrdinals()[0]));
      for (BTimeSchedule time : times)
        items.add(processTimes(time, dayVal, log));

    }

    HDict[] dicts = new HDict[items.size()];
    for (int i = 0; i < items.size(); i++)
      dicts[i] = items.get(i);

    HGrid schGrid = HGridBuilder.dictsToGrid(dicts);

    HDictBuilder sch = new HDictBuilder();
    sch.add("enum", processFacets(sched));
    sch.add("tz", HStr.make(tz));
    sch.add("schedule");
    sch.add("scheduleGrid", schGrid);
    sch.add("dis", sched.getDisplayName(null));

    return HGridBuilder.dictToGrid(sch.toDict());
  }

  public static HDict processTimes(BTimeSchedule time, HStr dayVal, Logger log)
  {
    if (time == null)
    {
      return HDict.EMPTY;
    }

    if (dayVal == null)
    {
      return HDict.EMPTY;
    }

    if (time.getStart().isNull() || time.getFinish().isNull())
    {
      return HDict.EMPTY;
    }

    if (time.getFinish().isBefore(time.getStart()))
    {
      return HDict.EMPTY;
    }

    HDictBuilder dict = new HDictBuilder();

    if (log != null)
    {
      log.fine("Processing " + time.toDebugString());
    }

    HTime start = HTime.make(time.getStart().getHour(), time.getStart().getMinute(), time.getStart().getSecond());
    HTime end = HTime.make(time.getFinish().getHour(), time.getFinish().getMinute(), time.getFinish().getSecond());
    HVal val = processStatusValue(time.getEffectiveValue());

    dict.add("start", start);
    dict.add("end", end);
    dict.add("val", val);
    dict.add("dates", "N");
    dict.add("weekdays", dayVal);

    return dict.toDict();
  }

  /**
   * Converts a BStatusValue to a HVal
   *
   * @param value the BStatusValue to convert
   * @return a HVal based on the type of BStatusValue given, if the given
   * BStatusValue is not valid or it is null then a not available
   * type will be returned
   */
  public static HVal processStatusValue(BStatusValue value)
  {
    HVal val = HNA.VAL;

    if (value == null)
    {
      return val;
    }

    if (value.getType() == BStatusBoolean.TYPE)
    {
      val = HBool.make(((BStatusBoolean) value).getBoolean());
    }
    else if (value.getType() == BStatusNumeric.TYPE)
    {
      if (value.getStatusValueFacets().isNull())
      {
        val = HNum.make(((BStatusNumeric) value).getNumeric());
      }
      else
      {
        BFacets f = value.getStatusValueFacets();
        if (f.get("units") != null)
        {
          BUnit u = (BUnit) f.get("units");
          val = HNum.make(((BStatusNumeric) value).getNumeric(), Resources.fromBajaUnit(u).name);
        }
        else
        {
          val = HNum.make(((BStatusNumeric) value).getNumeric());
        }
      }
    }
    else if (value.getType() == BStatusString.TYPE)
    {
      val = HStr.make(((BStatusString) value).getValue());
    }

    return val;
  }

  public static HVal processFacets(BWeeklySchedule w)
  {
    HVal facets = null;
    BFacets f = w.getFacets();

    if (w.getType() == BBooleanSchedule.TYPE)
    {
      String enumDef = f.get("falseText") + "," + f.get("trueText");
      facets = HStr.make(enumDef);
    }
    else if (w.getType() == BNumericSchedule.TYPE)
    {
      HDictBuilder numFacets = new HDictBuilder();
      numFacets.add("unit", HStr.make(f.gets("units", "")));
      numFacets.add("minVal", HNum.make(f.getf("min", Float.MIN_VALUE)));
      numFacets.add("maxVal", HNum.make(f.getf("max", Float.MAX_VALUE)));
      numFacets.add("precision", HNum.make(f.geti("precision", 1)));

      facets = numFacets.toDict();
    }
    else
    {
      facets = HStr.make("");
    }

    return facets;
  }

  /**
   * get the source for each link that is connected to [in1..in16, fallback]
   */
  private static String[] getLinkWho(BControlPoint point)
  {
    String[] who = new String[17];
    for (int i = 0; i < 17; i++)
      who[i] = "";

    BLink[] links = point.getLinks();
    for (BLink link : links)
    {
      String target = link.getTargetSlot().getName();
      Integer level = POINT_PROP_LEVELS.get(target);
      if (level != null)
      {
        who[level.intValue() - 1] += link.getSourceComponent().getSlotPath() + "/" + link.getSourceSlot().getName();
      }
    }

    return who;
  }

  private static final Map<String, Integer> POINT_PROP_LEVELS = new HashMap<>();

  static
  {
    POINT_PROP_LEVELS.put("in1", Integer.valueOf(1));
    POINT_PROP_LEVELS.put("in2", Integer.valueOf(2));
    POINT_PROP_LEVELS.put("in3", Integer.valueOf(3));
    POINT_PROP_LEVELS.put("in4", Integer.valueOf(4));
    POINT_PROP_LEVELS.put("in5", Integer.valueOf(5));
    POINT_PROP_LEVELS.put("in6", Integer.valueOf(6));
    POINT_PROP_LEVELS.put("in7", Integer.valueOf(7));
    POINT_PROP_LEVELS.put("in8", Integer.valueOf(8));
    POINT_PROP_LEVELS.put("in9", Integer.valueOf(9));
    POINT_PROP_LEVELS.put("in10", Integer.valueOf(10));
    POINT_PROP_LEVELS.put("in11", Integer.valueOf(11));
    POINT_PROP_LEVELS.put("in12", Integer.valueOf(12));
    POINT_PROP_LEVELS.put("in13", Integer.valueOf(13));
    POINT_PROP_LEVELS.put("in14", Integer.valueOf(14));
    POINT_PROP_LEVELS.put("in15", Integer.valueOf(15));
    POINT_PROP_LEVELS.put("in16", Integer.valueOf(16));
    POINT_PROP_LEVELS.put("fallback", Integer.valueOf(17));
  }

  /**
   * save the last point write to a BHGrid slot.
   */
  private static void saveLastWrite(BControlPoint point, int level, String who)
  {
    BHGrid oldGrid = (BHGrid) point.get(LAST_WRITE);

    if (oldGrid == null)
    {
      HGrid grid = saveLastWriteToGrid(HGrid.EMPTY, level, who);
      point.add(LAST_WRITE, BHGrid.make(grid), Flags.SUMMARY | Flags.READONLY);
    }
    else
    {
      HGrid grid = saveLastWriteToGrid(oldGrid.getGrid(), level, who);
      point.set(LAST_WRITE, BHGrid.make(grid));
    }
  }

  private static HGrid saveLastWriteToGrid(HGrid grid, int level, String who)
  {
    // store rows by level
    Map<HVal, HDict> map = new HashMap<>();
    for (int i = 0; i < grid.numRows(); i++)
    {
      HDict row = grid.row(i);
      map.put(row.get("level"), row);
    }

    // create or replace new row
    HNum hlevel = HNum.make(level);
    HDictBuilder db = new HDictBuilder();
    db.add("level", hlevel);
    db.add("who", HStr.make(who));
    map.put(hlevel, db.toDict());

    // create new grid
    HDict[] dicts = new HDict[map.size()];
    int n = 0;
    for (HDict hDict : map.values())
      dicts[n++] = hDict;

    return HGridBuilder.dictsToGrid(dicts);
  }

  private void onControlPointWrite(BControlPoint point, HDict rec, int level, HVal val, String who, HNum dur) // ignore this for now
          throws Exception
  {
    // check permissions on this Thread's saved context
    Context cx = ThreadContext.getContext(Thread.currentThread());
    if (!TypeUtil.canWrite(point, cx))
    {
      throw new PermissionException("Cannot write to " + rec.id());
    }

    // if its writable, just go ahead and do the write
    if (point instanceof BIWritablePoint)
    {
      onControlPointWriteLevel(point, /*point.getFacets(),*/ level, val, who);
    }

    // else do a remote write
    else
    {
      onControlPointWriteRemote(point, level, val, who);
    }

    // done
    saveLastWrite(point, level, who);
  }

  /**
   * onControlPointWriteRemote
   */
  private void onControlPointWriteRemote(BControlPoint point, int level, HVal val, String who) throws Exception
  {
    // make sure there is a writable table
    HDict tags = BHDict.findTagAnnotation(point);
    if (!tags.has("writable"))
    {
      LOG.severe("cannot write to " + point.getSlotPath() + ", does not have 'writable' tag.");
      return;
    }

    // look up remote point
    RemotePoint rp = RemotePoint.fromControlPoint(point);
    if (rp == null)
    {
      LOG.severe("cannot write to " + point.getSlotPath() + ", it is neither writable nor remote.");
      return;
    }

    // look up fox session
    long millis = service.getFoxLeaseInterval().getMillis();
    BFoxProxySession session = foxSessionMgr.getSession(RemotePoint.findParentDevice(point), millis);

    // resolve remote point
    BControlPoint remote = (BControlPoint) BOrd.make("station:|" + rp.getSlotPath()).get(session);
    remote.lease(1, millis);
    if (!(remote instanceof BIWritablePoint))
    {
      LOG.severe("cannot write to " + remote.getSlotPath() + ", it is not writable.");
      return;
    }

    // done
    onControlPointWriteLevel(remote, /*null,*/ level, val, who);
  }

  /**
   * onControlPointWriteLevel
   */
  private static void onControlPointWriteLevel(BControlPoint point, /*BFacets facets,*/
                                               int level, HVal val, String who)
  {
    BPriorityLevel plevel = matchLevel(level);

    if (plevel == BPriorityLevel.none)
    {
      LOG.severe("invalid priority level received " + level + " for " + point.getSlotPath().toDisplayString());
      return;
    }

    if (point instanceof BNumericWritable)
    {
      writeNW(point, plevel, val);
    }
    else if (point instanceof BBooleanWritable)
    {
      writeBW(point, plevel, val);
    }
    else if (point instanceof BEnumWritable)
    {
      writeEW(point, plevel, val);
    }
    else if (point instanceof BStringWritable)
    {
      writeSW(point, plevel, val);
    }
    else
    {
      LOG.severe("cannot write to " + point.getSlotPath() + ", unknown point type " + point.getClass());
    }
  }

  /**
   * Find the appropriate Niagara BControlPoint level in a priority
   * array based on an integer between 1 - 17.  In Niagara, level 17
   * is the fallback slot however Haystack doesn't recognise the
   * enumeration so it needs to be translated.
   *
   * @param level valid value is from 1 - 17
   * @return the corresponding priority level or none
   */
  public static BPriorityLevel matchLevel(int level)
  {
    BPriorityLevel plevel = BPriorityLevel.make(BPriorityLevel.NONE);

    if (level == FALLBACK_LEVEL)
    {
      plevel = BPriorityLevel.make(BPriorityLevel.FALLBACK);
    }
    else if (level >= 1 && level <= 16)
    {
      plevel = BPriorityLevel.make(level);
    }

    return plevel;
  }

  /**
   * Handle the actual write to a numeric writable point
   *
   * @param p   the control point being written to
   * @param l   the priority level to write to, including the fallback level
   * @param val the value to write, including null
   */
  public static void writeNW(BControlPoint p, BPriorityLevel l, HVal val)
  {
    BNumericWritable nw = (BNumericWritable) p;
    BStatusNumeric sn = null;

    if (l == BPriorityLevel.fallback)
    {
      sn = (BStatusNumeric) nw.getFallback().newCopy();
    }
    else
    {
      sn = (BStatusNumeric) nw.getLevel(l).newCopy();
    }

    if (val == null)
    {
      sn.setStatus(BStatus.nullStatus);
    }
    else
    {
      HNum num = (HNum) val;
      sn.setValue(num.val);
      sn.setStatus(BStatus.ok);
    }

    if (l == BPriorityLevel.fallback)
    {
      nw.setFallback(sn);
    }
    else
    {
      nw.set("in" + l.getOrdinal(), sn);
    }

  }

  /**
   * Handle the actual write to a boolean writable point
   *
   * @param p   the control point being written to
   * @param l   the priority level to write to, including the fallback level
   * @param val the value to write, including null
   */
  public static void writeBW(BControlPoint p, BPriorityLevel l, HVal val)
  {
    BBooleanWritable bw = (BBooleanWritable) p;
    BStatusBoolean sb = null;

    if (l == BPriorityLevel.fallback)
    {
      sb = (BStatusBoolean) bw.getFallback().newCopy();
    }
    else
    {
      sb = (BStatusBoolean) bw.getLevel(l).newCopy();
    }

    if (val == null)
    {
      sb.setStatus(BStatus.nullStatus);
    }
    else
    {
      HBool bool = (HBool) val;
      sb.setValue(bool.val);
      sb.setStatus(BStatus.ok);
    }

    if (l == BPriorityLevel.fallback)
    {
      bw.setFallback(sb);
    }
    else
    {
      bw.set("in" + l.getOrdinal(), sb);
    }
  }

  /**
   * Handle the actual write to an enum writable point
   *
   * @param p   the control point being written to
   * @param l   the priority level to write to, including the fallback level
   * @param val the value to write, including null
   */
  public static void writeEW(BControlPoint p, BPriorityLevel l, HVal val)
  {
    BEnumWritable ew = (BEnumWritable) p;
    BStatusEnum se = null;

    if (l == BPriorityLevel.fallback)
    {
      se = (BStatusEnum) ew.getFallback().newCopy();
    }
    else
    {
      se = (BStatusEnum) ew.getLevel(l).newCopy();
    }

    if (val == null)
    {
      se.setStatus(BStatus.nullStatus);
    }
    else
    {
      String str = SlotUtil.toNiagara(((HStr) val).val);
      BFacets facets = ew.getFacets();
      BEnumRange range = (BEnumRange) facets.get(BFacets.RANGE);
      BEnum enm = range.get(str);
      se.setValue(enm);
      se.setStatus(BStatus.ok);
    }

    if (l == BPriorityLevel.fallback)
    {
      ew.setFallback(se);
    }
    else
    {
      ew.set("in" + l.getOrdinal(), se);
    }
  }

  /**
   * Handle the actual write to a string writable point
   *
   * @param p   the control point being written to
   * @param l   the priority level to write to including the fallback level
   * @param val the value to write, including null
   */
  public static void writeSW(BControlPoint p, BPriorityLevel l, HVal val)
  {
    BStringWritable sw = (BStringWritable) p;
    BStatusString ss = null;

    if (l == BPriorityLevel.fallback)
    {
      ss = (BStatusString) sw.getFallback().newCopy();
    }
    else
    {
      ss = (BStatusString) sw.getLevel(l).newCopy();
    }

    if (val == null)
    {
      ss.setStatus(BStatus.nullStatus);
    }
    else
    {
      HStr str = (HStr) val;
      ss.setValue(str.val);
      ss.setStatus(BStatus.ok);
    }

    if (l == BPriorityLevel.fallback)
    {
      sw.setFallback(ss);
    }
    else
    {
      sw.set("in" + l.getOrdinal(), ss);
    }
  }

////////////////////////////////////////////////////////////////
// attribs 
////////////////////////////////////////////////////////////////

  private static final Logger LOG = Logger.getLogger("nhaystack");
  private static final String LAST_WRITE = "haystackLastWrite";

  private static final int FALLBACK_LEVEL = 17;

  private final BNHaystackService service;
  private final Cache cache;
  private final TagManager tagMgr;
  private final ScheduleManager schedMgr;
  private final FoxSessionManager foxSessionMgr;
}

