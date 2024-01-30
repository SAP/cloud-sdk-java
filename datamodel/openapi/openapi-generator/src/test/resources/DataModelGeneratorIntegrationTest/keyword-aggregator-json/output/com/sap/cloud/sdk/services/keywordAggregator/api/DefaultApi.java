/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.keywordAggregator.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.keywordAggregator.model.AllOfProperty ; //NOPMD
import com.sap.cloud.sdk.services.keywordAggregator.model.AnyOfProperty ; //NOPMD
import com.sap.cloud.sdk.services.keywordAggregator.model.OneOfProperty ; //NOPMD
import com.sap.cloud.sdk.services.keywordAggregator.model.RootObject ; //NOPMD
import com.sap.cloud.sdk.services.keywordAggregator.model.SomeEndpointPutRequest ; //NOPMD

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.annotations.Beta;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;

/**
* Sample Cloud SDK Test API in version 0.0.1.
*
* This is a sample API to test the Cloud SDK's OpenAPI generator.
*/

public class DefaultApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the Sample Cloud SDK Test API.
    *
    * @param httpDestination The destination that API should be used with
    */
    public DefaultApi( @Nonnull final Destination httpDestination )
    {
        super(httpDestination);
    }

    /**
    * Instantiates this API class to invoke operations on the Sample Cloud SDK Test API based on a given {@link ApiClient}.
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
    * <p></p>
     *<p>Return an allOf response.</p>
     * <p><b>200</b> - Retrieved
* @return AllOfProperty
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public AllOfProperty allOfEndpointGet() throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/all/of/endpoint").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final ParameterizedTypeReference<AllOfProperty> localVarReturnType = new ParameterizedTypeReference<AllOfProperty>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
    /**
    * <p></p>
     *<p>Return an anyOf response.</p>
     * <p><b>200</b> - Retrieved
* @return AnyOfProperty
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public AnyOfProperty anyOfEndpointGet() throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/any/of/endpoint").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final ParameterizedTypeReference<AnyOfProperty> localVarReturnType = new ParameterizedTypeReference<AnyOfProperty>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
    /**
    * <p></p>
     *<p>Return an oneOf response.</p>
     * <p><b>200</b> - Retrieved
* @return OneOfProperty
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public OneOfProperty oneOfEndpointGet() throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/one/of/endpoint").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final ParameterizedTypeReference<OneOfProperty> localVarReturnType = new ParameterizedTypeReference<OneOfProperty>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * <p></p>
     *<p></p>
     * <p><b>201</b> - Something was created.
     * <p><b>400</b> - Request was invalid.
     * <p><b>401</b> - Authentication Error
     * @param rootObject  (optional)
        The value for the parameter rootObject
     * @return RootObject
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public RootObject otherEndpointPost( @Nullable final RootObject rootObject) throws OpenApiRequestException {
        final Object localVarPostBody = rootObject;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/other/endpoint").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final ParameterizedTypeReference<RootObject> localVarReturnType = new ParameterizedTypeReference<RootObject>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p></p>
     *<p></p>
     * <p><b>201</b> - Something was created.
     * <p><b>400</b> - Request was invalid.
     * <p><b>401</b> - Authentication Error
* @return RootObject
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public RootObject otherEndpointPost() throws OpenApiRequestException {
        return otherEndpointPost(null);
    }

    /**
     * <p></p>
     *<p></p>
     * <p><b>200</b> - Retrieved
     * @param destination  (optional)
        The value for the parameter destination
     * @return SomeEndpointPutRequest
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public SomeEndpointPutRequest someEndpointPut( @Nullable final SomeEndpointPutRequest destination) throws OpenApiRequestException {
        final Object localVarPostBody = destination;
        
        final String localVarPath = UriComponentsBuilder.fromPath("/some/endpoint").build().toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] {  };

        final ParameterizedTypeReference<SomeEndpointPutRequest> localVarReturnType = new ParameterizedTypeReference<SomeEndpointPutRequest>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.PUT, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p></p>
     *<p></p>
     * <p><b>200</b> - Retrieved
* @return SomeEndpointPutRequest
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public SomeEndpointPutRequest someEndpointPut() throws OpenApiRequestException {
        return someEndpointPut(null);
    }
}
