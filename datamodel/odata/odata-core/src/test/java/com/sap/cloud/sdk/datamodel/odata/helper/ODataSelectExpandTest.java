/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

class ODataSelectExpandTest
{
    @Test
    void testSelectExpand()
    {
        final FluentHelperRead<?, TestEntity, TestEntitySelectable> entityRead =
            FluentHelperFactory.withServicePath("service/path/").read(TestEntity.class, "TestEntity");
        entityRead.select(TestEntity.FOO, TestEntity.TO_PARENT, TestEntity.TO_CHILDREN.select(TestEntity.BAR));

        final ODataRequestRead requestRead = entityRead.toRequest();

        assertThat(requestRead.getQueryString())
            .isEqualTo("$select=Foo,to_Parent/*,to_Children/Bar&$expand=to_Parent,to_Children");

        assertThat(requestRead.getRelativeUri())
            .hasPath("/service/path/TestEntity")
            .hasQuery("$select=Foo,to_Parent/*,to_Children/Bar&$expand=to_Parent,to_Children");
    }

    @Test
    void testSelectSome()
    {
        final FluentHelperRead<?, TestEntity, TestEntitySelectable> entityRead =
            FluentHelperFactory.withServicePath("service/path/").read(TestEntity.class, "TestEntity");
        entityRead.select(TestEntity.FOO, TestEntity.BAR, TestEntity.FOO);

        final ODataRequestRead requestRead = entityRead.toRequest();

        assertThat(requestRead.getQueryString()).isEqualTo("$select=Foo,Bar");

        assertThat(requestRead.getRelativeUri()).hasPath("/service/path/TestEntity").hasQuery("$select=Foo,Bar");
    }

    @Test
    void testSelectAll()
    {
        final FluentHelperRead<?, TestEntity, TestEntitySelectable> entityRead =
            FluentHelperFactory.withServicePath("service/path/").read(TestEntity.class, "TestEntity");
        entityRead.select(TestEntity.ALL_FIELDS);

        final ODataRequestRead requestRead = entityRead.toRequest();

        assertThat(requestRead.getQueryString()).isEqualTo("$select=*");

        assertThat(requestRead.getRelativeUri()).hasPath("/service/path/TestEntity").hasQuery("$select=*");
    }

    @Test
    void testExpand()
    {
        final FluentHelperRead<?, TestEntity, TestEntitySelectable> entityRead =
            FluentHelperFactory.withServicePath("service/path/").read(TestEntity.class, "TestEntity");
        entityRead.select(TestEntity.TO_PARENT, TestEntity.TO_CHILDREN.select(TestEntity.TO_PARENT));

        final ODataRequestRead requestRead = entityRead.toRequest();

        assertThat(requestRead.getQueryString())
            .isEqualTo("$select=to_Parent/*,to_Children/to_Parent/*&$expand=to_Parent,to_Children/to_Parent");

        assertThat(requestRead.getRelativeUri())
            .hasPath("/service/path/TestEntity")
            .hasQuery("$select=to_Parent/*,to_Children/to_Parent/*&$expand=to_Parent,to_Children/to_Parent");
    }

    // full set of generated VDM classes for TestEntity

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString( doNotUseGetters = true, callSuper = true )
    @EqualsAndHashCode( doNotUseGetters = true, callSuper = true )
    @JsonAdapter( com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class )
    public static class TestEntity extends VdmEntity<TestEntity>
    {
        public final static TestEntitySelectable ALL_FIELDS = new TestEntitySelectable()
        {
            @Getter
            public String fieldName = "*";
            @Getter
            public List<String> selections = Collections.singletonList("*");
        };

        public static final TestEntitySelectableOneToOneLink<TestEntity> TO_PARENT =
            new TestEntitySelectableOneToOneLink<>("to_Parent");
        public static final TestEntitySelectableLink<TestEntity> TO_CHILDREN =
            new TestEntitySelectableLink<>("to_Children");
        public static final TestEntitySelectableField<String> FOO = new TestEntitySelectableField<>("Foo");
        public static final TestEntitySelectableField<String> BAR = new TestEntitySelectableField<>("Bar");

        @Getter
        final String entityCollection = "TestEntity";

        @Getter
        private final String defaultServicePath = "/";

        @Getter
        private final Class<TestEntity> type = TestEntity.class;

        @SerializedName( "to_Parent" )
        @JsonProperty( "to_Parent" )
        @ODataField( odataName = "to_Parent" )
        @Nullable
        @Getter( AccessLevel.NONE )
        @Setter( AccessLevel.NONE )
        private TestEntity toParent;

        @SerializedName( "to_Children" )
        @JsonProperty( "to_Children" )
        @ODataField( odataName = "to_Children" )
        @Getter( AccessLevel.NONE )
        @Setter( AccessLevel.NONE )
        private List<TestEntity> toChildren;

        @SerializedName( "Foo" )
        @JsonProperty( "Foo" )
        @Nullable
        @ODataField( odataName = "Foo" )
        private String foo;

        @SerializedName( "Bar" )
        @JsonProperty( "Bar" )
        @Nullable
        @ODataField( odataName = "Bar" )
        private String bar;
    }

    private interface TestEntitySelectable extends EntitySelectable<TestEntity>
    {
    }

    private static class TestEntitySelectableField<FieldT> extends EntityField<TestEntity, FieldT>
        implements
        TestEntitySelectable
    {
        private TestEntitySelectableField( @Nonnull final String fieldName )
        {
            super(fieldName);
        }
    }

    private static class TestEntitySelectableLink<ObjectT extends VdmObject<?>>
        extends
        EntityLink<TestEntitySelectableLink<ObjectT>, TestEntity, ObjectT>
        implements
        TestEntitySelectable
    {
        private TestEntitySelectableLink( final String fieldName )
        {
            super(fieldName);
        }

        private TestEntitySelectableLink(
            final EntityLink<TestEntitySelectableLink<ObjectT>, TestEntity, ObjectT> toClone )
        {
            super(toClone);
        }

        @Nonnull
        @Override
        protected TestEntitySelectableLink<ObjectT> translateLinkType(
            final EntityLink<TestEntitySelectableLink<ObjectT>, TestEntity, ObjectT> link )
        {
            return new TestEntitySelectableLink<ObjectT>(link);
        }
    }

    private static class TestEntitySelectableOneToOneLink<ObjectT extends VdmObject<?>>
        extends
        TestEntitySelectableLink<ObjectT>
        implements
        OneToOneLink<TestEntity, ObjectT>
    {
        private TestEntitySelectableOneToOneLink( final String fieldName )
        {
            super(fieldName);
        }

        @Nonnull
        @Override
        public ExpressionFluentHelper<TestEntity> filter(
            @Nonnull final ExpressionFluentHelper<ObjectT> filterExpression )
        {
            return super.filterOnOneToOneLink(filterExpression);
        }
    }
}
