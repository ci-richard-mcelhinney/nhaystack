//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver;

import javax.baja.driver.*;
import javax.baja.driver.point.*;
import javax.baja.driver.util.*;
import javax.baja.sys.*;

public class BNHaystackNetwork extends BDeviceNetwork
{
    /*-
    class BNHaystackNetwork
    {
        properties
        {
            tuningPolicies: BTuningPolicyMap default{[ new BTuningPolicyMap()        ]}
            pollScheduler:  BPollScheduler   default{[ new BNHaystackPollScheduler() ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BNHaystackNetwork(4149131371)1.0$ @*/
/* Generated Mon Apr 07 12:12:20 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "tuningPolicies"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>tuningPolicies</code> property.
   * @see nhaystack.driver.BNHaystackNetwork#getTuningPolicies
   * @see nhaystack.driver.BNHaystackNetwork#setTuningPolicies
   */
  public static final Property tuningPolicies = newProperty(0, new BTuningPolicyMap(),null);
  
  /**
   * Get the <code>tuningPolicies</code> property.
   * @see nhaystack.driver.BNHaystackNetwork#tuningPolicies
   */
  public BTuningPolicyMap getTuningPolicies() { return (BTuningPolicyMap)get(tuningPolicies); }
  
  /**
   * Set the <code>tuningPolicies</code> property.
   * @see nhaystack.driver.BNHaystackNetwork#tuningPolicies
   */
  public void setTuningPolicies(BTuningPolicyMap v) { set(tuningPolicies,v,null); }

////////////////////////////////////////////////////////////////
// Property "pollScheduler"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>pollScheduler</code> property.
   * @see nhaystack.driver.BNHaystackNetwork#getPollScheduler
   * @see nhaystack.driver.BNHaystackNetwork#setPollScheduler
   */
  public static final Property pollScheduler = newProperty(0, new BNHaystackPollScheduler(),null);
  
  /**
   * Get the <code>pollScheduler</code> property.
   * @see nhaystack.driver.BNHaystackNetwork#pollScheduler
   */
  public BPollScheduler getPollScheduler() { return (BPollScheduler)get(pollScheduler); }
  
  /**
   * Set the <code>pollScheduler</code> property.
   * @see nhaystack.driver.BNHaystackNetwork#pollScheduler
   */
  public void setPollScheduler(BPollScheduler v) { set(pollScheduler,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackNetwork.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public Type getDeviceFolderType()
    {
        return BNHaystackServerFolder.TYPE;
    }

    public Type getDeviceType()
    {
        return BNHaystackServer.TYPE;
    }

    public boolean isParentLegal(BComponent comp)
    {
        return (comp instanceof BDriverContainer);
    }
}
