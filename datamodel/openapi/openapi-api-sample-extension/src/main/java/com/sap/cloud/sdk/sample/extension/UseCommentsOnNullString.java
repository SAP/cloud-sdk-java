package com.sap.cloud.sdk.sample.extension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.CodegenProperty;

import com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomization;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;

@Getter
public class UseCommentsOnNullString implements GeneratorCustomization.ToDefaultValue
{
    private final String configKey = "useDefaultTrue";
    private final String configValueDefault = "true";

    @SuppressWarnings( "rawtypes" )
    @Nullable
    @Override
    public String toDefaultValue(
        @Nonnull final ChainElementReturn<ToDefaultValue, String> chain,
        @Nonnull final CodegenProperty cp,
        @Nonnull final Schema schema )
    {
        final String defaultValue = chain.doNext(next -> next.get().toDefaultValue(next, cp, schema));
        return cp.isString && !cp.isEnum && defaultValue == null ? "null /* empty */" : defaultValue;
    }
}
