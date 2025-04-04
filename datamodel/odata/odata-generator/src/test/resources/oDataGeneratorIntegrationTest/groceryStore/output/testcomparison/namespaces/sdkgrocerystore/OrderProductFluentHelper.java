/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import java.net.URI;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.helper.SingleValuedFluentHelperFunction;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;


/**
 * Fluent helper for the <b>OrderProduct</b> OData function import.
 * 
 */
public class OrderProductFluentHelper
    extends SingleValuedFluentHelperFunction<OrderProductFluentHelper, Receipt, Receipt>
{

    private final Map<String, Object> values = Maps.newLinkedHashMap();

    /**
     * Creates a fluent helper object that will execute the <b>OrderProduct</b> OData function import with the provided parameters. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param quantity
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>Quantity</b></p>
     * @param productId
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>ProductId</b></p>
     * @param servicePath
     *     Service path to be used to call the functions against.
     * @param customerId
     *     Constraints: Not nullable<p>Original parameter name from the Odata EDM: <b>CustomerId</b></p>
     */
    public OrderProductFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final Integer customerId,
        @Nonnull
        final Integer productId,
        @Nonnull
        final Integer quantity) {
        super(servicePath);
        values.put("CustomerId", customerId);
        values.put("ProductId", productId);
        values.put("Quantity", quantity);
    }

    @Override
    @Nonnull
    protected Class<Receipt> getEntityClass() {
        return Receipt.class;
    }

    @Override
    @Nonnull
    protected String getFunctionName() {
        return "OrderProduct";
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
        return new HttpPost(uri);
    }

    /**
     * Execute this function import.
     * 
     */
    @Override
    @Nullable
    public Receipt executeRequest(
        @Nonnull
        final Destination destination) {
        return super.executeSingle(destination);
    }

}
