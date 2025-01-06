/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmOperation;

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

    Collection<ServiceFunction> getServiceFunction( final String functionImportName );

    Collection<ServiceFunction> getAllServiceFunctions();

    Collection<ServiceBoundFunction> getAllServiceBoundFunctions();

    Collection<ServiceBoundAction> getAllServiceBoundActions();

    Collection<ServiceAction> getServiceAction( final String actionImportName );

    Collection<ServiceAction> getAllServiceActions();

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

    interface DeprecationInfo
    {
        Option<String> getSuccessorApi();

        Option<String> getDeprecationDate();

        Option<String> getDeprecationRelease();
    }

    interface ExternalOverview
    {
        String getName();

        List<String> getValues();
    }

    interface Annotations
    {
        String getLabel();

        String getQuickInfo();

        String getDescription();

        String getLongDescription();
    }

    interface Annotatable
    {
        Annotations getAnnotations();
    }

    interface Facets
    {
        Boolean isNullable();

        String getDefaultValue();

        Integer getMaxLength();

        Integer getPrecision();

        Integer getScale();
    }

    interface Type
    {
        String getName();

        TypeKind getKind();
    }

    interface PrimitiveType extends Type
    {
        Class<?> getDefaultJavaType();

        @Override
        default TypeKind getKind()
        {
            return TypeKind.PRIMITIVE;
        }

        boolean isSupportedEdmType();
    }

    interface Element
    {
        Type getType();

        Multiplicity getMultiplicity();

        Facets getFacets();
    }

    interface ReturnType extends Element
    {
    }

    interface Parameter extends Element, Annotatable
    {
    }

    interface Property extends Element, Annotatable
    {
    }

    interface NavigationProperty extends Element, Annotatable
    {
    }

    interface EnumType extends Type, Annotatable
    {
        String getFullyQualifiedName();

        Collection<String> getMemberNames();

        String getMemberValue( final String name );
    }

    interface StructuralType extends Type, Annotatable
    {
        String getFullyQualifiedName();

        Collection<String> getPropertyNames();

        Property getProperty( String propertyName );
    }

    interface ComplexType extends StructuralType
    {
        @Override
        default TypeKind getKind()
        {
            return TypeKind.COMPLEX;
        }
    }

    interface EntityType extends StructuralType
    {
        Collection<String> getKeyPropertyNames();

        Collection<String> getNavigationPropertyNames();

        NavigationProperty getNavigationProperty( String navigationPropertyName );

        boolean hasMediaStream();

        @Override
        default TypeKind getKind()
        {
            return TypeKind.ENTITY;
        }
    }

    interface EntitySet extends Annotatable
    {
        String getName();

        EntityType getEntityType();
    }

    interface ServiceOperation extends Annotatable
    {
        String getName();

        @Nullable
        ReturnType getReturnType();

        String getHttpMethod();

        Collection<String> getParameterNames();

        Parameter getParameter( final String parameterName );
    }

    interface ServiceFunction extends ServiceOperation
    {

    }

    interface ServiceBoundOperation extends ServiceOperation
    {
        @Nonnull
        EdmOperation getOperation();

        boolean isFunction();
    }

    interface ServiceBoundFunction extends ServiceBoundOperation
    {
        @Nonnull
        EdmFunction getBoundFunction();

    }

    interface ServiceBoundAction extends ServiceBoundOperation
    {
        @Nonnull
        EdmAction getBoundAction();
    }

    interface ServiceAction extends ServiceOperation
    {

    }
}
