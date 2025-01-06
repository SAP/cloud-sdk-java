package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;

class TimeLimiterTest
{
    private static final TimeLimiterConfiguration timeLimiterConfig =
        TimeLimiterConfiguration.of().timeoutDuration(Duration.ofMillis(100));
    private static final ResilienceConfiguration resilienceConfiguration =
        ResilienceConfiguration.of("TimeLimiterTest.static").timeLimiterConfiguration(timeLimiterConfig);

    @Test
    @Disabled( "Test is unreliable on jenkins. Use this to verify the timeout behaviour locally." )
    void testTimeLimiterWithRepeatingCalls()
        throws Exception
    {
        final Callable<Integer> callable = spy(new TestCallable());
        final Callable<?> wrappedCallable = ResilienceDecorator.decorateCallable(callable, resilienceConfiguration);
        final int attempts = 15;

        for( int i = 0; i < attempts; i++ ) {
            assertThatThrownBy(wrappedCallable::call)
                .isExactlyInstanceOf(ResilienceRuntimeException.class)
                .hasCauseExactlyInstanceOf(TimeoutException.class);
        }
        verify(callable, times(attempts)).call();
    }

    @Test
    void testDisabledTimeLimiter()
        throws Exception
    {
        // Test that the callable runs in the main thread
        final long id = Thread.currentThread().getId();

        final Callable<?> callable = mock(Callable.class);
        when(callable.call()).then((Answer<Void>) ( invocation ) -> {
            assertThat(id).isEqualTo(Thread.currentThread().getId());
            return null;
        });

        final ResilienceConfiguration confWithoutTimeout =
            ResilienceConfiguration
                .of(UUID.randomUUID().toString())
                .timeLimiterConfiguration(TimeLimiterConfiguration.disabled());
        ResilienceDecorator.executeCallable(callable, confWithoutTimeout);

        verify(callable).call();
    }

    private static class TestCallable implements Callable<Integer>
    {
        @Override
        public Integer call()
            throws Exception
        {
            Thread.sleep(10000);
            fail("Thread wasn't canceled");
            return -1;
        }
    }
}
