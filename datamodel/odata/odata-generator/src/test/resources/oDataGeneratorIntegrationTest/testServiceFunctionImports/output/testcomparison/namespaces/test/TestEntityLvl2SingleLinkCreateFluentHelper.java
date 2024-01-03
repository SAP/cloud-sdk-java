/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class TestEntityLvl2SingleLinkCreateFluentHelper
    extends FluentHelperCreate<TestEntityLvl2SingleLinkCreateFluentHelper, TestEntityLvl2SingleLink>
{

    /**
     * {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity object that will be created in the S/4HANA system.
     * 
     */
    private final TestEntityLvl2SingleLink entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The TestEntityLvl2SingleLink to create.
     */
    public TestEntityLvl2SingleLinkCreateFluentHelper(
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
