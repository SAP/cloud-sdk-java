package com.sap.cloud.sdk.datamodel.odata.helper;

import com.google.common.annotations.Beta;

/**
 * Strategy to determine how a patch operation should be applied to an entity.
 *
 * @since 5.16.0
 */
@Beta
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
