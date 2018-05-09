//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   10 Feb 2013  Mike Jarmy     Creation
//   09 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.BObject;
import javax.baja.sys.BSimple;
import javax.baja.sys.BValue;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import org.projecthaystack.HDict;
import org.projecthaystack.io.HZincReader;

/**
 *  BHDict wraps a Haystack HDict
 */
@NiagaraType
public final class BHDict
    extends BSimple
{
    /** * The default is HDict.EMPTY. */
    public static final BHDict DEFAULT = new BHDict(HDict.EMPTY);

/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.BHDict(2979906276)1.0$ @*/
/* Generated Wed Nov 29 14:36:23 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BHDict.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
    /**
      * Make a BHDict instance from an HDict.
      */
    public static BHDict make(HDict dict) 
    { 
        return new BHDict(dict);  
    }

    /**
      * Make a BHDict instance from a ZINC-encoded string.
      */
    public static BHDict make(String s) 
    { 
        HZincReader zr = new HZincReader(s);
        return new BHDict(zr.readDict());
    }

    private BHDict(HDict dict) 
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

        if (!(obj instanceof BHDict)) return false;
        BHDict that = (BHDict) obj;
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
        return new BHDict(zr.readDict());
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
        return new BHDict(zr.readDict());
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    /**
      * Return the explicitly annotated tags for the component, 
      * or return null.
      *
      * In order for the annotation to be recognized, it
      * must be stored in a property called 'haystack'.
      */
    public static HDict findTagAnnotation(BComponent comp)
    {
        BValue val = comp.get(HAYSTACK_IDENTIFIER);
        if (val == null) return null;
        if (!(val instanceof BHDict)) return null;

        BHDict dict = (BHDict) comp.get(HAYSTACK_IDENTIFIER);
        return dict.getDict();
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
    public static final String HAYSTACK_IDENTIFIER = "haystack";
}
