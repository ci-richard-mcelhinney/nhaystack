package nhaystack.driver.worker;

import java.util.*;

import javax.baja.util.*;

import org.projecthaystack.*;

import nhaystack.driver.*;
import nhaystack.driver.point.*;
import nhaystack.worker.*;

/**
  * AbstractSubscribeChore
  */
public abstract class AbstractSubscribeChore extends WorkerChore
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
        StringBuffer sb = new StringBuffer(name);
        sb.append("[");
        Iterator it = proxyExts.keySet().iterator();
        int n = 0;
        while (it.hasNext())
        {
            HRef ref = (HRef) it.next();
            if (n++ > 0) sb.append(",");
            sb.append("(");
            sb.append(ref.toCode());
            sb.append(")");
        }
        sb.append("]");
        return sb.toString();
    }

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
        Array arr = new Array(HRef.class);
        Iterator it = proxyExts.keySet().iterator();
        while (it.hasNext())
            arr.add(it.next());
        return (HRef[]) arr.trim();
    }

    protected BNHaystackProxyExt[] getProxyExts()
    {
        Array arr = new Array(BNHaystackProxyExt.class);
        Iterator it = proxyExts.values().iterator();
        while (it.hasNext())
            arr.add(it.next());
        return (BNHaystackProxyExt[]) arr.trim();
    }

    protected BNHaystackProxyExt getProxyExt(HRef id)
    {
        return (BNHaystackProxyExt) proxyExts.get(id);
    }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    protected final BNHaystackServer server;
    private final Map proxyExts = new HashMap();
}
