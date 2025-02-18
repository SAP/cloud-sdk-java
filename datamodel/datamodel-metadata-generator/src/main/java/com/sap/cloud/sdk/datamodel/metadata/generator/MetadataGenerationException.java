package com.sap.cloud.sdk.datamodel.metadata.generator;

/**
 * Indicates an error during VDM metadata generation.
 */
public class MetadataGenerationException extends RuntimeException
{
    private static final long serialVersionUID = -199677362491505868L;

    MetadataGenerationException( final String message )
    {
        super(message);
    }

    MetadataGenerationException( final String message, final Throwable cause )
    {
        super(message, cause);
    }
}
