package nhaystack.driver.history.learn;

import javax.baja.job.*;
import javax.baja.history.*;
import javax.baja.sys.*;
import com.tridium.util.EscUtil;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.*;
import nhaystack.driver.*;

public class BNHaystackLearnHistoriesJob extends BSimpleJob 
{
    /*-
    class BNHaystackLearnHistoriesJob
    {
        properties
        {
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.BNHaystackLearnHistoriesJob(3057501146)1.0$ @*/
/* Generated Fri Apr 04 15:19:41 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackLearnHistoriesJob.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackLearnHistoriesJob() {}

    public BNHaystackLearnHistoriesJob(BNHaystackServer server) 
    {
        this.server = server;
    }

    public void doCancel(Context ctx) 
    {
        super.doCancel(ctx);
        throw new JobCancelException();
    }

    public void run(Context ctx) throws Exception 
    {
        HClient client = server.getHaystackClient();
        HGrid grid = client.readAll("his");
        for (int i = 0; i < grid.numRows(); i++)
        {
            HRow row = grid.row(i);

            String kind = row.getStr("kind");
            if (kind.equals("Bool") || kind.equals("Number"))
            {
                String name = EscUtil.slot.escape(row.dis());

                BNHaystackHistoryEntry entry = new BNHaystackHistoryEntry();

                entry.setId(BHRef.make(row.id()));
                entry.setImportedTags(BHTags.make(row));

                entry.setHistoryId(
                    BHistoryId.make(
                        Sys.getStation().getStationName(),
                        name));

                add(name, entry);
            }
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

   private BNHaystackServer server = null;
}
