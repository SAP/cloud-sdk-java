package com.sap.cloud.sdk.testutil;

import static org.mockito.Mockito.lenient;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.assertj.core.util.Sets;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.sap.cloud.sdk.cloudplatform.security.Authorization;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAttribute;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalFacade;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAttributeException;

import io.vavr.control.Try;
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

    @RequiredArgsConstructor
    private static class GetAttributeMockitoAnswer implements Answer<Try<PrincipalAttribute>>
    {
        private final Map<String, PrincipalAttribute> attributes;

        @Override
        public Try<PrincipalAttribute> answer( final InvocationOnMock invocation )
        {
            final String attributeName = invocation.getArgument(0);
            return attributes.containsKey(attributeName)
                ? Try.of(() -> attributes.get(attributeName))
                : Try
                    .failure(
                        new PrincipalAttributeException(
                            "No principal attribute mocked with name '" + attributeName + "'."));
        }
    }

    @Nonnull
    @Override
    public Principal mockPrincipal( @Nonnull final String principalId )
    {
        return mockPrincipal(principalId, null, null);
    }

    @Nonnull
    @Override
    public Principal mockPrincipal(
        @Nonnull final String principalId,
        @Nullable final Collection<Authorization> authorizations,
        @Nullable final Map<String, PrincipalAttribute> attributes )
    {
        resetPrincipalFacade.get();

        final Principal principal = Mockito.mock(Principal.class);

        lenient().when(principal.getPrincipalId()).thenReturn(principalId);

        if( authorizations != null ) {
            lenient().when(principal.getAuthorizations()).thenReturn(Sets.newHashSet(authorizations));
        } else {
            lenient().when(principal.getAuthorizations()).thenReturn(Collections.emptySet());
        }

        if( attributes != null ) {
            lenient()
                .when(principal.getAttribute(ArgumentMatchers.anyString()))
                .thenAnswer(new GetAttributeMockitoAnswer(attributes));
        } else {
            lenient()
                .when(principal.getAttribute(ArgumentMatchers.anyString()))
                .thenReturn(Try.failure(new PrincipalAttributeException("No principal attributes mocked.")));
        }

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
        return mockCurrentPrincipal(principalId, null, null);
    }

    @Nonnull
    @Override
    public Principal mockCurrentPrincipal(
        @Nonnull final String principalId,
        @Nullable final Collection<Authorization> authorizations,
        @Nullable final Map<String, PrincipalAttribute> attributes )
    {
        final Principal principal = mockPrincipal(principalId, authorizations, attributes);
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
