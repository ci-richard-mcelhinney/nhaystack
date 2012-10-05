//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;
import javax.baja.sys.*;
import haystack.*;
import nhaystack.*;

/**
  * NHServerIterator wraps an Iterator of BComponents,
  * and return an HDict for each BComponent that has been 
  * properly annotated with a BTags instance.
  */
public class NHServerIterator implements Iterator
{
    public NHServerIterator(
        NHServer server, 
        Iterator iterator)
    {
        this.server = server;
        this.iterator = iterator;
        findNext();
    }

////////////////////////////////////////////////////////////////
// Iterator
////////////////////////////////////////////////////////////////

    /**
      * Return true if there are any more BComponents 
      * that have been annotated with a BTags slot.
      */
    public boolean hasNext()
    {
        return nextComp != null;
    }

    /**
      * Return an HDict representation 
      * of the current annotated BComponent.
      */
    public Object next()
    {
        HDict dict = server.makeDict(nextComp);
        findNext();
        return dict;
    }

    /**
      * @throws UnsupportedOperationException
      */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private void findNext()
    {
        nextComp = null;
        while (iterator.hasNext())
        {
            BComponent comp = (BComponent) iterator.next();

            BTags tags = server.findTags(comp);
            if (tags != null)
            {
                nextComp = comp;
                return;
            }
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private final NHServer server;
    private final Iterator iterator;
    private BComponent nextComp;
}
