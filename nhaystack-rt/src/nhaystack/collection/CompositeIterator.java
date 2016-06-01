//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.collection;

import java.util.*;

/**
  * CompositeIterator iterates through an array of Iterators.
  */
public class CompositeIterator implements Iterator
{
    public CompositeIterator(Iterator[] iterators)
    {
        this.iterators = iterators;
        this.index = 0;
        findNext();
    }

////////////////////////////////////////////////////////////////
// Iterator
////////////////////////////////////////////////////////////////

    public boolean hasNext()
    {
        return nextObj != null;
    }

    public Object next()
    {
        Object obj = nextObj;
        findNext();
        return obj;
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
        nextObj = null;

        // Find the next iterator that has something to give us.
        // 'Usually' this will just be the current iterator.
        while (index < iterators.length && !iterators[index].hasNext())
            index++;

        // If we found a useful iterator, stash its next value.
        if (index < iterators.length)
            nextObj = iterators[index].next();
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private final Iterator[] iterators;
    private int index;
    private Object nextObj;
}
