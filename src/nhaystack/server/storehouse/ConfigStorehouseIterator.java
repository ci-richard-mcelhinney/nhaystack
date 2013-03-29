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
import nhaystack.server.*;

/**
  * ConfigStorehouseIterator is an Iterator created by a ConfigStorehouse.
  */
class ConfigStorehouseIterator implements Iterator
{
    ConfigStorehouseIterator(ConfigStorehouse storehouse)
    {
        this.storehouse = storehouse;
        this.iterator = new ComponentTreeIterator(
            (BComponent) BOrd.make("slot:/").resolve(storehouse.service, null).get());
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
                break;
            }
        }
    }

////////////////////////////////////////////////////////////////
// Attribs
////////////////////////////////////////////////////////////////

    private final ConfigStorehouse storehouse;
    private final ComponentTreeIterator iterator;

    private boolean init = false;
    private HDict nextDict;
}

