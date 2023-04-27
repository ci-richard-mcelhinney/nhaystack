//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   27 Jan 2013  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Added use of generics
//
package nhaystack.res;

import javax.baja.file.BIFile;
import javax.baja.naming.*;
import javax.baja.nre.util.TextUtil;
import javax.baja.sys.*;
import javax.baja.units.*;
import javax.baja.units.UnitDatabase.Quantity;
import java.io.*;
import java.util.*;

/**
 * Resources makes available all the various files downloaded
 * from project-haystack.org
 */
public final class Resources
{
  private static final String TAGS = "/nhaystack/res/tags.csv";
  private static final String AUTOMARKERS = "/nhaystack/res/autoMarker.csv";
  private static final String TZ = "/nhaystack/res/tz.txt";
  private static final String UNITS = "/nhaystack/res/units.txt";

  private static final String AHU = "/nhaystack/res/equip-points/ahu.txt";
  private static final String BOILER = "/nhaystack/res/equip-points/boiler.txt";
  private static final String CHILLED_WATER_PLANT = "/nhaystack/res/equip-points/chilledWaterPlant.txt";
  private static final String CHILLER = "/nhaystack/res/equip-points/chiller.txt";
  private static final String COOLING_TOWER = "/nhaystack/res/equip-points/coolingTower.txt";
  private static final String ELEC_METER = "/nhaystack/res/equip-points/elecMeter.txt";
  private static final String HEAT_EXCHANGER = "/nhaystack/res/equip-points/heatExchanger.txt";
  private static final String HOT_WATER_PLANT = "/nhaystack/res/equip-points/hotWaterPlant.txt";
  private static final String STEAM_PLANT = "/nhaystack/res/equip-points/steamPlant.txt";
  private static final String TANK = "/nhaystack/res/equip-points/tank.txt";
  private static final String VAV = "/nhaystack/res/equip-points/vav.txt";
  private static final String VFD = "/nhaystack/res/equip-points/vfd.txt";
  private static final String ZONE = "/nhaystack/res/equip-points/zone.txt";

  public static final String[] EMPTY_STRING_ARR = new String[0];
  public static final Unit[] EMPTY_UNIT_ARR = new Unit[0];

  private static Map<String, Set<String>> kindTags;
  private static Map<String, Set<String>> autoMarkers;

  private static String[] timeZones;

  private static Map<String, List<Unit>> unitsByQuantity;
  private static Map<String, Unit> unitsBySymbol;
  private static Map<String, Unit> unitsByLowerCaseName;
  private static String[] quantities;

  private static final String[] markerSets = {"ahu", "boiler", "chilledWaterPlant", "chiller", "coolingTower", "elecMeter", "heatExchanger", "hotWaterPlant", "steamPlant", "tank", "vav", "vfd", "zone"};
  private static Map<String, List<String>> markerSetTags;

  private Resources()
  {
  }

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
      loadMarkerSets();
      loadAutoMarkers(null); // New feature
    } catch (Exception e)
    {
      throw new BajaRuntimeException(e);
    }
  }

  private static void loadTags() throws Exception
  {
    InputStream in = Resources.class.getResourceAsStream(TAGS);
    BufferedReader bin = new BufferedReader(new InputStreamReader(in));

    kindTags = new HashMap<>();

    String str = bin.readLine(); // throw away header
    str = bin.readLine();
    while (str != null)
    {
      String[] tokens = TextUtil.split(str, ',');
      String name = tokens[0];
      String kind = tokens[1];

      kindTags.computeIfAbsent(kind, k -> new TreeSet<String>()).add(name);

      str = bin.readLine();
    }

    bin.close();
  }

  /**
   * loadAutoMarkers() loads a CSV file which stores default tags for AX point based on their name
   * A lot of devices use consistent names that can be used to tag points.
   * Some examples are :
   * DA-T (discharge air temperature), SF-C (supply fan command), SF-S (supply fan status) for Johnson Controls
   * It is then possible to build a list with known names and add a lot of speed to the tagging process
   * <p>
   * File is built simply
   * [pointName],[list of tags]
   * DA-T,discharge air temp sensor
   * SF-C,discharge fan cmd
   *
   * @throws Exception
   */
  public static void loadAutoMarkers(BOrd fq) throws Exception
  {
    // Load default dictionnary
    BOrd fileQuery;
    InputStream in;

    // Try to load local file if exists
    try
    {
      if (fq == null)
      {
        // must be a local file on workbench PC.... at startup, it may load but will be
        // overriden when service view will open. loading again will be necessary using the
        // button
        String shared_folder = Sys.getNiagaraSharedUserHome().getPath().replace("\\", "/");
        String customTagsDictFilePath = "local:|file:/" + shared_folder + "/nHaystack/customTagsDict.csv";
//            System.out.println(customTagsDictFilePath);
        fileQuery = BOrd.make(customTagsDictFilePath);
      }
      else
      {
        fileQuery = fq;
      }

      BIFile myFile = (BIFile) fileQuery.get();
      in = myFile.getInputStream();
    } catch (UnresolvedException re)
    {
      // handle case where file isn't found or doesn't exist.
//          System.out.println("nHaystack - Tag Dictionnary / No custom file, using default.");
      in = Resources.class.getResourceAsStream(AUTOMARKERS);
    } catch (IOException ioe)
    {
      // handle IO exceptions from trying to read from file
//          System.out.println("nHaystack - Tag Dictionnary / Errors in custom file, using default.");
      in = Resources.class.getResourceAsStream(AUTOMARKERS);
    }

    BufferedReader bin = new BufferedReader(new InputStreamReader(in));

    autoMarkers = new HashMap<>();

    String str = bin.readLine(); // throw away header
    str = bin.readLine();
    while (str != null)
    {
      String[] tokens = TextUtil.split(str, ',');
      String pointName = tokens[0];
      String[] markerList = TextUtil.split(tokens[1], ' ');

      Set<String> set = autoMarkers.computeIfAbsent(pointName, k -> new TreeSet<>());
      Collections.addAll(set, markerList);

      str = bin.readLine();
    }
//        System.out.println("Testing Generation Map (SF-C): " + getAutoMarkers("SF-C"));
//        System.out.println("Testing Generation Map (DA-T): " + getAutoMarkers("DA-T"));
    bin.close();
  }

  private static void loadTimeZones() throws Exception
  {
    InputStream in = Resources.class.getResourceAsStream(TZ);
    BufferedReader bin = new BufferedReader(new InputStreamReader(in));

    List<String> arr = new ArrayList<>();

    String str = bin.readLine();
    while (str != null)
    {
      arr.add(str.trim());
      str = bin.readLine();
    }

    timeZones = arr.toArray(EMPTY_STRING_ARR);
    bin.close();
  }

  private static void loadUnits() throws Exception
  {
    InputStream in = Resources.class.getResourceAsStream(UNITS);
    BufferedReader bin = new BufferedReader(new InputStreamReader(in, "UTF-8"));

    unitsByQuantity = new HashMap<>();
    unitsBySymbol = new HashMap<>();
    unitsByLowerCaseName = new HashMap<>();

    List<String> arr = new ArrayList<>();
    String curQuant = null;

    String str = bin.readLine();
    while (str != null)
    {
      if (!str.isEmpty())
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
            {
                loadUnit(new Unit(curQuant, tokens[0], tokens[0]));
            }
            else
            {
                for (int i = 0; i < tokens.length - 1; i++)
                    loadUnit(new Unit(curQuant, tokens[0], tokens[i + 1]));
            }
        }
      }

      str = bin.readLine();
    }

    quantities = arr.toArray(EMPTY_STRING_ARR);
    bin.close();
  }

  private static void loadUnit(Unit unit)
  {
    unitsByQuantity.computeIfAbsent(unit.quantity, k -> new ArrayList<>()).add(unit);

      if (unitsBySymbol.containsKey(unit.symbol))
      {
          throw new BajaRuntimeException("Duplicate symbol: " + unit.symbol);
      }

    unitsBySymbol.put(unit.symbol, unit);

    String name = unit.name.toLowerCase();
      if (!unitsByLowerCaseName.containsKey(name))
      {
          unitsByLowerCaseName.put(name, unit);
      }
  }

  private static void loadMarkerSets() throws Exception
  {
    markerSetTags = new HashMap<>();

    markerSetTags.put("ahu", loadMarkerSet(AHU));
    markerSetTags.put("boiler", loadMarkerSet(BOILER));
    markerSetTags.put("chilledWaterPlant", loadMarkerSet(CHILLED_WATER_PLANT));
    markerSetTags.put("chiller", loadMarkerSet(CHILLER));
    markerSetTags.put("coolingTower", loadMarkerSet(COOLING_TOWER));
    markerSetTags.put("elecMeter", loadMarkerSet(ELEC_METER));
    markerSetTags.put("heatExchanger", loadMarkerSet(HEAT_EXCHANGER));
    markerSetTags.put("hotWaterPlant", loadMarkerSet(HOT_WATER_PLANT));
    markerSetTags.put("steamPlant", loadMarkerSet(STEAM_PLANT));
    markerSetTags.put("tank", loadMarkerSet(TANK));
    markerSetTags.put("vav", loadMarkerSet(VAV));
    markerSetTags.put("vfd", loadMarkerSet(VFD));
    markerSetTags.put("zone", loadMarkerSet(ZONE));
  }

  private static List<String> loadMarkerSet(String resourcePath) throws Exception
  {
    InputStream in = Resources.class.getResourceAsStream(resourcePath);
    BufferedReader bin = new BufferedReader(new InputStreamReader(in));

    List<String> arr = new ArrayList<>();

    String str = bin.readLine();
    while (str != null)
    {
      if (!str.isEmpty() && !str.startsWith("**"))
      {
        while (str.contains("  ")) str = TextUtil.replace(str, "  ", " ");
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
      if (!kindTags.containsKey(kind))
      {
          return EMPTY_STRING_ARR;
      }

    Set<String> kindTagsValue = kindTags.get(kind);
    return kindTagsValue.toArray(EMPTY_STRING_ARR);
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
    List<Unit> arr = unitsByQuantity.get(quantity);
    return arr.toArray(EMPTY_UNIT_ARR);
  }

  /**
   * Get all the Units having a given quantity and name.
   */
  public static Unit[] getUnits(String quantity, String name)
  {
    List<Unit> units = unitsByQuantity.get(quantity);
    List<Unit> result = new ArrayList<>();

    for (Unit unit : units)
    {
        if (unit.name.equals(name))
        {
            result.add(unit);
        }
    }

    return result.toArray(EMPTY_UNIT_ARR);
  }

  /**
   * Get the unit having a given symbol.
   */
  public static Unit getSymbolUnit(String symbol)
  {
    Unit unit = unitsBySymbol.get(symbol);
      if (unit == null)
      {
          throw new BajaRuntimeException("Cannot find Unit for symbol '" + symbol + "'.");
      }
    return unit;
  }

  /**
   * Get the names of all the Marker Sets.
   */
  public static String[] getMarkerSets()
  {
    return markerSets;
  }

  /**
   * Get the space-delimited list of the tags
   * associated with the given Marker Set name.
   */
  public static String[] getMarkerSetTags(String group)
  {
    List<String> arr = markerSetTags.get(group);
      if (arr == null)
      {
          throw new BajaRuntimeException("Cannot find marker group '" + group + "'.");
      }
    return arr.toArray(EMPTY_STRING_ARR);
  }

  /**
   * Get the default tags based on pointName. Ex. DA-T loads "discharge air temp sensor" tags. Used in BAddHaystackSlot.
   */
  public static String[] getAutoMarkers(String pointName)
  {
    String key = SlotPath.unescape(pointName);
      if (!autoMarkers.containsKey(key))
      {
          return EMPTY_STRING_ARR;
      }

//      System.out.println("getAutoMArker" + pointName + " : " + (String[]) arr.trim());
    return autoMarkers.get(key).toArray(EMPTY_STRING_ARR);
  }

  /**
   * Get the Unit that corresponds to the given Niagara BUnit,
   * or return null if no corresponding Unit can be found.
   */
  public static Unit fromBajaUnit(BUnit bunit)
  {
    Unit unit = unitsByLowerCaseName.get(TextUtil.replace(bunit.getUnitName(), " ", "_").toLowerCase());

      if (unit != null)
      {
          return unit;
      }

      if (bunit.getUnitName().equals("dollar"))
      {
          return getSymbolUnit("$");
      }
      if (bunit.getUnitName().equals("pounds"))
      {
          return getSymbolUnit("GBP");
      }
      if (bunit.getUnitName().equals("rupee"))
      {
          return getSymbolUnit("INR");
      }
      if (bunit.getUnitName().equals("won"))
      {
          return getSymbolUnit("KRW");
      }
      if (bunit.getUnitName().equals("yen"))
      {
          return getSymbolUnit("JPY");
      }

    return null;
  }

  /**
   * Get the BUnit that corresponds to the given Haystack Unit,
   * or return null if no corresponding BUnit can be found.
   */
  public static BUnit toBajaUnit(Unit unit)
  {
      if (unit.name.equals("us_dollar"))
      {
          return BUnit.getUnit("dollar");
      }
      if (unit.name.equals("british_pound"))
      {
          return BUnit.getUnit("pounds");
      }
      if (unit.name.equals("indian_rupee"))
      {
          return BUnit.getUnit("rupee");
      }
      if (unit.name.equals("chinese_yuan"))
      {
          return BUnit.getUnit("won");
      }
      if (unit.name.equals("japanese_yen"))
      {
          return BUnit.getUnit("yen");
      }

    return BUnit.getUnit(TextUtil.replace(unit.name, "_", " ").toLowerCase());
  }

////////////////////////////////////////////////////////////////
// main
////////////////////////////////////////////////////////////////

  private static void missingUnitsToNiagara() throws Exception
  {
    // print all the haystack unit names which cannot automatically
    // be converted into Baja unit names.

    System.out.println("--------------------------------------------------------");
    System.out.println("Units which cannot be converted from Haystack to Niagara");
    System.out.println();

    String[] quantities = getUnitQuantities();
    for (String quantity : quantities)
    {
      boolean headerPrinted = false;

      Unit[] units = getUnits(quantity);
      for (Unit unit : units)
      {
        try
        {
          BUnit bunit = toBajaUnit(unit);
        } catch (UnitException e)
        {
          if (!headerPrinted)
          {
            System.out.println(quantity);
            headerPrinted = true;
          }
          System.out.println("    " + unit.name);
        }
      }
    }
  }

  private static void missingUnitsFromNiagara() throws Exception
  {
    // print all the niagara unit names which cannot automatically
    // be converted into haystack unit names.

    System.out.println("--------------------------------------------------------");
    System.out.println("Units which cannot be converted from Niagara to Haystack");
    System.out.println();

    UnitDatabase ud = UnitDatabase.getDefault();
    UnitDatabase.Quantity[] quantities = ud.getQuantities();

    for (Quantity quantity : quantities)
    {
      boolean headerPrinted = false;

      BUnit[] units = quantity.getUnits();
      for (BUnit unit : units)
      {
        if (fromBajaUnit(unit) == null)
        {
          if (!headerPrinted)
          {
            System.out.println(quantity.getName());
            headerPrinted = true;
          }

          System.out.println("    " + unit.getUnitName());
        }
      }
    }
  }

  public static void main(String[] args) throws Exception
  {
    missingUnitsFromNiagara();
    missingUnitsToNiagara();
  }
}
