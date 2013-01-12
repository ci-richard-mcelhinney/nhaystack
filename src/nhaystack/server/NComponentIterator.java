//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;
import javax.baja.control.*;
import javax.baja.history.*;
import javax.baja.sys.*;
import haystack.*;
import nhaystack.*;
import nhaystack.collection.*;

/**
  * NComponentIterator wraps an Iterator of BComponents,
  * and return an HDict for each BComponent that 
  * is either of a given type, or has been 
  * properly annotated with a BTags instance.
  */
public class NComponentIterator implements Iterator
{
    public NComponentIterator(
        NHServer server, 
        Iterator iterator,
        Type type)
    {
        this.server = server;
        this.iterator = iterator;
        this.type = type;
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
        return nextDict != null;
    }

    /**
      * Return an HDict representation 
      * of the current annotated BComponent.
      */
    public Object next()
    {
        if (nextDict == null) throw new IllegalStateException();

        HDict result = nextDict;
        findNext();
        return result;
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
        nextDict = null;
        while (iterator.hasNext())
        {
            BComponent comp = (BComponent) iterator.next();

            // Return an HDict for each BComponent that 
            // is either of a given type, or has been 
            // properly annotated with a BTags instance.
            if (comp.getType().is(type) ||
                (server.findAnnotatedTags(comp) != null))
            {
                nextDict = server.makeDict(comp);
                return;
            }
        }
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private final NHServer server;
    private final Iterator iterator;
    private final Type type;
    private HDict nextDict;
}
