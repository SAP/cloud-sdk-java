package com.sap.cloud.sdk.datamodel.odata.client;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataFunctionParameters;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestFunction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.result.ElementName;

import io.vavr.control.Try;
import lombok.Data;
import lombok.SneakyThrows;

public class ODataResponseParsingTest
{
    private static final String servicePathV2 = "/v2/";
    private static final String servicePathV4 = "/v4/";

    private static final String entityCollection = "ticket";
    private static final String functionEndpoint = "func";
    private static final ODataEntityKey entityKey =
        new ODataEntityKey(ODataProtocol.V4).addKeyProperty("Ticket", "123");

    private static final String byKeyResponseV2 = readResourceFile("Ticket_Single_V2.json");
    private static final String byKeyResponseV4 = readResourceFile("Ticket_Single_V4.json");
    private static final String getAllResponseV2 = readResourceFile("Ticket_Collection_V2.json");
    private static final String getAllResponseV4 = readResourceFile("Ticket_Collection_V4.json");
    private static final String functionEntityResponseV2 = readResourceFile("Ticket_Function_V2.json");
    private static final String primitiveResponseV2 = "{\"d\" : {\"" + functionEndpoint + "\" : \"someString\"}}";
    private static final String primitiveResponseV4 = "{\"value\" : \"someString\"}";

    @SneakyThrows
    private static String readResourceFile( final String s )
    {
        final ClassLoader cl = ODataResponseParsingTest.class.getClassLoader();
        return new String(Files.readAllBytes(Paths.get(cl.getResource("ODataResponseParsingTest/" + s).toURI())));
    }

    @Rule
    public WireMockRule rule = new WireMockRule(WireMockConfiguration.options().dynamicPort());

    private HttpClient client;

    @Data
    public static class Ticket
    {
        @ElementName( "versionIdentifier" )
        private String versionIdentifier;
        @ElementName( "Ticket" )
        private String ticket;
        @ElementName( "TicketName" )
        private String ticketName;
        @ElementName( "TicketIsBlocked" )
        private boolean ticketIsBlocked;
    }

    @Before
    public void setupHttpClient()
    {
        final Destination destination = DefaultHttpDestination.builder(rule.baseUrl()).build();
        client = HttpClientAccessor.getHttpClient(destination);
    }

    @Test
    public void testByKeyV2()
    {
        stubFor(get(urlPathEqualTo(servicePathV2 + entityCollection + entityKey)).willReturn(okJson(byKeyResponseV2)));

        final ODataRequestReadByKey request =
            new ODataRequestReadByKey(servicePathV2, entityCollection, entityKey, "", ODataProtocol.V2);
        final ODataRequestResultGeneric resultGeneric = request.execute(client);

        final Try<Ticket> maybeTicket = Try.of(() -> resultGeneric.as(Ticket.class));
        assertThat(maybeTicket).isNotEmpty();

        final Ticket ticket = maybeTicket.get();
        assertThat(ticket.getTicket()).isEqualTo("1000001");
        assertThat(ticket.isTicketIsBlocked()).isFalse();

        // The client does not handle etags yet
        // assertThat(ticket.getVersionIdentifier()).isEqualTo("DUEMONT20180116144654");
    }

    @Test
    public void testByKeyV4()
    {
        stubFor(get(urlPathEqualTo(servicePathV4 + entityCollection + entityKey)).willReturn(okJson(byKeyResponseV4)));

        final ODataRequestReadByKey request =
            new ODataRequestReadByKey(servicePathV4, entityCollection, entityKey, "", ODataProtocol.V4);
        final ODataRequestResultGeneric resultGeneric = request.execute(client);

        final Try<Ticket> maybeTicket = Try.of(() -> resultGeneric.as(Ticket.class));
        assertThat(maybeTicket).isNotEmpty();

        final Ticket ticket = maybeTicket.get();
        assertThat(ticket.getTicket()).isEqualTo("1000001");
        assertThat(ticket.isTicketIsBlocked()).isFalse();

        // The client does not handle etags yet
        // assertThat(ticket.getVersionIdentifier()).isEqualTo("DUEMONT20180116144654");
    }

    @Test
    public void testGetAllV2()
    {
        stubFor(get(urlPathEqualTo(servicePathV2 + entityCollection)).willReturn(okJson(getAllResponseV2)));

        final ODataRequestRead request = new ODataRequestRead(servicePathV2, entityCollection, "", ODataProtocol.V2);
        final ODataRequestResultGeneric resultGeneric = request.execute(client);

        final Try<List<Ticket>> maybeTicket = Try.of(() -> resultGeneric.asList(Ticket.class));
        assertThat(maybeTicket).isNotEmpty();

        final List<Ticket> tickets = maybeTicket.get();
        assertThat(tickets).isNotEmpty().hasSize(2);
    }

    @Test
    public void testGetAllV4()
    {
        stubFor(get(urlPathEqualTo(servicePathV4 + entityCollection)).willReturn(okJson(getAllResponseV4)));

        final ODataRequestRead request = new ODataRequestRead(servicePathV4, entityCollection, "", ODataProtocol.V4);
        final ODataRequestResultGeneric resultGeneric = request.execute(client);

        final Try<List<Ticket>> maybeTicket = Try.of(() -> resultGeneric.asList(Ticket.class));
        assertThat(maybeTicket).isNotEmpty();

        final List<Ticket> tickets = maybeTicket.get();
        assertThat(tickets).isNotEmpty().hasSize(2);
    }

    @Test
    public void testPrimitiveV2()
    {
        stubFor(get(urlPathEqualTo(servicePathV2 + functionEndpoint)).willReturn(okJson(primitiveResponseV2)));

        final ODataRequestFunction function =
            new ODataRequestFunction(
                servicePathV2,
                functionEndpoint,
                ODataFunctionParameters.empty(ODataProtocol.V2),
                ODataProtocol.V2);
        final ODataRequestResultGeneric genericResult = function.execute(client);

        final Try<String> maybeString = Try.of(() -> genericResult.as(String.class));
        assertThat(maybeString).isNotEmpty();

        final String value = maybeString.get();
        assertThat(value).isEqualTo("someString");
    }

    @Test
    public void testFunctionEntityResponseV2()
    {
        stubFor(get(urlPathEqualTo(servicePathV2 + functionEndpoint)).willReturn(okJson(functionEntityResponseV2)));

        final ODataRequestFunction function =
            new ODataRequestFunction(
                servicePathV2,
                functionEndpoint,
                ODataFunctionParameters.empty(ODataProtocol.V2),
                ODataProtocol.V2);
        final ODataRequestResultGeneric genericResult = function.execute(client);

        final Try<Ticket> maybePartner =
            Try.of(() -> genericResult.as(Ticket.class, e -> e.getAsJsonObject().get("Partner")));
        assertThat(maybePartner).isNotEmpty();

        assertThat(maybePartner.get().getTicketName()).isEqualTo("TESTSUPPLIER01");
    }

    @Test
    public void testPrimitiveV4()
    {
        stubFor(get(urlPathEqualTo(servicePathV4 + functionEndpoint + "()")).willReturn(okJson(primitiveResponseV4)));

        final ODataRequestFunction function =
            new ODataRequestFunction(
                servicePathV4,
                functionEndpoint,
                ODataFunctionParameters.empty(ODataProtocol.V4),
                ODataProtocol.V4);
        final ODataRequestResultGeneric genericResult = function.execute(client);

        final Try<String> maybeString = Try.of(() -> genericResult.as(String.class));
        assertThat(maybeString).isNotEmpty();

        final String value = maybeString.get();
        assertThat(value).isEqualTo("someString");
    }
}
