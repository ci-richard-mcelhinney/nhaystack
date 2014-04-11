package nhaystack.driver.history;

import javax.baja.driver.history.*;
import javax.baja.history.*;
import javax.baja.history.db.*;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.timezone.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.*;
import nhaystack.driver.*;
import nhaystack.util.*;
import nhaystack.worker.*;

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

        return server.postAsyncChore(
            new WorkerInvocation(
                server.getWorker(),
                choreName,
                new Invocation(this, action, value, cx)));
    }

    public final void doExecute()
    {
        executeInProgress();
        try
        {
            HClient client = server().getHaystackClient();

            // set up config
            BHistoryService service = (BHistoryService)Sys.getService(BHistoryService.TYPE);
            BHistoryDatabase db = service.getDatabase();
            BHistoryId id = getHistoryId();

            BHistoryConfig localCfg = makeLocalConfig(createConfig());
            localCfg.setTimeZone(BTimeZone.getLocal()); 

            if(!db.exists(id)) db.createHistory(localCfg);
            else db.reconfigureHistory(localCfg);

            BIHistory history = db.getHistory(id);

            // find time to fetch from
            BAbsTime last = history.getLastTimestamp();
            if (last == null) last = BAbsTime.DEFAULT;
            BAbsTime from = last.add(BRelTime.make(1L));

            HTimeZone tz = HTimeZone.make(getTz());
            HDateTime dt = TypeUtil.fromBajaAbsTime(from, tz);
            HDateTimeRange range = HDateTimeRange.make(dt.toZinc(), tz);

            // import records
            HGrid hisItems = client.hisRead(getId().getRef(), range);
            for (int i = 0; i < hisItems.numRows(); i++)
            {
                HRow row = hisItems.row(i);
                HDateTime ts = (HDateTime) row.get("ts");
                HVal val = row.get("val");
                history.append(makeTrendRecord(getKind(), ts, val));
            }

            executeOk();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            executeFail(e.getMessage());
        }
    }

    private BHistoryConfig createConfig()
    {
        BHistoryId id = getHistoryId();

        if (getKind().equals("Bool"))
        {
            return new BHistoryConfig(
                id, BTypeSpec.make(BBooleanTrendRecord.TYPE),
                BCapacity.UNLIMITED);
        }

        else if (getKind().equals("Number"))
        {
            return new BHistoryConfig(
                id, BTypeSpec.make(BNumericTrendRecord.TYPE),
                BCapacity.UNLIMITED);
        }

        else throw new IllegalStateException("Cannot create history for id " + getId() + ", kind " + getKind());
    }

    public static BTrendRecord makeTrendRecord(String kind, HDateTime ts, HVal val)
    {
        if (kind.equals("Bool"))
        {
            BBooleanTrendRecord boolTrend = new BBooleanTrendRecord();
            boolTrend.set(
                TypeUtil.toBajaAbsTime(ts),
                ((HBool) val).val,
                BStatus.ok);
            return boolTrend;
        }
        else if (kind.equals("Number"))
        {
            BNumericTrendRecord numTrend = new BNumericTrendRecord();
            numTrend.set(
                TypeUtil.toBajaAbsTime(ts),
                ((HNum) val).val,
                BStatus.ok);
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
}
