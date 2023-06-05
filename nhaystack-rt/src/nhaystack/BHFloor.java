//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   05 Feb 2013  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations
//

package nhaystack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BObject;
import javax.baja.sys.BSimple;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

/**
 *  BHFloor
 */
@NiagaraType
public final class BHFloor
    extends BSimple
{
    public static final BHFloor DEFAULT = new BHFloor("");

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.BHFloor(2979906276)1.0$ @*/
/* Generated Wed Nov 29 14:36:23 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHFloor.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public static BHFloor make(String floor) 
    { 
        return new BHFloor(floor);
    }

    private BHFloor(String floor) 
    { 
        this.floor = floor;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    @Override
    public int hashCode() 
    { 
        return floor.hashCode(); 
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHFloor)) return false;
        BHFloor that = (BHFloor) obj;
        return floor.equals(that.floor);
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    @Override
    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(floor);
    }

    @Override
    public BObject decode(DataInput decoder) throws IOException
    { 
        return new BHFloor(decoder.readUTF());
    }  

    @Override
    public String encodeToString() throws IOException
    { 
        return String.valueOf(floor);
    }

    @Override
    public BObject decodeFromString(String s) throws IOException
    { 
        return new BHFloor(s);
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    public String getFloor() { return floor; }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private final String floor;
}
