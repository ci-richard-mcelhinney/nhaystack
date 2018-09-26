//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy       Creation
//   08 May 2018  Eric Anderson    Migrated to slot annotations, added missing @Overrides
//                                 annotations, added use of generics
//   26 Sep 2018  Andrew Saunders  Added shared constants for siteRef and equipRef tag names

package nhaystack.driver.point.learn;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.control.BControlPoint;
import javax.baja.history.BHistoryId;
import javax.baja.job.BSimpleJob;
import javax.baja.job.JobCancelException;
import javax.baja.naming.BOrd;
import javax.baja.naming.SlotPath;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHDict;
import nhaystack.BHRef;
import nhaystack.util.NHaystackConst;
import nhaystack.driver.BHTags;
import nhaystack.driver.BNHaystackServer;
import nhaystack.driver.BPointGrouping;
import nhaystack.driver.BStructureSettings;
import nhaystack.driver.NameGenerator;
import nhaystack.driver.history.BNHaystackHistoryImport;
import nhaystack.driver.point.BNHaystackBoolPoint;
import nhaystack.driver.point.BNHaystackBoolWritable;
import nhaystack.driver.point.BNHaystackNumberPoint;
import nhaystack.driver.point.BNHaystackNumberWritable;
import nhaystack.driver.point.BNHaystackPointFolder;
import nhaystack.driver.point.BNHaystackProxyExt;
import nhaystack.driver.point.BNHaystackStrPoint;
import nhaystack.driver.point.BNHaystackStrWritable;
import nhaystack.server.BNHaystackRebuildCacheJob;
import nhaystack.server.BNHaystackService;
import nhaystack.server.TagManager;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
import nhaystack.site.BHTagged;
import org.projecthaystack.HBin;
import org.projecthaystack.HDate;
import org.projecthaystack.HDateTime;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HFilter;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HRef;
import org.projecthaystack.HTime;
import org.projecthaystack.HUri;
import org.projecthaystack.HVal;
import org.projecthaystack.client.HClient;

/**
  * BNHaystackLearnStructureJob is a Job which 'learns' all the remote
  * points from a remote haystack server, and puts them into a folder structure.
  */
@NiagaraType
public class BNHaystackLearnStructureJob extends BSimpleJob implements NHaystackConst
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.learn.BNHaystackLearnStructureJob(2979906276)1.0$ @*/
/* Generated Fri Nov 17 11:56:51 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackLearnStructureJob.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackLearnStructureJob() {}

    public BNHaystackLearnStructureJob(BNHaystackServer server)
    {
        this.server = server;
        BStructureSettings settings = server.getStructureSettings();

        this.siteFilter = makeFilter(settings.getSiteFilter());
        this.equipFilter = makeFilter(settings.getEquipFilter());
        this.pointFilter = makeFilter(settings.getPointFilter());

        this.groups = settings.getPointGroupings();
        this.groupFilters = new HFilter[groups.length];
        for (int i = 0; i < groups.length; i++)
            groupFilters[i] = HFilter.make(groups[i].getFilter());
    }

    private static HFilter makeFilter(String str)
    {
        if (str.equals("")) 
            return HFilter.make("id");
        else
            return HFilter.make(str);
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
        try
        {
            HClient client = server.getHaystackClient();
            traverse(client, HUri.make("equip:/"), server.getPoints());

            // rebuild cache
            BNHaystackService service = (BNHaystackService) Sys.getService(BNHaystackService.TYPE);
            BNHaystackRebuildCacheJob job = new BNHaystackRebuildCacheJob(service);
            job.run(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    private void traverse(HClient client, HUri parentNav, BComponent parent)
    {
        if (LOG.isLoggable(Level.FINE))
            LOG.fine("learn structure: nav " + parentNav);

        // use a name generator to create unique names
        NameGenerator nameGen = new NameGenerator();

        // call the nav() op
        HGrid req = makeNavGrid(parentNav);
        HGrid res = client.call("nav", req);

        for (int i = 0; i < res.numRows(); i++)
        {
            HDict rec = res.row(i);

            // skip things which do not pass the settings
            if (rec.has("site")  && !siteFilter.include(rec,  PATHER)) continue;
            if (rec.has("equip") && !equipFilter.include(rec, PATHER)) continue;
            if (rec.has("point") && !pointFilter.include(rec, PATHER)) continue;

            // make a name
            String name = rec.has("dis") ? 
                rec.getStr("dis") : rec.getStr("navName");
            name = SlotPath.escape(nameGen.makeUniqueName(name));

            if (rec.has("point"))
            {
                createPoint(parent, rec, name);
            }
            else
            {
                // add site
                if (rec.has("site"))
                    addSite(rec, name);

                // make folder
                BNHaystackPointFolder folder = ensureFolder(parent, name);

                // add implicit equip
                if (rec.has("equip") && folder.get("equip") == null)
                    folder.add("equip", createEquip(rec, name));

                // traverse recursively
                HUri childNav = (HUri) rec.get("navId");
                traverse(client, childNav, folder);
            }
        }
    }

    private void createPoint(BComponent parent, HDict rec, String name)
    {
        String groupName = findGroupName(rec);
        if (groupName != null)
            parent = ensureFolder(parent, groupName);

        BControlPoint point = (BControlPoint) parent.get(name);
        if (point == null)
        {
            point = makeControlPoint(rec);

            // create haystack dict
            point.add("haystack", BHDict.make(createHaystackDict(rec).toDict()));

            // create proxy ext
            BNHaystackProxyExt ext = (BNHaystackProxyExt) point.getProxyExt();
            point.setFacets(BNHaystackLearnPointsJob.makePointFacets(rec));
            ext.setId(BHRef.make(rec.id()));
            ext.setImportedTags(BHTags.make(rec));

            // add point
            parent.add(name, point);

            // create import
            if (rec.has("his"))
            {
                String hisName = SlotPath.escape(rec.dis());
                if (server.getHistories().get(hisName) == null)
                {
                    // site name
                    BHSite site = (BHSite) idComponents.get(rec.getRef(SITE_REF));
                    HDict d = site.getHaystack().getDict();
                    String siteName = d.has("dis") ?  d.getStr("dis") : d.getStr("navName");
                    siteName = SlotPath.escape(siteName);

                    // equip name
                    BHEquip equip = (BHEquip) idComponents.get(rec.getRef(EQUIP_REF));
                    d = equip.getHaystack().getDict();
                    String equipName = d.has("dis") ?  d.getStr("dis") : d.getStr("navName");
                    equipName = SlotPath.escape(equipName);

                    BNHaystackHistoryImport imp = new BNHaystackHistoryImport();
                    BHistoryId hisId = BHistoryId.make(
                        siteName, equipName + '_' + name);

                    imp.setId(BHRef.make(rec.id()));
                    imp.setImportedTags(BHTags.make(rec));
                    imp.setHistoryId(hisId);

                    server.getHistories().add(hisName, imp);
                }
            }
        }
    }

    private void addSite(HDict rec, String name)
    {
        BComponent root = (BComponent) BOrd.make("station:|slot:/").get(server, null);
        BHSite site = (BHSite) root.get(name);

        if (site == null)
        {
            site = new BHSite();
            site.setHaystack(BHDict.make(createHaystackDict(rec).toDict()));
            root.add(name, site);
        }

        idComponents.put(rec.id(), site);
    }

    private BHEquip createEquip(HDict rec, String name)
    {
        BHSite site = (BHSite) idComponents.get(rec.getRef(SITE_REF));

        BHEquip equip = new BHEquip();

        // create haystack dict, with ref to site
        HDictBuilder hdb = createHaystackDict(rec);
        hdb.add(SITE_REF, TagManager.makeSlotPathRef(site).getHRef());
        hdb.add("navNameFormat", "%parent.displayName%");
        equip.setHaystack(BHDict.make(hdb.toDict()));
        idComponents.put(rec.id(), equip);

        return equip;
    }

    private static HDictBuilder createHaystackDict(HDict rec)
    {
        HDictBuilder hdb = new HDictBuilder();

        Iterator<Map.Entry<String, HVal>> itr = rec.iterator();
        while (itr.hasNext())
        {
            Map.Entry<String, HVal> e = itr.next();
            String name = e.getKey();
            HVal val = e.getValue();

            if (!(val instanceof HRef ||
                  val instanceof HBin ||
                  val instanceof HUri ||
                  val instanceof HDate ||
                  val instanceof HTime ||
                  val instanceof HDateTime))
                hdb.add(name, val);
        }

        return hdb;
    }

    private String findGroupName(HDict rec)
    {
        for (int i = 0; i < groupFilters.length; i++)
        {
            if (groupFilters[i].include(rec,  PATHER))
                return groups[i].getGroupName();
        }
        return null;
    }

    private static BControlPoint makeControlPoint(HDict rec)
    {
        String kind = rec.getStr("kind");
        boolean writable = rec.has("writable");

        if (kind.equals("Bool"))
        {
            return writable ?
                new BNHaystackBoolWritable() :
                new BNHaystackBoolPoint();
        }
        else if (kind.equals("Number"))
        {
            return writable ?
                new BNHaystackNumberWritable() :
                new BNHaystackNumberPoint();
        }
        else if (kind.equals("Str"))
        {
            return writable ?
                new BNHaystackStrWritable() :
                new BNHaystackStrPoint();
        }
        else throw new IllegalStateException("Cannot create point for " + kind);
    }

    private static BNHaystackPointFolder ensureFolder(BComponent parent, String name)
    {
        BNHaystackPointFolder folder = (BNHaystackPointFolder) parent.get(name);
        if (folder == null)
            parent.add(name, folder = new BNHaystackPointFolder());
        return folder;
    }

    private static HGrid makeNavGrid(HUri navId)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add("navId", navId);
        return HGridBuilder.dictsToGrid(new HDict[] { hd.toDict() });
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack.driver");

    private BNHaystackServer server;
    private HFilter siteFilter;
    private HFilter equipFilter;
    private HFilter pointFilter;
    private BPointGrouping[] groups;
    private HFilter[] groupFilters;

    private final HFilter.Pather PATHER = new HFilter.Pather() {
        @Override
        public HDict find(String ref) { return null; } };

    private final Map<HRef, BHTagged> idComponents = new HashMap<>();
}
