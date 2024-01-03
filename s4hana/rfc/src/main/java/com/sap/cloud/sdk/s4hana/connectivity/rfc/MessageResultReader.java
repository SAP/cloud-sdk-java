/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings( "deprecation" )
class MessageResultReader
{
    static <RequestResultT extends AbstractRemoteFunctionRequestResult<?, RequestResultT>> void addMessageToResult(
        final RequestResultT result,
        final com.sap.cloud.sdk.s4hana.connectivity.rfc.AbstractRemoteFunctionRequestResult.MessageResult messageResult )
    {
        if( messageResult.getMessageType() == null ) {
            log.warn("Ignoring remote function message with unknown type: {}", messageResult);

            return;
        }

        final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message =
            new com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage(
                messageResult.getMessageType(),
                messageResult.getMessageClass(),
                messageResult.getMessageNumber(),
                messageResult.getMessageText());

        switch( messageResult.getMessageType() ) {
            case SUCCESS:
                result.addSuccessMessage(message);
                break;
            case INFORMATION:
                result.addInformationMessage(message);
                break;
            case WARNING:
                result.addWarningMessage(message);
                break;
            case ERROR:
            case ABORT:
            case EXIT:
                result.addErrorMessage(message);
                break;
            default:
                break;
        }
    }
}
