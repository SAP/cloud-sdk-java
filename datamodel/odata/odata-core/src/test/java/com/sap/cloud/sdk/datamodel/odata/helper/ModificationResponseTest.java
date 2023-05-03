package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;
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
import lombok.ToString;

public class ModificationResponseTest
{
    private static final String SERVICE_PATH = "/service-path";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString( doNotUseGetters = true, callSuper = true )
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
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
    public void testEntityResponse()
    {
        final TestObject inputObject = new TestObject();

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        when(request.getServicePath()).thenReturn(SERVICE_PATH);

        final Header[] responseHeaders = { new BasicHeader("fizz", "buzz"), new BasicHeader("fizz", "fuzz, bizz=1") };

        final HttpResponse response = mock(HttpResponse.class);
        doReturn(responseHeaders).when(response).getAllHeaders();
        doReturn(responseHeaders).when(response).getHeaders("ETag");
        doReturn(new StringEntity("{\"foo\":\"bar\"}", Charsets.UTF_8)).when(response).getEntity();
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK")).when(response).getStatusLine();

        final HttpDestinationProperties destination = mock(HttpDestinationProperties.class);

        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, response);
        final ModificationResponse<TestObject> modification = ModificationResponse.of(result, inputObject, destination);

        assertThat(modification).isNotNull();
        assertThat(modification.getResponseStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(modification.getRequestEntity()).isSameAs(inputObject);

        assertThat(modification.getResponseEntity().get()).isNotSameAs(inputObject);
        assertThat(modification.getResponseEntity().get()).isEqualTo(new TestObject("bar"));
        assertThat(modification.getModifiedEntity()).isEqualTo(new TestObject("bar"));

        assertThat(modification.getModifiedEntity().getDestinationForFetch()).isSameAs(destination);
        assertThat(modification.getModifiedEntity().getServicePathForFetch()).isEqualTo(SERVICE_PATH);

        assertThat(modification.getResponseHeaders()).containsOnlyKeys("fizz");
        assertThat(modification.getResponseHeaders().get("fizz")).containsExactly("buzz", "fuzz", "bizz=1");
    }

    @Test
    public void testEmptyResponse()
    {
        final TestObject inputObject = new TestObject();

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        when(request.getServicePath()).thenReturn(SERVICE_PATH);

        final HttpResponse response = mock(HttpResponse.class);
        doReturn(new Header[0]).when(response).getAllHeaders();
        doReturn(new Header[0]).when(response).getHeaders("ETag");
        doReturn(new BasicHttpEntity()).when(response).getEntity();
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_NO_CONTENT, "No Content"))
            .when(response)
            .getStatusLine();

        final HttpDestinationProperties destination = mock(HttpDestinationProperties.class);

        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, response);
        final ModificationResponse<TestObject> modification = ModificationResponse.of(result, inputObject, destination);

        assertThat(modification).isNotNull();
        assertThat(modification.getResponseStatusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(modification.getRequestEntity()).isSameAs(inputObject);
        assertThat(modification.getModifiedEntity()).isNotSameAs(inputObject);
        assertThat(modification.getModifiedEntity().getDestinationForFetch()).isSameAs(destination);
        assertThat(modification.getModifiedEntity().getServicePathForFetch()).isEqualTo(SERVICE_PATH);
        assertThat(modification.getModifiedEntity()).isEqualTo(inputObject);
        assertThat(modification.getResponseHeaders()).isEmpty();
    }

    @Test
    public void testResponseIsOnlyEvaluatedOnce()
    {
        final TestObject inputObject = new TestObject();

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final Header[] responseHeaders = {};

        final HttpResponse response = mock(HttpResponse.class);
        doReturn(responseHeaders).when(response).getAllHeaders();
        doReturn(responseHeaders).when(response).getHeaders("ETag");
        doReturn(new StringEntity("{\"foo\":\"bar\"}", Charsets.UTF_8)).when(response).getEntity();
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK")).when(response).getStatusLine();

        final HttpDestinationProperties destination = mock(HttpDestinationProperties.class);

        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, response);
        final ModificationResponse<TestObject> modification = ModificationResponse.of(result, inputObject, destination);

        modification.getResponseEntity();
        final Option<TestObject> responseEntity = modification.getResponseEntity();
        assertThat(responseEntity).isNotNull();

        modification.getModifiedEntity();
        final TestObject modifiedEntity = modification.getModifiedEntity();
        assertThat(modifiedEntity).isNotNull();

        verify(response, times(1)).getEntity();
    }
}
