/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction;
import com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction;

class BoundOperationGenerator
{
    static Class<?> getFunctionClass(
        final boolean boundToCollection,
        final boolean returnsCollection,
        final TypeKind resultTypeKind,
        final boolean composable )
    {
        if( !boundToCollection && !returnsCollection ) {
            if( !composable ) {
                return BoundFunction.SingleToSingle.class;
            }
            switch( resultTypeKind ) {
                case ENTITY:
                    return BoundFunction.SingleToSingleEntity.Composable.class;
                // TODO: Support further navigation on primitive, complex and enum results
                case PRIMITIVE:
                case COMPLEX:
                case ENUM:
                default:
                    return BoundFunction.SingleToSingle.class;
            }
        }
        if( !boundToCollection && returnsCollection ) {
            if( !composable ) {
                return BoundFunction.SingleToCollection.class;
            }
            switch( resultTypeKind ) {
                case ENTITY:
                    return BoundFunction.SingleToCollectionEntity.Composable.class;
                // TODO: Support further navigation on primitive, complex and enum results
                case PRIMITIVE:
                case COMPLEX:
                case ENUM:
                default:
                    return BoundFunction.SingleToCollection.class;
            }
        }
        if( boundToCollection && !returnsCollection ) {
            if( !composable ) {
                return BoundFunction.CollectionToSingle.class;
            }
            switch( resultTypeKind ) {
                case ENTITY:
                    return BoundFunction.CollectionToSingleEntity.Composable.class;
                // TODO: Support further navigation on primitive, complex and enum results
                case PRIMITIVE:
                case COMPLEX:
                case ENUM:
                default:
                    return BoundFunction.SingleToCollection.class;
            }
        }
        if( boundToCollection && returnsCollection ) {
            if( !composable ) {
                return BoundFunction.CollectionToCollection.class;
            }
            switch( resultTypeKind ) {
                case ENTITY:
                    return BoundFunction.CollectionToCollectionEntity.Composable.class;
                // TODO: Support further navigation on primitive, complex and enum results
                case PRIMITIVE:
                case COMPLEX:
                case ENUM:
                default:
                    return BoundFunction.SingleToCollection.class;
            }
        }
        throw new IllegalStateException("At this point all cases must be handled.");
    }

    static Class<?> getActionClass( final boolean boundToCollection, final boolean returnsCollection )
    {
        if( !boundToCollection && !returnsCollection ) {
            return BoundAction.SingleToSingle.class;
        }
        if( !boundToCollection && returnsCollection ) {
            return BoundAction.SingleToCollection.class;
        }
        if( boundToCollection && !returnsCollection ) {
            return BoundAction.CollectionToSingle.class;
        }
        if( boundToCollection && returnsCollection ) {
            return BoundAction.CollectionToCollection.class;
        }
        throw new IllegalStateException("At this point all cases must be handled.");
    }

    static String getJavadocDescriptionForOperation( final boolean isFunction, final boolean boundToCollection )
    {
        final String singleBindingText = " that can be applied to any entity object of this class.</p>";
        final String collectionBindingText = " that can be applied to a collection of entities of this class.</p>";

        return (isFunction ? "Function" : "Action") + (boundToCollection ? collectionBindingText : singleBindingText);
    }

    static String getJavadocReturnForOperation( final boolean isFunction, final boolean boundToCollection )
    {
        final String operation = isFunction ? "Function" : "Action";
        final String singleBindingDescriptionText =
            operation
                + " object prepared with the given parameters to be applied to any entity object of this class.</p>";
        final String collectionBindingDescriptionText =
            operation
                + " object prepared with the given parameters to be applied to a collection of entities of this class.</p>";

        final String singleBindingUsageText =
            String
                .format(
                    " To execute it use the {@code service.forEntity(entity).apply%s(this%s)} API.",
                    operation,
                    operation);
        final String collectionBindingUsageText =
            String
                .format(
                    " To execute it use the {@code service.forEntity(entity).apply%s(this%s)} API.",
                    operation,
                    operation);

        return boundToCollection
            ? collectionBindingDescriptionText + collectionBindingUsageText
            : singleBindingDescriptionText + singleBindingUsageText;
    }
}
