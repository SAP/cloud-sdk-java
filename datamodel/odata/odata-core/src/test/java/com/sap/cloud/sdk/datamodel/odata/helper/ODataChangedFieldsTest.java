/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.Ignore;
import org.junit.Test;

public class ODataChangedFieldsTest
{
    @Test
    public void testEmpty()
    {
        final TestVdmEntity testEntity = new TestVdmEntity();
        assertThat(testEntity.getChangedFields()).isEmpty();
    }

    @Test
    public void testBuilder()
    {
        final TestVdmEntity testEntity = TestVdmEntity.builder().stringValue("Foo").build();
        assertThat(testEntity.getChangedFields()).isEmpty();
    }

    @Test
    public void testAccessor()
    {
        final TestVdmEntity testEntity = new TestVdmEntity();
        testEntity.setStringValue("Foo");
        assertThat(testEntity.getChangedFields()).containsEntry("StringValue", "Foo");
    }

    @Test
    public void testAccessorNull()
    {
        final TestVdmEntity testEntity = new TestVdmEntity();
        testEntity.setDecimalValue(null);
        assertThat(testEntity.getChangedFields()).isEmpty();
    }

    @Test
    public void testCustomFields()
    {
        final TestVdmEntity testEntity = new TestVdmEntity();
        testEntity.setCustomField("BarValue", "Foo");
        assertThat(testEntity.getChangedFields()).containsEntry("BarValue", "Foo");
    }

    @Test
    public void testCustomFieldsNull()
    {
        final TestVdmEntity testEntity = new TestVdmEntity();
        testEntity.setCustomField("BarValue", null);
        assertThat(testEntity.getChangedFields()).isEmpty();
    }

    @Test
    public void testRevertingValue()
    {
        final TestVdmEntity testEntity = new TestVdmEntity();
        testEntity.setIntegerValue(9000);
        assertThat(testEntity.getChangedFields()).containsEntry("IntegerValue", 9000);

        testEntity.setIntegerValue(null);
        assertThat(testEntity.getChangedFields()).isEmpty();
    }

    @Test
    public void testComplexValue()
    {
        final TestVdmEntity testEntity = new TestVdmEntity();
        final TestVdmComplex complex1 = TestVdmComplex.builder().someValue("Foo").build();
        final TestVdmComplex complex2 = new TestVdmComplex();
        complex2.setSomeValue("Bar");

        testEntity.setComplexValue(complex1);
        assertThat(testEntity.getChangedFields()).containsOnly(entry("ComplexValue", complex1));

        testEntity.setComplexValue(complex2);
        assertThat(testEntity.getChangedFields()).containsOnly(entry("ComplexValue", complex2));
    }

    @Ignore( "Not yet implemented. See CLOUDECOSYSTEM-9065" )
    @Test
    public void testInnerComplexValue()
    {
        final TestVdmComplex complex = TestVdmComplex.builder().someValue("Tic").build();
        final TestVdmEntity testEntity = TestVdmEntity.builder().complexValue(complex).build();
        assertThat(testEntity.getChangedFields()).isEmpty();

        complex.setSomeValue("Tac");
        assertThat(testEntity.getChangedFields()).containsOnly(entry("ComplexValue", complex));
    }

    /**
     * Updating changes on related entities in navigation properties are not supported in OData V2 (aka Deep Update).
     */
    @Test
    public void testNavigationPropertyToOne()
    {
        final TestVdmEntity testEntity = new TestVdmEntity();
        final TestVdmEntity parent = TestVdmEntity.builder().stringValue("Foo").build();
        testEntity.setToParent(parent);
        assertThat(testEntity.getChangedFields()).isEmpty();
    }

    /**
     * Updating changes on related entities in navigation properties are not supported in OData V2 (aka Deep Update).
     */
    @Test
    public void testNavigationPropertyToMany()
    {
        final TestVdmEntity testEntity = new TestVdmEntity();
        final TestVdmEntity child1 = TestVdmEntity.builder().stringValue("Foo").build();
        testEntity.addToChildren(child1);
        assertThat(testEntity.getChangedFields()).isEmpty();
    }
}
