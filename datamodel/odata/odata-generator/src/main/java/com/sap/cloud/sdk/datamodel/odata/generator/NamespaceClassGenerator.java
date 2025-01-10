package com.sap.cloud.sdk.datamodel.odata.generator;

import static com.sap.cloud.sdk.datamodel.odata.generator.ApiFunction.CREATE;
import static com.sap.cloud.sdk.datamodel.odata.generator.ApiFunction.DELETE;
import static com.sap.cloud.sdk.datamodel.odata.generator.ApiFunction.READ;
import static com.sap.cloud.sdk.datamodel.odata.generator.ApiFunction.READ_BY_KEY;
import static com.sap.cloud.sdk.datamodel.odata.generator.ApiFunction.UPDATE;
import static com.sap.cloud.sdk.datamodel.odata.generator.CodeModelUtils.directExpression;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.generator.ServiceClassGenerator.ServiceClassAmplifier;
import com.sap.cloud.sdk.datamodel.odata.generator.annotation.AnnotationStrategy;
import com.sap.cloud.sdk.datamodel.odata.generator.annotation.EntityAnnotationModel;
import com.sap.cloud.sdk.datamodel.odata.generator.annotation.EntityPropertyAnnotationModel;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmComplex;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmMediaEntity;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingStrategy;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingUtils;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import io.vavr.control.Option;
import lombok.Data;

/**
 * For internal use only.
 */
@SuppressWarnings( "PMD.TooManyStaticImports" )
class NamespaceClassGenerator
{
    private static final Logger logger = MessageCollector.getLogger(NamespaceClassGenerator.class);

    private final Map<String, JPackage> generatedNamespacePackages = new HashMap<>();
    private final Map<String, PreparedEntityBluePrint> entityBluePrintMap = new HashMap<>();
    private final Table<String, ApiFunction, JDefinedClass> generatedFluentHelperClassesPerEntityType =
        HashBasedTable.create();

    private final JCodeModel codeModel;
    private final JPackage namespaceParentPackage;
    private final NamingStrategy codeNamingStrategy;
    private final AnnotationStrategy annotationStrategy;

    private final boolean generatePojosOnly;

    NamespaceClassGenerator(
        final JCodeModel codeModel,
        final JPackage namespaceParentPackage,
        final NamingStrategy codeNamingStrategy,
        final AnnotationStrategy annotationStrategy,
        final boolean generatePojosOnly )
    {
        this.codeModel = codeModel;
        this.namespaceParentPackage = namespaceParentPackage;
        this.codeNamingStrategy = codeNamingStrategy;
        this.annotationStrategy = annotationStrategy;
        this.generatePojosOnly = generatePojosOnly;
    }

    private ClassGeneratorResult generateEdmEntityClass(
        final JPackage namespacePackage,
        final VdmObjectModel entityModel,
        final Service service )
        throws JClassAlreadyExistsException
    {
        // 1) generate class stub
        @Nonnull
        JDefinedClass generatedEntityClass =
            generateEntityClassStub(
                namespacePackage,
                entityModel.getJavaClassName(),
                entityModel.isMediaStreamExposed());

        @Nullable
        final JDefinedClass specificEntitySelectableInterface;
        @Nullable
        final JDefinedClass specificEntityFieldClass;
        @Nullable
        final JDefinedClass entityOneToManyLinkClass;
        @Nullable
        final JDefinedClass entityOneToOneLinkClass;

        if( generatePojosOnly ) {
            specificEntitySelectableInterface = null;
            specificEntityFieldClass = null;
            entityOneToManyLinkClass = null;
            entityOneToOneLinkClass = null;
        } else {
            final EntitySelectableGenerator entitySelectableGenerator = new EntitySelectableGenerator(codeModel);
            final EntityFieldGenerator entityFieldGenerator = new EntityFieldGenerator(codeModel);
            final EntityLinkGenerator entityLinkGenerator = new EntityLinkGenerator(codeModel);

            // 2a) generate specificEntitySelectable
            specificEntitySelectableInterface =
                entitySelectableGenerator
                    .generateSpecificEntitySelectableInterface(namespacePackage, generatedEntityClass);

            // 3) generate specificEntityLink
            entityOneToManyLinkClass =
                entityLinkGenerator
                    .generateSpecificEntityLinkClass(
                        namespacePackage,
                        generatedEntityClass,
                        specificEntitySelectableInterface);

            // 3.1) generate specificEntityOneToOneLink
            entityOneToOneLinkClass =
                entityLinkGenerator
                    .generateSpecificEntityLinkOneToOneClass(
                        namespacePackage,
                        generatedEntityClass,
                        entityOneToManyLinkClass,
                        service);

            // 4) generate specificEntityField
            specificEntityFieldClass =
                entityFieldGenerator
                    .generateSpecificEntityFieldClass(
                        namespacePackage,
                        generatedEntityClass,
                        specificEntitySelectableInterface);

            // 2b) add javadoc to the specificEntitySelectable
            entitySelectableGenerator
                .addClassLevelJavadoc(
                    specificEntitySelectableInterface,
                    generatedEntityClass,
                    specificEntityFieldClass,
                    entityOneToManyLinkClass);
        }
        // 5) generate rest of the entityClass
        generatedEntityClass =
            completeEntityClassGeneration(
                generatedEntityClass,
                specificEntityFieldClass,
                entityModel,
                specificEntitySelectableInterface);

        return new ClassGeneratorResult(
            generatedEntityClass,
            specificEntitySelectableInterface,
            entityOneToManyLinkClass,
            entityOneToOneLinkClass);
    }

    private JDefinedClass generateEntityClassStub(
        final JClassContainer namespacePackage,
        final String javaClassName,
        final boolean isMediaStreamExposed )
        throws JClassAlreadyExistsException
    {
        final JDefinedClass entityClass = namespacePackage._class(JMod.PUBLIC, javaClassName);
        final JClass jClass;
        if( isMediaStreamExposed ) {
            jClass = codeModel.ref(VdmMediaEntity.class);
        } else {
            jClass = codeModel.ref(VdmEntity.class);
        }
        entityClass._extends(jClass.narrow(entityClass));
        return entityClass;
    }

    private JDefinedClass completeEntityClassGeneration(
        final JDefinedClass entityClass,
        final JDefinedClass specificEntityFieldClass,
        final VdmObjectModel entityModel,
        final JDefinedClass selectableInterface )
    {
        // getType method
        final JMethod getTypeMethod =
            entityClass.method(JMod.PUBLIC, codeModel.ref(Class.class).narrow(entityClass), "getType");
        getTypeMethod.annotate(codeModel.ref(Nonnull.class));
        getTypeMethod.annotate(codeModel.ref(Override.class));
        getTypeMethod.body()._return(entityClass.dotclass());

        // add description to entity class
        if( !Strings.isNullOrEmpty(entityModel.getDescription()) ) {
            entityClass.javadoc().add(entityModel.getDescription());
        }
        entityClass
            .javadoc()
            .add(String.format("<p>Original entity name from the Odata EDM: <b>%s</b></p>", entityModel.getEdmName()));

        // create class constant for link to ALL FIELDS
        if( !generatePojosOnly ) {
            createMemberAllFields(entityClass, selectableInterface);
        }

        final EntityAnnotationModel entityAnnotationModel = new VdmObjectModelAnnotationWrapper(entityModel);
        AnnotationHelper
            .addAllAnnotationsToJavaItem(
                annotationStrategy.getAnnotationsForEntity(entityAnnotationModel),
                entityClass);

        for( final Map.Entry<String, EntityPropertyModel> entry : entityModel.getProperties().entrySet() ) {
            final EntityPropertyModel mapping = entry.getValue();
            addPropertyAsField(entityClass, specificEntityFieldClass, selectableInterface, mapping);
        }

        // String getEntityCollection()
        final JMethod entityCollectionMethod = entityClass.method(JMod.PROTECTED, String.class, "getEntityCollection");
        entityCollectionMethod.annotate(Override.class);
        entityCollectionMethod.body()._return(JExpr.lit(entityModel.getEdmEntityCollectionName()));

        if( !generatePojosOnly ) {
            // Map<String,Object> getKey()
            createMethodGetKey(entityModel.getProperties(), entityClass);

            // Map<String,Object> toMap()
            createMethodToMap(entityModel.getProperties(), entityClass);

            // void fromMap(Map<String,Object>)
            processClassFields(entityModel.getProperties(), entityClass);

            final FieldFunctionGenerator fieldFunctionGenerator = new FieldFunctionGenerator(codeModel);
            fieldFunctionGenerator.addFieldFunction(entityClass, specificEntityFieldClass);
            fieldFunctionGenerator.addFieldFunctionWithTypeConverter(entityClass, specificEntityFieldClass);

            overrideGetDestinationForFetchMethod(entityClass);
            overrideSetServicePathForFetchMethod(entityClass);
            overrideAttachToServiceMethod(entityClass);
        }
        return entityClass;
    }

    private void addPropertyAsField(
        final JDefinedClass entityClass,
        final JDefinedClass specificEntityFieldClass,
        final JDefinedClass selectableInterface,
        final EntityPropertyModel mapping )
    {
        final JFieldVar entityClassField =
            entityClass.field(JMod.PRIVATE, mapping.getJavaFieldType(), mapping.getJavaFieldName());

        if( mapping.isKeyField() ) {
            entityClassField.javadoc().add("(Key Field)");
        }
        // De-lombok has a bug with the javadoc copy for getters & setters. It cannot handle the javadoc format
        // generated by Codemodel. Combining constraints and details into main javadoc comment for now.
        entityClassField.javadoc().add(mapping.getConstraintsDescription());
        entityClassField
            .javadoc()
            .add(String.format("<p>Original property name from the Odata EDM: <b>%s</b></p>", mapping.getEdmName()));

        if( !Strings.isNullOrEmpty(mapping.getDetailedDescription()) ) {
            entityClassField.javadoc().add(String.format("<p>%s</p>", mapping.getDetailedDescription()));
        }
        if( !Strings.isNullOrEmpty(mapping.getBasicDescription()) ) {
            entityClassField.javadoc().addReturn().add(mapping.getBasicDescription());
        } else {
            entityClassField
                .javadoc()
                .addReturn()
                .add(String.format("The %s contained in this entity.", entityClassField.name()));
        }

        final EntityPropertyAnnotationModel propertyAnnotationModel = new EntityPropertyModelAnnotationWrapper(mapping);
        AnnotationHelper
            .addAllAnnotationsToJavaItem(
                annotationStrategy.getAnnotationsForEntityProperty(propertyAnnotationModel),
                entityClassField);

        generateSetterMethod(entityClass, entityClassField, mapping.getEdmName(), mapping.getBasicDescription());

        if( !generatePojosOnly && mapping.isSimpleType() ) {
            final JFieldVar fluentHelperField =
                entityClass
                    .field(
                        JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
                        specificEntityFieldClass.narrow(mapping.getJavaFieldType()),
                        mapping.getJavaConstantName(),
                        JExpr
                            ._new(specificEntityFieldClass.narrow(mapping.getJavaFieldType()))
                            .arg(mapping.getEdmName()));

            fluentHelperField
                .javadoc()
                .add(
                    String
                        .format(
                            "Use with available fluent helpers to apply the <b>%s</b> field to query operations.",
                            mapping.getEdmName()));

            // add fluentHelperField class to EntitySelectable java docs
            JavadocUtils.addFieldReference(selectableInterface, entityClass, fluentHelperField);
        }
    }

    private void generateSetterMethod(
        @Nonnull final JDefinedClass entityClass,
        @Nonnull final JFieldVar fieldVar,
        @Nonnull final String fieldName,
        @Nullable final String parameterDescription )
    {
        final JMethod setterMethod =
            entityClass.method(JMod.PUBLIC, codeModel.VOID, "set" + StringUtils.capitalize(fieldVar.name()));
        final JVar inputParam = setterMethod.param(JMod.FINAL, fieldVar.type(), fieldVar.name());
        inputParam.annotate(Nullable.class);
        setterMethod.body().invoke("rememberChangedField").arg(fieldName).arg(JExpr._this().ref(fieldVar));
        setterMethod.body().assign(JExpr._this().ref(fieldVar.name()), inputParam);

        final String paramJavadoc;
        if( Strings.isNullOrEmpty(parameterDescription) ) {
            paramJavadoc = String.format("The %s to set.", fieldVar.name());
        } else {
            paramJavadoc = parameterDescription;
        }

        setterMethod.javadoc().add(fieldVar.javadoc());
        setterMethod.javadoc().addParam(inputParam).append(paramJavadoc);
    }

    void addNavigationPropertyCode(
        final PreparedEntityBluePrint entityBluePrint,
        final Map<String, JDefinedClass> generatedEntities )
    {
        final NavigationPropertyMethodsGenerator navPropGenerator =
            new NavigationPropertyMethodsGenerator(codeModel, entityBluePrint.getEntityClass());

        final Map<String, JFieldVar> generatedNavigationPropertyFields =
            navPropGenerator
                .createNavigationPropertyFields(
                    entityBluePrint.getNavigationProperties(),
                    generatedEntities,
                    annotationStrategy);

        if( !generatePojosOnly ) {
            navPropGenerator
                .addNavigationPropertyMethods(
                    generatedEntities,
                    entityBluePrint.getNavigationProperties(),
                    generatedNavigationPropertyFields,
                    entityBluePrint.getSelectableInterface(),
                    entityBluePrint.getEntityOneToManyLinkClass(),
                    entityBluePrint.getEntityOneToOneLinkClass());
        }
    }

    private
        JDefinedClass
        generateComplexTypeClass( final JPackage namespacePackage, final VdmObjectModel complexTypeModel )
            throws JClassAlreadyExistsException
    {
        final JDefinedClass complexTypeClass =
            namespacePackage._class(JMod.PUBLIC, complexTypeModel.getJavaClassName());
        complexTypeClass._extends(codeModel.ref(VdmComplex.class).narrow(complexTypeClass));

        final JMethod getTypeMethod =
            complexTypeClass.method(JMod.PUBLIC, codeModel.ref(Class.class).narrow(complexTypeClass), "getType");

        getTypeMethod.annotate(codeModel.ref(Nonnull.class));
        getTypeMethod.annotate(codeModel.ref(Override.class));
        getTypeMethod.body()._return(complexTypeClass.dotclass());

        if( !Strings.isNullOrEmpty(complexTypeModel.getDescription()) ) {
            complexTypeClass.javadoc().add(complexTypeModel.getDescription());
        }
        complexTypeClass
            .javadoc()
            .add(
                String
                    .format(
                        "<p>Original complex type name from the Odata EDM: <b>%s</b></p>",
                        complexTypeModel.getEdmName()));

        final EntityAnnotationModel complexTypeAnnotationModel = new VdmObjectModelAnnotationWrapper(complexTypeModel);
        AnnotationHelper
            .addAllAnnotationsToJavaItem(
                annotationStrategy.getAnnotationsForComplexType(complexTypeAnnotationModel),
                complexTypeClass);

        // Map<String,Object> toMap()
        createMethodToMap(complexTypeModel.getProperties(), complexTypeClass);

        // void fromMap(Map<String,Object>()
        processClassFields(complexTypeModel.getProperties(), complexTypeClass);

        // Map<String,Object> getKey()
        createMethodGetKey(complexTypeModel.getProperties(), complexTypeClass);

        // Base entity portion
        for( final Map.Entry<String, EntityPropertyModel> entry : complexTypeModel.getProperties().entrySet() ) {
            final EntityPropertyModel mapping = entry.getValue();

            processComplexTypeClassField(complexTypeClass, mapping);
        }

        return complexTypeClass;
    }

    private void processComplexTypeClassField( final JDefinedClass complexTypeClass, final EntityPropertyModel mapping )
    {
        final JFieldVar complexTypeClassField =
            complexTypeClass.field(JMod.PRIVATE, mapping.getJavaFieldType(), mapping.getJavaFieldName());

        if( mapping.isKeyField() ) {
            complexTypeClassField.javadoc().add("(Key Field) ");
        }
        // De-lombok has a bug with the javadoc copy for getters & setters. It cannot handle the javadoc format
        // generated by Codemodel. Combining constraints and details into main javadoc comment for now.
        complexTypeClassField.javadoc().add(mapping.getConstraintsDescription());
        complexTypeClassField
            .javadoc()
            .add(String.format("<p>Original property from the Odata EDM: <b>%s</b></p>", mapping.getEdmName()));

        if( !Strings.isNullOrEmpty(mapping.getDetailedDescription()) ) {
            complexTypeClassField.javadoc().add(String.format("<p>%s</p>", mapping.getDetailedDescription()));
        }
        if( !Strings.isNullOrEmpty(mapping.getBasicDescription()) ) {
            complexTypeClassField.javadoc().addReturn().add(mapping.getBasicDescription());
        }
        // Leaving it blank until Lombok gets fixed to avoid weird looking javadocs.
        complexTypeClassField.javadoc().addParam(complexTypeClassField).add("");

        final EntityPropertyAnnotationModel complexTypePropertyAnnotationModel =
            new EntityPropertyModelAnnotationWrapper(mapping);
        AnnotationHelper
            .addAllAnnotationsToJavaItem(
                annotationStrategy.getAnnotationsForComplexTypeProperty(complexTypePropertyAnnotationModel),
                complexTypeClassField);

        generateSetterMethod(
            complexTypeClass,
            complexTypeClassField,
            mapping.getEdmName(),
            mapping.getBasicDescription());
    }

    private void createMemberAllFields( final JDefinedClass entityClass, final JDefinedClass selectableInterface )
    {
        final String linkConstantName = "ALL_FIELDS";
        entityClass
            .field(
                JMod.FINAL | JMod.PUBLIC | JMod.STATIC,
                selectableInterface,
                linkConstantName,
                directExpression("() -> \"*\""))
            .javadoc()
            .add("Selector for all available fields of " + entityClass.name() + ".");
    }

    private
        void
        createMethodToMap( final Map<String, EntityPropertyModel> entityClassFields, final JDefinedClass entityClass )
    {
        final JClass fieldMapClass = codeModel.ref(Map.class).narrow(String.class, Object.class);
        final JMethod toMapMethod = entityClass.method(JMod.PROTECTED, fieldMapClass, "toMapOfFields");
        toMapMethod.annotate(Nonnull.class);
        toMapMethod.annotate(Override.class);
        final JBlock body = toMapMethod.body();
        final JVar v =
            body
                .decl(
                    JMod.FINAL,
                    fieldMapClass,
                    CommonConstants.INLINE_MAP_NAME,
                    JExpr._super().invoke("toMapOfFields"));
        for( final Map.Entry<String, EntityPropertyModel> entry : entityClassFields.entrySet() ) {
            final String javaFieldName = entry.getValue().getJavaFieldName();
            final String javaMethodName = "get" + StringUtils.capitalize(javaFieldName);
            body.invoke(v, "put").arg(entry.getKey()).arg(JExpr.invoke(javaMethodName));
        }
        body._return(v);
    }

    private
        void
        processClassFields( final Map<String, EntityPropertyModel> entityClassFields, final JDefinedClass entityClass )
    {
        final JClass fieldMapClass = codeModel.ref(Map.class).narrow(String.class, Object.class);
        final JMethod fromMapMethod = entityClass.method(JMod.PROTECTED, codeModel.VOID, "fromMap");
        fromMapMethod.annotate(Override.class);
        final JVar inputValues = fromMapMethod.param(JMod.FINAL, fieldMapClass, "inputValues");
        final JBlock body = fromMapMethod.body();
        final JVar values =
            body
                .decl(
                    JMod.FINAL,
                    fieldMapClass,
                    CommonConstants.INLINE_MAP_NAME,
                    codeModel.ref(Maps.class).staticInvoke("newLinkedHashMap").arg(inputValues));

        body.block().directStatement("// simple properties");
        final JBlock simplePropertiesBlock = new JBlock(true, true);
        body.add(simplePropertiesBlock);

        body.block().directStatement("// structured properties");
        final JBlock complexPropertiesBlock = new JBlock(true, true);
        body.add(complexPropertiesBlock);

        // loop for properties
        for( final Map.Entry<String, EntityPropertyModel> entry : entityClassFields.entrySet() ) {
            processClassField(fieldMapClass, values, simplePropertiesBlock, complexPropertiesBlock, entry);
        }

        // placeholder for navigation properties
        body.block().directStatement("// navigation properties");
        final JBlock navigationPropertiesBlock = new JBlock(true, true);
        body.add(navigationPropertiesBlock);
        body.block().invoke(JExpr._super(), "fromMap").arg(values);
    }

    private void processClassField(
        final JClass fieldMapClass,
        final JVar values,
        final JBlock simplePropertiesBlock,
        final JBlock complexPropertiesBlock,
        final Map.Entry<String, EntityPropertyModel> entry )
    {
        final String javaFieldName = entry.getValue().getJavaFieldName();
        final String javaMethodGet = "get" + StringUtils.capitalize(javaFieldName);
        final String javaMethodSet = "set" + StringUtils.capitalize(javaFieldName);

        if( entry.getValue().isSimpleType() ) {
            // continue with non-complex / simple property
            final JBlock ifFoundBody =
                simplePropertiesBlock._if(values.invoke("containsKey").arg(entry.getKey()))._then();
            final JVar value =
                ifFoundBody
                    .decl(
                        JMod.FINAL,
                        codeModel.ref(Object.class),
                        "value",
                        values.invoke("remove").arg(entry.getKey()));
            final JBlock ifChangeBody =
                ifFoundBody
                    ._if(value.eq(JExpr._null()).cor(value.invoke("equals").arg(JExpr.invoke(javaMethodGet)).not()))
                    ._then();
            ifChangeBody.invoke(javaMethodSet).arg(JExpr.cast(entry.getValue().getJavaFieldType(), value));
        } else {
            // continue with complex properties
            final JBlock ifFoundBody =
                complexPropertiesBlock._if(values.invoke("containsKey").arg(entry.getKey()))._then();
            final JVar value =
                ifFoundBody
                    .decl(
                        JMod.FINAL,
                        codeModel.ref(Object.class),
                        "value",
                        values.invoke("remove").arg(entry.getKey()));
            final JBlock ifIsMap = ifFoundBody._if(value._instanceof(codeModel.ref(Map.class)))._then();
            ifIsMap
                ._if(JExpr.invoke(javaMethodGet).eq(JExpr._null()))
                ._then()
                .invoke(javaMethodSet)
                .arg(JExpr._new(entry.getValue().getJavaFieldType()));
            ifIsMap.directStatement("@SuppressWarnings(\"unchecked\")");
            final JVar varInputMap =
                ifIsMap.decl(JMod.FINAL, fieldMapClass, "inputMap", JExpr.cast(fieldMapClass, value));
            ifIsMap.invoke(JExpr.invoke(javaMethodGet), "fromMap").arg(varInputMap);
            ifFoundBody
                ._if(value.eq(JExpr._null()).cand(JExpr.invoke(javaMethodGet).ne(JExpr._null())))
                ._then()
                .invoke(javaMethodSet)
                .arg(JExpr._null());
        }
    }

    private
        void
        createMethodGetKey( final Map<String, EntityPropertyModel> entityClassFields, final JDefinedClass entityClass )
    {
        final JClass fieldMapClass = codeModel.ref(Map.class).narrow(String.class, Object.class);
        final JMethod getKeyMethod = entityClass.method(JMod.PROTECTED, fieldMapClass, "getKey");
        getKeyMethod.annotate(Nonnull.class);
        getKeyMethod.annotate(Override.class);
        final JBlock body = getKeyMethod.body();
        final JVar v =
            body.decl(JMod.FINAL, fieldMapClass, "result", codeModel.ref(Maps.class).staticInvoke("newLinkedHashMap"));
        for( final Map.Entry<String, EntityPropertyModel> entry : entityClassFields.entrySet() ) {
            if( entry.getValue().isKeyField() ) {
                final String javaFieldName = entry.getValue().getJavaFieldName();
                final String javaMethodName = "get" + StringUtils.capitalize(javaFieldName);
                body.invoke(v, "put").arg(entry.getKey()).arg(JExpr.invoke(javaMethodName));
            }
        }
        body._return(v);
    }

    private JPackage getOrGenerateNamespacePackage( final String packageName )
    {
        JPackage namespacePackage = generatedNamespacePackages.get(packageName);
        if( namespacePackage == null ) {
            namespacePackage = namespaceParentPackage.subPackage(packageName);
            generatedNamespacePackages.put(packageName, namespacePackage);
        }
        return namespacePackage;
    }

    void processFunctionImport(
        final ServiceClassGenerator serviceClassGenerator,
        final FluentHelperClassGenerator fluentGenerator,
        final Service service,
        final Map<String, JDefinedClass> generatedEntities,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Service.FunctionImport functionImport,
        final NamingContext functionImportClassNamingContext,
        final NamingContext functionImportFetchMethodNamingContext )
        throws JClassAlreadyExistsException
    {
        final ServiceClassAmplifier serviceClassAmplifier =
            serviceClassGenerator.getOrGenerateServiceClassesAndGetAmplifier(service);

        final JPackage namespacePackage;
        JType javaReturnType;

        @Nullable
        final Service.Type edmReturnType = functionImport.getReturnType();

        if( edmReturnType == null ) {
            javaReturnType = codeModel.ref(Void.class);
        } else {
            switch( edmReturnType.getKind() ) {
                case SIMPLE: {
                    final Service.SimpleType edmSimpleReturnType = (Service.SimpleType) edmReturnType;
                    javaReturnType = codeModel.ref(edmSimpleReturnType.getDefaultType());
                    break;
                }
                case COMPLEX: {
                    namespacePackage = getOrGenerateNamespacePackage(service.getJavaPackageName());

                    javaReturnType = generatedComplexTypes.get(edmReturnType.getName());
                    if( javaReturnType == null ) {
                        final Service.ComplexType edmComplexReturnType = (Service.ComplexType) edmReturnType;
                        final JDefinedClass generatedComplexType =
                            processComplexType(namespacePackage, generatedComplexTypes, edmComplexReturnType);
                        generatedComplexTypes.put(edmReturnType.getName(), generatedComplexType);
                        javaReturnType = generatedComplexType;
                    }
                    break;
                }
                case ENTITY: {
                    javaReturnType = generatedEntities.get(edmReturnType.getName());
                    if( javaReturnType == null ) {
                        logger.warn(String.format("""
                            Unable to generate code for function import '%s': Return type is an entity (%s), \
                            but it is either not found or its entity set has been filtered out.\
                            """, functionImport.getName(), edmReturnType.getName()));
                        return;
                    }
                    break;
                }
                default: {
                    logger.error("Unsupported EDM return type found for function import " + functionImport.getName());
                    return;
                }
            }
        }

        final boolean isCollectionReturnType = functionImport.getReturnTypeMultiplicity() == Multiplicity.MANY;

        final List<FunctionImportParameterModel> parameters = processFunctionImportParameters(functionImport);
        if( parameters == null ) {
            return;
        }

        final String functionImportName = functionImport.getName();
        final String functionImportLabel = functionImport.getLabel();
        final String functionImportDescription = JavadocUtils.getCompleteDescription(functionImport);

        final JDefinedClass functionImportFluentHelperClass =
            fluentGenerator
                .generateFunctionImportFluentHelperClass(
                    getOrGenerateNamespacePackage(service.getJavaPackageName()),
                    functionImportName,
                    functionImportLabel,
                    javaReturnType,
                    isCollectionReturnType,
                    functionImport.getHttpMethod(),
                    parameters,
                    functionImportClassNamingContext);

        serviceClassAmplifier
            .addFunctionImportMethod(
                functionImportName,
                functionImportLabel,
                functionImportDescription,
                parameters,
                functionImportFluentHelperClass,
                functionImportFetchMethodNamingContext);
    }

    private List<FunctionImportParameterModel> processFunctionImportParameters(
        final Service.FunctionImport functionImport )
    {
        final List<FunctionImportParameterModel> functionImportParameters = new LinkedList<>();
        final NamingContext functionImportParameterNamingContext = new NamingContext();

        for( final String parameterName : functionImport.getParameterNames() ) {
            final Service.Parameter parameter = functionImport.getParameter(parameterName);
            final Service.Type edmParameterType = parameter.getType();

            if( edmParameterType.getKind() == TypeKind.SIMPLE ) {
                final Service.SimpleType edmParameterSimpleType = (Service.SimpleType) edmParameterType;
                final String parameterLabel = parameter.getLabel();

                final String javaName =
                    functionImportParameterNamingContext
                        .ensureUniqueName(
                            codeNamingStrategy.generateJavaMethodParameterName(parameterName, parameterLabel));
                final JType javaType = codeModel.ref(edmParameterSimpleType.getDefaultType());

                functionImportParameters
                    .add(
                        new FunctionImportParameterModel(
                            parameterName,
                            edmParameterType.getName(),
                            javaName,
                            javaType,
                            JavadocUtils.getDescriptionAndConstraints(parameterName, parameter),
                            Option.of(parameter.getFacets()).map(Service.Facets::isNullable).getOrNull()));
            } else {
                logger
                    .warn(
                        "Skipping generation of function import {}: The function has a non-simple parameter {}. This feature is not yet supported by the OData V2 generator.",
                        functionImport.getName(),
                        parameterName);
                return null;
            }
        }
        return functionImportParameters;
    }

    private JDefinedClass processComplexType(
        final JPackage namespacePackage,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Service.ComplexType complexTypeMetadata )
        throws JClassAlreadyExistsException
    {
        logger.info("  Found complex type " + complexTypeMetadata.getName());

        final String complexTypeDescription = JavadocUtils.getCompleteDescription(complexTypeMetadata);

        final NamingContext complexTypePropertyNamingContext = new NamingContext();
        complexTypePropertyNamingContext.loadGettersAndSettersOfClassAsAlreadyPresentFields(VdmComplex.class);

        final Map<String, EntityPropertyModel> entityClassFields =
            processProperties(
                namespacePackage,
                generatedComplexTypes,
                complexTypeMetadata,
                complexTypePropertyNamingContext,
                new NamingContext());

        final VdmObjectModel complexTypeModel =
            new VdmObjectModel(
                complexTypeMetadata.getName(),
                complexTypeMetadata.getLabel(),
                null,
                null,
                complexTypeMetadata.getName(),
                entityClassFields,
                complexTypeDescription,
                false);

        return generateComplexTypeClass(namespacePackage, complexTypeModel);
    }

    private Map<String, EntityPropertyModel> processProperties(
        final JPackage namespacePackage,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Service.StructuralType typeMetadata,
        final NamingContext entityNamingContext,
        final NamingContext entityClassConstantNamingContext )
        throws JClassAlreadyExistsException
    {
        final Map<String, EntityPropertyModel> entityClassFields = new LinkedHashMap<>();

        for( final String propertyName : typeMetadata.getPropertyNames() ) {
            final EntityPropertyModel entityPropertyModel =
                processProperty(
                    namespacePackage,
                    generatedComplexTypes,
                    typeMetadata,
                    propertyName,
                    entityNamingContext,
                    entityClassConstantNamingContext);
            entityClassFields.put(propertyName, entityPropertyModel);
        }
        return entityClassFields;
    }

    private EntityPropertyModel processProperty(
        final JPackage namespacePackage,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Service.StructuralType typeMetadata,
        final String propertyName,
        final NamingContext entityNamingContext,
        final NamingContext entityClassConstantNamingContext )
        throws JClassAlreadyExistsException
    {
        final Service.Typed propertyMetadata = typeMetadata.getProperty(propertyName);
        final Service.Type propertyType = propertyMetadata.getType();
        final TypeKind propertyKind = propertyType.getKind();

        // Not the nicest way, but I haven't found any cleaner way to get an EdmAnnotatable object from Olingo
        String propertyBasicDescription = null;
        String propertyDetailedDescription = null;
        String propertyConstraintsDescription = null;
        String propertyLabel = null;

        if( propertyMetadata instanceof Service.Property ) {
            propertyBasicDescription = JavadocUtils.getBasicDescription((Service.Annotatable) propertyMetadata);
            propertyDetailedDescription = JavadocUtils.getDetailedDescription((Service.Annotatable) propertyMetadata);
            propertyConstraintsDescription = JavadocUtils.getConstraints((Service.Element) propertyMetadata);
            propertyLabel = ((Service.Annotatable) propertyMetadata).getLabel();
        }

        final String classFieldName =
            entityNamingContext.ensureUniqueName(codeNamingStrategy.generateJavaFieldName(propertyName, propertyLabel));
        final String classConstantName =
            entityClassConstantNamingContext
                .ensureUniqueName(codeNamingStrategy.generateJavaConstantName(classFieldName, null));

        switch( propertyKind ) {
            case SIMPLE:
                final Service.SimpleType simpleType = (Service.SimpleType) propertyType;
                final Class<?> classFieldType = simpleType.getDefaultType();

                return new EntityPropertyModel(
                    propertyName,
                    propertyLabel,
                    simpleType.getName(),
                    classFieldName,
                    codeModel.ref(classFieldType),
                    classConstantName,
                    true,
                    false,
                    propertyBasicDescription,
                    propertyDetailedDescription,
                    propertyConstraintsDescription);

            case COMPLEX:
                final Service.ComplexType complexType = (Service.ComplexType) propertyType;
                final String complexTypeName = complexType.getName();

                JDefinedClass generatedComplexType = generatedComplexTypes.get(complexTypeName);
                if( generatedComplexType == null ) {
                    generatedComplexType = processComplexType(namespacePackage, generatedComplexTypes, complexType);
                    generatedComplexTypes.put(complexTypeName, generatedComplexType);
                }
                return new EntityPropertyModel(
                    propertyName,
                    propertyLabel,
                    complexType.getName(),
                    classFieldName,
                    generatedComplexType,
                    classConstantName,
                    false,
                    false,
                    propertyBasicDescription,
                    propertyDetailedDescription,
                    propertyConstraintsDescription);

            default:
                logger.error(String.format("""
                        Unsupported type detected:
                          property name: %s, type: %s\
                    """, propertyName, propertyType.getKind().toString()));
                return null;
        }
    }

    Option<PreparedEntityBluePrint> processEntitySet(
        final ServiceClassGenerator serviceClassGenerator,
        final FluentHelperClassGenerator fluentGenerator,
        final Service service,
        final String odataEndpointPath,
        final Map<String, JDefinedClass> generatedEntities,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Service.EntitySet entitySet,
        final NamingContext entityClassNamingContext,
        final boolean serviceMethodsPerEntitySet )
        throws JClassAlreadyExistsException
    {
        final Service.EntityType entityType = entitySet.getEntityType();
        final String entityTypeName = entityType.getName();

        logger.info("  Found entity type " + entityTypeName + " from set " + entitySet.getName());

        final JDefinedClass entityClass;
        final Option<PreparedEntityBluePrint> maybeGeneratedBlueprint;
        final JPackage namespacePackage = getOrGenerateNamespacePackage(service.getJavaPackageName());
        final List<EntityPropertyModel> keyProperties;

        if( !generatedEntities.containsKey(entityTypeName) ) {
            generatedFluentHelperClassesPerEntityType.clear();

            final String entityTypeLabel = entityType.getLabel();
            final String entityDescription = JavadocUtils.getCompleteDescription(entityType);

            final String javaClassName =
                entityClassNamingContext
                    .ensureUniqueName(codeNamingStrategy.generateJavaClassName(entityTypeName, entityTypeLabel));

            final NamingContext entityPropertyNamingContext = new NamingContext(new LowercaseNameFormattingStrategy());
            entityPropertyNamingContext.loadGettersAndSettersOfClassAsAlreadyPresentFields(VdmEntity.class);
            entityPropertyNamingContext.loadKnownGeneratedFields();

            final NamingContext entityClassConstantNamingContext = new NamingContext();

            final Map<String, EntityPropertyModel> entityClassFields =
                processProperties(
                    namespacePackage,
                    generatedComplexTypes,
                    entityType,
                    entityPropertyNamingContext,
                    entityClassConstantNamingContext);

            final VdmObjectModel entityModel =
                new VdmObjectModel(
                    entityTypeName,
                    entityTypeLabel,
                    odataEndpointPath,
                    entitySet.getName(),
                    javaClassName,
                    entityClassFields,
                    entityDescription,
                    entityType.hasMediaStream());

            keyProperties = processKeyProperties(entityType, entityClassFields);

            final List<NavigationPropertyModel> navigationProperties =
                processNavigationProperties(entityType, entityPropertyNamingContext, entityClassConstantNamingContext);

            final NamespaceClassGenerator.ClassGeneratorResult result =
                generateEdmEntityClass(namespacePackage, entityModel, service);

            entityClass = result.getGeneratedEntityClass();
            generatedEntities.put(entityTypeName, entityClass);

            final JDefinedClass specificEntitySelectableInterface = result.getSpecificEntitySelectableInterface();

            final JDefinedClass entityOneToManyLink = result.getEntityOneToManyLink();
            final JDefinedClass entityOneToOneLink = result.getEntityOneToOneLink();

            final PreparedEntityBluePrint entityBluePrint =
                new PreparedEntityBluePrint(
                    entityClass,
                    specificEntitySelectableInterface,
                    entityOneToManyLink,
                    entityOneToOneLink,
                    navigationProperties,
                    keyProperties);

            entityBluePrintMap.put(entityTypeName, entityBluePrint);

            maybeGeneratedBlueprint = Option.of(entityBluePrint);
        } else {
            logger
                .info(
                    "Model class for entity type "
                        + entityTypeName
                        + " has already been generated. Proceeding with generating the service methods.");

            entityClass = generatedEntities.get(entityTypeName);
            maybeGeneratedBlueprint = Option.none();
        }

        // continue request builder creation if keys are available or entity was already generated
        if( generatePojosOnly ) {
            return maybeGeneratedBlueprint;
        }

        final ServiceClassGenerator.ServiceClassAmplifier serviceClassAmplifier =
            serviceClassGenerator.getOrGenerateServiceClassesAndGetAmplifier(service);

        if( maybeGeneratedBlueprint.isDefined() ) {
            addDefaultServicePathMethod(entityClass, serviceClassAmplifier.getServiceInterfaceClass(), service);
        }

        if( maybeGeneratedBlueprint.isEmpty() && !serviceMethodsPerEntitySet ) {
            return maybeGeneratedBlueprint;
        }

        final Collection<ApiFunction> allowedFunctions = service.getAllowedFunctionsByEntity(entitySet.getName());

        final PreparedEntityBluePrint entityBluePrint =
            maybeGeneratedBlueprint.getOrElse(() -> entityBluePrintMap.get(entityType.getName()));

        if( allowedFunctions != null && !allowedFunctions.isEmpty() ) {
            final List<EntityPropertyModel> entityKeyProperties = entityBluePrint.getKeyProperties();

            addAllowedFunctions(
                serviceClassAmplifier,
                fluentGenerator,
                entityClassNamingContext,
                new EntityMetadata(namespacePackage, entityClass, entityTypeName, entitySet.getName()),
                allowedFunctions,
                entityKeyProperties,
                entityBluePrint.getSelectableInterface());
        }

        return maybeGeneratedBlueprint;
    }

    private void addDefaultServicePathMethod(
        final JDefinedClass generatedEntityClass,
        final JDefinedClass serviceInterfaceClass,
        final Service service )
    {
        final JMethod defaultServicePathMethod =
            generatedEntityClass.method(JMod.PROTECTED, String.class, "getDefaultServicePath");

        defaultServicePathMethod.annotate(Override.class);
        DeprecationUtils.addGetDefaultServicePathBody(defaultServicePathMethod, serviceInterfaceClass, service);
    }

    private void addAllowedFunctions(
        final ServiceClassAmplifier serviceClassAmplifier,
        final FluentHelperClassGenerator fluentGenerator,
        final NamingContext entityClassNamingContext,
        final EntityMetadata entityMetadata,
        final Collection<ApiFunction> allowedFunctions,
        final Iterable<EntityPropertyModel> keyProperties,
        final JDefinedClass specificEntitySelectableInterface )
        throws JClassAlreadyExistsException
    {
        final Iterable<ApiFunction> functionsToAdd;
        if( allowedFunctions.isEmpty() ) {
            functionsToAdd = Collections.emptyList();
        } else {
            // Using an enum set to leverage the natural order of the enum; necessary to get a deterministic behavior
            functionsToAdd = EnumSet.copyOf(allowedFunctions);
        }
        for( final ApiFunction function : functionsToAdd ) {
            addFunction(
                serviceClassAmplifier,
                fluentGenerator,
                entityClassNamingContext,
                entityMetadata,
                function,
                keyProperties,
                specificEntitySelectableInterface);
        }
    }

    private void addFunction(
        final ServiceClassAmplifier serviceClassAmplifier,
        final FluentHelperClassGenerator fluentGenerator,
        final NamingContext entityClassNamingContext,
        final EntityMetadata entityMetadata,
        final ApiFunction function,
        Iterable<EntityPropertyModel> keyProperties,
        final JDefinedClass specificEntitySelectableInterface )
        throws JClassAlreadyExistsException
    {
        JDefinedClass generatedFluentHelperClass;
        final String entityClassName = entityMetadata.getGeneratedEntityClass().name();

        switch( function ) {
            case READ:
                generatedFluentHelperClass = generatedFluentHelperClassesPerEntityType.get(entityClassName, READ);

                if( generatedFluentHelperClass == null ) {
                    generatedFluentHelperClass =
                        fluentGenerator
                            .generateEntityReadFluentHelperClass(
                                entityClassNamingContext,
                                entityMetadata.getNamespacePackage(),
                                entityMetadata.getGeneratedEntityClass(),
                                specificEntitySelectableInterface);

                    generatedFluentHelperClassesPerEntityType.put(entityClassName, READ, generatedFluentHelperClass);
                }

                serviceClassAmplifier.addGetAllMethod(entityMetadata, generatedFluentHelperClass);
                break;
            case READ_BY_KEY:
                generatedFluentHelperClass =
                    generatedFluentHelperClassesPerEntityType.get(entityClassName, READ_BY_KEY);

                final String getByKeyMethodName = serviceClassAmplifier.getByKeyMethodName(entityMetadata);
                keyProperties = serviceClassAmplifier.getRefinedKeyProperties(getByKeyMethodName, keyProperties);

                if( generatedFluentHelperClass == null ) {
                    generatedFluentHelperClass =
                        fluentGenerator
                            .generateEntityByKeyFluentHelperClass(
                                entityClassNamingContext,
                                entityMetadata.getNamespacePackage(),
                                entityMetadata.getGeneratedEntityClass(),
                                keyProperties,
                                specificEntitySelectableInterface);

                    generatedFluentHelperClassesPerEntityType
                        .put(entityClassName, READ_BY_KEY, generatedFluentHelperClass);
                }

                serviceClassAmplifier
                    .addGetByKeyMethod(entityMetadata, generatedFluentHelperClass, getByKeyMethodName, keyProperties);
                break;
            case CREATE:
                generatedFluentHelperClass = generatedFluentHelperClassesPerEntityType.get(entityClassName, CREATE);

                if( generatedFluentHelperClass == null ) {
                    generatedFluentHelperClass =
                        fluentGenerator
                            .generateCreateFluentHelper(
                                entityClassNamingContext,
                                entityMetadata.getNamespacePackage(),
                                entityMetadata.getGeneratedEntityClass());

                    generatedFluentHelperClassesPerEntityType.put(entityClassName, CREATE, generatedFluentHelperClass);
                }

                serviceClassAmplifier.addCreateMethod(entityMetadata, generatedFluentHelperClass);
                break;
            case UPDATE:
                generatedFluentHelperClass = generatedFluentHelperClassesPerEntityType.get(entityClassName, UPDATE);

                if( generatedFluentHelperClass == null ) {
                    generatedFluentHelperClass =
                        fluentGenerator
                            .generateUpdateFluentHelper(
                                entityClassNamingContext,
                                entityMetadata.getNamespacePackage(),
                                entityMetadata.getGeneratedEntityClass());

                    generatedFluentHelperClassesPerEntityType.put(entityClassName, UPDATE, generatedFluentHelperClass);
                }

                serviceClassAmplifier.addUpdateMethod(entityMetadata, generatedFluentHelperClass);
                break;
            case DELETE:
                generatedFluentHelperClass = generatedFluentHelperClassesPerEntityType.get(entityClassName, DELETE);

                if( generatedFluentHelperClass == null ) {
                    generatedFluentHelperClass =
                        fluentGenerator
                            .generateDeleteFluentHelper(
                                entityClassNamingContext,
                                entityMetadata.getNamespacePackage(),
                                entityMetadata.getGeneratedEntityClass());

                    generatedFluentHelperClassesPerEntityType.put(entityClassName, DELETE, generatedFluentHelperClass);
                }

                serviceClassAmplifier.addDeleteMethod(entityMetadata, generatedFluentHelperClass);
                break;
            default:
                throw new ODataGeneratorException("Found unknown ApiFunction: " + function);
        }
    }

    private List<EntityPropertyModel> processKeyProperties(
        final Service.EntityType typeMetadata,
        final Map<String, EntityPropertyModel> entityClassFields )
    {
        final List<EntityPropertyModel> keyProperties = new LinkedList<>();

        for( final String keyPropertyName : typeMetadata.getKeyPropertyNames() ) {
            final EntityPropertyModel propertyMapping = entityClassFields.get(keyPropertyName);
            if( propertyMapping != null ) {
                propertyMapping.setKeyField(true);
                keyProperties.add(propertyMapping);
            } else {
                logger.error("Key property " + keyPropertyName + " was not found in the set of processed properties.");
            }
        }
        return keyProperties;
    }

    private List<NavigationPropertyModel> processNavigationProperties(
        final Service.EntityType entityType,
        final NamingContext entityPropertyNamingContext,
        final NamingContext entityClassConstantNamingContext )
    {
        final List<NavigationPropertyModel> navigationPropertyMappings = new LinkedList<>();
        final NamingContext navPropertiesNamingContext = new NamingContext();
        final NamingContext builderMethodNamingContext = new NamingContext();

        for( final String navigationPropertyName : entityType.getNavigationPropertyNames() ) {
            final Service.Typed navigationProperty = entityType.getProperty(navigationPropertyName);

            final String javaMemberName =
                entityPropertyNamingContext
                    .ensureUniqueName(
                        codeNamingStrategy.generateJavaNavigationPropertyFieldName(navigationPropertyName));
            final String javaConstantName =
                entityClassConstantNamingContext
                    .ensureUniqueName(
                        codeNamingStrategy.generateJavaNavigationPropertyConstantName(navigationPropertyName));

            final String javaMethodBaseName =
                navPropertiesNamingContext
                    .ensureUniqueName(codeNamingStrategy.generateJavaMethodName(navigationPropertyName));
            final String javaBuilderMethodName =
                builderMethodNamingContext
                    .ensureUniqueName(codeNamingStrategy.generateJavaBuilderMethodName(navigationPropertyName));

            navigationPropertyMappings
                .add(
                    new NavigationPropertyModel(
                        navigationPropertyName,
                        navigationProperty.getType().getName(),
                        navigationProperty.getMultiplicity(),
                        javaMemberName,
                        javaConstantName,
                        NamingUtils.deriveJavaFetchMethodName(javaMethodBaseName),
                        NamingUtils.deriveJavaGetIfPresentMethodName(javaMethodBaseName),
                        NamingUtils.deriveJavaGetOrFetchMethodName(javaMethodBaseName),
                        NamingUtils.deriveJavaAddMethodName(javaMethodBaseName),
                        NamingUtils.deriveJavaSetMethodName(javaMethodBaseName),
                        javaBuilderMethodName));
        }
        return navigationPropertyMappings;
    }

    private void overrideSetServicePathForFetchMethod( final JDefinedClass entityClass )
    {
        final JMethod setterMethod = entityClass.method(JMod.PROTECTED, codeModel.VOID, "setServicePathForFetch");
        setterMethod.annotate(Override.class);

        final JVar newValueVar = setterMethod.param(JMod.FINAL, String.class, "servicePathForFetch");
        newValueVar.annotate(Nullable.class);

        setterMethod.body().invoke(JExpr._super(), "setServicePathForFetch").arg(newValueVar);
    }

    private void overrideGetDestinationForFetchMethod( final JDefinedClass entityClass )
    {
        final JMethod getterMethod = entityClass.method(JMod.PUBLIC, Destination.class, "getDestinationForFetch");
        getterMethod.annotate(Override.class);
        getterMethod.annotate(Nullable.class);

        getterMethod.body()._return(JExpr._super().invoke("getDestinationForFetch"));
    }

    private void overrideAttachToServiceMethod( final JDefinedClass entityClass )
    {
        final JMethod attachMethod = entityClass.method(JMod.PUBLIC, codeModel.VOID, "attachToService");
        attachMethod.annotate(Override.class);

        final JVar servicePathVar = attachMethod.param(JMod.FINAL, String.class, "servicePath");
        servicePathVar.annotate(Nullable.class);

        final JVar destinationVar = attachMethod.param(JMod.FINAL, Destination.class, "destination");
        destinationVar.annotate(Nonnull.class);

        attachMethod.body().invoke(JExpr._super(), "attachToService").arg(servicePathVar).arg(destinationVar);
    }

    Option<String> getQualifiedEntityClassName( @Nonnull final String entitySetName )
    {
        return entityBluePrintMap.containsKey(entitySetName)
            ? Option.some(entityBluePrintMap.get(entitySetName).getEntityClass().fullName())
            : Option.none();
    }

    @Data
    public static final class ClassGeneratorResult
    {
        @Nonnull
        final JDefinedClass generatedEntityClass;
        @Nullable
        final JDefinedClass specificEntitySelectableInterface;
        @Nullable
        final JDefinedClass entityOneToManyLink;
        @Nullable
        final JDefinedClass entityOneToOneLink;
    }
}
