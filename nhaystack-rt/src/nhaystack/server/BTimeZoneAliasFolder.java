//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 May 2013  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotation
//
package nhaystack.server;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BFolder;

/**
  * BTimeZoneAliasFolder contains BTimeZoneAliases
  */
@NiagaraType
public class BTimeZoneAliasFolder extends BFolder
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BTimeZoneAliasFolder(2979906276)1.0$ @*/
/* Generated Sat Nov 18 21:07:21 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BTimeZoneAliasFolder.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    public boolean isChildLegal(BComponent child)
    {
        return child instanceof BTimeZoneAlias;
    }

    public BTimeZoneAlias[] getAliases()
    {
        return getChildren(BTimeZoneAlias.class);
    }
}
