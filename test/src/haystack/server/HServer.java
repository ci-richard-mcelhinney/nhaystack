//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Nov 2011  Brian Frank  Creation
//
package haystack.server;

import java.util.*;
import haystack.*;

/**
 * HServer is the interface between HServlet and a database of
 * tag based entities.  All methods on HServer must be thread safe.
 *
 * @see <a href='http://project-haystack.org/doc/Rest'>Project Haystack</a>
 */
public abstract class HServer extends HProj
{

//////////////////////////////////////////////////////////////////////////
// Operations
//////////////////////////////////////////////////////////////////////////

  /**
   * Return the operations supported by this database.
   */
  public abstract HOp[] ops();

  /**
   * Lookup an operation by name.  If no operation is registered
   * for the given name, then return null or raise UnknownNameException
   * base on check flag.
   */
  public HOp op(String name, boolean checked)
  {
    // lazily build lookup map
    if (this.opsByName == null)
    {
      HashMap map = new HashMap();
      HOp[] ops = ops();
      for (int i=0; i<ops.length; ++i)
      {
        HOp op = ops[i];
        if (map.get(op.name()) != null)
          System.out.println("WARN: duplicate HOp name: " + op.name());
        map.put(op.name(), op);
      }
      this.opsByName = map;
    }

    // lookup
    HOp op = (HOp)opsByName.get(name);
    if (op != null) return op;
    if (checked) throw new UnknownNameException(name);
    return null;
  }

//////////////////////////////////////////////////////////////////////////
// About
//////////////////////////////////////////////////////////////////////////

  /**
   * Get the about metadata which should contain following tags:
   */
  public final HDict about()
  {
    return new HDictBuilder()
        .add(onAbout())
        .add("haystackVersion", "2.0")
        .add("serverTime", HDateTime.now())
        .add("serverBootTime", this.bootTime)
        .add("tz", HTimeZone.DEFAULT.name)
        .toDict();
  }

  /**
   * Implementation hook for "about" method.
   * Should return these tags:
   *   - serverName: Str
   *   - productName: Str
   *   - productVersion: Str
   *   - productUri: Uri
   *   - moduleName: Str
   *   - moduleVersion: Str
   *   - moduleUri: Uri
   */
  protected abstract HDict onAbout();

//////////////////////////////////////////////////////////////////////////
// Reads
//////////////////////////////////////////////////////////////////////////

  /**
   * Default implementation routes to onReadById
   */
  protected HGrid onReadByIds(HIdentifier[] ids)
  {
    HDict[] recs = new HDict[ids.length];
    for (int i=0; i<ids.length; ++i)
      recs[i] = onReadById(ids[i]);
    return HGridBuilder.dictsToGrid(recs);
  }

  /**
   * Default implementation scans all records using "iterator"
   */
  protected HGrid onReadAll(String filter, int limit)
  {
    HFilter f = HFilter.make(filter);
    ArrayList acc = new ArrayList();
    for (Iterator it = iterator(); it.hasNext(); )
    {
      HDict rec = (HDict)it.next();
      if (f.include(rec, filterPather))
      {
        acc.add(rec);
        if  (acc.size() >= limit) break;
      }
    }
    return HGridBuilder.dictsToGrid((HDict[])acc.toArray(new HDict[acc.size()]));
  }

  private HFilter.Pather filterPather = new HFilter.Pather()
  {
    public HDict find(String id) { return find(id); }
  };

  /**
   * Implementation hook to iterate every entity record in
   * the database as a HDict.
   */
  protected abstract Iterator iterator();

//////////////////////////////////////////////////////////////////////////
// Navigation
//////////////////////////////////////////////////////////////////////////

  /**
   * Return navigation children for given navId.
   */
  public HGrid nav(String navId)
  {
    return onNav(navId);
  }

  /**
   * Return navigation tree children for given navId.
   * The grid must define the "navId" column.
   */
  protected abstract HGrid onNav(String navId);

  /**
   * Read a record from the database using a navigation path.
   * If not found then return null or raise UnknownRecException
   * base on checked flag.
   */
  public HDict navReadByUri(HUri uri, boolean checked)
  {
    HDict rec = onNavReadByUri(uri);
    if (rec != null) return rec;
    if (checked) throw new UnknownRecException(uri.toString());
    return null;
  }

  /**
   * Implementation hook for navReadByUri.  Return null if not
   * found.  Do NOT raise any exceptions.
   */
  protected abstract HDict onNavReadByUri(HUri uri);

//////////////////////////////////////////////////////////////////////////
// Watches
//////////////////////////////////////////////////////////////////////////

  /**
   * Create a new watch with an empty subscriber list.  The dis
   * string is a debug string to keep track of who created the watch.
   */
  public final HWatch watchOpen(String dis)
  {
    dis = dis.trim();
    if (dis.length() == 0) throw new IllegalArgumentException("dis is empty");
    return onWatchOpen(dis);
  }

  /**
   * List the open watches.
   */
  public final HWatch[] watches()
  {
    return onWatches();
  }

  /**
   * Lookup a watch by its unique identifier.  If not found then
   * raise UnknownWatchErr or return null based on checked flag.
   */
  public HWatch watch(String id, boolean checked)
  {
    HWatch w = onWatch(id);
    if (w != null) return w;
    if (checked) throw new UnknownWatchException(id);
    return null;
  }

  /**
   * Implementation hook for watchOpen.
   */
  protected abstract HWatch onWatchOpen(String dis);

  /**
   * Implementation hook for watches.
   */
  protected abstract HWatch[] onWatches();

  /**
   * Implementation hook for watch lookup, return null if not found.
   */
  protected abstract HWatch onWatch(String id);

//////////////////////////////////////////////////////////////////////////
// Point Writes
//////////////////////////////////////////////////////////////////////////

  /**
   * Return priority array for writable point identified by id.
   * The grid contains 17 rows with following columns:
   *   - level: number from 1 - 17 (17 is default)
   *   - levelDis: human description of level
   *   - val: current value at level or null
   *   - who: who last controlled the value at this level
   */
  public final HGrid pointWriteArray(HRef id)
  {
    // lookup entity
    HDict rec = readById(id);

    // check that entity has "writable" tag
    if (rec.missing("writable"))
      throw new UnknownNameException("Rec missing 'writable' tag: " + rec.dis());

    return onPointWriteArray(rec);
  }

  /**
   * Write to the given priority array level.
   */
  public final void pointWrite(HRef id, int level, HVal val, String who, HNum dur)
  {
    // argument checks
    if (level < 1 || level > 17) throw new IllegalArgumentException("Invalid level 1-17: " + level);
    if (who == null) throw new IllegalArgumentException("who is null");

    // lookup entity
    HDict rec = readById(id);

    // check that entity has "writable" tag
    if (rec.missing("writable"))
      throw new UnknownNameException("Rec missing 'writable' tag: " + rec.dis());

    onPointWrite(rec, level, val, who, dur);
  }

  /**
   * Implementation hook for pointWriteArray
   */
  protected abstract HGrid onPointWriteArray(HDict rec);

  /**
   * Implementation hook for pointWrite
   */
  protected abstract void onPointWrite(HDict rec, int level, HVal val, String who, HNum dur);

//////////////////////////////////////////////////////////////////////////
// History
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
  public final HGrid hisRead(HRef id, Object range)
  {
    // lookup entity
    HDict rec = readById(id);

    // check that entity has "his" tag
    if (rec.missing("his"))
      throw new UnknownNameException("Rec missing 'his' tag: " + rec.dis());

    // lookup "tz" on entity
    HTimeZone tz = null;
    if (rec.has("tz")) tz = HTimeZone.make(rec.getStr("tz"), false);
    if (tz == null)
      throw new UnknownNameException("Rec missing or invalid 'tz' tag: " + rec.dis());

    // check or parse date range
    HDateTimeRange r = null;
    if (range instanceof HDateTimeRange)
    {
      r = (HDateTimeRange)range;
    }
    else
    {
      try
      {
        r = HDateTimeRange.make(range.toString(), tz);
      }
      catch (ParseException e)
      {
        throw new ParseException("Invalid date time range: " + range);
      }
    }

    // checking
    if (!r.start.tz.equals(tz))
      throw new RuntimeException("range.tz != rec: " + r.start.tz + " != " + tz);

    // route to subclass
    HHisItem[] items = onHisRead(rec, r);

    // check items
    if (items.length > 0)
    {
      if (r.start.millis() >= items[0].ts.millis()) throw new IllegalStateException("start range not met");
      if (r.end.millis() < items[items.length-1].ts.millis()) throw new IllegalStateException("end range not met");
    }

    // build and return result grid
    HDict meta = new HDictBuilder()
      .add("id", id)
      .add("hisStart", r.start)
      .add("hisEnd", r.end)
      .toDict();
    return HGridBuilder.hisItemsToGrid(meta, items);
  }

  /**
   * Implementation hook for hisRead.  The items must be exclusive
   * of start and inclusive of end time.
   */
  protected abstract HHisItem[] onHisRead(HDict rec, HDateTimeRange range);

  /**
   * Write a set of history time-series data to the given point record.
   * The record must already be defined and must be properly tagged as
   * a historized point.  The timestamp timezone must exactly match the
   * point's configured "tz" tag.  If duplicate or out-of-order items are
   * inserted then they must be gracefully merged.
   */
  public final void hisWrite(HRef id, HHisItem[] items)
  {
    // lookup entity
    HDict rec = readById(id);

    // check that entity has "his" tag
    if (rec.missing("his"))
      throw new UnknownNameException("Entity missing 'his' tag: " + rec.dis());

    // lookup "tz" on entity
    HTimeZone tz = null;
    if (rec.has("tz")) tz = HTimeZone.make(rec.getStr("tz"), false);
    if (tz == null)
      throw new UnknownNameException("Rec missing or invalid 'tz' tag: " + rec.dis());

    // check tz of items
    if (items.length == 0) return;
    for (int i=0; i<items.length; ++i)
      if (!items[i].ts.tz.equals(tz)) throw new RuntimeException("item.tz != rec.tz: " + items[i].ts.tz + " != " + tz);

    // route to subclass
    onHisWrite(rec, items);
  }

  /**
   * Implementation hook for onHisWrite.
   */
  protected abstract void onHisWrite(HDict rec, HHisItem[] items);

//////////////////////////////////////////////////////////////////////////
// Actions
//////////////////////////////////////////////////////////////////////////

  /**
   * Invoke an action identified by id and action.
   */
  public final HGrid invokeAction(HRef id, String action, HDict args)
  {
    // lookup entity
    HDict rec = readById(id);

    // route to subclass
    return onInvokeAction(rec, action, args);
  }

  /**
   * Implementation hook for invokeAction
   */
  protected abstract HGrid onInvokeAction(HDict rec, String action, HDict args);

//////////////////////////////////////////////////////////////////////////
// Fields
//////////////////////////////////////////////////////////////////////////

 final HDateTime bootTime = HDateTime.now();
 private HashMap opsByName;

}
