/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
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
 * Fluent helper for the <b>TestFunctionImportEdmReturnType</b> OData function import.
 * 
 */
public class TestFunctionImportEdmReturnFluentHelper
    extends SingleValuedFluentHelperFunction<TestFunctionImportEdmReturnFluentHelper, Boolean, Boolean>
{

    private final Map<String, Object> values = Maps.newHashMap();

    /**
     * Creates a fluent helper object that will execute the <b>TestFunctionImportEdmReturnType</b> OData function import with the provided parameters. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param servicePath
     *     Service path to be used to call the functions against.
     */
    public TestFunctionImportEdmReturnFluentHelper(
        @Nonnull
        final String servicePath) {
        super(servicePath);
    }

    @Override
    @Nonnull
    protected Class<Boolean> getEntityClass() {
        return Boolean.class;
    }

    @Override
    @Nonnull
    protected String getFunctionName() {
        return "TestFunctionImportEdmReturnType";
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
