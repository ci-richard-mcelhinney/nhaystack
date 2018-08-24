//
// Copyright (c) 2018, VRT Systems
//
// Based on BNHaystackLearnHistoriesJob
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Apr 2018  Stuart Longland  File Creation.

package nhaystack.driver.history.learn;

import java.util.*;

import javax.baja.history.*;
import javax.baja.history.db.*;
import javax.baja.job.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.nre.util.*;
import javax.baja.util.*;
import javax.baja.timezone.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.*;
import nhaystack.driver.*;
import javax.baja.nre.annotations.NiagaraType;

/**
  * BNHaystackLearnExpHistoriesJob is a Job which 'learns' the history points
  * that can be exported to a remote server.
  */
@NiagaraType
public class BNHaystackLearnExpHistoriesJob extends BSimpleJob
{

  /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
  /*@ $nhaystack.driver.history.learn.BNHaystackLearnExpHistoriesJob(2979906276)1.0$ @*/
  /* Generated Tue Apr 24 10:37:09 AEST 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

  ////////////////////////////////////////////////////////////////
  // Type
  ////////////////////////////////////////////////////////////////

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackLearnExpHistoriesJob.class);

  /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  public BNHaystackLearnExpHistoriesJob() {}

  public BNHaystackLearnExpHistoriesJob(BNHaystackServer server)
  {
    this.server = server;
  }

  public void doCancel(Context ctx)
  {
    super.doCancel(ctx);
    throw new JobCancelException();
  }

  public void run(Context ctx) throws Exception
  {
    NameGenerator nameGen = new NameGenerator();
    Map<String, BNHaystackHistoryExpEntry> entries =
	    new TreeMap<String, BNHaystackHistoryExpEntry>();

    BHistoryService service = (BHistoryService)Sys.getService(BHistoryService.TYPE);
    BHistoryDatabase db = service.getDatabase();
    BIHistory[] histPoints = db.getHistories();

    // Iterate over all histories, and just return the ones that have not been
    // exported to this server.
    for (int i = 0; i < histPoints.length; i++)
    {
      // TODO: filtering.
      BIHistory hist = histPoints[i];
      String name = hist.getNavName();
      if (name != null)
      {
        name = TextUtil.replace(name, " ", "_");
        name = SlotPath.escape(nameGen.makeUniqueName(name));

        BNHaystackHistoryExpEntry entry = new BNHaystackHistoryExpEntry();
        entry.setHistoryId(hist.getId());

        BHTimeZone tz = guessTimeZone(hist.getConfig().getTimeZone());
        if (tz != null) {
          // We have a time zone
          entry.setTz(tz);
        }

        entries.put(name, entry);
      }
    }

    Iterator it = entries.keySet().iterator();
    while (it.hasNext())
    {
      String name = (String) it.next();
      BNHaystackHistoryExpEntry entry = entries.get(name);
      add(name, entry);
    }
  }

  /**
   * Try to guess the correct Project Haystack time zone based on the Baja
   * (Olson/IANA) timezone ID given by BTimeZone.
   *
   * @return  Matching BHTimeZone if found.
   * @return  null if not found.
   */
  private static BHTimeZone guessTimeZone(BTimeZone bajaTz) {
    // The following is unceremoniously ripped from NHServer.java.
    String tzName = bajaTz.getId();

    // lop off the region, e.g. "America"
    int n = tzName.indexOf("/");
    if (n != -1)
    {
      String region = tzName.substring(0, n);
      if (BHTimeZone.TZ_REGIONS.contains(region))
        tzName = tzName.substring(n+1);
    }

    try
    {
      return BHTimeZone.make(HTimeZone.make(tzName));
    }
    catch (Exception e)
    {
      return null;
    }
  }

  ////////////////////////////////////////////////////////////////
  // Attributes
  ////////////////////////////////////////////////////////////////

  private BNHaystackServer server = null;
}
