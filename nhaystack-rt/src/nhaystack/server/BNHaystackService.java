//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//   10 May 2018  Eric Anderson       Migrated to slot annotations, added missing @Overrides
//                                    annotations
//
package nhaystack.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.driver.BDeviceNetwork;
import javax.baja.history.db.BHistoryDatabase;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.nre.util.TextUtil;
import javax.baja.sys.Action;
import javax.baja.sys.BAbstractService;
import javax.baja.sys.BComponent;
import javax.baja.sys.BIcon;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BString;
import javax.baja.sys.BValue;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BServiceContainer;
import javax.baja.util.IFuture;
import javax.baja.util.Invocation;
import nhaystack.BHDict;
import nhaystack.BHGrid;
import nhaystack.BHRef;
import nhaystack.site.BHEquip;
import nhaystack.site.BHSite;
import nhaystack.worker.BINHaystackWorkerParent;
import nhaystack.worker.BNHaystackWorker;
import nhaystack.worker.WorkerChore;
import nhaystack.worker.WorkerInvocation;
import org.projecthaystack.HDict;
import org.projecthaystack.HGridBuilder;
import org.projecthaystack.client.CallNetworkException;

/**
 * BNHaystackService makes an NHServer available.
 */
@NiagaraType
/**
 * Whether to show BHistoryConfigs that are linked to a BControlPoint
 */
@NiagaraProperty(
    name = "showLinkedHistories",
    type = "boolean",
    defaultValue = "false"
)
@NiagaraProperty(
    name = "servlet",
    type = "BNHaystackServlet",
    defaultValue = "new BNHaystackServlet()"
)
@NiagaraProperty(
    name = "stats",
    type = "BNHaystackStats",
    defaultValue = "new BNHaystackStats()"
)
@NiagaraProperty(
    name = "timeZoneAliases",
    type = "BTimeZoneAliasFolder",
    defaultValue = "new BTimeZoneAliasFolder()"
)
@NiagaraProperty(
    name = "worker",
    type = "BNHaystackWorker",
    defaultValue = "new BNHaystackWorker()"
)
@NiagaraProperty(
    name = "watchCount",
    type = "int",
    defaultValue = "0",
    flags = Flags.TRANSIENT|Flags.READONLY
)
@NiagaraProperty(
    name = "initialized",
    type = "boolean",
    defaultValue = "false",
    flags = Flags.TRANSIENT|Flags.READONLY
)
@NiagaraProperty(
    name = "initializationDelayTime",
    type = "BRelTime",
    defaultValue = "BRelTime.DEFAULT"
)
@NiagaraProperty(
    name = "foxLeaseInterval",
    type = "BRelTime",
    defaultValue = "BRelTime.makeMinutes(2)",
    flags = Flags.HIDDEN
)
@NiagaraProperty(
    name = "translateEnums",
    type = "boolean",
    defaultValue = "false",
    flags = Flags.HIDDEN
)

/**
 * Lookup an entity record by it's unique identifier.
 */
@NiagaraAction(
    name = "readById",
    parameterType = "BHRef",
    defaultValue = "BHRef.DEFAULT",
    returnType = "BHDict",
    flags = Flags.OPERATOR|Flags.HIDDEN
)
/**
 * Query every entity record that matches given filter.
 */
@NiagaraAction(
    name = "readAll",
    parameterType = "BString",
    defaultValue = "BString.DEFAULT",
    returnType = "BHGrid",
    flags = Flags.OPERATOR|Flags.HIDDEN
)
/**
 * fetch all the records that are tagged as 'site'.
 */
@NiagaraAction(
    name = "fetchSites",
    returnType = "BHGrid",
    flags = Flags.OPERATOR|Flags.HIDDEN
)
/**
 * fetch all the records that are tagged as 'equip'.
 */
@NiagaraAction(
    name = "fetchEquips",
    returnType = "BHGrid",
    flags = Flags.OPERATOR|Flags.HIDDEN
)
/**
 * fetch the site-equip-point nav tree in xml format
 */
@NiagaraAction(
    name = "fetchSepNav",
    returnType = "BString",
    flags = Flags.OPERATOR|Flags.HIDDEN
)
/**
 * fetch the tags that the server auto-generates.
 */
@NiagaraAction(
    name = "fetchAutoGenTags",
    returnType = "BString",
    flags = Flags.OPERATOR|Flags.HIDDEN
)
/**
 * find all the unique equip types
 */
@NiagaraAction(
    name = "findUniqueEquipTypes",
    parameterType = "BUniqueEquipTypeArgs",
    defaultValue = "new BUniqueEquipTypeArgs()",
    returnType = "BOrd",
    flags = Flags.OPERATOR|Flags.ASYNC|Flags.HIDDEN
)
/**
 * apply the schedule now that the ticket has expired
 */
@NiagaraAction(
    name = "applySchedule",
    parameterType = "BHScheduleEvent",
    defaultValue = "new BHScheduleEvent()",
    flags = Flags.OPERATOR|Flags.ASYNC|Flags.HIDDEN
)
/**
 * Initialize nhaystack
 */
@NiagaraAction(
    name = "initializeHaystack",
    flags = Flags.OPERATOR|Flags.ASYNC
)
/**
 * Rebuild the internal cache
 */
@NiagaraAction(
    name = "rebuildCache",
    returnType = "BOrd",
    flags = Flags.OPERATOR|Flags.ASYNC
)
/**
 * Remove all the invalid refs
 */
@NiagaraAction(
    name = "removeBrokenRefs",
    returnType = "BOrd",
    flags = Flags.OPERATOR|Flags.ASYNC
)
public class BNHaystackService 
    extends BAbstractService
    implements BINHaystackWorkerParent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackService(2741368347)1.0$ @*/
/* Generated Thu May 10 11:02:52 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "showLinkedHistories"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code showLinkedHistories} property.
   * Whether to show BHistoryConfigs that are linked to a BControlPoint
   * @see #getShowLinkedHistories
   * @see #setShowLinkedHistories
   */
  public static final Property showLinkedHistories = newProperty(0, false, null);
  
  /**
   * Get the {@code showLinkedHistories} property.
   * Whether to show BHistoryConfigs that are linked to a BControlPoint
   * @see #showLinkedHistories
   */
  public boolean getShowLinkedHistories() { return getBoolean(showLinkedHistories); }
  
  /**
   * Set the {@code showLinkedHistories} property.
   * Whether to show BHistoryConfigs that are linked to a BControlPoint
   * @see #showLinkedHistories
   */
  public void setShowLinkedHistories(boolean v) { setBoolean(showLinkedHistories, v, null); }

////////////////////////////////////////////////////////////////
// Property "servlet"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code servlet} property.
   * @see #getServlet
   * @see #setServlet
   */
  public static final Property servlet = newProperty(0, new BNHaystackServlet(), null);
  
  /**
   * Get the {@code servlet} property.
   * @see #servlet
   */
  public BNHaystackServlet getServlet() { return (BNHaystackServlet)get(servlet); }
  
  /**
   * Set the {@code servlet} property.
   * @see #servlet
   */
  public void setServlet(BNHaystackServlet v) { set(servlet, v, null); }

////////////////////////////////////////////////////////////////
// Property "stats"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code stats} property.
   * @see #getStats
   * @see #setStats
   */
  public static final Property stats = newProperty(0, new BNHaystackStats(), null);
  
  /**
   * Get the {@code stats} property.
   * @see #stats
   */
  public BNHaystackStats getStats() { return (BNHaystackStats)get(stats); }
  
  /**
   * Set the {@code stats} property.
   * @see #stats
   */
  public void setStats(BNHaystackStats v) { set(stats, v, null); }

////////////////////////////////////////////////////////////////
// Property "timeZoneAliases"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code timeZoneAliases} property.
   * @see #getTimeZoneAliases
   * @see #setTimeZoneAliases
   */
  public static final Property timeZoneAliases = newProperty(0, new BTimeZoneAliasFolder(), null);
  
  /**
   * Get the {@code timeZoneAliases} property.
   * @see #timeZoneAliases
   */
  public BTimeZoneAliasFolder getTimeZoneAliases() { return (BTimeZoneAliasFolder)get(timeZoneAliases); }
  
  /**
   * Set the {@code timeZoneAliases} property.
   * @see #timeZoneAliases
   */
  public void setTimeZoneAliases(BTimeZoneAliasFolder v) { set(timeZoneAliases, v, null); }

////////////////////////////////////////////////////////////////
// Property "worker"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code worker} property.
   * @see #getWorker
   * @see #setWorker
   */
  public static final Property worker = newProperty(0, new BNHaystackWorker(), null);
  
  /**
   * Get the {@code worker} property.
   * @see #worker
   */
  public BNHaystackWorker getWorker() { return (BNHaystackWorker)get(worker); }
  
  /**
   * Set the {@code worker} property.
   * @see #worker
   */
  public void setWorker(BNHaystackWorker v) { set(worker, v, null); }

////////////////////////////////////////////////////////////////
// Property "watchCount"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code watchCount} property.
   * @see #getWatchCount
   * @see #setWatchCount
   */
  public static final Property watchCount = newProperty(Flags.TRANSIENT | Flags.READONLY, 0, null);
  
  /**
   * Get the {@code watchCount} property.
   * @see #watchCount
   */
  public int getWatchCount() { return getInt(watchCount); }
  
  /**
   * Set the {@code watchCount} property.
   * @see #watchCount
   */
  public void setWatchCount(int v) { setInt(watchCount, v, null); }

////////////////////////////////////////////////////////////////
// Property "initialized"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code initialized} property.
   * @see #getInitialized
   * @see #setInitialized
   */
  public static final Property initialized = newProperty(Flags.TRANSIENT | Flags.READONLY, false, null);
  
  /**
   * Get the {@code initialized} property.
   * @see #initialized
   */
  public boolean getInitialized() { return getBoolean(initialized); }
  
  /**
   * Set the {@code initialized} property.
   * @see #initialized
   */
  public void setInitialized(boolean v) { setBoolean(initialized, v, null); }

////////////////////////////////////////////////////////////////
// Property "initializationDelayTime"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code initializationDelayTime} property.
   * @see #getInitializationDelayTime
   * @see #setInitializationDelayTime
   */
  public static final Property initializationDelayTime = newProperty(0, BRelTime.DEFAULT, null);
  
  /**
   * Get the {@code initializationDelayTime} property.
   * @see #initializationDelayTime
   */
  public BRelTime getInitializationDelayTime() { return (BRelTime)get(initializationDelayTime); }
  
  /**
   * Set the {@code initializationDelayTime} property.
   * @see #initializationDelayTime
   */
  public void setInitializationDelayTime(BRelTime v) { set(initializationDelayTime, v, null); }

////////////////////////////////////////////////////////////////
// Property "foxLeaseInterval"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code foxLeaseInterval} property.
   * @see #getFoxLeaseInterval
   * @see #setFoxLeaseInterval
   */
  public static final Property foxLeaseInterval = newProperty(Flags.HIDDEN, BRelTime.makeMinutes(2), null);
  
  /**
   * Get the {@code foxLeaseInterval} property.
   * @see #foxLeaseInterval
   */
  public BRelTime getFoxLeaseInterval() { return (BRelTime)get(foxLeaseInterval); }
  
  /**
   * Set the {@code foxLeaseInterval} property.
   * @see #foxLeaseInterval
   */
  public void setFoxLeaseInterval(BRelTime v) { set(foxLeaseInterval, v, null); }

////////////////////////////////////////////////////////////////
// Property "translateEnums"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code translateEnums} property.
   * @see #getTranslateEnums
   * @see #setTranslateEnums
   */
  public static final Property translateEnums = newProperty(Flags.HIDDEN, false, null);
  
  /**
   * Get the {@code translateEnums} property.
   * @see #translateEnums
   */
  public boolean getTranslateEnums() { return getBoolean(translateEnums); }
  
  /**
   * Set the {@code translateEnums} property.
   * @see #translateEnums
   */
  public void setTranslateEnums(boolean v) { setBoolean(translateEnums, v, null); }

////////////////////////////////////////////////////////////////
// Action "readById"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code readById} action.
   * Lookup an entity record by it's unique identifier.
   * @see #readById(BHRef parameter)
   */
  public static final Action readById = newAction(Flags.OPERATOR | Flags.HIDDEN, BHRef.DEFAULT, null);
  
  /**
   * Invoke the {@code readById} action.
   * Lookup an entity record by it's unique identifier.
   * @see #readById
   */
  public BHDict readById(BHRef parameter) { return (BHDict)invoke(readById, parameter, null); }

////////////////////////////////////////////////////////////////
// Action "readAll"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code readAll} action.
   * Query every entity record that matches given filter.
   * @see #readAll(BString parameter)
   */
  public static final Action readAll = newAction(Flags.OPERATOR | Flags.HIDDEN, BString.DEFAULT, null);
  
  /**
   * Invoke the {@code readAll} action.
   * Query every entity record that matches given filter.
   * @see #readAll
   */
  public BHGrid readAll(BString parameter) { return (BHGrid)invoke(readAll, parameter, null); }

////////////////////////////////////////////////////////////////
// Action "fetchSites"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code fetchSites} action.
   * fetch all the records that are tagged as 'site'.
   * @see #fetchSites()
   */
  public static final Action fetchSites = newAction(Flags.OPERATOR | Flags.HIDDEN, null);
  
  /**
   * Invoke the {@code fetchSites} action.
   * fetch all the records that are tagged as 'site'.
   * @see #fetchSites
   */
  public BHGrid fetchSites() { return (BHGrid)invoke(fetchSites, null, null); }

////////////////////////////////////////////////////////////////
// Action "fetchEquips"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code fetchEquips} action.
   * fetch all the records that are tagged as 'equip'.
   * @see #fetchEquips()
   */
  public static final Action fetchEquips = newAction(Flags.OPERATOR | Flags.HIDDEN, null);
  
  /**
   * Invoke the {@code fetchEquips} action.
   * fetch all the records that are tagged as 'equip'.
   * @see #fetchEquips
   */
  public BHGrid fetchEquips() { return (BHGrid)invoke(fetchEquips, null, null); }

////////////////////////////////////////////////////////////////
// Action "fetchSepNav"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code fetchSepNav} action.
   * fetch the site-equip-point nav tree in xml format
   * @see #fetchSepNav()
   */
  public static final Action fetchSepNav = newAction(Flags.OPERATOR | Flags.HIDDEN, null);
  
  /**
   * Invoke the {@code fetchSepNav} action.
   * fetch the site-equip-point nav tree in xml format
   * @see #fetchSepNav
   */
  public BString fetchSepNav() { return (BString)invoke(fetchSepNav, null, null); }

////////////////////////////////////////////////////////////////
// Action "fetchAutoGenTags"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code fetchAutoGenTags} action.
   * fetch the tags that the server auto-generates.
   * @see #fetchAutoGenTags()
   */
  public static final Action fetchAutoGenTags = newAction(Flags.OPERATOR | Flags.HIDDEN, null);
  
  /**
   * Invoke the {@code fetchAutoGenTags} action.
   * fetch the tags that the server auto-generates.
   * @see #fetchAutoGenTags
   */
  public BString fetchAutoGenTags() { return (BString)invoke(fetchAutoGenTags, null, null); }

////////////////////////////////////////////////////////////////
// Action "findUniqueEquipTypes"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code findUniqueEquipTypes} action.
   * find all the unique equip types
   * @see #findUniqueEquipTypes(BUniqueEquipTypeArgs parameter)
   */
  public static final Action findUniqueEquipTypes = newAction(Flags.OPERATOR | Flags.ASYNC | Flags.HIDDEN, new BUniqueEquipTypeArgs(), null);
  
  /**
   * Invoke the {@code findUniqueEquipTypes} action.
   * find all the unique equip types
   * @see #findUniqueEquipTypes
   */
  public BOrd findUniqueEquipTypes(BUniqueEquipTypeArgs parameter) { return (BOrd)invoke(findUniqueEquipTypes, parameter, null); }

////////////////////////////////////////////////////////////////
// Action "applySchedule"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code applySchedule} action.
   * apply the schedule now that the ticket has expired
   * @see #applySchedule(BHScheduleEvent parameter)
   */
  public static final Action applySchedule = newAction(Flags.OPERATOR | Flags.ASYNC | Flags.HIDDEN, new BHScheduleEvent(), null);
  
  /**
   * Invoke the {@code applySchedule} action.
   * apply the schedule now that the ticket has expired
   * @see #applySchedule
   */
  public void applySchedule(BHScheduleEvent parameter) { invoke(applySchedule, parameter, null); }

////////////////////////////////////////////////////////////////
// Action "initializeHaystack"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code initializeHaystack} action.
   * Initialize nhaystack
   * @see #initializeHaystack()
   */
  public static final Action initializeHaystack = newAction(Flags.OPERATOR | Flags.ASYNC, null);
  
  /**
   * Invoke the {@code initializeHaystack} action.
   * Initialize nhaystack
   * @see #initializeHaystack
   */
  public void initializeHaystack() { invoke(initializeHaystack, null, null); }

////////////////////////////////////////////////////////////////
// Action "rebuildCache"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code rebuildCache} action.
   * Rebuild the internal cache
   * @see #rebuildCache()
   */
  public static final Action rebuildCache = newAction(Flags.OPERATOR | Flags.ASYNC, null);
  
  /**
   * Invoke the {@code rebuildCache} action.
   * Rebuild the internal cache
   * @see #rebuildCache
   */
  public BOrd rebuildCache() { return (BOrd)invoke(rebuildCache, null, null); }

////////////////////////////////////////////////////////////////
// Action "removeBrokenRefs"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code removeBrokenRefs} action.
   * Remove all the invalid refs
   * @see #removeBrokenRefs()
   */
  public static final Action removeBrokenRefs = newAction(Flags.OPERATOR | Flags.ASYNC, null);
  
  /**
   * Invoke the {@code removeBrokenRefs} action.
   * Remove all the invalid refs
   * @see #removeBrokenRefs
   */
  public BOrd removeBrokenRefs() { return (BOrd)invoke(removeBrokenRefs, null, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackService.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    public boolean isParentLegal(BComponent parent)
    {
        return parent instanceof BServiceContainer;
    }

////////////////////////////////////////////////////////////////
// BIService
////////////////////////////////////////////////////////////////

    @Override
    public Type[] getServiceTypes() { return SERVICE_TYPES; }

    @Override
    public void serviceStarted() throws Exception
    {
        LOG.info("NHaystack Service started");
        this.server = createServer();
    }

    @Override
    public void serviceStopped()
    {
        LOG.info("NHaystack Service stopped");
    }

    @Override
    public void atSteadyState() throws Exception
    {
        BRelTime initDelay = getInitializationDelayTime();

        // initialize immediately
        if (initDelay.equals(BRelTime.DEFAULT))
        {
            initializeHaystack();
        }
        // wait for a while, so field bus can initialize, etc
        else
        {
            LOG.info("Delaying NHaystack initialization for " + initDelay);
            Clock.schedule(this, initDelay, initializeHaystack, null);
        }
    }

////////////////////////////////////////////////////////////////
// Actions
////////////////////////////////////////////////////////////////

    public BHDict doReadById(BHRef id)
    {
        return BHDict.make(server.readById(id.getRef()));
    }

    public BHGrid doReadAll(BString filter)
    {
        return BHGrid.make(server.readAll(filter.getString()));
    }

    public BHGrid doFetchSites()
    {
        BHSite[] sites = server.getCache().getAllSites();

        HDict[] dicts = new HDict[sites.length];
        for (int i = 0; i < sites.length; i++)
            dicts[i] = server.getTagManager().createComponentTags(sites[i]);

        return BHGrid.make(HGridBuilder.dictsToGrid(dicts));
    }

    public BHGrid doFetchEquips()
    {
        BHEquip[] equips = server.getCache().getAllEquips();

        HDict[] dicts = new HDict[equips.length];
        for (int i = 0; i < equips.length; i++)
            dicts[i] = server.getTagManager().createComponentTags(equips[i]);

        return BHGrid.make(HGridBuilder.dictsToGrid(dicts));
    }

    public BString doFetchSepNav()
    {
        try
        {
            return BString.make(server.getNav().fetchSepNav());
        }
        catch (Exception e)
        {
            throw new BajaRuntimeException(e);
        }
    }

    public BString doFetchAutoGenTags()
    {
        try
        {
            return BString.make(TextUtil.join(server.getAutoGeneratedTags(), ','));
        }
        catch (Exception e)
        {
            throw new BajaRuntimeException(e);
        }
    }

////////////////////////////////////////////////////////////////
// async
////////////////////////////////////////////////////////////////

    @Override
    public IFuture post(Action action, BValue value, Context cx)
    {
        if ((action == initializeHaystack) ||
            (action == rebuildCache) ||
            (action == removeBrokenRefs) ||
            (action == findUniqueEquipTypes) ||
            (action == applySchedule))
        {
            return postAsyncChore(
                new WorkerInvocation(
                    getWorker(),
                    action.getName(),
                    new Invocation(this, action, value, cx)));
        }

        else return super.post(action, value, cx);
    }

    public final IFuture postAsyncChore(WorkerChore chore)
    {
        if (!isRunning()) return null;

        if (!getEnabled())
        {
            if (LOG.isLoggable(Level.FINE))
                LOG.fine(getSlotPath() + " disabled: " + chore);
            return null;
        }

        try
        {
            getWorker().enqueueChore(chore);
            return null;
        }
        catch (Exception e)
        {
            LOG.severe(getSlotPath() + " Cannot post async: " + e.getMessage());
            return null;
        }
    }

    public void doInitializeHaystack()
    {
        LOG.info("Begin initializing NHaystack");

        getHaystackServer().getCache().rebuild(getStats());
        getServlet().enableWithMessage(true);
        setInitialized(true);

        LOG.info("End initializing NHaystack");
    }

    public BOrd doRebuildCache()
    {
        BNHaystackRebuildCacheJob job = new BNHaystackRebuildCacheJob(this);
        return job.submit(null);
    }

    public BOrd doRemoveBrokenRefs()
    {
        BNHaystackRemoveBrokenRefsJob job = new BNHaystackRemoveBrokenRefsJob(this);
        return job.submit(null);
    }

    public void doApplySchedule(BHScheduleEvent event)
    {
        server.getScheduleManager().applySchedule(event);
    }

    public BOrd doFindUniqueEquipTypes(BUniqueEquipTypeArgs args)
    {
        BUniqueEquipTypeJob job = new BUniqueEquipTypeJob(
            this, args.getFilter(), args.getPercentMatch(), args.getApplyTags());
        return job.submit(null);
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    public NHServer getHaystackServer()
    {
        return server;
    }

    public BHistoryDatabase getHistoryDb()
    {
        if (historyDb == null)
            historyDb = (BHistoryDatabase)
                BOrd.make("history:").resolve(this, null).get();

        return historyDb;
    }

    public final BDeviceNetwork getNiagaraNetwork()
    {
        if (niagaraNetwork == null)
            niagaraNetwork = (BDeviceNetwork)
                NIAGARA_NETWORK.resolve(this, null).get();

        return niagaraNetwork;
    }

////////////////////////////////////////////////////////////////
// BINHaystackWorkerParent
////////////////////////////////////////////////////////////////

    @Override
    public void handleNetworkException(WorkerChore chore, CallNetworkException e)
    {
        // no op
    }

////////////////////////////////////////////////////////////////
// protected
////////////////////////////////////////////////////////////////

    protected NHServer createServer()
    {
        return new NHServer(this);
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Logger LOG = Logger.getLogger("nhaystack");

    @Override
    public BIcon getIcon() { return ICON; }
    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/tag.png");

    private static final BOrd NIAGARA_NETWORK = BOrd.make("station:|slot:/Drivers/NiagaraNetwork");

    private static final Type[] SERVICE_TYPES = { TYPE };

    private NHServer server;

    private BHistoryDatabase historyDb;
    private BDeviceNetwork niagaraNetwork;
}
