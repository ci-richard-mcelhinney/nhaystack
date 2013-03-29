//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.server.storehouse;

import java.util.*;

import javax.baja.history.*;

import haystack.*;
import nhaystack.collection.*;

/**
  * HistoryStorehouseIterator is an Iterator created by a HistoryStorehouse.
  */
class HistoryStorehouseIterator implements Iterator
{
    HistoryStorehouseIterator(HistoryStorehouse storehouse)
    {
        this.storehouse = storehouse;
        this.iterator = new HistoryDbIterator(storehouse.service.getHistoryDb());
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

    public void remove() { throw new UnsupportedOperationException(); }

    public Object next()
    {
        if (!init) throw new IllegalStateException();
        if (nextDict == null) throw new IllegalStateException();

        HDict dict = nextDict;
        findNext();
        return dict;
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private void findNext()
    {
        nextDict = null;
        while (iterator.hasNext())
        {
            BHistoryConfig cfg = (BHistoryConfig) iterator.next();

            if (storehouse.isVisibleHistory(cfg))
            {
                nextDict = storehouse.createHistoryTags(cfg);
                break;
            }
        }
    }

////////////////////////////////////////////////////////////////
// Attribs
////////////////////////////////////////////////////////////////

    private final HistoryStorehouse storehouse;
    private final HistoryDbIterator iterator;

    private boolean init = false;
    private HDict nextDict;
}
