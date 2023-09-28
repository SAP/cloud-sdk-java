package com.sap.cloud.sdk.s4hana.connectivity;

import java.time.Duration;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;

/**
 * Collection of measurements for an S/4HANA request.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Getter
@Deprecated
public class RequestExecutionMeasurements
{
    @Setter
    @Nullable
    private Long beginTotal;

    @Setter
    @Nullable
    private Long endTotal;

    @Nullable
    private Duration buildRequestDuration;

    @Nullable
    private Duration executeRequestDuration;

    @Nullable
    private Duration parseResponseDuration;

    /**
     * For internal use only.
     *
     * @param duration
     *            The {@link Duration} to be formatted.
     *
     * @return The formatted {@link Duration}.
     */
    @Nonnull
    public static String formatDuration( @Nullable final Duration duration )
    {
        return duration == null ? "N/A" : duration.toString().substring(2).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Increase a build request duration by a given value.
     *
     * @param duration
     *            The build request duration.
     */
    public void addBuildRequestDuration( @Nonnull final Duration duration )
    {
        buildRequestDuration = buildRequestDuration == null ? duration : buildRequestDuration.plus(duration);
    }

    /**
     * Increase a request execution duration by a given value.
     *
     * @param duration
     *            The execute request duration.
     */
    public void addExecuteRequestDuration( @Nonnull final Duration duration )
    {
        executeRequestDuration = executeRequestDuration == null ? duration : executeRequestDuration.plus(duration);
    }

    /**
     * Increase a parse response duration by a given value.
     *
     * @param duration
     *            The parse response duration.
     */
    public void addParseResponseDuration( @Nonnull final Duration duration )
    {
        parseResponseDuration = parseResponseDuration == null ? duration : parseResponseDuration.plus(duration);
    }

    /**
     * Remove all current measurements for a request.
     */
    public void resetMeasurements()
    {
        beginTotal = null;
        endTotal = null;
        buildRequestDuration = null;
        executeRequestDuration = null;
        parseResponseDuration = null;
    }

    /**
     * Get all request measurements serialized as a {@link String}.
     *
     * @return The request measurements.
     */
    @Nonnull
    public String getMeasurementsString()
    {
        final Duration total = beginTotal != null && endTotal != null ? Duration.ofNanos(endTotal - beginTotal) : null;

        return String
            .format(
                "total: %s, build payload: %s, execute request: %s, parse response: %s",
                formatDuration(total),
                formatDuration(buildRequestDuration),
                formatDuration(executeRequestDuration),
                formatDuration(parseResponseDuration));
    }
}
