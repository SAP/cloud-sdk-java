package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfToken;
import com.sap.cloud.sdk.cloudplatform.connectivity.CsrfTokenRetriever;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultCsrfTokenRetriever;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic OData request class to provide default features for service requests.
 */
@EqualsAndHashCode
@Slf4j
public abstract class ODataRequestGeneric implements ODataRequestExecutable
{
    /**
     * Default {@link ODataFormat} that will be used if none is specified.
     */
    private static final ODataFormat DEFAULT_FORMAT = ODataFormat.JSON;

    /**
     * The service path of the targeted OData service. E.g. {@code sap/opu/odata/sap/API_BUSINESS_PARTNER}
     */
    @Getter( AccessLevel.PUBLIC )
    @Nonnull
    protected final String servicePath;

    /**
     * The {@link ODataResourcePath} that identifies the OData resource to operate on. E.g.
     * {@code /BusinessPartner('123')/BusinessPartnerAddress(456)}.
     */
    @Getter( AccessLevel.PROTECTED )
    @Nonnull
    protected final ODataResourcePath resourcePath;

    /**
     * The OData protocol version of this request.
     */
    @Getter
    private final ODataProtocol protocol;

    /**
     * List of listeners to observe and react on OData actions.
     */
    @Getter( AccessLevel.PROTECTED )
    private final List<ODataRequestListener> listeners = new ArrayList<>();

    /**
     * Map of HTTP header key-values which are added to the OData request.
     */
    final Map<String, Collection<String>> headers = new TreeMap<>();

    /**
     * Map of additional generic HTTP query parameters.
     */
    @Getter( AccessLevel.PROTECTED )
    private final Map<String, String> queryParameters = new TreeMap<>();

    /**
     * The CSRF token retriever.
     */
    @Nullable
    @Setter
    protected CsrfTokenRetriever csrfTokenRetriever;

    /**
     * The response buffer strategy to use for this request.
     */
    @Nonnull
    ODataRequestResultFactory requestResultFactory = ODataRequestResultFactory.WITH_BUFFER;

    ODataRequestGeneric(
        @Nonnull final String servicePath,
        @Nonnull final ODataResourcePath resourcePath,
        @Nonnull final ODataProtocol protocol )
    {
        this.protocol = protocol;
        this.servicePath = servicePath;
        this.resourcePath = resourcePath;
        headers.putIfAbsent(HttpHeaders.ACCEPT, Lists.newArrayList(DEFAULT_FORMAT.getHttpAccept()));
    }

    /**
     * Get the static request URI of the OData resource.
     *
     * @param uriEncodingStrategy
     *            URI encoding strategy.
     * @return The String representation of the request URI.
     */
    @Nonnull
    public abstract URI getRelativeUri( @Nonnull final UriEncodingStrategy uriEncodingStrategy );

    /**
     * Get the static request URI of the OData resource.
     *
     * @return The String representation of the request URI.
     */
    @Nonnull
    public URI getRelativeUri()
    {
        return getRelativeUri(UriEncodingStrategy.REGULAR);
    }

    /**
     * Use all OData query information to construct a HTTP request query String.
     *
     * @return The request query.
     */
    @Nonnull
    public String getRequestQuery()
    {
        return Joiner.on("&").withKeyValueSeparator("=").join(queryParameters);
    }

    /**
     * Attach a listener to the request process.
     *
     * @param listener
     *            The listener to react on OData request actions.
     */
    public void addListener( @Nonnull final ODataRequestListener listener )
    {
        listeners.add(listener);
    }

    /**
     * Replace a header in the OData HTTP request.
     *
     * @param key
     *            The header name.
     * @param value
     *            The header value.
     */
    public void setHeader( @Nonnull final String key, @Nullable final String value )
    {
        final List<String> values = new ArrayList<>(1);
        values.add(value);
        headers.put(key, values);
    }

    /**
     * Replace a header with multiple values in the OData HTTP request.
     *
     * @param key
     *            The header name.
     * @param values
     *            The header values.
     * @since 4.27.0
     */
    public void setHeader( @Nonnull final String key, @Nonnull final Collection<String> values )
    {
        headers.put(key, new ArrayList<>(values));
    }

    /**
     * Add a header to the OData HTTP request.
     *
     * @param key
     *            The header name.
     * @param value
     *            The header value.
     */
    public void addHeader( @Nonnull final String key, @Nullable final String value )
    {
        headers.computeIfAbsent(key, k -> new ArrayList<>(1)).add(value);
    }

    /**
     * Add a header to the OData HTTP request, if it is not included already.
     *
     * @param key
     *            The header name.
     * @param value
     *            The header value.
     */
    public void addHeaderIfAbsent( @Nonnull final String key, @Nullable final String value )
    {
        headers.putIfAbsent(key, Lists.newArrayList(value));
    }

    /**
     * Add a query parameter to the HTTP request. The value must be encoded.
     *
     * @param key
     *            The parameter key.
     * @param value
     *            The encoded parameters value.
     */
    public void addQueryParameter( @Nonnull final String key, @Nullable final String value )
    {
        queryParameters.put(key, value);
    }

    /**
     * Internal execute method. It will perform the given httpOperation on the given request and ensure a healthy HTTP
     * response code. Failures will always be a subtype of {@link ODataException}
     *
     * @param httpOperation
     *            The HTTP operation to perform, e.g. {@link ODataHttpRequest#requestGet()}
     * @param httpClient
     *            The HTTP client instance that is being used to perform the operation.
     * @return A {@code Try} containing either a successful {@link ODataRequestResultGeneric} or an
     *         {@code ODataException}.
     */
    @Nonnull
    protected
        Try<ODataRequestResultGeneric>
        tryExecute( @Nonnull final Supplier<HttpResponse> httpOperation, @Nonnull final HttpClient httpClient )
    {
        return Try
            .ofSupplier(httpOperation)
            .map(response -> requestResultFactory.create(this, response, httpClient))
            .andThenTry(ODataHealthyResponseValidator::requireHealthyResponse);
    }

    /**
     * Internal execute method. It will attempt to retrieve a CSRF token before issuing the actual request via
     * {@link ODataRequestGeneric#tryExecute(Supplier, HttpClient)}.
     * <p>
     * CSRF token retrieval is skipped, if a token is already present. The actual request is performed regardless
     * whether or not a CSRF token was retrieved.
     *
     * @param httpClient
     *            An {@link HttpClient} to execute the CSRF token retrieval.
     * @param httpOperation
     *            The HTTP operation to perform, e.g. {@link ODataHttpRequest#requestGet()}
     * @return A {@code Try} containing either a successful {@link ODataRequestResultGeneric} or an
     *         {@code ODataException}.
     */
    @Nonnull
    protected Try<ODataRequestResultGeneric> tryExecuteWithCsrfToken(
        @Nonnull final HttpClient httpClient,
        @Nonnull final Supplier<HttpResponse> httpOperation )
    {
        final CsrfTokenRetriever csrfTokenRetriever =
            Option.of(this.csrfTokenRetriever).getOrElse(DefaultCsrfTokenRetriever::new);

        if( !csrfTokenRetriever.isEnabled()
            || getHeaders().containsKey(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY) ) {
            log.debug("CSRF token already present, skipping retrieval.");
            return tryExecute(httpOperation, httpClient);
        }

        final Try<CsrfToken> csrfToken = tryGetCsrfToken(httpClient, csrfTokenRetriever);
        csrfToken.onSuccess(token -> addHeader(DefaultCsrfTokenRetriever.X_CSRF_TOKEN_HEADER_KEY, token.getToken()));

        final Try<ODataRequestResultGeneric> oDataRequest = tryExecute(httpOperation, httpClient);

        if( oDataRequest.isFailure() && csrfToken.isFailure() ) {
            oDataRequest.getCause().addSuppressed(csrfToken.getCause());
        }
        return oDataRequest;
    }

    /**
     * Get the list of headers that will be sent with this request. To add headers, please use
     * {@link #addHeader(String, String) addHeader} and {@link #addHeaderIfAbsent(String, String) addHeaderIfAbsent}
     *
     * @return The list of headers.
     */
    @Nonnull
    public Map<String, Collection<String>> getHeaders()
    {
        return new TreeMap<>(headers);
    }

    @Nonnull
    Try<CsrfToken>
        tryGetCsrfToken( @Nonnull final HttpClient httpClient, @Nonnull final CsrfTokenRetriever csrfTokenRetriever )
    {
        return Try.of(() -> csrfTokenRetriever.retrieveCsrfToken(httpClient, servicePath, getHeaders()));
    }

    void addVersionIdentifierToHeaderIfPresent( @Nullable final String versionIdentifier )
    {
        if( versionIdentifier != null ) {
            addHeaderIfAbsent(HttpHeaders.IF_MATCH, versionIdentifier);
        } else {
            log
                .debug(
                    "Version identifier for {} is either not defined on the entity or is explicitly ignored.",
                    getClass().getSimpleName());
        }
    }
}
