//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.driver;

import javax.baja.driver.BDeviceNetwork;
import javax.baja.driver.BDriverContainer;
import javax.baja.driver.point.BTuningPolicyMap;
import javax.baja.driver.util.BPollScheduler;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.worker.BINHaystackWorkerParent;
import nhaystack.worker.BNHaystackThreadPoolWorker;
import nhaystack.worker.WorkerChore;
import org.projecthaystack.client.CallNetworkException;

@NiagaraType
@NiagaraProperty(
  name = "tuningPolicies",
  type = "BTuningPolicyMap",
  defaultValue = "new BTuningPolicyMap()"
)
@NiagaraProperty(
  name = "pollScheduler",
  type = "BPollScheduler",
  defaultValue = "new BNHaystackPollScheduler()"
)
@NiagaraProperty(
  name = "threadPoolWorker",
  type = "BNHaystackThreadPoolWorker",
  defaultValue = "new BNHaystackThreadPoolWorker()"
)
public class BNHaystackNetwork 
    extends BDeviceNetwork
    implements BINHaystackWorkerParent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BNHaystackNetwork(1022407080)1.0$ @*/
/* Generated Sat Nov 18 17:54:32 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "tuningPolicies"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code tuningPolicies} property.
   * @see #getTuningPolicies
   * @see #setTuningPolicies
   */
  public static final Property tuningPolicies = newProperty(0, new BTuningPolicyMap(), null);
  
  /**
   * Get the {@code tuningPolicies} property.
   * @see #tuningPolicies
   */
  public BTuningPolicyMap getTuningPolicies() { return (BTuningPolicyMap)get(tuningPolicies); }
  
  /**
   * Set the {@code tuningPolicies} property.
   * @see #tuningPolicies
   */
  public void setTuningPolicies(BTuningPolicyMap v) { set(tuningPolicies, v, null); }

////////////////////////////////////////////////////////////////
// Property "pollScheduler"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code pollScheduler} property.
   * @see #getPollScheduler
   * @see #setPollScheduler
   */
  public static final Property pollScheduler = newProperty(0, new BNHaystackPollScheduler(), null);
  
  /**
   * Get the {@code pollScheduler} property.
   * @see #pollScheduler
   */
  public BPollScheduler getPollScheduler() { return (BPollScheduler)get(pollScheduler); }
  
  /**
   * Set the {@code pollScheduler} property.
   * @see #pollScheduler
   */
  public void setPollScheduler(BPollScheduler v) { set(pollScheduler, v, null); }

////////////////////////////////////////////////////////////////
// Property "threadPoolWorker"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code threadPoolWorker} property.
   * @see #getThreadPoolWorker
   * @see #setThreadPoolWorker
   */
  public static final Property threadPoolWorker = newProperty(0, new BNHaystackThreadPoolWorker(), null);
  
  /**
   * Get the {@code threadPoolWorker} property.
   * @see #threadPoolWorker
   */
  public BNHaystackThreadPoolWorker getThreadPoolWorker() { return (BNHaystackThreadPoolWorker)get(threadPoolWorker); }
  
  /**
   * Set the {@code threadPoolWorker} property.
   * @see #threadPoolWorker
   */
  public void setThreadPoolWorker(BNHaystackThreadPoolWorker v) { set(threadPoolWorker, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackNetwork.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    @Override
    public Type getDeviceFolderType()
    {
        return BNHaystackServerFolder.TYPE;
    }

    @Override
    public Type getDeviceType()
    {
        return BNHaystackServer.TYPE;
    }

    @Override
    public boolean isParentLegal(BComponent comp)
    {
        return comp instanceof BDriverContainer;
    }

    @Override
    public void handleNetworkException(WorkerChore chore, CallNetworkException e)
    {
    }
}
