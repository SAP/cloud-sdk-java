/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.exception;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.result.ElementName;
import com.sap.cloud.sdk.result.ResultElement;
import com.sap.cloud.sdk.result.ResultObject;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * OData error to serve the standard specification.
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class ODataServiceError implements ODataServiceErrorDetails
{
    private static final String ERROR_DETAILS_FIELD = "errordetails";

    @Nonnull
    @Getter
    @SerializedName( "code" )
    @ElementName( "code" )
    private final String oDataCode;

    @Nonnull
    @Getter
    @SerializedName( "message" )
    @ElementName( "message" )
    @JsonAdapter( MessageDeserializer.class )
    private final String oDataMessage;

    @Nullable
    @SerializedName( "target" )
    @ElementName( "target" )
    private final String target;

    @Nullable
    @SerializedName( "details" )
    @ElementName( "details" )
    @JsonAdapter( DetailsDeserializer.class )
    private List<ODataServiceErrorDetails> details;

    @Nullable
    @Setter( AccessLevel.NONE )
    @SerializedName( "innererror" )
    @ElementName( "innererror" )
    private Map<String, Object> innerError;

    /**
     * A list of all contained nested {@link ODataServiceErrorDetails ODataServiceErrors}. If none were found in the
     * response this list is empty. In case of OData V2 this corresponds to the {@code errordetails} field of
     * {@code innererror}.
     *
     * @return A potentially empty List of error details.
     *
     * @see #getInnerError()
     */
    @Nonnull
    public List<ODataServiceErrorDetails> getDetails()
    {
        return details != null ? details : Collections.emptyList();
    }

    /**
     * The {@code innererror} field of the response as a key-value map. If this field was not present on the response
     * this map will be empty. In case of OData V2 the nested field {@code errordetails} is available separately via
     * {@link #getDetails()}.
     *
     * @return A potentially empty Map containing the contents of the {@code innererror} field.
     *
     * @see #getDetails()
     */
    @Nonnull
    public Map<String, Object> getInnerError()
    {
        return innerError != null ? innerError : Collections.emptyMap();
    }

    /**
     * SDK internal method to construct an OData error from a {@link ResultObject}.
     *
     * @param resultObject
     *            The {@link ResultObject} that should be parsed.
     * @param protocol
     *            The {@link ODataProtocol OData protocol version} that should be assumed.
     * @return A new {@code ODataServiceError}.
     *
     * @throws UnsupportedOperationException
     *             if parsing the result object failed.
     */
    @Nonnull
    public static
        ODataServiceError
        fromResultObject( @Nonnull final ResultObject resultObject, @Nonnull final ODataProtocol protocol )
            throws UnsupportedOperationException
    {
        // OData V4 response are parsed normally by GSON
        if( protocol == ODataProtocol.V4 ) {
            return resultObject.as(ODataServiceError.class);
        }

        // OData V2 response contains the details within the innererror field
        // e.g. { "error" : { "innererror" : { "errordetails" : [...] } } }
        final String preparedErrorMessage =
            "Could not interpret the \"errordetails\" field of the "
                + protocol
                + " error as a list of OData errors. The list of details on the OData error will be empty.";

        final Option<List<ODataServiceError>> maybeDetails =
            Option
                .of(resultObject.get("innererror"))
                .filter(r -> r != null && r.isResultObject())
                .map(r -> r.getAsObject().get(ERROR_DETAILS_FIELD))
                .filter(r -> r != null && r.isResultCollection())
                .map(ResultElement::getAsCollection)
                .flatMap(
                    details -> Try
                        .of(() -> details.asList(ODataServiceError.class))
                        .onFailure(e -> log.debug(preparedErrorMessage, e))
                        .toOption());

        final ODataServiceError odataError = resultObject.as(ODataServiceError.class);

        if( maybeDetails.isEmpty() ) {
            return odataError;
        }

        // Move the content of errordetails into the details field if it is a list of OData errors
        odataError.getInnerError().remove(ERROR_DETAILS_FIELD);
        odataError.setDetails(maybeDetails.get());
        return odataError;
    }

    @Nonnull
    @Override
    public Option<String> getTarget()
    {
        return Option.of(target);
    }

    // unfortunately we need this since otherwise the generics don't work
    @SuppressWarnings( "unchecked" )
    private void setDetails( @Nullable final List<? extends ODataServiceErrorDetails> details )
    {
        this.details = (List<ODataServiceErrorDetails>) details;
    }

    /**
     * Custom adapter to deserialize an OData error "message" field. This deserializer handles the differences between
     * OData V2 SAP specification and OData V4 official specification.
     *
     * <p>
     * OData V2
     * </p>
     *
     * <pre>
     * <code>{
     *   "error": {
     *     "code": "UF0",
     *     "message": {
     *       "lang": "en",
     *       "value": "Unsupported functionality"
     *     },
     *     ...
     * }
     * </code>
     * </pre>
     * <p>
     * OData V4
     * </p>
     *
     * <pre>
     * <code>{
     *   "error": {
     *     "code": "UF0",
     *     "message": "Unsupported functionality",
     *     ...
     * }
     * </code>
     * </pre>
     */
    @SuppressWarnings( {
        "PMD.NullAnnotationMissingOnPublicMethod",
        "PMD.NullAnnotationMissingOnPublicMethodParameter" } )
    private static class MessageDeserializer implements JsonDeserializer<String>
    {
        @Override
        @Nullable
        public String deserialize(
            @Nonnull final JsonElement json,
            @Nonnull final Type typeOfT,
            @Nonnull final JsonDeserializationContext context )
            throws JsonParseException
        {
            if( json.isJsonPrimitive() ) {
                return json.getAsString();
            }
            if( json.isJsonObject() ) {
                final JsonElement value = json.getAsJsonObject().get("value");
                if( value != null && value.isJsonPrimitive() ) {
                    return value.getAsString();
                }
                log.warn("Unable to deserialize error value from \"message\": {}", value);
            } else {
                log.warn("Unable to deserialize a \"message\" value from JSON value: {}", json);
            }
            return null;
        }
    }

    /**
     * Custom adapter to deserialize the {@code details} field for OData V4 responses.
     */
    @SuppressWarnings( {
        "PMD.NullAnnotationMissingOnPublicMethod",
        "PMD.NullAnnotationMissingOnPublicMethodParameter" } )
    private static class DetailsDeserializer implements JsonDeserializer<List<ODataServiceErrorDetails>>
    {
        private static final Gson gson = new Gson();
        private static final Type listType = new TypeToken<List<ODataServiceError>>()
        {
        }.getType();

        @Override
        public
            List<ODataServiceErrorDetails>
            deserialize( final JsonElement json, final Type typeOfT, final JsonDeserializationContext context )
                throws JsonParseException
        {
            return json.isJsonNull() ? Collections.emptyList() : gson.fromJson(json, listType);
        }
    }
}
