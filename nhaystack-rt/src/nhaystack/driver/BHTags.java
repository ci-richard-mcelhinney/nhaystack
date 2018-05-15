//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   18 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.driver;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BIcon;
import javax.baja.sys.BObject;
import javax.baja.sys.BSimple;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import org.projecthaystack.HDict;
import org.projecthaystack.io.HZincReader;

/**
 * BHTags wraps a Haystack HDict.  BHTags is similar to BHDict,
 * except that it is intended to always be read-only. 
 * <p/>
 * BHTags is used to keep track of the tags that were imported when points or 
 * histories were added to a BNHaystackServer.
 */
@NiagaraType
public final class BHTags
    extends BSimple
{
    /** The default is HDict.EMPTY. */
    public static final BHTags DEFAULT = new BHTags(HDict.EMPTY);

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BHTags(2979906276)1.0$ @*/
/* Generated Wed Nov 29 14:36:23 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHTags.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
    /**
      * Make a BHTags instance from an HDict.
      */
    public static BHTags make(HDict dict) 
    { 
        return new BHTags(dict);  
    }

    /**
      * Make a BHTags instance from a ZINC-encoded string.
      */
    public static BHTags make(String s) 
    { 
        HZincReader zr = new HZincReader(s);
        return new BHTags(zr.readDict());
    }

    private BHTags(HDict dict) 
    { 
        this.dict = dict;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public int hashCode() 
    { 
        return dict.hashCode(); 
    }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        if (!(obj instanceof BHTags)) return false;
        BHTags that = (BHTags) obj;
        return dict.equals(that.dict);
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    @Override
    public String toString(Context context)
    {
        return dict.toZinc();
    }

    /**
      * Encode to ZINC format
      */
    @Override
    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(dict.toZinc()); 
    }

    /**
      * Decode from ZINC format
      */
    @Override
    public BObject decode(DataInput decoder) throws IOException
    { 
        HZincReader zr = new HZincReader(decoder.readUTF());
        return new BHTags(zr.readDict());
    }  

    /**
      * Encode to ZINC format
      */
    @Override
    public String encodeToString() throws IOException
    { 
        return dict.toZinc(); 
    }

    /**
      * Decode from ZINC format
      */
    @Override
    public BObject decodeFromString(String s) throws IOException
    { 
        HZincReader zr = new HZincReader(s);
        return new BHTags(zr.readDict());
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * Return the underlying HDict.
      */
    public HDict getDict() { return dict; }

////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////// 

    @Override
    public BIcon getIcon() { return ICON; }
    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/tag.png");

    private final HDict dict;
}
