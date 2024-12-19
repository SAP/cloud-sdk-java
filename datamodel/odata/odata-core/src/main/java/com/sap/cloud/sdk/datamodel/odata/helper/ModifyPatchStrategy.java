package com.sap.cloud.sdk.datamodel.odata.helper;

public enum ModifyPatchStrategy
{
    /** Only the top level fields can be patched */
    SHALLOW,

    /** All top level and nested fields can be patched, resulting in JSON containing only the changed fields */
    RECURSIVE_DELTA,

    /**
     * All top level and nested fields can be patched, resulting in JSON containing the full value of complex object.
     */
    RECURSIVE_FULL
}
