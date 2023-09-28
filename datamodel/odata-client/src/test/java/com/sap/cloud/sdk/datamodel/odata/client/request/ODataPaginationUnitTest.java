package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;

import lombok.SneakyThrows;

public class ODataPaginationUnitTest
{
    // corresponds to https://services.odata.org/V4/Northwind/Northwind.svc/Customers?$select=CustomerID&$count=true
    private static final String page1 =
        "{\"@odata.context\":\"https://services.odata.org/V4/Northwind/Northwind.svc/$metadata#Customers(CustomerID)\",\"@odata.count\":91,\"value\":[{\"CustomerID\":\"ALFKI\"},{\"CustomerID\":\"ANATR\"},{\"CustomerID\":\"ANTON\"},{\"CustomerID\":\"AROUT\"},{\"CustomerID\":\"BERGS\"},{\"CustomerID\":\"BLAUS\"},{\"CustomerID\":\"BLONP\"},{\"CustomerID\":\"BOLID\"},{\"CustomerID\":\"BONAP\"},{\"CustomerID\":\"BOTTM\"},{\"CustomerID\":\"BSBEV\"},{\"CustomerID\":\"CACTU\"},{\"CustomerID\":\"CENTC\"},{\"CustomerID\":\"CHOPS\"},{\"CustomerID\":\"COMMI\"},{\"CustomerID\":\"CONSH\"},{\"CustomerID\":\"DRACD\"},{\"CustomerID\":\"DUMON\"},{\"CustomerID\":\"EASTC\"},{\"CustomerID\":\"ERNSH\"}],\"@odata.nextLink\":\"Customers?$count=true&$select=CustomerID&$skiptoken='ERNSH'\"}";
    private static final String page2 =
        "{\"@odata.context\":\"https://services.odata.org/V4/Northwind/Northwind.svc/$metadata#Customers(CustomerID)\",\"@odata.count\":91,\"value\":[{\"CustomerID\":\"FAMIA\"},{\"CustomerID\":\"FISSA\"},{\"CustomerID\":\"FOLIG\"},{\"CustomerID\":\"FOLKO\"},{\"CustomerID\":\"FRANK\"},{\"CustomerID\":\"FRANR\"},{\"CustomerID\":\"FRANS\"},{\"CustomerID\":\"FURIB\"},{\"CustomerID\":\"GALED\"},{\"CustomerID\":\"GODOS\"},{\"CustomerID\":\"GOURL\"},{\"CustomerID\":\"GREAL\"},{\"CustomerID\":\"GROSR\"},{\"CustomerID\":\"HANAR\"},{\"CustomerID\":\"HILAA\"},{\"CustomerID\":\"HUNGC\"},{\"CustomerID\":\"HUNGO\"},{\"CustomerID\":\"ISLAT\"},{\"CustomerID\":\"KOENE\"},{\"CustomerID\":\"LACOR\"}],\"@odata.nextLink\":\"Customers?$count=true&$select=CustomerID&$skiptoken='LACOR'\"}";
    private static final String page3 =
        "{\"@odata.context\":\"https://services.odata.org/V4/Northwind/Northwind.svc/$metadata#Customers(CustomerID)\",\"@odata.count\":91,\"value\":[{\"CustomerID\":\"LAMAI\"},{\"CustomerID\":\"LAUGB\"},{\"CustomerID\":\"LAZYK\"},{\"CustomerID\":\"LEHMS\"},{\"CustomerID\":\"LETSS\"},{\"CustomerID\":\"LILAS\"},{\"CustomerID\":\"LINOD\"},{\"CustomerID\":\"LONEP\"},{\"CustomerID\":\"MAGAA\"},{\"CustomerID\":\"MAISD\"},{\"CustomerID\":\"MEREP\"},{\"CustomerID\":\"MORGK\"},{\"CustomerID\":\"NORTS\"},{\"CustomerID\":\"OCEAN\"},{\"CustomerID\":\"OLDWO\"},{\"CustomerID\":\"OTTIK\"},{\"CustomerID\":\"PARIS\"},{\"CustomerID\":\"PERIC\"},{\"CustomerID\":\"PICCO\"},{\"CustomerID\":\"PRINI\"}],\"@odata.nextLink\":\"Customers?$count=true&$select=CustomerID&$skiptoken='PRINI'\"}";
    private static final String page4 =
        "{\"@odata.context\":\"https://services.odata.org/V4/Northwind/Northwind.svc/$metadata#Customers(CustomerID)\",\"@odata.count\":91,\"value\":[{\"CustomerID\":\"QUEDE\"},{\"CustomerID\":\"QUEEN\"},{\"CustomerID\":\"QUICK\"},{\"CustomerID\":\"RANCH\"},{\"CustomerID\":\"RATTC\"},{\"CustomerID\":\"REGGC\"},{\"CustomerID\":\"RICAR\"},{\"CustomerID\":\"RICSU\"},{\"CustomerID\":\"ROMEY\"},{\"CustomerID\":\"SANTG\"},{\"CustomerID\":\"SAVEA\"},{\"CustomerID\":\"SEVES\"},{\"CustomerID\":\"SIMOB\"},{\"CustomerID\":\"SPECD\"},{\"CustomerID\":\"SPLIR\"},{\"CustomerID\":\"SUPRD\"},{\"CustomerID\":\"THEBI\"},{\"CustomerID\":\"THECR\"},{\"CustomerID\":\"TOMSP\"},{\"CustomerID\":\"TORTU\"}],\"@odata.nextLink\":\"Customers?$count=true&$select=CustomerID&$skiptoken='TORTU'\"}";
    private static final String page5 =
        "{\"@odata.context\":\"https://services.odata.org/V4/Northwind/Northwind.svc/$metadata#Customers(CustomerID)\",\"@odata.count\":91,\"value\":[{\"CustomerID\":\"TRADH\"},{\"CustomerID\":\"TRAIH\"},{\"CustomerID\":\"VAFFE\"},{\"CustomerID\":\"VICTE\"},{\"CustomerID\":\"VINET\"},{\"CustomerID\":\"WANDK\"},{\"CustomerID\":\"WARTH\"},{\"CustomerID\":\"WELLI\"},{\"CustomerID\":\"WHITC\"},{\"CustomerID\":\"WILMK\"},{\"CustomerID\":\"WOLZA\"}]}";

    @Test
    public void testCountOverPages()
        throws IOException
    {
        final HttpClient httpClient = mock(HttpClient.class);
        doReturn(
            createHttpResponse(page1),
            createHttpResponse(page2),
            createHttpResponse(page3),
            createHttpResponse(page4),
            createHttpResponse(page5)).when(httpClient).execute(any(HttpUriRequest.class));

        final ODataRequestRead request =
            new ODataRequestRead("V4/Northwind/Northwind.svc", "Customers", "$count=true", ODataProtocol.V4);

        // prepare check whether subsequent page requests also have this header
        request.addHeader("foo", "bar");
        final ArgumentMatcher<HttpUriRequest> headerMatcher = q -> "bar".equals(q.getHeaders("foo")[0].getValue());

        // initial request
        final ODataRequestResultGeneric initialResponse = request.execute(httpClient);
        final int initialCount = initialResponse.asListOfMaps().size();

        // assertion: entity count of initial response is less than inline-count
        final long overallCount = initialResponse.getInlineCount();
        assertThat(initialCount).isLessThan((int) overallCount);

        // critical code: iterate through pages and increment item count
        int countItems = 0;
        int countRequests = 1;
        for( final List<Object> nextPage : initialResponse.iteratePages(Object.class) ) {
            verify(httpClient, times(countRequests++)).execute(argThat(headerMatcher));
            countItems += nextPage.size();
        }

        // assertion: aggregated item count is equal to inline-count
        assertThat(countItems).isEqualTo(overallCount);
    }

    @Test
    public void testErrorForResponse()
        throws IOException
    {
        final HttpClient httpClient = mock(HttpClient.class);
        doReturn(createHttpResponse(page1), createHttpResponse(page2), createHttpResponseError("Something went wrong!"))
            .when(httpClient)
            .execute(any(HttpUriRequest.class));

        final ODataRequestRead request =
            new ODataRequestRead("V4/Northwind/Northwind.svc", "Customers", "$count=true", ODataProtocol.V4);

        final ODataRequestResultGeneric result = request.execute(httpClient);

        assertThatExceptionOfType(ODataException.class).isThrownBy(() -> {
            for( final List<Object> next : result.iteratePages(Object.class) ) {
                // iterate
            }
        }).withCauseInstanceOf(ODataResponseException.class);
    }

    @Test
    public void testErrorForRequest()
        throws IOException
    {
        final HttpClient httpClient = mock(HttpClient.class);

        when(httpClient.execute(any(HttpUriRequest.class)))
            .thenReturn(createHttpResponse(page1), createHttpResponse(page2), createHttpResponse(page3))
            .thenThrow(ConnectTimeoutException.class);

        final ODataRequestRead request =
            new ODataRequestRead("V4/Northwind/Northwind.svc", "Customers", "$count=true", ODataProtocol.V4);

        final ODataRequestResultGeneric result = request.execute(httpClient);

        assertThatExceptionOfType(ODataException.class).isThrownBy(() -> {
            for( final List<Object> next : result.iteratePages(Object.class) ) {
                // iterate
            }
        }).withCauseInstanceOf(ODataRequestException.class);
    }

    @SneakyThrows
    private HttpResponse createHttpResponse( final String message )
    {
        final BasicHttpResponse page = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        page.setEntity(new StringEntity(message));
        return page;
    }

    @SneakyThrows
    private HttpResponse createHttpResponseError( final String message )
    {
        final BasicHttpResponse page =
            new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, "Internal Server Error"));
        page.setEntity(new StringEntity(message));
        return page;
    }
}
