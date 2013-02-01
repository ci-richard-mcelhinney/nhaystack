//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//
package nhaystack;

import javax.baja.history.*;
import javax.baja.sys.*;
import haystack.*;
import haystack.util.*;

/**
  * NHRef uniquely identifies an object by its 
  * station, space, and handle.  Every AX object
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

        int colon = val.indexOf(":");
        int dot = val.indexOf(".");
        if ((colon == -1) || (dot == -1) || (colon > dot))
            throw new BajaRuntimeException("Could not parse HRef '" + ref + "'.");

        String stationName = val.substring(0, colon);
        String space       = val.substring(colon+1, dot);
        String handle      = Base64.URI.decodeUTF8(val.substring(dot+1));

        if (!(space.equals("c") || space.equals("h")))
            throw new BajaRuntimeException("Could not parse HRef '" + ref + "'.");

        return new NHRef(ref, stationName, space, handle);
    }

    /**
      * Convenience for <code>make(Sys.getStation().getStationName(), comp)</code>.
      */
    public static NHRef make(BComponent comp)
    {
        return make(Sys.getStation().getStationName(), comp);
    }

    /**
      * Make an ID from a BComponent.  If the BComponent
      * is a BHistoryConfig, create a HistorySpace ID.
      * Otherwise assume the BComponent is mounted, and 
      * create a BComponentSpace ID from the component's handle.
      */
    public static NHRef make(String stationName, BComponent comp)
    {
        // history space
        if (comp instanceof BHistoryConfig)
        {
            BHistoryConfig cfg = (BHistoryConfig) comp;
            BHistoryId hid = cfg.getId();
            String handle = Base64.URI.encodeUTF8(hid.toString());

            return new NHRef(stationName, "h", handle);
        }
        // component space
        else
        {
            String handle = Base64.URI.encodeUTF8(comp.getHandle().toString());
            
            return new NHRef(stationName, "c", handle);
        }
    }

    private NHRef(HRef ref, String stationName, String space, String handle)
    {
        this.ref         = ref;
        this.stationName = stationName;
        this.space       = space;
        this.handle      = handle;
    }

    private NHRef(String stationName, String space, String handle)
    {
        this(
            HRef.make(stationName + ":" + space + "." + handle),
            stationName, space, handle); 
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public String toString()
    {
        return "[NHRef " +
            "ref:" + ref + ", " +
            "stationName:" + stationName + ", " +
            "space:" + space + ", " +
            "handle:" + handle + "]";
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
      * The ref is always <code>stationName + ":" + space + "." + handle</code>.
      */
    public HRef getHRef() { return ref; }

    /**
      * The name of the station that the object resides in.  
      * Standard AX practice is that station names must be
      * unique within an entire system.
      */
    public String getStationName() { return stationName; }

    /**
      * The space is always either a componentSpace or a historySpace.
      */
    public boolean isComponentSpace() { return space.equals("c"); }
    public boolean isHistorySpace()   { return space.equals("h"); }

    /**
      * For a ComponentSpace, the handle is an encoding of the 
      * component's handle.
      *
      * For a HistorySpace, the handle is an encoding of the historyId.
      */
    public String getHandle() { return handle; }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private final HRef ref;
    private final String stationName;
    private final String space; 
    private final String handle;
}
