package com.sap.cloud.sdk.cloudplatform.resilience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.exception.ObjectLookupFailedException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

public class ResilienceDecoratorTest
{
    @Before
    @After
    public void resetFacadeLocator()
    {
        FacadeLocator.setMockableInstance(new FacadeLocator.MockableInstance());
    }

    @Test
    public void testGetDefaultDecorationStrategyReturnsSingleInstance()
    {
        final ResilienceDecorationStrategy singleStrategy = mock(ResilienceDecorationStrategy.class);
        mockDecorationStrategies(singleStrategy);

        assertThat(ResilienceDecorator.getDefaultDecorationStrategy()).isSameAs(singleStrategy);
    }

    @Test
    public void testGetDefaultDecorationStrategyReturnsNoResilience()
    {
        mockDecorationStrategies();

        assertThat(ResilienceDecorator.getDefaultDecorationStrategy())
            .isExactlyInstanceOf(NoResilienceDecorationStrategy.class);
    }

    @Test
    public void testGetDefaultDecorationStrategyReturnsThrowingStrategy()
    {
        final ResilienceDecorationStrategy firstStrategy = mock(ResilienceDecorationStrategy.class);
        final ResilienceDecorationStrategy secondInstance = mock(ResilienceDecorationStrategy.class);
        mockDecorationStrategies(firstStrategy, secondInstance);

        assertThat(ResilienceDecorator.getDefaultDecorationStrategy())
            .isExactlyInstanceOf(ResilienceDecorator.ThrowingResilienceDecorationStrategy.class);
    }

    @Test
    public void testThrowingStrategyThrowsTheSameInstance()
    {
        final ObjectLookupFailedException expected = new ObjectLookupFailedException("test exception");
        final ResilienceConfiguration configuration = ResilienceConfiguration.empty("test configuration");

        final ResilienceDecorationStrategy sut = new ResilienceDecorator.ThrowingResilienceDecorationStrategy(expected);

        assertThatThrownBy(() -> sut.decorateCallable(() -> null, configuration)).isSameAs(expected);
        assertThatThrownBy(() -> sut.decorateCallable(() -> null, configuration)).isSameAs(expected);
        assertThatThrownBy(() -> sut.decorateSupplier(() -> null, configuration)).isSameAs(expected);
        assertThatThrownBy(() -> sut.decorateSupplier(() -> null, configuration)).isSameAs(expected);
    }

    private static void mockDecorationStrategies( @Nonnull final ResilienceDecorationStrategy... strategies )
    {
        final FacadeLocator.MockableInstance facadeLocator = mock(FacadeLocator.MockableInstance.class);
        when(facadeLocator.getFacades(eq(ResilienceDecorationStrategy.class)))
            .thenReturn(Arrays.stream(strategies).collect(Collectors.toList()));

        FacadeLocator.setMockableInstance(facadeLocator);
    }
}
