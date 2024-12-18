package com.sap.cloud.sdk.datamodel.odata.helper;

public enum ModifyPatchStrategy
{
    /** Only the top level primitive fields can be patched */
    PRIMITIVE,

    /** All primitive and complex fields can be patched, resulting in JSON containing only the changed fields */
    COMPLEX_DELTA,

    /**
     * All primitive fields are patched, and complex fields are effectively replaced, resulting in JSON containing the
     * full complex object.
     */
    COMPLEX_FULL
}
