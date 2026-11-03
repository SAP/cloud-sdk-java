package com.sap.cloud.sdk.datamodel.odata.client;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.hc.client5.http.classic.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestAction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResult;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

@WireMockTest
class ODataApacheHttpClient5AccessorTest
{
    private static final String SERVICE_PATH = "/service/";
    private static final String ENTITY_SET = "Entities";
    private static final String ACTION_NAME = "TestAction";
    private static final String CSRF_TOKEN = "test-csrf-token";
    private static final String X_CSRF_TOKEN = "x-csrf-token";

    private HttpClient client;

    @BeforeEach
    void setup( final WireMockRuntimeInfo wm )
    {
        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        client = ODataApacheHttpClient5Accessor.getHttpClient(destination);
    }

    @Test
    void csrfTokenIsFetchedForMutatingODataRequests( final WireMockRuntimeInfo wm )
    {
        wm
            .getWireMock()
            .register(head(urlPathEqualTo(SERVICE_PATH)).willReturn(ok().withHeader(X_CSRF_TOKEN, CSRF_TOKEN)));
        wm.getWireMock().register(post(urlPathEqualTo(SERVICE_PATH + ACTION_NAME)).willReturn(noContent()));

        final ODataRequestAction request = new ODataRequestAction(SERVICE_PATH, ACTION_NAME, null, ODataProtocol.V4);
        final ODataRequestResult result = request.execute(client);

        assertThat(result).isNotNull();

        wm
            .getWireMock()
            .verifyThat(1, headRequestedFor(urlPathEqualTo(SERVICE_PATH)).withHeader(X_CSRF_TOKEN, equalTo("fetch")));
        wm
            .getWireMock()
            .verifyThat(
                1,
                postRequestedFor(urlPathEqualTo(SERVICE_PATH + ACTION_NAME))
                    .withHeader(X_CSRF_TOKEN, equalTo(CSRF_TOKEN)));
    }

    @Test
    void noHeadRequestForReadRequests( final WireMockRuntimeInfo wm )
    {
        wm.getWireMock().register(get(urlPathEqualTo(SERVICE_PATH + ENTITY_SET)).willReturn(okJson("{\"value\":[]}")));

        final ODataRequestRead request = new ODataRequestRead(SERVICE_PATH, ENTITY_SET, "", ODataProtocol.V4);
        final ODataRequestResultGeneric result = request.execute(client);

        assertThat(result).isNotNull();

        wm.getWireMock().verifyThat(0, headRequestedFor(anyUrl()));
        wm.getWireMock().verifyThat(1, getRequestedFor(urlPathEqualTo(SERVICE_PATH + ENTITY_SET)));
    }
}
