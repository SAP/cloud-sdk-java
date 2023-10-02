/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

/**
 * Class representing the result of a BAPI call returned by {@link BapiRequest}.
 * <p>
 * Use the methods {@link #get(String)}, {@link #get(int)}, {@link #getIfPresent(String)}, {@link #collect(String)},
 * {@link #size()}, {@link #isEmpty()}, or {@link #has(String)} to access the content of the {@link BapiRequestResult}.
 * <p>
 * Use the methods {@link #hasFailed()}, {@link #hasSuccessMessages()}, {@link #hasInformationMessages()},
 * {@link #hasWarningMessages()}, or {@link #hasErrorMessages()} to inspect the resulting messages from the BAPI call.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class BapiRequestResult extends AbstractRemoteFunctionRequestResult<BapiRequest, BapiRequestResult>
{

}
