package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.List;

import javax.annotation.Nonnull;

import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
@SuppressWarnings( { "rawtypes", "deprecation" } )
class DataModelGeneratorConfig implements CodegenConfig
{
    @Delegate
    private final CodegenConfig config;

    @SuppressWarnings( "deprecation" )
    DataModelGeneratorConfig( @Nonnull final ClientOptInput clientOptInput )
    {
        this(clientOptInput.getConfig());
    }

    @Override
    @Nonnull
    public
        OperationsMap
        postProcessOperationsWithModels( @Nonnull final OperationsMap objs, @Nonnull final List<ModelMap> allModels )
    {
        for( final CodegenOperation op : objs.getOperations().getOperation() ) {
            final var noContent = op.responses == null || op.responses.stream().anyMatch(r -> "204".equals(r.code));
            op.vendorExtensions.put("x-return-nullable", op.isResponseOptional || noContent);
        }
        return config.postProcessOperationsWithModels(objs, allModels);
    }
}
