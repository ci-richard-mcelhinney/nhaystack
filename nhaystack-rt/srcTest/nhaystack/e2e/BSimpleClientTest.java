//
// Copyright 2019 Project Haystack All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//    16 Mar 2020    Richard McELhinney  Creation
//

package nhaystack.e2e;

import com.tridium.history.log.BLogHistoryService;
import nhaystack.ntest.helper.BNHaystackStationTestBase;
import nhaystack.server.BRemotePointTest;
import org.projecthaystack.HDict;
import org.projecthaystack.client.CallException;
import org.projecthaystack.client.HClient;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;

import static org.testng.Assert.fail;

@NiagaraType
@Test
public class BSimpleClientTest extends BNHaystackStationTestBase
{

    @Override
    public Type getType() { return TYPE; }
    public static final Type TYPE = Sys.loadType(BSimpleClientTest.class);

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort) throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        this.station = station;
        station.getServices().add("LogHistoryService", new BLogHistoryService());
    }

    @BeforeTest
    @Override
    public void setupStation() throws Exception
    {
        super.setupStation();
        setupCompTree();
    }

    private void setupCompTree()
    {

    }

    @Test
    public void verifyAuth()
    {
        // get bad credentials
        try
        {  HClient.open(LOCAL_URI, INVALID_USER, INVALID_PASS).about(); fail(); }
        catch (Exception e)
        {
            Assert.assertTrue(true);
        }

        try
        {  HClient.open(LOCAL_URI, super.getSuperUsername(), INVALID_PASS).about(); fail(); }
        catch (CallException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            this.client = openClient(false);
            client.about();
        }
        catch(Exception e)
        {  e.printStackTrace(); fail(); }
    }

    @Test
    public void verifyAbout() throws Exception
    {
        // non-secure
        this.client = openClient(false);
        HDict r = client.about();
        Assert.assertEquals(r.getStr("haystackVersion"), "2.0");
        Assert.assertEquals(r.getStr("productName"), "Niagara 4");
        Assert.assertEquals(r.getStr("productVersion"), "4.8.0.110");
        Assert.assertEquals(r.getStr("moduleVersion"), "3.0.1");
    }
}
