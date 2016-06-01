//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   30 Mar 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;

import javax.baja.control.*;
import javax.baja.control.ext.*;
import javax.baja.driver.*;
import javax.baja.fox.*;
import javax.baja.history.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.security.*;
import javax.baja.sys.*;
import javax.baja.util.*;

class FoxSessionManager
{
    /**
      * getSession
      */
    BFoxProxySession getSession(BDevice station, long leaseInterval)
    throws Exception
    {
        synchronized(sessions)
        {
            String slotPath = station.getSlotPath().toString();
            FoxSession fs = (FoxSession) sessions.get(slotPath);

            if (fs == null)
            {
                fs = new FoxSession(slotPath, makeSession(station), leaseInterval);
                fs.proxy.connect();
                sessions.put(slotPath, fs);
                LOG.message("opened FoxSession for " + slotPath);
            }

            fs.scheduleTimeout();
            return fs.proxy;
        }
    }

    /**
      * makeSession
      */
    private static BFoxProxySession makeSession(BDevice station)
    throws Exception
    {
        throw new IllegalStateException("TODO: BPassword.getString() is deprecated");
//        BOrd address = (BOrd) station.get("address");
//        BHost host = (BHost) address.get();
//
//        BComponent clientConn = (BComponent) station.get("clientConnection");
//        int port = clientConn.getInt(clientConn.getProperty("port"));
//        BString username = (BString) clientConn.get("username");
//        BPassword password = (BPassword) clientConn.get("password");
//
//        return BFoxProxySession.make(
//            host, port, username.toString(), password.getString());
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
            public void run()
            {
                synchronized(sessions)
                {
                    LOG.message("closed FoxSession for " + slotPath);
                    proxy.disconnect();
                    sessions.remove(slotPath);
                }
            }
        }

        final String slotPath;
        final BFoxProxySession proxy;
        final long leaseInterval;

        final Timer timer = new Timer();
        Timeout timeout = null;
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack.fox");
    private final Map sessions = new HashMap();
}

