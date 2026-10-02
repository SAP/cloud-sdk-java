package com.sap.cloud.sdk.datamodel.odata.helper;

import static java.lang.String.format;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory;

import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * SERVICE A:
 *   GET /FunctionToMedia(Param1,Param2) -> multiple entities from A_MediaType
 *   GET /A_MediaEntitySet(Id)/$value -> media lookup
 *
 * SERVICE B:
 *   GET /A_EntitySet -> multiple entities from A_EntityType
 *   GET /A_EntitySet(Id) -> single entity from A_EntityType
 *   GET /A_EntitySet(Id)/to_RelatedEntity -> single entity from A_RelatedType
 *   GET /A_EntitySet(Id)/to_RelatedEntity/to_TransitiveEntity -> multiple entities from A_TransitiveType
 *   GET /A_RelatedEntitySet(Id) -> single entity from A_RelatedType
 *   GET /A_RelatedEntitySet(Id)/to_TransitiveEntity -> multiple entities from A_TransitiveType
 */
@WireMockTest
class ServicePathTest
{
    private static final String SERVICE_A = "/path/to/serviceA";
    private static final String SERVICE_B = "/path/to/serviceB";

    private HttpDestination destination;

    @BeforeEach
    void setupDestination( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        stubFor(head(urlEqualTo(SERVICE_A)).willReturn(ok()));
        stubFor(head(urlEqualTo(SERVICE_B)).willReturn(ok()));
    }

    @Test
    void testChildEntityFromGet()
    {
        final String responseEntityListWithRelated =
            createPayloadMultiple(createTestEntity("72", createRelatedEntity("13857", null)));
        stubFor(get(urlPathMatching(SERVICE_B + "/A_EntitySet")).willReturn(okJson(responseEntityListWithRelated)));

        final String responseEntityListTransitive = createPayloadMultiple(createTransitiveEntity("Bar"));
        stubFor(
            get(urlPathMatching(SERVICE_B + "/A_RelatedEntitySet\\('(.*)'\\)/to_TransitiveEntity"))
                .willReturn(okJson(responseEntityListTransitive)));

        final List<TestEntity> testEntitys =
            FluentHelperFactory
                .withServicePath(SERVICE_B)
                .read(TestEntity.class, "A_EntitySet")
                .select(TestEntity.TO_RELATED_ENTITY)
                .executeRequest(destination);

        final RelatedEntity bpRelatedEntity = testEntitys.get(0).getRelatedEntity();
        assertThat(bpRelatedEntity).isNotNull();

        final List<TransitiveEntity> bpTransitiveEntity = bpRelatedEntity.fetchTransitiveEntity();
        assertThat(bpTransitiveEntity).isNotNull();

        verify(getRequestedFor(urlPathMatching(SERVICE_B + "/A_RelatedEntitySet\\('(.*)'\\)/to_TransitiveEntity")));
    }

    @Test
    void testChildEntityFromGetByKey()
    {
        final String resonseEntityWithRelated =
            createPayloadSingle(createTestEntity("72", createRelatedEntity("13857", null)));
        stubFor(
            get(urlPathMatching(SERVICE_B + "/A_EntitySet\\('(.*)'\\)")).willReturn(okJson(resonseEntityWithRelated)));

        final String responseEntityListTransitive = createPayloadMultiple(createTransitiveEntity("Bar"));
        stubFor(
            get(urlPathMatching(SERVICE_B + "/A_RelatedEntitySet\\('(.*)'\\)/to_TransitiveEntity"))
                .willReturn(okJson(responseEntityListTransitive)));

        final TestEntity testEntity =
            FluentHelperFactory
                .withServicePath(SERVICE_B)
                .readByKey(TestEntity.class, "A_EntitySet", Collections.singletonMap("EntityId", "72"))
                .select(TestEntity.TO_RELATED_ENTITY)
                .executeRequest(destination);

        final RelatedEntity bpRelatedEntity = testEntity.getRelatedEntity();
        assertThat(bpRelatedEntity).isNotNull();

        final List<TransitiveEntity> bpTransitiveEntity = bpRelatedEntity.fetchTransitiveEntity();
        assertThat(bpTransitiveEntity).isNotNull();

        verify(getRequestedFor(urlPathMatching(SERVICE_B + "/A_RelatedEntitySet\\('(.*)'\\)/to_TransitiveEntity")));
    }

    @Test
    void testChildEntityFromCreate()
    {
        final String resonseEntityWithRelated =
            createPayloadSingle(createTestEntity("72", createRelatedEntity("13857", null)));
        stubFor(post(urlPathMatching(SERVICE_B + "/A_EntitySet")).willReturn(okJson(resonseEntityWithRelated)));

        final String responseEntityListTransitive = createPayloadMultiple(createTransitiveEntity("Bar"));
        stubFor(
            get(urlPathMatching(SERVICE_B + "/A_RelatedEntitySet\\('(.*)'\\)/to_TransitiveEntity"))
                .willReturn(okJson(responseEntityListTransitive)));

        TestEntity testEntity = new TestEntity();

        testEntity =
            FluentHelperFactory
                .withServicePath(SERVICE_B)
                .create("A_EntitySet", testEntity)
                .executeRequest(destination)
                .getModifiedEntity();

        final RelatedEntity bpRelatedEntity = testEntity.getRelatedEntity();
        assertThat(bpRelatedEntity).isNotNull();

        final List<TransitiveEntity> bpTransitiveEntity = bpRelatedEntity.fetchTransitiveEntity();
        assertThat(bpTransitiveEntity).isNotNull();

        verify(getRequestedFor(urlPathMatching(SERVICE_B + "/A_RelatedEntitySet\\('(.*)'\\)/to_TransitiveEntity")));
    }

    @Test
    void testChildEntityFromUpdate()
    {
        final String responseEntity = createPayloadSingle(createTestEntity("72", null));
        stubFor(get(urlPathMatching(SERVICE_B + "/A_EntitySet\\('(.*)'\\)")).willReturn(okJson(responseEntity)));

        final String responseEntityWithRelated =
            createPayloadSingle(createTestEntity("72", createRelatedEntity("13857", null)));
        stubFor(
            patch(urlPathMatching(SERVICE_B + "/A_EntitySet\\('(.*)'\\)"))
                .willReturn(okJson(responseEntityWithRelated)));

        final String responseEntityListTransitive = createPayloadMultiple(createTransitiveEntity("Bar"));
        stubFor(
            get(urlPathMatching(SERVICE_B + "/A_RelatedEntitySet\\('(.*)'\\)/to_TransitiveEntity"))
                .willReturn(okJson(responseEntityListTransitive)));

        TestEntity testEntity =
            FluentHelperFactory
                .withServicePath(SERVICE_B)
                .readByKey(TestEntity.class, "A_EntitySet", Collections.singletonMap("EntityId", "72"))
                .executeRequest(destination);

        testEntity.setRelatedEntity(new RelatedEntity());

        testEntity =
            FluentHelperFactory
                .withServicePath(SERVICE_B)
                .update("A_EntitySet", testEntity)
                .modifyingEntity()
                .executeRequest(destination)
                .getModifiedEntity();

        final RelatedEntity bpRelatedEntity = testEntity.getRelatedEntity();
        assertThat(bpRelatedEntity).isNotNull();

        final List<TransitiveEntity> bpTransitiveEntity = bpRelatedEntity.fetchTransitiveEntity();
        assertThat(bpTransitiveEntity).isNotNull();

        verify(getRequestedFor(urlPathMatching(SERVICE_B + "/A_RelatedEntitySet\\('(.*)'\\)/to_TransitiveEntity")));
    }

    @Test
    void testChildEntityFromNavigationProperty()
    {
        final String responseEntity = createPayloadSingle(createTestEntity("72", null));
        stubFor(get(urlPathMatching(SERVICE_B + "/A_EntitySet\\('(.*)'\\)")).willReturn(okJson(responseEntity)));

        final String responseEntityWithRelated = createPayloadSingle(createRelatedEntity("13857", null));
        stubFor(
            get(urlPathMatching(SERVICE_B + "/A_EntitySet\\('(.*)'\\)/to_RelatedEntity"))
                .willReturn(okJson(responseEntityWithRelated)));

        final String responseEntityListTransitive = createPayloadMultiple(createTransitiveEntity("Bar"));
        stubFor(
            get(urlPathMatching(SERVICE_B + "/A_RelatedEntitySet\\('(.*)'\\)/to_TransitiveEntity"))
                .willReturn(okJson(responseEntityListTransitive)));

        final TestEntity testEntity =
            FluentHelperFactory
                .withServicePath(SERVICE_B)
                .readByKey(TestEntity.class, "A_EntitySet", Collections.singletonMap("EntityId", "72"))
                .executeRequest(destination);

        final RelatedEntity bpRelatedEntity = testEntity.fetchRelatedEntity();
        assertThat(bpRelatedEntity).isNotNull();

        final List<TransitiveEntity> bpTransitiveEntity = bpRelatedEntity.fetchTransitiveEntity();
        assertThat(bpTransitiveEntity).isNotNull();

        verify(getRequestedFor(urlPathMatching(SERVICE_B + "/A_RelatedEntitySet\\('(.*)'\\)/to_TransitiveEntity")));
    }

    @Test
    void testChildEntityFromFunctionImport()
        throws IOException
    {
        final String responseFunctionImport =
            createPayloadMultiple(createTransitiveEntity("100"), createTransitiveEntity("101"));
        stubFor(
            get(urlPathMatching(SERVICE_A + "/FunctionToMedia"))
                .withQueryParam("Param1", equalTo("'MARA'"))
                .withQueryParam("Param2", equalTo("'SAPTEST'"))
                .willReturn(okJson(responseFunctionImport)));

        stubFor(
            get(urlPathMatching(SERVICE_A + "/A_MediaEntitySet\\((.*)\\)/\\$value"))
                .willReturn(okForContentType("text/plain", "Test file content")));

        final Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("Param1", "MARA");
        parameters.put("Param2", "SAPTEST");

        final List<MediaEntity> attachments =
            FluentHelperFactory
                .withServicePath(SERVICE_A)
                .functionMultipleGet(parameters, "FunctionToMedia", MediaEntity.class)
                .executeRequest(destination);

        for( final MediaEntity attachmentContent : attachments ) {
            try( final InputStream attachmentMedia = attachmentContent.fetchMediaStream() ) {
                assertThat(attachmentMedia).isNotNull();
                assertThat(attachmentMedia.available()).isGreaterThan(0);
            }
        }
    }

    private static String createPayloadSingle( final String payload )
    {
        return format("{\"d\":%s}", payload);
    }

    private static String createPayloadMultiple( final String... payloads )
    {
        return createPayloadSingle(format("{\"results\":[%s]}", String.join(",", payloads)));
    }

    private static String createTestEntity( final String id, final String rel )
    {
        final String uri = format("https://127.0.0.1/path/to/serviceB/A_EntitySet('%s')", id);
        final String m = format("{\"id\":\"%s\",\"uri\":\"%s\",\"type\":\"ServiceB.A_EntityType\"}", uri, uri);
        final String r = rel != null ? rel : format("{\"__deferred\":{\"uri\":\"%s/to_RelatedEntity\"}}", uri);
        return format("{\"__metadata\":%s,\"EntityId\":\"%s\",\"to_RelatedEntity\":%s}", m, id, r);
    }

    private static String createRelatedEntity( final String id, final String trn )
    {
        final String uri = format("https://127.0.0.1/path/to/serviceB/A_RelatedEntitySet('%s')", id);
        final String m = format("{\"id\":\"%s\",\"uri\":\"%s\",\"type\":\"ServiceB.A_RelatedType\"}", uri, uri);
        final String t = trn != null ? trn : format("{\"__deferred\":{\"uri\":\"%s/to_TransitiveEntity\"}}", uri);
        return format("{\"__metadata\":%s,\"RelatedId\":\"%s\",\"to_TransitiveEntity\":%s}", m, id, t);
    }

    private static String createTransitiveEntity( final String id )
    {
        final String uri = format("https://127.0.0.1/path/to/serviceB/A_TransitiveSet('%s')", id);
        final String format =
            "{\"id\":\"%s\",\"uri\":\"%s\",\"type\":\"ServiceB.A_MediaType\",\"content_type\":\"text/plain\",\"media_src\":\"%s/$value\"}";
        return format("{\"__metadata\":%s,\"MediaId\":\"%s\"}", format(format, uri, uri, uri), id);
    }

    @Data
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class TestEntity extends VdmEntity<TestEntity>
    {
        private final String entityCollection = "A_EntitySet";
        private final Class<TestEntity> type = TestEntity.class;

        @SerializedName( "EntityId" )
        @JsonProperty( "EntityId" )
        @ODataField( odataName = "EntityId" )
        private String entityId;

        @SerializedName( "to_RelatedEntity" )
        @JsonProperty( "to_RelatedEntity" )
        @ODataField( odataName = "to_RelatedEntity" )
        private RelatedEntity relatedEntity;

        public static final TestEntityLink<RelatedEntity> TO_RELATED_ENTITY = new TestEntityLink<>("to_RelatedEntity");

        @Nonnull
        @Override
        protected Map<String, Object> getKey()
        {
            return ImmutableMap.of("EntityId", getEntityId());
        }

        @Nonnull
        @Override
        protected Map<String, Object> toMapOfNavigationProperties()
        {
            return Collections.singletonMap("to_RelatedEntity", relatedEntity);
        }

        public RelatedEntity fetchRelatedEntity()
        {
            return fetchFieldAsSingle("to_RelatedEntity", RelatedEntity.class);
        }

        public void setRelatedEntity( final RelatedEntity relatedEntity )
        {
            rememberChangedField("to_RelatedEntity", this.relatedEntity);
            this.relatedEntity = relatedEntity;
        }
    }

    @Data
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class RelatedEntity extends VdmEntity<RelatedEntity>
    {
        private final String entityCollection = "A_RelatedEntitySet";
        private final Class<RelatedEntity> type = RelatedEntity.class;

        @SerializedName( "RelatedId" )
        @JsonProperty( "RelatedId" )
        @ODataField( odataName = "RelatedId" )
        private String relatedId;

        @SerializedName( "to_TransitiveEntity" )
        @JsonProperty( "to_TransitiveEntity" )
        @ODataField( odataName = "to_TransitiveEntity" )
        private List<TransitiveEntity> transitiveEntity;

        public static final RelatedEntityLink<TransitiveEntity> TO_RELATED_ENTITY =
            new RelatedEntityLink<>("to_TransitiveEntity");

        @Nonnull
        @Override
        protected Map<String, Object> getKey()
        {
            return ImmutableMap.of("RelatedId", getRelatedId());
        }

        @Nonnull
        @Override
        protected Map<String, Object> toMapOfNavigationProperties()
        {
            return Collections.singletonMap("to_TransitiveEntity", transitiveEntity);
        }

        public List<TransitiveEntity> fetchTransitiveEntity()
        {
            return fetchFieldAsList("to_TransitiveEntity", TransitiveEntity.class);
        }
    }

    @Data
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class TransitiveEntity extends VdmEntity<TransitiveEntity>
    {
        private final String entityCollection = "A_TransitiveEntitySet";
        private final Class<TransitiveEntity> type = TransitiveEntity.class;
    }

    @Data
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class MediaEntity extends VdmMediaEntity<MediaEntity>
    {
        private final String entityCollection = "A_MediaEntitySet";
        private final Class<MediaEntity> type = MediaEntity.class;

        @SerializedName( "MediaId" )
        @JsonProperty( "MediaId" )
        @ODataField( odataName = "MediaId" )
        private String mediaId;

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

    public static class RelatedEntityLink<ObjectT extends VdmObject<ObjectT>>
        extends
        EntityLink<RelatedEntityLink<ObjectT>, TestEntity, ObjectT>
    {
        public RelatedEntityLink( final String name )
        {
            super(name);
        }
    }
}
