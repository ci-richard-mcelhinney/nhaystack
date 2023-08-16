//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   13 Mar 2019  Andrew Saunders  Creation based on class in haystack-rt
//

package nhaystack.server.tags;

import static nhaystack.server.tags.BNEquipRefRelation.getDirectRelation;
import static nhaystack.server.tags.BNEquipRefRelation.getEquip;
import static nhaystack.util.NHaystackConst.ID_EQUIP;
import static nhaystack.util.NHaystackConst.ID_SITE_REF;

import javax.baja.control.BControlPoint;
import javax.baja.data.BIDataValue;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.BRelation;
import javax.baja.sys.BString;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Entity;
import javax.baja.tag.Tag;
import javax.baja.tagdictionary.BTagInfo;
import javax.baja.tagdictionary.BTagRuleCondition;

import nhaystack.BHDict;
import nhaystack.NHRef;
import nhaystack.server.Nav;
import nhaystack.server.SpaceManager;
import nhaystack.server.TagManager;
import nhaystack.util.NHaystackConst;
import org.projecthaystack.HDict;
import com.tridium.tagdictionary.condition.BIsTypeCondition;

/**
 * BNIdTag is a smart tag whose value is the id value of a
 * controlPoint; the tag is not applied if its value is null.
 */
@NiagaraType
@NiagaraProperty(
    name = "validity",
    type = "BTagRuleCondition",
    defaultValue = "new BIsTypeCondition(BComponent.TYPE)",
    override = true
)
public class BNIdTag extends BTagInfo
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.tags.BNIdTag(2493110824)1.0$ @*/
/* Generated Fri Feb 15 18:35:33 EST 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "validity"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code validity} property.
   * @see #getValidity
   * @see #setValidity
   */
  public static final Property validity = newProperty(0, new BIsTypeCondition(BComponent.TYPE), null);
  
  /**
   * Get the {@code validity} property.
   * @see #validity
   */
  public BTagRuleCondition getValidity() { return (BTagRuleCondition)get(validity); }
  
  /**
   * Set the {@code validity} property.
   * @see #validity
   */
  public void setValidity(BTagRuleCondition v) { set(validity, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNIdTag.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNIdTag()
    {
    }

    /**
     * Get a tag whose value is the point's out value.  This tag only works for
     * entities that are instanceof BControlPoint.  Otherwise, this method
     * returns null.  If the BControlPoint's out status is null, this method
     * returns null.  Otherwise, if the BControlPoint's out value is a
     * BIDataValue, the returned tag's value will be that BIDataValue.
     * Otherwise, the returned tag's value will be a BString encoding of that
     * out value.
     *
     * @param entity The entity that the tag is for.
     * @return a tag whose value is the point's out value or null if the entity
     * is not a BControlPoint or its out status is null.
     */
    @Override
    public Tag getTag(Entity entity)
    {
        if (entity instanceof BComponent)
        {
            BComponent comp = (BComponent)entity;
            if (SpaceManager.isVisibleComponent(comp))
            {
                // for visible comps, init returnRef to component structure by
                // default
                NHRef returnRef = TagManager.makeSlotPathRef(comp);

                if (comp instanceof BControlPoint)
                {
                    BComponent equip = getEquip((BControlPoint) comp);
                    if (equip != null)
                    {
                        BComponent site = getSite(equip);
                        if (site != null)
                        {
                            returnRef = makePointRef(comp, equip, site);
                        }
                    }
                }
                else if (comp.tags().contains(ID_EQUIP))
                {
                    BComponent site = getSite(comp);
                    if (site != null)
                    {
                        returnRef = makeEquipRef(comp, site);
                    }
                }
                else if (comp.tags().contains(NHaystackConst.ID_SITE))
                {
                    returnRef = makeSiteRef(comp);
                }

                if (returnRef != null)
                {
                    return new Tag(getTagId(), BString.make(returnRef.getHRef().toString()));
                }
            }
        }

        return null;
    }

    private static BComponent getSite(BComponent equip)
    {
        BRelation siteRef = getDirectRelation(equip, ID_SITE_REF);
        if (siteRef != null)
        {
            return (BComponent) siteRef.getEndpoint();
        }
        return null;
    }

    private static String makeNavName(BComponent comp)
    {
        HDict compTags = BHDict.findTagAnnotation(comp);
        if (compTags == null) compTags = HDict.EMPTY;
        return Nav.makeNavName(comp, compTags);
    }

    private static NHRef makePointRef(BComponent pointComp, BComponent equipComp, BComponent siteComp)
    {
        return TagManager.makeSepRef(new String[] { makeNavName(siteComp), makeNavName(equipComp), makeNavName(pointComp)});
    }

    private static NHRef makeEquipRef(BComponent equipComp, BComponent siteComp)
    {
        return TagManager.makeSepRef(new String[] { makeNavName(siteComp), makeNavName(equipComp)});
    }

    private static NHRef makeSiteRef(BComponent siteComp)
    {
        return TagManager.makeSepRef(new String[] { makeNavName(siteComp)});
    }

    /**
     * The default value is an empty String.
     */
    @Override
    public BIDataValue getDefaultValue()
    {
        return BString.DEFAULT;
    }
}
