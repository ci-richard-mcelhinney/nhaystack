//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   22 Mar 2013  Mike Jarmy       Creation
//   09 May 2018  Eric Anderson    Added support for the BMarker baja type, added use of generics
//   26 Sep 2018  Andrew Saunders  Added support for the geoCoord tag, added fromBajaDataValue to
//                                 support UI updates
//
package nhaystack.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.baja.control.BEnumWritable;
import javax.baja.control.util.BEnumOverride;
import javax.baja.data.BIDataValue;
import javax.baja.history.BHistoryConfig;
import javax.baja.history.BIHistory;
import javax.baja.history.HistorySpaceConnection;
import javax.baja.history.db.BHistoryDatabase;
import javax.baja.naming.BOrd;
import javax.baja.security.BPermissions;
import javax.baja.status.BStatus;
import javax.baja.sys.Action;
import javax.baja.sys.BBoolean;
import javax.baja.sys.BComplex;
import javax.baja.sys.BComponent;
import javax.baja.sys.BDouble;
import javax.baja.sys.BDynamicEnum;
import javax.baja.sys.BEnum;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.BMarker;
import javax.baja.sys.BNumber;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BSimple;
import javax.baja.sys.BString;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.timezone.BTimeZone;
import javax.baja.units.BUnit;
import nhaystack.res.Resources;
import nhaystack.res.Unit;
import org.projecthaystack.HBool;
import org.projecthaystack.HCoord;
import org.projecthaystack.HDict;
import org.projecthaystack.HMarker;
import org.projecthaystack.HNum;
import org.projecthaystack.HStr;
import org.projecthaystack.HTimeZone;
import org.projecthaystack.HVal;

/**
  * TypeUtil maps between Haystack types and Baja types.
  */
public abstract class TypeUtil
{
    private TypeUtil() {}

    public static BSimple toBajaSimple(HVal val)
    {
        if (val instanceof HStr)
        {
            return BString.make(((HStr) val).val); 
        }

        if (val instanceof HNum)
        {
            HNum num = (HNum) val;

            if (num.unit == null)
            {
                return BDouble.make(num.val);
            }
            else
            {
                Unit unit = Resources.getSymbolUnit(num.unit);
                if (unit.quantity.equals("time"))
                {
                    return makeRelTime(num, unit);
                }
                else
                {
                    return BDouble.make(num.val);
                }
            }
        }

        if (val instanceof HBool)
        {
            return BBoolean.make(((HBool) val).val);
        }

        if (val instanceof HMarker)
        {
            return BMarker.MARKER;
        }

        if (val instanceof HCoord)
        {
            return BString.make(val.toString());
        }

        throw new IllegalStateException(CANNOT_CONVERT +
            val.getClass() + ": " + val);
    }

    private static BRelTime makeRelTime(HNum num, Unit unit)
    {
        switch (unit.name)
        {
            case "nanosecond":        return BRelTime.make((long) (num.val / 1000 / 1000));
            case "microsecond":       return BRelTime.make((long) (num.val / 1000));
            case "millisecond":       return BRelTime.make((long) num.val);
            case "hundredths_second": return BRelTime.make((long) (num.val * 10));
            case "tenths_second":     return BRelTime.make((long) (num.val * 100));
            case "second":            return BRelTime.makeSeconds((int) num.val);
            case "minute":            return BRelTime.makeMinutes((int) num.val);
            case "hour":              return BRelTime.makeHours((int) num.val);
            case "day":               return BRelTime.makeHours((int) (num.val * 24));
            default: throw new IllegalStateException(CANNOT_CONVERT + num + " to RelTime");
        }
    }

    public static HVal fromBajaSimple(BSimple simple, boolean translateEnum)
    {
        if (simple instanceof BString)
        {
            return HStr.make(simple.toString());
        }

        if (simple instanceof BNumber)
        {
            return HNum.make(((BNumber) simple).getDouble());
        }

        if (simple instanceof BBoolean)
        {
            return HBool.make(((BBoolean) simple).getBoolean());
        }

        if (simple instanceof BEnum)
        {
            return HStr.make(SlotUtil.fromEnum(((BEnum) simple).getTag(), translateEnum));
        }

        throw new IllegalStateException(CANNOT_CONVERT + simple.getClass());
    }

    public static HVal fromBajaDataValue(BIDataValue dataValue)
    {
        if (dataValue instanceof BString)
        {
            return HStr.make(((BString)dataValue).getString());
        }

        if (dataValue instanceof BNumber)
        {
            return HNum.make(((BNumber) dataValue).getDouble());
        }

        if (dataValue instanceof BBoolean)
        {
            return HBool.make(((BBoolean) dataValue).getBoolean());
        }

        if (dataValue instanceof BMarker)
        {
            return HMarker.VAL;
        }

        if (dataValue instanceof BUnit)
        {
            if (((BUnit)dataValue).isNull())
            {
                return null;
            }
            else
            {
                return HStr.make(((BUnit)dataValue).getSymbol());
            }
        }

        throw new IllegalStateException(CANNOT_CONVERT + dataValue.getClass());
    }

    /**
      * Convert the args into a parameter that can be used to invoke the Action.
      */
    public static BValue actionArgsToBaja(HDict args, BComponent comp, Action action)
    {
        // NOTE we can't use args.size(), because if args is an HRow,
        // the size() can be non-zero even if args.iterator().hasNext() is false.

        BValue def = action.getParameterDefault();

        // null
        if (def == null)
        {
            return null;
        }
        // simple
        else if (def instanceof BSimple)
        {
            Map.Entry<String, HVal> e = (Map.Entry<String, HVal>)args.iterator().next();
            HVal val = e.getValue();

            BSimple simple;
            if (comp instanceof BEnumWritable)
            {
                String str = SlotUtil.toNiagara(((HStr) val).val);
                BFacets facets = ((BEnumWritable) comp).getFacets();
                BEnumRange range = (BEnumRange) facets.get(BFacets.RANGE);
                simple = range.get(str);
            }
            else
            {
                simple = toBajaSimple(val);
            }

            if (!simple.getType().is(action.getParameterType()))
            {
                throw new IllegalStateException(
                    "type mismatch: " + simple.getType() +
                    " is not " + action.getParameterType());
            }

            return simple;
        }
        else if (def instanceof BEnumOverride)
        {
            BEnumOverride cpx = (BEnumOverride) def;
            BFacets facets = comp.getAction(action.getName()).getFacets();
            if (facets.isNull()) throw new NullPointerException(
                "comp " + comp + " does not have "
                    + "facets that are needed for action " + action.getName());
            BEnumRange range;
            try {
                range = (BEnumRange) facets.get(BFacets.RANGE);
            } catch (ClassCastException e) {
                throw new ClassCastException("range facets of comp "
                    + comp + " must be of type " + BEnumRange.TYPE);
            }

            if (args.missing("value")) {
                throw new IllegalArgumentException("action args must "
                    + "have a non-null value for key='value'");
            }
            if (args.missing("duration")) {
                throw new IllegalArgumentException("action args must "
                    + "have a non-null value for key='duration'");
            }

            // construction of BDynamicEnum arg 'value'
            BSimple value = toBajaSimple(args.get("value"));
            if (value instanceof BString && range.isTag(value.toString()))
            {
                value = range.get(value.toString());
            }
            else if (value instanceof BDouble && range.isOrdinal(((BDouble) value).getInt()))
            {
                value = range.get(((BDouble) value).getInt());
            }
            else
            {
                try {
                    throw new IllegalStateException(
                        "value: " + value.toString()
                            + " is not ordinal nor tag of " + range.encodeToString());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            cpx.setValue((BDynamicEnum) value);

            // construction of BRelTime arg 'duration'
            BSimple duration = toBajaSimple(args.get("duration"));
            if (!(duration instanceof BRelTime)) {
                throw new IllegalStateException(
                    "duration: " + duration.toString()
                        + " is not a time value");
            }
            cpx.setDuration((BRelTime) duration);

            // construction of optional BRelTime arg 'maxOverrideDuration'
            if (args.has("maxOverrideDuration")) {
                BSimple maxOverrideDuration = toBajaSimple(args.get("maxOverrideDuration"));
                if (!(maxOverrideDuration instanceof BRelTime)) {
                    throw new IllegalStateException(
                        "maxOverrideDuration: " + maxOverrideDuration.toString()
                            + " is not a time value");
                }
                cpx.setMaxOverrideDuration((BRelTime) maxOverrideDuration);
            }

            // other arguments are ignored
            return cpx;
        }
        // complex
        else
        {
            BComplex cpx = (BComplex) def;

            // Set each slot in the BComplex to a dict entry.
            // Note that we do not currently support nesting structs within structs.
            Iterator<Map.Entry<String, HVal>> it = args.iterator();
            while (it.hasNext())
            {
                Map.Entry<String, HVal> e = it.next();
                cpx.set(e.getKey(), toBajaSimple(e.getValue()));
            }

            return cpx;
        }
    }

    public static BTimeZone toBajaTimeZone(HTimeZone tz)
    {
        return BTimeZone.getTimeZone(tz.java.getID());
    }

    public static BStatus toBajaStatus(HStr curStatus)
    {
        if (curStatus.val.equals("ok"))
        {
            return BStatus.ok;
        }

        if (curStatus.val.equals("fault"))
        {
            return BStatus.fault;
        }

        if (curStatus.val.equals("down"))
        {
            return BStatus.down;
        }

        if (curStatus.val.equals("disabled"))
        {
            return BStatus.disabled;
        }

        if (curStatus.val.equals("unknown"))
        {
            return BStatus.nullStatus;
        }

        throw new IllegalStateException(CANNOT_CONVERT + curStatus.val + " to BStatus");
    }

////////////////////////////////////////////////////////////////
// permissions
////////////////////////////////////////////////////////////////

    /**
      * Check if the permissions for the component allow us to read
      */
    public static boolean canRead(BComponent comp, Context cx)
    {          
        BPermissions perm = permissions(comp, cx);
        return 
            perm.has(BPermissions.OPERATOR_READ) ||
            perm.has(BPermissions.ADMIN_READ);
    }

    /**
      * Check if the permissions for the component allow us to write
      */
    public static boolean canWrite(BComponent comp, Context cx)
    {          
        BPermissions perm = permissions(comp, cx);
        return 
            perm.has(BPermissions.OPERATOR_WRITE) ||
            perm.has(BPermissions.ADMIN_WRITE);
    }

    /**
      * Check if the permissions for the component allow us to invoke
      */
    public static boolean canInvoke(BComponent comp, Context cx)
    {          
        BPermissions perm = permissions(comp, cx);
        return 
            perm.has(BPermissions.OPERATOR_INVOKE) ||
            perm.has(BPermissions.ADMIN_INVOKE);
    }

    private static BPermissions permissions(BComponent comp, Context cx)
    {
        // For history config, you have to look up the BIHistory
        // and get the permissions for that.
        if (comp instanceof BHistoryConfig)
        {
            BHistoryConfig cfg = (BHistoryConfig) comp;
            BHistoryDatabase historyDb = (BHistoryDatabase) 
                BOrd.make("history:").get(); 

            try (HistorySpaceConnection conn = historyDb.getConnection(null))
            {
                BIHistory history = conn.getHistory(cfg.getId());
                if (history == null)
                {
                    return BPermissions.DEFAULT;
                }
                return history.getPermissions(cx);
            }
        }
        else
        {
            return comp.getPermissions(cx);
        }
    }

    private static final String CANNOT_CONVERT = "Cannot convert ";
}

