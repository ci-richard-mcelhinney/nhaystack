//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Added missing @Overrides annotations

package nhaystack.driver.worker;

import nhaystack.driver.BNHaystackServer;
import nhaystack.driver.point.BNHaystackProxyExt;
import nhaystack.worker.WorkerChore;

/**
  * WriteChore handles writing to a point
  */
public class WriteChore extends DriverChore
{
    public WriteChore(BNHaystackServer server, BNHaystackProxyExt ext)
    {
        super(
            server.getWorker(),
            "WriteChore:" + 
            server.getHaystackUrl() + ':' +
            ext.getId());

        this.server = server;
        this.ext = ext;
    }

    @Override
    public void doRun() throws Exception
    {
        if (server.isDisabled() || server.isDown() || server.isFault())
            return;

        ext.doWrite();
    }

    @Override
    public boolean merge(WorkerChore chore)
    {
        return false;
    }

    @Override
    public boolean isPing() { return false; }

    private final BNHaystackServer server;
    private final BNHaystackProxyExt ext;
}
