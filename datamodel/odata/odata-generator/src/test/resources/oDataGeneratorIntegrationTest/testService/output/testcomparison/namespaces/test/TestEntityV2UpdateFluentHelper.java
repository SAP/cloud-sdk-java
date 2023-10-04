/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;


/**
 * Fluent helper to update an existing {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class TestEntityV2UpdateFluentHelper
    extends FluentHelperUpdate<TestEntityV2UpdateFluentHelper, TestEntityV2>
{

    /**
     * {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be updated in the S/4HANA system.
     * 
     */
    private final TestEntityV2 entity;

    /**
     * Creates a fluent helper object that will update a {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The TestEntityV2 to take the updated values from.
     */
    public TestEntityV2UpdateFluentHelper(
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
