//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//
package nhaystack.server;

import javax.baja.control.*;
import javax.baja.driver.*;
import javax.baja.history.*;
import javax.baja.history.db.*;
import javax.baja.history.ext.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import haystack.*;
import nhaystack.*;
import nhaystack.site.*;

/**
  * BNHaystackService makes an NHServer available.  
  */
public class BNHaystackService extends BAbstractService
{
    /*-
    class BNHaystackService
    {
        properties
        {
            leaseInterval: BRelTime
                 -- The amount of time that objects in watches are leased.
                default{[ BRelTime.make(2 * BRelTime.MINUTE.getMillis()) ]}
            showLinkedHistories: boolean
                 -- Whether to show BHistoryConfigs that are linked to a BControlPoint 
                default{[ false ]} 
            servlet: BNHaystackServlet
                default{[ new BNHaystackServlet() ]}
            stats: BNHaystackStats
                default{[ new BNHaystackStats() ]}
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
            rebuildCache()
                -- Rebuild the internal cache
                flags { operator }
            removeBrokenRefs()
                -- Remove all the invalid refs
                flags { operator }
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackService(1207068001)1.0$ @*/
/* Generated Sun Apr 28 10:34:33 EDT 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "leaseInterval"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>leaseInterval</code> property.
   * The amount of time that objects in watches are leased.
   * @see nhaystack.server.BNHaystackService#getLeaseInterval
   * @see nhaystack.server.BNHaystackService#setLeaseInterval
   */
  public static final Property leaseInterval = newProperty(0, BRelTime.make(2 * BRelTime.MINUTE.getMillis()),null);
  
  /**
   * Get the <code>leaseInterval</code> property.
   * @see nhaystack.server.BNHaystackService#leaseInterval
   */
  public BRelTime getLeaseInterval() { return (BRelTime)get(leaseInterval); }
  
  /**
   * Set the <code>leaseInterval</code> property.
   * @see nhaystack.server.BNHaystackService#leaseInterval
   */
  public void setLeaseInterval(BRelTime v) { set(leaseInterval,v,null); }

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
   * @see nhaystack.server.BNHaystackService#showLinkedHistories
   */
  public boolean getShowLinkedHistories() { return getBoolean(showLinkedHistories); }
  
  /**
   * Set the <code>showLinkedHistories</code> property.
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
   * @see nhaystack.server.BNHaystackService#fetchSepNav
   */
  public BString fetchSepNav() { return (BString)invoke(fetchSepNav,null,null); }

////////////////////////////////////////////////////////////////
// Action "rebuildCache"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>rebuildCache</code> action.
   * Rebuild the internal cache
   * @see nhaystack.server.BNHaystackService#rebuildCache()
   */
  public static final Action rebuildCache = newAction(Flags.OPERATOR,null);
  
  /**
   * Invoke the <code>rebuildCache</code> action.
   * @see nhaystack.server.BNHaystackService#rebuildCache
   */
  public void rebuildCache() { invoke(rebuildCache,null,null); }

////////////////////////////////////////////////////////////////
// Action "removeBrokenRefs"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>removeBrokenRefs</code> action.
   * Remove all the invalid refs
   * @see nhaystack.server.BNHaystackService#removeBrokenRefs()
   */
  public static final Action removeBrokenRefs = newAction(Flags.OPERATOR,null);
  
  /**
   * Invoke the <code>removeBrokenRefs</code> action.
   * @see nhaystack.server.BNHaystackService#removeBrokenRefs
   */
  public void removeBrokenRefs() { invoke(removeBrokenRefs,null,null); }

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
        // We need this here for when this service gets dropped into
        // a running station.  However, this will fail at station start time.
        // When that happens,  we'll ignore the error that it generates, and 
        // rely on atSteadyState() to get us set up.
        try
        {
            doRebuildCache();
        }
        catch (Exception e)
        {
        }
    }

    public void serviceStopped() throws Exception { }

    public void atSteadyState() throws Exception
    {
        doRebuildCache();
    }

////////////////////////////////////////////////////////////////
// Actions
////////////////////////////////////////////////////////////////

    public BHDict doReadById(BHRef id) throws Exception
    {
        return BHDict.make(server.readById(id.getRef()));
    }

    public BHGrid doReadAll(BString filter) throws Exception
    {
        return BHGrid.make(server.readAll(filter.getString()));
    }

    public BHGrid doFetchSites() throws Exception
    {
        BHSite[] sites = server.getCache().getAllSites();

        HDict[] dicts = new HDict[sites.length];
        for (int i = 0; i < sites.length; i++)
            dicts[i] = server.getComponentStorehouse().createComponentTags(sites[i]);

        return BHGrid.make(HGridBuilder.dictsToGrid(dicts));
    }

    public BHGrid doFetchEquips() throws Exception
    {
        BHEquip[] equips = server.getCache().getAllEquips();

        HDict[] dicts = new HDict[equips.length];
        for (int i = 0; i < equips.length; i++)
            dicts[i] = server.getComponentStorehouse().createComponentTags(equips[i]);

        return BHGrid.make(HGridBuilder.dictsToGrid(dicts));
    }

    public BString doFetchSepNav() throws Exception
    {
        return BString.make(server.getNav().fetchSepNav());
    }

    public void doRebuildCache() throws Exception
    {
        Cache cache = server.getCache();
        synchronized(cache)
        {
            cache.rebuild();

            BNHaystackStats stats = getStats();
            stats.setNumPoints(cache.numPoints);
            stats.setNumEquips(cache.numEquips);
            stats.setNumSites(cache.numSites);
            stats.setLastCacheRebuildDuration(cache.lastRebuildDuration);
            stats.setLastCacheRebuildTime(cache.lastRebuildTime);
        }
    }

    public void doRemoveBrokenRefs() throws Exception
    {
        Cache cache = server.getCache();
        synchronized(cache)
        {
            server.removeBrokenRefs();
        }
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

    /**
      * Return the BHistoryExt for the point, if there is one.
      * Returns null if the BHistoryExt has never been enabled.
      */
    public BHistoryExt lookupHistoryExt(BControlPoint point)
    {
        Cursor cursor = point.getProperties();
        if (cursor.next(BHistoryExt.class))
        {
            BHistoryExt ext = (BHistoryExt) cursor.get();

            // Return null if the extension has never been enabled.
            BHistoryConfig config = ext.getHistoryConfig();
            if (getHistoryDb().getHistory(config.getId()) == null)
                return null;

            return ext;
        }

        return null;
    }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    public BIcon getIcon() { return ICON; }
    private static final BIcon ICON = BIcon.make("module://nhaystack/nhaystack/icons/tag.png");
    private static BOrd NIAGARA_NETWORK = BOrd.make("station:|slot:/Drivers/NiagaraNetwork");

    private static final Type[] SERVICE_TYPES = new Type[] { TYPE };

    private final NHServer server = new NHServer(this);

    private BHistoryDatabase historyDb;    
    private BDeviceNetwork niagaraNetwork;
}
