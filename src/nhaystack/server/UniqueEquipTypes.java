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
    public HGrid createTypes(BComponent[] equips, String filter, double percentMatch)
    {
        // find all the distinct types
        Map typeMap = new HashMap();
        for (int i = 0; i < equips.length; i++)
        {
            HDict equipTags = server.getTagManager().createTags(equips[i]);
            Set pointNames = findPointNames(server, (BHEquip) equips[i]);

            EquipType type = (EquipType) typeMap.get(pointNames);
            if (type == null)
                typeMap.put(pointNames, type = new EquipType(pointNames));

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
            n++;
        }

        // compute the similarities (this is exponentially slow!!!)
        for (int i = 0; i < types.length; i++)
        {
            EquipType a = types[i];
            a.similarity[i] = 1;
            for (int j = i+1; j < types.length; j++)
            {
                EquipType b = types[j];
                double similarity = a.jaccardIndex(b); 
                a.similarity[j] = similarity;
                b.similarity[i] = similarity;
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
            hdb.add("equipTags", type.listOfEquipIds());
            hdb.add("equipType", i);

            hdb.add("similarTypes", type.similarTypes(i, percentMatch));

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
                HDict tags = (HDict) equipTags.get(i);
                sb.append("@");
                sb.append(tags.id());
            }
            return sb.toString();
        }

        /**
          * return a list of the types which are similar to this type
          */
        String similarTypes(int thisType, double percentMatch)
        {
            double d = percentMatch / 100;

            StringBuffer sb = new StringBuffer();
            int n = 0;
            for (int i = 0; i < similarity.length; i++)
            {
                if ((i != thisType) && (similarity[i] >= d))
                {
                    if (n++ > 0) sb.append(", ");
                    sb.append("@type" + padZero(i,3));
                    sb.append(" (" + ((int) (similarity[i] * 100)) + "%)");
                }
            }
            return sb.toString();
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

////////////////////////////////////////////////////////////////
// attribs
////////////////////////////////////////////////////////////////

    private NHServer server;
}
