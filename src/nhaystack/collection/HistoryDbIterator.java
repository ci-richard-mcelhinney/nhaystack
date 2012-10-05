//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   01 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.collection;

import java.util.*;
import javax.baja.history.*;
import javax.baja.history.db.*;

/**
  * HistoryDbIterator iterates through all
  * the BHistoryConfig entries in a BHistoryDatabase.
  */
public class HistoryDbIterator implements Iterator
{
    public HistoryDbIterator(BHistoryDatabase db)
    {
        this.histories = db.getHistories();
        this.index = 0;
    }

    /**
      * Return true if there are any more BHistoryConfigs
      * left to iterate.
      */
    public boolean hasNext()
    {
        return (index < histories.length);
    }

    /**
      * Return the next BHistoryConfig in the iteration.
      */
    public Object next()
    {
        return histories[index++].getConfig();
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

    private final BIHistory[] histories;
    private int index;
}
