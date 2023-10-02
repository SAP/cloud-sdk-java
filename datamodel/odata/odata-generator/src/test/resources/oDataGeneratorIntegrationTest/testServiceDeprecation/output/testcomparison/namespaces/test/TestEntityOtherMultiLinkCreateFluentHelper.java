/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class TestEntityOtherMultiLinkCreateFluentHelper
    extends FluentHelperCreate<TestEntityOtherMultiLinkCreateFluentHelper, TestEntityOtherMultiLink>
{

    /**
     * {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be created in the S/4HANA system.
     * 
     */
    private final TestEntityOtherMultiLink entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The TestEntityOtherMultiLink to create.
     */
    public TestEntityOtherMultiLinkCreateFluentHelper(
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
