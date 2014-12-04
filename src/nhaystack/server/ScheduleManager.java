//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//
package nhaystack.server;

import java.util.*;
import javax.baja.control.*;
import javax.baja.history.*;
import javax.baja.history.ext.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.timezone.*;
import javax.baja.units.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.io.*;
import org.projecthaystack.util.*;

import nhaystack.*;
import nhaystack.res.*;
import nhaystack.site.*;
import nhaystack.util.*;

/**
  * ScheduleManager does various task associated with generating tags
  * and looking things up based on ids, etc.
  */
public class ScheduleManager
{
    ScheduleManager(
        NHServer server,
        BNHaystackService service)
    {
        this.server = server;
        this.service = service;
    }

    void makePointEvents(BControlPoint[] points)
    {
        if (ticket != null)
        {
            LOG.trace("Canceling a ticket at " + ticketTs + " for " + ticketId);
            ticket.cancel();
            ticket = null;
        }
        for (int i = 0; i < points.length; i++)
            makePointEvent(points[i]);
    }

    void makePointEvent(BControlPoint point)
    {
        HRef id = server.getTagManager().makeComponentRef(point).getHRef();
        HHisItem[] items = hisItems(point);
        makeTicketFromItems(id, items);
    }

    void applySchedule(BHScheduleEvent event)
    {
        HRef id = event.getId().getRef();

        BControlPoint point = (BControlPoint) server.getTagManager().lookupComponent(id);
        HDict tags = BHDict.findTagAnnotation(point);

//System.out.println("ScheduleManager.applySchedule: " + 
//    event.getId() + ", " + event.getValue() + ", " + 
//    point.getSlotPath());

        // set the point
        HDictBuilder hdb = new HDictBuilder();
        hdb.add("id", id);
        HDict rec = hdb.toDict();
        int level = tags.getInt("schedulable");
        HVal val = TypeUtil.fromBajaSimple((BSimple) event.getValue());
        server.onPointWrite(rec, level, val, "", null, null);

        // try to make another ticket
        HHisItem[] items = hisItems(point);
        makeTicketFromItems(id, items);
    }

    private HHisItem[] hisItems(BControlPoint point)
    {
        HDict tags = BHDict.findTagAnnotation(point);
        HGrid grid = (new HZincReader(tags.getStr("weeklySchedule"))).readGrid();
        return HHisItem.gridToItems(grid);
    }

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
        if (!scheduleWeeklyTicket(id, normalizeWeek(items, sunday), curMillis))
        {
            // maybe next week
            sunday = HDateTime.make(sunday.date.plusDays(7), HTime.MIDNIGHT, tz);
            scheduleWeeklyTicket(id, normalizeWeek(items, sunday), curMillis);
        }
    }

    private boolean scheduleWeeklyTicket(HRef id, HHisItem[] items, long curMillis)
    {
        BTimeZone tz = TypeUtil.toBajaTimeZone(items[0].ts.tz);
        BAbsTime now = BAbsTime.make(curMillis, tz);

        // try to find an item from the future
        for (int i = 0; i < items.length; i++)
        {
            HHisItem item = items[i];
            if (item.ts.millis() > now.getMillis())
            {
                BAbsTime absTime = BAbsTime.make(
                    item.ts.millis(), 
                    TypeUtil.toBajaTimeZone(item.ts.tz));

                if (LOG.isTraceOn())
                    LOG.trace("Scheduling a ticket at " + item.ts + " for " + id);
                
                ticketTs = item.ts;
                ticketId = id;
                ticket = Clock.schedule(
                    service, 
                    absTime,
                    BNHaystackService.applySchedule,
                    new BHScheduleEvent(
                        BHRef.make(id),
                        TypeUtil.toBajaSimple(item.val)));

                return true;
            }
        }

        return false;
    }

    /**
      * Make sure all of the items are scheduled to happen this week.  
      */
    private HHisItem[] normalizeWeek(HHisItem[] items, HDateTime thisSun)
    {
        if (items.length == 0) throw new IllegalStateException();

        HTimeZone tz = items[0].ts.tz;
        HHisItem[] future = new HHisItem[items.length];

        // compute sunday of next week
        HDateTime nextSun = HDateTime.make(
            thisSun.date.plusDays(7), 
            HTime.MIDNIGHT, tz);

        for (int i = 0; i < items.length; i++)
        {
            HDateTime ts = items[i].ts;

            // subtract weeks until we are before next sunday
            while (ts.millis() >= nextSun.millis())
                ts = HDateTime.make(ts.date.minusDays(7), ts.time, tz);

            // add weeks until we are after this sunday
            while (ts.millis() < thisSun.millis())
                ts = HDateTime.make(ts.date.plusDays(7), ts.time, tz);

            future[i] = HHisItem.make(ts, items[i].val);
        }

        Arrays.sort(
            future,
            new Comparator() {
                public int compare(Object o1, Object o2) {
                    HHisItem h1 = (HHisItem) o1;
                    HHisItem h2 = (HHisItem) o2;
                    return (int) (h1.ts.millis() - h2.ts.millis());
                }
            });

        return future;
    }

////////////////////////////////////////////////////////////////
// attribs 
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private final NHServer server;
    private final BNHaystackService service;
    private Clock.Ticket ticket = null;
    private HDateTime ticketTs = null;
    private HRef ticketId = null;
}

