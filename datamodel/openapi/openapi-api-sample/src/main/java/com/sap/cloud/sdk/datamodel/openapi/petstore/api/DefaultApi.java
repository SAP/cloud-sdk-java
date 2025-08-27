/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.petstore.api;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.openapi.petstore.model.Pet;
import com.sap.cloud.sdk.datamodel.openapi.petstore.model.PetInput;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;

/**
 * Swagger Petstore in version 1.0.0.
 *
 * A sample API that uses a petstore as an example to demonstrate features in the swagger-2.0 specification
 */
public class DefaultApi extends AbstractOpenApiService
{
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
    @Beta
    public DefaultApi( @Nonnull final ApiClient apiClient )
    {
        super(apiClient);
    }

    /**
     * <p>
     * </p>
     * <p>
     * Creates a new pet in the store. Duplicates are allowed
     * </p>
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
            throw new OpenApiRequestException("Missing the required parameter 'pet' when calling addPet");
        }

        final String localVarPath = UriComponentsBuilder.fromPath("/pets").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { "application/json" };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {};

        final ParameterizedTypeReference<Pet> localVarReturnType = new ParameterizedTypeReference<Pet>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                HttpMethod.POST,
                localVarQueryParams,
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarAuthNames,
                localVarReturnType);
    }

    /**
     * <p>
     * </p>
     * <p>
     * deletes a single pet based on the ID supplied
     * </p>
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
            throw new OpenApiRequestException("Missing the required parameter 'id' when calling deletePet");
        }

        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("id", id);
        final String localVarPath =
            UriComponentsBuilder.fromPath("/pets/{id}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {};
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {};

        final ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>()
        {
        };
        apiClient
            .invokeAPI(
                localVarPath,
                HttpMethod.DELETE,
                localVarQueryParams,
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarAuthNames,
                localVarReturnType);
        return new OpenApiResponse(apiClient);
    }

    /**
     * <p>
     * </p>
     * <p>
     * Returns a user based on a single ID, if the user does not have access to the pet
     * </p>
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
            throw new OpenApiRequestException("Missing the required parameter 'id' when calling findPetById");
        }

        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("id", id);
        final String localVarPath =
            UriComponentsBuilder.fromPath("/pets/{id}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { "application/json", "application/xml", "text/xml", "text/html" };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {};
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {};

        final ParameterizedTypeReference<Pet> localVarReturnType = new ParameterizedTypeReference<Pet>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                HttpMethod.GET,
                localVarQueryParams,
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarAuthNames,
                localVarReturnType);
    }

    /**
     * <p>
     * </p>
     * <p>
     * Returns all pets from the system that the user has access to
     * </p>
     * <p>
     * <b>200</b> - pet response
     * <p>
     * <b>0</b> - unexpected error
     *
     * @param tags
     *            (optional tags to filter by
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

        final String localVarPath = UriComponentsBuilder.fromPath("/pets").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams
            .putAll(
                apiClient
                    .parameterToMultiValueMap(
                        ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)),
                        "tags",
                        tags));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "limit", limit));

        final String[] localVarAccepts = { "application/json", "application/xml", "text/xml", "text/html" };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {};
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {};

        final ParameterizedTypeReference<List<Pet>> localVarReturnType = new ParameterizedTypeReference<List<Pet>>()
        {
        };
        return apiClient
            .invokeAPI(
                localVarPath,
                HttpMethod.GET,
                localVarQueryParams,
                localVarPostBody,
                localVarHeaderParams,
                localVarFormParams,
                localVarAccept,
                localVarContentType,
                localVarAuthNames,
                localVarReturnType);
    }

    /**
     * <p>
     * </p>
     * <p>
     * Returns all pets from the system that the user has access to
     * </p>
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
