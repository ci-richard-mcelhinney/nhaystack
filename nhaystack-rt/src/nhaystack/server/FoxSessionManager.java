//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   30 Mar 2013  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Added missing @Overrides annotations, added use of generics
//
package nhaystack.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import javax.baja.driver.BDevice;
import javax.baja.fox.BFoxProxySession;
import javax.baja.naming.BHost;
import javax.baja.naming.BOrd;
import javax.baja.security.BIUserCredentials;
import com.tridium.fox.sys.BFoxClientConnection;
import com.tridium.nd.BNiagaraStation;

class FoxSessionManager
{
    /**
      * getSession
      */
    BFoxProxySession getSession(BDevice station, long leaseInterval) throws Exception
    {
        synchronized(sessions)
        {
            String slotPath = station.getSlotPath().toString();
            FoxSession fs = sessions.get(slotPath);

            if (fs == null)
            {
                fs = new FoxSession(slotPath, makeSession(station), leaseInterval);
                fs.proxy.connect();
                sessions.put(slotPath, fs);
                LOG.info("opened FoxSession for " + slotPath);
            }

            fs.scheduleTimeout();
            return fs.proxy;
        }
    }

    /**
      * makeSession
      */
    private static BFoxProxySession makeSession(BDevice station) throws Exception
    {
        BNiagaraStation s = (BNiagaraStation) station;

        BOrd address = s.getAddress();
        BHost host = (BHost) address.get();

        BFoxClientConnection clientConn = s.getClientConnection();
        int port = clientConn.getInt(clientConn.getProperty("port"));
        BIUserCredentials creds = clientConn.getCredentialStore().getCredentials();

        return BFoxProxySession.make(host, port, false, creds);
    }

    /**
      * FoxSession
      */
    class FoxSession
    {
        FoxSession(
            String slotPath,
            BFoxProxySession proxy,
            long leaseInterval)
        {
            this.slotPath = slotPath;
            this.proxy = proxy;
            this.leaseInterval = leaseInterval;
        }

        void scheduleTimeout()
        {
            if (timeout != null) timeout.cancel();
            timer.schedule(timeout = new Timeout(), leaseInterval);
        }

        class Timeout extends TimerTask
        {
            @Override
            public void run()
            {
                synchronized(sessions)
                {
                    LOG.info("closed FoxSession for " + slotPath);
                    proxy.disconnect();
                    sessions.remove(slotPath);
                }
            }
        }

        final String slotPath;
        final BFoxProxySession proxy;
        final long leaseInterval;

        final Timer timer = new Timer();
        Timeout timeout;
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack.fox");
    private final Map<String, FoxSession> sessions = new HashMap<>();
}
