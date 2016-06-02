//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.worker;

import java.util.logging.*;

import nhaystack.worker.*;

/**
  * PollChore handles polling subscribed points
  */
public abstract class DriverChore extends WorkerChore
{
    public DriverChore(BINHaystackWorker worker, String name) 
    {
        super(worker, name);
    }

    /**
      * Get the Log
      */
    protected final Logger getLogger() { return LOG; }

    private static final Logger LOG = Logger.getLogger("nhaystack.driver");
}
