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

public class NavExplorer
{
    public NavExplorer(String uri, String user, String password)
    {
        this.client = HClient.open(uri, user, password);
    }

    public void go() throws Exception
    {
        HGrid curNav = HGrid.EMPTY;

        HGrid navKids = client.call("nav", curNav);
        navKids.dump();
        System.out.print("$ ");

        BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        String str = buf.readLine();
        while (!str.equals("quit"))
        {
            curNav = (str.equals("")) ?
                HGrid.EMPTY : NTest.makeNavGrid(HStr.make(str));

            navKids = client.call("nav", curNav);
            navKids.dump();
            System.out.print("$ ");

            str = buf.readLine();
        }
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length != 3) throw new IllegalStateException();

        NavExplorer nav = new NavExplorer(args[0], args[1], args[2]);
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
}
