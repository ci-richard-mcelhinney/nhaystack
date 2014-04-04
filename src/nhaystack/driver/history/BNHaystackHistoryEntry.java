package nhaystack.driver.history;

import javax.baja.control.*;  
import javax.baja.history.*;
import javax.baja.sys.*;
import nhaystack.*;
import nhaystack.driver.history.*;

public final class BNHaystackHistoryEntry extends BComponent
{
    /*-
      class BNHaystackHistoryEntry
      {
        properties
        {
            id: BHRef default{[ BHRef.DEFAULT ]}
            kind: String default{[ "" ]}
            tz: String default{[ "" ]}
            historyId: BHistoryId default{[ BHistoryId.DEFAULT ]}
        } 
      }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.BNHaystackHistoryEntry(4258600562)1.0$ @*/
/* Generated Fri Apr 04 16:22:50 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "id"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>id</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#getId
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#setId
   */
  public static final Property id = newProperty(0, BHRef.DEFAULT,null);
  
  /**
   * Get the <code>id</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#id
   */
  public BHRef getId() { return (BHRef)get(id); }
  
  /**
   * Set the <code>id</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#id
   */
  public void setId(BHRef v) { set(id,v,null); }

////////////////////////////////////////////////////////////////
// Property "kind"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>kind</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#getKind
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#setKind
   */
  public static final Property kind = newProperty(0, "",null);
  
  /**
   * Get the <code>kind</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#kind
   */
  public String getKind() { return getString(kind); }
  
  /**
   * Set the <code>kind</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#kind
   */
  public void setKind(String v) { setString(kind,v,null); }

////////////////////////////////////////////////////////////////
// Property "tz"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>tz</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#getTz
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#setTz
   */
  public static final Property tz = newProperty(0, "",null);
  
  /**
   * Get the <code>tz</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#tz
   */
  public String getTz() { return getString(tz); }
  
  /**
   * Set the <code>tz</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#tz
   */
  public void setTz(String v) { setString(tz,v,null); }

////////////////////////////////////////////////////////////////
// Property "historyId"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>historyId</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#getHistoryId
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#setHistoryId
   */
  public static final Property historyId = newProperty(0, BHistoryId.DEFAULT,null);
  
  /**
   * Get the <code>historyId</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#historyId
   */
  public BHistoryId getHistoryId() { return (BHistoryId)get(historyId); }
  
  /**
   * Set the <code>historyId</code> property.
   * @see nhaystack.driver.history.BNHaystackHistoryEntry#historyId
   */
  public void setHistoryId(BHistoryId v) { set(historyId,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackHistoryEntry.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
      * Indentifies if the supplied component is point entry and whether it is the same one
      */
    public boolean is(BComponent component)
    {
        boolean res = false;

        if (component instanceof BNHaystackHistoryImport) 
        {
            BNHaystackHistoryImport imp = (BNHaystackHistoryImport) component;

            if (imp.getId().equals(getId()))
                res = true;
        }

        return res;
    }
}
