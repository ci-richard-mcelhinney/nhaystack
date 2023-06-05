//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Feb 2013  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations,
//                               replacing deprecated readScalar with readVal
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
import org.projecthaystack.HNum;
import org.projecthaystack.io.HZincReader;

/**
 *  BHNum wraps a Haystack HNum
 */
@NiagaraType
public final class BHNum
    extends BSimple
{
    /** * The default is HNum.ZERO. */
    public static final BHNum DEFAULT = new BHNum(HNum.ZERO);

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.BHNum(2979906276)1.0$ @*/
/* Generated Wed Nov 29 14:36:23 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHNum.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
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
        return new BHNum((HNum) zr.readVal());
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

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHNum)) return false;
        BHNum that = (BHNum) obj;
        return num.equals(that.num);
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
        return new BHNum((HNum) zr.readVal());
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
        return new BHNum((HNum) zr.readVal());
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

    private final HNum num;
}
