//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//
package nhaystack.server;

import java.io.*;
import javax.servlet.http.*;

import haystack.server.*;

import javax.baja.history.*;
import javax.baja.history.db.*;
import javax.baja.naming.*;
import javax.baja.space.*;
import javax.baja.sys.*;
import javax.baja.web.*;

/**
  * BNHaystackService makes a HaystackServer available.  
  */
public class BNHaystackService extends BAbstractService
{
    /*-
    class BNHaystackService
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackService(3531946816)1.0$ @*/
/* Generated Tue Oct 02 13:31:43 EDT 2012 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackService.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackService() { }

////////////////////////////////////////////////////////////////
// BIService
////////////////////////////////////////////////////////////////

    public Type[] getServiceTypes() { return SERVICE_TYPES; }

    public void serviceStarted() throws Exception { }

    public void serviceStopped() throws Exception { }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    public HaystackServer getHaystackServer() 
    { 
        return server; 
    }

    public BHistoryDatabase getHistoryDb() 
    { 
        if (historyDb == null)
            historyDb = (BHistoryDatabase) 
                BOrd.make("history:").resolve(this, null).get(); 

        return historyDb; 
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    public BIcon getIcon() { return ICON; }
    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/tag.png");

    private static final Type[] SERVICE_TYPES = new Type[] { TYPE };

    private final HaystackServer server = new HaystackServer(this);

    private BHistoryDatabase historyDb;    
}
