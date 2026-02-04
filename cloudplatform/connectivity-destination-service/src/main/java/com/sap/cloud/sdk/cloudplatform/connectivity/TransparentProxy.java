package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpMessage;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * A transparent proxy loader that enables routing traffic through a single registered gateway host.
 *
 * <p>
 * This class provides a mechanism to register a proxy gateway that will handle all destination requests transparently.
 * Once registered, all destination lookups will be routed through the configured gateway host and port.
 *
 * <p>
 * <strong>Key Features:</strong>
 * <ul>
 * <li>Single gateway registration - only one proxy can be registered at a time</li>
 * <li>Host validation - ensures hosts don't contain paths and are reachable</li>
 * <li>Automatic scheme normalization - defaults to HTTP if no scheme provided</li>
 * <li>Network connectivity validation before registration</li>
 * </ul>
 *
 * <p>
 * <strong>Usage Example:</strong>
 *
 * <pre>{@code
 * // Register with default port 80
 * TransparentProxy.register("gateway.svc.cluster.local");
 *
 * // Register with custom port
 * TransparentProxy.register("http://gateway.svc.cluster.local", 8080);
 * }</pre>
 *
 * <p>
 * <strong>Thread Safety:</strong> This class uses static state and is not thread-safe. Registration should be performed
 * during application initialization.
 *
 * @since 5.24.0
 */
@Slf4j
public class TransparentProxy implements DestinationLoader
{
    private static final ResilienceConfiguration.TimeLimiterConfiguration DEFAULT_TIME_LIMITER =
        ResilienceConfiguration.TimeLimiterConfiguration.of().timeoutDuration(Duration.ofSeconds(10));

    private static final String X_ERROR_INTERNAL_CODE_HEADER = "x-error-internal-code";
    private static final String X_ERROR_ORIGIN_HEADER = "x-error-origin";
    private static final String X_ERROR_MESSAGE_HEADER = "x-error-message";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final Integer DEFAULT_PORT = 80;
    private static final String SCHEME_SEPARATOR = "://";
    private static final String HTTP_SCHEME = HttpHost.DEFAULT_SCHEME.getId() + SCHEME_SEPARATOR;
    private static final String PORT_SEPARATOR = ":";
    private static final String HOST_CONTAINS_PATH_ERROR_MESSAGE_TEMPLATE =
        "Host '%s' contains a path '%s'. Paths are not allowed in host registration.";
    private static final ResilienceConfiguration resilienceConfiguration =
        createResilienceConfiguration("destinationverifier", DEFAULT_TIME_LIMITER);
    private static final String FAILED_TO_VERIFY_DESTINATION = "Failed to verify destination. ";
    private static final String NO_TENANT_PROVIDED_ERROR_MESSAGE =
        "No current tenant defined and no provider tenant id configured. Transparent proxy always requires an explicit tenant ID. Please use register(host, providerID) in case provider tenant access is intended";
    static String uri;
    static String providerTenantId;
    static NetworkVerifier networkVerifier = new NetworkVerifier();

    /**
     * Registers a transparent proxy gateway using the default port 80.
     *
     * <p>
     * This method registers the specified host as a transparent proxy gateway that will handle all subsequent
     * destination requests. The host will be validated for reachability and must not contain any path components.
     *
     * <p>
     * If no scheme is provided, HTTP will be used by default. The final URI will be constructed as:
     * {@code <normalized-host>:80}
     *
     * @param host
     *            the gateway host to register (e.g., "gateway.svc.cluster.local") Must not contain paths or be null
     * @throws DestinationAccessException
     *             if the proxy is already registered, the host contains a path, or the host is not reachable on port 80
     * @throws IllegalArgumentException
     *             if host is null
     * @see #register(String, Integer)
     */
    public static void register( @Nonnull final String host )
    {
        registerLoader(host, DEFAULT_PORT, null);
    }

    /**
     * Registers a transparent proxy gateway with a specified port.
     *
     * <p>
     * This method registers the specified host and port as a transparent proxy gateway that will handle all subsequent
     * destination requests. The host will be validated for reachability on the specified port and must not contain any
     * path components.
     *
     * <p>
     * If no scheme is provided, HTTP will be used by default. The final URI will be constructed as:
     * {@code <normalized-host>:<port>}
     *
     * @param host
     *            the gateway host to register (e.g., "gateway" or "<a href="http://gateway">...</a>") Must not contain
     *            paths or be null
     * @param port
     *            the port number to use for the gateway connection. Must not be null and should be a valid port number
     *            (1-65535)
     * @throws DestinationAccessException
     *             if the proxy is already registered, the host contains a path, or the host is not reachable on the
     *             specified port
     * @throws IllegalArgumentException
     *             if host or port is null
     * @see #register(String)
     */
    public static void register( @Nonnull final String host, @Nonnull final Integer port )
    {
        registerLoader(host, port, null);
    }

    /**
     * Registers a transparent proxy gateway with a specified port and provider tenant ID.
     *
     * <p>
     * This method registers the specified host and port as a transparent proxy gateway that will handle all subsequent
     * destination requests. The host will be validated for reachability on the specified port and must not contain any
     * path components. The provider tenant ID serves as a fallback when the current tenant cannot be accessed during
     * destination preparation.
     *
     * <p>
     * If no scheme is provided, HTTP will be used by default. The final URI will be constructed as:
     * {@code <normalized-host>:<port>}
     *
     * <p>
     * The provider tenant ID is particularly useful in scenarios where the transparent proxy needs to operate in
     * contexts where tenant information is not readily available, providing a default tenant for authentication and
     * authorization purposes.
     *
     * @param host
     *            the gateway host to register (e.g., "gateway" or "<a href="http://gateway">...</a>") Must not contain
     *            paths or be null
     * @param port
     *            the port number to use for the gateway connection. Must not be null and should be a valid port number
     *            (1-65535)
     * @param providerTenantId
     *            the provider tenant ID to use as a fallback when the current tenant cannot be accessed. Must not be
     *            null
     * @throws DestinationAccessException
     *             if the proxy is already registered, the host contains a path, or the host is not reachable on the
     *             specified port
     * @throws IllegalArgumentException
     *             if host, port, or providerTenantId is null
     * @see #register(String)
     * @see #register(String, Integer)
     */

    public static
        void
        register( @Nonnull final String host, @Nonnull final Integer port, @Nonnull final String providerTenantId )
    {
        registerLoader(host, port, providerTenantId);
    }

    /**
     * Registers a transparent proxy gateway using the default port 80 with a provider tenant ID.
     *
     * <p>
     * This method registers the specified host as a transparent proxy gateway that will handle all subsequent
     * destination requests, using the default port 80. The host will be validated for reachability and must not contain
     * any path components. The provider tenant ID serves as a fallback when the current tenant cannot be accessed
     * during destination preparation.
     *
     * <p>
     * If no scheme is provided, HTTP will be used by default. The final URI will be constructed as:
     * {@code <normalized-host>:80}
     *
     * <p>
     * The provider tenant ID is particularly useful in scenarios where the transparent proxy needs to operate in
     * contexts where tenant information is not readily available, providing a default tenant for authentication and
     * authorization purposes.
     *
     * @param host
     *            the gateway host to register (e.g., "gateway.svc.cluster.local") Must not contain paths or be null
     * @param providerTenantId
     *            the provider tenant ID to use as a fallback when the current tenant cannot be accessed. Must not be
     *            null
     * @throws DestinationAccessException
     *             if the proxy is already registered, the host contains a path, or the host is not reachable on port 80
     * @throws IllegalArgumentException
     *             if host or providerTenantId is null
     * @see #register(String)
     * @see #register(String, Integer)
     * @see #register(String, Integer, String)
     */
    public static void register( @Nonnull final String host, @Nonnull final String providerTenantId )
    {
        registerLoader(host, DEFAULT_PORT, providerTenantId);
    }

    private static void registerLoader( @Nonnull final String host, final Integer port, final String providerTenantId )
    {
        if( uri != null ) {
            throw new DestinationAccessException(
                "TransparentProxy is already registered. Only one registration is allowed.");
        }

        final String normalizedHost = normalizeHostWithScheme(host);
        try {
            final String hostForVerification = getHostForVerification(host, normalizedHost);
            networkVerifier.verifyHostConnectivity(hostForVerification, port);
        }
        catch( final URISyntaxException e ) {
            throw new DestinationAccessException(
                String.format("Invalid host format: [%s]. Caused by: %s", host, e.getMessage()),
                e);
        }
        uri = String.format("%s%s%d", normalizedHost, PORT_SEPARATOR, port);
        TransparentProxy.providerTenantId = providerTenantId;
        DestinationAccessor.prependDestinationLoader(new TransparentProxy());
    }

    @Nonnull
    private static String getHostForVerification( @Nonnull final String host, final String normalizedHost )
        throws URISyntaxException
    {
        final URI parsedUri = new URI(normalizedHost);

        final String path = parsedUri.getPath();
        if( path != null && !path.isEmpty() ) {
            throw new DestinationAccessException(String.format(HOST_CONTAINS_PATH_ERROR_MESSAGE_TEMPLATE, host, path));
        }

        final String hostForVerification = parsedUri.getHost();
        if( hostForVerification == null ) {
            throw new DestinationAccessException(String.format("Invalid host format: [%s]", host));
        }
        return hostForVerification;
    }

    @Nonnull
    private static String normalizeHostWithScheme( @Nonnull final String host )
    {
        if( host.contains(SCHEME_SEPARATOR) ) {
            return host;
        }
        return HTTP_SCHEME + host;
    }

    /**
     *
     * @param destination
     *            the destination to use
     */
    private static TransparentProxyDestination verifyDestination(
        @Nonnull final TransparentProxyDestination destination )
    {
        final HttpClient httpClient = ApacheHttpClient5Accessor.getHttpClient(destination);
        final URI destinationUri = URI.create(uri);
        final HttpHead headRequest = new HttpHead(destinationUri);
        final String destinationName = getDestinationName(destination, destinationUri);

        log
            .debug(
                "Performing HEAD request to destination with name {} to verify the destination exists",
                destinationName);
        final Supplier<ClassicHttpResponse> tpDestinationVerifierSupplier = prepareSupplier(httpClient, headRequest);
        try(
            ClassicHttpResponse response =
                ResilienceDecorator.executeSupplier(tpDestinationVerifierSupplier, resilienceConfiguration) ) {
            verifyTransparentProxyResponse(response, destinationName);
        }
        catch( final ResilienceRuntimeException | IOException e ) {
            if( hasCauseAssignableFrom(e, DestinationNotFoundException.class) ) {
                throw new DestinationNotFoundException(e);
            }
            throw new DestinationAccessException(e);
        }

        return destination;
    }

    private static boolean hasCauseAssignableFrom( @Nonnull final Throwable t, @Nonnull final Class<?> cls )
    {
        return ExceptionUtils.getThrowableList(t).stream().map(Throwable::getClass).anyMatch(cls::isAssignableFrom);
    }

    private static
        String
        getDestinationName( @Nonnull final TransparentProxyDestination destination, final URI destinationUri )
    {
        return destination
            .getHeaders(destinationUri)
            .stream()
            .filter(
                header -> TransparentProxyDestination.DESTINATION_NAME_HEADER_KEY.equalsIgnoreCase(header.getName()))
            .findFirst()
            .map(Header::getValue)
            .orElseThrow(
                () -> new IllegalStateException("Destination name header in Transparent Proxy loader is missing."));
    }

    private static void verifyTransparentProxyResponse( final HttpResponse response, final String destinationName )
    {
        if( response == null ) {
            throw new DestinationAccessException(FAILED_TO_VERIFY_DESTINATION + "Response is null.");
        }
        if( response.containsHeader(SET_COOKIE_HEADER) ) {
            final org.apache.hc.core5.http.Header[] header = response.getHeaders(SET_COOKIE_HEADER);
            final List<String> cookieNames = Arrays.stream(header).map(h -> h.getValue().split("=", 2)[0]).toList();
            log
                .warn(
                    "received set-cookie headers as part of destination health check. This is unexpected and may have side effects for your application. The following cookies were set: {}",
                    cookieNames);
        }

        final int statusCode = response.getCode();
        final String errorInternalCode = getHeaderValue(response, X_ERROR_INTERNAL_CODE_HEADER);
        final String errorMessage = getHeaderValue(response, X_ERROR_MESSAGE_HEADER);
        final String errorOrigin = getHeaderValue(response, X_ERROR_ORIGIN_HEADER);

        log
            .debug(
                "HEAD request to destination with name {} returned status code: {}, x-error-internal-code: {}",
                destinationName,
                statusCode,
                errorInternalCode);
        if( statusCode == HttpStatus.SC_BAD_GATEWAY
            && Integer.toString(HttpStatus.SC_NOT_FOUND).equals(errorInternalCode) ) {
            throw new DestinationNotFoundException(errorMessage);
        }
        if( !"".equals(errorOrigin) ) {
            final String detailedErrorMessage =
                String
                    .format(
                        "%s Destination name: [%s], Origin: [%s], Code: [%s], Message: [%s]",
                        FAILED_TO_VERIFY_DESTINATION,
                        destinationName,
                        errorOrigin,
                        errorInternalCode,
                        errorMessage);
            throw new DestinationAccessException(detailedErrorMessage);
        }
    }

    @Nonnull
    private static
        Supplier<ClassicHttpResponse>
        prepareSupplier( final HttpClient httpClient, final HttpHead headRequest )
    {
        return () -> {
            try {
                // migration from apache httpclient4 to httpclient5 by possibly adding a response handler to httpClient.execute(headRequest);
                return httpClient.execute(headRequest, classicHttpResponse -> classicHttpResponse);
            }
            catch( final IOException e ) {
                throw new DestinationAccessException(FAILED_TO_VERIFY_DESTINATION, e);
            }
        };
    }

    @Nonnull
    static ResilienceConfiguration createResilienceConfiguration(
        @Nonnull final String identifier,
        @Nonnull final ResilienceConfiguration.TimeLimiterConfiguration timeLimiterConfiguration )
    {
        return ResilienceConfiguration
            .of(TransparentProxy.class + identifier)
            .timeLimiterConfiguration(timeLimiterConfiguration);
    }

    private static String getHeaderValue( @Nonnull final HttpMessage message, @Nonnull final String headerName )
    {
        if( message.containsHeader(headerName) ) {
            return message.getFirstHeader(headerName).getValue();
        }
        return "";
    }

    @Nonnull
    private static
        TransparentProxyDestination
        prepareDestination( @Nonnull final String destinationName, @Nonnull final DestinationOptions options )
    {
        final Tenant tenant = retrieveTenant();

        final TransparentProxyDestination.GatewayBuilder gatewayBuilder =
            TransparentProxyDestination.gateway(destinationName, uri);
        gatewayBuilder.tenantId(tenant.getTenantId());

        final Option<String> fragmentNameOption =
            DestinationServiceOptionsAugmenter.getFragmentName(options).peek(gatewayBuilder::fragmentName);
        final Option<DestinationServiceOptionsAugmenter.CrossLevelScope> crossLevelScope =
            DestinationServiceOptionsAugmenter.getCrossLevelScope(options);
        if( fragmentNameOption.isDefined() ) {
            crossLevelScope.peek(gatewayBuilder::fragmentLevel);
        }
        crossLevelScope.peek(gatewayBuilder::destinationLevel);
        DestinationServiceOptionsAugmenter.getAdditionalHeaders(options).forEach(gatewayBuilder::header);

        return gatewayBuilder.build();
    }

    private static Tenant retrieveTenant()
    {
        return TenantAccessor.tryGetCurrentTenant().orElse(() -> {
            if( providerTenantId == null ) {
                return Try.failure(new TenantAccessException(NO_TENANT_PROVIDED_ERROR_MESSAGE));
            }
            return Try.success(new DefaultTenant(providerTenantId));
        }).getOrElseThrow(e -> new TenantAccessException(NO_TENANT_PROVIDED_ERROR_MESSAGE, e));
    }

    @Nonnull
    @Override
    public Try<Destination> tryGetDestination( @Nonnull final String destinationName )
    {
        return tryGetDestination(destinationName, DestinationOptions.builder().build());
    }

    @Nonnull
    @Override
    public
        Try<Destination>
        tryGetDestination( @Nonnull final String destinationName, @Nonnull final DestinationOptions options )
    {
        final TransparentProxyDestination destination = prepareDestination(destinationName, options);
        return Try.of(() -> verifyDestination(destination));
    }
}
