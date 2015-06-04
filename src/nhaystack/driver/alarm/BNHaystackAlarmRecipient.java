//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   26 Feb 2015  Mike Jarmy  Creation

package nhaystack.driver.alarm;

import java.util.*;

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
            miscAlarmId: String default{[ "" ]}
            haystackConnRef: String default{[ "" ]}
        }
    }
   -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.alarm.BNHaystackAlarmRecipient(3640000689)1.0$ @*/
/* Generated Thu Apr 30 08:35:05 EDT 2015 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
// Property "miscAlarmId"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>miscAlarmId</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#getMiscAlarmId
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#setMiscAlarmId
   */
  public static final Property miscAlarmId = newProperty(0, "",null);
  
  /**
   * Get the <code>miscAlarmId</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#miscAlarmId
   */
  public String getMiscAlarmId() { return getString(miscAlarmId); }
  
  /**
   * Set the <code>miscAlarmId</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#miscAlarmId
   */
  public void setMiscAlarmId(String v) { setString(miscAlarmId,v,null); }

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
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackAlarmRecipient.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void handleAlarm(BAlarmRecord alarm)
    {
        try
        {
System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            // look up the point
            BOrd sourceOrd = alarm.getSource().get(0);
            BComponent ext = (BComponent) sourceOrd.get(this, null);
            BComponent parent = (BComponent) ext.getParent();
            String alarmName = makeAlarmName(ext, alarm);

            if (LOG.isTraceOn())
                LOG.trace("handleAlarm: " + alarm + ", " + ext.getSlotPath());

            // create request either for a point, or for "miscellaneous"
            HGrid req = (parent instanceof BControlPoint) ?
                createPointAlarmRequest(alarm, (BControlPoint) parent, alarmName) :
                createMiscAlarmRequest(alarm, parent, alarmName);

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
        catch (Exception e)
        {
            throw new BajaRuntimeException(e);
        }
    }

    private String makeAlarmName(BComponent ext, BAlarmRecord alarm)
    {
        BFormat fmt = (BFormat) ext.get("sourceName");

        // if the extension doesn't have a sourceName, use the alarm class
        String name = (fmt == null) ?
            alarm.getAlarmClass() : 
            fmt.format(ext);

        // get rid of spaces and such
        name = TextUtil.replace(name, " ", "_");
        name = SlotUtil.fromNiagara(SlotPath.escape(name));

        return name;
    }

    private HGrid createPointAlarmRequest(BAlarmRecord alarm, BControlPoint point, String alarmName)
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
        hdb.add("alarmText",       getAlarmFacet(alarm, BAlarmRecord.MSG_TEXT));
        hdb.add("instructions",    getAlarmFacet(alarm, BAlarmRecord.INSTRUCTIONS));
        hdb.add("haystackConnRef", (new HZincReader(getHaystackConnRef())).readScalar());

        return HGridBuilder.dictsToGrid(
            new HDict[] { hdb.toDict(), fetchAlarmClassTags(alarm) });
    }

    private HGrid createMiscAlarmRequest(BAlarmRecord alarm, BComponent comp, String alarmName)
    throws Exception
    {
        // build the request
        HDictBuilder hdb = new HDictBuilder();
        hdb.add("isMisc",   HBool.TRUE);
        hdb.add("sourceId", getMiscAlarmId());

        hdb.add("alarmName",       alarmName);
        hdb.add("alarmUuid",       alarm.getUuid().encodeToString());
        hdb.add("priority",        alarm.getPriority());
        hdb.add("alarmText",       getAlarmFacet(alarm, BAlarmRecord.MSG_TEXT));
        hdb.add("instructions",    getAlarmFacet(alarm, BAlarmRecord.INSTRUCTIONS));
        hdb.add("haystackConnRef", (new HZincReader(getHaystackConnRef())).readScalar());

        return HGridBuilder.dictsToGrid(
            new HDict[] { hdb.toDict(), fetchAlarmClassTags(alarm) });
    }

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

    private String getAlarmFacet(BAlarmRecord alarm, String facetName)
    {
        BObject obj = alarm.getAlarmData().get(facetName);
        return (obj == null) ? "" : obj.toString();
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack.driver");
}
