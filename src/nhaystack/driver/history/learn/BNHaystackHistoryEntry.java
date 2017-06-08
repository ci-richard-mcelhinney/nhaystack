//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.history.learn;

import javax.baja.history.*;
import javax.baja.sys.*;

import nhaystack.*;
import nhaystack.driver.*;
import nhaystack.driver.history.*;

/**
  * BNHaystackHistoryEntry represents an object that was discovered
  * during a 'learn' in the BNHaystackHistoryImportManager.
  */
public final class BNHaystackHistoryEntry extends BComponent
{
    /*-
      class BNHaystackHistoryEntry
      {
          properties
          {
              id:   BHRef  default{[ BHRef.DEFAULT ]}
              importedTags: BHTags default{[ BHTags.DEFAULT ]}
              historyId: BHistoryId default{[ BHistoryId.DEFAULT ]}
          } 
      }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.learn.BNHaystackHistoryEntry(4001587975)1.0$ @*/
/* Generated Tue May 30 17:08:42 AEST 2017 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "id"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>id</code> property.
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#getId
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#setId
   */
  public static final Property id = newProperty(0, BHRef.DEFAULT,null);
  
  /**
   * Get the <code>id</code> property.
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#id
   */
  public BHRef getId() { return (BHRef)get(id); }
  
  /**
   * Set the <code>id</code> property.
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#id
   */
  public void setId(BHRef v) { set(id,v,null); }

////////////////////////////////////////////////////////////////
// Property "importedTags"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>importedTags</code> property.
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#getImportedTags
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#setImportedTags
   */
  public static final Property importedTags = newProperty(0, BHTags.DEFAULT,null);
  
  /**
   * Get the <code>importedTags</code> property.
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#importedTags
   */
  public BHTags getImportedTags() { return (BHTags)get(importedTags); }
  
  /**
   * Set the <code>importedTags</code> property.
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#importedTags
   */
  public void setImportedTags(BHTags v) { set(importedTags,v,null); }

////////////////////////////////////////////////////////////////
// Property "historyId"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>historyId</code> property.
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#getHistoryId
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#setHistoryId
   */
  public static final Property historyId = newProperty(0, BHistoryId.DEFAULT,null);
  
  /**
   * Get the <code>historyId</code> property.
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#historyId
   */
  public BHistoryId getHistoryId() { return (BHistoryId)get(historyId); }
  
  /**
   * Set the <code>historyId</code> property.
   * @see nhaystack.driver.history.learn.BNHaystackHistoryEntry#historyId
   */
  public void setHistoryId(BHistoryId v) { set(historyId,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackHistoryEntry.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

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
