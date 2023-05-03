package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;
import com.sap.cloud.sdk.datamodel.odata.helper.CollectionValuedFluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;
import com.sap.cloud.sdk.datamodel.odata.helper.SingleValuedFluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.TestVdmEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class TestVdmEntityBatch extends BatchFluentHelperBasic<TestVdmEntityBatch, TestVdmEntityBatch.TestVdmEntityChangeset>
{
    @Getter
    private final String servicePathForBatchRequest;

    {
        final AtomicInteger uuidCounter = new AtomicInteger();
        uuidProvider = () -> new UUID(0, uuidCounter.incrementAndGet());
    }

    @Nonnull
    @Override
    protected TestVdmEntityBatch getThis()
    {
        return this;
    }

    @Nonnull
    @Override
    public TestVdmEntityChangeset beginChangeSet()
    {
        return new TestVdmEntityChangeset(this);
    }

    static class TestVdmEntityChangeset
        extends
        BatchChangeSetFluentHelperBasic<TestVdmEntityBatch, TestVdmEntityChangeset>
    {
        public TestVdmEntityChangeset( final TestVdmEntityBatch batch )
        {
            super(batch, batch);
        }

        @Nonnull
        @Override
        protected TestVdmEntityChangeset getThis()
        {
            return this;
        }

        TestVdmEntityChangeset create( final TestVdmEntity obj )
        {
            return addRequestCreate(TestEntityCreate::new, obj);
        }

        TestVdmEntityChangeset update( final TestVdmEntity obj )
        {
            return addRequestUpdate(TestEntityUpdate::new, obj);
        }

        TestVdmEntityChangeset delete( final TestVdmEntity obj )
        {
            return addRequestDelete(TestEntityDelete::new, obj);
        }
    }

    static class TestEntityByKey
        extends
        FluentHelperByKey<TestEntityByKey, TestVdmEntity, EntitySelectable<TestVdmEntity>>
    {
        @Getter
        private final Map<String, Object> key;

        @SuppressWarnings( "deprecation" )
        public TestEntityByKey( final Map<String, Object> key )
        {
            super("", TestVdmEntity.builder().build().getEntityCollection());
            this.key = key;
        }

        @Nonnull
        @Override
        protected Class<TestVdmEntity> getEntityClass()
        {
            return TestVdmEntity.class;
        }
    }

    static class TestEntityRead extends FluentHelperRead<TestEntityRead, TestVdmEntity, TestVdmEntitySelectable>
    {
        @SuppressWarnings( "deprecation" )
        public TestEntityRead()
        {
            super("", TestVdmEntity.builder().build().getEntityCollection());
        }

        @Nonnull
        @Override
        protected Class<TestVdmEntity> getEntityClass()
        {
            return TestVdmEntity.class;
        }
    }

    static class TestEntityDelete extends FluentHelperDelete<TestEntityDelete, TestVdmEntity>
    {
        @Getter
        private final TestVdmEntity entity;

        @SuppressWarnings( "deprecation" )
        TestEntityDelete( final TestVdmEntity entity )
        {
            super("", entity.getEntityCollection());
            this.entity = entity;
        }
    }

    static class TestEntityUpdate extends FluentHelperUpdate<TestEntityUpdate, TestVdmEntity>
    {
        @Getter
        private final TestVdmEntity entity;

        @SuppressWarnings( "deprecation" )
        TestEntityUpdate( final TestVdmEntity entity )
        {
            super("", entity.getEntityCollection());
            this.entity = entity;
        }
    }

    static class TestEntityCreate extends FluentHelperCreate<TestEntityCreate, TestVdmEntity>
    {
        @Getter
        private final TestVdmEntity entity;

        @SuppressWarnings( "deprecation" )
        TestEntityCreate( final TestVdmEntity entity )
        {
            super("", entity.getEntityCollection());
            this.entity = entity;
        }
    }

    static class TestFunctionImportSingleResultHttpGet
        extends
        SingleValuedFluentHelperFunction<TestFunctionImportSingleResultHttpGet, String, String>
    {
        private final Map<String, Object> values = new HashMap<>();

        @Nonnull
        @Override
        protected Class<? extends String> getEntityClass()
        {
            return String.class;
        }

        TestFunctionImportSingleResultHttpGet( @Nonnull final String firstName, @Nonnull final String lastName )
        {
            super("");
            values.put("FirstName", firstName);
            values.put("LastName", lastName);
        }

        @Nullable
        @Override
        public String executeRequest( @Nonnull final HttpDestinationProperties destination )
        {
            return "awesomeStuff";
        }

        @Nonnull
        @Override
        protected Map<String, Object> getParameters()
        {
            return values;
        }

        @Nonnull
        @Override
        protected String getFunctionName()
        {
            return "awesomeFunction";
        }

        @Nonnull
        @Override
        protected HttpUriRequest createRequest( @Nonnull final URI uri )
        {
            return new HttpGet(uri);
        }
    }

    static class TestFunctionImportCollectionResultHttpGet
        extends
        CollectionValuedFluentHelperFunction<TestFunctionImportSingleResultHttpGet, String, List<String>>
    {
        private final Map<String, Object> values = new HashMap<>();

        @Nonnull
        @Override
        protected Class<? extends String> getEntityClass()
        {
            return String.class;
        }

        TestFunctionImportCollectionResultHttpGet()
        {
            super("");
        }

        @Nullable
        @Override
        public List<String> executeRequest( @Nonnull final HttpDestinationProperties destination )
        {
            return Arrays.asList("foo", "bar");
        }

        @Nonnull
        @Override
        protected Map<String, Object> getParameters()
        {
            return values;
        }

        @Nonnull
        @Override
        protected String getFunctionName()
        {
            return "awesomeFunction";
        }

        @Nonnull
        @Override
        protected HttpUriRequest createRequest( @Nonnull final URI uri )
        {
            return new HttpGet(uri);
        }
    }

    static class TestFunctionImportSingleEntityResultHttpGet
        extends
        SingleValuedFluentHelperFunction<TestFunctionImportSingleEntityResultHttpGet, TestVdmEntity, TestVdmEntity>
    {
        private final Map<String, Object> values = new HashMap<>();

        @Nonnull
        @Override
        protected Class<? extends TestVdmEntity> getEntityClass()
        {
            return TestVdmEntity.class;
        }

        TestFunctionImportSingleEntityResultHttpGet(
            @Nonnull final int integerValue,
            @Nonnull final String stringValue )
        {
            super("");
            values.put("IntegerValue", integerValue);
            values.put("StringValue", stringValue);
        }

        @Nullable
        @Override
        public TestVdmEntity executeRequest( @Nonnull final HttpDestinationProperties destination )
        {
            return TestVdmEntity
                .builder()
                .integerValue((int) values.get("IntegerValue"))
                .stringValue((String) values.get("StringValue"))
                .build();
        }

        @Nonnull
        @Override
        protected Map<String, Object> getParameters()
        {
            return values;
        }

        @Nonnull
        @Override
        protected String getFunctionName()
        {
            return "awesomeFunction";
        }

        @Nonnull
        @Override
        protected HttpUriRequest createRequest( @Nonnull final URI uri )
        {
            return new HttpGet(uri);
        }
    }

    static class TestFunctionImportHttpPost
        extends
        FluentHelperFunction<TestFunctionImportHttpPost, TestVdmEntity, String>
    {
        private final Map<String, Object> values = new HashMap<>();

        TestFunctionImportHttpPost( @Nonnull final String firstName, @Nonnull final String lastName )
        {
            super("");
            values.put("FirstName", firstName);
            values.put("LastName", lastName);
        }

        @Nonnull
        @Override
        protected Class<? extends TestVdmEntity> getEntityClass()
        {
            return TestVdmEntity.class;
        }

        @Nullable
        @Override
        public String executeRequest( @Nonnull final HttpDestinationProperties destination )
        {
            return "awesomeStuff";
        }

        @Nonnull
        @Override
        protected Map<String, Object> getParameters()
        {
            return values;
        }

        @Nonnull
        @Override
        protected String getFunctionName()
        {
            return "awesomeFunction";
        }

        @Nonnull
        @Override
        protected HttpUriRequest createRequest( @Nonnull final URI uri )
        {
            return new HttpPost(uri);
        }
    }

    interface TestVdmEntitySelectable extends EntitySelectable<TestVdmEntity>
    {

    }
}
