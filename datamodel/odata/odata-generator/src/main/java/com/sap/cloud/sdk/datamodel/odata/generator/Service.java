package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import io.vavr.control.Option;

interface Service
{
    String SERVICE_MAPPINGS_PACKAGE_SUFFIX = ".packageName";
    String SERVICE_MAPPINGS_CLASS_SUFFIX = ".className";

    String getTitle();

    boolean isDeprecated();

    Option<DeprecationInfo> getDeprecationInfo();

    String getServiceUrl();

    EntitySet getEntitySet( final String entitySetName );

    Collection<EntitySet> getAllEntitySets();

    FunctionImport getFunctionImport( final String functionImportName );

    Collection<FunctionImport> getAllFunctionImports();

    String getJavaPackageName();

    String getName();

    String getJavaClassName();

    String getInfoDescription();

    String getInfoVersion();

    String getMinErpVersion();

    String getExternalUrl();

    String getExternalDescription();

    Collection<ExternalOverview> getExternalOverview();

    Collection<ApiFunction> getAllowedFunctionsByEntity( String entity );

    boolean hasLinkToApiBusinessHub();

    interface ExternalOverview
    {
        String getName();

        List<String> getValues();
    }

    interface EntitySet
    {
        EntityType getEntityType();

        String getName();
    }

    interface FunctionImport extends Annotatable
    {
        String getName();

        @Nullable
        Type getReturnType();

        @Nullable
        Multiplicity getReturnTypeMultiplicity();

        String getHttpMethod();

        Collection<String> getParameterNames();

        Parameter getParameter( final String parameterName );
    }

    interface Parameter extends Element, Annotatable
    {

    }

    interface Property extends Element, Annotatable
    {

    }

    interface Element extends Typed
    {
        @Nullable
        Facets getFacets();
    }

    interface Facets
    {
        Boolean isNullable();

        String getDefaultValue();

        Integer getMaxLength();

        Integer getPrecision();

        Integer getScale();
    }

    interface Annotatable
    {
        AnnotationElement getDocumentationElement();

        String getLabel();

        String getQuickInfo();
    }

    interface AnnotationElement extends Named
    {
        Iterable<? extends AnnotationElement> getChildElements();

        String getText();
    }

    interface StructuralType extends Annotatable, Named
    {
        Collection<String> getPropertyNames();

        Typed getProperty( String propertyName );
    }

    interface SimpleType extends Type
    {
        Class<?> getDefaultType();
    }

    interface ComplexType extends StructuralType
    {

    }

    interface EntityType extends StructuralType
    {
        Collection<String> getKeyPropertyNames();

        Collection<String> getNavigationPropertyNames();

        boolean hasMediaStream();
    }

    interface Typed
    {
        Type getType();

        Multiplicity getMultiplicity();
    }

    interface Type extends Named
    {
        TypeKind getKind();
    }

    interface Named
    {
        String getName();
    }

    interface DeprecationInfo
    {
        Option<String> getSuccessorApi();

        Option<String> getDeprecationDate();

        Option<String> getDeprecationRelease();
    }
}
