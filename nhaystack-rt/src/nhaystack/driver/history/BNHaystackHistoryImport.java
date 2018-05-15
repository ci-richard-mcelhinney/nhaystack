//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   07 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.driver.history;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.driver.history.BHistoryImport;
import javax.baja.history.BBooleanTrendRecord;
import javax.baja.history.BCapacity;
import javax.baja.history.BHistoryConfig;
import javax.baja.history.BHistoryId;
import javax.baja.history.BHistoryService;
import javax.baja.history.BIHistory;
import javax.baja.history.BNumericTrendRecord;
import javax.baja.history.BTrendRecord;
import javax.baja.history.HistorySpaceConnection;
import javax.baja.history.db.BHistoryDatabase;
import javax.baja.history.db.HistoryDatabaseConnection;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatus;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BComplex;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BValue;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BTypeSpec;
import javax.baja.util.IFuture;
import javax.baja.util.Invocation;
import nhaystack.BHRef;
import nhaystack.driver.BHTags;
import nhaystack.driver.BNHaystackServer;
import nhaystack.driver.point.learn.BNHaystackLearnPointsJob;
import nhaystack.util.TypeUtil;
import org.projecthaystack.HBool;
import org.projecthaystack.HDateTime;
import org.projecthaystack.HDateTimeRange;
import org.projecthaystack.HDict;
import org.projecthaystack.HGrid;
import org.projecthaystack.HNum;
import org.projecthaystack.HRow;
import org.projecthaystack.HTimeZone;
import org.projecthaystack.HVal;
import org.projecthaystack.client.HClient;

/**
  * BNHaystackHistoryImport transfers remote haystack history data
  * into a local Baja history.
  */

@NiagaraType
@NiagaraProperty(
  name = "id",
  type = "BHRef",
  defaultValue = "BHRef.DEFAULT"
)
@NiagaraProperty(
  name = "importedTags",
  type = "BHTags",
  defaultValue = "BHTags.DEFAULT",
  flags = Flags.READONLY
)
public class BNHaystackHistoryImport extends BHistoryImport
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.BNHaystackHistoryImport(187490102)1.0$ @*/
/* Generated Fri Nov 17 11:50:35 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

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
// Property "importedTags"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code importedTags} property.
   * @see #getImportedTags
   * @see #setImportedTags
   */
  public static final Property importedTags = newProperty(Flags.READONLY, BHTags.DEFAULT, null);
  
  /**
   * Get the {@code importedTags} property.
   * @see #importedTags
   */
  public BHTags getImportedTags() { return (BHTags)get(importedTags); }
  
  /**
   * Set the {@code importedTags} property.
   * @see #importedTags
   */
  public void setImportedTags(BHTags v) { set(importedTags, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackHistoryImport.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    protected final IFuture postExecute(Action action, BValue value, Context cx)
    {
        if(!isRunning()) return null;

        String choreName = "HistoryImportExecute:" + getId();
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

    @Override
    public final void doExecute()
    {
        executeInProgress();
        try
        {
            // set units
            HDict tags = getImportedTags().getDict();
            BFacets facets = BNHaystackLearnPointsJob.makeNumberFacets(tags);

            if (!facets.equals(BFacets.NULL))
            {
                BComponent override = getConfigOverrides();
                if (override.get("valueFacets") == null)
                    override.add("valueFacets", facets);
                else
                    override.set("valueFacets", facets);
            }

            // set up config
            BHistoryService service = (BHistoryService)Sys.getService(BHistoryService.TYPE);
            BHistoryDatabase db = service.getDatabase();
            BHistoryId id = getHistoryId();

            if (LOG.isLoggable(Level.FINE))
                LOG.fine("historyImport.doExecute begin " + id);

            // make sure history exists
            BHistoryConfig localCfg = makeLocalConfig(createConfig());
            try (HistoryDatabaseConnection dbConn = db.getDbConnection(null))
            {
                if (dbConn.getHistory(id) == null)
                    dbConn.createHistory(localCfg);
                else
                    dbConn.reconfigureHistory(localCfg);
            }

            // NOTE: be careful, timeQuery() is inclusive of both start and end
            try (HistorySpaceConnection conn = db.getConnection(null))
            {
                BIHistory history = conn.getHistory(id);


                // find time to fetch from
                BAbsTime last = conn.getLastTimestamp(history);
                if (last == null) last = BAbsTime.DEFAULT;
                BAbsTime from = last.add(BRelTime.make(1L));

                HTimeZone tz = HTimeZone.make(getTz());
                HDateTime dt = HDateTime.make(from.getMillis(), tz);
                HDateTimeRange range = HDateTimeRange.make(dt.toZinc(), tz);

                // import records
                HClient client = server().getHaystackClient();
                HGrid hisItems = client.hisRead(getId().getRef(), range);
                for (int i = 0; i < hisItems.numRows(); i++)
                {
                    HRow row = hisItems.row(i);
                    if (row.has("ts") && row.has("val"))
                    {
                        HDateTime ts = (HDateTime) row.get("ts");
                        HVal val = row.get("val");
                        conn.append(history, makeTrendRecord(getKind(), ts, val));
                    }
                }

                if (LOG.isLoggable(Level.FINE))
                    LOG.fine("historyImport.doExecute end " + id + ": imported " + hisItems.numRows() + " rows.");

            }

            executeOk();
        }
        catch (Exception e)
        {
            LOG.fine("historyImport.doExecute fail " + id);
            e.printStackTrace();
            executeFail(e.getMessage());
        }
    }

    private BHistoryConfig createConfig()
    {
        BHistoryId id = getHistoryId();
        BHistoryConfig cfg = null;

        ////////////////////////////////
        // create history config

        if (getKind().equals("Bool"))
        {
            cfg = new BHistoryConfig(
                id, BTypeSpec.make(BBooleanTrendRecord.TYPE),
                BCapacity.UNLIMITED);
        }
        else if (getKind().equals("Number"))
        {
            cfg = new BHistoryConfig(
                id, BTypeSpec.make(BNumericTrendRecord.TYPE),
                BCapacity.UNLIMITED);
        }
        else throw new IllegalStateException(
            "Cannot create history for id " + getId() + ", kind " + getKind());

        ////////////////////////////////
        // set time zone

        cfg.setTimeZone(
            TypeUtil.toBajaTimeZone(
                HTimeZone.make(getTz())));

        ////////////////////////////////
        // done

        return cfg;
    }

    public static BTrendRecord makeTrendRecord(String kind, HDateTime ts, HVal val)
    {
        BAbsTime abs = BAbsTime.make(
            ts.millis(), 
            TypeUtil.toBajaTimeZone(ts.tz));

        if (kind.equals("Bool"))
        {
            BBooleanTrendRecord boolTrend = new BBooleanTrendRecord();
            boolTrend.set(abs, ((HBool) val).val, BStatus.ok);
            return boolTrend;
        }
        else if (kind.equals("Number"))
        {
            BNumericTrendRecord numTrend = new BNumericTrendRecord();
            numTrend.set(abs, ((HNum) val).val, BStatus.ok);
            return numTrend;
        }

        else throw new IllegalStateException("Cannot create trend record for kind " + kind);
    }

    private BNHaystackServer server()
    {
        BComplex comp = getParent();
        while ((comp != null) && !(comp instanceof BNHaystackServer))
            comp = comp.getParent();
        return (BNHaystackServer) comp;
    }

    public String getKind() { return getImportedTags().getDict().getStr("kind"); }
    public String getTz()   { return getImportedTags().getDict().getStr("tz");   }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack.driver");
}
