/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import javax.annotation.Nonnull;

/**
 * Generic type of an OData request result.
 */
public interface ODataRequestResultMultipart
{
    /**
     * Get the result from the OData batch response for a specific sub-request.
     *
     * @param request
     *            The request to look for in the OData batch response.
     * @return The OData result, that was extracted from the original OData batch response.
     */
    @Nonnull
    ODataRequestResult getResult( @Nonnull final ODataRequestGeneric request );
}
