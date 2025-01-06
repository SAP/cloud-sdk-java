/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.functionimportnameclash;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class BPCreateFluentHelper
    extends FluentHelperCreate<BPCreateFluentHelper, BP>
{

    /**
     * {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity object that will be created in the S/4HANA system.
     * 
     */
    private final BP entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The BP to create.
     */
    public BPCreateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final BP entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected BP getEntity() {
        return entity;
    }

}
