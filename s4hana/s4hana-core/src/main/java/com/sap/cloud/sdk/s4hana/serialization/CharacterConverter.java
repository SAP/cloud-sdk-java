/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link Character}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class CharacterConverter extends AbstractErpTypeConverter<Character>
{
    /**
     * Statically created instance of this converter.
     */
    public static final CharacterConverter INSTANCE = new CharacterConverter();

    @Nonnull
    @Override
    public Class<Character> getType()
    {
        return Character.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final Character object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<Character> fromDomainNonNull( @Nonnull final String domainObject )
    {
        final char[] chars = domainObject.toCharArray();

        if( chars.length != 1 ) {
            // Object contains more than one character
            return ConvertedObject.ofNotConvertible();
        }

        return ConvertedObject.of(chars[0]);
    }
}
