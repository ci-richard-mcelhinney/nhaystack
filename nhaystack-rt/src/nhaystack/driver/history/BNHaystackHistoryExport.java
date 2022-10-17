//
// Copyright (c) 2018, VRT Systems
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Apr 2018  Stuart Longland  Creation

package nhaystack.driver.history;

import java.util.logging.*;
import java.util.*;

import javax.baja.collection.*;
import javax.baja.driver.history.*;
import javax.baja.history.*;
import javax.baja.history.ext.*;
import javax.baja.history.db.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.util.*;
import javax.baja.control.*;
import javax.baja.naming.*;
import javax.baja.units.*;

import org.projecthaystack.*;
import org.projecthaystack.io.*;
import org.projecthaystack.client.*;

import nhaystack.*;
import nhaystack.res.*;
import nhaystack.driver.*;
import nhaystack.driver.point.learn.*;
import nhaystack.util.*;
import nhaystack.worker.*;

import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;

/**
 * BNHaystackHistoryExport transfers local Baja history data into a remote
 * Project Haystack server.
 */
@NiagaraType
@NiagaraProperty(
  name = "id",
  type = "BHRef",
  defaultValue = "BHRef.DEFAULT"
)
@NiagaraProperty(
  name = "tz",
  type = "BHTimeZone",
  defaultValue = "BHTimeZone.DEFAULT"
)
@NiagaraProperty(
  name = "uploadFromTime",
  type = "BAbsTime",
  defaultValue = "BAbsTime.DEFAULT"
)
@NiagaraProperty(
  name = "uploadSize",
  type = "int",
  defaultValue = "10000"
)
public class BNHaystackHistoryExport extends BHistoryExport
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.BNHaystackHistoryExport(2998859787)1.0$ @*/
/* Generated Tue Jul 24 17:57:28 AEST 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "id"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code id} property.
   * @see #getId
   * @see #setId
   */
  public static final Property id = newProperty(0, BHRef.DEFAULT, null);

  /**
   * Get the {@code id} property.
   * @see #id
   */
  public BHRef getId() { return (BHRef)get(id); }

  /**
   * Set the {@code id} property.
   * @see #id
   */
  public void setId(BHRef v) { set(id, v, null); }

////////////////////////////////////////////////////////////////
// Property "tz"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code tz} property.
   * @see #getTz
   * @see #setTz
   */
  public static final Property tz = newProperty(0, BHTimeZone.DEFAULT, null);

  /**
   * Get the {@code tz} property.
   * @see #tz
   */
  public BHTimeZone getTz() { return (BHTimeZone)get(tz); }

  /**
   * Set the {@code tz} property.
   * @see #tz
   */
  public void setTz(BHTimeZone v) { set(tz, v, null); }

////////////////////////////////////////////////////////////////
// Property "uploadFromTime"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code uploadFromTime} property.
   * @see #getUploadFromTime
   * @see #setUploadFromTime
   */
  public static final Property uploadFromTime = newProperty(0, BAbsTime.DEFAULT, null);

  /**
   * Get the {@code uploadFromTime} property.
   * @see #uploadFromTime
   */
  public BAbsTime getUploadFromTime() { return (BAbsTime)get(uploadFromTime); }

  /**
   * Set the {@code uploadFromTime} property.
   * @see #uploadFromTime
   */
  public void setUploadFromTime(BAbsTime v) { set(uploadFromTime, v, null); }

////////////////////////////////////////////////////////////////
// Property "uploadSize"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code uploadSize} property.
   * @see #getUploadSize
   * @see #setUploadSize
   */
  public static final Property uploadSize = newProperty(0, 10000, null);

  /**
   * Get the {@code uploadSize} property.
   * @see #uploadSize
   */
  public int getUploadSize() { return getInt(uploadSize); }

  /**
   * Set the {@code uploadSize} property.
   * @see #uploadSize
   */
  public void setUploadSize(int v) { setInt(uploadSize, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackHistoryExport.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
  protected final IFuture postExecute(Action action, BValue value, Context cx)
  {
    if(!isRunning()) return null;

    String choreName = "HistoryExportExecute:" + getId();
    BNHaystackServer server = server();

    try
    {
      if (!server.isRunning())
        throw new BajaRuntimeException("server not running.");

      if (!server.getEnabled())
        throw new BajaRuntimeException("server disabled.");

      if (server.getNetwork().isDisabled())
        throw new BajaRuntimeException("network disabled.");

      server.getWorker().enqueueChore(
          new DescriptorInvocation(
            server.getWorker(),
            choreName,
            this,
            new Invocation(this, action, value, cx)));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return null;
  }

  private HRef locatePoint(HClient client)
  {
    BHistoryId id = getHistoryId();
    StringBuilder pointFilter = new StringBuilder();
    pointFilter.append("point and axStation==");
    pointFilter.append(HStr.make(Sys.getStation().getStationName()).toZinc());
    pointFilter.append(" and axHistoryId==");
    pointFilter.append(HStr.make(id.encodeToString()).toZinc());

    LOG.info("Searching for point for history "
        + id + ": " + pointFilter.toString());
    try
    {
      HDict res = client.read(pointFilter.toString());
      HRef hsId = res.getRef("id");
      LOG.info("Existing entity for " + id + " is " + hsId);
      return hsId;
    }
    catch (UnknownRecException e)
    {
      LOG.info("No point found for history " + id);
      return null;
    }
  }

  private HRef createPoint(HClient client, BIHistory history)
  {
    HRef hsId = null;
    BHistoryId id = getHistoryId();
    HTimeZone tz = getTz().getTimeZone();
    BHistoryConfig cfg = history.getConfig();
    Type recType = cfg.getRecordType().getResolvedType();

    String pointName = null;
    String pointDis = null;
    String pointKind = null;
    String pointEnum = null;
    String pointUnit = null;
    String pointPath = null;

    if (recType.is(BNumericTrendRecord.TYPE))
    {
      pointKind = KIND_NUMBER;
    }
    else if (recType.is(BBooleanTrendRecord.TYPE))
    {
      pointKind = KIND_BOOL;
    }
    else
    {
      pointKind = KIND_STR;
    }

    // We can't find things like units from a BHistoryConfig (or at
    // least, the method is not obvious).  The following expects that
    // the history is sourced from a BHistoryExt which is the child of
    // a BControlPoint.
    try
    {
      // Retrieve the history extension
      BOrdList histExtOrdList = cfg.getSource();
      if (histExtOrdList.size() != 1)
      {
        throw new IllegalArgumentException(
            "History " + id + " has an unexpected number of sources: "
            + histExtOrdList);
      }

      BOrd histExtOrd = histExtOrdList.get(0);
      if (LOG.isLoggable(Level.FINE))
        LOG.fine("History " + id + " is sourced from " + histExtOrd);

      // Expect a ClassCastException if this doesn't map to a history extension.
      BHistoryExt histExt = (BHistoryExt)histExtOrd.get();

      // The parent should be a control point
      BControlPoint point = (BControlPoint)histExt.getParent();

      // Use the point's name and display text
      pointName = point.getNavName();
      pointDis = point.getNavDisplayName(Context.NULL);
      pointPath = point.getSlotPath().toString();

      // Retrieve the facets
      BFacets pointFacets = point.getFacets();
      if (point instanceof BNumericPoint)
      {
        pointKind = KIND_NUMBER;

        // If there's a UNITS facet, add that as "unit"
        BObject unitObj = pointFacets.get(BFacets.UNITS);
        if (unitObj instanceof BUnit)
        {
          Unit hsUnit = Resources.fromBajaUnit((BUnit)unitObj);
          if (hsUnit != null)
          {
            pointUnit = hsUnit.symbol;
          }
        }
      }
      else if (point instanceof BEnumPoint)
      {
        // This is an enum, so figure out the range of values and
        // make a list.
        List<String> enumList = new LinkedList<String>();
        BFacets facets = (BFacets) cfg.get("valueFacets");
        BEnumRange er = (BEnumRange) facets.get("range");

        // YEEUUCCHHH!!!  Horrible interface!
        int[] ordinals = er.getOrdinals();
        for (int i = 0; i < ordinals.length; i++)
        {
          enumList.add(SlotUtil.fromNiagara(er.getTag(ordinals[i])));
        }

        if (enumList.size() > 0)
          pointEnum = String.join(",", enumList);
      }
    }
    catch (Exception e)
    {
      // Tell the user in case they wonder where their units went!
      LOG.log(Level.WARNING,
          "Facet retrieval failed for "
          + id + ", creating point without unit or enum detail.",
          e);
    }

    if (pointName == null)
      pointName = history.getNavName();

    if (pointName == null)
      throw new IllegalArgumentException(
          "History " + id + " has no name");

    // The navName can have escape sequences like $, replace with ~
    pointName = pointName.replaceAll("[^a-zA-Z0-9_]","_");

    if (pointDis == null)
      pointDis = history.getNavDisplayName(Context.NULL);

    // If this still returns null, then use the name
    if ((pointDis == null) || (pointDis.isEmpty()))
      pointDis = pointName;

    // See if it exists already
    hsId = locatePoint(client);
    if (hsId != null)
    {
      // It does, stop here and return that.
      return hsId;
    }

    // None found, can we create one?
    if (!server().isCrudSupported())
    {
      // Nope, so bail out here.
      throw new IllegalArgumentException(
          "Export for " + id + " does not define a point, no matching "
          + " point exists (and the server does not support CRUD)");
    }

    // Create a new entity with the following tags:
    // - name: pointName
    // - dis: pointDis
    // - point: marker
    // - his: marker
    // - kind: figure it out from the source class type
    //    NumericWritable → Number
    //    BooleanWritable → Bool
    //    StringWritable → Str
    //    EnumWritable → Str; with enum tag
    // - tz: history TZ
    // - unit: if known and numeric
    // - enum: if an enum, the values
    // - axStation: name of the station this driver is running on
    // - axSlotPath: the ORD of the source, if known
    // - axHistoryId: the history ID
    HDictBuilder newRec = new HDictBuilder();
    // Mandatory fields
    newRec.add("name", pointName);
    newRec.add("dis", pointDis);
    newRec.add("point", HMarker.VAL);
    newRec.add("his", HMarker.VAL);
    newRec.add("kind", pointKind);
    newRec.add("tz", tz.name);
    newRec.add("axStation", Sys.getStation().getStationName());
    newRec.add("axHistoryId", id.encodeToString());

    if (pointPath != null)
      newRec.add("axSlotPath", pointPath);
    if (pointEnum != null)
      newRec.add("enum", pointEnum);
    if (pointUnit != null)
      newRec.add("unit", pointUnit);

    HGrid newRecRq = HGridBuilder.dictToGrid(newRec.toDict());
    if (LOG.isLoggable(Level.FINE))
      LOG.fine("New record request: " + HZincWriter.gridToString(newRecRq));
    HGrid newRecRes = client.call("createRec", newRecRq);
    if (LOG.isLoggable(Level.FINE))
      LOG.fine("New record response: " + HZincWriter.gridToString(newRecRes));

    // There should be one single row
    if (newRecRes.numRows() == 0)
    {
      throw new IllegalStateException("No records were created");
    }

    HRow createdRec = newRecRes.row(0);
    hsId = createdRec.getRef("id");
    LOG.info("New entity for " + id + " is " + hsId);
    return hsId;
  }

  public final void doExecute()
  {
    executeInProgress();
    BAbsTime lastSuccessTime = null;
    BHistoryId id = getHistoryId();

    try
    {
      // set up config
      BHistoryService service = (BHistoryService)Sys.getService(BHistoryService.TYPE);
      BHistoryDatabase db = service.getDatabase();
      HRef hsId = getId().getRef();

      HClient client = server().getHaystackClient();
      HTimeZone tz = getTz().getTimeZone();

      if (LOG.isLoggable(Level.FINE))
        LOG.fine("historyExport.doExecute begin " + id);

      // NOTE: be careful, timeQuery() is inclusive of both start and end
      try (HistorySpaceConnection conn = db.getConnection(null))
      {
        BIHistory history = conn.getHistory(id);

        // Do we have a point defined?
        if (hsId == null)
        {
          // No, figure out what the point's tags need to be.
          LOG.info("No point for history " + id);
          hsId = createPoint(client, history);

          // If we don't have an ID by now, then bail out.
          if (hsId == null)
          {
            throw new IllegalArgumentException(
                "Export for " + id + " does not define a point.");
          }
          else
          {
            // Save it for next time.
            setId(BHRef.make(hsId));
          }

          // Stop here.  Some implementations of Haystack (e.g. WideSky)
          // require some time after creating an entity to re-load
          // authorisation caches (or otherwise, the request will be refused).
          // This should have been completed by the next time we're scheduled
          // to push data.
          if (LOG.isLoggable(Level.FINE))
            LOG.fine("historyExport.doExecute end " + id
                + ": entity created, will commence upload on next run.");
        }
        else
        {
          // The point exists in the destination Haystack server, so we can
          // commence with shovelling the data into its new home.
          BHistoryConfig cfg = history.getConfig();
          BTypeSpec recTypeSpec = cfg.getRecordType();
          Type recType = recTypeSpec.getResolvedType();

          // Range start: our upload start time.
          BAbsTime rangeStart = getUploadFromTime();
          // Range end: our last recorded timestamp.
          BAbsTime rangeEnd = conn.getLastTimestamp(history);
          BITable table = (BITable) conn.timeQuery(history, rangeStart, rangeEnd);

          // Count the number of items uploaded.
          long itemCount = 0;

          // Gather items to be exported
          Deque<HHisItem> itemQueue = new LinkedList<HHisItem>();

          // Make a note of the previous row timestamp
          long prevMillis = 0;

          try (TableCursor cursor = table.cursor())
          {
            // iterate over results and extract HHisItem's
            while (cursor.next())
            {
              BHistoryRecord hrec = (BHistoryRecord) cursor.get();
              BAbsTime timestamp = (BAbsTime) hrec.get("timestamp");

              // Extract the timestamp in milliseconds
              long millis = timestamp.getMillis();

              // Kludge handling of identical timestamps.  Trend records should
              // never trigger this code, but (Security)AuditRecord and LogRecord
              // may.
              if (millis <= prevMillis)
              {
                millis = prevMillis + 1;
              }

              // extract the timestamp, declare a HVal for value storage
              HVal val = null;

              switch (recTypeSpec.toString()) // "module:type"
              {
                case "history:AuditRecord":
                case "history:LogRecord":
                  // serialise the entire hrec
                  val = HStr.make(hrec.toString());
                  break;
                case "history:SecurityAuditRecord":
                  // security record does not have a meaningful .toString()
                  // it just gives us the timestamp (which we already have)
                  Property[] recProps = hrec.getPropertiesArray();
                  StringBuilder recStr = new StringBuilder();
                  int i;
                  boolean firstEmitted = false;
                  for (i = 0; i < recProps.length; i++)
                  {
                    String prop = recProps[i].getName();
                    if (prop == "timestamp")
                    {
                      // we'll handle this separately
                      continue;
                    }

                    if (firstEmitted)
                    {
                      recStr.append("; ");
                    }
                    recStr.append(prop);
                    recStr.append(": ");
                    recStr.append(hrec.get(prop).toString());
                    firstEmitted = true;
                  }
                  val = HStr.make(recStr.toString());
                  break;
                default:
                  // extract value from BTrendRecord
                  BValue value = hrec.get("value");
                  if (value != null)
                  {
                    if (recType.is(BNumericTrendRecord.TYPE))
                    {
                      BNumber num = (BNumber) value;
                      val = HNum.make(num.getDouble());
                    }
                    else if (recType.is(BBooleanTrendRecord.TYPE))
                    {
                      BBoolean bool = (BBoolean) value;
                      val = HBool.make(bool.getBoolean());
                    }
                    else if (recType.is(BEnumTrendRecord.TYPE))
                    {
                      BDynamicEnum dyn = (BDynamicEnum) value;
                      BFacets facets = (BFacets) cfg.get("valueFacets");
                      BEnumRange er = (BEnumRange) facets.get("range");
                      val = HStr.make(SlotUtil.fromNiagara(er.getTag(dyn.getOrdinal())));
                    }
                    else
                    {
                      val = HStr.make(value.toString());
                    }
                  }
              }

              if (val != null)
              {
                HDateTime ts = HDateTime.make(millis, tz);
                itemQueue.addLast(HHisItem.make(ts, val));
                prevMillis = millis;
              }
            }
          }

          while (!itemQueue.isEmpty())
          {
            // Convert the first uploadSize elements of itemQueue to an array.
            HHisItem[] hisItems = new HHisItem[
              Math.min(itemQueue.size(), getUploadSize())
            ];
            for (int i = 0; (i < hisItems.length) && (!itemQueue.isEmpty()); i++)
            {
              hisItems[i] = itemQueue.removeFirst();
            }

            // Issue the write request
            if (LOG.isLoggable(Level.FINE))
              LOG.fine("Uploading " + hisItems.length + " records for " + id
                  + " from " + hisItems[0].ts + " to "
                  + hisItems[hisItems.length-1].ts);

            try
            {
              client.hisWrite(hsId, hisItems);
            }
            catch (CallNetworkException e)
            {
              // How to bugger up someone else's error handling in one easy
              // step:  Uselessly wrap the original exception in another
              // exception.

              Throwable cause = e.getCause();
              if (cause instanceof CallHttpException)
              {
                CallHttpException httpError = (CallHttpException)cause;

                if (httpError.code == 413)
                {
                  // This is a "payload too big" error.  Try halving the size next
                  // time.  We'll still fail this attempt though so the user is
                  // alerted to the fact there was a problem.  (And it means we'll
                  // retry the samples that were de-queued.)
                  setUploadSize(Math.max(1, hisItems.length / 2));
                  LOG.log(Level.WARNING,
                      "Server reports "
                      + hisItems.length
                      + " is too many for it to handle.  Upload size now set to "
                      + getUploadSize(), httpError);
                }
                else
                {
                  LOG.log(Level.WARNING,
                      "Server reports unhandled HTTP error code " + httpError.code, httpError);
                }
              }
              throw e;
            }

            itemCount += hisItems.length;

            // Make a note of when we last were successful.
            lastSuccessTime = BAbsTime.make(
                hisItems[hisItems.length-1].ts.millis());
          }

          if (LOG.isLoggable(Level.FINE))
            LOG.fine("historyExport.doExecute end " + id
                + ": exported " + itemCount + " rows.");
        }
      }

      executeOk();
    }
    catch (Exception e)
    {
      LOG.fine("historyExport.doExecute fail " + id);
      e.printStackTrace();
      executeFail(e.getMessage());
    }

    try
    {
      if (lastSuccessTime != null)
      {
        // Set the uploadFromTime to the predicted time of the next sample.
        // This should be at least one millisecond more than now.
        setUploadFromTime(lastSuccessTime.add(BRelTime.make(1)));
      }
    }
    catch (Exception e)
    {
      LOG.log(Level.WARNING, "Failed to update upload start time", e);
    }
  }

  private BNHaystackServer server()
  {
    BComplex comp = getParent();
    while ((comp != null) && (!(comp instanceof BNHaystackServer)))
      comp = comp.getParent();
    return (BNHaystackServer) comp;
  }

  private static final Logger LOG = Logger.getLogger("nhaystack.driver");

  /* The valid kind values for a 'point' */
  public static String KIND_NUMBER  = "Number";
  public static String KIND_BOOL    = "Bool";
  public static String KIND_STR     = "Str";
}
