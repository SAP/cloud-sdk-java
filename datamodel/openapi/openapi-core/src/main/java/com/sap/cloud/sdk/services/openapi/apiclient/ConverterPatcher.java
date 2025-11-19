package com.sap.cloud.sdk.services.openapi.apiclient;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;

import lombok.extern.slf4j.Slf4j;

// DO NOT IMPORT JACKSON CLASSES OTHER THAN ANNOTATIONS

@FunctionalInterface
interface ConverterPatcher
{
    @Nullable
    <T> T patch( @Nonnull T instance );

    default <T> void patchList( @Nonnull final List<T> instances )
    {
        final ListIterator<T> iterator = instances.listIterator();
        while( iterator.hasNext() ) {
            final T instance = patch(iterator.next());
            if( instance != null ) {
                iterator.set(instance);
            } else {
                iterator.remove();
            }
        }
    }

    @Slf4j
    class Jackson2 implements ConverterPatcher
    {
        @SuppressWarnings( "removal" )
        @Override
        public <T> T patch( @Nonnull final T instance )
        {
            final String springJacksonConverter =
                "org.springframework.http.converter.json.MappingJackson2HttpMessageConverter";
            final Class<?> cl = instance.getClass();
            if( cl.getName().equals(springJacksonConverter) ) {
                try {
                    final com.fasterxml.jackson.databind.ObjectMapper mapper =
                        new org.springframework.http.converter.json.Jackson2ObjectMapperBuilder()
                            .modules(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                            .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                            .visibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
                            .build();
                    ((org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) instance)
                        .setObjectMapper(mapper);
                }
                catch( final Exception e ) {
                    log.error("Failed to apply Jackson2 patch: " + e.getMessage(), e);
                }
            }
            return instance;
        }
    }

    @Slf4j
    class Jackson3 implements ConverterPatcher
    {
        @SuppressWarnings( "unchecked" )
        @Override
        public <T> T patch( @Nonnull final T instance )
        {
          // run the following code respectively if the classes were available:

          // Builder builder = ((JacksonJsonHttpMessageConverter) instance).getMapper().rebuild()
          //    .changeDefaultVisibility(v -> v
          //       .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
          //       .withSetterVisibility(JsonAutoDetect.Visibility.NONE));
          // return (T) new JacksonJsonHttpMessageConverter(builder);

            final String springJacksonConverter =
                "org.springframework.http.converter.json.JacksonJsonHttpMessageConverter";
            final Class<?> cl = instance.getClass();
            if( cl.getName().equals(springJacksonConverter) ) {
                final UnaryOperator<tools.jackson.databind.introspect.VisibilityChecker> vc =
                    v -> v
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE);
                tools.jackson.databind.json.JsonMapper.Builder builder =
                    ((org.springframework.http.converter.json.JacksonJsonHttpMessageConverter) instance)
                        .getMapper()
                        .rebuild();
                builder = builder.changeDefaultVisibility(vc);
                try {
                  final String jackson2SerName = "com.fasterxml.jackson.databind.JsonSerializable";
                  final String serializerName = "tools.jackson.databind.ser.jackson.RawSerializer";
                  final String moduleName = "tools.jackson.databind.module.SimpleModule";
                  final Class<?> jackson2ser = Class.forName(jackson2SerName);
                  final Object serializer =
                        Class
                            .forName(serializerName)
                            .getConstructor(Class.class)
                            .newInstance(jackson2ser);
                  Object module =
                        Class.forName(moduleName).getConstructor().newInstance();
                    module =
                        module.getClass().getMethod("addSerializer", serializer.getClass()).invoke(module, serializer);
                    builder =
                        (tools.jackson.databind.json.JsonMapper.Builder) builder
                            .getClass()
                            .getMethod("addModule", module.getClass())
                            .invoke(builder, module);
                }
                catch( final
                    Exception e ) {
                    log.debug("Could not find Jackson2 JsonSerializable class to add ToStringSerializer.", e);
                }
                return (T) new org.springframework.http.converter.json.JacksonJsonHttpMessageConverter(builder);
            }
            return instance;
        }
    }
}
