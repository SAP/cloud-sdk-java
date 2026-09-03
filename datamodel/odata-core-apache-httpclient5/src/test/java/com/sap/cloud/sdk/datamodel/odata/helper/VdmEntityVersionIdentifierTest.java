package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;

class VdmEntityVersionIdentifierTest
{
    @RegisterExtension
    static final WireMockExtension ERP_SERVER =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort().gzipDisabled(true)).build();

    private static final String key1val = "2015";
    private static final String key2val = "100000000";
    private static final String key3val = "1";
    private static final Map<String, Object> KEY_MAP =
        ImmutableMap.of("Key1", key1val, "Key2", key2val, "Key3", key3val);

    private static final String versionIdentifierInResponseBody =
        "W/\"datetimeoffset'2018-01-09T08%3A33%3A53.8828600Z'\"";
    private static final String versionIdentifierInHeader = "W/\"datetimeoffset'2015-01-09T08%3A33%3A53.8828600Z'\"";
    private static final String updatedVersionIdentifier = "W/\"datetimeoffset'2015-01-09T08%3A33%3A53.8828600Z'\"";

    private static final String ODATA_ENDPOINT_URL = "/path/to/service";
    private static final String ODATA_COLLECTION = "A_TestEntity";
    private static final String ODATA_DOCUMENT_ITEM_URL =
        String
            .format(
                "%s/%s(Key1='%s',Key2='%s',Key3='%s')",
                ODATA_ENDPOINT_URL,
                ODATA_COLLECTION,
                key1val,
                key2val,
                key3val);

    private static final String getDocumentItemResponseBody = """
        {
          "d": {
            "__metadata": {
              "id": "https://127.0.0.1/path/to/service/A_TestEntity(Key1='2018',Key2='100010641',Key3='1')",
              "uri": "https://127.0.0.1/path/to/service/A_TestEntity(Key1='2018',Key2='100010641',Key3='1')",
              "type": "SERVICE.A_TestEntityType",
              "etag": "W/\\"datetimeoffset'2018-01-09T08%3A33%3A53.8828600Z'\\""
            },
            "Key1": "2015",
            "Key2": "100000000",
            "Key3": "1",
            "SomeField": "Foo"
          }
        }
        """;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor( access = AccessLevel.PRIVATE )
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class TestEntity extends VdmEntity<TestEntity>
    {
        private final String entityCollection = ODATA_COLLECTION;
        private final Class<TestEntity> type = TestEntity.class;

        @With
        @SerializedName( "Key1" )
        @JsonProperty( "Key1" )
        @ODataField( odataName = "Key1" )
        private String key1;

        @With
        @SerializedName( "Key2" )
        @JsonProperty( "Key2" )
        @ODataField( odataName = "Key2" )
        private String key2;

        @With
        @SerializedName( "Key3" )
        @JsonProperty( "Key3" )
        @ODataField( odataName = "Key3" )
        private String key3;

        @With
        @SerializedName( "SomeField" )
        @JsonProperty( "SomeField" )
        @ODataField( odataName = "SomeField" )
        private String someField;

        @Nonnull
        @Override
        protected Map<String, Object> getKey()
        {
            return ImmutableMap.of("Key1", getKey1(), "Key2", getKey2(), "Key3", getKey3());
        }

        public void setSomeField( final String someField )
        {
            rememberChangedField("SomeField", this.someField);
            this.someField = someField;
        }
    }

    private DefaultHttpDestination destination;

    @BeforeEach
    void before()
    {
        destination = DefaultHttpDestination.builder(ERP_SERVER.baseUrl()).build();
        ERP_SERVER.stubFor(head(urlEqualTo(ODATA_ENDPOINT_URL)).willReturn(WireMock.ok()));
        ERP_SERVER
            .stubFor(
                get(urlEqualTo(ODATA_DOCUMENT_ITEM_URL)).willReturn(aResponse().withBody(getDocumentItemResponseBody)));
    }

    @Test
    void testVersionIdentifier()
    {
        ERP_SERVER.stubFor(patch(urlEqualTo(ODATA_DOCUMENT_ITEM_URL)).willReturn(ok()));

        final TestEntity item =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .readByKey(TestEntity.class, ODATA_COLLECTION, KEY_MAP)
                .executeRequest(destination);

        item.setKey1("2015");
        item.setSomeField("Bar");

        assertThat(item.getVersionIdentifier()).isEqualTo(Option.of(versionIdentifierInResponseBody));

        FluentHelperFactory
            .withServicePath(ODATA_ENDPOINT_URL)
            .update(ODATA_COLLECTION, item)
            .executeRequest(destination);

        ERP_SERVER
            .verify(
                patchRequestedFor(urlEqualTo(ODATA_DOCUMENT_ITEM_URL))
                    .withHeader("If-Match", equalTo(versionIdentifierInResponseBody)));
    }

    @Test
    void testVersionIdentifierSetInHeader()
    {
        ERP_SERVER
            .stubFor(
                get(urlEqualTo(ODATA_DOCUMENT_ITEM_URL))
                    .willReturn(
                        aResponse()
                            .withBody(getDocumentItemResponseBody)
                            .withHeader("ETag", versionIdentifierInHeader)));
        ERP_SERVER.stubFor(patch(urlEqualTo(ODATA_DOCUMENT_ITEM_URL)).willReturn(ok()));

        final TestEntity item =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .readByKey(TestEntity.class, ODATA_COLLECTION, KEY_MAP)
                .executeRequest(destination);

        item.setKey1("2015");
        item.setSomeField("Bar");

        assertThat(item.getVersionIdentifier()).isEqualTo(Option.of(versionIdentifierInHeader));

        FluentHelperFactory
            .withServicePath(ODATA_ENDPOINT_URL)
            .update(ODATA_COLLECTION, item)
            .executeRequest(destination);

        ERP_SERVER
            .verify(
                patchRequestedFor(urlEqualTo(ODATA_DOCUMENT_ITEM_URL))
                    .withHeader("If-Match", equalTo(versionIdentifierInHeader)));
    }

    @Test
    void testIgnoreVersionIdentifier()
    {
        ERP_SERVER.stubFor(patch(urlEqualTo(ODATA_DOCUMENT_ITEM_URL)).willReturn(ok()));

        final TestEntity item =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .readByKey(TestEntity.class, ODATA_COLLECTION, KEY_MAP)
                .executeRequest(destination);

        item.setKey1("2015");
        item.setSomeField("Bar");

        assertThat(item.getVersionIdentifier()).isEqualTo(Option.of(versionIdentifierInResponseBody));

        FluentHelperFactory
            .withServicePath(ODATA_ENDPOINT_URL)
            .update(ODATA_COLLECTION, item)
            .matchAnyVersionIdentifier()
            .executeRequest(destination);

        ERP_SERVER.verify(patchRequestedFor(urlEqualTo(ODATA_DOCUMENT_ITEM_URL)).withHeader("If-Match", equalTo("*")));
    }

    @Test
    void testIgnoreVersionIdentifierEvenIfNoVersionIdentifierPresent()
    {
        ERP_SERVER.stubFor(patch(urlEqualTo(ODATA_DOCUMENT_ITEM_URL)).willReturn(ok()));

        final TestEntity item = new TestEntity().withKey1(key1val).withKey2(key2val).withKey3(key3val);

        item.setKey1("2015");
        item.setSomeField("Bar");

        assertThat(item.getVersionIdentifier().isDefined()).isFalse();

        FluentHelperFactory
            .withServicePath(ODATA_ENDPOINT_URL)
            .update(ODATA_COLLECTION, item)
            .matchAnyVersionIdentifier()
            .executeRequest(destination);

        ERP_SERVER.verify(patchRequestedFor(urlEqualTo(ODATA_DOCUMENT_ITEM_URL)).withHeader("If-Match", equalTo("*")));
    }

    @Test
    void testExceptionWhenExpiredVersionIdentifierIsSent()
    {
        ERP_SERVER.stubFor(patch(urlEqualTo(ODATA_DOCUMENT_ITEM_URL)).willReturn(aResponse().withStatus(412)));

        final TestEntity item =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .readByKey(TestEntity.class, ODATA_COLLECTION, KEY_MAP)
                .executeRequest(destination);

        item.setKey1("2015");
        item.setSomeField("Bar");

        assertThat(item.getVersionIdentifier()).isEqualTo(Option.of(versionIdentifierInResponseBody));

        item.setVersionIdentifier(updatedVersionIdentifier);

        assertThat(item.getVersionIdentifier()).isEqualTo(Option.of(updatedVersionIdentifier));

        assertThatThrownBy(
            () -> FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .update(ODATA_COLLECTION, item)
                .executeRequest(destination))
            .isInstanceOf(ODataResponseException.class)
            .extracting("httpCode")
            .isEqualTo(412);
    }

    @Test
    void testExceptionWhenVersionIdentifierMissing()
    {
        ERP_SERVER.stubFor(patch(urlEqualTo(ODATA_DOCUMENT_ITEM_URL)).willReturn(aResponse().withStatus(428)));

        final TestEntity item =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .readByKey(TestEntity.class, ODATA_COLLECTION, KEY_MAP)
                .executeRequest(destination);

        item.setKey1("2015");
        item.setSomeField("Bar");

        assertThat(item.getVersionIdentifier()).isEqualTo(Option.of(versionIdentifierInResponseBody));

        item.setVersionIdentifier(updatedVersionIdentifier);

        assertThat(item.getVersionIdentifier()).isEqualTo(Option.of(updatedVersionIdentifier));

        assertThatThrownBy(
            () -> FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .update(ODATA_COLLECTION, item)
                .executeRequest(destination))
            .isInstanceOf(ODataResponseException.class)
            .extracting("httpCode")
            .isEqualTo(428);
    }

    @Test
    void testVersionIdentifierSentAndFetchedEvenAfterUpdate()
    {
        ERP_SERVER
            .stubFor(
                get(urlEqualTo(ODATA_DOCUMENT_ITEM_URL)).willReturn(aResponse().withBody(getDocumentItemResponseBody)));
        ERP_SERVER
            .stubFor(
                patch(urlEqualTo(ODATA_DOCUMENT_ITEM_URL))
                    .willReturn(ok().withHeader("ETag", versionIdentifierInHeader)));

        final TestEntity item =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .readByKey(TestEntity.class, ODATA_COLLECTION, KEY_MAP)
                .executeRequest(destination);

        item.setKey1("2015");
        item.setSomeField("Bar");

        assertThat(item.getVersionIdentifier()).isEqualTo(Option.of(versionIdentifierInResponseBody));

        final ModificationResponse<TestEntity> updateResponse =
            FluentHelperFactory
                .withServicePath(ODATA_ENDPOINT_URL)
                .update(ODATA_COLLECTION, item)
                .executeRequest(destination);

        ERP_SERVER
            .verify(
                patchRequestedFor(urlEqualTo(ODATA_DOCUMENT_ITEM_URL))
                    .withHeader("If-Match", equalTo(versionIdentifierInResponseBody)));

        assertThat(updateResponse.getRequestEntity().getVersionIdentifier())
            .isEqualTo(Option.of(versionIdentifierInResponseBody));
        assertThat(updateResponse.getModifiedEntity().getVersionIdentifier())
            .isEqualTo(Option.of(versionIdentifierInHeader));
    }
}
