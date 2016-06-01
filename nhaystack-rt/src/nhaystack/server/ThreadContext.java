//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   26 Nov 2014  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.concurrent.*;
import javax.baja.sys.*;

/**
  * ThreadContext manages Context meta-data associated with a Thread.
  */
public abstract class ThreadContext
{
    /**
      * Return the Context associated with a given Thread, or null.
      */
    public static Context getContext(Thread thread)
    {
        return (Context) HASH.get(thread.toString());
    }

    /**
      * Associate the Context with the given Thread.
      */
    public static void putContext(Thread thread, Context cx)
    {
        HASH.put(thread.toString(), cx);
    }

    /**
      * Un-associate the Context with the given Thread.
      */
    public static void removeContext(Thread thread)
    {
        HASH.remove(thread.toString());
    }

    private static final ConcurrentHashMap HASH = new ConcurrentHashMap();
}

