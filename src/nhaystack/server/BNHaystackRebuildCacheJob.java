//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Apr 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import javax.baja.job.*;
import javax.baja.sys.*;

public class BNHaystackRebuildCacheJob extends BSimpleJob
{
    /*-
    class BNHaystackRebuildCacheJob
    {
        properties
        {
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackRebuildCacheJob(1080354402)1.0$ @*/
/* Generated Tue May 30 17:08:42 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackRebuildCacheJob.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackRebuildCacheJob() { }

    public BNHaystackRebuildCacheJob(BNHaystackService service) 
    { 
        this.service = service;
    }

    public void run(Context cx) throws Exception
    {
        service.getHaystackServer().getCache().rebuild(service.getStats());
    }

    private BNHaystackService service;

}
