package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.util.List;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionExceptionFactory;

import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings( "deprecation" )
class ExceptionPropagatingRemoteFunctionRequestErrorHandler implements RemoteFunctionRequestErrorHandler
{
    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Getter
    @Setter
    private RemoteFunctionErrorMapper remoteFunctionErrorMapper = new DefaultRemoteFunctionErrorMapper();

    /**
     * <p>
     * Inspects the request result and returns an {@link RemoteFunctionException} encapsulated in an {@link Option} in
     * case the result contains an error message.
     * </p>
     *
     * <p>
     * If multiple error messages are included, the returned exception will contain a concatenated string of them in the
     * exception message.
     * </p>
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Nonnull
    @Override
    public <
        RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
        Option<RemoteFunctionException>
        handleRequestResult( @Nonnull final RequestResultT requestResult )
    {
        final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> errors =
            requestResult.getErrorMessages();
        final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> warnings =
            requestResult.getWarningMessages();
        final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> informations =
            requestResult.getInformationMessages();

        final TreeMap<RemoteFunctionExceptionPriority, RemoteFunctionException> prioritizedExceptions =
            getPrioritizedExceptionsFromMessages(errors);

        if( prioritizedExceptions.isEmpty() ) {
            final TreeMap<RemoteFunctionExceptionPriority, RemoteFunctionException> prioritizedWarnings =
                getPrioritizedExceptionsFromMessages(warnings);

            if( prioritizedWarnings.isEmpty() ) {
                final RemoteFunctionException exception =
                    new RemoteFunctionException(Iterables.concat(errors, warnings, informations));
                return Option.of(exception);
            } else {
                return Option.of(getWithHighestPriority(prioritizedWarnings));
            }
        } else {
            return Option.of(getWithHighestPriority(prioritizedExceptions));
        }
    }

    private static RemoteFunctionException getWithHighestPriority(
        final TreeMap<RemoteFunctionExceptionPriority, RemoteFunctionException> prioritizedExceptions )
    {
        return prioritizedExceptions.firstEntry().getValue();
    }

    private TreeMap<RemoteFunctionExceptionPriority, RemoteFunctionException> getPrioritizedExceptionsFromMessages(
        final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> messages )
    {
        final TreeMap<RemoteFunctionExceptionPriority, RemoteFunctionException> prioritizedExceptions =
            Maps.newTreeMap();

        for( final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message : messages ) {
            final com.sap.cloud.sdk.s4hana.serialization.MessageClass messageClass = message.getMessageClass();
            final com.sap.cloud.sdk.s4hana.serialization.MessageNumber messageNumber = message.getMessageNumber();

            final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionError error =
                new com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionError(messageClass, messageNumber);

            final RemoteFunctionExceptionFactory<?> exceptionFactory =
                remoteFunctionErrorMapper.getMapping().get(error);

            final RemoteFunctionExceptionPriority priority = remoteFunctionErrorMapper.getPriorities().get(error);

            if( exceptionFactory != null ) {
                final RemoteFunctionException exceptionSupplier = exceptionFactory.create(message);
                prioritizedExceptions.put(priority, exceptionSupplier);
            }
        }

        return prioritizedExceptions;
    }
}
