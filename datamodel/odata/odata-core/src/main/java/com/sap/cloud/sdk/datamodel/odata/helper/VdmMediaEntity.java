/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ODataResourcePath;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import lombok.EqualsAndHashCode;

/**
 * Represents a media entity which exposes additional data under a {@code $value} endpoint.
 *
 * @param <EntityT>
 *            The specific entity type.
 */
@EqualsAndHashCode( callSuper = true, doNotUseGetters = true )
public abstract class VdmMediaEntity<EntityT> extends VdmEntity<EntityT>
{
    /**
     * Get the binary data stream (file) from this media entity. Perform this operation <i>after retrieving</i> the
     * entity object from the OData service.
     * <p>
     * <i>Alternatively</i>, you can use this method to only retrieve the media resource without requesting the entity
     * data. Build this entity via its {@code .builder()} and use {@link #attachToService(String, Destination)} to
     * declare a service path and destination to request the media resource from. You can obtain the service path from
     * the {@code <ServiceClass>#DEFAULT_SERVICE_PATH}, e.g.
     * {@code BusinessPartnerServiceBusinessPartnerService.DEFAULT_SERVICE_PATH}
     * <p>
     * <strong>Please ensure this stream is closed after usage.</strong> The below example achieves this using
     * try-with-resources:
     *
     * <pre>
     * try( InputStream content = entity.fetchMediaStream() ) {
     *     // do something with the content here
     * }
     * </pre>
     *
     * @return File content as an {@link InputStream}.
     * @throws ODataException
     *             if the request could not be sent or the OData service responded with an error.
     */
    @Nonnull
    public InputStream fetchMediaStream()
        throws ODataException
    {
        final ODataResourcePath resource =
            ODataResourcePath
                .of(getEntityCollection(), ODataEntityKey.of(getKey(), ODataProtocol.V2))
                .addSegment("$value");

        final ODataRequestReadByKey request =
            new ODataRequestReadByKey(getServicePathForFetch(), resource, null, ODataProtocol.V2);

        final Destination destination = getDestinationForFetch();
        if( destination == null ) {
            throw new ODataRequestException(
                request,
                "Failed to fetch media stream.",
                new IllegalStateException(
                    "Unable to execute OData query. The entity was created locally without an assigned HttpDestination. This method is applicable only on entities which were retrieved or created using the OData VDM."));
        }
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final ODataRequestResultGeneric result = request.execute(httpClient);
        final HttpEntity entity = result.getHttpResponse().getEntity();
        if( entity == null ) {
            throw new ODataResponseException(
                request,
                result.getHttpResponse(),
                "Failed to read the input stream of the OData response: Response didn't contain any payload.",
                null);
        }
        try {
            return entity.getContent();
        }
        catch( final IOException | UnsupportedOperationException e ) {
            throw new ODataResponseException(
                request,
                result.getHttpResponse(),
                "Failed to read the input stream of the OData response.",
                e);
        }
    }
}
