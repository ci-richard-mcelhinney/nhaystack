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
import nhaystack.res.Unit;

/**
 *  BHUnit wraps a Haystack Unit symbol.
 */
@NiagaraType
public final class BHUnit
    extends BSimple
{
    /** * The default is "%".  */
    public static final BHUnit DEFAULT = new BHUnit("%");

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.BHUnit(2979906276)1.0$ @*/
/* Generated Wed Nov 29 14:36:23 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHUnit.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

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
        return symbol.equals(that.symbol);
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    @Override
    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(symbol);
    }

    @Override
    public BObject decode(DataInput decoder) throws IOException
    { 
        return new BHUnit(decoder.readUTF());
    }  

    @Override
    public String encodeToString() throws IOException
    { 
        return symbol;
    }

    @Override
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

    private final String symbol;
}
