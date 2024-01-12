/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import com.sap.cloud.sdk.datamodel.odata.helper.EntitySelectable;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocCommentable;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

class EntitySelectableGenerator
{
    private static final String CLASS_NAME_SELECTABLE_SUFFIX = "Selectable";
    private static final String NAMESPACE_SUB_PACKAGE_NAME = "selectable";

    private final JCodeModel codeModel;

    EntitySelectableGenerator( final JCodeModel codeModel )
    {
        this.codeModel = codeModel;
    }

    JDefinedClass
        generateSpecificEntitySelectableInterface( final JPackage namespacePackage, final JDefinedClass entityClass )
            throws JClassAlreadyExistsException
    {
        final String entityClassName = entityClass.name();
        final JPackage selectableSubPackage = namespacePackage.subPackage(NAMESPACE_SUB_PACKAGE_NAME);

        final JDefinedClass specificEntitySelector =
            selectableSubPackage._interface(JMod.PUBLIC, entityClassName + CLASS_NAME_SELECTABLE_SUFFIX);
        specificEntitySelector._extends(codeModel.ref(EntitySelectable.class).narrow(entityClass));

        return specificEntitySelector;
    }

    void addClassLevelJavadoc(
        final JDocCommentable entitySelectableInterface,
        final JDefinedClass entityClass,
        final JDefinedClass entityField,
        final JDefinedClass entityLink )
    {
        entitySelectableInterface
            .javadoc()
            .add(
                String
                    .format(
                        """
                        Interface to enable OData entity selectors for {@link %1$s %2$s}. \
                        This interface is used by {@link %3$s %4$s} and {@link %5$s %6$s}.
                        
                        """,
                        entityClass.fullName(),
                        entityClass.name(),
                        entityField.fullName(),
                        entityField.name(),
                        entityLink.fullName(),
                        entityLink.name()));
        entitySelectableInterface.javadoc().add("<p>Available instances:\n<ul>");
        entitySelectableInterface.javadoc().add("\n</ul>");
    }
}
