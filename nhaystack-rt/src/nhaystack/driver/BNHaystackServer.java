//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations,
//                               added use of generics

package nhaystack.driver;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.alarm.BAlarmSourceInfo;
import javax.baja.driver.BDevice;
import javax.baja.driver.util.BIPollable;
import javax.baja.driver.util.BPollFrequency;
import javax.baja.naming.BOrd;
import javax.baja.net.BInternetAddress;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.security.BPassword;
import javax.baja.security.BUsernameAndPassword;
import javax.baja.sys.Action;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BComponent;
import javax.baja.sys.BRelTime;
import javax.baja.sys.BajaRuntimeException;
import javax.baja.sys.Clock;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.IFuture;
import javax.baja.util.Invocation;
import javax.baja.util.QueueFullException;
import nhaystack.driver.history.BNHaystackHistoryDeviceExt;
import nhaystack.driver.history.learn.BNHaystackLearnExpHistoriesJob;
import nhaystack.driver.history.learn.BNHaystackLearnHistoriesJob;
import nhaystack.driver.point.*;
import nhaystack.driver.point.learn.*;
import nhaystack.driver.worker.PingInvocation;
import nhaystack.worker.BINHaystackWorker;
import nhaystack.worker.BINHaystackWorkerParent;
import nhaystack.worker.BNHaystackWorker;
import nhaystack.worker.WorkerChore;
import org.projecthaystack.HGrid;
import org.projecthaystack.HNum;
import org.projecthaystack.HRef;
import org.projecthaystack.HRow;
import org.projecthaystack.HWatch;
import org.projecthaystack.client.CallNetworkException;
import org.projecthaystack.client.HClient;

/**
 * BNHaystackServer models a device which is serving up haystack data
 */
@NiagaraType
@NiagaraProperty(name = "tls", type = "boolean", defaultValue = "false")
@NiagaraProperty(name = "internetAddress", type = "BInternetAddress", defaultValue = "BInternetAddress.NULL")
@NiagaraProperty(name = "uriPath", type = "String", defaultValue = "")
@NiagaraProperty(name = "credentials", type = "BUsernameAndPassword", defaultValue = "new BUsernameAndPassword()")
@NiagaraProperty(name = "histories", type = "BNHaystackHistoryDeviceExt", defaultValue = "new BNHaystackHistoryDeviceExt()")
@NiagaraProperty(name = "points", type = "BNHaystackPointDeviceExt", defaultValue = "new BNHaystackPointDeviceExt()")
@NiagaraProperty(name = "pollFrequency", type = "BPollFrequency", defaultValue = "BPollFrequency.normal")
@NiagaraProperty(name = "worker", type = "BNHaystackWorker", defaultValue = "new BNHaystackWorker()")
@NiagaraProperty(name = "alarmSourceInfo", type = "BAlarmSourceInfo", defaultValue = "new BAlarmSourceInfo()", flags = Flags.HIDDEN, override = true)
/**
 * The amount of time that objects in watches are leased.
 */
@NiagaraProperty(name = "leaseInterval", type = "BRelTime", defaultValue = "BRelTime.make(2 * BRelTime.MINUTE.getMillis())")
@NiagaraProperty(name = "structureSettings", type = "BStructureSettings", defaultValue = "new BStructureSettings()")
@NiagaraAction(name = "submitLearnHistoriesJob", returnType = "BOrd", flags = Flags.HIDDEN)
@NiagaraAction(name = "submitLearnExpHistoriesJob", returnType = "BOrd", flags = Flags.HIDDEN)
@NiagaraAction(name = "submitLearnPointsJob", returnType = "BOrd", flags = Flags.HIDDEN)
@NiagaraAction(name = "learnStructure", returnType = "BOrd")
public class BNHaystackServer extends BDevice implements BINHaystackWorkerParent, BIPollable
{
  /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
  /*@ $nhaystack.driver.BNHaystackServer(3188354716)1.0$ @*/
  /* Generated Mon May 14 23:00:03 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "tls"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the <code>tls</code> property.
   *
   * @see nhaystack.driver.BNHaystackServer#getTls
   * @see nhaystack.driver.BNHaystackServer#setTls
   */
  public static final Property tls = newProperty(0, false, null);

  /**
   * Get the <code>tls</code> property.
   *
   * @see nhaystack.driver.BNHaystackServer#tls
   */
  public boolean getTls()
  {
    return getBoolean(tls);
  }

  /**
   * Set the <code>tls</code> property.
   *
   * @see nhaystack.driver.BNHaystackServer#tls
   */
  public void setTls(boolean v)
  {
    setBoolean(tls, v, null);
  }

////////////////////////////////////////////////////////////////
// Property "internetAddress"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code internetAddress} property.
   *
   * @see #getInternetAddress
   * @see #setInternetAddress
   */
  public static final Property internetAddress = newProperty(0, BInternetAddress.NULL, null);

  /**
   * Get the {@code internetAddress} property.
   *
   * @see #internetAddress
   */
  public BInternetAddress getInternetAddress()
  {
    return (BInternetAddress) get(internetAddress);
  }

  /**
   * Set the {@code internetAddress} property.
   *
   * @see #internetAddress
   */
  public void setInternetAddress(BInternetAddress v)
  {
    set(internetAddress, v, null);
  }

////////////////////////////////////////////////////////////////
// Property "uriPath"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code uriPath} property.
   *
   * @see #getUriPath
   * @see #setUriPath
   */
  public static final Property uriPath = newProperty(0, "", null);

  /**
   * Get the {@code uriPath} property.
   *
   * @see #uriPath
   */
  public String getUriPath()
  {
    return getString(uriPath);
  }

  /**
   * Set the {@code uriPath} property.
   *
   * @see #uriPath
   */
  public void setUriPath(String v)
  {
    setString(uriPath, v, null);
  }

////////////////////////////////////////////////////////////////
// Property "credentials"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code credentials} property.
   *
   * @see #getCredentials
   * @see #setCredentials
   */
  public static final Property credentials = newProperty(0, new BUsernameAndPassword(), null);

  /**
   * Get the {@code credentials} property.
   *
   * @see #credentials
   */
  public BUsernameAndPassword getCredentials()
  {
    return (BUsernameAndPassword) get(credentials);
  }

  /**
   * Set the {@code credentials} property.
   *
   * @see #credentials
   */
  public void setCredentials(BUsernameAndPassword v)
  {
    set(credentials, v, null);
  }

////////////////////////////////////////////////////////////////
// Property "histories"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code histories} property.
   *
   * @see #getHistories
   * @see #setHistories
   */
  public static final Property histories = newProperty(0, new BNHaystackHistoryDeviceExt(), null);

  /**
   * Get the {@code histories} property.
   *
   * @see #histories
   */
  public BNHaystackHistoryDeviceExt getHistories()
  {
    return (BNHaystackHistoryDeviceExt) get(histories);
  }

  /**
   * Set the {@code histories} property.
   *
   * @see #histories
   */
  public void setHistories(BNHaystackHistoryDeviceExt v)
  {
    set(histories, v, null);
  }

////////////////////////////////////////////////////////////////
// Property "points"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code points} property.
   *
   * @see #getPoints
   * @see #setPoints
   */
  public static final Property points = newProperty(0, new BNHaystackPointDeviceExt(), null);

  /**
   * Get the {@code points} property.
   *
   * @see #points
   */
  public BNHaystackPointDeviceExt getPoints()
  {
    return (BNHaystackPointDeviceExt) get(points);
  }

  /**
   * Set the {@code points} property.
   *
   * @see #points
   */
  public void setPoints(BNHaystackPointDeviceExt v)
  {
    set(points, v, null);
  }

////////////////////////////////////////////////////////////////
// Property "pollFrequency"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code pollFrequency} property.
   *
   * @see #getPollFrequency
   * @see #setPollFrequency
   */
  public static final Property pollFrequency = newProperty(0, BPollFrequency.normal, null);

  /**
   * Get the {@code pollFrequency} property.
   *
   * @see #pollFrequency
   */
  @Override
  public BPollFrequency getPollFrequency()
  {
    return (BPollFrequency) get(pollFrequency);
  }

  /**
   * Set the {@code pollFrequency} property.
   *
   * @see #pollFrequency
   */
  public void setPollFrequency(BPollFrequency v)
  {
    set(pollFrequency, v, null);
  }

////////////////////////////////////////////////////////////////
// Property "worker"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code worker} property.
   *
   * @see #getWorker
   * @see #setWorker
   */
  public static final Property worker = newProperty(0, new BNHaystackWorker(), null);

  /**
   * Get the {@code worker} property.
   *
   * @see #worker
   */
  public BNHaystackWorker getWorker()
  {
    return (BNHaystackWorker) get(worker);
  }

  /**
   * Set the {@code worker} property.
   *
   * @see #worker
   */
  public void setWorker(BNHaystackWorker v)
  {
    set(worker, v, null);
  }

////////////////////////////////////////////////////////////////
// Property "alarmSourceInfo"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code alarmSourceInfo} property.
   *
   * @see #getAlarmSourceInfo
   * @see #setAlarmSourceInfo
   */
  public static final Property alarmSourceInfo = newProperty(Flags.HIDDEN, new BAlarmSourceInfo(), null);

  /**
   * Get the {@code alarmSourceInfo} property.
   *
   * @see #alarmSourceInfo
   */
  @Override
  public BAlarmSourceInfo getAlarmSourceInfo()
  {
    return (BAlarmSourceInfo) get(alarmSourceInfo);
  }

  /**
   * Set the {@code alarmSourceInfo} property.
   *
   * @see #alarmSourceInfo
   */
  @Override
  public void setAlarmSourceInfo(BAlarmSourceInfo v)
  {
    set(alarmSourceInfo, v, null);
  }

////////////////////////////////////////////////////////////////
// Property "leaseInterval"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code leaseInterval} property.
   * The amount of time that objects in watches are leased.
   *
   * @see #getLeaseInterval
   * @see #setLeaseInterval
   */
  public static final Property leaseInterval = newProperty(0, BRelTime.make(2 * BRelTime.MINUTE.getMillis()), null);

  /**
   * Get the {@code leaseInterval} property.
   * The amount of time that objects in watches are leased.
   *
   * @see #leaseInterval
   */
  public BRelTime getLeaseInterval()
  {
    return (BRelTime) get(leaseInterval);
  }

  /**
   * Set the {@code leaseInterval} property.
   * The amount of time that objects in watches are leased.
   *
   * @see #leaseInterval
   */
  public void setLeaseInterval(BRelTime v)
  {
    set(leaseInterval, v, null);
  }

////////////////////////////////////////////////////////////////
// Property "structureSettings"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code structureSettings} property.
   *
   * @see #getStructureSettings
   * @see #setStructureSettings
   */
  public static final Property structureSettings = newProperty(0, new BStructureSettings(), null);

  /**
   * Get the {@code structureSettings} property.
   *
   * @see #structureSettings
   */
  public BStructureSettings getStructureSettings()
  {
    return (BStructureSettings) get(structureSettings);
  }

  /**
   * Set the {@code structureSettings} property.
   *
   * @see #structureSettings
   */
  public void setStructureSettings(BStructureSettings v)
  {
    set(structureSettings, v, null);
  }

////////////////////////////////////////////////////////////////
// Action "submitLearnHistoriesJob"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code submitLearnHistoriesJob} action.
   *
   * @see #submitLearnHistoriesJob()
   */
  public static final Action submitLearnHistoriesJob = newAction(Flags.HIDDEN, null);

  /**
   * Invoke the {@code submitLearnHistoriesJob} action.
   *
   * @see #submitLearnHistoriesJob
   */
  public BOrd submitLearnHistoriesJob()
  {
    return (BOrd) invoke(submitLearnHistoriesJob, null, null);
  }

////////////////////////////////////////////////////////////////
// Action "submitLearnExpHistoriesJob"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code submitLearnExpHistoriesJob} action.
   * @see #submitLearnExpHistoriesJob()
   */
  public static final Action submitLearnExpHistoriesJob = newAction(Flags.HIDDEN, null);

  /**
   * Invoke the {@code submitLearnExpHistoriesJob} action.
   * @see #submitLearnExpHistoriesJob
   */
  public BOrd submitLearnExpHistoriesJob() { return (BOrd)invoke(submitLearnExpHistoriesJob, null, null); }

////////////////////////////////////////////////////////////////
// Action "submitLearnPointsJob"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code submitLearnPointsJob} action.
   *
   * @see #submitLearnPointsJob()
   */
  public static final Action submitLearnPointsJob = newAction(Flags.HIDDEN, null);

  /**
   * Invoke the {@code submitLearnPointsJob} action.
   *
   * @see #submitLearnPointsJob
   */
  public BOrd submitLearnPointsJob()
  {
    return (BOrd) invoke(submitLearnPointsJob, null, null);
  }

////////////////////////////////////////////////////////////////
// Action "learnStructure"
////////////////////////////////////////////////////////////////

  /**
   * Slot for the {@code learnStructure} action.
   *
   * @see #learnStructure()
   */
  public static final Action learnStructure = newAction(0, null);

  /**
   * Invoke the {@code learnStructure} action.
   *
   * @see #learnStructure
   */
  public BOrd learnStructure()
  {
    return (BOrd) invoke(learnStructure, null, null);
  }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

  @Override
  public Type getType()
  {
    return TYPE;
  }

  public static final Type TYPE = Sys.loadType(BNHaystackServer.class);

  /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  public BNHaystackServer() {
    super();
    featureDetectExpiry = null;
    supportsCrud = false;
  }

  @Override
  public void changed(Property property, Context context)
  {
    if (property == internetAddress || property == uriPath || property == credentials)
    {
      resetClient();
    }
  }

  @Override
  public boolean isParentLegal(BComponent comp)
  {
    return comp instanceof BNHaystackNetwork || comp instanceof BNHaystackServerFolder;
  }

  @Override
  public void started()
  {
    getNHaystackNetwork().getPollScheduler().subscribe(this);
  }

  @Override
  public void stopped()
  {
    getNHaystackNetwork().getPollScheduler().unsubscribe(this);

    synchronized (this)
    {
      if (hwatch != null)
      {
        hwatch.close();
      }
    }
  }

////////////////////////////////////////////////////////////////
// BDevice
////////////////////////////////////////////////////////////////

  @Override
  public Type getNetworkType()
  {
    return BNHaystackNetwork.TYPE;
  }

  @Override
  protected IFuture postPing()
  {
    BNHaystackNetwork network = getNHaystackNetwork();

    return onPostAsyncChore(network.getThreadPoolWorker(), new PingInvocation(network.getThreadPoolWorker(), "Ping:" + getHaystackUrl(), new Invocation(this, ping, null, null)));
  }

  @Override
  public void doPing() throws Exception
  {
    long begin = Clock.ticks();
    if (LOG.isLoggable(Level.FINE))
    {
      LOG.fine("Server Ping BEGIN " + getHaystackUrl() + ", " + Thread.currentThread().getName());
    }

    if (isDisabled() || isFault())
    {
      return;
    }

    try
    {
      HClient client = getHaystackClient();
      client.about();
      pingOk();
    }
    catch (Exception e)
    {
      resetClient();
      pingFail(e.getMessage());
      throw new BajaRuntimeException(e);
    }
    finally
    {
      if (LOG.isLoggable(Level.FINE))
      {
        long end = Clock.ticks();
        LOG.fine("Server Ping END " + getHaystackUrl() + " (" + (end - begin) + "ms)");
      }
    }
  }

////////////////////////////////////////////////////////////////
// actions
////////////////////////////////////////////////////////////////

  public BOrd doSubmitLearnHistoriesJob()
  {
    BNHaystackLearnHistoriesJob job = new BNHaystackLearnHistoriesJob(this);
    return job.submit(null);
  }

  public BOrd doSubmitLearnExpHistoriesJob()
  {
    BNHaystackLearnExpHistoriesJob job = new BNHaystackLearnExpHistoriesJob(this);
    return job.submit(null);
  }

  public BOrd doSubmitLearnPointsJob()
  {
    BNHaystackLearnPointsJob job = new BNHaystackLearnPointsJob(this);
    return job.submit(null);
  }

  public BOrd doLearnStructure()
  {
    BNHaystackLearnStructureJob job = new BNHaystackLearnStructureJob(this);
    return job.submit(null);
  }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

  public final IFuture postAsyncChore(WorkerChore chore)
  {
    return onPostAsyncChore(getWorker(), chore);
  }

  /**
   * Post a chore asynchronously to the worker queue
   */
  private IFuture onPostAsyncChore(BINHaystackWorker worker, WorkerChore chore)
  {
    if (!isRunning())
    {
      return null;
    }

    if (!getEnabled())
    {
      if (LOG.isLoggable(Level.FINE))
      {
        LOG.fine(getHaystackUrl() + " server disabled: " + chore);
      }
      return null;
    }

    if (getNetwork().isDisabled())
    {
      if (LOG.isLoggable(Level.FINE))
      {
        LOG.fine(getHaystackUrl() + " network disabled: " + chore);
      }
      return null;
    }

    try
    {
      worker.enqueueChore(chore);
    }
    catch (QueueFullException e)
    {
      e.printStackTrace();
      LOG.severe(getHaystackUrl() + " Cannot enqueue chore " + chore + ": QueueFullException");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      LOG.severe(getHaystackUrl() + " Cannot enqueue chore " + chore + ": " + e.getMessage());
    }
    return null;
  }

  /**
   * Obtain an HClient instance that can be used to communicate with
   * the remote server.
   */
  public synchronized HClient getHaystackClient()
  {
    //Security requirement for 4.6
    BPassword parameter = BPassword.make(AccessController.doPrivileged((PrivilegedAction<String>) getCredentials().getPassword()::getValue));

    if (hclient == null)
    {
      BPassword password = getCredentials().getPassword();
      String passwordValue = AccessController.doPrivileged((PrivilegedAction<String>) password::getValue);
      hclient = HClient.open(getHaystackUrl(), getCredentials().getUsername(), passwordValue);
    }
    
    return hclient;
  }

  /**
   * Obtain an HWatch that can be used to subscribe to remote objects.
   */
  public synchronized HWatch getHaystackWatch()
  {
    if (hwatch == null || !hwatch.isOpen())
    {
      hwatch = getHaystackClient().watchOpen(getHaystackUrl(), HNum.make(getLeaseInterval().getMillis(), "ms"));
    }

    return hwatch;
  }

  /**
   * Get the full Url to use when communicating with the remote server.
   */
  public String getHaystackUrl()
  {
    StringBuilder sb = new StringBuilder();

    if (getTls())
    {
      sb.append("https://");
    }
    else
    {
      sb.append("http://");
    }

    sb.append(getInternetAddress().getAuthority());

    String path = getUriPath();
    if (!path.startsWith("/"))
    {
      sb.append('/');
    }
    sb.append(path);
    if (!path.endsWith("/"))
    {
      sb.append('/');
    }

    return sb.toString();
  }

  /**
   * Do not call this method directly, it should only be used by the driver
   */
  public synchronized void registerProxyExt(BNHaystackProxyExt ext)
  {
    proxyExts.put(ext.getId().getRef(), ext);
  }

  /**
   * Do not call this method directly, it should only be used by the driver
   */
  public synchronized void unregisterProxyExt(BNHaystackProxyExt ext)
  {
    proxyExts.remove(ext.getId().getRef());
  }

  /**
   * Do not call this method directly, it should only be used by the driver
   */
  public synchronized BNHaystackProxyExt getRegisteredProxyExt(HRef id)
  {
    return proxyExts.get(id);
  }

  /**
   * Return the parent BNHaystackNetwork
   */
  public BNHaystackNetwork getNHaystackNetwork()
  {
    return (BNHaystackNetwork) getNetwork();
  }

  /**
   * Detect the features supported on the server.
   */
  public void detectFeatures()
  {
    /* Retrieve the 'ops' supported */
    HClient client = getHaystackClient();
    HGrid ops = client.ops();
    Set<String> opsSet = new HashSet<String>();
    Iterator it = ops.iterator();
    while (it.hasNext())
    {
      HRow row = (HRow)it.next();
      if (row.has("name"))
      {
        opsSet.add(row.getStr("name"));
      }
    }

    /*
     * read is part of the core Haystack API;
     * create, update and delete are extensions.
     */
    supportsCrud = opsSet.contains("createRec")
      && opsSet.contains("updateRec")
      && opsSet.contains("deleteRec");

    /*
     * Update the expiry time
     */
    featureDetectExpiry = BAbsTime.make().add(FEATURE_DETECT_EXPIRY_TIME);
  }

  /**
   * Return true if the feature detection information is current.
   *
   * @return  true if featureDetectExpiry is in the future
   * @return  false if featureDetectExpiry is null or in the past
   */
  public boolean isFeatureDetectionValid()
  {
    if (featureDetectExpiry == null)
    {
      return false;
    }

    if (featureDetectExpiry.isAfter(BAbsTime.make()))
    {
      return true;
    }

    featureDetectExpiry = null;
    return false;
  }

  /**
   * Return true if the server supports CRUD (Create, Read, Update, Delete)
   * operations.
   */
  public boolean isCrudSupported()
  {
    if (!isFeatureDetectionValid())
      detectFeatures();
    return supportsCrud;
  }

////////////////////////////////////////////////////////////////
// BINHaystackWorkerParent
////////////////////////////////////////////////////////////////

  /**
   * Handle a network exception that occured when running a chore.
   */
  @Override
  public synchronized void handleNetworkException(WorkerChore chore, CallNetworkException e)
  {
    LOG.severe("Network Exception! " + chore + ", " + e.getMessage());
    e.printStackTrace();

    resetClient();

    // By calling pingFail, we ensure that this server, and all its
    // points, etc, go into 'down' status.  This has the additional effect
    // of causing all non-ping WorkerChores to be ignored in
    // BNHaystackWorker.enqueueChore() until a ping succeeds.
    pingFail(e.getMessage());
  }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

  private synchronized void resetClient()
  {
    hclient = null;

    if (hwatch != null)
    {
      hwatch.close();
      hwatch = null;
    }

    featureDetectExpiry = null;

    if (isRunning() && LOG.isLoggable(Level.FINE))
    {
      LOG.fine(getHaystackUrl() + " reset client");
    }
  }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

  /**
   * Feature detection expiry; after 5 minutes.
   */
  private static final BRelTime FEATURE_DETECT_EXPIRY_TIME
    = BRelTime.makeMinutes(5);

  private static final Logger LOG = Logger.getLogger("nhaystack.driver");

  private HClient hclient;
  private HWatch hwatch;
  private final Map<HRef, BNHaystackProxyExt> proxyExts = new HashMap<>();

  /**
   * The time of expiry for feature detection.
   *
   * If this is null or a time in the past, then we will need to poll the
   * server's ops and about endpoints to determine what features are
   * supported.
   */
  private BAbsTime featureDetectExpiry;

  /**
   * Whether or not the server supports CRUD (Create, Read, Update Delete)
   * operations.
   *
   * This is determined by the feature detection code.
   */
  private boolean supportsCrud;
}
