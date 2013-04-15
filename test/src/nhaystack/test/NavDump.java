//
// Copyright (c) 2011, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.test;

import java.io.*;
import java.util.*;

import haystack.*;
import haystack.client.*;

public class NavDump
{
    public NavDump(String uri, String user, String password, String strDepth)
    {
        this.client = HClient.open(uri, user, password);
        this.maxDepth = Integer.parseInt(strDepth);
    }

    public void go() throws Exception
    {
        HGrid curNav = HGrid.EMPTY;
        dumpNav(client.call("nav", curNav), 1);
    }

    private void dumpNav(HGrid navGrid, int curDepth)
    {
        for (int i = 0; i < navGrid.numRows(); i++)
        {
            HRow row = navGrid.row(i);

            StringBuffer sb = new StringBuffer();

            HStr str = (HStr) row.get("navId", false);
            if (str != null) sb.append("[" + str + "]");
            else sb.append("[---]");

            sb.append(" '").append(row.get("dis")).append("'");

            for (int j = 0; j < curDepth-1; j++)
                System.out.print("    ");
            System.out.println(sb);

            if (curDepth < maxDepth)
            {
                str = (HStr) row.get("navId", false);
                if (str != null)
                {
                    HGrid navKids = client.call("nav", NTest.makeNavGrid(str));
                    dumpNav(navKids, curDepth + 1);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length != 4) throw new IllegalStateException();

        NavDump nav = new NavDump(args[0], args[1], args[2], args[3]);
        try
        {
            nav.go();
        }
        catch (CallErrException e)
        {
            e.printStackTrace();

            System.out.println();
            System.out.println("CallErrException server side trace:");
            System.out.println(((CallErrException)e).trace());
        }
    }

    private final HClient client;
    private final int maxDepth;
}
