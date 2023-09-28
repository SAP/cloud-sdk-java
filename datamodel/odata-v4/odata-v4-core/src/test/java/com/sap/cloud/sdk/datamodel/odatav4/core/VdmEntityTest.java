package com.sap.cloud.sdk.datamodel.odatav4.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.Test;

import io.vavr.control.Option;

public class VdmEntityTest
{
    @Test
    public void testModifications()
    {
        final TestEntity entity = new TestEntity();
        assertThat(entity).isEqualTo(new TestEntity());
        assertThat(entity.getVersionIdentifier()).isEqualTo(Option.none());

        entity.setCustomField("foo", "bar");

        final SimpleProperty<TestEntity> customField = () -> "fizz";
        entity.setCustomField(customField, "buzz");

        assertThat(entity.getCustomFields()).containsEntry("foo", "bar").containsEntry("fizz", "buzz");
        assertThat(entity.<String> getCustomField("foo")).isEqualTo("bar");
        assertThat(entity.<String> getCustomField(customField)).isEqualTo("buzz");
        assertThat(entity.hasCustomField("fizz")).isTrue();
        assertThat(entity.hasCustomField(customField)).isTrue();
        assertThat(entity.getCustomFieldNames()).containsExactly("foo", "fizz");

        entity.fromMap(Collections.singletonMap("foo", "barbar"));
        assertThat(entity.<String> getCustomField("foo")).isEqualTo("barbar");

        assertThat(entity.toString()).isNotNull();
    }

    @Test
    public void testChangedNonCustomFields()
    {
        final TestEntity entity = TestEntity.builder().id("old").build();

        assertThat(entity.getChangedFields()).isEmpty();

        entity.setId("old");
        assertThat(entity.getChangedFields()).isEmpty();

        entity.setId("new");
        assertThat(entity.getChangedFields()).containsOnlyKeys("id");
    }

    @Test
    public void testChangedCustomFields()
    {
        final TestEntity entity = TestEntity.builder().id("id").build();

        assertThat(entity.getChangedFields()).isEmpty();

        entity.setCustomField("foo", "bar");
        assertThat(entity.getChangedFields()).containsOnlyKeys("foo");

        entity.resetChangedFields();
        entity.setCustomField("foo", "bar");
        assertThat(entity.getChangedFields()).isEmpty();

        entity.setCustomField("foo", "baz");
        assertThat(entity.getChangedFields()).containsOnlyKeys("foo");
    }
}
