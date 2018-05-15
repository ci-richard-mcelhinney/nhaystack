//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Added missing @Overrides annotation

package nhaystack.driver.worker;

import java.util.logging.Logger;
import nhaystack.worker.BINHaystackWorker;
import nhaystack.worker.WorkerChore;

/**
  * PollChore handles polling subscribed points
  */
public abstract class DriverChore extends WorkerChore
{
    protected DriverChore(BINHaystackWorker worker, String name)
    {
        super(worker, name);
    }

    /**
      * Get the Log
      */
    @Override
    protected final Logger getLogger() { return LOG; }

    private static final Logger LOG = Logger.getLogger("nhaystack.driver");
}
