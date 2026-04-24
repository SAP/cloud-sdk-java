package com.sap.cloud.sdk.services.openapi.apache.core;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Thrown if an error occurs during the invocation of a OpenAPI service.
 */
@Accessors( fluent = true )
@Getter
@Setter
public class OpenApiRequestException extends RuntimeException
{
    private static final long serialVersionUID = -8248392392632616674L;

    @Nullable
    private Integer statusCode;
    @Nullable
    private transient Map<String, List<String>> responseHeaders;
    @Nullable
    private transient String responseBody;

    /**
     * Thrown if an error occurs during the invocation of a OpenAPI service.
     *
     * @param message
     *            The message of this exception
     */
    public OpenApiRequestException( @Nonnull final String message )
    {
        super(message);
    }

    /**
     * Thrown if an error occurs during the invocation of a OpenAPI service.
     *
     * @param message
     *            The message of this exception
     * @param cause
     *            The cause of this exception
     */
    public OpenApiRequestException( @Nonnull final String message, @Nonnull final Throwable cause )
    {
        super(message, cause);
    }

    /**
     * Thrown if an error occurs during the invocation of a OpenAPI service.
     *
     * @param cause
     *            The cause of this exception
     */
    public OpenApiRequestException( @Nonnull final Throwable cause )
    {
        super(cause);
    }
}
