//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Oct 2012  Mike Jarmy     Creation
//   07 May 2018  Eric Anderson  Added generics and missing @Override annotations
//
package nhaystack.collection;

import java.util.Iterator;

/**
  * CompositeIterator iterates through an array of Iterators.
  */
public class CompositeIterator implements Iterator<Object>
{
    public CompositeIterator(Iterator<?>[] iterators)
    {
        this.iterators = iterators;
        this.index = 0;
        findNext();
    }

////////////////////////////////////////////////////////////////
// Iterator
////////////////////////////////////////////////////////////////

    @Override
    public boolean hasNext()
    {
        return nextObj != null;
    }

    @Override
    public Object next()
    {
        Object obj = nextObj;
        findNext();
        return obj;
    }

    /**
      * @throws UnsupportedOperationException
      */
    @Override
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

    private final Iterator<?>[] iterators;
    private int index;
    private Object nextObj;
}
