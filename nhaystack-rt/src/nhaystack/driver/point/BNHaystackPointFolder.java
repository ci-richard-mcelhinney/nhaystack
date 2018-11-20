//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.driver.point;

import javax.baja.driver.point.BPointFolder;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.driver.BNHaystackNetwork;
import nhaystack.driver.BNHaystackServer;

/**
  * BNHaystackPointFolder organizes haystack points underneath a
  * BNHaystackPointDeviceExt.
  */
@NiagaraType
public class BNHaystackPointFolder extends BPointFolder
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackPointFolder(2979906276)1.0$ @*/
/* Generated Sat Nov 18 17:28:57 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackPointFolder.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public final BNHaystackNetwork getNHaystackNetwork()
    {
        return (BNHaystackNetwork)getNetwork();
    }

    public final BNHaystackServer getNHaystackServer()
    {
        return (BNHaystackServer) getDevice();
    }

    @Override
    public boolean isParentLegal(BComponent comp)
    {
        return 
            (comp instanceof BNHaystackPointDeviceExt) ||
            (comp instanceof BNHaystackPointFolder);
//            (comp instanceof BNHaystackPointFolder) ||
//            (comp instanceof BNHaystackNetwork);
    }
}
