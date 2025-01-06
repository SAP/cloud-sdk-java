package com.sap.cloud.sdk.datamodel.odatav4.adapter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Generic fluent helper converters for String based OData V4 primitives.
 *
 * @param <JavaT>
 *            The Java type to which conversion happens.
 */
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
final class ODataGenericConverter<JavaT> extends AbstractTypeConverter<JavaT, String>
{
    private static final ODataGenericConverter<LocalDate> LOCAL_DATE =
        new ODataGenericConverter<>(
            LocalDate.class,
            o -> o.format(DateTimeFormatter.ISO_LOCAL_DATE),
            s -> LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE));

    private static final ODataGenericConverter<LocalTime> LOCAL_TIME =
        new ODataGenericConverter<>(
            LocalTime.class,
            o -> o.format(DateTimeFormatter.ISO_LOCAL_TIME),
            s -> LocalTime.parse(s, DateTimeFormatter.ISO_LOCAL_TIME));

    private static final ODataGenericConverter<OffsetDateTime> OFFSET_DATE_TIME =
        new ODataGenericConverter<>(
            OffsetDateTime.class,
            o -> o.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            s -> OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME));

    private static final ODataGenericConverter<UUID> GUID =
        new ODataGenericConverter<>(UUID.class, UUID::toString, UUID::fromString);

    private static final ODataGenericConverter<Duration> DURATION =
        new ODataGenericConverter<>(Duration.class, Duration::toString, Duration::parse);

    private static final ODataGenericConverter<byte[]> BINARY =
        new ODataGenericConverter<>(byte[].class, Base64.getEncoder()::encodeToString, Base64.getDecoder()::decode);

    private static final ODataGenericConverter<String> STRING =
        new ODataGenericConverter<>(String.class, Function.identity(), Function.identity());

    /**
     * Array of OData value converters for primitive types.
     */
    public static final ODataGenericConverter<?>[] DEFAULT_CONVERTERS =
        { LOCAL_DATE, LOCAL_TIME, OFFSET_DATE_TIME, GUID, DURATION, BINARY, STRING };

    @Getter
    private final Class<JavaT> type;

    @Getter
    private final Class<String> domainType = String.class;

    private final Function<JavaT, String> serializer;
    private final Function<String, JavaT> deserializer;

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final JavaT object )
    {
        return ConvertedObject.of(serializer.apply(object));
    }

    @Nonnull
    @Override
    public ConvertedObject<JavaT> fromDomainNonNull( @Nonnull final String domainObject )
    {
        final Try<JavaT> maybe = Try.of(() -> deserializer.apply(domainObject));
        return maybe.map(ConvertedObject::of).getOrElse(ConvertedObject::ofNotConvertible);
    }
}
