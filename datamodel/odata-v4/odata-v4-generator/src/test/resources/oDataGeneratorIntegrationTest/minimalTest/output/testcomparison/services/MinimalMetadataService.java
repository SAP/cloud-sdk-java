package testcomparison.services;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import testcomparison.namespaces.minimalmetadata.SimplePerson;


/**
 * <p>You can use this inbound synchronous service to create, read and update.</p><p><a href='https://sap.com'>Business Documentation</a></p><p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/minimal_metadata?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>minimal_metadata</td></tr><tr><td align='right'>API Version:</td><td>1 </td></tr><tr><td align='right'>Communication Scenario:</td><td>minimal metadata</td></tr><tr><td align='right'>Scope Items:</td><td><a href='[API_BUSINESS_PARTNER_Entities](https://sap.com)'>[API_BUSINESS_PARTNER_Entities](https://sap.com)</a></td></tr><tr><td align='right'>Authentication Methods:</td><td>Basic, x509</td></tr><tr><td align='right'>Service Group Name:</td><td>API_BUSINESS_PARTNER</td></tr><tr><td align='right'>Business Object:</td><td>SimplePerson</td></tr></table>
 *
 */
public interface MinimalMetadataService {

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     *
     */
    String DEFAULT_SERVICE_PATH = "/";

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
     * Creates a batch request builder object.
     *
     * @return
     *     A request builder to handle batch operation on this service. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder#execute(Destination) execute} method on the request builder object.
     */
    @Nonnull
    BatchRequestBuilder batch();

    /**
     * Fetch multiple {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entities.
     *
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.minimalmetadata.SimplePerson>#execute execute} method on the request builder object.
     */
    @Nonnull
    GetAllRequestBuilder<SimplePerson> getAllSimplePerson();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity collection matching the filter and search expressions.
     *
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.minimalmetadata.SimplePerson>#execute execute} method on the request builder object.
     */
    @Nonnull
    CountRequestBuilder<SimplePerson> countSimplePerson();

    /**
     * Fetch a single {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity using key fields.
     *
     * @param person
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.minimalmetadata.SimplePerson>#execute execute} method on the request builder object.
     */
    @Nonnull
    GetByKeyRequestBuilder<SimplePerson> getSimplePersonByKey(final String person);

    /**
     * Create a new {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     *
     * @param simplePerson
     *     {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.minimalmetadata.SimplePerson>#execute execute} method on the request builder object.
     */
    @Nonnull
    CreateRequestBuilder<SimplePerson> createSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     *
     * @param simplePerson
     *     {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.minimalmetadata.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.minimalmetadata.SimplePerson>#execute execute} method on the request builder object.
     */
    @Nonnull
    UpdateRequestBuilder<SimplePerson> updateSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

}
