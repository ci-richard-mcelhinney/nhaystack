//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//   10 May 2018  Eric Anderson       Added missing @Overrides annotations, added use of generics
//
package nhaystack.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.control.BControlPoint;
import javax.baja.nre.util.TextUtil;
import javax.baja.schedule.BEnumSchedule;
import javax.baja.schedule.BWeekSchedule;
import javax.baja.schedule.BWeeklySchedule;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusEnum;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusString;
import javax.baja.status.BStatusValue;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BComponent;
import javax.baja.sys.BEnum;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.BIBoolean;
import javax.baja.sys.BIEnum;
import javax.baja.sys.BINumeric;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BSimple;
import javax.baja.sys.BTime;
import javax.baja.sys.BValue;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Clock;
import javax.baja.sys.Clock.Ticket;
import nhaystack.BHDict;
import nhaystack.BHRef;
import nhaystack.util.SlotUtil;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HDateTime;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HHisItem;
import org.projecthaystack.HRef;
import org.projecthaystack.HStr;
import org.projecthaystack.HTime;
import org.projecthaystack.HTimeZone;
import org.projecthaystack.HVal;
import org.projecthaystack.io.HZincReader;
import org.projecthaystack.io.HZincWriter;

/**
  * ScheduleManager does various task associated with generating tags
  * and looking things up based on ids, etc.
  */
class ScheduleManager
{
    ScheduleManager(
        NHServer server,
        BNHaystackService service)
    {
        this.server = server;
        this.service = service;
    }

    /**
      * this gets called during 'rebuildCache()'
      */
    void makePointEvents(BComponent[] points)
    {
        for (BComponent point : points)
        {
            HRef id = server.getTagManager().makeComponentRef(point).getHRef();

            // cancel any existing ticket
            Ticket ticket = ticketById.remove(id);
            if (ticket != null)
            {
                ticket.cancel();
            }

            // make a new ticket
            makeTicketFromItems(id, hisItems(point));
        }
    }

    /**
      * Apply the given schedule to its parent point, by calling onPointWrite
      */
    void applySchedule(BHScheduleEvent event)
    {
        HRef id = event.getId().getRef();

        BComponent point = server.getTagManager().lookupComponent(id);
        HDict tags = BHDict.findTagAnnotation(point);

        // set the point
        HDictBuilder hdb = new HDictBuilder();
        hdb.add("id", id);
        HDict rec = hdb.toDict();
        int level = tags.getInt("schedulable");
        HVal val = TypeUtil.fromBajaSimple((BSimple) event.getValue(), service.getTranslateEnums());
        server.onPointWrite(rec, level, val, "", null, null);

        // remove existing ticket
        ticketById.remove(id);

        // try to make another ticket
        makeTicketFromItems(id, hisItems(point));
    }

    /**
      * fish the HHisItems out of the opts from pointWrite().
      */
    HHisItem[] getOptionsSchedule(HDict opts)
    {
        if (opts == null) return null;
        if (!opts.has("schedule")) return null;

        String zinc = TextUtil.replace(opts.getStr("schedule"), "\\n", "\n");
        HZincReader reader = new HZincReader(zinc);
        HGrid grid = reader.readGrid();
        HHisItem[] items = HHisItem.gridToItems(grid);
        return items;
    }

    /**
      * Write a schedule
      */
    void onScheduleWrite(HDict rec, HHisItem[] items)
    {
        BComponent comp = server.getTagManager().lookupComponent(rec.id());
        if (comp == null) 
            throw new BajaRuntimeException("Cannot find component for " + rec.id());

        if (comp instanceof BControlPoint)
        {
            writePointSchedule((BControlPoint) comp, items);
        }
        else if (comp instanceof BWeeklySchedule)
        {
            writeWeeklySchedule((BWeeklySchedule) comp, items);
        }
        else
        {
            throw new BajaRuntimeException("Cannot write schedule to " + comp.getSlotPath());
        }
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private void writePointSchedule(BControlPoint point, HHisItem[] items)
    {
        HDict orig = BHDict.findTagAnnotation(point);
        if (orig == null) orig = HDict.EMPTY;

        HDictBuilder hdb = new HDictBuilder();
        Iterator<Map.Entry<String, HVal>> itr = orig.iterator();
        while (itr.hasNext())
        {
            Map.Entry<String, HVal> e = itr.next();
            String name = e.getKey();
            HVal val = e.getValue();

            if (name.equals("weeklySchedule")) continue;
            if (name.equals("tz") && items.length > 0) continue;
            hdb.add(name, val);
        }

        HGrid schedule = HGridBuilder.hisItemsToGrid(HDict.EMPTY, items);
        hdb.add("weeklySchedule", HZincWriter.gridToString(schedule));
        if (items.length > 0)
            hdb.add("tz", items[0].ts.tz.toString());

        if (point.get("haystack") == null)
            point.add("haystack", BHDict.make(hdb.toDict()));
        else
            point.set("haystack", BHDict.make(hdb.toDict()));

        HRef id = server.getTagManager().makeComponentRef(point).getHRef();
        makeTicketFromItems(id, items);
    } 

    private static void writeWeeklySchedule(BWeeklySchedule sched, HHisItem[] items)
    {
        items = normalizeWeek(items);

        BWeekSchedule week = new BWeekSchedule();
        BValue defaultValue = sched.getDefaultOutput().getValueValue();

        for (int i = 0; i < items.length; i++)
        {
            BSimple value = makeScheduleValue(sched, items[i]);

            // for enums, you have to compare ordinal value, because for
            // some reason the defaultValue does not have its tag set properly
            if (sched instanceof BEnumSchedule)
            {
                BEnum defaultEnm = (BEnum) defaultValue;
                BEnum enm = (BEnum) value;
                if (defaultEnm.getOrdinal() == enm.getOrdinal())
                    continue;
            }
            else
            {
                if (value.equals(defaultValue)) continue;
            }

            // skip values that correspond to the schedule's default output
            BStatusValue sv = makeStatusValue(value, BStatus.ok);

            // start
            BTime start = BTime.make(
                BAbsTime.make(
                    items[i].ts.millis(), 
                    TypeUtil.toBajaTimeZone(items[i].ts.tz)));

            // finish (just before midnight if its the last one)
            BTime finish;
            if (i < items.length-1)
            {
                finish = BTime.make(
                    BAbsTime.make(
                        items[i+1].ts.millis(), 
                        TypeUtil.toBajaTimeZone(items[i+1].ts.tz)));
            }
            else
            {
                finish = BTime.make(23, 59, 59, 999);
            }

            // apply to the appropriate weekday
            switch (items[i].ts.date.weekday())
            {
                case 1: week.getSunday()    .add(start, finish, sv, null); break;
                case 2: week.getMonday()    .add(start, finish, sv, null); break;
                case 3: week.getTuesday()   .add(start, finish, sv, null); break;
                case 4: week.getWednesday() .add(start, finish, sv, null); break;
                case 5: week.getThursday()  .add(start, finish, sv, null); break;
                case 6: week.getFriday()    .add(start, finish, sv, null); break;
                case 7: week.getSaturday()  .add(start, finish, sv, null); break;

                default: 
                    throw new IllegalStateException();
            }
        }

        sched.getSchedule().set("week", week);
    }

    private static BSimple makeScheduleValue(BWeeklySchedule sched, HHisItem item)
    {
        if (sched instanceof BEnumSchedule)
        {
            String str = SlotUtil.toNiagara(((HStr) item.val).val);
            BFacets facets = sched.getFacets();
            BEnumRange range = (BEnumRange) facets.get(BFacets.RANGE);
            return range.get(str);
        }
        else
        {
            return TypeUtil.toBajaSimple(item.val);
        }
    }

    /**
      * Make sure all of the items are scheduled to happen this week.  
      */
    private static HHisItem[] normalizeWeek(HHisItem[] items)
    {
        HTimeZone tz = items[0].ts.tz;
        long curMillis = System.currentTimeMillis();

        // compute sunday of this week
        HDateTime now = HDateTime.make(curMillis, tz);
        HDateTime sunday = HDateTime.make(
            now.date.minusDays(now.date.weekday()-1), 
            HTime.MIDNIGHT, tz);

        return doNormalizeWeek(items, sunday);
    }

    private static BStatusValue makeStatusValue(BValue value, BStatus status)
    {
        if (value instanceof BIBoolean) 
            return new BStatusBoolean(((BIBoolean)value).getBoolean(), status);

        if (value instanceof BINumeric) 
            return new BStatusNumeric(((BINumeric)value).getNumeric(), status);

        if (value instanceof BIEnum) 
            return new BStatusEnum(((BIEnum)value).getEnum(), status);

        return new BStatusString(value.toString(), status);
    }

    /**
      * read the HHisItems from the 'weeklySchedule' tag
      */
    private static HHisItem[] hisItems(BComponent point)
    {
        HDict tags = BHDict.findTagAnnotation(point);
        HGrid grid = new HZincReader(tags.getStr("weeklySchedule")).readGrid();
        return HHisItem.gridToItems(grid);
    }

    /**
      * create a ticket for the next future HHisItem
      */
    private void makeTicketFromItems(HRef id, HHisItem[] items)
    {
        if (items.length == 0) return;

        HTimeZone tz = items[0].ts.tz;
        long curMillis = System.currentTimeMillis();

        // compute sunday of this week
        HDateTime now = HDateTime.make(curMillis, tz);
        HDateTime sunday = HDateTime.make(
            now.date.minusDays(now.date.weekday()-1), 
            HTime.MIDNIGHT, tz);

        // try this week
        if (!scheduleWeeklyTicket(id, doNormalizeWeek(items, sunday), curMillis))
        {
            // maybe next week
            sunday = HDateTime.make(sunday.date.plusDays(7), HTime.MIDNIGHT, tz);
            scheduleWeeklyTicket(id, doNormalizeWeek(items, sunday), curMillis);
        }
    }

    /**
      * Attempt to schedule something for this week from the given HHisItems.
      * If there is nothing available, then  
      */
    private boolean scheduleWeeklyTicket(HRef id, HHisItem[] items, long curMillis)
    {
        HDateTime now = HDateTime.now(items[0].ts.tz);

        // try to find an item from the future
        for (HHisItem item : items)
        {
            if (item.ts.millis() > now.millis())
            {
                BAbsTime absTime = BAbsTime.make(
                    item.ts.millis(), 
                    TypeUtil.toBajaTimeZone(item.ts.tz));

                if (LOG.isLoggable(Level.FINE))
                    LOG.fine("Scheduling a ticket at " + item.ts + " for " + id);

                ticketById.put(
                    id, 
                    Clock.schedule(
                        service, 
                        absTime,
                        BNHaystackService.applySchedule,
                        new BHScheduleEvent(
                            BHRef.make(id),
                            TypeUtil.toBajaSimple(item.val))));

                return true;
            }
        }

        return false;
    }

    /**
      * Make sure all of the items are scheduled to happen this week.  
      */
    private static HHisItem[] doNormalizeWeek(HHisItem[] items, HDateTime thisSun)
    {
        if (items.length == 0) throw new IllegalStateException();

        HTimeZone tz = items[0].ts.tz;
        ArrayList<HHisItem> arr = new ArrayList<>();

        Set<HDateTime> timestamps = new HashSet<>();

        // compute sunday of next week
        HDateTime nextSun = HDateTime.make(
            thisSun.date.plusDays(7), 
            HTime.MIDNIGHT, tz);

        for (HHisItem item : items)
        {
            // Skip null values
            if (item.val == null) continue;

            HDateTime ts = item.ts;

            // subtract weeks until we are before next sunday
            while (ts.millis() >= nextSun.millis())
                ts = HDateTime.make(ts.date.minusDays(7), ts.time, tz);

            // add weeks until we are after this sunday
            while (ts.millis() < thisSun.millis())
                ts = HDateTime.make(ts.date.plusDays(7), ts.time, tz);

            // Skip timestamps that we've already seen.
            // This way we don't end up normalizing values that 'wrap-around'.
            if (timestamps.contains(ts)) continue;

            arr.add(HHisItem.make(ts, item.val));
            timestamps.add(ts);
        }

        HHisItem[] result = arr.toArray(EMPTY_HIS_ITEM_ARRAY);

        Arrays.sort(
            result,
            new Comparator<HHisItem>() {
                @Override
                public int compare(HHisItem h1, HHisItem h2) {
                    return (int) (h1.ts.millis() - h2.ts.millis());
                }
            });

        return result;
    }

////////////////////////////////////////////////////////////////
// attribs 
////////////////////////////////////////////////////////////////

    private static final long MILLIS_IN_WEEK = BRelTime.MILLIS_IN_DAY * 7;
    private static final HHisItem[] EMPTY_HIS_ITEM_ARRAY = new HHisItem[0];

    private static final Logger LOG = Logger.getLogger("nhaystack");

    private final NHServer server;
    private final BNHaystackService service;

    // <HRef,Clock.Ticket>
    private final Map<HRef, Ticket> ticketById = new ConcurrentHashMap<>();
}

