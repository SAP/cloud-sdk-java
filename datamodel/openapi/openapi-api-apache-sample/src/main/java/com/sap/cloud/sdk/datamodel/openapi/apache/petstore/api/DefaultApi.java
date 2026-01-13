/*
 * Copyright (c) 2026 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.apache.petstore.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.openapi.apache.petstore.model.Pet;
import com.sap.cloud.sdk.datamodel.openapi.apache.petstore.model.PetInput;
import com.sap.cloud.sdk.services.openapi.apache.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.apache.Pair;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;

/**
 * Swagger Petstore in version 1.0.0.
 *
 * A sample API that uses a petstore as an example to demonstrate features in the swagger-2.0 specification
 */
public class DefaultApi extends BaseApi
{

    /**
     * Instantiates this API class to invoke operations on the Swagger Petstore
     */
    public DefaultApi()
    {
    }

    /**
     * Instantiates this API class to invoke operations on the Swagger Petstore.
     *
     * @param httpDestination
     *            The destination that API should be used with
     */
    public DefaultApi( @Nonnull final Destination httpDestination )
    {
        super(httpDestination);
    }

    /**
     * Instantiates this API class to invoke operations on the Swagger Petstore based on a given {@link ApiClient}.
     *
     * @param apiClient
     *            ApiClient to invoke the API on
     */
    public DefaultApi( @Nonnull final ApiClient apiClient )
    {
        super(apiClient);
    }

    /**
     * <p>
     * <p>
     * Creates a new pet in the store. Duplicates are allowed
     * <p>
     * <b>200</b> - pet response
     * <p>
     * <b>0</b> - unexpected error
     *
     * @param pet
     *            Pet to add to the store
     * @return Pet
     * @throws OpenApiRequestException
     *             if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public Pet addPet( @Nonnull final PetInput pet )
        throws OpenApiRequestException
    {
        final Object localVarPostBody = pet;

        // verify the required parameter 'pet' is set
        if( pet == null ) {
            throw new OpenApiRequestException("Missing the required parameter 'pet' when calling addPet")
                .statusCode(400);
        }

        // create path and map variables
        final String localVarPath = "/pets";

        final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        String localVarQueryParameterBaseName;
        final List<Pair> localVarQueryParams = new ArrayList<Pair>();
        final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        final Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);

        final String[] localVarContentTypes = { "application/json" };
        final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);

        final TypeReference<Pet> localVarReturnType = new TypeReference<Pet>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                "POST",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType);
    }

    /**
     * <p>
     * <p>
     * deletes a single pet based on the ID supplied
     * <p>
     * <b>204</b> - pet deleted
     * <p>
     * <b>0</b> - unexpected error
     *
     * @param id
     *            ID of pet to delete
     * @return An OpenApiResponse containing the status code of the HttpResponse.
     * @throws OpenApiRequestException
     *             if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public OpenApiResponse deletePet( @Nonnull final Long id )
        throws OpenApiRequestException
    {
        final Object localVarPostBody = null;

        // verify the required parameter 'id' is set
        if( id == null ) {
            throw new OpenApiRequestException("Missing the required parameter 'id' when calling deletePet")
                .statusCode(400);
        }

        // create path and map variables
        final String localVarPath =
            "/pets/{id}".replaceAll("\\{" + "id" + "\\}", ApiClient.escapeString(ApiClient.parameterToString(id)));

        final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        String localVarQueryParameterBaseName;
        final List<Pair> localVarQueryParams = new ArrayList<Pair>();
        final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        final Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);

        final TypeReference<OpenApiResponse> localVarReturnType = new TypeReference<OpenApiResponse>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                "DELETE",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType);
    }

    /**
     * <p>
     * <p>
     * Returns a user based on a single ID, if the user does not have access to the pet
     * <p>
     * <b>200</b> - pet response
     * <p>
     * <b>0</b> - unexpected error
     *
     * @param id
     *            ID of pet to fetch
     * @return Pet
     * @throws OpenApiRequestException
     *             if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public Pet findPetById( @Nonnull final Long id )
        throws OpenApiRequestException
    {
        final Object localVarPostBody = null;

        // verify the required parameter 'id' is set
        if( id == null ) {
            throw new OpenApiRequestException("Missing the required parameter 'id' when calling findPetById")
                .statusCode(400);
        }

        // create path and map variables
        final String localVarPath =
            "/pets/{id}".replaceAll("\\{" + "id" + "\\}", ApiClient.escapeString(ApiClient.parameterToString(id)));

        final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        String localVarQueryParameterBaseName;
        final List<Pair> localVarQueryParams = new ArrayList<Pair>();
        final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        final Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json", "application/xml", "text/xml", "text/html" };
        final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);

        final TypeReference<Pet> localVarReturnType = new TypeReference<Pet>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                "GET",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType);
    }

    /**
     * <p>
     * <p>
     * Returns all pets from the system that the user has access to
     * <p>
     * <b>200</b> - pet response
     * <p>
     * <b>0</b> - unexpected error
     *
     * @param tags
     *            (optional) tags to filter by
     * @param limit
     *            (optional) maximum number of results to return
     * @return List&lt;Pet&gt;
     * @throws OpenApiRequestException
     *             if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public List<Pet> findPets( @Nullable final List<String> tags, @Nullable final Integer limit )
        throws OpenApiRequestException
    {
        final Object localVarPostBody = null;

        // create path and map variables
        final String localVarPath = "/pets";

        final StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
        String localVarQueryParameterBaseName;
        final List<Pair> localVarQueryParams = new ArrayList<Pair>();
        final List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        final Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        final Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        localVarCollectionQueryParams.addAll(ApiClient.parameterToPairs("csv", "tags", tags));
        localVarQueryParams.addAll(ApiClient.parameterToPair("limit", limit));

        final String[] localVarAccepts = { "application/json", "application/xml", "text/xml", "text/html" };
        final String localVarAccept = ApiClient.selectHeaderAccept(localVarAccepts);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = ApiClient.selectHeaderContentType(localVarContentTypes);

        final TypeReference<List<Pet>> localVarReturnType = new TypeReference<List<Pet>>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                "GET",
                localVarQueryParams,
                localVarCollectionQueryParams,
                localVarQueryStringJoiner.toString(),
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarReturnType);
    }

    /**
     * <p>
     * <p>
     * Returns all pets from the system that the user has access to
     * <p>
     * <b>200</b> - pet response
     * <p>
     * <b>0</b> - unexpected error
     *
     * @return List&lt;Pet&gt;
     * @throws OpenApiRequestException
     *             if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public List<Pet> findPets()
        throws OpenApiRequestException
    {
        return findPets(null, null);
    }
}
