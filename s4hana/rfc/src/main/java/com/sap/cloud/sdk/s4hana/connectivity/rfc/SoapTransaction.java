/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.http.HttpStatus;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionCommitFailedException;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@link Transaction} interface to be used for SOAP queries.
 *
 * @param <RequestT>
 *            The type of the request to execute.
 * @param <RequestResultT>
 *            The type of the result to return.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class SoapTransaction<RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
    implements
    Transaction<RequestT, RequestResultT>
{
    /** The serializer to be used during execution. */
    private final com.sap.cloud.sdk.s4hana.connectivity.RequestSerializer<RequestT, RequestResultT> requestSerializer;

    /** The executor logic to be used in the execute case. */
    private final com.sap.cloud.sdk.s4hana.connectivity.ErpHttpRequestExecutor<RequestT, RequestResultT> requestExecutorLogic;

    /** The executor logic to be used in the commit and rollback case. */
    private final com.sap.cloud.sdk.s4hana.connectivity.ErpHttpRequestExecutor<BapiRequest, BapiRequestResult> bapiRequestExecutorLogic;

    @Override
    public void before( @Nonnull final Destination destination, @Nonnull final RequestT request )
    {
        // nothing to do
    }

    @Override
    @Nonnull
    public RequestResultT execute( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        try {
            return requestExecutorLogic.execute(destination.asHttp(), request, requestSerializer);
        }
        catch( final com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException e ) {
            final com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException alternativeException =
                throwExceptionsBasedOnSoapResponsePayload(e.getMessage());
            throw alternativeException != null ? alternativeException : new RemoteFunctionException(e);
        }
    }

    private
        com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
        throwExceptionsBasedOnSoapResponsePayload( final String responsePayload )
    {
        if( responsePayload != null
            && responsePayload
                .contains(
                    "<faultcode>"
                        + com.sap.cloud.sdk.s4hana.connectivity.rfc.SoapNamespace.RESPONSE_PREFIX_SOAP_ENV) ) {
            final String prefix = HttpStatus.SC_INTERNAL_SERVER_ERROR + " Internal Server Error. ";

            if( responsePayload
                .contains(
                    "<faultcode>"
                        + com.sap.cloud.sdk.s4hana.connectivity.rfc.SoapNamespace.RESPONSE_PREFIX_SOAP_ENV
                        + ":Server</faultcode>") ) {
                final String message =
                    prefix
                        + "The ERP user lacks authorization to call the SOAP service (Authorization Object S_SERVICE). "
                        + responsePayload;

                return new com.sap.cloud.sdk.s4hana.connectivity.exception.AccessDeniedException(message);
            }

            if( responsePayload
                .contains(
                    "<faultcode>"
                        + com.sap.cloud.sdk.s4hana.connectivity.rfc.SoapNamespace.RESPONSE_PREFIX_SOAP_ENV
                        + ":Client</faultcode>") ) {
                String exceptionName = "";
                Pattern pattern = Pattern.compile("<Name>(.+?)</Name>");
                Matcher matcher = pattern.matcher(responsePayload);
                if( matcher.find() ) {
                    exceptionName = matcher.group(1);
                }

                String exceptionText = "";
                pattern = Pattern.compile("<Text>(.+?)</Text>");
                matcher = pattern.matcher(responsePayload);
                if( matcher.find() ) {
                    exceptionText = matcher.group(1);
                }

                final StringBuilder messageStringBuilder =
                    new StringBuilder(prefix + " Exception occurred during execution of SOAP service.");

                if( !exceptionName.isEmpty() ) {
                    messageStringBuilder.append(" Exception Name: ").append(exceptionName).append(".");
                }
                if( !exceptionText.isEmpty() ) {
                    messageStringBuilder.append(" Exception Text: ").append(exceptionText).append(".");
                }

                return new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException(
                    messageStringBuilder.toString());
            }
        }
        return null;
    }

    @Override
    public void commit( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        log.debug("Committing BAPI transaction for request: {}.", request);

        final com.sap.cloud.sdk.s4hana.serialization.ErpBoolean waitValue =
            com.sap.cloud.sdk.s4hana.serialization.ErpBoolean
                .of(request.getCommitStrategy().isWaitingForCommitToFinish());

        final String requestBody =
            String
                .format(
                    "<soapenv:Envelope "
                        + "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                        + "xmlns:urn=\"urn:sap-com:document:sap:soap:functions:mc-style\">"
                        + "<soapenv:Header/>"
                        + "<soapenv:Body>"
                        + "<urn:TransactionCommit>"
                        + "<Wait>%s</Wait>"
                        + "</urn:TransactionCommit>"
                        + "</soapenv:Body>"
                        + "</soapenv:Envelope>",
                    waitValue);

        final BapiRequest commitRequest = new BapiRequest("BAPI_TRANSACTION_COMMIT", false);
        final com.sap.cloud.sdk.s4hana.connectivity.SerializedRequest<BapiRequest> serializedRequest =
            new SoapSerializedRequestBuilder<>(commitRequest.withSameCustomHttpHeadersAs(request), requestBody).build();

        final String commitResultBody = bapiRequestExecutorLogic.execute(destination.asHttp(), serializedRequest);

        if( commitResultBody.contains("150") ) {
            throw new RemoteFunctionCommitFailedException(
                "Failed to commit BAPI transaction "
                    + "due to an unknown error on ERP side. Please investigate the respective ABAP logs.");
        }

        log.debug("Successfully committed BAPI transaction for request: {}.", request);
    }

    @Override
    public void rollback( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        log.debug("Rolling back BAPI transaction for request: {}.", request);

        final com.sap.cloud.sdk.s4hana.connectivity.SerializedRequest<BapiRequest> serializedRequest =
            new SoapSerializedRequestBuilder<>(
                new BapiRequest("BAPI_TRANSACTION_ROLLBACK", false).withSameCustomHttpHeadersAs(request),
                "<x:Envelope "
                    + "xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                    + "xmlns:urn1=\"urn:sap-com:document:sap:soap:functions:mc-style\">"
                    + "<x:Header/>"
                    + "<x:Body>"
                    + "<urn1:TransactionRollback/>"
                    + "</x:Body>"
                    + "</x:Envelope>")
                .build();

        bapiRequestExecutorLogic.execute(destination.asHttp(), serializedRequest);

        log.debug("Successfully rolled back BAPI transaction for request: {}.", request);
    }

    @Override
    public void after()
    {
        // nothing to do
    }
}
