//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.worker;

import org.projecthaystack.*;

import nhaystack.driver.*;
import nhaystack.driver.point.*;
import nhaystack.worker.*;

/**
  * PollChore handles polling subscribed points
  */
public class PollChore extends WorkerChore
{
    public PollChore(BNHaystackServer server)
    {
        super(server.getWorker(), "PollChore:" + server.getHaystackUrl());

        this.server = server;
    }

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
    public boolean merge(WorkerChore chore)
    {
        return (chore instanceof PollChore);
    }

    public boolean isPing() { return false; }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    private final BNHaystackServer server;
}
