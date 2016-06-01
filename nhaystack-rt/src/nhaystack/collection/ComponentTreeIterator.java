//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.collection;

import java.util.*;
import javax.baja.sys.*;
import javax.baja.nre.util.*;

/**
  * ComponentTreeIterator iterates through a tree
  * of BComponents, returning each component in turn.
  *
  * The iteration is done 'top-down':  At each level 
  * of the tree, first the parent is returned, and 
  * then the parent's children.
  */
public class ComponentTreeIterator implements Iterator
{
    public ComponentTreeIterator(BComponent root)
    {
        stack.push(new RootIterator(root));
    }

    /**
      * Return true if there are any more BComponents
      * left to iterate.
      */
    public boolean hasNext()
    {
        for (int i = stack.size()-1; i >= 0; i--)
        {
            Iterator itr = (Iterator) stack.get(i);
            if (itr.hasNext()) return true;
        }
        return false;
    }

    /**
      * Return the next BComponent in the iteration.
      */
    public Object next()
    {
        Iterator itr = (Iterator) stack.peek();
        while (!itr.hasNext())
        {
            stack.pop();
            itr = (Iterator) stack.peek();
        }

        BComponent comp = (BComponent) itr.next();
        stack.push(
            new CursorIterator(
                comp.getProperties(),
                BComponent.class));
        return comp;
    }

    /**
      * @throws UnsupportedOperationException
      */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

////////////////////////////////////////////////////////////////
// RootIterator
////////////////////////////////////////////////////////////////

    private static class RootIterator implements Iterator
    {
        private RootIterator(BComponent root) 
        { 
            this.root = root; 
            this.hasNext = true;
        }

        public boolean hasNext() { return hasNext; }

        public Object next()
        {
            if (!hasNext) throw new IllegalStateException();
            hasNext = false;
            return root;
        }

        public void remove() { throw new UnsupportedOperationException(); }

        private BComponent root;
        private boolean hasNext;
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    public int getStackDepth() { return stack.size(); }

    private final Array stack = new Array(Iterator.class);
}
