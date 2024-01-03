/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.common.collect.Lists;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchResponse;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchResponseChangeSet;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

@WireMockTest
class ODataBatchRequestTest
{
    private static final String ODATA_SERVICE_PATH = "/service/path";

    private static String multipartCreatedEntity( final String key, final String description )
    {
        final String url = String.format("https://127.0.0.1/service/path/A_TestEntity('%s')", key);
        final String metadata = String.format("{\"id\":\"%s\",\"uri\":\"%s\",\"type\":\"A_TestEntityType\"}", url, url);
        return "Content-Type: application/http\r\n"
            + "Content-Length: 2811\r\n"
            + "content-transfer-encoding: binary\r\n"
            + "\r\n"
            + "HTTP/1.1 201 Created\r\n"
            + "Content-Type: application/json\r\n"
            + "Content-Length: 2553\r\n"
            + ("location: " + url + ")\r\n")
            + "dataserviceversion: 2.0\r\n"
            + "\r\n"
            + "{"
            + ("\"d\":{\"__metadata\":" + metadata + ",")
            + ("\"Name\":\"" + key + "\",")
            + ("\"GeneratedProperty\":\"" + UUID.randomUUID() + "'\",")
            + ("\"Description\":\"" + description + "\"")
            + "}}\r\n";
    }

    private static String multipartNoContent()
    {
        return "Content-Type: application/http\r\n"
            + "Content-Length: 71\r\n"
            + "content-transfer-encoding: binary\r\n"
            + "\r\n"
            + "HTTP/1.1 204 No Content\r\n"
            + "Content-Length: 0\r\n"
            + "dataserviceversion: 2.0\r\n";
    }

    private static final String SINGLE_CHANGESET_RESPONSE_MULTIPART =
        "--FB687BFCC8917ABB09014537111216650\r\n"
            + "Content-Type: multipart/mixed; boundary=FB687BFCC8917ABB09014537111216651\r\n"
            + "Content-Length: 5921\r\n" // ignored
            + "\r\n"
            + "--FB687BFCC8917ABB09014537111216651\r\n"
            + multipartCreatedEntity("501", "Same description")
            + "--FB687BFCC8917ABB09014537111216651\r\n"
            + multipartCreatedEntity("502", "Same description")
            + "--FB687BFCC8917ABB09014537111216651--\r\n"
            + "\r\n"
            + "--FB687BFCC8917ABB09014537111216650--";

    private static final String TWO_CHANGESETS_RESPONSE_MULTIPART =
        "--9955BD2AAB53BE8D34D8913DDB06697B0\r\n"
            + "Content-Type: multipart/mixed; boundary=9955BD2AAB53BE8D34D8913DDB06697B1\r\n"
            + "Content-Length: 2980\r\n" // ignored
            + "\r\n"
            + "--9955BD2AAB53BE8D34D8913DDB06697B1\r\n"
            + multipartCreatedEntity("503", "Alternate description")
            + "--9955BD2AAB53BE8D34D8913DDB06697B1--\r\n"
            + "\r\n"
            + "--9955BD2AAB53BE8D34D8913DDB06697B0\r\n"
            + "Content-Type: multipart/mixed; boundary=9955BD2AAB53BE8D34D8913DDB06697B1\r\n"
            + "Content-Length: 2980\r\n" // ignored
            + "\r\n"
            + "--9955BD2AAB53BE8D34D8913DDB06697B1\r\n"
            + multipartCreatedEntity("504", "Strange description")
            + "--9955BD2AAB53BE8D34D8913DDB06697B1--\r\n"
            + "\r\n"
            + "--9955BD2AAB53BE8D34D8913DDB06697B0--";

    private static final String SINGLE_UPDATE_DELETE_CHANGESET_RESPONSE_MULTIPART =
        "--4729730C52B1E9D05AAAB969F2FEE6970\r\n"
            + "Content-Type: multipart/mixed; boundary=4729730C52B1E9D05AAAB969F2FEE6971\r\n"
            + "Content-Length: 437\r\n" // ignored
            + "\r\n"
            + "--4729730C52B1E9D05AAAB969F2FEE6971\r\n"
            + multipartNoContent()
            + "\r\n"
            + "\r\n"
            + "--4729730C52B1E9D05AAAB969F2FEE6971\r\n"
            + multipartNoContent()
            + "\r\n"
            + "\r\n"
            + "--4729730C52B1E9D05AAAB969F2FEE6971--\r\n"
            + "\r\n"
            + "--4729730C52B1E9D05AAAB969F2FEE6970--";

    private HttpDestination destination;

    @BeforeEach
    void before( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        stubFor(head(urlEqualTo(ODATA_SERVICE_PATH)).willReturn(ok()));
    }

    @Test
    void testTwoCreatesInOneChangeSet()
    {
        // prepare mocked data
        final ResponseDefinitionBuilder response =
            okForContentType(
                "multipart/mixed; boundary=FB687BFCC8917ABB09014537111216650",
                SINGLE_CHANGESET_RESPONSE_MULTIPART);
        stubFor(post(urlEqualTo(ODATA_SERVICE_PATH + "/$batch")).willReturn(response));

        final TestEntity address1 = new TestEntity();
        address1.setDescription("Same description");

        final TestEntity address2 = new TestEntity();
        address2.setDescription("Same description");

        final BatchResponse result =
            new TestBatch()
                .beginChangeSet()
                .createTestEntity(address1)
                .createTestEntity(address2)
                .endChangeSet()
                .executeRequest(destination);

        final Try<BatchResponseChangeSet> changeSet = result.get(0);
        assertThat(changeSet.isSuccess()).isTrue();

        final List<VdmEntity<?>> createdEntities = changeSet.get().getCreatedEntities();
        assertResponse(createdEntities, 2, address1);
    }

    @Test
    void testTwoCreatesInMultipleChangeSets()
    {
        // prepare mocked data
        final ResponseDefinitionBuilder response =
            okForContentType(
                "multipart/mixed; boundary=9955BD2AAB53BE8D34D8913DDB06697B0",
                TWO_CHANGESETS_RESPONSE_MULTIPART);
        stubFor(post(urlEqualTo(ODATA_SERVICE_PATH + "/$batch")).willReturn(response));

        final TestEntity address1 = new TestEntity();
        address1.setDescription("Alternate description");

        final TestEntity address2 = new TestEntity();
        address2.setDescription("Strange description");

        final BatchResponse result =
            new TestBatch()

                .beginChangeSet()
                .createTestEntity(address1)
                .endChangeSet()

                .beginChangeSet()
                .createTestEntity(address2)
                .endChangeSet()

                .executeRequest(destination);

        assertThat(result.get(0)).isNotEmpty();
        assertResponse(result.get(0).get().getCreatedEntities(), 1, address1);

        assertThat(result.get(1)).isNotEmpty();
        assertResponse(result.get(1).get().getCreatedEntities(), 1, address2);
    }

    @Test
    void testUpdateDeleteOperationInOneChangeSet()
    {
        final String concreteNameEndpoint = "A_TestEntity(%27Test%27)";

        final TestEntity address = new TestEntity().withName("Test");

        // prepare mocked data
        final ResponseDefinitionBuilder response =
            okForContentType(
                "multipart/mixed; boundary=4729730C52B1E9D05AAAB969F2FEE6970",
                SINGLE_UPDATE_DELETE_CHANGESET_RESPONSE_MULTIPART);
        stubFor(
            post(urlEqualTo(ODATA_SERVICE_PATH + "/$batch"))
                .withRequestBody(containing("PATCH " + concreteNameEndpoint))
                .withRequestBody(containing("DELETE " + concreteNameEndpoint))
                .willReturn(response));

        // change entity
        address.setDescription("Alternative Description");

        final BatchResponse result =
            new TestBatch()
                .beginChangeSet()
                .updateTestEntity(address)
                .deleteTestEntity(address)
                .endChangeSet()
                .executeRequest(destination);

        final Try<BatchResponseChangeSet> changeSet = result.get(0);
        assertThat(changeSet.isSuccess()).isTrue();
    }

    @Test
    void testUpdateWithPatchRetainingNullValues()
    {
        final String concreteNameEndpoint = "A_TestEntity(%27Test%27)";

        final TestEntity address = new TestEntity().withName("Test").withDescription("Existing description");

        // prepare mocked data
        final ResponseDefinitionBuilder response =
            okForContentType(
                "multipart/mixed; boundary=4729730C52B1E9D05AAAB969F2FEE6970",
                SINGLE_UPDATE_DELETE_CHANGESET_RESPONSE_MULTIPART);
        stubFor(
            post(urlEqualTo(ODATA_SERVICE_PATH + "/$batch"))
                .withRequestBody(containing("PATCH " + concreteNameEndpoint))
                .willReturn(response));

        // change entity
        address.setDescription(null);
        final BatchResponse result =
            new TestBatch().beginChangeSet().updateTestEntity(address).endChangeSet().executeRequest(destination);

        verify(
            postRequestedFor(urlEqualTo(ODATA_SERVICE_PATH + "/$batch"))
                .withRequestBody(containing("\"Description\":" + null)));
        final Try<BatchResponseChangeSet> changeSet = result.get(0);
        assertThat(changeSet.isSuccess()).isTrue();
    }

    /*
     * Assertion helpers
     */
    private void assertResponse( List<VdmEntity<?>> createdEntities, int expectedSize, TestEntity referenceEntity )
    {
        // general assertion: two created entities not equal to each other or to the input instance
        assertThat(createdEntities).hasSize(expectedSize).doesNotHaveDuplicates().doesNotContain((VdmEntity<?>) null);
        assertThat(createdEntities).allMatch(TestEntity.class::isInstance).doesNotContain(referenceEntity);

        // cast items to TestEntity
        final List<TestEntity> createdTestEntities =
            createdEntities.stream().map(TestEntity.class::cast).collect(Collectors.toList());

        // check for auto generated fields ID and UUID, must not be similar or equal to input
        assertThat(createdTestEntities)
            .extracting(TestEntity::getName)
            .isNotEmpty()
            .doesNotHaveDuplicates()
            .doesNotContain(referenceEntity.getName(), "", null);

        assertThat(createdTestEntities)
            .extracting(TestEntity::getGeneratedProperty)
            .isNotEmpty()
            .doesNotHaveDuplicates()
            .doesNotContain(referenceEntity.getGeneratedProperty());

        // check for valid input data
        for( final Function<TestEntity, Object> handler : Lists
            .<Function<TestEntity, Object>> newArrayList(TestEntity::getDescription) ) {
            assertThat(createdTestEntities).extracting(handler).containsOnly(handler.apply(referenceEntity));
        }
    }

    @Test
    void testEncodingInBatchedUpdate()
    {
        final ResponseDefinitionBuilder response =
            okForContentType(
                "multipart/mixed; boundary=FB687BFCC8917ABB09014537111216650",
                SINGLE_CHANGESET_RESPONSE_MULTIPART);
        stubFor(
            post(urlEqualTo(ODATA_SERVICE_PATH + "/$batch"))
                .withRequestBody(containing("Müller Straße"))
                .willReturn(response));

        final TestEntity address = new TestEntity().withName("Müller Straße");
        address.setDescription("Müller Straße");

        new TestBatch().beginChangeSet().updateTestEntity(address).endChangeSet().executeRequest(destination);

        verify(1, postRequestedFor(urlEqualTo(ODATA_SERVICE_PATH + "/$batch")));
        verify(1, headRequestedFor(anyUrl()).withHeader("x-csrf-token", equalTo("fetch")));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode( callSuper = true )
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class TestEntity extends VdmEntity<TestEntity>
    {
        private final String entityCollection = "A_TestEntity";
        private final Class<TestEntity> type = TestEntity.class;

        @SerializedName( "Name" )
        @JsonProperty( "Name" )
        @ODataField( odataName = "Name" )
        @With
        private String name;

        @SerializedName( "Description" )
        @JsonProperty( "Description" )
        @ODataField( odataName = "Description" )
        @With
        private String description;

        @SerializedName( "GeneratedProperty" )
        @JsonProperty( "GeneratedProperty" )
        @ODataField( odataName = "GeneratedProperty" )
        private String generatedProperty;

        @Nonnull
        @Override
        protected Map<String, Object> getKey()
        {
            return Collections.singletonMap("Name", getName());
        }

        @Nonnull
        @Override
        protected Map<String, Object> toMapOfFields()
        {
            final Map<String, Object> result = new LinkedHashMap<>();
            result.put("Name", getName());
            result.put("Description", getDescription());
            return result;
        }

        public void setDescription( final String description )
        {
            rememberChangedField("Description", this.description);
            this.description = description;
        }
    }

    @Getter
    public static class TestBatch extends BatchFluentHelperBasic<TestBatch, TestBatchChangeset>
    {
        private final String servicePathForBatchRequest = ODATA_SERVICE_PATH;

        @Nonnull
        @Override
        protected TestBatch getThis()
        {
            return this;
        }

        @Nonnull
        @Override
        public TestBatchChangeset beginChangeSet()
        {
            return new TestBatchChangeset(this);
        }
    }

    public static class TestBatchChangeset extends BatchChangeSetFluentHelperBasic<TestBatch, TestBatchChangeset>
    {
        public TestBatchChangeset( final TestBatch parent )
        {
            super(parent, parent);
        }

        @Nonnull
        @Override
        protected TestBatchChangeset getThis()
        {
            return this;
        }

        public TestBatchChangeset updateTestEntity( final TestEntity entity )
        {
            return super.addRequestUpdate(FluentHelperFactory.withServicePath(ODATA_SERVICE_PATH)::update, entity);
        }

        public TestBatchChangeset createTestEntity( final TestEntity entity )
        {
            return super.addRequestCreate(FluentHelperFactory.withServicePath(ODATA_SERVICE_PATH)::create, entity);
        }

        public TestBatchChangeset deleteTestEntity( final TestEntity entity )
        {
            return super.addRequestDelete(FluentHelperFactory.withServicePath(ODATA_SERVICE_PATH)::delete, entity);
        }
    }
}
