package com.sap.cloud.sdk.s4hana.serialization;

import com.sap.cloud.sdk.typeconverter.TypeConverter;

/**
 * Type converter for converting types to and from their ERP counterparts.
 * <p>
 * <strong>Important:</strong> Implementations must be thread-safe.
 *
 * @param <T>
 *            The generic sub class type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public interface ErpTypeConverter<T> extends TypeConverter<T, String>
{

}
