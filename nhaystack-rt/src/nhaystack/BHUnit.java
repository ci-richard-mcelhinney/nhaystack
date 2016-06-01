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
 *  BHUnit wraps a Haystack Unit symbol.
 */
public final class BHUnit
    extends BSimple
{
    public static BHUnit make(String symbol) 
    { 
        return new BHUnit(symbol);
    }

    public static BHUnit make(Unit unit) 
    { 
        return new BHUnit(unit.symbol);
    }

    private BHUnit(String symbol) 
    { 
        this.symbol = symbol;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public int hashCode() 
    { 
        return symbol.hashCode(); 
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHUnit)) return false;
        BHUnit that = (BHUnit) obj;
        return (symbol.equals(that.symbol));
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(symbol);
    }

    public BObject decode(DataInput decoder) throws IOException
    { 
        return new BHUnit(decoder.readUTF());
    }  

    public String encodeToString() throws IOException
    { 
        return symbol;
    }

    public BObject decodeFromString(String s) throws IOException
    { 
        return new BHUnit(s);
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * Return the underlying Unit symbol.
      */
    public String getSymbol() { return symbol; }

////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////// 

    /** * The default is "%".  */
    public static final BHUnit DEFAULT = new BHUnit("%");

    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHUnit.class);

    private final String symbol;
}
