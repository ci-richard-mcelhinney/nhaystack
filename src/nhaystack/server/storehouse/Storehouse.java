//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.server.storehouse;

import javax.baja.sys.*;
import javax.baja.timezone.*;
import javax.baja.units.*;

import haystack.*;
import nhaystack.res.*;
import nhaystack.server.*;

/**
  * A Storehouse is similar to a javax.baja.space.BSpace.
  *
  * However, not all Storehouses have a BSpace, and
  * not all BSpaces have a Storehouse.
  */
public abstract class Storehouse
{
    public Storehouse(NHServer server)
    {
        this.server = server;
        this.service = server.getService();
    }

    /**
      * Return navigation tree children for given navId. 
      */
    public abstract HGrid onNav(String navId);

////////////////////////////////////////////////////////////////
// protected
////////////////////////////////////////////////////////////////

    /**
      * add the 'kind' tag, along with an associated tags 
      * like 'enum' or 'unit'
      */
    static void addPointKindTags(
        int pointKind, 
        BFacets facets, 
        HDict tags, 
        HDictBuilder hdb)
    {
        switch(pointKind)
        {
            case NUMERIC_KIND:

                if (!tags.has("kind")) hdb.add("kind", "Number");

                if (!tags.has("unit"))
                {
                    Unit unit = findUnit(facets);
                    if (unit != null) 
                        hdb.add("unit", unit.symbol);
                }

                break;

            case BOOLEAN_KIND:

                if (!tags.has("kind")) hdb.add("kind", "Bool");
                if (!tags.has("enum")) hdb.add("enum", findTrueFalse(facets));
                break;

            case ENUM_KIND:

                if (!tags.has("kind")) hdb.add("kind", "Str");
                if (!tags.has("enum")) hdb.add("enum", findRange(facets));
                break;

            case STRING_KIND:

                if (!tags.has("kind")) hdb.add("kind", "Str");
                break;
        }
    }

    static HTimeZone makeTimeZone(BTimeZone timeZone)
    {
        String tzName = timeZone.getId();

        // lop off the continent, e.g. "America" 
        int n = tzName.indexOf("/");
        if (n != -1) tzName = tzName.substring(n+1);

        return HTimeZone.make(tzName, false);
    }

    static Unit findUnit(BFacets facets)
    {
        if (facets == null) 
            return null;

        BUnit unit = (BUnit)facets.get("units");
        if ((unit == null) || (unit.isNull()))
            return null;

        int conv = facets.geti("unitConversion", 0);
        if (conv != 0)
            unit = BUnitConversion.make(conv).getDesiredUnit(unit);

        return Resources.convertFromNiagaraUnit(unit);
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private static String findTrueFalse(BFacets facets)
    {
        if (facets == null) 
            return "false,true";

        return 
            facets.gets("falseText", "false") + "," +
            facets.gets("trueText", "true");
    }

    private static String findRange(BFacets facets)
    {
        if (facets == null) 
            return "";

        BEnumRange range = (BEnumRange) facets.get("range");
        if ((range == null) || (range.isNull()))
            return "";

        StringBuffer sb = new StringBuffer();
        int[] ord = range.getOrdinals();
        for (int i = 0; i < ord.length; i++)
        {
            if (i > 0) sb.append(",");
            sb.append(range.get(i).getTag());
        }
        return sb.toString();
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    // point kinds
    static final int UNKNOWN_KIND = -1;
    static final int NUMERIC_KIND =  0;
    static final int BOOLEAN_KIND =  1;
    static final int ENUM_KIND    =  2;
    static final int STRING_KIND  =  3;

    final NHServer server;
    final BNHaystackService service;
}

