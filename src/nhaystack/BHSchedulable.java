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
 *  BHSchedulable
 */
public final class BHSchedulable
    extends BSimple
{
    public static BHSchedulable make(int priority) 
    { 
        return new BHSchedulable(priority);
    }

    private BHSchedulable(int priority) 
    { 
        this.priority = priority;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public int hashCode() 
    { 
        return priority; 
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHSchedulable)) return false;
        BHSchedulable that = (BHSchedulable) obj;
        return (priority == that.priority);
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeInt(priority);
    }

    public BObject decode(DataInput decoder) throws IOException
    { 
        return new BHSchedulable(decoder.readInt());
    }  

    public String encodeToString() throws IOException
    { 
        return String.valueOf(priority);
    }

    public BObject decodeFromString(String s) throws IOException
    { 
        return new BHSchedulable(Integer.parseInt(s));
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * Return the underlying Schedulable priority.
      */
    public int getPriority() { return priority; }

////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////// 

    public static final BHSchedulable DEFAULT = new BHSchedulable(-1);

    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHSchedulable.class);

    private final int priority;
}
