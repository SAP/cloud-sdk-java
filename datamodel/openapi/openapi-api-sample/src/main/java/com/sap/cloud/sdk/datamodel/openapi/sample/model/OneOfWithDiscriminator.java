/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

/*
 * SodaStore API
 * API for managing soda products and orders in SodaStore.
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package com.sap.cloud.sdk.datamodel.openapi.sample.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * OneOfWithDiscriminator
 */
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, property = "sodaType", visible = true )
@JsonSubTypes( {
    @JsonSubTypes.Type( value = Cola.class, name = "Cola" ),
    @JsonSubTypes.Type( value = Fanta.class, name = "Fanta" ), } )

public interface OneOfWithDiscriminator
{
}
