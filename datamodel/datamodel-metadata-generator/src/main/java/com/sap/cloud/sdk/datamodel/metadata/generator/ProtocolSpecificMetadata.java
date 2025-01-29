package com.sap.cloud.sdk.datamodel.metadata.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Protocol-specific Datamodel Metadata.
 */
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
@Getter
public final class ProtocolSpecificMetadata
{
    @Nonnull
    private final ServiceType serviceType;
    @Nullable
    private final ApiUsageMetadata apiUsageMetadata;

    /**
     * Returns the {@link ProtocolSpecificMetadata} initialized with the {@link ServiceType#ODATA_V2} and the
     * {@link ODataApiUsageMetadata}.
     *
     * @param oDataApiUsageMetadata
     *            The OData API usage metadata
     * @return The initialized {@link ProtocolSpecificMetadata}
     */
    @Nonnull
    public static ProtocolSpecificMetadata ofODataV2( @Nullable final ODataApiUsageMetadata oDataApiUsageMetadata )
    {
        return new ProtocolSpecificMetadata(ServiceType.ODATA_V2, oDataApiUsageMetadata);
    }

    /**
     * Returns the {@link ProtocolSpecificMetadata} initialized with the {@link ServiceType#ODATA_V4} and the
     * {@link ODataApiUsageMetadata}.
     *
     * @param oDataApiUsageMetadata
     *            The OData API usage metadata
     * @return The initialized {@link ProtocolSpecificMetadata}
     */
    @Nonnull
    public static ProtocolSpecificMetadata ofODataV4( @Nullable final ODataApiUsageMetadata oDataApiUsageMetadata )
    {
        return new ProtocolSpecificMetadata(ServiceType.ODATA_V4, oDataApiUsageMetadata);
    }

    /**
     * Returns the {@link ProtocolSpecificMetadata} initialized with the {@link ServiceType#REST} and the
     * {@link RestApiUsageMetadata}.
     *
     * @param restApiUsageMetadata
     *            The REST API usage metadata
     * @return The initialized {@link ProtocolSpecificMetadata}
     */
    @Nonnull
    public static ProtocolSpecificMetadata ofRest( @Nullable final RestApiUsageMetadata restApiUsageMetadata )
    {
        return new ProtocolSpecificMetadata(ServiceType.REST, restApiUsageMetadata);
    }
}
