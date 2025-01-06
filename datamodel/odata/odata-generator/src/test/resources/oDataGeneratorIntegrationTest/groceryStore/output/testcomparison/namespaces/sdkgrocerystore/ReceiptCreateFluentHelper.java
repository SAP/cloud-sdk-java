/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class ReceiptCreateFluentHelper
    extends FluentHelperCreate<ReceiptCreateFluentHelper, Receipt>
{

    /**
     * {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity object that will be created in the S/4HANA system.
     * 
     */
    private final Receipt entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The Receipt to create.
     */
    public ReceiptCreateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final Receipt entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected Receipt getEntity() {
        return entity;
    }

}
