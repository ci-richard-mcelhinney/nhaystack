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

/**
  * ReadSubscribeChore handles subscribing to a point
  */
public class ReadSubscribeChore extends AbstractSubscribeChore
{
    public ReadSubscribeChore(
        BNHaystackServer server, 
        BNHaystackProxyExt ext)
    {
        super(server, "ReadSubscribeChore:" + server.getHaystackUrl());

        putProxyExt(ext);
    }

    @Override
    public void doRun()
    {
        if (server.isDisabled() || server.isDown() || server.isFault())
            return;

        HGrid grid = server.getHaystackWatch().sub(getProxyExtIds(), true);
        for (int i = 0; i < grid.numRows(); i++)
        {
            HRow row = grid.row(i);
            BNHaystackProxyExt ext = getProxyExt(row.id());
            ext.doRead(row.get("curVal"), (HStr) row.get("curStatus"));
        }
    }

    /**
      * A merge succeeds when a ReadSubscribeChore is passed in.
      * The proxyExts are combined together.
      */
    @Override
    public boolean merge(WorkerChore chore)
    {
        if (chore instanceof ReadSubscribeChore)
        {
            combineProxyExts((ReadSubscribeChore) chore);
            return true;
        }

        return false;
    }
}
