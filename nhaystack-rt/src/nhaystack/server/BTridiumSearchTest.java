//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 May 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import javax.baja.naming.BOrd;
import javax.baja.sys.*;
import javax.baja.search.*;
import javax.baja.tag.Entity;
import javax.baja.tag.Tag;
import javax.baja.tag.Tags;
import javax.baja.util.BFolder;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

/**
  * BTimeZoneAliasFolder contains BTimeZoneAliases
  */
public class BTridiumSearchTest extends BFolder
{
    /*-
    class BTridiumSearchTest
    {
      properties
      {}
      actions
      {
        search(search: BString)
          -- Lookup an entity record by it's unique identifier.
          flags { operator }
          default {[ BString.DEFAULT ]}
      }
      topics
      {}
    }
    -*/


/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BTridiumSearchTest(4237832510)1.0$ @*/
/* Generated Fri Nov 18 13:26:33 AEDT 2016 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Action "search"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code search} action.
   * Lookup an entity record by it's unique identifier.
   * @see #search(BString parameter)
   */
  public static final Action search = newAction(Flags.OPERATOR, BString.DEFAULT, null);
  
  /**
   * Invoke the {@code search} action.
   * Lookup an entity record by it's unique identifier.
   * @see #search
   */
  public void search(BString parameter) { invoke(search, parameter, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTridiumSearchTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/



  public void doSearch(BString qry) throws Exception
  {

    BOrd ordQry = BOrd.make("neql:" + qry);
    System.out.println("searching: " + ordQry.encodeToString());
    BSearchService search = (BSearchService) Sys.getService(BSearchService.TYPE);
    BSearchParams params = new BSearchParams(ordQry, Sys.getStation());

    BOrd ordToTask = search.search(params);
    BSearchResultSet results = null;
    while (results == null || !results.getResultsComplete())
    {
      Thread.sleep(100); // Give the search some time to complete

      // This code asks for all available results at this time, but you could ask for
      // chunks of results by adjusting the startIndex and maxResults arguments
      BResultsRequest resultsRequest = BResultsRequest.make(ordToTask, /*startIndex*/0, /*maxResults*/-1);
      results = search.retrieveResults(resultsRequest);
    }

    results.streamResults().forEach((res) -> {
      BComponent c = (BComponent)(((BSearchResult)res).getOrd().resolve(this).get());
      Iterator<Tag> ti = c.tags().iterator();
      System.out.print("### new entity: ");
      while (ti.hasNext())
      {
        Tag t = ti.next();
        try
        {
          System.out.print(t.getId() + ":" + t.getValue().encodeToString());
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      System.out.println();
    });
  }

}
