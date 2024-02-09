/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServicePropertySuppliers.BUSINESS_LOGGING;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServicePropertySuppliers.BUSINESS_RULES;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServicePropertySuppliers.CONNECTIVITY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServicePropertySuppliers.WORKFLOW;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.bindingWithCredentials;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.BusinessLoggingOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.BusinessRulesOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.WorkflowOptions;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import io.vavr.control.Try;

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
    @DisplayName( "Business Logging" )
    class BusinessLoggingTest
    {
        private final ServiceBinding binding =
            bindingWithCredentials(
                ServiceIdentifier.of("business-logging"),
                entry("endpoints.configservice", "https://business-logging.config_api.example.com"),
                entry("endpoints.readservice", "https://business-logging.read_api.example.com"),
                entry("endpoints.textresourceservice", "https://business-logging.text_api.example.com"),
                entry("endpoints.writeservice", "https://business-logging.write_api.example.com/buslogs/log"));

        @ParameterizedTest
        @EnumSource( BusinessLoggingOptions.class )
        void testApiSelection( final BusinessLoggingOptions api )
        {
            final ServiceBindingDestinationOptions options =
                ServiceBindingDestinationOptions.forService(binding).withOption(api).build();

            final OAuth2PropertySupplier sut = BUSINESS_LOGGING.resolve(options);

            assertThat(sut.getServiceUri().toString()).contains(api.name().toLowerCase());
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
}
