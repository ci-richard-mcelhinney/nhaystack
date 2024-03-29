//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Apr 2013  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//
package nhaystack.server;

import javax.baja.job.BSimpleJob;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

@NiagaraType
public class BNHaystackRebuildCacheJob extends BSimpleJob
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackRebuildCacheJob(2979906276)1.0$ @*/
/* Generated Sat Nov 18 18:33:08 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackRebuildCacheJob.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackRebuildCacheJob() { }

    public BNHaystackRebuildCacheJob(BNHaystackService service) 
    { 
        this.service = service;
    }

    @Override
    public void run(Context cx) throws Exception
    {
        service.getHaystackServer().getTagManager().resetTagGroupInfo();
        service.getHaystackServer().getCache().rebuild(service.getStats());
    }

    private BNHaystackService service;
}
