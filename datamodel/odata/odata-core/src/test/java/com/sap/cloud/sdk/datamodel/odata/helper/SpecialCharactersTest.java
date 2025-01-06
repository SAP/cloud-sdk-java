/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.allRequests;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@WireMockTest
class SpecialCharactersTest
{
    private static final String SERVICE_URL = "/service/path";

    private DefaultHttpDestination destination;

    @BeforeEach
    void before( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Data
    @EqualsAndHashCode( callSuper = true )
    public static class Ticket1 extends VdmEntity<Ticket1>
    {
        private final String entityCollection = "Tickets";
        private final Class<Ticket1> type = Ticket1.class;
        private String keyVal1 = "Val1";
        @Getter( lazy = true )
        private final Map<String, Object> key = ImmutableMap.of("Key1", keyVal1);
    }

    @Data
    @EqualsAndHashCode( callSuper = true )
    public static class Ticket2 extends VdmEntity<Ticket2>
    {
        private final String entityCollection = "Tickets";
        private final Class<Ticket2> type = Ticket2.class;
        private String keyVal1 = "Val1";
        private String keyVal2 = "Val2";
        @Getter( lazy = true )
        private final Map<String, Object> key = ImmutableMap.of("Key1", keyVal1, "Key2", keyVal2);
    }

    @Test
    void testGetByKey()
    {
        final String keyVal1 = "F?oo";
        final String keyVal2 = "A/ #";
        final String ticketsUrl = SERVICE_URL + "/Tickets(Key1='F%3Foo',Key2='A%2F%20%23')";
        stubFor(get(urlEqualTo(ticketsUrl)).willReturn(okJson("{\"d\":{\"Foo\":\"0\"}}")));

        final Map<String, Object> key = ImmutableMap.of("Key1", keyVal1, "Key2", keyVal2);
        final Ticket2 item =
            FluentHelperFactory
                .withServicePath(SERVICE_URL)
                .readByKey(Ticket2.class, "Tickets", key)
                .executeRequest(destination);

        assertThat(item).isNotNull();
        verify(1, allRequests());
        verify(1, getRequestedFor(urlEqualTo(ticketsUrl)));
    }

    @Test
    void testGetNavigationPropertyByKey()
    {
        final String ticketsUrl = SERVICE_URL + "/Tickets(Key1='s4L3s%2F%200rd3%3Fr',Key2='1t3%23m')/to_NextTicket";

        stubFor(get(urlEqualTo(ticketsUrl)).willReturn(okJson("{\"d\":{\"\":\"\"}}")));

        final Ticket2 item = new Ticket2();
        item.setKeyVal1("s4L3s/ 0rd3?r");
        item.setKeyVal2("1t3#m");

        item.setDestinationForFetch(destination);
        item.setServicePathForFetch(SERVICE_URL);
        final Ticket2 nextTicket = item.fetchFieldAsSingle("to_NextTicket", Ticket2.class);

        assertThat(nextTicket).isNotNull();
        assertThat(nextTicket.getDestinationForFetch()).isEqualTo(destination);
        verify(1, allRequests());
        verify(1, getRequestedFor(urlEqualTo(ticketsUrl)));
    }

    @Test
    void testFilterExpression()
    {
        final String field1Val = "&?:/";
        final String field2Val = "#/\\";

        final String ticketsUrl =
            SERVICE_URL + "/Tickets?$filter=(Field1%20eq%20'%26%3F:/')%20and%20(Field2%20eq%20'%23/%5C')";

        stubFor(get(urlEqualTo(ticketsUrl)).willReturn(okJson("{\"d\":{\"results\":[{\"Foo\":\"0\"}]}}")));

        final EntityField<Ticket2, String> field1 = new EntityField<>("Field1");
        final EntityField<Ticket2, String> field2 = new EntityField<>("Field2");
        final Ticket2 item =
            FluentHelperFactory
                .withServicePath(SERVICE_URL)
                .read(Ticket2.class, "Tickets")
                .filter(field1.eq(field1Val).and(field2.eq(field2Val)))
                .executeRequest(destination)
                .get(0);

        assertThat(item).isNotNull();
        verify(1, allRequests());
        verify(1, getRequestedFor(urlEqualTo(ticketsUrl)));
    }

    @Test
    void testEscapedNestedSingleQuote()
    {
        // test escaping for get by filter
        {
            final String getByFilter = "/Tickets?$filter=Field1%20eq%20'foo''bar'";
            stubFor(
                get(urlEqualTo(SERVICE_URL + getByFilter))
                    .willReturn(okJson("{\"d\":{\"results\":[{\"Foo\":\"foo'bar\"}]}}")));

            final EntityField<Ticket1, String> field1 = new EntityField<>("Field1");
            final Ticket1 item =
                FluentHelperFactory
                    .withServicePath(SERVICE_URL)
                    .read(Ticket1.class, "Tickets")
                    .filter(field1.eq("foo'bar"))
                    .executeRequest(destination)
                    .get(0);
            assertThat(item).isNotNull();
            verify(1, getRequestedFor(urlEqualTo(SERVICE_URL + getByFilter)));
        }

        // test escaping for get by key
        {
            final String getByKey = "/Tickets('foo''bar')";
            stubFor(get(urlEqualTo(SERVICE_URL + getByKey)).willReturn(okJson("{\"d\":{\"Foo\":\"foo'bar\"}}")));

            final Map<String, Object> key = ImmutableMap.of("Field1", "foo'bar");
            final Ticket1 item =
                FluentHelperFactory
                    .withServicePath(SERVICE_URL)
                    .readByKey(Ticket1.class, "Tickets", key)
                    .executeRequest(destination);
            assertThat(item).isNotNull();
            verify(1, getRequestedFor(urlEqualTo(SERVICE_URL + getByKey)));
        }

        // test escaping for lazily fetching a navigation property
        {
            final String getByFilter = "/Tickets('foo''bar')/to_NextTicket";
            stubFor(
                get(urlEqualTo(SERVICE_URL + getByFilter))
                    .willReturn(okJson("{\"d\":{\"results\":[{\"Foo\":\"0\"}]}}")));
            final Ticket1 item = new Ticket1();
            item.setKeyVal1("foo'bar");

            item.setDestinationForFetch(destination);
            item.setServicePathForFetch(SERVICE_URL);
            final Ticket1 nextTicket = item.fetchFieldAsSingle("to_NextTicket", Ticket1.class);

            assertThat(nextTicket).isNotNull();
            assertThat(nextTicket.getDestinationForFetch()).isEqualTo(destination);
            verify(1, getRequestedFor(urlEqualTo(SERVICE_URL + getByFilter)));
        }

    }

    @Test
    void testEscapedOnlySingleQuote()
    {
        final String oderKey = "'";
        final String itemKey = "'";
        stubFor(head(urlEqualTo(SERVICE_URL)).willReturn(ok()));

        // test escaping for get by filter
        {
            final String getByFilter = "/Tickets?$filter=(Field1%20eq%20'''')%20and%20(Field2%20eq%20'''')";
            stubFor(
                get(urlEqualTo(SERVICE_URL + getByFilter))
                    .willReturn(okJson("{\"d\":{\"results\":[{\"Foo\":\"0\"}]}}")));

            final EntityField<Ticket2, String> field1 = new EntityField<>("Field1");
            final EntityField<Ticket2, String> field2 = new EntityField<>("Field2");
            final Ticket2 item =
                FluentHelperFactory
                    .withServicePath(SERVICE_URL)
                    .read(Ticket2.class, "Tickets")
                    .filter(field1.eq(oderKey).and(field2.eq(itemKey)))
                    .executeRequest(destination)
                    .get(0);

            assertThat(item).isNotNull();
            verify(1, getRequestedFor(urlEqualTo(SERVICE_URL + getByFilter)));
        }

        // test escaping for lazily fetching a navigation property
        {
            final String getByFilter = "/Tickets(Key1='''',Key2='''')/to_NextTicket";
            stubFor(
                get(urlEqualTo(SERVICE_URL + getByFilter))
                    .willReturn(okJson("{\"d\":{\"results\":[{\"Foo\":\"0\"}]}}")));
            final Ticket2 item = new Ticket2();
            item.setKeyVal1(oderKey);
            item.setKeyVal2(itemKey);

            item.setDestinationForFetch(destination);
            item.setServicePathForFetch(SERVICE_URL);
            final Ticket2 nextTicket = item.fetchFieldAsSingle("to_NextTicket", Ticket2.class);

            assertThat(nextTicket).isNotNull();
            assertThat(nextTicket.getDestinationForFetch()).isEqualTo(destination);
            verify(1, getRequestedFor(urlEqualTo(SERVICE_URL + getByFilter)));
        }

        // test escaping for get by key
        {
            final String getByKey = "/Tickets(Key1='''',Key2='''')";
            stubFor(get(urlEqualTo(SERVICE_URL + getByKey)).willReturn(okJson("{\"d\":{\"Foo\":\"0\"}}")));

            final Map<String, Object> key = ImmutableMap.of("Key1", oderKey, "Key2", itemKey);
            final Ticket2 item =
                FluentHelperFactory
                    .withServicePath(SERVICE_URL)
                    .readByKey(Ticket2.class, "Tickets", key)
                    .executeRequest(destination);
            assertThat(item).isNotNull();
            verify(getRequestedFor(urlEqualTo(SERVICE_URL + getByKey)));
        }
    }
}
