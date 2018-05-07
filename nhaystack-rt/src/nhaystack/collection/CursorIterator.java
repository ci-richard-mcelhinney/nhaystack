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
import javax.baja.sys.BValue;
import javax.baja.sys.Slot;
import javax.baja.sys.SlotCursor;

/**
  * CursorIterator wraps a SlotCursor inside an Iterator
  */
public class CursorIterator <T extends BValue> implements Iterator<T>
{
    /**
      * Convenience for <code>new CursorIterator(cursor, null)</code>.
      */
    public CursorIterator(SlotCursor<? extends Slot> cursor)
    {
        this(cursor, null);
    }

    /**
      * @param cursor The cursor to wrap as an Iterator.
      * @param cls The class of the objects that we want the
      *            cursor to return, or null to return everything.
      */
    public CursorIterator(SlotCursor<? extends Slot> cursor, Class<? extends T> cls)
    {
        this.cursor = cursor;
        this.cls = cls;

        this.hasNext = (cls == null) ?  
            cursor.next() : cursor.next(cls);
    }

////////////////////////////////////////////////////////////////
// Iterator
////////////////////////////////////////////////////////////////

    /**
      * Return true if the underyling cursor has any more entries.
      */
    @Override
    public boolean hasNext()
    {
        return hasNext;
    }

    /**
      * Return the next entry in the underlying cursor.
      */
    @SuppressWarnings("unchecked")
    @Override
    public T next()
    {
        BValue obj = cursor.get();

        hasNext = (cls == null) ?  
            cursor.next() : cursor.next(cls);

        // This unchecked exception is safe because the cursor.next method
        // will queue up an object of type T that will be returned by get.
        return (T)obj;
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
// Attributes
////////////////////////////////////////////////////////////////

    private final SlotCursor<? extends Slot> cursor;
    private final Class<? extends BValue> cls;
    private boolean hasNext;
}
