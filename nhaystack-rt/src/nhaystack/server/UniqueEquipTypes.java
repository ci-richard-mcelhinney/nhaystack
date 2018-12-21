//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   05 Jun 2014  Mike Jarmy       Creation
//   10 May 2018  Eric Anderson    Added use of generics
//   21 Dec 2018  Andrew Saunders  Allowing plain components to be used as sites and equips
//
package nhaystack.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.baja.nre.util.TextUtil;
import javax.baja.sys.BComponent;
import nhaystack.BHDict;
import org.projecthaystack.HDict;
import org.projecthaystack.HDictBuilder;
import org.projecthaystack.HGrid;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.HRef;

/**
  * Custom Ops for NHServer
  */
public class UniqueEquipTypes
{
    UniqueEquipTypes(NHServer server)
    {
        this.server = server;
    }

    /**
      * uniqueEquipTypes
      */
    public HGrid createTypes(BComponent[] equips, String filter, double percentMatch, boolean applyTags)
    {
        // find all the distinct types
        Map<String, EquipType> typeMap = new HashMap<>();
        for (BComponent equip : equips)
        {
            HDict equipTags = server.getTagManager().createTags(equip);
            Set<String> pointNames = findPointNames(server, equip);

            String key = pointNames.toString();
            EquipType type = typeMap.computeIfAbsent(key, k -> new EquipType(pointNames));

            type.equips.add(equip);
            type.equipTags.add(equipTags);
        }

        if (typeMap.size() >= 1000)
            throw new IllegalStateException("There are too many equip types: " + typeMap.size());

        // save types to array
        EquipType[] types = new EquipType[typeMap.size()];
        Iterator<EquipType> it = typeMap.values().iterator();
        int n = 0;
        while (it.hasNext())
        {
            EquipType type = it.next();
            types[n] = type;

            // init matching
            type.similarity = new double[typeMap.size()];

            // update tags with type
            if (applyTags)
            {
                for (int i = 0; i < type.equips.size(); i++)
                {
                    BComponent equip = type.equips.get(i);
                    HDict tags = BHDict.findTagAnnotation(equip);
                    if (tags == null) tags = HDict.EMPTY;

                    HDictBuilder hdb = new HDictBuilder();
                    hdb.add(tags);
                    hdb.add("equipType", n);
                    BHDict hd = BHDict.make(hdb.toDict());

                    if (equip.get("haystack") == null)
                        equip.add("haystack", hd);
                    else
                        equip.set("haystack", hd);
                }
            }
            n++;
        }

        // compute the similarities 
        // NOTE: this is O(n**2)!!!!
        for (int i = 0; i < types.length; i++)
        {
            EquipType a= types[i];
            a.similarity[i] = 1;
            for (int j = i+1; j < types.length; j++)
            {
                EquipType b = types[j];
                double sml = a.jaccardIndex(b); 
                a.similarity[j] = sml;
                b.similarity[i] = sml;
            }
        }

        // create grid
        HDictBuilder meta = new HDictBuilder();
        meta.add("filter", filter);
        meta.add("percentMatch", percentMatch);

        ArrayList<HDict> arr = new ArrayList<>();
        for (int i = 0; i < types.length; i++)
        {
            EquipType type = types[i];

            HDictBuilder hdb = new HDictBuilder();
            hdb.add("id", HRef.make("type" + padZero(i,3)));
            hdb.add("numEquips", type.equipTags.size());
            hdb.add("numPoints", type.pointNames.size());
            hdb.add("equipIds", type.listOfEquipIds());
            hdb.add("equipType", i);

            type.addSimilarTypes(hdb, i, percentMatch, types);

            arr.add(hdb.toDict());
        }

        return HGridBuilder.dictsToGrid(meta.toDict(), arr.toArray(EMPTY_HDICT_ARRAY));
    }

    private static String padZero(int number, int width)
    {
        String s = TextUtil.padLeft(String.valueOf(number), width);
        return TextUtil.replace(s, " ", "0");
    }

    /**
      * findPointNames
      */
    private static Set<String> findPointNames(NHServer server, BComponent equip)
    {
        Set<String> set = new TreeSet<>();
        BComponent[] points = server.getCache().getEquipPoints(equip);
        for (BComponent point : points)
        {
            HDict pointTags = server.getTagManager().createTags(point);
            set.add(pointTags.getStr("navName"));
        }
        return set;
    }

    /**
      * EquipType
      */
    static class EquipType
    {
        EquipType(Set<String> pointNames)
        {
            this.pointNames = pointNames;
            this.equips = new ArrayList<>();
            this.equipTags = new ArrayList<>();
        }

        /**
          * return a prettified list of the equips that have this type
          */
        String listOfEquipIds()
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < equipTags.size(); i++)
            {
                if (i > 0) sb.append(',');
                HDict tags = equipTags.get(i);
                sb.append('@');
                sb.append(tags.id());
            }
            return sb.toString();
        }

        /**
          * return a list of the types which are similar to this type
          */
        void addSimilarTypes(
            HDictBuilder hdb, int thisType, 
            double percentMatch, EquipType[] types)
        {
            double dblPerc = percentMatch / 100;

            StringBuilder st = new StringBuilder();
            int n = 0;
            for (int i = 0; i < similarity.length; i++)
            {
                // similar
                if (i != thisType && similarity[i] >= dblPerc)
                {
                    if (n++ > 0) st.append(", ");
                    st.append("@type" + padZero(i,3));
                    st.append(" (" + (int)(similarity[i] * 100) + "%)");

                    hdb.add("type"+padZero(i,3)+"Missing", diffTypes(this, types[i]));
                    hdb.add("type"+padZero(i,3)+"Has",     diffTypes(types[i], this));
                }
            }

            hdb.add("similarTypes", st.toString());
        }

        /**
          * Compute the similary of this type to the other type, using
          * the Jaccard Index algorithm
          */
        double jaccardIndex(EquipType that)
        {
            Set<String> intersection = new TreeSet<>(pointNames);
            intersection.retainAll(that.pointNames);

            Set<String> union = new TreeSet<>(pointNames);
            union.addAll(that.pointNames);

            if (union.isEmpty())
                return 1;
            else
                return (double)intersection.size() / union.size();
        }

        final Set<String> pointNames;
        final List<BComponent> equips;
        final List<HDict> equipTags;
        double[] similarity; // percentage
    }

    static String diffTypes(EquipType a, EquipType b)
    {
        StringBuilder sb = new StringBuilder();

        Set<String> s = new TreeSet<>(a.pointNames);
        s.removeAll(b.pointNames);
        int n = 0;
        for (String value : s)
        {
            if (n++ > 0) sb.append(',');
            sb.append(value);
        }
        return sb.toString();
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private static final HDict[] EMPTY_HDICT_ARRAY = new HDict[0];

    private final NHServer server;
}
