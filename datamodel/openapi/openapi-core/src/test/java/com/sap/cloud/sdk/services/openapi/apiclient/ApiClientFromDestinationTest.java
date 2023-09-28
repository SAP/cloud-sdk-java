package com.sap.cloud.sdk.services.openapi.apiclient;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;

public class ApiClientFromDestinationTest
{
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Test
    public void testServiceInvocation()
    {
        final HttpDestination testDestination = DefaultHttpDestination.builder(wireMockRule.baseUrl()).build();

        final MyTestAbstractOpenApiService service = new MyTestAbstractOpenApiService(testDestination);

        service.foo();
    }

    @Test
    public void testExceptionIsThrown()
    {
        final HttpDestination testDestination = DefaultHttpDestination.builder(wireMockRule.baseUrl()).build();

        final MyExceptionThrowingServiceAbstract service = new MyExceptionThrowingServiceAbstract(testDestination);

        assertThatExceptionOfType(IllegalAccessException.class).isThrownBy(service::foo);
    }

    private class MyTestAbstractOpenApiService extends AbstractOpenApiService
    {
        public MyTestAbstractOpenApiService( final Destination destination )
        {
            super(destination);
        }

        void foo()
        {
            assertThat(apiClient.getBasePath()).isEqualTo(wireMockRule.baseUrl());
        }
    }

    private static class MyExceptionThrowingServiceAbstract extends AbstractOpenApiService
    {
        public MyExceptionThrowingServiceAbstract( final Destination destination )
        {
            super(destination);
        }

        void foo()
            throws IllegalAccessException
        {
            throw new IllegalAccessException("Something went horribly wrong");
        }
    }
}
