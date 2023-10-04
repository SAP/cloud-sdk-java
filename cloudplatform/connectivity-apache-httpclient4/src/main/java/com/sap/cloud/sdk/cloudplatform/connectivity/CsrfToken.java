/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * CSRF token wrapper type.
 */
@Value
@RequiredArgsConstructor
public class CsrfToken
{
    @Nonnull
    String token;
}
