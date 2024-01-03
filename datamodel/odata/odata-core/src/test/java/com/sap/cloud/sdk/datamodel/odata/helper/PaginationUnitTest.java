/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@WireMockTest
class PaginationUnitTest
{
    private static final int PAGE_SIZE = 20;
    private static final int ENTITIES_COUNT = 91;
    private static final int PAGES_COUNT = 5;

    private DefaultHttpDestination destination;

    // corresponds to https://services.odata.org/V4/Northwind/Northwind.svc/Customers?$select=CustomerID
    final String page1 =
        "{ \"d\" : { \"results\": [ { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('ALFKI')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"ALFKI\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('ANATR')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"ANATR\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('ANTON')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"ANTON\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('AROUT')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"AROUT\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('BERGS')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"BERGS\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('BLAUS')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"BLAUS\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('BLONP')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"BLONP\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('BOLID')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"BOLID\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('BONAP')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"BONAP\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('BOTTM')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"BOTTM\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('BSBEV')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"BSBEV\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('CACTU')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"CACTU\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('CENTC')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"CENTC\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('CHOPS')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"CHOPS\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('COMMI')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"COMMI\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('CONSH')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"CONSH\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('DRACD')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"DRACD\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('DUMON')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"DUMON\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('EASTC')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"EASTC\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('ERNSH')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"ERNSH\" } ], \"__next\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers?$select=CustomerID&$skiptoken='ERNSH'\" } }";
    final String page2 =
        "{ \"d\" : { \"results\": [ { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('FAMIA')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"FAMIA\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('FISSA')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"FISSA\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('FOLIG')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"FOLIG\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('FOLKO')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"FOLKO\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('FRANK')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"FRANK\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('FRANR')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"FRANR\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('FRANS')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"FRANS\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('FURIB')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"FURIB\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('GALED')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"GALED\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('GODOS')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"GODOS\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('GOURL')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"GOURL\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('GREAL')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"GREAL\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('GROSR')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"GROSR\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('HANAR')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"HANAR\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('HILAA')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"HILAA\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('HUNGC')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"HUNGC\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('HUNGO')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"HUNGO\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('ISLAT')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"ISLAT\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('KOENE')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"KOENE\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('LACOR')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"LACOR\" } ], \"__next\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers?$select=CustomerID&$skiptoken='LACOR'\" } }";
    final String page3 =
        "{ \"d\" : { \"results\": [ { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('LAMAI')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"LAMAI\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('LAUGB')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"LAUGB\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('LAZYK')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"LAZYK\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('LEHMS')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"LEHMS\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('LETSS')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"LETSS\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('LILAS')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"LILAS\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('LINOD')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"LINOD\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('LONEP')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"LONEP\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('MAGAA')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"MAGAA\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('MAISD')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"MAISD\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('MEREP')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"MEREP\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('MORGK')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"MORGK\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('NORTS')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"NORTS\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('OCEAN')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"OCEAN\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('OLDWO')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"OLDWO\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('OTTIK')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"OTTIK\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('PARIS')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"PARIS\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('PERIC')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"PERIC\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('PICCO')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"PICCO\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('PRINI')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"PRINI\" } ], \"__next\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers?$select=CustomerID&$skiptoken='PRINI'\" } }";
    final String page4 =
        "{ \"d\" : { \"results\": [ { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('QUEDE')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"QUEDE\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('QUEEN')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"QUEEN\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('QUICK')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"QUICK\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('RANCH')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"RANCH\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('RATTC')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"RATTC\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('REGGC')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"REGGC\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('RICAR')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"RICAR\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('RICSU')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"RICSU\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('ROMEY')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"ROMEY\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('SANTG')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"SANTG\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('SAVEA')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"SAVEA\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('SEVES')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"SEVES\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('SIMOB')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"SIMOB\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('SPECD')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"SPECD\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('SPLIR')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"SPLIR\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('SUPRD')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"SUPRD\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('THEBI')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"THEBI\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('THECR')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"THECR\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('TOMSP')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"TOMSP\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('TORTU')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"TORTU\" } ], \"__next\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers?$select=CustomerID&$skiptoken='TORTU'\" } }";
    final String page5 =
        "{ \"d\" : { \"results\": [ { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('TRADH')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"TRADH\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('TRAIH')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"TRAIH\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('VAFFE')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"VAFFE\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('VICTE')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"VICTE\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('VINET')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"VINET\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('WANDK')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"WANDK\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('WARTH')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"WARTH\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('WELLI')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"WELLI\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('WHITC')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"WHITC\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('WILMK')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"WILMK\" }, { \"__metadata\": { \"uri\": \"https://services.odata.org/V2/Northwind/Northwind.svc/Customers('WOLZA')\", \"type\": \"NorthwindModel.Customer\" }, \"CustomerID\": \"WOLZA\" } ] } }";

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        stubFor(
            get(UrlPattern.ANY)
                .withHeader("Prefer", equalTo("odata.maxpagesize=20"))
                .withQueryParam("$skiptoken", absent())
                .willReturn(okJson(page1)));
        stubFor(
            get(UrlPattern.ANY)
                .withHeader("Prefer", equalTo("odata.maxpagesize=20"))
                .withQueryParam("$skiptoken", equalTo("'ERNSH'"))
                .willReturn(okJson(page2)));
        stubFor(
            get(UrlPattern.ANY)
                .withHeader("Prefer", equalTo("odata.maxpagesize=20"))
                .withQueryParam("$skiptoken", equalTo("'LACOR'"))
                .willReturn(okJson(page3)));
        stubFor(
            get(UrlPattern.ANY)
                .withHeader("Prefer", equalTo("odata.maxpagesize=20"))
                .withQueryParam("$skiptoken", equalTo("'PRINI'"))
                .willReturn(okJson(page4)));
        stubFor(
            get(UrlPattern.ANY)
                .withHeader("Prefer", equalTo("odata.maxpagesize=20"))
                .withQueryParam("$skiptoken", equalTo("'TORTU'"))
                .willReturn(okJson(page5)));

        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Test
    void testGetAll()
    {
        final List<Customer> result =
            newCustomerRead().select(Customer.CUSTOMER_ID).withPreferredPageSize(PAGE_SIZE).executeRequest(destination);

        verify(PAGES_COUNT, getRequestedFor(UrlPattern.ANY));

        Assertions
            .assertThat(result)
            .hasSize(ENTITIES_COUNT)
            .extracting(Customer::getCustomerId)
            .allMatch(Objects::nonNull);
    }

    @Test
    void testGetAllIteratingEntities()
    {
        final Iterable<Customer> result =
            newCustomerRead()
                .select(Customer.CUSTOMER_ID)
                .withPreferredPageSize(PAGE_SIZE)
                .iteratingEntities()
                .executeRequest(destination);

        verify(1, getRequestedFor(UrlPattern.ANY));

        int countPages = 0;
        int countEntities = 0;
        for( final Customer entity : result ) {
            if( countEntities++ % PAGE_SIZE == 0 ) {
                verify(++countPages, getRequestedFor(UrlPattern.ANY));
            }
            assertThat(entity).extracting(Customer::getCustomerId).isNotNull();
        }

        assertThat(countEntities).isEqualTo(ENTITIES_COUNT);
        verify(PAGES_COUNT, getRequestedFor(UrlPattern.ANY));
    }

    @Test
    void testGetAllStreamingEntities()
    {
        final Stream<Customer> result =
            newCustomerRead()
                .select(Customer.CUSTOMER_ID)
                .withPreferredPageSize(PAGE_SIZE)
                .streamingEntities()
                .executeRequest(destination);

        verify(1, getRequestedFor(UrlPattern.ANY));

        final Stream<String> intermediateStream = result.map(Customer::getCustomerId).peek(Objects::requireNonNull);
        verify(1, getRequestedFor(UrlPattern.ANY));

        final long countEntities = intermediateStream.count();
        assertThat(countEntities).isEqualTo(ENTITIES_COUNT);
        verify(PAGES_COUNT, getRequestedFor(UrlPattern.ANY));

        // repeated access to stream is not permitted as of Java API
        assertThatIllegalStateException()
            .isThrownBy(result::count)
            .withMessage("stream has already been operated upon or closed");
    }

    @Test
    void testGetAllIteratingPages()
    {
        final Iterable<List<Customer>> result =
            newCustomerRead()
                .select(Customer.CUSTOMER_ID)
                .withPreferredPageSize(PAGE_SIZE)
                .iteratingPages()
                .executeRequest(destination);

        verify(1, getRequestedFor(UrlPattern.ANY));

        int countPages = 0;
        int countEntities = 0;
        for( final List<Customer> entities : result ) {
            verify(++countPages, getRequestedFor(UrlPattern.ANY));
            countEntities += entities.size();
            Assertions.assertThat(entities).extracting(Customer::getCustomerId).allMatch(Objects::nonNull);
        }
        assertThat(countEntities).isEqualTo(ENTITIES_COUNT);
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString( doNotUseGetters = true, callSuper = true )
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
    public static class Customer extends VdmEntity<com.sap.cloud.sdk.datamodel.odata.helper.TestVdmEntity>
    {
        private static final String SERVICE_PATH = "/V2/Northwind/Northwind.svc";

        @Getter
        final String entityCollection = "Customers";

        @Getter
        private final String defaultServicePath = SERVICE_PATH;

        @Getter
        private final Class<com.sap.cloud.sdk.datamodel.odata.helper.TestVdmEntity> type =
            com.sap.cloud.sdk.datamodel.odata.helper.TestVdmEntity.class;

        @Key
        @SerializedName( "CustomerID" )
        @JsonProperty( "CustomerID" )
        @Nullable
        @ODataField( odataName = "CustomerID" )
        private String customerId;

        public final static CustomerField<String> CUSTOMER_ID = new CustomerField<String>("CustomerID");
    }

    public static class CustomerField<FieldT> extends EntityField<Customer, FieldT> implements CustomerSelectable
    {
        public CustomerField( final String fieldName )
        {
            super(fieldName);
        }
    }

    public interface CustomerSelectable extends EntitySelectable<Customer>
    {
    }

    static <
        T extends FluentHelperRead<T, Customer, CustomerSelectable>>
        FluentHelperRead<T, Customer, CustomerSelectable>
        newCustomerRead()
    {
        return FluentHelperFactory.withServicePath(Customer.SERVICE_PATH).read(Customer.class, "Customers");
    }
}
