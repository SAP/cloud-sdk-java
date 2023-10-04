/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;

import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.security.BasicAuthHeaderEncoder;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;

class SecurityUtil
{
    static Header newBasicHeader( @Nonnull final BasicCredentials credentials )
    {
        final String base64Credentials = BasicAuthHeaderEncoder.encodeUserPasswordBase64(credentials);
        return new Header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);
    }
}
