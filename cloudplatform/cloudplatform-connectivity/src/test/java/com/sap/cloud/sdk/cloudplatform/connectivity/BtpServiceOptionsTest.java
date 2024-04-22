package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.OptionsEnhancer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.Value;

class BtpServiceOptionsTest
{
    @Test
    void testCreateGenericEnumOption()
    {
        // collect all available enum enhancers
        final List<Class<?>> enumEnhancers =
            Arrays
                .stream(BtpServiceOptions.class.getClasses())
                .filter(Class::isEnum)
                .filter(c -> (c.getModifiers() & Modifier.PUBLIC) > 0)
                .toList();

        for( final Class<?> enumClass : enumEnhancers ) {
            final String enumName = enumClass.getSimpleName();
            for( final Object enumEntry : enumClass.getEnumConstants() ) {
                assertThat(BtpServiceOptions.withGenericOption(enumName, enumEntry.toString())).isSameAs(enumEntry);
            }
        }
    }

    @Test
    void testEnumOptionIsCaseSensitive()
    {
        final Map<String, Function<Object[], OptionsEnhancer<?>>> map = new HashMap<>();

        BtpServiceOptions.addGenericEnumEnhancerBuilder(map, TestEnumEnhancer.class);

        assertThat(map.get("TestEnumEnhancer").apply(new Object[] { "OPTION1" })).isSameAs(TestEnumEnhancer.OPTION1);
        assertThatThrownBy(() -> map.get("TestEnumEnhancer").apply(new Object[] { "option1" }))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testProvidingTooFewArguments()
    {
        final Map<String, Function<Object[], OptionsEnhancer<?>>> map = new HashMap<>();

        BtpServiceOptions.addGenericEnumEnhancerBuilder(map, TestEnumEnhancer.class);

        assertThatThrownBy(() -> map.get("TestEnumEnhancer").apply(new Object[] {}))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testProvidingTooManyArguments()
    {
        final Map<String, Function<Object[], OptionsEnhancer<?>>> map = new HashMap<>();

        BtpServiceOptions.addGenericEnumEnhancerBuilder(map, TestEnumEnhancer.class);

        assertThat(map.get("TestEnumEnhancer").apply(new Object[] { "OPTION1", "OPTION2" }))
            .isSameAs(TestEnumEnhancer.OPTION1);
    }

    @Test
    void testProvidingWrongArgumentType()
    {
        final Map<String, Function<Object[], OptionsEnhancer<?>>> map = new HashMap<>();

        BtpServiceOptions.addGenericEnumEnhancerBuilder(map, TestEnumEnhancer.class);

        assertThatThrownBy(() -> map.get("TestEnumEnhancer").apply(new Object[] { 1 }))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource( "genericIasParameters" )
    void testCreateIasOption( @Nonnull final IasParameter parameter )
    {
        final OptionsEnhancer<?> actual =
            BtpServiceOptions
                .withGenericOption(BtpServiceOptions.IasOptions.class.getSimpleName(), parameter.arguments);
        assertThat(actual).isNotNull();
        parameter.getValidator().accept(actual);
    }

    @Test
    void testAllIasMethodsAreTested()
    {
        final List<Method> methods =
            new ArrayList<>(
                Arrays
                    .stream(BtpServiceOptions.IasOptions.class.getDeclaredMethods())
                    .filter(m -> (m.getModifiers() & Modifier.PUBLIC) > 0)
                    .filter(m -> (m.getModifiers() & Modifier.STATIC) > 0)
                    .toList());
        // sanity check
        assertThat(methods).isNotEmpty();

        for( final IasParameter testedMethod : genericIasParameters().toList() ) {
            methods.removeIf(testedMethod::matches);
        }

        assertThat(methods).isEmpty();
    }

    @Test
    void testProvidingInvalidIasMethod()
    {
        assertThatThrownBy(
            () -> BtpServiceOptions
                .withGenericOption(BtpServiceOptions.IasOptions.class.getSimpleName(), "invalidMethod"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testProvidingInvalidIasParameter()
    {
        assertThatThrownBy(
            () -> BtpServiceOptions
                .withGenericOption(BtpServiceOptions.IasOptions.class.getSimpleName(), "withTargetUri", 1337))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testProvidingTooFewIasParameters()
    {
        assertThatThrownBy(
            () -> BtpServiceOptions
                .withGenericOption(BtpServiceOptions.IasOptions.class.getSimpleName(), "withTargetUri"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testProvidingTooManyIasParameters()
    {
        assertThat(
            BtpServiceOptions
                .withGenericOption(
                    BtpServiceOptions.IasOptions.class.getSimpleName(),
                    "withTargetUri",
                    "https://foo.baz",
                    "https://bar.baz"))
            .isEqualTo(BtpServiceOptions.IasOptions.withTargetUri("https://foo.baz"));
    }

    static Stream<IasParameter> genericIasParameters()
    {
        return Stream
            .of(
                new IasParameter(
                    "withTargetUri",
                    new Object[] { "https://example.com" },
                    actual -> assertThat(actual)
                        .isEqualTo(BtpServiceOptions.IasOptions.withTargetUri("https://example.com"))),
                new IasParameter(
                    "withTargetUri",
                    new Object[] { URI.create("https://example.com") },
                    actual -> assertThat(actual)
                        .isEqualTo(BtpServiceOptions.IasOptions.withTargetUri(URI.create("https://example.com")))),
                new IasParameter(
                    "withoutTokenForTechnicalProviderUser",
                    new Object[0],
                    actual -> assertThat(actual)
                        .isEqualTo(BtpServiceOptions.IasOptions.withoutTokenForTechnicalProviderUser())),
                new IasParameter(
                    "withApplicationName",
                    new Object[] { "applicationName" },
                    actual -> assertThat(actual)
                        .isEqualTo(BtpServiceOptions.IasOptions.withApplicationName("applicationName"))),
                new IasParameter(
                    "withConsumerClient",
                    new Object[] { "consumerClient" },
                    actual -> assertThat(actual)
                        .isEqualTo(BtpServiceOptions.IasOptions.withConsumerClient("consumerClient"))),
                new IasParameter(
                    "withConsumerClient",
                    new Object[] { "consumerClient", "consumerTenant" },
                    actual -> assertThat(actual)
                        .isEqualTo(
                            BtpServiceOptions.IasOptions.withConsumerClient("consumerClient", "consumerTenant"))));
    }

    @Value
    private static class IasParameter
    {
        Object[] arguments;
        Consumer<OptionsEnhancer<?>> validator;

        public IasParameter(
            @Nonnull final String methodName,
            @Nonnull final Object[] arguments,
            @Nonnull final Consumer<OptionsEnhancer<?>> validator )
        {
            this.arguments = new Object[arguments.length + 1];
            this.arguments[0] = methodName;
            System.arraycopy(arguments, 0, this.arguments, 1, arguments.length);
            this.validator = validator;
        }

        public String getMethodName()
        {
            return (String) arguments[0];
        }

        public boolean matches( @Nonnull final Method method )
        {
            if( !getMethodName().equals(method.getName()) ) {
                return false;
            }

            if( arguments.length - 1 != method.getParameterCount() ) {
                return false;
            }

            for( int i = 1; i < arguments.length; i++ ) {
                if( !arguments[i].getClass().equals(method.getParameterTypes()[i - 1]) ) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public String toString()
        {
            return getMethodName() + "(" + Arrays.toString(arguments) + ")";
        }
    }

    private enum TestEnumEnhancer implements OptionsEnhancer<TestEnumEnhancer>
    {
        OPTION1,
        OPTION2;

        @Override
        public TestEnumEnhancer getValue()
        {
            return this;
        }
    }
}
