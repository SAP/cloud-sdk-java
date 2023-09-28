package com.sap.cloud.sdk.cloudplatform.thread;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.junit.Test;

import io.vavr.control.Try;
import lombok.SneakyThrows;

public class DefaultThreadContextTest
{
    @Test
    public void testEqualityTrivial()
    {
        assertThat(new DefaultThreadContext()).isEqualTo(new DefaultThreadContext());
    }

    @Test
    public void testEqualityWithProperties()
    {
        final DefaultThreadContext context1 = new DefaultThreadContext();
        context1.setPropertyIfAbsent("foo", Property.ofTry(Try.success("bar")));
        context1.setPropertyIfAbsent("baz", Property.ofTry(Try.failure(new IllegalStateException())));
        context1.removeProperty("baz");

        final DefaultThreadContext context2 = new DefaultThreadContext();
        context2.setPropertyIfAbsent("foo", Property.ofTry(Try.success("bar")));

        assertThat(context1).isEqualTo(context2);
    }

    @SneakyThrows
    @Test
    public void testEqualityOverThreads()
    {
        final DefaultThreadContext contextThisThread = new DefaultThreadContext();
        final DefaultThreadContext contextNewThread = CompletableFuture.supplyAsync(DefaultThreadContext::new).get();

        assertThat(contextThisThread).isEqualTo(contextNewThread);
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testDuplicateCopiesProperties()
    {
        final Property<String> copiedProperty = Property.of("foo");

        final Property<String> property = (Property<String>) mock(Property.class);
        when(property.getValue()).thenReturn(Try.success("foo"));
        when(property.copy()).thenReturn(copiedProperty);

        final DefaultThreadContext initial = new DefaultThreadContext();
        initial.setProperty("property", property);

        final ThreadContext duplicated = initial.duplicate();
        assertThat(duplicated.containsProperty("property")).isTrue();
        assertThat(duplicated.getPropertyValue("property")).isSameAs(copiedProperty.getValue());

        verify(property, times(1)).copy();
    }

    @Test
    public void testLazyPropertiesCanHaveDifferentValues()
    {
        final Supplier<Integer> supplier = new AtomicInteger()::getAndIncrement;

        final DefaultThreadContext initial = new DefaultThreadContext();
        initial.setProperty("property", LazyProperty.of(supplier));

        final ThreadContext duplicated = initial.duplicate();
        assertThat(duplicated.containsProperty("property")).isTrue();
        // we are accessing the duplicated value first, so it will have the lower number
        assertThat(duplicated.<Integer> getPropertyValue("property").get()).isEqualTo(0);
        // the property of the initial context is evaluated later and, therefore, will have the higher number
        assertThat(initial.<Integer> getPropertyValue("property").get()).isEqualTo(1);
    }
}
