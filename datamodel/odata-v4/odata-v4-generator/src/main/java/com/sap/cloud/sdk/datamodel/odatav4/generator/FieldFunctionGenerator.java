/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.util.Arrays;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.TypeConverter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocCommentable;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

class FieldFunctionGenerator
{
    private final JCodeModel codeModel;

    FieldFunctionGenerator( final JCodeModel codeModel )
    {
        this.codeModel = codeModel;
    }

    void addFieldFunction( final JDefinedClass entityClass, final JDefinedClass specificEntityFieldClass )
    {
        final String[] genericStrings = { "T" };
        final JClass[] generics = generics(genericStrings);

        final JType returnType = specificEntityFieldClass.narrow(generics[0]);

        final JMethod fieldFunc = fieldFunction(entityClass, genericStrings, returnType);
        final JVar fieldNameParameter = fieldNameParam(fieldFunc);
        final JVar fieldTypeParameter = fieldTypeParam(generics, fieldFunc);

        addJavadoc(fieldFunc, fieldNameParameter, fieldTypeParameter, genericStrings[0]);

        addReturn(returnType, fieldFunc, fieldNameParameter);
    }

    void addFieldFunctionWithTypeConverter(
        final JDefinedClass entityClass,
        final JDefinedClass specificEntityFieldClass )
    {
        final String[] genericStrings = { "T", "DomainT" };
        final JClass[] generics = generics(genericStrings);

        final JType extFieldMethodReturnType = specificEntityFieldClass.narrow(generics[0]);

        final JMethod fieldFunc = fieldFunction(entityClass, genericStrings, extFieldMethodReturnType);
        final JVar fieldNameParam = fieldNameParam(fieldFunc);
        final JVar typeConverterParam = typeConverterParam(generics, fieldFunc);

        addJavadocWithTypeConverter(
            fieldFunc,
            fieldNameParam,
            typeConverterParam,
            genericStrings[0],
            genericStrings[1]);
        addReturnWithTypeConverter(extFieldMethodReturnType, fieldFunc, fieldNameParam, typeConverterParam);
    }

    private JClass[] generics( final String... generics )
    {
        return Arrays.stream(generics).map(codeModel::directClass).toArray(JClass[]::new);
    }

    private JMethod fieldFunction(
        final JDefinedClass entityClass,
        final String[] genericStrings,
        final JType extFieldMethodReturnType )
    {
        final JMethod fieldFuncWithTypeConverter =
            entityClass.method(JMod.PUBLIC | JMod.STATIC, extFieldMethodReturnType, "field");

        fieldFuncWithTypeConverter.generify(String.join(",", genericStrings));
        fieldFuncWithTypeConverter.annotate(Nonnull.class);
        return fieldFuncWithTypeConverter;
    }

    private JVar fieldNameParam( final JMethod fieldFuncWithTypeConverter )
    {
        final JVar fieldNameParam = fieldFuncWithTypeConverter.param(JMod.FINAL, String.class, "fieldName");
        fieldNameParam.annotate(Nonnull.class);
        return fieldNameParam;
    }

    private JVar typeConverterParam( final JClass[] generics, final JMethod fieldFuncWithTypeConverter )
    {
        final JVar typeConverterParam =
            fieldFuncWithTypeConverter
                .param(JMod.FINAL, codeModel.ref(TypeConverter.class).narrow(generics), "typeConverter");
        typeConverterParam.annotate(Nonnull.class);
        return typeConverterParam;
    }

    private JVar fieldTypeParam( final JClass[] generics, final JMethod fieldFunc )
    {
        final JVar fieldTypeParameter =
            fieldFunc.param(JMod.FINAL, codeModel.ref(Class.class).narrow(generics), "fieldType");
        fieldTypeParameter.annotate(Nonnull.class);
        return fieldTypeParameter;
    }

    private void addReturn( final JType returnType, final JMethod fieldFunc, final JExpression fieldNameParameter )
    {
        fieldFunc.body()._return(JExpr._new(returnType).arg(fieldNameParameter));
    }

    private void addReturnWithTypeConverter(
        final JType extFieldMethodReturnType,
        final JMethod fieldFuncWithTypeConverter,
        final JExpression fieldNameParam,
        final JExpression typeConverterParam )
    {
        fieldFuncWithTypeConverter
            .body()
            ._return(JExpr._new(extFieldMethodReturnType).arg(fieldNameParam).arg(typeConverterParam));
    }

    private void addJavadoc(
        final JDocCommentable fieldFunc,
        final JVar fieldNameParameter,
        final JVar fieldTypeParameter,
        final String genericTypeName )
    {
        fieldFunc.javadoc().add("Use with available request builders to apply an extension field to query operations.");
        fieldFunc.javadoc().addReturn().add("A representation of an extension field from this entity.");

        fieldFunc
            .javadoc()
            .addParam(fieldNameParameter)
            .add("The name of the extension field as returned by the OData service.");

        fieldFunc
            .javadoc()
            .addParam(fieldTypeParameter)
            .add("The Java type to use for the extension field when performing value comparisons.");
        fieldFunc
            .javadoc()
            .addParam(generify(genericTypeName))
            .add("The type of the extension field when performing value comparisons.");
    }

    private void addJavadocWithTypeConverter(
        final JDocCommentable fieldFuncWithTypeConverter,
        final JVar fieldNameParam,
        final JVar typeConverterParam,
        final String genericTypeName,
        final String genericTypeConverterName )
    {
        fieldFuncWithTypeConverter
            .javadoc()
            .add("Use with available request builders to apply an extension field to query operations.");
        fieldFuncWithTypeConverter
            .javadoc()
            .addReturn()
            .add(
                "A representation of an extension field from this entity, holding a reference to the given TypeConverter.");

        fieldFuncWithTypeConverter
            .javadoc()
            .addParam(fieldNameParam)
            .add("The name of the extension field as returned by the OData service.");

        fieldFuncWithTypeConverter
            .javadoc()
            .addParam(typeConverterParam)
            .add("A TypeConverter<T, DomainT> instance whose first generic type matches the Java type of the field");

        fieldFuncWithTypeConverter
            .javadoc()
            .addParam(generify(genericTypeName))
            .add("The type of the extension field when performing value comparisons.");
        fieldFuncWithTypeConverter
            .javadoc()
            .addParam(generify(genericTypeConverterName))
            .add("The type of the extension field as returned by the OData service.");
    }

    private String generify( final String genericName )
    {
        return "<" + genericName + ">";
    }
}
