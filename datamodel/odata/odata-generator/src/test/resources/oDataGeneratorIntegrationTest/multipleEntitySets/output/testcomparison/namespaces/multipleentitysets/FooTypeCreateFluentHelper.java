/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.multipleentitysets;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class FooTypeCreateFluentHelper
    extends FluentHelperCreate<FooTypeCreateFluentHelper, FooType>
{

    /**
     * {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be created in the S/4HANA system.
     * 
     */
    private final FooType entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The FooType to create.
     */
    public FooTypeCreateFluentHelper(
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
