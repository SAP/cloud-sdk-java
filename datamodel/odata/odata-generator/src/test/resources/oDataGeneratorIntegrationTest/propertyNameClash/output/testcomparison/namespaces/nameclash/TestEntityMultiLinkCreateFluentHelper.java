/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.nameclash;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class TestEntityMultiLinkCreateFluentHelper
    extends FluentHelperCreate<TestEntityMultiLinkCreateFluentHelper, TestEntityMultiLink>
{

    /**
     * {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity object that will be created in the S/4HANA system.
     * 
     */
    private final TestEntityMultiLink entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.nameclash.TestEntityMultiLink TestEntityMultiLink} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The TestEntityMultiLink to create.
     */
    public TestEntityMultiLinkCreateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final TestEntityMultiLink entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected TestEntityMultiLink getEntity() {
        return entity;
    }

}
