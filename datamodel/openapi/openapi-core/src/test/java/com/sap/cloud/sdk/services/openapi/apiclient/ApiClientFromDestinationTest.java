package com.sap.cloud.sdk.services.openapi.apiclient;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.services.openapi.core.AbstractOpenApiService;

class ApiClientFromDestinationTest
{
    @RegisterExtension
    static final WireMockExtension SERVER =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    @Test
    void testServiceInvocation()
    {
        final HttpDestination testDestination = DefaultHttpDestination.builder(SERVER.baseUrl()).build();

        final MyTestAbstractOpenApiService springService = new MyTestAbstractOpenApiService(testDestination);
        final MyTestAbstractApacheOpenApiService apacheService =
            new MyTestAbstractApacheOpenApiService(testDestination);

        springService.foo();
        apacheService.foo();
    }

    @Test
    void testExceptionIsThrown()
    {
        final HttpDestination testDestination = DefaultHttpDestination.builder(SERVER.baseUrl()).build();

        final MyExceptionThrowingServiceAbstract springService =
            new MyExceptionThrowingServiceAbstract(testDestination);
        final MyExceptionThrowingApacheServiceAbstract apacheService =
            new MyExceptionThrowingApacheServiceAbstract(testDestination);

        assertThatExceptionOfType(IllegalAccessException.class).isThrownBy(springService::foo);
        assertThatExceptionOfType(IllegalAccessException.class).isThrownBy(apacheService::foo);
    }

    private class MyTestAbstractOpenApiService extends AbstractOpenApiService
    {
        public MyTestAbstractOpenApiService( final Destination destination )
        {
            super(destination);
        }

        void foo()
        {
            assertThat(apiClient.getBasePath()).isEqualTo(SERVER.baseUrl());
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

    private static class MyTestAbstractApacheOpenApiService extends com.sap.cloud.sdk.services.openapi.apache.BaseApi
    {
        public MyTestAbstractApacheOpenApiService( final Destination destination )
        {
            super(destination);
        }

        void foo()
        {
            assertThat(apiClient.getBasePath()).isEqualTo(SERVER.baseUrl());
        }
    }

    private static class MyExceptionThrowingApacheServiceAbstract
        extends
        com.sap.cloud.sdk.services.openapi.apache.BaseApi
    {
        public MyExceptionThrowingApacheServiceAbstract( final Destination destination )
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
