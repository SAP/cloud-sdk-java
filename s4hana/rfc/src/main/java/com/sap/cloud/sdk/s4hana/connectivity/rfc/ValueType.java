/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

/**
 * Value type for remote functions.
 */
enum ValueType
{
    /**
     * One Value with valueType FIELD contains one object.
     */
    FIELD,

    /**
     * One Value with valueType STRUCTURE contains a list of Values.
     */
    STRUCTURE,

    /**
     * One Value with valueType TABLE contains a list of lists of Values.
     */
    TABLE
}
