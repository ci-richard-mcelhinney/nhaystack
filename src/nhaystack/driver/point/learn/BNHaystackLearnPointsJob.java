package nhaystack.driver.point.learn;

import javax.baja.control.*;
import javax.baja.history.*;
import javax.baja.job.*;
import javax.baja.net.*;
import javax.baja.sys.*;
import javax.baja.util.*;
import com.tridium.util.EscUtil;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.*;
import nhaystack.driver.*;

public class BNHaystackLearnPointsJob extends BSimpleJob 
{
    /*-
    class BNHaystackLearnPointsJob
    {
        properties
        {
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackLearnPointsJob(677323658)1.0$ @*/
/* Generated Mon Apr 07 08:34:06 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackLearnPointsJob.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackLearnPointsJob() {}

    public BNHaystackLearnPointsJob(BNHaystackServer server)
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
        HGrid grid = client.readAll("point");
        for (int i = 0; i < grid.numRows(); i++)
        {
            HRow row = grid.row(i);

            String kind = row.getStr("kind");
            if (kind.equals("Bool") || kind.equals("Number"))
            {
                String name = EscUtil.slot.escape(row.dis());

                BNHaystackPointEntry entry = new BNHaystackPointEntry();

                entry.setId(BHRef.make(row.id()));
                entry.setKind(kind);
                entry.setWritable(row.has("writable"));

                add(name, entry);
            }
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private BNHaystackServer server = null;
}
