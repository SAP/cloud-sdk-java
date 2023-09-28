package com.sap.cloud.sdk.cloudplatform.thread;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyException;

public class LazyPropertyTest
{
    @Test
    @SuppressWarnings( "unchecked" )
    public void testGetValueIsEvaluatedOnce()
    {
        final Supplier<String> supplier = (Supplier<String>) mock(Supplier.class);
        when(supplier.get()).thenReturn("foo");

        final LazyProperty<String> sut = LazyProperty.of(supplier);
        assertThat(sut.hasBeenEvaluated()).isFalse();
        // supplier is not called upon instantiation
        verify(supplier, times(0)).get();

        assertThat(sut.getValue().get()).isEqualTo("foo");
        assertThat(sut.hasBeenEvaluated()).isTrue();
        // supplier is called when the value is actually requested
        verify(supplier, times(1)).get();

        assertThat(sut.getValue().get()).isEqualTo("foo");
        // supplier is called only once because the value is cached
        verify(supplier, times(1)).get();
    }

    @Test
    public void testCopiedPropertyIsAlsoLazy()
    {
        final Supplier<Integer> supplier = new AtomicInteger()::getAndIncrement;

        final LazyProperty<Integer> initial = LazyProperty.of(supplier);

        assertThat(initial.getValue().get()).isEqualTo(0);

        final Property<Integer> copy = initial.copy();
        assertThat(copy).isExactlyInstanceOf(LazyProperty.class);

        // the copy will call the supplier again
        assertThat(copy.getValue().get()).isEqualTo(1);
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testToStringDoesNotCallSupplier()
    {
        final Supplier<String> supplier = (Supplier<String>) mock(Supplier.class);
        when(supplier.get()).thenReturn("foo");

        final LazyProperty<String> sut = LazyProperty.of(supplier);

        assertThat(sut.toString()).isEqualTo("LazyProperty(value=(not yet evaluated))");
        assertThat(sut.hasBeenEvaluated()).isFalse();
        verify(supplier, times(0)).get();

        assertThat(sut.getValue().get()).isEqualTo("foo");
        assertThat(sut.toString()).isEqualTo("LazyProperty(value=foo)");
        assertThat(sut.hasBeenEvaluated()).isTrue();
        verify(supplier, times(1)).get();
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testSupplierReturnsNull()
    {
        final Supplier<String> supplier = (Supplier<String>) mock(Supplier.class);
        when(supplier.get()).thenReturn(null);

        final LazyProperty<String> sut = LazyProperty.of(supplier);
        assertThat(sut.hasBeenEvaluated()).isFalse();

        VavrAssertions
            .assertThat(sut.getValue())
            .isFailure()
            .failBecauseOf(ThreadContextPropertyException.class)
            .failReasonHasMessage("Property value cannot be null.");
        assertThat(sut.hasBeenEvaluated()).isTrue();
        verify(supplier, times(1)).get();
    }
}
