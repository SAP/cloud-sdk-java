/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.IasOptions;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServicePropertySuppliers.AI_CORE;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServicePropertySuppliers.BUSINESS_LOGGING;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServicePropertySuppliers.BUSINESS_RULES;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServicePropertySuppliers.CONNECTIVITY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServicePropertySuppliers.IDENTITY_AUTHENTICATION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServicePropertySuppliers.WORKFLOW;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.bindingWithCredentials;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.environment.servicebinding.api.exception.ServiceBindingAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.BusinessLoggingOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.BusinessRulesOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.WorkflowOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@SuppressWarnings( "unchecked" ) // suppressed for the `entry("key", value)` calls
class BtpServicePropertySuppliersTest
{
    @Test
    void testKnownServiceBindingPropertyMappings()
    {
        // we want to support every service identifier (except Auditlog)  that comes from the ServiceIdentifier class OOTB
        // find all known entries via reflection
        final List<ServiceBindingDestinationOptions> identifierOptions =
            Arrays
                .stream(ServiceIdentifier.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> ServiceIdentifier.class.isAssignableFrom(f.getType()))
                .map(f -> Try.of(() -> (ServiceIdentifier) f.get(null)).get()) //    ServiceIdentifier
                .filter(f -> !f.equals(ServiceIdentifier.AUDIT_LOG_RETRIEVAL))// exclude Auditlog
                .map(ServiceBindingTestUtility::bindingWithCredentials) //           ServiceBinding
                .map(s -> ServiceBindingDestinationOptions.forService(s).build()) // ServiceBindingDestinationOptions
                .collect(Collectors.toList());

        final List<OAuth2PropertySupplierResolver> defaultResolvers =
            BtpServicePropertySuppliers.getDefaultServiceResolvers();
        assertThat(identifierOptions).allMatch(o -> defaultResolvers.stream().anyMatch(f -> f.matches(o)));
    }

    @Nested
    @DisplayName( "Destination" )
    class DestinationTest
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                ServiceIdentifier.DESTINATION,
                entry("uri", "https://destination-configuration.example.com"));

        @Test
        void testDestination()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(binding).build();

            final OAuth2PropertySupplier sut = BtpServicePropertySuppliers.DESTINATION.resolve(options);

            assertThat(sut.getServiceUri()).hasToString("https://destination-configuration.example.com");
        }
    }

    @Nested
    @DisplayName( "Connectivity" )
    class ConnectivityTest
    {
        private ServiceBindingDestinationOptions createOptionsWithCredentials(
            final Map.Entry<String, Object>... entries )
        {
            final ServiceBinding binding = bindingWithCredentials(ServiceIdentifier.CONNECTIVITY, entries);

            return ServiceBindingDestinationOptions.forService(binding).build();
        }

        @Test
        void testGetServiceUriUsesNewProxyPortProperty()
        {
            final ServiceBindingDestinationOptions options =
                createOptionsWithCredentials(
                    entry("onpremise_proxy_host", "host"),
                    entry("onpremise_proxy_http_port", 1337));

            final OAuth2PropertySupplier sut = CONNECTIVITY.resolve(options);

            assertThat(sut.getServiceUri()).hasToString("http://host:1337");
        }

        @Test
        void testGetServiceUriPrefersNewProxyPortProperty()
        {
            final ServiceBindingDestinationOptions options =
                createOptionsWithCredentials(
                    entry("onpremise_proxy_host", "host"),
                    entry("onpremise_proxy_port", 7331),
                    entry("onpremise_proxy_http_port", 1337));

            final OAuth2PropertySupplier sut = CONNECTIVITY.resolve(options);

            assertThat(sut.getServiceUri()).hasToString("http://host:1337");
        }

        @Test
        void testGetServiceUriUsesLegacyProxyPortPropertyAsFallback()
        {
            final ServiceBindingDestinationOptions options =
                createOptionsWithCredentials(
                    entry("onpremise_proxy_host", "host"),
                    entry("onpremise_proxy_port", 1337));

            final OAuth2PropertySupplier sut = CONNECTIVITY.resolve(options);

            assertThat(sut.getServiceUri()).hasToString("http://host:1337");
        }

        @Test
        void testGetServiceUriThrowsWithoutProxyHost()
        {
            final ServiceBindingDestinationOptions options =
                createOptionsWithCredentials(entry("onpremise_proxy_http_port", 1337));

            final OAuth2PropertySupplier sut = CONNECTIVITY.resolve(options);

            assertThatThrownBy(sut::getServiceUri)
                .isExactlyInstanceOf(DestinationAccessException.class)
                .hasMessageContaining("onpremise_proxy_host");
        }

        @Test
        void testGetServiceUriThrowsWithoutProxyPort()
        {
            final ServiceBindingDestinationOptions options =
                createOptionsWithCredentials(entry("onpremise_proxy_host", "host"));

            final OAuth2PropertySupplier sut = CONNECTIVITY.resolve(options);

            assertThatThrownBy(sut::getServiceUri)
                .isExactlyInstanceOf(DestinationAccessException.class)
                .hasMessageContaining("onpremise_proxy_port");
        }
    }

    @Nested
    @DisplayName( "Business Rules" )
    class BusinessRulesTest
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                ServiceIdentifier.BUSINESS_RULES,
                entry("endpoints.rule_repository_url", "https://business-rules.authoring_api.example.com"),
                entry("endpoints.rule_runtime_url", "https://business-rules.execution_api.example.com"));

        @ParameterizedTest
        @EnumSource( BusinessRulesOptions.class )
        void testApiSelection( final BusinessRulesOptions api )
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(binding).withOption(api).build();

            final OAuth2PropertySupplier sut = BUSINESS_RULES.resolve(options);

            assertThat(sut.getServiceUri().toString()).containsIgnoringCase(api.name());
        }

        @Test
        void testThrowsWithoutOption()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(binding).build();

            final OAuth2PropertySupplier sut = BUSINESS_RULES.resolve(options);

            assertThatThrownBy(sut::getServiceUri)
                .isExactlyInstanceOf(DestinationAccessException.class)
                .hasMessageContaining("No option given");
        }
    }

    @Nested
    @DisplayName( "Workflow" )
    class WorkflowTest
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                ServiceIdentifier.WORKFLOW,
                entry("endpoints.workflow_rest_url", "https://workflow-rest_api.example.com"),
                entry("endpoints.workflow_odata_url", "https://workflow-odata_api.example.com"));

        @ParameterizedTest
        @EnumSource( WorkflowOptions.class )
        void testApiSelection( final WorkflowOptions api )
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(binding).withOption(api).build();

            final OAuth2PropertySupplier sut = WORKFLOW.resolve(options);

            assertThat(sut.getServiceUri().toString()).contains(api.name().toLowerCase());
        }

        @Test
        void testThrowsWithoutOption()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(binding).build();

            final OAuth2PropertySupplier sut = WORKFLOW.resolve(options);

            assertThatThrownBy(sut::getServiceUri)
                .isExactlyInstanceOf(DestinationAccessException.class)
                .hasMessageContaining("No option given");
        }
    }

    @Nested
    @DisplayName( "AiCore" )
    class AiCoreTest
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                ServiceIdentifier.of("aicore"),
                entry("serviceurls.AI_API_URL", "https://api.ai.internalprod.eu-central-1.aws.ml.hana.ondemand.com"),
                entry("clientid", "client-id"),
                entry("clientsecret", "client-secret"),
                entry("url", "https://subaccount.authentication.sap.hana.ondemand.com"));

        @Test
        void testAiCore()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(binding).build();

            final OAuth2PropertySupplier sut = AI_CORE.resolve(options);

            assertThat(sut).isNotNull();
            assertThat(sut.getServiceUri())
                .isEqualTo(URI.create("https://api.ai.internalprod.eu-central-1.aws.ml.hana.ondemand.com"));
            assertThat(sut.getClientIdentity().getId()).isEqualTo("client-id");
            assertThat(sut.getClientIdentity().getSecret()).isEqualTo("client-secret");
            assertThat(sut.getTokenUri())
                .isEqualTo(URI.create("https://subaccount.authentication.sap.hana.ondemand.com"));
        }
    }

    @Nested
    @DisplayName( "Business Logging" )
    class BusinessLoggingTest
    {
        private final ServiceBinding binding =
            bindingWithCredentials(
                ServiceIdentifier.of("business-logging"),
                entry("endpoints.configservice", "https://business-logging.config_api.example.com"),
                entry("endpoints.readservice", "https://business-logging.read_api.example.com"),
                entry("endpoints.textresourceservice", "https://business-logging.text_api.example.com"),
                entry("endpoints.writeservice", "https://business-logging.write_api.example.com"));

        private final ServiceBinding bindingWithRedundantPaths =
            bindingWithCredentials(
                ServiceIdentifier.of("business-logging"),
                entry("endpoints.configservice", "https://business-logging.config_api.example.com/buslogs/configs"),
                entry(
                    "endpoints.readservice",
                    "https://business-logging.read_api.example.com/odata/v2/com.sap.bs.businesslogging.DisplayMessage"),
                entry(
                    "endpoints.textresourceservice",
                    "https://business-logging.text_api.example.com/buslogs/configs/textresources"),
                entry("endpoints.writeservice", "https://business-logging.write_api.example.com/buslogs/log"));

        @ParameterizedTest
        @EnumSource( BusinessLoggingOptions.class )
        void testApiSelection( final BusinessLoggingOptions api )
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(binding).withOption(api).build();

            final OAuth2PropertySupplier sut = BUSINESS_LOGGING.resolve(options);
            assertThat(sut).isNotNull();

            final ServiceBindingDestinationOptions redundantOptions =
                ServiceBindingDestinationOptions.forService(bindingWithRedundantPaths).withOption(api).build();

            final OAuth2PropertySupplier redundantSut = BUSINESS_LOGGING.resolve(redundantOptions);
            assertThat(redundantSut).isNotNull();

            final URI uri = sut.getServiceUri();
            assertThat(uri).hasPath("/");
            assertThat(uri.toString()).contains(api.name().toLowerCase());

            final URI redundantUri = redundantSut.getServiceUri();
            assertThat(redundantUri).hasPath("/");
            assertThat(redundantUri).isEqualTo(uri);
        }

        @Test
        void testThrowsWithoutOption()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(binding).build();

            final OAuth2PropertySupplier sut = BUSINESS_LOGGING.resolve(options);

            assertThatThrownBy(sut::getServiceUri)
                .isExactlyInstanceOf(DestinationAccessException.class)
                .hasMessageContaining("No option given");
        }
    }

    @Nested
    @DisplayName( "Identity Authentication" )
    class IdentityAuthenticationTest
    {
        private static final String PROVIDER_URL = "https://provider.ias.domain";
        private static final String PROVIDER_TENANT_ID = "test-provider-tenant";
        private static final String SUBSCRIBER_TENANT_ID = "test-subscriber-tenant";
        private static final ServiceBinding BINDING =
            bindingWithCredentials(
                ServiceIdentifier.IDENTITY_AUTHENTICATION,
                entry("app_tid", PROVIDER_TENANT_ID),
                entry("url", PROVIDER_URL),
                entry("credential-type", "X509_GENERATED"),
                entry("clientid", "ias-client-id"),
                entry("key", getKey()),
                entry("certificate", getCert()));

        @Test
        void testNoParameters()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(BINDING).build();

            final OAuth2PropertySupplier sut = IDENTITY_AUTHENTICATION.resolve(options);

            assertThat(sut).isNotNull();
            assertThat(sut.getTokenUri()).hasToString(PROVIDER_URL + "/oauth2/token");
            assertThat(sut.getServiceUri()).hasToString(PROVIDER_URL);

            final OAuth2Options oAuth2Options = sut.getOAuth2Options();
            assertThat(oAuth2Options.skipTokenRetrieval()).isFalse();
            assertThat(oAuth2Options.getClientKeyStore()).isNotNull();
            assertThatClientCertificateIsContained(oAuth2Options.getClientKeyStore());
            assertThat(oAuth2Options.getAdditionalTokenRetrievalParameters())
                .containsKey("app_tid")
                .containsValue(PROVIDER_TENANT_ID);
        }

        @Test
        void testTargetUri()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions
                    .forService(BINDING)
                    .withOption(IasOptions.withTargetUri("https://foo.bar.baz"))
                    .build();

            final OAuth2PropertySupplier sut = IDENTITY_AUTHENTICATION.resolve(options);

            assertThat(sut).isNotNull();
            assertThat(sut.getTokenUri()).hasToString(PROVIDER_URL + "/oauth2/token");
            assertThat(sut.getServiceUri()).hasToString("https://foo.bar.baz");

            final OAuth2Options oAuth2Options = sut.getOAuth2Options();
            assertThat(oAuth2Options.skipTokenRetrieval()).isFalse();
            assertThat(oAuth2Options.getClientKeyStore()).isNotNull();
            assertThatClientCertificateIsContained(oAuth2Options.getClientKeyStore());
            assertThat(oAuth2Options.getAdditionalTokenRetrievalParameters())
                .containsKey("app_tid")
                .containsValue(PROVIDER_TENANT_ID);
        }

        @Test
        void testApplicationLogicalName()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions
                    .forService(BINDING)
                    .withOption(IasOptions.withApplicationName("application-name"))
                    .build();

            final OAuth2PropertySupplier sut = IDENTITY_AUTHENTICATION.resolve(options);

            assertThat(sut).isNotNull();
            assertThat(sut.getTokenUri()).hasToString(PROVIDER_URL + "/oauth2/token");
            assertThat(sut.getServiceUri()).hasToString(PROVIDER_URL);

            final OAuth2Options oAuth2Options = sut.getOAuth2Options();
            assertThat(oAuth2Options.skipTokenRetrieval()).isFalse();
            assertThat(oAuth2Options.getClientKeyStore()).isNotNull();
            assertThatClientCertificateIsContained(oAuth2Options.getClientKeyStore());
            assertThat(oAuth2Options.getAdditionalTokenRetrievalParameters())
                .containsExactlyInAnyOrderEntriesOf(
                    Map
                        .of(
                            "resource",
                            "urn:sap:identity:application:provider:name:application-name",
                            "app_tid",
                            PROVIDER_TENANT_ID));
        }

        @Test
        void testClientIdWithoutTenantId()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions
                    .forService(BINDING)
                    .withOption(IasOptions.withConsumerClient("client-id"))
                    .build();

            final OAuth2PropertySupplier sut = IDENTITY_AUTHENTICATION.resolve(options);

            assertThat(sut).isNotNull();
            assertThat(sut.getTokenUri()).hasToString(PROVIDER_URL + "/oauth2/token");
            assertThat(sut.getServiceUri()).hasToString(PROVIDER_URL);

            final OAuth2Options oAuth2Options = sut.getOAuth2Options();
            assertThat(oAuth2Options.skipTokenRetrieval()).isFalse();
            assertThat(oAuth2Options.getClientKeyStore()).isNotNull();
            assertThatClientCertificateIsContained(oAuth2Options.getClientKeyStore());
            assertThat(oAuth2Options.getAdditionalTokenRetrievalParameters())
                .containsExactlyInAnyOrderEntriesOf(
                    Map.of("resource", "urn:sap:identity:consumer:clientid:client-id", "app_tid", PROVIDER_TENANT_ID));
        }

        @Test
        void testClientIdWithTenantId()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions
                    .forService(BINDING)
                    .withOption(IasOptions.withConsumerClient("client-id", "tenant-id"))
                    .build();

            final OAuth2PropertySupplier sut = IDENTITY_AUTHENTICATION.resolve(options);

            assertThat(sut).isNotNull();
            assertThat(sut.getTokenUri()).hasToString(PROVIDER_URL + "/oauth2/token");
            assertThat(sut.getServiceUri()).hasToString(PROVIDER_URL);

            final OAuth2Options oAuth2Options = sut.getOAuth2Options();
            assertThat(oAuth2Options.skipTokenRetrieval()).isFalse();
            assertThat(oAuth2Options.getClientKeyStore()).isNotNull();
            assertThatClientCertificateIsContained(oAuth2Options.getClientKeyStore());
            assertThat(oAuth2Options.getAdditionalTokenRetrievalParameters())
                .containsExactlyInAnyOrderEntriesOf(
                    Map
                        .of(
                            "resource",
                            "urn:sap:identity:consumer:clientid:client-id:apptid:tenant-id",
                            "app_tid",
                            PROVIDER_TENANT_ID));
        }

        @AllArgsConstructor
        private enum MutualTlsForTechnicalProviderAuthenticationTest
        {
            TECHNICAL_PROVIDER_NO_TENANT(OnBehalfOf.TECHNICAL_USER_PROVIDER, null, true),
            TECHNICAL_PROVIDER_SOME_TENANT(OnBehalfOf.TECHNICAL_USER_PROVIDER, SUBSCRIBER_TENANT_ID, true),
            TECHNICAL_PROVIDER(OnBehalfOf.TECHNICAL_USER_PROVIDER, PROVIDER_TENANT_ID, true),
            TECHNICAL_CURRENT_NO_TENANT(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT, null, true),
            TECHNICAL_CURRENT_SOME_TENANT(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT, SUBSCRIBER_TENANT_ID, false),
            TECHNICAL_CURRENT_PROVIDER_TENANT(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT, PROVIDER_TENANT_ID, true),
            NAMED_USER_NO_TENANT(OnBehalfOf.NAMED_USER_CURRENT_TENANT, null, false),
            NAMED_USER_SOME_TENANT(OnBehalfOf.NAMED_USER_CURRENT_TENANT, SUBSCRIBER_TENANT_ID, false),
            NAMED_USER_PROVIDER_TENANT(OnBehalfOf.NAMED_USER_CURRENT_TENANT, PROVIDER_TENANT_ID, false),;

            @Nonnull
            private final OnBehalfOf behalf;
            @Nullable
            private final String currentTenantId;
            private final boolean expectedSkipTokenRetrieval;
        }

        @ParameterizedTest
        @EnumSource( MutualTlsForTechnicalProviderAuthenticationTest.class )
        void testMutualTlsForTechnicalProviderAuthenticationOnly(
            @Nonnull final MutualTlsForTechnicalProviderAuthenticationTest test )
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions
                    .forService(BINDING)
                    .onBehalfOf(test.behalf)
                    .withOption(IasOptions.withoutTokenForTechnicalProviderUser())
                    .build();

            final OAuth2PropertySupplier sut = IDENTITY_AUTHENTICATION.resolve(options);

            assertThat(sut).isNotNull();
            assertThat(sut.getTokenUri()).hasToString(PROVIDER_URL + "/oauth2/token");
            assertThat(sut.getServiceUri()).hasToString(PROVIDER_URL);

            final OAuth2Options oAuth2Options;
            if( test.currentTenantId != null ) {
                oAuth2Options =
                    TenantAccessor.executeWithTenant(new DefaultTenant(test.currentTenantId), sut::getOAuth2Options);
                assertThat(oAuth2Options).isNotNull();
            } else {
                oAuth2Options = sut.getOAuth2Options();
            }
            assertThat(oAuth2Options.skipTokenRetrieval()).isEqualTo(test.expectedSkipTokenRetrieval);
            assertThat(oAuth2Options.getClientKeyStore()).isNotNull();
            assertThatClientCertificateIsContained(oAuth2Options.getClientKeyStore());

            if( oAuth2Options.skipTokenRetrieval() ) {
                assertThat(oAuth2Options.getAdditionalTokenRetrievalParameters()).isEmpty();
            } else {
                assertThat(oAuth2Options.getAdditionalTokenRetrievalParameters())
                    .containsOnly(entry("app_tid", PROVIDER_TENANT_ID));
            }
        }

        @Test
        void testMutualTlsCanBeCombinedWithTokenRetrievalOptions()
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions
                    .forService(BINDING)
                    .onBehalfOf(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT)
                    .withOption(IasOptions.withoutTokenForTechnicalProviderUser())
                    .withOption(IasOptions.withApplicationName("app-name"))
                    .build();

            final OAuth2PropertySupplier sut = IDENTITY_AUTHENTICATION.resolve(options);
            assertThat(sut).isNotNull();

            final OAuth2Options noTokenRetrievalOptions =
                TenantAccessor.executeWithTenant(() -> PROVIDER_TENANT_ID, sut::getOAuth2Options);
            final OAuth2Options tokenRetrievalOptions =
                TenantAccessor.executeWithTenant(() -> SUBSCRIBER_TENANT_ID, sut::getOAuth2Options);

            assertThat(noTokenRetrievalOptions).isNotNull();
            assertThat(tokenRetrievalOptions).isNotNull();

            assertThat(noTokenRetrievalOptions.skipTokenRetrieval()).isTrue();
            assertThat(noTokenRetrievalOptions.getAdditionalTokenRetrievalParameters()).isEmpty();

            assertThat(tokenRetrievalOptions.skipTokenRetrieval()).isFalse();
            assertThat(tokenRetrievalOptions.getAdditionalTokenRetrievalParameters()).isNotEmpty();
        }

        @Test
        @DisplayName( "Test the credential type X509_ATTESTED" )
        void testMutualTlsWithZeroTrustIdentityService()
        {
            final ServiceBinding binding =
                bindingWithCredentials(
                    ServiceIdentifier.IDENTITY_AUTHENTICATION,
                    entry("app_tid", PROVIDER_TENANT_ID),
                    entry("url", PROVIDER_URL),
                    entry("credential-type", "X509_ATTESTED"),
                    entry("clientid", "ias-client-id"));

            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(binding).build();

            final OAuth2PropertySupplier sut = IDENTITY_AUTHENTICATION.resolve(options);
            assertThat(sut).isNotNull();

            assertThatThrownBy(sut::getClientIdentity)
                .isInstanceOf(CloudPlatformException.class)
                .describedAs("We are not mocking the ZTIS service here so this should fail")
                .hasRootCauseInstanceOf(ServiceBindingAccessException.class);
        }

        @Test
        void testMutuallyExclusiveOptions()
        {
            final ServiceBindingDestinationOptions.OptionsEnhancer<?> applicationName =
                IasOptions.withApplicationName("application-name");
            final ServiceBindingDestinationOptions.OptionsEnhancer<?> clientId =
                IasOptions.withConsumerClient("client-id");
            final ServiceBindingDestinationOptions.OptionsEnhancer<?> clientIdAndTenantId =
                IasOptions.withConsumerClient("client-id", "tenant-id");

            final List<ServiceBindingDestinationOptions.OptionsEnhancer<?>> allOptions =
                List.of(applicationName, clientId, clientIdAndTenantId);

            for( final ServiceBindingDestinationOptions.OptionsEnhancer<?> firstOption : allOptions ) {
                for( final ServiceBindingDestinationOptions.OptionsEnhancer<?> secondOption : allOptions ) {
                    assertThat(firstOption.getClass()).isSameAs(secondOption.getClass());
                    final ServiceBindingDestinationOptions.Builder builder =
                        ServiceBindingDestinationOptions.forService(BINDING).withOption(firstOption);
                    assertThatThrownBy(() -> builder.withOption(secondOption))
                        .isExactlyInstanceOf(IllegalArgumentException.class);
                }
            }
        }

        @SneakyThrows
        private static void assertThatClientCertificateIsContained( @Nonnull final KeyStore keyStore )
        {
            final X509Certificate certificate = extractX509Certificate(keyStore);

            final String certificateAsString = certificate.toString();
            assertThat(certificateAsString).contains("C=DE");
            assertThat(certificateAsString).contains("ST=Brandenburg");
            assertThat(certificateAsString).contains("L=Potsdam");
            assertThat(certificateAsString).contains("CN=SAP Cloud SDK for Java");
        }

        @SneakyThrows
        private static X509Certificate extractX509Certificate( @Nonnull final KeyStore keyStore )
        {
            final Enumeration<String> aliases = keyStore.aliases();
            while( aliases.hasMoreElements() ) {
                final String alias = aliases.nextElement();
                final Certificate certificate = keyStore.getCertificate(alias);
                if( certificate instanceof X509Certificate ) {
                    return (X509Certificate) certificate;
                }
            }

            throw new IllegalStateException("No X509 certificate found in the keystore");
        }

        private static String getKey()
        {
            return getContentFromResource("src/test/resources/IdentityAuthenticationPropertySupplier/privatekey.pem");
        }

        private static String getCert()
        {
            return getContentFromResource("src/test/resources/IdentityAuthenticationPropertySupplier/certificate.crt");
        }

        @SneakyThrows
        private static String getContentFromResource( @Nonnull final String path )
        {
            return Files.readString(Path.of(path));
        }
    }
}
