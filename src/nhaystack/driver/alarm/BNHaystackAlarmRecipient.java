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
import nhaystack.*;
import nhaystack.driver.*;
import nhaystack.server.*;

public class BNHaystackAlarmRecipient
    extends BAlarmRecipient
{
    /*-
    class BNHaystackAlarmRecipient
    {
        properties
        {
            haystackServer: BOrd default{[ BOrd.DEFAULT ]}
        }
    }
   -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.alarm.BNHaystackAlarmRecipient(2355764136)1.0$ @*/
/* Generated Fri Mar 20 15:36:04 EDT 2015 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackAlarmRecipient.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void handleAlarm(BAlarmRecord alarm)
    {
        try
        {
            // look up the point
            BOrd sourceOrd = alarm.getSource().get(0);
            BComponent ext = (BComponent) sourceOrd.get(this, null);
            BControlPoint point = (BControlPoint) ext.getParent();

//System.out.println("handleAlarm: " + alarm + ", " + point.getSlotPath());

            // look up the ref
            BNHaystackService service = (BNHaystackService)
                Sys.getService(BNHaystackService.TYPE);
            NHRef id = service.getHaystackServer().getTagManager().makeComponentRef(point);

//            // build the tags.  we really only need this for curVal
//            HDict tags = service.getHaystackServer().getTagManager().createTags(point);

            // build the request
            HDictBuilder hdb = new HDictBuilder();
            hdb.add("sourceId",     id.getHRef().toZinc());
            hdb.add("alarmClass",   alarm.getAlarmClass());
            hdb.add("alarmUuid",    alarm.getUuid().encodeToString());
//            hdb.add("alarmValue",   tags.get("curVal"));
            hdb.add("priority",     alarm.getPriority());
            hdb.add("alarmText",    getAlarmFacet(alarm, BAlarmRecord.MSG_TEXT));
            hdb.add("instructions", getAlarmFacet(alarm, BAlarmRecord.INSTRUCTIONS));

            HGrid req = HGridBuilder.dictToGrid(hdb.toDict());

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
                        alarm.getSourceState()  + ", " + point.getSlotPath()); 
            }
        }
        catch (Exception e)
        {
            throw new BajaRuntimeException(e);
        }
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
