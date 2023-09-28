package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.function.Function;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class EnvVarDestinationLoaderTest
{
    @Test
    public void testBasicDestinationAttributes()
    {
        final String destinationName = "someDestination";
        final URI destinationUri = URI.create("https://www.sap.de");

        final String variableName = "destinations";
        final String variableContent =
            "[{\"name\": \""
                + destinationName
                + "\", \"URL\": \""
                + destinationUri
                + "\", \"username\": \"USER\", \"password\": \"PASSWORD\", \"object\": {\"inner\": \"value\", \"other\":\"also value\"}}]";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);

        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isSuccess();

        final Destination destination = maybeDestination.get();

        assertThat(destination.isHttp()).isTrue();
        assertThat(destination.asHttp().getUri()).isEqualTo(destinationUri);
    }

    @Test
    public void testCompleteDestinationAttributes()
    {
        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"proxyType\": \"Internet\","
                + "\"description\": \"This destination rocks!\","
                + "\"authentication\": \"BasicAuthentication\","
                + "\"URL\": \"https://URL\","
                + "\"user\": \"USER\","
                + "\"password\": \"PASSWORD\","
                + "\"isTrustingAllCertificates\": \"true\","
                + "\"TrustStoreLocation\": \"LOCATION\","
                + "\"TrustStorePassword\": \"PASSWORD\","
                + "\"KeyStoreLocation\": \"LOCATION\","
                + "\"KeyStorePassword\": \"PASSWORD\","
                + "\"TLSVersion\" : \"1.3\","
                + "\"proxy\": \"proxy:1234\""
                + "}"
                + "]";

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isSuccess();

        final Destination destination = maybeDestination.get();

        assertThat(destination.isHttp()).isTrue();

        final Option<String> description = destination.get("description", String.class::cast);

        assertThat(description.get()).isEqualTo("This destination rocks!");

        final HttpDestination httpDestination = destination.asHttp();

        final Option<String> name = httpDestination.get(DestinationProperty.NAME);

        assertThat(name.get()).isEqualTo(destinationName);

        final Option<ProxyType> proxyType = httpDestination.getProxyType();

        assertThat(proxyType.get()).isEqualTo(ProxyType.INTERNET);

        assertThat(httpDestination.getUri()).isEqualTo(URI.create("https://URL"));

        assertThat(httpDestination.isTrustingAllCertificates()).isTrue();

        assertThat(httpDestination.getProxyConfiguration().get().getUri()).isEqualTo(URI.create("http://proxy:1234"));

        assertThat(httpDestination.getTlsVersion().get()).isEqualTo("1.3");

        assertThat(httpDestination.getAuthenticationType())
            .isEqualByComparingTo(AuthenticationType.BASIC_AUTHENTICATION);

        final Option<BasicCredentials> basicCredentials = httpDestination.getBasicCredentials();
        assertThat(basicCredentials.get().getUsername()).isEqualTo("USER");
        assertThat(basicCredentials.get().getPassword()).isEqualTo("PASSWORD");
    }

    @Test
    public void testProxyUriSpecified()
    {
        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"URL\": \"https://URL\","
                + "\"proxy\": \"https://proxy:1234\","
                + "}"
                + "]";

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        final URI proxyUri = maybeDestination.get().asHttp().getProxyConfiguration().get().getUri();

        assertThat(proxyUri).hasHost("proxy").hasPort(1234).hasScheme("https");
    }

    @Test
    public void testProxyUriAndProxyHostAndPortSpecified()
    {
        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"URL\": \"https://URL\","
                + "\"proxyHost\": \"looser\","
                + "\"proxyPort\": \"5678\","
                + "\"proxy\": \"winner:1234\","
                + "}"
                + "]";

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        final URI proxyUri = maybeDestination.get().asHttp().getProxyConfiguration().get().getUri();

        assertThat(proxyUri).hasHost("winner").hasPort(1234).hasScheme("http");
    }

    @Test
    public void testProxyHostAndPortSpecified()
    {
        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"URL\": \"https://URL\","
                + "\"proxyHost\": \"proxy\","
                + "\"proxyPort\": \"1234\","
                + "}"
                + "]";

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        final URI proxyUri = maybeDestination.get().asHttp().getProxyConfiguration().get().getUri();

        assertThat(proxyUri).hasHost("proxy").hasPort(1234).hasScheme("http");
    }

    @Test
    public void testProxyTypeUncommonSpelling()
    {
        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"proxyType\": \"onpremise\","
                + "\"description\": \"This destination rocks!\","
                + "\"authentication\": \"BasicAuthentication\""
                + "\"URL\": \"https://URL\","
                + "}"
                + "]";

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isSuccess();

        final Destination destination = maybeDestination.get();

        assertThat(destination.isHttp()).isTrue();

        final HttpDestination httpDestination = destination.asHttp();

        final Option<ProxyType> proxyType = httpDestination.getProxyType();
        assertThat(proxyType.get()).isEqualTo(ProxyType.ON_PREMISE);
    }

    @Test
    public void testAdditionalProperty()
    {
        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"proxyType\": \"onpremise\","
                + "\"description\": \"This destination rocks!\","
                + "\"authentication\": \"BasicAuthentication\""
                + "\"URL\": \"https://URL\","
                + "\"shoeSize\": 42"
                + "}"
                + "]";

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
    public void testDestinationWithoutNameProperty()
    {
        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"URL\": \"https://URL\","
                + "\"proxy\": \"https://proxy:1234\","
                + "}"
                + "]";

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);

        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isFailure();

        assertThat(maybeDestination.getCause()).isInstanceOf(DestinationAccessException.class);
    }

    @Test
    public void testUpperAndLowerCaseInParameterNames()
    {
        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"URL\": \"https://URL\","
                + "\"Proxy\": \"https://proxy:1234\","
                + "}"
                + "]";

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        final URI proxyUri = maybeDestination.get().asHttp().getProxyConfiguration().get().getUri();

        assertThat(proxyUri).hasHost("proxy").hasPort(1234).hasScheme("https");
    }

    @Test
    public void testFallbackPropertyKeys()
    {
        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"proxyType\": \"onpremise\","
                + "\"description\": \"This destination rocks!\","
                + "\"URL\": \"https://URL\","
                + "\"username\": \"USER\","
                + "\"password\": \"PASSWORD\","
                + "\"authtype\": \"BasicAuthentication\""
                + "}"
                + "]";

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
    public void testFallbackToBasicAuth()
    {
        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"URL\": \"https://URL\","
                + "\"username\": \"USER\","
                + "\"password\": \"PASSWORD\","
                + "}"
                + "]";

        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        VavrAssertions.assertThat(maybeDestination).isSuccess();

        final Destination destination = maybeDestination.get();

        assertThat(destination.asHttp().getAuthenticationType()).isEqualTo(AuthenticationType.BASIC_AUTHENTICATION);
    }

    @Test
    public void testEmptyEnvironmentVariable()
    {
        final String variableContent = "[]";
        final String variableName = "destinations";

        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination("some-name");
        VavrAssertions.assertThat(maybeDestination).isFailure().failBecauseOf(DestinationNotFoundException.class);
    }

    @Test
    public void testMissingEnvironmentVariable()
    {
        final String variableName = "destinations";
        final Function<String, String> envVars = HashMap.of(variableName, null);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination("some-name");
        VavrAssertions.assertThat(maybeDestination).isFailure().failBecauseOf(DestinationNotFoundException.class);
    }

    @Test
    public void tokenForwardingInJsonShouldBeReadAsTokenForwardingAuthenticationType()
    {
        final String variableName = "destinations";

        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"URL\": \"https://URL\","
                + "\"authentication\": \"TokenForwarding\""
                + "}"
                + "]";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        assertThat(maybeDestination.get().asHttp().getAuthenticationType())
            .isEqualTo(AuthenticationType.TOKEN_FORWARDING);
    }

    @Test
    public void forwardAuthTokenInJsonShouldBeReadAsTokenForwardingAuthenticationType()
    {
        final String variableName = "destinations";

        final String destinationName = "MyDestination";
        final String variableContent =
            "["
                + "{"
                + "\"type\": \"HTTP\","
                + "\"name\": \""
                + destinationName
                + "\","
                + "\"URL\": \"https://URL\","
                + "\"forwardAuthToken\": true"
                + "}"
                + "]";
        final Function<String, String> envVars = HashMap.of(variableName, variableContent);
        final Try<Destination> maybeDestination =
            new EnvVarDestinationLoader(envVars, variableName).tryGetDestination(destinationName);

        assertThat(maybeDestination.get().asHttp().getAuthenticationType())
            .isEqualTo(AuthenticationType.TOKEN_FORWARDING);
    }

    @Test
    public void testGetAllDestinations()
    {
        final String variableContent =
            "["
                + "{\"type\":\"HTTP\",\"name\":\"foo\",\"URL\":\"https://foo\"},"
                + "{\"type\":\"HTTP\",\"name\":\"bar\",\"URL\":\"https://bar\"},"
                + "{\"type\":\"HTTP\",\"name\":\"baz\",\"URL\":\"https://baz\"}"
                + "]";

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
