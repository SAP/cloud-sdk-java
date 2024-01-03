/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import com.sap.cloud.sdk.datamodel.odata.helper.EntityField;
import com.sap.cloud.sdk.typeconverter.TypeConverter;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EntityFieldGenerator
{
    private static final String NAMESPACE_SUB_PACKAGE_NAME = "field";

    private final JCodeModel codeModel;

    JDefinedClass generateSpecificEntityFieldClass(
        final JPackage namespacePackage,
        final JDefinedClass entityClass,
        final JDefinedClass selectableInterface )
        throws JClassAlreadyExistsException
    {
        final String entityClassName = entityClass.name();
        final JPackage fieldSubPackage = namespacePackage.subPackage(NAMESPACE_SUB_PACKAGE_NAME);

        final JDefinedClass specificEntityField =
            fieldSubPackage._class(JMod.PUBLIC, entityClassName + CommonConstants.CLASS_NAME_FIELD_SUFFIX);
        final JTypeVar fieldT = specificEntityField.generify("FieldT");
        specificEntityField._extends(codeModel.ref(EntityField.class).narrow(entityClass, fieldT));
        specificEntityField._implements(selectableInterface);

        specificEntityField
            .javadoc()
            .add(
                String
                    .format(
                        "Template class to represent entity fields of the Entity {@link %s %s}. Instances of this object are used in query modifier methods of the entity\n"
                            + "fluent helpers. Contains methods to compare a field's value with a provided value.\n\n"
                            + "Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData\n"
                            + "field names, so use the constructor with caution.\n\n",
                        entityClass.fullName(),
                        entityClass.name()));

        specificEntityField.javadoc().add("@param <FieldT>\nField type");

        final JMethod specificEntityFieldConstructor = specificEntityField.constructor(JMod.PUBLIC);
        specificEntityFieldConstructor
            .javadoc()
            .add(
                "Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.");
        final JVar fieldNameParam =
            specificEntityFieldConstructor.param(JMod.FINAL, codeModel.ref(String.class), "fieldName");
        specificEntityFieldConstructor
            .javadoc()
            .addParam(fieldNameParam)
            .append("OData field name. Must match the field returned by the underlying OData service.");
        specificEntityFieldConstructor.body().invoke("super").arg(fieldNameParam);

        final JMethod constructorWithTypeConverter = specificEntityField.constructor(JMod.PUBLIC);
        constructorWithTypeConverter
            .javadoc()
            .add(
                "Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.");
        constructorWithTypeConverter
            .javadoc()
            .add(
                "When creating instances for custom fields, this constructor can be used to add a type converter that will be automatically used by the respective entity when getting or setting custom fields.");

        final JVar fieldNameParam2 =
            constructorWithTypeConverter.param(JMod.FINAL, codeModel.ref(String.class), "fieldName");
        constructorWithTypeConverter
            .javadoc()
            .addParam(fieldNameParam)
            .append("OData field name. Must match the field returned by the underlying OData service.");

        final JVar typeConverterParam =
            constructorWithTypeConverter
                .param(
                    JMod.FINAL,
                    codeModel.ref(TypeConverter.class).narrow(fieldT, codeModel.wildcard()),
                    "typeConverter");
        constructorWithTypeConverter
            .javadoc()
            .addParam(typeConverterParam)
            .append(
                "An implementation of a TypeConverter. The first type must match FieldT, the second type must match the type Olingo returns.");
        constructorWithTypeConverter.body().invoke("super").arg(fieldNameParam2).arg(typeConverterParam);

        return specificEntityField;
    }

}
