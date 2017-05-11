//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   05 Jun 2014  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;

import javax.baja.control.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.security.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.io.*;
import org.projecthaystack.server.*;

import nhaystack.*;
import nhaystack.collection.*;
import nhaystack.site.*;
import nhaystack.util.*;

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
        Map typeMap = new HashMap();
        for (int i = 0; i < equips.length; i++)
        {
            HDict equipTags = server.getTagManager().createTags(equips[i]);
            Set pointNames = findPointNames(server, (BHEquip) equips[i]);

            String key = pointNames.toString();
            EquipType type = (EquipType) typeMap.get(key);
            if (type == null)
                typeMap.put(key, type = new EquipType(pointNames));

            type.equips.add(equips[i]);
            type.equipTags.add(equipTags);
        }

        if (typeMap.size() >= 1000)
            throw new IllegalStateException("There are too many equip types: " + typeMap.size());

        // save types to array
        EquipType[] types = new EquipType[typeMap.size()];
        Iterator it = typeMap.values().iterator();
        int n = 0;
        while (it.hasNext())
        {
            EquipType type = (EquipType) it.next();
            types[n] = type;

            // init matching
            type.similarity = new double[typeMap.size()];

            // update tags with type
            if (applyTags)
            {
                for (int i = 0; i < type.equips.size(); i++)
                {
                    BComponent equip = (BComponent) type.equips.get(i);
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

        Array arr = new Array(HDict.class);
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

        return HGridBuilder.dictsToGrid(meta.toDict(), (HDict[]) arr.trim());
    }

    private static String padZero(int number, int width)
    {
        String s = TextUtil.padLeft(""+number, width);
        return TextUtil.replace(s, " ", "0");
    }

    /**
      * findPointNames
      */
    private static Set findPointNames(NHServer server, BHEquip equip)
    {
        Set set = new TreeSet();
        BComponent[] points = server.getCache().getEquipPoints(equip);
        for (int i = 0; i < points.length; i++)
        {
            HDict pointTags = server.getTagManager().createTags(points[i]);
            set.add(pointTags.getStr("navName"));
        }
        return set;
    }

    /**
      * EquipType
      */
    static class EquipType
    {
        EquipType(Set pointNames)
        {
            this.pointNames = pointNames;
            this.equips = new ArrayList();
            this.equipTags = new ArrayList();
        }

        /**
          * return a prettified list of the equips that have this type
          */
        String listOfEquipIds()
        {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < equipTags.size(); i++)
            {
                if (i > 0) sb.append(",");
                HDict tags = (HDict) equipTags.get(i);
                sb.append("@");
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

            StringBuffer st = new StringBuffer();
            int n = 0;
            for (int i = 0; i < similarity.length; i++)
            {
                // similar
                if ((i != thisType) && (similarity[i] >= dblPerc))
                {
                    if (n++ > 0) st.append(", ");
                    st.append("@type" + padZero(i,3));
                    st.append(" (" + ((int) (similarity[i] * 100)) + "%)");

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
            Set intersection = new TreeSet(pointNames);
            intersection.retainAll(that.pointNames);

            Set union = new TreeSet(pointNames);
            union.addAll(that.pointNames);

            if (union.size() == 0) return 1;
            return ((double) intersection.size())/((double) union.size());
        }

        Set pointNames;
        List equips;
        List equipTags;
        double[] similarity; // percentage
    }

    static String diffTypes(EquipType a, EquipType b)
    {
        StringBuffer sb = new StringBuffer();

        Set s = new TreeSet(a.pointNames); 
        s.removeAll(b.pointNames);
        int n = 0;
        Iterator it = s.iterator();
        while (it.hasNext())
        {
            if (n++ > 0) sb.append(",");
            sb.append(it.next());
        }
        return sb.toString();
    }

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private NHServer server;
}
