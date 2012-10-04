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

/**
  * CursorIterator wraps a Cursor inside an Iterator
  */
public class CursorIterator implements Iterator
{
    /**
      * Convenience for <code>new CursorIterator(cursor, null)</code>.
      */
    public CursorIterator(Cursor cursor)
    {
        this(cursor, null);
    }

    /**
      * @param cursor The cursor to wrap as an Iterator.
      * @param cls The class of the objects that we want the
      *            cursor to return, or null to return everything.
      */
    public CursorIterator(Cursor cursor, Class cls)
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
    public boolean hasNext()
    {
        return hasNext;
    }

    /**
      * Return the next entry in the underlying cursor.
      */
    public Object next()
    {
        Object obj = cursor.get();

        hasNext = (cls == null) ?  
            cursor.next() : cursor.next(cls);

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
// Attributes
////////////////////////////////////////////////////////////////

    private final Cursor cursor;
    private final Class cls;
    private boolean hasNext;
}
