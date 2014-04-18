//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   14 Apr 2014  Mike Jarmy  Creation

package nhaystack.driver.point.learn;

import javax.baja.sys.*;
import javax.baja.control.*;  

import nhaystack.*;
import nhaystack.driver.*;
import nhaystack.driver.point.*;

/**
  * BNHaystackPointEntry represents an object that was discovered
  * during a 'learn' in the BNHaystackPointManager.
  */
public final class BNHaystackPointEntry extends BComponent
{
    /*-
      class BNHaystackPointEntry
      {
          properties
          {
              facets: BFacets default {[ BFacets.DEFAULT ]}
              id: BHRef default{[ BHRef.DEFAULT ]}
              importedTags: BHTags default{[ BHTags.DEFAULT ]}
          } 
      }
    -*/
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.driver.point.learn.BNHaystackPointEntry(1085006972)1.0$ @*/
/* Generated Thu Apr 10 15:56:03 EDT 2014 by Slot-o-Matic 2000 (c) Tridium, Inc. 2000 */

////////////////////////////////////////////////////////////////
// Property "facets"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>facets</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#getFacets
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#setFacets
   */
  public static final Property facets = newProperty(0, BFacets.DEFAULT,null);
  
  /**
   * Get the <code>facets</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#facets
   */
  public BFacets getFacets() { return (BFacets)get(facets); }
  
  /**
   * Set the <code>facets</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#facets
   */
  public void setFacets(BFacets v) { set(facets,v,null); }

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
// Property "importedTags"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the <code>importedTags</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#getImportedTags
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#setImportedTags
   */
  public static final Property importedTags = newProperty(0, BHTags.DEFAULT,null);
  
  /**
   * Get the <code>importedTags</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#importedTags
   */
  public BHTags getImportedTags() { return (BHTags)get(importedTags); }
  
  /**
   * Set the <code>importedTags</code> property.
   * @see nhaystack.driver.point.learn.BNHaystackPointEntry#importedTags
   */
  public void setImportedTags(BHTags v) { set(importedTags,v,null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
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
