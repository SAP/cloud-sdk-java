/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Helper JUnit rule/extension to allow for customized tokens in {@link AuthTokenAccessor}. <br>
 * Sample usage:
 *
 * <pre>
 * &#64;Rule // JUnit 4
 * &#64;RegisterExtension // JUnit 5
 * TokenRule rule = TokenRule.createXsuaa();
 * ...
 * rule.getTokenBuilder().withClaim("foo", "bar");
 * </pre>
 */
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public class TokenRule implements TestRule, AfterEachCallback, BeforeEachCallback
{
    @Getter
    private final JWTCreator.Builder tokenBuilder;

    /**
     * Register an XSUAA token for resolving current token via {@link AuthTokenAccessor}. <br>
     * An XSUAA token is recognized when its payload contains {@code {"ext_attr":{"enhancer":"XSUAA"}}}.
     *
     * @return A new instance.
     */
    static TokenRule createXsuaa()
    {
        final Map<String, String> attrEnhancer =
            Collections
                .singletonMap(
                    DestinationRetrievalStrategyResolver.JWT_ATTR_ENHANCER,
                    DestinationRetrievalStrategyResolver.JWT_ATTR_XSUAA);
        return new TokenRule(JWT.create().withClaim(DestinationRetrievalStrategyResolver.JWT_ATTR_EXT, attrEnhancer));
    }

    @Override
    public Statement apply( final Statement statement, final Description description )
    {
        return new Statement()
        {
            @Override
            public void evaluate()
                throws Throwable
            {
                beforeEach(null);
                try {
                    statement.evaluate();
                }
                finally {
                    afterEach(null);
                }
            }
        };
    }

    @Override
    public void afterEach( @Nullable final ExtensionContext extensionContext )
    {
        AuthTokenAccessor.setAuthTokenFacade(null);
    }

    @Override
    public void beforeEach( @Nullable final ExtensionContext extensionContext )
    {
        final AuthToken token = new AuthToken(JWT.decode(tokenBuilder.sign(Algorithm.none())));
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(token));
    }
}
