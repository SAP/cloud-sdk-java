package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.apache.http.HttpVersion.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.nio.charset.StandardCharsets;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;

import lombok.Getter;

class ODataHealthyResponseValidatorTest
{
    private static final ODataRequestGeneric REQUEST =
        new ODataRequestRead("service-path", "EntitySet", null, ODataProtocol.V2);

    @Test
    void testSuccess()
    {
        final HttpResponse httpResponse = new BasicHttpResponse(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final ODataRequestResult odataResult = new ODataRequestResultGeneric(REQUEST, httpResponse);

        ODataHealthyResponseValidator.requireHealthyResponse(odataResult);
    }

    @Test
    void testNotFound()
    {
        final HttpResponse httpResponse = new BasicHttpResponse(HTTP_1_1, HttpStatus.SC_NOT_FOUND, "Not Found");
        final ODataRequestResult odataResult = new ODataRequestResultGeneric(REQUEST, httpResponse);

        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> ODataHealthyResponseValidator.requireHealthyResponse(odataResult))
            .withMessageContaining(HttpStatus.SC_NOT_FOUND + "");
    }

    @Test
    void testODataError()
    {
        final String odata_error_json = """
            {
              "error": {
                "code": "err123",
                "message": "Unsupported functionality",
                "target": "query",
                "details": [
                  {
                    "code": "forty-two",
                    "target": "$search",
                    "message": "$search query option not supported"
                  }
                ],
                "innererror": {
                  "foo": 123,
                  "bar": "ok"
                }
              }
            }
            """;

        final HttpResponse httpResponse = new BasicHttpResponse(HTTP_1_1, HttpStatus.SC_INTERNAL_SERVER_ERROR, "Oh!");
        httpResponse.setEntity(new StringEntity(odata_error_json, StandardCharsets.UTF_8));
        final ODataRequestResult odataResult = new ODataRequestResultGeneric(REQUEST, httpResponse);

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
