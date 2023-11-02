package com.sap.cloud.sdk.cloudplatform.resilience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

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

    @After
    public void resetDecorationStrategy()
    {
        ResilienceDecorator.resetDecorationStrategy();
    }

    @Test
    public void testGetDecorationStrategyReturnsSingleInstance()
    {
        final ResilienceDecorationStrategy singleStrategy = mock(ResilienceDecorationStrategy.class);
        mockDecorationStrategies(singleStrategy);

        ResilienceDecorator.resetDecorationStrategy();

        assertThat(ResilienceDecorator.getDecorationStrategy()).isSameAs(singleStrategy);
    }

    @Test
    public void testGetDecorationStrategyReturnsNoResilience()
    {
        mockDecorationStrategies();

        ResilienceDecorator.resetDecorationStrategy();

        assertThat(ResilienceDecorator.getDecorationStrategy())
            .isExactlyInstanceOf(NoResilienceDecorationStrategy.class);
    }

    @Test
    public void testGetDecorationStrategyThrowsOnMultipleStrategies()
    {
        final ResilienceDecorationStrategy firstStrategy = mock(ResilienceDecorationStrategy.class);
        final ResilienceDecorationStrategy secondInstance = mock(ResilienceDecorationStrategy.class);
        mockDecorationStrategies(firstStrategy, secondInstance);

        ResilienceDecorator.resetDecorationStrategy();

        assertThatThrownBy(ResilienceDecorator::getDecorationStrategy)
            .isExactlyInstanceOf(ObjectLookupFailedException.class);
    }

    private static void mockDecorationStrategies( @Nonnull final ResilienceDecorationStrategy... strategies )
    {
        final FacadeLocator.MockableInstance facadeLocator = mock(FacadeLocator.MockableInstance.class);
        when(facadeLocator.getFacades(eq(ResilienceDecorationStrategy.class))).thenReturn(Arrays.asList(strategies));

        FacadeLocator.setMockableInstance(facadeLocator);
    }
}
