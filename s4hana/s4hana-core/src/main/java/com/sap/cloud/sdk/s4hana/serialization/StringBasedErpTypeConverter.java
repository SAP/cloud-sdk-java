/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for String like ERP types.
 *
 * @param <T>
 *            The generic sub class type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class StringBasedErpTypeConverter<T extends StringBasedErpType<T>> extends AbstractErpTypeConverter<T>
{
    @Nonnull
    private final Class<T> type;

    @Nonnull
    @Override
    public Class<T> getType()
    {
        return type;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nullable final T object )
    {
        return object == null ? ConvertedObject.ofNull() : ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<T> fromDomainNonNull( @Nullable final String domainObject )
    {
        if( Strings.isNullOrEmpty(domainObject) ) {
            return ConvertedObject.ofNull();
        }

        T object;
        try {
            object = type.getConstructor(String.class).newInstance(domainObject);

            // if input is only filling characters -> null
            switch( object.getFillCharStrategy() ) {
                case FILL_LEADING:
                case FILL_LEADING_IF_NUMERIC:
                    if( CharMatcher.is(object.getFillChar()).matchesAllOf(domainObject) ) {
                        object = null;
                    }
                    break;
                default:
            }
        }
        catch( final NoSuchMethodException | SecurityException | IllegalAccessException | InstantiationException e ) {
            throw new ShouldNotHappenException(
                String
                    .format(
                        "Failed to instantiate object from %s: No constructor available with %s parameter.",
                        getType().getSimpleName(),
                        String.class.getSimpleName()),
                e);
        }
        catch( final InvocationTargetException e ) {
            if( log.isDebugEnabled() ) {
                log.debug("Failed to convert ERP object to " + getType().getName() + ": " + domainObject + ".");
            }
            return ConvertedObject.ofNotConvertible();
        }

        return ConvertedObject.of(object);
    }
}
