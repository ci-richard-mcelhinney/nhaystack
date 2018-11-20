//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   26 Nov 2014  Mike Jarmy     Creation
//   10 May 2018  Eric Anderson  Added use of generics
//
package nhaystack.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.baja.sys.Context;

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
        return HASH.get(thread.toString());
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

    private static final Map<String, Context> HASH = new ConcurrentHashMap<>();
}

