//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.site;

import javax.baja.sys.*;

import haystack.*;

/**
  * SiteNavId uniquely identifies a site by its name.
  */
public class SiteNavId
{
    public static SiteNavId make(String siteName)
    {
        return new SiteNavId(
            HRef.make("/site/" + siteName),
            siteName);
    }

    private SiteNavId(HRef ref, String siteName)
    {
        this.ref = ref;
        this.siteName = siteName;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public String toString()
    {
        return "[SiteNavId " +
            "ref:" + ref + ", " +
            "siteName:" + siteName + "]";
    }

    public int hashCode() { return ref.hashCode(); }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        return (obj instanceof SiteNavId) ?
            ref.equals(((SiteNavId) obj).ref) :
            false;
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    public HRef getHRef() { return ref; }

    public String getSiteName() { return siteName; }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private final HRef ref;
    private final String siteName;
}
