//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//
package nhaystack.server;

import javax.baja.history.*;
import javax.baja.sys.*;
import haystack.*;
import nhaystack.util.*;

/**
  * NHId uniquely identifies an object by its 
  * station, space, and handle.  Every AX object
  * that is represented as a haystack entity has a
  * unique NHId.
  */
public class NHId
{
    /**
      * Make an ID from an HRef, or return null
      * if the HRef could not be parsed.
      */
    public static NHId make(HRef ref)
    {
        int colon = ref.val.indexOf(":");
        int dot = ref.val.indexOf(".");
        if (colon == -1) return null;
        if (dot == -1) return null;
        if (colon > dot) return null;

        String stationName = ref.val.substring(0, colon);
        String space       = ref.val.substring(colon+1, dot);
        String handle      = ref.val.substring(dot+1);

        if (!(space.equals("c") || space.equals("h")))
            return null;

        return new NHId(ref, stationName, space, handle);
    }

    /**
      * Make an ID from a BComponent.  If the BComponent
      * is a BHistoryConfig, create a HistorySpace id.
      * Otherwise assume the BComponent is mounted, and 
      * create a ComponentSpace id.
      */
    public static NHId make(BComponent comp)
    {
        // history space
        if (comp instanceof BHistoryConfig)
        {
            BHistoryConfig cfg = (BHistoryConfig) comp;
            BHistoryId hid = cfg.getId();

            String stationName = Sys.getStation().getStationName();
            String space  = "h";
            String handle = UriUtil.encodeToUri(hid.toString());

            return new NHId(
                HRef.make(stationName + ":" + space + "." + handle),
                stationName, space, handle);
        }
        // component space
        else
        {
            String stationName = Sys.getStation().getStationName();
            String space  = "c";
            String handle = comp.getHandle().toString();
            
            return new NHId(
                HRef.make(stationName + ":" + space + "." + handle),
                stationName, space, handle);
        }
    }

    private NHId(HRef ref, String stationName, String space, String handle)
    {
        this.ref = ref;
        this.stationName = stationName;
        this.space  = space;
        this.handle = handle;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public String toString() { return ref.val; }

    public int hashCode() { return ref.hashCode(); }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        return (obj instanceof NHId) ?
            ref.equals(((NHId) obj).ref) :
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
      * For a ComponentSpace, the handle is BComponent.getHandle(),
      * which in practice is always a hexadecimal String.
      * <p>
      * For a HistorySpace, the handle is a reversible, URI-friendly 
      * encoding of the BHistoryId. BHistoryId is the closest thing 
      * to a "handle" that histories have.
      */
    public final String handle;
}
