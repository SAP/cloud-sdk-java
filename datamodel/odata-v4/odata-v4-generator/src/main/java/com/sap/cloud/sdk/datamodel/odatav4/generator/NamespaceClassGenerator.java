package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.cloudplatform.util.StringUtils;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.utility.LegacyClassScanner;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingStrategy;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingUtils;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmEnumDeserializer;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmEnumSerializer;
import com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty;
import com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmComplex;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntitySet;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;
import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.AnnotationDefinition;
import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.AnnotationStrategy;
import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.EntityAnnotationModel;
import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.EntityPropertyAnnotationModel;
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
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Data;
import lombok.Getter;

/**
 * For internal use only.
 */
class NamespaceClassGenerator
{
    private static final Logger logger = MessageCollector.getLogger(NamespaceClassGenerator.class);

    private final Map<String, JPackage> generatedNamespacePackages = new HashMap<>();
    private final Map<String, PreparedEntityBluePrint> entityBluePrintMap = new HashMap<>();
    private final JCodeModel codeModel;
    private final JPackage namespaceParentPackage;
    private final NamingStrategy codeNamingStrategy;
    private final AnnotationStrategy annotationStrategy;

    private final boolean generatePojosOnly;
    private final boolean serviceMethodsPerEntitySet;
    private final LegacyClassScanner classScanner;

    NamespaceClassGenerator(
        final JCodeModel codeModel,
        final JPackage namespaceParentPackage,
        final NamingStrategy codeNamingStrategy,
        final AnnotationStrategy annotationStrategy,
        final boolean generatePojosOnly,
        final boolean serviceMethodsPerEntitySet,
        final LegacyClassScanner classScanner )
    {
        this.codeModel = codeModel;
        this.namespaceParentPackage = namespaceParentPackage;
        this.codeNamingStrategy = codeNamingStrategy;
        this.annotationStrategy = annotationStrategy;
        this.generatePojosOnly = generatePojosOnly;
        this.serviceMethodsPerEntitySet = serviceMethodsPerEntitySet;
        this.classScanner = classScanner;
    }

    private
        ClassGeneratorResult
        generateEdmEntityClass( final JPackage namespacePackage, final VdmObjectModel entityModel )
            throws JClassAlreadyExistsException
    {
        // 1) generate class stub
        @Nonnull
        JDefinedClass generatedEntityClass = generateEntityClassStub(namespacePackage, entityModel.getJavaClassName());
        // 2) generate rest of the entityClass
        generatedEntityClass = completeEntityClassGeneration(generatedEntityClass, entityModel);

        return new ClassGeneratorResult(generatedEntityClass);
    }

    private JDefinedClass generateEntityClassStub( final JClassContainer namespacePackage, final String javaClassName )
        throws JClassAlreadyExistsException
    {
        final JDefinedClass entityClass = namespacePackage._class(JMod.PUBLIC, javaClassName);
        entityClass._extends(codeModel.ref(VdmEntity.class).narrow(entityClass));
        return entityClass;
    }

    private
        JDefinedClass
        completeEntityClassGeneration( final JDefinedClass entityClass, final VdmObjectModel entityModel )
    {
        //Generating the odataType
        //Todo populate the correct value by parsing and appending namespace(from root) and name(from entity) properties
        final JVar odataTypeField =
            entityClass
                .field(
                    JMod.PRIVATE | JMod.FINAL,
                    codeModel.ref(String.class),
                    "odataType",
                    JExpr.lit(entityModel.getEdmNameFullyQualified()));
        odataTypeField.annotate(Getter.class);

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
            entityClass
                .field(
                    JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
                    codeModel.ref(SimpleProperty.class).narrow(entityClass),
                    "ALL_FIELDS",
                    JExpr.invoke("all"))
                .javadoc()
                .add("Selector for all available fields of " + entityClass.name() + ".");
        }

        final EntityAnnotationModel entityAnnotationModel = new VdmObjectModelAnnotationWrapper(entityModel);
        AnnotationHelper
            .addAllAnnotationsToJavaItem(
                annotationStrategy.getAnnotationsForEntity(entityAnnotationModel),
                entityClass);

        for( final Map.Entry<String, EntityPropertyModel> entry : entityModel.getProperties().entrySet() ) {
            final EntityPropertyModel mapping = entry.getValue();
            addPropertyAsField(entityClass, mapping, annotationStrategy::getAnnotationsForEntityProperty);
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

            /*if( entityModel.isMediaStreamExposed() ) {
                createMethodFetchAsStream(entityClass);
            }*/
        }
        return entityClass;
    }

    private void createSimpleProperty(
        @Nonnull final JDefinedClass entityClass,
        @Nullable final JClass javaFieldClass,
        @Nonnull final String javaConstantName,
        @Nonnull final String edmName,
        final boolean isCollection,
        final boolean isEnum,
        @Nonnull final String edmType )
    {
        final int mods = JMod.PUBLIC | JMod.STATIC | JMod.FINAL;
        final Class<?> simplePropertyType;
        final JClass primitiveType;

        switch( edmType ) {
            case "String":
                simplePropertyType = SimpleProperty.String.class;
                primitiveType = codeModel.ref(String.class);
                break;
            case "Boolean":
                simplePropertyType = SimpleProperty.Boolean.class;
                primitiveType = codeModel.ref(Boolean.class);
                break;
            case "Decimal":
            case "Single":
            case "Double":
                simplePropertyType = SimpleProperty.NumericDecimal.class;
                primitiveType = codeModel.ref(Double.class);
                break;
            case "Int64":
            case "Int32":
            case "Int16":
            case "Byte":
                simplePropertyType = SimpleProperty.NumericInteger.class;
                primitiveType = codeModel.ref(Integer.class);
                break;
            case "Duration":
                simplePropertyType = SimpleProperty.Duration.class;
                primitiveType = codeModel.ref(Duration.class);
                break;
            case "DateTimeOffset":
                simplePropertyType = SimpleProperty.DateTime.class;
                primitiveType = codeModel.ref(OffsetDateTime.class);
                break;
            case "Date":
                simplePropertyType = SimpleProperty.Date.class;
                primitiveType = codeModel.ref(LocalDate.class);
                break;
            case "TimeOfDay":
                simplePropertyType = SimpleProperty.Time.class;
                primitiveType = codeModel.ref(LocalTime.class);
                break;
            case "Guid":
                simplePropertyType = SimpleProperty.Guid.class;
                primitiveType = codeModel.ref(UUID.class);
                break;
            case "Binary":
                simplePropertyType = SimpleProperty.Binary.class;
                primitiveType = codeModel.ref(byte[].class);
                break;
            case "Stream":
                logger.warn("    Unsupported type detected:\n      property name: {}, type: {}", edmName, edmType);
                return;
            default:
                if( isEnum ) {
                    simplePropertyType = SimpleProperty.Enum.class;
                    primitiveType = javaFieldClass;
                } else {
                    logger.error("    Unsupported type detected:\n      property name: {}, type: {}", edmName, edmType);
                    return;
                }
        }

        // singular property
        if( !isCollection ) {

            // non enum property
            if( !isEnum ) {
                final JClass t = codeModel.ref(simplePropertyType).narrow(entityClass);
                final JInvocation init = JExpr._new(t).arg(entityClass.dotclass()).arg(edmName);
                entityClass.field(mods, t, javaConstantName, init);
                return;
            }

            // enum property
            final JClass t = codeModel.ref(simplePropertyType).narrow(entityClass, primitiveType);
            final JInvocation init = JExpr._new(t).arg(entityClass.dotclass()).arg(edmName).arg(edmType);
            entityClass.field(mods, t, javaConstantName, init);
            return;
        }

        // collection property
        final JClass t = codeModel.ref(SimpleProperty.Collection.class).narrow(entityClass, primitiveType);
        final JInvocation init = JExpr._new(t).arg(entityClass.dotclass()).arg(edmName).arg(primitiveType.dotclass());
        entityClass.field(mods, t, javaConstantName, init);
    }

    private void addPropertyAsField(
        final JDefinedClass entityClass,
        final EntityPropertyModel mapping,
        final Function<EntityPropertyAnnotationModel, Set<AnnotationDefinition>> annotationSupplier )
    {
        JType javaFieldType = mapping.getJavaFieldClass();
        if( mapping.isCollection() ) {
            javaFieldType = codeModel.ref(Collection.class).narrow(javaFieldType);
        }

        final JFieldVar entityClassField = entityClass.field(JMod.PRIVATE, javaFieldType, mapping.getJavaFieldName());

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
                .add(
                    String
                        .format(
                            "The %s contained in this {@link %s}.",
                            entityClassField.name(),
                            entityClass._extends().erasure().name()));
        }

        final EntityPropertyAnnotationModel propertyAnnotationModel = new EntityPropertyModelAnnotationWrapper(mapping);
        AnnotationHelper
            .addAllAnnotationsToJavaItem(annotationSupplier.apply(propertyAnnotationModel), entityClassField);

        generateSetterMethod(entityClass, entityClassField, mapping.getEdmName(), mapping.getBasicDescription());

        if( !generatePojosOnly ) {
            if( mapping.isSimpleType() || mapping.isEnum() ) {
                createSimpleProperty(
                    entityClass,
                    mapping.getJavaFieldClass(),
                    mapping.getJavaConstantName(),
                    mapping.getEdmName(),
                    mapping.isCollection(),
                    mapping.isEnum(),
                    mapping.getEdmType());
            } else {
                createComplexProperty(
                    entityClass,
                    mapping.getJavaFieldClass(),
                    mapping.getJavaConstantName(),
                    mapping.getEdmName(),
                    mapping.isCollection());
            }
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
        final Map<String, JDefinedClass> generatedEntities,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Map<String, JDefinedClass> generatedEnumTypes,
        final NamingContext entityClassNamingContext )
        throws JClassAlreadyExistsException
    {
        for( final NavigationPropertyModel navigationProperty : entityBluePrint.getNavigationProperties() ) {
            final JDefinedClass associatedEntity =
                generatedEntities.get(navigationProperty.getReturnEntityType().getName());
            if( associatedEntity == null ) {
                final PreparedEntityBluePrint childEntityBluePrint =
                    processEntity(
                        entityBluePrint.getEntityClass().getPackage(),
                        generatedComplexTypes,
                        generatedEnumTypes,
                        navigationProperty.getReturnEntityType(),
                        navigationProperty.getEdmName(),
                        entityClassNamingContext);

                generatedEntities
                    .put(navigationProperty.getReturnEntityType().getName(), childEntityBluePrint.getEntityClass());

                addNavigationPropertyCode(
                    childEntityBluePrint,
                    generatedEntities,
                    generatedComplexTypes,
                    generatedEnumTypes,
                    entityClassNamingContext);
            }
        }

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
                    entityBluePrint.getKeyProperties(),
                    entityBluePrint.getNavigationProperties(),
                    generatedNavigationPropertyFields);
        }
    }

    private void createComplexProperty(
        @Nonnull final JDefinedClass entityClass,
        @Nonnull final JClass associatedEntity,
        @Nonnull final String complexPropertyConstantName,
        @Nonnull final String complexPropertyEdmName,
        final boolean isOneToMany )
    {
        final JClass navPropertyType =
            isOneToMany
                ? codeModel.ref(ComplexProperty.Collection.class).narrow(entityClass, associatedEntity)
                : codeModel.ref(ComplexProperty.Single.class).narrow(entityClass, associatedEntity);

        final JExpression initLink =
            JExpr
                ._new(navPropertyType)
                .arg(entityClass.dotclass())
                .arg(complexPropertyEdmName)
                .arg(associatedEntity.dotclass());
        final JFieldVar navPropertyConstantField =
            entityClass
                .field(JMod.FINAL | JMod.PUBLIC | JMod.STATIC, navPropertyType, complexPropertyConstantName, initLink);
        navPropertyConstantField
            .javadoc()
            .add(
                String
                    .format(
                        "Use with available request builders to apply the <b>%s</b> complex property to query operations.",
                        complexPropertyEdmName));
    }

    private
        JDefinedClass
        generateComplexTypeClass( final JPackage namespacePackage, final VdmObjectModel complexTypeModel )
            throws JClassAlreadyExistsException
    {
        final JDefinedClass complexTypeClass =
            namespacePackage._class(JMod.PUBLIC, complexTypeModel.getJavaClassName());
        complexTypeClass._extends(codeModel.ref(VdmComplex.class).narrow(complexTypeClass));

        //Generating the odataType
        final JVar odataTypeField =
            complexTypeClass
                .field(
                    JMod.PRIVATE | JMod.FINAL,
                    codeModel.ref(String.class),
                    "odataType",
                    JExpr.lit(complexTypeModel.getEdmNameFullyQualified()));
        odataTypeField.annotate(Getter.class);

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

            addPropertyAsField(complexTypeClass, mapping, annotationStrategy::getAnnotationsForComplexTypeProperty);
        }

        return complexTypeClass;
    }

    @Nonnull
    private JDefinedClass generateEnumTypeClass( final JPackage namespacePackage, final Service.EnumType enumTypeModel )
        throws JClassAlreadyExistsException
    {
        final JDefinedClass clazz = namespacePackage._enum(enumTypeModel.getName());
        clazz._implements(codeModel.ref(VdmEnum.class));

        clazz.annotate(codeModel.ref(JsonAdapter.class)).param("value", codeModel.ref(GsonVdmAdapterFactory.class));
        clazz
            .annotate(codeModel.ref(JsonSerialize.class))
            .param("using", codeModel.ref(JacksonVdmEnumSerializer.class));
        clazz
            .annotate(codeModel.ref(JsonDeserialize.class))
            .param("using", codeModel.ref(JacksonVdmEnumDeserializer.class));

        enumTypeModel.getMemberNames().forEach(memberName -> {
            final String classConstantName = codeNamingStrategy.generateJavaConstantName(memberName, null);
            final String value = enumTypeModel.getMemberValue(memberName);
            final JExpression enumValue = Try.of(() -> JExpr.lit(Long.parseLong(value))).getOrElse(JExpr::_null);
            clazz.enumConstant(classConstantName).arg(JExpr.lit(memberName)).arg(enumValue).javadoc().add(memberName);
        });

        final JFieldVar fieldName = clazz.field(JMod.FINAL | JMod.PRIVATE, String.class, "name");
        final JFieldVar fieldValue = clazz.field(JMod.FINAL | JMod.PRIVATE, Long.class, "value");

        final JMethod constructor = clazz.constructor(JMod.PRIVATE);
        final JVar paramName = constructor.param(JMod.FINAL, String.class, "enumName");
        final JVar paramValue = constructor.param(JMod.FINAL, Long.class, "enumValue");
        constructor.body().assign(fieldName, paramName).assign(fieldValue, paramValue);

        final JMethod getName = clazz.method(JMod.PUBLIC, String.class, "getName");
        getName.annotate(Override.class);
        getName.body()._return(fieldName);

        final JMethod getValue = clazz.method(JMod.PUBLIC, Long.class, "getValue");
        getValue.annotate(Override.class);
        getValue.body()._return(fieldValue);

        clazz
            .javadoc()
            .add(
                String.format("<p>Original enum type name from the Odata EDM: <b>%s</b></p>", enumTypeModel.getName()));

        return clazz;
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
        final JClass typeObject = codeModel.ref(Object.class);
        final JClass typeIterable = codeModel.ref(Iterable.class);
        final JClass typeEnum = codeModel.ref(VdmEnum.class);
        final JClass typeString = codeModel.ref(String.class);

        final String javaFieldName = entry.getValue().getJavaFieldName();
        final String javaMethodGet = "get" + StringUtils.capitalize(javaFieldName);
        final String javaMethodSet = "set" + StringUtils.capitalize(javaFieldName);
        final JType javaType = entry.getValue().getJavaFieldClass();
        final JType listType = codeModel.ref(LinkedList.class).narrow(javaType);

        final boolean isPrimitive = entry.getValue().isSimpleType();
        final boolean isCollection = entry.getValue().isCollection();
        final boolean isEnum = entry.getValue().isEnum();

        final JBlock block = isPrimitive || isEnum ? simplePropertiesBlock : complexPropertiesBlock;
        final JBlock ifFoundBody = block._if(values.invoke("containsKey").arg(entry.getKey()))._then();
        final JInvocation invokeRemove = values.invoke("remove").arg(entry.getKey());
        final JVar value = ifFoundBody.decl(JMod.FINAL, typeObject, "value", invokeRemove);
        final JExpression valueIsNull = value.eq(JExpr._null());
        final JInvocation objectsEquals = codeModel.ref(Objects.class).staticInvoke("equals");

        // enum property
        if( isEnum ) {
            final JInvocation enumDeserializer = typeEnum.staticInvoke("getConstant").arg(javaType.boxify().dotclass());
            if( isCollection ) {
                // collection of enum property values
                final JExpression fieldIsNotEmpty = JExpr.invoke(javaMethodGet).ne(JExpr._null());
                ifFoundBody._if(valueIsNull.cand(fieldIsNotEmpty))._then().invoke(javaMethodSet).arg(JExpr._null());
                final JBlock ifChangeBody = ifFoundBody._if(value._instanceof(typeIterable))._then();
                final JVar listInst = ifChangeBody.decl(JMod.FINAL, listType, javaFieldName, JExpr._new(listType));
                final JExpression iter = JExpr.cast(typeIterable.narrow(codeModel.wildcard()), value);
                final JForEach forEach = ifChangeBody.forEach(typeObject, "item", iter);
                final JBlock ifString = forEach.body()._if(forEach.var()._instanceof(typeString))._then();
                final JExpression enumLookup = enumDeserializer.arg(JExpr.cast(typeString, forEach.var()));
                final JVar enumConstant = ifString.decl(JMod.FINAL, javaType, "enumConstant", enumLookup);
                ifString.add(listInst.invoke("add").arg(enumConstant));
                final JExpression valueChange = objectsEquals.arg(listInst).arg(JExpr.invoke(javaMethodGet)).not();
                ifChangeBody._if(valueChange)._then().invoke(javaMethodSet).arg(listInst);
            } else {
                // singular enum property value
                final JBlock ifString = ifFoundBody._if(value._instanceof(typeString).cor(valueIsNull))._then();
                final JExpression enumLookup = enumDeserializer.arg(JExpr.cast(typeString, value));
                final JVar enumConstant = ifString.decl(JMod.FINAL, javaType, javaFieldName, enumLookup);
                final JExpression valueChange = objectsEquals.arg(enumConstant).arg(JExpr.invoke(javaMethodGet)).not();
                ifString._if(valueChange)._then().invoke(javaMethodSet).arg(enumConstant);
            }
        }
        // primitive property
        else if( isPrimitive ) {
            if( isCollection ) {
                // collection of primitive property values
                final JBlock ifChangeBody = ifFoundBody._if(value._instanceof(typeIterable))._then();
                final JVar listInst = ifChangeBody.decl(JMod.FINAL, listType, javaFieldName, JExpr._new(listType));
                final JExpression iter = JExpr.cast(typeIterable.narrow(codeModel.wildcard()), value);
                final JForEach forEach = ifChangeBody.forEach(typeObject, "item", iter);
                forEach.body().add(listInst.invoke("add").arg(JExpr.cast(javaType, forEach.var())));
                ifChangeBody.invoke(javaMethodSet).arg(listInst);
            } else {
                // singular primitive property value
                final JExpression valueChanged = value.invoke("equals").arg(JExpr.invoke(javaMethodGet)).not();
                final JBlock ifChangedBody = ifFoundBody._if(valueIsNull.cor(valueChanged))._then();
                ifChangedBody.invoke(javaMethodSet).arg(JExpr.cast(javaType, value));
            }
        }
        // complex property
        else {
            if( isCollection ) {
                // collection of complex property values
                final JBlock ifValueIsList = ifFoundBody._if(value._instanceof(typeIterable))._then();
                final JVar listInst = ifValueIsList.decl(JMod.FINAL, listType, javaFieldName, JExpr._new(listType));
                final JExpression iter = JExpr.cast(typeIterable.narrow(codeModel.wildcard()), value);
                final JForEach forEach = ifValueIsList.forEach(typeObject, "cloudSdkProperties", iter);
                final JBlock isMap = forEach.body()._if(forEach.var()._instanceof(codeModel.ref(Map.class)))._then();
                final JVar item = isMap.decl(JMod.FINAL, javaType, "item", JExpr._new(javaType));
                isMap.directStatement("@SuppressWarnings(\"unchecked\")");
                final JExpression castValueMap = JExpr.cast(fieldMapClass, value);
                final JVar varInputMap = isMap.decl(JMod.FINAL, fieldMapClass, "inputMap", castValueMap);
                isMap.add(item.invoke("fromMap").arg(varInputMap));
                isMap.add(listInst.invoke("add").arg(item));
                ifValueIsList.invoke(javaMethodSet).arg(listInst);
            } else {
                // singular complex property value
                final JBlock ifValueIsMap = ifFoundBody._if(value._instanceof(codeModel.ref(Map.class)))._then();
                final JBlock ifFieldIsEmpty = ifValueIsMap._if(JExpr.invoke(javaMethodGet).eq(JExpr._null()))._then();
                ifFieldIsEmpty.invoke(javaMethodSet).arg(JExpr._new(javaType));
                ifValueIsMap.directStatement("@SuppressWarnings(\"unchecked\")");
                final JExpression castValueMap = JExpr.cast(fieldMapClass, value);
                final JVar varInputMap = ifValueIsMap.decl(JMod.FINAL, fieldMapClass, "inputMap", castValueMap);
                ifValueIsMap.invoke(JExpr.invoke(javaMethodGet), "fromMap").arg(varInputMap);
            }

            final JExpression fieldIsNotEmpty = JExpr.invoke(javaMethodGet).ne(JExpr._null());
            ifFoundBody._if(valueIsNull.cand(fieldIsNotEmpty))._then().invoke(javaMethodSet).arg(JExpr._null());
        }
    }

    private
        void
        createMethodGetKey( final Map<String, EntityPropertyModel> entityClassFields, final JDefinedClass entityClass )
    {
        final JClass oDataEntityKeyClass = codeModel.ref(ODataEntityKey.class);
        final JMethod getKeyMethod = entityClass.method(JMod.PROTECTED, oDataEntityKeyClass, "getKey");
        getKeyMethod.annotate(Nonnull.class);
        getKeyMethod.annotate(Override.class);
        final JBlock body = getKeyMethod.body();
        final JVar v = body.decl(JMod.FINAL, oDataEntityKeyClass, "entityKey", JExpr._super().invoke("getKey"));
        for( final Map.Entry<String, EntityPropertyModel> entry : entityClassFields.entrySet() ) {
            if( entry.getValue().isKeyField() ) {
                final String javaFieldName = entry.getValue().getJavaFieldName();
                final String javaMethodName = "get" + StringUtils.capitalize(javaFieldName);
                body.invoke(v, "addKeyProperty").arg(entry.getKey()).arg(JExpr.invoke(javaMethodName));
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

    void processUnboundOperation(
        final ServiceClassGenerator serviceClassGenerator,
        final Service service,
        final Map<String, JDefinedClass> generatedEntities,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Map<String, JDefinedClass> generatedEnumTypes,
        final Service.ServiceOperation unboundOperation,
        final NamingContext entityClassNamingContext,
        final NamingContext unboundOperationFetchMethodNamingContext,
        final boolean isFunction )
        throws JClassAlreadyExistsException
    {
        final JPackage namespacePackage = getOrGenerateNamespacePackage(service.getJavaPackageName());
        final List<OperationParameterModel> parameters =
            processOperationParameters(
                namespacePackage,
                unboundOperation,
                false,
                generatedEntities,
                generatedComplexTypes,
                generatedEnumTypes);
        if( parameters == null ) {
            return;
        }

        final ServiceClassGenerator.ServiceClassAmplifier serviceClassAmplifier =
            serviceClassGenerator.getOrGenerateServiceClassesAndGetAmplifier(service);

        @Nullable
        final Service.Type edmReturnType =
            unboundOperation.getReturnType() != null ? unboundOperation.getReturnType().getType() : null;
        final boolean isCollectionReturnType;

        @Nullable
        final JClass javaReturnType =
            getOrGenerateClassForOperation(
                service,
                generatedEntities,
                generatedComplexTypes,
                generatedEnumTypes,
                edmReturnType,
                unboundOperation.getName(),
                null,
                entityClassNamingContext);
        if( javaReturnType == null ) {
            return;
        }
        if( edmReturnType != null ) {
            isCollectionReturnType = unboundOperation.getReturnType().getMultiplicity() == Multiplicity.MANY;
        }
        //Unbound actions which do not explicitly state the return type are assumed to return Void.
        else {
            isCollectionReturnType = false;
        }

        if( isFunction ) {
            final String functionImportName = unboundOperation.getName();
            final String functionImportLabel = unboundOperation.getAnnotations().getLabel();
            final String functionImportDescription = JavadocUtils.getCompleteDescription(unboundOperation);
            serviceClassAmplifier
                .addUnboundOperation(
                    functionImportName,
                    functionImportLabel,
                    functionImportDescription,
                    parameters,
                    unboundOperationFetchMethodNamingContext,
                    isCollectionReturnType,
                    javaReturnType,
                    true);
        } else {
            final String actionImportName = unboundOperation.getName();
            final String actionImportLabel = unboundOperation.getAnnotations().getLabel();
            final String actionImportDescription = JavadocUtils.getCompleteDescription(unboundOperation);
            serviceClassAmplifier
                .addUnboundOperation(
                    actionImportName,
                    actionImportLabel,
                    actionImportDescription,
                    parameters,
                    unboundOperationFetchMethodNamingContext,
                    isCollectionReturnType,
                    javaReturnType,
                    false);
        }
    }

    private List<OperationParameterModel> processOperationParameters(
        final JPackage namespacePackage,
        final Service.ServiceOperation operation,
        final boolean isBound,
        final Map<String, JDefinedClass> generatedEntities,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Map<String, JDefinedClass> generatedEnumTypes )
        throws JClassAlreadyExistsException
    {
        final List<OperationParameterModel> unboundOperationParameters = new LinkedList<>();
        final NamingContext unboundOperationParameterNamingContext = new NamingContext();

        /*
        Bound operations will have their binding parameter included under "getParameterNames()".
        We have to skip it explicitly here.

         Per OData spec the first parameter is always the binding parameter.
        Unfortunately there is no other way to exclude it, because we can't get the binding parameter name from Olingo.
        So we have to go based on the ordering.
         */
        final List<String> parameterList = operation.getParameterNames().stream().skip(isBound ? 1 : 0).toList();

        for( final String parameterName : parameterList ) {
            final Service.Parameter parameter = operation.getParameter(parameterName);
            final Service.Type edmParameterType = parameter.getType();
            JType javaType = null;

            switch( edmParameterType.getKind() ) {
                case PRIMITIVE:
                    javaType = codeModel.ref(((Service.PrimitiveType) edmParameterType).getDefaultJavaType());
                    break;
                case ENTITY:
                    javaType = generatedEntities.get(edmParameterType.getName());
                    if( javaType == null ) {
                        logger
                            .warn(
                                "Skipping operation {} generation as it contains entity parameter {} unassociated with an entity set.",
                                operation.getName(),
                                parameterName);
                        return null;
                    }
                    break;
                case COMPLEX:
                    javaType = generatedComplexTypes.get(edmParameterType.getName());
                    if( javaType == null ) {
                        javaType =
                            processComplexType(
                                namespacePackage,
                                generatedComplexTypes,
                                generatedEnumTypes,
                                (Service.ComplexType) edmParameterType);
                        generatedComplexTypes.put(edmParameterType.getName(), (JDefinedClass) javaType);
                    }
                    break;
                case ENUM:
                    javaType = generatedEnumTypes.get(edmParameterType.getName());
                    if( javaType == null ) {
                        javaType = processEnumType(namespacePackage, (Service.EnumType) edmParameterType);
                        generatedEnumTypes.put(edmParameterType.getName(), (JDefinedClass) javaType);
                    }
                    break;
            }

            if( parameter.getMultiplicity() == Multiplicity.MANY ) {
                javaType = codeModel.ref(Collection.class).narrow(javaType);
            }

            final String parameterLabel = parameter.getAnnotations().getLabel();
            final String javaName =
                unboundOperationParameterNamingContext
                    .ensureUniqueName(
                        codeNamingStrategy.generateJavaMethodParameterName(parameterName, parameterLabel));
            unboundOperationParameters
                .add(
                    new OperationParameterModel(
                        parameterName,
                        edmParameterType.getName(),
                        javaName,
                        javaType,
                        JavadocUtils.getDescriptionAndConstraints(parameterName, parameter),
                        parameter.getFacets().isNullable()));
        }
        return unboundOperationParameters;
    }

    @Nonnull
    private JDefinedClass processComplexType(
        final JPackage namespacePackage,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Map<String, JDefinedClass> generatedEnumTypes,
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
                generatedEnumTypes,
                complexTypeMetadata,
                complexTypePropertyNamingContext,
                new NamingContext());

        final VdmObjectModel complexTypeModel =
            new VdmObjectModel(
                complexTypeMetadata.getName(),
                complexTypeMetadata.getFullyQualifiedName(),
                complexTypeMetadata.getAnnotations().getLabel(),
                null,
                complexTypeMetadata.getName(),
                entityClassFields,
                complexTypeDescription,
                false);

        return generateComplexTypeClass(namespacePackage, complexTypeModel);
    }

    @Nonnull
    private JDefinedClass processEnumType( final JPackage namespacePackage, final Service.EnumType enumTypeMetadata )
        throws JClassAlreadyExistsException
    {
        logger.info("  Found enum type " + enumTypeMetadata.getName());
        return generateEnumTypeClass(namespacePackage, enumTypeMetadata);
    }

    private Map<String, EntityPropertyModel> processProperties(
        final JPackage namespacePackage,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Map<String, JDefinedClass> generatedEnumTypes,
        final Service.StructuralType typeMetadata,
        final NamingContext entityNamingContext,
        final NamingContext entityClassConstantNamingContext )
        throws JClassAlreadyExistsException
    {
        final Map<String, EntityPropertyModel> entityClassFields = new LinkedHashMap<>();

        for( final String propertyName : typeMetadata.getPropertyNames() ) {
            //check unsupported property type
            final Service.Property propertyMetadata = typeMetadata.getProperty(propertyName);
            final Service.Type propertyType = propertyMetadata.getType();
            if( propertyType instanceof Service.PrimitiveType
                && !((Service.PrimitiveType) propertyType).isSupportedEdmType() ) {
                logger.warn("Encountered unsupported property type {}", propertyType.getName());
                continue;
            }
            final EntityPropertyModel entityPropertyModel =
                processProperty(
                    namespacePackage,
                    generatedComplexTypes,
                    generatedEnumTypes,
                    propertyMetadata,
                    propertyType,
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
        final Map<String, JDefinedClass> generatedEnumTypes,
        final Service.Property propertyMetadata,
        final Service.Type propertyType,
        final String propertyName,
        final NamingContext entityNamingContext,
        final NamingContext entityClassConstantNamingContext )
        throws JClassAlreadyExistsException
    {
        final TypeKind propertyKind = propertyType.getKind();
        // Not the nicest way, but I haven't found any cleaner way to get an EdmAnnotatable object from Olingo
        String propertyBasicDescription = null;
        String propertyDetailedDescription = null;
        String propertyConstraintsDescription = null;
        String propertyLabel = null;
        Integer precision = null;
        Integer scale = null;

        propertyBasicDescription = JavadocUtils.getBasicDescription(propertyMetadata);
        propertyDetailedDescription = JavadocUtils.getDetailedDescription(propertyMetadata);
        propertyConstraintsDescription = JavadocUtils.getConstraints(propertyMetadata);
        propertyLabel = propertyMetadata.getAnnotations().getLabel();
        if( Objects.nonNull(propertyMetadata.getFacets()) ) {
            precision = propertyMetadata.getFacets().getPrecision();
            scale = propertyMetadata.getFacets().getScale();
        }

        final String classFieldName =
            entityNamingContext.ensureUniqueName(codeNamingStrategy.generateJavaFieldName(propertyName, propertyLabel));
        final String classConstantName =
            entityClassConstantNamingContext
                .ensureUniqueName(codeNamingStrategy.generateJavaConstantName(classFieldName, null));

        final boolean isCollection = propertyMetadata.getMultiplicity() == Multiplicity.MANY;

        switch( propertyKind ) {
            case PRIMITIVE:
                final Service.PrimitiveType primitiveType = (Service.PrimitiveType) propertyType;
                final JClass javaFieldType = codeModel.ref(primitiveType.getDefaultJavaType());

                return new EntityPropertyModel(
                    propertyName,
                    propertyLabel,
                    primitiveType.getName(),
                    classFieldName,
                    javaFieldType,
                    classConstantName,
                    true, // is simple type
                    isCollection,
                    false, // is NOT enum
                    false, // default: is NOT key field
                    propertyBasicDescription,
                    propertyDetailedDescription,
                    propertyConstraintsDescription,
                    precision,
                    scale);

            case COMPLEX:
                final Service.ComplexType complexType = (Service.ComplexType) propertyType;
                final String complexTypeName = complexType.getName();

                JDefinedClass generatedComplexType = generatedComplexTypes.get(complexTypeName);
                if( generatedComplexType == null ) {
                    generatedComplexType =
                        processComplexType(namespacePackage, generatedComplexTypes, generatedEnumTypes, complexType);
                    generatedComplexTypes.put(complexTypeName, generatedComplexType);
                }

                return new EntityPropertyModel(
                    propertyName,
                    propertyLabel,
                    complexType.getName(),
                    classFieldName,
                    generatedComplexType,
                    classConstantName,
                    false, // is NOT simple type
                    isCollection,
                    false, // is NOT enum
                    false, // default: is NOT key field
                    propertyBasicDescription,
                    propertyDetailedDescription,
                    propertyConstraintsDescription,
                    precision,
                    scale);

            case ENUM:
                final Service.EnumType enumType = (Service.EnumType) propertyType;
                final String enumTypeName = enumType.getName();

                JDefinedClass generatedEnumType = generatedEnumTypes.get(enumTypeName);
                if( generatedEnumType == null ) {
                    generatedEnumType = processEnumType(namespacePackage, enumType);
                    generatedEnumTypes.put(enumTypeName, generatedEnumType);
                }
                return new EntityPropertyModel(
                    propertyName,
                    propertyLabel,
                    enumType.getFullyQualifiedName(),
                    classFieldName,
                    generatedEnumType,
                    classConstantName,
                    false, // is NOT simple type
                    isCollection,
                    true, // is enum
                    false, // default: is NOT key field
                    propertyBasicDescription,
                    propertyDetailedDescription,
                    propertyConstraintsDescription,
                    precision,
                    scale);
            default:
                logger.error(String.format("""
                        Unsupported type detected:
                          property name: %s, type: %s\
                    """, propertyName, propertyType.getKind()));
                return null;
        }
    }

    @Nonnull
    PreparedEntityBluePrint processEntity(
        final JPackage namespacePackage,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Map<String, JDefinedClass> generatedEnumTypes,
        final Service.EntityType entityType,
        @Nullable final String entitySetName,
        final NamingContext entityClassNamingContext )
        throws JClassAlreadyExistsException
    {
        final String entityTypeName = entityType.getName();
        final String entityTypeLabel = entityType.getAnnotations().getLabel();
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
                generatedEnumTypes,
                entityType,
                entityPropertyNamingContext,
                entityClassConstantNamingContext);

        final VdmObjectModel entityModel =
            new VdmObjectModel(
                entityTypeName,
                entityType.getFullyQualifiedName(),
                entityTypeLabel,
                entitySetName,
                javaClassName,
                entityClassFields,
                entityDescription,
                entityType.hasMediaStream());

        final List<EntityPropertyModel> keyProperties = processKeyProperties(entityType, entityClassFields);

        final List<NavigationPropertyModel> navigationProperties =
            processNavigationProperties(entityType, entityPropertyNamingContext, entityClassConstantNamingContext);

        final NamespaceClassGenerator.ClassGeneratorResult result =
            generateEdmEntityClass(namespacePackage, entityModel);

        return new PreparedEntityBluePrint(result.getGeneratedEntityClass(), navigationProperties, keyProperties);
    }

    Option<PreparedEntityBluePrint> processEntitySet(
        final ServiceClassGenerator serviceClassGenerator,
        final Service service,
        final Map<String, JDefinedClass> generatedEntities,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Map<String, JDefinedClass> generatedEnumTypes,
        final Service.EntitySet entitySet,
        final NamingContext entityClassNamingContext )
        throws JClassAlreadyExistsException
    {
        final Service.EntityType entityType = entitySet.getEntityType();
        final String entityTypeName = entityType.getName();
        final JPackage namespacePackage = getOrGenerateNamespacePackage(service.getJavaPackageName());

        logger.info("  Found entity type " + entityTypeName + " from set " + entitySet.getName());

        final JDefinedClass entityClass;
        final Option<PreparedEntityBluePrint> maybeGeneratedBlueprint;

        if( !generatedEntities.containsKey(entityTypeName) ) {
            final PreparedEntityBluePrint entityBlueprint =
                processEntity(
                    namespacePackage,
                    generatedComplexTypes,
                    generatedEnumTypes,
                    entityType,
                    entitySet.getName(),
                    entityClassNamingContext);

            entityClass = entityBlueprint.getEntityClass();
            generatedEntities.put(entityTypeName, entityClass);
            entityBluePrintMap.put(entityTypeName, entityBlueprint);
            maybeGeneratedBlueprint = Option.some(entityBlueprint);
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

        if( allowedFunctions != null && !allowedFunctions.isEmpty() ) {
            final List<EntityPropertyModel> entityKeyProperties =
                maybeGeneratedBlueprint
                    .map(PreparedEntityBluePrint::getKeyProperties)
                    .getOrElse(
                        () -> Option
                            .of(entityBluePrintMap.get(entityType.getName()))
                            .map(PreparedEntityBluePrint::getKeyProperties)
                            .getOrElse(Collections::emptyList));

            addAllowedFunctions(
                serviceClassAmplifier,
                new EntityMetadata(namespacePackage, entityClass, entitySet.getName()),
                allowedFunctions,
                entityKeyProperties);
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
        final ServiceClassGenerator.ServiceClassAmplifier serviceClassAmplifier,
        final EntityMetadata entityMetadata,
        final Collection<ApiFunction> allowedFunctions,
        final Iterable<EntityPropertyModel> keyProperties )
    {
        final Iterable<ApiFunction> functionsToAdd;
        if( allowedFunctions.isEmpty() ) {
            functionsToAdd = Collections.emptyList();
        } else {
            // Using an enum set to leverage the natural order of the enum; necessary to get a deterministic behavior
            functionsToAdd = EnumSet.copyOf(allowedFunctions);
        }
        for( final ApiFunction function : functionsToAdd ) {
            addFunction(serviceClassAmplifier, entityMetadata, function, keyProperties);
        }
    }

    private void addFunction(
        final ServiceClassGenerator.ServiceClassAmplifier serviceClassAmplifier,
        final EntityMetadata entityMetadata,
        final ApiFunction function,
        Iterable<EntityPropertyModel> keyProperties )
    {
        switch( function ) {
            case READ:
                // Get All
                serviceClassAmplifier.addGetAllMethod(entityMetadata);
                // Count
                serviceClassAmplifier.addCountMethod(entityMetadata);
                break;
            case READ_BY_KEY:
                final String getByKeyMethodName = serviceClassAmplifier.getByKeyMethodName(entityMetadata);
                keyProperties = serviceClassAmplifier.getRefinedKeyProperties(getByKeyMethodName, keyProperties);
                serviceClassAmplifier.addGetByKeyMethod(entityMetadata, getByKeyMethodName, keyProperties);
                break;
            case CREATE:
                serviceClassAmplifier.addCreateMethod(entityMetadata);
                break;
            case UPDATE:
                serviceClassAmplifier.addUpdateMethod(entityMetadata);
                break;
            case DELETE:
                serviceClassAmplifier.addDeleteMethod(entityMetadata);
                break;
            case NAVIGATE:
                entityMetadata.getGeneratedEntityClass()._implements(VdmEntitySet.class);
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
            final Service.NavigationProperty navigationProperty =
                entityType.getNavigationProperty(navigationPropertyName);

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
                        (Service.EntityType) navigationProperty.getType(),
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

    Option<String> getQualifiedEntityClassName( @Nonnull final String entitySetName )
    {
        return entityBluePrintMap.containsKey(entitySetName)
            ? Option.some(entityBluePrintMap.get(entitySetName).getEntityClass().fullName())
            : Option.none();
    }

    void processBoundOperation(
        final Service service,
        final Map<String, JDefinedClass> generatedEntities,
        final Map<String, JDefinedClass> generatedComplexTypes,
        final Map<String, JDefinedClass> generatedEnumTypes,
        final Service.ServiceBoundOperation serviceBoundOperation,
        final NamingContext entityClassNamingContext )
        throws JClassAlreadyExistsException
    {
        final EdmOperation operation = serviceBoundOperation.getOperation();
        final boolean isFunction = serviceBoundOperation.isFunction();
        final boolean hasReturnType = serviceBoundOperation.getReturnType() != null;
        @Nullable
        final Service.Type returnType;
        if( hasReturnType ) {
            returnType = serviceBoundOperation.getReturnType().getType();
        } else {
            returnType = null;
        }
        final boolean isReturnTypeCollection = hasReturnType && operation.getReturnType().isCollection();

        final boolean isBoundToCollection = operation.isBindingParameterTypeCollection();
        final boolean isComposable = isFunction && ((EdmFunction) operation).isComposable();

        final JPackage namespacePackage = getOrGenerateNamespacePackage(service.getJavaPackageName());
        final List<OperationParameterModel> parameterModels =
            processOperationParameters(
                namespacePackage,
                serviceBoundOperation,
                true,
                generatedEntities,
                generatedComplexTypes,
                generatedEnumTypes);
        if( parameterModels == null ) {
            return;
        }

        final String methodName =
            codeNamingStrategy
                .generateJavaOperationMethodName(
                    serviceBoundOperation.getName(),
                    serviceBoundOperation.getAnnotations().getLabel());

        /* Derive Binding Type
           We must assume the binding type is an entity, because the Olingo API has no means to get the type of the binding parameter.
           Also we can't easily generate an entity type at this point because, again, we don't know the entity type and would have to look it up somehow.
         */
        final JDefinedClass javaBindingType = generatedEntities.get(operation.getBindingParameterTypeFqn().getName());
        if( javaBindingType == null ) {
            logger
                .warn(
                    "Operation {} bound to type {} will be skipped: Type is not an entity or entity is not part of an entity set.",
                    operation.getName(),
                    operation.getBindingParameterTypeFqn());
            return;
        }

        final List<List<OperationParameterModel>> boundOperationFactoryMethods =
            classScanner
                .determineArgumentsForMethod(
                    javaBindingType.fullName(),
                    methodName,
                    parameterModels,
                    OperationParameterModel::getJavaName);

        // Derive return type - generate if missing
        final JClass javaReturnType =
            getOrGenerateClassForOperation(
                service,
                generatedEntities,
                generatedComplexTypes,
                generatedEnumTypes,
                returnType,
                operation.getName(),
                operation.getEntitySetPath(),
                entityClassNamingContext);
        if( javaReturnType == null ) {
            return;
        }

        final Class<?> operationClass =
            isFunction
                ? BoundOperationGenerator
                    .getFunctionClass(
                        isBoundToCollection,
                        isReturnTypeCollection,
                        Objects.requireNonNull(returnType).getKind(),
                        isComposable)
                : BoundOperationGenerator.getActionClass(isBoundToCollection, isReturnTypeCollection);

        final JClass operationClassWithGenerics = codeModel.ref(operationClass).narrow(javaBindingType, javaReturnType);

        for( final List<OperationParameterModel> paramList : boundOperationFactoryMethods ) {
            final JMethod method =
                javaBindingType.method(JMod.PUBLIC | JMod.STATIC, operationClassWithGenerics, methodName);
            method.annotate(Nonnull.class);

            method.javadoc().add(JavadocUtils.getCompleteDescription(serviceBoundOperation));

            method
                .javadoc()
                .add(BoundOperationGenerator.getJavadocDescriptionForOperation(isFunction, isBoundToCollection));
            method
                .javadoc()
                .addReturn()
                .add(BoundOperationGenerator.getJavadocReturnForOperation(isFunction, isBoundToCollection));
            final Map<String, JVar> generatedParameters = new HashMap<>();
            for( final OperationParameterModel param : paramList ) {
                final JVar parameter = method.param(JMod.FINAL, param.getJavaType(), param.getJavaName());
                parameter.annotate(param.isNullable() ? Nullable.class : Nonnull.class);
                final JCommentPart parameterJavadoc = method.javadoc().addParam(parameter);
                parameterJavadoc.add(param.getDescription());
                generatedParameters.put(param.getEdmName(), parameter);
            }

            final JVar parameterMap;
            final JClass keyType = codeModel.ref(String.class);
            final JClass valueType = codeModel.ref(Object.class);

            if( generatedParameters.isEmpty() ) {
                parameterMap =
                    method
                        .body()
                        .decl(
                            JMod.FINAL,
                            codeModel.ref(Map.class).narrow(keyType, valueType),
                            "parameters",
                            codeModel.ref(Collections.class).staticInvoke("emptyMap"));
            } else {
                parameterMap =
                    method
                        .body()
                        .decl(
                            JMod.FINAL,
                            codeModel.ref(Map.class).narrow(keyType, valueType),
                            "parameters",
                            JExpr._new(codeModel.ref(HashMap.class).narrow(keyType, valueType)));
            }
            for( final OperationParameterModel param : paramList ) {
                method
                    .body()
                    .add(
                        parameterMap
                            .invoke("put")
                            .arg(param.getEdmName())
                            .arg(generatedParameters.get(param.getEdmName())));
            }

            final JInvocation returnStatement =
                JExpr
                    ._new(operationClassWithGenerics)
                    .arg(javaBindingType.dotclass())
                    .arg(javaReturnType.dotclass())
                    .arg(operation.getFullQualifiedName().getFullQualifiedNameAsString())
                    .arg(parameterMap);

            method.body()._return(returnStatement);
        }
    }

    private JClass getOrGenerateClassForOperation(
        @Nonnull final Service service,
        @Nonnull final Map<String, JDefinedClass> generatedEntities,
        @Nonnull final Map<String, JDefinedClass> generatedComplexTypes,
        @Nonnull final Map<String, JDefinedClass> generatedEnumTypes,
        @Nullable final Service.Type edmType,
        @Nonnull final String operationName,
        @Nullable final String entitySetPath,
        @Nonnull final NamingContext entityClassNamingContext )
    {

        if( edmType == null ) {
            return codeModel.ref(Void.class);
        }

        JClass javaReturnType;
        switch( edmType.getKind() ) {
            case PRIMITIVE: {
                final Service.PrimitiveType edmSimpleReturnType = (Service.PrimitiveType) edmType;
                javaReturnType = codeModel.ref(edmSimpleReturnType.getDefaultJavaType());
                break;
            }
            case COMPLEX: {
                javaReturnType = generatedComplexTypes.get(edmType.getName());
                if( javaReturnType == null ) {
                    final JPackage namespacePackage = getOrGenerateNamespacePackage(service.getJavaPackageName());
                    final Service.ComplexType edmComplexReturnType = (Service.ComplexType) edmType;

                    final JDefinedClass generatedComplexType;
                    try {
                        generatedComplexType =
                            processComplexType(
                                namespacePackage,
                                generatedComplexTypes,
                                generatedEnumTypes,
                                edmComplexReturnType);
                    }
                    catch( final JClassAlreadyExistsException e ) {
                        throw new IllegalStateException(
                            "Complex type "
                                + edmType.getName()
                                + " not found among generated complex types, but class already exists.",
                            e);
                    }

                    generatedComplexTypes.put(edmType.getName(), generatedComplexType);
                    javaReturnType = generatedComplexType;
                }
                break;
            }
            case ENTITY: {
                javaReturnType = generatedEntities.get(edmType.getName());
                if( javaReturnType == null ) {
                    final JPackage namespacePackage = getOrGenerateNamespacePackage(service.getJavaPackageName());
                    final Service.EntityType edmEntityReturnType = (Service.EntityType) edmType;

                    final PreparedEntityBluePrint childEntityBluePrint;
                    try {
                        childEntityBluePrint =
                            processEntity(
                                namespacePackage,
                                generatedComplexTypes,
                                generatedEnumTypes,
                                edmEntityReturnType,
                                entitySetPath != null ? entitySetPath : operationName,
                                entityClassNamingContext);

                        generatedEntities.put(edmType.getName(), childEntityBluePrint.getEntityClass());

                        addNavigationPropertyCode(
                            childEntityBluePrint,
                            generatedEntities,
                            generatedComplexTypes,
                            generatedEnumTypes,
                            entityClassNamingContext);
                    }
                    catch( final JClassAlreadyExistsException e ) {
                        throw new IllegalStateException(
                            "Entity type "
                                + edmType.getName()
                                + " not found among generated entities, but class already exists.",
                            e);
                    }

                    javaReturnType = childEntityBluePrint.getEntityClass();
                }
                break;
            }
            case ENUM:
                // TODO
            default: {
                logger
                    .error(
                        "Unsupported EDM return type {} found for operation {}. Skipping the operation.",
                        edmType.getKind(),
                        operationName);
                return null;
            }
        }
        return javaReturnType;
    }

    @Data
    public static final class ClassGeneratorResult
    {
        @Nonnull
        final JDefinedClass generatedEntityClass;
    }
}
