/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Streams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonToken;
import com.sap.cloud.sdk.datamodel.odata.client.JsonPath;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.ODataResponseDeserializer;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.cloud.sdk.result.ResultCollection;
import com.sap.cloud.sdk.result.ResultElement;
import com.sap.cloud.sdk.result.ResultObject;
import com.sap.cloud.sdk.result.ResultPrimitive;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * OData request result for reading entities.
 */
@Slf4j
@EqualsAndHashCode
public class ODataRequestResultGeneric
    implements
    ODataRequestResult,
    ODataRequestResultDeserializable,
    ODataRequestResultPagination
{
    private final ODataResponseDeserializer deserializer;

    @Nullable
    private volatile HttpResponse bufferedHttpResponse;
    private volatile boolean isBufferHttpResponse = true;

    @Getter
    @Nonnull
    private final ODataRequestGeneric oDataRequest;

    @Nonnull
    private final HttpResponse httpResponse;

    private NumberDeserializationStrategy numberStrategy = NumberDeserializationStrategy.DOUBLE;

    @Nonnull
    private final ODataProtocol protocol;

    @Getter( AccessLevel.PACKAGE )
    @Nullable
    private final transient HttpClient httpClient;

    /**
     * Default constructor.
     *
     * @param oDataRequest
     *            The original OData request
     * @param httpResponse
     *            The original Http response
     */
    public ODataRequestResultGeneric(
        @Nonnull final ODataRequestGeneric oDataRequest,
        @Nonnull final HttpResponse httpResponse )
    {
        this(oDataRequest, httpResponse, null);
    }

    /**
     *
     * Default constructor with enabled pagination.
     *
     * @param oDataRequest
     *            The original OData request
     * @param httpResponse
     *            The original Http response
     * @param httpClient
     *            The original Http client
     */
    public ODataRequestResultGeneric(
        @Nonnull final ODataRequestGeneric oDataRequest,
        @Nonnull final HttpResponse httpResponse,
        @Nullable final HttpClient httpClient )
    {
        this.oDataRequest = oDataRequest;
        this.httpResponse = httpResponse;
        this.httpClient = httpClient;
        this.protocol = oDataRequest.getProtocol();

        deserializer = new ODataResponseDeserializer(protocol);
    }

    /**
     * Set the default number deserialization strategy for generic JSON numbers without target type mapping.
     *
     * @param numberStrategy
     *            The number deserialization strategy to use.
     * @return The same instance of {@link ODataRequestGeneric}.
     */
    @Nonnull
    public ODataRequestResultGeneric withNumberDeserializationStrategy(
        @Nonnull final NumberDeserializationStrategy numberStrategy )
    {
        this.numberStrategy = numberStrategy;
        return this;
    }

    /**
     * Method that allows consumers to disable buffering HTTP response entity. Note that once this is disabled, HTTP
     * responses can only be streamed/read once
     *
     */
    public void disableBufferingHttpResponse()
    {
        if( bufferedHttpResponse == null ) {
            isBufferHttpResponse = false;
        } else {
            log.warn("Buffering the HTTP response cannot be disabled! The content has already been buffered.");
        }
    }

    /**
     * Method that creates a {@link BufferedHttpEntity} from the {@link HttpEntity} if buffering the HTTP response is
     * not turned off by using {@link ODataRequestResultGeneric#disableBufferingHttpResponse()}.
     *
     * @return An HttpResponse
     */
    @Nonnull
    @Override
    public HttpResponse getHttpResponse()
    {
        if( !isBufferHttpResponse ) {
            log.debug("Buffering is disabled, returning unbuffered http response");
            return httpResponse;
        }

        if( bufferedHttpResponse != null ) {
            return Objects.requireNonNull(bufferedHttpResponse);
        }

        synchronized( this ) {
            if( bufferedHttpResponse != null ) {
                return Objects.requireNonNull(bufferedHttpResponse);
            }

            final StatusLine statusLine = httpResponse.getStatusLine();
            final HttpEntity httpEntity = httpResponse.getEntity();
            if( statusLine == null || httpEntity == null ) {
                log
                    .debug(
                        "skipping buffering of http entity as either there is no http entity or response does not include a status-line.");
                return httpResponse;
            }

            final Try<HttpEntity> entity = Try.of(() -> new BufferedHttpEntity(httpEntity));
            if( entity.isFailure() ) {
                log.warn("Failed to buffer HTTP response. Unable to buffer HTTP entity.", entity.getCause());
                return httpResponse;
            }

            final BasicHttpResponse proxyResponse = new BasicHttpResponse(statusLine);
            proxyResponse.setHeaders(httpResponse.getAllHeaders());
            proxyResponse.setEntity(entity.get());
            Option.of(httpResponse.getLocale()).peek(proxyResponse::setLocale);
            bufferedHttpResponse = proxyResponse;
        }

        return Objects.requireNonNull(bufferedHttpResponse);
    }

    @Override
    public void streamElements( @Nonnull final Consumer<ResultElement> handler )
    {
        final GsonResultElementFactory resultElementFactory = getResultElementFactory();

        final Integer numConsumedElements = HttpEntityReader.stream(this, reader -> {
            deserializer.positionReaderToResultSet(reader);

            int count = 0;
            while( reader.hasNext() && reader.peek() == JsonToken.BEGIN_OBJECT ) {
                final JsonElement jsonElement = JsonParser.parseReader(reader);
                final ResultElement resultElement = resultElementFactory.create(jsonElement);
                handler.accept(resultElement);
                count++;
            }
            reader.close();
            return count;
        });

        log.debug("Iterated {} elements.", numConsumedElements);
    }

    private GsonResultElementFactory getResultElementFactory()
    {
        final GsonBuilder gsonBuilder = ODataGsonBuilder.newGsonBuilder(numberStrategy);
        return new GsonResultElementFactory(gsonBuilder);
    }

    /**
     * Try to extract a version identifier from the ETag header.
     *
     * @return An option holding the version identifier or {@link Option.None}, if none was found.
     */
    @Nonnull
    public Option<String> getVersionIdentifierFromHeader()
    {
        return Option.ofOptional(Streams.stream(getHeaderValues("ETag")).filter(StringUtils::isNotEmpty).findFirst());
    }

    @Nonnull
    private ResultPrimitive loadPrimitiveFromResponse(
        @Nonnull final Function<JsonElement, JsonElement> jsonElementExtractor )
    {
        final GsonResultElementFactory elementFactory = getResultElementFactory();
        final ResultPrimitive result = HttpEntityReader.read(this, element -> {
            final Option<ResultPrimitive> single =
                deserializer
                    .getElementToResultPrimitiveSingle(element)
                    .map(jsonElementExtractor)
                    .map(elementFactory::create)
                    .map(ResultElement::getAsPrimitive);
            return single.getOrNull();
        });
        if( result == null ) {
            log.debug("{} response cannot be read as a primitive value.", protocol);
            throw new ODataDeserializationException(
                getODataRequest(),
                getHttpResponse(),
                "Unable to read " + protocol + " response.",
                null);
        }
        return result;
    }

    @Nonnull
    private ResultCollection loadPrimitiveCollectionFromResponse()
    {
        final GsonResultElementFactory elementFactory = getResultElementFactory();

        final ResultCollection result = HttpEntityReader.read(this, element -> {
            final Option<ResultCollection> set =
                deserializer
                    .getElementToResultPrimitiveSet(element)
                    .map(elementFactory::create)
                    .map(ResultElement::getAsCollection);
            return set.getOrNull();
        });
        if( result == null ) {
            log.debug("{} response cannot be read as set of primitive values.", protocol);
            throw new ODataDeserializationException(
                getODataRequest(),
                getHttpResponse(),
                "Unable to read " + protocol + " response.",
                null);
        }
        return result;
    }

    @Nonnull
    private ResultObject loadEntryFromResponse( @Nonnull final Function<JsonElement, JsonElement> jsonElementExtractor )
    {
        final GsonResultElementFactory elementFactory = getResultElementFactory();
        final ResultObject result = HttpEntityReader.read(this, element -> {
            final Option<ResultObject> single =
                deserializer
                    .getElementToResultSingle(element)
                    .map(jsonElementExtractor)
                    .map(elementFactory::create)
                    .map(ResultElement::getAsObject);
            return single.getOrNull();
        });
        if( result == null ) {
            log.debug("{} response cannot be read as a single entity.", protocol);
            throw new ODataDeserializationException(
                getODataRequest(),
                getHttpResponse(),
                "Unable to read " + protocol + " response.",
                null);
        }
        return result;
    }

    @Nonnull
    private ResultCollection loadEntryCollectionFromResponse()
    {
        final GsonResultElementFactory elementFactory = getResultElementFactory();

        final ResultCollection result = HttpEntityReader.read(this, element -> {
            final Option<ResultCollection> set =
                deserializer
                    .getElementToResultSet(element)
                    .map(elementFactory::create)
                    .map(ResultElement::getAsCollection);
            return set.getOrNull();
        });
        if( result == null ) {
            log.debug("{} response cannot be read as set of entities.", protocol);
            throw new ODataDeserializationException(
                getODataRequest(),
                getHttpResponse(),
                "Unable to read " + protocol + " response.",
                null);
        }
        return result;
    }

    @Nonnull
    private ResultElement getResultElement()
    {
        return loadEntryFromResponse(Function.identity());
    }

    @Nonnull
    Iterable<ResultElement> getResultElements()
    {
        return loadEntryCollectionFromResponse();
    }

    @Nonnull
    @Override
    public Iterator<ResultElement> iterator()
    {
        assertNonEmptyPayload();
        return getResultElements().iterator();
    }

    @Override
    @Nonnull
    public <T> T as( @Nonnull final Class<T> objectType )
    {
        return as(objectType, Function.identity());
    }

    /**
     * Converts ODataRequestResult into a POJO based on a function extracting the relevant JSON response object.
     *
     * @param objectType
     *            type of POJO
     * @param <T>
     *            The generic type of POJO
     * @param resultExtractor
     *            A function extracting the relevant result object of the JSON root object. In case of OData V2 the
     *            {@code "d"} object is treated as the root object. Pass {@link Function#identity()} in case no
     *            transformation should take place.
     * @return T - POJO
     *
     * @throws ODataResponseException
     *             When the HTTP status indicates an erroneous response.
     * @throws ODataDeserializationException
     *             When deserialization process failed for the OData response object.
     */
    @Nonnull
    public <
        T> T as( @Nonnull final Class<T> objectType, @Nonnull final Function<JsonElement, JsonElement> resultExtractor )
    {
        assertNonEmptyPayload();
        assertResultTypeIsNotVoid(objectType);
        if( isPrimitiveOrWrapperOrString(objectType) ) {
            @Nullable
            final ContentType contentType = ContentType.get(getHttpResponse().getEntity());

            // parse text/plain responses directly, do not use JSON deserializers
            if( contentType != null
                && Objects.equals(contentType.getMimeType(), ContentType.TEXT_PLAIN.getMimeType()) ) {
                return getPrimitiveObjectFromPlainText(objectType);
            }

            return getPrimitiveObjectFromJson(objectType, resultExtractor);
        } else {
            return getComplexObjectFromJson(objectType, resultExtractor);
        }
    }

    @Nonnull
    private <T> T getComplexObjectFromJson(
        @Nonnull final Class<T> objectType,
        @Nonnull final Function<JsonElement, JsonElement> resultExtractor )
    {
        final ResultObject resultObject = loadEntryFromResponse(resultExtractor);

        final ODataRequestGeneric r = getODataRequest();

        return Try
            .of(() -> resultObject.as(objectType))
            .onFailure(e -> log.debug("Failed to deserialize {} from JSON response.", objectType))
            .getOrElseThrow(
                e -> new ODataDeserializationException(
                    r,
                    getHttpResponse(),
                    "Failed to deserialize a complex object.",
                    e));
    }

    @Nonnull
    private <T> T getPrimitiveObjectFromJson(
        @Nonnull final Class<T> objectType,
        @Nonnull final Function<JsonElement, JsonElement> resultExtractor )
    {
        final ResultPrimitive resultPrimitive = loadPrimitiveFromResponse(resultExtractor);

        final ODataRequestGeneric r = getODataRequest();

        return Try
            .of(() -> getPrimitiveAsType(resultPrimitive, objectType))
            .onFailure(e -> log.debug("Failed to deserialize {} from JSON response.", objectType))
            .getOrElseThrow(
                e -> new ODataDeserializationException(
                    r,
                    getHttpResponse(),
                    "Failed to deserialize a primitive object.",
                    e));
    }

    @Nonnull
    private <T> T getPrimitiveObjectFromPlainText( @Nonnull final Class<T> objectType )
        throws ODataDeserializationException
    {
        final ODataRequestGeneric r = getODataRequest();
        final HttpResponse httpResponse = getHttpResponse();

        final String objectText =
            Try
                .of(() -> EntityUtils.toString(getHttpResponse().getEntity(), StandardCharsets.UTF_8))
                .getOrElseThrow(
                    e -> new ODataDeserializationException(r, httpResponse, "Failed to parse HTTP response.", e));

        return Try
            .of(() -> new Gson().fromJson(objectText, objectType))
            .filterTry(
                Objects::nonNull,
                () -> new ODataDeserializationException(r, httpResponse, "The response is null.", null))
            .onFailure(e -> log.debug("Failed to deserialize {} from text/plain response: {}", objectType, objectText))
            .getOrElseThrow(
                e -> new ODataDeserializationException(
                    r,
                    httpResponse,
                    "Failed to deserialize a primitive object.",
                    e));
    }

    /*
     * Helper function to check if the objectType passed in any of the primitive or wrapper types (Boolean, Byte,
     * Character, Short, Integer, Long, Double, Float) or is String.
     */
    private boolean isPrimitiveOrWrapperOrString( @Nonnull final Class<?> objectType )
    {
        return ClassUtils.isPrimitiveOrWrapper(objectType) || objectType == String.class;
    }

    @Nonnull
    @SuppressWarnings( "unchecked" )
    private <T> T as( @Nonnull final Type objectType )
    {
        final Class<T> typeClass;
        if( objectType instanceof ParameterizedType ) {
            typeClass = (Class<T>) ((ParameterizedType) objectType).getRawType();
        } else {
            typeClass = (Class<T>) objectType.getClass();
        }
        return as(typeClass);
    }

    @Override
    @Nonnull
    public <T> List<T> asList( @Nonnull final Class<T> objectType )
    {
        assertNonEmptyPayload();
        assertResultTypeIsNotVoid(objectType);
        final ResultCollection result =
            isPrimitiveOrWrapperOrString(objectType)
                ? loadPrimitiveCollectionFromResponse()
                : loadEntryCollectionFromResponse();

        return Try
            .of(() -> result.asList(objectType))
            .onFailure(e -> log.debug("Failed to parse {} result to a list of {}", protocol, objectType))
            .getOrElseThrow(
                e -> new ODataDeserializationException(
                    getODataRequest(),
                    getHttpResponse(),
                    "Failed to parse " + protocol + " result to a list.",
                    e));
    }

    @Nonnull
    @SuppressWarnings( "unchecked" )
    private <T> List<T> asList( @Nonnull final Type objectType )
    {
        final Class<T> typeClass;
        if( objectType instanceof ParameterizedType ) {
            typeClass = (Class<T>) ((ParameterizedType) objectType).getRawType();
        } else {
            typeClass = (Class<T>) objectType.getClass();
        }
        return asList(typeClass);
    }

    @Override
    public long getInlineCount()
    {
        assertNonEmptyPayload();
        for( final JsonPath path : getODataRequest().getProtocol().getPathToInlineCount().getPaths() ) {
            final ResultElement resultElement = getResultElement(path);
            if( resultElement != null ) {
                return resultElement.getAsPrimitive().asLong();
            }
        }

        final String message = "Inline count not found in " + protocol + " response payload.";
        throw new ODataDeserializationException(oDataRequest, getHttpResponse(), message, null);
    }

    @Override
    @Nonnull
    public Option<String> getNextLink()
    {
        log.debug("Checking for a next link on current page.");
        assertNonEmptyPayload();
        for( final JsonPath path : getODataRequest().getProtocol().getPathToNextLink().getPaths() ) {
            final ResultElement resultElement = getResultElement(path);
            if( resultElement != null ) {
                return Option
                    .of(resultElement)
                    .map(ResultElement::asString)
                    .peek(link -> log.debug("Found reference to next page: {}", link));
            }
        }
        log.debug("Result does not reference any further pages.");
        return Option.none();
    }

    /**
     * Get the delta link of the current result-set.
     *
     * @return the OData protocol specific value of delta link property.
     */
    @Nonnull
    public Option<String> getDeltaLink()
    {
        log.debug("Checking for a delta link on current page.");
        assertNonEmptyPayload();
        for( final JsonPath path : getODataRequest().getProtocol().getPathToDeltaLink().getPaths() ) {
            final ResultElement resultElement = getResultElement(path);
            if( resultElement != null ) {
                return Option
                    .of(resultElement)
                    .map(ResultElement::asString)
                    .peek(link -> log.debug("Found reference to delta page: {}", link));
            }
        }
        log.debug("Result does not contain a delta reference.");
        return Option.none();
    }

    @Nullable
    private ResultElement getResultElement( @Nonnull final JsonPath path )
    {
        ResultElement resultElement = getResultElement();
        final List<String> nodes = path.getNodes();
        for( int i = 0; i < nodes.size() && resultElement != null && resultElement.isResultObject(); i++ ) {
            resultElement = resultElement.getAsObject().get(nodes.get(i));
        }
        return resultElement;
    }

    @Override
    @Nonnull
    public Map<String, Object> asMap()
    {
        assertNonEmptyPayload();
        final Type type = new TypeToken<Map<String, Object>>()
        {
            private static final long serialVersionUID = 42L;
        }.getType();
        return as(type);
    }

    @Override
    @Nonnull
    public List<Map<String, Object>> asListOfMaps()
    {
        assertNonEmptyPayload();
        final Type type = new TypeToken<Map<String, Object>>()
        {
            private static final long serialVersionUID = 42L;
        }.getType();
        return asList(type);
    }

    @Nonnull
    private <T> T getPrimitiveAsType( @Nonnull final ResultPrimitive primitive, @Nonnull final Class<T> type )
        throws IllegalArgumentException
    {
        final Object primitiveAsType;
        try {
            if( type == Boolean.class ) {
                primitiveAsType = primitive.asBoolean();
            } else if( type == Byte.class ) {
                primitiveAsType = primitive.asByte();
            } else if( type == Short.class ) {
                primitiveAsType = primitive.asShort();
            } else if( type == Integer.class ) {
                primitiveAsType = primitive.asInteger();
            } else if( type == Long.class ) {
                primitiveAsType = primitive.asLong();
            } else if( type == BigInteger.class ) {
                primitiveAsType = primitive.asBigInteger();
            } else if( type == Float.class ) {
                primitiveAsType = primitive.asFloat();
            } else if( type == Double.class ) {
                primitiveAsType = primitive.asDouble();
            } else if( type == BigDecimal.class ) {
                primitiveAsType = primitive.asBigDecimal();
            } else if( type == Character.class ) {
                primitiveAsType = primitive.asCharacter();
            } else if( type == String.class ) {
                primitiveAsType = primitive.asString();
            } else {
                throw new IllegalArgumentException(
                    "Failed to convert primitive '"
                        + primitive.asString()
                        + "' to unsupported type: "
                        + type.getName()
                        + ".");
            }
        }
        catch( final UnsupportedOperationException e ) {
            throw new IllegalArgumentException(
                "Failed to convert primitive '" + primitive.asString() + "' to type: " + type.getName() + ".",
                e);
        }

        @SuppressWarnings( "unchecked" )
        final T result = (T) primitiveAsType;
        return result;
    }

    @Override
    @Nonnull
    public Try<ODataRequestResultGeneric> tryGetNextPage()
    {
        final ODataRequestGeneric rawRequest = getODataRequest();
        if( !(rawRequest instanceof ODataRequestRead) ) {
            return Try.failure(new IllegalStateException("Pagination is only applicable for read requests."));
        }
        final ODataRequestRead request = (ODataRequestRead) rawRequest;

        final HttpClient httpClient = getHttpClient();
        if( httpClient == null ) {
            final String message =
                "Unable to access response of next page: HTTP client was not provided when creating this response object.";
            return Try.failure(new ODataRequestException(request, message, null));
        }

        final Try<String> nextQuery =
            getNextLink()
                .toTry(() -> new IllegalStateException("Current page of result-set does not reference a next page."))
                .map(URI::create)
                .map(URI::getRawQuery);

        if( nextQuery.isFailure() ) {
            final String message = "Unable to extract query parameters for querying next page of result-set.";
            return Try.failure(new ODataRequestException(request, message, nextQuery.getCause()));
        }

        log.debug("Querying {} service for next page {}", protocol, nextQuery.get());

        // create next read request
        final ODataRequestRead nextReadRequest =
            new ODataRequestRead(
                request.getServicePath(),
                request.getResourcePath(),
                nextQuery.get(),
                request.getProtocol());

        // populate headers
        request.getHeaders().forEach(nextReadRequest::setHeader);

        // execute request
        return Try.of(() -> nextReadRequest.execute(httpClient));
    }

    /**
     * Check whether or not the HttpResponse contains (potentially empty) payload.
     *
     * @return True, if the HTTP response contains an {@link HttpEntity}.
     */
    public boolean hasPayload()
    {
        return getHttpResponse().getEntity() != null;
    }

    /**
     * @throws ODataDeserializationException
     *             if the response doesn't contain any payload
     */
    private void assertNonEmptyPayload()
    {
        if( !hasPayload() ) {
            throw new ODataDeserializationException(
                getODataRequest(),
                getHttpResponse(),
                protocol + " response did not contain any payload.",
                null);
        }
    }

    /**
     * @throws IllegalArgumentException
     *             in case the passed class is {@link Void}
     */
    private void assertResultTypeIsNotVoid( @Nonnull final Class<?> cls )
    {
        if( Void.class.equals(cls) ) {
            throw new IllegalArgumentException("Interpreting results as Void is not allowed.");
        }

    }
}
