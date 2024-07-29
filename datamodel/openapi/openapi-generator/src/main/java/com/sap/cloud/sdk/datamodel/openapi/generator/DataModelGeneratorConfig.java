package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.List;

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
    public static DataModelGeneratorConfig ofInput( ClientOptInput clientOptInput )
    {
        return new DataModelGeneratorConfig(clientOptInput.getConfig());
    }

    @Override
    public OperationsMap postProcessOperationsWithModels( OperationsMap objs, List<ModelMap> allModels )
    {

        for( CodegenOperation op : objs.getOperations().getOperation() ) {
            op.isResponseOptional |= op.responses == null || op.responses.stream().anyMatch(r -> "204".equals(r.code));
        }
        return config.postProcessOperationsWithModels(objs, allModels);
    }
}
