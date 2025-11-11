package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import io.vavr.control.Try;

class TransparentProxyTest
{
    private TransparentProxy loader;
    private NetworkValidator mockNetworkValidator;

    @BeforeEach
    void setUp()
    {
        loader = new TransparentProxy();
        mockNetworkValidator = mock(NetworkValidator.class);
        TransparentProxy.networkValidator = mockNetworkValidator;
    }

    @AfterEach
    void resetLoader()
    {
        TransparentProxy.uri = null;
    }

    // ========== Tests for register(String host) method ==========

    @Test
    void testRegisterWithLocalhostHost()
    {
        // Test with localhost which should always be reachable
        TransparentProxy.register("127.0.0.1");

        // Verify the stored URI has the http scheme
        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://127.0.0.1");
    }

    @Test
    void testRegisterWithHostWithoutScheme()
    {
        // Test that http:// is automatically added to host without scheme
        TransparentProxy.register("gateway");

        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway");
    }

    @Test
    void testRegisterWithHostWithHttpScheme()
    {
        // Test that existing http:// scheme is preserved
        TransparentProxy.register("http://gateway");

        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway");
    }

    @Test
    void testRegisterWithHostWithHttpsScheme()
    {
        // Test that existing https:// scheme is preserved
        TransparentProxy.register("https://gateway");

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
            .when(mockNetworkValidator)
            .verifyHostConnectivity("unknown-host", 8080);

        final DestinationAccessException exception =
            assertThrows(DestinationAccessException.class, () -> TransparentProxy.register("unknown-host", 8080));

        assertThat(exception.getMessage()).contains("could not be resolved");
    }

    @Test
    void testRegisterWithUnreachablePort()
    {
        doThrow(new DestinationAccessException("Host 'gateway' on port 65432 is not reachable"))
            .when(mockNetworkValidator)
            .verifyHostConnectivity("gateway", 65432);

        final DestinationAccessException exception =
            assertThrows(DestinationAccessException.class, () -> TransparentProxy.register("gateway", 65432));

        assertThat(exception.getMessage()).contains("is not reachable");
    }

    // ========== Tests for tryGetDestination methods ==========

    @Test
    void testTryGetDestinationWithoutOptions()
    {
        TransparentProxy.register("gateway");

        Try<Destination> result = loader.tryGetDestination("test-destination");

        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination).isNotNull();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway");
    }

    @Test
    void testTryGetDestinationWithOptions()
    {
        TransparentProxy.register("gateway");

        Try<Destination> result = loader.tryGetDestination("test-destination-with-options", null);

        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination).isNotNull();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway");
    }

    @Test
    void testTryGetDestinationReturnsSuccessfulTry()
    {
        TransparentProxy.register("https://gateway");

        Try<Destination> result1 = loader.tryGetDestination("destination1");
        Try<Destination> result2 = loader.tryGetDestination("destination2", null);

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
    {
        // Test that first registration succeeds
        TransparentProxy.register("gateway");

        Try<Destination> result1 = loader.tryGetDestination("test1");
        assertThat(result1.get().asHttp().getUri().toString()).startsWith("http://gateway");

        // Test that second registration throws exception
        DestinationAccessException exception =
            assertThrows(DestinationAccessException.class, () -> TransparentProxy.register("https://gateway"));

        assertThat(exception.getMessage())
            .contains("TransparentProxy is already registered. Only one registration is allowed.");

        // Verify that the original registration is still active
        Try<Destination> result2 = loader.tryGetDestination("test2");
        assertThat(result2.get().asHttp().getUri().toString()).startsWith("http://gateway");
    }

    @Test
    void testDestinationLoaderInterface()
    {
        // Test that TransparentProxyLoader properly implements DestinationLoader
        assertThat(loader).isInstanceOf(DestinationLoader.class);

        // Setup: register a host first
        TransparentProxy.register("gateway");

        // Test interface methods
        DestinationLoader destinationLoader = loader;

        Try<Destination> result1 = destinationLoader.tryGetDestination("test");
        Try<Destination> result2 = destinationLoader.tryGetDestination("test", null);

        assertThat(result1.isSuccess()).isTrue();
        assertThat(result2.isSuccess()).isTrue();
    }

    @Test
    void testRegisterWithDifferentSchemes()
    {
        // Test various schemes to ensure they are preserved
        String[] schemes = { "http://", "https://", "ftp://", "custom://" };
        String hostname = "gateway";

        for( String scheme : schemes ) {
            // Reset registration state between tests
            TransparentProxy.uri = null;

            // Register with the current scheme
            TransparentProxy.register(scheme + hostname);

            // Verify the scheme is preserved in the destination
            Try<Destination> result = loader.tryGetDestination("test-destination");
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.get().asHttp().getUri().toString()).startsWith(scheme + hostname);
        }
    }

    @Test
    void testRegisterWithPortInUriButNoPath()
    {
        TransparentProxy.register("https://gateway:9443");

        Try<Destination> result = loader.tryGetDestination("test-destination");
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
    {
        TransparentProxy.register("gateway", 8080);

        Try<Destination> result = loader.tryGetDestination("test-destination");
        assertThat(result.isSuccess()).isTrue();

        Destination destination = result.get();
        assertThat(destination.asHttp().getUri().toString()).startsWith("http://gateway:8080");
    }

    @Test
    void testRegisterWithHostAndPortWithScheme()
    {
        TransparentProxy.register("https://gateway", 443);

        Try<Destination> result = loader.tryGetDestination("test-destination");
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

    @Test
    void testRegisterWithValidHostsWithoutPathsSucceeds()
    {
        // Test various valid host patterns that should all succeed
        String[] validHosts =
            {
                "gateway",
                "gateway.example.com",
                "gateway.svc.cluster.local",
                "http://gateway",
                "https://gateway",
                "ftp://gateway",
                "gateway:8080",
                "gateway:443",
                "http://gateway:8080",
                "https://gateway:443",
                "127.0.0.1",
                "192.168.1.1:8080",
                "localhost", };

        for( String validHost : validHosts ) {
            // Reset registration state between tests
            resetLoader();

            // This should not throw an exception
            TransparentProxy.register(validHost);

            // Verify the registration worked
            Try<Destination> result = loader.tryGetDestination("test-destination");
            assertThat(result.isSuccess()).as("Registration should succeed for valid host: " + validHost).isTrue();
        }
    }

}
