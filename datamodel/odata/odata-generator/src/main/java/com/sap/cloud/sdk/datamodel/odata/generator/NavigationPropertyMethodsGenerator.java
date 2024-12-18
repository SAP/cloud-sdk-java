/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.SourceVersion;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.generator.annotation.AnnotationStrategy;
import com.sap.cloud.sdk.datamodel.odata.generator.annotation.NavigationPropertyAnnotationModel;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocCommentable;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

class NavigationPropertyMethodsGenerator
{
    private static final Logger logger = MessageCollector.getLogger(NavigationPropertyMethodsGenerator.class);

    private final JCodeModel codeModel;
    private final JDefinedClass entityClass;

    NavigationPropertyMethodsGenerator( final JCodeModel codeModel, final JDefinedClass entityClass )
    {
        this.codeModel = codeModel;
        this.entityClass = entityClass;
    }

    @Nonnull
    Map<String, JFieldVar> createNavigationPropertyFields(
        final Iterable<NavigationPropertyModel> navigationProperties,
        final Map<String, JDefinedClass> generatedEntities,
        final AnnotationStrategy annotationStrategy )
    {
        final Map<String, JFieldVar> navigationPropertyFields = new HashMap<>();

        for( final NavigationPropertyModel navigationProperty : navigationProperties ) {
            final JDefinedClass associatedEntity = getAssociatedEntity(navigationProperty, generatedEntities);
            if( associatedEntity != null ) {
                navigationPropertyFields
                    .put(
                        navigationProperty.getEdmName(),
                        createNavigationPropertyField(navigationProperty, associatedEntity, annotationStrategy));
            }
        }
        return navigationPropertyFields;
    }

    void addNavigationPropertyMethods(
        final Map<String, JDefinedClass> generatedEntities,
        final Iterable<NavigationPropertyModel> navigationProperties,
        final Map<String, JFieldVar> generatedNavigationPropertyFields,
        final JDocCommentable selectableInterface,
        final JClass entityOneToManyLink,
        final JClass entityOneToOneLink )
    {
        final JClass mapType = codeModel.ref(Map.class).narrow(String.class, Object.class);

        final List<Object> fromMapContents =
            entityClass.getMethod("fromMap", new JType[] { mapType }).body().getContents();
        final Object statementBeforeSuper = fromMapContents.get(fromMapContents.size() - 2);
        final JBlock fromMapBlock = statementBeforeSuper instanceof JBlock ? (JBlock) statementBeforeSuper : null;

        final JClass fieldMapClass = codeModel.ref(Map.class).narrow(String.class, Object.class);
        final JMethod toMapOfNavigationPropertiesMethod =
            entityClass.method(JMod.PROTECTED, fieldMapClass, "toMapOfNavigationProperties");
        toMapOfNavigationPropertiesMethod.annotate(Nonnull.class);
        toMapOfNavigationPropertiesMethod.annotate(Override.class);
        final JVar valuesMap =
            toMapOfNavigationPropertiesMethod
                .body()
                .decl(
                    JMod.FINAL,
                    fieldMapClass,
                    CommonConstants.INLINE_MAP_NAME,
                    JExpr._super().invoke("toMapOfNavigationProperties"));

        for( final NavigationPropertyModel navigationProperty : navigationProperties ) {
            final JFieldVar navigationPropertyField =
                generatedNavigationPropertyFields.get(navigationProperty.getEdmName());
            final JDefinedClass associatedEntity = getAssociatedEntity(navigationProperty, generatedEntities);

            if( navigationPropertyField != null && associatedEntity != null ) {
                addNavigationPropertyLogic(
                    navigationPropertyField,
                    selectableInterface,
                    entityOneToManyLink,
                    entityOneToOneLink,
                    mapType,
                    toMapOfNavigationPropertiesMethod,
                    fromMapBlock,
                    navigationProperty,
                    associatedEntity);

                navigationPropertyField
                    .annotate(Getter.class)
                    .param("value", codeModel.ref(AccessLevel.class).staticRef("NONE"));
                navigationPropertyField
                    .annotate(Setter.class)
                    .param("value", codeModel.ref(AccessLevel.class).staticRef("NONE"));
            }
        }

        toMapOfNavigationPropertiesMethod.body()._return(valuesMap);
    }

    @Nullable
    private JDefinedClass getAssociatedEntity(
        final NavigationPropertyModel navigationProperty,
        final Map<String, JDefinedClass> generatedEntities )
    {
        final JDefinedClass associatedEntity = generatedEntities.get(navigationProperty.getReturnEntityType());

        if( associatedEntity == null ) {
            logger
                .warn(
                    "Unable to generate code for navigation property "
                        + navigationProperty.getEdmName()
                        + " of entity "
                        + entityClass.name()
                        + ":  Associated entity type "
                        + navigationProperty.getReturnEntityType()
                        + " is either not found or its entity set has been filtered out.");
        }

        return associatedEntity;
    }

    private JFieldVar createNavigationPropertyField(
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final AnnotationStrategy annotationStrategy )
    {
        final boolean isOneToMany = navigationProperty.getMultiplicity() == Multiplicity.MANY;

        final JClass returnType;
        if( isOneToMany ) {
            returnType = codeModel.ref(List.class).narrow(associatedEntity);
        } else {
            returnType = associatedEntity;
        }

        // create the class field storing the lazy generated Entity/List<Entity>
        final JFieldVar navigationPropertyField =
            createClassMember(navigationProperty, associatedEntity, isOneToMany, returnType, annotationStrategy);

        // change builder
        changeBuilder(navigationProperty, associatedEntity, isOneToMany, navigationPropertyField);

        return navigationPropertyField;
    }

    private void addNavigationPropertyLogic(
        final JFieldVar navigationPropertyField,
        final JDocCommentable selectableInterface,
        final JClass specificEntityOneToManyLinkClass,
        final JClass specificEntityOneToOneLinkClass,
        final JClass mapType,
        final JMethod toMapOfNavigationPropertiesMethod,
        final JBlock fromMapBlock,
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity )
    {
        final boolean isOneToMany = navigationProperty.getMultiplicity() == Multiplicity.MANY;
        final JType returnType = navigationPropertyField.type();

        final JFieldVar fluentHelperField =
            isOneToMany
                ? createLinkConstant(specificEntityOneToManyLinkClass, navigationProperty, associatedEntity)
                : createLinkConstant(specificEntityOneToOneLinkClass, navigationProperty, associatedEntity);

        // add fluentHelperField class to EntitySelectable java docs
        JavadocUtils.addFieldReference(selectableInterface, entityClass, fluentHelperField);

        // handle additions for fromMap(Map<String,Object>)
        if( fromMapBlock != null ) {
            handleFromMapAdditions(
                mapType,
                fromMapBlock,
                navigationProperty,
                associatedEntity,
                isOneToMany,
                navigationPropertyField);
        }

        createFetchMethod(navigationProperty, associatedEntity, isOneToMany, returnType, navigationPropertyField);

        // nullable getter method - Option<List<T>> getXOrNull()
        createGetIfPresentMethod(
            navigationProperty,
            associatedEntity,
            isOneToMany,
            returnType,
            navigationPropertyField);

        createSetterMethod(navigationProperty, associatedEntity, isOneToMany, returnType, navigationPropertyField);

        if( isOneToMany ) {
            createAddMethod(navigationProperty, associatedEntity, navigationPropertyField);
        }

        addIfAndPutStatementToNavigationPropertiesMap(
            toMapOfNavigationPropertiesMethod,
            navigationProperty,
            navigationPropertyField);
    }

    private void addIfAndPutStatementToNavigationPropertiesMap(
        final JMethod toMapOfNavigationPropertiesMethod,
        final NavigationPropertyModel navigationProperty,
        final JFieldVar propField )
    {
        final JBlock body = toMapOfNavigationPropertiesMethod.body();
        body
            ._if(propField.ne(JExpr._null()))
            ._then()
            .invoke(JExpr.direct(CommonConstants.INLINE_MAP_NAME), "put")
            .arg(navigationProperty.getEdmName())
            .arg(propField);
    }

    private void createAddMethod(
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final JAssignmentTarget propField )
    {
        final String addToName = navigationProperty.getJavaMethodNameAdd();
        final JMethod addToMethod = entityClass.method(JMod.PUBLIC, codeModel.VOID, addToName);
        final JVar entity = addToMethod.varParam(associatedEntity, "entity");
        addToMethod
            .javadoc()
            .add(
                String
                    .format(
                        "Adds elements to the list of associated <b>%s</b> entities. This corresponds to the OData navigation property <b>%s</b>.",
                        associatedEntity.name(),
                        navigationProperty.getEdmName()));
        addToMethod.javadoc().add(JavadocUtils.getLazyWarningMessage(navigationProperty, entityClass));
        addToMethod
            .javadoc()
            .addParam(entity)
            .add(String.format("Array of <b>%s</b> entities.", associatedEntity.name()));
        addToMethod
            .body()
            ._if(propField.eq(JExpr._null()))
            ._then()
            .assign(propField, codeModel.ref(Lists.class).staticInvoke("newArrayList"));
        addToMethod
            .body()
            .invoke(propField, "addAll")
            .arg(codeModel.ref(Lists.class).staticInvoke("newArrayList").arg(entity));
    }

    private void createSetterMethod(
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final boolean isOneToMany,
        final JType returnType,
        final JAssignmentTarget propField )
    {
        if( isOneToMany ) {
            // set method (only for multiple items)
            final String setterName = navigationProperty.getJavaMethodNameSet();
            final JMethod setterMethod = entityClass.method(JMod.PUBLIC, codeModel.VOID, setterName);
            setterMethod
                .javadoc()
                .add(
                    String
                        .format(
                            "Overwrites the list of associated <b>%s</b> entities for the loaded navigation property <b>%s</b>.",
                            associatedEntity.name(),
                            navigationProperty.getEdmName()));
            setterMethod.javadoc().add(JavadocUtils.getLazyWarningMessage(navigationProperty, entityClass));
            final JVar setterParam = setterMethod.param(JMod.FINAL, returnType, "cloudSdkValue");
            setterMethod
                .javadoc()
                .addParam(setterParam)
                .add(String.format("List of <b>%s</b> entities.", associatedEntity.name()));
            setterParam.annotate(Nonnull.class);
            final JBlock setterBody = setterMethod.body();
            setterBody
                ._if(propField.eq(JExpr._null()))
                ._then()
                .assign(propField, codeModel.ref(Lists.class).staticInvoke("newArrayList"));
            setterBody.invoke(propField, "clear");
            setterBody.invoke(propField, "addAll").arg(setterParam);
        } else {
            // set method (only for single item)
            final String setterName = navigationProperty.getJavaMethodNameSet();
            final JMethod setterMethod = entityClass.method(JMod.PUBLIC, codeModel.VOID, setterName);
            setterMethod
                .javadoc()
                .add(
                    String
                        .format(
                            "Overwrites the associated <b>%s</b> entity for the loaded navigation property <b>%s</b>.",
                            associatedEntity.name(),
                            navigationProperty.getEdmName()));
            final JVar setterParam = setterMethod.param(JMod.FINAL, returnType, "cloudSdkValue");
            setterMethod
                .javadoc()
                .addParam(setterParam)
                .add(String.format("New <b>%s</b> entity.", associatedEntity.name()));
            final JBlock setterBody = setterMethod.body();
            setterBody.assign(propField, setterParam);
        }
    }

    private void createGetIfPresentMethod(
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final boolean isOneToMany,
        final JType returnType,
        final JExpression propField )
    {
        final String getterIfPresentName = navigationProperty.getJavaMethodNameGetIfPresent();
        final JMethod getterIfPresentMethod =
            entityClass.method(JMod.PUBLIC, codeModel.ref(Option.class).narrow(returnType), getterIfPresentName);

        getterIfPresentMethod.annotate(Nonnull.class);

        getterIfPresentMethod
            .javadoc()
            .add(
                String
                    .format(
                        """
                            Retrieval of associated <b>%s</b> %s. This corresponds to the OData navigation property <b>%s</b>.
                            <p>
                            If the navigation property for an entity <b>%s</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.\
                            """,
                        associatedEntity.name(),
                        isOneToMany ? "entities (one to many)" : "entity (one to one)",
                        navigationProperty.getEdmName(),
                        entityClass.name()));
        getterIfPresentMethod
            .javadoc()
            .addReturn()
            .add(
                String
                    .format(
                        "If the information for navigation property <b>%s</b> is already loaded, the result will contain the <b>%s</b> %s. If not, an <code>Option</code> with result state <code>empty</code> is returned.",
                        navigationProperty.getEdmName(),
                        associatedEntity.name(),
                        isOneToMany ? "entities" : "entity"));
        getterIfPresentMethod.body()._return(codeModel.ref(Option.class).staticInvoke("of").arg(propField));
    }

    private void createFetchMethod(
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final boolean isOneToMany,
        final JType returnType,
        final JAssignmentTarget propField )
    {
        // actual fetch method
        final JMethod fetchMethod =
            entityClass.method(JMod.PUBLIC, returnType, navigationProperty.getJavaMethodNameFetch());

        fillFetchJavadoc(navigationProperty, associatedEntity, isOneToMany, fetchMethod);
        fillFetchMethodBody(navigationProperty, associatedEntity, isOneToMany, fetchMethod);

        // lazy getter method - List<T> getXOrFetch()
        final String getterFetchName = navigationProperty.getJavaMethodNameGetOrFetch();
        final JMethod getterFetchMethod = entityClass.method(JMod.PUBLIC, returnType, getterFetchName);

        fillGetOrFetchJavadoc(navigationProperty, associatedEntity, isOneToMany, getterFetchMethod);
        fillGetOrFetchMethodBody(propField, fetchMethod, getterFetchMethod);

        if( isOneToMany ) {
            fetchMethod.annotate(Nonnull.class);
            getterFetchMethod.annotate(Nonnull.class);
        } else {
            fetchMethod.annotate(Nullable.class);
            getterFetchMethod.annotate(Nullable.class);
        }
    }

    private void fillGetOrFetchMethodBody(
        final JAssignmentTarget propField,
        final JMethod fetchMethod,
        final JMethod getterFetchMethod )
    {
        final JBlock getterBody = getterFetchMethod.body();
        getterBody._if(propField.eq(JExpr._null()))._then().assign(propField, JExpr.invoke(fetchMethod));
        getterBody._return(propField);
    }

    private void fillGetOrFetchJavadoc(
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final boolean isOneToMany,
        final JDocCommentable getterFetchMethod )
    {
        getterFetchMethod
            .javadoc()
            .add(
                String
                    .format(
                        "Retrieval of associated <b>%s</b> %s. This corresponds to the OData navigation property <b>%s</b>.",
                        associatedEntity.name(),
                        isOneToMany ? "entities (one to many)" : "entity (one to one)",
                        navigationProperty.getEdmName()));
        getterFetchMethod.javadoc().add(JavadocUtils.getLazyWarningMessage(navigationProperty, entityClass));
        getterFetchMethod
            .javadoc()
            .addReturn()
            .add(
                String
                    .format(
                        "List of associated <b>%s</b> %s.",
                        associatedEntity.name(),
                        isOneToMany ? "entities" : "entity"));
        getterFetchMethod.javadoc().addThrows(ODataException.class).append(JavadocUtils.ILLEGAL_STATE_JAVADOC_STRING);
    }

    private void fillFetchJavadoc(
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final boolean isOneToMany,
        final JDocCommentable fetchMethod )
    {
        fetchMethod
            .javadoc()
            .add(
                String
                    .format(
                        "Fetches the <b>%s</b> %s associated with this entity. This corresponds to the OData navigation property <b>%s</b>.\n<p>\nPlease note: This method will not cache or persist the query results.",
                        associatedEntity.name(),
                        isOneToMany ? "entities (one to many)" : "entity (one to one)",
                        navigationProperty.getEdmName()));
        fetchMethod
            .javadoc()
            .addReturn()
            .add(
                String
                    .format(
                        isOneToMany
                            ? "List containing one or more associated <b>%s</b> entities. If no entities are associated then an empty list is returned. "
                            : "The single associated <b>%s</b> entity, or {@code null} if an entity is not associated. ",
                        associatedEntity.name()));
        fetchMethod.javadoc().addThrows(ODataException.class).append(JavadocUtils.ILLEGAL_STATE_JAVADOC_STRING);
    }

    private void fillFetchMethodBody(
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final boolean isOneToMany,
        final JMethod fetchMethod )
    {
        final String fieldName = navigationProperty.getEdmName();
        final JExpression fieldType = associatedEntity.dotclass();
        final String methodName = isOneToMany ? "fetchFieldAsList" : "fetchFieldAsSingle";
        fetchMethod.body()._return(JExpr.invoke(methodName).arg(fieldName).arg(fieldType));
    }

    private JFieldVar createLinkConstant(
        final JClass specificEntityLinkClass,
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity )
    {
        final String linkConstantName = navigationProperty.getJavaConstantName();
        final JClass linkType = specificEntityLinkClass.narrow(associatedEntity);
        final JExpression initLink = JExpr._new(linkType).arg(navigationProperty.getEdmName());
        final JFieldVar fluentHelperField =
            entityClass.field(JMod.FINAL | JMod.PUBLIC | JMod.STATIC, linkType, linkConstantName, initLink);
        fluentHelperField
            .javadoc()
            .add(
                String
                    .format(
                        "Use with available fluent helpers to apply the <b>%s</b> navigation property to query operations.",
                        navigationProperty.getEdmName()));
        return fluentHelperField;
    }

    private JFieldVar createClassMember(
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final boolean isOneToMany,
        final JClass returnType,
        final AnnotationStrategy annotationStrategy )
    {
        final String classMemberName = navigationProperty.getJavaMemberName();
        final JFieldVar propField = entityClass.field(JMod.PRIVATE, returnType, classMemberName);

        final NavigationPropertyAnnotationModel annotationModel =
            new NavigationPropertyModelAnnotationWrapper(navigationProperty);

        AnnotationHelper
            .addAllAnnotationsToJavaItem(
                annotationStrategy.getAnnotationsForAssociatedEntity(annotationModel),
                propField);

        propField
            .javadoc()
            .add(
                String
                    .format(
                        "Navigation property <b>%s</b> for <b>%s</b> to %s <b>%s</b>.",
                        navigationProperty.getEdmName(),
                        entityClass.name(),
                        isOneToMany ? "multiple" : "single",
                        associatedEntity.name()));

        return propField;
    }

    private void handleFromMapAdditions(
        final JClass mapType,
        final JBlock fromMapBlock,
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final boolean isOneToMany,
        final JFieldVar propField )
    {
        final JBlock recursiveCallContainer;
        final JVar recursiveCallSource;
        final JVar recursiveCallTarget;
        final JBlock foundValueBlock =
            fromMapBlock
                ._if(
                    JExpr
                        .direct(CommonConstants.INLINE_MAP_NAME)
                        .invoke("containsKey")
                        .arg(navigationProperty.getEdmName()))
                ._then();
        final JVar newValue =
            foundValueBlock
                .decl(
                    JMod.FINAL,
                    codeModel.ref(Object.class),
                    "cloudSdkValue",
                    JExpr
                        .direct(CommonConstants.INLINE_MAP_NAME)
                        .invoke("remove")
                        .arg(navigationProperty.getEdmName()));
        if( isOneToMany ) {
            final JBlock isIterableBlock =
                foundValueBlock._if(newValue._instanceof(codeModel.ref(Iterable.class)))._then();
            final JConditional isNullCheck = isIterableBlock._if(propField.eq(JExpr._null()));
            isNullCheck._then().assign(propField, codeModel.ref(Lists.class).staticInvoke("newArrayList"));
            isNullCheck
                ._else()
                .assign(propField, codeModel.ref(Lists.class).staticInvoke("newArrayList").arg(propField));
            final JVar varI = isIterableBlock.decl(codeModel.INT, "i", JExpr.lit(0));
            final JForEach forEach =
                isIterableBlock
                    .forEach(
                        codeModel.ref(Object.class),
                        "item",
                        JExpr.cast(codeModel.ref(Iterable.class).narrow(codeModel.wildcard()), newValue));
            recursiveCallContainer = forEach.body();
            recursiveCallSource = forEach.var();
            recursiveCallContainer
                ._if(recursiveCallSource._instanceof(codeModel.ref(Map.class)).not())
                ._then()
                ._continue();
            recursiveCallTarget = recursiveCallContainer.decl(associatedEntity, "entity");
            final JConditional foundElement = recursiveCallContainer._if(propField.invoke("size").gt(varI));
            final JBlock foundElementBlock = foundElement._then();
            foundElementBlock.assign(recursiveCallTarget, propField.invoke("get").arg(varI));
            final JBlock notFoundBlock = foundElement._else();
            notFoundBlock.assign(recursiveCallTarget, JExpr._new(associatedEntity));
            notFoundBlock.invoke(propField, "add").arg(recursiveCallTarget);
            recursiveCallContainer.assign(varI, varI.plus(JExpr.lit(1)));
        } else {
            recursiveCallContainer = foundValueBlock._if(newValue._instanceof(codeModel.ref(Map.class)))._then();
            recursiveCallContainer
                ._if(propField.eq(JExpr._null()))
                ._then()
                .assign(propField, JExpr._new(associatedEntity));
            recursiveCallTarget = propField;
            recursiveCallSource = newValue;
        }
        recursiveCallContainer.directStatement("@SuppressWarnings(\"unchecked\")");
        final JVar inputMapVar =
            recursiveCallContainer.decl(JMod.FINAL, mapType, "inputMap", JExpr.cast(mapType, recursiveCallSource));
        recursiveCallContainer.invoke(recursiveCallTarget, "fromMap").arg(inputMapVar);
    }

    private void changeBuilder(
        final NavigationPropertyModel navigationProperty,
        final JDefinedClass associatedEntity,
        final boolean isOneToMany,
        final JFieldVar classMemberField )
    {
        final JDefinedClass builderClass = getOrGenerateBuilder();

        // explicitly define private setter, thus hiding the blocking the Lombok implementation
        final JExpression init = isOneToMany ? codeModel.ref(Lists.class).staticInvoke("newArrayList") : null;
        final String classMemberName = classMemberField.name();
        final JType returnType = classMemberField.type();
        final JFieldVar buildField = builderClass.field(JMod.PRIVATE, returnType, classMemberName, init);
        final JMethod origMethod = builderClass.method(JMod.PRIVATE, builderClass, classMemberName);
        final JVar origMethodVal = origMethod.param(JMod.FINAL, returnType, "cloudSdkValue");
        if( isOneToMany ) {
            origMethod.body().invoke(buildField, "addAll").arg(origMethodVal);
        } else {
            origMethod.body().assign(buildField, origMethodVal);
        }
        origMethod.body()._return(JExpr._this());

        // create public builder setter for navigation property
        final String builderFieldName = navigationProperty.getJavaMethodNameSetBuilder();

        if( SourceVersion.isKeyword(builderFieldName) ) {
            logger.warn("Skip builder setter method with name \"{}\" for being a reserved keyword.", builderFieldName);
        } else {
            final JMethod fixedMethod = builderClass.method(JMod.PUBLIC, builderClass, builderFieldName);

            final JInvocation invocation;
            if( isOneToMany ) {
                final JVar fixedMethodParam = fixedMethod.varParam(associatedEntity, "cloudSdkValue");
                invocation =
                    JExpr
                        .invoke(origMethod)
                        .arg(codeModel.ref(Lists.class).staticInvoke("newArrayList").arg(fixedMethodParam));

                fixedMethod
                    .javadoc()
                    .addParam(fixedMethodParam)
                    .add(String.format("The %ss to build this %s with.", associatedEntity.name(), entityClass.name()));
            } else {
                final JVar fixedMethodParam = fixedMethod.param(JMod.FINAL, associatedEntity, "cloudSdkValue");
                invocation = JExpr.invoke(origMethod).arg(fixedMethodParam);

                fixedMethod
                    .javadoc()
                    .addParam(fixedMethodParam)
                    .add(String.format("The %s to build this %s with.", associatedEntity.name(), entityClass.name()));
            }
            fixedMethod.body()._return(invocation);
            fixedMethod.annotate(Nonnull.class);
            fixedMethod.javadoc().add(classMemberField.javadoc());
            fixedMethod.javadoc().addReturn().add("This Builder to allow for a fluent interface.");
        }

        // if there is a class member with the same name as the navigation property (ignoring "^to")
        final JFieldVar originalField = entityClass.fields().get(builderFieldName);
        if( originalField != null ) {
            provideBuilderMethodFromField(builderClass, originalField);
        }
    }

    private JDefinedClass getOrGenerateBuilder()
    {
        JDefinedClass builderCLass = null;
        final String builderName = entityClass.name() + "Builder";
        for( final Iterator<JDefinedClass> it = entityClass.classes(); it.hasNext(); ) {
            final JDefinedClass cl = it.next();
            if( cl.name().equals(builderName) ) {
                builderCLass = cl;
                break;
            }
        }
        if( builderCLass == null ) {
            try {
                builderCLass = entityClass._class(JMod.PUBLIC | JMod.FINAL | JMod.STATIC, builderName);

                builderCLass
                    .javadoc()
                    .add(
                        String
                            .format("Helper class to allow for fluent creation of %s instances.", entityClass.name()));
            }
            catch( final JClassAlreadyExistsException e ) {
                // should not happen, as we searched for the builder before generating it
                throw new ODataGeneratorException(
                    String.format("Builder class does already exist for entity \"%s\".", entityClass.name()),
                    e);
            }
        }
        return builderCLass;
    }

    private void provideBuilderMethodFromField( final JDefinedClass builderClass, final JFieldVar originalField )
    {
        final JType origType = originalField.type();
        final String origName = originalField.name();
        final JMethod originalMethod = builderClass.method(JMod.PUBLIC, builderClass, origName);
        final JFieldVar origBuildField = builderClass.field(JMod.PRIVATE, origType, origName, JExpr._null());
        final JVar originalMethodParameter = originalMethod.param(JMod.FINAL, origType, "cloudSdkValue");

        originalMethod.javadoc().add(originalField.javadoc());

        originalMethod.body().assign(origBuildField, originalMethodParameter);
        originalMethod.body()._return(JExpr._this());

        originalMethod.annotate(Nonnull.class);
        originalMethod.javadoc().addReturn().add("This Builder to allow for a fluent interface.");

        originalMethod
            .javadoc()
            .addParam(originalMethodParameter.name())
            .add(String.format("The %s to build this %s with.", origName, entityClass.name()));
    }
}
