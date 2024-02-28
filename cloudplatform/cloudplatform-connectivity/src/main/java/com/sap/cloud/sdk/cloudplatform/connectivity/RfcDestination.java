/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

/**
 * Platform independent representation of a rfc destination as a collection of key-value pairs.
 * <p>
 * Additionally, provides an easy way to decorate itself with a given decorator function.
 *
 * @deprecated Please use {@link Destination} instead.
 */
@Deprecated
public interface RfcDestination extends Destination, RfcDestinationProperties
{

}
