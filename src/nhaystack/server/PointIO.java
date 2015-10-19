//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Feb 2015  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;

import javax.baja.control.*;
import javax.baja.control.enums.*;
import javax.baja.fox.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.schedule.*;
import javax.baja.security.*;
import javax.baja.status.*;
import javax.baja.sys.*;

import org.projecthaystack.*;

import nhaystack.*;
import nhaystack.util.*;

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

        if (LOG.isTraceOn())
            LOG.trace("onPointWriteArray " + rec.id());


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

            if (LOG.isTraceOn())
                LOG.trace("onPointWrite " + 
                    "id:"    + rec.id() + ", " +
                    "level:" + level    + ", " +
                    "val:"   + val      + ", " +
                    "who:"   + who      + ", " +
                    "dur:"   + dur      + ", " +
                    "opts:"   + ((opts == null) ? "null" : opts.toZinc()));

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
                    LOG.trace("ignoring write to " + comp.getSlotPath());
                    return;
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

            // Numeric
            if (point instanceof BNumericWritable)
            {
                BNumericWritable nw = (BNumericWritable) point;
                for (int i = 0; i < 16; i++)
                {
                    BStatusNumeric sn = (BStatusNumeric) nw.get("in" + (i+1));
                    if (!sn.getStatus().isNull())
                        vals[i] = HNum.make(sn.getValue());
                }
                BStatusNumeric sn = (BStatusNumeric) nw.getFallback();
                if (!sn.getStatus().isNull())
                    vals[16] = HNum.make(sn.getValue());
            }
            // Boolean
            else if (point instanceof BBooleanWritable)
            {
                BBooleanWritable bw = (BBooleanWritable) point;
                for (int i = 0; i < 16; i++)
                {
                    BStatusBoolean sb = (BStatusBoolean) bw.get("in" + (i+1));
                    if (!sb.getStatus().isNull())
                        vals[i] = HBool.make(sb.getValue());
                }
                BStatusBoolean sb = (BStatusBoolean) bw.getFallback();
                if (!sb.getStatus().isNull())
                    vals[16] = HBool.make(sb.getValue());
            }
            // Enum
            else if (point instanceof BEnumWritable)
            {
                BEnumWritable ew = (BEnumWritable) point;
                for (int i = 0; i < 16; i++)
                {
                    BStatusEnum se = (BStatusEnum) ew.get("in" + (i+1));
                    if (!se.getStatus().isNull())
                        vals[i] = HStr.make(SlotUtil.fromEnum(
                            se.getValue().getTag(),
                            service.getTranslateEnums()));
                }
                BStatusEnum se = (BStatusEnum) ew.getFallback();
                if (!se.getStatus().isNull())
                    vals[16] = HStr.make(SlotUtil.fromEnum(
                        se.getValue().getTag(),
                        service.getTranslateEnums()));
            }
            // String
            else if (point instanceof BStringWritable)
            {
                BStringWritable sw = (BStringWritable) point;
                for (int i = 0; i < 16; i++)
                {
                    BStatusString s = (BStatusString) sw.get("in" + (i+1));
                    if (!s.getStatus().isNull())
                        vals[i] = HStr.make(s.getValue());
                }
                BStatusString s = (BStatusString) sw.getFallback();
                if (!s.getStatus().isNull())
                    vals[16] = HStr.make(s.getValue());
            }
            else
            {
                throw new IllegalStateException("unknown type: " + point.getType());
            }

            //////////////////////////////////////////////

            // Return priority array for writable point identified by id.
            // The grid contains 17 rows with following columns:
            //   - level: number from 1 - 17 (17 is default)
            //   - levelDis: human description of level
            //   - val: current value at level or null
            //   - who: who last controlled the value at this level

            String[] who = getLinkWho(point);
            HDict[] result = new HDict[17];
            for (int i = 0; i < 17; i++)
            {
                HDictBuilder hd = new HDictBuilder();
                HNum level = HNum.make(i+1);
                hd.add("level", level);
                hd.add("levelDis", "level " + (i+1)); // TODO?
                if (vals[i] != null)
                    hd.add("val", vals[i]);

                if (who[i].length() > 0)
                    hd.add("who", who[i]);

                result[i] = hd.toDict();
            }
            return HGridBuilder.dictsToGrid(result);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
      * return point array for BWeeklySchedule
      */
    private HGrid doScheduleArray(BWeeklySchedule sched)
    {
        return HGrid.EMPTY;
    }

    /**
      * get the source for each link that is connected to [in1..in16, fallback]
      */
    private String[] getLinkWho(BControlPoint point)
    {
        String[] who = new String[17];
        for (int i = 0; i < 17; i++)
            who[i] = "";

        BLink[] links = point.getLinks();
        for (int i = 0; i < links.length; i++)
        {
            String target = links[i].getTargetSlot().getName();
            Integer level = (Integer) POINT_PROP_LEVELS.get(target);
            if (level != null)
            {
                who[level.intValue()-1] +=
                    (links[i].getSourceComponent().getSlotPath() + "/" + 
                     links[i].getSourceSlot().getName());
            }
        }

        return who;
    }

    private static Map POINT_PROP_LEVELS = new HashMap();
    static
    {
        POINT_PROP_LEVELS.put("in1",  new Integer(1));
        POINT_PROP_LEVELS.put("in2",  new Integer(2));
        POINT_PROP_LEVELS.put("in3",  new Integer(3));
        POINT_PROP_LEVELS.put("in4",  new Integer(4));
        POINT_PROP_LEVELS.put("in5",  new Integer(5));
        POINT_PROP_LEVELS.put("in6",  new Integer(6));
        POINT_PROP_LEVELS.put("in7",  new Integer(7));
        POINT_PROP_LEVELS.put("in8",  new Integer(8));
        POINT_PROP_LEVELS.put("in9",  new Integer(9));
        POINT_PROP_LEVELS.put("in10", new Integer(10));
        POINT_PROP_LEVELS.put("in11", new Integer(11));
        POINT_PROP_LEVELS.put("in12", new Integer(12));
        POINT_PROP_LEVELS.put("in13", new Integer(13));
        POINT_PROP_LEVELS.put("in14", new Integer(14));
        POINT_PROP_LEVELS.put("in15", new Integer(15));
        POINT_PROP_LEVELS.put("in16", new Integer(16));
        POINT_PROP_LEVELS.put("fallback", new Integer(17));
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
        Map map = new HashMap();
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
        Iterator it = map.values().iterator();
        while (it.hasNext())
            dicts[n++] = (HDict) it.next();

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
            LOG.error("cannot write to " + point.getSlotPath() + ", does not have 'writable' tag.");
            return;
        }

        // look up remote point
        RemotePoint rp = RemotePoint.fromControlPoint(point);
        if (rp == null)
        {
            LOG.error("cannot write to " + point.getSlotPath() + ", it is neither writable nor remote.");
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
            LOG.error("cannot write to " + remote.getSlotPath() + ", it is not writable.");
            return;
        }

        // done
        onControlPointWriteLevel(remote, /*null,*/ level, val, who);
    }

    /**
      * onControlPointWriteLevel
      */
    private void onControlPointWriteLevel(
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
            LOG.error("cannot write to " + point.getSlotPath() + ", unknown point type " + point.getClass());
        }
    }

////////////////////////////////////////////////////////////////
// attribs 
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");
    private static final String LAST_WRITE = "haystackLastWrite";

    private final BNHaystackService service;
    private final Cache cache;
    private final TagManager tagMgr;
    private final ScheduleManager schedMgr;
    private final FoxSessionManager foxSessionMgr;
}

