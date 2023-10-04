/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;


/**
 * Fluent helper to delete an existing {@link testcomparison.namespaces.test.Unrelated Unrelated} entity in the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class UnrelatedDeleteFluentHelper
    extends FluentHelperDelete<UnrelatedDeleteFluentHelper, Unrelated>
{

    /**
     * {@link testcomparison.namespaces.test.Unrelated Unrelated} entity object that will be deleted in the S/4HANA system.
     * 
     */
    private final Unrelated entity;

    /**
     * Creates a fluent helper object that will delete a {@link testcomparison.namespaces.test.Unrelated Unrelated} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     The entity collection to direct the update requests to.
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The Unrelated to delete from the endpoint.
     */
    public UnrelatedDeleteFluentHelper(
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
