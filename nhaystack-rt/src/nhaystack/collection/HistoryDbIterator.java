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
import javax.baja.history.BHistoryConfig;
import javax.baja.history.BIHistory;
import javax.baja.history.db.BHistoryDatabase;

/**
  * HistoryDbIterator iterates through all
  * the BHistoryConfig entries in a BHistoryDatabase.
  */
public class HistoryDbIterator implements Iterator<BHistoryConfig>
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
    @Override
    public boolean hasNext()
    {
        return index < histories.length;
    }

    /**
      * Return the next BHistoryConfig in the iteration.
      */
    @Override
    public BHistoryConfig next()
    {
        return histories[index++].getConfig();
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

    private final BIHistory[] histories;
    private int index;
}
