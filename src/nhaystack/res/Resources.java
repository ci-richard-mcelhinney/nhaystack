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

    private static final String ELEC_METER     = "/nhaystack/res/equip-points/elecMeter.txt";
    private static final String VAV            = "/nhaystack/res/equip-points/vav.txt";
    private static final String HEAT_EXCHANGER = "/nhaystack/res/equip-points/heatExchanger.txt";
    private static final String COOLING_TOWER  = "/nhaystack/res/equip-points/coolingTower.txt";
    private static final String CHILLER_PLANT  = "/nhaystack/res/equip-points/chillerPlant.txt";
    private static final String CHILLER        = "/nhaystack/res/equip-points/chiller.txt";
    private static final String AHU            = "/nhaystack/res/equip-points/ahu.txt";

    private static Map kindTags; // <String,Set<String>>

    private static String[] timeZones;

    private static Map quantityUnits; // <String,Array<Unit>>
    private static Map symbolUnits;   // <String,Unit>
    private static String[] quantities;

    private static String[] markerGroups = new String[] {
        "elecMeter", "vav", "heatExchanger", "coolingTower",
        "chillerPlant", "chiller", "ahu" };
    private static Map markerGroupTags; // <String,Array<String>>

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
            loadMarkerGroups();
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

        bin.close();
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
        bin.close();
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
        bin.close();
    }

    private static void loadUnit(Unit unit)
    {
        Array arr = (Array) quantityUnits.get(unit.quantity);
        if (arr == null)
            quantityUnits.put(unit.quantity, arr = new Array(Unit.class));
        arr.add(unit);

        if (symbolUnits.containsKey(unit.symbol))
            throw new BajaRuntimeException("Duplicate symbol: " + unit.symbol);

        symbolUnits.put(unit.symbol, unit);
    }

    private static void loadMarkerGroups() throws Exception
    {
        markerGroupTags = new HashMap();

        markerGroupTags.put("elecMeter",     loadMarkerGroup(ELEC_METER));
        markerGroupTags.put("vav",           loadMarkerGroup(VAV));
        markerGroupTags.put("heatExchanger", loadMarkerGroup(HEAT_EXCHANGER));
        markerGroupTags.put("coolingTower",  loadMarkerGroup(COOLING_TOWER));
        markerGroupTags.put("chillerPlant",  loadMarkerGroup(CHILLER_PLANT));
        markerGroupTags.put("chiller",       loadMarkerGroup(CHILLER));
        markerGroupTags.put("ahu",           loadMarkerGroup(AHU));
    }

    private static Array loadMarkerGroup(String resourcePath) throws Exception
    {
        InputStream in = Resources.class.getResourceAsStream(resourcePath);
        BufferedReader bin = new BufferedReader(new InputStreamReader(in));

        Array arr = new Array(String.class);

        String str = bin.readLine(); 
        while (str != null)
        {
            if (!str.equals("") && !str.startsWith("**"))
            {
                while (str.indexOf("  ") != -1)
                    str = TextUtil.replace(str, "  ", " ");
                arr.add(str);
            }

            str = bin.readLine();
        }

        bin.close();
        return arr;
    }

////////////////////////////////////////////////////////////////
// access
////////////////////////////////////////////////////////////////

    /**
      * Get the different 'kinds' of tags: Str, Marker, Bool, etc
      */
    public static String[] getKindTags(String kind)
    {
        if (!kindTags.containsKey(kind)) return new String[0];

        Array arr = new Array(String.class, (Set) kindTags.get(kind));
        return (String[]) arr.trim();
    }

    /**
      * Get all the timezones.
      */
    public static String[] getTimeZones()
    {
        return timeZones;
    }

    /**
      * Get all the different quantities of units.
      */
    public static String[] getUnitQuantities()
    {
        return quantities;
    }

    /**
      * Get all the Units having a given quantity.
      */
    public static Unit[] getUnits(String quantity)
    {
        Array arr = (Array) quantityUnits.get(quantity);
        return (Unit[]) arr.trim();
    }

    /**
      * Get all the Units having a given quantity and name.
      */
    public static Unit[] getUnits(String quantity, String name)
    {
        Array a1 = (Array) quantityUnits.get(quantity);
        Array a2 = new Array(Unit.class);

        for (int i = 0; i < a1.size(); i++)
        {
            Unit unit = (Unit) a1.get(i);
            if (unit.name.equals(name))
                a2.add(unit);
        }

        return (Unit[]) a2.trim();
    }

    /**
      * Get the unit having a given symbol.
      */
    public static Unit getSymbolUnit(String symbol)
    {
        Unit unit = (Unit) symbolUnits.get(symbol);
        if (unit == null)
            throw new BajaRuntimeException(
                "Cannot find Unit for symbol '" + symbol + "'.");
        return unit;
    }

    /**
      * Get the names of all the Marker Groups.
      */
    public static String[] getMarkerGroups()
    {
        return markerGroups;
    }

    /**
      * Get the space-delimited list of the tags
      * associated with the given Marker Group name.
      */
    public static String[] getMarkerGroupTags(String group)
    {
        Array arr = (Array) markerGroupTags.get(group);
        if (arr == null) throw new BajaRuntimeException(
            "Cannot find marker group '" + group + "'.");
        return (String[]) arr.trim();
    }
}
