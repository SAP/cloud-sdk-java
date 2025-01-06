package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorDetails;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;

@WireMockTest
class CreateViaNavigationPropertyTest
{
    private static final String ODATA_SERVICE_PATH = "/service/path";
    private static final String ODATA_ENTITY = "A_TestEntity";
    private static final String ODATA_ENTITY_RELATED = "A_RelatedEntity";
    private static final String KEY = "('2')";
    private static final String RELATIONS_NAV_PROP = "to_Relations";

    private static final String JSON_MOCK_REQUEST = "{\"Description\" :\"DE\"}";
    private static final String JSON_CREATED_RESPONSE = """
        {
          "d": {
            "__metadata": {
              "id": "https://127.0.0.1/service/path/A_TestEntity(Name='2',Relation='652138')",
              "uri": "https://127.0.0.1/service/path/A_TestEntity(Name='2',Relation='652138')",
              "type": "TEST_SERVICE.A_RelatedEntityType"
            }
          }
        }
        """;
    private static final String JSON_NOT_IMPLEMENTED_RESPONSE = """
        {
          "error": {
            "code": "AB/100",
            "message": {
              "lang": "en",
              "value": "Invalid method invocation: 'CREATE' method is called on the non-root entity 'A_RelatedEntity'"
            },
            "innererror": {
              "application": {
                "component_id": "AB-CDE-FGH-IJ",
                "service_namespace": "/SAP/",
                "service_id": "TEST_SERVICE",
                "service_version": "0001"
              },
              "transactionid": "10000",
              "timestamp": "20180815132618.3364870",
              "errordetails": [
                {
                  "code": "INNER-CODE-1",
                  "message": "INNER-MESSAGE-1",
                  "propertyref": "",
                  "severity": "error",
                  "target": ""
                }
              ]
            }
          }
        }
        """;

    private DefaultHttpDestination destination;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        stubFor(head(urlEqualTo(ODATA_SERVICE_PATH)).willReturn(ok()));
    }

    @Test
    void happyPathWorksAsExpected()
    {
        stubFor(
            post(urlEqualTo(ODATA_SERVICE_PATH + "/" + ODATA_ENTITY + KEY + "/" + RELATIONS_NAV_PROP))
                .withRequestBody(equalToJson(JSON_MOCK_REQUEST))
                .willReturn(created().withBody(JSON_CREATED_RESPONSE)));

        FluentHelperFactory
            .withServicePath(ODATA_SERVICE_PATH)
            .create(ODATA_ENTITY, new RelatedEntity("DE"))
            .asChildOf(new TestEntity().withName("2"), TestEntity.TO_RELATIONS)
            .executeRequest(destination);

        verify(1, headRequestedFor(urlEqualTo(ODATA_SERVICE_PATH)));
        verify(
            1,
            postRequestedFor(urlEqualTo(ODATA_SERVICE_PATH + "/" + ODATA_ENTITY + KEY + "/" + RELATIONS_NAV_PROP)));
    }

    @Test
    void nullParametersResultInDefaultBehavior()
    {
        stubFor(
            post(urlEqualTo(ODATA_SERVICE_PATH + "/" + ODATA_ENTITY_RELATED))
                .withRequestBody(equalToJson(JSON_MOCK_REQUEST))
                .willReturn(created().withBody(JSON_CREATED_RESPONSE)));

        FluentHelperFactory
            .withServicePath(ODATA_SERVICE_PATH)
            .create(ODATA_ENTITY_RELATED, new RelatedEntity("DE"))
            .asChildOf(null, null)
            .executeRequest(destination);

        verify(1, headRequestedFor(urlEqualTo(ODATA_SERVICE_PATH)));
        verify(1, postRequestedFor(urlEqualTo(ODATA_SERVICE_PATH + "/" + ODATA_ENTITY_RELATED)));
    }

    @Test
    void directCreationFails()
    {
        stubFor(
            post(urlEqualTo(ODATA_SERVICE_PATH + "/" + ODATA_ENTITY_RELATED))
                .withRequestBody(equalToJson(JSON_MOCK_REQUEST))
                .willReturn(
                    aResponse()
                        .withStatus(501)
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JSON_NOT_IMPLEMENTED_RESPONSE)));

        assertThatCode(
            () -> FluentHelperFactory
                .withServicePath(ODATA_SERVICE_PATH)
                .create(ODATA_ENTITY_RELATED, new RelatedEntity("DE"))
                .executeRequest(destination))
            .hasMessage(
                "The HTTP response code (501) indicates an error. The OData service responded with an error message.")
            .asInstanceOf(InstanceOfAssertFactories.type(ODataServiceErrorException.class))
            .matches(e -> e.getHttpCode() == 501)
            .extracting(ODataServiceErrorException::getOdataError)
            .satisfies(e -> {
                assertThat(e.getODataCode()).isEqualTo("AB/100");
                assertThat(e.getODataMessage()).startsWith("Invalid method invocation: 'CREATE'");

                final List<ODataServiceErrorDetails> details = e.getDetails();
                assertThat(details).isNotNull().hasSize(1);
                assertThat(details.get(0).getODataCode()).isEqualTo("INNER-CODE-1");
                assertThat(details.get(0).getODataMessage()).isEqualTo("INNER-MESSAGE-1");
            });

        verify(1, headRequestedFor(urlEqualTo(ODATA_SERVICE_PATH)));
        verify(1, postRequestedFor(urlEqualTo(ODATA_SERVICE_PATH + "/" + ODATA_ENTITY_RELATED)));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode( callSuper = true )
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class TestEntity extends VdmEntity<TestEntity>
    {
        private final String entityCollection = ODATA_ENTITY;
        private final Class<TestEntity> type = TestEntity.class;

        @SerializedName( "Name" )
        @JsonProperty( "Name" )
        @ODataField( odataName = "Name" )
        @With
        private String name;

        @SerializedName( RELATIONS_NAV_PROP )
        @JsonProperty( RELATIONS_NAV_PROP )
        @ODataField( odataName = RELATIONS_NAV_PROP )
        private List<RelatedEntity> relations;

        public static final TestEntityLink<RelatedEntity> TO_RELATIONS = new TestEntityLink<>(RELATIONS_NAV_PROP);

        @Nonnull
        @Override
        protected Map<String, Object> getKey()
        {
            return Collections.singletonMap("Name", getName());
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode( callSuper = true )
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class RelatedEntity extends VdmEntity<RelatedEntity>
    {
        private final String entityCollection = ODATA_ENTITY_RELATED;
        private final Class<RelatedEntity> type = RelatedEntity.class;

        @SerializedName( "Description" )
        @JsonProperty( "Description" )
        @ODataField( odataName = "Description" )
        private String description;
    }

    public static class TestEntityLink<ObjectT extends VdmObject<ObjectT>>
        extends
        EntityLink<TestEntityLink<ObjectT>, TestEntity, ObjectT>
    {
        public TestEntityLink( final String name )
        {
            super(name);
        }
    }
}
