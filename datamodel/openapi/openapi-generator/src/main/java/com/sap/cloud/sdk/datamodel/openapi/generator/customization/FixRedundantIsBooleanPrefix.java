package com.sap.cloud.sdk.datamodel.openapi.generator.customization;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomization;

import lombok.Getter;

/**
 * Fix isIsBoolean() to isBoolean() for fields specified as `"isBoolean":{"type":"boolean"}`.
 */
@Getter
public class FixRedundantIsBooleanPrefix implements GeneratorCustomization, GeneratorCustomization.ToBooleanGetter
{
    private static final Predicate<String> DOUBLE_IS_PATTERN = Pattern.compile("^isIs[A-Z]").asPredicate();

    private final String configKey = "fixRedundantIsBooleanPrefix";

    @Nullable
    @Override
    public
        String
        toBooleanGetter( @Nonnull final ChainElementReturn<ToBooleanGetter, String> chain, @Nullable final String name )
    {
        final String superValue = chain.doNext(next -> next.get().toBooleanGetter(next, name));

        if( superValue != null && DOUBLE_IS_PATTERN.test(superValue) ) {
            return "is" + superValue.substring(4);
        }
        return superValue;
    }
}
