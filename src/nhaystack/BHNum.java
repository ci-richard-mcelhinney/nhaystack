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
import org.projecthaystack.*;
import org.projecthaystack.io.*;

/**
 *  BHNum wraps a Haystack HNum
 */
public final class BHNum
    extends BSimple
{
    /**
      * Make a BHNum instance from an HNum.
      */
    public static BHNum make(HNum num) 
    { 
        return new BHNum(num);  
    }

    /**
      * Make a BHNum instance from a ZINC-encoded string.
      */
    public static BHNum make(String s) 
    { 
        HZincReader zr = new HZincReader(s);
        return new BHNum((HNum) zr.readScalar());
    }

    private BHNum(HNum num) 
    { 
        this.num = num;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    @Override
    public int hashCode() 
    { 
        return num.hashCode(); 
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHNum)) return false;
        BHNum that = (BHNum) obj;
        return (num.equals(that.num));
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    /**
      * Encode to ZINC format
      */
    @Override
    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(num.toZinc()); 
    }

    /**
      * Decode from ZINC format
      */
    @Override
    public BObject decode(DataInput decoder) throws IOException
    { 
        HZincReader zr = new HZincReader(decoder.readUTF());
        return new BHNum((HNum) zr.readScalar());
    }  

    /**
      * Encode to ZINC format
      */
    @Override
    public String encodeToString() throws IOException
    { 
        return num.toZinc(); 
    }

    /**
      * Decode from ZINC format
      */
    @Override
    public BObject decodeFromString(String s) throws IOException
    { 
        HZincReader zr = new HZincReader(s);
        return new BHNum((HNum) zr.readScalar());
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * Return the underlying HNum.
      */
    public HNum getNum() { return num; }

////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////// 

    /** * The default is HNum.ZERO. */
    public static final BHNum DEFAULT = new BHNum(HNum.ZERO);

    @Override
    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHNum.class);

    private final HNum num;
}
