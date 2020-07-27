//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   17 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Added use of generics

package nhaystack.driver;

import java.util.HashSet;
import java.util.Set;

/**
  * NameGenerator generates a sequence of unique names
  */
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

    final Set<String> set = new HashSet<>();
}
