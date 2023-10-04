/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.entitywithkeynamedfield;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import testcomparison.namespaces.entitywithkeynamedfield.selectable.EntityWithoutKeyLabelSelectable;


/**
 * Fluent helper to fetch multiple {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class EntityWithoutKeyLabelFluentHelper
    extends FluentHelperRead<EntityWithoutKeyLabelFluentHelper, EntityWithoutKeyLabel, EntityWithoutKeyLabelSelectable>
{


    /**
     * Creates a fluent helper using the specified service path and entity collection to send the read requests.
     * 
     * @param entityCollection
     *     The entity collection to direct the requests to.
     * @param servicePath
     *     The service path to direct the read requests to.
     */
    public EntityWithoutKeyLabelFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
    }

    @Override
    @Nonnull
    protected Class<EntityWithoutKeyLabel> getEntityClass() {
        return EntityWithoutKeyLabel.class;
    }

}
