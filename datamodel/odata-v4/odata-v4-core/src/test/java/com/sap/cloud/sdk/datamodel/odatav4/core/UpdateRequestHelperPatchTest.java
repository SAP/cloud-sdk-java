/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.assertj.core.util.Lists;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UpdateRequestHelperPatchTest
{
    private static final String TEST_DEFAULT_SERVICE_PATH = "/odata/default";

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonAdapter( com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class )
    private static class TestEntity extends VdmEntity<TestEntity>
    {
        @Getter
        private final String odataType = "TestEntity";

        @Getter
        private final String entityCollection = "EntityParentCollection";

        @Getter
        private final String defaultServicePath = TEST_DEFAULT_SERVICE_PATH;

        @Getter
        private final Class<TestEntity> type = TestEntity.class;

        @Getter
        @ElementName( "Neighbor" )
        private TestEntity neighbor;

        @Getter
        @ElementName( "Value" )
        private BigDecimal value;

        @Getter
        @ElementName( "Emails" )
        private List<String> emails;

        @Getter
        @ElementName( "Name" )
        private String name;

        @Getter
        @ElementName( "EffortValue" )
        private Double effortValue;

        @Getter
        @ElementName( "ShoeSize" )
        private Integer shoeSize;

        @Getter
        @ElementName( "IsRetired" )
        private Boolean isRetired;

        @Getter
        @ElementName( "BirthDate" )
        private LocalDate birthDate;

        @Getter
        @ElementName( "FavouriteCharacter" )
        private Character favouriteCharacter;

        @Getter
        @ElementName( "ComplexProperty" )
        private TestComplex complexProperty;

        @Getter
        @ElementName( "ComplexProperties" )
        private Collection<TestComplex> complexProperties;

        @Nonnull
        @Override
        protected ODataEntityKey getKey()
        {
            final ODataEntityKey key = new ODataEntityKey(ODataProtocol.V4);
            key.addKeyProperty("Name", name);
            return key;
        }

        public void setNeighbor( TestEntity neighbor )
        {
            rememberChangedField("Neighbor", this.neighbor);
            this.neighbor = neighbor;
        }

        public void setName( String name )
        {
            rememberChangedField("Name", this.name);
            this.name = name;
        }

        public void setEffortValue( Double effortValue )
        {
            rememberChangedField("EffortValue", this.effortValue);
            this.effortValue = effortValue;
        }

        public void setValue( BigDecimal value )
        {
            rememberChangedField("Value", this.value);
            this.value = value;
        }

        public void setEmails( List<String> emails )
        {
            rememberChangedField("Emails", this.emails);
            this.emails = emails;
        }

        public void setShoeSize( Integer shoeSize )
        {
            rememberChangedField("ShoeSize", this.shoeSize);
            this.shoeSize = shoeSize;
        }

        public void setIsRetired( Boolean isRetired )
        {
            rememberChangedField("IsRetired", this.isRetired);
            this.isRetired = isRetired;
        }

        public void setBirthDate( LocalDate birthDate )
        {
            rememberChangedField("BirthDate", this.birthDate);
            this.birthDate = birthDate;
        }

        public void setFavouriteCharacter( Character favouriteCharacter )
        {
            rememberChangedField("FavouriteCharacter", this.favouriteCharacter);
            this.favouriteCharacter = favouriteCharacter;
        }

        public void setComplexProperty( TestComplex complexProperty )
        {
            rememberChangedField("ComplexProperty", this.complexProperty);
            this.complexProperty = complexProperty;
        }

        public void setComplexProperties( Collection<TestComplex> complexProperties )
        {
            rememberChangedField("ComplexProperties", this.complexProperties);
            this.complexProperties = complexProperties;
        }

        @Nonnull
        @Override
        protected Map<String, Object> toMapOfFields()
        {
            final Map<String, Object> result = new HashMap<>();
            result.put("Name", name);
            result.put("Value", value);
            result.put("Neighbor", neighbor);
            result.put("Emails", emails);
            result.put("EffortValue", effortValue);
            result.put("ShoeSize", shoeSize);
            result.put("IsRetired", isRetired);
            result.put("BirthDate", birthDate);
            result.put("FavouriteCharacter", favouriteCharacter);
            result.put("ComplexProperty", complexProperty);
            result.put("ComplexProperties", complexProperties);
            return result;
        }
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestComplex extends VdmComplex<TestComplex>
    {
        @Getter
        private final String odataType = "TestComplex";

        @Getter
        @ElementName( "StringProperty" )
        private String stringProperty;

        @Getter
        @ElementName( "ComplexProperty" )
        private TestComplex complexProperty;

        @Nonnull
        @Override
        public Class<TestComplex> getType()
        {
            return TestComplex.class;
        }

        @Nonnull
        @Override
        protected Map<String, Object> toMapOfFields()
        {
            final Map<java.lang.String, Object> values = super.toMapOfFields();
            values.put("StringProperty", getStringProperty());
            values.put("ComplexProperty", getComplexProperty());
            return values;
        }

        public void setStringProperty( String stringProperty )
        {
            rememberChangedField("StringProperty", this.stringProperty);
            this.stringProperty = stringProperty;
        }

        public void setComplexProperty( TestComplex complexProperty )
        {
            rememberChangedField("ComplexProperty", this.complexProperty);
            this.complexProperty = complexProperty;
        }
    }

    @Test
    public void testSimpleBigDecimalPropertyPatchPayload()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").build();

        entity.setValue(new BigDecimal("0.00000000001"));

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"Value\":0.00000000001,\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testSimpleDoublePropertyPatchPayload()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").build();

        entity.setEffortValue(42d);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"EffortValue\":42,\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testSimpleIntegerPropertyPatchPayload()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").build();

        entity.setShoeSize(46);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"ShoeSize\":46,\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testSimpleStringPropertyPatchPayload()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").build();

        entity.setName("Bar");

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"Name\":\"Bar\",\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testSimpleLocalDatePropertyPatchPayload()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").build();

        entity.setBirthDate(LocalDate.MIN);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals(
            "{\"BirthDate\":\"-999999999-01-01\",\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testSimpleSingleCharacterDatePropertyPatchPayload()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").build();

        entity.setFavouriteCharacter('A');

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"FavouriteCharacter\":\"A\",\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testSimpleBooleanPropertyPatchPayload()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").build();

        entity.setIsRetired(Boolean.FALSE);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"IsRetired\":false,\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testDirectSimpleCollectionPropertyPatchPayload()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").build();

        entity.setEmails(Lists.newArrayList("foo@sap.com"));

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"Emails\":[\"foo@sap.com\"],\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Ignore( "Indirect changes on entity properties are not tracked. CLOUDECOSYSTEM-8217" )
    //    @Test
    public void testIndirectCollectionPropertyViaGetPatchPayload()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().name("Foo").emails(Lists.newArrayList()).build();

        entity.getEmails().add("foo@sap.com");

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"Emails\":[\"foo@sap.com\"],\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testNavigationPropertyExistingPatchPayload()
        throws Exception
    {
        final TestEntity entityFoo = TestEntity.builder().name("Foo").value(BigDecimal.ZERO).build();
        final TestEntity entityBar = TestEntity.builder().name("Bar").value(BigDecimal.ONE).build();

        entityFoo.setNeighbor(entityBar);

        final String json = new UpdateRequestHelperPatch().toJson(entityFoo, Collections.emptySet());
        assertEquals(
            "{\"Neighbor\":{\"@id\":\"EntityParentCollection('Bar')\"},\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testNavigationPropertyNewPatchPayload()
        throws Exception
    {
        final TestEntity entityFoo = TestEntity.builder().name("Foo").value(BigDecimal.ZERO).build();

        final TestEntity entityBar = new TestEntity();
        entityBar.setName("Bar");
        entityBar.setValue(BigDecimal.ONE);

        entityFoo.setNeighbor(entityBar);

        final String json = new UpdateRequestHelperPatch().toJson(entityFoo, Collections.emptySet());

        assertEquals(
            "{\"Neighbor\":{\"Value\":1,\"Name\":\"Bar\"},\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testNavigationPropertyChangePatchPayload()
        throws Exception
    {
        final TestEntity entityFoo = TestEntity.builder().name("Foo").value(BigDecimal.ZERO).build();
        final TestEntity entityBar = TestEntity.builder().name("Bar").build();
        entityBar.setValue(BigDecimal.ONE);
        entityFoo.setNeighbor(entityBar);

        final String json = new UpdateRequestHelperPatch().toJson(entityFoo, Collections.emptySet());

        assertEquals(
            "{\"Neighbor\":{\"Value\":1,\"@id\":\"EntityParentCollection('Bar')\"},\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testNavigationPropertyCyclicPatchPayload()
        throws Exception
    {
        final TestEntity entityBar = new TestEntity();
        entityBar.setName("Bar");

        final TestEntity entityFoo = new TestEntity();
        entityFoo.setName("Foo");
        entityFoo.setNeighbor(entityBar);
        entityBar.setNeighbor(entityFoo);

        final String json = new UpdateRequestHelperPatch().toJson(entityFoo, Collections.emptySet());
        assertEquals(
            "{"
                + "\"Neighbor\":{"
                + "  \"Neighbor\":{\"@id\":\"EntityParentCollection('Foo')\"},"
                + "  \"Name\":\"Bar\""
                + "},"
                + "\"Name\":\"Foo\","
                + "\"@odata.type\":\"#TestEntity\""
                + "}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testSetComplexMember()
        throws Exception
    {
        final TestComplex complex = TestComplex.builder().stringProperty("Foo").build();
        complex.setCustomField("customField", "customValue");

        final TestEntity entity = TestEntity.builder().build();
        entity.setComplexProperty(complex);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals(
            "{\"ComplexProperty\":{\"StringProperty\":\"Foo\",\"customField\":\"customValue\"},\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testRemoveComplexMember()
        throws Exception
    {
        final TestEntity entity =
            TestEntity.builder().complexProperty(TestComplex.builder().stringProperty("Foo").build()).build();
        entity.setComplexProperty(null);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"ComplexProperty\":null,\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testUpdateFieldOfComplexMember()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().complexProperty(new TestComplex()).build();
        entity.getComplexProperty().setStringProperty("Foo");

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals(
            "{\"ComplexProperty\":{\"StringProperty\":\"Foo\"},\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testRemoveFieldOfComplexMember()
        throws Exception
    {
        final TestComplex complex = TestComplex.builder().stringProperty("Foo").build();
        complex.setCustomField("customField", "customValue");
        final TestEntity entity = TestEntity.builder().complexProperty(complex).build();
        entity.getComplexProperty().setStringProperty(null);
        entity.getComplexProperty().setCustomField("customField", null);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals(
            "{\"ComplexProperty\":{\"StringProperty\":null,\"customField\":null},\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testUpdateFieldOfNestedComplexMember()
        throws Exception
    {
        final TestComplex complexChild = new TestComplex();
        final TestComplex complexParent =
            TestComplex.builder().stringProperty("Parent").complexProperty(complexChild).build();
        final TestEntity entity = TestEntity.builder().complexProperty(complexParent).build();
        complexChild.setStringProperty("Hello, World!");

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals(
            "{\"ComplexProperty\":{\"StringProperty\":\"Parent\",\"ComplexProperty\":{\"StringProperty\":\"Hello, World!\"}},\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testRemoveFieldOfNestedComplexMember()
        throws Exception
    {
        final TestComplex complexChild = TestComplex.builder().stringProperty("Child").build();
        final TestComplex complexParent =
            TestComplex.builder().stringProperty("Parent").complexProperty(complexChild).build();
        final TestEntity entity = TestEntity.builder().complexProperty(complexParent).build();
        entity.getComplexProperty().getComplexProperty().setStringProperty(null);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals(
            "{\"ComplexProperty\":{\"StringProperty\":\"Parent\",\"ComplexProperty\":{\"StringProperty\":null}},\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testSetNullableCollectionToEmpty()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().build();
        assertThat(entity.getComplexProperties()).isNull();
        entity.setComplexProperties(new ArrayList<>());

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"ComplexProperties\":[],\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testSetEmptyToCollectionToEmpty()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().complexProperties(new ArrayList<>()).build();
        entity.setComplexProperties(new ArrayList<>());

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testRemoveCollection()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().complexProperties(new ArrayList<>()).build();
        entity.setComplexProperties(null);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"ComplexProperties\":null,\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }

    @Test
    public void testSetCollectionOfComplexProperties()
        throws Exception
    {
        final TestComplex complex = TestComplex.builder().stringProperty("Foo").build();
        complex.setCustomField("customField", "customValue");

        final TestEntity entity = TestEntity.builder().complexProperties(new ArrayList<>()).build();
        entity.setComplexProperties(Collections.singletonList(complex));

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals(
            "{\"ComplexProperties\":[{\"StringProperty\":\"Foo\",\"ComplexProperty\":null,\"customField\":\"customValue\"}],\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testUpdateComplexMemberInCollection()
        throws Exception
    {
        final TestComplex collectionMember = new TestComplex();
        final TestEntity entity = TestEntity.builder().complexProperties(Lists.newArrayList(collectionMember)).build();
        collectionMember.setStringProperty("Foo");
        collectionMember.setCustomField("customField", "customValue");

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals(
            "{\"ComplexProperties\":[{\"StringProperty\":\"Foo\",\"customField\":\"customValue\"}],\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Test
    public void testRemoveFieldOfComplexMemberInCollection()
        throws Exception
    {
        final TestComplex collectionMember = TestComplex.builder().stringProperty("Foo").build();
        final TestEntity entity = TestEntity.builder().complexProperties(Lists.newArrayList(collectionMember)).build();
        collectionMember.setStringProperty(null);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals(
            "{\"ComplexProperties\":[{\"StringProperty\":null}],\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Ignore( "Tracking changes within a collection of complex properties is not yet supported. CLOUDECOSYSTEM-8217" )
    //    @Test
    public void testAddMemberToComplexCollection()
        throws Exception
    {
        final TestEntity entity = TestEntity.builder().complexProperties(new ArrayList<>()).build();
        entity.getComplexProperties().add(TestComplex.builder().stringProperty("Foo").build());

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals(
            "{\"ComplexProperties\":[{\"StringProperty\":\"Foo\", \"ComplexProperty\":{}}],\"@odata.type\":\"#TestEntity\"}",
            json,
            JSONCompareMode.LENIENT);
    }

    @Ignore( "Tracking changes within a collection of complex properties is not yet supported. CLOUDECOSYSTEM-8217" )
    //    @Test
    public void testRemoveMemberFromComplexCollection()
        throws Exception
    {
        final TestComplex collectionMember = TestComplex.builder().stringProperty("Foo").build();
        final TestEntity entity =
            TestEntity.builder().complexProperties(Collections.singletonList(collectionMember)).build();
        entity.getComplexProperties().remove(collectionMember);

        final String json = new UpdateRequestHelperPatch().toJson(entity, Collections.emptySet());
        assertEquals("{\"ComplexProperties\":[],\"@odata.type\":\"#TestEntity\"}", json, JSONCompareMode.LENIENT);
    }
}
