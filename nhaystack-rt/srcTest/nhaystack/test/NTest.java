//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.test;

import java.lang.reflect.*;
import org.projecthaystack.*;
import org.projecthaystack.client.*;
import org.projecthaystack.test.*;

/**
 * Simple test harness to avoid pulling in dependencies.
 */
public abstract class NTest
{
    static HGrid makeNavGrid(HStr navId)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add("navId", navId);
        return HGridBuilder.dictsToGrid(new HDict[] { hd.toDict() });
    }

    static HGrid makeIdGrid(HVal id)
    {
        HDictBuilder hd = new HDictBuilder();
        hd.add("id", id);
        return HGridBuilder.dictsToGrid(new HDict[] { hd.toDict() });
    }

    void verifyGridContains(HGrid g, String col, String val) 
    { 
        verifyGridContains(g, col, HStr.make(val)); 
    }

    void verifyGridContains(HGrid g, String col, HVal val)
    {
        boolean found = false;
        for (int i=0; i<g.numRows(); ++i)
        {
            HVal x = g.row(i).get(col, false);
            if (x != null && x.equals(val)) { found = true; break; }
        }
        if (!found)
        {
            System.out.println("verifyGridContains " + col + "=" + val + " failed!");
//            fail();
        }
    }

    HDateTime ts(HDict r, String col) { return (HDateTime)r.get(col); }
    HDateTime ts(HDict r) { return (HDateTime)r.get("ts"); }
    HNum numVal(HRow r) { return (HNum)r.get("val"); }

    HStr localTz() { return HStr.make(HTimeZone.DEFAULT.name); }
}

