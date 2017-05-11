//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   26 Feb 2015  Mike Jarmy  Creation

package nhaystack.driver.alarm;

import java.util.concurrent.*;

import javax.baja.alarm.*;
import javax.baja.control.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;
import org.projecthaystack.io.*;
import nhaystack.*;
import nhaystack.driver.*;
import nhaystack.server.*;
import nhaystack.util.*;

public class BNHaystackAlarmRecipient
    extends BAlarmRecipient
{
    /*-
    class BNHaystackAlarmRecipient
    {
        properties
        {
            haystackServer: BOrd default{[ BOrd.DEFAULT ]}
            miscAlarmRef: String default{[ "" ]}
            haystackConnRef: String default{[ "" ]}
        }
    }
   -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.alarm.BNHaystackAlarmRecipient(1587153237)1.0$ @*/
/* Generated Fri Jun 12 08:14:38 EDT 2015 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "haystackServer"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystackServer</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#getHaystackServer
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#setHaystackServer
   */
  public static final Property haystackServer = newProperty(0, BOrd.DEFAULT,null);
  
  /**
   * Get the <code>haystackServer</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#haystackServer
   */
  public BOrd getHaystackServer() { return (BOrd)get(haystackServer); }
  
  /**
   * Set the <code>haystackServer</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#haystackServer
   */
  public void setHaystackServer(BOrd v) { set(haystackServer,v,null); }

////////////////////////////////////////////////////////////////
// Property "miscAlarmRef"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>miscAlarmRef</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#getMiscAlarmRef
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#setMiscAlarmRef
   */
  public static final Property miscAlarmRef = newProperty(0, "",null);
  
  /**
   * Get the <code>miscAlarmRef</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#miscAlarmRef
   */
  public String getMiscAlarmRef() { return getString(miscAlarmRef); }
  
  /**
   * Set the <code>miscAlarmRef</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#miscAlarmRef
   */
  public void setMiscAlarmRef(String v) { setString(miscAlarmRef,v,null); }

////////////////////////////////////////////////////////////////
// Property "haystackConnRef"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystackConnRef</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#getHaystackConnRef
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#setHaystackConnRef
   */
  public static final Property haystackConnRef = newProperty(0, "",null);
  
  /**
   * Get the <code>haystackConnRef</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#haystackConnRef
   */
  public String getHaystackConnRef() { return getString(haystackConnRef); }
  
  /**
   * Set the <code>haystackConnRef</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#haystackConnRef
   */
  public void setHaystackConnRef(String v) { setString(haystackConnRef,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackAlarmRecipient.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
      * handleAlarm
      */
    @Override
    public void handleAlarm(BAlarmRecord alarm)
    {
        try
        {
            if (isIgnore(alarm.getUuid()))
            {
                if (LOG.isTraceOn())
                    LOG.trace("handleAlarm: ignoring " + alarm.getUuid());
                endIgnore(alarm.getUuid());
            }
            else
            {
                // look up the point
                BOrd sourceOrd = alarm.getSource().get(0);
                BComponent ext = (BComponent) sourceOrd.get(this, null);
                BComponent parent = (BComponent) ext.getParent();

                if (LOG.isTraceOn())
                    LOG.trace("handleAlarm: " + alarm.getUuid() + ", " + alarm + ", " + ext.getSlotPath());
//dumpAlarmRecord(alarm);

                // create alarm name
                String alarmName = getAlarmFacetValue(alarm, BAlarmRecord.SOURCE_NAME);
                alarmName = alarmName.replace(' ', '_');
                alarmName = SlotUtil.fromNiagara(SlotPath.escape(alarmName));

                // create request either for a point, or for "miscellaneous"
                HGrid req = (parent instanceof BControlPoint) ?
                    createPointAlarmRequest(alarm, (BControlPoint) parent, ext, alarmName) :
                    createMiscAlarmRequest(alarm, ext, alarmName);

                // send an alarm to the server
                BNHaystackServer server = (BNHaystackServer) 
                    getHaystackServer().get(this, null);
                HClient client = server.getHaystackClient();

                switch(alarm.getSourceState().getOrdinal())
                {
                    case BSourceState.OFFNORMAL:
                    case BSourceState.FAULT:
                        client.call("finToAlarm", req);
                        break;
                    case BSourceState.NORMAL:
                        client.call("finToNormal", req);
                        break;
                    default:
                        LOG.warning(
                            "Cannot process alarm source state " + 
                            alarm.getSourceState()  + ", " + parent.getSlotPath()); 
                }
            }

        }
        catch (Exception e)
        {
            throw new BajaRuntimeException(e);
        }
    }

    /**
      * getAlarmFacetValue
      */
    private String getAlarmFacetValue(BAlarmRecord alarm, String key)
    {
        if (key.equals("sourceOrd"))
            return alarm.getSource().toString();

        BFacets facets = alarm.getAlarmData();
        Object value = facets.get(key);
        if (value == null) return "";
        return BFormat.make(value.toString()).format(alarm).toString();
    }

    /**
      * createPointAlarmRequest
      */
    private HGrid createPointAlarmRequest(
        BAlarmRecord alarm, 
        BControlPoint point, 
        BComponent ext,
        String alarmName)
    throws Exception
    {
        // look up the ref
        BNHaystackService service = (BNHaystackService) Sys.getService(BNHaystackService.TYPE);
        NHRef id = service.getHaystackServer().getTagManager().makeComponentRef(point);

        // build the request
        HDictBuilder hdb = new HDictBuilder();
        hdb.add("isMisc",   HBool.FALSE);
        hdb.add("sourceId", id.getHRef().toZinc());

        hdb.add("alarmName",       alarmName);
        hdb.add("alarmUuid",       alarm.getUuid().encodeToString());
        hdb.add("priority",        alarm.getPriority());
        hdb.add("alarmText",       getAlarmFacetValue(alarm, BAlarmRecord.MSG_TEXT));
        hdb.add("instructions",    getAlarmFacetValue(alarm, BAlarmRecord.INSTRUCTIONS));
        hdb.add("haystackConnRef", (new HZincReader(getHaystackConnRef())).readScalar());

        return HGridBuilder.dictsToGrid(
            new HDict[] { hdb.toDict(), fetchAlarmClassTags(alarm) });
    }

    /**
      * createMiscAlarmRequest
      */
    private HGrid createMiscAlarmRequest(
        BAlarmRecord alarm, 
        BComponent ext,
        String alarmName)
    throws Exception
    {
        // build the request
        HDictBuilder hdb = new HDictBuilder();
        hdb.add("isMisc",   HBool.TRUE);
        hdb.add("sourceId", getMiscAlarmRef());

        hdb.add("alarmName",       alarmName);
        hdb.add("alarmUuid",       alarm.getUuid().encodeToString());
        hdb.add("priority",        alarm.getPriority());
        hdb.add("alarmText",       getAlarmFacetValue(alarm, BAlarmRecord.MSG_TEXT));
        hdb.add("instructions",    getAlarmFacetValue(alarm, BAlarmRecord.INSTRUCTIONS));
        hdb.add("haystackConnRef", (new HZincReader(getHaystackConnRef())).readScalar());

        return HGridBuilder.dictsToGrid(
            new HDict[] { hdb.toDict(), fetchAlarmClassTags(alarm) });
    }

    /**
      * fetchAlarmClassTags
      */
    private HDict fetchAlarmClassTags(BAlarmRecord alarm)
    {
        // look up the extra tags on the alarm class, if any
        BAlarmService alarmService = (BAlarmService) Sys.getService(BAlarmService.TYPE);
        HDict extraTags = BHDict.findTagAnnotation(
            alarmService.lookupAlarmClass(
                alarm.getAlarmClass()));
        if (extraTags == null)
            extraTags = HDict.EMPTY;

        return extraTags;
    }

////////////////////////////////////////////////////////////////
// debug
////////////////////////////////////////////////////////////////

    /**
      * dumpAlarmRecord
      */
    private void dumpAlarmRecord(BAlarmRecord alarm)
    {
        System.out.println("--------------------------------------");
        BFacets facets = alarm.getAlarmData();
        String[] keys = facets.list();
        for (int i = 0; i < keys.length; i++)
            System.out.println(keys[i] + ", " + getAlarmFacetValue(alarm, keys[i]));
    }

////////////////////////////////////////////////////////////////
// ignore
////////////////////////////////////////////////////////////////

    /**
      * Start ignoring calls to handleAlarm().
      */
    public void beginIgnore(BUuid alarmId)
    {
        ignore.put(alarmId, alarmId);
    }

    /**
      * Return whether or not calls to handleAlarm should be ignored.
      */
    private static boolean isIgnore(BUuid alarmId)
    {
        boolean result = ignore.containsKey(alarmId);
        return result;
    }

    /**
      * Stop ignoring calls to handleAlarm().
      */
    private static void endIgnore(BUuid alarmId)
    {
        ignore.remove(alarmId);
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final ConcurrentHashMap ignore = new ConcurrentHashMap();

    private static final Log LOG = Log.getLog("nhaystack.driverAlarm");
}
