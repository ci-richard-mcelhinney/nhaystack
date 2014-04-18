//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   17 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver;

import java.util.*;

public class NameGenerator
{
    public String makeUniqueName(String name)
    {
        if (set.contains(name))
        {
            int n = 1;
            String s = name + n;
            while (set.contains(s))
                s = name + (++n);
            name = s;
        }

        set.add(name);
        return name;
    }

    Set set = new HashSet();
}
