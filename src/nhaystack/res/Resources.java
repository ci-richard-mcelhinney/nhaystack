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
    // tags.csv: kind -> tagName
    private static final Map kindTags = new TreeMap();

    static
    {
        try
        {
            loadTags(Resources.class);
        }
        catch (Exception e)
        {
            throw new BajaRuntimeException(e);
        }
    }

    private static void loadTags(Class cls) throws Exception
    {
        InputStream in = cls.getResourceAsStream("/nhaystack/res/tags.csv");
        BufferedReader bin = new BufferedReader(new InputStreamReader(in));

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

    public static String[] getKindTags(String kind)
    {
        if (!kindTags.containsKey(kind)) return new String[0];

        Array arr = new Array(String.class, (Set) kindTags.get(kind));
        return (String[]) arr.trim();
    }
}
