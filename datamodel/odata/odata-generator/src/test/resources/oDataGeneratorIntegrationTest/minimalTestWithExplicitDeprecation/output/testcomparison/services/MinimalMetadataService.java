/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
import testcomparison.namespaces.minimalmetadata.SimplePerson;
import testcomparison.namespaces.minimalmetadata.SimplePersonByKeyFluentHelper;
import testcomparison.namespaces.minimalmetadata.SimplePersonCreateFluentHelper;
import testcomparison.namespaces.minimalmetadata.SimplePersonDeleteFluentHelper;
import testcomparison.namespaces.minimalmetadata.SimplePersonFluentHelper;
import testcomparison.namespaces.minimalmetadata.SimplePersonUpdateFluentHelper;
import testcomparison.namespaces.minimalmetadata.batch.MinimalMetadataServiceBatch;


/**
 * <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/minimal_metadata?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>minimal_metadata</td></tr></table>
 * 
 * @deprecated
 *     The service and all its related classes are deprecated. This is a custom deprecation message.
 */
@Deprecated
public interface MinimalMetadataService
    extends BatchService<MinimalMetadataServiceBatch>
{

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     * 
     */
    String DEFAULT_SERVICE_PATH = "/some/path/SOME_API";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also adjusts the respective entity URLs.
     * 
     * @param servicePath
     *     Service path that will override the default.
     * @return
     *     A new service instance with the specified service path.
     */
    @Nonnull
    MinimalMetadataService withServicePath(
        @Nonnull
        final String servicePath);

    /**
     * Fetch multiple {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entities.
     * 
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.minimalmetadata.SimplePersonFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    SimplePersonFluentHelper getAllSimplePerson();

    /**
     * Fetch a single {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity using key fields.
     * 
     * @param person
     *     Person Summary.<p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.minimalmetadata.SimplePersonByKeyFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    SimplePersonByKeyFluentHelper getSimplePersonByKey(final String person);

    /**
     * Create a new {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity. To perform execution, call the {@link testcomparison.namespaces.minimalmetadata.SimplePersonCreateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    SimplePersonCreateFluentHelper createSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity. To perform execution, call the {@link testcomparison.namespaces.minimalmetadata.SimplePersonUpdateFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    SimplePersonUpdateFluentHelper updateSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Deletes an existing {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity in the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity. To perform execution, call the {@link testcomparison.namespaces.minimalmetadata.SimplePersonDeleteFluentHelper#execute execute} method on the fluent helper object. 
     */
    @Nonnull
    SimplePersonDeleteFluentHelper deleteSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

}
