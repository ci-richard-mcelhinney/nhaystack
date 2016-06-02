//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//
package nhaystack.server;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import javax.baja.driver.*;
import javax.baja.history.db.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.util.*;
import javax.baja.nre.util.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.*;
import nhaystack.worker.*;
import nhaystack.site.*;

/**
  * BNHaystackService makes an NHServer available.  
  */
public class BNHaystackService 
    extends BAbstractService
    implements BINHaystackWorkerParent
{
    /*-
    class BNHaystackService
    {
        properties
        {
            showLinkedHistories: boolean
                 -- Whether to show BHistoryConfigs that are linked to a BControlPoint 
                default{[ false ]} 
            servlet: BNHaystackServlet
                default{[ new BNHaystackServlet() ]}
            stats: BNHaystackStats
                default{[ new BNHaystackStats() ]}
            timeZoneAliases: BTimeZoneAliasFolder
                default{[ new BTimeZoneAliasFolder() ]}
            worker: BNHaystackWorker
                default{[ new BNHaystackWorker() ]}
            watchCount: int
                flags { transient, readonly }
                default{[ 0 ]}
            initialized: boolean
                flags { transient, readonly }
                default{[ false ]}
            initializationDelayTime: BRelTime 
                default{[ BRelTime.DEFAULT ]}
            foxLeaseInterval: BRelTime 
                flags { hidden }
                default{[ BRelTime.makeMinutes(2) ]}
            translateEnums: boolean
                flags { hidden }
                default{[ false ]}
        }
        actions
        {
            readById(id: BHRef): BHDict 
                -- Lookup an entity record by it's unique identifier.
                flags { operator, hidden }
                default {[ BHRef.DEFAULT ]}
            readAll(filter: BString): BHGrid
                -- Query every entity record that matches given filter.
                flags { operator, hidden }
                default {[ BString.DEFAULT ]}
            fetchSites(): BHGrid
                -- fetch all the records that are tagged as 'site'.
                flags { operator, hidden }
            fetchEquips(): BHGrid
                -- fetch all the records that are tagged as 'equip'.
                flags { operator, hidden }
            fetchSepNav(): BString
                -- fetch the site-equip-point nav tree in xml format
                flags { operator, hidden }
            fetchAutoGenTags(): BString
                -- fetch the tags that the server auto-generates.
                flags { operator, hidden }

            findUniqueEquipTypes(args: BUniqueEquipTypeArgs): BOrd
                -- find all the unique equip types
                flags { operator, async, hidden }
                default {[ new BUniqueEquipTypeArgs() ]}

            applySchedule(event: BHScheduleEvent)
                -- apply the schedule now that the ticket has expired
                flags { operator, async, hidden }
                default {[ new BHScheduleEvent() ]}

            initializeHaystack()
                -- Initialize nhaystack
                flags { operator, async }
            rebuildCache(): BOrd
                -- Rebuild the internal cache
                flags { operator, async }
            removeBrokenRefs(): BOrd
                -- Remove all the invalid refs
                flags { operator, async }
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackService(617458771)1.0$ @*/
/* Generated Mon Oct 19 10:37:25 PDT 2015 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "showLinkedHistories"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>showLinkedHistories</code> property.
   * Whether to show BHistoryConfigs that are linked to
   * a BControlPoint
   * @see nhaystack.server.BNHaystackService#getShowLinkedHistories
   * @see nhaystack.server.BNHaystackService#setShowLinkedHistories
   */
  public static final Property showLinkedHistories = newProperty(0, false,null);
  
  /**
   * Get the <code>showLinkedHistories</code> property.
   * Whether to show BHistoryConfigs that are linked to
   * a BControlPoint
   * @see nhaystack.server.BNHaystackService#showLinkedHistories
   */
  public boolean getShowLinkedHistories() { return getBoolean(showLinkedHistories); }
  
  /**
   * Set the <code>showLinkedHistories</code> property.
   * Whether to show BHistoryConfigs that are linked to
   * a BControlPoint
   * @see nhaystack.server.BNHaystackService#showLinkedHistories
   */
  public void setShowLinkedHistories(boolean v) { setBoolean(showLinkedHistories,v,null); }

////////////////////////////////////////////////////////////////
// Property "servlet"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>servlet</code> property.
   * @see nhaystack.server.BNHaystackService#getServlet
   * @see nhaystack.server.BNHaystackService#setServlet
   */
  public static final Property servlet = newProperty(0, new BNHaystackServlet(),null);
  
  /**
   * Get the <code>servlet</code> property.
   * @see nhaystack.server.BNHaystackService#servlet
   */
  public BNHaystackServlet getServlet() { return (BNHaystackServlet)get(servlet); }
  
  /**
   * Set the <code>servlet</code> property.
   * @see nhaystack.server.BNHaystackService#servlet
   */
  public void setServlet(BNHaystackServlet v) { set(servlet,v,null); }

////////////////////////////////////////////////////////////////
// Property "stats"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>stats</code> property.
   * @see nhaystack.server.BNHaystackService#getStats
   * @see nhaystack.server.BNHaystackService#setStats
   */
  public static final Property stats = newProperty(0, new BNHaystackStats(),null);
  
  /**
   * Get the <code>stats</code> property.
   * @see nhaystack.server.BNHaystackService#stats
   */
  public BNHaystackStats getStats() { return (BNHaystackStats)get(stats); }
  
  /**
   * Set the <code>stats</code> property.
   * @see nhaystack.server.BNHaystackService#stats
   */
  public void setStats(BNHaystackStats v) { set(stats,v,null); }

////////////////////////////////////////////////////////////////
// Property "timeZoneAliases"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>timeZoneAliases</code> property.
   * @see nhaystack.server.BNHaystackService#getTimeZoneAliases
   * @see nhaystack.server.BNHaystackService#setTimeZoneAliases
   */
  public static final Property timeZoneAliases = newProperty(0, new BTimeZoneAliasFolder(),null);
  
  /**
   * Get the <code>timeZoneAliases</code> property.
   * @see nhaystack.server.BNHaystackService#timeZoneAliases
   */
  public BTimeZoneAliasFolder getTimeZoneAliases() { return (BTimeZoneAliasFolder)get(timeZoneAliases); }
  
  /**
   * Set the <code>timeZoneAliases</code> property.
   * @see nhaystack.server.BNHaystackService#timeZoneAliases
   */
  public void setTimeZoneAliases(BTimeZoneAliasFolder v) { set(timeZoneAliases,v,null); }

////////////////////////////////////////////////////////////////
// Property "worker"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>worker</code> property.
   * @see nhaystack.server.BNHaystackService#getWorker
   * @see nhaystack.server.BNHaystackService#setWorker
   */
  public static final Property worker = newProperty(0, new BNHaystackWorker(),null);
  
  /**
   * Get the <code>worker</code> property.
   * @see nhaystack.server.BNHaystackService#worker
   */
  public BNHaystackWorker getWorker() { return (BNHaystackWorker)get(worker); }
  
  /**
   * Set the <code>worker</code> property.
   * @see nhaystack.server.BNHaystackService#worker
   */
  public void setWorker(BNHaystackWorker v) { set(worker,v,null); }

////////////////////////////////////////////////////////////////
// Property "watchCount"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>watchCount</code> property.
   * @see nhaystack.server.BNHaystackService#getWatchCount
   * @see nhaystack.server.BNHaystackService#setWatchCount
   */
  public static final Property watchCount = newProperty(Flags.TRANSIENT|Flags.READONLY, 0,null);
  
  /**
   * Get the <code>watchCount</code> property.
   * @see nhaystack.server.BNHaystackService#watchCount
   */
  public int getWatchCount() { return getInt(watchCount); }
  
  /**
   * Set the <code>watchCount</code> property.
   * @see nhaystack.server.BNHaystackService#watchCount
   */
  public void setWatchCount(int v) { setInt(watchCount,v,null); }

////////////////////////////////////////////////////////////////
// Property "initialized"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>initialized</code> property.
   * @see nhaystack.server.BNHaystackService#getInitialized
   * @see nhaystack.server.BNHaystackService#setInitialized
   */
  public static final Property initialized = newProperty(Flags.TRANSIENT|Flags.READONLY, false,null);
  
  /**
   * Get the <code>initialized</code> property.
   * @see nhaystack.server.BNHaystackService#initialized
   */
  public boolean getInitialized() { return getBoolean(initialized); }
  
  /**
   * Set the <code>initialized</code> property.
   * @see nhaystack.server.BNHaystackService#initialized
   */
  public void setInitialized(boolean v) { setBoolean(initialized,v,null); }

////////////////////////////////////////////////////////////////
// Property "initializationDelayTime"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>initializationDelayTime</code> property.
   * @see nhaystack.server.BNHaystackService#getInitializationDelayTime
   * @see nhaystack.server.BNHaystackService#setInitializationDelayTime
   */
  public static final Property initializationDelayTime = newProperty(0, BRelTime.DEFAULT,null);
  
  /**
   * Get the <code>initializationDelayTime</code> property.
   * @see nhaystack.server.BNHaystackService#initializationDelayTime
   */
  public BRelTime getInitializationDelayTime() { return (BRelTime)get(initializationDelayTime); }
  
  /**
   * Set the <code>initializationDelayTime</code> property.
   * @see nhaystack.server.BNHaystackService#initializationDelayTime
   */
  public void setInitializationDelayTime(BRelTime v) { set(initializationDelayTime,v,null); }

////////////////////////////////////////////////////////////////
// Property "foxLeaseInterval"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>foxLeaseInterval</code> property.
   * @see nhaystack.server.BNHaystackService#getFoxLeaseInterval
   * @see nhaystack.server.BNHaystackService#setFoxLeaseInterval
   */
  public static final Property foxLeaseInterval = newProperty(Flags.HIDDEN, BRelTime.makeMinutes(2),null);
  
  /**
   * Get the <code>foxLeaseInterval</code> property.
   * @see nhaystack.server.BNHaystackService#foxLeaseInterval
   */
  public BRelTime getFoxLeaseInterval() { return (BRelTime)get(foxLeaseInterval); }
  
  /**
   * Set the <code>foxLeaseInterval</code> property.
   * @see nhaystack.server.BNHaystackService#foxLeaseInterval
   */
  public void setFoxLeaseInterval(BRelTime v) { set(foxLeaseInterval,v,null); }

////////////////////////////////////////////////////////////////
// Property "translateEnums"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>translateEnums</code> property.
   * @see nhaystack.server.BNHaystackService#getTranslateEnums
   * @see nhaystack.server.BNHaystackService#setTranslateEnums
   */
  public static final Property translateEnums = newProperty(Flags.HIDDEN, false,null);
  
  /**
   * Get the <code>translateEnums</code> property.
   * @see nhaystack.server.BNHaystackService#translateEnums
   */
  public boolean getTranslateEnums() { return getBoolean(translateEnums); }
  
  /**
   * Set the <code>translateEnums</code> property.
   * @see nhaystack.server.BNHaystackService#translateEnums
   */
  public void setTranslateEnums(boolean v) { setBoolean(translateEnums,v,null); }

////////////////////////////////////////////////////////////////
// Action "readById"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>readById</code> action.
   * Lookup an entity record by it's unique identifier.
   * @see nhaystack.server.BNHaystackService#readById()
   */
  public static final Action readById = newAction(Flags.OPERATOR|Flags.HIDDEN,BHRef.DEFAULT,null);
  
  /**
   * Invoke the <code>readById</code> action.
   * Lookup an entity record by it's unique identifier.
   * @see nhaystack.server.BNHaystackService#readById
   */
  public BHDict readById(BHRef id) { return (BHDict)invoke(readById,id,null); }

////////////////////////////////////////////////////////////////
// Action "readAll"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>readAll</code> action.
   * Query every entity record that matches given filter.
   * @see nhaystack.server.BNHaystackService#readAll()
   */
  public static final Action readAll = newAction(Flags.OPERATOR|Flags.HIDDEN,BString.DEFAULT,null);
  
  /**
   * Invoke the <code>readAll</code> action.
   * Query every entity record that matches given filter.
   * @see nhaystack.server.BNHaystackService#readAll
   */
  public BHGrid readAll(BString filter) { return (BHGrid)invoke(readAll,filter,null); }

////////////////////////////////////////////////////////////////
// Action "fetchSites"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>fetchSites</code> action.
   * fetch all the records that are tagged as 'site'.
   * @see nhaystack.server.BNHaystackService#fetchSites()
   */
  public static final Action fetchSites = newAction(Flags.OPERATOR|Flags.HIDDEN,null);
  
  /**
   * Invoke the <code>fetchSites</code> action.
   * fetch all the records that are tagged as 'site'.
   * @see nhaystack.server.BNHaystackService#fetchSites
   */
  public BHGrid fetchSites() { return (BHGrid)invoke(fetchSites,null,null); }

////////////////////////////////////////////////////////////////
// Action "fetchEquips"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>fetchEquips</code> action.
   * fetch all the records that are tagged as 'equip'.
   * @see nhaystack.server.BNHaystackService#fetchEquips()
   */
  public static final Action fetchEquips = newAction(Flags.OPERATOR|Flags.HIDDEN,null);
  
  /**
   * Invoke the <code>fetchEquips</code> action.
   * fetch all the records that are tagged as 'equip'.
   * @see nhaystack.server.BNHaystackService#fetchEquips
   */
  public BHGrid fetchEquips() { return (BHGrid)invoke(fetchEquips,null,null); }

////////////////////////////////////////////////////////////////
// Action "fetchSepNav"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>fetchSepNav</code> action.
   * fetch the site-equip-point nav tree in xml format
   * @see nhaystack.server.BNHaystackService#fetchSepNav()
   */
  public static final Action fetchSepNav = newAction(Flags.OPERATOR|Flags.HIDDEN,null);
  
  /**
   * Invoke the <code>fetchSepNav</code> action.
   * fetch the site-equip-point nav tree in xml format
   * @see nhaystack.server.BNHaystackService#fetchSepNav
   */
  public BString fetchSepNav() { return (BString)invoke(fetchSepNav,null,null); }

////////////////////////////////////////////////////////////////
// Action "fetchAutoGenTags"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>fetchAutoGenTags</code> action.
   * fetch the tags that the server auto-generates.
   * @see nhaystack.server.BNHaystackService#fetchAutoGenTags()
   */
  public static final Action fetchAutoGenTags = newAction(Flags.OPERATOR|Flags.HIDDEN,null);
  
  /**
   * Invoke the <code>fetchAutoGenTags</code> action.
   * fetch the tags that the server auto-generates.
   * @see nhaystack.server.BNHaystackService#fetchAutoGenTags
   */
  public BString fetchAutoGenTags() { return (BString)invoke(fetchAutoGenTags,null,null); }

////////////////////////////////////////////////////////////////
// Action "findUniqueEquipTypes"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>findUniqueEquipTypes</code> action.
   * find all the unique equip types
   * @see nhaystack.server.BNHaystackService#findUniqueEquipTypes()
   */
  public static final Action findUniqueEquipTypes = newAction(Flags.OPERATOR|Flags.ASYNC|Flags.HIDDEN,new BUniqueEquipTypeArgs(),null);
  
  /**
   * Invoke the <code>findUniqueEquipTypes</code> action.
   * find all the unique equip types
   * @see nhaystack.server.BNHaystackService#findUniqueEquipTypes
   */
  public BOrd findUniqueEquipTypes(BUniqueEquipTypeArgs args) { return (BOrd)invoke(findUniqueEquipTypes,args,null); }

////////////////////////////////////////////////////////////////
// Action "applySchedule"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>applySchedule</code> action.
   * apply the schedule now that the ticket has expired
   * @see nhaystack.server.BNHaystackService#applySchedule()
   */
  public static final Action applySchedule = newAction(Flags.OPERATOR|Flags.ASYNC|Flags.HIDDEN,new BHScheduleEvent(),null);
  
  /**
   * Invoke the <code>applySchedule</code> action.
   * apply the schedule now that the ticket has expired
   * @see nhaystack.server.BNHaystackService#applySchedule
   */
  public void applySchedule(BHScheduleEvent event) { invoke(applySchedule,event,null); }

////////////////////////////////////////////////////////////////
// Action "initializeHaystack"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>initializeHaystack</code> action.
   * Initialize nhaystack
   * @see nhaystack.server.BNHaystackService#initializeHaystack()
   */
  public static final Action initializeHaystack = newAction(Flags.OPERATOR|Flags.ASYNC,null);
  
  /**
   * Invoke the <code>initializeHaystack</code> action.
   * Initialize nhaystack
   * @see nhaystack.server.BNHaystackService#initializeHaystack
   */
  public void initializeHaystack() { invoke(initializeHaystack,null,null); }

////////////////////////////////////////////////////////////////
// Action "rebuildCache"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>rebuildCache</code> action.
   * Rebuild the internal cache
   * @see nhaystack.server.BNHaystackService#rebuildCache()
   */
  public static final Action rebuildCache = newAction(Flags.OPERATOR|Flags.ASYNC,null);
  
  /**
   * Invoke the <code>rebuildCache</code> action.
   * Rebuild the internal cache
   * @see nhaystack.server.BNHaystackService#rebuildCache
   */
  public BOrd rebuildCache() { return (BOrd)invoke(rebuildCache,null,null); }

////////////////////////////////////////////////////////////////
// Action "removeBrokenRefs"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>removeBrokenRefs</code> action.
   * Remove all the invalid refs
   * @see nhaystack.server.BNHaystackService#removeBrokenRefs()
   */
  public static final Action removeBrokenRefs = newAction(Flags.OPERATOR|Flags.ASYNC,null);
  
  /**
   * Invoke the <code>removeBrokenRefs</code> action.
   * Remove all the invalid refs
   * @see nhaystack.server.BNHaystackService#removeBrokenRefs
   */
  public BOrd removeBrokenRefs() { return (BOrd)invoke(removeBrokenRefs,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackService.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public boolean isParentLegal(BComponent parent)
    {
        return parent instanceof BServiceContainer;
    }

////////////////////////////////////////////////////////////////
// BIService
////////////////////////////////////////////////////////////////

    public Type[] getServiceTypes() { return SERVICE_TYPES; }

    public void serviceStarted() throws Exception 
    { 
        LOG.info("NHaystack Service started");
        this.server = createServer();
    }

    public void serviceStopped()
    { 
        LOG.info("NHaystack Service stopped");
    }

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

    public BIcon getIcon() { return ICON; }
    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/tag.png");
    private static BOrd NIAGARA_NETWORK = BOrd.make("station:|slot:/Drivers/NiagaraNetwork");

    private static final Type[] SERVICE_TYPES = new Type[] { TYPE };

    private NHServer server;

    private BHistoryDatabase historyDb;    
    private BDeviceNetwork niagaraNetwork;
}
