//
// Copyright 2019 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   11 Apr 2019  Eric Anderson  Creation based on class in haystack-rt
//
package nhaystack.server.tags;

import static nhaystack.server.tags.BNEquipRefRelation.getEquip;
import static nhaystack.server.tags.BNEquipRefRelation.hasDirectRelation;
import static nhaystack.server.tags.BNEquipRefRelation.hasEquipTag;
import static nhaystack.util.NHaystackConst.ID_EQUIP_REF;
import static nhaystack.util.NHaystackConst.ID_SITE;

import java.util.Collection;
import java.util.Optional;
import javax.baja.control.BControlPoint;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.BComponent;
import javax.baja.sys.RelationKnob;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.BasicRelation;
import javax.baja.tag.Entity;
import javax.baja.tag.Id;
import javax.baja.tag.Relation;
import javax.baja.tag.Relations;
import javax.baja.tagdictionary.BRelationInfo;

/**
 * If a point has a direct or implied equip (see {@link BNEquipRefRelation}) and
 * that equip has a direct hs:siteRef relation, a relation is implied between
 * the point and that hs:siteRef endpoint.
 */
@NiagaraType
public class BNSiteRefRelation extends BRelationInfo
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $nhaystack.server.tags.BNSiteRefRelation(2979906276)1.0$ @*/
/* Generated Sat Apr 13 17:11:43 EDT 2019 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BNSiteRefRelation.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
     * Get a relation to the direct hs:siteRef endpoint of the direct or implied
     * equip component (see {@link BNEquipRefRelation}).  The entity must be a
     * BControlPoint.
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
            BComponent equip = getEquip((BControlPoint) point);
            if (equip != null)
            {
                Relation siteRef = getRelation(equip, relationId);
                if (siteRef != null)
                {
                    return Optional.of(new BasicRelation(relationId, siteRef.getEndpoint(), Relation.OUTBOUND));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * If the entity has an hs:site tag, add an inbound relation to each
     * BControlPoint descendant of an equip component related to the entity
     * unless the point has a direct hs:siteRef relation.
     * <p/>
     * If the entity is a BControlPoint, add an outbound relation based on
     * {@link #getRelation(Entity)}.
     */
    @Override
    public void addRelations(Entity entity, Collection<Relation> relations)
    {
        // Add the outbound relation to a BControlPoint
        getRelation(entity).ifPresent(relations::add);

        // If equip entity found, it will add an inbound hs:siteRef from each of the equip's descendant
        // BControlPoints if the BControlPoint doesn't have a direct hs:siteRef relation.
        String relationQName = getRelationId().getQName();
        if (entity instanceof BComponent && hasSiteTag(entity))
        {
            // Traverse any direct inbound hs:equipRef relations.
            for (RelationKnob relationKnob : ((BComponent)entity).getRelationKnobs())
            {
                if (relationQName.equals(relationKnob.getRelationId()))
                {
                    BComponent equip = relationKnob.getRelationComponent();
                    if (hasEquipTag(equip))
                    {
                        addRelationsToEquipPoints(equip, relations);
                    }
                }
            }
        }
    }

    private void addRelationsToEquipPoints(BComponent equip, Collection<Relation> relations)
    {
        for (Relation relation : equip.relations().getAll(ID_EQUIP_REF, Relations.IN))
        {
            Entity endpoint = relation.getEndpoint();
            if (endpoint instanceof BControlPoint)
            {
                BControlPoint point = (BControlPoint) endpoint;
                if (!hasDirectRelation(point, getRelationId()))
                {
                    relations.add(new BasicRelation(getRelationId(), point, Relation.INBOUND));
                }
            }
        }
    }

    private static boolean hasSiteTag(Entity entity)
    {
        return entity.tags().contains(ID_SITE);
    }

    private static Relation getRelation(BComponent component, Id id)
    {
        for (Relation relation : component.relations())
        {
            if (relation.getId().equals(id))
            {
                return relation;
            }
        }
        return null;
    }
}
