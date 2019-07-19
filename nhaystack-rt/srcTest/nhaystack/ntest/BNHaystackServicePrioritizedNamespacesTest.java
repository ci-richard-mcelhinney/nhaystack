//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//     19 Jul 2019  Eric Anderson  Creation
//
package nhaystack.ntest;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

import nhaystack.ntest.helper.BNHaystackStationTestBase;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@NiagaraType
@Test(singleThreaded = true)
public class BNHaystackServicePrioritizedNamespacesTest
    extends BNHaystackStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.ntest.BNHaystackServicePrioritizedNamespacesTest(2979906276)1.0$ @*/
/* Generated Fri Jul 12 17:30:27 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackServicePrioritizedNamespacesTest.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort)
        throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        configureAction.run();
    }

    @DataProvider
    private static Object[][] startupProvider()
    {
        return new Object[][]
        {
            {"", Collections.singletonList("hs")},
            {"hs", Collections.singletonList("hs")},
            {"hs,b", Arrays.asList("hs", "b")},
        };
    }

    @Test(dataProvider = "startupProvider")
    public void prioritizedNamespacesInitializedAtStartup(String propertyValue, List<String> expected)
        throws Exception
    {
        teardownStation();
        configureAction = () -> nhaystackService.setPrioritizedNamespaces(propertyValue);
        setupStation();
        assertEquals(nhaystackService.getPrioritizedNamespaceList(), expected);
    }

    @DataProvider
    private static Object[][] dataProvider()
    {
        return new Object[][]
        {
            {"", Collections.singletonList("hs")},
            {" ", Collections.singletonList("hs")},
            {",", Collections.singletonList("hs")},
            {" , ", Collections.singletonList("hs")},
            {"hs", Collections.singletonList("hs")},
            {"hs ", Collections.singletonList("hs")},
            {" hs ", Collections.singletonList("hs")},
            {" hs", Collections.singletonList("hs")},
            {",hs,", Collections.singletonList("hs")},
            {"hs,b", Arrays.asList("hs", "b")},
            {"hs, b", Arrays.asList("hs", "b")},
            {" hs , b ", Arrays.asList("hs", "b")},
            {" , hs , b , ", Arrays.asList("hs", "b")},
            {"b,hs", Arrays.asList("b", "hs")},
            {"hs,b,c", Arrays.asList("hs", "b", "c")},
            {"hs, b, c", Arrays.asList("hs", "b", "c")},
            {" hs , b , c ", Arrays.asList("hs", "b", "c")},
            {" , hs , b , c , ", Arrays.asList("hs", "b", "c")},
            {"b,hs,c", Arrays.asList("b", "hs", "c")},
            {"b,c,hs", Arrays.asList("b", "c", "hs")},
        };
    }

    @Test(dataProvider = "dataProvider")
    public void prioritizedNamespacesParsingTest(String propertyValue, List<String> expected)
    {
        nhaystackService.setPrioritizedNamespaces(propertyValue);
        assertEquals(nhaystackService.getPrioritizedNamespaceList(), expected);
    }

    private Runnable configureAction = () -> {};
}
