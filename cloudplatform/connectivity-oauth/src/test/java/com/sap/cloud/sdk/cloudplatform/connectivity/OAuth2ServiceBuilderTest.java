package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;

class OAuth2ServiceBuilderTest
{
    @Test
    void testTokenUri()
    {
        final var sut = new OAuth2Service.Builder();

        final Function<String, URI> getActual =
            input -> ((OAuth2Service.Builder) sut.withTokenUri(URI.create(input))).getTokenUri();

        assertThat(getActual.apply("https://foo.bar")).hasToString("https://foo.bar/oauth/token");
        assertThat(getActual.apply("https://foo.bar/baz")).hasToString("https://foo.bar/baz");
    }

    @Test
    void testTokenUriFromString()
    {
        final var sut = new OAuth2Service.Builder();

        final Function<String, URI> getActual =
            input -> ((OAuth2Service.Builder) sut.withTokenUri(input)).getTokenUri();

        assertThat(getActual.apply("https://foo.bar")).hasToString("https://foo.bar/oauth/token");
        assertThat(getActual.apply("https://foo.bar/baz")).hasToString("https://foo.bar/baz");
    }

    @Test
    void testTenantPropagationStrategyFrom()
    {
        final var sut = new OAuth2Service.Builder();

        final Function<String, OAuth2Service.TenantPropagationStrategy> getActual =
            input -> ((OAuth2Service.Builder) sut.withTenantPropagationStrategyFrom(ServiceIdentifier.of(input)))
                .getTenantPropagationStrategy();

        assertThat(getActual.apply("identity")).isEqualTo(OAuth2Service.TenantPropagationStrategy.TENANT_SUBDOMAIN);
        assertThat(getActual.apply("IDENTITY")).isEqualTo(OAuth2Service.TenantPropagationStrategy.TENANT_SUBDOMAIN);
        assertThat(getActual.apply("not identity")).isEqualTo(OAuth2Service.TenantPropagationStrategy.ZID_HEADER);
    }

    @Test
    void testTenantPropagationStrategyFromNull()
    {
        final var sut = (OAuth2Service.Builder) new OAuth2Service.Builder().withTenantPropagationStrategyFrom(null);

        assertThat(sut.getTenantPropagationStrategy()).isEqualTo(OAuth2Service.TenantPropagationStrategy.ZID_HEADER);
    }

    @Test
    void testAdditionalParameter()
    {
        final var sut = (OAuth2Service.Builder) new OAuth2Service.Builder().withAdditionalParameter("key", "val");

        assertThat(sut.getAdditionalParameters()).isEqualTo(Map.of("key", "val"));
    }

    @Test
    void testAdditionalParametersAreAdded()
    {
        final var sut =
            (OAuth2Service.Builder) new OAuth2Service.Builder()
                .withAdditionalParameter("key1", "val1")
                .withAdditionalParameters(Map.of("key2", "val2"));

        assertThat(sut.getAdditionalParameters()).isEqualTo(Map.of("key1", "val1", "key2", "val2"));
    }

    @Test
    void testAdditionalParameterOverridesExistingValue()
    {
        final var sut =
            (OAuth2Service.Builder) new OAuth2Service.Builder()
                .withAdditionalParameter("key", "foo")
                .withAdditionalParameter("key", "bar");

        assertThat(sut.getAdditionalParameters()).isEqualTo(Map.of("key", "bar"));
    }

    @Test
    void testAdditionalParametersOverrideExistingValues()
    {
        final var sut =
            (OAuth2Service.Builder) new OAuth2Service.Builder()
                .withAdditionalParameter("key1", "foo")
                .withAdditionalParameters(Map.of("key1", "bar", "key2", "val2"));

        assertThat(sut.getAdditionalParameters()).isEqualTo(Map.of("key1", "bar", "key2", "val2"));
    }
}
