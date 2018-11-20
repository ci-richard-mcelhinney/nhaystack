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
import org.projecthaystack.HGrid;
import org.projecthaystack.HRow;
import org.projecthaystack.HStr;
import org.projecthaystack.HWatch;

/**
  * PollChore handles polling subscribed points
  */
public class PollChore extends DriverChore
{
    public PollChore(BNHaystackServer server)
    {
        super(server.getWorker(), "PollChore:" + server.getHaystackUrl());

        this.server = server;
    }

    @Override
    public void doRun() throws Exception
    {
        if (server.isDisabled() || server.isDown() || server.isFault())
            return;

        HWatch watch = server.getHaystackWatch();
        if (watch.id() == null) return; // nothing subscribed

        HGrid grid = watch.pollChanges();
        for (int i = 0; i < grid.numRows(); i++)
        {
            HRow row = grid.row(i);
            BNHaystackProxyExt ext = server.getRegisteredProxyExt(row.id());
            ext.doRead(row.get("curVal"), (HStr) row.get("curStatus"));
        }
    }

    /**
      * PollChore does a 'merge' by just ignoring new Poll requests
      */
    @Override
    public boolean merge(WorkerChore chore)
    {
        return chore instanceof PollChore;
    }

    @Override
    public boolean isPing() { return false; }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    private final BNHaystackServer server;
}
