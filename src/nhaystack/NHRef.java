//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack;

import javax.baja.sys.*;

import org.projecthaystack.*;

/**
  * NHRef uniquely identifies an object by its 
  * station, space, and path.  Every AX object
  * that is represented as a haystack entity has a
  * unique NHRef.
  */
public class NHRef
{
    /**
      * Make an ID from an HRef.
      */
    public static NHRef make(HRef ref)
    {
        String val = ref.val;

        int dot = val.indexOf(".");
        if (dot == -1)
            throw new BajaRuntimeException(
                "Could not parse HRef '" + ref + "'.");

        String space = val.substring(0, dot);
        String path  = val.substring(dot+1);

        return make(space, path);
    }

    /**
      * Make an ID from an HRef.
      */
    public static NHRef make(String space, String path)
    {
        if (!(space.equals(COMP) || 
              space.equals(HIS) ||
              space.equals(COMP_BASE64) || 
              space.equals(HIS_BASE64) ||
              space.equals(SEP)))
        {
            throw new BajaRuntimeException(
                "Invalid space: '" + space + "'");
        }

        return new NHRef(space, path);
    }

    /**
      * Constructor
      */
    private NHRef(String space, String path)
    {
        this.ref   = HRef.make(space + "." + path);
        this.space = space;
        this.path  = path;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public String toString()
    {
        return "[NHRef " +
            "ref:" + ref + ", " +
            "space:" + space + ", " +
            "path:" + path + "]";
    }

    public int hashCode() { return ref.hashCode(); }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        return (obj instanceof NHRef) ?
            ref.equals(((NHRef) obj).ref) :
            false;
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * The ref is always <code>space + "." + path</code>.
      */
    public HRef getHRef() { return ref; }

    /**
      * The space is always one of COMP, HIS, or SEP (or sometimes COMP_BASE64 or HIS_BASE64).
      */
    public String getSpace() { return space; }

    /**
      * The path identifies an object within its space.
      */
    public String getPath() { return path; }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    public static final String COMP = "C";
    public static final String HIS  = "H";
    public static final String SEP  = "S";

    // for backwards compatibility
    public static final String COMP_BASE64 = "c";
    public static final String HIS_BASE64  = "h";

    private final HRef ref;
    private final String space; 
    private final String path;
}
