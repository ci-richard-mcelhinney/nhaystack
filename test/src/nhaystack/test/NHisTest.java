//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   06 Feb 2013  Mike Jarmy  Creation
//
package nhaystack.test;

import haystack.*;
import haystack.io.*;
import haystack.client.*;
import haystack.test.*;
import haystack.util.*;
import nhaystack.*;

/**
 * NHisTest -- this test uses his_sup
 */
public class NHisTest extends NTest
{
    final String URI = "http://localhost:6000/haystack/";
    HClient client;

    public void test() throws Exception
    {
        this.client = HClient.open(URI, "admin", "abc123");

        verifyHisRead();
    }

    void verifyHisRead() throws Exception
    {
        HGrid grid = client.readAll("his");
        verify(grid.numRows() == 302);

        for (int i = 0; i < grid.numRows(); i++)
        {
            HDict rec = grid.row(i);
            HGrid his = client.hisRead(rec.id(), "today");

            verifyEq(rec.get("tz"), localTz());

            if (rec.get("dis").equals(HStr.make("his_jace1_SineWave1")))
            {
                verifyEq(rec.get("unit"), HStr.make("°F"));
                for (int j = 0; j < his.numRows(); j++)
                    verifyEq(numVal(his.row(j)).unit, "°F");
            }
            else
            {
                verify(rec.missing("unit"));
                for (int j = 0; j < his.numRows(); j++)
                    verify(his.row(j).missing("unit"));
            }

            verifyEq(his.meta().id(), rec.id());
        }
    }

//////////////////////////////////////////////////////////////////////////
// Main
//////////////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        runTests(new String[] { "nhaystack.test.NHisTest", }, null);
    }
}
