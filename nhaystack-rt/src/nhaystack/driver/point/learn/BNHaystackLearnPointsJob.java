//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations,
//                               added use of generics

package nhaystack.driver.point.learn;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.baja.job.BSimpleJob;
import javax.baja.job.JobCancelException;
import javax.baja.naming.SlotPath;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.nre.util.TextUtil;
import javax.baja.sys.BEnumRange;
import javax.baja.sys.BFacets;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHRef;
import nhaystack.driver.BHTags;
import nhaystack.driver.BNHaystackServer;
import nhaystack.driver.NameGenerator;
import nhaystack.res.Resources;
import org.projecthaystack.HDict;
import org.projecthaystack.HGrid;
import org.projecthaystack.HRow;
import org.projecthaystack.client.HClient;

/**
  * BNHaystackLearnPointsJob is a Job which 'learns' all the remote
  * points from a remote haystack server.
  */
@NiagaraType
public class BNHaystackLearnPointsJob extends BSimpleJob 
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.learn.BNHaystackLearnPointsJob(2979906276)1.0$ @*/
/* Generated Fri Nov 17 11:55:13 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackLearnPointsJob.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackLearnPointsJob() {}

    public BNHaystackLearnPointsJob(BNHaystackServer server)
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
        Map<String, BNHaystackPointEntry> entries = new TreeMap<>();
        
        HClient client = server.getHaystackClient();
        HGrid grid = client.readAll("point");
        for (int i = 0; i < grid.numRows(); i++)
        {
            HRow row = grid.row(i);

            String name = SlotPath.escape(nameGen.makeUniqueName(row.dis()));

            BNHaystackPointEntry entry = new BNHaystackPointEntry();

            entry.setFacets(makePointFacets(row));
            entry.setId(BHRef.make(row.id()));
            entry.setImportedTags(BHTags.make(row));

            entries.put(name, entry);
        }

        for (Entry<String, BNHaystackPointEntry> entry : entries.entrySet())
        {
            add(entry.getKey(), entry.getValue());
        }
    }

    static BFacets makePointFacets(HDict rec)
    {
        String kind = rec.getStr("kind");
        if      (kind.equals("Bool"))   return makeBoolFacets(rec);
        else if (kind.equals("Number")) return makeNumberFacets(rec);
        else if (kind.equals("Str"))    return makeStrFacets(rec);
        else return BFacets.NULL;
    }

    private static BFacets makeBoolFacets(HDict rec)
    {
        if (!rec.has("enum")) return BFacets.NULL;

        String[] tokens = TextUtil.split(rec.getStr("enum"), ',');

        // first true, then false
        return BFacets.makeBoolean(tokens[1], tokens[0]); 
    }

    public static BFacets makeNumberFacets(HDict rec)
    {
        try
        {
            if (!rec.has("unit")) return BFacets.NULL;

            String unit = rec.getStr("unit");
            if (unit.toLowerCase().equals("none"))
                return BFacets.NULL;

            return BFacets.make(
                BFacets.UNITS,
                Resources.toBajaUnit(
                    Resources.getSymbolUnit(unit)));
        }
        catch (Exception e)
        {
            LOG.severe("Cannot make units for " + rec);
            return BFacets.NULL;
        }
    }

    private static BFacets makeStrFacets(HDict rec)
    {
        if (!rec.has("enum")) return BFacets.NULL;

        String[] tokens = TextUtil.split(rec.getStr("enum"), ',');

        for (String token : tokens)
            if (!SlotPath.isValidName(token)) return BFacets.NULL;

        return BFacets.makeEnum(BEnumRange.make(tokens));
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack.driver");

    private BNHaystackServer server;
}
