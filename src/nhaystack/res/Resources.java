//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Jan 2013  Mike Jarmy Creation
//
package nhaystack.res;

import java.io.*;
import java.util.*;

import javax.baja.sys.*;
import javax.baja.util.*;

/**
  * Resources makes available all the various files downloaded 
  * from project-haystack.org
  */
public class Resources
{
    private static final String TAGS  = "/nhaystack/res/tags.csv";
    private static final String TZ    = "/nhaystack/res/tz.txt";
    private static final String UNITS = "/nhaystack/res/units.txt";

    // String => Set<String>
    private static Map kindTags;

    private static String[] timeZones;

    // String => Array<Unit>
    private static Map quantityUnits;

    // String => Unit
    private static Map symbolUnits;

    private static String[] quantities;

////////////////////////////////////////////////////////////////
//  load
////////////////////////////////////////////////////////////////

    static
    {
        try
        {
            loadTags();
            loadTimeZones();
            loadUnits();
        }
        catch (Exception e)
        {
            throw new BajaRuntimeException(e);
        }
    }

    private static void loadTags() throws Exception
    {
        InputStream in = Resources.class.getResourceAsStream(TAGS);
        BufferedReader bin = new BufferedReader(new InputStreamReader(in));

        kindTags = new HashMap();

        String str = bin.readLine(); // throw away header
        str = bin.readLine();
        while (str != null)
        {
            String[] tokens = TextUtil.split(str, ',');
            String name = tokens[0];
            String kind = tokens[1];

            Set set = (Set) kindTags.get(kind);
            if (set == null)
                kindTags.put(kind, set = new TreeSet());
            set.add(name);

            str = bin.readLine();
        }
    }

    private static void loadTimeZones() throws Exception
    {
        InputStream in = Resources.class.getResourceAsStream(TZ);
        BufferedReader bin = new BufferedReader(new InputStreamReader(in));

        Array arr = new Array(String.class);

        String str = bin.readLine(); 
        while (str != null)
        {
            arr.add(str.trim());
            str = bin.readLine();
        }

        timeZones = (String[]) arr.trim();
    }

    private static void loadUnits() throws Exception
    {
        InputStream in = Resources.class.getResourceAsStream(UNITS);
        BufferedReader bin = new BufferedReader(new InputStreamReader(in));

        quantityUnits = new HashMap();
        symbolUnits = new HashMap();

        Array arr = new Array(String.class);
        String curQuant = null;

        String str = bin.readLine(); 
        while (str != null)
        {
            if (!str.equals("")) 
            {
                if (str.startsWith("-- "))
                {
                    curQuant = str.substring(3, str.length() - 3);
                    arr.add(curQuant);
                }
                else
                {
                    String[] tokens = TextUtil.split(str, ',');

                    if (tokens.length == 1)
                        loadUnit(new Unit(curQuant, tokens[0], tokens[0]));
                    else
                        for (int i = 0; i < tokens.length-1; i++)
                            loadUnit(new Unit(curQuant, tokens[0], tokens[i+1]));
                }
            }

            str = bin.readLine();
        }

        quantities = (String[]) arr.trim();
    }

    private static void loadUnit(Unit unit)
    {
        Array arr = (Array) quantityUnits.get(unit.quantity);
        if (arr == null)
            quantityUnits.put(unit.quantity, arr = new Array(Unit.class));
        arr.add(unit);

        if (symbolUnits.containsKey(unit.symbol))
            throw new IllegalStateException("Duplicate symbol: " + unit.symbol);

        symbolUnits.put(unit.symbol, unit);
    }

////////////////////////////////////////////////////////////////
// access
////////////////////////////////////////////////////////////////

    public static String[] getKindTags(String kind)
    {
        if (!kindTags.containsKey(kind)) return new String[0];

        Array arr = new Array(String.class, (Set) kindTags.get(kind));
        return (String[]) arr.trim();
    }

    public static String[] getTimeZones()
    {
        return timeZones;
    }

    public static String[] getUnitQuantities()
    {
        return quantities;
    }

    public static Unit[] getUnits(String quantity)
    {
        Array arr = (Array) quantityUnits.get(quantity);
        return (Unit[]) arr.trim();
    }

    public static Unit getSymbolUnit(String symbol)
    {
        Unit unit = (Unit) symbolUnits.get(symbol);
        if (unit == null)
            throw new IllegalStateException("Cannot find Unit for symbol '" + symbol + "'.");
        return unit;
    }
}
