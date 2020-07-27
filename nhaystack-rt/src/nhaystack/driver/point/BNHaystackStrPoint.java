//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.driver.point;

import javax.baja.control.BStringPoint;
import javax.baja.control.ext.BAbstractProxyExt;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHRef;

/**
  * BNHaystackStrPoint is a read-only string proxy point
  */
@NiagaraType
@NiagaraProperty(
  name = "proxyExt",
  type = "BAbstractProxyExt",
  defaultValue = "new BNHaystackStrProxyExt()"
)
public class BNHaystackStrPoint 
    extends BStringPoint
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackStrPoint(4156367978)1.0$ @*/
/* Generated Mon Nov 20 14:58:00 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "proxyExt"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code proxyExt} property.
   * @see #getProxyExt
   * @see #setProxyExt
   */
  public static final Property proxyExt = newProperty(0, new BNHaystackStrProxyExt(), null);
  
  /**
   * Get the {@code proxyExt} property.
   * @see #proxyExt
   */
  @Override
  public BAbstractProxyExt getProxyExt() { return (BAbstractProxyExt)get(proxyExt); }
  
  /**
   * Set the {@code proxyExt} property.
   * @see #proxyExt
   */
  @Override
  public void setProxyExt(BAbstractProxyExt v) { set(proxyExt, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackStrPoint.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackStrPoint() { }

    public BNHaystackStrPoint(BHRef id)
    {
        ((BNHaystackProxyExt) getProxyExt()).setId(id);
    }

    @Override
    public boolean isParentLegal(BComponent comp)
    {
        return (comp instanceof BNHaystackPointDeviceExt) || (comp instanceof BNHaystackPointFolder);
    }
}
