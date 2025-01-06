/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.util.Collections;

import javax.annotation.Nonnull;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sap.cloud.sdk.result.DefaultResultCollection;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.cloud.sdk.result.GsonResultObject;
import com.sap.cloud.sdk.result.GsonResultPrimitive;
import com.sap.cloud.sdk.result.ResultCollection;
import com.sap.cloud.sdk.result.ResultObject;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode( callSuper = true )
@Data
class SoapGsonResultPrimitive extends GsonResultPrimitive
{
    protected final GsonResultElementFactory resultElementFactory;

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public SoapGsonResultPrimitive(
        final JsonPrimitive jsonPrimitive,
        final GsonResultElementFactory resultElementFactory )
    {
        super(jsonPrimitive);
        this.resultElementFactory = resultElementFactory;
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Override
    public boolean isResultCollection()
    {
        if( Strings.isNullOrEmpty(jsonPrimitive.getAsString()) ) {
            return true;
        }

        return super.isResultCollection();
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Override
    public boolean isResultObject()
    {
        if( Strings.isNullOrEmpty(jsonPrimitive.getAsString()) ) {
            return true;
        }

        return super.isResultObject();
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Nonnull
    @Override
    public ResultCollection getAsCollection()
        throws UnsupportedOperationException
    {
        if( Strings.isNullOrEmpty(jsonPrimitive.getAsString()) ) {
            return new DefaultResultCollection(Collections.emptyList());
        }

        return super.getAsCollection();
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Nonnull
    @Override
    public ResultObject getAsObject()
        throws UnsupportedOperationException
    {
        if( Strings.isNullOrEmpty(jsonPrimitive.getAsString()) ) {
            return new GsonResultObject(new JsonObject(), resultElementFactory);
        }

        return super.getAsObject();
    }
}
