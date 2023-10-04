/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import testcomparison.namespaces.metadata.SimplePerson;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>metadata</td></tr></table>
 * 
 */
public interface MetadataService {

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     * 
     */
    String DEFAULT_SERVICE_PATH = "/API_MINIMAL_TEST_CASE";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also adjusts the respective entity URLs.
     * 
     * @param servicePath
     *     Service path that will override the default.
     * @return
     *     A new service instance with the specified service path.
     */
    @Nonnull
    MetadataService withServicePath(
        @Nonnull
        final String servicePath);

    /**
     * Creates a batch request builder object.
     * 
     * @return
     *     A request builder to handle batch operation on this service. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder#execute(Destination) execute} method on the request builder object.
     */
    @Nonnull
    BatchRequestBuilder batch();

    /**
     * Fetch multiple {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.metadata.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<SimplePerson> getAllSimplePersons();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.metadata.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<SimplePerson> countSimplePersons();

    /**
     * Fetch a single {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity using key fields.
     * 
     * @param firstName
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @param lastName
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.metadata.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<SimplePerson> getSimplePersonsByKey(final String firstName, final String lastName);

    /**
     * Create a new {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.metadata.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<SimplePerson> createSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.metadata.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<SimplePerson> updateSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Deletes an existing {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity in the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.metadata.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.metadata.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<SimplePerson> deleteSimplePersons(
        @Nonnull
        final SimplePerson simplePerson);

}
