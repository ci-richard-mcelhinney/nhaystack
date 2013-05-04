//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   05 Feb 2013  Mike Jarmy  Creation
//

package nhaystack;

import java.io.*;
import java.util.*;

import javax.baja.sys.*;

import haystack.*;

/**
 *  BHTimeZone wraps a Haystack Timezone.
 */
public final class BHTimeZone
    extends BSimple
{
    public static BHTimeZone make(HTimeZone timeZone) 
    { 
        return new BHTimeZone(timeZone);
    }

    private BHTimeZone(HTimeZone timeZone) 
    { 
        this.timeZone = timeZone;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public int hashCode() 
    { 
        return timeZone.hashCode(); 
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHTimeZone)) return false;
        BHTimeZone that = (BHTimeZone) obj;
        return (timeZone.equals(that.timeZone));
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(timeZone.name);
    }

    public BObject decode(DataInput decoder) throws IOException
    { 
        return new BHTimeZone(HTimeZone.make(decoder.readUTF()));
    }  

    public String encodeToString() throws IOException
    { 
        return timeZone.name;
    }

    public BObject decodeFromString(String s) throws IOException
    { 
        return new BHTimeZone(HTimeZone.make(s));
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * Return the underlying timeZone.
      */
    public HTimeZone getTimeZone() { return timeZone; }

////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////// 

    /** * The default is HTimeZone.DEFAULT. */
    public static final BHTimeZone DEFAULT = new BHTimeZone(HTimeZone.DEFAULT);

    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHTimeZone.class);

    public static final Set TZ_REGIONS;
    static
    {
        TZ_REGIONS = new HashSet(Arrays.asList(
            new String[] {
              "Africa",     
              "America",    
              "Antarctica", 
              "Asia",       
              "Atlantic",   
              "Australia",  
              "Etc",        
              "Europe",     
              "Indian",     
              "Pacific" }));
    }

    private final HTimeZone timeZone;
}
