package com.sap.cloud.sdk.datamodel.odata.helper;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * OData selector for entities to select each and every field, i.e. star selector. Instances of this object are used in
 * query modifier methods of the entity fluent helpers. Methods to compare with provided values are not supported.
 *
 * @param <EntityT>
 *            VdmObject that the field belongs to
 */
public class EntityFieldAll<EntityT extends VdmObject<?>> implements EntitySelectable<EntityT>
{
    @Nonnull
    @Override
    public String getFieldName()
    {
        return "*";
    }

    @Nonnull
    @Override
    public List<String> getSelections()
    {
        return Collections.singletonList("*");
    }

    @Nonnull
    @Override
    public String toString()
    {
        return "*";
    }

    @Override
    public boolean equals( @Nullable final Object o )
    {
        return o instanceof EntitySelectable && ((EntitySelectable<?>) o).getSelections().equals(getSelections());
    }

    @Override
    public int hashCode()
    {
        return getSelections().hashCode();
    }
}
