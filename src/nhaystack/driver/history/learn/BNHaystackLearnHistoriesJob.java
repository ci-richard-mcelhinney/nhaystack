//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.history.learn;

import java.util.*;

import javax.baja.history.*;
import javax.baja.job.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.*;
import nhaystack.driver.*;

/**
  * BNHaystackLearnHistoriesJob is a Job which 'learns' all the remote
  * histories from a remote haystack server.
  */
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
/*@ $nhaystack.driver.history.learn.BNHaystackLearnHistoriesJob(3645683744)1.0$ @*/
/* Generated Tue May 30 17:08:42 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
        NameGenerator nameGen = new NameGenerator();
        Map entries = new TreeMap();

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

        Iterator it = entries.keySet().iterator();
        while (it.hasNext())
        {
            String name = (String) it.next();
            BNHaystackHistoryEntry entry = (BNHaystackHistoryEntry) entries.get(name);
            add(name, entry);
        }
    }

    private String hisName(HRow row)
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

   private BNHaystackServer server = null;
}
