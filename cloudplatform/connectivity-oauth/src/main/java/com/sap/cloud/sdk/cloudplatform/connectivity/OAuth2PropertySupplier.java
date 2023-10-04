/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.sap.cloud.security.config.ClientIdentity;

/**
 * A supplier of OAuth client information. Implementations should extract the client information out of a
 * {@link com.sap.cloud.environment.servicebinding.api.ServiceBinding}.
 *
 * @since 4.20.0
 */
@Beta
public interface OAuth2PropertySupplier
{
    /**
     * Indicates if the binding is supported by this supplier.
     *
     * @return True, if the binding appears to be an OAuth binding and this property supplier is capable of parsing it.
     */
    boolean isOAuth2Binding();

    /**
     * URL that the OAuth client should authenticate to.
     *
     * @return A valid URI.
     */
    @Nonnull
    URI getServiceUri();

    /**
     * URL of the OAuth token service.
     *
     * @return A valid URI.
     */
    @Nonnull
    URI getTokenUri();

    /**
     * OAuth client identity to be used for obtaining a token.
     *
     * @return a {@link ClientIdentity}.
     */
    @Nonnull
    ClientIdentity getClientIdentity();
}
