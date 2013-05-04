//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 May 2013  Mike Jarmy  Creation
//
package nhaystack.server;

import javax.baja.sys.*;
import javax.baja.util.*;

import nhaystack.*;

/**
  * BTimeZoneAliasFolder contains BTimeZoneAliases
  */
public class BTimeZoneAliasFolder extends BFolder
{
    /*-
    class BTimeZoneAliasFolder
    {
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BTimeZoneAliasFolder(528550170)1.0$ @*/
/* Generated Sat May 04 11:48:21 GMT-05:00 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTimeZoneAliasFolder.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public boolean isChildLegal(BComponent child)
    {
        return (child instanceof BTimeZoneAlias);
    }

    public BTimeZoneAlias[] getAliases()
    {
        return (BTimeZoneAlias[]) getChildren(BTimeZoneAlias.class);
    }
}
