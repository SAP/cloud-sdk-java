package com.sap.cloud.sdk.datamodel.odata.helper;

import lombok.EqualsAndHashCode;

/**
 * Complex type in the virtual data model.
 *
 * @param <ObjectT>
 *            Object type of the complex type.
 */
@EqualsAndHashCode( callSuper = true, doNotUseGetters = true )
public abstract class VdmComplex<ObjectT> extends VdmObject<ObjectT>
{

}
