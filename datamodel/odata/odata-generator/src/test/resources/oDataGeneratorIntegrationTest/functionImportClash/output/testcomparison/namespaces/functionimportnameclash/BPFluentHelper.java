/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.functionimportnameclash;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import testcomparison.namespaces.functionimportnameclash.selectable.BPSelectable;


/**
 * Fluent helper to fetch multiple {@link testcomparison.namespaces.functionimportnameclash.BP BP} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class BPFluentHelper
    extends FluentHelperRead<BPFluentHelper, BP, BPSelectable>
{


    /**
     * Creates a fluent helper using the specified service path and entity collection to send the read requests.
     * 
     * @param entityCollection
     *     The entity collection to direct the requests to.
     * @param servicePath
     *     The service path to direct the read requests to.
     */
    public BPFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
    }

    @Override
    @Nonnull
    protected Class<BP> getEntityClass() {
        return BP.class;
    }

}
