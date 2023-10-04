/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * The strategy how the commitment of created, updated or deleted records is conducted after the execution of an
 * {@link AbstractRemoteFunctionRequest} in the remote system.
 * </p>
 *
 * <p>
 * For instance, after the invocation of a BAPI using {@link BapiRequest} it may be required to invoke
 * BAPI_TRANSACTION_COMMIT in order to trigger the commitment in the remote system. This strategy decides if
 * BAPI_TRANSACTION_COMMIT is invoked and if so, whether its processing is executed synchronously or asynchronously.
 * </p>
 *
 * @see BapiRequest
 * @see RfmRequest
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@RequiredArgsConstructor
@Getter( AccessLevel.PACKAGE )
@Deprecated
public enum CommitStrategy
{
    /**
     * No commitment is triggered in the remote system.
     */
    NO_COMMIT(false, false),

    /**
     * The commitment in the remote system is triggered synchronously. That is, the method
     * {@link AbstractRemoteFunctionRequest#execute()} returns <b>after</b> the commitment is finished in the remote
     * system. If the commitment in the remote system failed, the method {@link AbstractRemoteFunctionRequest#execute()}
     * throws an {@link com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionCommitFailedException}
     * accordingly.
     */
    COMMIT_SYNC(true, true),

    /**
     * The commitment in the remote system is triggered asynchronously. That is, the method
     * {@link AbstractRemoteFunctionRequest#execute()} invokes the commitment in the remote system, but <b>does not
     * wait</b> for its processing. Consequently, there is no means to find out if the commitment in the remote system
     * was successful.
     */
    COMMIT_ASYNC(true, false);

    /**
     * Whether the request is actually committed.
     */
    private final boolean performingCommit;

    /**
     * Whether to wait the transaction commit to finish.
     */
    private final boolean waitingForCommitToFinish;
}
