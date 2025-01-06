package com.sap.cloud.sdk.datamodel.odata.generator;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;

import lombok.Data;

@Data
final class EntityMetadata
{
    private final JPackage namespacePackage;
    private final JDefinedClass generatedEntityClass;
    private final String entityTypeName;
    private final String entitySetName;
}
