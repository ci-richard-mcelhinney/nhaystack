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
import org.projecthaystack.io.*;

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
            id:       BHRef  default{[ BHRef.DEFAULT  ]} 
            kind:     String default{[ ""             ]} 
            tz:       String default{[ ""             ]} 
            haystack: BHDict default{[ BHDict.DEFAULT ]} 
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.BNHaystackHistoryImport(830107801)1.0$ @*/
/* Generated Mon Apr 07 17:13:50 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
// Property "kind"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>kind</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#getKind
   * @see nhaystack.driver.history.BNHaystackHistoryImport#setKind
   */
  public static final Property kind = newProperty(0, "",null);
  
  /**
   * Get the <code>kind</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#kind
   */
  public String getKind() { return getString(kind); }
  
  /**
   * Set the <code>kind</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#kind
   */
  public void setKind(String v) { setString(kind,v,null); }

////////////////////////////////////////////////////////////////
// Property "tz"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>tz</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#getTz
   * @see nhaystack.driver.history.BNHaystackHistoryImport#setTz
   */
  public static final Property tz = newProperty(0, "",null);
  
  /**
   * Get the <code>tz</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#tz
   */
  public String getTz() { return getString(tz); }
  
  /**
   * Set the <code>tz</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#tz
   */
  public void setTz(String v) { setString(tz,v,null); }

////////////////////////////////////////////////////////////////
// Property "haystack"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystack</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#getHaystack
   * @see nhaystack.driver.history.BNHaystackHistoryImport#setHaystack
   */
  public static final Property haystack = newProperty(0, BHDict.DEFAULT,null);
  
  /**
   * Get the <code>haystack</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#haystack
   */
  public BHDict getHaystack() { return (BHDict)get(haystack); }
  
  /**
   * Set the <code>haystack</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryImport#haystack
   */
  public void setHaystack(BHDict v) { set(haystack,v,null); }

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
                history.append(convertTrendRecord(ts, val));
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

    private BTrendRecord convertTrendRecord(HDateTime ts, HVal val)
    {
        if (getKind().equals("Bool"))
        {
            BBooleanTrendRecord boolTrend = new BBooleanTrendRecord();
            boolTrend.set(
                TypeUtil.toBajaAbsTime(ts),
                ((HBool) val).val,
                BStatus.ok);
            return boolTrend;
        }
        else if (getKind().equals("Number"))
        {
            BNumericTrendRecord numTrend = new BNumericTrendRecord();
            numTrend.set(
                TypeUtil.toBajaAbsTime(ts),
                ((HNum) val).val,
                BStatus.ok);
            return numTrend;
        }

        else throw new IllegalStateException("Cannot create trend record for id " + getId() + ", kind " + getKind());
    }

    private BNHaystackServer server()
    {
        BComplex comp = getParent();
        while ((comp != null) && (!(comp instanceof BNHaystackServer)))
            comp = comp.getParent();
        return (BNHaystackServer) comp;
    }
}
