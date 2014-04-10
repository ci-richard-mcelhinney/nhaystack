package nhaystack.driver.point;

import java.util.*;

import javax.baja.driver.point.*;
import javax.baja.status.*;
import javax.baja.sys.*;

import org.projecthaystack.*;

import nhaystack.*;
import nhaystack.driver.*;
import nhaystack.driver.worker.*;

public abstract class BNHaystackProxyExt extends BProxyExt
{
    /*-
    class BNHaystackProxyExt
    {
        properties
        {
            id: BHRef default{[ BHRef.DEFAULT ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackProxyExt(1839545253)1.0$ @*/
/* Generated Mon Apr 07 08:34:06 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "id"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>id</code> property.
   * @see nhaystack.driver.point.BNHaystackProxyExt#getId
   * @see nhaystack.driver.point.BNHaystackProxyExt#setId
   */
  public static final Property id = newProperty(0, BHRef.DEFAULT,null);
  
  /**
   * Get the <code>id</code> property.
   * @see nhaystack.driver.point.BNHaystackProxyExt#id
   */
  public BHRef getId() { return (BHRef)get(id); }
  
  /**
   * Set the <code>id</code> property.
   * @see nhaystack.driver.point.BNHaystackProxyExt#id
   */
  public void setId(BHRef v) { set(id,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackProxyExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

////////////////////////////////////////////////////////////////
// BComponent
////////////////////////////////////////////////////////////////

    public void started()
    {
        this.server = findServer();
        server.registerProxyExt(this);
    }

    public void stopped()
    {
        if (server != null) server.unregisterProxyExt(this);
    }

    private BNHaystackServer findServer()
    {
        BComplex comp = getParent();
        while ((comp != null) && (!(comp instanceof BNHaystackServer)))
            comp = comp.getParent();
        return (BNHaystackServer) comp;
    }

////////////////////////////////////////////////////////////////
// BProxyExt
////////////////////////////////////////////////////////////////

    public final Type getDeviceExtType()
    {
        return BNHaystackPointDeviceExt.TYPE;
    }

    public final BReadWriteMode getMode()
    {
        return getParentPoint().isWritablePoint() ? 
            BReadWriteMode.readWrite : 
            BReadWriteMode.readonly;
    }

    /**
      * readSubscribed
      */
    public final void readSubscribed(Context context) throws Exception
    {
        if (server != null)
            server.postAsyncChore(new ReadSubscribeChore(server, this));
    }

    /**
      * readUnsubscribed
      */
    public final void readUnsubscribed(Context context) throws Exception
    {
        if (server != null)
            server.postAsyncChore(new ReadUnsubscribeChore(server, this));
    }

    /**
      * write
      */
    public final boolean write(Context context) throws Exception
    {
        return true;
//        if (server == null) return false;
//        server.postAsyncChore(new WriteChore(server, this));
//        return true;
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    public abstract void doRead(HVal curVal, HStr curStatus);

//    public abstract void doWrite() throws Exception;

//    public BNHaystackServer getNHaystackServer() { return server; }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    private BNHaystackServer server;
}

