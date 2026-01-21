package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Try;

class TransparentProxyTest
{
    private final String expectedErrorMessage = "Destination not found: non-existent-destination";

    private TransparentProxy loader;
    private NetworkVerifier mockNetworkVerifier;

    @BeforeEach
    void setUp()
    {
        loader = new TransparentProxy();
        mockNetworkVerifier = mock(NetworkVerifier.class);
        TransparentProxy.networkVerifier = mockNetworkVerifier;
    }

    @AfterEach
    void resetLoader()
    {
        TransparentProxy.uri = null;
        TransparentProxy.providerTenantId = null;
        HttpClientAccessor.setHttpClientFactory(null);
    }

    private <T> T executeWithTenant( java.util.concurrent.Callable<T> callable )
        throws Exception
    {
        Tenant tenant = new DefaultTenant("tenant-id", "");
        return TenantAccessor.executeWithTenant(tenant, callable);
    }

    // ========== Tests for register(String host) method ==========

    @Test
    void testRegisterWithLocalhostHost()
        throws Exception
    {
        // Test with localhost which should always be reachable

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("127.0.0.1", "tenant-id");

        // Verify the stored URI has the http scheme
        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://127.0.0.1");
    }

    @Test
    void testRegisterWithHostWithoutScheme()
        throws Exception
    {
        // Test that http:// is automatically added to host without scheme

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("gateway", "tenant-id");

        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway");
    }

    @Test
    void testRegisterWithHostWithHttpScheme()
        throws Exception
    {
        // Test that existing http:// scheme is preserved

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("http://gateway", "tenant-id");

        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway");
    }

    @Test
    void testRegisterWithHostWithHttpsScheme()
        throws Exception
    {
        // Test that existing https:// scheme is preserved

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("https://gateway", "tenant-id");

        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("https://gateway");
    }

    // ========== Tests for register(String host, Integer port) method ==========

    @Test
    void testRegisterWithUnknownHostAndPort()
    {
        doThrow(new DestinationAccessException("Host 'unknown-host' could not be resolved"))
            .when(mockNetworkVerifier)
            .verifyHostConnectivity("unknown-host", 8080);

        final DestinationAccessException exception =
            assertThrows(DestinationAccessException.class, () -> TransparentProxy.register("unknown-host", 8080));

        assertThat(exception.getMessage()).contains("could not be resolved");
    }

    @Test
    void testRegisterWithUnreachablePort()
    {
        doThrow(new DestinationAccessException("Host 'gateway' on port 65432 is not reachable"))
            .when(mockNetworkVerifier)
            .verifyHostConnectivity("gateway", 65432);

        final DestinationAccessException exception =
            assertThrows(DestinationAccessException.class, () -> TransparentProxy.register("gateway", 65432));

        assertThat(exception.getMessage()).contains("is not reachable");
    }

    // ========== Tests for register methods with providerTenantId ==========

    @Test
    void testRegisterWithHostPortAndProviderTenantId()
        throws IOException
    {
        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("gateway", 8080, "provider-tenant-123");

        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isNotNull();
    }

    @Test
    void testTenantIdFromOptionsOverridesProviderTenantId()
        throws IOException
    {
        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        // Register with provider tenant ID
        TransparentProxy.register("gateway", 8080, "provider-tenant-fallback");

        // Pass a different tenant ID in options using custom headers - this should take precedence
        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .customHeaders(
                            new Header(TransparentProxyDestination.TENANT_ID_HEADER_KEY, "options-tenant-123")))
                .build();

        Try<Destination> result = loader.tryGetDestination("test-destination", options);
        assertThat(result.isSuccess()).isTrue();

        // Verify that the tenant ID from options is used
        HttpDestination destination = result.get().asHttp();
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.TENANT_ID_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "options-tenant-123".equals(h.getValue()));
    }

    @Test
    void testContextTenantPreventsFallbackToProviderTenantId()
        throws IOException
    {
        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        // Register with provider tenant ID
        TransparentProxy.register("gateway", 8080, "provider-tenant-fallback");

        // Execute with a tenant in context - providerTenantId should NOT be used
        Tenant contextTenant = new DefaultTenant("context-tenant-id", "context-tenant-name");

        Try<Destination> result =
            TenantAccessor.executeWithTenant(contextTenant, () -> loader.tryGetDestination("test-destination"));

        assertThat(result.isSuccess()).isTrue();

        HttpDestination destination = result.get().asHttp();

        // When tenant is available in context, providerTenantId should NOT be used
        // The tenant from context takes precedence, so providerTenantId is ignored
        assertThat(destination.getHeaders(destination.getUri()))
            .noneMatch(
                h -> TransparentProxyDestination.TENANT_ID_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "provider-tenant-fallback".equals(h.getValue()));
    }

    @Test
    void testRegisterWithHostAndProviderTenantId()
        throws IOException
    {
        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("gateway", "provider-tenant-456");

        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway:80");
        assertThat(TransparentProxy.providerTenantId).isEqualTo("provider-tenant-456");
    }

    @Test
    void testRegisterWithHostPortProviderTenantIdAndScheme()
        throws IOException
    {
        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("https://gateway", 443, "provider-tenant-789");

        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("https://gateway:443");
        assertThat(TransparentProxy.providerTenantId).isEqualTo("provider-tenant-789");
    }

    @Test
    void testRegisterWithProviderTenantIdStoresCorrectly()
        throws IOException
    {
        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        // Test with 3-parameter version
        TransparentProxy.register("localhost", 8080, "tenant-abc");
        assertThat(TransparentProxy.providerTenantId).isEqualTo("tenant-abc");

        // Reset and test with 2-parameter version
        TransparentProxy.uri = null;
        TransparentProxy.providerTenantId = null;

        TransparentProxy.register("localhost", "tenant-xyz");
        assertThat(TransparentProxy.providerTenantId).isEqualTo("tenant-xyz");
    }

    @Test
    void testRegisterWithProviderTenantIdFailsOnHostWithPath()
    {
        final DestinationAccessException exception =
            assertThrows(
                DestinationAccessException.class,
                () -> TransparentProxy.register("gateway/api", 8080, "provider-tenant"));

        assertThat(exception.getMessage()).contains("contains a path");
        assertThat(exception.getMessage()).contains("Paths are not allowed");
    }

    @Test
    void testRegisterWithProviderTenantIdFailsOnSecondRegistration()
        throws IOException
    {
        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("gateway", "provider-tenant-1");

        final DestinationAccessException exception =
            assertThrows(
                DestinationAccessException.class,
                () -> TransparentProxy.register("other-gateway", "provider-tenant-2"));

        assertThat(exception.getMessage())
            .contains("TransparentProxy is already registered. Only one registration is allowed.");
    }

    @Test
    void testTryGetDestinationWithoutOptions()
        throws Exception
    {
        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("gateway", "tenant-id");

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("test-destination"));

        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination).isNotNull();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway");
    }

    @Test
    void testTryGetDestinationWithOptions()
        throws Exception
    {
        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("gateway", "tenant-id");

        Try<Destination> result =
            executeWithTenant(
                () -> loader.tryGetDestination("test-destination-with-options", DestinationOptions.builder().build()));

        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination).isNotNull();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway");
    }

    @Test
    void testTryGetDestinationReturnsSuccessfulTry()
        throws Exception
    {
        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("https://gateway", "tenant-id");

        Try<Destination> result1 = executeWithTenant(() -> loader.tryGetDestination("destination1"));
        Try<Destination> result2 =
            executeWithTenant(() -> loader.tryGetDestination("destination2", DestinationOptions.builder().build()));

        assertThat(result1.isSuccess()).isTrue();
        assertThat(result2.isSuccess()).isTrue();
        assertThat(result1.isFailure()).isFalse();
        assertThat(result2.isFailure()).isFalse();
    }

    // ========== Tests for edge cases and error handling ==========

    @Test
    void testRegisterWithHostContainingPathFails()
    {
        final DestinationAccessException exception =
            assertThrows(DestinationAccessException.class, () -> TransparentProxy.register("gateway/some/path"));

        assertThat(exception.getMessage()).contains("contains a path");
        assertThat(exception.getMessage()).contains("Paths are not allowed");
    }

    @Test
    void testRegisterWithComplexUriContainingPathFails()
    {
        final DestinationAccessException exception =
            assertThrows(
                DestinationAccessException.class,
                () -> TransparentProxy.register("https://gateway:443/api/v1"));

        assertThat(exception.getMessage()).contains("contains a path");
        assertThat(exception.getMessage()).contains("/api/v1");
        assertThat(exception.getMessage()).contains("Paths are not allowed");
    }

    @Test
    void testMultipleRegistrationsThrowsException()
        throws Exception
    {
        // Test that first registration succeeds

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        TransparentProxy.register("gateway", "tenant-id");

        Try<Destination> result1 = executeWithTenant(() -> loader.tryGetDestination("test1"));
        assertThat(result1.get().asHttp().getUri().toString()).startsWith("http://gateway");

        // Test that second registration throws exception
        DestinationAccessException exception =
            assertThrows(DestinationAccessException.class, () -> TransparentProxy.register("https://gateway"));

        assertThat(exception.getMessage())
            .contains("TransparentProxy is already registered. Only one registration is allowed.");

        // Verify that the original registration is still active
        Try<Destination> result2 = executeWithTenant(() -> loader.tryGetDestination("test2"));
        assertThat(result2.get().asHttp().getUri().toString()).startsWith("http://gateway");
    }

    @Test
    void testDestinationLoaderInterface()
        throws Exception
    {
        // Test that TransparentProxyLoader properly implements DestinationLoader

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        assertThat(loader).isInstanceOf(DestinationLoader.class);

        // Setup: register a host first
        TransparentProxy.register("gateway", "tenant-id");

        // Test interface methods
        DestinationLoader destinationLoader = loader;

        Try<Destination> result1 = executeWithTenant(() -> destinationLoader.tryGetDestination("test"));
        Try<Destination> result2 =
            executeWithTenant(() -> destinationLoader.tryGetDestination("test", DestinationOptions.builder().build()));

        assertThat(result1.isSuccess()).isTrue();
        assertThat(result2.isSuccess()).isTrue();
    }

    @Test
    void testRegisterWithDifferentSchemes()
        throws Exception
    {
        // Test various schemes to ensure they are preserved

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        String[] schemes = { "http://", "https://", "ftp://", "custom://" };
        String hostname = "gateway";

        for( String scheme : schemes ) {
            // Reset registration state between tests
            TransparentProxy.uri = null;
            TransparentProxy.providerTenantId = null;

            // Register with the current scheme
            TransparentProxy.register(scheme + hostname, "tenant-id");

            // Verify the scheme is preserved in the destination
            Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("test-destination"));
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.get().asHttp().getUri().toString()).startsWith(scheme + hostname);
        }
    }

    @Test
    void testRegisterWithPortInUriButNoPath()
        throws Exception
    {
        TransparentProxy.register("https://gateway:9443", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("test-destination"));
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("https://gateway:9443");
    }

    @Test
    void testRegisterWithPortInUriAndPathFails()
    {
        final DestinationAccessException exception =
            assertThrows(DestinationAccessException.class, () -> TransparentProxy.register("https://gateway:9443/api"));

        assertThat(exception.getMessage()).contains("contains a path");
        assertThat(exception.getMessage()).contains("/api");
        assertThat(exception.getMessage()).contains("Paths are not allowed");
    }

    @Test
    void testRegisterWithHostAndPort()
        throws Exception
    {
        TransparentProxy.register("gateway", 8080, "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("test-destination"));
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway:8080");
    }

    @Test
    void testRegisterWithHostAndPortWithScheme()
        throws Exception
    {
        TransparentProxy.register("https://gateway", 443, "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("test-destination"));
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("https://gateway:443");
    }

    // ========== Comprehensive Path Validation Tests ==========

    @Test
    void testRegisterWithRootPathIsNotAllowed()
    {
        // Test that root path "/" is rejected
        final DestinationAccessException exception =
            assertThrows(DestinationAccessException.class, () -> TransparentProxy.register("https://gateway/"));

        assertThat(exception.getMessage()).contains("contains a path");
        assertThat(exception.getMessage()).contains("/");
        assertThat(exception.getMessage()).contains("Paths are not allowed");
    }

    @Test
    void testRegisterWithVariousPathsFails()
    {
        // Test various path patterns that should all fail
        String[] hostsWithPaths =
            {
                "gateway/api",
                "gateway/api/v1",
                "gateway/path/to/resource",
                "http://gateway/api",
                "https://gateway/api/v1",
                "gateway:8080/api",
                "https://gateway:443/api/v1",
                "gateway/api?query=param",
                "gateway/api#fragment" };

        for( String hostWithPath : hostsWithPaths ) {
            // Reset registration state between tests
            resetLoader();

            DestinationAccessException exception =
                assertThrows(
                    DestinationAccessException.class,
                    () -> TransparentProxy.register(hostWithPath),
                    "Expected path validation to fail for: " + hostWithPath);

            assertThat(exception.getMessage())
                .as("Error message should mention path for: " + hostWithPath)
                .contains("contains a path");
            assertThat(exception.getMessage())
                .as("Error message should mention paths not allowed for: " + hostWithPath)
                .contains("Paths are not allowed");
        }
    }

    // ========== Tests for isDestinationNotFound - Destination Not Found Cases ==========

    @Test
    void testDestinationNotFoundWhenStatus502WithErrorCode404()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response =
            new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_GATEWAY, "Bad Gateway");
        response.setHeader("x-error-internal-code", "404");
        response.setHeader("x-error-message", expectedErrorMessage);

        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("non-existent-destination"));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isInstanceOf(DestinationNotFoundException.class);
        assertThat(result.getCause().getMessage()).contains(expectedErrorMessage);
    }

    @Test
    void testDestinationNotFoundWhenStatus502WithErrorCode404WithOptions()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response =
            new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_GATEWAY, "Bad Gateway");
        response.setHeader("x-error-internal-code", "404");
        response.setHeader("x-error-message", expectedErrorMessage);

        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        Try<Destination> result =
            executeWithTenant(
                () -> loader.tryGetDestination("non-existent-destination", DestinationOptions.builder().build()));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isInstanceOf(DestinationNotFoundException.class);
        assertThat(result.getCause().getMessage()).contains(expectedErrorMessage);
    }

    @Test
    void testDestinationFoundWhenStatus200()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");

        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("existing-destination"));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isNotNull();
    }

    @Test
    void testDestinationFoundWhenStatus502WithoutErrorCode()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response =
            new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_GATEWAY, "Bad Gateway");

        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("destination-with-502"));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isNotNull();
    }

    @Test
    void testDestinationFoundWhenStatus502WithDifferentErrorCode()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response =
            new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_GATEWAY, "Bad Gateway");
        response.setHeader("x-error-internal-code", "500");

        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("destination-with-different-error"));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isNotNull();
    }

    @Test
    void testRegisterWithValidHostsWithoutPathsSucceeds()
        throws Exception
    {
        String[] validHosts =
            {
                "gateway",
                "gateway.example.com",
                "gateway.svc.cluster.local",
                "http://gateway",
                "https://gateway",
                "gateway:8080",
                "gateway:443",
                "http://gateway:8080",
                "https://gateway:443",
                "127.0.0.1",
                "192.168.1.1:8080",
                "localhost" };

        for( String validHost : validHosts ) {
            // Reset registration state between tests
            resetLoader();

            // This should not throw an exception
            TransparentProxy.register(validHost, "tenant-id");

            // Set up mock after registration
            final HttpClient mockHttpClient = mock(HttpClient.class);
            final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
            when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
            HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

            // Verify the registration worked
            Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("test-destination"));
            assertThat(result.isSuccess()).as("Registration should succeed for valid host: " + validHost).isTrue();
        }
    }

    @Test
    void testTryGetDestinationWithLevelOptions()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .crossLevelConsumption(DestinationServiceOptionsAugmenter.CrossLevelScope.SUBACCOUNT))
                .build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-with-levels", options));

        assertThat(result.isSuccess()).isTrue();
        HttpDestination destination = result.get().asHttp();
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.DESTINATION_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "subaccount".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .noneMatch(
                h -> TransparentProxyDestination.FRAGMENT_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "subaccount".equals(h.getValue()));
    }

    @Test
    void testTryGetDestinationWithAllSupportedOptions()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .fragmentName("complete-fragment")
                        .customHeaders(
                            new Header(TransparentProxyDestination.FRAGMENT_OPTIONAL_HEADER_KEY, "false"),
                            new Header(TransparentProxyDestination.TENANT_ID_HEADER_KEY, "complete-tenant"),
                            new Header(TransparentProxyDestination.CLIENT_ASSERTION_HEADER_KEY, "complete-assertion"),
                            new Header(TransparentProxyDestination.AUTHORIZATION_HEADER_KEY, "Bearer complete-token")))
                .build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-with-all", options));

        assertThat(result.isSuccess()).isTrue();
        HttpDestination destination = result.get().asHttp();

        // Verify all key headers are present
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(h -> TransparentProxyDestination.FRAGMENT_NAME_HEADER_KEY.equalsIgnoreCase(h.getName()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(h -> TransparentProxyDestination.FRAGMENT_OPTIONAL_HEADER_KEY.equalsIgnoreCase(h.getName()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(h -> TransparentProxyDestination.TENANT_ID_HEADER_KEY.equalsIgnoreCase(h.getName()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(h -> TransparentProxyDestination.CLIENT_ASSERTION_HEADER_KEY.equalsIgnoreCase(h.getName()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(h -> TransparentProxyDestination.AUTHORIZATION_HEADER_KEY.equalsIgnoreCase(h.getName()));
    }

    // ========== Unit Tests for Augmented Options Scenarios ==========

    @Test
    void testFragmentNameWithAugmentedOptions()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(DestinationServiceOptionsAugmenter.augmenter().fragmentName("test-fragment"))
                .build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-with-fragment", options));

        assertThat(result.isSuccess()).isTrue();
        HttpDestination destination = result.get().asHttp();
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.FRAGMENT_NAME_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "test-fragment".equals(h.getValue()));
    }

    @Test
    void testCrossLevelConsumptionSubaccount()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .crossLevelConsumption(DestinationServiceOptionsAugmenter.CrossLevelScope.SUBACCOUNT))
                .build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-subaccount", options));

        assertThat(result.isSuccess()).isTrue();
        HttpDestination destination = result.get().asHttp();
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.DESTINATION_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "subaccount".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .noneMatch(
                h -> TransparentProxyDestination.FRAGMENT_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "subaccount".equals(h.getValue()));
    }

    @Test
    void testCrossLevelConsumptionProviderSubaccount()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .crossLevelConsumption(DestinationServiceOptionsAugmenter.CrossLevelScope.PROVIDER_SUBACCOUNT))
                .build();

        Try<Destination> result =
            executeWithTenant(() -> loader.tryGetDestination("dest-provider-subaccount", options));

        assertThat(result.isSuccess()).isTrue();
        HttpDestination destination = result.get().asHttp();
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.DESTINATION_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "provider_subaccount".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .noneMatch(
                h -> TransparentProxyDestination.FRAGMENT_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "provider_subaccount".equals(h.getValue()));
    }

    @Test
    void testCrossLevelConsumptionInstance()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .crossLevelConsumption(DestinationServiceOptionsAugmenter.CrossLevelScope.INSTANCE))
                .build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-instance", options));

        assertThat(result.isSuccess()).isTrue();
        HttpDestination destination = result.get().asHttp();
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.DESTINATION_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "instance".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .noneMatch(
                h -> TransparentProxyDestination.FRAGMENT_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "instance".equals(h.getValue()));
    }

    @Test
    void testCrossLevelConsumptionProviderInstance()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .crossLevelConsumption(DestinationServiceOptionsAugmenter.CrossLevelScope.PROVIDER_INSTANCE))
                .build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-provider-instance", options));

        assertThat(result.isSuccess()).isTrue();
        HttpDestination destination = result.get().asHttp();
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.DESTINATION_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "provider_instance".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .noneMatch(
                h -> TransparentProxyDestination.FRAGMENT_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "provider_instance".equals(h.getValue()));
    }

    @Test
    void testCustomHeadersWithAugmentedOptions()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .customHeaders(
                            new Header("X-Custom-Header-1", "value1"),
                            new Header("X-Custom-Header-2", "value2")))
                .build();

        Try<Destination> result =
            executeWithTenant(() -> loader.tryGetDestination("dest-with-custom-headers", options));

        assertThat(result.isSuccess()).isTrue();
        HttpDestination destination = result.get().asHttp();
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(h -> "X-Custom-Header-1".equalsIgnoreCase(h.getName()) && "value1".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(h -> "X-Custom-Header-2".equalsIgnoreCase(h.getName()) && "value2".equals(h.getValue()));
    }

    @Test
    void testCombinedFragmentAndCrossLevelOptions()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .fragmentName("combined-fragment")
                        .crossLevelConsumption(DestinationServiceOptionsAugmenter.CrossLevelScope.INSTANCE))
                .build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-combined", options));

        assertThat(result.isSuccess()).isTrue();
        HttpDestination destination = result.get().asHttp();
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.FRAGMENT_NAME_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "combined-fragment".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.DESTINATION_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "instance".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.FRAGMENT_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "instance".equals(h.getValue()));
    }

    @Test
    void testFragmentWithCustomHeadersAndCrossLevel()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .fragmentName("full-fragment")
                        .crossLevelConsumption(DestinationServiceOptionsAugmenter.CrossLevelScope.PROVIDER_INSTANCE)
                        .customHeaders(
                            new Header("X-Test-Header", "test-value"),
                            new Header(TransparentProxyDestination.FRAGMENT_OPTIONAL_HEADER_KEY, "true")))
                .build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-full-options", options));

        assertThat(result.isSuccess()).isTrue();
        HttpDestination destination = result.get().asHttp();
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.FRAGMENT_NAME_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "full-fragment".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.DESTINATION_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "provider_instance".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.FRAGMENT_LEVEL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "provider_instance".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(h -> "X-Test-Header".equalsIgnoreCase(h.getName()) && "test-value".equals(h.getValue()));
        assertThat(destination.getHeaders(destination.getUri()))
            .anyMatch(
                h -> TransparentProxyDestination.FRAGMENT_OPTIONAL_HEADER_KEY.equalsIgnoreCase(h.getName())
                    && "true".equals(h.getValue()));
    }

    @Test
    void testRetrievalStrategyWithAugmentedOptions()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .retrievalStrategy(DestinationServiceRetrievalStrategy.ALWAYS_PROVIDER))
                .build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-with-strategy", options));

        assertThat(result.isSuccess()).isTrue();
        // Verify the strategy is stored in options
        assertThat(DestinationServiceOptionsAugmenter.getRetrievalStrategy(options))
            .isNotEmpty()
            .contains(DestinationServiceRetrievalStrategy.ALWAYS_PROVIDER);
    }

    @Test
    void testTokenExchangeStrategyWithAugmentedOptions()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN))
                .build();

        Try<Destination> result =
            executeWithTenant(() -> loader.tryGetDestination("dest-with-token-strategy", options));

        assertThat(result.isSuccess()).isTrue();
        // Verify the token exchange strategy is stored in options
        assertThat(DestinationServiceOptionsAugmenter.getTokenExchangeStrategy(options))
            .isNotEmpty()
            .contains(DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN);
    }

    @Test
    void testRefreshTokenWithAugmentedOptions()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(DestinationServiceOptionsAugmenter.augmenter().refreshToken("test-refresh-token"))
                .build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-with-refresh-token", options));

        assertThat(result.isSuccess()).isTrue();
        // Verify the refresh token is stored in options
        assertThat(DestinationServiceOptionsAugmenter.getRefreshToken(options))
            .isNotEmpty()
            .contains("test-refresh-token");
    }

    @Test
    void testEmptyAugmentedOptions()
        throws Exception
    {
        TransparentProxy.register("gateway", "tenant-id");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        DestinationOptions options =
            DestinationOptions.builder().augmentBuilder(DestinationServiceOptionsAugmenter.augmenter()).build();

        Try<Destination> result = executeWithTenant(() -> loader.tryGetDestination("dest-empty-augmented", options));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isNotNull();
    }

    @Test
    void testTenantAccessExceptionWhenTenantMissingInContextAndProviderTenantId()
        throws IOException
    {
        TransparentProxy.register("gateway");

        final HttpClient mockHttpClient = mock(HttpClient.class);
        final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        when(mockHttpClient.execute(org.mockito.ArgumentMatchers.any(HttpHead.class))).thenReturn(response);
        HttpClientAccessor.setHttpClientFactory(dest -> mockHttpClient);

        com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException exception =
            assertThrows(
                com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException.class,
                () -> loader.tryGetDestination("test-destination"));

        assertThat(exception.getMessage()).contains("No current tenant defined");
        assertThat(exception.getMessage()).contains("no provider tenant id configured");
    }

}
