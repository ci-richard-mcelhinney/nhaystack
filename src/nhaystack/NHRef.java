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
import javax.baja.util.*;
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
        String handle      = val.substring(dot+1);

        if (!(space.equals("c") || space.equals("h")))
            throw new BajaRuntimeException("Could not parse HRef '" + ref + "'.");

        return new NHRef(ref, stationName, space, handle);
    }

    /**
      * Make an ID from a BComponent.  If the BComponent
      * is a BHistoryConfig, create a HistorySpace ID.
      * Otherwise assume the BComponent is mounted, and 
      * create a BComponentSpace ID from the component's handle.
      */
    public static NHRef make(BComponent comp)
    {
        // history space
        if (comp instanceof BHistoryConfig)
        {
            BHistoryConfig cfg = (BHistoryConfig) comp;
            BHistoryId hid = cfg.getId();

            String stationName = Sys.getStation().getStationName();
            String space  = "h";
            String handle = Base64.URI.encodeUTF8(hid.toString());

            return new NHRef(stationName, space, handle);
        }
        // component space
        else
        {
            String stationName = Sys.getStation().getStationName();
            String space  = "c";
            String handle = Base64.URI.encodeUTF8(comp.getHandle().toString());
            
            return new NHRef(stationName, space, handle);
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

    public String toString() { return ref.val; }

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

    public boolean isComponentSpace() { return space.equals("c"); }
    public boolean isHistorySpace()   { return space.equals("h"); }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    /**
      * The ref is always <code>stationName + ":" + space + "." + handle</code>.
      */
    public final HRef ref;

    /**
      * The name of the station that the object resides in.  
      * Standard AX practice is that station names must be
      * unique within an entire system.
      */
    public final String stationName;

    /**
      * The space is always either "c" for ComponentSpace,
      * or "h" for HistorySpace;
      */
    public final String space; 

    /**
      * For a ComponentSpace, the handle is an encoding of the 
      * component's handle.
      *
      * For a HistorySpace, the handle is an encoding of the historyId.
      */
    public final String handle;
}
