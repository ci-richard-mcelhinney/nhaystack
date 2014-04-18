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
  * ReadSubscribeChore
  */
public class ReadSubscribeChore extends AbstractSubscribeChore
{
    public ReadSubscribeChore(
        BNHaystackServer server, 
        BNHaystackProxyExt ext)
    {
        super(server, "ReadSubscribeChore:" + server.getApiUrl());

        putProxyExt(ext);
    }

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
