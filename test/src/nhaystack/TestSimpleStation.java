//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   21 Dec 2012  Mike Jarmy  Creation
//

package nhaystack.util;

import java.util.*;
import junit.framework.*;

import haystack.*;
import haystack.client.*;

public class TestSimpleStation extends TestCase 
{
    private static void dumpDict(HDict dict)
    {
        System.out.println("-----------------------------------------------------");
        for (Iterator it = dict.iterator(); it.hasNext(); )
        {
            Map.Entry e = (Map.Entry)it.next();
            String name = (String)e.getKey();
            HVal val    = (HVal)e.getValue();
            System.out.println(name + ": " + val);
        }
    }

    public void testSimpleStation() throws Exception
    {
        HClient hc = HClient.open("http://localhost/haystack/", "admin", "");

        HGrid ops = hc.ops();
        ops.dump();

        HDict about = hc.about();
        dumpDict(about);
    }
}
