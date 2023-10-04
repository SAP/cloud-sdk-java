/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;


/**
 * Fluent helper to update an existing {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class TestEntityLvl2SingleLinkUpdateFluentHelper
    extends FluentHelperUpdate<TestEntityLvl2SingleLinkUpdateFluentHelper, TestEntityLvl2SingleLink>
{

    /**
     * {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be updated in the S/4HANA system.
     * 
     */
    private final TestEntityLvl2SingleLink entity;

    /**
     * Creates a fluent helper object that will update a {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The TestEntityLvl2SingleLink to take the updated values from.
     */
    public TestEntityLvl2SingleLinkUpdateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final TestEntityLvl2SingleLink entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected TestEntityLvl2SingleLink getEntity() {
        return entity;
    }

}
