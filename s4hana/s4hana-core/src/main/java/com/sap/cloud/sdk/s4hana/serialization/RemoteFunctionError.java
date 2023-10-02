/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * The wrapper class for MessageClass and MessageNumber.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@AllArgsConstructor
@EqualsAndHashCode
@Deprecated
public class RemoteFunctionError
{
    private final MessageClass messageClass;
    private final MessageNumber messageNumber;
}
