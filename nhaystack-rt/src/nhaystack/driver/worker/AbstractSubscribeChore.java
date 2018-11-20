//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Added missing @Overrides annotations, added use of generics

package nhaystack.driver.worker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import nhaystack.driver.BNHaystackServer;
import nhaystack.driver.point.BNHaystackProxyExt;
import org.projecthaystack.HRef;

/**
  * AbstractSubscribeChore handles subscribing and unsubscribing a point
  */
public abstract class AbstractSubscribeChore extends DriverChore
{
    AbstractSubscribeChore(
        BNHaystackServer server, 
        String name)
    {
        super(server.getWorker(), name);
        this.server = server;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder(name);
        sb.append('[');
        Iterator<HRef> it = proxyExts.keySet().iterator();
        int n = 0;
        while (it.hasNext())
        {
            HRef ref = it.next();
            if (n++ > 0) sb.append(',');
            sb.append('(');
            sb.append(ref.toCode());
            sb.append(')');
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean isPing() { return false; }

////////////////////////////////////////////////////////////////
// protected
////////////////////////////////////////////////////////////////

    protected void putProxyExt(BNHaystackProxyExt ext)
    {
        proxyExts.put(ext.getId().getRef(), ext);
    }

    protected void combineProxyExts(AbstractSubscribeChore chore)
    {
        proxyExts.putAll(chore.proxyExts);
    }

    protected HRef[] getProxyExtIds()
    {
        return proxyExts.keySet().toArray(new HRef[proxyExts.size()]);
    }

    protected BNHaystackProxyExt[] getProxyExts()
    {
        return proxyExts.values().toArray(new BNHaystackProxyExt[proxyExts.size()]);
    }

    protected BNHaystackProxyExt getProxyExt(HRef id)
    {
        return proxyExts.get(id);
    }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    protected final BNHaystackServer server;
    private final Map<HRef, BNHaystackProxyExt> proxyExts = new HashMap<>();
}
