/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;


/**
 * Fluent helper to update an existing {@link testcomparison.namespaces.test.Unrelated Unrelated} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class UnrelatedUpdateFluentHelper
    extends FluentHelperUpdate<UnrelatedUpdateFluentHelper, Unrelated>
{

    /**
     * {@link testcomparison.namespaces.test.Unrelated Unrelated} entity object that will be updated in the S/4HANA system.
     * 
     */
    private final Unrelated entity;

    /**
     * Creates a fluent helper object that will update a {@link testcomparison.namespaces.test.Unrelated Unrelated} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The Unrelated to take the updated values from.
     */
    public UnrelatedUpdateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final Unrelated entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected Unrelated getEntity() {
        return entity;
    }

}
