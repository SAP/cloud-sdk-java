package com.sap.cloud.sdk.datamodel.odatav4.core;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.annotation.Nonnull;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.result.ElementName;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;

class ModificationResponseTest
{
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString( doNotUseGetters = true, callSuper = true )
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
    @JsonSerialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class )
    @JsonDeserialize( using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class )
    public static class TestObject extends VdmEntity<TestObject>
    {
        @Getter
        private final String odataType = "TestObject";

        @Getter
        private final Class<TestObject> type = TestObject.class;

        @ElementName( "foo" )
        private String name;

        @Nonnull
        @Override
        protected String getEntityCollection()
        {
            return odataType;
        }
    }

    @Test
    void testEntityResponse()
    {
        final TestObject inputObject = new TestObject();

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final Header[] responseHeaders = { new BasicHeader("fizz", "buzz"), new BasicHeader("fizz", "fuzz, bizz=1") };

        final HttpResponse response = mock(HttpResponse.class);
        doReturn(responseHeaders).when(response).getAllHeaders();
        doReturn(responseHeaders).when(response).getHeaders("ETag");
        doReturn(new StringEntity("{\"foo\":\"bar\"}", UTF_8)).when(response).getEntity();
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK")).when(response).getStatusLine();

        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, response);
        final ModificationResponse<TestObject> modification = ModificationResponse.of(result, inputObject);

        assertThat(modification).isNotNull();
        assertThat(modification.getResponseStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(modification.getRequestEntity()).isSameAs(inputObject);

        assertThat(modification.getResponseEntity().get()).isNotSameAs(inputObject);
        assertThat(modification.getResponseEntity().get()).isEqualTo(new TestObject("bar"));
        assertThat(modification.getModifiedEntity()).isEqualTo(new TestObject("bar"));

        assertThat(modification.getResponseHeaders()).containsOnlyKeys("fizz");
        assertThat(modification.getResponseHeaders().get("fizz")).containsExactly("buzz", "fuzz, bizz=1");
    }

    @Test
    void testEmptyResponse()
    {
        final TestObject inputObject = new TestObject();

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final HttpResponse response = mock(HttpResponse.class);
        doReturn(new Header[0]).when(response).getAllHeaders();
        doReturn(new Header[0]).when(response).getHeaders("ETag");
        doReturn(new BasicHttpEntity()).when(response).getEntity();
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_NO_CONTENT, "No Content"))
            .when(response)
            .getStatusLine();

        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, response);
        final ModificationResponse<TestObject> modification = ModificationResponse.of(result, inputObject);

        assertThat(modification).isNotNull();
        assertThat(modification.getResponseStatusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(modification.getRequestEntity()).isSameAs(inputObject);
        assertThat(modification.getModifiedEntity()).isNotSameAs(inputObject);
        assertThat(modification.getModifiedEntity()).isEqualTo(inputObject);
        assertThat(modification.getResponseHeaders()).isEmpty();
    }

    @SneakyThrows
    @Test
    void testResponseIsOnlyEvaluatedOnce()
    {
        final TestObject inputObject = new TestObject();

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
        final StringEntity entity = spy(new StringEntity("{\"foo\":\"bar\"}", UTF_8));
        response.setEntity(entity);

        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, response);
        final ModificationResponse<TestObject> modification = ModificationResponse.of(result, inputObject);

        modification.getResponseEntity();
        final Option<TestObject> responseEntity = modification.getResponseEntity();
        assertThat(responseEntity).isNotNull();

        modification.getModifiedEntity();
        final TestObject modifiedEntity = modification.getModifiedEntity();
        assertThat(modifiedEntity).isNotNull();

        verify(entity, times(1)).getContent();
    }
}
