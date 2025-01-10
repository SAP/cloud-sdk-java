/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import testcomparison.namespaces.test.selectable.TestEntityV2Selectable;


/**
 * Fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class TestEntityV2FluentHelper
    extends FluentHelperRead<TestEntityV2FluentHelper, TestEntityV2, TestEntityV2Selectable>
{


    /**
     * Creates a fluent helper using the specified service path and entity collection to send the read requests.
     * 
     * @param entityCollection
     *     The entity collection to direct the requests to.
     * @param servicePath
     *     The service path to direct the read requests to.
     */
    public TestEntityV2FluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
    }

    @Override
    @Nonnull
    protected Class<TestEntityV2> getEntityClass() {
        return TestEntityV2 .class;
    }

}
