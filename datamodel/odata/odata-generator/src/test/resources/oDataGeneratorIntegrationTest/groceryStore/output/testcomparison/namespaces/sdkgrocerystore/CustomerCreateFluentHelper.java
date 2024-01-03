/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class CustomerCreateFluentHelper
    extends FluentHelperCreate<CustomerCreateFluentHelper, Customer>
{

    /**
     * {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity object that will be created in the S/4HANA system.
     * 
     */
    private final Customer entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The Customer to create.
     */
    public CustomerCreateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final Customer entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected Customer getEntity() {
        return entity;
    }

}
