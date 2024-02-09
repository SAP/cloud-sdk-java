package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.security.config.ClientCredentials;

class OAuth2ServiceBuilderTest
{
    @Test
    void testTokenUri()
    {
        final OAuth2Service.Builder sut = new OAuth2Service.Builder();

        final Function<String, URI> getActual = input -> sut.withTokenUri(URI.create(input)).getTokenUri();

        assertThat(getActual.apply("https://foo.bar")).hasToString("https://foo.bar/oauth/token");
        assertThat(getActual.apply("https://foo.bar/baz")).hasToString("https://foo.bar/baz");
    }

    @Test
    void testTokenUriFromString()
    {
        final OAuth2Service.Builder sut = new OAuth2Service.Builder();

        final Function<String, URI> getActual = input -> sut.withTokenUri(input).getTokenUri();

        assertThat(getActual.apply("https://foo.bar")).hasToString("https://foo.bar/oauth/token");
        assertThat(getActual.apply("https://foo.bar/baz")).hasToString("https://foo.bar/baz");
    }

    @Test
    void testTenantPropagationStrategyFrom()
    {
        final OAuth2Service.Builder sut = new OAuth2Service.Builder();

        final Function<String, OAuth2Service.TenantPropagationStrategy> getActual =
            input -> sut.withTenantPropagationStrategyFrom(ServiceIdentifier.of(input)).getTenantPropagationStrategy();

        assertThat(getActual.apply("identity")).isEqualTo(OAuth2Service.TenantPropagationStrategy.TENANT_SUBDOMAIN);
        assertThat(getActual.apply("IDENTITY")).isEqualTo(OAuth2Service.TenantPropagationStrategy.TENANT_SUBDOMAIN);
        assertThat(getActual.apply("not identity")).isEqualTo(OAuth2Service.TenantPropagationStrategy.ZID_HEADER);
    }

    @Test
    void testTenantPropagationStrategyFromNull()
    {
        final OAuth2Service.Builder sut = new OAuth2Service.Builder().withTenantPropagationStrategyFrom(null);

        assertThat(sut.getTenantPropagationStrategy()).isEqualTo(OAuth2Service.TenantPropagationStrategy.ZID_HEADER);
    }

    @Test
    void testAdditionalParameter()
    {
        final OAuth2Service.Builder sut = new OAuth2Service.Builder().withAdditionalParameter("key", "val");

        assertThat(sut.getAdditionalParameters()).isEqualTo(Map.of("key", "val"));
    }

    @Test
    void testAdditionalParametersAreAdded()
    {
        final OAuth2Service.Builder sut =
            new OAuth2Service.Builder()
                .withAdditionalParameter("key1", "val1")
                .withAdditionalParameters(Map.of("key2", "val2"));

        assertThat(sut.getAdditionalParameters()).isEqualTo(Map.of("key1", "val1", "key2", "val2"));
    }

    @Test
    void testAdditionalParameterOverridesExistingValue()
    {
        final OAuth2Service.Builder sut =
            new OAuth2Service.Builder().withAdditionalParameter("key", "foo").withAdditionalParameter("key", "bar");

        assertThat(sut.getAdditionalParameters()).isEqualTo(Map.of("key", "bar"));
    }

    @Test
    void testAdditionalParametersOverrideExistingValues()
    {
        final OAuth2Service.Builder sut =
            new OAuth2Service.Builder()
                .withAdditionalParameter("key1", "foo")
                .withAdditionalParameters(Map.of("key1", "bar", "key2", "val2"));

        assertThat(sut.getAdditionalParameters()).isEqualTo(Map.of("key1", "bar", "key2", "val2"));
    }

    @Test
    void testBuildWithoutTokenUriThrowsException()
    {
        final OAuth2Service.Builder sut =
            new OAuth2Service.Builder().withIdentity(new ClientCredentials("id", "secret"));

        assertThatThrownBy(sut::build)
            .isExactlyInstanceOf(ShouldNotHappenException.class)
            .hasMessageContaining("Some required parameters for the OAuth2Service are null.");
    }

    @Test
    void testBuildWithoutIdentityThrowsException()
    {
        final OAuth2Service.Builder sut = new OAuth2Service.Builder().withTokenUri("https://foo.bar");

        assertThatThrownBy(sut::build)
            .isExactlyInstanceOf(ShouldNotHappenException.class)
            .hasMessageContaining("Some required parameters for the OAuth2Service are null.");
    }

    @Test
    void testBuildWithMandatoryParametersDoesNotThrow()
    {
        final OAuth2Service.Builder sut =
            new OAuth2Service.Builder()
                .withTokenUri("https://foo.bar")
                .withIdentity(new ClientCredentials("id", "secret"));

        assertThatNoException().isThrownBy(sut::build);
    }
}
