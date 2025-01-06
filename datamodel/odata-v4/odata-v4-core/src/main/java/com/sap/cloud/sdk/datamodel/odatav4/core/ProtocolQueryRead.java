/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import javax.annotation.Nonnull;

/**
 * Interface that allows for constructing OData read queries.
 *
 * @param <EntityT>
 *            The entity type to be queried.
 */
interface ProtocolQueryRead<EntityT extends VdmObject<?>>
{
    /**
     * Query modifier to limit which field values of the entity {@linkplain EntityT} get fetched and populated.
     * Navigational properties supplied here will be expanded. If this method is called at least once, then only the
     * specified fields will be fetched and populated.
     *
     * If none of the select methods is called, then all fields will be fetched and populated. Calling this multiple
     * times will combine the set(s) of fields of each call.
     *
     * @param fields
     *            Properties of {@linkplain EntityT} to be selected.
     * @return This request object with the added selections.
     */
    @Nonnull
    @SuppressWarnings( { "varargs", "unchecked" } )
    ProtocolQueryRead<EntityT> select( @Nonnull final Property<EntityT>... fields );
}
