//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   05 Feb 2013  Mike Jarmy  Creation
//

package nhaystack;

import java.io.*;
import javax.baja.sys.*;
import nhaystack.res.*;

/**
 *  BHFloor
 */
public final class BHFloor
    extends BSimple
{
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

    public int hashCode() 
    { 
        return floor.hashCode(); 
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHFloor)) return false;
        BHFloor that = (BHFloor) obj;
        return (floor.equals(that.floor));
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(floor);
    }

    public BObject decode(DataInput decoder) throws IOException
    { 
        return new BHFloor(decoder.readUTF());
    }  

    public String encodeToString() throws IOException
    { 
        return String.valueOf(floor);
    }

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

    public static final BHFloor DEFAULT = new BHFloor("");

    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHFloor.class);

    private final String floor;
}
