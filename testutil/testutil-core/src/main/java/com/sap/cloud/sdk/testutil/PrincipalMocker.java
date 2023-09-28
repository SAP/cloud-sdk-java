package com.sap.cloud.sdk.testutil;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;

@SuppressWarnings( "deprecation" )
interface PrincipalMocker
{
    /**
     * Mocks a {@link Principal} with the given name.
     */
    @Nonnull
    Principal mockPrincipal( @Nonnull final String principalId );

    /**
     * Mocks a {@link Principal}.
     *
     * @param principalId
     *            Identifier of the principal.
     * @param authorizations
     *            Optional authorizations of the principal.
     * @param attributes
     *            Optional attributes of the principal.
     */
    @Nonnull
    Principal mockPrincipal(
        @Nonnull final String principalId,
        @Nullable final Collection<com.sap.cloud.sdk.cloudplatform.security.Authorization> authorizations,
        @Nullable final Map<String, com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAttribute> attributes );

    /**
     * Mocks the current {@link Principal} with principal identifier {@link MockUtil#MOCKED_PRINCIPAL}.
     */
    @Nonnull
    Principal mockCurrentPrincipal();

    /**
     * Mocks the current {@link Principal} with the given identifier.
     */
    @Nonnull
    Principal mockCurrentPrincipal( @Nonnull final String principalId );

    /**
     * Mocks the current {@link Principal}.
     *
     * @param principalId
     *            Identifier of the principal.
     * @param authorizations
     *            Optional authorizations of the principal.
     * @param attributes
     *            Optional attributes of the principal.
     */
    @Nonnull
    Principal mockCurrentPrincipal(
        @Nonnull final String principalId,
        @Nullable final Collection<com.sap.cloud.sdk.cloudplatform.security.Authorization> authorizations,
        @Nullable final Map<String, com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAttribute> attributes );

    /**
     * Sets the current {@link Principal}. Clears the current {@link Principal} if given {@code null}.
     */
    void setCurrentPrincipal( @Nullable final String principalId );

    /**
     * Sets or mocks the current {@link Principal}. Clears the current {@link Principal} if given {@code null}.
     */
    void setOrMockCurrentPrincipal( @Nullable final String principalId );

    /**
     * Clears all previously mocked {@link Principal}s.
     */
    void clearPrincipals();
}
