/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.Collection;
import java.util.LinkedList;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JDocCommentable;
import com.sun.codemodel.JFieldVar;

class JavadocUtils
{
    static final String ILLEGAL_STATE_JAVADOC_STRING =
        """
        If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no \
        ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's \
        services or returned from the VDM's services as the result of a CREATE or UPDATE call. \
        """;

    private static final String lazyWarningTemplate =
        """
        
        <p>
        If the navigation property <b>%s</b> of a queried <b>%s</b> is operated lazily, \
        an <b>ODataException</b> can be thrown in case of an OData query error.
        <p>
        Please note: <i>Lazy</i> loading of OData entity associations is the process of \
        asynchronous retrieval and persisting of items from a navigation property. \
        If a <i>lazy</i> property is requested by the application for the first time and \
        it has not yet been loaded, an OData query will be run in order to load the missing \
        information and its result will get cached for future invocations.\
        """;

    static
        void
        addFieldReference( final JDocCommentable target, final JDefinedClass entityClass, final JFieldVar entityField )
    {
        final JDocComment targetDoc = target.javadoc();
        targetDoc
            .add(
                targetDoc.size() - 1,
                String.format("\n<li>{@link %1$s#%2$s %2$s}</li>", entityClass.fullName(), entityField.name()));
    }

    static
        String
        getLazyWarningMessage( final NavigationPropertyModel navigationProperty, final JDefinedClass entityClass )
    {
        return String.format(lazyWarningTemplate, navigationProperty.getEdmName(), entityClass.name());
    }

    // Business hub service gives description texts that are very poorly formatted (extra spaces inserted in random spots).
    static String formatDescriptionText( final String rawText )
    {
        return rawText.replaceAll(" {2,}", " ");
    }

    private static
        String
        getDocumentationElementValue( final Service.Annotatable annotatable, final String docElementName )
    {
        final Service.AnnotationElement docElement = annotatable.getDocumentationElement();
        if( docElement != null ) {
            for( final Service.AnnotationElement childElements : docElement.getChildElements() ) {
                if( childElements.getName().equals(docElementName) && childElements.getText() != null ) {
                    final String trimmedText = childElements.getText().trim();
                    if( !trimmedText.isEmpty() ) {
                        return trimmedText;
                    }
                }
            }
        }
        return null;
    }

    static String getBasicDescription( final Service.Annotatable element )
    {
        final String documentationElementString = getDocumentationElementValue(element, "Summary");
        if( !Strings.isNullOrEmpty(documentationElementString) ) {
            return documentationElementString;
        }
        String descriptionAttribute = element.getQuickInfo();
        if( descriptionAttribute == null ) {
            descriptionAttribute = element.getLabel();
        }
        if( descriptionAttribute != null ) {
            final String trimmedText = descriptionAttribute.trim();
            if( !trimmedText.isEmpty() ) {
                return trimmedText;
            }
        }
        return "";
    }

    static String getDetailedDescription( final Service.Annotatable edmElement )
    {
        final String documentationElementString = getDocumentationElementValue(edmElement, "LongDescription");
        if( !Strings.isNullOrEmpty(documentationElementString) ) {
            return documentationElementString;
        }
        return "";
    }

    static String getCompleteDescription( final Service.Annotatable element )
    {
        final String basicDescription = getBasicDescription(element);
        final String detailedDescription = getDetailedDescription(element);

        return basicDescription
            + (basicDescription.isEmpty() ? detailedDescription : String.format("<p>%s</p>", detailedDescription));
    }

    static String getConstraints( final Service.Element edmElement )
    {
        String parameterConstraintsString = "Constraints: ";

        final Service.Facets parameterFacets = edmElement.getFacets();
        final Collection<String> parameterFacetsStrings = new LinkedList<>();

        if( parameterFacets != null ) {
            if( parameterFacets.isNullable() != null && parameterFacets.isNullable() ) {
                parameterFacetsStrings.add("Nullable");
            } else {
                parameterFacetsStrings.add("Not nullable");
            }
            if( !Strings.isNullOrEmpty(parameterFacets.getDefaultValue()) ) {
                parameterFacetsStrings.add(String.format("Default value: %s", parameterFacets.getDefaultValue()));
            }
            if( parameterFacets.getMaxLength() != null ) {
                parameterFacetsStrings
                    .add(String.format("Maximum length: %s", parameterFacets.getMaxLength().toString()));
            }
            if( parameterFacets.getPrecision() != null ) {
                parameterFacetsStrings.add(String.format("Precision: %s", parameterFacets.getPrecision().toString()));
            }
            if( parameterFacets.getScale() != null ) {
                parameterFacetsStrings.add(String.format("Scale: %s", parameterFacets.getScale().toString()));
            }
        }

        if( !parameterFacetsStrings.isEmpty() ) {
            parameterConstraintsString += Joiner.on(", ").join(parameterFacetsStrings);
        } else {
            parameterConstraintsString += "none";
        }
        return parameterConstraintsString;
    }

    static <T extends Service.Annotatable & Service.Element> String getDescriptionAndConstraints(
        final String edmName,
        final T edmElement )
    {
        String description = getCompleteDescription(edmElement);
        final String constraints = getConstraints(edmElement);

        description += description.isEmpty() ? constraints : String.format("<p>%s</p>", constraints);

        final String edmReference = String.format("Original parameter name from the Odata EDM: <b>%s</b>", edmName);
        description += description.isEmpty() ? edmReference : String.format("<p>%s</p>", edmReference);

        return description;
    }

    static void inheritJavadoc( final JDocCommentable commentable )
    {
        commentable.javadoc().add("{@inheritDoc}");
    }

}
