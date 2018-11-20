//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.server;

import javax.baja.job.BSimpleJob;
import javax.baja.job.JobCancelException;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHGrid;
import org.projecthaystack.HGrid;

/**
  * BUniqueEquipTypeJob 
  */
@NiagaraType
public class BUniqueEquipTypeJob extends BSimpleJob 
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BUniqueEquipTypeJob(2979906276)1.0$ @*/
/* Generated Sat Nov 18 21:09:04 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BUniqueEquipTypeJob.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BUniqueEquipTypeJob() {}

    public BUniqueEquipTypeJob(
        BNHaystackService service, String filter, double percentMatch, boolean applyTags) 
    {
        this.service = service;
        this.filter = filter;
        this.percentMatch = percentMatch;
        this.applyTags = applyTags;
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
        NHServer server = service.getHaystackServer();

        HGrid grid = HGrid.EMPTY;
        BComponent[] equips = NHServerOps.getFilterComponents(
            server, "equip and (" + filter + ')', null);
        if (equips.length > 0)
            grid = new UniqueEquipTypes(server).createTypes(
                equips, filter, percentMatch, applyTags);

        if (service.get(UNIQUE_EQUIP_TYPES) == null)
            service.add(UNIQUE_EQUIP_TYPES, BHGrid.make(grid));
        else
            service.set(UNIQUE_EQUIP_TYPES, BHGrid.make(grid));
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    public static final String UNIQUE_EQUIP_TYPES = "uniquipEquipTypes";

    private BNHaystackService service;
    private String filter;
    private double percentMatch;
    private boolean applyTags;
}
