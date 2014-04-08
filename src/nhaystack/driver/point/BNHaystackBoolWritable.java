package nhaystack.driver.point;

import javax.baja.control.*;
import javax.baja.control.ext.*;
import javax.baja.driver.point.*;
import javax.baja.status.*;
import javax.baja.sys.*;

import nhaystack.*;

public class BNHaystackBoolWritable 
    extends BBooleanWritable
{
    /*-
    class BNHaystackBoolWritable
    {
        properties
        {
            proxyExt: BAbstractProxyExt default{[ new BNHaystackBoolProxyExt() ]}
            haystack: BHDict default{[ BHDict.DEFAULT ]} 
        }
    }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.BNHaystackBoolWritable(106697507)1.0$ @*/
/* Generated Mon Apr 07 17:13:50 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "proxyExt"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>proxyExt</code> property.
   * @see nhaystack.driver.point.BNHaystackBoolWritable#getProxyExt
   * @see nhaystack.driver.point.BNHaystackBoolWritable#setProxyExt
   */
  public static final Property proxyExt = newProperty(0, new BNHaystackBoolProxyExt(),null);
  
  /**
   * Get the <code>proxyExt</code> property.
   * @see nhaystack.driver.point.BNHaystackBoolWritable#proxyExt
   */
  public BAbstractProxyExt getProxyExt() { return (BAbstractProxyExt)get(proxyExt); }
  
  /**
   * Set the <code>proxyExt</code> property.
   * @see nhaystack.driver.point.BNHaystackBoolWritable#proxyExt
   */
  public void setProxyExt(BAbstractProxyExt v) { set(proxyExt,v,null); }

////////////////////////////////////////////////////////////////
// Property "haystack"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystack</code> property.
   * @see nhaystack.driver.point.BNHaystackBoolWritable#getHaystack
   * @see nhaystack.driver.point.BNHaystackBoolWritable#setHaystack
   */
  public static final Property haystack = newProperty(0, BHDict.DEFAULT,null);
  
  /**
   * Get the <code>haystack</code> property.
   * @see nhaystack.driver.point.BNHaystackBoolWritable#haystack
   */
  public BHDict getHaystack() { return (BHDict)get(haystack); }
  
  /**
   * Set the <code>haystack</code> property.
   * @see nhaystack.driver.point.BNHaystackBoolWritable#haystack
   */
  public void setHaystack(BHDict v) { set(haystack,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackBoolWritable.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNHaystackBoolWritable() { }

    public BNHaystackBoolWritable(BHRef id)
    {
        ((BNHaystackProxyExt) getProxyExt()).setId(id);
    }

    public boolean isParentLegal(BComponent comp)
    {
        return (comp instanceof BNHaystackPointDeviceExt) || (comp instanceof BNHaystackPointFolder);
    }
}
