//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   11 Jul 2011  Brian Frank  Creation
//   26 Sep 2012  Brian Frank  Revamp original code
//
package org.projecthaystack;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import org.projecthaystack.*;
import org.projecthaystack.io.*;

/**
 * HProj is the common interface for HClient and HServer to provide
 * access to a database tagged entity records.
 *
 * @see <a href='http://project-haystack.org/doc/TagModel'>Project Haystack</a>
 */
public abstract class HProj
{

//////////////////////////////////////////////////////////////////////////
// Operations
//////////////////////////////////////////////////////////////////////////

  /**
   * Get the summary "about" information.
   */
  public abstract HDict about();

//////////////////////////////////////////////////////////////////////////
// Read by id
//////////////////////////////////////////////////////////////////////////

  /**
   * Convenience for "readById(id, true)"
   */
  public final HDict readById(HRef id)
  {
    return readById(id, true);
  }

  /**
   * Call "read" to lookup an entity record by it's unique identifier.
   * If not found then return null or throw an UnknownRecException based
   * on checked.
   */
  public final HDict readById(HRef id, boolean checked)
  {
    HDict rec = onReadById(id);
    if (rec != null) return rec;
    if (checked) throw new UnknownRecException(id);
    return null;
  }

  /**
   * Convenience for "readByIds(ids, true)"
   */
  public final HGrid readByIds(HRef[] ids)
  {
    return readByIds(ids, true);
  }

  /**
   * Read a list of entity records by their unique identifier.
   * Return a grid where each row of the grid maps to the respective
   * id array (indexes line up).  If checked is true and any one of the
   * ids cannot be resolved then raise UnknownRecException for first id
   * not resolved.  If checked is false, then each id not found has a
   * row where every cell is null.
   */
  public final HGrid readByIds(HRef[] ids, boolean checked)
  {
    HGrid grid = onReadByIds(ids);
    if (checked)
    {
      for (int i=0; i<grid.numRows(); ++i)
        if (grid.row(i).missing("id")) throw new UnknownRecException(ids[i]);
    }
    return grid;
  }

  /**
   * Subclass hook for readById, return null if not found.
   */
  protected abstract HDict onReadById(HRef id);

  /**
   * Subclass hook for readByIds, return rows with nulls cells
   * for each id not found.
   */
  protected abstract HGrid onReadByIds(HRef[] ids);

//////////////////////////////////////////////////////////////////////////
// Read by filter
//////////////////////////////////////////////////////////////////////////

  /**
   * Convenience for "read(filter, true)".
   */
  public final HDict read(String filter)
  {
    return read(filter, true);
  }

  /**
   * Query one entity record that matches the given filter.  If
   * there is more than one record, then it is undefined which one is
   * returned.  If there are no matches than return null or raise
   * UnknownRecException based on checked flag.
   */
  public final HDict read(String filter, boolean checked)
  {
    HGrid grid = readAll(filter, 1);
    if (grid.numRows() > 0) return grid.row(0);
    if (checked) throw new UnknownRecException(filter);
    return null;
  }

  /**
   * Convenience for "readAll(filter, max)".
   */
  public final HGrid readAll(String filter)
  {
    return readAll(filter, Integer.MAX_VALUE);
  }

  /**
   * Call "read" to query every entity record that matches given filter.
   * Clip number of results by "limit" parameter.
   */
  public final HGrid readAll(String filter, int limit)
  {
    return onReadAll(filter, limit);
  }

  /**
   * Subclass hook for read and readAll.
   */
  protected abstract HGrid onReadAll(String filter, int limit);

//////////////////////////////////////////////////////////////////////////
// Watches
//////////////////////////////////////////////////////////////////////////

  /**
   * Create a new watch with an empty subscriber list.  The dis
   * string is a debug string to keep track of who created the watch.
   * Pass the desired lease time or null to use default.
   */
  public abstract HWatch watchOpen(String dis, HNum lease);

  /**
   * List the open watches.
   */
  public abstract HWatch[] watches();

  /**
   * Convenience for "watch(id, true)"
   */
  public final HWatch watch(String id) { return watch(id, true); }

  /**
   * Lookup a watch by its unique identifier.  If not found then
   * raise UnknownWatchErr or return null based on checked flag.
   */
  public abstract HWatch watch(String id, boolean checked);

//////////////////////////////////////////////////////////////////////////
// Historian
//////////////////////////////////////////////////////////////////////////

  /**
   * Read history time-series data for given record and time range. The
   * items returned are exclusive of start time and inclusive of end time.
   * Raise exception if id does not map to a record with the required tags
   * "his" or "tz".  The range may be either a String or a HDateTimeRange.
   * If HTimeDateRange is passed then must match the timezone configured on
   * the history record.  Otherwise if a String is passed, it is resolved
   * relative to the history record's timezone.
   */
  public abstract HGrid hisRead(HRef id, Object range);

  /**
   * Write a set of history time-series data to the given point record.
   * The record must already be defined and must be properly tagged as
   * a historized point.  The timestamp timezone must exactly match the
   * point's configured "tz" tag.  If duplicate or out-of-order items are
   * inserted then they must be gracefully merged.
   */
  public abstract void hisWrite(HRef id, HHisItem[] items);

}