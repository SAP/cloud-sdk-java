package com.sap.cloud.sdk.datamodel.odatav4.generator.annotation;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer;
import com.sap.cloud.sdk.datamodel.odatav4.core.DecimalDescriptor;
import com.sap.cloud.sdk.datamodel.odatav4.generator.MessageCollector;
import com.sap.cloud.sdk.result.ElementName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Default implementation of {@link AnnotationStrategy} that applies the necessary annotations for the full set of
 * generated VDM classes (POJOs, fluent helpers, service classes, etc.), which uses the SAP Cloud SDK to access OData
 * services.
 */
@Beta
public class DefaultAnnotationStrategy implements AnnotationStrategy
{
    private static final Logger logger = MessageCollector.getLogger(DefaultAnnotationStrategy.class);

    private static final int JAVA_MAXIMUM_ARGUMENTS = 254;

    private static final Map<String, Class<?>> ODATA_GSON_TYPE_ADAPTERS = new HashMap<>();

    private static final Map<String, Class<?>> ODATA_JACKSON_TYPE_DESERIALIZER = new HashMap<>();

    private static final Map<String, Class<?>> ODATA_JACKSON_TYPE_SERIALIZER = new HashMap<>();

    private final String EDM_DECIMAL_TYPE = "Decimal";

    /**
     * Default implementation for the SAP Cloud SDK which adds the following annotations:
     * <ul>
     * <li>Lombok {@link Builder}, {@link NoArgsConstructor}, and {@link AllArgsConstructor}. But only if there are less
     * than 254 properties in the entity (Java constructor limitation).</li>
     * <li>Lombok {@link Data}</li>
     * <li>Lombok {@link ToString} with parameters {@code doNotUseGetters=true} and {@code callSuper=true}</li>
     * <li>Lombok {@link EqualsAndHashCode} with parameters {@code doNotUseGetters=true} and {@code callSuper=true}</li>
     * <li>Gson {@link JsonAdapter} with default parameter set to {@code ODataVdmEntityAdapterFactory.class}, for
     * deserializing OData responses.</li>
     * </ul>
     *
     * @param context
     *            Object representing an OData entity.
     * @return Default set of annotations that are needed by the SAP Cloud SDK implementation of the VDM.
     */
    @Override
    @Nonnull
    public Set<AnnotationDefinition> getAnnotationsForEntity( @Nonnull final EntityAnnotationModel context )
    {
        final Set<AnnotationDefinition> result = new LinkedHashSet<>();

        final boolean isArgLimitExceeded = context.getNumberOfProperties() > JAVA_MAXIMUM_ARGUMENTS;
        if( !isArgLimitExceeded ) {
            result.add(new AnnotationDefinition(Builder.class));
        } else {
            logger
                .info(
                    String
                        .format(
                            "  VdmObject class %s has %d properties, which exceeds the Java limit of constructor arguments (%d). "
                                + "Builder will not be available for this entity.",
                            context.getJavaClassName(),
                            context.getNumberOfProperties(),
                            JAVA_MAXIMUM_ARGUMENTS));
        }

        result.add(new AnnotationDefinition(Data.class));

        if( !isArgLimitExceeded ) {
            // Because Builder makes unavailable the default constructor (still needed for proper OData response deserialization)
            result.add(new AnnotationDefinition(NoArgsConstructor.class));
            // Because NoArgsConstructor makes the all arguments constructor unavailable to the builder
            result.add(new AnnotationDefinition(AllArgsConstructor.class));
        }

        result
            .add(
                new AnnotationDefinition(
                    ToString.class,
                    new AnnotationParameter("doNotUseGetters", true),
                    new AnnotationParameter("callSuper", true)));
        result
            .add(
                new AnnotationDefinition(
                    EqualsAndHashCode.class,
                    new AnnotationParameter("doNotUseGetters", true),
                    new AnnotationParameter("callSuper", true)));

        result
            .add(
                new AnnotationDefinition(
                    JsonAdapter.class,
                    new AnnotationParameter("value", GsonVdmAdapterFactory.class)));
        result
            .add(
                new AnnotationDefinition(
                    JsonSerialize.class,
                    new AnnotationParameter("using", JacksonVdmObjectSerializer.class)));
        result
            .add(
                new AnnotationDefinition(
                    JsonDeserialize.class,
                    new AnnotationParameter("using", JacksonVdmObjectDeserializer.class)));

        return result;
    }

    /**
     * Default implementation for the SAP Cloud SDK which adds the following annotations:
     * <ul>
     * <li>Gson {@link SerializedName} with the value set to the OData EDM name of the entity property.</li>
     * <li>Jackson {@link JsonProperty} with the value set to the OData EDM name of the entity property.</li>
     * <li>{@link Nullable}</li>
     * <li>Gson {@link JsonAdapter} with default parameter set to an appropriate {@link com.google.gson.TypeAdapter}
     * class. The adapter used is based on the OData EDM type of the entity property.</li>
     * </ul>
     *
     * @param context
     *            Object representing an OData entity property.
     * @return Default set of annotations that are needed by the SAP Cloud SDK implementation of the VDM.
     */
    @Override
    @Nonnull
    public Set<AnnotationDefinition> getAnnotationsForEntityProperty(
        @Nonnull final EntityPropertyAnnotationModel context )
    {
        final Set<AnnotationDefinition> result = new LinkedHashSet<>();
        result.add(new AnnotationDefinition(Nullable.class));
        result.add(new AnnotationDefinition(ElementName.class, new AnnotationParameter("value", context.getEdmName())));

        //Currently, considering precision and scale only for decimal properties
        if( EDM_DECIMAL_TYPE.equalsIgnoreCase(context.getEdmType()) && context.getPrecision() != null ) {
            //According to OData specification if scale is not defined it defaults to 0
            final int scale = (context.getScale() != null) ? context.getScale() : 0;
            result
                .add(
                    new AnnotationDefinition(
                        DecimalDescriptor.class,
                        new AnnotationParameter("precision", context.getPrecision()),
                        new AnnotationParameter("scale", scale)));
        }
        if( context.isSimpleType() ) {
            if( ODATA_JACKSON_TYPE_SERIALIZER.containsKey(context.getEdmType()) ) {
                result
                    .add(
                        new AnnotationDefinition(
                            JsonSerialize.class,
                            new AnnotationParameter("using", ODATA_JACKSON_TYPE_SERIALIZER.get(context.getEdmType()))));
            }
            if( ODATA_JACKSON_TYPE_DESERIALIZER.containsKey(context.getEdmType()) ) {
                result
                    .add(
                        new AnnotationDefinition(
                            JsonDeserialize.class,
                            new AnnotationParameter(
                                "using",
                                ODATA_JACKSON_TYPE_DESERIALIZER.get(context.getEdmType()))));
            }
            if( ODATA_GSON_TYPE_ADAPTERS.containsKey(context.getEdmType()) ) {
                result
                    .add(
                        new AnnotationDefinition(
                            JsonAdapter.class,
                            new AnnotationParameter("value", ODATA_GSON_TYPE_ADAPTERS.get(context.getEdmType()))));
            }
        }
        return result;
    }

    /**
     * Default implementation for the SAP Cloud SDK which adds the following annotations:
     * <ul>
     * <li>Gson {@link SerializedName} with the value set to the OData EDM name of the navigation property.</li>
     * <li>Jackson {@link JsonProperty} with the value set to the OData EDM name of the navigation property.</li>
     * <li>{@link Nullable}, but only if the multiplicity of the navigation property is 1..1 or 0..1</li>
     * </ul>
     *
     * @param context
     *            Object representing an OData navigation property.
     * @return Default set of annotations that are needed by the SAP Cloud SDK implementation of the VDM.
     */
    @Override
    @Nonnull
    public Set<AnnotationDefinition> getAnnotationsForAssociatedEntity(
        @Nonnull final NavigationPropertyAnnotationModel context )
    {
        final Set<AnnotationDefinition> result = new LinkedHashSet<>();
        result.add(new AnnotationDefinition(ElementName.class, new AnnotationParameter("value", context.getEdmName())));

        if( !context.isManyMultiplicity() ) {
            result.add(new AnnotationDefinition(Nullable.class));
        }

        return result;
    }

    /**
     * Default implementation for the SAP Cloud SDK which adds the following annotations:
     * <ul>
     * <li>Lombok {@link Builder}, {@link NoArgsConstructor}, and {@link AllArgsConstructor}. But only if there are less
     * than 254 properties in the entity (Java constructor limitation).</li>
     * <li>Lombok {@link Data}</li>
     * <li>Lombok {@link ToString} with parameters {@code doNotUseGetters=true} and {@code callSuper=true}</li>
     * <li>Lombok {@link EqualsAndHashCode} with parameters {@code doNotUseGetters=true} and {@code callSuper=true}</li>
     * <li>Gson {@link JsonAdapter} with default parameter set to {@code ODataVdmEntityAdapterFactory.class}, for
     * deserializing OData responses.</li>
     * </ul>
     *
     * @param context
     *            Object representing an OData complex type.
     * @return Default set of annotations that are needed by the SAP Cloud SDK implementation of the VDM.
     */
    @Override
    @Nonnull
    public Set<AnnotationDefinition> getAnnotationsForComplexType( @Nonnull final EntityAnnotationModel context )
    {
        // Just so happens that with our VDM implementation the annotations are the same.
        return getAnnotationsForEntity(context);
    }

    /**
     * Default implementation for the SAP Cloud SDK which adds the following annotations:
     * <ul>
     * <li>Gson {@link SerializedName} with the value set to the OData EDM name of the complex type property.</li>
     * <li>Jackson {@link JsonProperty} with the value set to the OData EDM name of the complex type property.</li>
     * <li>{@link Nullable}</li>
     * <li>Gson {@link JsonAdapter} with default parameter set to an appropriate {@link com.google.gson.TypeAdapter}
     * class. The adapter used is based on the OData EDM type of the complex type property.</li>
     * </ul>
     *
     * @param context
     *            Object representing an OData complex type property.
     * @return Default set of annotations that are needed by the SAP Cloud SDK implementation of the VDM.
     */
    @Override
    @Nonnull
    public Set<AnnotationDefinition> getAnnotationsForComplexTypeProperty(
        @Nonnull final EntityPropertyAnnotationModel context )
    {
        // Just so happens that with our VDM implementation the annotations are the same.
        return getAnnotationsForEntityProperty(context);
    }
}
