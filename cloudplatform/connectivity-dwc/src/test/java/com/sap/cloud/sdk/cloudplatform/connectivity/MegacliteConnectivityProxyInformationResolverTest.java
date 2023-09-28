package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.sap.cloud.sdk.cloudplatform.CloudPlatform;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Try;
import lombok.SneakyThrows;

public class MegacliteConnectivityProxyInformationResolverTest
{

    private static final HttpResponse successResponse;
    private static final HttpResponse failureResponse;

    static {
        successResponse = mock(HttpResponse.class);
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"))
            .when(successResponse)
            .getStatusLine();
        doReturn(new StringEntity("{ \"proxy\":\"http://some.proxy\", \"proxyAuth\":\"Bearer 1234\"}", Charsets.UTF_8))
            .when(successResponse)
            .getEntity();

        failureResponse = mock(HttpResponse.class);
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_INTERNAL_SERVER_ERROR, "NOT OK"))
            .when(failureResponse)
            .getStatusLine();
    }

    @BeforeClass
    public static void prepareCloudPlatform()
    {
        final CloudPlatform mock = mock(CloudPlatform.class);

        CloudPlatformAccessor.setCloudPlatformFacade(() -> Try.success(mock));
    }

    @AfterClass
    public static void resetCloudPlatform()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
    }

    private MegacliteConnectivityProxyInformationResolver sut;

    @Before
    public void setup()
    {
        final DwcConfiguration dwcConfig = new DwcConfiguration(URI.create("megaclite.com"), "provider-id");
        final MegacliteDestinationFactory destinationFactory = new MegacliteDestinationFactory(dwcConfig);
        sut = spy(new MegacliteConnectivityProxyInformationResolver(destinationFactory));

        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
    }

    @After
    public void reset()
    {
        TenantAccessor.setTenantFacade(null);
    }

    @SneakyThrows
    @Test
    public void testProxyUrlIsCachedAcrossTenants()
    {
        doReturn(successResponse).when(sut).makeHttpRequest(any());

        assertThat(sut.getProxyUrl()).isEqualTo(URI.create("http://some.proxy"));

        doReturn(failureResponse).when(sut).makeHttpRequest(any());

        final URI proxyUrl = TenantAccessor.executeWithTenant(new DefaultTenant("foo"), sut::getProxyUrl);
        assertThat(proxyUrl)
            .describedAs("The proxy URL should be the same across all tenants")
            .isEqualTo(sut.getProxyUrl());

        verify(sut, times(1)).getProxyInformationFromMegaclite();
    }

    @SneakyThrows
    @Test
    public void testAuthTokenIsCachedPerTenant()
    {
        doReturn(successResponse).when(sut).makeHttpRequest(any());

        final String token = sut.getAuthorizationToken();
        assertThat(token).isSameAs(sut.getAuthorizationToken()).isEqualTo("Bearer 1234");

        doReturn(failureResponse).when(sut).makeHttpRequest(any());

        TenantAccessor.executeWithTenant(new DefaultTenant("foo"), () -> {
            assertThatThrownBy(sut::getAuthorizationToken)
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(ResilienceRuntimeException.class)
                .hasRootCauseInstanceOf(IllegalStateException.class);
        });

        // sanity check that the original tenant still works and is cached
        assertThat(sut.getAuthorizationToken()).isSameAs(token);

        verify(sut, times(2)).getProxyInformationFromMegaclite();
    }

    @SneakyThrows
    @Test
    public void testProxyAuthHeader()
    {
        doReturn(successResponse).when(sut).makeHttpRequest(any());

        assertThat(sut.getHeaders(mock(DestinationRequestContext.class)))
            .contains(new Header(HttpHeaders.PROXY_AUTHORIZATION, "Bearer 1234"));
    }
}
