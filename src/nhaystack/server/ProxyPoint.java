//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   04 Oct 2012  Mike Jarmy  Creation
//
package nhaystack.server;

import java.util.*;
import javax.baja.control.*;
import javax.baja.control.ext.*;
import javax.baja.driver.*;
import javax.baja.history.*;
import javax.baja.log.*;
import javax.baja.naming.*;
import javax.baja.sys.*;
import javax.baja.util.*;

/**
  * A ProxyPoint represents a point that is proxied in some way
  * in a NiagaraNetwork.  A ProxyPoint always has either an 
  * associated imported history, or an associated imported point, 
  * or both.
  */
public class ProxyPoint
{
    /**
      * Create a ProxyPoint for a BControlPoint.
      * The BControlPoint must be for an imported point.
      */
    public static ProxyPoint make(BControlPoint point)
    {
        // Check for a NiagaraProxyExt
        BAbstractProxyExt proxyExt = point.getProxyExt();
        if (!proxyExt.getType().is(ProxyPointManager.NIAGARA_PROXY_EXT)) 
            throw new IllegalStateException();

        // "pointId" seems to always contain the slotPath on 
        // the remote host.
        String slotPath = proxyExt.get("pointId").toString();

        // get rid of unneeded "station:" ordQuery
        if (slotPath.startsWith("station:|"))   
            slotPath = slotPath.substring("station:|".length());

        // Find the ancestor NiagaraStation
        BDevice device = findParentDevice(point);
        if (device == null) return null;
        if (!device.getType().is(ProxyPointManager.NIAGARA_STATION)) return null;

        // We are sure the point is proxied.  
        return new ProxyPoint(device.getName(), slotPath);
    }

    /**
      * Create a ProxyPoint for a BHistoryConfig.
      * The BHistoryConfig must be for an imported history.
      */
    public static ProxyPoint make(BHistoryConfig cfg)
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
        String slotPath = ords[0].toString();

        // get rid of unneeded "station:" ordQuery
        if (slotPath.startsWith("station:|"))   
            slotPath = slotPath.substring("station:|".length());

        // The slotPath will be of the form "slot:/Path/To/Point/HistoryExt".
        // This doesn't *quite* match the "pointId" slot on the 
        // proxy point since it goes all the way to 
        // the History Ext.  So we'll get rid of that last slot.
        int last = slotPath.lastIndexOf("/");
        slotPath = slotPath.substring(0, last);

        // done
        return new ProxyPoint(cfg.getId().getDeviceName(), slotPath);
    }

    private ProxyPoint(String stationName, String slotPathStr)
    {
        if (!slotPathStr.startsWith("slot:"))
            throw new IllegalStateException();

        this.stationName = stationName;
        this.slotPath = new SlotPath("slot", slotPathStr.substring("slot:".length()));

        this.hashCode =
            31*31*stationName.hashCode() + 
            31*slotPath.getScheme().hashCode() +
            slotPath.getBody().hashCode();
    }

    private static BDevice findParentDevice(BComplex cpx)
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
        return "[ProxyPoint " +
            "stationName:" + stationName + ", " +
            "slotPath:" + slotPath + "]";
    }

    public boolean equals(Object obj) 
    {
        if (this == obj) return true;

        if (!(obj instanceof ProxyPoint)) return false;

        ProxyPoint that = (ProxyPoint) obj;
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

    // The name of the remote station that the point is proxied from.  
    // This will always correspond to the name of a NiagaraStation 
    // underneath the NiagaraNetwork.
    public String getStationName() { return stationName; }

    // The slotPath of the point on the remote station.
    public SlotPath getSlotPath() { return slotPath; }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

    private final String stationName;
    private final SlotPath slotPath;

    private final int hashCode;
}
