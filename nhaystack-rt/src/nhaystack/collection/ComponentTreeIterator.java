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
import java.util.Stack;
import javax.baja.sys.BComponent;

/**
  * ComponentTreeIterator iterates through a tree
  * of BComponents, returning each component in turn.
  *
  * The iteration is done 'top-down':  At each level 
  * of the tree, first the parent is returned, and 
  * then the parent's children.
  */
public class ComponentTreeIterator implements Iterator<BComponent>
{
    public ComponentTreeIterator(BComponent root)
    {
        stack.push(new RootIterator(root));
    }

    /**
      * Return true if there are any more BComponents
      * left to iterate.
      */
    @Override
    public boolean hasNext()
    {
        for (int i = stack.size()-1; i >= 0; i--)
        {
            Iterator<BComponent> itr = stack.get(i);
            if (itr.hasNext()) return true;
        }
        return false;
    }

    /**
      * Return the next BComponent in the iteration.
      */
    @Override
    public BComponent next()
    {
        Iterator<BComponent> itr = stack.peek();
        while (!itr.hasNext())
        {
            stack.pop();
            itr = stack.peek();
        }

        BComponent comp = itr.next();
        stack.push(
            new CursorIterator<BComponent>(
                comp.getProperties(),
                BComponent.class));
        return comp;
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
// RootIterator
////////////////////////////////////////////////////////////////

    private static class RootIterator implements Iterator<BComponent>
    {
        private RootIterator(BComponent root) 
        { 
            this.root = root; 
            this.hasNext = true;
        }

        @Override
        public boolean hasNext()
        {
            return hasNext;
        }

        @Override
        public BComponent next()
        {
            if (!hasNext) throw new IllegalStateException();
            hasNext = false;
            return root;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        private final BComponent root;
        private boolean hasNext;
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    public int getStackDepth() { return stack.size(); }

    private final Stack<Iterator<BComponent>> stack = new Stack<>();
}
