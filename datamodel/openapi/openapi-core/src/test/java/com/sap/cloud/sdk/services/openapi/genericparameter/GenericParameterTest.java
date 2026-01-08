package com.sap.cloud.sdk.services.openapi.genericparameter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.services.openapi.apache.ApiClient;
import com.sap.cloud.sdk.services.openapi.apache.BaseApi;
import com.sap.cloud.sdk.services.openapi.apache.Pair;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;

import lombok.Builder;
import lombok.Value;

@WireMockTest
class GenericParameterTest
{
    final static String EXPECTED_BODY = """
        {\
        "id":"foo",\
        "fieldPopulatedAsStringWithAnnotation":{"foo": "bar"},\
        "fieldPopulatedAsStringWithoutAnnotation":"{\\"foo\\": \\"bar\\"}",\
        "fieldPopulatedAsJacksonJsonNode":{"foo":"bar"}\
        }\
        """;

    @Test
    void testInvocationWithGenericParameter( @Nonnull final WireMockRuntimeInfo wm )
        throws JsonProcessingException
    {

        WireMock
            .stubFor(
                post(urlEqualTo("/api"))
                    .withRequestBody(equalTo(EXPECTED_BODY))
                    .willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

        final DefaultHttpDestination httpDestination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        final String jsonString = "{\"foo\": \"bar\"}";

        //this is how you pass a JSON representation into a parameter of type Object
        final JsonNode jacksonJsonNode = new ObjectMapper().readTree(jsonString);

        final TestModelPOST testModelPOST =
            TestModelPOST
                .builder()
                .id("foo")
                .fieldPopulatedAsStringWithAnnotation(jsonString)
                .fieldPopulatedAsStringWithoutAnnotation(jsonString)
                .fieldPopulatedAsJacksonJsonNode(jacksonJsonNode)
                .build();

        new TestSpringApi(httpDestination).testMethod(testModelPOST);
        new TestApacheApi(httpDestination).testMethod(testModelPOST);
        verify(2, postRequestedFor(urlEqualTo("/api")).withRequestBody(equalTo(EXPECTED_BODY)));
    }

    @Value
    @Builder
    private static class TestModelPOST
    {
        @JsonProperty( "id" )
        String id;

        @JsonProperty( "fieldPopulatedAsStringWithAnnotation" )
        @JsonRawValue
        Object fieldPopulatedAsStringWithAnnotation;

        @JsonProperty( "fieldPopulatedAsStringWithoutAnnotation" )
        Object fieldPopulatedAsStringWithoutAnnotation;

        @JsonProperty( "fieldPopulatedAsJacksonJsonNode" )
        Object fieldPopulatedAsJacksonJsonNode;
    }

    private static class TestApacheApi extends BaseApi
    {
        public TestApacheApi( @Nonnull final Destination destination )
        {
            super(destination);
        }

        @Nullable
        public TestModel testMethod( @Nonnull final TestModelPOST body )
            throws OpenApiRequestException
        {
            final Object localVarPostBody = body;

            // verify the required parameter 'pet' is set
            if( body == null ) {
                throw new OpenApiRequestException("Missing the required parameter 'pet' when calling addPet")
                    .statusCode(400);
            }

            // create path and map variables
            final String localVarPath = "/api";

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

            final TypeReference<TestModel> localVarReturnType = new TypeReference<TestModel>()
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

    }

    private static class TestSpringApi extends AbstractOpenApiService
    {
        public TestSpringApi( @Nonnull final Destination httpDestination )
        {
            super(httpDestination);
        }

        @Nullable
        public TestModel testMethod( @Nonnull final TestModelPOST body )
            throws OpenApiRequestException
        {
            final Object postBody = body;

            // verify the required parameter 'body' is set
            if( body == null ) {
                throw new OpenApiRequestException("Missing the required parameter 'body' when calling createInstances");
            }

            final String path = UriComponentsBuilder.fromPath("/api").build().toUriString();

            final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
            final HttpHeaders headerParams = new HttpHeaders();
            final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

            final String[] accepts = { "application/json" };
            final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
            final String[] contentTypes = { "application/json" };
            final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

            final String[] authNames = new String[] { "Oauth2_AuthorizationCode", "Oauth2_ClientCredentials" };

            final ParameterizedTypeReference<TestModel> returnType = new ParameterizedTypeReference<TestModel>()
            {
            };
            return apiClient
                .invokeAPI(
                    path,
                    HttpMethod.POST,
                    queryParams,
                    postBody,
                    headerParams,
                    formParams,
                    accept,
                    contentType,
                    authNames,
                    returnType);
        }
    }

    private static class TestModel
    {
    }
}
