/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

/**
 * OData filter expression operand for a generic entity field reference.
 */
public interface FieldReference extends Expressions.OperandSingle
{
    /**
     * Static factory method to easily instantiate a generic field reference.
     *
     * @param fieldName
     *            The field name.
     * @return The newly created instance.
     */
    @Nonnull
    static FieldUntyped of( @Nonnull final String fieldName )
    {
        return () -> fieldName;
    }

    /**
     * Static factory method to easily instantiate a nested field reference via a path of fields.
     *
     * @param fieldNames
     *            The field name(s) identifying the field.
     * @return The newly created instance.
     */
    @Nonnull
    static FieldUntyped ofPath( @Nonnull final String... fieldNames )
    {
        final String fieldIdentifier = String.join("/", fieldNames);
        return () -> fieldIdentifier;
    }

    /**
     * javadoc
     *
     * @return The field name this reference points towards.
     */
    @Nonnull
    String getFieldName();

    @Nonnull
    @Override
    default String getExpression(
        @Nonnull final ODataProtocol protocol,
        @Nonnull final Map<String, Predicate<FieldReference>> prefixes )
    {
        String result = getFieldName();
        final Optional<String> prefix =
            prefixes.entrySet().stream().filter(e -> e.getValue().test(this)).map(Map.Entry::getKey).findFirst();

        if( prefix.isPresent() ) {
            result = prefix.get() + "/" + result;
        }
        return result;
    }
}
