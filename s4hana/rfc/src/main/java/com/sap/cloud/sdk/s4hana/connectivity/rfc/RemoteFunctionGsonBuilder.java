package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.sap.cloud.sdk.result.AnnotatedFieldGsonExclusionStrategy;
import com.sap.cloud.sdk.result.ElementName;
import com.sap.cloud.sdk.result.ElementNameGsonFieldNamingStrategy;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.cloud.sdk.result.ResultElement;
import com.sap.cloud.sdk.typeconverter.TypeConverterGsonDeserializer;

final class RemoteFunctionGsonBuilder
{
    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public static GsonBuilder newRequestGsonBuilder(
        final Collection<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters )
    {
        final GsonBuilder gsonBuilder =
            new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Duration.class, new com.sap.cloud.sdk.s4hana.connectivity.DurationDeserializer())
                .registerTypeAdapterFactory(new com.sap.cloud.sdk.s4hana.connectivity.ErpTypeGsonTypeAdapterFactory())
                .disableHtmlEscaping();

        for( final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?> typeConverter : typeConverters ) {
            gsonBuilder
                .registerTypeAdapter(typeConverter.getType(), new TypeConverterGsonDeserializer<>(typeConverter));
        }

        return gsonBuilder;
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public static GsonBuilder newRequestResultGsonBuilder(
        final Collection<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters )
    {
        final GsonBuilder gsonBuilder = newResultBuilder(typeConverters);

        gsonBuilder
            .registerTypeAdapter(
                ResultElement.class,
                new ResultElementJsonDeserializer(new GsonResultElementFactory(gsonBuilder)));

        return gsonBuilder;
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public static GsonBuilder newSoapRequestResultGsonBuilder(
        final Collection<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters )
    {
        final GsonBuilder gsonBuilder = newResultBuilder(typeConverters);

        gsonBuilder
            .registerTypeAdapter(
                ResultElement.class,
                new ResultElementJsonDeserializer(new SoapGsonResultElementFactory(gsonBuilder)));
        gsonBuilder.registerTypeAdapter(List.class, new CustomSoapListDeserializer());

        return gsonBuilder;
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public static GsonBuilder newJCoRequestResultGsonBuilder(
        final Collection<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters )
    {
        final GsonBuilder gsonBuilder = newResultBuilder(typeConverters);

        // Consider use of JCoResultElementFactory to avoid de-serialization via JSON.
        gsonBuilder
            .registerTypeAdapter(
                ResultElement.class,
                new ResultElementJsonDeserializer(new SoapGsonResultElementFactory(gsonBuilder)));

        for( final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?> typeConverter : typeConverters ) {
            gsonBuilder
                .registerTypeAdapter(typeConverter.getType(), new TypeConverterGsonDeserializer<>(typeConverter));
        }

        return gsonBuilder;
    }

    @SuppressWarnings( "deprecation" )
    private static GsonBuilder newResultBuilder(
        final Collection<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters )
    {
        final GsonBuilder gsonBuilder =
            new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingStrategy(new ElementNameGsonFieldNamingStrategy())
                .setExclusionStrategies(new AnnotatedFieldGsonExclusionStrategy<>(ElementName.class))
                .registerTypeAdapter(Duration.class, new com.sap.cloud.sdk.s4hana.connectivity.DurationDeserializer())
                .registerTypeAdapterFactory(new com.sap.cloud.sdk.s4hana.connectivity.ErpTypeGsonTypeAdapterFactory());

        for( final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?> typeConverter : typeConverters ) {
            gsonBuilder
                .registerTypeAdapter(typeConverter.getType(), new TypeConverterGsonDeserializer<>(typeConverter));
        }

        return gsonBuilder;
    }
}
