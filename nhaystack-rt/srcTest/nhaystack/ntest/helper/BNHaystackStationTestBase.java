//
// Copyright (c) 2018. Tridium, Inc. All rights reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   16 May 2018  Eric Anderson  Creation
//

package nhaystack.ntest.helper;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BServiceContainer;
import javax.baja.web.BWebService;
import nhaystack.server.BNHaystackService;
import nhaystack.server.NHServer;
import org.projecthaystack.client.HClient;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import com.tridium.testng.BStationTestBase;

@NiagaraType
public abstract class BNHaystackStationTestBase extends BStationTestBase
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.test.helper.BNHaystackStationTestBase(2979906276)1.0$ @*/
/* Generated Wed May 16 10:01:19 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackStationTestBase.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    private static final String SECURE_URI = "https://localhost/haystack/";

    protected static final String EXTENDED_OP_NAME = "extended";
    protected static final String FUNCTION_OP_ARG_NAME = "function";

    protected final BNHaystackService nhaystackService = new BNHaystackService();
    protected NHServer nhServer;
    protected HClient hClient;

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort) throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        BServiceContainer services = station.getServices();

        services.add("NHaystackService", nhaystackService);
        nhaystackService.setSchemaVersion(1);

        BWebService webService = getWebService();
        webService.setHttpEnabled(true);
        webService.setHttpsOnly(false);
        webService.setHttpsEnabled(true);
    }

    @BeforeTest
    @Override
    public void setupStation() throws Exception
    {
        super.setupStation();

        nhServer = nhaystackService.getHaystackServer();
        hClient = openClient(false);
    }

    protected HClient openClient(boolean useHttps) throws InterruptedException
    {
        int count = 0;
        // wait up to 10 seconds for async operation to complete
        nhaystackService.doInitializeHaystack();
        while (!nhaystackService.getInitialized() || nhaystackService.getSchemaVersion() < 1 && count < 10)
        {
            Thread.sleep(1000);
            ++count;
        }

        if (nhaystackService.getSchemaVersion() < 1)
        {
            Assert.fail("Tag update not completed after nhaystackService started, aborting test.");
        }

        String baseURI = getBaseURI() + "haystack/";
        if (useHttps)
        {
            baseURI = SECURE_URI;
        }

        return HClient.open(baseURI, getSuperUsername(), getSuperUserPassword());
    }
}
