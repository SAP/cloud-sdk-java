package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;

class ForwardAuthTokenTest
{
    static Collection<TestCase> createTestCases()
    {
        return List
            .of(
                // forwardAuthToken = true && authType = NoAuthentication
                TestCaseBuilder
                    .forProperty(DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(), "true")
                    .and(DestinationProperty.AUTH_TYPE.getKeyName(), AuthenticationType.NO_AUTHENTICATION)
                    .expectTokenForwarding(),
                // forwardAuthToken = true && unset authType
                TestCaseBuilder
                    .forProperty(DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(), "true")
                    .expectTokenForwarding(),
                // forwardAuthToken = true && other authType
                TestCaseBuilder
                    .forProperty(DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(), "true")
                    .and(DestinationProperty.AUTH_TYPE.getKeyName(), AuthenticationType.BASIC_AUTHENTICATION)
                    .and(DestinationProperty.BASIC_AUTH_USERNAME.getKeyName(), "username")
                    .and(DestinationProperty.BASIC_AUTH_PASSWORD.getKeyName(), "securePassword")
                    .expectNoTokenForwarding(),
                // forwardAuthToken = false && authType = NoAuthentication
                TestCaseBuilder
                    .forProperty(DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(), "false")
                    .and(DestinationProperty.AUTH_TYPE.getKeyName(), AuthenticationType.NO_AUTHENTICATION)
                    .expectNoTokenForwarding(),
                // forwardAuthToken = false && unset authType
                TestCaseBuilder
                    .forProperty(DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(), "false")
                    .expectNoTokenForwarding(),
                // forwardAuthToken = false && other authType
                TestCaseBuilder
                    .forProperty(DestinationProperty.FORWARD_AUTH_TOKEN.getKeyName(), "false")
                    .and(DestinationProperty.AUTH_TYPE.getKeyName(), AuthenticationType.BASIC_AUTHENTICATION)
                    .and(DestinationProperty.BASIC_AUTH_USERNAME.getKeyName(), "username")
                    .and(DestinationProperty.BASIC_AUTH_PASSWORD.getKeyName(), "securePassword")
                    .expectNoTokenForwarding(),

                // HTML5.ForwardAuthToken = true && authType = NoAuthentication
                TestCaseBuilder
                    .forProperty(DestinationProperty.APPROUTER_FORWARD_AUTH_TOKEN.getKeyName(), "true")
                    .and(DestinationProperty.AUTH_TYPE.getKeyName(), AuthenticationType.NO_AUTHENTICATION)
                    .expectTokenForwarding(),
                // HTML5.ForwardAuthToken = true && unset authType
                TestCaseBuilder
                    .forProperty(DestinationProperty.APPROUTER_FORWARD_AUTH_TOKEN.getKeyName(), "true")
                    .expectTokenForwarding(),
                // HTML5.forwardAuthToken = true && other authType
                TestCaseBuilder
                    .forProperty(DestinationProperty.APPROUTER_FORWARD_AUTH_TOKEN.getKeyName(), "true")
                    .and(DestinationProperty.AUTH_TYPE.getKeyName(), AuthenticationType.BASIC_AUTHENTICATION)
                    .and(DestinationProperty.BASIC_AUTH_USERNAME.getKeyName(), "username")
                    .and(DestinationProperty.BASIC_AUTH_PASSWORD.getKeyName(), "securePassword")
                    .expectNoTokenForwarding(),
                // HTML5.ForwardAuthToken = false && authType = NoAuthentication
                TestCaseBuilder
                    .forProperty(DestinationProperty.APPROUTER_FORWARD_AUTH_TOKEN.getKeyName(), "false")
                    .and(DestinationProperty.AUTH_TYPE.getKeyName(), AuthenticationType.NO_AUTHENTICATION)
                    .expectNoTokenForwarding(),
                // HTML5.ForwardAuthToken = false && unset authType
                TestCaseBuilder
                    .forProperty(DestinationProperty.APPROUTER_FORWARD_AUTH_TOKEN.getKeyName(), "false")
                    .expectNoTokenForwarding(),
                // HTML5.ForwardAuthToken = false && other authType
                TestCaseBuilder
                    .forProperty(DestinationProperty.APPROUTER_FORWARD_AUTH_TOKEN.getKeyName(), "false")
                    .and(DestinationProperty.AUTH_TYPE.getKeyName(), AuthenticationType.BASIC_AUTHENTICATION)
                    .and(DestinationProperty.BASIC_AUTH_USERNAME.getKeyName(), "username")
                    .and(DestinationProperty.BASIC_AUTH_PASSWORD.getKeyName(), "securePassword")
                    .expectNoTokenForwarding(),

                // unset forwardAuthToken && authType = NoAuthentication
                TestCaseBuilder
                    .forProperty(DestinationProperty.AUTH_TYPE.getKeyName(), AuthenticationType.NO_AUTHENTICATION)
                    .expectNoTokenForwarding(),
                // unset forwardAuthToken && unset authType
                TestCaseBuilder.forNoProperties().expectNoTokenForwarding());
    }

    private enum AssertionType
    {
        TOKEN_IS_FORWARDED(ForwardAuthTokenTest::assertThatTokenIsForwarded),
        TOKEN_IS_NOT_FORWARDED(ForwardAuthTokenTest::assertThatTokenIsNotForwarded);

        private final Consumer<HttpDestination> assertion;

        AssertionType( final Consumer<HttpDestination> assertion )
        {
            this.assertion = assertion;
        }
    }

    private record TestCase( Map<String, Object> properties, AssertionType forwardingAssertion )
    {
    }

    private static class TestCaseBuilder
    {

        private final Map<String, Object> properties = new HashMap<>();

        TestCaseBuilder( String key, Object value )
        {
            properties.put(key, value);
        }

        TestCaseBuilder()
        {

        }

        static TestCaseBuilder forProperty( String key, Object value )
        {
            return new TestCaseBuilder(key, value);
        }

        static TestCaseBuilder forNoProperties()
        {
            return new TestCaseBuilder();
        }

        TestCaseBuilder and( final String key, final Object value )
        {
            properties.put(key, value);
            return this;
        }

        TestCase expectTokenForwarding()
        {
            return new TestCase(properties, AssertionType.TOKEN_IS_FORWARDED);
        }

        TestCase expectNoTokenForwarding()
        {
            return new TestCase(properties, AssertionType.TOKEN_IS_NOT_FORWARDED);
        }
    }

    @ParameterizedTest
    @MethodSource( "createTestCases" )
    void localDestinationShouldFulfillTestCase( @Nonnull final TestCase testCase )
    {
        final HttpDestination destination = buildLocalDestination(testCase.properties);
        testCase.forwardingAssertion.assertion.accept(destination);
    }

    private HttpDestination buildLocalDestination( Map<String, Object> properties )
    {
        return DefaultDestination.fromMap(properties).uri("sap.com").build().asHttp();
    }

    private static void assertThatTokenIsNotForwarded( final HttpDestination destination )
    {
        final Collection<Header> headers =
            RequestHeaderAccessor
                .executeWithHeaderContainer(Map.of(HttpHeaders.AUTHORIZATION, "token"), () -> destination.getHeaders());

        assertThat(headers).doesNotContain(new Header(HttpHeaders.AUTHORIZATION, "token"));
    }

    private static void assertThatTokenIsForwarded( final HttpDestination destination )
    {
        final Collection<Header> headers =
            RequestHeaderAccessor
                .executeWithHeaderContainer(Map.of(HttpHeaders.AUTHORIZATION, "token"), () -> destination.getHeaders());

        assertThat(headers).containsExactly(new Header(HttpHeaders.AUTHORIZATION, "token"));
    }
}
