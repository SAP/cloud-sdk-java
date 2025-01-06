/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.services;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CountRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.DeleteRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.ServiceWithNavigableEntities;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueActionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.SingleValueFunctionRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.UpdateRequestBuilder;
import lombok.Getter;
import testcomparison.namespaces.actionsandfunctions.FunctionResult;
import testcomparison.namespaces.actionsandfunctions.NewComplexResult;
import testcomparison.namespaces.actionsandfunctions.SimplePerson;


/**
 * <h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>actionsAndFunctions</td></tr></table>
 * 
 */
public class DefaultActionsAndFunctionsService
    implements ServiceWithNavigableEntities, ActionsAndFunctionsService
{

    @Nonnull
    @Getter
    private final String servicePath;

    /**
     * Creates a service using {@link ActionsAndFunctionsService#DEFAULT_SERVICE_PATH} to send the requests.
     * 
     */
    public DefaultActionsAndFunctionsService() {
        servicePath = ActionsAndFunctionsService.DEFAULT_SERVICE_PATH;
    }

    /**
     * Creates a service using the provided service path to send the requests.
     * <p>
     * Used by the fluent {@link #withServicePath(String)} method.
     * 
     */
    private DefaultActionsAndFunctionsService(
        @Nonnull
        final String servicePath) {
        this.servicePath = servicePath;
    }

    @Override
    @Nonnull
    public DefaultActionsAndFunctionsService withServicePath(
        @Nonnull
        final String servicePath) {
        return new DefaultActionsAndFunctionsService(servicePath);
    }

    @Override
    @Nonnull
    public BatchRequestBuilder batch() {
        return new BatchRequestBuilder(servicePath);
    }

    @Override
    @Nonnull
    public GetAllRequestBuilder<SimplePerson> getAllSimplePerson() {
        return new GetAllRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, "SimplePersons");
    }

    @Override
    @Nonnull
    public CountRequestBuilder<SimplePerson> countSimplePerson() {
        return new CountRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, "SimplePersons");
    }

    @Override
    @Nonnull
    public GetByKeyRequestBuilder<SimplePerson> getSimplePersonByKey(final String person) {
        final Map<String, Object> key = new HashMap<String, Object>();
        key.put("Person", person);
        return new GetByKeyRequestBuilder<SimplePerson>(servicePath, SimplePerson.class, key, "SimplePersons");
    }

    @Override
    @Nonnull
    public CreateRequestBuilder<SimplePerson> createSimplePerson(
        @Nonnull
        final SimplePerson simplePerson) {
        return new CreateRequestBuilder<SimplePerson>(servicePath, simplePerson, "SimplePersons");
    }

    @Override
    @Nonnull
    public UpdateRequestBuilder<SimplePerson> updateSimplePerson(
        @Nonnull
        final SimplePerson simplePerson) {
        return new UpdateRequestBuilder<SimplePerson>(servicePath, simplePerson, "SimplePersons");
    }

    @Override
    @Nonnull
    public DeleteRequestBuilder<SimplePerson> deleteSimplePerson(
        @Nonnull
        final SimplePerson simplePerson) {
        return new DeleteRequestBuilder<SimplePerson>(servicePath, simplePerson, "SimplePersons");
    }

    @Override
    @Nonnull
    public SingleValueFunctionRequestBuilder<FunctionResult> getPersonWithMostFriends() {
        return new SingleValueFunctionRequestBuilder<FunctionResult>(servicePath, "GetPersonWithMostFriends", FunctionResult.class);
    }

    @Override
    @Nonnull
    public SingleValueFunctionRequestBuilder<NewComplexResult> functionWithNewResultType() {
        return new SingleValueFunctionRequestBuilder<NewComplexResult>(servicePath, "FunctionWithNewResultType", NewComplexResult.class);
    }

    @Override
    @Nonnull
    public SingleValueFunctionRequestBuilder<Integer> functionWithTypeDef(
        @Nullable
        final String functionParameter) {
        final LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("FunctionParameter", functionParameter);
        return new SingleValueFunctionRequestBuilder<Integer>(servicePath, "FunctionWithTypeDef", parameters, Integer.class);
    }

    @Override
    @Nonnull
    public SingleValueActionRequestBuilder<Void> noArgAction() {
        return new SingleValueActionRequestBuilder<Void>(servicePath, "NoArgAction", Void.class);
    }

}
