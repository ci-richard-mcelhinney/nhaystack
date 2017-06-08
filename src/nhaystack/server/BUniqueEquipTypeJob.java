//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.server;

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
  * BUniqueEquipTypeJob 
  */
public class BUniqueEquipTypeJob extends BSimpleJob 
{
    /*-
    class BUniqueEquipTypeJob
    {
        properties
        {
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BUniqueEquipTypeJob(3858554168)1.0$ @*/
/* Generated Tue May 30 17:08:43 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
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

    public void doCancel(Context ctx) 
    {
        super.doCancel(ctx);
        throw new JobCancelException();
    }

    public void run(Context ctx) throws Exception 
    {
        NHServer server = service.getHaystackServer();

        HGrid grid = HGrid.EMPTY;
        BComponent[] equips = NHServerOps.getFilterComponents(
            server, "equip and (" + filter + ")", null);
        if (equips.length > 0)
            grid = (new UniqueEquipTypes(server)).createTypes(
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

    private BNHaystackService service = null;
    private String filter;
    private double percentMatch;
    private boolean applyTags;
}
