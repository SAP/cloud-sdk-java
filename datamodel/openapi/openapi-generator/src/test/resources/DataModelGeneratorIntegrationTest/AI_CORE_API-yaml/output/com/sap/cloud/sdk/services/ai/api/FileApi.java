/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.ai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.ai.model.Error ; //NOPMD
import java.io.File ; //NOPMD
import com.sap.cloud.sdk.services.ai.model.FileCreationResponse ; //NOPMD

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
* AI Core in version 2.26.0.
*
* Provides tools to manage your scenarios and workflows in SAP AI Core. Execute pipelines as a batch job, for example to pre-process or train your models, or perform batch inference.  Serve inference requests of trained models. Deploy а trained machine learning model as a web service to serve inference requests with high performance.  Register your own Docker registry, synchronize your AI content from your own git repository, and register your own object store for training data and trained models. 
*/

public class FileApi extends AbstractOpenApiService {
    /**
    * Instantiates this API class to invoke operations on the AI Core.
    *
    * @param httpDestination The destination that API should be used with
    */
    public FileApi( @Nonnull final Destination httpDestination )
    {
        super(httpDestination);
    }

    /**
    * Instantiates this API class to invoke operations on the AI Core based on a given {@link ApiClient}.
    *
    * @param apiClient
    *            ApiClient to invoke the API on
    */
    @Beta
    public FileApi( @Nonnull final ApiClient apiClient )
    {
         super(apiClient);
    }

    
    /**
     * <p>Delete file</p>
     *<p>Delete the file specified by the path parameter.</p>
     * <p><b>204</b> - The request was processed successfully.
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>404</b> - The specified resource was not found
     * @param path  (required)
        path relative to the object store root URL in the secret
     * @param aiResourceGroup  (optional)
        Specify a resource group id
     * @return An OpenApiResponse containing the status code of the HttpResponse.
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
     @Nonnull public OpenApiResponse fileDelete( @Nonnull final String path,  @Nullable final String aiResourceGroup) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'path' is set
        if (path == null) {
            throw new OpenApiRequestException("Missing the required parameter 'path' when calling fileDelete");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("path", path);
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/dataset/files/{path}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<Void> localVarReturnType = new ParameterizedTypeReference<Void>() {};
        apiClient.invokeAPI(localVarPath, HttpMethod.DELETE, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
        return new OpenApiResponse(apiClient);
    }

    /**
    * <p>Delete file</p>
     *<p>Delete the file specified by the path parameter.</p>
     * <p><b>204</b> - The request was processed successfully.
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>404</b> - The specified resource was not found
* @param path
        path relative to the object store root URL in the secret
* @return An OpenApiResponse containing the status code of the HttpResponse.
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
     @Nonnull  public OpenApiResponse fileDelete( @Nonnull final String path) throws OpenApiRequestException {
        return fileDelete(path, null);
    }

    /**
     * <p>Download file</p>
     *<p>Endpoint for downloading file. The path must point to an individual file.</p>
     * <p><b>200</b> - OK
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>404</b> - The specified resource was not found
     * @param path  (required)
        path relative to the object store root URL in the secret
     * @param aiResourceGroup  (optional)
        Specify a resource group id
     * @return File
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public File fileDownload( @Nonnull final String path,  @Nullable final String aiResourceGroup) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'path' is set
        if (path == null) {
            throw new OpenApiRequestException("Missing the required parameter 'path' when calling fileDownload");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("path", path);
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/dataset/files/{path}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/octet-stream", "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<File> localVarReturnType = new ParameterizedTypeReference<File>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.GET, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Download file</p>
     *<p>Endpoint for downloading file. The path must point to an individual file.</p>
     * <p><b>200</b> - OK
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>404</b> - The specified resource was not found
* @param path
        path relative to the object store root URL in the secret
* @return File
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public File fileDownload( @Nonnull final String path) throws OpenApiRequestException {
        return fileDownload(path, null);
    }

    /**
     * <p>Upload file (size &lt;&#x3D; 100Mb)</p>
     *<p>Endpoint for uploading file. The maximum file size depends on the actual implementation  but must not exceed 100MB. The actual file size limit can be obtained by querying  the AI API Runtime Capabilities Endpoint and checking the limits in the section of the &#x60;fileUpload&#x60; extension.  Path cannot be a prefix, it must be a path to an object. Clients may group the objects in any manner they choose by specifying path prefixes.  Allowed mime-types will be decided by the implementation. Content-Type header can be set to \&quot;application/octet-stream\&quot; but the implementation is responsible  for detecting the actual mime type and checking against the allowed list of mime types.  For security reasons, implementations cannot trust the mime type sent by the client.  Example URLs:  /files/dar/schemas/schema.json  /files/icr/datasets/training/20201001/20201001-01.csv  /files/icr/datasets/training/20201001/20201001-02.csv   /files/mask-detection/training/mask-detection-20210301.tar.gz</p>
     * <p><b>201</b> - Created
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>409</b> - The specified file already exists and cannot be overwritten.
     * <p><b>413</b> - The file size exceeds the supported limit.
     * @param path  (required)
        path relative to the object store root URL in the secret
     * @param aiResourceGroup  (optional)
        Specify a resource group id
     * @param overwrite  (optional)
        If true, then file is overwritten. Default is false.
     * @param body  (optional)
        Body of the file upload request
     * @return FileCreationResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable  public FileCreationResponse fileUpload( @Nonnull final String path,  @Nullable final String aiResourceGroup,  @Nullable final Boolean overwrite,  @Nullable final String body) throws OpenApiRequestException {
        final Object localVarPostBody = body;
        
        // verify the required parameter 'path' is set
        if (path == null) {
            throw new OpenApiRequestException("Missing the required parameter 'path' when calling fileUpload");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("path", path);
        final String localVarPath = UriComponentsBuilder.fromPath("/lm/dataset/files/{path}").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "overwrite", overwrite));
        

        if (aiResourceGroup != null)
            localVarHeaderParams.add("AI-Resource-Group", apiClient.parameterToString(aiResourceGroup));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "Oauth2" };

        final ParameterizedTypeReference<FileCreationResponse> localVarReturnType = new ParameterizedTypeReference<FileCreationResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.PUT, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
    * <p>Upload file (size &lt;&#x3D; 100Mb)</p>
     *<p>Endpoint for uploading file. The maximum file size depends on the actual implementation  but must not exceed 100MB. The actual file size limit can be obtained by querying  the AI API Runtime Capabilities Endpoint and checking the limits in the section of the &#x60;fileUpload&#x60; extension.  Path cannot be a prefix, it must be a path to an object. Clients may group the objects in any manner they choose by specifying path prefixes.  Allowed mime-types will be decided by the implementation. Content-Type header can be set to \&quot;application/octet-stream\&quot; but the implementation is responsible  for detecting the actual mime type and checking against the allowed list of mime types.  For security reasons, implementations cannot trust the mime type sent by the client.  Example URLs:  /files/dar/schemas/schema.json  /files/icr/datasets/training/20201001/20201001-01.csv  /files/icr/datasets/training/20201001/20201001-02.csv   /files/mask-detection/training/mask-detection-20210301.tar.gz</p>
     * <p><b>201</b> - Created
     * <p><b>400</b> - The specification of the resource was incorrect
     * <p><b>409</b> - The specified file already exists and cannot be overwritten.
     * <p><b>413</b> - The file size exceeds the supported limit.
* @param path
        path relative to the object store root URL in the secret
* @return FileCreationResponse
* @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nullable   public FileCreationResponse fileUpload( @Nonnull final String path) throws OpenApiRequestException {
        return fileUpload(path, null, null, null);
    }
}
