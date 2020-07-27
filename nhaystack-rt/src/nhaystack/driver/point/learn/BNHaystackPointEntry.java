//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy     Creation
//   08 May 2018  Eric Anderson  Migrated to slot annotations, added missing @Overrides annotations

package nhaystack.driver.point.learn;

import javax.baja.control.BControlPoint;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import nhaystack.BHRef;
import nhaystack.driver.BHTags;
import nhaystack.driver.point.BNHaystackProxyExt;

/**
  * BNHaystackPointEntry represents an object that was discovered
  * during a 'learn' in the BNHaystackPointManager.
  */
@NiagaraType
@NiagaraProperty(
  name = "facets",
  type = "BFacets",
  defaultValue = "BFacets.DEFAULT"
)
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
public final class BNHaystackPointEntry extends BComponent
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.learn.BNHaystackPointEntry(114880829)1.0$ @*/
/* Generated Fri Nov 17 11:59:00 EST 2017 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "facets"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code facets} property.
   * @see #getFacets
   * @see #setFacets
   */
  public static final Property facets = newProperty(0, BFacets.DEFAULT, null);
  
  /**
   * Get the {@code facets} property.
   * @see #facets
   */
  public BFacets getFacets() { return (BFacets)get(facets); }
  
  /**
   * Set the {@code facets} property.
   * @see #facets
   */
  public void setFacets(BFacets v) { set(facets, v, null); }

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
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNHaystackPointEntry.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

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

    public String  getKind()     { return getImportedTags().getDict().getStr("kind");  }
    public boolean getWritable() { return getImportedTags().getDict().has("writable"); }
}
