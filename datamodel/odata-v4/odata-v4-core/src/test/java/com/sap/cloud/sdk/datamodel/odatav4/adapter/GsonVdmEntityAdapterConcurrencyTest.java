package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;

/**
 * The {@link Field} objects held by the adapter are shared between all threads that use the same {@link Gson} instance.
 * Toggling their accessibility on every read or write lets one thread restore "not accessible" while another thread is
 * about to read the field, which fails intermittently with an {@link IllegalAccessException}.
 */
class GsonVdmEntityAdapterConcurrencyTest
{
    @Test
    void testSharedFieldsStayAccessibleAfterSerializationAndDeserialization()
        throws Exception
    {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        final Person person = Person.builder().userName("user").firstName("First").lastName("Last").build();

        final GsonVdmEntityAdapter<Person> adapter =
            new GsonVdmEntityAdapter<>(new GsonVdmAdapterFactory(), gson, Person.class);

        final String json = adapter.toJson(person);
        final Person deserialized = (Person) adapter.fromJson(json);

        assertThat(json).contains("\"LastName\":\"Last\"");
        assertThat(deserialized.getLastName()).isEqualTo("Last");

        final Field entityPropertiesField = adapter.getClass().getDeclaredField("entityProperties");
        entityPropertiesField.setAccessible(true);
        final Map<?, ?> entityProperties = (Map<?, ?>) entityPropertiesField.get(adapter);
        assertThat(entityProperties).isNotEmpty();

        for( final Object info : entityProperties.values() ) {
            final java.lang.reflect.Method getter = info.getClass().getDeclaredMethod("getJavaField");
            getter.setAccessible(true);
            final Field javaField = (Field) getter.invoke(info);
            assertThat(javaField.canAccess(person)).as("field %s", javaField.getName()).isTrue();
        }
    }
}
