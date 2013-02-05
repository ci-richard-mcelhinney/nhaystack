//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.server.storehouse;

import java.util.*;

import javax.baja.control.*;
import javax.baja.naming.*;
import javax.baja.sys.*;

import haystack.*;
import nhaystack.collection.*;

/**
  * ConfigStorehouseIterator is an Iterator created by a ConfigStorehouse.
  * It builds an internal data structure as it is being traversed.
  */
public class ConfigStorehouseIterator implements Iterator
{
    ConfigStorehouseIterator(ConfigStorehouse storehouse)
    {
        this.storehouse = storehouse;
        this.iterator = new ComponentTreeIterator(
            (BComponent) BOrd.make("slot:/").resolve(storehouse.service, null).get());
    }

////////////////////////////////////////////////////////////////
// Object 
////////////////////////////////////////////////////////////////

    public String toString()
    {
        return "[ConfigStorehouseIterator: " + remotePoints + "]";
    }

////////////////////////////////////////////////////////////////
// Iterator
////////////////////////////////////////////////////////////////

    public boolean hasNext() 
    { 
        if (!init)
        {
            init = true;
            findNext();
        }
        return nextDict != null; 
    }

    public Object next()
    {
        if (!init) throw new IllegalStateException();
        if (nextDict == null) throw new IllegalStateException();

        HDict dict = nextDict;
        findNext();
        return dict;
    }

    public void remove() 
    { 
        throw new UnsupportedOperationException(); 
    }

////////////////////////////////////////////////////////////////
// package-scope
////////////////////////////////////////////////////////////////

    /**
      * NOTE: This method cannot be called until 
      * this Iterator has been exhausted!
      */
    BControlPoint getControlPoint(RemotePoint remote)
    {
        if (!init) throw new IllegalStateException(
            "ConfigStorehouseIterator has not been completely exhausted!");

        if (nextDict != null) throw new IllegalStateException(
            "ConfigStorehouseIterator has not been completely exhausted!");

        return (BControlPoint) remotePoints.get(remote);
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private void findNext()
    {
        nextDict = null;
        while (iterator.hasNext())
        {
            BComponent comp = (BComponent) iterator.next();

            if (storehouse.isVisibleComponent(comp))
            {
                nextDict = storehouse.createComponentTags(comp);

                // stash away all the remote points
                if (comp instanceof BControlPoint)
                {
                    BControlPoint point = (BControlPoint) comp;
                    if (point.getProxyExt().getType().is(RemotePoint.NIAGARA_PROXY_EXT)) 
                    {
                        RemotePoint remote = RemotePoint.fromControlPoint(point);
                        if (remote != null) remotePoints.put(remote, point);
                    }
                }

                break;
            }
        }
    }

////////////////////////////////////////////////////////////////
// Attribs
////////////////////////////////////////////////////////////////

    private final ConfigStorehouse storehouse;

    private final ComponentTreeIterator iterator;
    private final Map remotePoints = new HashMap();

    private boolean init = false;
    private HDict nextDict;
}

