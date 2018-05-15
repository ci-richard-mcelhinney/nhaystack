//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   07 May 2018  Eric Anderson  Migrated to slot annotations
package nhaystack.driver.history.learn;

import javax.baja.history.BHistoryId;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHRef;
import nhaystack.driver.BHTags;
import nhaystack.driver.history.BNHaystackHistoryImport;

/**
  * BNHaystackHistoryEntry represents an object that was discovered
  * during a 'learn' in the BNHaystackHistoryImportManager.
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
  defaultValue = "BHTags.DEFAULT"
)
@NiagaraProperty(
  name = "historyId",
  type = "BHistoryId",
  defaultValue = "BHistoryId.DEFAULT"
)
public final class BNHaystackHistoryEntry extends BComponent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.history.learn.BNHaystackHistoryEntry(1279932463)1.0$ @*/
/* Generated Fri Nov 17 11:46:38 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

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
  public static final Property importedTags = newProperty(0, BHTags.DEFAULT, null);
  
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
// Property "historyId"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code historyId} property.
   * @see #getHistoryId
   * @see #setHistoryId
   */
  public static final Property historyId = newProperty(0, BHistoryId.DEFAULT, null);
  
  /**
   * Get the {@code historyId} property.
   * @see #historyId
   */
  public BHistoryId getHistoryId() { return (BHistoryId)get(historyId); }
  
  /**
   * Set the {@code historyId} property.
   * @see #historyId
   */
  public void setHistoryId(BHistoryId v) { set(historyId, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
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
