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

public class BNHaystackRemoveBrokenRefsJob extends BSimpleJob
{
    /*-
    class BNHaystackRemoveBrokenRefsJob
    {
        properties
        {
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackRemoveBrokenRefsJob(3224119689)1.0$ @*/
/* Generated Tue May 30 17:08:43 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackRemoveBrokenRefsJob.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackRemoveBrokenRefsJob() { }

    public BNHaystackRemoveBrokenRefsJob(BNHaystackService service) 
    { 
        this.service = service;
    }

    public void run(Context cx) throws Exception
    {
        service.getHaystackServer().removeBrokenRefs();
    }

    private BNHaystackService service;

}
