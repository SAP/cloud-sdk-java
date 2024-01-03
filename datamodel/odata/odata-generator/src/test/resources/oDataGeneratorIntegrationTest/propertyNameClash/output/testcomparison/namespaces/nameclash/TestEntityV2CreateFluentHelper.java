/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.nameclash;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class TestEntityV2CreateFluentHelper
    extends FluentHelperCreate<TestEntityV2CreateFluentHelper, TestEntityV2>
{

    /**
     * {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity object that will be created in the S/4HANA system.
     * 
     */
    private final TestEntityV2 entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The TestEntityV2 to create.
     */
    public TestEntityV2CreateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final TestEntityV2 entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected TestEntityV2 getEntity() {
        return entity;
    }

}
