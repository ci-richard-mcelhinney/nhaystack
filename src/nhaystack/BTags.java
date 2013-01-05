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
 *  BTags wraps a Haystack HDict
 */
public final class BTags
    extends BSimple
{
    /**
      * Make a BTags instance from an HDict.
      */
    public static BTags make(HDict dict) 
    { 
        return new BTags(dict);  
    }

    /**
      * Make a BTags instance from a ZINC-encoded string.
      */
    public static BTags make(String s) 
    { 
        HZincReader zr = new HZincReader(s);
        return new BTags(zr.readDict());
    }

    private BTags(HDict dict) 
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

        if (!(obj instanceof BTags)) return false;
        BTags that = (BTags) obj;
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
        return new BTags(zr.readDict());
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
        return new BTags(zr.readDict());
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    public HDict getDict() { return dict; }

////////////////////////////////////////////////////////////////
// Attributes
//////////////////////////////////////////////////////////////// 

    public static final BTags DEFAULT = new BTags(HDict.EMPTY);
    public static final BTags NULL = new BTags(HDict.EMPTY);

    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BTags.class);

    private final HDict dict;
}
