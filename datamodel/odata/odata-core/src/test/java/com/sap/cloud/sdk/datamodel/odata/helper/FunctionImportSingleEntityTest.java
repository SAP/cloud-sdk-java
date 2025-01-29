package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@WireMockTest
class FunctionImportSingleEntityTest
{
    private static final String TEST_SERVICE_PATH = "/some/service/path";
    private static final String TEST_FUNCTION_NAME = "Testing";
    private static final String CSRF = "secret";
    private static final TestingEntity TEST_ENTITY = new TestingEntity("hello", "world");

    private HttpDestinationProperties destination;

    @BeforeEach
    void assignMockedDestination( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        final Map<String, ImmutableMap<String, TestingEntity>> functionResult =
            ImmutableMap.of("d", ImmutableMap.of(TEST_FUNCTION_NAME, TEST_ENTITY));
        stubFor(get(anyUrl()).willReturn(okForJson(functionResult)));
        stubFor(head(anyUrl()).willReturn(ok().withHeader("x-csrf-token", CSRF)));
    }

    @EqualsAndHashCode( callSuper = true )
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
    public static class TestingEntity extends VdmEntity<TestingEntity>
    {
        @Getter( AccessLevel.PROTECTED )
        private final String entityCollection = "TestingCollection";

        @Getter
        private final Class<TestingEntity> type = TestingEntity.class;

        @SerializedName( "id" )
        @ODataField( odataName = "id" )
        private String id;

        @SerializedName( "text" )
        @ODataField( odataName = "text" )
        private String text;
    }

    private static class SingleEntityFunctionImport
        extends
        FluentHelperFunction<SingleEntityFunctionImport, TestingEntity, TestingEntity>
    {
        @Getter
        private final Map<String, Object> parameters = Maps.newHashMap();

        @Getter
        private final String functionName = TEST_FUNCTION_NAME;

        @Getter
        private final Class<TestingEntity> entityClass = TestingEntity.class;

        public SingleEntityFunctionImport()
        {
            super(TEST_SERVICE_PATH);
        }

        @Nullable
        @Override
        protected JsonElement refineJsonResponse( @Nullable final JsonElement jsonElement )
        {
            return Option.of(jsonElement).toTry().map(o -> o.getAsJsonObject().get(getFunctionName())).getOrNull();
        }

        @Nonnull
        @Override
        protected HttpUriRequest createRequest( @Nonnull final URI uri )
        {
            return new HttpGet(uri);
        }

        @Nullable
        @Override
        public TestingEntity executeRequest( @Nonnull final Destination destination )
        {
            return super.executeSingle(destination);
        }

        private void addParameter( final String key, final Object value )
        {
            parameters.put(key, value);
        }
    }

    @Test
    void testFunctionParameterWithPlusSign()
    {
        final SingleEntityFunctionImport functionImport = new SingleEntityFunctionImport();
        functionImport.addParameter("foo", "ba+r");

        final TestingEntity responseEntity = functionImport.executeRequest(destination);

        verify(getRequestedFor(urlEqualTo(TEST_SERVICE_PATH + "/Testing?foo='ba%2Br'")));
        assertThat(responseEntity).isEqualTo(TEST_ENTITY);
    }

    @Test
    void testFunctionParameterWithWhitespace()
    {
        final SingleEntityFunctionImport functionImport = new SingleEntityFunctionImport();
        functionImport.addParameter("foo", "b a+  r ");

        final TestingEntity responseEntity = functionImport.executeRequest(destination);

        verify(getRequestedFor(urlEqualTo(TEST_SERVICE_PATH + "/Testing?foo='b%20a%2B%20%20r%20'")));
        assertThat(responseEntity).isEqualTo(TEST_ENTITY);
    }

    @Test
    void testFunctionParameterWithQuotes()
    {
        final SingleEntityFunctionImport functionImport = new SingleEntityFunctionImport();
        functionImport.addParameter("GUID", UUID.fromString("571c74ab-c66a-4d34-ab32-ff16ec4653a5"));

        final TestingEntity responseEntity = functionImport.executeRequest(destination);

        verify(
            getRequestedFor(
                urlEqualTo(TEST_SERVICE_PATH + "/Testing?GUID=guid'571c74ab-c66a-4d34-ab32-ff16ec4653a5'")));
        assertThat(responseEntity).isEqualTo(TEST_ENTITY);
    }

    @Test
    void testFunctionParameterWithSpecialCharacters()
    {
        final SingleEntityFunctionImport functionImport = new SingleEntityFunctionImport();
        functionImport.addParameter("Address", "Unusual? Road #99 with 100%");

        final TestingEntity responseEntity = functionImport.executeRequest(destination);

        verify(
            getRequestedFor(
                urlEqualTo(TEST_SERVICE_PATH + "/Testing?Address='Unusual%3F%20Road%20%2399%20with%20100%25'")));
        assertThat(responseEntity).isEqualTo(TEST_ENTITY);
    }

    @Test
    void testFunctionParameterWithParentheses()
    {
        final SingleEntityFunctionImport functionImport = new SingleEntityFunctionImport();
        functionImport.addParameter("Address", "Potsdam (Germany)");

        final TestingEntity responseEntity = functionImport.executeRequest(destination);

        verify(getRequestedFor(urlEqualTo(TEST_SERVICE_PATH + "/Testing?Address='Potsdam%20(Germany)'")));
        assertThat(responseEntity).isEqualTo(TEST_ENTITY);
    }

    @Test
    void testCustomJsonResponseParsing()
    {
        final String jsonResponse = "{\"d\":{\"CUSTOM_OBJECT\":{\"id\":\"hello\",\"text\":\"world\"}}}";
        final MappingBuilder requestMapping = get(urlEqualTo(TEST_SERVICE_PATH + "/Testing"));
        stubFor(requestMapping.willReturn(okJson(jsonResponse)));

        final SingleEntityFunctionImport originalFunctionImport = new SingleEntityFunctionImport()
        {
            @Nonnull
            @Override
            public String getFunctionName()
            {
                return "Testing";
            }

            @Override
            protected JsonElement refineJsonResponse( @Nullable final JsonElement jsonElement )
            {
                return jsonElement.getAsJsonObject().get("CUSTOM_OBJECT");
            }
        };

        final TestingEntity responseEntity = originalFunctionImport.executeRequest(destination);
        assertThat(responseEntity).isEqualTo(TEST_ENTITY);
    }

    @Test
    void testFunctionParameterWithConventionalValue()
    {
        final SingleEntityFunctionImport functionImport = new SingleEntityFunctionImport();
        functionImport.addParameter("Address", "Potsdam");

        final TestingEntity responseEntity = functionImport.executeRequest(destination);

        verify(getRequestedFor(urlEqualTo(TEST_SERVICE_PATH + "/Testing?Address='Potsdam'")));
        assertThat(responseEntity).isEqualTo(TEST_ENTITY);
    }

    @Test
    void testFunctionImportWithoutParameters()
    {
        final SingleEntityFunctionImport functionImport = new SingleEntityFunctionImport();

        final TestingEntity responseEntity = functionImport.executeRequest(destination);

        verify(getRequestedFor(urlEqualTo(TEST_SERVICE_PATH + "/Testing")));
        assertThat(responseEntity).isEqualTo(TEST_ENTITY);
    }

    @Test
    void testFunctionImportReturnsNullAsResponseBody()
    {
        final SingleEntityFunctionImport functionImport = new SingleEntityFunctionImport();

        stubFor(get(urlEqualTo(TEST_SERVICE_PATH + "/Testing")).willReturn(null));

        assertThatExceptionOfType(ODataException.class)
            .isThrownBy(() -> functionImport.executeRequest(destination))
            .withMessageContaining("Unable to read OData 2.0 response.");
    }
}
