package com.sap.cloud.sdk.cloudplatform.security;

import static org.assertj.vavr.api.VavrAssertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.security.exception.BasicAuthenticationAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextFacade;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadLocalThreadContextFacade;

import io.vavr.control.Try;
import lombok.Builder;
import lombok.Value;
import lombok.With;

class DefaultBasicAuthenticationFacadeTest
{
    private static final BasicCredentials BASIC_1_CREDENTIALS = new BasicCredentials("username", "password");
    private static final String BaSiC_1_BASE64 = "BaSiC dXNlcm5hbWU6cGFzc3dvcmQ=";
    private static final String BASIC_1_BASE64 = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";
    private static final BasicCredentials BASIC_2_CREDENTIALS = new BasicCredentials("foo", "bar");
    private static final String BASIC_2_BASE64 = "Basic Zm9vOmJhcg==";
    private static final BasicCredentials UNICODE_CREDENTIALS = new BasicCredentials("😀", "😡");
    private static final String UNICODE_BASE64 = "Basic 8J+YgDrwn5ih";

    @AfterEach
    public void resetAccessors()
    {
        ThreadContextAccessor.tryGetThreadContextFacade().peek(ThreadContextFacade::removeCurrentContext);
        ThreadContextAccessor.setThreadContextFacade(null);
        RequestHeaderAccessor.setHeaderFacade(null);
    }

    private static List<TestCase> provideTestCases()
    {
        return Arrays
            .asList(
                // with ThreadContextAccessor should read credentials from thread context API
                TestCase
                    .builder()
                    .threadContextAccessor(Values.of(BASIC_1_CREDENTIALS))
                    .assertSuccess(BASIC_1_CREDENTIALS),

                // with RequestHeaderAccessor should read credentials from request theader API
                TestCase.builder().requestHeaderAccessor(Values.of(BASIC_1_BASE64)).assertSuccess(BASIC_1_CREDENTIALS),

                // with ThreadContextAccessor and RequestHeaderAccessor should read credentials from thread context API
                TestCase
                    .builder()
                    .requestHeaderAccessor(Values.of(BASIC_1_BASE64))
                    .threadContextAccessor(Values.of(BASIC_2_CREDENTIALS))
                    .assertSuccess(BASIC_2_CREDENTIALS),

                // fail on no-auth headers in RequestHeaderAccessor
                TestCase.builder().requestHeaderAccessor(Values.NULL).assertError(),

                // fail on empty auth headers in RequestHeaderAccessor
                TestCase.builder().requestHeaderAccessor(Values.EMPTY).assertError());
    }

    @ParameterizedTest
    @MethodSource( "provideTestCases" )
    public void testBasicAuthentication( @Nonnull final TestCase testCase )
    {
        testCase.setupThreadContextAccessor();
        testCase.setupRequestHeaderAccessor();

        final Try<BasicCredentials> credentials = new DefaultBasicAuthenticationFacade().tryGetBasicCredentials();
        testCase.assertion.accept(credentials);
    }

    @Builder
    @Value
    @With
    private static class TestCase
    {
        @Builder.Default
        Values threadContextAccessor = Values.DISABLED;
        @Builder.Default
        Values requestHeaderAccessor = Values.DISABLED;
        @Nonnull
        Consumer<Try<BasicCredentials>> assertion;

        private void setupThreadContextAccessor()
        {
            if( threadContextAccessor.isAccessorEnabled() ) {
                final ThreadContext context = new DefaultThreadContext();
                final ThreadLocalThreadContextFacade threadContextFacade = new ThreadLocalThreadContextFacade();
                threadContextFacade.setCurrentContext(context);
                ThreadContextAccessor.setThreadContextFacade(threadContextFacade);
                context
                    .setPropertyIfAbsent(
                        BasicAuthenticationThreadContextListener.PROPERTY_BASIC_AUTH_HEADER,
                        Property.of(Objects.requireNonNull(threadContextAccessor.getValues()).get(0)));
            }
        }

        @SuppressWarnings( "unchecked" )
        private void setupRequestHeaderAccessor()
        {
            if( requestHeaderAccessor.isAccessorEnabled() ) {
                final DefaultRequestHeaderContainer.Builder container = DefaultRequestHeaderContainer.builder();
                if( requestHeaderAccessor.getValues() != null ) {
                    container.withHeader(HttpHeaders.AUTHORIZATION, (List<String>) requestHeaderAccessor.getValues());
                }
                RequestHeaderAccessor.setHeaderFacade(() -> Try.success(container.build()));
            }
        }

        static class TestCaseBuilder
        {
            public TestCase assertSuccess( @Nonnull final BasicCredentials credentials )
            {
                return assertion(c -> assertThat(c).contains(credentials)).build();
            }

            public TestCase assertError()
            {
                return assertion(c -> assertThat(c).failBecauseOf(BasicAuthenticationAccessException.class)).build();
            }
        }
    }

    @Value
    private static class Values
    {
        boolean accessorEnabled;
        List<?> values;

        static Values DISABLED = new Values(false, Collections.emptyList()); // no Accessor change
        static Values EMPTY = new Values(true, Collections.emptyList()); // Accessor returns empty values
        static Values NULL = new Values(true, null); // Accessor returns null

        static Values of( @Nonnull final Object... values )
        {
            return new Values(true, Arrays.asList(values));
        }
    }
}
