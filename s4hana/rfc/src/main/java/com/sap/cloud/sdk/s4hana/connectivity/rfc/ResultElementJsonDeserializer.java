/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.cloud.sdk.result.ResultElement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
class ResultElementJsonDeserializer implements JsonDeserializer<ResultElement>
{
    protected final GsonResultElementFactory resultElementFactory;

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Override
    public
        ResultElement
        deserialize( final JsonElement json, final Type typeOfT, final JsonDeserializationContext context )
            throws JsonParseException
    {
        try {
            return resultElementFactory.create(json);
        }
        catch( final IllegalArgumentException e ) {
            if( log.isWarnEnabled() ) {
                log
                    .warn(
                        "Failed to convert "
                            + JsonElement.class.getSimpleName()
                            + " "
                            + json
                            + " to instance of "
                            + ResultElement.class.getSimpleName()
                            + ".",
                        e);
            }
            return null;
        }
    }
}
