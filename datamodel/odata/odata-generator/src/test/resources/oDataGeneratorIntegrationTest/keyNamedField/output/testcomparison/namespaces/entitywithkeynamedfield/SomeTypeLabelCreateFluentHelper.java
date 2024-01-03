/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.entitywithkeynamedfield;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class SomeTypeLabelCreateFluentHelper
    extends FluentHelperCreate<SomeTypeLabelCreateFluentHelper, SomeTypeLabel>
{

    /**
     * {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity object that will be created in the S/4HANA system.
     * 
     */
    private final SomeTypeLabel entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The SomeTypeLabel to create.
     */
    public SomeTypeLabelCreateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final SomeTypeLabel entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected SomeTypeLabel getEntity() {
        return entity;
    }

}
