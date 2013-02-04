//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   07 Nov 2011  Richard McElhinney  Creation
//   28 Sep 2012  Mike Jarmy          Ported from axhaystack
//
package nhaystack.server;

import javax.baja.driver.*;
import javax.baja.history.db.*;
import javax.baja.naming.*;
import javax.baja.sys.*;

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
                default{[ true ]} 
            servlet: BNHaystackServlet
                default{[ new BNHaystackServlet() ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.BNHaystackService(3918282007)1.0$ @*/
/* Generated Mon Feb 04 14:03:54 EST 2013 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
  public static final Property showLinkedHistories = newProperty(0, true,null);
  
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
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackService.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

////////////////////////////////////////////////////////////////
// BIService
////////////////////////////////////////////////////////////////

    public Type[] getServiceTypes() { return SERVICE_TYPES; }

    public void serviceStarted() throws Exception { }

    public void serviceStopped() throws Exception { }

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
