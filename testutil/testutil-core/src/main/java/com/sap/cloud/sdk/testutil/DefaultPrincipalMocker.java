/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.testutil;

import static org.mockito.Mockito.lenient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalFacade;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class DefaultPrincipalMocker implements PrincipalMocker
{
    private final Supplier<PrincipalFacade> resetPrincipalFacade;

    @Getter( AccessLevel.PACKAGE )
    private final Map<String, Principal> principals = new HashMap<>();

    @Getter( AccessLevel.PACKAGE )
    @Nullable
    private Principal currentPrincipal;

    @Nonnull
    @Override
    public Principal mockPrincipal( @Nonnull final String principalId )
    {
        resetPrincipalFacade.get();

        final Principal principal = Mockito.mock(Principal.class);

        lenient().when(principal.getPrincipalId()).thenReturn(principalId);

        principals.put(principalId, principal);
        return principal;
    }

    @Nonnull
    @Override
    public Principal mockCurrentPrincipal()
    {
        return mockCurrentPrincipal(MockUtil.MOCKED_PRINCIPAL);
    }

    @Nonnull
    @Override
    public Principal mockCurrentPrincipal( @Nonnull final String principalId )
    {
        final Principal principal = mockPrincipal(principalId);
        currentPrincipal = principal;
        return principal;
    }

    @Override
    public void setCurrentPrincipal( @Nullable final String principalId )
    {
        if( principalId == null ) {
            currentPrincipal = null;
        } else {
            final Principal principal = principals.get(principalId);

            if( principal == null ) {
                throw new TestConfigurationError(
                    "No principal mocked with identifier '"
                        + principalId
                        + "'. Make sure to mock the respective principal before calling this method.");
            }

            currentPrincipal = principal;
        }
    }

    @Override
    public void setOrMockCurrentPrincipal( @Nullable final String principalId )
    {
        if( principalId == null ) {
            currentPrincipal = null;
            return;
        }

        Principal principal = principals.get(principalId);
        if( principal == null ) {
            principal = mockPrincipal(principalId);
        }

        currentPrincipal = principal;
    }

    @Override
    public void clearPrincipals()
    {
        currentPrincipal = null;
        principals.clear();
    }
}
