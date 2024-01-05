/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.methods.HttpUriRequest;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.helper.CollectionValuedFluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;
import com.sap.cloud.sdk.datamodel.odata.helper.SingleValuedFluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.utility.LegacyClassScanner;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingStrategy;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingUtils;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FluentHelperClassGenerator
{
    private static final String SERVICE_PATH_FIELD_NAME = "servicePath";
    private static final String ENTITY_COLLECTION_FIELD_NAME = "entityCollection";

    private final JCodeModel codeModel;
    private final NamingStrategy codeNamingStrategy;
    private final LegacyClassScanner classScanner;

    private void createGetEntityClass( final JDefinedClass clazz, final JClass entityClass )
    {
        final JMethod getEntityClassMethod =
            clazz.method(JMod.PROTECTED, codeModel.ref(Class.class).narrow(entityClass), "getEntityClass");
        getEntityClassMethod.annotate(Override.class);
        getEntityClassMethod.annotate(Nonnull.class);
        getEntityClassMethod.body()._return(entityClass.dotclass());
    }

    private void createRefineJsonResponse( final JDefinedClass clazz )
    {
        final JMethod method = clazz.method(JMod.PROTECTED, codeModel.ref(JsonElement.class), "refineJsonResponse");
        final JVar jsonElement = method.param(codeModel.ref(JsonElement.class), "jsonElement");
        jsonElement.annotate(Nullable.class);
        method.annotate(Override.class);
        method.annotate(Nullable.class);

        final JExpression castedJsonElement = JExpr.cast(codeModel.ref(JsonObject.class), jsonElement);
        final JInvocation getFunctionName = JExpr.invoke("getFunctionName");
        method
            .body()
            ._if(
                jsonElement
                    ._instanceof(codeModel.ref(JsonObject.class))
                    .cand(castedJsonElement.invoke("has").arg(getFunctionName)))
            ._then()
            .assign(jsonElement, castedJsonElement.invoke("get").arg(getFunctionName));
        method.body()._return(JExpr._super().invoke(method).arg(jsonElement));

    }

    private void generateReadConstructor( final JDefinedClass fluentHelperClass )
    {
        final JMethod constructor = fluentHelperClass.constructor(JMod.PUBLIC);

        final JVar servicePathParam = constructor.param(JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAME);
        servicePathParam.annotate(Nonnull.class);

        final JVar entityCollectionParam = constructor.param(JMod.FINAL, String.class, ENTITY_COLLECTION_FIELD_NAME);
        entityCollectionParam.annotate(Nonnull.class);

        constructor.body().invoke("super").arg(servicePathParam).arg(entityCollectionParam);

        constructor
            .javadoc()
            .add(
                "Creates a fluent helper using the specified service path and entity collection to send the read requests.");
        constructor.javadoc().addParam(servicePathParam).add("The service path to direct the read requests to.");
        constructor.javadoc().addParam(entityCollectionParam).add("The entity collection to direct the requests to.");
    }

    JDefinedClass generateEntityReadFluentHelperClass(
        final NamingContext entityClassNamingContext,
        final JClassContainer namespacePackage,
        final JDefinedClass entityClass,
        final JDefinedClass specificEntitySelectableClass )
        throws JClassAlreadyExistsException
    {
        final String fluentHelperClassName =
            entityClassNamingContext
                .ensureUniqueName(NamingUtils.deriveJavaEntityFluentHelperClassName(entityClass.name()));

        final JDefinedClass entityFluentHelperClass = namespacePackage._class(JMod.PUBLIC, fluentHelperClassName);

        entityFluentHelperClass
            ._extends(
                codeModel
                    .ref(FluentHelperRead.class)
                    .narrow(entityFluentHelperClass, entityClass, specificEntitySelectableClass));

        entityFluentHelperClass
            .javadoc()
            .add(
                String
                    .format(
                        "Fluent helper to fetch multiple {@link %s %s} entities. "
                            + "This fluent helper allows methods which modify the underlying query to be called before executing the query itself. ",
                        entityClass.fullName(),
                        entityClass.name()));

        generateReadConstructor(entityFluentHelperClass);

        createGetEntityClass(entityFluentHelperClass, entityClass);

        return entityFluentHelperClass;
    }

    JDefinedClass generateEntityByKeyFluentHelperClass(
        final NamingContext entityClassNamingContext,
        final JClassContainer namespacePackage,
        final JDefinedClass entityClass,
        final Iterable<EntityPropertyModel> keyProperties,
        final JDefinedClass specificEntitySelectableClass )
        throws JClassAlreadyExistsException
    {
        final String fluentHelperClassName =
            entityClassNamingContext
                .ensureUniqueName(NamingUtils.deriveJavaEntityByKeyFluentHelperClassName(entityClass.name()));

        final JDefinedClass entityByKeyFluentHelperClass = namespacePackage._class(JMod.PUBLIC, fluentHelperClassName);

        entityByKeyFluentHelperClass
            ._extends(
                codeModel
                    .ref(FluentHelperByKey.class)
                    .narrow(entityByKeyFluentHelperClass, entityClass, specificEntitySelectableClass));

        entityByKeyFluentHelperClass
            .javadoc()
            .add(
                String
                    .format(
                        "Fluent helper to fetch a single {@link %s %s} entity using key fields. "
                            + "This fluent helper allows methods which modify the underlying query to be called before executing the query itself. ",
                        entityClass.fullName(),
                        entityClass.name()));

        createGetEntityClass(entityByKeyFluentHelperClass, entityClass);

        final JClass keyClass = codeModel.ref(Map.class).narrow(String.class, Object.class);
        final JFieldVar keyMapField =
            entityByKeyFluentHelperClass
                .field(
                    JMod.PRIVATE | JMod.FINAL,
                    keyClass,
                    "key",
                    codeModel.ref(Maps.class).staticInvoke("newHashMap"));

        // constructor with service path and entity collection parameter
        final JMethod constructor =
            generateEntityByKeyFluentHelperConstructor(entityClass, entityByKeyFluentHelperClass);

        // iterate key properties of entity
        for( final EntityPropertyModel keyProperty : keyProperties ) {
            final JVar param =
                constructor.param(JMod.FINAL, keyProperty.getJavaFieldType(), keyProperty.getJavaFieldName());
            constructor
                .body()
                .add(JExpr.refthis(keyMapField.name()).invoke("put").arg(keyProperty.getEdmName()).arg(param));
            final JCommentPart parameterJavadoc = constructor.javadoc().addParam(param);
            parameterJavadoc.add(keyProperty.getBasicDescription());
            if( !Strings.isNullOrEmpty(keyProperty.getBasicDescription()) ) {
                parameterJavadoc.add(String.format("<p>%s</p>", keyProperty.getConstraintsDescription()));
            }
        }

        // getKey()
        final JMethod getKeyMethod = entityByKeyFluentHelperClass.method(JMod.PROTECTED, keyClass, "getKey");
        getKeyMethod.annotate(Override.class);
        getKeyMethod.annotate(Nonnull.class);
        getKeyMethod.body()._return(keyMapField);

        return entityByKeyFluentHelperClass;
    }

    @Nonnull
    private JMethod generateEntityByKeyFluentHelperConstructor(
        final JDefinedClass entityClass,
        final JDefinedClass entityByKeyFluentHelperClass )
    {
        final JMethod constructor = entityByKeyFluentHelperClass.constructor(JMod.PUBLIC);
        constructor
            .javadoc()
            .add(
                String
                    .format(
                        "Creates a fluent helper object that will fetch a single {@link %s %s} entity with the provided key field values. "
                            + "To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.",
                        entityClass.fullName(),
                        entityClass.name()));

        final JVar servicePathParam = constructor.param(JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAME);
        servicePathParam.annotate(Nonnull.class);
        constructor
            .javadoc()
            .addParam(servicePathParam)
            .add(String.format("Service path to be used to fetch a single {@code %s}", entityClass.name()));

        final JVar entityCollectionParam = constructor.param(JMod.FINAL, String.class, ENTITY_COLLECTION_FIELD_NAME);
        entityCollectionParam.annotate(Nonnull.class);
        constructor
            .javadoc()
            .addParam(entityCollectionParam)
            .add(String.format("Entity Collection to be used to fetch a single {@code %s}", entityClass.name()));

        constructor.body().invoke("super").arg(servicePathParam).arg(entityCollectionParam);
        return constructor;
    }

    private JClass getReturnTypeClass( final JType javaReturnType )
    {
        return javaReturnType.isPrimitive() ? javaReturnType.boxify() : (JClass) javaReturnType;
    }

    JDefinedClass generateFunctionImportFluentHelperClass(
        final JPackage namespacePackage,
        final String edmName,
        final String edmLabel,
        final JType javaReturnType,
        final boolean isCollectionReturnType,
        final String httpMethod,
        final Iterable<FunctionImportParameterModel> parameters,
        final NamingContext functionImportClassNamingContext )
        throws JClassAlreadyExistsException
    {
        final JClass returnTypeClass = getReturnTypeClass(javaReturnType);

        final String fluentHelperClassName =
            functionImportClassNamingContext
                .ensureUniqueName(codeNamingStrategy.generateJavaFluentHelperClassName(edmName, edmLabel));

        final JDefinedClass functionImportFluentHelperClass =
            namespacePackage._class(JMod.PUBLIC, fluentHelperClassName);

        final JClass executionResultClass =
            isCollectionReturnType ? codeModel.ref(List.class).narrow(returnTypeClass) : returnTypeClass;

        final JClass functionTypeClass =
            isCollectionReturnType
                ? codeModel.ref(CollectionValuedFluentHelperFunction.class)
                : codeModel.ref(SingleValuedFluentHelperFunction.class);

        functionImportFluentHelperClass
            ._extends(functionTypeClass.narrow(functionImportFluentHelperClass, returnTypeClass, executionResultClass));

        functionImportFluentHelperClass
            .javadoc()
            .add(String.format("Fluent helper for the <b>%s</b> OData function import.", edmName));

        final JFieldVar valuesMapField =
            functionImportFluentHelperClass
                .field(
                    JMod.PRIVATE | JMod.FINAL,
                    codeModel.ref(Map.class).narrow(String.class, Object.class),
                    "values",
                    codeModel.ref(Maps.class).staticInvoke("newHashMap"));

        createGetEntityClass(functionImportFluentHelperClass, returnTypeClass);

        final List<List<FunctionImportParameterModel>> constructors =
            classScanner
                .determineArgumentsForConstructor(
                    functionImportFluentHelperClass.fullName(),
                    parameters,
                    FunctionImportParameterModel::getJavaName,
                    1);

        for( final List<FunctionImportParameterModel> arguments : constructors ) {

            // constructor
            final JMethod constructor = functionImportFluentHelperClass.constructor(JMod.PUBLIC);
            constructor
                .javadoc()
                .add(
                    String
                        .format(
                            "Creates a fluent helper object that will execute the <b>%s</b> OData function import with the provided parameters. "
                                + "To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.",
                            edmName));

            final JVar servicePathParam = constructor.param(JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAME);
            servicePathParam.annotate(Nonnull.class);
            constructor
                .javadoc()
                .addParam(servicePathParam)
                .add("Service path to be used to call the functions against.");
            constructor.body().invoke("super").arg(servicePathParam);

            for( final FunctionImportParameterModel functionImportParam : arguments ) {
                final JVar param =
                    constructor.param(JMod.FINAL, functionImportParam.getJavaType(), functionImportParam.getJavaName());
                param.annotate(functionImportParam.isNonnull() ? Nonnull.class : Nullable.class);
                final JBlock container =
                    functionImportParam.isNonnull()
                        ? constructor.body()
                        : constructor.body()._if(param.ne(JExpr._null()))._then().block();
                container.invoke(valuesMapField, "put").arg(functionImportParam.getEdmName()).arg(param);
                constructor.javadoc().addParam(param).add(functionImportParam.getDescription());
            }
        }

        // getFunctionName()
        final JMethod getFunctionNameMethod =
            functionImportFluentHelperClass.method(JMod.PROTECTED, String.class, "getFunctionName");
        getFunctionNameMethod.annotate(Override.class);
        getFunctionNameMethod.annotate(Nonnull.class);
        getFunctionNameMethod.body()._return(JExpr.lit(edmName));

        // refineJsonResponse(JsonObject)
        createRefineJsonResponse(functionImportFluentHelperClass);

        // getParameters()
        final JMethod getParametersMethod =
            functionImportFluentHelperClass
                .method(JMod.PROTECTED, codeModel.ref(Map.class).narrow(String.class, Object.class), "getParameters");
        getParametersMethod.annotate(Override.class);
        getParametersMethod.annotate(Nonnull.class);
        getParametersMethod.body()._return(valuesMapField);

        final JClass httpRequestClass =
            codeModel
                .ref("org.apache.http.client.methods.Http" + NamingUtils.httpMethodToApacheClientClassName(httpMethod));
        final JMethod createRequestMethod =
            functionImportFluentHelperClass
                .method(JMod.PROTECTED, codeModel.ref(HttpUriRequest.class), "createRequest");
        createRequestMethod.annotate(Override.class);
        createRequestMethod.annotate(Nonnull.class);
        final JVar uriParam = createRequestMethod.param(JMod.FINAL, codeModel.ref(URI.class), "uri");
        uriParam.annotate(Nonnull.class);
        createRequestMethod.body()._return(JExpr._new(httpRequestClass).arg(uriParam));

        final JMethod executeMethod =
            functionImportFluentHelperClass.method(JMod.PUBLIC, executionResultClass, "executeRequest");

        final JVar destinationParam = executeMethod.param(JMod.FINAL, codeModel.ref(Destination.class), "destination");
        destinationParam.annotate(Nonnull.class);

        executeMethod.annotate(Override.class);
        executeMethod.javadoc().append("Execute this function import.");

        if( isCollectionReturnType ) {
            executeMethod.annotate(Nonnull.class);
            executeMethod.body()._return(JExpr._super().invoke("executeMultiple").arg(destinationParam));
        } else {
            executeMethod.annotate(Nullable.class);
            executeMethod.body()._return(JExpr._super().invoke("executeSingle").arg(destinationParam));
        }

        return functionImportFluentHelperClass;
    }

    JDefinedClass generateCreateFluentHelper(
        final NamingContext entityClassNamingContext,
        final JPackage namespacePackage,
        final JDefinedClass entityClass )
        throws JClassAlreadyExistsException
    {
        final String fluentHelperClassName =
            entityClassNamingContext
                .ensureUniqueName(NamingUtils.deriveJavaCreateFluentHelperClassName(entityClass.name()));

        final JDefinedClass createFluentHelperClass = namespacePackage._class(JMod.PUBLIC, fluentHelperClassName);

        createFluentHelperClass
            ._extends(codeModel.ref(FluentHelperCreate.class).narrow(createFluentHelperClass, entityClass));

        createFluentHelperClass
            .javadoc()
            .add(
                String
                    .format(
                        "Fluent helper to create a new {@link %s %s} entity and save it to the S/4HANA system.<p>\n"
                            + "To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.",
                        entityClass.fullName(),
                        entityClass.name()));

        // constructor with service path and entity collection parameter
        generateCreateFluentHelperConstructor(entityClass, createFluentHelperClass);

        // entity value with protected getEntity()
        final JFieldVar entityField = addEntityInstanceFieldAndGetter(createFluentHelperClass, entityClass);
        entityField
            .javadoc()
            .add(
                String
                    .format(
                        "{@link %s %s} entity object that will be created in the S/4HANA system.",
                        entityClass.fullName(),
                        entityClass.name()));

        return createFluentHelperClass;
    }

    private void generateCreateFluentHelperConstructor(
        final JDefinedClass entityClass,
        final JDefinedClass createFluentHelperClass )
    {
        final JMethod constructor = createFluentHelperClass.constructor(JMod.PUBLIC);

        final JVar servicePathParam = constructor.param(JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAME);
        servicePathParam.annotate(Nonnull.class);

        final JVar entityParam = constructor.param(JMod.FINAL, entityClass, "entity");
        entityParam.annotate(Nonnull.class);

        final JVar entityCollectionParam = constructor.param(JMod.FINAL, String.class, ENTITY_COLLECTION_FIELD_NAME);
        entityCollectionParam.annotate(Nonnull.class);

        constructor.body().invoke("super").arg(servicePathParam).arg(entityCollectionParam);
        constructor.body().assign(JExpr.refthis("entity"), entityParam);
        constructor
            .javadoc()
            .add(
                String
                    .format(
                        "Creates a fluent helper object that will create a {@link %s %s} entity on the OData endpoint. "
                            + "To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.",
                        entityClass.fullName(),
                        entityClass.name()));
        constructor.javadoc().addParam(servicePathParam).add("The service path to direct the create requests to.");
        constructor.javadoc().addParam(entityParam).add(String.format("The %s to create.", entityClass.name()));
        constructor
            .javadoc()
            .addParam(entityCollectionParam)
            .add("Entity Collection  to direct the create requests to.");
    }

    private
        JFieldVar
        addEntityInstanceFieldAndGetter( final JDefinedClass fluentHelperClass, final JDefinedClass entityClass )
    {
        final JFieldVar entityField = fluentHelperClass.field(JMod.FINAL | JMod.PRIVATE, entityClass, "entity");

        final JMethod getEntityMethod = fluentHelperClass.method(JMod.PROTECTED, entityClass, "getEntity");
        getEntityMethod.annotate(Override.class);
        getEntityMethod.annotate(Nonnull.class);
        getEntityMethod.body()._return(entityField);

        return entityField;
    }

    JDefinedClass generateUpdateFluentHelper(
        final NamingContext entityClassNamingContext,
        final JPackage namespacePackage,
        final JDefinedClass entityClass )
        throws JClassAlreadyExistsException
    {
        final String fluentHelperClassName =
            entityClassNamingContext
                .ensureUniqueName(NamingUtils.deriveJavaUpdateFluentHelperClassName(entityClass.name()));
        final JDefinedClass updateFluentHelperClass = namespacePackage._class(JMod.PUBLIC, fluentHelperClassName);

        updateFluentHelperClass
            ._extends(codeModel.ref(FluentHelperUpdate.class).narrow(updateFluentHelperClass, entityClass));

        updateFluentHelperClass
            .javadoc()
            .add(
                String
                    .format(
                        "Fluent helper to update an existing {@link %s %s} entity and save it to the S/4HANA system.<p>\n"
                            + "To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.",
                        entityClass.fullName(),
                        entityClass.name()));

        // constructor with service path and entity collection parameter
        generateUpdateFluentHelperConstructor(entityClass, updateFluentHelperClass);

        // entity value with protected getEntity()
        final JFieldVar entityField = addEntityInstanceFieldAndGetter(updateFluentHelperClass, entityClass);
        entityField
            .javadoc()
            .add(
                String
                    .format(
                        "{@link %s %s} entity object that will be updated in the S/4HANA system.",
                        entityClass.fullName(),
                        entityClass.name()));

        return updateFluentHelperClass;
    }

    private void generateUpdateFluentHelperConstructor(
        final JDefinedClass entityClass,
        final JDefinedClass updateFluentHelperClass )
    {
        final JMethod constructor = updateFluentHelperClass.constructor(JMod.PUBLIC);
        final JVar servicePathParam = constructor.param(JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAME);
        final JVar entityParam = constructor.param(JMod.FINAL, entityClass, "entity");
        final JVar entityCollectionParam = constructor.param(JMod.FINAL, String.class, ENTITY_COLLECTION_FIELD_NAME);
        servicePathParam.annotate(Nonnull.class);
        entityParam.annotate(Nonnull.class);
        entityCollectionParam.annotate(Nonnull.class);
        constructor.body().invoke("super").arg(servicePathParam).arg(entityCollectionParam);
        constructor.body().assign(JExpr.refthis("entity"), entityParam);
        constructor
            .javadoc()
            .add(
                String
                    .format(
                        "Creates a fluent helper object that will update a {@link %s %s} entity on the OData endpoint. "
                            + "To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.",
                        entityClass.fullName(),
                        entityClass.name()));
        constructor.javadoc().addParam(servicePathParam).add("The service path to direct the update requests to.");
        constructor
            .javadoc()
            .addParam(entityParam)
            .add(String.format("The %s to take the updated values from.", entityClass.name()));
    }

    JDefinedClass generateDeleteFluentHelper(
        final NamingContext entityClassNamingContext,
        final JPackage namespacePackage,
        final JDefinedClass entityClass )
        throws JClassAlreadyExistsException
    {
        final String fluentHelperClassName =
            entityClassNamingContext
                .ensureUniqueName(NamingUtils.deriveJavaDeleteFluentHelperClassName(entityClass.name()));

        final JDefinedClass deleteFluentHelperClass = namespacePackage._class(JMod.PUBLIC, fluentHelperClassName);

        deleteFluentHelperClass
            ._extends(codeModel.ref(FluentHelperDelete.class).narrow(deleteFluentHelperClass, entityClass));

        deleteFluentHelperClass
            .javadoc()
            .add(
                String
                    .format(
                        "Fluent helper to delete an existing {@link %s %s} entity in the S/4HANA system.<p>\n"
                            + "To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.",
                        entityClass.fullName(),
                        entityClass.name()));

        // constructor with service path and entity collection parameter
        generateDeleteFluentHelperConstructor(entityClass, deleteFluentHelperClass);

        // entity value with protected getEntity()
        final JFieldVar entityField = addEntityInstanceFieldAndGetter(deleteFluentHelperClass, entityClass);
        entityField
            .javadoc()
            .add(
                String
                    .format(
                        "{@link %s %s} entity object that will be deleted in the S/4HANA system.",
                        entityClass.fullName(),
                        entityClass.name()));

        return deleteFluentHelperClass;
    }

    private void generateDeleteFluentHelperConstructor(
        final JDefinedClass entityClass,
        final JDefinedClass deleteFluentHelperClass )
    {
        final JMethod constructor = deleteFluentHelperClass.constructor(JMod.PUBLIC);
        final JVar servicePathParam = constructor.param(JMod.FINAL, String.class, SERVICE_PATH_FIELD_NAME);
        final JVar entityParam = constructor.param(JMod.FINAL, entityClass, "entity");
        final JVar entityCollectionParam = constructor.param(JMod.FINAL, String.class, ENTITY_COLLECTION_FIELD_NAME);
        servicePathParam.annotate(Nonnull.class);
        entityParam.annotate(Nonnull.class);
        entityCollectionParam.annotate(Nonnull.class);
        constructor.body().invoke("super").arg(servicePathParam).arg(entityCollectionParam);
        constructor.body().assign(JExpr.refthis("entity"), entityParam);
        constructor
            .javadoc()
            .add(
                String
                    .format(
                        "Creates a fluent helper object that will delete a {@link %s %s} entity on the OData endpoint. "
                            + "To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.",
                        entityClass.fullName(),
                        entityClass.name()));
        constructor.javadoc().addParam(servicePathParam).add("The service path to direct the update requests to.");
        constructor
            .javadoc()
            .addParam(entityParam)
            .add(String.format("The %s to delete from the endpoint.", entityClass.name()));
        constructor
            .javadoc()
            .addParam(entityCollectionParam)
            .add("The entity collection to direct the update requests to.");
    }
}
