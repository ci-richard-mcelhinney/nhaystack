//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack;

import javax.baja.history.*;
import javax.baja.naming.*;
import javax.baja.sys.*;

import haystack.*;
import haystack.util.*;

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

        if (!(space.equals(COMPONENT) || space.equals(HISTORY)))
            throw new BajaRuntimeException(
                "Could not parse HRef '" + ref + "'.");

        return new NHRef(ref, space, path);
    }

    /**
      * Make an ID from a BComponent.  
      */
    public static NHRef make(BComponent comp)
    {
        // history space
        if (comp instanceof BHistoryConfig)
        {
            BHistoryConfig cfg = (BHistoryConfig) comp;
            BHistoryId hid = cfg.getId();
            String path = Base64.URI.encodeUTF8(hid.toString());

            return new NHRef(HISTORY, path);
        }
        // component space
        else
        {
            String path = Base64.URI.encodeUTF8(comp.getSlotPath().toString());
            
            return new NHRef(COMPONENT, path);
        }
    }

    private NHRef(HRef ref, String space, String path)
    {
        this.ref   = ref;
        this.space = space;
        this.path  = path;
    }

    private NHRef(String space, String path)
    {
        this(
            HRef.make(space + "." + path),
            space, path); 
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
      * The space is always either COMPONENT or HISTORY.
      */
    public String getSpace() { return space; }

    /**
      * The Base64-encoded path identifies an object within its space.
      */
    public String getPath() { return path; }

    /**
      * Get the BOrd that this NHRef corresponds to.
      */
    public BOrd getOrd()
    {
        if (space.equals(NHRef.COMPONENT))
        {
            return BOrd.make("station:|" + Base64.URI.decodeUTF8(path));
        }
        else if (space.equals(NHRef.HISTORY))
        {
            return BOrd.make("history:" + Base64.URI.decodeUTF8(path));
        }
        else throw new IllegalStateException();
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    public static final String COMPONENT = "c";
    public static final String HISTORY   = "h";

    private final HRef ref;
    private final String space; 
    private final String path;
}
