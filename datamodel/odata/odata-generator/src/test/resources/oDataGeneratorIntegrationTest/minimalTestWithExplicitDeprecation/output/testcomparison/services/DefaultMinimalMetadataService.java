/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
import testcomparison.namespaces.minimalmetadata.SimplePerson;
import testcomparison.namespaces.minimalmetadata.SimplePersonByKeyFluentHelper;
import testcomparison.namespaces.minimalmetadata.SimplePersonCreateFluentHelper;
import testcomparison.namespaces.minimalmetadata.SimplePersonDeleteFluentHelper;
import testcomparison.namespaces.minimalmetadata.SimplePersonFluentHelper;
import testcomparison.namespaces.minimalmetadata.SimplePersonUpdateFluentHelper;
import testcomparison.namespaces.minimalmetadata.batch.DefaultMinimalMetadataServiceBatch;


/**
 * <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/minimal_metadata?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>minimal_metadata</td></tr></table>
 * 
 * @deprecated
 *     The service and all its related classes are deprecated. This is a custom deprecation message.
 */
@Deprecated
public class DefaultMinimalMetadataService
    implements MinimalMetadataService
{

    @Nonnull
    private final String servicePath;

    /**
     * Creates a service using {@link MinimalMetadataService#DEFAULT_SERVICE_PATH} to send the requests.
     * 
     */
    public DefaultMinimalMetadataService() {
        servicePath = MinimalMetadataService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     * 
     */
    private DefaultMinimalMetadataService(
        @Nonnull
        final String servicePath) {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultMinimalMetadataService withServicePath(
        @Nonnull
        final String servicePath) {
        return new DefaultMinimalMetadataService(servicePath);
    }

    @Override
    @Nonnull
    public DefaultMinimalMetadataServiceBatch batch() {
        return new DefaultMinimalMetadataServiceBatch(this, servicePath);
    }

    @Override
    @Nonnull
    public SimplePersonFluentHelper getAllSimplePerson() {
        return new SimplePersonFluentHelper(servicePath, "A_SimplePerson");
    }

    @Override
    @Nonnull
    public SimplePersonByKeyFluentHelper getSimplePersonByKey(final String person) {
        return new SimplePersonByKeyFluentHelper(servicePath, "A_SimplePerson", person);
    }

    @Override
    @Nonnull
    public SimplePersonCreateFluentHelper createSimplePerson(
        @Nonnull
        final SimplePerson simplePerson) {
        return new SimplePersonCreateFluentHelper(servicePath, simplePerson, "A_SimplePerson");
    }

    @Override
    @Nonnull
    public SimplePersonUpdateFluentHelper updateSimplePerson(
        @Nonnull
        final SimplePerson simplePerson) {
        return new SimplePersonUpdateFluentHelper(servicePath, simplePerson, "A_SimplePerson");
    }

    @Override
    @Nonnull
    public SimplePersonDeleteFluentHelper deleteSimplePerson(
        @Nonnull
        final SimplePerson simplePerson) {
        return new SimplePersonDeleteFluentHelper(servicePath, simplePerson, "A_SimplePerson");
    }

}
