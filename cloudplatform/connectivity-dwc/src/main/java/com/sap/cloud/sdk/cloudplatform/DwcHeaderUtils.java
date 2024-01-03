/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.exception.DwcHeaderNotFoundException;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;

import lombok.Value;

/**
 * Utility class to extract specific values from the SAP Deploy with Confidence request headers.
 */
@Beta
public class DwcHeaderUtils
{
    /**
     * The name of the header that contains the Deploy with Confidence user (princial) information.
     */
    public static final String DWC_USER_HEADER = "dwc-user";
    /**
     * The name of the header that contains the Deploy with Confidence tenant information.
     */
    public static final String DWC_TENANT_HEADER = "dwc-tenant";
    /**
     * The name of the header that contains the Deploy with Confidence scopes information.
     */
    public static final String DWC_SUBDOMAIN_HEADER = "dwc-subdomain";

    /**
     * This method fetches the value of the {@link #DWC_TENANT_HEADER} header or throws an
     * {@link DwcHeaderNotFoundException} if the header was not found.
     *
     * @return The value of the {@link #DWC_TENANT_HEADER} header.
     * @throws DwcHeaderNotFoundException
     *             if the header was not found.
     */
    @Nonnull
    public static String getDwcTenantIdOrThrow()
    {
        return getNonEmptyDwcHeaderValue(DWC_TENANT_HEADER);
    }

    /**
     * This method fetches the value of the {@link #DWC_SUBDOMAIN_HEADER} header or throws an
     * {@link DwcHeaderNotFoundException} if the header was not found.
     *
     * @return The value of the {@link #DWC_SUBDOMAIN_HEADER} header.
     * @throws DwcHeaderNotFoundException
     *             if the header was not found.
     */
    @Nonnull
    public static String getDwCSubdomainOrThrow()
    {
        return getNonEmptyDwcHeaderValue(DWC_SUBDOMAIN_HEADER);
    }

    /**
     * This method fetches the value of the {@link #DWC_USER_HEADER} header or throws an
     * {@link DwcHeaderNotFoundException} if the header was not found.
     *
     * @return The value of the {@link #DWC_USER_HEADER} header.
     * @throws DwcHeaderNotFoundException
     *             if the header was not found.
     */
    @Nonnull
    public static String getDwcPrincipalIdOrThrow()
    {
        final String dwcUserBase64 = getNonEmptyDwcHeaderValue(DWC_USER_HEADER);
        final String dwcUserJson = new String(Base64.getDecoder().decode(dwcUserBase64), StandardCharsets.UTF_8);
        final DwcUserModel dwcUser = new Gson().fromJson(dwcUserJson, DwcUserModel.class);
        final String logonName = dwcUser.getLogonName();
        if( logonName == null ) {
            throw new DwcHeaderNotFoundException("Header value of " + DWC_USER_HEADER + " has no logon name.");
        }
        return logonName;
    }

    @Nonnull
    private static String getNonEmptyDwcHeaderValue( @Nonnull final String key )
        throws DwcHeaderNotFoundException
    {
        final RequestHeaderContainer container =
            RequestHeaderAccessor
                .tryGetHeaderContainer()
                .getOrElseThrow(e -> new DwcHeaderNotFoundException("Unable to read the " + key + " header value.", e));

        return container
            .getHeaderValues(key)
            .stream()
            .filter(val -> !Strings.isNullOrEmpty(val))
            .findFirst()
            .orElseThrow(() -> new DwcHeaderNotFoundException("Unable to read the " + key + " header value."));
    }

    @Value
    private static class DwcUserModel
    {
        String email;
        String givenName;
        String familyName;
        String logonName;
    }
}
