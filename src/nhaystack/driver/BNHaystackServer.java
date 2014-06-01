//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver;

import java.util.*;

import javax.baja.alarm.*;
import javax.baja.driver.*;
import javax.baja.driver.util.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.net.*;
import javax.baja.security.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.driver.history.*;
import nhaystack.driver.history.learn.*;
import nhaystack.driver.point.*;
import nhaystack.driver.point.learn.*;
import nhaystack.driver.worker.*;
import nhaystack.worker.*;

/**
  * BNHaystackServer models a device which is serving up haystack data
  */
public class BNHaystackServer 
    extends BDevice
    implements BINHaystackWorkerParent, BIPollable
{
    /*-
    class BNHaystackServer
    {
        properties
        {
            internetAddress: BInternetAddress default{[ BInternetAddress.NULL ]}
            uriPath: String default{[ "" ]}
            credentials: BUsernameAndPassword default{[ new BUsernameAndPassword() ]}

            histories: BNHaystackHistoryDeviceExt default{[ new BNHaystackHistoryDeviceExt() ]}
            points:    BNHaystackPointDeviceExt   default{[ new BNHaystackPointDeviceExt()   ]}

            pollFrequency: BPollFrequency default{[ BPollFrequency.normal ]}

            worker: BNHaystackWorker default{[ new BNHaystackWorker() ]}

            alarmSourceInfo: BAlarmSourceInfo
                flags { hidden }
                default {[ new BAlarmSourceInfo() ]}
        }
        actions  
        {  
            submitLearnHistoriesJob(): BOrd flags { hidden }
            submitLearnPointsJob():    BOrd flags { hidden }
        }  
    }
  -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BNHaystackServer(2474489210)1.0$ @*/
/* Generated Fri Apr 18 12:50:39 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "internetAddress"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>internetAddress</code> property.
   * @see nhaystack.driver.BNHaystackServer#getInternetAddress
   * @see nhaystack.driver.BNHaystackServer#setInternetAddress
   */
  public static final Property internetAddress = newProperty(0, BInternetAddress.NULL,null);
  
  /**
   * Get the <code>internetAddress</code> property.
   * @see nhaystack.driver.BNHaystackServer#internetAddress
   */
  public BInternetAddress getInternetAddress() { return (BInternetAddress)get(internetAddress); }
  
  /**
   * Set the <code>internetAddress</code> property.
   * @see nhaystack.driver.BNHaystackServer#internetAddress
   */
  public void setInternetAddress(BInternetAddress v) { set(internetAddress,v,null); }

////////////////////////////////////////////////////////////////
// Property "uriPath"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>uriPath</code> property.
   * @see nhaystack.driver.BNHaystackServer#getUriPath
   * @see nhaystack.driver.BNHaystackServer#setUriPath
   */
  public static final Property uriPath = newProperty(0, "",null);
  
  /**
   * Get the <code>uriPath</code> property.
   * @see nhaystack.driver.BNHaystackServer#uriPath
   */
  public String getUriPath() { return getString(uriPath); }
  
  /**
   * Set the <code>uriPath</code> property.
   * @see nhaystack.driver.BNHaystackServer#uriPath
   */
  public void setUriPath(String v) { setString(uriPath,v,null); }

////////////////////////////////////////////////////////////////
// Property "credentials"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>credentials</code> property.
   * @see nhaystack.driver.BNHaystackServer#getCredentials
   * @see nhaystack.driver.BNHaystackServer#setCredentials
   */
  public static final Property credentials = newProperty(0, new BUsernameAndPassword(),null);
  
  /**
   * Get the <code>credentials</code> property.
   * @see nhaystack.driver.BNHaystackServer#credentials
   */
  public BUsernameAndPassword getCredentials() { return (BUsernameAndPassword)get(credentials); }
  
  /**
   * Set the <code>credentials</code> property.
   * @see nhaystack.driver.BNHaystackServer#credentials
   */
  public void setCredentials(BUsernameAndPassword v) { set(credentials,v,null); }

////////////////////////////////////////////////////////////////
// Property "histories"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>histories</code> property.
   * @see nhaystack.driver.BNHaystackServer#getHistories
   * @see nhaystack.driver.BNHaystackServer#setHistories
   */
  public static final Property histories = newProperty(0, new BNHaystackHistoryDeviceExt(),null);
  
  /**
   * Get the <code>histories</code> property.
   * @see nhaystack.driver.BNHaystackServer#histories
   */
  public BNHaystackHistoryDeviceExt getHistories() { return (BNHaystackHistoryDeviceExt)get(histories); }
  
  /**
   * Set the <code>histories</code> property.
   * @see nhaystack.driver.BNHaystackServer#histories
   */
  public void setHistories(BNHaystackHistoryDeviceExt v) { set(histories,v,null); }

////////////////////////////////////////////////////////////////
// Property "points"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>points</code> property.
   * @see nhaystack.driver.BNHaystackServer#getPoints
   * @see nhaystack.driver.BNHaystackServer#setPoints
   */
  public static final Property points = newProperty(0, new BNHaystackPointDeviceExt(),null);
  
  /**
   * Get the <code>points</code> property.
   * @see nhaystack.driver.BNHaystackServer#points
   */
  public BNHaystackPointDeviceExt getPoints() { return (BNHaystackPointDeviceExt)get(points); }
  
  /**
   * Set the <code>points</code> property.
   * @see nhaystack.driver.BNHaystackServer#points
   */
  public void setPoints(BNHaystackPointDeviceExt v) { set(points,v,null); }

////////////////////////////////////////////////////////////////
// Property "pollFrequency"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>pollFrequency</code> property.
   * @see nhaystack.driver.BNHaystackServer#getPollFrequency
   * @see nhaystack.driver.BNHaystackServer#setPollFrequency
   */
  public static final Property pollFrequency = newProperty(0, BPollFrequency.normal,null);
  
  /**
   * Get the <code>pollFrequency</code> property.
   * @see nhaystack.driver.BNHaystackServer#pollFrequency
   */
  public BPollFrequency getPollFrequency() { return (BPollFrequency)get(pollFrequency); }
  
  /**
   * Set the <code>pollFrequency</code> property.
   * @see nhaystack.driver.BNHaystackServer#pollFrequency
   */
  public void setPollFrequency(BPollFrequency v) { set(pollFrequency,v,null); }

////////////////////////////////////////////////////////////////
// Property "worker"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>worker</code> property.
   * @see nhaystack.driver.BNHaystackServer#getWorker
   * @see nhaystack.driver.BNHaystackServer#setWorker
   */
  public static final Property worker = newProperty(0, new BNHaystackWorker(),null);
  
  /**
   * Get the <code>worker</code> property.
   * @see nhaystack.driver.BNHaystackServer#worker
   */
  public BNHaystackWorker getWorker() { return (BNHaystackWorker)get(worker); }
  
  /**
   * Set the <code>worker</code> property.
   * @see nhaystack.driver.BNHaystackServer#worker
   */
  public void setWorker(BNHaystackWorker v) { set(worker,v,null); }

////////////////////////////////////////////////////////////////
// Property "alarmSourceInfo"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>alarmSourceInfo</code> property.
   * @see nhaystack.driver.BNHaystackServer#getAlarmSourceInfo
   * @see nhaystack.driver.BNHaystackServer#setAlarmSourceInfo
   */
  public static final Property alarmSourceInfo = newProperty(Flags.HIDDEN, new BAlarmSourceInfo(),null);
  
  /**
   * Get the <code>alarmSourceInfo</code> property.
   * @see nhaystack.driver.BNHaystackServer#alarmSourceInfo
   */
  public BAlarmSourceInfo getAlarmSourceInfo() { return (BAlarmSourceInfo)get(alarmSourceInfo); }
  
  /**
   * Set the <code>alarmSourceInfo</code> property.
   * @see nhaystack.driver.BNHaystackServer#alarmSourceInfo
   */
  public void setAlarmSourceInfo(BAlarmSourceInfo v) { set(alarmSourceInfo,v,null); }

////////////////////////////////////////////////////////////////
// Action "submitLearnHistoriesJob"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>submitLearnHistoriesJob</code> action.
   * @see nhaystack.driver.BNHaystackServer#submitLearnHistoriesJob()
   */
  public static final Action submitLearnHistoriesJob = newAction(Flags.HIDDEN,null);
  
  /**
   * Invoke the <code>submitLearnHistoriesJob</code> action.
   * @see nhaystack.driver.BNHaystackServer#submitLearnHistoriesJob
   */
  public BOrd submitLearnHistoriesJob() { return (BOrd)invoke(submitLearnHistoriesJob,null,null); }

////////////////////////////////////////////////////////////////
// Action "submitLearnPointsJob"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>submitLearnPointsJob</code> action.
   * @see nhaystack.driver.BNHaystackServer#submitLearnPointsJob()
   */
  public static final Action submitLearnPointsJob = newAction(Flags.HIDDEN,null);
  
  /**
   * Invoke the <code>submitLearnPointsJob</code> action.
   * @see nhaystack.driver.BNHaystackServer#submitLearnPointsJob
   */
  public BOrd submitLearnPointsJob() { return (BOrd)invoke(submitLearnPointsJob,null,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackServer.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void changed(Property property, Context context)
    {
        if ((property == internetAddress) || 
            (property == uriPath) || 
            (property == credentials))
            resetClient();
    }

    public boolean isParentLegal(BComponent comp)
    {
        return 
            (comp instanceof BNHaystackNetwork) || 
            (comp instanceof BNHaystackServerFolder);
    }

    public void started()
    {
        getNHaystackNetwork().getPollScheduler().subscribe(this);
    }

    public void stopped()
    {
        getNHaystackNetwork().getPollScheduler().unsubscribe(this);

        synchronized(this)
        {
            if (hwatch != null) hwatch.close();
        }
    }

////////////////////////////////////////////////////////////////
// BDevice
////////////////////////////////////////////////////////////////

    public Type getNetworkType()
    {
        return BNHaystackNetwork.TYPE;
    }

    protected IFuture postPing()
    {
        return postAsyncChore(
            new PingInvocation(
                getWorker(),
                "Ping:" + getHaystackUrl(),
                new Invocation(this, ping, null, null)));
    }

    public void doPing() throws Exception
    {
        long begin = Clock.ticks();
        if (LOG.isTraceOn())
            LOG.trace("Server Ping BEGIN " + getHaystackUrl());

        if (isDisabled() || isFault())
            return;

        try
        {
            HClient client = getHaystackClient();
            client.about();
            pingOk();
        }
        catch (Exception e)
        {
            pingFail(e.getMessage());
            throw new BajaRuntimeException(e);
        }
        finally
        {
            if (LOG.isTraceOn())
            {
                long end = Clock.ticks();
                LOG.trace("Server Ping END " + getHaystackUrl() + " (" + (end-begin) + "ms)");
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

    public BOrd doSubmitLearnPointsJob()
    {
        BNHaystackLearnPointsJob job = new BNHaystackLearnPointsJob(this);
        return job.submit(null);
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    /**
      * Post a chore asynchronously to the worker queue
      */
    public final IFuture postAsyncChore(WorkerChore chore)
    {
        if (!isRunning()) return null;

        if (!getEnabled())
        {
            if (LOG.isTraceOn())
                LOG.trace(getHaystackUrl() + " server disabled: " + chore);
            return null;
        }

        if (getNetwork().isDisabled())
        {
            if (LOG.isTraceOn())
                LOG.trace(getHaystackUrl() + " network disabled: " + chore);
            return null;
        }

        try
        {
            // the worker is responsible for coalescing tasks
            getWorker().enqueueChore(chore);
            return null;
        }
        catch (Exception e)
        {
            LOG.error(getHaystackUrl() + " Cannot post async: " + e.getMessage());
            return null;
        }
    }

    /**
      * Obtain an HClient instance that can be used to communicate with
      * the remote server.
      */
    public synchronized HClient getHaystackClient() 
    {
        if (hclient == null)
        {
            hclient = HClient.open(
                getHaystackUrl(),
                getCredentials().getUsername(),
                getCredentials().getPassword().getString());
        }
        return hclient;
    }

    /**
      * Obtain an HWatch that can be used to subscribe to remote objects.
      */
    public synchronized HWatch getHaystackWatch() 
    {
        if ((hwatch == null) || !hwatch.isOpen())
            hwatch = getHaystackClient().watchOpen(getHaystackUrl());

        return hwatch;
    }

    /**
      * Get the full Url to use when communicating with the remote server.
      */
    public String getHaystackUrl()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("http://");
        sb.append(getInternetAddress().getAuthority());

        String path = getUriPath();
        if (!path.startsWith("/")) sb.append("/");
        sb.append(path);
        if (!path.endsWith("/")) sb.append("/");

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
        return (BNHaystackProxyExt) proxyExts.get(id);
    }

    /**
      * Return the parent BNHaystackNetwork
      */
    public BNHaystackNetwork getNHaystackNetwork()
    {
        return (BNHaystackNetwork) getNetwork();
    }

////////////////////////////////////////////////////////////////
// BINHaystackWorkerParent
////////////////////////////////////////////////////////////////

    /**
      * Handle a network exception that occured when running a chore.
      */
    public synchronized void handleNetworkException(WorkerChore chore, CallNetworkException e)
    {
        LOG.error("Network Exception! " + chore + ", " + e.getMessage());
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

        if (isRunning() && LOG.isTraceOn())
            LOG.trace(getHaystackUrl() + " reset client");
    }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack.driver");

    private HClient hclient;
    private HWatch hwatch;
    private Map proxyExts = new HashMap();
}
