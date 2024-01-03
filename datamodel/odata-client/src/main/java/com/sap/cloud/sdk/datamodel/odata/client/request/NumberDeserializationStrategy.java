/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.google.gson.GsonBuilder;

import lombok.RequiredArgsConstructor;

/**
 * Number deserialization strategy to determine behavior for JSON numbers without target type references.
 */
@RequiredArgsConstructor
public enum NumberDeserializationStrategy
{
    /**
     * Double strategy to deserialize JSON numbers to double, if no target type references are specified.
     */
    DOUBLE(gsonBuilder -> {
        // default behavior of GSON
    }),

    /**
     * BigDecimal strategy to deserialize JSON numbers to BigDecimal, if no target type references are specified.
     */
    BIG_DECIMAL(gsonBuilder -> {
        gsonBuilder.setObjectToNumberStrategy(com.google.gson.ToNumberPolicy.BIG_DECIMAL); // FQN for backwards-compatibility
    });

    private final Consumer<GsonBuilder> adapter;

    /**
     * Adjust the deserialization strategy for untyped numbers.
     *
     * @param gsonBuilder
     *            The GsonBuilder to change.
     */
    void decorate( @Nonnull final GsonBuilder gsonBuilder )
    {
        adapter.accept(gsonBuilder);
    }
}
