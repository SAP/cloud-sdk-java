package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link MessageClass}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class MessageClassConverter extends AbstractErpTypeConverter<MessageClass>
{
    /**
     * Statically created instance of this converter.
     */
    public static final MessageClassConverter INSTANCE = new MessageClassConverter();

    @Nonnull
    @Override
    public Class<MessageClass> getType()
    {
        return MessageClass.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final MessageClass object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<MessageClass> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(new MessageClass(domainObject));
    }
}
