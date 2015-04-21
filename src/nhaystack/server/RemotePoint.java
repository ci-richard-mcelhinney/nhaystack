//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.server;

import javax.baja.control.*;
import javax.baja.control.ext.*;
import javax.baja.driver.*;
import javax.baja.fox.*;
import javax.baja.history.*;
import javax.baja.naming.*;
import javax.baja.security.*;
import javax.baja.sys.*;
import javax.baja.util.*;

import nhaystack.driver.*;
import nhaystack.driver.point.*;

/**
  * A RemotePoint represents a point that is present in
  * in a NiagaraNetwork.  A RemotePoint always has either an 
  * associated NiagaraNetwork history, or an associated 
  * NiagaraNetwork point, or both.
  */
class RemotePoint
{
    /**
      * Return whether the point is remote
      */
    static boolean isRemotePoint(BControlPoint point)
    {
        if (point.getProxyExt().getType().is(NIAGARA_PROXY_EXT)) return true;
        if (point.getProxyExt() instanceof BNHaystackProxyExt) return true;
        return false;
    }

    /**
      * Return whether the point is remote
      */
    private static boolean isRemoteDevice(BDevice device)
    {
        if (device.getType().is(NIAGARA_STATION)) return true;
        if (device instanceof BNHaystackServer) return true;
        return false;
    }

    /**
      * Create a RemotePoint from a BControlPoint, or return null.
      * The BControlPoint must be an imported point.
      */
    static RemotePoint fromControlPoint(BControlPoint point)
    {
        if (!isRemotePoint(point)) return null;

        BAbstractProxyExt proxyExt = point.getProxyExt();

        // "pointId" seems to always contain the slotPath on 
        // the remote host.
        String slotPath = proxyExt.get("pointId").toString();

        // get rid of unneeded "station:" ordQuery
        if (slotPath.startsWith("station:|"))   
            slotPath = slotPath.substring("station:|".length());

        // make sure its a valid slot path
        if (!slotPath.startsWith("slot:")) return null;

        // Find the ancestor NiagaraStation
        BDevice device = findParentDevice(point);
        if (device == null) return null;
        if (!isRemoteDevice(device)) return null;

        // We are sure the point is proxied.  
        return new RemotePoint(device.getName(), slotPath);
    }

    /**
      * Create a RemotePoint from a BHistoryConfig, or return null.
      * The BHistoryConfig must be an imported history.
      */
    static RemotePoint fromHistoryConfig(BHistoryConfig cfg)
    {
        // cannot be local history
        if (cfg.getId().getDeviceName().equals(Sys.getStation().getStationName()))
            throw new IllegalStateException();

        // For imported histories, the "source" OrdList seems 
        // to always have two ords, in the following order:
        //
        //      (1) the slotPath of the historyExt on the remote host,
        //      (2) the slotPath of the imported history on this host.  
        //
        // We want the first one, because that matches the 
        // "pointId" slotPath on the matching imported 
        // proxy point (if there is one)
        BOrd[] ords = cfg.getSource().toArray();
        if (ords.length == 0) return null; // misconfigured history
        String slotPath = ords[0].toString();

        // get rid of unneeded "station:" ordQuery
        if (slotPath.startsWith("station:|"))   
            slotPath = slotPath.substring("station:|".length());

        // if its not actually a SlotPath (e.g. imported AuditHistory), then return null
        if (!slotPath.startsWith("slot:"))
            return null;

        // The slotPath will be of the form "slot:/Path/To/Point/HistoryExt".
        // This doesn't *quite* match the "pointId" slot on the 
        // proxy point since it goes all the way to 
        // the History Ext.  So we'll get rid of that last slot.
        int last = slotPath.lastIndexOf("/");
        slotPath = slotPath.substring(0, last);

        // done
        return new RemotePoint(cfg.getId().getDeviceName(), slotPath);
    }

    private RemotePoint(String stationName, String slotPathStr)
    {
        if (!slotPathStr.startsWith("slot:"))
            throw new IllegalStateException(
                "Unexpected slot path value: '" + slotPathStr + "'");

        this.stationName = stationName;
        this.slotPath = new SlotPath("slot", slotPathStr.substring("slot:".length()));

        this.hashCode =
            31*31*stationName.hashCode() + 
            31*slotPath.getScheme().hashCode() +
            slotPath.getBody().hashCode();
    }

    static BDevice findParentDevice(BComplex cpx)
    {
        if (cpx == null) return null;
        if (cpx instanceof BDevice) return (BDevice) cpx;
        return findParentDevice(cpx.getParent());
    }

////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////

    public String toString()
    {
        return "[RemotePoint " +
            "stationName:" + stationName + ", " +
            "slotPath:" + slotPath + "]";
    }

    public boolean equals(Object obj) 
    {
        if (this == obj) return true;

        if (!(obj instanceof RemotePoint)) return false;

        RemotePoint that = (RemotePoint) obj;
        return 
            this.stationName          .equals(that.stationName) &&
            this.slotPath.getScheme() .equals(that.slotPath.getScheme()) &&
            this.slotPath.getBody()   .equals(that.slotPath.getBody());
    }

    public int hashCode() 
    { 
        return hashCode;
    }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

    /**
      * The name of the remote station that the point is proxied from.  
      * This will always correspond to the name of a NiagaraStation 
      * underneath the NiagaraNetwork.
      */
    String getStationName() { return stationName; }

    /**
      * The slotPath of the point on the remote station.
      */
    SlotPath getSlotPath() { return slotPath; }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private static final Type NIAGARA_PROXY_EXT;
    private static final Type NIAGARA_STATION;
    static
    {
        NIAGARA_PROXY_EXT = BTypeSpec.make("niagaraDriver:NiagaraProxyExt") .getResolvedType();
        NIAGARA_STATION   = BTypeSpec.make("niagaraDriver:NiagaraStation")  .getResolvedType();
    }

    private final String stationName;
    private final SlotPath slotPath;

    private final int hashCode;
}
