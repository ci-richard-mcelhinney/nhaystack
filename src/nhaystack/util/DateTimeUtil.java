//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Apr 2014  Mike Jarmy  Creation
//
package nhaystack.util;

import javax.baja.sys.*;

import org.projecthaystack.*;

public abstract class DateTimeUtil
{
    public static HDateTime toHaystackDateTime(BAbsTime absTime, HTimeZone tz)
    {
        return HDateTime.make(
            HDate.make(
                absTime.getYear(),
                absTime.getMonth().getMonthOfYear(),
                absTime.getDay()),
            HTime.make(
                absTime.getHour(),
                absTime.getMinute(),
                absTime.getSecond(),
                absTime.getMillisecond()),
            tz);
    }

    public static BAbsTime fromHaystackDateTime(HDateTime dt)
    {
        return BAbsTime.make(
            dt.date.year,
            BMonth.make(dt.date.month - 1),
            dt.date.day,
            dt.time.hour,
            dt.time.min,
            dt.time.sec,
            dt.time.ms);
    }
}
