//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Feb 2015  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Added missing @Overrides annotations, added use of generics
//
package nhaystack.server;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.control.BBooleanWritable;
import javax.baja.control.BControlPoint;
import javax.baja.control.BEnumWritable;
import javax.baja.control.BIWritablePoint;
import javax.baja.control.BNumericWritable;
import javax.baja.control.BStringWritable;
import javax.baja.control.enums.BPriorityLevel;
import javax.baja.fox.BFoxProxySession;
import javax.baja.naming.BOrd;
import javax.baja.schedule.BWeeklySchedule;
import javax.baja.security.PermissionException;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusEnum;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.sys.BComponent;
import javax.baja.sys.BEnum;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.BLink;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import nhaystack.BHDict;
import nhaystack.BHGrid;
import nhaystack.util.SlotUtil;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HBool;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HHisItem;
import org.projecthaystack.HNum;
import org.projecthaystack.HStr;
import org.projecthaystack.HVal;

/**
  * PointIO handles onPointWriteArray() and onPointWrite()
  */
class PointIO
{
    PointIO(
        BNHaystackService service,
        Cache cache,
        TagManager tagMgr,
        ScheduleManager schedMgr,
        FoxSessionManager foxSessionMgr)
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
            throw new IllegalStateException(Cache.NOT_INITIALIZED);

        if (LOG.isLoggable(Level.FINE))
            LOG.fine("onPointWriteArray " + rec.id());


        BComponent comp = tagMgr.lookupComponent(rec.id());

        if (comp instanceof BControlPoint)
            return doControlPointArray((BControlPoint) comp);

        else if (comp instanceof BWeeklySchedule)
            return doScheduleArray((BWeeklySchedule) comp);

        else
            throw new BajaRuntimeException("pointWriteArray() failed for " + comp.getSlotPath());
    }

    /**
      * Implementation hook for pointWrite
      */
    void onPointWrite(
        HDict rec, 
        int level, 
        HVal val, 
        String who, 
        HNum dur, // ignore this for now
        HDict opts)
    {
        try
        {
            if (!cache.initialized()) 
                throw new IllegalStateException(Cache.NOT_INITIALIZED);

            if (LOG.isLoggable(Level.FINE))
                LOG.fine("onPointWrite " +
                    "id:"    + rec.id() + ", " +
                    "level:" + level    + ", " +
                    "val:"   + val      + ", " +
                    "who:"   + who      + ", " +
                    "dur:"   + dur      + ", " +
                    "opts:"  + (opts == null ? "null" : opts.toZinc()));

            HHisItem[] schedItems = schedMgr.getOptionsSchedule(opts);
            if (schedItems == null)
            {
                BComponent comp = tagMgr.lookupComponent(rec.id());
                if (comp == null) 
                    throw new BajaRuntimeException("Cannot find component for " + rec.id());

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
                BFoxProxySession session = foxSessionMgr.getSession(
                        RemotePoint.findParentDevice(point), millis);

                // resolve remote point
                remote = (BControlPoint)
                        BOrd.make("station:|" + rp.getSlotPath()).get(session);
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
                    BStatusNumeric sn = (BStatusNumeric) nw.get("in" + (i+1));
                    if (!sn.getStatus().isNull())
                        vals[i] = HNum.make(sn.getValue());
                }
                BStatusNumeric sn = nw.getFallback();
                if (!sn.getStatus().isNull())
                    vals[16] = HNum.make(sn.getValue());
            }
            // Boolean
            else if (working instanceof BBooleanWritable)
            {
                BBooleanWritable bw = (BBooleanWritable) working;
                for (int i = 0; i < 16; i++)
                {
                    BStatusBoolean sb = (BStatusBoolean) bw.get("in" + (i+1));
                    if (!sb.getStatus().isNull())
                        vals[i] = HBool.make(sb.getValue());
                }
                BStatusBoolean sb = bw.getFallback();
                if (!sb.getStatus().isNull())
                    vals[16] = HBool.make(sb.getValue());
            }
            // Enum
            else if (working instanceof BEnumWritable)
            {
                BEnumWritable ew = (BEnumWritable) working;
                for (int i = 0; i < 16; i++)
                {
                    BStatusEnum se = (BStatusEnum) ew.get("in" + (i+1));
                    if (!se.getStatus().isNull())
                        vals[i] = HStr.make(SlotUtil.fromEnum(
                            se.getValue().getTag(),
                            service.getTranslateEnums()));
                }
                BStatusEnum se = ew.getFallback();
                if (!se.getStatus().isNull())
                    vals[16] = HStr.make(SlotUtil.fromEnum(
                        se.getValue().getTag(),
                        service.getTranslateEnums()));
            }
            // String
            else if (working instanceof BStringWritable)
            {
                BStringWritable sw = (BStringWritable) working;
                for (int i = 0; i < 16; i++)
                {
                    BStatusString s = (BStatusString) sw.get("in" + (i+1));
                    if (!s.getStatus().isNull())
                        vals[i] = HStr.make(s.getValue());
                }
                BStatusString s = sw.getFallback();
                if (!s.getStatus().isNull())
                    vals[16] = HStr.make(s.getValue());
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
                HNum level = HNum.make(i+1);
                hd.add("level", level);
                hd.add("levelDis", "level " + (i+1)); // TODO?
                if (vals[i] != null)
                    hd.add("val", vals[i]);

                if (!who[i].isEmpty())
                    hd.add("who", who[i]);

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
        return HGrid.EMPTY;
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
                who[level.intValue() - 1] +=
                    link.getSourceComponent().getSlotPath() + "/" +
                    link.getSourceSlot().getName();
            }
        }

        return who;
    }

    private static final Map<String, Integer> POINT_PROP_LEVELS = new HashMap<>();
    static
    {
        POINT_PROP_LEVELS.put("in1",  Integer.valueOf(1));
        POINT_PROP_LEVELS.put("in2",  Integer.valueOf(2));
        POINT_PROP_LEVELS.put("in3",  Integer.valueOf(3));
        POINT_PROP_LEVELS.put("in4",  Integer.valueOf(4));
        POINT_PROP_LEVELS.put("in5",  Integer.valueOf(5));
        POINT_PROP_LEVELS.put("in6",  Integer.valueOf(6));
        POINT_PROP_LEVELS.put("in7",  Integer.valueOf(7));
        POINT_PROP_LEVELS.put("in8",  Integer.valueOf(8));
        POINT_PROP_LEVELS.put("in9",  Integer.valueOf(9));
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
            point.add(
                LAST_WRITE, 
                BHGrid.make(grid),
                Flags.SUMMARY | Flags.READONLY);
        }
        else
        {
            HGrid grid = saveLastWriteToGrid(oldGrid.getGrid(), level, who);
            point.set(
                LAST_WRITE, 
                BHGrid.make(grid));
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

    private void onControlPointWrite(
        BControlPoint point,
        HDict rec, 
        int level, 
        HVal val, 
        String who, 
        HNum dur) // ignore this for now
    throws Exception
    {
        // check permissions on this Thread's saved context
        Context cx = ThreadContext.getContext(Thread.currentThread());
        if (!TypeUtil.canWrite(point, cx)) 
            throw new PermissionException("Cannot write to " + rec.id()); 

        // if its writable, just go ahead and do the write
        if (point instanceof BIWritablePoint)
            onControlPointWriteLevel(point, /*point.getFacets(),*/ level, val, who);

        // else do a remote write
        else
            onControlPointWriteRemote(point, level, val, who);

        // done
        saveLastWrite(point, level, who);
    }

    /**
      * onControlPointWriteRemote
      */
    private void onControlPointWriteRemote(BControlPoint point, int level, HVal val, String who)
    throws Exception
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
        BFoxProxySession session = foxSessionMgr.getSession(
            RemotePoint.findParentDevice(point), millis);

        // resolve remote point
        BControlPoint remote = (BControlPoint) 
            BOrd.make("station:|" + rp.getSlotPath()).get(session);
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
    private static void onControlPointWriteLevel(
        BControlPoint point, /*BFacets facets,*/
        int level, HVal val, String who)
    {
        BPriorityLevel plevel = BPriorityLevel.make(level);

        if (point instanceof BNumericWritable)
        {
            BNumericWritable nw = (BNumericWritable) point;
            BStatusNumeric sn = (BStatusNumeric) nw.getLevel(plevel).newCopy();

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
            nw.set("in" + level, sn);
        }
        else if (point instanceof BBooleanWritable)
        {
            BBooleanWritable bw = (BBooleanWritable) point;
            BStatusBoolean sb = (BStatusBoolean) bw.getLevel(plevel).newCopy();

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
            bw.set("in" + level, sb);
        }
        else if (point instanceof BEnumWritable)
        {
            BEnumWritable ew = (BEnumWritable) point;
            BStatusEnum se = (BStatusEnum) ew.getLevel(plevel).newCopy();

            if (val == null)
            {
                se.setStatus(BStatus.nullStatus);
            }
            else
            {
                String str = SlotUtil.toNiagara(((HStr) val).val);
                BFacets facets = point.getFacets();
                BEnumRange range = (BEnumRange) facets.get(BFacets.RANGE);
                BEnum enm = range.get(str);
                se.setValue(enm);
                se.setStatus(BStatus.ok);
            }
            ew.set("in" + level, se);
        }
        else if (point instanceof BStringWritable)
        {
            BStringWritable sw = (BStringWritable) point;
            BStatusString s = (BStatusString) sw.getLevel(plevel).newCopy();

            if (val == null)
            {
                s.setStatus(BStatus.nullStatus);
            }
            else
            {
                HStr str = (HStr) val;
                s.setValue(str.val);
                s.setStatus(BStatus.ok);
            }
            sw.set("in" + level, s);
        }
        else 
        {
            LOG.severe("cannot write to " + point.getSlotPath() + ", unknown point type " + point.getClass());
        }
    }

////////////////////////////////////////////////////////////////
// attribs 
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack");
    private static final String LAST_WRITE = "haystackLastWrite";

    private final BNHaystackService service;
    private final Cache cache;
    private final TagManager tagMgr;
    private final ScheduleManager schedMgr;
    private final FoxSessionManager foxSessionMgr;
}

