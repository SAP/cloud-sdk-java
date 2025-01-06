/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import testcomparison.namespaces.sdkgrocerystore.selectable.OpeningHoursSelectable;


/**
 * Fluent helper to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class OpeningHoursFluentHelper
    extends FluentHelperRead<OpeningHoursFluentHelper, OpeningHours, OpeningHoursSelectable>
{


    /**
     * Creates a fluent helper using the specified service path and entity collection to send the read requests.
     * 
     * @param entityCollection
     *     The entity collection to direct the requests to.
     * @param servicePath
     *     The service path to direct the read requests to.
     */
    public OpeningHoursFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
    }

    @Override
    @Nonnull
    protected Class<OpeningHours> getEntityClass() {
        return OpeningHours.class;
    }

}
