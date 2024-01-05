/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.datamodel.odata.client.request;

import com.google.common.annotations.Beta;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.google.common.net.PercentEscaper;
import com.google.common.net.UrlEscapers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Encoding strategy.
 */
@Beta
@RequiredArgsConstructor
public enum UriEncodingStrategy
{
    /**
     * Do not encode.
     */
    NONE(
        Escapers.nullEscaper(), // path
        Escapers.nullEscaper() // query
    ),

    /**
     * Regular request. Consider allowed characters safe.
     */
    REGULAR(
        UrlEscapers.urlPathSegmentEscaper(), // path
        new PercentEscaper("_*-:,/'().", false) // query
    ),

    /**
     * Batch segment. Only keep absolute safe characters.
     */
    BATCH(
        new PercentEscaper("_~-.", false), // path
        new PercentEscaper("_~-.", false) // query
    );

    @Getter
    private final Escaper pathPercentEscaper;

    @Getter
    private final Escaper queryPercentEscaper;
}
