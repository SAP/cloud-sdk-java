/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import com.sun.codemodel.JType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
final class OperationParameterModel
{
    private String edmName;
    private String edmType;
    private String javaName;
    private JType javaType;
    private String description;
    private boolean isNullable;
}
