/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

/**
 * An class for executing code while allowing throwing checked exceptions. It is therefore similar to {@link Runnable},
 * but allows checked exceptions to be thrown in the execution block.
 */
@SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
@FunctionalInterface
public interface Executable
{
    /**
     * Executes some logic, or throws an exception if unable to do so.
     *
     * @throws Exception
     *             If there is an issue while executing.
     */
    void execute()
        throws Exception;
}
