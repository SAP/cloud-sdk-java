/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;

interface PrincipalMocker
{
    /**
     * Mocks a {@link Principal} with the given name.
     */
    @Nonnull
    Principal mockPrincipal( @Nonnull final String principalId );

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
