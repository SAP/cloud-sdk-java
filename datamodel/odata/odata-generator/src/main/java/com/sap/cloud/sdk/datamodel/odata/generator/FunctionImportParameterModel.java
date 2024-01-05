/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import com.sun.codemodel.JType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
final class FunctionImportParameterModel
{
    private String edmName;
    private String edmType;
    private String javaName;
    private JType javaType;
    private String description;
    private Boolean nullable;

    public boolean isNonnull()
    {
        return nullable == null || !nullable;
    }
}
