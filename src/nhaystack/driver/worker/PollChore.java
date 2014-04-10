/**
  * Copyright (c) 2012 All Right Reserved, J2 Innovations
  */
package nhaystack.driver.worker;

import java.net.*;
import java.util.*;

import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.io.*;

import nhaystack.driver.*;
import nhaystack.driver.point.*;
import nhaystack.worker.*;

/**
  * PollChore
  */
public class PollChore extends WorkerChore
{
    public PollChore(BNHaystackServer server)
    {
        super(server.getWorker(), "PollChore:" + server.getApiUrl());

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
