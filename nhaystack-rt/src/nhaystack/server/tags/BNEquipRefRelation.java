//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   11 Apr 2019  Eric Anderson  Creation based on class in haystack-rt
//
package nhaystack.server.tags;

import static nhaystack.util.NHaystackConst.ID_EQUIP;
import static nhaystack.util.NHaystackConst.ID_EQUIP_REF;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.baja.control.BControlPoint;
import javax.baja.control.ext.BNullProxyExt;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComplex;
import javax.baja.sys.BComponent;
import javax.baja.sys.BRelation;
import javax.baja.sys.Property;
import javax.baja.sys.SlotCursor;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.BasicRelation;
import javax.baja.tag.Entity;
import javax.baja.tag.Id;
import javax.baja.tag.Relation;
import javax.baja.tagdictionary.BRelationInfo;

import nhaystack.site.BHEquip;

/**
 * Implies a relation between a point and an ancestor component tagged with
 * hs:equip or a BHEquip within an ancestor component.
 */
@NiagaraType
public class BNEquipRefRelation extends BRelationInfo
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.tags.BNEquipRefRelation(2979906276)1.0$ @*/
/* Generated Sat Apr 13 17:11:43 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNEquipRefRelation.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    private static final Logger LOGGER = Logger.getLogger("nhaystack");

    /**
     * Get a relation to the ancestor component that has an hs:equip tag or a
     * BHEquip component within an ancestor. The entity must be a BControlPoint.
     */
    @Override
    public Optional<Relation> getRelation(Entity point)
    {
        if (!(point instanceof BControlPoint))
        {
            return Optional.empty();
        }

        Id relationId = getRelationId();
        if (!hasDirectRelation((BComponent) point, relationId))
        {
            BComponent equip = getImpliedEquip((BControlPoint) point);
            if (equip != null)
            {
                return Optional.of(new BasicRelation(relationId, equip, Relation.OUTBOUND));
            }
        }

        return Optional.empty();
    }

    /**
     * If the entity has an hs:equip tag and does not contain a BHEquip, add an
     * inbound relation from all descendant BControlPoints if the entity is the
     * nearest ancestor of that BControlPoint with an hs:equip tag. If the given
     * entity is a BHEquip, add an inbound relation from all sibling
     * BControlPoints or BControlPoints descendants of siblings. BControlPoints
     * with a direct hs:equipRef relation are skipped.
     * <p/>
     * If the entity is a BControlPoint, add an outbound relation based on
     * {@link #getRelation(Entity)}.
     */
    @Override
    public void addRelations(Entity entity, Collection<Relation> relations)
    {
        // Add the outbound relation to a BControlPoint
        getRelation(entity).ifPresent(relations::add);

        if (entity instanceof BHEquip)
        {
            if (isPrimaryHEquip((BHEquip) entity))
            {
                // Traverse descendants starting with the parent component so
                // siblings of this BHEquip will be included.
                BComponent start = ((BHEquip) entity).getParent().asComponent();
                relateFromDescendantPoints(start, relations);

                // Traverse descendants starting of this BHEquip, too.
                relateFromDescendantPoints((BHEquip) entity, relations);
            }
        }
        else if (entity instanceof BComponent && hasEquipTag(entity) &&
                 !hasHEquipChild((BComponent) entity))
        {
            // Give preference to the BHEquip component even if this component
            // has an hs:equip tag.
            // Wait for the BHEquip child to be handled so relations are made to
            // it.
            relateFromDescendantPoints((BComponent) entity, relations);
        }
    }

    private static BComponent getImpliedEquip(BControlPoint point)
    {
        if (hasNullProxyExt(point))
        {
            return null;
        }

        BComplex parent = point.getParent();
        while (parent != null)
        {
            BHEquip hEquip = getHEquipChild(parent.asComponent());
            if (hEquip != null)
            {
                return hEquip;
            }

            if (hasEquipTag(parent.asComponent()))
            {
                return parent.asComponent();
            }

            parent = parent.getParent();
        }

        return null;
    }

    private static boolean hasNullProxyExt(BControlPoint point)
    {
        return point.getProxyExt() instanceof BNullProxyExt;
    }

    static BComponent getEquip(BControlPoint point)
    {
        BRelation equipRelation = getDirectRelation(point, ID_EQUIP_REF);
        if (equipRelation != null)
        {
            return (BComponent) equipRelation.getEndpoint();
        }
        else
        {
            return getImpliedEquip(point);
        }
    }

    private void relateFromDescendantPoints(BComponent component, Collection<Relation> relations)
    {
        SlotCursor<Property> children = component.getProperties();
        while (children.nextComponent())
        {
            BComponent child = (BComponent) children.get();
            if (child instanceof BControlPoint &&
                !hasNullProxyExt((BControlPoint) child) &&
                !hasDirectRelation(child, getRelationId()))
            {
                relations.add(new BasicRelation(getRelationId(), child, Relation.INBOUND));
            }

            if (!hasEquipTag(child) && !hasHEquipChild(child))
            {
                relateFromDescendantPoints(child, relations);
            }
        }
    }

    static boolean hasEquipTag(Entity entity)
    {
        return entity.tags().contains(ID_EQUIP);
    }

    private static boolean hasHEquipChild(BComponent component)
    {
        return getHEquipChild(component) != null;
    }

    private static BHEquip getHEquipChild(BComponent component)
    {
        BHEquip[] hEquips = component.getChildren(BHEquip.class);
        if (hEquips.length == 1)
        {
            return hEquips[0];
        }

        if (hEquips.length > 1)
        {
            if (LOGGER.isLoggable(Level.FINE))
            {
                LOGGER.fine("Component " + component.getSlotPath() + " has more than 1 BHEquip child");
            }

            for (BHEquip hEquip : hEquips)
            {
                if (isNamedEquip(hEquip))
                {
                    return hEquip;
                }
            }

            return hEquips[0];
        }

        return null;
    }

    private static boolean isPrimaryHEquip(BHEquip hEquip)
    {
        if (isNamedEquip(hEquip))
        {
            return true;
        }

        return hEquip == getHEquipChild(hEquip.getParent().asComponent());
    }

    private static boolean isNamedEquip(BHEquip hEquip)
    {
        return "equip".equals(hEquip.getName());
    }

    static boolean hasDirectRelation(BComponent component, Id id)
    {
        return getDirectRelation(component, id) != null;
    }

    static BRelation getDirectRelation(BComponent component, Id id)
    {
        SlotCursor<Property> relations = component.getProperties();
        while (relations.next(BRelation.class))
        {
            BRelation relation = (BRelation) relations.get();
            if (relation.getId().equals(id) && relation.isOutbound())
            {
                return relation;
            }
        }

        return null;
    }
}
