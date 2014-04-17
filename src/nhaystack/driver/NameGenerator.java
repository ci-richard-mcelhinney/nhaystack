//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   17 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver;

import java.util.*;
import org.projecthaystack.*;

public class NameGenerator
{
    public String makeName(HDict rec)
    {
        String name = dis(rec);

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

    private String dis(HDict rec)
    {
        if (rec.has("dis"))
            return rec.getStr("dis");

        HRef id = rec.id();
        if (id.dis != null) 
            return id.dis;

        return id.val;
    }

    Set set = new HashSet();
}
