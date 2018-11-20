//
// Copyright 2018 Tridium, Inc. All Rights Reserved.
// Licensed under the Academic Free License version 3.0
//
// History:
//   31 Oct 2018  Andrew Saunders  Creation based on class in haystack-rt
//

package nhaystack.server.tags;

import javax.baja.control.BControlPoint;
import javax.baja.data.BIDataValue;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatusValue;
import javax.baja.sys.BString;
import javax.baja.sys.BValue;
import javax.baja.sys.Property;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.tag.Entity;
import javax.baja.tag.Id;
import javax.baja.tag.Tag;
import javax.baja.tagdictionary.BTagInfo;
import javax.baja.tagdictionary.BTagRuleCondition;

import com.tridium.tagdictionary.condition.BIsTypeCondition;

/**
 * BNCurValTag is a smart tag whose value is the current value of a
 * controlPoint; the tag is not applied if its value is null.
 */
@NiagaraType
@NiagaraProperty(
    name = "validity",
    type = "BTagRuleCondition",
    defaultValue = "new BIsTypeCondition(BControlPoint.TYPE)"
)
public class BNCurValTag extends BTagInfo
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $com.tridium.haystack.BNCurValTag(2054420547)1.0$ @*/
/* Generated Mon Oct 08 16:08:36 EDT 2018 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "validity"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code validity} property.
   * @see #getValidity
   * @see #setValidity
   */
  public static final Property validity = newProperty(0, new BIsTypeCondition(BControlPoint.TYPE), null);
  
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
  public static final Type TYPE = Sys.loadType(BNCurValTag.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public BNCurValTag()
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
        if (entity instanceof BControlPoint)
        {
            BStatusValue statusValue = ((BControlPoint)entity).getOutStatusValue();
            return makeTagForStatusValue(getTagId(), statusValue);
        }
        return null;
    }

    /**
     * The default value is an empty String.
     */
    @Override
    public BIDataValue getDefaultValue()
    {
        return BString.DEFAULT;
    }

    public static Tag makeTagForStatusValue(Id tagId, BStatusValue statusValue)
    {
        if (statusValue.getStatus().isNull())
        {
            return null;
        }
        else
        {
            BValue value = statusValue.getValueValue();
            return value instanceof BIDataValue ?
                new Tag(tagId, (BIDataValue)value) :
                new Tag(tagId, BString.make(value.toString(null)));
        }
    }
}
