//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   01 Oct 2012  Mike Jarmy          Ported from axhaystack
//

package nhaystack;

import java.io.*;
import javax.baja.sys.*;
import haystack.*;
import haystack.io.*;

/**
 *  BHDict wraps a Haystack HDict
 */
public final class BHDict
    extends BSimple
{
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
        return (dict.equals(that.dict));
    }

////////////////////////////////////////////////////////////////
// BSimple
////////////////////////////////////////////////////////////////

    /**
      * Encode to ZINC format
      */
    public void encode(DataOutput encoder) throws IOException
    { 
        encoder.writeUTF(dict.toZinc()); 
    }

    /**
      * Decode from ZINC format
      */
    public BObject decode(DataInput decoder) throws IOException
    { 
        HZincReader zr = new HZincReader(decoder.readUTF());
        return new BHDict(zr.readDict());
    }  

    /**
      * Encode to ZINC format
      */
    public String encodeToString() throws IOException
    { 
        return dict.toZinc(); 
    }

    /**
      * Decode from ZINC format
      */
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
    public static BHDict findTagAnnotation(BComponent comp)
    {
        BValue val = comp.get("haystack");
        if (val == null) return null;

        return (val instanceof BHDict) ? (BHDict) val : null;
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

    public BIcon getIcon() { return ICON; }
    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/tag.png");

    public static final BHDict DEFAULT = new BHDict(HDict.EMPTY);
    public static final BHDict NULL = new BHDict(HDict.EMPTY);

    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BHDict.class);

    private final HDict dict;
}
