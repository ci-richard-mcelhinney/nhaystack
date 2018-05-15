//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.driver.point;

import javax.baja.driver.point.BProxyExt;
import javax.baja.driver.point.BReadWriteMode;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComplex;
import javax.baja.sys.Context;
import javax.baja.sys.Flags;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHRef;
import nhaystack.driver.BHTags;
import nhaystack.driver.BNHaystackServer;
import nhaystack.driver.worker.ReadSubscribeChore;
import nhaystack.driver.worker.ReadUnsubscribeChore;
import nhaystack.driver.worker.WriteChore;
import org.projecthaystack.HStr;
import org.projecthaystack.HVal;

/**
  * BNHaystackProxyExt is a proxy extension for remote haystack points.
  */
@NiagaraType
@NiagaraProperty(
  name = "id",
  type = "BHRef",
  defaultValue = "BHRef.DEFAULT"
)
@NiagaraProperty(
  name = "importedTags",
  type = "BHTags",
  defaultValue = "BHTags.DEFAULT",
  flags = Flags.READONLY
)
/**
 * the level to use when writing to haystack
 */
@NiagaraProperty(
  name = "haystackWriteLevel",
  type = "int",
  defaultValue = "16"
)
public abstract class BNHaystackProxyExt extends BProxyExt
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackProxyExt(29100958)1.0$ @*/
/* Generated Sat Nov 18 17:29:59 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "id"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code id} property.
   * @see #getId
   * @see #setId
   */
  public static final Property id = newProperty(0, BHRef.DEFAULT, null);
  
  /**
   * Get the {@code id} property.
   * @see #id
   */
  public BHRef getId() { return (BHRef)get(id); }
  
  /**
   * Set the {@code id} property.
   * @see #id
   */
  public void setId(BHRef v) { set(id, v, null); }

////////////////////////////////////////////////////////////////
// Property "importedTags"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code importedTags} property.
   * @see #getImportedTags
   * @see #setImportedTags
   */
  public static final Property importedTags = newProperty(Flags.READONLY, BHTags.DEFAULT, null);
  
  /**
   * Get the {@code importedTags} property.
   * @see #importedTags
   */
  public BHTags getImportedTags() { return (BHTags)get(importedTags); }
  
  /**
   * Set the {@code importedTags} property.
   * @see #importedTags
   */
  public void setImportedTags(BHTags v) { set(importedTags, v, null); }

////////////////////////////////////////////////////////////////
// Property "haystackWriteLevel"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code haystackWriteLevel} property.
   * the level to use when writing to haystack
   * @see #getHaystackWriteLevel
   * @see #setHaystackWriteLevel
   */
  public static final Property haystackWriteLevel = newProperty(0, 16, null);
  
  /**
   * Get the {@code haystackWriteLevel} property.
   * the level to use when writing to haystack
   * @see #haystackWriteLevel
   */
  public int getHaystackWriteLevel() { return getInt(haystackWriteLevel); }
  
  /**
   * Set the {@code haystackWriteLevel} property.
   * the level to use when writing to haystack
   * @see #haystackWriteLevel
   */
  public void setHaystackWriteLevel(int v) { setInt(haystackWriteLevel, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackProxyExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

////////////////////////////////////////////////////////////////
// BComponent
////////////////////////////////////////////////////////////////

    @Override
    public void started()
    {
        this.server = findServer();
        server.registerProxyExt(this);
    }

    @Override
    public void stopped()
    {
        if (server != null) server.unregisterProxyExt(this);
    }

    private BNHaystackServer findServer()
    {
        // traverse upwards
        BComplex comp = getParent();
        while ((comp != null) && !(comp instanceof BNHaystackServer))
            comp = comp.getParent();
        return (BNHaystackServer) comp;
    }

////////////////////////////////////////////////////////////////
// BProxyExt
////////////////////////////////////////////////////////////////

    @Override
    public final Type getDeviceExtType()
    {
        return BNHaystackPointDeviceExt.TYPE;
    }

    @Override
    public final BReadWriteMode getMode()
    {
        return getParentPoint().isWritablePoint() ? 
            BReadWriteMode.readWrite : 
            BReadWriteMode.readonly;
    }

    /**
      * readSubscribed
      */
    @Override
    public final void readSubscribed(Context context) throws Exception
    {
        if (server != null)
            server.postAsyncChore(new ReadSubscribeChore(server, this));
    }

    /**
      * readUnsubscribed
      */
    @Override
    public final void readUnsubscribed(Context context) throws Exception
    {
        if (server != null)
            server.postAsyncChore(new ReadUnsubscribeChore(server, this));
    }

    /**
      * write
      */
    @Override
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

