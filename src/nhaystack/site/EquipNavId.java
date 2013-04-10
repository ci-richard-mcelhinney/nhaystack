//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academi Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.site;

import javax.baja.sys.*;

import haystack.*;

/**
  * EquipNavId uniquely identifies an equip by its name, and its site name.
  */
public class EquipNavId
{
    public static EquipNavId make(String siteName, String equipName)
    {
        return new EquipNavId(
            HRef.make("/site/" + siteName + "/" + equipName),
            siteName,
            equipName);
    }

    private EquipNavId(HRef ref, String siteName, String equipName)
    {
        this.ref = ref;
        this.siteName = siteName;
        this.equipName = equipName;
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public String toString()
    {
        return "[EquipNavId " +
            "ref:" + ref + ", " +
            "siteName:" + siteName + ", " +
            "equipName:" + equipName + "]";
    }

    public int hashCode() { return ref.hashCode(); }

    public boolean equals(Object obj)
    {
        if (this == obj) return true;

        return (obj instanceof EquipNavId) ?
            ref.equals(((EquipNavId) obj).ref) :
            false;
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    public HRef getHRef() { return ref; }

    public String getSiteName() { return siteName; }
    public String getEquipName() { return equipName; }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private final HRef ref;
    private final String siteName;
    private final String equipName;
}
