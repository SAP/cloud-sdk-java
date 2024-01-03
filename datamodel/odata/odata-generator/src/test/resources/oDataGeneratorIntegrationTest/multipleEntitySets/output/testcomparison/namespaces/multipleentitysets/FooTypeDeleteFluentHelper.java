/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.multipleentitysets;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;


/**
 * Fluent helper to delete an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity in the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class FooTypeDeleteFluentHelper
    extends FluentHelperDelete<FooTypeDeleteFluentHelper, FooType>
{

    /**
     * {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be deleted in the S/4HANA system.
     * 
     */
    private final FooType entity;

    /**
     * Creates a fluent helper object that will delete a {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     The entity collection to direct the update requests to.
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The FooType to delete from the endpoint.
     */
    public FooTypeDeleteFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final FooType entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected FooType getEntity() {
        return entity;
    }

}
