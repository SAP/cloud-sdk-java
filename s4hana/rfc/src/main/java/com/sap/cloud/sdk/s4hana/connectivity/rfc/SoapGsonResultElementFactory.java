package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.cloud.sdk.result.ResultPrimitive;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Factory implementation that creates a {@code ResultElement}, based on a given {@code JsonElement}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode( callSuper = true )
@Data
@Deprecated
public class SoapGsonResultElementFactory extends GsonResultElementFactory
{
    public SoapGsonResultElementFactory( @Nonnull final GsonBuilder gsonBuilder )
    {
        super(gsonBuilder);
    }

    @Override
    @Nonnull
    protected ResultPrimitive newPrimitive( @Nonnull final JsonElement resultElement )
    {
        return new SoapGsonResultPrimitive(resultElement.getAsJsonPrimitive(), this);
    }
}
