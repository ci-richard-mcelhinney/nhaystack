//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//
package nhaystack.server;

import java.util.*;

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
import nhaystack.collection.*;
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
                default{[ BRelTime.make(BRelTime.MINUTE.getMillis()) ]}
            showLinkedHistories: boolean
                 -- Whether to show BHistoryConfigs that are linked to a BControlPoint 
                default{[ false ]} 
            servlet: BNHaystackServlet
                default{[ new BNHaystackServlet() ]}
        }
        actions
        {
            readById(id: BHRef): BHDict 
                -- Lookup an entity record by it's unique identifier.
                flags { hidden }
                default {[ BHRef.DEFAULT ]}
            fetchSites(): BHGrid
                -- fetch all the records that are tagged as 'site'.
                flags { hidden }
            fetchEquips(): BHGrid
                -- fetch all the records that are tagged as 'equip'.
                flags { hidden }
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackService(2747625900)1.0$ @*/
/* Generated Wed Feb 13 11:03:52 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "leaseInterval"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>leaseInterval</code> property.
   * The amount of time that objects in watches are leased.
   * @see nhaystack.server.BNHaystackService#getLeaseInterval
   * @see nhaystack.server.BNHaystackService#setLeaseInterval
   */
  public static final Property leaseInterval = newProperty(0, BRelTime.make(BRelTime.MINUTE.getMillis()),null);
  
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
// Action "readById"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>readById</code> action.
   * Lookup an entity record by it's unique identifier.
   * @see nhaystack.server.BNHaystackService#readById()
   */
  public static final Action readById = newAction(Flags.HIDDEN,BHRef.DEFAULT,null);
  
  /**
   * Invoke the <code>readById</code> action.
   * @see nhaystack.server.BNHaystackService#readById
   */
  public BHDict readById(BHRef id) { return (BHDict)invoke(readById,id,null); }

////////////////////////////////////////////////////////////////
// Action "fetchSites"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>fetchSites</code> action.
   * fetch all the records that are tagged as 'site'.
   * @see nhaystack.server.BNHaystackService#fetchSites()
   */
  public static final Action fetchSites = newAction(Flags.HIDDEN,null);
  
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
  public static final Action fetchEquips = newAction(Flags.HIDDEN,null);
  
  /**
   * Invoke the <code>fetchEquips</code> action.
   * @see nhaystack.server.BNHaystackService#fetchEquips
   */
  public BHGrid fetchEquips() { return (BHGrid)invoke(fetchEquips,null,null); }

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

    public void serviceStarted() throws Exception { }

    public void serviceStopped() throws Exception { }

////////////////////////////////////////////////////////////////
// Actions
////////////////////////////////////////////////////////////////

    public BHDict doReadById(BHRef id) throws Exception
    {
        return BHDict.make(server.readById(id.getRef()));
    }

    public BHGrid doFetchSites() throws Exception
    {
        BHSite[] sites = server.getSiteStorehouse().fetchSites();

        Array arr = new Array(HDict.class);
        for (int i = 0; i < sites.length; i++)
            arr.add(server.getConfigStorehouse().createComponentTags(sites[i]));
            
        return BHGrid.make(HGridBuilder.dictsToGrid((HDict[]) arr.trim()));
    }

    public BHGrid doFetchEquips() throws Exception
    {
        BHEquip[] equips = server.getSiteStorehouse().fetchEquips();

        Array arr = new Array(HDict.class);
        for (int i = 0; i < equips.length; i++)
            arr.add(server.getConfigStorehouse().createComponentTags(equips[i]));
            
        return BHGrid.make(HGridBuilder.dictsToGrid((HDict[]) arr.trim()));
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
