/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import testcomparison.namespaces.actionsandfunctions.FunctionResult;
import testcomparison.namespaces.actionsandfunctions.NewComplexResult;
import testcomparison.namespaces.actionsandfunctions.SimplePerson;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>actionsAndFunctions</td></tr></table>
 * 
 */
public interface ActionsAndFunctionsService {

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     * 
     */
    String DEFAULT_SERVICE_PATH = "/tests/actionsAndFunctions/API_ACTIONS_FUNCTIONS_TEST_CASE";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also adjusts the respective entity URLs.
     * 
     * @param servicePath
     *     Service path that will override the default.
     * @return
     *     A new service instance with the specified service path.
     */
    @Nonnull
    ActionsAndFunctionsService withServicePath(
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
     * Fetch multiple {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entities.
     * 
     * @return
     *     A request builder to fetch multiple {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder<testcomparison.namespaces.actionsandfunctions.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetAllRequestBuilder<SimplePerson> getAllSimplePerson();

    /**
     * Fetch the number of entries from the {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity collection matching the filter and search expressions.
     * 
     * @return
     *     A request builder to fetch the count of {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entities. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder<testcomparison.namespaces.actionsandfunctions.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CountRequestBuilder<SimplePerson> countSimplePerson();

    /**
     * Fetch a single {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity using key fields.
     * 
     * @param person
     *     <p>Constraints: Not nullable, Maximum length: 10</p>
     * @return
     *     A request builder to fetch a single {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity using key fields. This request builder allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder<testcomparison.namespaces.actionsandfunctions.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    GetByKeyRequestBuilder<SimplePerson> getSimplePersonByKey(final String person);

    /**
     * Create a new {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity object that will be created in the S/4HANA system.
     * @return
     *     A request builder to create a new {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder<testcomparison.namespaces.actionsandfunctions.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    CreateRequestBuilder<SimplePerson> createSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Update an existing {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity and save it to the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity object that will be updated in the S/4HANA system.
     * @return
     *     A request builder to update an existing {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder<testcomparison.namespaces.actionsandfunctions.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    UpdateRequestBuilder<SimplePerson> updateSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * Deletes an existing {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity in the S/4HANA system.
     * 
     * @param simplePerson
     *     {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A request builder to delete an existing {@link testcomparison.namespaces.actionsandfunctions.SimplePerson SimplePerson} entity. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder<testcomparison.namespaces.actionsandfunctions.SimplePerson>#execute execute} method on the request builder object. 
     */
    @Nonnull
    DeleteRequestBuilder<SimplePerson> deleteSimplePerson(
        @Nonnull
        final SimplePerson simplePerson);

    /**
     * <p>Creates a request builder for the <b>GetPersonWithMostFriends</b> OData function.</p>
     * 
     * @return
     *     A request builder object that will execute the <b>GetPersonWithMostFriends</b> OData function with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    SingleValueFunctionRequestBuilder<FunctionResult> getPersonWithMostFriends();

    /**
     * <p>Creates a request builder for the <b>FunctionWithNewResultType</b> OData function.</p>
     * 
     * @return
     *     A request builder object that will execute the <b>FunctionWithNewResultType</b> OData function with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    SingleValueFunctionRequestBuilder<NewComplexResult> functionWithNewResultType();

    /**
     * <p>Creates a request builder for the <b>FunctionWithTypeDef</b> OData function.</p>
     * 
     * @param functionParameter
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>FunctionParameter</b></p>
     * @return
     *     A request builder object that will execute the <b>FunctionWithTypeDef</b> OData function with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    SingleValueFunctionRequestBuilder<Integer> functionWithTypeDef(
        @Nullable
        final String functionParameter);

    /**
     * <p>Creates a request builder for the <b>NoArgAction</b> OData action.</p>
     * 
     * @return
     *     A request builder object that will execute the <b>NoArgAction</b> OData action with the provided parameters. To perform execution, call the {@link com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder#execute execute} method on the request builder object.
     */
    @Nonnull
    SingleValueActionRequestBuilder<Void> noArgAction();

}
