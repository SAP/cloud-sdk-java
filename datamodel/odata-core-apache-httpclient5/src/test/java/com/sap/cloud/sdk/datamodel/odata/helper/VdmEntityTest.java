package com.sap.cloud.sdk.datamodel.odata.helper;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;

class VdmEntityTest
{
    private static final String TEST_DEFAULT_SERVICE_PATH = "/odata/default";
    private static final String TEST_DESTINATION_NAME = "UnitTestDestination";

    private static HttpDestinationProperties TEST_DESTINATION;

    private static class TestEntity extends VdmEntity<TestEntity>
    {
        @Override
        protected String getEntityCollection()
        {
            return null;
        }

        @Nonnull
        @Override
        public Class<TestEntity> getType()
        {
            return TestEntity.class;
        }

        @Override
        protected String getDefaultServicePath()
        {
            return TEST_DEFAULT_SERVICE_PATH;
        }
    }

    @BeforeAll
    static void setUp()
    {
        TEST_DESTINATION = DefaultHttpDestination.builder("").name(TEST_DESTINATION_NAME).build();
    }

    @Test
    void testAttachToErpWithDefaults()
    {
        final TestEntity sut = new TestEntity();
        sut.attachToService(null, (HttpDestinationProperties) null);

        assertThat(sut.getServicePathForFetch()).isNotEmpty();
        assertThat(sut.getServicePathForFetch()).isEqualTo(TEST_DEFAULT_SERVICE_PATH);

        assertThat(sut.getDestinationForFetch()).isNull();
    }

    @Test
    void testAttachToErpWithCustomErpConfigContext()
    {
        final TestEntity sut = new TestEntity();
        sut.attachToService(null, TEST_DESTINATION);

        assertThat(sut.getServicePathForFetch()).isNotEmpty();
        assertThat(sut.getServicePathForFetch()).isEqualTo(TEST_DEFAULT_SERVICE_PATH);

        assertThat(sut.getDestinationForFetch()).isNotNull();
        assertThat(sut.getDestinationForFetch().get(DestinationProperty.NAME)).contains(TEST_DESTINATION_NAME);
    }

    @Test
    void testAttachToErpWithCustomServicePath()
    {
        final TestEntity sut = new TestEntity();
        sut.attachToService("/sap/opu/odata", (HttpDestinationProperties) null);

        assertThat(sut.getServicePathForFetch()).isNotEmpty();
        assertThat(sut.getServicePathForFetch()).isEqualTo("/sap/opu/odata");

        assertThat(sut.getDestinationForFetch()).isNull();
    }

    @Test
    void testAttachToErpWithAllCustomParameters()
    {
        final TestEntity sut = new TestEntity();
        sut.attachToService("/sap/opu/odata", TEST_DESTINATION);

        assertThat(sut.getServicePathForFetch()).isNotEmpty();
        assertThat(sut.getServicePathForFetch()).isEqualTo("/sap/opu/odata");

        assertThat(sut.getDestinationForFetch()).isNotNull();
        assertThat(sut.getDestinationForFetch().get(DestinationProperty.NAME)).contains(TEST_DESTINATION_NAME);
    }

    @Test
    void testEntityComparison()
    {
        final TestVdmEntity foo1 = TestVdmEntity.builder().stringValue("foo").build();
        final TestVdmEntity foo2 = TestVdmEntity.builder().stringValue("foo").build();
        assertThat(foo1)
            .withFailMessage(
                "Entities wit equal properties should be equal. Expected:\n %s\n to be equal to:\n %s\nbut was not.",
                foo1,
                foo2)
            .isEqualTo(foo2);

        foo1.setVersionIdentifier("1");
        foo2.setVersionIdentifier("1");
        assertThat(foo1).withFailMessage("Equal entities with equal ETags should be equal.").isEqualTo(foo2);

        foo1.setServicePathForFetch("bar");
        foo2.setServicePathForFetch("baz");
        assertThat(foo1)
            .withFailMessage("Equal entities with different service paths should be equal.")
            .isEqualTo(foo2);

        foo1.setDestinationForFetch(DefaultHttpDestination.builder("bar").build());
        foo2.setDestinationForFetch(DefaultHttpDestination.builder("baz").build());
        assertThat(foo1)
            .withFailMessage("Equal entities with different service paths should be equal.")
            .isEqualTo(foo2);

        foo2.setVersionIdentifier("2");
        assertThat(foo1).withFailMessage("Equal entities with different ETags should not be equal.").isNotEqualTo(foo2);
    }

    @Test
    void testChangedNonCustomFields()
    {
        final TestVdmEntity entity = TestVdmEntity.builder().stringValue("old").build();

        assertThat(entity.getChangedFields()).isEmpty();

        entity.setStringValue("old");
        assertThat(entity.getChangedFields()).isEmpty();

        entity.setStringValue("new");
        assertThat(entity.getChangedFields()).containsOnlyKeys("StringValue");
    }

    @Test
    void testChangedCustomFields()
    {
        final TestVdmEntity entity = TestVdmEntity.builder().build();

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
