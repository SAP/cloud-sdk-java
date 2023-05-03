package com.sap.cloud.sdk.testutil.rules;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;

@Getter
class SystemResult
{
    @Nonnull
    private final String systemAlias;
    @Nonnull
    private final DestinationType destinationType;
    @Nonnull
    private final String destinationName;
    @Nonnull
    private final RunStatus runStatus;
    @Nullable
    private final Throwable caughtException;

    private SystemResult(
        @Nonnull final String systemAlias,
        @Nonnull final DestinationType destinationType,
        @Nonnull final String destinationName,
        @Nonnull final RunStatus runStatus,
        @Nullable final Throwable caughtException )
    {
        this.systemAlias = systemAlias;
        this.destinationType = destinationType;
        this.destinationName = destinationName;
        this.runStatus = runStatus;
        this.caughtException = caughtException;
    }

    SystemResult(
        @Nonnull final String systemAlias,
        @Nonnull final DestinationType destinationType,
        @Nonnull final String destinationName,
        @Nullable final Throwable caughtException )
    {
        this(systemAlias, destinationType, destinationName, RunStatus.FAILURE, caughtException);
    }

    SystemResult(
        @Nonnull final String systemAlias,
        @Nonnull final DestinationType destinationType,
        @Nonnull final String destinationName,
        @Nonnull final RunStatus runStatus )
    {
        this(systemAlias, destinationType, destinationName, runStatus, null);
    }

    SystemResult(
        @Nonnull final String systemAlias,
        @Nonnull final DestinationType destinationType,
        @Nonnull final String destinationName )
    {
        this(systemAlias, destinationType, destinationName, RunStatus.SUCCESS, null);
    }

    boolean isSuccess()
    {
        return RunStatus.SUCCESS == runStatus;
    }

    String getResultString()
    {
        final StringBuilder builder = new StringBuilder();
        builder
            .append(systemAlias)
            .append(" ")
            .append(destinationType)
            .append(" ")
            .append(destinationName)
            .append(" ")
            .append(runStatus);

        if( caughtException != null ) {
            builder.append(" ");
            builder.append(caughtException.getClass().getSimpleName());

            if( caughtException.getMessage() != null ) {
                builder.append(" ");
                builder.append(caughtException.getMessage().replaceAll("\n", " "));
            }
        }
        return builder.toString();
    }
}
