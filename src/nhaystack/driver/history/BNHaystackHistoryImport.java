//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.history;

import javax.baja.driver.history.*;
import javax.baja.history.*;
import javax.baja.history.db.*;
import javax.baja.log.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.*;
import nhaystack.driver.*;
import nhaystack.util.*;
import nhaystack.worker.*;

/**
  * BNHaystackHistoryImport transfers remote haystack history data
  * into a local Baja history.
  */
public class BNHaystackHistoryImport extends BHistoryImport
{
    /*-
    class BNHaystackHistoryImport
    {
        properties
        {
            id: BHRef default{[ BHRef.DEFAULT ]}
            importedTags: BHTags default{[ BHTags.DEFAULT ]} flags { readonly }
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.BNHaystackHistoryImport(2248446507)1.0$ @*/
/* Generated Thu Apr 10 15:13:14 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "id"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>id</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#getId
   * @see nhaystack.driver.history.BNHaystackHistoryImport#setId
   */
  public static final Property id = newProperty(0, BHRef.DEFAULT,null);
  
  /**
   * Get the <code>id</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#id
   */
  public BHRef getId() { return (BHRef)get(id); }
  
  /**
   * Set the <code>id</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#id
   */
  public void setId(BHRef v) { set(id,v,null); }

////////////////////////////////////////////////////////////////
// Property "importedTags"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>importedTags</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#getImportedTags
   * @see nhaystack.driver.history.BNHaystackHistoryImport#setImportedTags
   */
  public static final Property importedTags = newProperty(Flags.READONLY, BHTags.DEFAULT,null);
  
  /**
   * Get the <code>importedTags</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#importedTags
   */
  public BHTags getImportedTags() { return (BHTags)get(importedTags); }
  
  /**
   * Set the <code>importedTags</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#importedTags
   */
  public void setImportedTags(BHTags v) { set(importedTags,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackHistoryImport.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

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
        finally
        {
            return null;
        }
    }

    public final void doExecute()
    {
        executeInProgress();
        try
        {
            // set up config
            BHistoryService service = (BHistoryService)Sys.getService(BHistoryService.TYPE);
            BHistoryDatabase db = service.getDatabase();
            BHistoryId id = getHistoryId();

            if (LOG.isTraceOn())
                LOG.trace("historyImport.doExecute begin " + id);

            BHistoryConfig localCfg = makeLocalConfig(createConfig());

            if(!db.exists(id)) db.createHistory(localCfg);
            else db.reconfigureHistory(localCfg);

            BIHistory history = db.getHistory(id);

            // find time to fetch from
            BAbsTime last = history.getLastTimestamp();
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
                HDateTime ts = (HDateTime) row.get("ts");
                HVal val = row.get("val");
                history.append(makeTrendRecord(getKind(), ts, val));
            }

            if (LOG.isTraceOn())
                LOG.trace("historyImport.doExecute end " + id + ": imported " + hisItems.numRows() + " rows.");

            executeOk();
        }
        catch (Exception e)
        {
            LOG.trace("historyImport.doExecute fail " + id);
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
        while ((comp != null) && (!(comp instanceof BNHaystackServer)))
            comp = comp.getParent();
        return (BNHaystackServer) comp;
    }

    public String getKind() { return getImportedTags().getDict().getStr("kind"); }
    public String getTz()   { return getImportedTags().getDict().getStr("tz");   }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack.driver");
}
