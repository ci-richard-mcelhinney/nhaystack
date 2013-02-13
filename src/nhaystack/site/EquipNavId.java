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
  * EquipNavId uniquely identifies an equip by its name, and its site name.
  */
public class EquipNavId
{
    public static EquipNavId make(HRef ref)
    {
        String val = ref.val;

        int colon = val.indexOf(":");
        int dot = val.indexOf(".");
        if ((colon == -1) || (dot == -1) || (colon > dot))
            throw new BajaRuntimeException(
                "Could not parse HRef '" + ref + "'.");

        String realm     = val.substring(0, colon);
        String siteName  = val.substring(colon+1, dot);
        String equipName = val.substring(dot+1);

        if (!realm.equals(EQUIP))
            throw new BajaRuntimeException(
                "Could not parse HRef '" + ref + "'.");

        return new EquipNavId(ref, siteName, equipName);
    }

    public static EquipNavId make(String siteName, String equipName)
    {
        return new EquipNavId(
            HRef.make(EQUIP + ":" + siteName + "." + equipName),
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

    public static final String EQUIP = "equip";

    private final HRef ref;
    private final String siteName;
    private final String equipName;
}
