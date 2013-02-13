//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Feb 2013  Mike Jarmy  Creation
//

package nhaystack;

import java.io.*;
import javax.baja.sys.*;
import haystack.*;
import haystack.io.*;

/**
 *  BHRef wraps a Haystack HRef
 */
public final class BHRef
    extends BSimple
{
    /**
      * Make a BHRef instance from an HRef.
      */
    public static BHRef make(HRef ref) 
    { 
        return new BHRef(ref);  
    }

    /**
      * Make a BHRef instance from a ZINC-encoded string.
      */
    public static BHRef make(String s) 
    { 
        HZincReader zr = new HZincReader(s);
        return new BHRef((HRef) zr.readScalar());
    }

    private BHRef(HRef ref) 
    { 
        this.ref = ref;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public int hashCode() 
    { 
        return ref.hashCode(); 
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHRef)) return false;
        BHRef that = (BHRef) obj;
        return (ref.equals(that.ref));
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    /**
      * Encode to ZINC format
      */
    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(ref.toZinc()); 
    }

    /**
      * Decode from ZINC format
      */
    public BObject decode(DataInput decoder) throws IOException
    { 
        HZincReader zr = new HZincReader(decoder.readUTF());
        return new BHRef((HRef) zr.readScalar());
    }  

    /**
      * Encode to ZINC format
      */
    public String encodeToString() throws IOException
    { 
        return ref.toZinc(); 
    }

    /**
      * Decode from ZINC format
      */
    public BObject decodeFromString(String s) throws IOException
    { 
        HZincReader zr = new HZincReader(s);
        return new BHRef((HRef) zr.readScalar());
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * Return the underlying HRef.
      */
    public HRef getRef() { return ref; }

////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////// 

    /** * The default is HRef.make("null"). */
    public static final BHRef DEFAULT = new BHRef(HRef.make("null"));

    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHRef.class);

    private final HRef ref;
}
