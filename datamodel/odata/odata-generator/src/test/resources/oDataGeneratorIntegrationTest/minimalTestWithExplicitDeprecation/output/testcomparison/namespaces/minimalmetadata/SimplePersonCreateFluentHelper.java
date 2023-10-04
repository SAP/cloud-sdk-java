/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.minimalmetadata;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class SimplePersonCreateFluentHelper
    extends FluentHelperCreate<SimplePersonCreateFluentHelper, SimplePerson>
{

    /**
     * {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * 
     */
    private final SimplePerson entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The SimplePerson to create.
     */
    public SimplePersonCreateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final SimplePerson entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected SimplePerson getEntity() {
        return entity;
    }

}
