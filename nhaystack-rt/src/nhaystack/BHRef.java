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
import org.projecthaystack.HRef;
import org.projecthaystack.io.HZincReader;

/**
 *  BHRef wraps a Haystack HRef
 */
@NiagaraType
public final class BHRef
    extends BSimple
{
    /** * The default is HRef.make("null"). */
    public static final BHRef DEFAULT = new BHRef(HRef.nullRef);

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.BHRef(2979906276)1.0$ @*/
/* Generated Wed Nov 29 14:32:36 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHRef.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
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
        return new BHRef((HRef) zr.readVal());
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
        return ref.equals(that.ref);
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
        encoder.writeUTF(ref.toZinc()); 
    }

    /**
      * Decode from ZINC format
      */
    @Override
    public BObject decode(DataInput decoder) throws IOException
    { 
        HZincReader zr = new HZincReader(decoder.readUTF());
        return new BHRef((HRef) zr.readVal());
    }  

    /**
      * Encode to ZINC format
      */
    @Override
    public String encodeToString() throws IOException
    { 
        return ref.toZinc(); 
    }

    /**
      * Decode from ZINC format
      */
    @Override
    public BObject decodeFromString(String s) throws IOException
    { 
        HZincReader zr = new HZincReader(s);
        return new BHRef((HRef) zr.readVal());
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

    private final HRef ref;
}
