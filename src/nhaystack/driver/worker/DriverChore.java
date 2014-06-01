//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.worker;

import javax.baja.log.*;

import nhaystack.worker.*;

/**
  * PollChore handles polling subscribed points
  */
public abstract class DriverChore extends WorkerChore
{
    public DriverChore(BNHaystackWorker worker, String name) 
    {
        super(worker, name);
    }

    /**
      * Get the Log
      */
    protected final Log getLog() { return LOG; }

    private static final Log LOG = Log.getLog("nhaystack.driver");
}
