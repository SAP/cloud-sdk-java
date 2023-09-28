package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;

import io.vavr.control.Try;
import lombok.Value;

@SuppressWarnings( "deprecation" )
class OAuth2AuthTokenPrincipalExtractorTest
{
    private static final String CLIENT_ID = "someId";
    private static final String USER_NAME = "some userName";
    private static final String PREFIX = "prefix";

    private static final List<String> SCOPES =
        Arrays
            .asList(
                PREFIX + ".Tags.View",
                "uaa.resource",
                PREFIX + ".Locations.View",
                PREFIX + ".Equipment.View",
                PREFIX + ".Persons.View",
                PREFIX + ".Workplaces.View");

    private static final List<String> PREFIX_STRIPPED_SCOPES =
        SCOPES
            .stream()
            .filter(scope -> scope.startsWith(PREFIX + "."))
            .map(name -> name.replace(PREFIX + ".", ""))
            .collect(Collectors.toList());

    private static final LocalScopePrefixProvider PREFIX_PROVIDER = () -> Try.success(PREFIX);

    @After
    public void cleanupAccessors()
    {
        AuthTokenAccessor.setAuthTokenFacade(null);
    }

    @Test
    void testExceptionIsThrownOnNonExistingGrantType()
    {
        mockAuthTokenFacade(JWT.create().withClaim("user_name", USER_NAME));

        VavrAssertions
            .assertThat(new OAuth2AuthTokenPrincipalExtractor(PREFIX_PROVIDER).tryGetCurrentPrincipal())
            .failBecauseOf(PrincipalAccessException.class);
    }

    @Test
    void testExceptionIsThrownOnNonExistingUserNameClaim()
    {
        mockAuthTokenFacade(JWT.create().withClaim("grant_type", "password"));

        VavrAssertions
            .assertThat(new OAuth2AuthTokenPrincipalExtractor(PREFIX_PROVIDER).tryGetCurrentPrincipal())
            .failBecauseOf(PrincipalAccessException.class);
    }

    @Test
    void testExceptionIsThrownOnNonExistingClientCredentialsClaim()
    {
        mockAuthTokenFacade(JWT.create().withClaim("grant_type", "client_credentials"));

        VavrAssertions
            .assertThat(new OAuth2AuthTokenPrincipalExtractor(PREFIX_PROVIDER).tryGetCurrentPrincipal())
            .failBecauseOf(PrincipalAccessException.class);
    }

    @Test
    void testExceptionIsThrownOnUnknownGrantType()
    {
        mockAuthTokenFacade(JWT.create().withClaim("grant_type", "something_unknown"));

        VavrAssertions
            .assertThat(new OAuth2AuthTokenPrincipalExtractor(PREFIX_PROVIDER).tryGetCurrentPrincipal())
            .failBecauseOf(PrincipalAccessException.class);
    }

    @Test
    void testExceptionIsThrownOnMissingAuthToken()
    {
        mockAuthTokenFacadeWithMissingAuthToken();

        VavrAssertions
            .assertThat(new OAuth2AuthTokenPrincipalExtractor(PREFIX_PROVIDER).tryGetCurrentPrincipal())
            .isFailure();
    }

    @Test
    void testCustomGrantTypeFunctionIsUsed()
    {
        final String customGrantType = "something_unknown";
        final String customIdKey = "custom_id";
        final String customIdValue = "custom_value";
        mockAuthTokenFacade(
            JWT.create().withClaim("grant_type", customGrantType).withClaim(customIdKey, customIdValue));

        final OAuth2AuthTokenPrincipalExtractor principalFacade =
            new OAuth2AuthTokenPrincipalExtractor(PREFIX_PROVIDER);
        principalFacade.setIdExtractorFunction(customGrantType, jwt -> jwt.getClaim(customIdKey).asString());

        final Try<Principal> currentPrincipal = principalFacade.tryGetCurrentPrincipal();

        VavrAssertions.assertThat(currentPrincipal).isSuccess();

        assertThat(currentPrincipal.get().getPrincipalId()).isEqualTo(customIdValue);
        assertThat(
            currentPrincipal
                .get()
                .getAuthorizations()
                .stream()
                .map(com.sap.cloud.sdk.cloudplatform.security.Authorization::getName))
            .containsExactlyInAnyOrderElementsOf(PREFIX_STRIPPED_SCOPES);
    }

    @Test
    void testCustomGrantTypeFunctionsExceptionIsWrapped()
    {
        final String customGrantType = "something_unknown";
        mockAuthTokenFacade(JWT.create().withClaim("grant_type", customGrantType));

        final OAuth2AuthTokenPrincipalExtractor principalFacade =
            new OAuth2AuthTokenPrincipalExtractor(PREFIX_PROVIDER);
        principalFacade.setIdExtractorFunction(customGrantType, jwt -> {
            throw new ShouldNotHappenException();
        });

        assertThatThrownBy(() -> principalFacade.tryGetCurrentPrincipal().get())
            .isInstanceOf(PrincipalAccessException.class)
            .hasCauseInstanceOf(ShouldNotHappenException.class);
    }

    @Test
    void testParseJwtWithSingleAudience()
    {
        final String encodedJwt =
            JWT
                .create()
                .withAudience("audience")
                .withClaim("grant_type", "password")
                .withClaim("user_name", USER_NAME)
                .sign(Algorithm.none());

        final DecodedJWT decodedJwt = JWT.decode(encodedJwt);
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(new AuthToken(decodedJwt)));

        final OAuth2AuthTokenPrincipalExtractor principalFacade =
            new OAuth2AuthTokenPrincipalExtractor(PREFIX_PROVIDER);

        final Try<Principal> principalTry = principalFacade.tryGetCurrentPrincipal();
        assertThat(principalTry.isSuccess()).isTrue();
        assertThat(principalTry.get().getAuthorizationsByAudience())
            .containsExactly(
                entry(new com.sap.cloud.sdk.cloudplatform.security.Audience("audience"), Collections.emptySet()));
    }

    @ParameterizedTest
    @MethodSource( "grantTypeMappings" )
    void testGrantTypeMappings( @Nonnull final Mapping mapping )
    {
        mockAuthTokenFacade(
            JWT
                .create()
                .withClaim("grant_type", mapping.grantType)
                // we set both values (userName and clientId) here and expect one of those as the principalId
                .withClaim("user_name", USER_NAME)
                .withClaim("client_id", CLIENT_ID));

        final Try<Principal> currentPrincipal =
            new OAuth2AuthTokenPrincipalExtractor(PREFIX_PROVIDER).tryGetCurrentPrincipal();

        VavrAssertions.assertThat(currentPrincipal).isSuccess();

        assertThat(currentPrincipal.get().getPrincipalId()).isEqualTo(mapping.expectedPrincipalId);
        assertThat(
            currentPrincipal
                .get()
                .getAuthorizations()
                .stream()
                .map(com.sap.cloud.sdk.cloudplatform.security.Authorization::getName))
            .containsExactlyInAnyOrderElementsOf(PREFIX_STRIPPED_SCOPES);
    }

    private static Stream<Mapping> grantTypeMappings()
    {
        return Stream
            .of(
                Mapping.of("password", USER_NAME),
                Mapping.of("client_credentials", CLIENT_ID),
                Mapping.of("authorization_code", USER_NAME),
                Mapping.of("user_token", USER_NAME),
                Mapping.of("urn:ietf:params:oauth:grant-type:saml2-bearer", USER_NAME),
                Mapping.of("urn:ietf:params:oauth:grant-type:jwt-bearer", USER_NAME));
    }

    @Value( staticConstructor = "of" )
    private static class Mapping
    {
        String grantType;
        String expectedPrincipalId;

    }

    private void mockAuthTokenFacade( final JWTCreator.Builder jwtBuilder )
    {
        final String encodedJwt =
            jwtBuilder.withArrayClaim("scope", SCOPES.toArray(new String[0])).sign(Algorithm.none());
        final DecodedJWT decodedJwt = JWT.decode(encodedJwt);

        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(new AuthToken(decodedJwt)));
    }

    private void mockAuthTokenFacadeWithMissingAuthToken()
    {
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.failure(new AuthTokenAccessException("Auth token not mocked.")));
    }
}
