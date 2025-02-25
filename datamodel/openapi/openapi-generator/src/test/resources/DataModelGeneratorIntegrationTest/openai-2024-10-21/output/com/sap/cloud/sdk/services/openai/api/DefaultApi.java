/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openai.api;

import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;
import com.sap.cloud.sdk.services.openapi.core.OpenApiResponse;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

import com.sap.cloud.sdk.services.openai.model.AudioResponseFormat;
import java.math.BigDecimal;
import com.sap.cloud.sdk.services.openai.model.ChatCompletionsCreate200Response;
import com.sap.cloud.sdk.services.openai.model.CreateChatCompletionRequest;
import com.sap.cloud.sdk.services.openai.model.CreateCompletionRequest;
import com.sap.cloud.sdk.services.openai.model.CreateCompletionResponse;
import com.sap.cloud.sdk.services.openai.model.DalleErrorResponse;
import com.sap.cloud.sdk.services.openai.model.EmbeddingsCreate200Response;
import com.sap.cloud.sdk.services.openai.model.EmbeddingsCreateRequest;
import com.sap.cloud.sdk.services.openai.model.ErrorResponse;
import java.io.File;
import com.sap.cloud.sdk.services.openai.model.GenerateImagesResponse;
import com.sap.cloud.sdk.services.openai.model.ImageGenerationsRequest;
import com.sap.cloud.sdk.services.openai.model.TranscriptionsCreate200Response;

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

import com.sap.ai.sdk.core.AiCoreService;

/**
 * Azure OpenAI Service API in version 2024-10-21.
 *
 * Azure OpenAI APIs for completions and search
 */
public class DefaultApi extends AbstractOpenApiService {

    /**
     * Instantiates this API class to invoke operations on the Azure OpenAI Service API
     */
    public DefaultApi()
    {
         super(new AiCoreService().getApiClient());
    }

    /**
     * Instantiates this API class to invoke operations on the Azure OpenAI Service API
     *
     * @param aiCoreService The configured connectivity instance to AI Core
     */
    public DefaultApi( @Nonnull final AiCoreService aiCoreService )
    {
        super(aiCoreService.getApiClient());
    }

        /**
     * <p>Creates a completion for the chat message</p>
     * <p></p>
     * <p><b>200</b> - OK
     * <p><b>0</b> - Service unavailable
     * @param deploymentId
     *      The value for the parameter deploymentId
     * @param apiVersion
     *      The value for the parameter apiVersion
     * @param createChatCompletionRequest
     *      The value for the parameter createChatCompletionRequest
     * @return ChatCompletionsCreate200Response
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public ChatCompletionsCreate200Response chatCompletionsCreate( @Nonnull final String deploymentId,  @Nonnull final String apiVersion,  @Nonnull final CreateChatCompletionRequest createChatCompletionRequest) throws OpenApiRequestException {
        final Object localVarPostBody = createChatCompletionRequest;
        
        // verify the required parameter 'deploymentId' is set
        if (deploymentId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'deploymentId' when calling chatCompletionsCreate");
        }
        
        // verify the required parameter 'apiVersion' is set
        if (apiVersion == null) {
            throw new OpenApiRequestException("Missing the required parameter 'apiVersion' when calling chatCompletionsCreate");
        }
        
        // verify the required parameter 'createChatCompletionRequest' is set
        if (createChatCompletionRequest == null) {
            throw new OpenApiRequestException("Missing the required parameter 'createChatCompletionRequest' when calling chatCompletionsCreate");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("deployment-id", deploymentId);
        final String localVarPath = UriComponentsBuilder.fromPath("/deployments/{deployment-id}/chat/completions").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "api-version", apiVersion));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKey", "bearer" };

        final ParameterizedTypeReference<ChatCompletionsCreate200Response> localVarReturnType = new ParameterizedTypeReference<ChatCompletionsCreate200Response>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
    /**
     * <p>Creates a completion for the provided prompt, parameters and chosen model.</p>
     * <p></p>
     * <p><b>200</b> - OK
     * <p><b>0</b> - Service unavailable
     * @param deploymentId
     *      The value for the parameter deploymentId
     * @param apiVersion
     *      The value for the parameter apiVersion
     * @param createCompletionRequest
     *      The value for the parameter createCompletionRequest
     * @return CreateCompletionResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public CreateCompletionResponse completionsCreate( @Nonnull final String deploymentId,  @Nonnull final String apiVersion,  @Nonnull final CreateCompletionRequest createCompletionRequest) throws OpenApiRequestException {
        final Object localVarPostBody = createCompletionRequest;
        
        // verify the required parameter 'deploymentId' is set
        if (deploymentId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'deploymentId' when calling completionsCreate");
        }
        
        // verify the required parameter 'apiVersion' is set
        if (apiVersion == null) {
            throw new OpenApiRequestException("Missing the required parameter 'apiVersion' when calling completionsCreate");
        }
        
        // verify the required parameter 'createCompletionRequest' is set
        if (createCompletionRequest == null) {
            throw new OpenApiRequestException("Missing the required parameter 'createCompletionRequest' when calling completionsCreate");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("deployment-id", deploymentId);
        final String localVarPath = UriComponentsBuilder.fromPath("/deployments/{deployment-id}/completions").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "api-version", apiVersion));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKey", "bearer" };

        final ParameterizedTypeReference<CreateCompletionResponse> localVarReturnType = new ParameterizedTypeReference<CreateCompletionResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
    /**
     * <p>Get a vector representation of a given input that can be easily consumed by machine learning models and algorithms.</p>
     * <p></p>
     * <p><b>200</b> - OK
     * @param deploymentId
     *      The deployment id of the model which was deployed.
     * @param apiVersion
     *      The value for the parameter apiVersion
     * @param embeddingsCreateRequest
     *      The value for the parameter embeddingsCreateRequest
     * @return EmbeddingsCreate200Response
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public EmbeddingsCreate200Response embeddingsCreate( @Nonnull final String deploymentId,  @Nonnull final String apiVersion,  @Nonnull final EmbeddingsCreateRequest embeddingsCreateRequest) throws OpenApiRequestException {
        final Object localVarPostBody = embeddingsCreateRequest;
        
        // verify the required parameter 'deploymentId' is set
        if (deploymentId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'deploymentId' when calling embeddingsCreate");
        }
        
        // verify the required parameter 'apiVersion' is set
        if (apiVersion == null) {
            throw new OpenApiRequestException("Missing the required parameter 'apiVersion' when calling embeddingsCreate");
        }
        
        // verify the required parameter 'embeddingsCreateRequest' is set
        if (embeddingsCreateRequest == null) {
            throw new OpenApiRequestException("Missing the required parameter 'embeddingsCreateRequest' when calling embeddingsCreate");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("deployment-id", deploymentId);
        final String localVarPath = UriComponentsBuilder.fromPath("/deployments/{deployment-id}/embeddings").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "api-version", apiVersion));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKey", "bearer" };

        final ParameterizedTypeReference<EmbeddingsCreate200Response> localVarReturnType = new ParameterizedTypeReference<EmbeddingsCreate200Response>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }
    /**
     * <p>Generates a batch of images from a text caption on a given DALLE model deployment</p>
     * <p></p>
     * <p><b>200</b> - Ok
     * <p><b>0</b> - An error occurred.
     * @param deploymentId
     *      The value for the parameter deploymentId
     * @param apiVersion
     *      The value for the parameter apiVersion
     * @param imageGenerationsRequest
     *      The value for the parameter imageGenerationsRequest
     * @return GenerateImagesResponse
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public GenerateImagesResponse imageGenerationsCreate( @Nonnull final String deploymentId,  @Nonnull final String apiVersion,  @Nonnull final ImageGenerationsRequest imageGenerationsRequest) throws OpenApiRequestException {
        final Object localVarPostBody = imageGenerationsRequest;
        
        // verify the required parameter 'deploymentId' is set
        if (deploymentId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'deploymentId' when calling imageGenerationsCreate");
        }
        
        // verify the required parameter 'apiVersion' is set
        if (apiVersion == null) {
            throw new OpenApiRequestException("Missing the required parameter 'apiVersion' when calling imageGenerationsCreate");
        }
        
        // verify the required parameter 'imageGenerationsRequest' is set
        if (imageGenerationsRequest == null) {
            throw new OpenApiRequestException("Missing the required parameter 'imageGenerationsRequest' when calling imageGenerationsCreate");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("deployment-id", deploymentId);
        final String localVarPath = UriComponentsBuilder.fromPath("/deployments/{deployment-id}/images/generations").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "api-version", apiVersion));

        final String[] localVarAccepts = { 
            "application/json"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKey", "bearer" };

        final ParameterizedTypeReference<GenerateImagesResponse> localVarReturnType = new ParameterizedTypeReference<GenerateImagesResponse>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * <p>Transcribes audio into the input language.</p>
     *<p></p>
     * <p><b>200</b> - OK
     * @param deploymentId  (required)
        The value for the parameter deploymentId
     * @param apiVersion  (required)
        The value for the parameter apiVersion
     * @param _file  (required)
        The audio file object to transcribe.
     * @param prompt  (optional)
        An optional text to guide the model&#39;s style or continue a previous audio segment. The prompt should match the audio language.
     * @param responseFormat  (optional)
        The value for the parameter responseFormat
     * @param temperature  (optional, default to 0)
        The sampling temperature, between 0 and 1. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. If set to 0, the model will use log probability to automatically increase the temperature until certain thresholds are hit.
     * @param language  (optional)
        The language of the input audio. Supplying the input language in ISO-639-1 format will improve accuracy and latency.
     * @return TranscriptionsCreate200Response
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public TranscriptionsCreate200Response transcriptionsCreate( @Nonnull final String deploymentId,  @Nonnull final String apiVersion,  @Nonnull final org.springframework.core.io.Resource _file,  @Nullable final String prompt,  @Nullable final AudioResponseFormat responseFormat,  @Nullable final BigDecimal temperature,  @Nullable final String language) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'deploymentId' is set
        if (deploymentId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'deploymentId' when calling transcriptionsCreate");
        }
        
        // verify the required parameter 'apiVersion' is set
        if (apiVersion == null) {
            throw new OpenApiRequestException("Missing the required parameter 'apiVersion' when calling transcriptionsCreate");
        }
        
        // verify the required parameter '_file' is set
        if (_file == null) {
            throw new OpenApiRequestException("Missing the required parameter '_file' when calling transcriptionsCreate");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("deployment-id", deploymentId);
        final String localVarPath = UriComponentsBuilder.fromPath("/deployments/{deployment-id}/audio/transcriptions").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "api-version", apiVersion));
        

        if (_file != null)
            localVarFormParams.add("file", _file);
        if (prompt != null)
            localVarFormParams.add("prompt", prompt);
        if (responseFormat != null)
            localVarFormParams.add("response_format", responseFormat);
        if (temperature != null)
            localVarFormParams.add("temperature", temperature);
        if (language != null)
            localVarFormParams.add("language", language);

        final String[] localVarAccepts = { 
            "application/json", "text/plain"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "multipart/form-data"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKey", "bearer" };

        final ParameterizedTypeReference<TranscriptionsCreate200Response> localVarReturnType = new ParameterizedTypeReference<TranscriptionsCreate200Response>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * <p>Transcribes audio into the input language.</p>
     * <p></p>
     * <p><b>200</b> - OK
     * @param deploymentId
     *      The value for the parameter deploymentId
     * @param apiVersion
     *      The value for the parameter apiVersion
     * @param _file
     *      The audio file object to transcribe.
     * @return TranscriptionsCreate200Response
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public TranscriptionsCreate200Response transcriptionsCreate( @Nonnull final String deploymentId,  @Nonnull final String apiVersion,  @Nonnull final org.springframework.core.io.Resource _file) throws OpenApiRequestException {
        return transcriptionsCreate(deploymentId, apiVersion, _file, null, null, null, null);
    }

    /**
     * <p>Transcribes and translates input audio into English text.</p>
     *<p></p>
     * <p><b>200</b> - OK
     * @param deploymentId  (required)
        The value for the parameter deploymentId
     * @param apiVersion  (required)
        The value for the parameter apiVersion
     * @param _file  (required)
        The audio file to translate.
     * @param prompt  (optional)
        An optional text to guide the model&#39;s style or continue a previous audio segment. The prompt should be in English.
     * @param responseFormat  (optional)
        The value for the parameter responseFormat
     * @param temperature  (optional, default to 0)
        The sampling temperature, between 0 and 1. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. If set to 0, the model will use log probability to automatically increase the temperature until certain thresholds are hit.
     * @return TranscriptionsCreate200Response
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public TranscriptionsCreate200Response translationsCreate( @Nonnull final String deploymentId,  @Nonnull final String apiVersion,  @Nonnull final org.springframework.core.io.Resource _file,  @Nullable final String prompt,  @Nullable final AudioResponseFormat responseFormat,  @Nullable final BigDecimal temperature) throws OpenApiRequestException {
        final Object localVarPostBody = null;
        
        // verify the required parameter 'deploymentId' is set
        if (deploymentId == null) {
            throw new OpenApiRequestException("Missing the required parameter 'deploymentId' when calling translationsCreate");
        }
        
        // verify the required parameter 'apiVersion' is set
        if (apiVersion == null) {
            throw new OpenApiRequestException("Missing the required parameter 'apiVersion' when calling translationsCreate");
        }
        
        // verify the required parameter '_file' is set
        if (_file == null) {
            throw new OpenApiRequestException("Missing the required parameter '_file' when calling translationsCreate");
        }
        
        // create path and map variables
        final Map<String, Object> localVarPathParams = new HashMap<String, Object>();
        localVarPathParams.put("deployment-id", deploymentId);
        final String localVarPath = UriComponentsBuilder.fromPath("/deployments/{deployment-id}/audio/translations").buildAndExpand(localVarPathParams).toUriString();

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

                localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "api-version", apiVersion));
        

        if (_file != null)
            localVarFormParams.add("file", _file);
        if (prompt != null)
            localVarFormParams.add("prompt", prompt);
        if (responseFormat != null)
            localVarFormParams.add("response_format", responseFormat);
        if (temperature != null)
            localVarFormParams.add("temperature", temperature);

        final String[] localVarAccepts = { 
            "application/json", "text/plain"
        };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "multipart/form-data"
        };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        final String[] localVarAuthNames = new String[] { "apiKey", "bearer" };

        final ParameterizedTypeReference<TranscriptionsCreate200Response> localVarReturnType = new ParameterizedTypeReference<TranscriptionsCreate200Response>() {};
        return apiClient.invokeAPI(localVarPath, HttpMethod.POST, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
    }

    /**
     * <p>Transcribes and translates input audio into English text.</p>
     * <p></p>
     * <p><b>200</b> - OK
     * @param deploymentId
     *      The value for the parameter deploymentId
     * @param apiVersion
     *      The value for the parameter apiVersion
     * @param _file
     *      The audio file to translate.
     * @return TranscriptionsCreate200Response
     * @throws OpenApiRequestException if an error occurs while attempting to invoke the API
     */
    @Nonnull
    public TranscriptionsCreate200Response translationsCreate( @Nonnull final String deploymentId,  @Nonnull final String apiVersion,  @Nonnull final org.springframework.core.io.Resource _file) throws OpenApiRequestException {
        return translationsCreate(deploymentId, apiVersion, _file, null, null, null);
    }
}
