package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.languages.JavaClientCodegen;

import lombok.Getter;

/**
 * Fix isIsBoolean() to isBoolean() for fields specified as `"isBoolean":{"type":"boolean"}`.
 */
@Getter
public class GeneratorCustomizationFixRedundantIsBooleanPrefix
    implements
    GeneratorCustomization,
    GeneratorCustomization.ToBooleanGetter
{
    private static final Predicate<String> DOUBLE_IS_PATTERN = Pattern.compile("^isIs[A-Z]").asPredicate();

    private final String configKey = "fixRedundantIsBooleanPrefix";

    @Override
    public String toBooleanGetter(
        @Nonnull final JavaClientCodegen ref,
        @Nullable final String superValue,
        @Nullable final String name )
    {
        if( superValue != null && DOUBLE_IS_PATTERN.test(superValue) ) {
            return "is" + superValue.substring(4);
        }
        return superValue;
    }
}
