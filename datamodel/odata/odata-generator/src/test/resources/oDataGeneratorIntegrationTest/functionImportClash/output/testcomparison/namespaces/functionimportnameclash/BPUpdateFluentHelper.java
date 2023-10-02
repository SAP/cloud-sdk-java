/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.functionimportnameclash;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;


/**
 * Fluent helper to update an existing {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class BPUpdateFluentHelper
    extends FluentHelperUpdate<BPUpdateFluentHelper, BP>
{

    /**
     * {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity object that will be updated in the S/4HANA system.
     * 
     */
    private final BP entity;

    /**
     * Creates a fluent helper object that will update a {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The BP to take the updated values from.
     */
    public BPUpdateFluentHelper(
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
