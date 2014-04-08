package nhaystack.driver.point;

import javax.baja.control.*;
import javax.baja.control.ext.*;
import javax.baja.driver.point.*;
import javax.baja.sys.*;

import nhaystack.*;

public class BNHaystackNumberPoint 
    extends BNumericPoint
{
    /*-
    class BNHaystackNumberPoint
    {
        properties
        {
            proxyExt: BAbstractProxyExt default{[ new BNHaystackNumberProxyExt() ]}
            haystack: BHDict default{[ BHDict.DEFAULT ]} 
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackNumberPoint(2759762148)1.0$ @*/
/* Generated Mon Apr 07 17:13:50 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "proxyExt"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>proxyExt</code> property.
   * @see nhaystack.driver.point.BNHaystackNumberPoint#getProxyExt
   * @see nhaystack.driver.point.BNHaystackNumberPoint#setProxyExt
   */
  public static final Property proxyExt = newProperty(0, new BNHaystackNumberProxyExt(),null);
  
  /**
   * Get the <code>proxyExt</code> property.
   * @see nhaystack.driver.point.BNHaystackNumberPoint#proxyExt
   */
  public BAbstractProxyExt getProxyExt() { return (BAbstractProxyExt)get(proxyExt); }
  
  /**
   * Set the <code>proxyExt</code> property.
   * @see nhaystack.driver.point.BNHaystackNumberPoint#proxyExt
   */
  public void setProxyExt(BAbstractProxyExt v) { set(proxyExt,v,null); }

////////////////////////////////////////////////////////////////
// Property "haystack"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystack</code> property.
   * @see nhaystack.driver.point.BNHaystackNumberPoint#getHaystack
   * @see nhaystack.driver.point.BNHaystackNumberPoint#setHaystack
   */
  public static final Property haystack = newProperty(0, BHDict.DEFAULT,null);
  
  /**
   * Get the <code>haystack</code> property.
   * @see nhaystack.driver.point.BNHaystackNumberPoint#haystack
   */
  public BHDict getHaystack() { return (BHDict)get(haystack); }
  
  /**
   * Set the <code>haystack</code> property.
   * @see nhaystack.driver.point.BNHaystackNumberPoint#haystack
   */
  public void setHaystack(BHDict v) { set(haystack,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackNumberPoint.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackNumberPoint() { }

    public BNHaystackNumberPoint(BHRef id)
    {
        ((BNHaystackProxyExt) getProxyExt()).setId(id);
    }

    public boolean isParentLegal(BComponent comp)
    {
        return (comp instanceof BNHaystackPointDeviceExt) || (comp instanceof BNHaystackPointFolder);
    }
}
