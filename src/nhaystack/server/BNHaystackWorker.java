//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   09 May 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import javax.baja.sys.*;
import javax.baja.util.*;

public class BNHaystackWorker extends BWorker
{
  /*-
   class BNHaystackWorker
   {
   }
   -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackWorker(2892176268)1.0$ @*/
/* Generated Thu May 09 09:27:13 EDT 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackWorker.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public Worker getWorker() 
    { 
        return worker; 
    }  

    public void enqueue(Runnable runnable)
    {
        queue.enqueue(runnable);
    }
  
    private final Queue queue = new Queue();
    private final Worker worker = new Worker(queue);
}
