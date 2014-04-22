//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.point;

import javax.baja.control.*;
import javax.baja.control.ext.*;
import javax.baja.sys.*;

import nhaystack.*;

/**
  * BNHaystackStrWritable is a writable string proxy point
  */
public class BNHaystackStrWritable 
    extends BStringWritable
{
    /*-
    class BNHaystackStrWritable
    {
        properties
        {
            proxyExt: BAbstractProxyExt default{[ new BNHaystackStrProxyExt() ]}
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackStrWritable(961794100)1.0$ @*/
/* Generated Thu Apr 10 15:56:03 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "proxyExt"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>proxyExt</code> property.
   * @see nhaystack.driver.point.BNHaystackStrWritable#getProxyExt
   * @see nhaystack.driver.point.BNHaystackStrWritable#setProxyExt
   */
  public static final Property proxyExt = newProperty(0, new BNHaystackStrProxyExt(),null);
  
  /**
   * Get the <code>proxyExt</code> property.
   * @see nhaystack.driver.point.BNHaystackStrWritable#proxyExt
   */
  public BAbstractProxyExt getProxyExt() { return (BAbstractProxyExt)get(proxyExt); }
  
  /**
   * Set the <code>proxyExt</code> property.
   * @see nhaystack.driver.point.BNHaystackStrWritable#proxyExt
   */
  public void setProxyExt(BAbstractProxyExt v) { set(proxyExt,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackStrWritable.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackStrWritable() { }

    public BNHaystackStrWritable(BHRef id)
    {
        ((BNHaystackProxyExt) getProxyExt()).setId(id);
    }

    public boolean isParentLegal(BComponent comp)
    {
        return (comp instanceof BNHaystackPointDeviceExt) || (comp instanceof BNHaystackPointFolder);
    }
}
