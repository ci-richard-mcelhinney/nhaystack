//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   03 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver;

import javax.baja.alarm.*;
import javax.baja.driver.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.net.*;
import javax.baja.security.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import org.projecthaystack.*;
import org.projecthaystack.client.*;

import nhaystack.driver.history.*;
import nhaystack.worker.*;

public class BNHaystackServer 
    extends BDevice
    implements BINHaystackWorkerParent
{
    /*-
    class BNHaystackServer
    {
        properties
        {
            internetAddress: BInternetAddress default{[ BInternetAddress.NULL ]}
            projectName: String default{[ "" ]}

            credentials: BUsernameAndPassword default{[ new BUsernameAndPassword() ]}

            histories: BNHaystackHistoryDeviceExt  default{[ new BNHaystackHistoryDeviceExt()  ]}

            worker: BNHaystackWorker default{[ new BNHaystackWorker() ]}

            alarmSourceInfo: BAlarmSourceInfo
                flags { hidden }
                default {[ new BAlarmSourceInfo() ]}
        }
        actions  
        {  
            submitLearnHistoriesJob(): BOrd flags { hidden }
        }  
    }
  -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.BNHaystackServer(3462888085)1.0$ @*/
/* Generated Fri Apr 04 15:36:15 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
// Property "projectName"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>projectName</code> property.
   * @see nhaystack.driver.BNHaystackServer#getProjectName
   * @see nhaystack.driver.BNHaystackServer#setProjectName
   */
  public static final Property projectName = newProperty(0, "",null);
  
  /**
   * Get the <code>projectName</code> property.
   * @see nhaystack.driver.BNHaystackServer#projectName
   */
  public String getProjectName() { return getString(projectName); }
  
  /**
   * Set the <code>projectName</code> property.
   * @see nhaystack.driver.BNHaystackServer#projectName
   */
  public void setProjectName(String v) { setString(projectName,v,null); }

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
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackServer.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void changed(Property property, Context context)
    {
        if ((property == internetAddress) || 
            (property == projectName) || 
            (property == credentials))
            resetClient();
    }

    public boolean isParentLegal(BComponent comp)
    {
        return 
            (comp instanceof BNHaystackNetwork) || 
            (comp instanceof BNHaystackServerFolder);
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
                "Ping:" + makeApiUrl(),
                new Invocation(this, ping, null, null)));
    }

    public void doPing() throws Exception
    {
        long begin = Clock.ticks();
        if (LOG.isTraceOn())
            LOG.trace("Server Ping BEGIN " + makeApiUrl());

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
                LOG.trace("Server Ping END " + makeApiUrl() + " (" + (end-begin) + "ms)");
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

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    public final IFuture postAsyncChore(WorkerChore chore)
    {
        if (!isRunning()) return null;

        if (!getEnabled())
        {
            if (LOG.isTraceOn())
                LOG.trace(makeApiUrl() + " server disabled: " + chore);
            return null;
        }

        if (getNetwork().isDisabled())
        {
            if (LOG.isTraceOn())
                LOG.trace(makeApiUrl() + " network disabled: " + chore);
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
            LOG.error(makeApiUrl() + " Cannot post async: " + e.getMessage());
            return null;
        }
    }

//    /**
//      * This is called whenever a chore which operates on this server
//      * experiences a SocketTimeoutException
//      */
//    public synchronized void socketTimeout(WorkerChore chore, Exception e)
//    {
//        LOG.error(
//            "Socket timeout! " + makeApiUrl() + ": " + 
//            chore + ", " + e.getMessage());
//        e.printStackTrace();
//
//        resetClient();
//        pingFail(e.getMessage()); 
//    }

    public synchronized HClient getHaystackClient() throws Exception
    {
        if (hclient == null)
        {
            hclient = HClient.open(
                makeApiUrl(),
                getCredentials().getUsername(),
                getCredentials().getPassword().getString());
        }
        return hclient;
    }

    public String makeApiUrl()
    {
        return 
            "http://" + getInternetAddress().getAuthority() + 
            "/api/" + getProjectName() + "/";
    }

////////////////////////////////////////////////////////////////
// private
////////////////////////////////////////////////////////////////

    private synchronized void resetClient()
    {
        hclient = null;

        if (isRunning() && LOG.isTraceOn())
            LOG.trace(makeApiUrl() + " reset client");
    }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    private static final Log LOG = Log.getLog("nhaystack");

    private HClient hclient;
}
