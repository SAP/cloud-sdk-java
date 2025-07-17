package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DefaultOAuth2PropertySupplier.convert;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.environment.servicebinding.api.exception.ServiceBindingAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.security.config.ClientCertificate;
import com.sap.cloud.security.config.CredentialType;

import lombok.RequiredArgsConstructor;

class DefaultOAuth2PropertySupplierTest
{
    private DefaultOAuth2PropertySupplier sut;

    @Test
    void testValueConverter()
    {
        assertThat(convert(null, String.class)).isNull();
        assertThat(convert(null, URI.class)).isNull();
        assertThat(convert(null, Integer.class)).isNull();
        assertThat(convert(null, CredentialType.class)).isNull();

        assertThat(convert("foo", String.class)).isEqualTo("foo");
        assertThat(convert(1337, String.class)).isEqualTo("1337");

        assertThat(convert(1337, Integer.class)).isEqualTo(1337);
        assertThat(convert("1337", Integer.class)).isEqualTo(1337);

        assertThat(convert("https://foo.bar", URI.class)).isEqualTo(URI.create("https://foo.bar"));
        assertThat(convert(URI.create("https://foo.bar"), URI.class)).isEqualTo(URI.create("https://foo.bar"));
        assertThatThrownBy(() -> convert("not a valid uri", URI.class))
            .isExactlyInstanceOf(DestinationAccessException.class)
            .hasCauseExactlyInstanceOf(URISyntaxException.class);

        assertThat(convert(CredentialType.X509, CredentialType.class)).isEqualTo(CredentialType.X509);
        assertThat(convert(CredentialType.X509.toString(), CredentialType.class)).isEqualTo(CredentialType.X509);
        assertThat(convert("X509_GENERATED", CredentialType.class)).isEqualTo(CredentialType.X509_GENERATED);
        assertThatThrownBy(() -> convert("not a valid credential type", CredentialType.class))
            .isExactlyInstanceOf(DestinationAccessException.class)
            .hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCredentialAccess()
    {
        final ServiceBinding binding =
            new ServiceBindingBuilder(ServiceIdentifier.DESTINATION)
                .with("name", "asdf")
                .with("credentials.name", "foo")
                .with("credentials.uaa.name", "bar")
                .build();

        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        sut = new DefaultOAuth2PropertySupplier(options, Collections.emptyList());

        assertThat(sut.getCredential(String.class, "name")).contains("foo");
        assertThat(sut.getCredential(String.class, "uaa", "name")).contains("bar");
        assertThat(sut.getCredential(String.class, "bar")).isEmpty();

        assertThatThrownBy(() -> sut.getCredentialOrThrow(String.class, "bar"))
            .isInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testOAuthCredentialAccess()
    {
        final ServiceBinding binding =
            new ServiceBindingBuilder(ServiceIdentifier.DESTINATION)
                .with("name", "asdf")
                .with("credentials.name", "foo")
                .with("credentials.uaa.name", "bar")
                .build();
        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        sut = new DefaultOAuth2PropertySupplier(options);

        assertThat(sut.getOAuthCredential(String.class, "name")).contains("bar");
        assertThat(sut.getOAuthCredential(String.class, "uaa", "name")).isEmpty();

        assertThatThrownBy(() -> sut.getOAuthCredentialOrThrow(String.class, "bar"))
            .isInstanceOf(DestinationAccessException.class);
    }

    @Test
    void testClientSecretIsTheDefault()
    {
        final ServiceBindingDestinationOptions options =
            ServiceBindingDestinationOptions
                .forService(new ServiceBindingBuilder(ServiceIdentifier.DESTINATION).build())
                .build();

        sut = new DefaultOAuth2PropertySupplier(options);

        assertThat(sut.getCredentialType()).isEqualTo(CredentialType.BINDING_SECRET);
    }

    @Test
    void testCredentialTypeInstanceSecret()
    {
        final ServiceBinding binding =
            new ServiceBindingBuilder(ServiceIdentifier.of("testInstanceSecret"))
                .with("credentials.uaa.credential-type", "instance-secret")
                .build();
        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        sut = new DefaultOAuth2PropertySupplier(options);

        assertThat(sut.getCredentialType()).isEqualTo(CredentialType.INSTANCE_SECRET);
        assertThatCode(sut::getClientIdentity)
            .isInstanceOf(DestinationAccessException.class)
            .hasMessage("Failed to resolve property [uaa][clientid] from service binding.");
    }

    @Test
    void testCredentialTypeX509()
    {
        final ServiceBinding binding =
            new ServiceBindingBuilder(ServiceIdentifier.of("testX509"))
                .with("credentials.uaa.credential-type", "x509")
                .with("credentials.uaa.clientid", "id")
                .with("credentials.uaa.certificate", "cert")
                .with("credentials.uaa.key", "key")
                .build();
        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        sut = new DefaultOAuth2PropertySupplier(options);

        assertThat(sut.getCredentialType()).isEqualTo(CredentialType.X509);
        assertThat(sut.getClientIdentity()).isInstanceOfSatisfying(ClientCertificate.class, cc -> {
            assertThat(cc.getId()).isEqualTo("id");
            assertThat(cc.getCertificate()).isEqualTo("cert");
            assertThat(cc.getKey()).isEqualTo("key");
        });
    }

    @Test
    void testCredentialTypeX509Generated()
    {
        final ServiceBinding binding =
            new ServiceBindingBuilder(ServiceIdentifier.of("testX509"))
                .with("credentials.uaa.credential-type", "X509_GENERATED")
                .with("credentials.uaa.clientid", "id")
                .with("credentials.uaa.certificate", "cert")
                .with("credentials.uaa.key", "key")
                .build();
        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        sut = new DefaultOAuth2PropertySupplier(options);

        assertThat(sut.getCredentialType()).isEqualTo(CredentialType.X509_GENERATED);
        assertThat(sut.getClientIdentity()).isInstanceOfSatisfying(ClientCertificate.class, cc -> {
            assertThat(cc.getId()).isEqualTo("id");
            assertThat(cc.getCertificate()).isEqualTo("cert");
            assertThat(cc.getKey()).isEqualTo("key");
        });
    }

    @Test
    void testCredentialTypeX509Provided()
    {
        final ServiceBinding binding =
            new ServiceBindingBuilder(ServiceIdentifier.of("testX509"))
                .with("credentials.uaa.credential-type", "X509_PROVIDED")
                .with("credentials.uaa.clientid", "id")
                .build();
        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        sut = new DefaultOAuth2PropertySupplier(options);

        assertThatThrownBy(sut::getClientIdentity)
            .isInstanceOf(DestinationAccessException.class)
            .hasMessageContaining("not supported");
    }

    @Test
    void testCredentialTypeX509_ATTESTED()
    {
        final ServiceBinding binding =
            new ServiceBindingBuilder(ServiceIdentifier.of("testX509_attested"))
                .with("credentials.uaa.credential-type", "X509_AttEsTEd") // should be case-insensitive
                .with("credentials.uaa.clientid", "id")
                .build();
        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        sut = new DefaultOAuth2PropertySupplier(options);

        assertThat(sut.getCredentialType()).isEqualTo(CredentialType.X509_ATTESTED);
        assertThatThrownBy(sut::getClientIdentity)
            .isInstanceOf(CloudPlatformException.class)
            .describedAs("We are not mocking the Zero Trust Identity Service here, so this should be a failure")
            .hasRootCauseInstanceOf(ServiceBindingAccessException.class);
    }

    @Test
    void testTimeoutConfiguration()
    {
        final ServiceBinding binding =
            new ServiceBindingBuilder(ServiceIdentifier.DESTINATION).with("name", "asdf").build();
        ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        sut = new DefaultOAuth2PropertySupplier(options);

        assertThat(sut.getOAuth2Options().getTimeLimiter()).isSameAs(OAuth2Options.DEFAULT_TIMEOUT);

        options =
            ServiceBindingDestinationOptions
                .forService(binding)
                .withOption(
                    OAuth2Options.TokenRetrievalTimeout.of(TimeLimiterConfiguration.of(Duration.ofSeconds(100))))
                .build();
        sut = new DefaultOAuth2PropertySupplier(options);
        assertThat(sut.getOAuth2Options().getTimeLimiter())
            .isEqualTo(TimeLimiterConfiguration.of(Duration.ofSeconds(100)));
    }

    @Test
    void testTokenCacheConfigurationOption()
    {
        final ServiceBinding binding =
            new ServiceBindingBuilder(ServiceIdentifier.DESTINATION).with("name", "asdf").build();
        ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        sut = new DefaultOAuth2PropertySupplier(options);

        assertThat(sut.getOAuth2Options().getTokenCacheParameters())
            .isSameAs(OAuth2Options.DEFAULT_TOKEN_CACHE_PARAMETERS);

        options =
            ServiceBindingDestinationOptions
                .forService(binding)
                .withOption(OAuth2Options.TokenCacheParameters.of(Duration.ofSeconds(10), 5, Duration.ofSeconds(1)))
                .build();
        sut = new DefaultOAuth2PropertySupplier(options);
        assertThat(sut.getOAuth2Options().getTokenCacheParameters())
            .usingRecursiveComparison()
            .isEqualTo(OAuth2Options.TokenCacheParameters.of(Duration.ofSeconds(10), 5, Duration.ofSeconds(1)));
    }

    @RequiredArgsConstructor
    private static final class ServiceBindingBuilder
    {
        @Nonnull
        private final Map<String, Object> properties = new HashMap<>();

        @Nonnull
        private final ServiceIdentifier serviceIdentifier;

        @Nonnull
        @SuppressWarnings( "unchecked" )
        ServiceBindingBuilder with( @Nonnull final String key, @Nonnull final Object value )
        {
            final String[] paths = key.split("\\.");

            Map<String, Object> currentContainer = properties;
            for( int pathIndex = 0; pathIndex < paths.length - 1; ++pathIndex ) {
                final String path = paths[pathIndex];
                final Object subContainer = currentContainer.computeIfAbsent(path, k -> new HashMap<String, Object>());

                if( !(subContainer instanceof Map) ) {
                    throw new IllegalStateException(String.format("Unable to set value '%s' = '%s'.", key, value));
                }

                currentContainer = (Map<String, Object>) subContainer;
            }

            currentContainer.put(paths[paths.length - 1], value);
            return this;
        }

        @Nonnull
        ServiceBinding build()
        {
            return DefaultServiceBinding
                .builder()
                .copy(properties)
                .withCredentialsKey("credentials")
                .withServiceIdentifier(serviceIdentifier)
                .build();
        }
    }
}
