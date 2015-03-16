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
            haystackServers: BOrdList default{[ BOrdList.DEFAULT ]}
        }
    }
   -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.alarm.BNHaystackAlarmRecipient(3819306821)1.0$ @*/
/* Generated Mon Mar 09 15:46:06 EDT 2015 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
// Property "haystackServers"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystackServers</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#getHaystackServers
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#setHaystackServers
   */
  public static final Property haystackServers = newProperty(0, BOrdList.DEFAULT,null);
  
  /**
   * Get the <code>haystackServers</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#haystackServers
   */
  public BOrdList getHaystackServers() { return (BOrdList)get(haystackServers); }
  
  /**
   * Set the <code>haystackServers</code> property.
   * @see nhaystack.driver.alarm.BNHaystackAlarmRecipient#haystackServers
   */
  public void setHaystackServers(BOrdList v) { set(haystackServers,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackAlarmRecipient.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void handleAlarm(BAlarmRecord alarmRecord)
    {
System.out.println("handleAlarm: aaa " + alarmRecord);

        // look up the point
        BOrd sourceOrd = alarmRecord.getSource().get(0);
        BComponent ext = (BComponent) sourceOrd.get(this, null);
        BControlPoint point = (BControlPoint) ext.getParent();

        // look up the ref
        BNHaystackService service = (BNHaystackService)
            Sys.getService(BNHaystackService.TYPE);
        NHRef id = service.getHaystackServer().getTagManager().makeComponentRef(point);
        HGridBuilder gb = new HGridBuilder();
        HGrid req = HGridBuilder.dictToGrid(
            new HDictBuilder().add("id", id.getHRef()).toDict());

System.out.println("handleAlarm: bbb " + point.getSlotPath());
req.dump();

        // send an alarm to the server
        BNHaystackServer server = (BNHaystackServer) 
            getHaystackServer().get(this, null);
        HClient client = server.getHaystackClient();

        switch(alarmRecord.getSourceState().getOrdinal())
        {
            case BSourceState.FAULT:
                client.call("finToAlarm", req);
                break;
            case BSourceState.NORMAL:
                client.call("finToNormal", req);
                break;
            default:
                LOG.warning(
                    "Cannot process alarm source state " + 
                    alarmRecord.getSourceState()  + ", " + point.getSlotPath()); 
//    offnormal,
//    fault,
//    alert
        }
System.out.println("handleAlarm: ccc");
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack.driver");
}
