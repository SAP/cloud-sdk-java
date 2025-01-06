/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.function.Function;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import io.vavr.control.Try;

class EnvVarDestinationLoaderTest
{
    @Test
    void testBasicDestinationAttributes()
    {
        final String destinationName = "someDestination";
        final URI destinationUri = URI.create("https://www.sap.de");

        final String variableName = "destinations";
        final String variableContent = """
            [
              {
                "name": "%s",
                "URL": "%s",
                "username": "USER",
                "password": "PASSWORD",
                "object": {
                  "inner": "value",
                  "other": "also value"
                }
              }
            ]
            """.formatted(destinationName, destinationUri.toString());
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);

        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isSuccess();

        final Destination destination = maybeDestination.get();

        assertThat(destination.isHttp()).isTrue();
        assertThat(destination.asHttp().getUri()).isEqualTo(destinationUri);
    }

    @Test
    void testCompleteDestinationAttributes()
    {
        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "proxyType": "Internet",
                "description": "This destination rocks!",
                "authentication": "BasicAuthentication",
                "URL": "https://URL",
                "user": "USER",
                "password": "PASSWORD",
                "isTrustingAllCertificates": "true",
                "TrustStoreLocation": "LOCATION",
                "TrustStorePassword": "PASSWORD",
                "KeyStoreLocation": "LOCATION",
                "KeyStorePassword": "PASSWORD",
                "TLSVersion" : "1.3",
                "proxy": "proxy:1234"
              }
            ]
            """.formatted(destinationName);

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isSuccess();

        final Destination destination = maybeDestination.get();
        assertThat(destination.get("description")).contains("This destination rocks!");

        assertThat(destination.isHttp()).isTrue();
        final HttpDestination httpDestination = destination.asHttp();

        assertThat(httpDestination.get(DestinationProperty.NAME)).contains(destinationName);
        assertThat(httpDestination.getProxyType()).contains(ProxyType.INTERNET);
        assertThat(httpDestination.getUri()).isEqualTo(URI.create("https://URL"));
        assertThat(httpDestination.isTrustingAllCertificates()).isTrue();
        assertThat(httpDestination.getProxyConfiguration().get().getUri()).isEqualTo(URI.create("http://proxy:1234"));
        assertThat(httpDestination.getTlsVersion()).contains("1.3");
        assertThat(httpDestination.getAuthenticationType())
            .isEqualByComparingTo(AuthenticationType.BASIC_AUTHENTICATION);

        final Option<BasicCredentials> basicCredentials = httpDestination.getBasicCredentials();
        assertThat(basicCredentials.get().getUsername()).isEqualTo("USER");
        assertThat(basicCredentials.get().getPassword()).isEqualTo("PASSWORD");
    }

    @Test
    void testProxyUriSpecified()
    {
        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "URL": "https://URL",
                "proxy": "https://proxy:1234"
              }
            ]
            """.formatted(destinationName);

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        final URI proxyUri = maybeDestination.get().asHttp().getProxyConfiguration().get().getUri();

        assertThat(proxyUri).hasHost("proxy").hasPort(1234).hasScheme("https");
    }

    @Test
    void testProxyUriAndProxyHostAndPortSpecified()
    {
        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "URL": "https://URL",
                "proxyHost": "looser",
                "proxyPort": "5678",
                "proxy": "winner:1234"
              }
            ]
            """.formatted(destinationName);

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        final URI proxyUri = maybeDestination.get().asHttp().getProxyConfiguration().get().getUri();

        assertThat(proxyUri).hasHost("winner").hasPort(1234).hasScheme("http");
    }

    @Test
    void testProxyHostAndPortSpecified()
    {
        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "URL": "https://URL",
                "proxyHost": "proxy",
                "proxyPort": "1234",
              }
            ]
            """.formatted(destinationName);

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        final URI proxyUri = maybeDestination.get().asHttp().getProxyConfiguration().get().getUri();

        assertThat(proxyUri).hasHost("proxy").hasPort(1234).hasScheme("http");
    }

    @Test
    void testProxyTypeUncommonSpelling()
    {
        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "proxyType": "Internet",
                "description": "This destination rocks!",
                "authentication": "BasicAuthentication"
                "URL": "https://URL",
              }
            ]
            """.formatted(destinationName);

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isSuccess();

        final Destination destination = maybeDestination.get();

        assertThat(destination.isHttp()).isTrue();

        final HttpDestination httpDestination = destination.asHttp();

        final Option<ProxyType> proxyType = httpDestination.getProxyType();
        assertThat(proxyType.get()).isEqualTo(ProxyType.INTERNET);
    }

    @Test
    void testAdditionalProperty()
    {
        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "proxyType": "Internet",
                "description": "This destination rocks!",
                "authentication": "BasicAuthentication"
                "URL": "https://URL",
                "shoeSize": 42
              }
            ]
            """.formatted(destinationName);

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isSuccess();

        final Destination destination = maybeDestination.get();

        final Option<Integer> shoeSize = destination.get("shoeSize", o -> Integer.valueOf((String) o));

        assertThat(shoeSize.get()).isEqualTo(42);
    }

    @Test
    void testDestinationWithoutNameProperty()
    {
        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "URL": "https://URL",
                "proxy": "https://proxy:1234"
              }
            ]
            """;

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);

        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isFailure();

        assertThat(maybeDestination.getCause()).isInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testUpperAndLowerCaseInParameterNames()
    {
        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "URL": "https://URL",
                "proxy": "https://proxy:1234"
              }
            ]
            """.formatted(destinationName);

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        final URI proxyUri = maybeDestination.get().asHttp().getProxyConfiguration().get().getUri();

        assertThat(proxyUri).hasHost("proxy").hasPort(1234).hasScheme("https");
    }

    @Test
    void testFallbackPropertyKeys()
    {
        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "proxyType": "Internet",
                "description": "This destination rocks!",
                "URL": "https://URL",
                "username": "USER",
                "password": "PASSWORD",
                "authtype": "BasicAuthentication"
              }
            ]
            """.formatted(destinationName);

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isSuccess();

        final Destination destination = maybeDestination.get();

        assertThat(destination.asHttp().getBasicCredentials().get().getUsername()).isEqualTo("USER");
        assertThat(destination.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.BASIC_AUTHENTICATION);
    }

    @Test
    void testFallbackToBasicAuth()
    {
        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "URL": "https://URL",
                "username": "USER",
                "password": "PASSWORD",
              }
            ]
            """.formatted(destinationName);

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isSuccess();

        final Destination destination = maybeDestination.get();

        assertThat(destination.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.BASIC_AUTHENTICATION);
    }

    @Test
    void testEmptyEnvironmentVariable()
    {
        final String variableContent = "[]";
        final String variableName = "destinations";

        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination("some-name");
        VavrAssertions.assertThat(maybeDestination).isFailure().failBecauseOf(DestinationNotFoundException.class);
    }

    @Test
    void testMissingEnvironmentVariable()
    {
        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, null);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination("some-name");
        VavrAssertions.assertThat(maybeDestination).isFailure().failBecauseOf(DestinationNotFoundException.class);
    }

    @Test
    void tokenForwardingInJsonShouldBeReadAsTokenForwardingAuthenticationType()
    {
        final String variableName = "destinations";

        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "URL": "https://URL",
                "authentication": "TokenForwarding"
              }
            ]
            """.formatted(destinationName);
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        assertThat(maybeDestination.get().asHttp().getAuthenticationType())
            .isEqualTo(AuthenticationType.TOKEN_FORWARDING);
    }

    @Test
    void forwardAuthTokenInJsonShouldBeReadAsTokenForwardingAuthenticationType()
    {
        final String variableName = "destinations";

        final String destinationName = "MyDestination";
        final String variableContent = """
            [
              {
                "type": "HTTP",
                "name": "%s",
                "URL": "https://URL",
                "forwardAuthToken": true
              }
            ]
            """.formatted(destinationName);
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        assertThat(maybeDestination.get().asHttp().getAuthenticationType())
            .isEqualTo(AuthenticationType.TOKEN_FORWARDING);
    }

    @Test
    void testGetAllDestinations()
    {
        final String variableContent = """
            [
              {"type":"HTTP","name":"foo","URL":"https://foo"},
              {"type":"HTTP","name":"bar","URL":"https://bar"},
              {"type":"HTTP","name":"baz","URL":"https://baz"}
            ]
            """;

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Iterable<Destination>> destinations =
            new EnvVarDestinationLoader(envVars, variableName).tryGetAllDestinations();

        assertThat(destinations).isNotEmpty();
        assertThat(destinations.get())
            .hasSize(3)
            .extracting(Destination::asHttp)
            .extracting(HttpDestination::getUri)
            .extracting(URI::getHost)
            .containsExactly("foo", "bar", "baz");
    }
}
