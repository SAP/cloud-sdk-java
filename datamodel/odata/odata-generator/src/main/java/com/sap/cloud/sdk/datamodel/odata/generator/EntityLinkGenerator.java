/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.ExpressionFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.helper.OneToOneLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocCommentable;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;

class EntityLinkGenerator
{
    private static final String NAMESPACE_SUB_PACKAGE_NAME = "link";

    private final JCodeModel codeModel;

    EntityLinkGenerator( final JCodeModel codeModel )
    {
        this.codeModel = codeModel;
    }

    JDefinedClass generateSpecificEntityLinkClass(
        final JPackage namespacePackage,
        final JDefinedClass entityClass,
        final JDefinedClass selectableInterface )
        throws JClassAlreadyExistsException
    {
        final String entityClassName = entityClass.name();
        final JPackage linkSubPackage = getLinkSubPackage(namespacePackage);

        final JDefinedClass specificEntityLink =
            linkSubPackage._class(JMod.PUBLIC, entityClassName + CommonConstants.CLASS_NAME_LINK_SUFFIX);
        final JTypeVar objectT =
            specificEntityLink.generify("ObjectT", codeModel.ref(VdmObject.class).narrow(codeModel.wildcard()));
        final JClass entityLinkExtension =
            codeModel.ref(EntityLink.class).narrow(specificEntityLink.narrow(objectT), entityClass, objectT);
        specificEntityLink._extends(entityLinkExtension);
        specificEntityLink._implements(selectableInterface);

        // class documentation
        fillLinkClassJavadoc(entityClass, specificEntityLink);

        // default, public constructor with fieldName parameter
        createLinkConstructor(specificEntityLink);

        // private constructor to enable copying (to ensure type safety)
        createLinkCopyConstructor(specificEntityLink, entityLinkExtension);

        // protected translate function (to ensure type safety)
        createTranslateLinkTypeMethod(specificEntityLink, objectT, entityLinkExtension);

        return specificEntityLink;
    }

    JDefinedClass generateSpecificEntityLinkOneToOneClass(
        final JPackage namespacePackage,
        final JDefinedClass generatedEntityClass,
        final JDefinedClass generatedSpecificEntityLinkClass,
        final Service service )
        throws JClassAlreadyExistsException
    {
        final String entityClassName = generatedEntityClass.name();
        final JPackage linkSubPackage = getLinkSubPackage(namespacePackage);

        final JDefinedClass specificEntityOneToOneLink =
            linkSubPackage._class(JMod.PUBLIC, entityClassName + "OneToOneLink");
        final JTypeVar objectT =
            specificEntityOneToOneLink.generify("ObjectT", codeModel.ref(VdmObject.class).narrow(codeModel.wildcard()));
        specificEntityOneToOneLink._extends(generatedSpecificEntityLinkClass.narrow(objectT));
        specificEntityOneToOneLink._implements(codeModel.ref(OneToOneLink.class).narrow(generatedEntityClass, objectT));

        createLinkConstructor(specificEntityOneToOneLink);

        // class JavaDoc
        specificEntityOneToOneLink
            .javadoc()
            .add(
                String
                    .format(
                        """
                            Template class to represent entity navigation links of {@link %s %s} to other entities, where\
                             the cardinality of the related entity is at most 1. This class extends {@link %s %s} and \
                            provides an additional filter function.
                            """,
                        generatedEntityClass.fullName(),
                        generatedEntityClass.name(),
                        generatedSpecificEntityLinkClass.fullName(),
                        generatedSpecificEntityLinkClass.name()));

        specificEntityOneToOneLink
            .javadoc()
            .add(
                "@param <ObjectT>\nEntity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.");

        // implement filter function
        final JMethod filterFunction =
            specificEntityOneToOneLink
                .method(
                    JMod.PUBLIC,
                    codeModel.ref(ExpressionFluentHelper.class).narrow(generatedEntityClass),
                    "filter");

        // param
        final JVar filterExpression =
            filterFunction
                .param(JMod.FINAL, codeModel.ref(ExpressionFluentHelper.class).narrow(objectT), "filterExpression");
        filterExpression.annotate(codeModel.ref(Nonnull.class));

        filterFunction.body()._return(JExpr._super().invoke("filterOnOneToOneLink").arg(filterExpression));

        // javadoc
        filterFunction.javadoc().add("""
            Query modifier to restrict the result set to entities for which this expression (formulated over a \
            property of a <b>related</b> entity) evaluates to true. Note that filtering on a related entity \
            does not expand the selection of the respective query to that entity.\
            """);
        filterFunction.javadoc().addParam(filterExpression).add("A filter expression on the related entity.");
        filterFunction
            .javadoc()
            .addReturn()
            .add("A filter expression over a related entity, scoped to the parent entity.");

        // annotations
        filterFunction.annotate(codeModel.ref(Nonnull.class));
        filterFunction.annotate(codeModel.ref(Override.class));

        return specificEntityOneToOneLink;
    }

    private JPackage getLinkSubPackage( final JPackage namespacePackage )
    {
        return namespacePackage.subPackage(NAMESPACE_SUB_PACKAGE_NAME);
    }

    private void fillLinkClassJavadoc( final JDefinedClass entityClass, final JDocCommentable specificEntityLink )
    {
        specificEntityLink
            .javadoc()
            .add(
                String
                    .format(
                        """
                            Template class to represent entity navigation links of {@link %s %s} to other entities. Instances of this object are used in query modifier methods of the entity
                            fluent helpers. Contains methods to compare a field's value with a provided value.

                            Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
                            field names, so use the constructor with caution.

                            """,
                        entityClass.fullName(),
                        entityClass.name()));

        specificEntityLink
            .javadoc()
            .add(
                "@param <ObjectT>\nEntity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.");
    }

    private void createLinkConstructor( final JDefinedClass specificEntityLink )
    {
        final JMethod specificEntityLinkConstructor = specificEntityLink.constructor(JMod.PUBLIC);
        specificEntityLinkConstructor
            .javadoc()
            .add(
                "Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.");
        final JVar fieldNameParam =
            specificEntityLinkConstructor.param(JMod.FINAL, codeModel.ref(String.class), "fieldName");
        specificEntityLinkConstructor
            .javadoc()
            .addParam(fieldNameParam)
            .append("OData navigation field name. Must match the field returned by the underlying OData service.");
        specificEntityLinkConstructor.body().invoke("super").arg(fieldNameParam);
    }

    private void createLinkCopyConstructor( final JDefinedClass specificEntityLink, final JClass entityLinkExtension )
    {
        final JMethod cloneConstructor = specificEntityLink.constructor(JMod.PRIVATE);
        final JVar toCloneParam = cloneConstructor.param(JMod.FINAL, entityLinkExtension, "toClone");
        cloneConstructor.body().invoke("super").arg(toCloneParam);
    }

    private void createTranslateLinkTypeMethod(
        final JDefinedClass specificEntityLink,
        final JTypeVar objectT,
        final JClass entityLinkExtension )
    {
        final JMethod linkTranslateMethod =
            specificEntityLink.method(JMod.PROTECTED, specificEntityLink.narrow(objectT), "translateLinkType");
        final JVar linkParam = linkTranslateMethod.param(JMod.FINAL, entityLinkExtension, "link");
        linkTranslateMethod.annotate(Nonnull.class);
        linkTranslateMethod.annotate(Override.class);
        linkTranslateMethod.body()._return(JExpr._new(specificEntityLink.narrow(objectT)).arg(linkParam));
    }
}
