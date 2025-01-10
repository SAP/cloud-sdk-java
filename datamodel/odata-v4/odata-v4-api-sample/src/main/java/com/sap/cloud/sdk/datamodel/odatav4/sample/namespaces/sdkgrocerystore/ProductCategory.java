/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.sample.namespaces.sdkgrocerystore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmEnumDeserializer;
import com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmEnumSerializer;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;

/**
 * <p>
 * Original enum type name from the Odata EDM: <b>ProductCategory</b>
 * </p>
 *
 */
@JsonAdapter( GsonVdmAdapterFactory.class )
@JsonSerialize( using = JacksonVdmEnumSerializer.class )
@JsonDeserialize( using = JacksonVdmEnumDeserializer.class )
public enum ProductCategory implements VdmEnum
{

    /**
     * Vegetables
     *
     */
    VEGETABLES("Vegetables", 1L),

    /**
     * Fruits
     *
     */
    FRUITS("Fruits", 2L),

    /**
     * Meat
     *
     */
    MEAT("Meat", 3L),

    /**
     * Fish
     *
     */
    FISH("Fish", 4L),

    /**
     * Dairy
     *
     */
    DAIRY("Dairy", 5L),

    /**
     * Beverages
     *
     */
    BEVERAGES("Beverages", 6L);

    private final String name;
    private final Long value;

    private ProductCategory( final String enumName, final Long enumValue )
    {
        name = enumName;
        value = enumValue;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Long getValue()
    {
        return value;
    }

}
