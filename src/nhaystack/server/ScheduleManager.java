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

System.out.println("ScheduleManager.applySchedule: " + 
    event.getId() + ", " + event.getValue() + ", " + 
    point.getSlotPath());

        // set the point
        HDictBuilder hdb = new HDictBuilder();
        hdb.add("id", id);
        HDict rec = hdb.toDict();
        int level = tags.getInt("schedulable");
        HVal val = TypeUtil.fromBajaSimple((BSimple) event.getValue());
        server.onPointWrite(rec, level, val, "", null);

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

        BTimeZone tz = TypeUtil.toBajaTimeZone(items[0].ts.tz);
        BAbsTime now = BAbsTime.make(System.currentTimeMillis(), tz);

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
                
                // TODO save this thing so we can cancel it
                Clock.Ticket ticket = Clock.schedule(
                    service, 
                    absTime,
                    BNHaystackService.applySchedule,
                    new BHScheduleEvent(
                        BHRef.make(id),
                        TypeUtil.toBajaSimple(item.val)));

                return;
            }
        }
    }

////////////////////////////////////////////////////////////////
// attribs 
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private final NHServer server;
    private final BNHaystackService service;
}

