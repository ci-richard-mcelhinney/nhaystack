
package nhaystack.driver.point.learn;

import javax.baja.sys.*;
import javax.baja.control.*;  

import nhaystack.*;
import nhaystack.driver.*;
import nhaystack.driver.point.*;

public final class BNHaystackPointEntry extends BComponent
{
    /*-
      class BNHaystackPointEntry
      {
          properties
          {
              id: BHRef default{[ BHRef.DEFAULT ]}
              kind: String default{[ "" ]} 
              writable: boolean default {[ false ]} 
              haystack: BHDict default{[ BHDict.DEFAULT ]} 
          } 
      }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.learn.BNHaystackPointEntry(2127642458)1.0$ @*/
/* Generated Mon Apr 07 17:13:50 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "id"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>id</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#getId
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#setId
   */
  public static final Property id = newProperty(0, BHRef.DEFAULT,null);
  
  /**
   * Get the <code>id</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#id
   */
  public BHRef getId() { return (BHRef)get(id); }
  
  /**
   * Set the <code>id</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#id
   */
  public void setId(BHRef v) { set(id,v,null); }

////////////////////////////////////////////////////////////////
// Property "kind"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>kind</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#getKind
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#setKind
   */
  public static final Property kind = newProperty(0, "",null);
  
  /**
   * Get the <code>kind</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#kind
   */
  public String getKind() { return getString(kind); }
  
  /**
   * Set the <code>kind</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#kind
   */
  public void setKind(String v) { setString(kind,v,null); }

////////////////////////////////////////////////////////////////
// Property "writable"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>writable</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#getWritable
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#setWritable
   */
  public static final Property writable = newProperty(0, false,null);
  
  /**
   * Get the <code>writable</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#writable
   */
  public boolean getWritable() { return getBoolean(writable); }
  
  /**
   * Set the <code>writable</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#writable
   */
  public void setWritable(boolean v) { setBoolean(writable,v,null); }

////////////////////////////////////////////////////////////////
// Property "haystack"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>haystack</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#getHaystack
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#setHaystack
   */
  public static final Property haystack = newProperty(0, BHDict.DEFAULT,null);
  
  /**
   * Get the <code>haystack</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#haystack
   */
  public BHDict getHaystack() { return (BHDict)get(haystack); }
  
  /**
   * Set the <code>haystack</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#haystack
   */
  public void setHaystack(BHDict v) { set(haystack,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackPointEntry.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
      * Indentifies if the supplied component is point entry and whether it is the same one
      */
    public boolean is(BComponent component)
    {
        boolean res = false;

        if (component instanceof BControlPoint) 
        {
            BNHaystackProxyExt proxyExt = (BNHaystackProxyExt)((BControlPoint)component).getProxyExt();

            if (proxyExt.getId().equivalent(getId()))
                res = true;
        }

        return res;
    }
}
