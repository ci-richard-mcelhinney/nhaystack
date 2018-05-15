//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   26 Feb 2015  Mike Jarmy     Creation
//   07 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations,
//                               replaced deprecated getScalar with getVal, added use of generics

package nhaystack.driver.alarm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.alarm.BAlarmRecipient;
import javax.baja.alarm.BAlarmRecord;
import javax.baja.alarm.BAlarmService;
import javax.baja.alarm.BSourceState;
import javax.baja.control.BControlPoint;
import javax.baja.naming.BOrd;
import javax.baja.naming.SlotPath;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BFormat;
import javax.baja.util.BUuid;
import nhaystack.BHDict;
import nhaystack.NHRef;
import nhaystack.driver.BNHaystackServer;
import nhaystack.server.BNHaystackService;
import nhaystack.util.SlotUtil;
import org.projecthaystack.HBool;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.client.HClient;
import org.projecthaystack.io.HZincReader;

@NiagaraType
@NiagaraProperty(
  name = "haystackServer",
  type = "BOrd",
  defaultValue = "BOrd.DEFAULT"
)
@NiagaraProperty(
  name = "miscAlarmRef",
  type = "String",
  defaultValue = ""
)
@NiagaraProperty(
  name = "haystackConnRef",
  type = "String",
  defaultValue = ""
)
public class BNHaystackAlarmRecipient
    extends BAlarmRecipient
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.alarm.BNHaystackAlarmRecipient(689562049)1.0$ @*/
/* Generated Fri Nov 17 11:43:50 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "haystackServer"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code haystackServer} property.
   * @see #getHaystackServer
   * @see #setHaystackServer
   */
  public static final Property haystackServer = newProperty(0, BOrd.DEFAULT, null);
  
  /**
   * Get the {@code haystackServer} property.
   * @see #haystackServer
   */
  public BOrd getHaystackServer() { return (BOrd)get(haystackServer); }
  
  /**
   * Set the {@code haystackServer} property.
   * @see #haystackServer
   */
  public void setHaystackServer(BOrd v) { set(haystackServer, v, null); }

////////////////////////////////////////////////////////////////
// Property "miscAlarmRef"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code miscAlarmRef} property.
   * @see #getMiscAlarmRef
   * @see #setMiscAlarmRef
   */
  public static final Property miscAlarmRef = newProperty(0, "", null);
  
  /**
   * Get the {@code miscAlarmRef} property.
   * @see #miscAlarmRef
   */
  public String getMiscAlarmRef() { return getString(miscAlarmRef); }
  
  /**
   * Set the {@code miscAlarmRef} property.
   * @see #miscAlarmRef
   */
  public void setMiscAlarmRef(String v) { setString(miscAlarmRef, v, null); }

////////////////////////////////////////////////////////////////
// Property "haystackConnRef"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code haystackConnRef} property.
   * @see #getHaystackConnRef
   * @see #setHaystackConnRef
   */
  public static final Property haystackConnRef = newProperty(0, "", null);
  
  /**
   * Get the {@code haystackConnRef} property.
   * @see #haystackConnRef
   */
  public String getHaystackConnRef() { return getString(haystackConnRef); }
  
  /**
   * Set the {@code haystackConnRef} property.
   * @see #haystackConnRef
   */
  public void setHaystackConnRef(String v) { setString(haystackConnRef, v, null); }

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
                if (LOG.isLoggable(Level.FINE))
                    LOG.fine("handleAlarm: ignoring " + alarm.getUuid());
                endIgnore(alarm.getUuid());
            }
            else
            {
                // look up the point
                BOrd sourceOrd = alarm.getSource().get(0);
                BComponent ext = (BComponent) sourceOrd.get(this, null);
                BComponent parent = (BComponent) ext.getParent();

                if (LOG.isLoggable(Level.FINE))
                    LOG.fine("handleAlarm: " + alarm.getUuid() + ", " + alarm + ", " + ext.getSlotPath());
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
    private static String getAlarmFacetValue(BAlarmRecord alarm, String key)
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
        hdb.add("haystackConnRef", new HZincReader(getHaystackConnRef()).readVal());

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
        hdb.add("haystackConnRef", new HZincReader(getHaystackConnRef()).readVal());

        return HGridBuilder.dictsToGrid(
            new HDict[] { hdb.toDict(), fetchAlarmClassTags(alarm) });
    }

    /**
      * fetchAlarmClassTags
      */
    private static HDict fetchAlarmClassTags(BAlarmRecord alarm)
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
    private static void dumpAlarmRecord(BAlarmRecord alarm)
    {
        System.out.println("--------------------------------------");
        BFacets facets = alarm.getAlarmData();
        String[] keys = facets.list();
        for (String key : keys)
            System.out.println(key + ", " + getAlarmFacetValue(alarm, key));
    }

////////////////////////////////////////////////////////////////
// ignore
////////////////////////////////////////////////////////////////

    /**
      * Start ignoring calls to handleAlarm().
      */
    public void beginIgnore(BUuid alarmId)
    {
        ignore.put(alarmId, false);
    }

    /**
      * Return whether or not calls to handleAlarm should be ignored.
      */
    private static boolean isIgnore(BUuid alarmId)
    {
        return ignore.containsKey(alarmId);
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

    private static final ConcurrentHashMap<BUuid, Boolean> ignore = new ConcurrentHashMap<>();

    private static final Logger LOG = Logger.getLogger("nhaystack.driverAlarm");
}
