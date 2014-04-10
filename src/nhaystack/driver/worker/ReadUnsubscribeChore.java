package nhaystack.driver.worker;

import java.net.*;
import java.util.*;

import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.io.*;

import nhaystack.driver.*;
import nhaystack.driver.point.*;
import nhaystack.worker.*;

/**
  * ReadUnsubscribeChore
  */
public class ReadUnsubscribeChore extends AbstractSubscribeChore
{
    public ReadUnsubscribeChore(
        BNHaystackServer server, 
        BNHaystackProxyExt ext)
    {
        super(server, "ReadUnsubscribeChore:" + server.getApiUrl());

        putProxyExt(ext);
    }

    public void doRun()
    {
        if (server.isDisabled() || server.isDown() || server.isFault())
            return;

        server.getHaystackWatch().unsub(getProxyExtIds());
    }

    /**
      * A merge succeeds when a ReadUnsubscribeChore is passed in.
      * The proxyExts are combined together.
      */
    public boolean merge(WorkerChore chore)
    {
        if (chore instanceof ReadUnsubscribeChore)
        {
            combineProxyExts((ReadUnsubscribeChore) chore);
            return true;
        }

        return false;
    }
}
