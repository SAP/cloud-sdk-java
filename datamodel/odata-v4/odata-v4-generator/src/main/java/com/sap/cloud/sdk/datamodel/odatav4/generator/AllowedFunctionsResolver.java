/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmPropertyValue;
import org.slf4j.Logger;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.vavr.control.Try;

class AllowedFunctionsResolver
{
    private static final Logger logger = MessageCollector.getLogger(AllowedFunctionsResolver.class);

    private static final String[] TERMS_READ_RESTRICTIONS =
        { "Capabilities.ReadRestrictions", "SAP__capabilities.ReadRestrictions" };
    private static final String[] TERMS_INSERT_RESTRICTIONS =
        { "Capabilities.InsertRestrictions", "SAP__capabilities.InsertRestrictions" };
    private static final String[] TERMS_UPDATE_RESTRICTIONS =
        { "Capabilities.UpdateRestrictions", "SAP__capabilities.UpdateRestrictions" };
    private static final String[] TERMS_DELETE_RESTRICTIONS =
        { "Capabilities.DeleteRestrictions", "SAP__capabilities.DeleteRestrictions" };

    // ReadByKeyRestrictions is a property of Capabilities.ReadRestrictions, not an annotation of its own.
    private static final String PROPERTY_READ_BY_KEY = "ReadByKeyRestrictions";
    private static final String PROPERTY_READABLE = "Readable";
    private static final String PROPERTY_INSERTABLE = "Insertable";
    private static final String PROPERTY_UPDATABLE = "Updatable";
    private static final String PROPERTY_DELETABLE = "Deletable";

    private final Charset encoding;

    AllowedFunctionsResolver( final Charset encoding )
    {
        this.encoding = encoding;
    }

    @Nonnull
    Multimap<String, ApiFunction> findAllowedFunctions( @Nonnull final Edm metadata, @Nullable final File swaggerFile )
    {
        Multimap<String, ApiFunction> allowedFunctionsByEntity = null;
        if( swaggerFile != null && swaggerFile.exists() ) {
            allowedFunctionsByEntity = readFromSwaggerFile(swaggerFile);
        } else if( swaggerFile != null ) {
            logger
                .debug(
                    "Could not find swagger file at {}. Trying to read the allowed functions from the metadata file.",
                    swaggerFile.getAbsolutePath());

        } else {
            logger.debug("No swagger file given. Trying to read the allowed functions from the metadata file.");
        }
        if( allowedFunctionsByEntity == null ) {
            allowedFunctionsByEntity = readFromMetadata(metadata);
        }
        return allowedFunctionsByEntity;
    }

    private Multimap<String, ApiFunction> readFromSwaggerFile( final File swaggerFile )
    {
        final Multimap<String, ApiFunction> allowedFunctionsByEntity =
            MultimapBuilder.hashKeys().hashSetValues().build();
        final Iterable<Map.Entry<String, JsonElement>> paths = readPaths(swaggerFile);

        for( final Map.Entry<String, JsonElement> pathsEntry : paths ) {
            handleSwaggerPath(allowedFunctionsByEntity, pathsEntry);
        }

        return allowedFunctionsByEntity.isEmpty() ? null : allowedFunctionsByEntity;
    }

    private Iterable<Map.Entry<String, JsonElement>> readPaths( final File swaggerFile )
    {
        try( Reader reader = new InputStreamReader(Files.newInputStream(swaggerFile.toPath()), encoding) ) {
            final JsonElement swaggerJson = JsonParser.parseReader(reader);
            return swaggerJson.getAsJsonObject().get("paths").getAsJsonObject().entrySet();
        }
        catch( final IOException e ) {
            throw new ODataGeneratorReadException(e);
        }
    }

    private void handleSwaggerPath(
        final Multimap<String, ApiFunction> allowedFunctionsByEntity,
        final Map.Entry<String, JsonElement> pathsEntry )
    {
        final String pathEntry = pathsEntry.getKey();
        final String[] split = StringUtils.removeStart(pathEntry, "/").split("\\(");
        final String pathEntitySet = split[0];

        // Mark entity set as navigable.
        allowedFunctionsByEntity.put(pathEntitySet, ApiFunction.NAVIGATE);

        for( final Map.Entry<String, JsonElement> entry : pathsEntry.getValue().getAsJsonObject().entrySet() ) {
            switch( entry.getKey() ) {
                case "get":
                    if( split.length > 1 ) {
                        allowedFunctionsByEntity.put(pathEntitySet, ApiFunction.READ_BY_KEY);
                    } else {
                        allowedFunctionsByEntity.put(pathEntitySet, ApiFunction.READ);
                    }
                    break;
                case "post":
                    allowedFunctionsByEntity.put(pathEntitySet, ApiFunction.CREATE);
                    break;
                case "patch":
                    allowedFunctionsByEntity.put(pathEntitySet, ApiFunction.UPDATE);
                    break;
                case "delete":
                    allowedFunctionsByEntity.put(pathEntitySet, ApiFunction.DELETE);
                    break;
                case "put":
                case "options":
                case "head":
                case "trace":
                    logger.warn("Skipping unsupported operation '" + entry.getKey() + "'.");
                    break;
                case "summary":
                case "description":
                case "parameters":
                case "servers":
                    logger.debug("Skipping field '" + entry.getKey() + "' from the Swagger file.");
                    break;
                default:
                    logger.info("Skipping unexpected field " + entry.getKey() + "' from Swagger file.");
                    break;
            }
        }
    }

    private Multimap<String, ApiFunction> readFromMetadata( final Edm metadata )
    {
        final Multimap<String, ApiFunction> allowedFunctionsByEntity =
            MultimapBuilder.hashKeys().hashSetValues().build();

        for( final EdmEntitySet entitySet : metadata.getEntityContainer().getEntitySetsWithAnnotations() ) {
            final String entitySetName = entitySet.getName();

            // Mark entity set as navigable.
            allowedFunctionsByEntity.put(entitySetName, ApiFunction.NAVIGATE);

            if( isEntitySetReadableByKey(metadata, entitySet) ) {
                allowedFunctionsByEntity.put(entitySetName, ApiFunction.READ_BY_KEY);
            }
            if( isEntitySetOperationPermitted(entitySet, TERMS_READ_RESTRICTIONS, PROPERTY_READABLE) ) {
                allowedFunctionsByEntity.put(entitySetName, ApiFunction.READ);
            }
            if( isEntitySetOperationPermitted(entitySet, TERMS_INSERT_RESTRICTIONS, PROPERTY_INSERTABLE) ) {
                allowedFunctionsByEntity.put(entitySetName, ApiFunction.CREATE);
            }
            if( isEntitySetOperationPermitted(entitySet, TERMS_UPDATE_RESTRICTIONS, PROPERTY_UPDATABLE) ) {
                allowedFunctionsByEntity.put(entitySetName, ApiFunction.UPDATE);
            }
            if( isEntitySetOperationPermitted(entitySet, TERMS_DELETE_RESTRICTIONS, PROPERTY_DELETABLE) ) {
                allowedFunctionsByEntity.put(entitySetName, ApiFunction.DELETE);
            }
        }

        return allowedFunctionsByEntity;
    }

    private boolean isEntitySetOperationPermitted(
        final EdmEntitySet entitySet,
        final String[] termNames,
        final String propertyName )
    {
        final Collector<EdmAnnotation, ?, Map<String, EdmAnnotation>> fqnMapper =
            Collectors.toMap(annotation -> annotation.getTerm().getFullQualifiedName().toString(), Function.identity());

        final Map<String, EdmAnnotation> annotations =
            entitySet.getAnnotations().stream().filter(annotation -> annotation.getTerm() != null).collect(fqnMapper);

        for( final String termFqn : termNames ) {
            final EdmAnnotation annotation = annotations.get(termFqn);

            if( annotation != null ) {
                final EdmPropertyValue edmPropertyValue = getEdmPropertyValue(annotation.getExpression(), propertyName);
                final Boolean operationFlag = getBooleanFromPropertyValue(edmPropertyValue);

                if( operationFlag != null ) {
                    return operationFlag;
                } else {
                    logger
                        .warn(
                            String
                                .format(
                                    "Annotation %s of entity set %s is in an unexpected format. Unable to determine if %s operation is permitted.",
                                    termFqn,
                                    entitySet.getName(),
                                    propertyName));
                }
            }
        }
        return true;
    }

    private boolean isEntitySetReadableByKey( final Edm metadata, final EdmEntitySet entitySet )
    {
        for( final String termFqn : TERMS_READ_RESTRICTIONS ) {
            final EdmTerm term = metadata.getTerm(new FullQualifiedName(termFqn));
            final EdmAnnotation annotation = Try.of(() -> entitySet.getAnnotation(term, null)).getOrNull();

            if( annotation != null ) {
                final EdmPropertyValue readByKeyProperty =
                    getEdmPropertyValue(annotation.getExpression(), PROPERTY_READ_BY_KEY);

                if( readByKeyProperty != null ) {
                    final EdmPropertyValue edmPropertyValue =
                        getEdmPropertyValue(readByKeyProperty.getValue(), PROPERTY_READABLE);
                    final Boolean operationFlag = getBooleanFromPropertyValue(edmPropertyValue);

                    if( operationFlag != null ) {
                        return operationFlag;
                    } else {
                        logger
                            .warn(
                                String
                                    .format(
                                        "Annotation %s of entity set %s is in an unexpected format. Unable to determine if reading by key is enabled.",
                                        termFqn,
                                        entitySet.getName()));
                    }
                }
            }
        }
        return true;
    }

    private EdmPropertyValue getEdmPropertyValue( final EdmExpression expression, final String propertyName )
    {
        if( expression != null && expression.isDynamic() && expression.asDynamic().isRecord() ) {

            final List<EdmPropertyValue> propertyValues = expression.asDynamic().asRecord().getPropertyValues();

            for( final EdmPropertyValue pv : propertyValues ) {
                if( pv.getProperty().equals(propertyName) ) {
                    return pv;
                }
            }
        }
        return null;
    }

    private Boolean getBooleanFromPropertyValue( final EdmPropertyValue propertyValue )
    {
        if( propertyValue != null && propertyValue.getValue() != null && propertyValue.getValue().isConstant() ) {

            try {
                final Object value = propertyValue.getValue().asConstant().asPrimitive();

                if( value instanceof Boolean ) {
                    return (Boolean) value;
                }
            }
            catch( final IllegalArgumentException e ) {
                return null;
            }
        }
        return null;
    }
}
