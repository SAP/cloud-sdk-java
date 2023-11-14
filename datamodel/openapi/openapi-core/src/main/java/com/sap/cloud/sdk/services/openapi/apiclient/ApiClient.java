/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.apiclient;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Accessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.services.openapi.apiclient.auth.ApiKeyAuth;
import com.sap.cloud.sdk.services.openapi.apiclient.auth.Authentication;
import com.sap.cloud.sdk.services.openapi.apiclient.auth.HttpBasicAuth;
import com.sap.cloud.sdk.services.openapi.core.OpenApiRequestException;

/**
 * Used by the OpenAPI VDM to access HTTP-related information, such as the target URL of the headers.
 */
public final class ApiClient
{

    /**
     * Enum representing the delimiter of a given collection.
     */
    public enum CollectionFormat
    {
        /**
         * CSV
         */
        CSV(","),

        /**
         * TSV
         */
        TSV("\t"),

        /**
         * SSV
         */
        SSV(" "),

        /**
         * Pipes
         */
        PIPES("|"),

        /**
         * Multiple
         */
        MULTI(null);

        private final String separator;

        CollectionFormat( final String separator )
        {
            this.separator = separator;
        }

        private String collectionToString( final Collection<? extends CharSequence> collection )
        {
            return StringUtils.collectionToDelimitedString(collection, separator);
        }
    }

    private boolean debugging = false;

    private final HttpHeaders defaultHeaders = new HttpHeaders();

    private String basePath = "will-be-overwritten";

    private final RestTemplate restTemplate;

    private Map<String, Authentication> authentications;

    private int statusCode;
    private MultiValueMap<String, String> responseHeaders;

    private DateFormat dateFormat;

    /**
     * Creates an instance of this class. The rest template will ignore getters and setters names for Jackson
     * properties.
     */
    public ApiClient()
    {
        this.restTemplate = newDefaultRestTemplate();
        init();
    }

    /**
     * Creates an instance of this class given an instance of {@link RestTemplate}.
     *
     * @param restTemplate
     *            An instance of {@link RestTemplate}
     */
    public ApiClient( @Nonnull final RestTemplate restTemplate )
    {
        this.restTemplate = restTemplate;
        init();
    }

    /**
     * Creates an instance of this class given an instance of {@link Destination}. The rest template will ignore getters
     * and setters names for Jackson properties.
     *
     * @param destination
     *            An instance of {@link Destination}
     */
    public ApiClient( @Nonnull final Destination destination )
    {
        this.restTemplate = setRequestFactory(newDefaultRestTemplate(), destination);
        init();
    }

    /**
     * Initializes this ApiClient
     */
    private void init()
    {
        // Use RFC3339 format for date and datetime.
        // See http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14
        this.dateFormat = new RFC3339DateFormat();

        // Use UTC as the default time zone.
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Set default User-Agent.
        setUserAgent("Java-SDK");

        // Setup authentications (key: authentication name, value: authentication).
        authentications = new HashMap<>();
        // Prevent the authentications from being modified.
        authentications = Collections.unmodifiableMap(authentications);
    }

    /**
     * Get the current base path
     *
     * @return String the base path
     */
    @Nonnull
    public String getBasePath()
    {
        return basePath;
    }

    /**
     * Set the base path, which should include the host
     *
     * @param basePath
     *            the base path
     * @return ApiClient this client
     */
    @Nonnull
    public ApiClient setBasePath( @Nonnull final String basePath )
    {
        this.basePath = basePath;
        return this;
    }

    /**
     * Gets the status code of the previous request
     *
     * @return HttpStatus the status code
     */
    public int getStatusCode()
    {
        return statusCode;
    }

    /**
     * Gets the response headers of the previous request
     *
     * @return MultiValueMap a map of response headers
     */
    @Nonnull
    public MultiValueMap<String, String> getResponseHeaders()
    {
        return responseHeaders;
    }

    /**
     * Get authentications (key: authentication name, value: authentication).
     *
     * @return Map the currently configured authentication types
     */
    @Nonnull
    public Map<String, Authentication> getAuthentications()
    {
        return authentications;
    }

    /**
     * Get authentication for the given name.
     *
     * @param authName
     *            The authentication name
     * @return The authentication, null if not found
     */
    @Nonnull
    public Authentication getAuthentication( @Nonnull final String authName )
    {
        return authentications.get(authName);
    }

    /**
     * @return restTemplate
     */
    RestTemplate getRestTemplate()
    {
        return this.restTemplate;
    }

    /**
     * Helper method to set username for the first HTTP basic authentication.
     *
     * @param username
     *            the username
     */
    public void setUsername( @Nonnull final String username )
    {
        for( final Authentication auth : authentications.values() ) {
            if( auth instanceof HttpBasicAuth ) {
                ((HttpBasicAuth) auth).setUsername(username);
                return;
            }
        }
        throw new OpenApiRequestException("No HTTP basic authentication configured!");
    }

    /**
     * Helper method to set password for the first HTTP basic authentication.
     *
     * @param password
     *            the password
     */
    public void setPassword( @Nonnull final String password )
    {
        for( final Authentication auth : authentications.values() ) {
            if( auth instanceof HttpBasicAuth ) {
                ((HttpBasicAuth) auth).setPassword(password);
                return;
            }
        }
        throw new OpenApiRequestException("No HTTP basic authentication configured!");
    }

    /**
     * Helper method to set API key value for the first API key authentication.
     *
     * @param apiKey
     *            the API key
     */
    public void setApiKey( @Nonnull final String apiKey )
    {
        for( final Authentication auth : authentications.values() ) {
            if( auth instanceof ApiKeyAuth ) {
                ((ApiKeyAuth) auth).setApiKey(apiKey);
                return;
            }
        }
        throw new OpenApiRequestException("No API key authentication configured!");
    }

    /**
     * Helper method to set API key prefix for the first API key authentication.
     *
     * @param apiKeyPrefix
     *            the API key prefix
     */
    public void setApiKeyPrefix( @Nonnull final String apiKeyPrefix )
    {
        for( final Authentication auth : authentications.values() ) {
            if( auth instanceof ApiKeyAuth ) {
                ((ApiKeyAuth) auth).setApiKeyPrefix(apiKeyPrefix);
                return;
            }
        }
        throw new OpenApiRequestException("No API key authentication configured!");
    }

    /**
     * Set the User-Agent header's value (by adding to the default header map).
     *
     * @param userAgent
     *            the user agent string
     * @return ApiClient this client
     */
    @Nonnull
    public ApiClient setUserAgent( @Nonnull final String userAgent )
    {
        addDefaultHeader("User-Agent", userAgent);
        return this;
    }

    /**
     * Add a default header.
     *
     * @param name
     *            The header's name
     * @param value
     *            The header's value
     * @return ApiClient this client
     */
    @Nonnull
    public ApiClient addDefaultHeader( @Nonnull final String name, @Nonnull final String value )
    {
        defaultHeaders.remove(name);
        defaultHeaders.add(name, value);
        return this;
    }

    /**
     * Check that whether debugging is enabled for this API client.
     *
     * @return boolean true if this client is enabled for debugging, false otherwise
     */
    public boolean isDebugging()
    {
        return debugging;
    }

    /**
     * Get the date format used to parse/format date parameters.
     *
     * @return DateFormat format
     */
    @Nonnull
    public DateFormat getDateFormat()
    {
        return dateFormat;
    }

    /**
     * Set the date format used to parse/format date parameters.
     *
     * @param dateFormat
     *            Date format
     * @return API client
     */
    @Nonnull
    public ApiClient setDateFormat( @Nonnull final DateFormat dateFormat )
    {
        this.dateFormat = dateFormat;
        return this;
    }

    /**
     * Parse the given string into Date object.
     *
     * @param str
     *            the string to parse
     * @return the Date parsed from the string
     */
    @Nonnull
    public Date parseDate( @Nonnull final String str )
    {
        try {
            return dateFormat.parse(str);
        }
        catch( final ParseException e ) {
            throw new OpenApiRequestException(e);
        }
    }

    /**
     * Format the given Date object into string.
     *
     * @param date
     *            the date to format
     * @return the formatted date as string
     */
    @Nonnull
    public String formatDate( @Nonnull final Date date )
    {
        return dateFormat.format(date);
    }

    /**
     * Format the given parameter object into string.
     *
     * @param param
     *            the object to convert
     * @return String the parameter represented as a String
     */
    @Nonnull
    public String parameterToString( @Nonnull final Object param )
    {
        if( param instanceof Date ) {
            return formatDate((Date) param);
        } else if( param instanceof Collection ) {
            final StringBuilder b = new StringBuilder();
            for( final Object o : (Collection<?>) param ) {
                if( b.length() > 0 ) {
                    b.append(",");
                }
                b.append(o);
            }
            return b.toString();
        } else {
            return String.valueOf(param);
        }
    }

    /**
     * Formats the specified collection path parameter to a string value.
     *
     * @param collectionFormat
     *            The collection format of the parameter.
     * @param values
     *            The values of the parameter.
     * @return String representation of the parameter
     */
    @Nonnull
    public String collectionPathParameterToString(
        @Nullable CollectionFormat collectionFormat,
        @Nonnull final Collection<? extends CharSequence> values )
    {
        // create the value based on the collection format
        if( CollectionFormat.MULTI.equals(collectionFormat) ) {
            // not valid for path params
            return parameterToString(values);
        }

        // collectionFormat is assumed to be "csv" by default
        if( collectionFormat == null ) {
            collectionFormat = CollectionFormat.CSV;
        }

        return collectionFormat.collectionToString(values);
    }

    /**
     * Converts a parameter to a {@link MultiValueMap} for use in REST requests
     *
     * @param collectionFormat
     *            The format to convert to
     * @param name
     *            The name of the parameter
     * @param value
     *            The parameter's value Can be {@link Map},{@link Collection} or also be a generic {@link Object}
     * @return a Map containing the String value(s) of the input parameter
     */
    @Nonnull
    public MultiValueMap<String, String> parameterToMultiValueMap(
        @Nullable CollectionFormat collectionFormat,
        @Nullable final String name,
        @Nullable final Object value )
    {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        if( name == null || name.isEmpty() || value == null ) {
            return params;
        }

        if( collectionFormat == null ) {
            collectionFormat = CollectionFormat.CSV;
        }

        if( value instanceof Map ) {
            @SuppressWarnings( "unchecked" )
            final Map<String, Object> valuesMap = (Map<String, Object>) value;
            for( final Entry<String, Object> entry : valuesMap.entrySet() ) {
                params.add(entry.getKey(), parameterToString(entry.getValue()));
            }
            return params;
        }

        Collection<?> valueCollection;
        if( value instanceof Collection ) {
            valueCollection = (Collection<?>) value;
        } else {
            params.add(name, parameterToString(value));
            return params;
        }

        if( valueCollection.isEmpty() ) {
            return params;
        }

        if( collectionFormat.equals(CollectionFormat.MULTI) ) {
            for( final Object item : valueCollection ) {
                params.add(name, parameterToString(item));
            }
            return params;
        }

        final List<String> values = new ArrayList<>();
        for( final Object o : valueCollection ) {
            values.add(parameterToString(o));
        }
        params.add(name, collectionFormat.collectionToString(values));

        return params;
    }

    /**
     * Check if the given {@code String} is a JSON MIME.
     *
     * @param mediaType
     *            the input MediaType
     * @return boolean true if the MediaType represents JSON, false otherwise
     */
    public boolean isJsonMime( @Nonnull final String mediaType )
    {
        // "* / *" is default to JSON
        if( "*/*".equals(mediaType) ) {
            return true;
        }

        try {
            return isJsonMime(MediaType.parseMediaType(mediaType));
        }
        catch( final InvalidMediaTypeException e ) {
            throw new OpenApiRequestException(e);
        }
    }

    /**
     * Check if the given MIME is a JSON MIME. JSON MIME examples: application/json application/json; charset=UTF8
     * APPLICATION/JSON
     *
     * @param mediaType
     *            the input MediaType
     * @return boolean true if the MediaType represents JSON, false otherwise
     */
    public boolean isJsonMime( @Nonnull final MediaType mediaType )
    {
        return MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)
            || mediaType.getSubtype().matches("^.*\\+json[;]?\\s*$");
    }

    /**
     * Check if the given {@code String} is a Problem JSON MIME (RFC-7807).
     *
     * @param mediaType
     *            the input MediaType
     * @return boolean true if the MediaType represents Problem JSON, false otherwise
     */
    public boolean isProblemJsonMime( @Nonnull final String mediaType )
    {
        return "application/problem+json".equalsIgnoreCase(mediaType);
    }

    /**
     * Select the Accept header's value from the given accepts array: if JSON exists in the given array, use it;
     * otherwise use all of them (joining into a string)
     *
     * @param accepts
     *            The accepts array to select from
     * @return List The list of MediaTypes to use for the Accept header
     */
    @Nullable
    public List<MediaType> selectHeaderAccept( @Nonnull final String[] accepts )
    {
        if( accepts.length == 0 ) {
            return null;
        }
        for( final String accept : accepts ) {
            final MediaType mediaType = MediaType.parseMediaType(accept);
            if( isJsonMime(mediaType) && !isProblemJsonMime(accept) ) {
                return Collections.singletonList(mediaType);
            }
        }
        return MediaType.parseMediaTypes(StringUtils.arrayToCommaDelimitedString(accepts));
    }

    /**
     * Select the Content-Type header's value from the given array: if JSON exists in the given array, use it; otherwise
     * use the first one of the array.
     *
     * @param contentTypes
     *            The Content-Type array to select from
     * @return MediaType The Content-Type header to use. If the given array is empty, JSON will be used.
     */
    @Nonnull
    public MediaType selectHeaderContentType( @Nonnull final String[] contentTypes )
    {
        if( contentTypes.length == 0 ) {
            return MediaType.APPLICATION_JSON;
        }
        for( final String contentType : contentTypes ) {
            final MediaType mediaType = MediaType.parseMediaType(contentType);
            if( isJsonMime(mediaType) ) {
                return mediaType;
            }
        }
        return MediaType.parseMediaType(contentTypes[0]);
    }

    /**
     * Select the body to use for the request
     *
     * @param obj
     *            the body object
     * @param formParams
     *            the form parameters
     * @param contentType
     *            the content type of the request
     * @return Object the selected body
     */
    private
        Object
        selectBody( final Object obj, final MultiValueMap<String, Object> formParams, final MediaType contentType )
    {
        final boolean isForm =
            MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType)
                || MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType);
        return isForm ? formParams : obj;
    }

    /**
     * Invoke API by sending HTTP request with the given options.
     *
     * @param <T>
     *            the return type to use
     * @param path
     *            The sub-path of the HTTP URL
     * @param method
     *            The request method
     * @param queryParams
     *            The query parameters
     * @param body
     *            The request body object
     * @param headerParams
     *            The header parameters
     * @param formParams
     *            The form parameters
     * @param accept
     *            The request's Accept header
     * @param contentType
     *            The request's Content-Type header
     * @param authNames
     *            The authentications to apply
     * @param returnType
     *            The return type into which to deserialize the response
     * @return The response body in chosen type
     * @throws OpenApiRequestException
     *             Thrown in case an exception occurs during invocation of the REST API
     */
    @Nullable
    public <T> T invokeAPI(
        @Nonnull final String path,
        @Nonnull final HttpMethod method,
        @Nullable final MultiValueMap<String, String> queryParams,
        @Nullable final Object body,
        @Nullable final HttpHeaders headerParams,
        @Nullable final MultiValueMap<String, Object> formParams,
        @Nullable final List<MediaType> accept,
        @Nullable final MediaType contentType,
        @Nullable final String[] authNames,
        @Nonnull final ParameterizedTypeReference<T> returnType )
        throws OpenApiRequestException
    {
        // auth headers are added automatically by the SDK
        // updateParamsForAuth(authNames, queryParams, headerParams);

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(basePath).path(path);
        if( queryParams != null ) {
            //encode the query parameters in case they contain unsafe characters
            for( final List<String> values : queryParams.values() ) {
                if( values != null ) {
                    for( int i = 0; i < values.size(); i++ ) {
                        try {
                            values.set(i, URLEncoder.encode(values.get(i), "utf8"));
                        }
                        catch( final UnsupportedEncodingException e ) {
                            throw new OpenApiRequestException(e);
                        }
                    }
                }
            }
            builder.queryParams(queryParams);
        }

        final URI uri;
        try {
            uri = new URI(builder.build().toUriString());
        }
        catch( final URISyntaxException ex ) {
            throw new OpenApiRequestException("Could not build URL: " + builder.toUriString(), ex);
        }

        final BodyBuilder requestBuilder = RequestEntity.method(method, uri);
        if( accept != null ) {
            requestBuilder.accept(accept.toArray(new MediaType[0]));
        }
        if( contentType != null ) {
            requestBuilder.contentType(contentType);
        }

        addHeadersToRequest(headerParams, requestBuilder);
        addHeadersToRequest(defaultHeaders, requestBuilder);

        final RequestEntity<Object> requestEntity = requestBuilder.body(selectBody(body, formParams, contentType));

        final ResponseEntity<T> responseEntity = restTemplate.exchange(requestEntity, returnType);

        statusCode = responseEntity.getStatusCode().value();
        responseHeaders = responseEntity.getHeaders();

        if( statusCode == 204 ) {
            return null;
        } else if( statusCode >= 200 && statusCode < 300 ) {
            return responseEntity.getBody();
        } else {
            // The error handler built into the RestTemplate should handle 400 and 500 series errors.
            throw new OpenApiRequestException(
                "API returned " + statusCode + " and it wasn't handled by the RestTemplate error handler");
        }
    }

    /**
     * Add headers to the request that is being built
     *
     * @param headers
     *            The headers to add
     * @param requestBuilder
     *            The current request
     */
    private void addHeadersToRequest( @Nullable final HttpHeaders headers, final BodyBuilder requestBuilder )
    {
        if( headers != null ) {
            for( final Entry<String, List<String>> entry : headers.entrySet() ) {
                final List<String> values = entry.getValue();
                for( final String value : values ) {
                    if( value != null ) {
                        requestBuilder.header(entry.getKey(), value);
                    }
                }
            }
        }
    }

    @Nonnull
    private static RestTemplate newDefaultRestTemplate()
    {
        final RestTemplate restTemplate = new RestTemplate();

        final ObjectMapper objectMapper = newDefaultObjectMapper();
        restTemplate
            .getMessageConverters()
            .stream()
            .filter(MappingJackson2HttpMessageConverter.class::isInstance)
            .map(MappingJackson2HttpMessageConverter.class::cast)
            .forEach(converter -> converter.setObjectMapper(objectMapper));

        return restTemplate;
    }

    @Nonnull
    private static ObjectMapper newDefaultObjectMapper()
    {
        return new Jackson2ObjectMapperBuilder()
            .modules(new JavaTimeModule())
            .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .visibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
            .build();
    }

    @Nonnull
    private static
        RestTemplate
        setRequestFactory( @Nonnull final RestTemplate restTemplate, @Nonnull final Destination destination )
    {
        // instantiate template with prepared HttpClient, featuring repeated response reading
        final HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();

        try {
            httpRequestFactory.setHttpClient(ApacheHttpClient5Accessor.getHttpClient(destination));
        }
        catch( final Exception e ) {
            throw new IllegalStateException("Unable to set the HttpClient for the RestTemplate.", e);
        }

        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpRequestFactory));

        return restTemplate;
    }
}
