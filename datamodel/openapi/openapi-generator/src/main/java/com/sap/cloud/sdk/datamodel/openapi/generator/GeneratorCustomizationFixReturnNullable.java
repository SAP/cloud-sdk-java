package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.List;

import javax.annotation.Nonnull;

import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Fix nullable return object via injecting "x-return-nullable" extension.
 */
@Slf4j
@Getter
public class GeneratorCustomizationFixReturnNullable
    implements
    GeneratorCustomization,
    GeneratorCustomization.PostProcessOperationsWithModels
{
    private final String configKey = "fixReturnNullable";
    private final String configValueDefault = "true";

    @Override
    public @Nonnull OperationsMap postProcessOperationsWithModels(
        @Nonnull final JavaClientCodegen ref,
        @Nonnull final OperationsMap ops,
        @Nonnull final List<ModelMap> allModels )
    {
        for( final CodegenOperation op : ops.getOperations().getOperation() ) {
            final var noContent =
                op.isResponseOptional
                    || op.responses == null
                    || op.responses.stream().anyMatch(r -> "204".equals(r.code));
            op.vendorExtensions.put("x-return-nullable", op.returnType != null && noContent);
        }
        return ops;
    }
}
