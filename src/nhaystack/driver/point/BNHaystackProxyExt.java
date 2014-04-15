//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.point;

import org.projecthaystack.*;

import javax.baja.driver.point.*;
import javax.baja.sys.*;

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
            importedTags: BHTags default{[ BHTags.DEFAULT ]} flags { readonly }
            haystackWriteLevel: int 
                -- the level to use when writing to haystack
                default{[ 16 ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackProxyExt(557806362)1.0$ @*/
/* Generated Thu Apr 10 16:32:45 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

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
// Property "importedTags"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>importedTags</code> property.
   * @see nhaystack.driver.point.BNHaystackProxyExt#getImportedTags
   * @see nhaystack.driver.point.BNHaystackProxyExt#setImportedTags
   */
  public static final Property importedTags = newProperty(Flags.READONLY, BHTags.DEFAULT,null);
  
  /**
   * Get the <code>importedTags</code> property.
   * @see nhaystack.driver.point.BNHaystackProxyExt#importedTags
   */
  public BHTags getImportedTags() { return (BHTags)get(importedTags); }
  
  /**
   * Set the <code>importedTags</code> property.
   * @see nhaystack.driver.point.BNHaystackProxyExt#importedTags
   */
  public void setImportedTags(BHTags v) { set(importedTags,v,null); }

////////////////////////////////////////////////////////////////
// Property "haystackWriteLevel"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystackWriteLevel</code> property.
   * the level to use when writing to haystack
   * @see nhaystack.driver.point.BNHaystackProxyExt#getHaystackWriteLevel
   * @see nhaystack.driver.point.BNHaystackProxyExt#setHaystackWriteLevel
   */
  public static final Property haystackWriteLevel = newProperty(0, 16,null);
  
  /**
   * Get the <code>haystackWriteLevel</code> property.
   * @see nhaystack.driver.point.BNHaystackProxyExt#haystackWriteLevel
   */
  public int getHaystackWriteLevel() { return getInt(haystackWriteLevel); }
  
  /**
   * Set the <code>haystackWriteLevel</code> property.
   * @see nhaystack.driver.point.BNHaystackProxyExt#haystackWriteLevel
   */
  public void setHaystackWriteLevel(int v) { setInt(haystackWriteLevel,v,null); }

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
        if (server == null) return false;
        server.postAsyncChore(new WriteChore(server, this));
        return true;
    }

////////////////////////////////////////////////////////////////
// public
////////////////////////////////////////////////////////////////

    public abstract void doRead(HVal curVal, HStr curStatus);

    public abstract void doWrite() throws Exception;

    public BNHaystackServer getHaystackServer() { return server; }

////////////////////////////////////////////////////////////////
// attributes
////////////////////////////////////////////////////////////////

    private BNHaystackServer server;
}

