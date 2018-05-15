//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   07 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations,
//                               added use of generics

package nhaystack.driver.history.learn;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.baja.history.BHistoryId;
import javax.baja.job.BSimpleJob;
import javax.baja.job.JobCancelException;
import javax.baja.naming.SlotPath;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.nre.util.TextUtil;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHRef;
import nhaystack.driver.BHTags;
import nhaystack.driver.BNHaystackServer;
import nhaystack.driver.NameGenerator;
import org.projecthaystack.HGrid;
import org.projecthaystack.HRow;
import org.projecthaystack.client.HClient;

/**
  * BNHaystackLearnHistoriesJob is a Job which 'learns' all the remote
  * histories from a remote haystack server.
  */
@NiagaraType
public class BNHaystackLearnHistoriesJob extends BSimpleJob 
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.learn.BNHaystackLearnHistoriesJob(2979906276)1.0$ @*/
/* Generated Fri Nov 17 11:48:22 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackLearnHistoriesJob.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackLearnHistoriesJob() {}

    public BNHaystackLearnHistoriesJob(BNHaystackServer server) 
    {
        this.server = server;
    }

    @Override
    public void doCancel(Context ctx)
    {
        super.doCancel(ctx);
        throw new JobCancelException();
    }

    @Override
    public void run(Context ctx) throws Exception
    {
        NameGenerator nameGen = new NameGenerator();
        Map<String, BNHaystackHistoryEntry> entries = new TreeMap<>();

        //Map deviceNames = new HashMap();

        HClient client = server.getHaystackClient();
        HGrid grid = client.readAll("his");
        for (int i = 0; i < grid.numRows(); i++)
        {
            HRow row = grid.row(i);

            String kind = row.getStr("kind");
            if (kind.equals("Bool") || kind.equals("Number"))
            {
                String name = hisName(row);
                if (name != null)
                {
                    name = TextUtil.replace(name, " ", "_");
                    name = SlotPath.escape(nameGen.makeUniqueName(name));

                    BNHaystackHistoryEntry entry = new BNHaystackHistoryEntry();
                    entry.setId(BHRef.make(row.id()));
                    entry.setImportedTags(BHTags.make(row));
                    entry.setHistoryId(BHistoryId.make(server.getName(), name));

                    entries.put(name, entry);
                }
            }
        }

        for (Entry<String, BNHaystackHistoryEntry> entry : entries.entrySet())
        {
            add(entry.getKey(), entry.getValue());
        }
    }

    private static String hisName(HRow row)
    {
        if (row.has("navName")) return row.getStr("navName");
        if (row.has("dis")) return row.getStr("dis");
        return null;
    }

//    private String fetchDeviceName(HClient client, Map deviceNames, HRow row)
//    {
//        if (!row.has("equipRef")) 
//            return Sys.getStation().getStationName();
//
//        HRef equipRef = row.getRef("equipRef");
//        if (deviceNames.containsKey(equipRef))
//            return (String) deviceNames.get(equipRef);
//
//        HDict equip = client.readById(equipRef);
//        String name = equip.getStr("navName");
//        deviceNames.put(equipRef, name);
//        return name;
//    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

   private BNHaystackServer server;
}
