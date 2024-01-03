/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;


/**
 * Fluent helper to update an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class TestEntityOtherMultiLinkUpdateFluentHelper
    extends FluentHelperUpdate<TestEntityOtherMultiLinkUpdateFluentHelper, TestEntityOtherMultiLink>
{

    /**
     * {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be updated in the S/4HANA system.
     * 
     */
    private final TestEntityOtherMultiLink entity;

    /**
     * Creates a fluent helper object that will update a {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The TestEntityOtherMultiLink to take the updated values from.
     */
    public TestEntityOtherMultiLinkUpdateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final TestEntityOtherMultiLink entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected TestEntityOtherMultiLink getEntity() {
        return entity;
    }

}
