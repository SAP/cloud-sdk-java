/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.typeconverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;

/**
 * Abstract type converter base class for converting types to and from their domain-specific counterparts.
 *
 * @param <T>
 *            The type to convert from/to the domain-specific counterpart.
 * @param <DomainT>
 *            The domain-specific type.
 */
@Slf4j
public abstract class AbstractTypeConverter<T, DomainT> implements TypeConverter<T, DomainT>
{

    @Nonnull
    @Override
    public final ConvertedObject<DomainT> toDomain( @Nullable final T object )
    {
        if( object == null ) {
            return ConvertedObject.ofNull();
        }

        try {
            return toDomainNonNull(object);
        }
        catch( final Exception e ) {
            log.debug("Failed to convert to domain object: {}.", object, e);
            return ConvertedObject.ofNotConvertible();
        }
    }

    @Nonnull
    @Override
    public final ConvertedObject<T> fromDomain( @Nullable final DomainT domainObject )
    {
        if( domainObject == null ) {
            return ConvertedObject.ofNull();
        }

        try {
            return fromDomainNonNull(domainObject);
        }
        catch( final Exception e ) {
            if( log.isDebugEnabled() ) {
                log
                    .debug(
                        "Failed to convert domain object of type "
                            + getDomainType().getName()
                            + " to "
                            + getType().getName()
                            + ": "
                            + domainObject
                            + ".",
                        e);
            }
            return ConvertedObject.ofNotConvertible();
        }
    }

    /**
     * Actual converter implementation from an arbitrary object to its domain-specific counterpart.
     *
     * @param object
     *            The object to transform to its domain-specific counterpart.
     *
     * @return A wrapper containing the domain-specific counterpart.
     *
     * @throws Exception
     *             If an error occurred during the transformation.
     */
    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
    @Nonnull
    public abstract ConvertedObject<DomainT> toDomainNonNull( @Nonnull final T object )
        throws Exception;

    /**
     * Actual converter implementation from a domain-specific object to an arbitrary type.
     *
     * @param domainObject
     *            The domain-specific object to transform.
     *
     * @return A wrapper containing the converted object.
     *
     * @throws Exception
     *             If an error occurred during the transformation.
     */
    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
    @Nonnull
    public abstract ConvertedObject<T> fromDomainNonNull( @Nonnull final DomainT domainObject )
        throws Exception;
}
