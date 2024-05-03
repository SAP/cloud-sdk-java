/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.BASIC_AUTHENTICATION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.CLIENT_CERTIFICATE_AUTHENTICATION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.NO_AUTHENTICATION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.OAUTH2_JWT_BEARER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.OAUTH2_REFRESH_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.OAUTH2_SAML_BEARER_ASSERTION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.PRINCIPAL_PROPAGATION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.SAML_ASSERTION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.SAP_ASSERTION_SSO;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AuthenticationTypeTest
{
    @Test
    void getIdentifier()
    {
        assertThat(NO_AUTHENTICATION.getIdentifier()).isEqualTo("NoAuthentication");
        assertThat(BASIC_AUTHENTICATION.getIdentifier()).isEqualTo("BasicAuthentication");
        assertThat(PRINCIPAL_PROPAGATION.getIdentifier()).isEqualTo("PrincipalPropagation");
        assertThat(OAUTH2_SAML_BEARER_ASSERTION.getIdentifier()).isEqualTo("OAuth2SAMLBearerAssertion");
        assertThat(SAP_ASSERTION_SSO.getIdentifier()).isEqualTo("SAPAssertionSSO");
        assertThat(CLIENT_CERTIFICATE_AUTHENTICATION.getIdentifier()).isEqualTo("ClientCertificateAuthentication");
        assertThat(OAUTH2_JWT_BEARER.getIdentifier()).isEqualTo("OAuth2JWTBearer");
        assertThat(SAML_ASSERTION.getIdentifier()).isEqualTo("SAMLAssertion");
        assertThat(OAUTH2_REFRESH_TOKEN.getIdentifier()).isEqualTo("OAuth2RefreshToken");
    }

    @Test
    void ofIdentifier()
    {
        assertThat(AuthenticationType.ofIdentifier("NoAuthentication")).isEqualTo(NO_AUTHENTICATION);
        assertThat(AuthenticationType.ofIdentifier("BasicAuthentication")).isEqualTo(BASIC_AUTHENTICATION);
        assertThat(AuthenticationType.ofIdentifier("PrincipalPropagation")).isEqualTo(PRINCIPAL_PROPAGATION);
        assertThat(AuthenticationType.ofIdentifier("OAuth2SAMLBearerAssertion"))
            .isEqualTo(OAUTH2_SAML_BEARER_ASSERTION);
        assertThat(AuthenticationType.ofIdentifier("OAuth2JWTBearer")).isEqualTo(OAUTH2_JWT_BEARER);
        assertThat(AuthenticationType.ofIdentifier("SAPAssertionSSO")).isEqualTo(SAP_ASSERTION_SSO);
        assertThat(AuthenticationType.ofIdentifier("ClientCertificateAuthentication"))
            .isEqualTo(CLIENT_CERTIFICATE_AUTHENTICATION);
        assertThat(AuthenticationType.ofIdentifier("SAMLAssertion")).isEqualTo(SAML_ASSERTION);
        assertThat(AuthenticationType.ofIdentifier("OAuth2RefreshToken")).isEqualTo(OAUTH2_REFRESH_TOKEN);
    }

    @Test
    void ofIdentifierOrDefault()
    {
        assertThat(AuthenticationType.ofIdentifierOrDefault("NonExisting", NO_AUTHENTICATION))
            .isEqualTo(NO_AUTHENTICATION);
    }
}
