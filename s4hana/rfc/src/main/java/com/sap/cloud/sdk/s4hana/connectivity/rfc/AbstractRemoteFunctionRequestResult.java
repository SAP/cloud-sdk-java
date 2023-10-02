/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.sap.cloud.sdk.result.CollectedResultCollection;
import com.sap.cloud.sdk.result.DefaultCollectedResultCollection;
import com.sap.cloud.sdk.result.ElementName;
import com.sap.cloud.sdk.result.ResultElement;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class for RemoteFunctionRequestResult.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 *
 * @param <RequestT>
 *            The generic request type.
 * @param <RequestResultT>
 *            The generic result type.
 */
@ToString
@Slf4j
@Deprecated
public abstract class AbstractRemoteFunctionRequestResult<RequestT extends com.sap.cloud.sdk.s4hana.connectivity.Request<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
    implements
    com.sap.cloud.sdk.s4hana.connectivity.RequestResult<RequestT, RequestResultT>,
    Iterable<ResultElement>
{
    @Getter
    @Setter( AccessLevel.PACKAGE )
    private RequestT request;

    @Nullable
    @Getter( AccessLevel.PACKAGE )
    @Setter( AccessLevel.PACKAGE )
    @ElementName( "RESULT" )
    private ArrayList<Result> resultList;

    @Nullable
    @Getter( AccessLevel.PACKAGE )
    @Setter( AccessLevel.PACKAGE )
    @ElementName( "EXCEPTION" )
    private ExceptionResult exception;

    @Getter
    private final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> successMessages;

    @Getter
    private final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> informationMessages;

    @Getter
    private final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> warningMessages;

    @Getter
    private final List<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> errorMessages;

    @Data
    static class Result
    {
        @ElementName( "NAME" )
        private final String name;

        @ElementName( "VALUE" )
        private final ResultElement value;
    }

    @Data
    @NoArgsConstructor
    static class MessageResult
    {
        @ElementName( "TYPE" )
        com.sap.cloud.sdk.s4hana.serialization.MessageType messageType;

        @ElementName( "ID" )
        com.sap.cloud.sdk.s4hana.serialization.MessageClass messageClass;

        @ElementName( "NUMBER" )
        com.sap.cloud.sdk.s4hana.serialization.MessageNumber messageNumber;

        @ElementName( "MESSAGE" )
        String messageText;

        @ElementName( "MESSAGE_V1" )
        String messageVariable1;

        @ElementName( "MESSAGE_V2" )
        String messageVariable2;

        @ElementName( "MESSAGE_V3" )
        String messageVariable3;

        @ElementName( "MESSAGE_V4" )
        String messageVariable4;
    }

    @EqualsAndHashCode( callSuper = true )
    @Data
    static class ExceptionResult extends MessageResult
    {
        @ElementName( "NAME" )
        String name;

        @ElementName( "VALUE" )
        long parameterKind;
    }

    AbstractRemoteFunctionRequestResult()
    {
        successMessages = new ArrayList<>();
        informationMessages = new ArrayList<>();
        warningMessages = new ArrayList<>();
        errorMessages = new ArrayList<>();
    }

    /**
     * Indicates whether success messages occurred during BAPI execution.
     */
    void addSuccessMessage( @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage successMessage )
    {
        successMessages.add(successMessage);
    }

    /**
     * Indicates whether information messages occurred during BAPI execution.
     */
    void addInformationMessage(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage informationMessage )
    {
        informationMessages.add(informationMessage);
    }

    /**
     * Indicates whether warning messages occurred during BAPI execution.
     */
    void addWarningMessage( @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage warningMessage )
    {
        warningMessages.add(warningMessage);
    }

    /**
     * Indicates whether error messages occurred during BAPI execution.
     */
    void addErrorMessage( @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage errorMessage )
    {
        errorMessages.add(errorMessage);
    }

    /**
     * Indicates whether success messages occurred during BAPI execution.
     *
     * @return True if there is a success message.
     */
    public boolean hasSuccessMessages()
    {
        return !successMessages.isEmpty();
    }

    /**
     * Indicates whether information messages occurred during BAPI execution.
     *
     * @return True if there is an information message.
     */
    public boolean hasInformationMessages()
    {
        return !informationMessages.isEmpty();
    }

    /**
     * Indicates whether warning messages occurred during BAPI execution.
     *
     * @return True if there is a warning message.
     */
    public boolean hasWarningMessages()
    {
        return !warningMessages.isEmpty();
    }

    /**
     * Check whether the request failed.
     *
     * @return Checks whether the call failed, i.e. whether the result contains error messages.
     */
    public boolean hasFailed()
    {
        return !wasSuccessful();
    }

    /**
     * Check whether the request was successful.
     *
     * @return Whether the BAPI request was successful.
     */
    public boolean wasSuccessful()
    {
        return !hasErrorMessages();
    }

    /**
     * Check whether there are error messages.
     *
     * @return Whether error messages occurred during BAPI execution.
     */
    public boolean hasErrorMessages()
    {
        return !errorMessages.isEmpty();
    }

    private ResultElement newResultElement( final Result result )
    {
        return result.getValue();
    }

    /**
     * Get the size of the result list.
     *
     * @return The number of elements in the result.
     */
    public int size()
    {
        if( resultList == null ) {
            log.debug("Result list is null, returning 0 for size().");
            return 0;
        }

        return resultList.size();
    }

    /**
     * Check whether result list is empty.
     *
     * @return Whether this result has any elements.
     */
    public boolean isEmpty()
    {
        if( resultList == null ) {
            log.debug("Result list is null, returning true for isEmpty().");
            return true;
        }

        return resultList.isEmpty();
    }

    /**
     * Indicates whether the element with the given name exists in the result.
     *
     * @param elementName
     *            The name of the element to be accessed.
     * @return True if a result element can be found under that name.
     */
    public boolean has( @Nonnull final String elementName )
    {
        return getIfPresent(elementName).isDefined();
    }

    /**
     * Returns the element with the given name from this result.
     *
     * @param elementName
     *            The name of the element to be accessed.
     *
     * @return If existing, an optional instance of {@link ResultElement} representing the corresponding element.
     *
     */
    @Nonnull
    public Option<ResultElement> getIfPresent( @Nonnull final String elementName )
    {
        @Nullable
        final ResultElement resultElement = getResultElement(elementName);

        return Option.of(resultElement);
    }

    @Nullable
    private ResultElement getResultElement( final String elementName )
        throws IllegalArgumentException
    {
        if( resultList == null ) {
            log.debug("Result list is null, returning null element.");
            return null;
        }

        for( final Result result : resultList ) {
            if( elementName.equals(result.getName()) ) {
                return result.getValue();
            }
        }

        return null;
    }

    /**
     * Returns the element with the given name from this result.
     *
     * @param index
     *            The index of the element to be accessed.
     *
     * @return An instance of {@link ResultElement} representing the corresponding element.
     *
     * @throws IndexOutOfBoundsException
     *             If the given index is out of bounds.
     */
    @Nonnull
    public ResultElement get( final int index )
        throws IndexOutOfBoundsException
    {
        if( resultList == null ) {
            throw new IndexOutOfBoundsException("Failed to return element: result list is null.");
        }

        return resultList.get(index).getValue();
    }

    /**
     * Returns the element with the given name from this result.
     *
     * @param elementName
     *            The name of the element to be accessed.
     *
     * @return An instance of {@link ResultElement} representing the corresponding element.
     *
     * @throws IllegalArgumentException
     *             If the element could not be found.
     */
    @Nonnull
    public ResultElement get( @Nonnull final String elementName )
        throws IllegalArgumentException
    {
        final Option<ResultElement> resultElement = getIfPresent(elementName);

        return resultElement
            .getOrElseThrow(
                () -> new IllegalArgumentException("Unable to find element with name '" + elementName + "'."));
    }

    /**
     * Collects the resulting elements with the given name from the parameters of this result.
     *
     * @param elementName
     *            The name of the nested elements to be collected.
     *
     * @return An instance of {@link CollectedResultCollection} representing the corresponding elements.
     */
    @Nullable
    public CollectedResultCollection collect( @Nonnull final String elementName )
    {
        if( resultList == null ) {
            log.debug("Result list is null, returning null element.");
            return null;
        }

        return new DefaultCollectedResultCollection(elementName, getResultElements());

    }

    /**
     * Get the list of all result elements.
     *
     * @return A list of all result elements.
     */
    @Nonnull
    public ArrayList<ResultElement> getResultElements()
    {
        if( resultList == null ) {
            log.debug("Result list is null, returning empty list of elements.");
            return new ArrayList<>();
        }

        final ArrayList<ResultElement> result = Lists.newArrayListWithExpectedSize(resultList.size());

        for( final Result currentResult : resultList ) {
            result.add(currentResult.getValue());
        }

        return result;
    }

    @Nonnull
    @Override
    public Iterator<ResultElement> iterator()
    {
        return new ResultElementIterator();
    }

    private class ResultElementIterator implements Iterator<ResultElement>
    {
        private int index = 0;

        @Override
        public boolean hasNext()
        {
            if( resultList == null ) {
                log.debug("Result list is null, returning false for hasNext().");
                return false;
            }

            return index < resultList.size();
        }

        @Override
        @Nonnull
        public ResultElement next()
        {
            if( resultList == null ) {
                throw new NoSuchElementException("Cannot return next element, result list is null.");
            }

            return newResultElement(resultList.get(index++));
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Cannot remove elements from remote function result.");
        }
    }
}
