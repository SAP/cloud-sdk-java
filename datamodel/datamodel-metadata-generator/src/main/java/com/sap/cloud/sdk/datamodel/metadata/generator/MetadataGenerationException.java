/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import com.google.common.annotations.Beta;

/**
 * Indicates an error during VDM metadata generation.
 */
@Beta
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
