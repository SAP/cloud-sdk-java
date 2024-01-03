/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.minimalmetadata;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;


/**
 * Fluent helper to delete an existing {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity in the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class SimplePersonDeleteFluentHelper
    extends FluentHelperDelete<SimplePersonDeleteFluentHelper, SimplePerson>
{

    /**
     * {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * 
     */
    private final SimplePerson entity;

    /**
     * Creates a fluent helper object that will delete a {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     The entity collection to direct the update requests to.
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The SimplePerson to delete from the endpoint.
     */
    public SimplePersonDeleteFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final SimplePerson entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected SimplePerson getEntity() {
        return entity;
    }

}
