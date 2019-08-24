//
// Copyright (c) 2018. Tridium, Inc. All rights reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   16 May 2018  Eric Anderson  Creation
//   19 Jul 2018  Eric Anderson  Moved rebuildCache method for reuse in other tests
//

package nhaystack.ntest.helper;

import static nhaystack.ntest.helper.NHaystackTestUtil.purgeDirectory;
import static nhaystack.util.NHaystackConst.TAGS_VERSION_IMPORT;
import static org.testng.Assert.assertEquals;

import java.io.File;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tagdictionary.BTagDictionaryService;
import javax.baja.util.BServiceContainer;
import javax.baja.web.BWebServer;
import javax.baja.web.BWebService;

import nhaystack.server.BNHaystackRebuildCacheJob;
import nhaystack.server.BNHaystackService;
import nhaystack.server.NHServer;
import org.projecthaystack.client.HClient;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import com.tridium.haystack.BHsTagDictionary;
import com.tridium.jetty.BJettyWebServer;
import com.tridium.testng.BStationTestBase;
import com.tridium.testng.TestUtil;

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

    @Override
    protected void configureTestStation(BStation station, String stationName, int webPort, int foxPort) throws Exception
    {
        super.configureTestStation(station, stationName, webPort, foxPort);

        // Disable history warmup
        System.setProperty("niagara.history.warmup", "false");

        // Ensure that the history directory is purged so histories from other tests do not show up
        // and fail tests in this class that are not expecting them (audit history, for example).
        File protectedStationHome = Sys.getProtectedStationHome();
        if (protectedStationHome.exists())
        {
            purgeDirectory(protectedStationHome);
        }

        BServiceContainer services = station.getServices();

        BTagDictionaryService tagDictionaryService = new BTagDictionaryService();
        services.add("tagDictionaryService", tagDictionaryService);

        // Set tagsImportFile
        haystackDict = new BHsTagDictionary();
        haystackDict.setTagsImportFile(BOrd.make("module://nhaystack/nhaystack/res/tagsMerge.csv"));
        tagDictionaryService.add("haystack", haystackDict);

        nhaystackService = new BNHaystackService();
        nhaystackService.setSchemaVersion(1);
        nhaystackService.setShowLinkedHistories(true);
        services.add("NHaystackService", nhaystackService);
    }

    @Override
    protected BWebService makeWebService(int port) throws Exception
    {
        BWebService service = new BWebService();
        service.setHttpsEnabled(false);
        service.setHttpsOnly(false);
        service.setHttpEnabled(true);
        service.getHttpPort().setPublicServerPort(port);
        service.getHttpsPort().setPublicServerPort(8443);

        BWebServer server = new BJettyWebServer();
        service.add("JettyWebServer", server);

        return service;
    }

    @BeforeTest
    @Override
    public void setupStation() throws Exception
    {
        super.setupStation();

        nhServer = nhaystackService.getHaystackServer();
        client = openClient(false);

        TestUtil.waitFor(10, () -> !"1.0".equals(haystackDict.getVersion()), "Waiting for asynchronous import to finish");
        assertEquals(haystackDict.getVersion(), TAGS_VERSION_IMPORT, "dictionary version after import");
    }

    protected void rebuildCache()
    {
        try
        {
            new BNHaystackRebuildCacheJob(nhaystackService).run(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    protected HClient openClient(boolean useHttps) throws InterruptedException
    {
        // wait up to 10 seconds for async operation to complete
        nhaystackService.doInitializeHaystack();
        TestUtil.waitFor(10, () -> nhaystackService.getSchemaVersion() > 0,
            "Tag update not completed after nhaystackService started, aborting test.");

        String baseURI = getBaseURI() + "haystack/";
        if (useHttps)
        {
            baseURI = SECURE_URI;
        }

        return HClient.open(baseURI, getSuperUsername(), getSuperUserPassword());
    }

    private static final String SECURE_URI = "https://localhost/haystack/";

    protected static final String EXTENDED_OP_NAME = "extended";
    protected static final String FUNCTION_OP_ARG_NAME = "function";

    protected BHsTagDictionary haystackDict;
    protected BNHaystackService nhaystackService;
    protected NHServer nhServer;
    protected HClient client;
}
