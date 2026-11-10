package com.sap.cloud.sdk.datamodel.odata.helper;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

/**
 * Interface to enable management of OData entity selectors. This interface is used by
 * {@link com.sap.cloud.sdk.datamodel.odata.helper.EntityField EntityField} and
 * {@link com.sap.cloud.sdk.datamodel.odata.helper.EntityLink EntityLink}.
 *
 * @param <EntityT>
 *            The generic entity type.
 */
public interface EntitySelectable<EntityT>
{
    /**
     * Get the field name of OData entity property.
     *
     * @return The field name
     */
    @Nonnull
    String getFieldName();

    /**
     * Get the select expression that represents the OData entity property.
     *
     * @return The serialized select terms.
     */
    @Nonnull
    default List<String> getSelections()
    {
        return Lists.newArrayList(getFieldName());
    }
}
