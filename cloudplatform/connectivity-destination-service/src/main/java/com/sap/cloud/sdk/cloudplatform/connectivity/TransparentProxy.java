package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;

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
public class TransparentProxy implements DestinationLoader {
    private static final ResilienceConfiguration.TimeLimiterConfiguration DEFAULT_TIME_LIMITER =
            ResilienceConfiguration.TimeLimiterConfiguration.of().timeoutDuration(Duration.ofSeconds(10));

    private static final String X_ERROR_INTERNAL_CODE_HEADER = "x-error-internal-code";
    private static final String X_ERROR_ORIGIN_HEADER = "x-error-origin";
    private static final String X_ERROR_MESSAGE_HEADER = "x-error-message";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final Integer DEFAULT_PORT = 80;
    private static final String SCHEME_SEPARATOR = "://";
    private static final String HTTP_SCHEME = org.apache.http.HttpHost.DEFAULT_SCHEME_NAME + SCHEME_SEPARATOR;
    private static final String PORT_SEPARATOR = ":";
    private static final String HOST_CONTAINS_PATH_ERROR_MESSAGE_TEMPLATE =
            "Host '%s' contains a path '%s'. Paths are not allowed in host registration.";
    private static final ResilienceConfiguration resilienceConfiguration =
            createResilienceConfiguration("destinationverifier", DEFAULT_TIME_LIMITER);
    private static final String FAILED_TO_VERIFY_DESTINATION = "Failed to verify destination. ";
    static String uri;
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
     * @param host the gateway host to register (e.g., "gateway.svc.cluster.local") Must not contain paths or be null
     * @throws DestinationAccessException if the proxy is already registered, the host contains a path, or the host is not reachable on port 80
     * @throws IllegalArgumentException   if host is null
     * @see #register(String, Integer)
     */
    public static void register(@Nonnull final String host) {
        registerLoader(host, DEFAULT_PORT);
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
     * @param host the gateway host to register (e.g., "gateway" or "<a href="http://gateway">...</a>") Must not contain
     *             paths or be null
     * @param port the port number to use for the gateway connection. Must not be null and should be a valid port number
     *             (1-65535)
     * @throws DestinationAccessException if the proxy is already registered, the host contains a path, or the host is not reachable on the
     *                                    specified port
     * @throws IllegalArgumentException   if host or port is null
     * @see #register(String)
     */
    public static void register(@Nonnull final String host, @Nonnull final Integer port) {
        registerLoader(host, port);
    }

    private static void registerLoader(@Nonnull final String host, final Integer port) {
        if (uri != null) {
            throw new DestinationAccessException(
                    "TransparentProxy is already registered. Only one registration is allowed.");
        }

        final String normalizedHost = normalizeHostWithScheme(host);
        try {
            final String hostForVerification = getHostForVerification(host, normalizedHost);
            verifyHostConnectivity(hostForVerification, port);
        } catch (final URISyntaxException e) {
            throw new DestinationAccessException(
                    String.format("Invalid host format: [%s]. Caused by: %s", host, e.getMessage()),
                    e);
        }
        uri = String.format("%s%s%d", normalizedHost, PORT_SEPARATOR, port);
        DestinationAccessor.prependDestinationLoader(new TransparentProxy());
    }

    @Nonnull
    private static String getHostForVerification(@Nonnull final String host, final String normalizedHost)
            throws URISyntaxException {
        final URI parsedUri = new URI(normalizedHost);

        final String path = parsedUri.getPath();
        if (path != null && !path.isEmpty()) {
            throw new DestinationAccessException(String.format(HOST_CONTAINS_PATH_ERROR_MESSAGE_TEMPLATE, host, path));
        }

        final String hostForVerification = parsedUri.getHost();
        if (hostForVerification == null) {
            throw new DestinationAccessException(String.format("Invalid host format: [%s]", host));
        }
        return hostForVerification;
    }

    @Nonnull
    private static String normalizeHostWithScheme(@Nonnull final String host) {
        if (host.contains(SCHEME_SEPARATOR)) {
            return host;
        }
        return HTTP_SCHEME + host;
    }

    private static void verifyHostConnectivity(@Nonnull final String host, final int port) {
        networkVerifier.verifyHostConnectivity(host, port);
    }

    /**
     *
     * @param destination the destination to use
     */
    private static TransparentProxyDestination verifyDestination(
            @Nonnull final TransparentProxyDestination destination) {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final URI destinationUri = destination.getUri();
        final HttpHead headRequest = new HttpHead(destinationUri);
        final String destinationName = getDestinationName(destination, destinationUri);

        log.info("Performing HEAD request to destination {} to verify the destination exists", destinationName);
        Supplier<Void> tpDestinationVerifierSupplier = prepareSupplier(httpClient, headRequest, destinationName);
        try {
            ResilienceDecorator.executeSupplier(tpDestinationVerifierSupplier, resilienceConfiguration);
        } catch (final ResilienceRuntimeException e) {
            if (hasCauseAssignableFrom(e, DestinationNotFoundException.class)) {
                throw new DestinationNotFoundException(e);
            }
            throw new DestinationAccessException(e);
        }

        return destination;
    }

    private static boolean hasCauseAssignableFrom(@Nonnull final Throwable t, @Nonnull final Class<?> cls) {
        return ExceptionUtils.getThrowableList(t).stream().map(Throwable::getClass).anyMatch(cls::isAssignableFrom);
    }

    private static String getDestinationName(@Nonnull TransparentProxyDestination destination, URI destinationUri) {
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

    private static void verifyTransparentProxyResponse(final HttpResponse response, final String destinationName) {
        if (response == null) {
            throw new DestinationAccessException(FAILED_TO_VERIFY_DESTINATION + "Response is null.");
        }
        final String setCookieHeader = getHeaderValue(response, SET_COOKIE_HEADER);
        if (!"".equals(setCookieHeader)) {
            log
                    .warn(
                            "Received 'Set-Cookie' header from transparent proxy destination {}: {}",
                            destinationName,
                            setCookieHeader);
        }

        final int statusCode = response.getStatusLine().getStatusCode();
        String errorInternalCode = getHeaderValue(response, X_ERROR_INTERNAL_CODE_HEADER);
        final String errorMessage = getHeaderValue(response, X_ERROR_MESSAGE_HEADER);
        final String errorOrigin = getHeaderValue(response, X_ERROR_ORIGIN_HEADER);

        log
                .debug(
                        "HEAD request to destination {} returned status code: {}, x-error-internal-code: {}",
                        destinationName,
                        statusCode,
                        errorInternalCode);
        if (statusCode == HttpStatus.SC_BAD_GATEWAY
                && Integer.toString(HttpStatus.SC_NOT_FOUND).equals(errorInternalCode)) {
            throw new DestinationNotFoundException(errorMessage);
        }
        if (!"".equals(errorOrigin)) {
            String detailedErrorMessage =
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
    private static Supplier<Void> prepareSupplier(HttpClient httpClient, HttpHead headRequest, String destinationName) {
        return () -> {
            try {
                HttpResponse response = httpClient.execute(headRequest);
                verifyTransparentProxyResponse(response, destinationName);
                return null;
            } catch (final IOException e) {
                throw new DestinationAccessException(FAILED_TO_VERIFY_DESTINATION, e);
            }
        };
    }

    @Nonnull
    static ResilienceConfiguration createResilienceConfiguration(
            @Nonnull final String identifier,
            @Nonnull final ResilienceConfiguration.TimeLimiterConfiguration timeLimiterConfiguration) {
        return ResilienceConfiguration
                .of(TransparentProxy.class + identifier)
                .timeLimiterConfiguration(timeLimiterConfiguration);
    }

    /**
     * Helper method to extract header value from HTTP message.
     *
     * @param message    the HTTP message
     * @param headerName the name of the header to extract
     * @return the header value if present, "" otherwise
     */
    private static String getHeaderValue(@Nonnull final HttpMessage message, @Nonnull final String headerName) {
        if (message.containsHeader(headerName)) {
            return message.getFirstHeader(headerName).getValue();
        }
        return "";
    }

    @Nonnull
    private static TransparentProxyDestination
    prepareDestination(@Nonnull final String destinationName, @Nonnull final DestinationOptions options) {
        String fragmentName =
                options.get(TransparentProxyDestination.FRAGMENT_NAME_HEADER_KEY).getOrElse("").toString();
        String fragmentOptional =
                options.get(TransparentProxyDestination.FRAGMENT_OPTIONAL_HEADER_KEY).getOrElse("").toString();
        String clientAssertion =
                options.get(TransparentProxyDestination.CLIENT_ASSERTION_HEADER_KEY).getOrElse("").toString();
        String tenantSubdomain =
                options.get(TransparentProxyDestination.TENANT_SUBDOMAIN_HEADER_KEY).getOrElse("").toString();
        String tenantId = options.get(TransparentProxyDestination.TENANT_ID_HEADER_KEY).getOrElse("").toString();
        String destinationLevel =
                options.get(TransparentProxyDestination.DESTINATION_LEVEL_HEADER_KEY).getOrElse("").toString();
        String fragmentLevel =
                options.get(TransparentProxyDestination.FRAGMENT_LEVEL_HEADER_KEY).getOrElse("").toString();
        String tokenServiceTenant =
                options.get(TransparentProxyDestination.TOKEN_SERVICE_TENANT_HEADER_KEY).getOrElse("").toString();
        String clientAssertionType =
                options.get(TransparentProxyDestination.CLIENT_ASSERTION_TYPE_HEADER_KEY).getOrElse("").toString();
        String clientAssertionDestinationName =
                options
                        .get(TransparentProxyDestination.CLIENT_ASSERTION_DESTINATION_NAME_HEADER_KEY)
                        .getOrElse("")
                        .toString();
        String authorization =
                options.get(TransparentProxyDestination.AUTHORIZATION_HEADER_KEY).getOrElse("").toString();
        String subjectTokenType =
                options.get(TransparentProxyDestination.SUBJECT_TOKEN_TYPE_HEADER_KEY).getOrElse("").toString();
        String actorToken = options.get(TransparentProxyDestination.ACTOR_TOKEN_HEADER_KEY).getOrElse("").toString();
        String actorTokenType =
                options.get(TransparentProxyDestination.ACTOR_TOKEN_TYPE_HEADER_KEY).getOrElse("").toString();
        String redirectUri = options.get(TransparentProxyDestination.REDIRECT_URI_HEADER_KEY).getOrElse("").toString();
        String codeVerifier =
                options.get(TransparentProxyDestination.CODE_VERIFIER_HEADER_KEY).getOrElse("").toString();
        String chainName = options.get(TransparentProxyDestination.CHAIN_NAME_HEADER_KEY).getOrElse("").toString();
        String chainVarSubjectToken =
                options.get(TransparentProxyDestination.CHAIN_VAR_SUBJECT_TOKEN_HEADER_KEY).getOrElse("").toString();
        String chainVarSubjectTokenType =
                options.get(TransparentProxyDestination.CHAIN_VAR_SUBJECT_TOKEN_TYPE_HEADER_KEY).getOrElse("").toString();
        String chainVarSamlProviderDestinationName =
                options
                        .get(TransparentProxyDestination.CHAIN_VAR_SAML_PROVIDER_DESTINATION_NAME_HEADER_KEY)
                        .getOrElse("")
                        .toString();

        TransparentProxyDestination.GatewayBuilder gatewayBuilder =
                TransparentProxyDestination.gateway(destinationName, uri);

        if (!fragmentName.isEmpty()) {
            gatewayBuilder.fragmentName(fragmentName);
        }
        if (!fragmentOptional.isEmpty()) {
            gatewayBuilder.fragmentOptional(Boolean.parseBoolean(fragmentOptional));
        }
        if (!clientAssertion.isEmpty()) {
            gatewayBuilder.clientAssertion(clientAssertion);
        }
        if (!tenantSubdomain.isEmpty()) {
            gatewayBuilder.tenantSubdomain(tenantSubdomain);
        }
        if (!tenantId.isEmpty()) {
            gatewayBuilder.tenantId(tenantId);
        }
        if (!destinationLevel.isEmpty()) {
            gatewayBuilder
                    .destinationLevel(DestinationServiceOptionsAugmenter.CrossLevelScope.valueOf(destinationLevel));
        }
        if (!fragmentLevel.isEmpty()) {
            gatewayBuilder.fragmentLevel(DestinationServiceOptionsAugmenter.CrossLevelScope.valueOf(fragmentLevel));
        }
        if (!tokenServiceTenant.isEmpty()) {
            gatewayBuilder.tokenServiceTenant(tokenServiceTenant);
        }
        if (!clientAssertionType.isEmpty()) {
            gatewayBuilder.clientAssertionType(clientAssertionType);
        }
        if (!clientAssertionDestinationName.isEmpty()) {
            gatewayBuilder.clientAssertionDestinationName(clientAssertionDestinationName);
        }
        if (!authorization.isEmpty()) {
            gatewayBuilder.authorization(authorization);
        }
        if (!subjectTokenType.isEmpty()) {
            gatewayBuilder.subjectTokenType(subjectTokenType);
        }
        if (!actorToken.isEmpty()) {
            gatewayBuilder.actorToken(actorToken);
        }
        if (!actorTokenType.isEmpty()) {
            gatewayBuilder.actorTokenType(actorTokenType);
        }
        if (!redirectUri.isEmpty()) {
            gatewayBuilder.redirectUri(redirectUri);
        }
        if (!codeVerifier.isEmpty()) {
            gatewayBuilder.codeVerifier(codeVerifier);
        }
        if (!chainName.isEmpty()) {
            gatewayBuilder.chainName(chainName);
        }
        if (!chainVarSubjectToken.isEmpty()) {
            gatewayBuilder.chainVarSubjectToken(chainVarSubjectToken);
        }
        if (!chainVarSubjectTokenType.isEmpty()) {
            gatewayBuilder.chainVarSubjectTokenType(chainVarSubjectTokenType);
        }
        if (!chainVarSamlProviderDestinationName.isEmpty()) {
            gatewayBuilder.chainVarSamlProviderDestinationName(chainVarSamlProviderDestinationName);
        }

        return gatewayBuilder.build();
    }

    @Nonnull
    @Override
    public Try<Destination> tryGetDestination(@Nonnull final String destinationName) {
        return tryGetDestination(destinationName, DestinationOptions.builder().build());
    }

    @Nonnull
    @Override
    public Try<Destination>
    tryGetDestination(@Nonnull final String destinationName, @Nonnull final DestinationOptions options) {
        final TransparentProxyDestination destination = prepareDestination(destinationName, options);
        return Try.of(() -> verifyDestination(destination));
    }
}
