/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.net.URI;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.helper.SingleValuedFluentHelperFunction;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;


/**
 * Fluent helper for the <b>TestFunctionImportMultipleParams</b> OData function import.
 * 
 */
public class TestFunctionImportMultipleParamsFluentHelper
    extends SingleValuedFluentHelperFunction<TestFunctionImportMultipleParamsFluentHelper, Boolean, Boolean>
{

    private final Map<String, Object> values = Maps.newLinkedHashMap();

    /**
     * Creates a fluent helper object that will execute the <b>TestFunctionImportMultipleParams</b> OData function import with the provided parameters. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param stringParam
     *     Constraints: none<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @param servicePath
     *     Service path to be used to call the functions against.
     * @param booleanParam
     *     Constraints: none<p>Original parameter name from the Odata EDM: <b>BooleanParam</b></p>
     */
    public TestFunctionImportMultipleParamsFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String stringParam,
        @Nonnull
        final Boolean booleanParam) {
        super(servicePath);
        values.put("StringParam", stringParam);
        values.put("BooleanParam", booleanParam);
    }

    @Override
    @Nonnull
    protected Class<Boolean> getEntityClass() {
        return Boolean.class;
    }

    @Override
    @Nonnull
    protected String getFunctionName() {
        return "TestFunctionImportMultipleParams";
    }

    @Override
    @Nullable
    protected JsonElement refineJsonResponse(
        @Nullable
        JsonElement jsonElement) {
        if ((jsonElement instanceof JsonObject)&&((JsonObject) jsonElement).has(getFunctionName())) {
            jsonElement = ((JsonObject) jsonElement).get(getFunctionName());
        }
        return super.refineJsonResponse(jsonElement);
    }

    @Override
    @Nonnull
    protected Map<String, Object> getParameters() {
        return values;
    }

    @Override
    @Nonnull
    protected HttpUriRequest createRequest(
        @Nonnull
        final URI uri) {
        return new HttpGet(uri);
    }

    /**
     * Execute this function import.
     * 
     */
    @Override
    @Nullable
    public Boolean executeRequest(
        @Nonnull
        final Destination destination) {
        return super.executeSingle(destination);
    }

}
