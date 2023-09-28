package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.security.BasicAuthenticationAccessor;
import com.sap.cloud.sdk.cloudplatform.security.BasicAuthenticationFacade;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;

import io.vavr.control.Try;

@Deprecated
class BasicCredentialsPrincipalExtractorTest
{
    private static final String someUserName = "someName";

    private BasicAuthenticationFacade mockedFacade;

    @BeforeEach
    void setUp()
    {
        mockedFacade = mock(BasicAuthenticationFacade.class);
        BasicAuthenticationAccessor.setBasicAuthenticationFacade(mockedFacade);
    }

    @AfterEach
    void tearDown()
    {
        BasicAuthenticationAccessor.setBasicAuthenticationFacade(null);
    }

    @Test
    void tryGetCurrentPrincipalShouldExtractUserName()
    {
        final BasicCredentials someCredentials = new BasicCredentials(someUserName, "somePassword");
        when(mockedFacade.tryGetBasicCredentials()).thenReturn(Try.success(someCredentials));

        final Try<Principal> retrievedPrincipal = new BasicCredentialsPrincipalExtractor().tryGetCurrentPrincipal();

        VavrAssertions
            .assertThat(retrievedPrincipal)
            .contains(new ScpCfPrincipal(someUserName, Collections.emptySet(), Collections.emptyMap()));
    }

    @Test
    void tryGetCurrentPrincipalShouldPassOnException()
    {
        when(mockedFacade.tryGetBasicCredentials()).thenReturn(Try.failure(new ShouldNotHappenException()));

        final Try<Principal> retrievedPrincipal = new BasicCredentialsPrincipalExtractor().tryGetCurrentPrincipal();

        VavrAssertions.assertThat(retrievedPrincipal).failBecauseOf(ShouldNotHappenException.class);
    }
}
