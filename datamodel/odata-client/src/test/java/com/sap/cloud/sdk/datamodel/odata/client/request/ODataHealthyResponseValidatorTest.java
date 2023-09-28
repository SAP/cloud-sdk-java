package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;

@RunWith( MockitoJUnitRunner.class )
public class ODataHealthyResponseValidatorTest
{
    @Mock
    ODataRequestResult odataResult;

    @Mock
    ODataRequestGeneric odataRequest;

    @Mock
    HttpResponse httpResponse;

    @Mock
    StatusLine httpResponseStatusLine;

    @Before
    public void adjustMocks()
    {
        when(odataRequest.getProtocol()).thenReturn(ODataProtocol.V2);
        when(odataResult.getHttpResponse()).thenReturn(httpResponse);
        when(odataResult.getODataRequest()).thenReturn(odataRequest);
        when(httpResponse.getStatusLine()).thenReturn(httpResponseStatusLine);
        when(httpResponse.getAllHeaders()).thenReturn(new Header[0]);
        when(httpResponseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    }

    @Test
    public void testSuccess()
    {
        ODataHealthyResponseValidator.requireHealthyResponse(odataResult);
    }

    @Test
    public void testNotFound()
    {
        when(httpResponseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_NOT_FOUND);

        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> ODataHealthyResponseValidator.requireHealthyResponse(odataResult))
            .withMessageContaining(HttpStatus.SC_NOT_FOUND + "");
    }

    @Test
    public void testODataError()
    {
        final String odata_error_json =
            "{"
                + "\"error\": {"
                + "  \"code\": \"err123\","
                + "  \"message\": \"Unsupported functionality\","
                + "  \"target\": \"query\","
                + "  \"details\": ["
                + "     {"
                + "       \"code\": \"forty-two\","
                + "       \"target\": \"$search\", "
                + "       \"message\": \"$search query option not supported\""
                + "     }"
                + "   ],"
                + "  \"innererror\": {"
                + "     \"foo\": 123,"
                + "     \"bar\": \"ok\""
                + "  }"
                + "}"
                + "}";

        when(httpResponseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(httpResponse.getEntity()).thenReturn(new StringEntity(odata_error_json, StandardCharsets.UTF_8));

        assertThatExceptionOfType(ODataServiceErrorException.class)
            .isThrownBy(() -> ODataHealthyResponseValidator.requireHealthyResponse(odataResult))
            .withMessageContaining(HttpStatus.SC_INTERNAL_SERVER_ERROR + "")
            .satisfies(e -> {
                assertThat(e.getHttpBody()).containsExactly(odata_error_json);
                assertThat(e.getHttpCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                assertThat(e.getOdataError().getODataCode()).isEqualTo("err123");
                assertThat(e.getOdataError().getODataMessage()).isEqualTo("Unsupported functionality");
            });
    }
}
